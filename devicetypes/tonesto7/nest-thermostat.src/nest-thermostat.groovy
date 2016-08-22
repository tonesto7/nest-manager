/**
 *  Nest Thermostat
 *	Author: Anthony S. (@tonesto7)
 *	Contributor: Ben W. (@desertBlade) & Eric S. (@E_Sch)
 *  Graphing Modeled on code from Andreas Amann (@ahndee)
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
import groovy.time.*

preferences {  }

def devVer() { return "3.1.0"}

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
        attribute "safetyTempExceeded", "string"
        attribute "comfortHumidityMax", "string"
        attribute "comfortHumidtyExceeded", "string"
        //attribute "safetyHumidityMin", "string"
        attribute "comfortDewpointMax", "string"
        attribute "comfortDewpointExceeded", "string"
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
        attribute "nestType", "string"
        attribute "pauseUpdates", "string"
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

        controlTile("heatSliderControl", "device.heatingSetpoint", "slider", height: 2, width: 3, inactiveLabel: false) {
            state "default", action:"setHeatingSetpoint", backgroundColor:"#FF3300"
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

        controlTile("coolSliderControl", "device.coolingSetpoint", "slider", height: 2, width: 3, inactiveLabel: false) {
            state "setCoolingSetpoint", action:"setCoolingSetpoint", backgroundColor:"#0099FF"
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

        htmlTile(name:"graphHTML", action: "getGraphHTML", width: 6, height: 8, whitelist: ["www.gstatic.com", "raw.githubusercontent.com", "cdn.rawgit.com"])

        main("temp2")
        details( ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp",
                  "coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "graphHTML", "refresh"])
                  //"coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "graphHTML", "heatSliderControl", "coolSliderControl", "refresh"] )
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
    path("/getGraphHTML") {action: [GET: "getGraphHTML"]}
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
    pauseEvent("false")
    parent.refresh(this)
}

// parent calls this method to queue data.
// goal is to return to parent asap to avoid execution timeouts

def generateEvent(Map eventData) {
    //log.trace("generateEvent Parsing data ${eventData}")
    state.eventData = eventData
    runIn(3, "processEvent", [overwrite: true] )
}

def processEvent() {
    def pauseUpd = !device.currentValue("pauseUpdates") ? false : device.currentValue("pauseUpdates").value
    if(pauseUpd == "true") { log.warn "pausing"; return }

    def eventData = state?.eventData
    state.eventData = null

    //log.trace("processEvent Parsing data ${eventData}")
    try {
        Logger("------------START OF API RESULTS DATA------------", "warn")
        if(eventData) {
            if(virtType()) { nestTypeEvent("virtual") } else { nestTypeEvent("physical") }
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
            if(eventData?.htmlInfo) { state?.htmlInfo = eventData?.htmlInfo }
            if(eventData?.allowDbException) { state?.allowDbException = eventData?.allowDbException = false ? false : true }
            if(eventData?.safetyTemps) { safetyTempsEvent(eventData?.safetyTemps) }
            if(eventData?.comfortHumidity) { comfortHumidityEvent(eventData?.comfortHumidity) }
            if(eventData?.comfortDewpoint) { comfortDewpointEvent(eventData?.comfortDewpoint) }
            def hvacMode = state?.hvac_mode
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
                    else if (hvacMode == "auto") {
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
                    else if (hvacMode == "auto") {
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
            getSomeData(true)
            lastUpdatedEvent()
        }
        //This will return all of the devices state data to the logs.
        //log.debug "Device State Data: ${getState()}"
        return null
    }
    catch (ex) {
        log.error "generateEvent Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "generateEvent")
    }
}

def getStateSize()      { return state?.toString().length() }
def getStateSizePerc()  { return (int) ((stateSize/100000)*100).toDouble().round(0) }

def getDataByName(String name) {
    state[name] ?: device.getDataValue(name)
}

def getDeviceStateData() {
    return getState()
}

def getTimeZone() {
    def tz = null
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = state?.nestTimeZone ? TimeZone.getTimeZone(state?.nestTimeZone) : null }
    if(!tz) { log.warn "getTimeZone: Hub or Nest TimeZone is not found ..." }
    return tz
}

def isCodeUpdateAvailable(newVer, curVer) {
    def result = false
    def latestVer
    def versions = [newVer, curVer]
    if(newVer != curVer) {
        latestVer = versions?.max { a, b ->
            def verA = a?.tokenize('.')
            def verB = b?.tokenize('.')
            def commonIndices = Math.min(verA?.size(), verB?.size())
            for (int i = 0; i < commonIndices; ++i) {
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
}

def pauseEvent(val) {
    def curData = device.currentState("pauseUpdates")?.value
    if(!curData?.equals(val)) {
        Logger("UPDATED | Pause Updates is: (${val}) | Original State: (${curData})")
        sendEvent(name: 'pauseUpdates', value: val, displayed: false)
    } else { Logger("Pause Updates is: (${val}) | Original State: (${curData})") }
}

def deviceVerEvent(ver) {
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

def nestTypeEvent(type) {
    def val = device.currentState("nestType")?.value
    state?.nestType=type
    if(!val.equals(type)) {
        log.debug("UPDATED | nestType: (${type}) | Original State: (${val})")
        sendEvent(name: 'nestType', value: type, displayed: true)
    } else { Logger("nestType: (${type}) | Original State: (${val})") }
}

def debugOnEvent(debug) {
    def val = device.currentState("debugOn")?.value
    def dVal = debug ? "On" : "Off"
    state?.debugStatus = dVal
    state?.debug = debug.toBoolean() ? true : false
    if(!val.equals(dVal)) {
        log.debug("UPDATED | debugOn: (${dVal}) | Original State: (${val.toString().capitalize()})")
        sendEvent(name: 'debugOn', value: dVal, displayed: false)
    } else { Logger("debugOn: (${dVal}) | Original State: (${val})") }
}

def lastCheckinEvent(checkin) {
    //log.trace "lastCheckinEvent()..."
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
    try {
        def temp = device.currentState("temperature")?.value.toString()
        def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
        if(!temp.equals(rTempVal.toString())) {
            log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
            sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
        } else { Logger("Temperature is (${rTempVal}) | Original Temp: (${temp})") }
        checkSafetyTemps()
    }
    catch (ex) {
        log.error "temperatureEvent Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "temperatureEvent")
    }
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
    def val = getPresence()
    def pres = (presence == "home") ? "present" : "not present"
    def nestPres = state?.nestPresence
    def newNestPres = (presence == "home") ? "home" : ((presence == "auto-away") ? "auto-away" : "away")
    def statePres = state?.present
    state?.present = (pres == "present") ? true : false
    state?.nestPresence = newNestPres
    if(!val.equals(pres) || !nestPres.equals(newNestPres) || !nestPres) {
        log.debug("UPDATED | Presence: ${pres.toString().capitalize()} | Original State: ${val.toString().capitalize()} | State Variable: ${statePres}")
        sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: false, isStateChange: true, state: pres )
        sendEvent(name: 'nestPresence', value: newNestPres, descriptionText: "Nest Presence is: ${newNestPres}", displayed: true, isStateChange: true )
    } else { Logger("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
}

def hvacModeEvent(mode) {
    def hvacMode = getHvacMode()
    def newMode = (mode == "heat-cool") ? "auto" : mode
    state?.hvac_mode = newMode
    if(!hvacMode.equals(newMode)) {
        log.debug("UPDATED | Hvac Mode is (${newMode.toString().capitalize()}) | Original State: (${hvacMode.toString().capitalize()})")
        sendEvent(name: "thermostatMode", value: newMode, descriptionText: "HVAC mode is ${newMode} mode", displayed: true, isStateChange: true)
    } else { Logger("Hvac Mode is (${newMode}) | Original State: (${hvacMode})") }
}

def fanModeEvent(fanActive) {
    def val = state?.has_fan ? ((fanActive == "true") ? "on" : "auto") : "disabled"
    def fanMode = device.currentState("thermostatFanMode")?.value
    if(!fanMode.equals(val)) {
        log.debug("UPDATED | Fan Mode: (${val.toString().capitalize()}) | Original State: (${fanMode.toString().capitalize()})")
        sendEvent(name: "thermostatFanMode", value: val, descriptionText: "Fan Mode is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("Fan Active: (${val}) | Original State: (${fanMode})") }
}

def operatingStateEvent(operatingState) {
    def hvacState = device.currentState("thermostatOperatingState")?.value
    def operState = (operatingState == "off") ? "idle" : operatingState
    if(!hvacState.equals(operState)) {
        log.debug("UPDATED | OperatingState is (${operState.toString().capitalize()}) | Original State: (${hvacState.toString().capitalize()})")
        sendEvent(name: 'thermostatOperatingState', value: operState, descriptionText: "Device is ${operState}", displayed: true, isStateChange: true)
    } else { Logger("OperatingState is (${operState}) | Original State: (${hvacState})") }
}

def tempLockOnEvent(isLocked) {
    def curState = device.currentState("tempLockOn")?.value.toString()
    def newState = isLocked?.toString()
    state?.tempLockOn = newState
    if(!curState?.equals(newState)) {
        log.debug("UPDATED | Temperature Lock is set to (${newState}) | Original State: (${curState})")
        sendEvent(name:'tempLockOn', value: newState,  descriptionText: "Temperature Lock: ${newState}" , displayed: false, isStateChange: true, state: newState)
    } else { Logger("Temperature Lock is set to (${newState}) | Original State: (${curState})") }
}

def lockedTempEvent(Double minTemp, Double maxTemp) {
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

def safetyTempsEvent(safetyTemps) {
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
        checkSafetyTemps()
    } else {
        Logger("Safety Temperature Minimum is  (${newMinTemp}${state?.tempUnit}) | Original Minimum Temp: (${curMinTemp}${state?.tempUnit})")
        Logger("Safety Temperature Maximum is  (${newMaxTemp}${state?.tempUnit}) | Original Maximum Temp: (${curMaxTemp}${state?.tempUnit})")
    }
}

def checkSafetyTemps() {
    def curMinTemp = device.currentState("safetyTempMin")?.doubleValue
    def curMaxTemp = device.currentState("safetyTempMax")?.doubleValue
    def curTemp = device.currentState("temperature")?.doubleValue
    def curRangeStr = device.currentState("safetyTempExceeded")?.toString()
    def curInRange = !curRangeStr?.toBoolean()
    def inRange = true
    if(curMinTemp && curMaxTemp) {
        if((curMinTemp > curTemp || curMaxTemp < curTemp)) { inRange = false }
    }
    //log.debug("curMin: ${curMinTemp}  curMax: ${curMaxTemp} curTemp: ${curTemp} curinRange: ${curinRange} inRange: ${inRange}")
    if (curRangeStr == null || inRange != curInRange) {
        sendEvent(name:'safetyTempExceeded', value: (inRange ? "false" : "true"),  descriptionText: "Safety Temperature ${inRange ? "OK" : "Exceeded"} ${curTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
        log.debug("UPDATED | Safety Temperature Exceeded is (${inRange ? "false" : "true"}) | Current Temp: (${curTemp}${state?.tempUnit})")
    } else {
        Logger("Safety Temperature Exceeded is (${inRange ? "false" : "true"}) | Current Temp: (${curTemp}${state?.tempUnit})")
    }
}

def comfortHumidityEvent(comfortHum) {
    //def curMinHum = device.currentState("comfortHumidityMin")?.integerValue
    def curMaxHum = device.currentState("comfortHumidityMax")?.integerValue
    //def newMinHum = comfortHum?.min.toInteger() ?: 0
    def newMaxHum = comfortHum?.toInteger() ?: 0
    if(curMaxHum != newMaxHum) {
        //log.debug("UPDATED | Comfort Humidity Minimum is (${newMinHum}) | Original Temp: (${curMinHum})")
        log.debug("UPDATED | Comfort Humidity Maximum is (${newMaxHum}%) | Original Humidity: (${curMaxHum}%)")
        sendEvent(name:'comfortHumidityMax', value: newMaxHum, unit: "%", descriptionText: "Safety Humidity Maximum is ${newMaxHum}%" , displayed: true, isStateChange: true)
    } else {
        //Logger("Comfort Humidity Minimum is (${newMinHum}) | Original Minimum Humidity: (${curMinHum})")
        Logger("Comfort Humidity Maximum is (${newMaxHum}%) | Original Maximum Humidity: (${curMaxHum}%)")
    }
}

def comfortDewpointEvent(comfortDew) {
    //def curMinDew = device.currentState("comfortDewpointMin")?.integerValue
    def curMaxDew = device.currentState("comfortDewpointMax")?.doubleValue
    //def newMinDew = comfortDew?.min.toInteger() ?: 0
    def newMaxDew = comfortDew?.toDouble() ?: 0.0
    if(curMaxDew != newMaxDew) {
        //log.debug("UPDATED | Comfort Dewpoint Minimum is (${newMinDew}) | Original Temp: (${curMinDew})")
        log.debug("UPDATED | Comfort Dewpoint Maximum is (${newMaxDew}) | Original Dewpoint: (${curMaxDew})")
        //sendEvent(name:'comfortDewpointMin', value: newMinDew, unit: "%", descriptionText: "Comfort Dewpoint Minimum is ${newMinDew}" , displayed: true, isStateChange: true)
        sendEvent(name:'comfortDewpointMax', value: newMaxDew, unit: state?.tempUnit, descriptionText: "Comfort Dewpoint Maximum is ${newMaxDew}" , displayed: true, isStateChange: true)
    } else {
        //Logger("Comfort Dewpoint is (${newMinDew}) | Original Minimum Dewpoint: (${curMinDew})")
        Logger("Comfort Dewpoint Maximum is (${newMaxDew}) | Original Maximum Dewpoint: (${curMaxDew})")
    }
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
    def curStat = device.currentState("apiStatus")?.value
    def newStat = issue ? "issue" : "ok"
    state?.apiStatus = newStat
    if(!curStat.equals(newStat)) {
        log.debug("UPDATED | API Status is: (${newStat.toString().capitalize()}) | Original State: (${curStat.toString().capitalize()})")
        sendEvent(name: "apiStatus", value: newStat, descriptionText: "API Status is: ${newStat}", displayed: true, isStateChange: true, state: newStat)
    } else { Logger("API Status is: (${newStat}) | Original State: (${curStat})") }
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
    return !device.currentValue("coolingSetpoint") ? 0 : device.currentValue("coolingSetpoint")
}

def getHeatTemp() {
    return !device.currentValue("heatingSetpoint") ? 0 : device.currentValue("heatingSetpoint")
}

def getFanMode() {
    return !device.currentState("thermostatFanMode")?.value ? "unknown" : device.currentState("thermostatFanMode")?.value.toString()
}

def getHvacMode() {
    return !state?.hvac_mode ? device.currentState("thermostatMode")?.value.toString() : state.hvac_mode
    //return !device.currentState("thermostatMode") ? "unknown" : device.currentState("thermostatMode")?.value.toString()
}

def getHvacState() {
    return !device.currentState("thermostatOperatingState") ? "unknown" : device.currentState("thermostatOperatingState")?.value.toString()
}

def getNestPresence() {
    return !state?.nestPresence ? device.currentState("nestPresence")?.value.toString() : state.nestPresence
    //return !device.currentState("nestPresence") ? "home" : device.currentState("nestPresence")?.value.toString()
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

def getHumidity() {
    return !device.currentValue("humidity") ? 0 : device.currentValue("humidity")
}

def getTempWaitVal() {
    return state?.childWaitVal ? state?.childWaitVal.toInteger() : 4
}

def wantMetric() { return (state?.tempUnit == "C") }


/************************************************************************************************
|							Temperature Setpoint Functions for Buttons							|
*************************************************************************************************/
void heatingSetpointUp() {
    //log.trace "heatingSetpointUp()..."
    def operMode = getHvacMode()
    if ( operMode == "heat" || operMode == "auto" ) {
        levelUpDown(1,"heat")
    }
}

