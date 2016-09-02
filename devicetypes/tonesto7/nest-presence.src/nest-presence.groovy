/**
 *  Nest Presence
 *	Author: Ben W. (@desertBlade)
 *	Author: Anthony S. (@tonesto7)
 *
 *
 * Copyright (C) 2016 Ben W, Anthony S.
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

preferences {  }

def devVer() { return "3.1.0" }

// for the UI
metadata {
    definition (name: "${textDevName()}", namespace: "tonesto7", author: "DesertBlade") {

        capability "Presence Sensor"
        capability "Sensor"
        capability "Refresh"

        command "setPresence"
        command "refresh"
        command "log"

        attribute "lastConnection", "string"
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
        attribute "devTypeVer", "string"
        attribute "nestPresence", "string"
    }

    simulator {
        status "present": "presence: 1"
        status "not present": "presence: 0"
    }

    tiles(scale: 2) {
        standardTile("presence", "device.presence", width: 4, height: 4, canChangeBackground: true) {
            state("present", 	labelIcon:"st.presence.tile.mobile-present", 	backgroundColor:"#53a7c0", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/nest_dev_pres_icon.png")
            state("not present",labelIcon:"st.presence.tile.mobile-not-present",backgroundColor:"#ebeef2", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/nest_dev_away_icon.png")
        }
        standardTile("nestPresence", "device.nestPresence", width:2, height:2, decoration: "flat") {
            state "home",	action: "setPresence",	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_home_icon.png"
            state "away", 		action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_away_icon.png"
            state "auto-away", 	action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_autoaway_icon.png"
            state "unknown", 	action: "setPresence", 	icon: "st.unknown.unknown.unknown"
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
        main ("presence")
        details ("presence", "nestPresence", "refresh")
    }
}

def initialize() {
    log.debug "initialize"
}

def parse(String description) {
    log.debug "Parsing '${description}'"
}

def configure() { }

def poll() {
    log.debug "Polling parent..."
    parent.refresh(this)
}

def refresh() {
    poll()
}

// parent calls this method to queue data.
// goal is to return to parent asap to avoid execution timeouts

def generateEvent(Map eventData) {
    //log.trace("generateEvent Parsing data ${eventData}")
    state.eventData = eventData
    runIn(3, "processEvent", [overwrite: true] )
}

def processEvent() {
    def eventData = state?.eventData
    state.eventData = null
    //log.trace("processEvent Parsing data ${eventData}")
    try {
        Logger("------------START OF API RESULTS DATA------------", "warn")
        if(eventData) {
            state.nestTimeZone = !location?.timeZone ? eventData?.tz : null
            state?.useMilitaryTime = !eventData?.mt ? false : true
            debugOnEvent(!eventData?.debug ? false : true)
            presenceEvent(eventData?.pres)
            apiStatusEvent((!eventData?.apiIssues ? false : true))
            deviceVerEvent(eventData?.latestVer.toString())
            if(eventData?.allowDbException) { state?.allowDbException = eventData?.allowDbException = false ? false : true }
            lastUpdatedEvent()
        }
        //This will return all of the devices state data to the logs.
        //log.debug "Device State Data: ${getState()}"
        return null
    }
    catch (ex) {
        log.error "generateEvent Exception:", ex
        exceptionDataHandler(ex.message, "generateEvent")
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
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = state?.nestTimeZone ? TimeZone.getTimeZone(state?.nestTimeZone) : null }
    if(!tz) { log.warn "getTimeZone: Hub or Nest TimeZone is not found ..." }
    return tz
}

def deviceVerEvent(ver) {
    def curData = device.currentState("devTypeVer")?.value
    def pubVer = ver ?: null
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
    state.debug = debug ? true : false
    if(!val.equals(stateVal)) {
        log.debug("UPDATED | debugOn: (${stateVal}) | Original State: (${val})")
        sendEvent(name: 'debugOn', value: stateVal, displayed: false)
    } else { Logger("debugOn: (${stateVal}) | Original State: (${val})") }
}

def lastUpdatedEvent() {
    def now = new Date()
    def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
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

def presenceEvent(presence) {
    def val = device.currentState("presence")?.value
    def pres = (presence == "home") ? "present" : "not present"
    def nestPres = !device.currentState("nestPresence") ? null : device.currentState("nestPresence")?.value.toString()
    def newNestPres = (presence == "home") ? "home" : ((presence == "auto-away") ? "auto-away" : "away")
    def statePres = state?.present
    state?.present = (pres == "present") ? true : false
    state?.nestPresence = newNestPres
    if(!val.equals(pres) || !nestPres.equals(newNestPres) || !nestPres) {
        log.debug("UPDATED | Presence: ${pres} | Original State: ${val} | State Variable: ${statePres}")
        sendEvent(name: 'nestPresence', value: newNestPres, descriptionText: "Nest Presence is: ${newNestPres}", displayed: true, isStateChange: true )
        sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: true, isStateChange: true )
    } else { Logger("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
}

def apiStatusEvent(issue) {
    def curStat = device.currentState("apiStatus")?.value
    def newStat = issue ? "issue" : "ok"
    state?.apiStatus = newStat
    if(!curStat.equals(newStat)) {
        log.debug("UPDATED | API Status is: (${newStat}) | Original State: (${curStat})")
        sendEvent(name: "apiStatus", value: newStat, descriptionText: "API Status is: ${newStat}", displayed: true, isStateChange: true, state: newStat)
    } else { Logger("API Status is: (${newStat}) | Original State: (${curStat})") }
}

def getNestPresence() {
    return !device.currentState("nestPresence") ? "home" : device.currentState("nestPresence")?.value.toString()
}

def getPresence() {
    return !device.currentState("presence") ? "present" : device.currentState("presence").value.toString()
}

/************************************************************************************************
|									NEST PRESENCE FUNCTIONS										|
*************************************************************************************************/
void setPresence() {
    try {
        log.trace "setPresence()..."
        def pres = getNestPresence()
        log.trace "Current Nest Presence: ${pres}"
        if(pres == "auto-away" || pres == "away") { setHome() }
        else if (pres == "home") { setAway() }
    }
    catch (ex) {
        log.error "setPresence Exception:", ex
        exceptionDataHandler(ex.message, "setPresence")
    }
}

def setAway() {
    try {
        log.trace "setAway()..."
        parent.setStructureAway(this, "true")
        presenceEvent("away")
    }
    catch (ex) {
        log.error "setAway Exception:", ex
        exceptionDataHandler(ex.message, "setAway")
    }
}

def setHome() {
    try {
        log.trace "setHome()..."
        parent.setStructureAway(this, "false")
        presenceEvent("home")
    }
    catch (ex) {
        log.error "setHome Exception:", ex
        exceptionDataHandler(ex.message, "setHome")
    }
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

def exceptionDataHandler(msg, methodName) {
    if(state?.allowDbException == false) {
        return
    } else {
        if(msg && methodName) {
            def msgString = "${msg}"
            parent?.sendChildExceptionData("presence", devVer(), msgString, methodName)
        }
    }
}

private def textDevName()   { return "Nest Presence${appDevName()}" }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
