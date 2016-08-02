/**
 *  Nest Protect
 *	Authors: Anthony S. (@tonesto7), Ben W. (@desertblade), Eric S. (@E_Sch)
 *
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

preferences { }

def devVer() { return "2.6.0" }

metadata {
    definition (name: "${textDevName()}", author: "Anthony S.", namespace: "tonesto7") {
        //capability "Polling"
        capability "Sensor"
        capability "Battery"
        capability "Smoke Detector"
        capability "Carbon Monoxide Detector"
        capability "Refresh"
        
        command "refresh"
        command "poll"
        command "log", ["string","string"]
        command "runsmoketest"
        command "runcotest"
        command "runbatterytest"
        
        attribute "alarmState", "string"
        attribute "batteryState", "string"
        attribute "battery", "string"
        attribute "uiColor", "string"
        attribute "softwareVer", "string"
        attribute "lastConnection", "string"
        attribute "lastUpdateDt", "string"
        attribute "lastTested", "string"
        attribute "isTesting", "string"
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
        attribute "devTypeVer", "string"
        attribute "onlineStatus", "string"
        attribute "carbonMonoxide", "string"
        attribute "smoke", "string"
        attribute "nestCarbonMonoxide", "string"
        attribute "nestSmoke", "string"
    }
    
    simulator {
        // TODO: define status and reply messages here
    }
            
    tiles(scale: 2) {
        multiAttributeTile(name:"alarmState", type:"generic", width:6, height:4) {
            tileAttribute("device.alarmState", key: "PRIMARY_CONTROL") {
                attributeState("default", label:'--', icon: "st.unknown.unknown.unknown")
                attributeState("ok", label:"clear", icon:"st.alarm.smoke.clear", backgroundColor:"#44B621")
                attributeState("smoke-warning", label:"SMOKE!\nWARNING", icon:"st.alarm.smoke.smoke", backgroundColor:"#e8d813")
                attributeState("smoke-emergency", label:"SMOKE!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
                attributeState("co-warning", label:"CO!\nWARNING!", icon:"st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor:"#e8d813")
                attributeState("co-emergency", label:"CO!", icon:"st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor:"#e86d13")
            }
            tileAttribute("device.batteryState", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'unknown', icon: "st.unknown.unknown.unknown")
                attributeState("ok", label: "Battery: OK", backgroundColor: "#44B621", 
                    icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/battery_ok_v.png")
                attributeState("replace", label: "Battery: REPLACE!", backgroundColor: "#e86d13", 
                    icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/battery_low_v.png")
            }
        }
        standardTile("main2", "device.alarmState", width: 2, height: 2) {
            state("default", label:'--', icon: "st.unknown.unknown.unknown")
            state("ok", label:"clear", backgroundColor:"#44B621",
                icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/alarm_clear.png")
            state("smoke-warning", label:"SMOKE!\nWARNING", backgroundColor:"#e8d813",
                icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_warn.png")
            state("smoke-emergency", label:"SMOKE!", backgroundColor:"#e8d813",
                icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_emergency.png")
            state("co-warning", label:"CO!\nWARNING!", backgroundColor:"#e86d13",
                icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_warn.png")
            state("co-emergency", label:"CO!", backgroundColor:"#e86d13",
                icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_emergency.png")
        }
        standardTile("smoke", "device.nestSmoke", width: 2, height: 2) {
            state("default", label:'unknown', icon: "st.unknown.unknown.unknown")
            state("ok", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_clear.png")
            state("warning", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_warn.png")
            state("emergency", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_emergency.png")
        }
        standardTile("carbonMonoxide", "device.nestCarbonMonoxide", width: 2, height: 2){
            state("default", label:'unknown', icon: "st.unknown.unknown.unknown")
            state("ok", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_clear.png")
            state("warning", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_warn.png")
            state("emergency", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_emergency.png")
        }
         standardTile("batteryState", "device.batteryState", width: 2, height: 2){
            state("default", label:'unknown')
            state("ok", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/battery_ok.png")
            state("replace", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/battery_low.png")
        }
        standardTile("filler", "device.filler", width: 2, height: 2){
            state("default", label:'')
        }
        valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
            state("default", label: 'Network Status:\n${currentValue}')
        }
        valueTile("uiColor", "device.uiColor", inactiveLabel: false, width: 2, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'UI Color:\n${currentValue}')
        }
        valueTile("softwareVer", "device.softwareVer", inactiveLabel: false, width: 2, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Firmware:\nv${currentValue}')
        }
        valueTile("lastConnection", "device.lastConnection", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Protect Last Checked-In:\n${currentValue}')
        }
        valueTile("lastTested", "device.lastTested", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Last Manual Test:\n${currentValue}')
        }
        standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
            state "default", label: 'refresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon"
        }
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Data Last Received:\n${currentValue}')
        }
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
            state("default", label: 'Device Type:\nv${currentValue}')
        }
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, decoration: "flat", wordWrap: true) {
            state "ok", label: "API Status:\nOK"
            state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
        }
        valueTile("debugOn", "device.debugOn", width: 2, height: 1, decoration: "flat") {
            state "true", 	label: 'Debug:\n${currentValue}'
            state "false", 	label: 'Debug:\n${currentValue}'
        }
        htmlTile(name:"devInfoHtml", action: "getInfoHtml", width: 6, height: 5)
        
    main "main2"
    details(["alarmState", "devInfoHtml", "refresh"])
    //details(["alarmState", "filler", "batteryState", "filler", "devInfoHtml", "refresh"])
   }
}

mappings {
    path("/getInfoHtml") {action: [GET: "getInfoHtml"]}
}

def initialize() {
    log.info "Nest Protect ${textVersion()} ${textCopyright()}"
    poll()
}

def parse(String description) {
    log.debug "Parsing '${description}'"
}

def poll() {
    log.debug "polling parent..."
    parent.refresh(this)
}

def refresh() {
    log.debug "refreshing parent..."
    poll()
}


//ERS
def runsmoketest() {
    log.trace("runsmoketest()")
//values from nest are ok, warning, emergency
    testingStateEvent("true")
    carbonSmokeStateEvent("ok", "emergency")
    schedEndTest()
}

def runcotest() {
    log.trace("runcotest()")
//values from nest are ok, warning, emergency
    testingStateEvent("true")
    carbonSmokeStateEvent("emergency", "ok")
    schedEndTest()
}

def runbatterytest() {
    log.trace("runbatterytest()")
//values from nest are ok, replace
    testingStateEvent("true")
    batteryStateEvent("replace")
    schedEndTest()
}

def schedEndTest() {
    runIn(5, "endTest", [overwrite: true])
    refresh()  // this typically takes more than 5 seconds to complete
}

def endTest() {
    carbonSmokeStateEvent("ok", "ok")
    batteryStateEvent("ok")
    testingStateEvent("false")
    refresh()
}



def generateEvent(Map eventData) {
    //log.trace("generateEvent parsing data ${eventData}")
    try {
        Logger("------------START OF API RESULTS DATA------------", "warn")
        if(eventData) {
            def results = eventData?.data
            state?.useMilitaryTime = eventData?.mt ? true : false
            state.nestTimeZone = !location?.timeZone ? eventData?.tz : null
            state?.showProtActEvts = eventData?.showProtActEvts ? true : false
            carbonSmokeStateEvent(results?.co_alarm_state.toString(),results?.smoke_alarm_state.toString())
            if(!results?.last_connection) { lastCheckinEvent(null) } 
            else { lastCheckinEvent(results?.last_connection) }
            lastTestedEvent(results?.last_manual_test_time)
            apiStatusEvent(eventData?.apiIssues)
            debugOnEvent(eventData?.debug ? true : false)
            onlineStatusEvent(results?.is_online.toString())
            batteryStateEvent(results?.battery_health.toString())
            testingStateEvent(results?.is_manual_test_active.toString())
            uiColorEvent(results?.ui_color_state.toString())
            softwareVerEvent(results?.software_version.toString())
            deviceVerEvent(eventData?.latestVer.toString())
            state?.cssUrl = eventData?.cssUrl
        }
        lastUpdatedEvent()
        //This will return all of the devices state data to the logs.
        //log.debug "Device State Data: ${getState()}"
        return null
    } 
    catch (ex) {
        log.error "generateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "generateEvent")
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
        sendChildExceptionData("protect", devVer(), ex?.toString(), "isCodeUpdateAvailable")
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
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "deviceVerEvent")
    }
}

def lastCheckinEvent(checkin) {
    try {
        def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat(formatVal)
        tf.setTimeZone(getTimeZone())
        def lastConn = checkin ? "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}" : "Not Available"
        def lastChk = device.currentState("lastConnection")?.value
        state?.lastConnection = lastConn?.toString()
        if(!lastChk.equals(lastConn?.toString())) {
            Logger("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
            sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: state?.showProtActEvts, isStateChange: true)
        } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
    } 
    catch (ex) {
        log.error "lastCheckinEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "lastCheckinEvent")
    }
}

def lastTestedEvent(dt) {
    try {
        def lastTstVal = device.currentState("lastTested")?.value
        def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat(formatVal)
        tf.setTimeZone(getTimeZone())
        def lastTest = !dt ? "No Test Recorded" : "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dt))}"
        state?.lastTested = lastTest
        if(!lastTstVal.equals(lastTest?.toString())) {
            Logger("UPDATED | Last Manual Test was: (${lastTest}) | Original State: (${lastTstVal})")
            sendEvent(name: 'lastTested', value: lastTest, displayed: true, isStateChange: true)
        } else { Logger("Last Manual Test was: (${lastTest}) | Original State: (${lastTstVal})") }
    } 
    catch (ex) {
        log.error "lastTestedEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "lastTestedEvent")
    }
}

def softwareVerEvent(ver) {
    try {
        def verVal = device.currentState("softwareVer")?.value
        state?.softwareVer = ver
        if(!verVal.equals(ver)) {
            log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
            sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now v${ver}", displayed: false)
        } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
    } 
    catch (ex) {
        log.error "softwareVerEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "softwareVerEvent")
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
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "debugOnEvent")
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
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "apiStatusEvent")
    }
}

def lastUpdatedEvent() {
    try {
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
    catch (ex) {
        log.error "lastUpdatedEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "lastUpdatedEvent")
    }
}

def uiColorEvent(color) {
    try {
        def colorVal = device.currentState("uiColor")?.value
        if(!colorVal.equals(color)) {
            log.debug("UI Color is: (${color}) | Original State: (${colorVal})")
            sendEvent(name:'uiColor', value: color.toString(), displayed: false, isStateChange: true) 
        } else { Logger("UI Color: (${color}) | Original State: (${colorVal})") }
    } 
    catch (ex) {
        log.error "uiColorEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "uiColorEvent")
    }
}

def onlineStatusEvent(online) {
    try {
        def isOn = device.currentState("onlineStatus")?.value
        def val = online ? "Online" : "Offline"
        state?.onlineStatus = val
        if(!isOn.equals(val)) { 
            log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
            sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: state?.showProtActEvts, isStateChange: true, state: val)
        } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
    } 
    catch (ex) {
        log.error "onlineStatusEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "onlineStatusEvent")
    }
}

def batteryStateEvent(batt) {
    try {
        def stbattery = (batt == "replace") ? 5 : 100
        def battVal = device.currentState("batteryState")?.value
        def stbattVal = device.currentState("battery")?.value
        state?.battVal = batt
        if(!battVal.equals(batt) || !stbattVal) {
            log.debug("Battery is: ${batt} | Original State: (${battVal})")
            sendEvent(name:'batteryState', value: batt, descriptionText: "Nest Battery status is: ${batt}", displayed: true, isStateChange: true)
            sendEvent(name:'battery', value: stbattery, descriptionText: "Battery is: ${stbattery}", displayed: true, isStateChange: true)
        } else { Logger("Battery State: (${batt}) | Original State: (${battVal})") }
    } 
    catch (ex) {
        log.error "batteryStateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "batteryStateEvent")
    }
}

def testingStateEvent(test) {
    try {
        def testVal = device.currentState("isTesting")?.value
        if(!testVal.equals(test)) {
            log.debug("Testing State: (${test}) | Original State: (${testVal})")
            //Not displaying the results of this, not sure if it is truly needed
            sendEvent(name:'isTesting', value: test, descriptionText: "Manual test: ${test}", displayed: true, isStateChange: true) 
        } else { Logger("Testing State: (${test}) | Original State: (${testVal})") }
    } 
    catch (ex) {
        log.error "testingStateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "testingStateEvent")
    }
}

 def carbonSmokeStateEvent(coState, smokeState) {
        //values in ST are tested, clear, detected
        //values from nest are ok, warning, emergency
    try {
        def carbonVal = device.currentState("nestCarbonMonoxide")?.value
        def smokeVal = device.currentState("nestSmoke")?.value
        def testVal = device.currentState("isTesting")?.value 

        def alarmStateST = "ok"
        def smokeValStr = "clear"
        def carbonValStr = "clear"
           
        if (smokeState == "emergency" || smokeState == "warning") {
            alarmStateST = smokeState == "emergency" ? "smoke-emergency" : "smoke-warning"
            smokeValStr = "detected"
        } 
        if (coState == "emergency" || coState == "warning") {
            alarmStateST = coState == "emergency" ? "co-emergency" : "co-warning"
            carbonValStr = "detected"
        } 
        if(!smokeVal.equals(smokeState)) {
            log.debug("Nest Smoke State is: (${smokeState.toString().toUpperCase()}) | Original State: (${smokeVal.toString().toUpperCase()})")
            sendEvent( name: 'nestSmoke', value: smokeState, descriptionText: "Nest Smoke Alarm: ${smokeState}", type: "physical", displayed: true, isStateChange: true )      
            sendEvent( name: 'smoke', value: smokeValStr, descriptionText: "Smoke Alarm: ${smokeState} Testing: ${testVal}", type: "physical", displayed: true, isStateChange: true )
        } else { Logger("Smoke State: (${smokeState.toString().toUpperCase()}) | Original State: (${smokeVal.toString().toUpperCase()})") }
        if(!carbonVal.equals(coState)) {
            sendEvent( name: 'nestCarbonMonoxide', value: coState, descriptionText: "Nest CO Alarm: ${coState}", type: "physical", displayed: true, isStateChange: true ) 
            sendEvent( name: 'carbonMonoxide', value: carbonValStr, descriptionText: "CO Alarm: ${coState} Testing: ${testVal}", type: "physical", displayed: true, isStateChange: true )
        } else { Logger("CO State: (${coState.toString().toUpperCase()}) | Original State: (${carbonVal.toString().toUpperCase()})") }

        log.info "alarmState: ${alarmStateST} (Nest Smoke: ${smokeState.toString().capitalize()} | Nest CarbonMonoxide: ${coState.toString().capitalize()})"
        sendEvent( name: 'alarmState', value: alarmStateST, descriptionText: "Alarm: ${alarmStateST} (Smoke/CO: ${smokeState}/${coState}) ( ${stvalStr} )", type: "physical", displayed: state?.showProtActEvts )
    } 
    catch (ex) {
        log.error "carbonSmokeStateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "carbonSmokeStateEvent")
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
// Print log message from parent
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
    return null
}

def getCarbonImg() {
    try {
        def carbonVal = device.currentState("nestCarbonMonoxide")?.value
        //values in ST are tested, clear, detected
        //values from nest are ok, warning, emergency
        switch(carbonVal) {
            case "warning":
                return getImgBase64(getImg("co_warn_tile.png"), "png")
                break
            case "emergency":
                return getImgBase64(getImg("co_emergency_tile.png"), "png")
                break
            default:
                return getImgBase64(getImg("co_clear_tile.png"), "png")
                break
        }
    } 
    catch (ex) {
        log.error "getCarbonImg Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "getCarbonImg")
    }
}

def getSmokeImg() {
    try {
        def smokeVal = device.currentState("nestSmoke")?.value
        //values in ST are tested, clear, detected
        //values from nest are ok, warning, emergency
        switch(smokeVal) {
            case "warning":
                return getImgBase64(getImg("smoke_warn_tile.png"), "png")
                break
            case "emergency":
                return getImgBase64(getImg("smoke_emergency_tile.png"), "png")
                break
            default:
                return getImgBase64(getImg("smoke_clear_tile.png"), "png")
                break
        }
    } 
    catch (ex) {
        log.error "getSmokeImg Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "getSmokeImg")
    }
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
                int size = 2048
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
        log.error "getImgBase64 Exception: $ex"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "getImgBase64")
    }
}

def getTestImg(imgName) { return imgName ? "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/Test/$imgName" : "" }
def getImg(imgName) { 
    try {
        return imgName ? "https://cdn.rawgit.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : "" 
    }
    catch (ex) {
        log.error "getImg Exception: ${ex}"
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "getImg")
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
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "getCSS")
    }
}

def getInfoHtml() {
    try {
        def battImg = (state?.battVal == "low") ? "<img class='battImg' src=\"${getImgBase64(getImg("battery_low_h.png"), "png")}\">" : 
                "<img class='battImg' src=\"${getImgBase64(getImg("battery_ok_h.png"), "png")}\">"
        def coImg = "<img class='alarmImg' src=\"${getCarbonImg()}\">"
        def smokeImg = "<img class='alarmImg' src=\"${getSmokeImg()}\">"
        def testVal = device.currentState("isTesting")?.value 
        def testModeHTML = (testVal.toString() == "true") ? "<h3>Test Mode</h3>" : ""
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
                <meta name="viewport" content="width = device-width, user-scalable=no, initial-scale=1.0">
            </head>
            <body>
                <style type="text/css">
                    ${getCSS()}
                </style>
                ${updateAvail}
                ${testModeHTML}
                    <div class="row">
                        <div class="offset-by-two four columns centerText">
                            $coImg
                        </div>
                        <div class="four columns centerText">
                            $smokeImg
                        </div>
                        </div>
                <table>
                <col width="50%">
                <col width="50%">
                <thead>
                    <th>Network Status</th>
                    <th>API Status</th>
                </thead>
                    <tbody>
                    <tr>
                        <td>${state?.onlineStatus.toString()}</td>
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
                    <td>v${state?.softwareVer.toString()}</td>
                    <td>${state?.debugStatus}</td>
                    <td>${state?.devTypeVer.toString()}</td>
                </table>
                <table>
                <thead>
                    <th>Nest Last Checked-In</th>
                    <th>Data Last Received</th>
                </thead>
                <tbody>
                    <tr>
                    <td class="dateTimeText">${state?.lastConnection.toString()}</td>
                    <td class="dateTimeText">${state?.lastUpdatedDt.toString()}</td>
                    </tr>
                </tbody>
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
        parent?.sendChildExceptionData("protect", devVer(), ex.toString(), "getInfoHtml")
    }
}

private def textDevName()   { return "Nest Protect${appDevName()}" }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