void heatingSetpointDown() {
    //log.trace "heatingSetpointDown()..."
    def operMode = getHvacMode()
    if ( operMode == "heat" || operMode == "auto" ) {
        levelUpDown(-1, "heat")
    }
}

void coolingSetpointUp() {
    //log.trace "coolingSetpointUp()..."
    def operMode = getHvacMode()
    if ( operMode == "cool" || operMode == "auto" ) {
        levelUpDown(1, "cool")
    }
}

void coolingSetpointDown() {
    //log.trace "coolingSetpointDown()..."
    def operMode = getHvacMode()
    if ( operMode == "cool" || operMode == "auto" ) {
        levelUpDown(-1, "cool")
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
            pauseEvent("true")
            switch (hvacMode) {
                case "heat":
                    Logger("Sending changeSetpoint(Temp: ${targetVal})")
                    if (state?.oldHeat == null) { state.oldHeat = curHeatpoint}
                    thermostatSetpointEvent(targetVal)
                    heatingSetpointEvent(targetVal)
                    if (!chgType) { chgType = "" }
//                        runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
                    scheduleChangeSetpoint()
                    break
                case "cool":
                    Logger("Sending changeSetpoint(Temp: ${targetVal})")
                    if (state?.oldCool == null) { state.oldCool = curCoolpoint}
                    thermostatSetpointEvent(targetVal)
                    coolingSetpointEvent(targetVal)
                    if (!chgType) { chgType = "" }
//                        runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
                    scheduleChangeSetpoint()
                    break
                case "auto":
                    if (chgType) {
                        switch (chgType) {
                            case "cool":
                                Logger("Sending changeSetpoint(Temp: ${targetVal})")
                                if (state?.oldCool == null) { state.oldCool = curCoolpoint}
                                coolingSetpointEvent(targetVal)
//                                    runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
                                scheduleChangeSetpoint()
                                break
                            case "heat":
                                Logger("Sending changeSetpoint(Temp: ${targetVal})")
                                if (state?.oldHeat == null) { state.oldHeat = curHeatpoint}
                                heatingSetpointEvent(targetVal)
//                                    runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
                                scheduleChangeSetpoint()
                                break
                            default:
                                log.warn "Can not change temp while in this mode ($chgType}!!!"
                                break
                        }
                    } else { log.warn "Temp Change without a chgType is not supported!!!" }
                    break
                default:
                    pauseEvent("false")
                    log.warn "Unsupported Mode Received: ($hvacMode}!!!"
                    break
            }
        }
    } else { log.debug "levelUpDown: Cannot adjust temperature due to presence: $state?.present or hvacMode $hvacMode" }
}

