/**
 *  Nest Weather
 *	Author: Anthony S. (@tonesto7)
 *  Author: Ben W. (@desertBlade)  Eric S. (@E_sch) 
 *	
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

// TODO: Need to update Copyright

import java.text.SimpleDateFormat
import org.apache.commons.codec.binary.Base64
import java.io.FileInputStream
import java.io.ByteArrayOutputStream

preferences {  }

def devVer() { return "1.0.0" }

// for the UI
metadata {
	definition (name: "Nest Weather", namespace: "tonesto7", author: "Anthony S.") {

        capability "Illuminance Measurement"
        capability "Sensor"
  		capability "Refresh"
        capability "Relative Humidity Measurement"
        capability "Temperature Measurement"
        
        command "refresh"
		command "log"
        
        attribute "lastConnection", "string"
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
        attribute "devTypeVer", "string"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		htmlTile(name:"weatherHtml", action: "getWeatherHtml2", width: 6, height: 6)
        
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
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
			state("default", label: 'Device Type:\nv${currentValue}')
		}
		main ("temp2")
		details ("weatherHtml", "refresh")
	}
}

mappings {
	path("/getInfoHtml") {action: [GET: "getInfoHtml"]}
	path("/getWeatherHtml2") {action: [GET: "getWeatherHtml2"]}
}

def initialize() {
	log.debug "initialize"
}

def parse(String description) {
	log.debug "Parsing '${description}'"
}

def configure() {
	
}

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
        state?.useMilitaryTime = !parent?.settings?.useMilitaryTime ? false : true
    	debugOnEvent(parent?.settings?.childDebug)
        apiStatusEvent(parent?.apiIssues())
        deviceVerEvent()
   	}
    lastUpdatedEvent()
    getWeatherConditions()
    getWeatherForecast()
 	return null
}

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
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
    	tf.setTimeZone(location?.timeZone)
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

def temperatureEvent(Double tempVal) {
	def temp = device.currentState("temperature")?.value.toString()
	def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
    if(!temp.equals(rTempVal.toString())) {
        log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
    	sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
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
       // log.debug "cur: $cur"
        state.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f).toInteger()
        state.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c).toInteger()
        state.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
        state.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
        state.curWeatherCond = cur?.current_observation?.weather.toString()
        state.curWeatherIcon = cur?.current_observation?.icon.toString()
		state.zipCode = cur?.current_observation?.display_location.zip.toString()
      
		
        def curTemp = (state?.tempUnit == "C") ? cur?.current_observation?.temp_c.toDouble() : cur?.current_observation?.temp_f.toDouble()
        def curWeatherTemp = (state?.tempUnit == "C") ? "${state?.curWeatherTemp_c}°C": "${state?.curWeatherTemp_f}°F"
        state.curWeatherTemp = curWeatherTemp
        temperatureEvent(curTemp)
        humidityEvent(state?.curWeatherHum)
        illuminanceEvent(estimateLux(state.curWeatherIcon))

        Logger("${state?.curWeatherLoc} Weather | humidity: ${state?.curWeatherHum} | temp_f: ${state?.curWeatherTemp_f} | temp_c: ${state?.curWeatherTemp_c} | Current Conditions: ${state?.curWeatherCond}")
    }
}

def getWeatherForecast() {
    def cur = parent?.getWForecastData()
    if(cur) {
        state.curForecast = cur
        //log.debug "cur: $cur"
    }
}

private estimateLux(weatherIcon) {
        //log.trace "estimateLux ( ${weatherIcon} )"
        def sunriseDate = null
        def sunsetDate = null
        sunriseDate = parent.sunrise()
        sunsetDate = parent.sunset()
        def lux = 0
        def now = new Date().time
        //log.trace "sunrise: ${sunriseDate.time}  now: ${now} sunset: ${sunsetDate.time}"
        if (now < sunsetDate.time && sunriseDate.time > sunsetDate.time) {
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
                //def afterSunrise = now - sunriseDate.time
                def afterSunrise = now - (sunriseDate.time - (1000*60*60*24) - 60*100*3)
                def beforeSunset = sunsetDate.time - now
                def oneHour = 1000 * 60 * 60

                if(afterSunrise < oneHour) {
                        //dawn
                        lux = (long)(lux * (afterSunrise/oneHour))
                } else if (beforeSunset < oneHour) {
                        //dusk
                        lux = (long)(lux * (beforeSunset/oneHour))
                }
        }
        else {
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
	if ( state?.tempUnit == "C" ) {
    	return "${state?.curWeather?.current_observation?.feelslike_c}°C"
    } else {
    	return "${state?.curWeather?.current_observation?.feelslike_f}°F"
    }
}

def getLux() {
	return estimateLux(state?.curWeather?.current_observation?.icon)
}

private localDate(timeZone) {
	def df = new SimpleDateFormat("yyyy-MM-dd")
	df.setTimeZone(TimeZone.getTimeZone(timeZone))
	df.format(new Date())
}

private get(feature) {
	getWeatherFeature(feature, "${ state?.curWeather?.current_observation.display_location.zip}")
}

def getSunriseSunset() {
		// Sunrise / sunset
		def a = get("astronomy")?.moon_phase
		def today = localDate("GMT${state.curWeather?.current_observation?.local_tz_offset}")

    		def ltf = new SimpleDateFormat("yyyy-MM-dd HH:mm")
         
		ltf.setTimeZone(TimeZone.getTimeZone("GMT${state.curWeather?.current_observation?.local_tz_offset}"))

    def utf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    utf.setTimeZone(TimeZone.getTimeZone("GMT"))

		def sunriseDate = ltf.parse("${today} ${a.sunrise.hour}:${a.sunrise.minute}")
		def sunsetDate = ltf.parse("${today} ${a.sunset.hour}:${a.sunset.minute}")

        def tf = new java.text.SimpleDateFormat("h:mm a")
        tf.setTimeZone(TimeZone.getTimeZone("GMT${state.curWeather?.current_observation?.local_tz_offset}"))
        def localSunrise = "${tf.format(sunriseDate)}"
        def localSunset = "${tf.format(sunsetDate)}"
      
		def sunriseSunset = "<b>Sunrise:</b> ${localSunrise} <br> <b>Sunset: </b> ${localSunset} <br>"
   	
   return sunriseSunset
}


def getWeatherHtml2() { 
	renderHTML {
    	head {
        	"""
          <style type="text/css">
            body {
              font-family: 'Lato', Calibri, Arial, sans-serif;
            }

            #header {
              font-size: 4vw;
              font-weight: bold;
              text-align: center;
              background: #00a1db;
              color: #f5f5f5;
            }

            #weatherInfo {
              text-align: left;
            }
			#leftData {
            	width:50%;
                float:left;
                clear: left;
              }
            #city {
            	font-size: 6vw;
                width:100%;
                text-align: center;
                border-bottom:2px solid #00a1db;
                }
            
            #temp {
    		  font-size: 9vw;
              border-bottom:2px solid #00a1db;
            }

            #data {
              font-size: 4vw;
              padding: 5px;
            }
           

            #weatherIcon {
              float: right;
              clear: right;
              width: 50%;
              font-size: 6vw;
              text-align: right;
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

        </style>
           	"""
        }
        body {
        	"""
            <div class="container">
  <div id="header">Current Weather Conditions</div>
  <div id="weatherInfo">
    	<div id="city"> ${state?.curWeather?.current_observation?.observation_location.full} </div>
  <div id="leftData">
    <div id="temp">
      ${getTemp()}
    </div>
       <div id="data">
          <b>Feels Like:</b> ${getFeelslike()} <br>
          <b>Humidity:</b> ${state?.curWeather?.current_observation?.relative_humidity}<br>
         <b>UV Index: </b>${state.curWeather?.current_observation?.UV}<br>
          <b>Visibility:</b> ${state.curWeather?.current_observation?.visibility_mi} Miles<br>
          <b>Lux:</b> ${getLux()}<br>
     		${getSunriseSunset()}
          <b>Wind:</b> ${state?.curWeather?.current_observation?.wind_string} <br>
    </div>
    
    </div>

<div id="weatherIcon">
      <img src="${getWeatherIcon()}"> <br>
      <b>${state.curWeatherCond}</b>
  </div>
    


</div>
	"""
        }
    }
}
