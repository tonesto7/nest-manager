/**
 *  Nest Location Watchdog
 *
 *  Copyright 2016 Anthony S.
 *
 */

import groovy.json.*
import groovy.time.*
import java.text.SimpleDateFormat

definition(
    name: "${textAppName()}",
    namespace: "${textNamespace()}",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    singleInstance: true)

def appVersion() { "0.0.1" }
def appVerDate() { "7-28-2016" }
def appVerInfo() {
    def str = ""

    str += "V0.0.1 (July 28th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Merged in Eric's latest patches."

    return str
}

preferences {
    page(name: "startPage")
}

def startPage() {
    return dynamicPage(name: "startPage", title: "Main Page", nextPage: "", install: false, uninstall: false) {
        if(disableAutomation) {
            section("Title") {
                paragraph "This Automation is currently disabled!!!\nTurn it back on to resume operation...", image: getAppImg("instruct_icon.png")
            }
        }
        else {
            section("Enable/Disable this Automation") {
                input "disableAutomation", "bool", title: "Disable this Automation?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("switch_off_icon.png")
                if(!atomicState?.disableAutomation && disableAutomation) {
                    LogAction("This Automation was Disabled at (${getDtNow()})", "info", true)
                    atomicState.disableAutomationDt = getDtNow()
                } else if (atomicState?.disableAutomation && !disableAutomation) {
                    LogAction("This Automation was Restored at (${getDtNow()})", "info", true)
                    atomicState.disableAutomationDt = null
                }
            }
            section("Debug Options") {
                input (name: "showDebug", type: "bool", title: "Show App Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("log.png"))
                atomicState.showDebug = showDebug
            }
        }
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def uninstalled() {
    log.debug "uninstalled..."
}

def updated() {
    log.debug "Updated with settings: ${settings}"

    if(!atomicState?.automationType) { atomicState.automationType = "watchDog" }
    if(!atomicState?.disableAutomation) { atomicState.disableAutomation = false }

    unsubscribe()
    unschedule()
    initialize()
}

def initialize() {

    def tstats = parent.getTstats()
    def foundtstats

    if(tstats) {
        foundtstats = tstats.collect { dni ->
            def d1 = parent.getThermostatDevice(dni)
            if(d1) {
                LogAction("Found: ${d1?.displayName} with (Id: ${dni?.key})", "debug", true)

// temperature is for DEBUG
       subscribe(d1, "temperature", safetyEvt)

                subscribe(d1, "safetyTempExceeded", safetyEvt)
            }
            return d1
        }
    }
}

def safetyEvt(evt) {
    LogAction("Safety Temp Exceeded Event | Thermostat Temp: ${evt?.displayName} (${evt?.value})", "trace", true)
    if(disableAutomation) { return }
}


/************************************************************************************************
|                                                                       LOGGING AND Diagnostic                                                                          |
*************************************************************************************************/
def LogTrace(msg) {
    def trOn = advAppDebug ? true : false
    if(trOn) { Logger(msg, "trace") }
}

def LogAction(msg, type = "debug", showAlways = false) {
    try {
        def isDbg = parent ? ((atomicState?.showDebug || showDebug)  ? true : false) : (appDebug ? true : false)
        if(showAlways) { Logger(msg, type) }

        else if (isDbg && !showAlways) { Logger(msg, type) }
    } catch (ex) {
        log.error("LogAction Exception: ${ex}")
        sendExceptionData(ex, "LogAction")
    }
}

def Logger(msg, type) {
    if(msg && type) {
        def labelstr = ""
def debugAppendAppName = true
        if (debugAppendAppName) { labelstr = "${app.label} | " }
        switch(type) {
            case "debug":
                log.debug "${labelstr}${msg}"
                break
            case "info":
                log.info "${labelstr}${msg}"
                break
            case "trace":
                log.trace "${labelstr}${msg}"
                break
            case "error":
                log.error "${labelstr}${msg}"
                break
            case "warn":
                log.warn "${labelstr}${msg}"
                break
            default:
                log.debug "${labelstr}${msg}"
                break
        }
    }
    else { log.error "Logger Error - type: ${type} | msg: ${msg}" }
}


def sendExceptionData(exMsg, methodName) {
    try {
        def exCnt = 0
        exCnt = atomicState?.appExceptionCnt ? atomicState?.appExceptionCnt + 1 : 1
        atomicState?.appExceptionCnt = exCnt ?: 1
        if (optInSendExceptions) {
            def appType = parent ? "automationApp" : "managerApp"
            def exData = ["methodName":methodName, "appVersion":(appVersion() ?: "Not Available"),"errorMsg":exMsg.toString(), "errorDt":getDtNow().toString()]
            def results = new groovy.json.JsonOutput().toJson(exData)
            sendFirebaseExceptionData(results, "errorData/${appType}/${methodName}.json")
        }
    } catch (ex) {
        LogAction("sendExceptionData Exception: ${ex}", "error", true)
    }
}

def sendFirebaseExceptionData(data, pathVal) {
    //log.trace "sendExceptionData(${data}, ${pathVal}"
    def json = new groovy.json.JsonOutput().prettyPrint(data)
    def result = false
    def params = [ uri: "${getFirebaseAppUrl()}/${pathVal}", body: json.toString() ]
    try {
        httpPostJson(params) { resp ->
            //log.debug "resp: ${resp}"
            if( resp?.status == 200) {
                LogAction("sendFirebaseExceptionData: Exception Data Sent Successfully!!!", "info", true)
                atomicState?.lastSentExceptionDataDt = getDtNow()
                result = true
            }
            else if(resp?.status == 400) {
                LogAction("sendFirebaseExceptionData: 'Bad Request' Exception: ${resp?.status}", "error", true)
            }
            else {
                LogAction("sendFirebaseExceptionData: 'Unexpected' Response: ${resp?.status}", "warn", true)
            }
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            LogAction("sendFirebaseExceptionData: 'HttpResponseException' Exception: ${ex}", "error", true)
        }
        else { LogAction("sendFirebaseExceptionData: Exception: ${ex}", "error", true) }
    }
    return result
}


/******************************************************************************
*                                       Keep These Methods                                                *
*******************************************************************************/
//def getFirebaseAppUrl()         { return "https://st-nest-manager.firebaseio.com" }
def getFirebaseAppUrl()         { return "${parent.getFirebaseAppUrl()}" }
//def getAppImg(imgName, on = null)       { return "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/App/$imgName" }
def getAppImg(imgName)       { return "${parent.getAppImg($imgName)}" }


//Returns app State Info
def getStateSize()      { return state?.toString().length() }
def getStateSizePerc()  { return (int) ((stateSize/100000)*100).toDouble().round(0) }


def getAutomationType() {
    return atomicState?.automationType ? atomicState?.automationType : "watchDog"
}

def getIsAutomationDisabled() {
    return disableAutomation ? true : false
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

def getTimeZone() {
    def tz = null
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = TimeZone.getTimeZone(parent.getNestTimeZone()) }
    if(!tz) { LogAction("getTimeZone: Hub or Nest TimeZone is not found ...", "warn", true) }
    return tz
}

def formatDt(dt) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
    else {
        LogAction("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...", "warn", true)
    }
    return tf.format(dt)
}


///////////////////////////////////////////////////////////////////////////////
/******************************************************************************
*                Application Help and License Info Variables                  *
*******************************************************************************/
///////////////////////////////////////////////////////////////////////////////
private def appName()           { return "Nest Location Watchdog${appDevName()}" }
private def appAuthor()         { return "Anthony S." }
private def appNamespace()      { return "tonesto7" }
private def gitBranch()     { return "master" }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
private def appInfoDesc()       {
    def cur = atomicState?.appData?.updater?.versions?.app?.ver.toString()
    def ver = (isAppUpdateAvail()) ? "${textVersion()} (Lastest: v${cur})" : textVersion()
    return "${textAppName()}\n• ${ver}\n• ${textModified()}"
}
private def textAppName()   { return "${appName()}" }
private def textVersion()   { return "Version: ${appVersion()}" }
private def textModified()  { return "Updated: ${appVerDate()}" }
private def textAuthor()    { return "${appAuthor()}" }
private def textNamespace() { return "${appNamespace()}" }
private def textVerInfo()   { return "${appVerInfo()}" }
private def textDonateLink(){ return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2CJEVN439EAWS" }
private def stIdeLink()     { return "https://graph.api.smartthings.com" }
private def textCopyright() { return "Copyright© 2016 - Anthony S." }
private def textDesc()      { return "This SmartApp is used to integrate you're Nest devices with SmartThings - Watches the global safety values and alerts you if they go over the threshold." }
private def textHelp()      { return "" }
private def textLicense() {
    return "Licensed under the Apache License, Version 2.0 (the 'License'); "+
        "you may not use this file except in compliance with the License. "+
        "You may obtain a copy of the License at"+
        "\n\n"+
        "    http://www.apache.org/licenses/LICENSE-2.0"+
        "\n\n"+
        "Unless required by applicable law or agreed to in writing, software "+
        "distributed under the License is distributed on an 'AS IS' BASIS, "+
        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. "+
        "See the License for the specific language governing permissions and "+
        "limitations under the License."
}
