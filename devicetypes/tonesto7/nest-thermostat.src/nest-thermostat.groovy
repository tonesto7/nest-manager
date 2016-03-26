/**
 *  Nest Thermostat
 *	Author: Anthony S. (@tonesto7)
 *	Contributor: Ben W. (@desertBlade) & Eric S. (@E_Sch)
 
 * Based off of the EcoBee thermostat under Templates in the IDE 
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

def devVer() { return "1.1.3"}

// for the UI
metadata {
	definition (name: "Nest Thermostat", namespace: "tonesto7", author: "Anthony S.") {
		capability "Actuator"
        capability "Polling"
		capability "Relative Humidity Measurement"
        capability "Refresh"
        capability "Sensor"
		capability "Thermostat"
        capability "Thermostat Cooling Setpoint"
		capability "Thermostat Fan Mode"
		capability "Thermostat Heating Setpoint"
		capability "Thermostat Mode"
		capability "Thermostat Operating State"
		capability "Thermostat Setpoint"
		capability "Temperature Measurement"
		
        command "refresh"
        command "poll"
        
        command "away"
        command "present"
		//command "setAway"
        //command "setHome"
        command "setPresence"
        command "setFanMode"
        command "setTemperature"
        command "setThermostatMode"
        command "levelUpDown"
 	    command "levelUp"
        command "levelDown"
		command "log"
		command "heatingSetpointUp"
		command "heatingSetpointDown"
		command "coolingSetpointUp"
		command "coolingSetpointDown"

		attribute "temperatureUnit", "string"
        attribute "targetTemp", "string"
        attribute "softwareVer", "string"
        attribute "lastConnection", "string"
        attribute "nestPresence", "string"
        attribute "apiStatus", "string"
        attribute "hasLeaf", "string"
        attribute "debugOn", "string"
        attribute "devTypeVer", "string"
        attribute "onlineStatus", "string"
        attribute "nestPresence", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}
                    
    tiles(scale: 2) {
		multiAttributeTile(name:"temperature", type:"thermostat", width:6, height:4, canChangeIcon: true) {
  			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
    			attributeState("default", label:'${currentValue}°')
  			}
  			tileAttribute("device.temperature", key: "VALUE_CONTROL") {
    			//attributeState("default", action: "setTemperature")
                //attributeState("VALUE_UP", action: "temperatureUp")
 				//attributeState("VALUE_DOWN", action: "temperatureDown")
            	attributeState("default", action: "levelUpDown")
 				attributeState("VALUE_UP", action: "levelUp")
  				attributeState("VALUE_DOWN", action: "levelDown")
  			}
  			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'${currentValue}%', unit:"%")
  			}
  			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
   				attributeState("idle",backgroundColor:"#44b621")
   				attributeState("heating",backgroundColor:"#ffa81e")
   				attributeState("fan only",		backgroundColor:"#2ABBF0")
            	attributeState("pending heat",	backgroundColor:"#2ABBF0")
            	attributeState("pending cool",	backgroundColor:"#2ABBF0")
            	attributeState("vent economizer",	backgroundColor:"#2ABBF0")
  			}
  			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
    			attributeState("off", label:'${name}')
    			attributeState("heat", label:'${name}')
    			attributeState("cool", label:'${name}')
    			attributeState("auto", label:'${name}')
                attributeState("emergency Heat", label:'${name}')
  			}
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
    			attributeState("default", label:'${currentValue}')
  			}
  			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
    			attributeState("default", label:'${currentValue}')
  			}
        }
        standardTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
        	state("default", label:'${currentValue}°', 	icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png")
        }
        standardTile("mode2", "device.thermostatMode", width: 2, height: 2, decoration: "flat") {
	        state("off",  icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_off_icon.png")
			state("heat", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_heat_icon.png")
            state("cool", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_cool_icon.png")
            state("auto", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_heat_cool_icon.png")
        }
        standardTile("thermostatMode", "device.thermostatMode", width:2, height:2, decoration: "flat") {
			//state("off", 	action:"thermostat.heat", 	nextState: "heat", 	icon: "st.thermostat.heating-cooling-off")
			//state("heat", action:"thermostat.cool", 	nextState: "cool", 	icon: "st.thermostat.heat")
            //state("cool", action:"thermostat.auto", 	nextState: "auto", 	icon: "st.thermostat.cool")
            //state("auto", action:"thermostat.off", 	nextState: "off", 	icon: "st.thermostat.auto")
            state("off", 	action:"thermostat.heat", 	nextState: "heat", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_off.png")
			state("heat", 	action:"thermostat.cool", 	nextState: "cool", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_heat.png")
            state("cool", 	action:"thermostat.auto", 	nextState: "auto", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_cool.png")
            state("auto", 	action:"thermostat.off", 	nextState: "off", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_auto.png")
            state("emergency heat", action:"thermostat.heat", nextState: "heat", icon: "st.thermostat.emergency")
		}
       standardTile("thermostatFanMode", "device.thermostatFanMode", width:2, height:2, decoration: "flat") {
       		//state "auto",action:"fanOn", icon: "st.thermostat.fan-auto"
            //state "on",action:"fanAuto", icon: "st.thermostat.fan-on"
			state "auto",	action:"fanOn", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_auto_icon.png"
            state "on",		action:"fanAuto", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_on_icon.png"
		}
		standardTile("nestPresence", "device.nestPresence", width:2, height:2, decoration: "flat") {
        	//state "present", 	label:'home', 		action: "setPresence",	icon: "st.Home.home2"
			//state "away", 	label:'away', 		action: "setPresence", 	icon: "st.Transportation.transportation5"
            //state "auto-away",label:'auto\naway', action: "setPresence", 	icon: "st.Transportation.transportation5"
			state "present", 	action: "setPresence",	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_home_icon.png"
			state "away", 		action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_away_icon.png"
            state "auto-away", 	action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_autoaway_icon.png"
        	state "unknown",	action: "setPresence", 	icon: "st.unknown.unknown.unknown"
		}
		standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        valueTile("softwareVer", "device.softwareVer", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Firmware:\nv${currentValue}')
		}
		valueTile("hasLeaf", "device.hasLeaf", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Leaf:\n${currentValue}')
		}
        valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Network Status:\n${currentValue}')
            //state("on", label: 'Network Status:\n\Online', backgroundColor:"#44b621")
            //state("off", label: 'Network Status:\nOffline', backgroundColor:"#bc2323")
		}
        valueTile("debugOn", "device.debugOn", width: 2, height: 1, decoration: "flat") {
			state "true", 	label: 'Debug:\n${currentValue}'
            state "false", 	label: 'Debug:\n${currentValue}'
		}
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
			state("default", label: 'Device Type:\nv${currentValue}')
		}
        standardTile("heatingSetpointUp", "device.heatingSetpoint", width: 1, height: 1, canChangeIcon: false,  decoration: "flat") {
			state "heatingSetpointUp", label:'  ', action:"heatingSetpointUp", icon:"st.thermostat.thermostat-up", backgroundColor:"#bc2323"
		}
        
        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 1, height: 1, canChangeIcon: false) {
			state "default", label:'${currentValue}', unit:"Heat", backgroundColor:"#bc2323"
		}
        
        valueTile("coolingSetpoint", "device.coolingSetpoint", width: 1, height: 1, canChangeIcon: false) {
			state "default", label:'${currentValue}', unit:"Cool", backgroundColor:"#1e9cbb"
		}

		standardTile("heatingSetpointDown", "device.heatingSetpoint",  width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
			state "heatingSetpointDown", label:'  ', action:"heatingSetpointDown", icon:"st.thermostat.thermostat-down", backgroundColor:"#bc2323"
		}
        
        standardTile("coolingSetpointUp", "device.coolingSetpoint", width: 1, height: 1,canChangeIcon: false, decoration: "flat") {
			state "coolingSetpointUp", label:'  ', action:"coolingSetpointUp", icon:"st.thermostat.thermostat-up", backgroundColor:"#1e9cbb"
		}

		standardTile("coolingSetpointDown", "device.coolingSetpoint", width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
			state "coolingSetpointDown", label:'  ', action:"coolingSetpointDown", icon:"st.thermostat.thermostat-down", backgroundColor:"#1e9cbb"
		}
        
        valueTile("lastConnection", "device.lastConnection", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Nest Last Checked-In:\n${currentValue}')
	    }
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Data Last Received:\n${currentValue}')
	    }
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
        	state "ok", label: "API Status:\nOK"
            state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
		}
		main( tileMain() )
		details( tileSelect() )
	}
}

def tileMain() { 
    return ["temp2"]
}

def tileSelect() { 
	//log.debug "hvacMode: ${getHvacMode()}"
	if(getHvacMode() == "heat" || getHvacMode() == "cool") {
    	//log.debug "tileSelect if | hvacMode: ${getHvacMode()}"
        return ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "onlineStatus", "apiStatus", "softwareVer", "hasLeaf", "lastConnection", "refresh", 
        		"lastUpdatedDt", "softwareVer", "debugOn", "devTypeVer"] 
    } 
    else if(getHvacMode() == "auto" || getHvacMode() == "unknown") { 
    	//log.debug "tileSelect else if | hvacMode: ${getHvacMode()}"
    	return ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp", 
        		"coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "onlineStatus", "apiStatus", "hasLeaf", "lastConnection", "refresh", 
                "lastUpdatedDt", "softwareVer", "debugOn", "devTypeVer"]
    }
}

def initialize() {
	log.debug "initialize"
}

def parse(String description) {
	log.debug "Parsing '${description}'"
}

def poll() {
	log.debug "Polling parent..."
    parent.refresh()
}

def refresh() {
	parent.refresh()
}

def generateEvent(Map results) {
	//Logger("generateEvents Parsing data ${results}")
  	Logger("-------------------------------------------------------------------", "warn")
	
    if(results) {
        state.useMilitaryTime = !parent?.settings?.useMilitaryTime ? false : true
        debugOnEvent(parent.settings?.childDebug)
		tempUnitEvent(results?.temperature_scale)   // Maybe use getTemperatureScale()  // SmartThings built-in to get ST scale rather than Nest scale
		canHeatCool(results?.can_heat, results?.can_cool)
        hasFan(results?.has_fan.toString())
        presenceEvent(parent?.locationPresence())
        hvacModeEvent(results?.hvac_mode.toString())
        hasLeafEvent(results?.has_leaf)
        humidityEvent(results?.humidity.toString())
        operatingStateEvent(results?.hvac_state.toString())
 		fanModeEvent(results?.fan_timer_active.toString())
        lastCheckinEvent(results?.last_connection)
        softwareVerEvent(results?.software_version.toString())
        onlineStatusEvent(results?.is_online.toString())
        deviceVerEvent()
        apiStatusEvent(parent?.apiIssues())
       
		def hvacMode = results?.hvac_mode
		def tempUnit = device.latestValue('temperatureUnit')
		switch (tempUnit) {
			case "C":
				def heatingSetpoint = 0.0
				def coolingSetpoint = 0.0
				def temp = results?.ambient_temperature_c.toDouble() 
				def targetTemp = results?.target_temperature_c.toDouble()

				if (hvacMode == "cool") { 
                	coolingSetpoint = targetTemp
                	//clearHeatingSetpoint()
                } 
                else if (hvacMode == "heat") { 
                	heatingSetpoint = targetTemp 
                	//clearCoolingSetpoint()
                } 
                else if (hvacMode == "heat-cool") {
					coolingSetpoint = Math.round(results?.target_temperature_high_c.toDouble())
					heatingSetpoint = Math.round(results?.target_temperature_low_c.toDouble())
				}
				if (!state?.present) {
					if (results?.away_temperature_high_c) { coolingSetpoint = results?.away_temperature_high_c.toDouble() }
					if (results?.away_temperature_low_c) { heatingSetpoint = results?.away_temperature_low_c.toDouble() }
				}
                temperatureEvent(temp)
                thermostatSetpointEvent(targetTemp)
				coolingSetpointEvent(coolingSetpoint)
				heatingSetpointEvent(heatingSetpoint)
				break
                
			case "F":
            	def heatingSetpoint = 0
                def coolingSetpoint = 0
                def temp = results?.ambient_temperature_f
                def targetTemp = results?.target_temperature_f
				
                if (hvacMode == "cool") { 
                	coolingSetpoint = targetTemp
                	//clearHeatingSetpoint()
                } 
                else if (hvacMode == "heat") { 
                	heatingSetpoint = targetTemp
                	//clearCoolingSetpoint()
                } 
                else if (hvacMode == "heat-cool") {
					coolingSetpoint = results?.target_temperature_high_f
					heatingSetpoint = results?.target_temperature_low_f
				}
				if (!state?.present) {
					if (results?.away_temperature_high_f) { coolingSetpoint = results?.away_temperature_high_f }
					if (results?.away_temperature_low_f)  { heatingSetpoint = results?.away_temperature_low_f }
				}
				temperatureEvent(temp)
                thermostatSetpointEvent(targetTemp)
				coolingSetpointEvent(coolingSetpoint)
				heatingSetpointEvent(heatingSetpoint)
				break
			
            default:
 	           Logger("no Temperature data $tempUnit")
               break
        }
	}
    lastUpdatedEvent()
    return null
}

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def deviceVerEvent() {
    def curData = device.currentState("devTypeVer")?.value
    def pubVer = parent?.latestProtVer().ver.toString()
	def dVer = devVer() ? devVer() : null
    def newData = (pubVer != dVer) ? "${dVer}(New: v${pubVer})" : "${dVer}(Current)"
    if(curData != newData) {
        Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
    	sendEvent(name: 'devTypeVer', value: newData, displayed: false)
    } else { Logger("Device Type Version is: (${newData}) | Original State: (${curData})") }
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
    def stateVal = debug ? "On" : "Off"
	if(!val.equals(stateVal)) {
    	log.debug("UPDATED | debugOn: (${stateVal}) | Original State: (${val})")
        sendEvent(name: 'debugOn', value: stateVal, displayed: false)
   	} else { Logger("debugOn: (${stateVal}) | Original State: (${val})") }
}

def lastCheckinEvent(checkin) {
	//log.trace "lastCheckinEvent()..."
    def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    	tf.setTimeZone(location?.timeZone)
   	def lastConn = "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}"
	def lastChk = device.currentState("lastConnection")?.value
    if(!lastChk.equals(lastConn?.toString())) {
        log.debug("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
        sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: false, isStateChange: true)
    } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
}

def lastUpdatedEvent() {
    def now = new Date()
    def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    	tf.setTimeZone(location?.timeZone)
   	def lastDt = "${tf?.format(now)}"
	def lastUpd = device.currentState("lastUpdatedDt")?.value
    if(!lastUpd.equals(lastDt?.toString())) {
        Logger("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
    	sendEvent(name: 'lastUpdatedDt', value: lastDt?.toString(), displayed: false, isStateChange: true)
    }
}

def softwareVerEvent(ver) {
    def verVal = device.currentState("softwareVer")?.value
    if(!verVal.equals(ver)) {
    	log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
        sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now ${ver}", displayed: false, isStateChange: true)
    } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
}

def tempUnitEvent(unit) {
	def tmpUnit = device.currentState("temperatureUnit")?.value
	if(!tmpUnit.equals(unit)) {   
    	log.debug("UPDATED | Temperature Unit: (${unit}) | Original State: (${tmpUnit})")
        sendEvent(name:'temperatureUnit', value: unit, descriptionText: "Temperature Unit is now: '${unit}'", displayed: true, isStateChange: true)
        state?.tempUnit = unit
    } else { Logger("Temperature Unit: (${unit}) | Original State: (${tmpUnit})") }
}

def targetTempEvent(targetTemp) {
	def temp = device.currentState("targetTemperature")?.value.toString()
	def rTargetTemp = wantMetric() ? targetTemp.round(1) : targetTemp.round(0).toInteger()
	if(!temp.equals(rTargetTemp.toString())) {
	    log.debug("UPDATED | thermostatSetPoint Temperature is (${rTargetTemp}) | Original Temp: (${temp})")
	    sendEvent(name:'targetTemperature', value: rTargetTemp, unit: state?.tempUnit, descriptionText: "Target Temperature is ${rTargetTemp}", displayed: false, isStateChange: true)
	} else { Logger("targetTemperature is (${rTargetTemp}) | Original Temp: (${temp})") }
}

def thermostatSetpointEvent(Double targetTemp) {
	def temp = device.currentState("thermostatSetpoint")?.value.toString()
	def rTargetTemp = wantMetric() ? targetTemp.round(1) : targetTemp.round(0).toInteger()
	if(!temp.equals(rTargetTemp.toString())) {
	    log.debug("UPDATED | thermostatSetPoint Temperature is (${rTargetTemp}) | Original Temp: (${temp})")
	    sendEvent(name:'thermostatSetpoint', value: rTargetTemp, descriptionText: "thermostatSetpoint Temperature is ${rTargetTemp}", displayed: false, isStateChange: true)
	} else { Logger("thermostatSetpoint is (${rTargetTemp}) | Original Temp: (${temp})") }
}

def temperatureEvent(Double tempVal) {
	def temp = device.currentState("temperature")?.value.toString()
	def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
    if(!temp.equals(rTempVal.toString())) {
        log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
    	sendEvent(name:'temperature', value: rTempVal, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
    } else { Logger("Temperature is (${rTempVal}) | Original Temp: (${temp})") }
}

def heatingSetpointEvent(Double tempVal) {
	def temp = device.currentState("heatingSetpoint")?.value.toString()
    def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
	if(!temp.equals(rTempVal.toString())) {
        log.debug("UPDATED | HeatingSetpoint is (${rTempVal}) | Original Temp: (${temp})")
        def disp = false
		def hvacMode = getHvacMode()
		if (hvacMode == "auto" || hvacMode == "heat") { disp = true }
    	sendEvent(name:'heatingSetpoint', value: rTempVal, descriptionText: "Heat Setpoint is ${rTempVal}" , displayed: disp, isStateChange: true, state: "heat")
    } else { Logger("HeatingSetpoint is (${rTempVal}) | Original Temp: (${temp})") }
}

def coolingSetpointEvent(Double tempVal) {
	def temp = device.currentState("coolingSetpoint")?.value.toString()
	def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
	if(!temp.equals(rTempVal.toString())) {
        log.debug("UPDATED | CoolingSetpoint is (${rTempVal}) | Original Temp: (${temp})")
        def disp = false
		def hvacMode = getHvacMode()
		if (hvacMode == "auto" || hvacMode == "cool") { disp = true }
    	sendEvent(name:'coolingSetpoint', value: rTempVal, descriptionText: "Cool Setpoint is ${rTempVal}" , displayed: disp, isStateChange: true, state: "cool")
    } else { Logger("CoolingSetpoint is (${rTempVal}) | Original Temp: (${temp})") }
}

def hasLeafEvent(Boolean hasLeaf) {
	def leaf = device.currentState("hasLeaf")?.value
    def lf = hasLeaf ? "On" : "Off"
	if(!leaf.equals(lf)) {
        log.debug("UPDATED | Leaf is set to (${lf}) | Original State: (${leaf})")
		sendEvent(name:'hasLeaf', value: lf,  descriptionText: "Leaf: ${lf}" , displayed: false, isStateChange: true, state: lf)
    } else { Logger("Leaf is set to (${lf}) | Original State: (${leaf})") }
}

def humidityEvent(humidity) {
	def hum = device.currentState("humidity")?.value
	if(!hum.equals(humidity)) {
        log.debug("UPDATED | Humidity is (${humidity}) | Original State: (${hum})")
		sendEvent(name:'humidity', value: humidity, unit: "%", descriptionText: "Humidity is ${humidity}" , displayed: false, isStateChange: true)
    } else { Logger("Humidity is (${humidity}) | Original State: (${hum})") }
}

def presenceEvent(presence) {
	def val = device.currentState("presence")?.value
	def pres = (presence == "home") ? "present" : "not present"
    def nestPres = getNestPresence()
    def newNestPres = (presence == "home") ? "present" : ((presence == "auto-away") ? "auto-away" : "away")
    state?.nestPresence = newNestPres
    if(!val.equals(pres) || !nestPres.equals(newNestPres)) {
        log.debug("UPDATED | Presence: ${pres} | Original State: ${val} | State Variable: ${state?.present}")
   		sendEvent(name: 'nestPresence', value: newNestPres, descriptionText: "Nest Presence is: ${newNestPres}", displayed: true, isStateChange: true )
		sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: false, isStateChange: true, state: pres )
   		state?.present = (pres == "present") ? true : false
    } else { Logger("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
}

def hvacModeEvent(mode) {
	def pres = getNestPresence()
	def hvacMode = getHvacMode()
    def newMode = (parent?.settings?.showAwayAsAuto && ((pres == "away" || pres == "auto-away") && (mode == "heat" || mode == "cool")) || (mode == "heat-cool")) ? "auto" : mode
    if(!hvacMode.equals(newMode)) {
		log.debug("UPDATED | Hvac Mode is (${newMode}) | Original State: (${hvacMode})")
   		sendEvent(name: "thermostatMode", value: newMode, descriptionText: "HVAC mode is ${newMode} mode", displayed: true, isStateChange: true)
        state?.hvac_mode = newMode
   	} else { Logger("Hvac Mode is (${newMode}) | Original State: (${hvacMode})") }
} 

def fanModeEvent(fanActive) {
	def val = (fanActive == "true") ? "on" : "auto"
	def fanMode = device.currentState("thermostatFanMode")?.value
	if(!fanMode.equals(val) && state?.has_fan == true) {
		log.debug("UPDATED | Fan Mode: (${val}) | Original State: (${fanMode})")
        sendEvent(name: "thermostatFanMode", value: val, descriptionText: "Fan Mode is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("Fan Active: (${val}) | Original State: (${fanMode})") }
}

def operatingStateEvent(operatingState) {
	def hvacState = device.currentState("thermostatOperatingState")?.value
	def operState = (operatingState == "off") ? "idle" : operatingState
    if(!hvacState.equals(operState)) {
    	log.debug("UPDATED | OperatingState is (${operState}) | Original State: (${hvacState})")
		sendEvent(name: 'thermostatOperatingState', value: operState, descriptionText: "Device is ${operState}", displayed: true, isStateChange: true)
    } else { Logger("OperatingState is (${operState}) | Original State: (${hvacState})") }
}

def onlineStatusEvent(online) {
	def isOn = device.currentState("onlineStatus")?.value
    def val = online ? "Online" : "Offline"
	if(!isOn.equals(val)) { 
        log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
   		sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
}

def apiStatusEvent(issue) {
	def appStat = device.currentState("apiStatus")?.value
    def val = issue ? "issue" : "ok"
	if(!appStat.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${appStat})")
   		sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${appStat})") }
}

def canHeatCool(canHeat, canCool) {
    state?.can_heat = !canHeat ? false : true
    state?.can_cool = !canCool ? false : true
}

def hasFan(hasFan) {
	state?.has_fan = !hasFan ? false : true
}	

def isEmergencyHeat(val) {
	state?.is_using_emergency_heat = !val ? false : true
}

def clearHeatingSetpoint() {
    sendEvent(name:'heatingSetpoint', value: "",  descriptionText: "Clear Heating Setpoint" , display: false, displayed: true, isStateChange: true)
	state?.heating_setpoint = ""
}

def clearCoolingSetpoint() {
    sendEvent(name:'coolingSetpoint', value: "",  descriptionText: "Clear Cooling Setpoint" , display: false, displayed: true, isStateChange: true)
    state?.cooling_setpoint = ""
}

def getCoolTemp() { 
	try { return device.currentValue("coolingSetpoint") } 
	catch (e) { return 0 }
}

def getHeatTemp() { 
	try { return device.currentValue("heatingSetpoint") } 
	catch (e) { return 0 }
}

def getFanMode() { 
	try { return device.currentState("thermostatFanMode")?.value.toString() } 
	catch (e) { return "unknown" }
}

def getHvacMode() { 
	try { return device.currentState("thermostatMode")?.value.toString() } 
	catch (e) { return "unknown" }
}

def getNestPresence() { 
	try { return device.currentState("nestPresence").value.toString() } 
	catch (e) { return "present" }
}

def getPresence() { 
	try { return device.currentState("presence").value.toString() }
  	catch (e) { return "present" }
}

def getTargetTemp() { 
	try { return device.currentValue("targetTemperature") } 
	catch (e) { return 0 }
}

def getThermostatSetpoint() { 
	try { return device.currentValue("thermostatSetpoint") } 
	catch (e) { return 0 }
}

def getTemp() { 
	try { return device.currentValue("temperature") } 
	catch (e) { return 0 }
}

def tempWaitVal() { return parent?.getChildWaitVal() ? parent?.getChildWaitVal().toInteger() : 4 }

def wantMetric() { return (device.currentValue('temperatureUnit') == "C") }


/************************************************************************************************
|							Temperature Setpoint Functions for Buttons							|
*************************************************************************************************/
void heatingSetpointUp() {
	log.trace "heatingSetpointUp()..."
	def operMode = getHvacMode()
	if ( operMode == "heat" || operMode == "auto" ) {
		levelUpDown(1,"heat")
	}
}