def scheduleChangeSetpoint() {
    if (getLastChangeSetpointSec() > 15) {
        state?.lastChangeSetpointDt = getDtNow()
        runIn( 25, "changeSetpoint", [overwrite: true] )
    }
}

def getLastChangeSetpointSec() { return !state?.lastChangeSetpointDt ? 100000 : GetTimeDiffSeconds(state?.lastChangeSetpointDt).toInteger() }

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

def formatDt(dt) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
    else {
        log.warn "SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save..."
    }
    return tf.format(dt)
}


//Returns time differences is seconds
def GetTimeDiffSeconds(lastDate) {
    if(lastDate?.contains("dtNow")) { return 10000 }
    def now = new Date()
    def lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
    def start = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(lastDt)).getTime()
    def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(now)).getTime()
    def diff = (int) (long) (stop - start) / 1000
    return diff
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

void changeSetpoint() {
    //log.trace "changeSetpoint()... ($val)"
    try {
        if ( canChangeTemp() ) {

//            def temp = val?.temp?.value.toDouble()
//            def md = !val?.mode?.value ? null : val?.mode?.value

            def md
            def hvacMode = getHvacMode()
            def curHeatpoint = getHeatTemp()
            def curCoolpoint = getCoolTemp()

            log.trace "changeSetpoint()... hvacMode: ${hvacMode} curHeatpoint: ${curHeatpoint}  curCoolpoint: ${curCoolpoint} oldCool: ${state?.oldCool} oldHeat: ${state?.oldHeat}"

            switch (hvacMode) {
                case "heat":
                    state.oldHeat = null
                    setHeatingSetpoint(curHeatpoint)
                    break
                case "cool":
                    state.oldCool = null
                    setCoolingSetpoint(curCoolpoint)
                    break
                case "auto":
                    if ( (state?.oldCool != null) && (state?.oldHeat == null) ) { md = "cool"}
                    if ( (state?.oldCool == null) && (state?.oldHeat != null) ) { md = "heat"}
                    if ( (state?.oldCool != null) && (state?.oldHeat != null) ) { md = "both"}

                    def heatFirst
                    if(md) {
                        if (curHeatpoint >= curCoolpoint) {
                            log.warn "changeSetpoint: Invalid Temp Type received in auto mode... ${curHeatpoint} ${curCoolpoint}"
                        } else {
                            if("${md}" == "heat") { state.oldHeat = null; setHeatingSetpoint(curHeatpoint) }
                            else if ("${md}" == "cool") { state.oldCool = null; setCoolingSetpoint(curCoolpoint) }
                            else if ("${md}" == "both") {
                                if (curHeatpoint <= state.oldHeat) { heatfirst = true }
                                else if (curCoolpoint >= state.oldCool) { heatFirst = false }
                                else if (curHeatpoint > state.oldHeat) { heatFirst = false }
                                else { heatFirst = true }
                                if (heatFirst) {
                                    state.oldHeat = null
                                    setHeatingSetpoint(curHeatpoint)
                                    state.oldCool = null
                                    setCoolingSetpoint(curCoolpoint)
                                } else {
                                    state.oldCool = null
                                    setCoolingSetpoint(curCoolpoint)
                                    state.oldHeat = null
                                    setHeatingSetpoint(curHeatpoint)
                                }
                            }
                        }
                    } else {
                        log.warn "changeSetpoint: Invalid Temp Type received... ${md}"
                        state.oldCool = null
                        state.oldHeat = null
                    }
                    break
                default:
                    if (curHeatpoint > curCoolpoint) {
                        log.warn "changeSetpoint: Invalid Temp Type received in auto mode... ${curHeatpoint} ${curCoolpoint} ${val}"
                    }
                    //thermostatSetpointEvent(temp)
                    break
            }
        }
        pauseEvent("false")
    }
    catch (ex) {
        pauseEvent("false")
        log.error "changeSetpoint Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "changeSetpoint")
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
                            parent.setTargetTempLow(this, tempUnit, temp, virtType())
                            heatingSetpointEvent(temp)
                        }
                        if (hvacMode == 'heat') {
                            parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
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
                            parent.setTargetTempLow(this, tempUnit, temp, virtType())
                            heatingSetpointEvent(temp)
                        }
                        if (hvacMode == 'heat') {
                            parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
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
        log.error "setHeatingSetpoint Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "setHeatingSetpoint")
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
                            parent.setTargetTempHigh(this, tempUnit, temp, virtType())
                            coolingSetpointEvent(temp)
                        }
                        if (hvacMode == 'cool') {
                            parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
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
                            parent.setTargetTempHigh(this, tempUnit, temp, virtType())
                            coolingSetpointEvent(temp)
                        }
                        if (hvacMode == 'cool') {
                            parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
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
        log.error "setCoolingSetpoint Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "setCoolingSetpoint")
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
            if (parent.setStructureAway(this, "false", virtType())) { presenceEvent("home") }
        }
        else if (pres == "home") {
            if (parent.setStructureAway(this, "true", virtType())) { presenceEvent("away") }
        }
    }
    catch (ex) {
        log.error "setPresence Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "setPresence")
    }
}

