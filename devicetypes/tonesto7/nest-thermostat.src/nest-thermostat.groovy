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

def devVer() { return "2.5.6"}

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
        attribute "safetyTempMin", "string"
        attribute "safetyTempMax", "string"
        attribute "comfortHumidityMax", "string"
        //attribute "safetyHumidityMin", "string"
        attribute "comfortDewpointMax", "string"
        attribute "tempLockOn", "string"
        attribute "lockedTempMin", "string"
        attribute "lockedTempMax", "string"
        attribute "devTypeVer", "string"
        attribute "onlineStatus", "string"
        attribute "nestPresence", "string"
        attribute "presence", "string"
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
            state("updating", label:"", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
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
        parent?.sendChildExceptionData("thermostat", ex.toString(), "getTempColors")
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

def generateEvent(Map eventData) {
    //log.trace("generateEvents Parsing data ${eventData}")
    try {
        Logger("------------START OF API RESULTS DATA------------", "warn")
        if(eventData) {
            state.useMilitaryTime = eventData?.mt ? true : false
            state.nestTimeZone = !location?.timeZone ? eventData.tz : null
            debugOnEvent(eventData?.debug ? true : false)
            tempUnitEvent(getTemperatureScale())
            if(eventData?.data?.is_locked) { tempLockOnEvent(eventData?.data?.is_locked.toString() == "true" ? true : false) }
            canHeatCool(eventData?.data?.can_heat, eventData?.data?.can_cool)
            hasFan(eventData?.data?.has_fan.toString())
            presenceEvent(eventData?.pres.toString())
            hvacModeEvent(eventData?.data?.hvac_mode.toString())
            hasLeafEvent(eventData?.data?.has_leaf)
            humidityEvent(eventData?.data?.humidity.toString())
            operatingStateEvent(eventData?.data?.hvac_state.toString())
            fanModeEvent(eventData?.data?.fan_timer_active.toString())
            if(!eventData?.data?.last_connection) { lastCheckinEvent(null) } 
            else { lastCheckinEvent(eventData?.data?.last_connection) }

            softwareVerEvent(eventData?.data?.software_version.toString())
            onlineStatusEvent(eventData?.data?.is_online.toString())
            deviceVerEvent(eventData?.latestVer.toString())
            apiStatusEvent(eventData?.apiIssues)
            state?.childWaitVal = eventData?.childWaitVal.toInteger()
            state?.cssUrl = eventData?.cssUrl.toString()
            if(eventData?.safetyTemps) { safetyTempsEvent(eventData?.safetyTemps) }
            if(eventData?.comfortHumidity) { comfortHumidityEvent(eventData?.comfortHumidity) }
            if(eventData?.comfortDewpoint) { comfortDewpointEvent(eventData?.comfortDewpoint) } 
            def hvacMode = eventData?.data?.hvac_mode
            def tempUnit = state?.tempUnit
            switch (tempUnit) {
                case "C":
                    def heatingSetpoint = 0.0
                    def coolingSetpoint = 0.0
                    def temp = eventData?.data?.ambient_temperature_c.toDouble() 
                    def targetTemp = eventData?.data?.target_temperature_c.toDouble()

                    if (hvacMode == "cool") { 
                        coolingSetpoint = targetTemp
                        //clearHeatingSetpoint()
                    } 
                    else if (hvacMode == "heat") { 
                        heatingSetpoint = targetTemp 
                        //clearCoolingSetpoint()
                    } 
                    else if (hvacMode == "heat-cool") {
                        coolingSetpoint = Math.round(eventData?.data?.target_temperature_high_c.toDouble())
                        heatingSetpoint = Math.round(eventData?.data?.target_temperature_low_c.toDouble())
                    }
                    if (!state?.present) {
                        if (eventData?.data?.away_temperature_high_c) { coolingSetpoint = eventData?.data?.away_temperature_high_c.toDouble() }
                        if (eventData?.data?.away_temperature_low_c) { heatingSetpoint = eventData?.data?.away_temperature_low_c.toDouble() }
                    }
                    temperatureEvent(temp)
                    thermostatSetpointEvent(targetTemp)
                    coolingSetpointEvent(coolingSetpoint)
                    heatingSetpointEvent(heatingSetpoint)
                    if(eventData?.data?.locked_temp_min_c && eventData?.data?.locked_temp_max_c) { lockedTempEvent(eventData?.data?.locked_temp_min_c, eventData?.data?.locked_temp_max_c) }
                    break
                    
                case "F":
                    def heatingSetpoint = 0
                    def coolingSetpoint = 0
                    def temp = eventData?.data?.ambient_temperature_f
                    def targetTemp = eventData?.data?.target_temperature_f
                    
                    if (hvacMode == "cool") { 
                        coolingSetpoint = targetTemp
                        //clearHeatingSetpoint()
                    } 
                    else if (hvacMode == "heat") { 
                        heatingSetpoint = targetTemp
                        //clearCoolingSetpoint()
                    } 
                    else if (hvacMode == "heat-cool") {
                        coolingSetpoint = eventData?.data?.target_temperature_high_f
                        heatingSetpoint = eventData?.data?.target_temperature_low_f
                    }
                    if (!state?.present) {
                        if (eventData?.data?.away_temperature_high_f) { coolingSetpoint = eventData?.data?.away_temperature_high_f }
                        if (eventData?.data?.away_temperature_low_f)  { heatingSetpoint = eventData?.data?.away_temperature_low_f }
                    }
                    temperatureEvent(temp)
                    thermostatSetpointEvent(targetTemp)
                    coolingSetpointEvent(coolingSetpoint)
                    heatingSetpointEvent(heatingSetpoint)
                    if(eventData?.data?.locked_temp_min_f && eventData?.data?.locked_temp_max_f) { lockedTempEvent(eventData?.data?.locked_temp_min_f, eventData?.data?.locked_temp_max_f) }
                    break
                
                default:
                    Logger("no Temperature data $tempUnit")
                    break
            }
        }
        lastUpdatedEvent()
        //This will return all of the devices state data to the logs.
        //log.debug "Device State Data: ${getState()}"
        return null
    }
    catch (ex) {
        log.error "generateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "generateEvent")
    }
}

def getDataByName(String name) {
    state[name] ?: device.getDataValue(name)
}

def getDeviceStateData() {
    return getState()
}

def getTimeZone() { 
    try {
        def tz = null
        if (location?.timeZone) { tz = location?.timeZone }
        else { tz = state?.nestTimeZone ? TimeZone.getTimeZone(state?.nestTimeZone) : null }
        if(!tz) { log.warn "getTimeZone: Hub or Nest TimeZone is not found ..." }
        return tz
    } catch (ex) {
        log.error "getTimeZone Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "getTimeZone")
    }
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
        sendChildExceptionData("thermostat", devVer(), ex?.toString(), "isCodeUpdateAvailable")
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
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "deviceVerEvent")
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
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "debugOnEvent")
    }
}

