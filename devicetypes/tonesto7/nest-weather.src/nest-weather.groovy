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

def devVer() { return "2.5.5" }

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
        attribute "dewpoint", "string"
        attribute "visibility", "string"
        attribute "alert", "string"
        attribute "alertKeys", "string"
        attribute "sunriseDate", "string"
        attribute "sunsetDate", "string"

    }

    simulator { }

    tiles(scale: 2) {
        htmlTile(name:"weatherHtml", action: "getWeatherHtml", width: 6, height: 10)
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
    try {
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
    catch (ex) {
        log.error "getTempColors Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getTempColors")
    }
}

def poll() {
    log.debug "Polling parent..."
    parent.refresh(this)
}

def refresh() {
    poll()
}

def generateEvent(Map eventData) {
    //log.trace("generateEvents Parsing data ${eventData}")
    try {
        Logger("------------START OF API RESULTS DATA------------", "warn")
        if(eventData) {
            state.tempUnit = getTemperatureScale()
            
            state.useMilitaryTime = eventData?.mt ? true : false
            state.nestTimeZone = !location?.timeZone ? eventData?.tz : null
            state.weatherAlertNotify = !eventData?.weathAlertNotif ? false : true
            debugOnEvent(eventData?.debug ? true : false)
            apiStatusEvent(eventData?.apiIssues)
            deviceVerEvent(eventData?.latestVer.toString())
            state?.cssUrl = eventData?.cssUrl
            
            getWeatherAstronomy(eventData?.data?.weatAstronomy?.sun_phase ? eventData?.data?.weatAstronomy : null)
            getWeatherForecast(eventData?.data?.weatForecast?.forecast ? eventData?.data?.weatForecast : null)
            getWeatherAlerts(eventData?.data?.weatAlerts ? eventData?.data?.weatAlerts : null)
            getWeatherConditions(eventData?.data?.weatCond?.current_observation ? eventData?.data?.weatCond : null)
        }
        lastUpdatedEvent()
        //This will return all of the devices state data to the logs.
        //log.debug "Device State Data: ${getState()}"
        return null
    }
    catch (ex) {
        log.error "generateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "generateEvent")
    }
}

def getDataByName(String name) {
    state[name] ?: device.getDataValue(name)
}

def getDeviceStateData() {
    return getState()
}

def getTimeZone() { 
    def tz = null
    if (!state?.nestTimeZone) { tz = location?.timeZone }
    else { tz = TimeZone.getTimeZone(state?.nestTimeZone) }
    if(!tz) { LogAction("getTimeZone: Hub or Nest TimeZone is not found ...", "warn", true) }
    return tz
}

def isCodeUpdateAvailable(newVer, curVer) {
    try {
        def result = false
        def latestVer 
        def versions = [newVer, curVer]
        if(newVer != curVer) {
            latestVer = versions?.max { a, b -> 
                def verA = a?.tokenize('.')
                def verB = b?.tokenize('.')
                def commonIndices = Math.min(verA?.size(), verB?.size())
                for (int i = 0; i < commonIndices; ++i) {
                    //log.debug "comparing $numA and $numB"
                    if (verA[i]?.toInteger() != verB[i]?.toInteger()) {
                        return verA[i]?.toInteger() <=> verB[i]?.toInteger()
                    }
                }
                verA?.size() <=> verB?.size()
            }
            result = (latestVer == newVer) ? true : false
        }
        //log.debug "type: $type | newVer: $newVer | curVer: $curVer | newestVersion: ${latestVer} | result: $result"
        return result
    } catch (ex) {
        LogAction("isCodeUpdateAvailable Exception: ${ex}", "error", true)
        sendChildExceptionData("weather", devVer(), ex?.toString(), "isCodeUpdateAvailable")
    }
}

def deviceVerEvent(ver) {
    try {
        def curData = device.currentState("devTypeVer")?.value.toString()
        def pubVer = ver ?: null
        def dVer = devVer() ?: null
        def newData = isCodeUpdateAvailable(pubVer, dVer) ? "${dVer}(New: v${pubVer})" : "${dVer}"
        state?.devTypeVer = newData
        state?.updateAvailable = isCodeUpdateAvailable(pubVer, dVer)
        if(!curData?.equals(newData)) {
            Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
            sendEvent(name: 'devTypeVer', value: newData, displayed: false)
        } else { Logger("Device Type Version is: (${newData}) | Original State: (${curData})") }
    }
    catch (ex) {
        log.error "deviceVerEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "deviceVerEvent")
    }
}

