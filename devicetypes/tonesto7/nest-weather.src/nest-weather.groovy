/**
 *  Nest Weather
 *	Author: Anthony S. (@tonesto7)
 *  Author: Ben W. (@desertBlade)  Eric S. (@E_sch) 
 *
 * Copyright (C) 2016 Anthony S., Ben W.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.text.SimpleDateFormat

preferences {  }

def devVer() { return "1.1.2" }

metadata {
    definition (name: "${textDevName()}", namespace: "tonesto7", author: "Anthony S.") {

        capability "Illuminance Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Relative Humidity Measurement"
        capability "Temperature Measurement"
        
        command "refresh"
        command "log"
        
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
        attribute "devTypeVer", "string"
        attribute "lastUpdatedDt", "string"

        attribute "localSunrise", "string"
        attribute "localSunset", "string"
        attribute "city", "string"
        attribute "timeZoneOffset", "string"
        attribute "weather", "string"
        attribute "wind", "string"
        attribute "windgust", "string"
        attribute "windDir", "string"
        attribute "weatherIcon", "string"
        attribute "forecastIcon", "string"
        attribute "feelsLike", "string"
        attribute "percentPrecip", "string"
        attribute "uvindex", "string"
        attribute "visibility", "string"
        attribute "alert", "string"
        attribute "alertKeys", "string"
        attribute "sunriseDate", "string"
        attribute "sunsetDate", "string"

    }

    simulator { }

    tiles(scale: 2) {
        htmlTile(name:"weatherHtml", action: "getWeatherHtml", width: 6, height: 9)
        valueTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state("default", label:'${currentValue}°', 	icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/weather_icon.png", 
                    backgroundColors: getTempColors() )
        }
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Data Last Received:\n${currentValue}')
        }
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, decoration: "flat", wordWrap: true) {
            state "ok", label: "API Status:\nOK"
            state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
        }
        standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh-icon"
        }
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
            state("default", label: 'Device Type:\nv${currentValue}')
        }
        main ("temp2")
        details ("weatherHtml", "refresh")
    }
}

mappings {
    path("/getWeatherHtml") {action: [GET: "getWeatherHtml"]}
}

def initialize() {
    log.debug "initialize"
}

def parse(String description) {
    log.debug "Parsing '${description}'"
}

def configure() { }

def getTempColors() {
    def colorMap
    if (wantMetric()) {
        colorMap = [
            // Celsius Color Range
            [value: 0, color: "#153591"],
            [value: 7, color: "#1e9cbb"],
            [value: 15, color: "#90d2a7"],
            [value: 23, color: "#44b621"],
            [value: 29, color: "#f1d801"],
            [value: 33, color: "#d04e00"],
            [value: 36, color: "#bc2323"]
            ]
    } else {
        colorMap = [
            // Fahrenheit Color Range
            [value: 40, color: "#153591"],
            [value: 44, color: "#1e9cbb"],
            [value: 59, color: "#90d2a7"],
            [value: 74, color: "#44b621"],
            [value: 84, color: "#f1d801"],
            [value: 92, color: "#d04e00"],
            [value: 96, color: "#bc2323"]
            ]
    }
}

def poll() {
    log.debug "Polling parent..."
    parent.refresh(this)
}

def refresh() {
    poll()
}

def generateEvent(Map results) {
    //Logger("generateEvents Parsing data ${results}")
    Logger("-------------------------------------------------------------------", "warn")
    if(!results) {
        state.results = results
        state.tempUnit = getTemperatureScale()
        state.useMilitaryTime = !parent?.settings?.useMilitaryTime ? false : true
        state.timeZone = !location?.timeZone ? parent?.getNestTimeZone() : null
        debugOnEvent(parent?.settings?.childDebug)
        apiStatusEvent(parent?.apiIssues())
        deviceVerEvent()
    }
    lastUpdatedEvent()
    getWeatherAstronomy()
    getWeatherForecast()
    getWeatherConditions()
    getWeatherAlerts()
    return null
}

def getDataByName(String name) {
    state[name] ?: device.getDataValue(name)
}

def getTimeZone() { 
    def tz = null
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = state?.timeZone ? TimeZone.getTimeZone(state?.timeZone) : null }
    if(!tz) { log.warn "getTimeZone: Hub or Nest TimeZone is not found ..." }
    return tz
}

def deviceVerEvent() {
    def curData = device.currentState("devTypeVer")?.value
    def pubVer = parent?.latestWeathVer().ver.toString()
    def dVer = devVer() ? devVer() : null
    def newData = (pubVer != dVer) ? "${dVer}(New: v${pubVer})" : "${dVer}(Current)"
    state?.devTypeVer = newData
    if(curData != newData) {
        Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
        sendEvent(name: 'devTypeVer', value: newData, displayed: false)
    } else { Logger("Device Type Version is: (${newData}) | Original State: (${curData})") }
}

def debugOnEvent(debug) {
    def val = device.currentState("debugOn")?.value
    def dVal = debug ? "On" : "Off"
    state?.debugStatus = dVal
    if(!val.equals(dVal)) {
        log.debug("UPDATED | debugOn: (${dVal}) | Original State: (${val})")
        sendEvent(name: 'debugOn', value: dVal, displayed: false)
    } else { Logger("debugOn: (${dVal}) | Original State: (${val})") }
}

def lastUpdatedEvent() {
    def now = new Date()
    def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    tf.setTimeZone(getTimeZone())
    def lastDt = "${tf?.format(now)}"
    def lastUpd = device.currentState("lastUpdatedDt")?.value
    state?.lastUpdatedDt = lastDt?.toString()
    if(!lastUpd.equals(lastDt?.toString())) {
        Logger("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
        sendEvent(name: 'lastUpdatedDt', value: lastDt?.toString(), displayed: false, isStateChange: true)
    }
}

def apiStatusEvent(issue) {
    def appStat = device.currentState("apiStatus")?.value
    def val = issue ? "issue" : "ok"
    state?.apiStatus = val
    if(!appStat.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${appStat})")
        sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${appStat})") }
}

def humidityEvent(humidity) {
    def hum = device.currentState("humidity")?.value
    if(!hum.equals(humidity)) {
        log.debug("UPDATED | Humidity is (${humidity}) | Original State: (${hum})")
        sendEvent(name:'humidity', value: humidity, unit: "%", descriptionText: "Humidity is ${humidity}" , displayed: false, isStateChange: true)
    } else { Logger("Humidity is (${humidity}) | Original State: (${hum})") }
}

def illuminanceEvent(illum) {
    def cur = device.currentState("illuminance")?.value.toString()
    if(!cur.equals(illum.toString())) {
        log.debug("UPDATED | Illuminance is (${illum}) | Original State: (${cur})")
        sendEvent(name:'illuminance', value: illum, unit: "lux", descriptionText: "Illuminance is ${illum}" , displayed: false, isStateChange: true)
    } else { Logger("Illuminance is (${illum}) | Original State: (${cur})") }
}

def temperatureEvent(Double tempVal, Double feelsVal) {
    def temp = device.currentState("temperature")?.value.toString()
    def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
    def rFeelsVal = wantMetric() ? feelsVal.round(1) : feelsVal.round(0).toInteger()
    if(!temp.equals(rTempVal.toString())) {
        log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
        sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
        sendEvent(name:'feelsLike', value: rFeelsVal, unit: state?.tempUnit, descriptionText: "Feels Like Temperature is ${rFeelsVal}" , displayed: false)
    } else { Logger("Temperature is (${rTempVal}) | Original Temp: (${temp})") }
}

def getTemp() { 
    try { return state.curWeatherTemp } 
    catch (e) { return 0 }
}

def getCurWeather() { 
    try { return state.curWeather } 
    catch (e) { return 0 }
}

def getHumidity() { 
    try { return device.currentValue("humidity") } 
    catch (e) { return 0 }
}

def wantMetric() { return (state?.tempUnit == "C") }
/************************************************************************************************
|									Weather Info for Tiles										|
*************************************************************************************************/

