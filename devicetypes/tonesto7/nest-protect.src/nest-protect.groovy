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

preferences {
       input (description: "Setting Operational Mode allows you to test different Nest Protects states. Once saved hit refresh in Device Handler",
           title: "Testing Mode", displayDuringSetup: false, type: "paragraph", element: "paragraph")
    input("testMode", "enum", title: "Testing State", required: false, 
            options: [
                "off":"off",
                "testSmoke":"Smoke Alert",
                "testCO": "CO Alert",
                "testWarnSmoke": "Smoke Warning",
                "testWarnCO": "CO Warning"
            ])
}

def devVer() { return "2.0.3" }

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
        attribute "NestcarbonMonoxide", "string"
        attribute "Nestsmoke", "string"
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
        standardTile("smoke", "device.Nestsmoke", width: 2, height: 2) {
            state("default", label:'unknown', icon: "st.unknown.unknown.unknown")
            state("ok", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_clear.png")
            state("warning", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_warn.png")
            state("emergency", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_emergency.png")
        }
        standardTile("carbonMonoxide", "device.NestcarbonMonoxide", width: 2, height: 2){
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
    if (state?.testMode) {
        log.warn "Test mode is active: nest alarm state data will not be received until it is turned off"
        switch (state?.testMode) {
            case "testSmoke" :
                alarmStateEvent("", "emergency")
                break
            case "testCO":
                alarmStateEvent("emergency", "")
                break
            case "testWarnSmoke" :
                alarmStateEvent("", "warning")
                break
            case "testWarnCO":
                alarmStateEvent("warning", "")
                   break
            default:
                state.testMode = false
                   log.warn "Test mode is inactive"
                break   
        }	
    } 
    poll()
}

def generateEvent(Map results) {	
    state?.testMode = !testMode ? null : testMode
    //Logger("Gen Event parsing data ${results}")
    Logger("-------------------------------------------------------------------", "warn")
    
    if(results) {	
        state?.useMilitaryTime = !parent?.settings?.useMilitaryTime ? false : true
        state.timeZone = !location?.timeZone ? parent?.getNestTimeZone() : null
        state?.showProtActEvts = !parent?.settings?.showProtActEvts ? true : false
        lastCheckinEvent(results?.last_connection)
        lastTestedEvent(results?.last_manual_test_time)
        apiStatusEvent(parent?.apiIssues())
        debugOnEvent(parent.settings?.childDebug)
        onlineStatusEvent(results?.is_online.toString())
        batteryStateEvent(results?.battery_health.toString())
        carbonStateEvent(results?.co_alarm_state.toString())
        smokeStateEvent(results?.smoke_alarm_state.toString())
        if (!state.testMode) { alarmStateEvent(results?.co_alarm_state.toString(), results?.smoke_alarm_state.toString()) }
        uiColorEvent(results?.ui_color_state.toString())
        testingStateEvent(results?.is_manual_test_active.toString())
        softwareVerEvent(results?.software_version.toString())
        deviceVerEvent()
        //getInfoHtml()
    }
    lastUpdatedEvent()
    return null
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
    def pubVer = parent?.latestProtVer().ver.toString()
    def dVer = devVer() ? devVer() : null
    def newData = (pubVer != dVer) ? "v${dVer}(New: v${pubVer})" : "${dVer}(Current)"
    state?.devTypeVer = newData
    if(curData != newData) {
        Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
        sendEvent(name: 'devTypeVer', value: newData, displayed: false)
    } else { Logger("Device Type Version is: (${newData}) | Original State: (${curData})") }
}

def lastCheckinEvent(checkin) {
    def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    tf.setTimeZone(getTimeZone())
    def lastConn = "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}"
    def lastChk = device.currentState("lastConnection")?.value
    state?.lastConnection = lastConn?.toString()
    if(!lastChk.equals(lastConn?.toString())) {
        Logger("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
        sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: state?.showProtActEvts, isStateChange: true)
    } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
}

def lastTestedEvent(dt) {
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

def softwareVerEvent(ver) {
    def verVal = device.currentState("softwareVer")?.value
    state?.softwareVer = ver
    if(!verVal.equals(ver)) {
        log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
        sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now v${ver}", displayed: false)
    } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
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

def apiStatusEvent(issue) {
    def apiStat = device.currentState("apiStatus")?.value
    def val = issue ? "Issue" : "Ok"
    state?.apiStatus = val
    if(!apiStat.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${apiStat})")
           sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${apiStat})") }
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

def uiColorEvent(color) {
    def colorVal = device.currentState("uiColor")?.value
    if(!colorVal.equals(color)) {
        log.debug("UI Color is: (${color}) | Original State: (${colorVal})")
        sendEvent(name:'uiColor', value: color.toString(), displayed: false, isStateChange: true) 
    } else { Logger("UI Color: (${color}) | Original State: (${colorVal})") }
}

def carbonStateEvent(carbon) {
    def carbonVal = device.currentState("NestcarbonMonoxide")?.value
    def stcarbonVal = device.currentState("carbonMonoxide")?.value
//values in st are tested  clear detected
// values from nest are ok warning emergency
    def stcarbonstatus = ""
    switch (carbon) {
       case "ok":
        stcarbonstatus = "clear"
        break
       case "warning":
        stcarbonstatus = "detected"
        break
       case "emergency":
        stcarbonstatus = "detected"
        break
       default:
        log.debug("Unknown Nest Carbon State is: ${carbon}")
        break
    }
    if(!carbonVal.equals(carbon)) {
        log.debug("Nest CO State is: (${carbon}) | Original State: (${carbonVal})")
        sendEvent(name:'NestcarbonMonoxide', value: carbon, descriptionText: "Nest CO State is: ${carbon}",  displayed: true, isStateChange: true) 
        sendEvent(name:'carbonMonoxide', value: stcarbonstatus, descriptionText: "CO State is: ${stcarbonstatus}",  displayed: true, isStateChange: true) 
    } else { Logger("CO State: (${carbon}) | Original State: (${carbonVal})") }
}

def onlineStatusEvent(online) {
    def isOn = device.currentState("onlineStatus")?.value
    def val = online ? "Online" : "Offline"
    state?.onlineStatus = val
    if(!isOn.equals(val)) { 
        log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
           sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: state?.showProtActEvts, isStateChange: true, state: val)
    } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
}

def batteryStateEvent(batt) {
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

def smokeStateEvent(smoke) {
    def smokeVal = device.currentState("Nestsmoke")?.value
    def stsmokeVal = device.currentState("smoke")?.value
// st values are detected clear tested
// nest values are ok warning emergency
    def stsmokestatus = ""
    switch (smoke) {
       case "ok":
        stsmokestatus = "clear"
        break
       case "warning":
        stsmokestatus = "detected"
        break
       case "emergency":
        stsmokestatus = "detected"
        break
       default:
        log.debug("Unknown Nest Smoke State is: ${smoke}")
        break
    }
    if(!smokeVal.equals(smoke)) {
    log.debug("Nest Smoke State is: (${smoke.toString().toUpperCase()}) | Original State: (${smokeVal.toString().toUpperCase()})")
    sendEvent(name:'Nestsmoke', value: smoke,  descriptionText: "Nest Smoke State is: ${smoke.toString().toUpperCase()}", displayed: true, isStateChange: true)
    sendEvent(name:'smoke', value: stsmokestatus,  descriptionText: "Smoke State is: ${stsmokestatus}", displayed: true, isStateChange: true)
    } else { Logger("Smoke State: (${smoke.toString().toUpperCase()}) | Original State: (${smokeVal.toString().toUpperCase()})") }
}

def testingStateEvent(test) {
    def testVal = device.currentState("isTesting")?.value
    if(!testVal.equals(test)) {
        log.debug("Testing State: (${test}) | Original State: (${testVal})")
        //Not displaying the results of this, not sure if it is truly needed
         sendEvent(name:'isTesting', value: test, descriptionText: "Manual test: ${test}", displayed: true, isStateChange: true) 
     } else { Logger("Testing State: (${test}) | Original State: (${testVal})") }
}

 def alarmStateEvent(coState, smokeState) {
    def testVal = device.currentState("isTesting")?.value
    def alarmState = ""

    def stvalStr = "detected"
    if (state?.testMode || testVal) { stvalStr = "tested"  }
 
    if ( smokeState == "emergency" ) {
        alarmState = "smoke-emergency"
        sendEvent( name: 'Nestsmoke', value: smokeState, descriptionText: "Nest Smoke Alarm: ${smokeState}", type: "physical", displayed: true, isStateChange: true )      
        sendEvent( name: 'smoke', value: stvalStr, descriptionText: "Smoke Alarm: ${smokeState}", type: "physical", displayed: true, isStateChange: true )      
    } else if (coState == "emergency" ) {
        alarmState = "co-emergency"
        sendEvent( name: 'NestcarbonMonoxide', value: coState, descriptionText: "Nest CO Alarm: ${coState}", type: "physical", displayed: true, isStateChange: true ) 
        sendEvent( name: 'carbonMonoxide', value: stvalStr, descriptionText: "CO Alarm: ${coState}", type: "physical", displayed: true, isStateChange: true ) 
    } else if (smokeState == "warning" ) {
        alarmState = "smoke-warning"
        sendEvent( name: 'Nestsmoke', value: smokeState, descriptionText: "Nest Smoke Alarm: ${smokeState}", type: "physical", displayed: true, isStateChange: true )      
        sendEvent( name: 'smoke', value: stvalStr, descriptionText: "Smoke Alarm: ${smokeState}", type: "physical", displayed: true, isStateChange: true )    
    } else if (coState == "warning" ) {
        alarmState = "co-warning"
        sendEvent( name: 'NestcarbonMonoxide', value: coState, descriptionText: "Nest CO Alarm: ${coState}", type: "physical", displayed: true, isStateChange: true ) 
        sendEvent( name: 'carbonMonoxide', value: stvalStr, descriptionText: "CO Alarm: ${coState}", type: "physical", displayed: true, isStateChange: true ) 
    } else {
        alarmState = "ok"
    } 
    
     log.info "alarmState: ${alarmState} (Nest Smoke: ${smokeState.toString().capitalize()} | Nest CarbonMonoxide: ${coState.toString().capitalize()})"
     sendEvent( name: 'alarmState', value: alarmState, descriptionText: "Alarm: ${alarmState} (Smoke/CO: ${smokeState}/${coState}) ( ${stvalStr} )", type: "physical", displayed: state?.showProtActEvts, isStateChange: true )
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
// Print log message from parent
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
    return null
}

def getCarbonImg() {
    def carbonVal = device.currentState("NestcarbonMonoxide")?.value
    switch(carbonVal) {
        case "warn":
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

def getSmokeImg() {
    def smokeVal = device.currentState("Nestsmoke")?.value
    switch(smokeVal) {
        case "warn":
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
        log.error "getImageBytes Exception: $ex"
    }
}
def getTestImg(imgName) { return imgName ? "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/Test/$imgName" : "" }
def getImg(imgName) { return imgName ? "https://cdn.rawgit.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : "" }

def getInfoHtml() { 
    def battImg = (state?.battVal == "low") ? "<img class='battImg' src=\"${getImgBase64(getImg("battery_low_h.png"), "png")}\">" : 
            "<img class='battImg' src=\"${getImgBase64(getImg("battery_ok_h.png"), "png")}\">"
    def coImg = "<td><img class='alarmImg' src=\"${getCarbonImg()}\"></td>"
    def smokeImg = "<td><img class='alarmImg' src=\"${getSmokeImg()}\"></td>"
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
                  padding: 4px;
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
                }
                
                .img-table {
                  width: 100%;
                  height: 100%;
                }
                
                .img-table tr {
                  text-align: center;
                }
                .h40 {
                  width: 39.99%;
                  font-weight: bold;
                  font-size: 3.2vmax;
                }
                .h20 {
                  width: 19.99%;
                  font-weight: bold;
                  font-size: 3.4vmax;
                }
                .r40 {
                  width: 39.99%;
                  font-size: 3.6vmax;
                }
                .r20 {
                  width: 19.99%;
                  font-size: 3.6vmax;
                }
                .rowLong {
                  font-size: 3.2vmax;
                }
                .datetime {
                  font-size: 3.4vmax;
                }
                .battImg {
                  width:30px; height:15px;
                }
                .alarmImg {
                  vertical-align: top;
                  width:60px; height:60px;
                }
            </style>
               """
        }
        body {
            """
            <table class="img-table">
              <tbody>
                <tr>
                  $coImg
                  $smokeImg
                </tr>
              </tbody>
            </table>
            <table class="flat-table">
              <thead>
                <th class="h40">Network Status</th>
                <th class="h20">Battery</th>
                <th class="h40">API Status</th>
              </thead>
                <tbody>
                  <tr>
                    <td class="r40">${state?.onlineStatus.toString()}</td>
                    <td class="r20">${battImg}</td>
                    <td class="r40">${state?.apiStatus}</td>
                  </tr>
                  <tr>
                    <th class="h40" width=33.33%>Firmware Version</th>
                    <th class="h20">Debug</th>
                    <th class="h40">Device Type</th>
                  </tr>
                  <td class="r40">v${state?.softwareVer.toString()}</td>
                  <td class="r20">${state?.debugStatus}</td>
                  <td class="rowLong">${state?.devTypeVer.toString()}</td>
                </tbody>
             </table>
              <table class="flat-table">
               <thead>
                 <th class="h40">Nest Last Checked-In</th>
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

private def textDevName()   { "Nest Protect${appDevName()}" }
private def appDevType()    { false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