// backward compatibility for previous nest thermostat (and rule machine)
void away() {
    try {
        log.trace "away()..."
        setAway()
    }
    catch (ex) {
        log.error "away Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "away")
    }
}

// backward compatibility for previous nest thermostat (and rule machine)
void present() {
    try {
        log.trace "present()..."
        setHome()
    }
    catch (ex) {
        log.error "present Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "present")
    }
}

def setAway() {
    try {
        log.trace "setAway()..."
        if (parent.setStructureAway(this, "true", virtType())) { presenceEvent("away") }
    }
    catch (ex) {
        log.error "setAway Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "setAway")
    }
}

def setHome() {
    try {
        log.trace "setHome()..."
        if (parent.setStructureAway(this, "false", virtType()) ) { presenceEvent("home") }
    }
    catch (ex) {
        log.error "setHome Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "setHome")
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
        def currentMode = getHvacMode()
        def lastTriedMode = currentMode ?: "off"
        def modeOrder = getHvacModes()
        def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
        def nextMode = next(lastTriedMode)
        log.trace "changeMode() currentMode: ${currentMode}   lastTriedMode:  ${lastTriedMode}  modeOrder:  ${modeOrder}   nextMode: ${nextMode}"
        setHvacMode(nextMode)
    }
    catch (ex) {
        log.error "changeMode Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "changeMode")
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
        log.error "setHvacMode Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "setHvacMode")
    }
}

