/**
 *  Nest Thermostat
 *	Author: Anthony S. (@tonesto7)
 *	Contributor: Ben W. (@desertBlade) & Eric S. (@E_Sch)
 *
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

def devVer() { return "2.0.4"}

// for the UI
metadata {
    definition (name: "${textDevName()}", namespace: "tonesto7", author: "Anthony S.") {
        capability "Actuator"
        //capability "Polling"
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
        //command "setFanMode"
        //command "setTemperature"
        command "setThermostatMode"
        command "levelUpDown"
        command "levelUp"
        command "levelDown"
        command "log"
        command "heatingSetpointUp"
        command "heatingSetpointDown"
        command "coolingSetpointUp"
        command "coolingSetpointDown"
        command "changeMode"

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
        attribute "canHeat", "string"
        attribute "canCool", "string"
        attribute "hasFan", "string"
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
                attributeState("default", action: "levelUpDown")
                attributeState("VALUE_UP", action: "levelUp")
                attributeState("VALUE_DOWN", action: "levelDown")
            }
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'${currentValue}%', unit:"%")
            }
            tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
                attributeState("idle",            backgroundColor:"#44B621")
                attributeState("heating",         backgroundColor:"#FFA81E")
                attributeState("cooling",         backgroundColor:"#2ABBF0")
                attributeState("fan only",		  backgroundColor:"#145D78")
                attributeState("pending heat",	  backgroundColor:"#B27515")
                attributeState("pending cool",	  backgroundColor:"#197090")
                attributeState("vent economizer", backgroundColor:"#8000FF")
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
        valueTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state("default", label:'${currentValue}°', icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png", 
                    backgroundColors: getTempColors())
        }
        standardTile("mode2", "device.thermostatMode", width: 2, height: 2, decoration: "flat") {
            state("off",  icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/off_icon.png")
            state("heat", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/heat_icon.png")
            state("cool", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/cool_icon.png")
            state("auto", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/heat_cool_icon.png")
        }
        standardTile("thermostatMode", "device.thermostatMode", width:2, height:2, decoration: "flat") {
            state("off", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/off_btn_icon.png")
            state("heat", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_btn_icon.png")
            state("cool", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_btn_icon.png")
            state("auto", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_cool_btn_icon.png")
            state("emergency heat", action:"changeMode", nextState: "updating", icon: "st.thermostat.emergency")
            state("updating", label:"Working", icon: "st.secondary.secondary")
        }
       standardTile("thermostatFanMode", "device.thermostatFanMode", width:2, height:2, decoration: "flat") {
            state "auto",	action:"fanOn", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_auto_icon.png"
            state "on",		action:"fanAuto", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_on_icon.png"
            state "disabled", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_disabled_icon.png"
        }
        standardTile("nestPresence", "device.nestPresence", width:2, height:2, decoration: "flat") {
            state "home", 	    action: "setPresence",	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_home_icon.png"
            state "away", 		action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_away_icon.png"
            state "auto-away", 	action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_autoaway_icon.png"
            state "unknown",	action: "setPresence", 	icon: "st.unknown.unknown.unknown"
        }
        standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
            state "default", label: 'refresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon"
        }
        valueTile("softwareVer", "device.softwareVer", width: 2, height: 1, wordWrap: true, decoration: "flat") {
            state("default", label: 'Firmware:\nv${currentValue}')
        }
        valueTile("hasLeaf", "device.hasLeaf", width: 2, height: 1, wordWrap: true, decoration: "flat") {
            state("default", label: 'Leaf:\n${currentValue}')
        }
        valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
            state("default", label: 'Network Status:\n${currentValue}')
        }
        valueTile("debugOn", "device.debugOn", width: 2, height: 1, decoration: "flat") {
            state "true", 	label: 'Debug:\n${currentValue}'
            state "false", 	label: 'Debug:\n${currentValue}'
        }
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
            state("default", label: 'Device Type:\nv${currentValue}')
        }
        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 1, height: 1) {
            state("heatingSetpoint", label:'${currentValue}', unit: "Heat", foregroundColor: "#FFFFFF",
                backgroundColors: [ [value: 0, color: "#FFFFFF"], [value: 7, color: "#FF3300"], [value: 15, color: "#FF3300"] ])
            state("disabled" , label: '', foregroundColor: "#FFFFFF", backgroundColor: "#FFFFFF")
        }
        valueTile("coolingSetpoint", "device.coolingSetpoint", width: 1, height: 1) {
            state("coolingSetpoint", label: '${currentValue}', unit: "Cool", foregroundColor: "#FFFFFF",
                backgroundColors: [ [value: 0, color: "#FFFFFF"], [value: 7, color: "#0099FF"], [value: 15, color: "#0099FF"] ])
            state("disabled", label: '', foregroundColor: "#FFFFFF", backgroundColor: "#FFFFFF")
        }
        standardTile("heatingSetpointUp", "device.heatingSetpoint", width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
            state "default", label: '', action:"heatingSetpointUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_up.png"
            state "", label: ''
        }
        standardTile("heatingSetpointDown", "device.heatingSetpoint",  width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
            state "default", label:'', action:"heatingSetpointDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_down.png"
            state "", label: ''
        }
        standardTile("coolingSetpointUp", "device.coolingSetpoint", width: 1, height: 1,canChangeIcon: false, decoration: "flat") {
            state "default", label:'', action:"coolingSetpointUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_up.png"
            state "", label: ''
        }
        standardTile("coolingSetpointDown", "device.coolingSetpoint", width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
            state "default", label:'', action:"coolingSetpointDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
            state "", label: ''
        }
        valueTile("lastConnection", "device.lastConnection", width: 4, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Nest Checked-In At:\n${currentValue}')
        }
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Data Last Received:\n${currentValue}')
        }
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
            state "ok", label: "API Status:\nOK"
            state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
        }
        valueTile("weatherCond", "device.weatherCond", width: 2, height: 1, wordWrap: true, decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        htmlTile(name:"devInfoHtml", action: "getInfoHtml", refreshInterval: 10, width: 6, height: 4)
        
        main( tileMain() )
        details( tileSelect() )
    }
}

def tileMain() { 
    return ["temp2"]
}

def tileSelect() { 
    def type = null// Setting to 1 shows the Original ST Tiles 
    switch(type) { //Original ST Layout
        case 1: 
            return ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp", 
                    "coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "onlineStatus", "weatherCond" , "hasLeaf", "lastConnection", "refresh", 
                    "lastUpdatedDt", "softwareVer", "apiStatus", "devTypeVer", "debugOn"]
            break
        case 2:
            return ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp", 
                    "coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "devInfoHtml", "refresh"]
            break
        default:
            return ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp", 
                    "coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "devInfoHtml", "refresh"]
            break
    }
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

mappings {
    path("/getInfoHtml") {action: [GET: "getInfoHtml"]}
}

def initialize() {
    log.debug "initialize"
}

def parse(String description) {
    log.debug "Parsing '${description}'"
}

def poll() {
    log.debug "Polling parent..."
    poll()
}

def refresh() {
    parent.refresh(this)
}

def generateEvent(Map results) {
    //Logger("generateEvents Parsing data ${results}")
      Logger("------------START OF API RESULTS DATA-------------", "warn")
    if(results) {
        state.useMilitaryTime = !parent?.settings?.useMilitaryTime ? false : true
        state.timeZone = !location?.timeZone ? parent?.getNestTimeZone() : null
        debugOnEvent(parent.settings?.childDebug)
        tempUnitEvent(getTemperatureScale())
        canHeatCool(results?.can_heat, results?.can_cool)
        hasFan(results?.has_fan.toString())
        presenceEvent(parent?.locationPresence())
        hvacModeEvent(results?.hvac_mode.toString())
        hasLeafEvent(results?.has_leaf)
        humidityEvent(results?.humidity.toString())
        operatingStateEvent(results?.hvac_state.toString())
        fanModeEvent(results?.fan_timer_active.toString())
        if(results?.last_connection) { lastCheckinEvent(results?.last_connection) }
        softwareVerEvent(results?.software_version.toString())
        onlineStatusEvent(results?.is_online.toString())
        deviceVerEvent()
        apiStatusEvent(parent?.apiIssues())
       
        def hvacMode = results?.hvac_mode
        def tempUnit = state?.tempUnit
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
    //sendEvent(name:"devInfoHtml", value: getInfoHtml(), isStateChange: true)
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
    def pubVer = parent?.latestTstatVer().ver.toString()
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

def lastCheckinEvent(checkin) {
    //log.trace "lastCheckinEvent()..."
    def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    tf.setTimeZone(getTimeZone())
    def lastConn = "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}"
    def lastChk = device.currentState("lastConnection")?.value
    state?.lastConnection = lastConn?.toString()
    if(!lastChk.equals(lastConn?.toString())) {
        log.debug("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
        sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: false, isStateChange: true)
    } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
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

def softwareVerEvent(ver) {
    def verVal = device.currentState("softwareVer")?.value
    state?.softwareVer = ver
    if(!verVal.equals(ver)) {
        log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
        sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now ${ver}", displayed: false, isStateChange: true)
    } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
}

def tempUnitEvent(unit) {
    def tmpUnit = device.currentState("temperatureUnit")?.value
    state?.tempUnit = unit
    if(!tmpUnit.equals(unit)) {   
        log.debug("UPDATED | Temperature Unit: (${unit}) | Original State: (${tmpUnit})")
        sendEvent(name:'temperatureUnit', value: unit, descriptionText: "Temperature Unit is now: '${unit}'", displayed: true, isStateChange: true)
    } else { Logger("Temperature Unit: (${unit}) | Original State: (${tmpUnit})") }
}

def targetTempEvent(Double targetTemp) {
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
        sendEvent(name:'thermostatSetpoint', value: rTargetTemp, unit: state?.tempUnit, descriptionText: "thermostatSetpoint Temperature is ${rTargetTemp}", displayed: false, isStateChange: true)
    } else { Logger("thermostatSetpoint is (${rTargetTemp}) | Original Temp: (${temp})") }
}

def temperatureEvent(Double tempVal) {
    def temp = device.currentState("temperature")?.value.toString()
    def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
    if(!temp.equals(rTempVal.toString())) {
        log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
        sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
    } else { Logger("Temperature is (${rTempVal}) | Original Temp: (${temp})") }
}

def heatingSetpointEvent(Double tempVal) {
    def temp = device.currentState("heatingSetpoint")?.value.toString()
    if(tempVal.toInteger() == 0 || !state?.can_heat || (getHvacMode == "off")) { 
        if(temp != "") { clearHeatingSetpoint() }
    } else {
        def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        if(!temp.equals(rTempVal.toString())) {
            log.debug("UPDATED | HeatingSetpoint is (${rTempVal}) | Original Temp: (${temp})")
            def disp = false
            def hvacMode = getHvacMode()
            if (hvacMode == "auto" || hvacMode == "heat") { disp = true }
            sendEvent(name:'heatingSetpoint', value: rTempVal, unit: state?.tempUnit, descriptionText: "Heat Setpoint is ${rTempVal}" , displayed: disp, isStateChange: true, state: "heat")
        } else { Logger("HeatingSetpoint is (${rTempVal}) | Original Temp: (${temp})") }
    }
}

def coolingSetpointEvent(Double tempVal) {
    def temp = device.currentState("coolingSetpoint")?.value.toString()
    if(tempVal.toInteger() == 0 || !state?.can_cool || (getHvacMode == "off")) { 
        if(temp != "") { clearCoolingSetpoint() }
    } else {
        def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        if(!temp.equals(rTempVal.toString())) {
            log.debug("UPDATED | CoolingSetpoint is (${rTempVal}) | Original Temp: (${temp})")
            def disp = false
            def hvacMode = getHvacMode()
            if (hvacMode == "auto" || hvacMode == "cool") { disp = true }
            sendEvent(name:'coolingSetpoint', value: rTempVal, unit: state?.tempUnit, descriptionText: "Cool Setpoint is ${rTempVal}" , displayed: disp, isStateChange: true, state: "cool")
        } else { Logger("CoolingSetpoint is (${rTempVal}) | Original Temp: (${temp})") }
    }
}

def hasLeafEvent(Boolean hasLeaf) {
    def leaf = device.currentState("hasLeaf")?.value
    def lf = hasLeaf ? "On" : "Off"
    state?.hasLeaf = hasLeaf
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
    def newNestPres = (presence == "home") ? "home" : ((presence == "auto-away") ? "auto-away" : "away")
    def statePres = state?.present
    state?.present = (pres == "present") ? true : false
    state?.nestPresence = newNestPres
    if(!val.equals(pres) || !nestPres.equals(newNestPres)) {
        log.debug("UPDATED | Presence: ${pres} | Original State: ${val} | State Variable: ${statePres}")
        sendEvent(name: 'nestPresence', value: newNestPres, descriptionText: "Nest Presence is: ${newNestPres}", displayed: true, isStateChange: true )
        sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: false, isStateChange: true, state: pres )
    } else { Logger("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
}

def hvacModeEvent(mode) {
    def pres = getNestPresence()
    def hvacMode = getHvacMode()
    def newMode = (mode == "heat-cool") ? "auto" : mode
    state?.hvac_mode = newMode
    if(!hvacMode.equals(newMode)) {
        log.debug("UPDATED | Hvac Mode is (${newMode}) | Original State: (${hvacMode})")
        sendEvent(name: "thermostatMode", value: newMode, descriptionText: "HVAC mode is ${newMode} mode", displayed: true, isStateChange: true)
    } else { Logger("Hvac Mode is (${newMode}) | Original State: (${hvacMode})") }
} 

def fanModeEvent(fanActive) {
    def val = state?.has_fan ? ((fanActive == "true") ? "on" : "auto") : "disabled"
    def fanMode = device.currentState("thermostatFanMode")?.value
    if(!fanMode.equals(val)) {
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
    state?.onlineStatus = val
    if(!isOn.equals(val)) { 
        log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
        sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
}

def apiStatusEvent(issue) {
    def appStat = device.currentState("apiStatus")?.value
    def val = issue ? "Issue" : "Ok"
    state?.apiStatus = val
    if(!appStat.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${appStat})")
        sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${appStat})") }
}

def canHeatCool(canHeat, canCool) {
    state?.can_heat = !canHeat ? false : true
    state?.can_cool = !canCool ? false : true
    sendEvent(name: "canHeat", value: state?.can_heat.toString())
    sendEvent(name: "canCool", value: state?.can_cool.toString())
}

def hasFan(hasFan) {
    state?.has_fan = (hasFan == "true") ? true : false
    sendEvent(name: "hasFan", value: hasFan.toString())
}

def isEmergencyHeat(val) {
    state?.is_using_emergency_heat = !val ? false : true
}

def clearHeatingSetpoint() {
    sendEvent(name:'heatingSetpoint', value: "",  descriptionText: "Clear Heating Setpoint" , display: false, displayed: true )
    state?.heating_setpoint = ""
}

def clearCoolingSetpoint() {
    sendEvent(name:'coolingSetpoint', value: "",  descriptionText: "Clear Cooling Setpoint" , display: false, displayed: true)
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
    catch (e) { return "home" }
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

def wantMetric() { return (state?.tempUnit == "C") }


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
                if (targetVal < 50.0) { targetVal = 50 }
                if (targetVal > 90.0) { targetVal = 90 }
            }
        } else {
            //log.debug "Reducing by 1 increment"
            if (tempUnit == "C" ) {
                targetVal = targetVal.toDouble() - 0.5
                if (targetVal < 9.0) { targetVal = 9.0 }
                if (targetVal > 32.0 ) { targetVal = 32.0 }
            } else {
                targetVal = targetVal.toDouble() - 1.0
                if (targetVal < 50.0) { targetVal = 50 }
                if (targetVal > 90.0) { targetVal = 90 }
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
    def tempUnit = state?.tempUnit
    def temp = 0.0
    def canHeat = state?.can_heat.toBoolean()
    def result = false
                
    log.debug "Heat Temp Received: ${reqtemp} (${tempUnit})"
    if (state?.present && canHeat) {
        switch (tempUnit) {
            case "C":
                temp = Math.round(reqtemp.round(1) * 2) / 2.0f
                if (temp) {
                    if (temp < 9.0) { temp = 9.0 }
                    if (temp > 32.0 ) { temp = 32.0 }
                        log.debug "Sending Heat Temp ($temp)"
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
                temp = reqtemp.round(0).toInteger()
                if (temp) {
                    if (temp < 50) { temp = 50 }
                    if (temp > 90) { temp = 90 }
                    log.debug "Sending Heat Temp ($temp)"
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
    def tempUnit = state?.tempUnit
    def canCool = state?.can_cool.toBoolean()
    def result = false
    
    log.debug "Cool Temp Received: ${reqtemp} (${tempUnit})"
    if (state?.present && canCool) {
        switch (tempUnit) {
            case "C":
                temp = Math.round(reqtemp.round(1) * 2) / 2.0f
                if (temp) {
                    if (temp < 9.0) { temp = 9.0 }
                    if (temp > 32.0) { temp = 32.0 }
                    log.debug "Sending Cool Temp ($temp)"
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
                
            case "F":
                temp = reqtemp.round(0).toInteger()
                if (temp) {
                    if (temp < 50) { temp = 50 }
                    if (temp > 90) { temp = 90 }
                    log.debug "Sending Cool Temp ($temp)"        
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
                    Logger("no Temperature data $tempUnit")
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
void setPresence() {
    log.trace "setPresence()..."
    def pres = getNestPresence()
    log.trace "Current Nest Presence: ${pres}"
    if(pres == "auto-away" || pres == "away") {
        if (parent.setStructureAway(this, "false")) { presenceEvent("home") }
    }
    else if (pres == "home") {
        if (parent.setStructureAway(this, "true")) { presenceEvent("away") }
    }
}

// backward compatibility for previous nest thermostat (and rule machine)
void away() {
    log.trace "away()..."
    setAway()
}

// backward compatibility for previous nest thermostat (and rule machine)
void present() {
    log.trace "present()..."
    setHome()
}

def setAway() {
    log.trace "setAway()..."
    if (parent.setStructureAway(this, "true")) { presenceEvent("away") }
}

def setHome() {
    log.trace "setHome()..."
    if (parent.setStructureAway(this, "false") ) { presenceEvent("home") }
}

/************************************************************************************************
|										HVAC MODE FUNCTIONS										|
************************************************************************************************/