def lastCheckinEvent(checkin) {
    //log.trace "lastCheckinEvent()..."
    try {
        def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat(formatVal)
        tf.setTimeZone(getTimeZone())
        def lastConn = checkin ? "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}" : "Not Available"
        def lastChk = device.currentState("lastConnection")?.value
        state?.lastConnection = lastConn?.toString()
        if(!lastChk.equals(lastConn?.toString())) {
            log.debug("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
            sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: false, isStateChange: true)
        } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
    }
    catch (ex) {
        log.error "lastCheckinEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "lastCheckinEvent")
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
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "lastUpdatedEvent")
    }
}

def softwareVerEvent(ver) {
    try {
        def verVal = device.currentState("softwareVer")?.value
        state?.softwareVer = ver
        if(!verVal.equals(ver)) {
            log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
            sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now ${ver}", displayed: false, isStateChange: true)
        } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
    }
    catch (ex) {
        log.error "targetTempEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "targetTempEvent")
    }
}

def tempUnitEvent(unit) {
    try {
        def tmpUnit = device.currentState("temperatureUnit")?.value
        state?.tempUnit = unit
        if(!tmpUnit.equals(unit)) {   
            log.debug("UPDATED | Temperature Unit: (${unit}) | Original State: (${tmpUnit})")
            sendEvent(name:'temperatureUnit', value: unit, descriptionText: "Temperature Unit is now: '${unit}'", displayed: true, isStateChange: true)
        } else { Logger("Temperature Unit: (${unit}) | Original State: (${tmpUnit})") }
    }
    catch (ex) {
        log.error "tempUnitEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "tempUnitEvent")
    }
}

def targetTempEvent(Double targetTemp) {
    try {
        def temp = device.currentState("targetTemperature")?.value.toString()
        def rTargetTemp = wantMetric() ? targetTemp.round(1) : targetTemp.round(0).toInteger()
        if(!temp.equals(rTargetTemp.toString())) {
            log.debug("UPDATED | thermostatSetPoint Temperature is (${rTargetTemp}) | Original Temp: (${temp})")
            sendEvent(name:'targetTemperature', value: rTargetTemp, unit: state?.tempUnit, descriptionText: "Target Temperature is ${rTargetTemp}", displayed: false, isStateChange: true)
        } else { Logger("targetTemperature is (${rTargetTemp}) | Original Temp: (${temp})") }
    }
    catch (ex) {
        log.error "targetTempEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "targetTempEvent")
    }
}