def doChangeMode() {
    try {
        def currentMode = device.currentState("thermostatMode")?.value
        log.debug "doChangeMode()  currentMode:  ${currentMode}"
        def errflag = true
        switch(currentMode) {
            case "auto":
                if (parent.setHvacMode(this, "heat-cool", virtType())) {
                    errflag = false
                }
                break
            case "heat":
                if (parent.setHvacMode(this, "heat", virtType())) {
                    errflag = false
                }
                break
            case "cool":
                if (parent.setHvacMode(this, "cool", virtType())) {
                    errflag = false
                }
                break
            case "off":
                if (parent.setHvacMode(this, "off", virtType())) {
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
        log.error "doChangeMode Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "doChangeMode")
    }
}

void off() {
    log.trace "off()..."
    hvacModeEvent("off")
    doChangeMode()
}

void heat() {
    log.trace "heat()..."
    hvacModeEvent("heat")
    doChangeMode()
}

void emergencyHeat() {
    log.trace "emergencyHeat()..."
    log.warn "Emergency Heat setting not allowed"
}

void cool() {
    log.trace "cool()..."
    hvacModeEvent("cool")
    doChangeMode()
}

void auto() {
    log.trace "auto()..."
    hvacModeEvent("auto")
    doChangeMode()
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
    try {
        log.trace "fanOn()..."
        if ( state?.has_fan.toBoolean() ) {
            if (parent.setFanMode(this, true, virtType()) ) { fanModeEvent("true") }
        } else { log.error "Error setting fanOn" }
    }
    catch (ex) {
        log.error "fanOn Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "fanOn")
    }
}

// non standard by ST Capabilities Thermostat Fan Mode
void fanOff() {
    log.trace "fanOff()..."
    fanAuto()
}

void fanCirculate() {
    log.trace "fanCirculate()..."
    fanOn()
}

void fanAuto() {
    try {
        log.trace "fanAuto()..."
        if ( state?.has_fan.toBoolean() ) {
            if (parent.setFanMode(this,false, virtType()) ) { fanModeEvent("false") }
        } else { log.error "Error setting fanAuto" }
    }
    catch (ex) {
        log.error "fanAuto Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "fanAuto")
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
        case "off":   // non standard by ST Capabilities Thermostat Fan Mode
            fanOff()
            break
        default:
            log.warn "setThermostatFanMode Received an Invalid Request: ${fanModeStr}"
            break
    }
}


/**************************************************************************
|										        LOGGING FUNCTIONS										          |
***************************************************************************/

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

def exceptionDataHandler(msg, methodName) {
    if(state?.allowDbException == false) {
        return
    } else {
        if(msg && methodName) {
            def msgString = "${msg}"
            parent?.sendChildExceptionData("thermostat", devVer(), msgString, methodName)
        }
    }
}


/**************************************************************************
|										  HTML TILE RENDER FUNCTIONS										      |
***************************************************************************/

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
                int size = 3072
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
        log.error "getImageBytes Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "getImgBase64")
    }
}

def getFileBase64(url,preType,fileType) {
    try {
        def params = [
            uri: url,
            contentType: '$preType/$fileType'
        ]
        httpGet(params) { resp ->
            if(resp.data) {
                def respData = resp?.data
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
                int len
                int size = 4096
                byte[] buf = new byte[size]
                while ((len = respData.read(buf, 0, size)) != -1)
                    bos.write(buf, 0, len)
                buf = bos.toByteArray()
                //log.debug "buf: $buf"
                String s = buf?.encodeBase64()
                //log.debug "resp: ${s}"
                return s ? "data:${preType}/${fileType};base64,${s.toString()}" : null
            }
        }
    }
    catch (ex) {
        log.error "getFileBase64 Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "getFileBase64")
    }
}

def getCSS(url = null){
    try {
        def params = [
            uri: !url ? cssUrl() : url?.toString(),
            contentType: 'text/css'
        ]
        httpGet(params)  { resp ->
            return resp?.data.text
        }
    }
    catch (ex) {
        log.error "getCss Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "getCSS")
    }
}

def getJS(url){
    def params = [
        uri: url?.toString(),
        contentType: "text/plain"
    ]
    httpGet(params)  { resp ->
        return resp?.data.text
    }
}

def getCssData() {
    def cssData = null
    //def htmlInfo = state?.htmlInfo
//ERS
    def htmlInfo
    state.cssData = null

    if(htmlInfo?.cssUrl && htmlInfo?.cssVer) {
        if(state?.cssData) {
            if (state?.cssVer?.toInteger() == htmlInfo?.cssVer?.toInteger()) {
                log.debug "getCssData: CSS Data is Current | Loading Data from State..."
                cssData = state?.cssData
            } else if (state?.cssVer?.toInteger() < htmlInfo?.cssVer?.toInteger()) {
                log.debug "getCssData: CSS Data is Outdated | Loading Data from Source..."
                cssData = getFileBase64(htmlInfo.cssUrl, "text", "css")
                state.cssData = cssData
                state?.cssVer = htmlInfo?.cssVer
            }
        } else {
            log.debug "getCssData: CSS Data is Missing | Loading Data from Source..."
            cssData = getFileBase64(htmlInfo.cssUrl, "text", "css")
            state?.cssData = cssData
            state?.cssVer = htmlInfo?.cssVer
        }
    } else {
        log.debug "getCssData: No Stored CSS Info Data Found for Device... Loading for Static URL..."
        cssData = getFileBase64(cssUrl(), "text", "css")
    }
    return cssData
}

def getChartJsData() {
    def chartJsData = null
    //def htmlInfo = state?.htmlInfo
//ERS
    def htmlInfo
    state.chartJsData = null

    if(htmlInfo?.chartJsUrl && htmlInfo?.chartJsVer) {
        if(state?.chartJsData) {
            if (state?.chartJsVer?.toInteger() == htmlInfo?.chartJsVer?.toInteger()) {
                log.debug "getChartJsData: Chart Javascript Data is Current | Loading Data from State..."
                chartJsData = state?.chartJsData
            } else if (state?.chartJsVer?.toInteger() < htmlInfo?.chartJsVer?.toInteger()) {
                log.debug "getChartJsData: Chart Javascript Data is Outdated | Loading Data from Source..."
                chartJsData = getFileBase64(htmlInfo.chartJsUrl, "text", "javascript")
                state.chartJsData = chartJsData
                state?.chartJsVer = htmlInfo?.chartJsVer
            }
        } else {
            log.debug "getChartJsData: Chart Javascript Data is Missing | Loading Data from Source..."
            chartJsData = getFileBase64(htmlInfo.chartJsUrl, "text", "javascript")
            state?.chartJsData = chartJsData
            state?.chartJsVer = htmlInfo?.chartJsVer
        }
    } else {
        log.debug "getChartJsData: No Stored Chart Javascript Data Found for Device... Loading for Static URL..."
        chartJsData = getFileBase64(chartJsUrl(), "text", "javascript")
    }
    return chartJsData
}