def getWeatherConditions() {
    def cur = parent?.getWData()
    if(cur) {
        state.curWeather = cur
        //log.debug "cur: $cur"
        state.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f)
        state.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c)
        state.curFeelsTemp_f = Math.round(cur?.current_observation?.feelslike_f as Double)
        state.curFeelsTemp_c = Math.round(cur?.current_observation?.feelslike_c as Double)
        state.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
        state.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
        state.curWeatherCond = cur?.current_observation?.weather.toString()
        state.curWeatherIcon = cur?.current_observation?.icon.toString()
        state.zipCode = cur?.current_observation?.display_location.zip.toString()
        state.curWeatherTemp = ( wantMetric() ) ? "${state?.curWeatherTemp_c}°C": "${state?.curWeatherTemp_f}°F"
        def curTemp = ( wantMetric() ) ? cur?.current_observation?.temp_c.toDouble() : cur?.current_observation?.temp_f.toDouble()
        temperatureEvent( ( wantMetric() ? state?.curWeatherTemp_c : state?.curWeatherTemp_f), 
                     ( wantMetric() ? state?.curFeelsTemp_c : state?.curFeelsTemp_f) )
        humidityEvent(state?.curWeatherHum)
        illuminanceEvent(estimateLux(state.curWeatherIcon))
        sendEvent(name: "weather", value: cur?.current_observation?.weather)
        sendEvent(name: "weatherIcon", value: state.curWeatherIcon, displayed:false)
        def wspeed = 0.0
        def wgust = 0.0
        if (wantMetric()) {
            wspeed = Math.round(cur?.current_observation?.wind_kph as float)
            wgust = Math.round(cur?.current_observation?.wind_gust_kph as float)
            sendEvent(name: "visibility", value: cur?.current_observation?.visibility_km, unit: "km")
            sendEvent(name: "wind", value: wspeed as String, unit: "KPH")
            sendEvent(name: "windgust", value: wgust as String, unit: "KPH")
            wspeed += " KPH"
            wgust += " KPH"
        } else {
            wspeed = Math.round(cur?.current_observation?.wind_mph as float)
            wgust = Math.round(cur?.current_observation?.wind_gust_mph as float)
            sendEvent(name: "visibility", value: cur?.current_observation?.visibility_mi, unit: "miles")
            sendEvent(name: "wind", value: wspeed as String, unit: "MPH")
            sendEvent(name: "windgust", value: wgust as String, unit: "MPH")
            wspeed += " MPH"
            wgust += " MPH"
        }
        def wdir = cur?.current_observation?.wind_dir
        sendEvent(name: "windDir", value: wdir)
        state.windStr = "From the ${wdir} at ${wspeed} Gusting to ${wgust}"
        sendEvent(name: "timeZoneOffset", value: cur?.current_observation?.local_tz_offset)
        def cityValue = "${cur?.current_observation?.display_location.city}, ${cur?.current_observation?.display_location.state}"
        sendEvent(name: "city", value: cityValue)

        sendEvent(name: "uvindex", value: cur?.current_observation?.UV)
        Logger("${state?.curWeatherLoc} Weather | humidity: ${state?.curWeatherHum} | temp_f: ${state?.curWeatherTemp_f} | temp_c: ${state?.curWeatherTemp_c} | Current Conditions: ${state?.curWeatherCond}")
    }
}