def getHvacModes() {
    log.debug "Building Modes list"
    def modesList = ['off']
    if( state?.can_heat == true ) { modesList.push('heat') }
    if( state?.can_cool == true ) { modesList.push('cool') }
    if( state?.can_heat == true || state?.can_cool == true ) { modesList.push('auto') }
    Logger("Modes = ${modesList}")
    return modesList
}

def changeMode() {
    log.debug "changeMode.."
    def currentMode = device.currentState("thermostatMode")?.value
    def lastTriedMode = state.lastTriedMode ?: currentMode ?: "off"
    def modeOrder = getHvacModes()
    def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
    def nextMode = next(lastTriedMode)
    setHvacMode(nextMode)
}

def setHvacMode(nextMode) {
    log.debug "setHvacMode(${nextMode})"
    if (nextMode in getHvacModes()) {
        state.lastTriedMode = nextMode
        "$nextMode"()
    } else {
        log.debug("Invalid Mode '$nextMode'")
    }
}

void off() {
    log.trace "off()..."
    if (parent.setHvacMode(this, "off")) {
        hvacModeEvent("off")
    } else {
           log.error "Error setting off mode." 
    }
}

void heat() {
    log.trace "heat()..."
    def curPres = getNestPresence()
    if (curPres == "home") {
        if (parent.setHvacMode(this, "heat")) { 
            hvacModeEvent("heat") 
        } else {
        log.error "Error setting heat mode." 
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
    if (curPres == "home") {
        if (parent.setHvacMode(this, "cool")) { 
            hvacModeEvent("cool") 
        } else {
               log.error "Error setting cool mode." 
        }
    }
}

void auto() {
    log.trace "auto()..."
    def curPres = getNestPresence()
    if (curPres == "home") {
        if (parent.setHvacMode(this, "heat-cool")) { 
            hvacModeEvent("auto") 
        } else {
               log.error "Error setting auto mode." 
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
    log.trace "fanOn()..."
    def curPres = getNestPresence()
    if( (curPres == "home") && state?.has_fan.toBoolean() ) {
        if (parent.setFanMode(this, true) ) { fanModeEvent("true") }
    } else {
           log.error "Error setting fanOn" 
    }
}

void fanOff() {
    log.trace "fanOff()..."
    def curPres = getNestPresence()
    if ( (curPres == "home") && state?.has_fan.toBoolean() ) {
        if (parent.setFanMode (this, "off") ) { fanModeEvent("false") } 
    } else {
           log.error "Error setting fanOff" 
    }
}

void fanCirculate() {
    log.trace "fanCirculate()..."
    log.warn "fanCirculate setting not supported by Nest API"
}

void fanAuto() {
    log.trace "fanAuto()..."
    def curPres = getNestPresence()
    if ( (curPres == "home") && state?.has_fan.toBoolean() ) {
           if (parent.setFanMode(this,false) ) { fanModeEvent("false") }
    } else {
        log.error "Error setting fanAuto" 
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

def getImgBase64(url,type) {
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
                //log.debug "buf: $buf"
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

def getImg(imgName) { return imgName ? "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : "" }

def getInfoHtml() {
    def leafImg = state?.hasLeaf ? "<img src=\"${getImgBase64(getImg("nest_leaf_on.gif"), "gif")}\" class='leafImg'>" : 
                    "<img src=\"${getImgBase64(getImg("nest_leaf_off.gif"), "gif")}\" class='leafImg'>"
    renderHTML {
        head {
            """
            <style type="text/css">
                .flat-table {
                  width: 100%;
                  font-family: 'San Francisco', 'Roboto', 'Arial';
                  border: none;
                  border-radius: 3px;
                  -webkit-border-radius: 3px;
                  -moz-border-radius: 3px;
                }

                .flat-table th,
                .flat-table td {
                  box-shadow: inset 0 0px rgba(0, 0, 0, 0.25), inset 0 0px rgba(0, 0, 0, 0.25);
                  padding: 5px;
                }

                .flat-table th {
                  -webkit-font-smoothing: antialiased;
                  color: #f5f5f5;
                  text-shadow: 0 0 1px rgba(0, 0, 0, 0.1);
                  -webkit-border-radius: 2px;
                  -moz-border-radius: 2px;
                  background: #00a1db;
                }

                .flat-table td {
                  color: grey;
                  text-shadow: 0 0 1px rgba(255, 255, 255, 0.1);
                  text-align: center;
                }

                .flat-table tr {
                  -webkit-transition: background 0.3s, box-shadow 0.3s;
                  -moz-transition: background 0.3s, box-shadow 0.3s;
                  transition: background 0.3s, box-shadow 0.3s;
                  //vertical-align: top;
                }

                .h40 {
                  width: 39.99%;
                  font-weight: bold;
                  font-size: 3.2vmax;
                }

                .h20 {
                  width: 19.99%;
                  font-weight: bold;
                  font-size: 3.5vmax;
                }

                .r40 {
                  width: 39.99%;
                  font-size: 3.8vmax;
                }

                .r20 {
                  width: 19.99%;
                  font-size: 3.8vmax;
                }

                .rowLong {
                  font-size: 3.58vmax;
                }

                .datetime {
                  font-size: 3.2vmax;
                }
                .leafImg {
                  width: 25px;
                  height: 25px;
                }
            </style>
               """
        }
        body {
            """
             <table class="flat-table">
               <thead>
                 <th class="h40">Network Status</th>
                 <th class="h20">Leaf</th>
                 <th class="h40">API Status</th>
               </thead>
                 <tbody>
                   <tr>
                     <td class="r40">${state?.onlineStatus.toString()}</td>
                          <td class="r20">${leafImg}</td>
                     <td class="r40">${state?.apiStatus}</td>
                   </tr>
                   <tr>
                     <th class="h40">Firmware Version</th>
                     <th class="h20">Debug</th>
                     <th class="h40">Device Type</th>
                   </tr>
                   <td class="r40">${state?.softwareVer.toString()}</td>
                   <td class="r20">${state?.debugStatus}</td>
                   <td class="rowLong">${state?.devTypeVer.toString()}</td>
                 </tbody>
            </table>
            <table class="flat-table">
              <thead>
                <th class="h40">Nest Checked-In</th>
                <th class="h40">Data Last Received</th>
              </thead>
              <tbody>
                <tr>
                  <td><div class="datetime">${state?.lastConnection.toString()}</div></td>
                  <td><div class="datetime">${state?.lastUpdatedDt.toString()}</div></td>
                </tr>
              </tbody>
             </table>
            """
        }
    }
}

private def textDevName()   { "Nest Thermostat${appDevName()}" }
private def appDevType()    { false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