def cssUrl() { return "https://raw.githubusercontent.com/desertblade/ST-HTMLTile-Framework/master/css/smartthings.css" }
def chartJsUrl() { return "https://www.gstatic.com/charts/loader.js" }

def getImg(imgName) {
    return imgName ? "https://cdn.rawgit.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : ""
}

/*
     variable      attribute for history       getRoutine             variable is present

   temperature       "temperature"              getTemp                   true                         #
   coolSetpoint     "coolingSetpoint"           getCoolTemp           state.can_cool                   #
   heatSetpoint     "heatingSetpoint"           getHeatTemp           state.can_heat                   #
   operatingState "thermostatOperatingState"   getHvacState                true                 idle cooling heating
   operatingMode    "thermostatMode"           getHvacMode                true                 heat cool off auto
    presence           "presence"              getPresence                true                 present  not present
*/

String getDataString(Integer seriesIndex) {
    //log.trace "getDataString ${seriesIndex}"
    def dataString = ""
    def dataTable = []
    switch (seriesIndex) {
        case 1:
            dataTable = state?.temperatureTableYesterday
            break
        case 2:
           dataTable = state?.temperatureTable
            break
        case 3:
            dataTable = state?.operatingStateTable
            break
        case 4:
            dataTable = state?.humidityTable
            break
        case 5:
            dataTable = state?.coolSetpointTable
            break
        case 6:
            dataTable = state?.heatSetpointTable
            break
    }

    def lastVal = 200

    //log.debug "getDataString ${seriesIndex} ${dataTable}"
    //log.debug "getDataString ${seriesIndex}"

    def lastAdded = false
    def dataArray
    def myval
    def myindex
    def lastdataArray = null


    if (seriesIndex == 5) {
      // state.can_cool
    }
    if (seriesIndex == 6) {
       // state.can_heat
    }

    dataTable.each() {
        myindex = seriesIndex
        if (state?.can_heat && state?.can_cool) { dataArray = [[it[0],it[1],0],null,null,null,null,null,null] }
        else {
            dataArray = [[it[0],it[1],0],null,null,null,null,null]
            if (myindex == 6) {myindex = 5}
        }
        //convert idle / non-idle to numeric value
        if (myindex == 3) {
            myval = it[2]
            if (myval == "idle") { myval = 0 }
            else { myval = 8 }
        } else { myval = it[2] }

        dataArray[myindex] = myval

        //reduce # of points to graph
        if (lastVal != myval) {
            lastAdded = true
            if (lastdataArray) {   //controls curves
                dataString += lastdataArray?.toString() + ","
            }
            lastdataArray = null
            lastVal = myval
            dataString += dataArray?.toString() + ","
        } else { lastAdded = false; lastdataArray = dataArray }
    }

    if (!lastAdded && dataString) {
        dataArray[myindex] = myval
        dataString += dataArray?.toString() + ","
    }

    if (dataString == "") {
        dataArray = [[0,0,0],null,null,null,null,null,null]
        dataArray[myindex] = 0
        dataString += dataArray?.toString() + ","
    }
    //log.debug "${dataString}"
    return dataString
}

def tgetSomeOldData(val) {
    log.trace "tgetSomeOldData ${val}"
    def type = val?.type?.value
    def attributestr  = val?.attributestr?.value
    def gfloat = val?.gfloat?.value
    def devpoll = val?.devpoll?.value
    log.trace "calling getSomeOldData ( ${type}, ${attributestr}, ${gfloat}, ${devpoll})"
    getSomeOldData(type, attributestr, gfloat, devpoll)
}

def getSomeOldData(type, attributestr, gfloat, devpoll = false, nostate = true) {
    log.trace "getSomeOldData ( ${type}, ${attributestr}, ${gfloat}, ${devpoll})"

//    if (devpoll && (!state?."${type}TableYesterday" || !state?."${type}Table")) {
//        runIn( 66, "tgetSomeOldData", [data: [type:type, attributestr:attributestr, gfloat:gfloat, devpoll:false]])
//        return
//    }

    def startOfToday = timeToday("00:00", location.timeZone)
    def newValues
    def dataTable = []

    if (( nostate || state?."${type}TableYesterday" == null) && attributestr ) {
        log.trace "Querying DB for yesterday's ${type} data…"
        def yesterdayData = device.statesBetween("${attributestr}", startOfToday - 1, startOfToday, [max: 100])
        log.debug "got ${yesterdayData.size()}"
        if (yesterdayData.size() > 0) {
            while ((newValues = device.statesBetween("${attributestr}", startOfToday - 1, yesterdayData.last().date, [max: 100])).size()) {
                log.debug "got ${newValues.size()}"
                yesterdayData += newValues
            }
        }
        log.debug "got ${yesterdayData.size()}"
        dataTable = []
        yesterdayData.reverse().each() {
            if (gfloat) { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.floatValue]) }
            else { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.stringValue]) }
        }
        log.debug "finished ${dataTable}"
        if (!nostate) {
            state."${type}TableYesterday" = dataTable
        }
    }

    if ( nostate || state?."${type}Table" == null) {
        log.trace "Querying DB for today's ${type} data…"
        def todayData = device.statesSince("${attributestr}", startOfToday, [max: 100])
        log.debug "got ${todayData.size()}"
        if (todayData.size() > 0) {
            while ((newValues = device.statesBetween("${attributestr}", startOfToday, todayData.last().date, [max: 100])).size()) {
                log.debug "got ${newValues.size()}"
                todayData += newValues
            }
        }
        log.debug "got ${todayData.size()}"
        dataTable = []
        todayData.reverse().each() {
            if (gfloat) { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.floatValue]) }
            else { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.stringValue]) }
        }
        log.debug "finished ${dataTable}"
        if (!nostate) {
            state."${type}Table" = dataTable
        }
    }
}