def debugOnEvent(debug) {
    try {
        def val = device.currentState("debugOn")?.value
        def dVal = debug ? "On" : "Off"
        state?.debugStatus = dVal
        state?.debug = debug.toBoolean() ? true : false
        if(!val.equals(dVal)) {
            log.debug("UPDATED | debugOn: (${dVal}) | Original State: (${val})")
            sendEvent(name: 'debugOn', value: dVal, displayed: false)
        } else { Logger("debugOn: (${dVal}) | Original State: (${val})") }
    }
    catch (ex) {
        log.error "debugOnEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "debugOnEvent")
    }
}

def lastUpdatedEvent() {
    try {
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
    catch (ex) {
        log.error "lastUpdatedEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "lastUpdatedEvent")
    }
}

def apiStatusEvent(issue) {
    try {
        def curStat = device.currentState("apiStatus")?.value
        def newStat = issue ? "issue" : "ok"
        state?.apiStatus = newStat
        if(!curStat.equals(newStat)) { 
            log.debug("UPDATED | API Status is: (${newStat}) | Original State: (${curStat})")
            sendEvent(name: "apiStatus", value: newStat, descriptionText: "API Status is: ${newStat}", displayed: true, isStateChange: true, state: newStat)
        } else { Logger("API Status is: (${newStat}) | Original State: (${curStat})") }
    }
    catch (ex) {
        log.error "apiStatusEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "apiStatusEvent")
    }
}

def humidityEvent(humidity) {
    try {
        def hum = device.currentState("humidity")?.value
        if(!hum.equals(humidity)) {
            log.debug("UPDATED | Humidity is (${humidity}) | Original State: (${hum})")
            sendEvent(name:'humidity', value: humidity, unit: "%", descriptionText: "Humidity is ${humidity}" , displayed: false, isStateChange: true)
        } else { Logger("Humidity is (${humidity}) | Original State: (${hum})") }
    }
    catch (ex) {
        log.error "humidityEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "humidityEvent")
    }
}

def illuminanceEvent(illum) {
    try {
        def cur = device.currentState("illuminance")?.value.toString()
        if(!cur.equals(illum.toString())) {
            log.debug("UPDATED | Illuminance is (${illum}) | Original State: (${cur})")
            sendEvent(name:'illuminance', value: illum, unit: "lux", descriptionText: "Illuminance is ${illum}" , displayed: false, isStateChange: true)
        } else { Logger("Illuminance is (${illum}) | Original State: (${cur})") }
    }
    catch (ex) {
        log.error "illuminanceEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "illuminanceEvent")
    }
}

def dewpointEvent(Double tempVal) {
    try {
        def temp = device.currentState("dewpoint")?.value.toString()
        def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        if(!temp.equals(rTempVal.toString())) {
            log.debug("UPDATED | DewPoint Temperature is (${rTempVal}) | Original Temp: (${temp})")
            sendEvent(name:'dewpoint', value: rTempVal, unit: state?.tempUnit, descriptionText: "Dew point Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
        } else { Logger("DewPoint Temperature is (${rTempVal}) | Original Temp: (${temp})") }
    }
    catch (ex) {
        log.error "dewpointEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "dewpointEvent")
    }
}

def temperatureEvent(Double tempVal, Double feelsVal) {
    try {
        def temp = device.currentState("temperature")?.value.toString()
        def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        def rFeelsVal = wantMetric() ? feelsVal.round(1) : feelsVal.round(0).toInteger()
        if(!temp.equals(rTempVal.toString())) {
            log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
            sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
            sendEvent(name:'feelsLike', value: rFeelsVal, unit: state?.tempUnit, descriptionText: "Feels Like Temperature is ${rFeelsVal}" , displayed: false)
        } else { Logger("Temperature is (${rTempVal}) | Original Temp: (${temp})") }
    }
    catch (ex) {
        log.error "temperatureEvent Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "temperatureEvent")
    }
}

def getTemp() { 
    try { 
     if ( wantMetric() ) {
         return "${state?.curWeatherTemp_c}°C"
     } else {
         return	"${state?.curWeatherTemp_f}°F"
    }       
    } catch (ex) { 
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getTemp")
        return 0 
    }
}

def getDewpoint() { 
    try { 
     if ( wantMetric() ) {
         return "${state?.curWeatherDewPoint_c}°C"
     } else {
         return	"${state?.curWeatherDewPoint_f}°F"
    }       
    } catch (ex) { 
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getDewpoint")
        return 0 
    }
}

def getCurWeather() { 
    try { return state.curWeather } 
    catch (ex) { 
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getCurWeather")
        return 0 
    }
}

def getHumidity() { 
    try { return device.currentValue("humidity") } 
    catch (ex) { 
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getHumidity")
        return 0 
    }
}

def wantMetric() { return (state?.tempUnit == "C") }
/************************************************************************************************
|									Weather Info for Tiles										|
*************************************************************************************************/

def getWeatherConditions(Map weatData) {
    try {
        if(!weatData.current_observation) {
            log.warn "There is an Issue getting the weather condition data"
            return
        } else {
            def cur = weatData
            if(cur) {
                state.curWeather = cur
                
                state.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f).toInteger()
                state.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c.toDouble())
                state.curFeelsTemp_f = Math.round(cur?.current_observation?.feelslike_f as Double)
                state.curFeelsTemp_c = Math.round(cur?.current_observation?.feelslike_c as Double)
                state.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
                state.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
                state.curWeatherCond = cur?.current_observation?.weather.toString()
                state.curWeatherIcon = cur?.current_observation?.icon.toString()
                state.zipCode = cur?.current_observation?.display_location.zip.toString()
                def curTemp = wantMetric() ? cur?.current_observation?.temp_c.toDouble() : cur?.current_observation?.temp_f.toDouble()
                temperatureEvent( (wantMetric() ? state?.curWeatherTemp_c : state?.curWeatherTemp_f), (wantMetric() ? state?.curFeelsTemp_c : state?.curFeelsTemp_f) )
                humidityEvent(state?.curWeatherHum)
                illuminanceEvent(estimateLux(state?.curWeatherIcon))
                def hum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "") as Double
                def Tc = Math.round(cur?.current_observation?.feelslike_c as Double) as Double
                state.curWeatherDewPoint_c = estimateDewPoint(hum,Tc)
                state.curWeatherDewPoint_f =  Math.round(state.curWeatherDewPoint_c * 9.0/5.0 + 32.0)
                dewpointEvent((wantMetric() ? state?.curWeatherDewPoint_c : state?.curWeatherDewPoint_f))
                sendEvent(name: "weather", value: cur?.current_observation?.weather)
                sendEvent(name: "weatherIcon", value: state?.curWeatherIcon, displayed:false)
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
    }
    catch (ex) {
        log.error "getWeatherConditions Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex, "getWeatherConditions")
    }
}

def getWeatherForecast(Map weatData) {
    try {
        if(!weatData) {
            log.warn "There is an Issue getting the weather forecast"
            return
        } else {
            def cur = weatData
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
    }
    catch (ex) {
        log.error "getWeatherForecast Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex, "getWeatherForecast")
    }
}

def getWeatherAstronomy(weatData) {
    try {
        if(!weatData) {
            log.warn "There is an Issue getting the weather astronomy data"
            return
        } else {
            def cur = weatData
            if(cur) {
                state.curAstronomy = cur
                //log.debug "cur: $cur"
                getSunriseSunset()
                sendEvent(name: "localSunrise", value: state.localSunrise, descriptionText: "Sunrise today is at ${state.localSunrise}")
                sendEvent(name: "localSunset", value: state.localSunset, descriptionText: "Sunset today at is ${state.localSunset}")
            }
        }
    }
    catch (ex) {
        log.error "getWeatherAstronomy Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex, "getWeatherAstronomy")
    }
}

def getWeatherAlerts(weatData) {
    try {
        if(!weatData) {
            log.warn "There is an Issue getting the weather alert data"
            return
        } else {
            def cur = weatData
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

                            // Try to format message some
                            state.walertMessage = state.walertMessage.replaceAll(/\.\.\./, ' ')
                            state.walertMessage = state.walertMessage.replaceAll(/\*/, '')
                            state.walertMessage = state.walertMessage.replaceAll(/\n\n\n/, '\n\n')
                            state.walertMessage = state.walertMessage.replaceAll(/\n\n\n/, '\n\n')
                            state.walertMessage = state.walertMessage.replaceAll(/\n\n\n/, '\n\n')
                            state.walertMessage = state.walertMessage.replaceAll(/\n\n/, '<br>')
                            state.walertMessage = state.walertMessage.replaceAll(/\n/, ' ')

                            if(state?.weatherAlertNotify && (alert?.message.toString() != state?.lastWeatherAlertNotif.toString())) {
                                sendNofificationMsg("WEATHER ALERT: ${alert?.message}", "Warn")
                                state?.lastWeatherAlertNotif = alert?.message
                            }
                        }
                    }

                    if (!newAlerts && device.currentValue("alert") != noneString) {
                        sendEvent(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts")
                        state.walert = noneString
                    }
                }
            }
        }
    }
    catch (ex) {
        log.error "getWeatherAlerts Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex, "getWeatherAlerts")
    }
}