def thermostatSetpointEvent(Double targetTemp) {
    try {
        def temp = device.currentState("thermostatSetpoint")?.value.toString()
        def rTargetTemp = wantMetric() ? targetTemp.round(1) : targetTemp.round(0).toInteger()
        if(!temp.equals(rTargetTemp.toString())) {
            log.debug("UPDATED | thermostatSetPoint Temperature is (${rTargetTemp}) | Original Temp: (${temp})")
            sendEvent(name:'thermostatSetpoint', value: rTargetTemp, unit: state?.tempUnit, descriptionText: "thermostatSetpoint Temperature is ${rTargetTemp}", displayed: false, isStateChange: true)
        } else { Logger("thermostatSetpoint is (${rTargetTemp}) | Original Temp: (${temp})") }
    }
    catch (ex) {
        log.error "thermostatSetpointEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "thermostatSetpointEvent")
    }
}

def temperatureEvent(Double tempVal) {
    try {
        def temp = device.currentState("temperature")?.value.toString()
        def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        if(!temp.equals(rTempVal.toString())) {
            log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
            sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
        } else { Logger("Temperature is (${rTempVal}) | Original Temp: (${temp})") }
    }
    catch (ex) {
        log.error "temperatureEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "temperatureEvent")
    }
}

def heatingSetpointEvent(Double tempVal) {
    try {
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
    catch (ex) {
        log.error "heatingSetpointEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "heatingSetpointEvent")
    }
}

def coolingSetpointEvent(Double tempVal) {
    try {
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
    catch (ex) {
        log.error "coolingSetpointEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "coolingSetpointEvent") 
    }
}

def hasLeafEvent(Boolean hasLeaf) {
    try {
        def leaf = device.currentState("hasLeaf")?.value
        def lf = hasLeaf ? "On" : "Off"
        state?.hasLeaf = hasLeaf
        if(!leaf.equals(lf)) {
            log.debug("UPDATED | Leaf is set to (${lf}) | Original State: (${leaf})")
            sendEvent(name:'hasLeaf', value: lf,  descriptionText: "Leaf: ${lf}" , displayed: false, isStateChange: true, state: lf)
        } else { Logger("Leaf is set to (${lf}) | Original State: (${leaf})") }
    }
    catch (ex) {
        log.error "hasLeafEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "hasLeafEvent")
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
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "humidityEvent")
    }
}

def presenceEvent(presence) {
    try {
        def val = device.currentState("presence")?.value
        def pres = (presence == "home") ? "present" : "not present"
        def nestPres = device.currentState("nestPresence") ? device.currentState("nestPresence")?.value : null 
        def newNestPres = (presence == "home") ? "home" : ((presence == "auto-away") ? "auto-away" : "away")
        def statePres = state?.present
        state?.present = (pres == "present") ? true : false
        state?.nestPresence = newNestPres
        if(!val.equals(pres) || !nestPres.equals(newNestPres) || !nestPres) {
            log.debug("UPDATED | Presence: ${pres} | Original State: ${val} | State Variable: ${statePres}")
            sendEvent(name: 'nestPresence', value: newNestPres, descriptionText: "Nest Presence is: ${newNestPres}", displayed: true, isStateChange: true )
            sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: false, isStateChange: true, state: pres )
        } else { Logger("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
    }
    catch (ex) {
        log.error "presenceEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "presenceEvent")
    }
}

def hvacModeEvent(mode) {
    try {
        def pres = getNestPresence()
        def hvacMode = getHvacMode()
        def newMode = (mode == "heat-cool") ? "auto" : mode
        state?.hvac_mode = newMode
        if(!hvacMode.equals(newMode)) {
            log.debug("UPDATED | Hvac Mode is (${newMode}) | Original State: (${hvacMode})")
            sendEvent(name: "thermostatMode", value: newMode, descriptionText: "HVAC mode is ${newMode} mode", displayed: true, isStateChange: true)
        } else { Logger("Hvac Mode is (${newMode}) | Original State: (${hvacMode})") }
    }
    catch (ex) {
        log.error "hvacModeEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "hvacModeEvent")
    }
} 

def fanModeEvent(fanActive) {
    try {
        def val = state?.has_fan ? ((fanActive == "true") ? "on" : "auto") : "disabled"
        def fanMode = device.currentState("thermostatFanMode")?.value
        if(!fanMode.equals(val)) {
            log.debug("UPDATED | Fan Mode: (${val}) | Original State: (${fanMode})")
            sendEvent(name: "thermostatFanMode", value: val, descriptionText: "Fan Mode is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Fan Active: (${val}) | Original State: (${fanMode})") }
    }
    catch (ex) {
        log.error "fanModeEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "fanModeEvent")
    }
}

def operatingStateEvent(operatingState) {
    try {
        def hvacState = device.currentState("thermostatOperatingState")?.value
        def operState = (operatingState == "off") ? "idle" : operatingState
        if(!hvacState.equals(operState)) {
            log.debug("UPDATED | OperatingState is (${operState}) | Original State: (${hvacState})")
            sendEvent(name: 'thermostatOperatingState', value: operState, descriptionText: "Device is ${operState}", displayed: true, isStateChange: true)
        } else { Logger("OperatingState is (${operState}) | Original State: (${hvacState})") }
    }
    catch (ex) {
        log.error "operatingStateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "operatingStateEvent")
    }
}

def tempLockOnEvent(isLocked) {
    try {
        def curState = device.currentState("tempLockOn")?.value.toString()
        def newState = isLocked?.toString()
        state?.hasLeaf = newState
        if(!curState?.equals(newState)) {
            log.debug("UPDATED | Temperature Lock is set to (${newState}) | Original State: (${curState})")
            sendEvent(name:'tempLockOn', value: newState,  descriptionText: "Temperature Lock: ${newState}" , displayed: false, isStateChange: true, state: newState)
        } else { Logger("Temperature Lock is set to (${newState}) | Original State: (${curState})") }
    }
    catch (ex) {
        log.error "tempLockOnEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "tempLockOnEvent")
    }
}

def lockedTempEvent(Double minTemp, Double maxTemp) {
    try {
        def curMinTemp = device.currentState("lockedTempMin")?.doubleValue
        def curMaxTemp = device.currentState("lockedTempMax")?.doubleValue
        //def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        if(curMinTemp != minTemp || curMaxTemp != maxTemp) {
            log.debug("UPDATED | Temperature Lock Minimum is (${minTemp}) | Original Temp: (${curMinTemp})")
            log.debug("UPDATED | Temperature Lock Maximum is (${maxTemp}) | Original Temp: (${curMaxTemp})")
            sendEvent(name:'lockedTempMin', value: minTemp, unit: state?.tempUnit, descriptionText: "Temperature Lock Minimum is ${minTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
            sendEvent(name:'lockedTempMax', value: maxTemp, unit: state?.tempUnit, descriptionText: "Temperature Lock Maximum is ${maxTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
        } else { 
            Logger("Temperature Lock Minimum is (${minTemp}${state?.tempUnit}) | Original Minimum Temp: (${curMinTemp}${state?.tempUnit})")
            Logger("Temperature Lock Maximum is (${maxTemp}${state?.tempUnit}) | Original Maximum Temp: (${curMaxTemp}${state?.tempUnit})") 
        }
    }
    catch (ex) {
        log.error "lockedTempEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "lockedTempEvent")
    }
}

def safetyTempsEvent(safetyTemps) {
    try {
        def curMinTemp = device.currentState("safetyTempMin")?.doubleValue
        def curMaxTemp = device.currentState("safetyTempMax")?.doubleValue
        def newMinTemp = safetyTemps?.min.toDouble() ?: 0
        def newMaxTemp = safetyTemps?.max.toDouble() ?: 0
        
        //def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        if(curMinTemp != newMinTemp || curMaxTemp != newMaxTemp) {
            log.debug("UPDATED | Safety Temperature Minimum is (${newMinTemp}${state?.tempUnit}) | Original Temp: (${curMinTemp}${state?.tempUnit})")
            log.debug("UPDATED | Safety Temperature Maximum is (${newMaxTemp}${state?.tempUnit}) | Original Temp: (${curMaxTemp}${state?.tempUnit})")
            sendEvent(name:'safetyTempMin', value: newMinTemp, unit: state?.tempUnit, descriptionText: "Safety Temperature Minimum is ${newMinTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
            sendEvent(name:'safetyTempMax', value: newMaxTemp, unit: state?.tempUnit, descriptionText: "Safety Temperature Maximum is ${newMaxTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
        } else { 
            Logger("Safety Temperature Minimum is  (${newMinTemp}${state?.tempUnit}) | Original Minimum Temp: (${curMinTemp}${state?.tempUnit})")
            Logger("Safety Temperature Maximum is  (${newMaxTemp}${state?.tempUnit}) | Original Maximum Temp: (${curMaxTemp}${state?.tempUnit})") 
        }
    }
    catch (ex) {
        log.error "safetyTempsEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "safetyTempsEvent")
    }
}

def comfortHumidityEvent(comfortHum) {
    try {
        //def curMinHum = device.currentState("safetyHumidityMin")?.integerValue
        def curMaxHum = device.currentState("comfortHumidityMax")?.integerValue
        //def newMinHum = safetyHum?.min.toInteger() ?: 0
        //def newMaxHum = safetyHum?.max.toInteger() ?: 0
        def newMaxHum = comfortHum?.toInteger() ?: 0
        if(curMaxHum != newMaxHum) {
            //log.debug("UPDATED | Safety Humidity Minimum is (${newMinHum}) | Original Temp: (${curMinHum})")
            log.debug("UPDATED | Safety Humidity Maximum is (${newMaxHum}%) | Original Humidity: (${curMaxHum}%)")
            //sendEvent(name:'safetyHumidityMin', value: newMinHum, unit: "%", descriptionText: "Safety Humidity Minimum is ${newMinHum}" , displayed: true, isStateChange: true)
            sendEvent(name:'comfortHumidityMax', value: newMaxHum, unit: "%", descriptionText: "Safety Humidity Maximum is ${newMaxHum}%" , displayed: true, isStateChange: true)
        } else { 
            //Logger("Humidity Minimum is (${newMinHum}) | Original Minimum Humidity: (${curMinHum})")
            Logger("Humidity Maximum is (${newMaxHum}%) | Original Maximum Humidity: (${curMaxHum}%)") 
        }
    }
    catch (ex) {
        log.error "comfortHumidityEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "comfortHumidityEvent")
    }
}

def comfortDewpointEvent(comfortDew) {
    try {
        //def curMinDew = device.currentState("safetyDewpointMin")?.integerValue
        def curMaxDew = device.currentState("comfortDewpointMax")?.doubleValue
        //def newMinDew = safetyDew?.min.toInteger() ?: 0
        //def newMaxDew = safetyDew?.max.toInteger() ?: 0
        def newMaxDew = comfortDew?.toDouble() ?: 0.0
        if(curMaxDew != newMaxDew) {
            //log.debug("UPDATED | Safety Dewpoint Minimum is (${newMinDew}) | Original Temp: (${curMinDew})")
            log.debug("UPDATED | Safety Dewpoint Maximum is (${newMaxDew}) | Original Dewpoint: (${curMaxDew})")
            //sendEvent(name:'safetyDewpointMin', value: newMinDew, unit: "%", descriptionText: "Safety Dewpoint Minimum is ${newMinDew}" , displayed: true, isStateChange: true)
            sendEvent(name:'comfortDewpointMax', value: newMaxDew, unit: state?.tempUnit, descriptionText: "Safety Dewpoint Maximum is ${newMaxDew}" , displayed: true, isStateChange: true)
        } else { 
            //Logger("Humidity Dewpoint is (${newMinDew}) | Original Minimum Dewpoint: (${curMinDew})")
            Logger("Dewpoint Maximum is (${newMaxDew}) | Original Maximum Dewpoint: (${curMaxDew})") 
        }
    }
    catch (ex) {
        log.error "comfortDewpointEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "comfortDewpointEvent")
    }
}

def onlineStatusEvent(online) {
    try {
        def isOn = device.currentState("onlineStatus")?.value
        def val = online ? "Online" : "Offline"
        state?.onlineStatus = val
        if(!isOn.equals(val)) { 
            log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
            sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
    }
    catch (ex) {
        log.error "onlineStatusEvent Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "onlineStatusEvent")
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
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "apiStatusEvent")
    }
}

def canHeatCool(canHeat, canCool) {
    try {
        state?.can_heat = !canHeat ? false : true
        state?.can_cool = !canCool ? false : true
        sendEvent(name: "canHeat", value: state?.can_heat.toString())
        sendEvent(name: "canCool", value: state?.can_cool.toString())
    }
    catch (ex) {
        log.error "canHeatCool Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "canHeatCool")
    }
}

def hasFan(hasFan) {
    try {
        state?.has_fan = (hasFan == "true") ? true : false
        sendEvent(name: "hasFan", value: hasFan.toString())
    }
    catch (ex) {
        log.error "hasFan Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "hasFan")
    }
}

def isEmergencyHeat(val) {
    try {
        state?.is_using_emergency_heat = !val ? false : true
    }
    catch (ex) {
        log.error "isEmergencyHeat Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "isEmergencyHeat")
    }
}

def clearHeatingSetpoint() {
    try {
        sendEvent(name:'heatingSetpoint', value: "",  descriptionText: "Clear Heating Setpoint" , display: false, displayed: true )
        state?.heating_setpoint = ""
    }
    catch (ex) {
        log.error "clearHeatingSetpoint Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "clearHeatingSetpoint")
    }
}

def clearCoolingSetpoint() {
    try {
        sendEvent(name:'coolingSetpoint', value: "",  descriptionText: "Clear Cooling Setpoint" , display: false, displayed: true)
        state?.cooling_setpoint = ""
    }
    catch (ex) {
        log.error "clearCoolingSetpoint Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "clearCoolingSetpoint")
    }
}

def getCoolTemp() { 
    return !device.currentValue("coolingSetpoint") ? 0 : device.currentValue("coolingSetpoint") 
}

def getHeatTemp() { 
    return !device.currentValue("heatingSetpoint") ? 0 : device.currentValue("heatingSetpoint") 
}

def getFanMode() { 
    return !device.currentState("thermostatFanMode")?.value ? "unknown" : device.currentState("thermostatFanMode")?.value.toString() 
}

def getHvacMode() { 
    return !device.currentState("thermostatMode") ? "unknown" : device.currentState("thermostatMode")?.value.toString() 
}

def getNestPresence() { 
    return !device.currentState("nestPresence") ? "home" : device.currentState("nestPresence")?.value.toString()
}

def getPresence() { 
    return !device.currentState("presence") ? "present" : device.currentState("presence").value.toString()
}

def getTargetTemp() { 
    return !device.currentValue("targetTemperature") ? 0 : device.currentValue("targetTemperature") 
}

def getThermostatSetpoint() { 
    return !device.currentValue("thermostatSetpoint") ? 0 : device.currentValue("thermostatSetpoint") 
}

def getTemp() { 
    return !device.currentValue("temperature") ? 0 : device.currentValue("temperature") 
}

def getTempWaitVal() { 
    return state?.childWaitVal ? state?.childWaitVal.toInteger() : 4
}

def wantMetric() { return (state?.tempUnit == "C") }


/************************************************************************************************
|							Temperature Setpoint Functions for Buttons							|
*************************************************************************************************/
void heatingSetpointUp() {
    try {
        log.trace "heatingSetpointUp()..."
        def operMode = getHvacMode()
        if ( operMode == "heat" || operMode == "auto" ) {
            levelUpDown(1,"heat")
        }
    }
    catch (ex) {
        log.error "heatingSetpointUp Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "heatingSetpointUp")
    }
}

void heatingSetpointDown() {
    try {
        log.trace "heatingSetpointDown()..."
        def operMode = getHvacMode()
        if ( operMode == "heat" || operMode == "auto" ) {
            levelUpDown(-1, "heat")
        }
    }
    catch (ex) {
        log.error "heatingSetpointDown Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "heatingSetpointDown")
    }
}

void coolingSetpointUp() {
    try {
        log.trace "coolingSetpointUp()..."
        def operMode = getHvacMode()
        if ( operMode == "cool" || operMode == "auto" ) {
            levelUpDown(1, "cool")
        }
    }
    catch (ex) {
        log.error "coolingSetpointUp Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "coolingSetpointUp")
    }
}

void coolingSetpointDown() {
    try {
        log.trace "coolingSetpointDown()..."
        def operMode = getHvacMode()
        if ( operMode == "cool" || operMode == "auto" ) {
            levelUpDown(-1, "cool")
        }
    }
    catch (ex) {
        log.error "coolingSetpointDown Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "coolingSetpointDown")
    }
}

void levelUp() {
    levelUpDown(1)
}

void levelDown() {
    levelUpDown(-1)
}

void levelUpDown(tempVal, chgType = null) {
    //log.trace "levelUpDown()...($tempVal | $chgType)"
    try {
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
                        runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
                        break
                    case "cool":
                        Logger("Sending changeSetpoint(Temp: ${targetVal})") 
                        thermostatSetpointEvent(targetVal)
                        coolingSetpointEvent(targetVal)
                        if (!chgType) { chgType = "" }
                        runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
                        break
                    case "auto":
                        if (chgType) {
                            switch (chgType) {
                                case "cool":
                                    Logger("Sending changeSetpoint(Temp: ${targetVal})")
                                    coolingSetpointEvent(targetVal)
                                    runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
                                    break
                                case "heat":
                                    Logger("Sending changeSetpoint(Temp: ${targetVal})")
                                    heatingSetpointEvent(targetVal)
                                    runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
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
    catch (ex) {
        log.error "levelUpDown Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "levelUpDown")
    }
}

// Nest does not allow temp changes in away modes
def canChangeTemp() {
    //log.trace "canChangeTemp()..."
    try {
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
    catch (ex) {
        log.error "canChangeTemp Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "canChangeTemp")
    }
}

void changeSetpoint(val) {
    //log.trace "changeSetpoint()... ($val)"
    try {
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
    catch (ex) {
        log.error "changeSetpoint Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "changeSetpoint")
    }
}

// Nest Only allows F temperatures as #.0  and C temperatures as either #.0 or #.5
void setHeatingSetpoint(temp) {
    setHeatingSetpoint(temp.toDouble())
}

void setHeatingSetpoint(Double reqtemp) {
    try {
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
    }
    catch (ex) {
        log.error "setHeatingSetpoint Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setHeatingSetpoint")
    }
}

void setCoolingSetpoint(temp) {
    setCoolingSetpoint( temp.toDouble() )
}

void setCoolingSetpoint(Double reqtemp) {
    try {
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
    }
    catch (ex) {
        log.error "setCoolingSetpoint Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setCoolingSetpoint")
    }
}

/************************************************************************************************
|									NEST PRESENCE FUNCTIONS										|
*************************************************************************************************/
void setPresence() {
    try {
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
    catch (ex) {
        log.error "setPresence Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setPresence")
    }
}

// backward compatibility for previous nest thermostat (and rule machine)
void away() {
    try {
        log.trace "away()..."
        setAway()
    }
    catch (ex) {
        log.error "away Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "away")
    }
}

// backward compatibility for previous nest thermostat (and rule machine)
void present() {
    try {
        log.trace "present()..."
        setHome()
    }
    catch (ex) {
        log.error "present Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "present")
    }
}

def setAway() {
    try {
        log.trace "setAway()..."
        if (parent.setStructureAway(this, "true")) { presenceEvent("away") }
    }
    catch (ex) {
        log.error "setAway Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setAway")
    }
}

def setHome() {
    try {
        log.trace "setHome()..."
        if (parent.setStructureAway(this, "false") ) { presenceEvent("home") }
    }
    catch (ex) {
        log.error "setHome Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setHome")
    }
}

/************************************************************************************************
|										HVAC MODE FUNCTIONS										|
************************************************************************************************/

def getHvacModes() {
    //log.debug "Building Modes list"
    def modesList = ['off']
    if( state?.can_heat == true ) { modesList.push('heat') }
    if( state?.can_cool == true ) { modesList.push('cool') }
    if( state?.can_heat == true && state?.can_cool == true ) { modesList.push('auto') }
    Logger("Modes = ${modesList}")
    return modesList
}

def changeMode() {
    try {
        //log.debug "changeMode.."
        def currentMode = device.currentState("thermostatMode")?.value
        def lastTriedMode = currentMode ?: "off"
        def modeOrder = getHvacModes()
        def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
        def nextMode = next(lastTriedMode)
log.trace "changeMode() currentMode: ${currentMode}   lastTriedMode:  ${lastTriedMode}  modeOrder:  ${modeOrder}   nextMode: ${nextMode}"
        setHvacMode(nextMode)
    }
    catch (ex) {
        log.error "changeMode Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "changeMode")
    }
}

def setHvacMode(nextMode) {
    try {
        log.debug "setHvacMode(${nextMode})"
        if (nextMode in getHvacModes()) {
            state.lastTriedMode = nextMode
            "$nextMode"()
        } else {
            log.debug("Invalid Mode '$nextMode'")
        }
    }
    catch (ex) {
        log.error "setHvacMode Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setHvacMode")
    }
}

def doChangeMode() {
    try {
        def currentMode = device.currentState("thermostatMode")?.value
        log.debug "doChangeMode()  currentMode:  ${currentMode}"
        def errflag = true
        switch(currentMode) {
            case "auto":
                if (parent.setHvacMode(this, "heat-cool")) { 
                    errflag = false
                }
                break
            case "heat":
                if (parent.setHvacMode(this, "heat")) { 
                    errflag = false
                }
                break
            case "cool":
                if (parent.setHvacMode(this, "cool")) { 
                    errflag = false
                }
                break
            case "off":
                if (parent.setHvacMode(this, "off")) {
                    errflag = false
                }
                break
            default:
                log.warn "doChangeMode Received an Invalid Request: ${currentMode}"
                break
        }
        if (errflag) {
            log.warn "doChangeMode call to change mode failed: ${currentMode}"
            refresh()
        }
    }
    catch (ex) {
        log.error "doChangeMode Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "doChangeMode")
    }
}

void off() {
    try {
        log.trace "off()..."
        hvacModeEvent("off")
        runIn( getTempWaitVal() * 2, "doChangeMode", [overwrite: true] )
    }
    catch (ex) {
        log.error "off Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "off")
    }
}

void heat() {
    try {
        log.trace "heat()..."
        hvacModeEvent("heat") 
        runIn( getTempWaitVal() * 2, "doChangeMode", [overwrite: true] )
    }
    catch (ex) {
        log.error "heat Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "heat")
    }
}

void emergencyHeat() {
    log.trace "emergencyHeat()..."
    log.warn "Emergency Heat setting not allowed"
}

void cool() {
    try {
        log.trace "cool()..."
        hvacModeEvent("cool") 
        runIn( getTempWaitVal() * 2, "doChangeMode", [overwrite: true] )
    }
    catch (ex) {
        log.error "cool Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "cool")
    }
}

void auto() {
    try {
        log.trace "auto()..."
        hvacModeEvent("auto") 
        runIn( getTempWaitVal() * 2, "doChangeMode", [overwrite: true] )
    }
    catch (ex) {
        log.error "auto Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "auto")
    }
}

void setThermostatMode(modeStr) {
    try {
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
    catch (ex) {
        log.error "setThermostatMode Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setThermostatMode")
    }
}


/************************************************************************************************
|										FAN MODE FUNCTIONS										|
*************************************************************************************************/
void fanOn() {
    try {
        log.trace "fanOn()..."
        if ( state?.has_fan.toBoolean() ) {
            if (parent.setFanMode(this, true) ) { fanModeEvent("true") }
        } else { log.error "Error setting fanOn" }
    }
    catch (ex) {
        log.error "fanOn Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "fanOn")
    }
}

void fanOff() {
    try {
        log.trace "fanOff()..."
        if ( state?.has_fan.toBoolean() ) {
            if (parent.setFanMode (this, "off") ) { fanModeEvent("false") } 
        } else { log.error "Error setting fanOff" }
    }
    catch (ex) {
        log.error "fanOff Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "fanOff")
    }
}

void fanCirculate() {
    log.trace "fanCirculate()..."
    fanOn()
}

void fanAuto() {
    try {
        log.trace "fanAuto()..."
        if ( state?.has_fan.toBoolean() ) {
            if (parent.setFanMode(this,false) ) { fanModeEvent("false") }
        } else { log.error "Error setting fanAuto" }
    }
    catch (ex) {
        log.error "fanAuto Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "fanAuto")
    }
}

void setThermostatFanMode(fanModeStr) {
    try {
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
    catch (ex) {
        log.error "setThermostatFanMode Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "setThermostatFanMode")
    }
}


/************************************************************************************************
|										LOGGING FUNCTIONS										|
*************************************************************************************************/
// Local Application Logging
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

def getImgBase64(url,type) {
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
                //log.debug "buf: $buf"
                String s = buf?.encodeBase64()
                //log.debug "resp: ${s}"
                return s ? "data:image/${type};base64,${s.toString()}" : null
            }
        }
    }
    catch (ex) {
        log.error "getImageBytes Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "getImgBase64")
    }
}

def getCSS(){
    try {
        def params = [ 
            uri: state?.cssUrl.toString(),
            contentType: 'text/css'
        ]
        httpGet(params)  { resp ->
            return resp?.data.text
        }
    }
    catch (ex) {
        log.error "Failed to load CSS - Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "getCSS")
    }
}

def getImg(imgName) { 
    try {
        return imgName ? "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : "" 
    }
    catch (ex) {
        log.error "getImg Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "getImg")
    }
}

def getInfoHtml() {
    try {
        def leafImg = state?.hasLeaf ? "<img src=\"${getImgBase64(getImg("nest_leaf_on.gif"), "gif")}\" class='leafImg'>" : 
                        "<img src=\"${getImgBase64(getImg("nest_leaf_off.gif"), "gif")}\" class='leafImg'>"
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
                <table>
                <col width="40%">
                <col width="20%">
                <col width="40%">
                <thead>
                    <th>Network Status</th>
                    <th>Leaf</th>
                    <th>API Status</th>
                </thead>
                    <tbody>
                    <tr>
                        <td>${state?.onlineStatus.toString()}</td>
                        <td>${leafImg}</td>
                        <td>${state?.apiStatus}</td>
                    </tr>
                </tbody>
                </table>
                
               <p class="centerText">
                	<a href="#openModal" class="button">More info</a>
                </p>
                
                  <div id="openModal" class="topModal">
                        <div>
                            <a href="#close" title="Close" class="close">X</a>
                          <table>  
                            <tr>
                                <th>Firmware Version</th>
                                <th>Debug</th>
                                <th>Device Type</th>
                            </tr>
                            <td>${state?.softwareVer.toString()}</td>
                            <td>${state?.debugStatus}</td>
                            <td>${state?.devTypeVer.toString()}</td>
                            </tbody>
                        </table>
                        <table>
                        <thead>
                            <th>Nest Checked-In</th>
                            <th>Data Last Received</th>
                        </thead>
                        <tbody>
                            <tr>
                            <td class="dateTimeText">${state?.lastConnection.toString()}</td>
                            <td class="dateTimeText">${state?.lastUpdatedDt.toString()}</td>
                            </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </body>
        </html>
        """
        render contentType: "text/html", data: html, status: 200
    }
    catch (ex) {
        log.error "getInfoHtml Exception: ${ex}"
        parent?.sendChildExceptionData("thermostat", devVer(), ex.toString(), "getInfoHtml")
    }
}

private def textDevName()  { return "Nest Thermostat${appDevName()}" }
private def appDevType()   { return false }
private def appDevName()   { return appDevType() ? " (Dev)" : "" }