def getWeatherForecast() {
    def cur = parent?.getWForecastData()
    if(cur) {
        state.curForecast = cur
        //log.debug "cur: $cur"
        def f1 = cur?.forecast?.simpleforecast?.forecastday
        if (f1) {
            def icon = f1[0].icon
            def value = f1[0].pop as String // as String because of bug in determining state change of 0 numbers
            sendEvent(name: "percentPrecip", value: value, unit: "%")
            sendEvent(name: "forecastIcon", value: icon, displayed: false)
        }
    }
}

def getWeatherAstronomy() {
    def cur = parent?.getWAstronomyData()
    if(cur) {
        state.curAstronomy = cur
        //log.debug "cur: $cur"
        getSunriseSunset()
        sendEvent(name: "localSunrise", value: state.localSunrise, descriptionText: "Sunrise today is at ${state.localSunrise}")
        sendEvent(name: "localSunset", value: state.localSunset, descriptionText: "Sunset today at is ${state.localSunset}")
    }
}

def getWeatherAlerts() {
    def cur = parent?.getWAlertsData()
    if(cur) {
        state.curAlerts = cur
        //log.debug "cur: $cur"
        def alerts = cur?.alerts
        def newKeys = alerts?.collect{it.type + it.date_epoch} ?: []
      //log.debug "${device.displayName}: newKeys: $newKeys"
      //log.trace device.currentState("alertKeys")
        def oldKeys = device.currentState("alertKeys")?.jsonValue
      //log.debug "${device.displayName}: oldKeys: $oldKeys"

        def noneString = ""
        if (!newKeys && oldKeys == null) {
                sendEvent(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)
                sendEvent(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts")
                state.walert = noneString
        }
        else if (newKeys != oldKeys) {
                if (oldKeys == null) {
                        oldKeys = []
                }
                sendEvent(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)

                def newAlerts = false
                alerts.each {alert ->
                        if (!oldKeys.contains(alert.type + alert.date_epoch)) {
                                def msg = "${alert.description} from ${alert.date} until ${alert.expires}"
                                sendEvent(name: "alert", value: pad(alert.description), descriptionText: msg)
                                newAlerts = true
                                state.walert = pad(alert.description) // description
                                state.walertMessage = pad(alert.message) // message
                        }
                }

                if (!newAlerts && device.currentValue("alert") != noneString) {
                        sendEvent(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts")
                        state.walert = noneString
                }
        }
    }
}

private pad(String s, size = 25) {
        def n = (size - s.size()) / 2
        if (n > 0) {
                def sb = ""
                n.times {sb += " "}
                sb += s
                n.times {sb += " "}
                return sb
        }
        else {
                return s
        }
}

private estimateLux(weatherIcon) {
    //log.trace "estimateLux ( ${weatherIcon} )"
    def lux = 0
    def twilight = 20 * 60 * 1000 // 20 minutes
    def now = new Date().time
    def sunriseDate = state?.sunriseDate.time
    def sunsetDate = state?.sunsetDate.time
    sunriseDate -= twilight
    sunsetDate += twilight
    if (now > sunriseDate && now < sunsetDate) {
        //day
        switch(weatherIcon) {
            case 'tstorms':
                lux = 200
                break
            case ['cloudy', 'fog', 'rain', 'sleet', 'snow', 'flurries',
                'chanceflurries', 'chancerain', 'chancesleet',
                'chancesnow', 'chancetstorms']:
                lux = 1000
                break
            case 'mostlycloudy':
                lux = 2500
                break
            case ['partlysunny', 'partlycloudy', 'hazy']:
                lux = 7500
                break
            default:
                //sunny, clear
                lux = 10000
       }

       //adjust for dusk/dawn
       def afterSunrise = now - sunriseDate
       def beforeSunset = sunsetDate - now
       def oneHour = 1000 * 60 * 60

       if(afterSunrise < oneHour) {
           //dawn
           lux = (long)(lux * (afterSunrise/oneHour))
       } else if (beforeSunset < oneHour) {
           //dusk
           lux = (long)(lux * (beforeSunset/oneHour))
       }
    } else {
        //night - always set to 10 for now
        //could do calculations for dusk/dawn too
        lux = 10
    }
   return lux
}


/************************************************************************************************
|										LOGGING FUNCTIONS										|
*************************************************************************************************/
// Local Device Logging
def Logger(msg, logType = "debug") {
     if(parent.settings?.childDebug) { 
        switch (logType) {
            case "trace":
                log.trace "${msg}"
                break;
            case "debug":
                log.debug "${msg}"
                break
            case "warn":
                log.warn "${msg}"
                break
            case "error":
                log.error "${msg}"
                break
            default:
                log.debug "${msg}"
                break;
        }
     }
 }
 
 //This will Print logs from the parent app when added to parent method that the child calls
def log(message, level = "trace") {
    switch (level) {
        case "trace":
            log.trace "PARENT_Log>> " + message
            break;
        case "debug":
            log.debug "PARENT_Log>> " + message
            break
        case "warn":
            log.warn "PARENT_Log>> " + message
            break
        case "error":
            log.error "PARENT_Log>> " + message
            break
        default:
            log.error "PARENT_Log>> " + message
            break;
    }            
    return null // always child interface call with a return value
}

def getImgBase64(url, type) {
    def params = [ 
        uri: url,
           contentType: 'image/$type'
    ]
    try {
    httpGet(params) { resp ->
        if(resp.data) {
            def respData = resp?.data
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
            int len
            int size = 1024
            byte[] buf = new byte[size]
            while ((len = respData.read(buf, 0, size)) != -1)
               bos.write(buf, 0, len)
               buf = bos.toByteArray()
               String s = buf?.encodeBase64()
               //log.debug "resp: ${s}"
               return s ? "data:image/${type};base64,${s.toString()}" : null
            }
        }
    }
    catch (ex) {
        log.error "getImageBytes Exception: $ex"
    }
}

def getWeatherIcon(weatherIcon) {
  def url = "https://icons.wxug.com/i/c/v4/" + state?.curWeather?.current_observation?.icon + ".svg"
     return getImgBase64(url, "svg+xml")
}

def getFeelslike() {
    if ( wantMetric() ) {
        return "${state?.curWeather?.current_observation?.feelslike_c}°C"
    } else {
        return "${state?.curWeather?.current_observation?.feelslike_f}°F"
    }
}

def getVisibility() {
    if ( wantMetric() ) {
        return "${state.curWeather?.current_observation?.visibility_km} km"
    } else {
        return "${state.curWeather?.current_observation?.visibility_mi} Miles"
    }
}

def getLux() {
    def cur = device.currentState("illuminance")?.value.toString()
    return cur
}

private localDate(timeZone) {
    def df = new SimpleDateFormat("yyyy-MM-dd")
    df.setTimeZone(TimeZone.getTimeZone(timeZone))
    df.format(new Date())
}

def getSunriseSunset() {
    // Sunrise / sunset
    def a = state?.curAstronomy?.moon_phase
    def today = localDate("GMT${state.curWeather?.current_observation?.local_tz_offset}")

    def ltf = new SimpleDateFormat("yyyy-MM-dd HH:mm")
         
    ltf.setTimeZone(TimeZone.getTimeZone("GMT${state.curWeather?.current_observation?.local_tz_offset}"))

    def utf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    utf.setTimeZone(TimeZone.getTimeZone("GMT"))

    def sunriseDate = ltf.parse("${today} ${a.sunrise.hour}:${a.sunrise.minute}")
    def sunsetDate = ltf.parse("${today} ${a.sunset.hour}:${a.sunset.minute}")
    state.sunriseDate = sunriseDate
    state.sunsetDate = sunsetDate
 
    def tf = new java.text.SimpleDateFormat("h:mm a")
    tf.setTimeZone(TimeZone.getTimeZone("GMT${state.curWeather?.current_observation?.local_tz_offset}"))
    def localSunrise = "${tf.format(sunriseDate)}"
    def localSunset = "${tf.format(sunsetDate)}"
    state.localSunrise = localSunrise
    state.localSunset = localSunset
}


def forecastDay(day) {
    def dayName = "<b>${state.curForecast.forecast.txt_forecast.forecastday[day].title} </b><br>"
    def forecastImageLink = "<a href=\"#${day}\"><img src=\"${getImgBase64(state.curForecast.forecast.txt_forecast.forecastday[day].icon_url, gif)}\"></a><br>"
    def forecastTxt = ""
    
    def modalHead = "<div id=\"${day}\" class=\"forecastModal\"><div><a href=\"#close\" title=\"Close\" class=\"close\">X</a>"
    def modalTitle = " <h2>${state.curForecast.forecast.txt_forecast.forecastday[day].title}</h2>"
	def forecastImage = "<img src=\"${getImgBase64(state.curForecast.forecast.txt_forecast.forecastday[day].icon_url, gif)}\">"
    
    if ( wantMetric() ) {
         forecastTxt = "<p>${state.curForecast.forecast.txt_forecast.forecastday[day].fcttext_metric}</p>"
    } else {
         forecastTxt = "<p>${state.curForecast.forecast.txt_forecast.forecastday[day].fcttext}</p>"
    }
    
    def modalClose = "</div> </div>"

    return  dayName + forecastImageLink + modalHead + modalTitle + forecastImage + forecastTxt + modalClose
}

def getWeatherHtml() { 
    renderHTML {
        head {
            """ <style type="text/css">
                body {
                  font-family: 'San Francisco', 'Roboto', 'Arial';
                }

                #header {
                  font-size: 4vw;
                  font-weight: bold;
                  text-align: center;
                  background: #00a1db;
                  color: #f5f5f5;
                }

                #alert, #alert a {
                  font-size: 6vw;
                  font-weight: bold;
                  text-align: center;
                  background: #B74C4C;
                  color: #f5f5f5;
                }

                #weatherInfo {
                  text-align: left;
                }

                #leftData {
                  width: 98%;
                  float: left;
                  clear: left;
                }

                #city {
                  font-size: 6vw;
                  width: 100%;
                  text-align: center;
                  border-bottom: 2px solid #808080;
                }

                #data {
                  font-size: 4vw;
                  padding: 5px;
                }

                #forecast {
                  border-top: 2px solid #00a1db;
                  clear: left;
                  padding: 5px;
                }

                #day {
                  width: 30%;
                  float: left;
                }

                #station {
  					padding:auto;
                }

                #weatherIcon {
                  text-align: center;
                }

                #condition {
                  border-top: 2px solid #00a1db;
                  text-align: center;
                  width: 80%;
                  padding-bottom: 5px;
                  margin-left: auto;
                  margin-right: auto;
                  font-size: 6vw;
                }

                #temp {
                  font-size: 9vw;
                  text-align: center;
                  margin-left: auto;
                  margin-right: auto;
                }

                .icon {
                  margin-left: auto;
                  margin-right: auto;
                  width: 70%;
                }

                #dataDump {
                  float: left;
                  clear: left;
                }

                hr {
                  background: #00a1db;
                  width: 100%;
                  height: 1px;
                }

                .r33 {
                  width: 33%;
                  vertical-align: top;
                  font-size: 3vw;
                  padding: 3px;
                  text-align: center;
                }

                .r50 {
                  width: 48%;
                }
                
        .alertModal, .forecastModal {
            position: fixed;
            font-family: 'San Francisco', 'Roboto', 'Arial';
            font-size: 3vw;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            background: rgba(0,161,219,0.4);
            z-index: 99999;
            opacity:0;
            -webkit-transition: opacity 400ms ease-in;
            -moz-transition: opacity 400ms ease-in;
            transition: opacity 400ms ease-in;
            pointer-events: none;
        }

        .alertModal:target, .forecastModal:target {
            opacity:1;
            pointer-events: auto;
        }

        .alertModal > div {
            width: 65%;
            position: relative;
            margin: 10% auto;
            padding: 5px 20px 13px 20px;
            border-radius: 10px;
            background: #fff;
            background: -moz-linear-gradient(#fff, #999);
            background: -webkit-linear-gradient(#fff, #999);
            background: -o-linear-gradient(#fff, #999);
        }
        
         .forecastModal > div {
            width: 65%;
            position: relative;
            margin:  75% auto;
            padding: 5px 20px 13px 20px;
            border-radius: 10px;
            background: #fff;
            background: -moz-linear-gradient(#fff, #999);
            background: -webkit-linear-gradient(#fff, #999);
            background: -o-linear-gradient(#fff, #999);
        }

        .close {
            background: #606061;
            color: #FFFFFF;
            line-height: 25px;
            position: absolute;
            right: -12px;
            text-align: center;
            top: -10px;
            width: 24px;
            text-decoration: none;
            font-weight: bold;
            -webkit-border-radius: 12px;
            -moz-border-radius: 12px;
            border-radius: 12px;
            -moz-box-shadow: 1px 1px 3px #000;
            -webkit-box-shadow: 1px 1px 3px #000;
            box-shadow: 1px 1px 3px #000;
        }

		.close:hover { background: #00d9ff; }
              </style>
               """
        }
        body {
            """
              <div class="container">
              <div id="header">Current Weather Conditions</div>
              <div id="weatherInfo">
              <div id="alert"><a href="#openModal">${state?.walert}</a></div>
              <div id="city"> ${state?.curWeather?.current_observation?.display_location.full} </div>
              <div id="leftData">
              <table>
                <tbody>
                  <tr>
                    <td class="r50">
                      <div id="leftData">
                        <div id="data">
                          <b>Feels Like:</b> ${getFeelslike()} <br>
                          <b>Precip: </b> ${device.currentState("percentPrecip")?.value}% <br>
                          <b>Humidity:</b> ${state?.curWeather?.current_observation?.relative_humidity}<br>
                          <b>UV Index: </b>${state.curWeather?.current_observation?.UV}<br>
                          <b>Visibility:</b> ${getVisibility()} <br>
                          <b>Lux:</b> ${getLux()}<br>
                          <b>Sunrise:</b> ${state?.localSunrise} <br> <b>Sunset: </b> ${state?.localSunset} <br>
                          <b>Wind:</b> ${state?.windStr} <br>
                        </div>
                      </div>
                    </td>
                  <td class="r50">
                    <div id="weatherIcon">
                      <div>
                        <img src="${getWeatherIcon()}" class="icon"> <br>
                        <div id="temp">${getTemp()}</div>
                        <div id ="condition">${state.curWeatherCond}</div>
                      </div>
                    </div>
                  </td>
                </tr>
             </table>
             <table id="forecast">
               <tbody>
                 <tr>
                   <td class="r33">${forecastDay(0)}</td>
                   <td class="r33">${forecastDay(1)}</td>
                   <td class="r33">${forecastDay(2)}</td>
                 </tr>
                 <tr>
                   <td class="r33">${forecastDay(3)}</td>
                   <td class="r33">${forecastDay(4)}</td>
                   <td class="r33">${forecastDay(5)}</td>
                 </tr>
                  <tr>
                   <td class="r33">${forecastDay(6)}</td>
                   <td class="r33">${forecastDay(7)}</td>
                   <td class="r33"><div class="station"><b>Station Id:</b> ${state?.curWeather?.current_observation?.station_id} </div></td>
                 </tr>
               </table>
               
        
               <div id="openModal" class="alertModal">
					<div>
                        <a href="#close" title="Close" class="close">X</a>
                        <h2>Special Message</h2>
                        <p>${state?.walertMessage} </p>
                    </div>
                </div>
          </div>
          """
        }
    }
}
private def textDevName()   { "Nest Weather${appDevName()}" }
private def appDevType()    { false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