private pad(String s, size = 25) {
    try {
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
    catch (ex) {
        log.error "pad Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "pad")
    }
}


private estimateDewPoint(double rh,double t) {
    def L = Math.log(rh/100)
    def M = 17.27 * t
    def N = 237.3 + t
    def B = (L + (M/N)) / 17.27
    def dp = (237.3 * B) / (1 - B)

    def t1 = t * 9.0/5.0 + 32
    def dp1 = 243.04 * ( Math.log(rh / 100) + ( (17.625 * t1) / (243.04 + t1) ) ) / (17.625 - Math.log(rh / 100) - ( (17.625 * t1) / (243.04 + t1) ) ) 
    dp1 = (dp1 - 32) * 5.0/9.0
    def ave = (dp + dp1)/2
    //log.debug "dp: ${dp.round(1)}  dp1: ${dp1.round(1)} ave: ${ave.round(1)}" 
    return ave.round(1)
}


private estimateLux(weatherIcon) {
    //log.trace "estimateLux ( ${weatherIcon} )"
    try {
        if(!state?.sunriseDate?.time || !state?.sunsetDate?.time) { 
            log.warn "estimateLux: Weather Data missing..."
            return
        } else {
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
    }
    catch (ex) {
        log.error "estimateLux Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex, "estimateLux")
    }
}

def sendNofificationMsg(msg, msgType, recips = null, sms = null, push = null) {
    if(recips || sms || push) {
        parent?.sendMsg(msg, msgType, recips, sms, push)
        //LogAction("Send Push Notification to $recips...", "info", true)
    } else {
        parent?.sendMsg(msg, msgType)
    }
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

def formatDt(dt) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
    else {
        LogAction("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...", "warn", true)
    }
    return tf.format(dt)
}
/************************************************************************************************
|										LOGGING FUNCTIONS										|
*************************************************************************************************/
// Local Device Logging
def Logger(msg, logType = "debug") {
     if(state?.debug) { 
        switch (logType) {
            case "trace":
                log.trace "${msg}"
                break
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
                break
        }
    }
}
 
 //This will Print logs from the parent app when added to parent method that the child calls
def log(message, level = "trace") {
    switch (level) {
        case "trace":
            log.trace "PARENT_Log>> " + message
            break
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
            break
    }            
    return null // always child interface call with a return value
}

def getImgBase64(url, type) {
    try {
        def params = [ 
            uri: url,
            contentType: 'image/$type'
        ]
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
        log.error "getImageBase64 Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getImgBase64")
    }
}

def getCSS(){
    try {
        def params = [ 
            uri: state?.cssUrl.toString(),
            contentType: 'text/css'
        ]
        try {
            httpGet(params)  { resp ->
            return resp?.data.text
        }
    }
    catch (ex) {
        log.error "getCss Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getCSS")
    }
}
 catch (ex) {
        log.error "Failed to load CSS - Exception: $ex"
    }
}