def getSomeData(devpoll = false) {
    //log.trace "getSomeData ${app}"

// hackery to test getting old data
    def tryNum = 1
    if (state.eric != tryNum ) {
        if (devpoll) {
            runIn( 33, "getSomeData", [overwrite: true])
            return
        }

        runIn( 33, "getSomeData", [overwrite: true])
        state.eric = tryNum

        state.temperatureTableYesterday = null
        state.operatingStateTableYesterday = null
        state.humidityTableYesterday = null
        state.coolSetpointTableYesterday = null
        state.heatSetpointTableYesterday = null

        state.temperatureTable = null
        state.operatingStateTable = null
        state.humidityTable = null
        state.coolSetpointTable = null
        state.heatSetpointTable = null

        state.remove("temperatureTableYesterday")
        state.remove("operatingStateTableYesterday")
        state.remove("humidityTableYesterday")
        state.remove("coolSetpointTableYesterday")
        state.remove("heatSetpointTableYesterday")

        state.remove("today")
        state.remove("temperatureTable")
        state.remove("operatingStateTable")
        state.remove("humidityTable")
        state.remove("coolSetpointTable")
        state.remove("heatSetpointTable")

        return
    } else {
        //getSomeOldData("temperature", "temperature", true, devpoll)
        //getSomeOldData("operatingState", "thermostatOperatingState", false, devpoll)
        //getSomeOldData("humidity", "humidity", false, devpoll)
        //if (state?.can_cool) { getSomeOldData("coolSetpoint", "coolingSetpoint", true, devpoll) }
        //if (state?.can_heat) { getSomeOldData("heatSetpoint", "heatingSetpoint", true, devpoll) }
    }

    def todayDay = new Date().format("dd",location.timeZone)

    if (state?.temperatureTable == null) {

    // these are commented out as the platform continuously times out
        //getSomeOldData("temperature", "temperature", true, devpoll)
        //getSomeOldData("operatingState", "thermostatOperatingState", false, devpoll)
        //getSomeOldData("humidity", "humidity", false, devpoll)
        //if (state?.can_cool) { getSomeOldData("coolSetpoint", "coolingSetpoint", true, devpoll) }
        //if (state?.can_heat) { getSomeOldData("heatSetpoint", "heatingSetpoint", true, devpoll) }

        state.temperatureTable = []
        state.operatingStateTable = []
        state.humidityTable = []
        state.coolSetpointTable = []
        state.heatSetpointTable = []
        addNewData()
    }

    def temperatureTable = state?.temperatureTable
    def operatingStateTable = state?.operatingStateTable
    def humidityTable = state?.humidityTable
    def coolSetpointTable = state?.coolSetpointTable
    def heatSetpointTable = state?.heatSetpointTable

    if (state?.temperatureTableYesterday?.size() == 0) {
        state.temperatureTableYesterday = temperatureTable
        state.operatingStateTableYesterday = operatingStateTable
        state.humidityTableYesterday = humidityTable
        state.coolSetpointTableYesterday = coolSetpointTable
        state.heatSetpointTableYesterday = heatSetpointTable
    }

    if (!state?.today || state.today != todayDay) {
        state.today = todayDay
        state.temperatureTableYesterday = temperatureTable
        state.operatingStateTableYesterday = operatingStateTable
        state.humidityTableYesterday = humidityTable
        state.coolSetpointTableYesterday = coolSetpointTable
        state.heatSetpointTableYesterday = heatSetpointTable

        state.temperatureTable = []
        state.operatingStateTable = []
        state.humidityTable = []
        state.coolSetpointTable = []
        state.heatSetpointTable = []
    }
    addNewData()
}

def addNewData() {
    def currentTemperature = getTemp()
    def currentcoolSetPoint = getCoolTemp()
    def currentheatSetPoint = getHeatTemp()
    def currentoperatingState = getHvacState()
    def currenthumidity = getHumidity()

    def temperatureTable = state?.temperatureTable
    def operatingStateTable = state?.operatingStateTable
    def humidityTable = state?.humidityTable
    def coolSetpointTable = state?.coolSetpointTable
    def heatSetpointTable = state?.heatSetpointTable

    // add latest coolSetpoint & temperature readings for the graph
    def newDate = new Date()
    temperatureTable?.add([newDate.format("H", location.timeZone),newDate.format("m", location.timeZone),currentTemperature])
    operatingStateTable?.add([newDate.format("H", location.timeZone),newDate.format("m", location.timeZone),currentoperatingState])
    humidityTable?.add([newDate.format("H", location.timeZone),newDate.format("m", location.timeZone),currenthumidity])
    coolSetpointTable?.add([newDate.format("H", location.timeZone),newDate.format("m", location.timeZone),currentcoolSetPoint])
    heatSetpointTable?.add([newDate.format("H", location.timeZone),newDate.format("m", location.timeZone),currentheatSetPoint])

    state.temperatureTable = temperatureTable
    state.operatingStateTable = operatingStateTable
    state.humidityTable = humidityTable
    state.coolSetpointTable = coolSetpointTable
    state.heatSetpointTable = heatSetpointTable
}

def getStartTime() {
    def startTime = 24
    if (state?.temperatureTable?.size()) { startTime = state?.temperatureTable?.min{it[0].toInteger()}[0].toInteger() }
    if (state?.temperatureTableYesterday?.size()) { startTime = Math.min(startTime, state?.temperatureTableYesterday?.min{it[0].toInteger()}[0].toInteger()) }
    //log.trace "startTime ${startTime}"
    return startTime
}

def getMinTemp() {
    def list = []
    if (state?.temperatureTableYesterday?.size()) { list.add(state?.temperatureTableYesterday?.min { it[2] }[2].toInteger()) }
    if (state?.temperatureTable?.size()) { list.add(state?.temperatureTable.min { it[2] }[2].toInteger()) }
    if (state?.can_cool && state?.coolSetpointTable?.size()) { list.add(state?.coolSetpointTable.min { it[2] }[2].toInteger()) }
    if (state?.can_heat && state?.heatSetpointTable?.size()) { list.add(state?.heatSetpointTable.min { it[2] }[2].toInteger()) }
    //log.trace "getMinTemp: ${list.min()} result: ${list}"
    return list?.min()
}

def getMaxTemp() {
    def list = []
    if (state?.temperatureTableYesterday?.size()) { list.add(state?.temperatureTableYesterday.max { it[2] }[2].toInteger()) }
    if (state?.temperatureTable?.size()) { list.add(state?.temperatureTable.max { it[2] }[2].toInteger()) }
    if (state?.can_cool && state?.coolSetpointTable?.size()) { list.add(state?.coolSetpointTable.max { it[2] }[2].toInteger()) }
    if (state?.can_heat && state?.heatSetpointTable?.size()) { list.add(state?.heatSetpointTable.max { it[2] }[2].toInteger()) }
    //log.trace "getMaxTemp: ${list.max()} result: ${list}"
    return list?.max()
}