void heatingSetpointDown() {
	log.trace "heatingSetpointDown()..."
	def operMode = getHvacMode()
	if ( operMode == "heat" || operMode == "auto" ) {
	   	levelUpDown(-1, "heat")
	}
}

void coolingSetpointUp() {
	log.trace "coolingSetpointUp()..."
	def operMode = getHvacMode()
	if ( operMode == "cool" || operMode == "auto" ) {
        levelUpDown(1, "cool")
	}
}

void coolingSetpointDown() {
	log.trace "coolingSetpointDown()..."
	def operMode = getHvacMode()
	if ( operMode == "cool" || operMode == "auto" ) {
        levelUpDown(-1, "cool")
	}
}

void levelUp() {
	//log.trace "levelUp()..."
	levelUpDown(1)
}

void levelDown() {
    //log.trace "levelDown()..."
    levelUpDown(-1)
}

void levelUpDown(tempVal, chgType = null) {
	//log.trace "levelUpDown()...($tempVal | $chgType)"
	def hvacMode = getHvacMode()
    
    if (canChangeTemp()) {
    // From RBOY https://community.smartthings.com/t/multiattributetile-value-control/41651/23
    // Determine OS intended behaviors based on value behaviors (urrgghhh.....ST!)
        def upLevel 
        
        if (!state?.lastLevelUpDown) { state.lastLevelUpDown = 0 } // If it isn't defined lets baseline it

        if ((state.lastLevelUpDown == 1) && (tempVal == 1)) { upLevel = true } //Last time it was 1 and again it's 1 its increase
        	
        else if ((state.lastLevelUpDown == 0) && (tempVal == 0)) { upLevel = false } //Last time it was 0 and again it's 0 then it's decrease
        	
        else if ((state.lastLevelUpDown == -1) && (tempVal == -1)) { upLevel = false } //Last time it was -1 and again it's -1 then it's decrease
        	
        else if ((tempVal - state.lastLevelUpDown) > 0) { upLevel = true } //If it's increasing then it's up
        	
        else if ((tempVal - state.lastLevelUpDown) < 0) { upLevel = false } //If it's decreasing then it's down
		
        else { log.error "UNDEFINED STATE, CONTACT DEVELOPER. Last level $state.lastLevelUpDown, Current level, $value" }

		state.lastLevelUpDown = tempVal // Save it

		def targetVal = 0.0
        def tempUnit = device.currentValue('temperatureUnit')
        def curHeatpoint = device.currentValue("heatingSetpoint")
        def curCoolpoint = device.currentValue("coolingSetpoint")
        def curThermSetpoint = device.latestValue("thermostatSetpoint")
        targetVal = curThermSetpoint ?: 0.0
		if (hvacMode == "auto") {
			if (chgType == "cool") { 
				targetVal = curCoolpoint
				curThermSetpoint = targetVal
			}
			if (chgType == "heat") { 
				targetVal = curHeatpoint
				curThermSetpoint = targetVal
			}
		}

		if (upLevel) {
            //log.debug "Increasing by 1 increment"
            if (tempUnit == "C" ) {
                targetVal = targetVal.toDouble() + 0.5
                if (targetVal < 9.0) { targetVal = 9.0 }
                if (targetVal > 32.0 ) { targetVal = 32.0 }
            } else {
                targetVal = targetVal.toDouble() + 1.0
                if (targetVal < 51.0) { targetVal = 51.0 }
                if (targetVal > 89.0) { targetVal = 89.0 }
            }
        } else {
            //log.debug "Reducing by 1 increment"
            if (tempUnit == "C" ) {
                targetVal = targetVal.toDouble() - 0.5
                if (targetVal < 9.0) { targetVal = 9.0 }
                if (targetVal > 32.0 ) { targetVal = 32.0 }
            } else {
                targetVal = targetVal.toDouble() - 1.0
                if (targetVal < 51.0) { targetVal = 51.0 }
                if (targetVal > 89.0) { targetVal = 89.0 }
            }
        }

        if (targetVal != curThermSetpoint ) {
			switch (hvacMode) {
    			case "heat":
                	Logger("Sending changeSetpoint(Temp: ${targetVal})") 
                	thermostatSetpointEvent(targetVal)
                	heatingSetpointEvent(targetVal)
                	if (!chgType) { chgType = "" }
                	runIn( tempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
            		break
				case "cool":
                	Logger("Sending changeSetpoint(Temp: ${targetVal})") 
                	thermostatSetpointEvent(targetVal)
                	coolingSetpointEvent(targetVal)
                	if (!chgType) { chgType = "" }
                	runIn( tempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
            		break
        		case "auto":
          			if (chgType) {
						switch (chgType) {
							case "cool":
								Logger("Sending changeSetpoint(Temp: ${targetVal})")
								coolingSetpointEvent(targetVal)
								runIn( tempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
								break
							case "heat":
								Logger("Sending changeSetpoint(Temp: ${targetVal})")
								heatingSetpointEvent(targetVal)
								runIn( tempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
								break
							default:
                            	log.warn "Can not change temp while in this mode ($chgType}!!!"
								break
						}
					} else { log.warn "Temp Change without a chgType is not supported!!!" }
        			break
        		default:
                	log.warn "Unsupported Mode Received: ($hvacMode}!!!"
        			break
        	}
     	}
   	} else { log.debug "levelUpDown: Cannot adjust temperature due to presence: $state?.present or hvacMode $hvacMode" }
}

// Nest does not allow temp changes in away modes
def canChangeTemp() {
    //log.trace "canChangeTemp()..."
    def curPres = getNestPresence()
    if (curPres == "home") {
		def hvacMode = getHvacMode()
		switch (hvacMode) {
    		case "heat":
            	return true
            	break
			case "cool":
            	return true
            	break
         	case "auto":
            	return true
        		break
        	default:
            	return false
        		break
    	}
    } else { return false }
}

void changeSetpoint(val) {
	//log.trace "changeSetpoint()... ($val)"
	if ( canChangeTemp() ) {
		def temp = val?.temp?.value.toDouble()
    	def md = !val?.mode?.value ? null : val?.mode?.value
		def hvacMode = getHvacMode()
    
    	switch (hvacMode) {
    		case "heat":
           		setHeatingSetpoint(temp)
            	break
			case "cool":
        		setCoolingSetpoint(temp)
            	break
         	case "auto":
         		if(md) {
	       			if("${md}" == "heat") { setHeatingSetpoint(temp) }
           			else if ("${md}" == "cool") { setCoolingSetpoint(temp) }
                	else { log.warn "changeSetpoint: Invalid Temp Type received... ${md}" }
            	}
        		break
        	default:
				def curHeatpoint = device.currentValue("heatingSetpoint")
				def curCoolpoint = device.currentValue("coolingSetpoint")
				if (curHeatpoint > curCoolpoint) {
				log.warn "changeSetpoint: Invalid Temp Type received in auto mode... ${curHeatpoint} ${curCoolpoint} ${val}" 
				}
 				//thermostatSetpointEvent(temp)
        		break
    	}
    }
}

// Nest Only allows F temperatures as #.0  and C temperatures as either #.0 or #.5
void setHeatingSetpoint(temp) {
    setHeatingSetpoint(temp.toDouble())
}

void setHeatingSetpoint(Double reqtemp) {
	log.trace "setHeatingSetpoint()... ($reqtemp)"
	def hvacMode = getHvacMode()
	def temp = 0.0
	if (wantMetric()) {
		temp = Math.round(reqtemp.round(1) * 2) / 2.0f
	} else {
		temp = reqtemp.round(0).toInteger()
	}
	def tempUnit = state?.tempUnit
	def canHeat = state?.can_heat.toBoolean()
	def result = false
    
    log.debug "Heat Temp Received: ${temp} (${tempUnit})"
    if (state?.present && canHeat) {
		switch (tempUnit) {
			case "C":
				if (temp) {
                    if (temp < 9.0) { temp = 9.0 }
                    if (temp > 32.0 ) { temp = 32.0 }
					if (hvacMode == 'auto') {
					    parent.setTargetTempLow(this, tempUnit, temp)
                        heatingSetpointEvent(temp)
                    }
                    if (hvacMode == 'heat') {
				        parent.setTargetTemp(this, tempUnit, temp)
                        thermostatSetpointEvent(temp)
                        heatingSetpointEvent(temp)
					}
				}
            	result = true
				break
			case "F":
				if (temp) {
                    if (temp < 51.0) { temp = 51.0 }
                    if (temp > 89.0) { temp = 89.0 }
                    if (hvacMode == 'auto') {
                        parent.setTargetTempLow(this, tempUnit, temp) 
                        heatingSetpointEvent(temp)
					}  
                    if (hvacMode == 'heat') {
                        parent.setTargetTemp(this, tempUnit, temp) 
                        thermostatSetpointEvent(temp)
                        heatingSetpointEvent(temp)
                    }
				}
            	result = true
            	break
            default:
 	           Logger("no Temperature data $tempUnit")
               break
		}
	} else { 
    	log.debug "Skipping heat change" 
    	result = false
    }
    //return result
}

void setCoolingSetpoint(temp) {
    setCoolingSetpoint( temp.toDouble() )
}

void setCoolingSetpoint(Double reqtemp) {
	log.trace "setCoolingSetpoint()... ($reqtemp)"
	def hvacMode = getHvacMode()
	def temp = 0.0
	if (wantMetric()) {
		temp = Math.round(reqtemp.round(1) * 2) / 2.0f
	} else {
		temp = reqtemp.round(0).toInteger()
	}
	def tempUnit = state?.tempUnit
	def canCool = state?.can_cool.toBoolean()
	def result = false

    log.debug "Cool Temp Received: ${temp} (${tempUnit})"
    if (state?.present && canCool) {
		switch (tempUnit) {
			case "C":
				if (temp) {
                    if (temp < 9.0) { temp = 9.0 }
                    if (temp > 32.0) { temp = 32.0 }
					
                    if (hvacMode == 'auto') {
					    parent.setTargetTempHigh(this, tempUnit, temp) 
                        coolingSetpointEvent(temp)
					} 
                
                    if (hvacMode == 'cool') {
                        parent.setTargetTemp(this, tempUnit, temp) 
                        thermostatSetpointEvent(temp)
                        coolingSetpointEvent(temp)
					}
                }
            	result = true
				break
                
			default:
				if (temp) {
                    if (temp < 51.0) { temp = 51.0 }
                    if (temp > 89.0) { temp = 89.0 }
                    
                    if (hvacMode == 'auto') {
                        parent.setTargetTempHigh(this, tempUnit, temp) 
                        coolingSetpointEvent(temp)
                    }
                    if (hvacMode == 'cool') {
                        parent.setTargetTemp(this, tempUnit, temp) 
                        thermostatSetpointEvent(temp)
                        coolingSetpointEvent(temp)
					}
				}
            	result = true
            	break
		}
	} else {
		log.debug "Skipping cool change"
        result = false
	}
    //return result
}

/************************************************************************************************
|									NEST PRESENCE FUNCTIONS										|
*************************************************************************************************/
def setPresence() {
	log.trace "setPresence()..."
    def pres = getNestPresence()
    log.trace "Current Nest Presence: ${pres}"
    if(pres == "auto-away" || pres == "away") {
		parent.setStructureAway(this, "false")
        presenceEvent("home") 
    }
    else if (pres == "home") {
        parent.setStructureAway(this, "true")
        presenceEvent("away")
    }
}

// backward compatibility for previous nest thermostat (and rule machine)
def away() {
    log.trace "away()..."
    setAway()
}

// backward compatibility for previous nest thermostat (and rule machine)
def present() {
    log.trace "present()..."
    setHome()
}

def setAway() {
	log.trace "setAway()..."
    parent.setStructureAway(this, "true")
    presenceEvent("away")
}

def setHome() {
	log.trace "setHome()..."
    parent.setStructureAway(this, "false")
    presenceEvent("home")
}

/************************************************************************************************
|										HVAC MODE FUNCTIONS										|
*************************************************************************************************/

void off() {
	log.trace "off()..."
    def currentMode = getHvacMode()
    if (parent.setHvacMode(this, "off")) {
        hvacModeEvent("off")
    } else {
       	log.error "Error setting off mode." 
        hvacModeEvent(currentMode) // reset the tile back
    }
}

void heat() {
	log.trace "heat()..."
    def curPres = getNestPresence()
    def currentMode = getHvacMode()
	if (curPres == "home") {
    	if (parent.setHvacMode(this, "heat")) { 
        	hvacModeEvent("heat") 
        } else {
			log.error "Error setting heat mode." 
			hvacModeEvent(currentMode) // reset the tile back
    	}
    }
}

void emergencyHeat() {
    log.trace "emergencyHeat()..."
    log.warn "Emergency Heat setting not allowed"
}

void cool() {
	log.trace "cool()..."
    def curPres = getNestPresence()
    def currentMode = getHvacMode()
	if (curPres == "home") {
    	if (parent.setHvacMode(this, "cool")) { 
        	hvacModeEvent("cool") 
        } else {
       		log.error "Error setting cool mode." 
        	hvacModeEvent(currentMode) // reset the tile back
    	}
    }
}

void auto() {
	log.trace "auto()..."
    def curPres = getNestPresence()
    def currentMode = getHvacMode()
	if (curPres == "home") {
    	if (parent.setHvacMode(this, "heat-cool")) { 
        	hvacModeEvent("auto") 
        } else {
       		log.error "Error setting auto mode." 
        	hvacModeEvent(currentMode) // reset the tile back
    	}
    }
}

void setThermostatMode(modeStr) {
	log.trace "setThermostatMode()..."
	switch(modeStr) {
    	case "auto":
        	auto()
        	break
    	case "heat":
        	heat()
        	break
       	case "cool":
        	cool()
        	break
        case "off":
        	off()
        	break
		case "emergency heat":
			emergencyHeat()
			break
        default:
        	log.warn "setThermostatMode Received an Invalid Request: ${modeStr}"
            break
    }
}


/************************************************************************************************
|										FAN MODE FUNCTIONS										|
*************************************************************************************************/
void fanOn() {
	if(state?.has_fan.toBoolean()) {
    	parent.setFanMode(this, true)
        fanModeEvent("true")
    }
}

void fanOff() {
	log.trace "fanOff()..."
	if(state?.has_fan.toBoolean()) {
    	parent.setFanMode (this, "off")
        fanModeEvent("false")
    }
}

void fanCirculate() {
	log.trace "fanCirculate()..."
	log.warn "fanCirculate setting not supported by Nest API"
}

void fanAuto() {
	log.trace "fanAuto()..."
	if(state?.has_fan.toBoolean()) {
   		parent.setFanMode(this,false)
        fanModeEvent("false")
    }
}

void setThermostatFanMode(fanModeStr) {
    log.trace "setThermostatFanMode()... ($fanModeStr)"
	switch(fanModeStr) {
    	case "auto":
        	fanAuto()
        	break
    	case "on":
        	fanOn()
        	break
       	case "circulate":
        	fanCirculate()
        	break
        case "off":   // non standard by Nest Capabilities Thermostat
        	fanOff()
        	break
        default:
        	log.warn "setThermostatFanMode Received an Invalid Request: ${fanModeStr}"
            break
    }
}


/************************************************************************************************
|										LOGGING FUNCTIONS										|
*************************************************************************************************/
// Local Application Logging
def Logger(msg, logType = "debug") {
 	if(parent.settings?.childDebug) { 
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