def getWeatherIcon(weatherIcon) {
    try {
        return getImgBase64(state?.curWeather?.current_observation?.icon_url, gif)
    }
    catch (ex) {
        log.error "getWeatherIcon Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getWeatherIcon")
    }
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
    try {
        def df = new SimpleDateFormat("yyyy-MM-dd")
        df.setTimeZone(TimeZone.getTimeZone(timeZone))
        df.format(new Date())
    }
    catch (ex) {
        log.error "localDate Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "localDate")
    }
}

def getSunriseSunset() {
    // Sunrise / sunset
    try {
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
    } catch (ex) {
        log.error "getSunriseSunset Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getSunriseSunset")
    }
}


def forecastDay(day) {
    def dayName = "<b>${state.curForecast.forecast.txt_forecast.forecastday[day].title} </b><br>"
    def forecastImageLink = "<a href=\"#${day}\"><img src=\"${getImgBase64(state.curForecast.forecast.txt_forecast.forecastday[day].icon_url, gif)}\"></a><br>"
    def forecastTxt = ""
    
    def modalHead = "<div id=\"${day}\" class=\"bottomModal\"><div><a href=\"#close\" title=\"Close\" class=\"close\">X</a>"
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
    try {
        def updateAvail = !state.updateAvailable ? "" : "<h3>Device Update Available!</h3>"
        def html = """
        <!DOCTYPE html>
        <html>
            <head>
                <meta http-equiv="cache-control" content="max-age=0"/>
                <meta http-equiv="cache-control" content="no-cache"/>
                <meta http-equiv="expires" content="0"/>
                <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
                <meta http-equiv="pragma" content="no-cache"/>
                <meta name="viewport" content="width = device-width, initial-scale=1.0">
            </head>
            <body>
            <style type="text/css">
            ${getCSS()}
            </style>
                ${updateAvail}
                <div class="container">
                <h4>Current Weather Conditions</h4>
                <h3><a href="#openModal">${state?.walert}</a></h3>
                <h1 class="bottomBorder"> ${state?.curWeather?.current_observation?.display_location.full} </h1>
                    <div class="row">
                            <div class="six columns">
                            <b>Feels Like:</b> ${getFeelslike()} <br>
                            <b>Precip: </b> ${device.currentState("percentPrecip")?.value}% <br>
                            <b>Humidity:</b> ${state?.curWeather?.current_observation?.relative_humidity}<br>
                            <b>Dew Point: </b>${getDewpoint()}<br>
                            <b>UV Index: </b>${state.curWeather?.current_observation?.UV}<br>
                            <b>Visibility:</b> ${getVisibility()} <br>
                            <b>Lux:</b> ${getLux()}<br>
                            <b>Sunrise:</b> ${state?.localSunrise} <br> <b>Sunset: </b> ${state?.localSunset} <br>
                            <b>Wind:</b> ${state?.windStr} <br>
                        </div>
                        <div class="six columns">
                            <img class="offset-by-two eight columns" src="${getWeatherIcon()}"> <br>
                            <h2>${getTemp()}</h2>
                            <h1 class ="offset-by-two topBorder">${state.curWeatherCond}</h1>
                        </div>
                    </div>  
                    <div class="row topBorder">
                    <div class="centerText four columns">${forecastDay(0)}</div>
                    <div class="centerText four columns">${forecastDay(1)}</div>
                    <div class="centerText four columns">${forecastDay(2)}</div>
                    </div>
                    <div class="row">
                    <div class="centerText four columns">${forecastDay(3)}</div>
                    <div class="centerText four columns">${forecastDay(4)}</div>
                    <div class="centerText four columns">${forecastDay(5)}</div>
                    </div>
                    <div class="row">
                    <div class="centerText offset-by-two four columns">${forecastDay(6)}</div>
                    <div class="centerText four columns">${forecastDay(7)}</div>
                    </div>		
                    <div class="row topBorder">
                    <div class="centerText offset-by-three six columns">
                        <b>Station Id: ${state?.curWeather?.current_observation?.station_id}</b> 
                        <b>${state?.curWeather?.current_observation?.observation_time}</b>
                    </div>    
                    </div>
            
                <div id="openModal" class="topModal">
                        <div>
                            <a href="#close" title="Close" class="close">X</a>
                            <h2>Special Message</h2>
                            <p>${state?.walertMessage} </p>
                        </div>
                    </div>
                </div>
            </body>
        </html>
        """
        render contentType: "text/html", data: html, status: 200
    }
    catch (ex) {
        log.error "getWeatherHtml Exception: ${ex}"
        parent?.sendChildExceptionData("weather", devVer(), ex.toString(), "getWeatherHtml")
    }
}
private def textDevName()  { return "Nest Weather${appDevName()}" }
private def appDevType()   { return false }
private def appDevName()   { return appDevType() ? " (Dev)" : "" }