def getGraphHTML() {
    try {
        log.debug "State Size: ${getStateSize()} (${getStateSizePerc()}%)"
        def leafImg = state?.hasLeaf ? getImgBase64(getImg("nest_leaf_on.gif"), "gif") : getImgBase64(getImg("nest_leaf_off.gif"), "gif")
        def updateAvail = !state.updateAvailable ? "" : "<h3>Device Update Available!</h3>"

        def chartHtml = (
                state.temperatureTable?.size() > 0 &&
                state.operatingStateTable?.size() > 0 &&
                state.temperatureTableYesterday?.size() > 0 &&
                state.humidityTable?.size() > 0 &&
                state.coolSetpointTable?.size() > 0 &&
                state.heatSetpointTable?.size() > 0) ? showChartHtml() : hideChartHtml()

        def html = """
        <!DOCTYPE html>
        <html>
            <head>
                <meta http-equiv="cache-control" content="max-age=0"/>
                <meta http-equiv="cache-control" content="no-cache"/>
                <meta http-equiv="expires" content="0"/>
                <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
                <meta http-equiv="pragma" content="no-cache"/>
                <meta name="viewport" content="width = device-width, user-scalable=no, initial-scale=1.0">
                <link rel="stylesheet prefetch" href="${getCssData()}"/>
                <script type="text/javascript" src="${getChartJsData()}"></script>
            </head>
            <body>
                ${updateAvail}

                ${chartHtml}

                <br></br>
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
                    <td><img src="${leafImg}" class="leafImg"></img></td>
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
            </body>
        </html>
        """
        render contentType: "text/html", data: html, status: 200
    } catch (ex) {
        log.error "graphHTML Exception: ${ex}", ex
        exceptionDataHandler(ex.message, "graphHTML")
    }

}

def showChartHtml() {
    def tempStr = "°F"
    if ( wantMetric() ) {
        tempStr = "°C"
    }

    def coolstr1 = "data.addColumn('number', 'CoolSP');"
    def coolstr2 =  getDataString(5)
    def coolstr3 = "4: {targetAxisIndex: 1, type: 'line', color: '#85AAFF', lineWidth: 1},"

    def heatstr1 = "data.addColumn('number', 'HeatSP');"
    def heatstr2 = getDataString(6)
    def heatstr3 = "5: {targetAxisIndex: 1, type: 'line', color: '#FF4900', lineWidth: 1}"

    if (state?.can_cool && !state?.can_heat) { coolstr3 = "4: {targetAxisIndex: 1, type: 'line', color: '#85AAFF', lineWidth: 1}" }

    if (!state?.can_cool && state?.can_heat) { heatstr3 = "4: {targetAxisIndex: 1, type: 'line', color: '#FF4900', lineWidth: 1}" }

    if (!state?.can_cool) {
        coolstr1 = ""
        coolstr2 = ""
        coolstr3 = ""
    }

    if (!state?.can_heat) {
        heatstr1 = ""
        heatstr2 = ""
        heatstr3 = ""
    }

    def minval = getMinTemp()
    def minstr = "minValue: ${minval},"

    def maxval = getMaxTemp()
    def maxstr = "maxValue: ${maxval},"

    def differ = maxval - minval
    if (differ > (maxval/4) || differ < (wantMetric() ? 10:20) ) {
        minstr = "minValue: ${(minval - (wantMetric() ? 10:20))},"
        if (differ < (wantMetric() ? 10:20) ) {
            maxstr = "maxValue: ${(maxval + (wantMetric() ? 10:20))},"
        }
    }

    def data = """
    <script type="text/javascript">
        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(drawGraph);
        function drawGraph() {
            var data = new google.visualization.DataTable();
            data.addColumn('timeofday', 'time');
            data.addColumn('number', 'Temp (Y)');
            data.addColumn('number', 'Temp (T)');
            data.addColumn('number', 'Operating');
            data.addColumn('number', 'Humidity');
            ${coolstr1}
            ${heatstr1}
            data.addRows([
                ${getDataString(1)}
                ${getDataString(2)}
                ${getDataString(3)}
                ${getDataString(4)}
                ${coolstr2}
                ${heatstr2}
            ]);
            var options = {
            width: '100%',
            height: '100%',
                hAxis: {
                    format: 'H:mm',
                    minValue: [${getStartTime()},0,0],
                    slantedText: true,
                    slantedTextAngle: 30
                },
                series: {
                    0: {targetAxisIndex: 1, type: 'area', color: '#FFC2C2', lineWidth: 1},
                    1: {targetAxisIndex: 1, type: 'area', color: '#FF0000'},
                    2: {targetAxisIndex: 0, type: 'area', color: '#ffdc89'},
                    3: {targetAxisIndex: 0, type: 'area', color: '#B8B8B8'},
                    ${coolstr3}
                    ${heatstr3}
                },
                vAxes: {
                    0: {
                        title: 'Humidity (%)',
                        format: 'decimal',
                        minValue: 0,
                        maxValue: 100,
                        textStyle: {color: '#B8B8B8'},
                        titleTextStyle: {color: '#B8B8B8'}
                    },
                    1: {
                        title: 'Temperature (${tempStr})',
                        format: 'decimal',
                        ${minstr}
                        ${maxstr}
                        textStyle: {color: '#FF0000'},
                        titleTextStyle: {color: '#FF0000'}
                    }
                },
                legend: {
                    position: 'bottom',
                    maxLines: 4,
                    textStyle: {color: '#000000'}
                },
                chartArea: {
                    left: '12%',
                    right: '18%',
                    top: '3%',
                    bottom: '20%',
                    height: '85%',
                    width: '100%'
                }
            };
            var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
            chart.draw(data, options);
        }
      </script>
      <h4 style="font-size: 22px; font-weight: bold; text-align: center; background: #00a1db; color: #f5f5f5;">Event History</h4>
      <div id="chart_div" style="width: 100%; height: 225px;"></div>
    """
    return data
}

def hideChartHtml() {
    def data = """
    <h4 style="font-size: 22px; font-weight: bold; text-align: center; background: #00a1db; color: #f5f5f5;">Event History</h4>
    <br></br>
    <div class="centerText">
      <p>Waiting for more data to be collected...</p>
      <p>This may take at least 24 hours</p>
    </div>
    """
    return data
}

private def textDevName()  { return "Nest ${virtDevName()}Thermostat${appDevName()}" }
private def appDevType()   { return false }
private def appDevName()   { return appDevType() ? " (Dev)" : "" }
private def virtType()     { return false }
private def virtDevName()   { return virtType() ? "Virtual " : "" }
