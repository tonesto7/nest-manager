/********************************************************************************************
|    Application Name: Nest Manager                                                         |
|    Author: Anthony S. (@tonesto7),                                                        |
|    Contributors: Ben W. (@desertblade) | Eric S. (@E_sch)                                 |
|                                                                                           |
|    Initial code was loosely based off of the SmartThings Ecobee App                       |
|*******************************************************************************************|
|    There maybe portions of the code that may resemble code from other apps in the         |
|    community. I may have used some of it as a point of reference.                         |
|    Thanks go out to those Authors!!!                                                      |
|                                                                                           |
|    I apologize if i've missed anyone.  Please let me know and I will add your credits     |
|                                                                                           |
|    ### I really hope that we don't have a ton or forks being released to the community,   |
|    ### I hope that we can collaborate and make app and device type that will accomodate   |
|    ### every use case                                                                     |
*********************************************************************************************/

import groovy.json.*
import groovy.time.*
import java.text.SimpleDateFormat

definition(
    name: "${textAppName()}",
    namespace: "${textNamespace()}",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager%402x.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager%403x.png",
    singleInstance: true,
    oauth: true )

{
    appSetting "clientId"
    appSetting "clientSecret"
}

def appVersion() { "2.0.9" }
def appVerDate() { "5-15-2016" }
def appVerInfo() {
    
    "V2.0.8 (May 13th, 2016)\n" +
    "Updated Certain Inputs to turn blue when there settings have been configured.\n\n" +
    
    "V2.0.7 (May 3rd, 2016)\n" +
    "Fixed UI to work with the new mobile app design.\n\n" +
    
    "V2.0.6 (May 2nd, 2016)\n" +
    "Added: Showing what types of automations are installed now\n\n" +

    "V2.0.4 (Apr 28th, 2016)\n" +
    "Fixed: Very minor bug fixes\n\n" +
    
    "V2.0.3 (Apr 27th, 2016)\n" +
    "Fixed: Bug found when unselecting a location nothing would be found again.\n" +
    "Updated: Changed the way that data was sent to presence device\n"+
    "Added: Support for Custom Child Notifications...\n\n" +

    "V2.0.1 (Apr 22nd, 2016)\n" +
    "Fixed: Everything\n\n" +

    "V2.0.0 (Apr 21th, 2016)\n" +
    "Fixed: Everything\n\n" +
    "---------------------------------------------"
}

preferences {
    page(name: "authPage", title: "Nest", nextPage:"mainPage", content:"authPage", uninstall: true, install:true)
    page(name: "mainPage" )
    page(name: "prefsPage")
    page(name: "infoPage")
    page(name: "nestInfoPage")
    page(name: "structInfoPage")
    page(name: "tstatInfoPage")
    page(name: "protInfoPage")
    page(name: "pollPrefPage")
    page(name: "debugPrefPage")
    page(name: "notifPrefPage")
    page(name: "diagPage")
    page(name: "devNamePage")
    page(name: "devNameResetPage")
    page(name: "resetDiagQueuePage")
    page(name: "quietTimePage")
    page(name: "devPrefPage")
    page(name: "nestLoginPrefPage")
    page(name: "nestTokenResetPage")
    page(name: "uninstallPage")
    page(name: "custWeatherPage")
    page(name: "automationsPage")
}

mappings {
    path("/oauth/initialize") 	{action: [GET: "oauthInitUrl"]}
    path("/oauth/callback") 	{action: [GET: "callback"]}
    if(diagLogs) {
        path("/renderLogs")		{action: [GET: "renderLogJson"]}
        path("/renderState")	{action: [GET: "renderStateJson"]}
        path("/renderDebug")	{action: [GET: "renderDebugJson"]}
    }
}

def authPage() {
    //log.trace "authPage()"

    //atomicState.exLogs = [] //Uncomment this is you are seeing a state size is over 100000 error and it will reset the logs
    getAccessToken()
    preReqCheck()
    deviceHandlerTest()

    //if (!atomicState?.accessToken || !atomicState?.preReqTested || (!atomicState?.isInstalled && !atomicState?.devHandlersTested)) {
    if (!atomicState?.accessToken || (!atomicState?.isInstalled && !atomicState?.devHandlersTested)) {
        return dynamicPage(name: "authPage", title: "Status Page", nextPage: "", install: false, uninstall:false) {
            section ("Status Page:") {
                def desc
                if(!atomicState?.accessToken) {
                    desc = "OAuth is not Enabled for the Nest Manager application.  Please click remove and review the installation directions again..."
                }
                //else if (!atomicState?.preReqTested) {
                    //desc = "SmartThings Location or ZipCode info not found on your ST account.  Please edit you account preferences to make sure they are set..."
                //}
                else if (!atomicState?.devHandlersTested) {
                    desc = "Device Handlers are likely Missing or Not Published.  Please read the installation instructions and verify all device handlers are present before continuing."
                }
                else {
                    desc = "Application Status has not received any messages to display"
                }
                LogAction("Status Message: $desc", "warn", true)
                paragraph "$desc"
            }
        }
    }

    updateWebStuff(true)
    setStateVar(true)

    def description
    def uninstallAllowed = false
    def oauthTokenProvided = false

    if(atomicState?.authToken) {
        description = "You are connected."
        oauthTokenProvided = true
        uninstallAllowed = true
    } else { description = "Click to enter Nest Credentials" }

    def redirectUrl = buildRedirectUrl
    //log.debug "RedirectUrl = ${redirectUrl}"

    if (!oauthTokenProvided && atomicState?.accessToken) {
        LogAction("AuthToken not found: Directing to Login Page...", "debug", true)
        return dynamicPage(name: "authPage", title: "Login Page", nextPage: "", uninstall: uninstallAllowed) {
            section("") {
                paragraph appInfoDesc(), image: getAppImg("nest_manager%402x.png", true)
            }
            section(""){
                paragraph "Tap 'Login to Nest' below to authorize SmartThings to access your Nest Account.\nAfter logon you will be taken to the 'Works with Nest' page. Read the info and if you 'Agree' press the 'Accept' button."
                paragraph "FYI: If using Nest Family please signin with the parent Nest account, family member accounts will not work correctly..."
                href url: redirectUrl, style:"embedded", required: true, title: "Login to Nest", description: description
            }
        }
    } else {
        return mainPage()
    }
}

def mainPage() {
    return dynamicPage(name: "mainPage", title: "Main Page", install: true, uninstall: true) {
        section("") {
            paragraph appInfoDesc(), image: getAppImg("nest_manager%402x.png", true)
            if(!appDevType() && isAppUpdateAvail()) {
                paragraph "An Update is Available for ${appName()}!!!\nCurrent: v${appVersion()} | New: ${atomicState.appData.versions.app.ver}\nPlease visit the IDE to update the code.",
                    image: getAppImg("update_icon.png")
            }
        }
        def structs = getNestStructures()
        def structDesc = !structs?.size() ? "No Locations Found" : "Found (${structs?.size()}) Locations..."
        LogAction("Locations: Found ${structs?.size()} (${structs})", "info", false)
        if (atomicState?.thermostats || atomicState?.protects || atomicState?.presDevice || atomicState?.weatherDevice || isAutoAppInst() ) {  // if devices are configured, you cannot change the structure until they are removed
            section("Your Location:") {
                 paragraph "Location: ${structs[atomicState?.structures]}\n\n(Remove All Devices to Change!)", image: getAppImg("nest_structure_icon.png")
            }
        } else {
            section("Select your Location:") {
                input(name: "structures", title:"Nest Locations", type: "enum", required: true, multiple: false, submitOnChange: true, description: structDesc, metadata: [values:structs],
                        image: getAppImg("nest_structure_icon.png"))
            }
        }
        if (structures) {
            atomicState.structures = structures ? structures : null
            def stats = getNestThermostats()
            def statDesc = stats.size() ? "Found (${stats.size()}) Thermostats..." : "No Thermostats"
            LogAction("Thermostats: Found ${stats?.size()} (${stats})", "info", false)

            def coSmokes = getNestProtects()
            def coDesc = coSmokes.size() ? "Found (${coSmokes.size()}) Protects..." : "No Protects"
            LogAction("Protects: Found ${coSmokes.size()} (${coSmokes})", "info", false)
            section("Select your Devices:") {
                if (!stats.size() && !coSmokes.size()) { paragraph "No Devices were found..." }
                if (stats?.size() > 0) {
                    input(name: "thermostats", title:"Nest Thermostats", type: "enum", required: false, multiple: true, submitOnChange: true, description: statDesc, metadata: [values:stats],
                            image: getAppImg("thermostat_icon.png"))
                }
                atomicState.thermostats =  thermostats ? statState(thermostats) : null
                if (coSmokes.size() > 0) {
                    input(name: "protects", title:"Nest Protects", type: "enum", required: false, multiple: true, submitOnChange: true, description: coDesc, metadata: [values:coSmokes],
                            image: getAppImg("protect_icon.png"))
                }
                atomicState.protects = protects ? coState(protects) : null
                input(name: "presDevice", title:"Add Presence Device?\n", type: "bool", description: "", default: false, required: false, submitOnChange: true, image: getAppImg("presence_icon.png"))
                atomicState.presDevice = presDevice ? true : false
                input(name: "weatherDevice", title:"Add Weather Device?\n", type: "bool", description: "", default: false, required: false, submitOnChange: true, image: getAppImg("weather_icon.png"))
                atomicState.weatherDevice = weatherDevice ? true : false
                //if(atomicState?.weatherDevice && !isWeatherDeviceInst()) {
                if(atomicState?.weatherDevice) {
                    if(!getStZipCode() || getStZipCode() != getNestZipCode()) {
                        href "custWeatherPage", title: "Customize Weather Location?", description: "Tap to configure...", image: getAppImg("weather_icon_grey.png")
                    }
                }
                if(!atomicState?.isInstalled && (thermostats || protects || presDevice || weatherDevice)) {
                    href "devNamePage", title: "Customize Device Names?", description: "Tap to Configure...", image: getAppImg("device_name_icon.png")
                }
            }
            if(atomicState?.isInstalled && atomicState?.structures && (atomicState?.thermostats || atomicState?.protects)) {
                def autoDesc = isAutoAppInst() ? "${getInstAutoTypesDesc()}\n\nTap to Modify..." : null
                section("Automations:") {
                    href "automationsPage", title: "Automations...", description: (autoDesc ? autoDesc : "Tap to Configure..."), state: (autoDesc ? "complete" : null), image: getAppImg("automation_icon.png")
                }
            }
            if((atomicState?.isInstalled && atomicState?.structures && (atomicState?.thermostats || atomicState?.protects || atomicState?.weatherDevice)) || diagLogs) {
                def diagInfoDesc = !diagLogs ? "API Info:" : "Diagnostics/Info:"
                section(diagInfoDesc) {
                    if(atomicState?.structures && (atomicState?.thermostats || atomicState?.protects || atomicState?.weatherDevice) && atomicState?.isInstalled) {
                        href "nestInfoPage", title: "View Nest API Info...", description: "Tap to view info...", image: getAppImg("api_icon.png")
                    }
                    if(diagLogs) {
                        def diagDesc = "Log Entries: (${getExLogSize()} Items)\nTap to view more..."
                        href "diagPage", title: "View Diagnostics...", description: diagDesc, state: (diagDesc ? "complete" : null), image: getAppImg("diag_icon.png")
                    }
                }
            }
            section("Preferences:") {
                def prefDesc = "Notifications: (${pushStatus()})\nDebug: App: (${debugStatus()})/Device: (${deviceDebugStatus()})\nTap to Configure..."
                href "prefsPage", title: "Preferences", description: prefDesc, state: (prefDesc ? "complete" : null), image: getAppImg("settings_icon.png")
            }
        }
        section(" ") {
            href "infoPage", title: "Help, Info and Instructions", description: "Tap to view...", image: getAppImg("info.png")
        }
    }
}

def automationsPage() {
    dynamicPage(name: "automationsPage", title: "", nextPage: "", install: false) {
        def autoApp = findChildAppByName( getAutoAppChildName() )
        def cAppVer = getChildAppVer(autoApp) ? getChildAppVer(autoApp) : null
        
        if(autoApp) {
            section("Installed Automations...") { }
        } else {
            section(" ") {
                paragraph "You haven't created any Automations yet!!!\nTap Create New Automation to get Started..."
            }
        }
        section("Add a new Automation:") {
            app(name: "autoApp", appName: getAutoAppChildName(), namespace: "tonesto7", multiple: true, title: "Create New Automation...", image: getAppImg("automation_icon.png"))
            
            def rText = "NOTICE:\nThe Nest Automations App is still in BETA!!!\nIt will likely contain bugs or unforseen issues. Features may change or be removed during development without notice.\n" +
                        "We are not responsible for any damages caused by using this SmartApp and Device Handlers.\n\nUSE AT YOUR OWN RISK!!!"
            paragraph "$rText"
            !cAppVer ? " " : paragraph("			  Automations App: ${cAppVer}")
        }
    }
}

def custWeatherPage() {
    dynamicPage(name: "custWeatherPage", title: "", nextPage: "", install: false) {
        section("Set Custom Weather Location") {
            def validEnt = "\n\nWeather Stations: [pws:station_id]\nZipCodes: [90250]"
            href url:"https://www.wunderground.com/weatherstation/ListStations.asp", style:"embedded", required:false, title:"Weather Station ID Lookup",
                    description: "Lookup Weather Station ID...", image: getAppImg("search_icon.png")
            def defZip = getStZipCode() ? getStZipCode() : getNestZipCode()
            input("custLocStr", "text", title: "Set Custom Weather Location?", description: "Please enter a ZipCode\n or 'pws:station_id'", required: false, defaultValue: defZip, submitOnChange: true,
                    image: getAppImg("weather_icon_grey.png"))
            paragraph "Valid location entries are:${validEnt}", image: getAppImg("blank_icon.png")
            atomicState.lastWeatherUpdDt = 0
            atomicState?.lastForecastUpdDt = 0
        }
    }
}

//Defines the Preference Page
def prefsPage() {
    def showUninst = (atomicState?.structures || atomicState?.thermostats || atomicState?.protects || atomicState?.presDevice || atomicState?.weatherDevice)
    dynamicPage(name: "prefsPage", title: "Application Preferences", nextPage: "", install: false, uninstall: false ) {
        section("Polling:") {
            def pollDevDesc = "Device Polling: ${getInputEnumLabel(pollValue, pollValEnum())}"
            def pollStrDesc = "\nStructure Polling: ${getInputEnumLabel(pollStrValue, pollValEnum())}"
            def pollWeaDesc = atomicState?.weatherDevice ? "\nWeather Polling: ${getInputEnumLabel(pollWeatherValue, notifValEnum())}" : ""
            def pollStatus = !atomicState?.pollingOn ? "Not Active" : "Active\n${pollDevDesc}${pollStrDesc}${pollWeaDesc}"
            href "pollPrefPage", title: "Polling Preferences", description: "Polling: ${pollStatus}\n\nTap to configure...", state: "complete", image: getAppImg("timer_icon.png")
        }
        section("Devices:") {
            href "devPrefPage", title: "Device Customization", description: "Tap to configure...", image: getAppImg("device_pref_icon.png")
        }
        section("Notifications:") {
            href "notifPrefPage", title: "Notifications", description: "Notifications: (${pushStatus()})\n${getQTimeLabel()}\n\nTap to configure...", state: (pushStatus() in ["Push Active", "Active"] ? "complete" : null), 
                    image: getAppImg("notification_icon.png")
        }
        section("Logging:") {
            href "debugPrefPage", title: "Logs", description: "App Logs: (${debugStatus()})\nDevice Logs: (${deviceDebugStatus()})\n\nTap to configure...", state: (debugStatus() == "On" || deviceDebugStatus() == "On" ? "complete" : null),
                    image: getAppImg("log.png")
        }
        section ("Diagnostics:") {
            input (name: "diagLogs", type: "bool", title: "Enable Diagnostics?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("diag_icon.png"))
            paragraph "Diagnostics allow errors to be stored inside of the SmartApps data store. You can view the logs or share them with the developer to help resolve issues..."
            if (diagLogs) { LogAction("Diagnostic Log Queuing is Enabled...", "info", false) }
            else {
                LogAction("Diagnostic Log Queuing is Disabled...", "info", false)
                atomicState.exLogs = []
            }
        }
        section("Nest Login:") {
            href "nestLoginPrefPage", title: "Nest Login Preferences", description: "Tap to configure...", image: getAppImg("login_icon.png")
        }
        section ("Misc. Options:") {
            input ("useMilitaryTime", "bool", title: "Use Military Time (HH:mm)?", description: "", defaultValue: false, submitOnChange: true, required: false, image: getAppImg("military_time_icon.png"))
            input ("disAppIcons", "bool", title: "Disable App Icons?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("no_icon.png"))
        }
        section("Change the Name of the App:") {
            label title:"Application Label (optional)", required:false
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has been installed...")
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has updated settings...")
}

def uninstalled() {
    //log.debug "uninstalled..."
    if(addRemoveDevices(true)) {
        //Revokes Smartthings endpoint token...
        revokeAccessToken()
        //Revokes Nest Auth Token
        if(atomicState?.authToken) { revokeNestToken() }
        //sends notification of uninstall
        sendNotificationEvent("${textAppName()} is uninstalled...")
    }
}

def initialize() {
    //log.debug "initialize..."
    setStateVar()
    unschedule()
    unsubscribe()
    atomicState.pollingOn = false
    atomicState.lastChildUpdDt = null // force child update on next poll
    atomicState.lastForcePoll = null
    if (addRemoveDevices()) { // if we changed devices, reset queues and polling
        atomicState.cmdQlist = []
    }
    if(thermostats || protects || presDevice || weatherDevice) {
        atomicState?.isInstalled = true
    } else { atomicState.isInstalled = false }
    subscriber()
    setPollingState()
    runIn(20, "stateCleanup", [overwrite: true])
}

def getChildAppVer(appName) { return appName?.appVersion() ? "v${appName?.appVersion()}" : "" }
def appBtnDesc(val) {
    return atomicState?.automationsActive ? (atomicState?.automationsActiveDesc ? "${atomicState?.automationsActiveDesc}\nTap to Modify..." : "Tap to Modify...") :  "Tap to Install..."
}

def isAutoAppInst() {
    def autoApp = childApps.size()
    return (autoApp > 0) ? true : false
}

def autoAppInst(Boolean val) {
    log.debug "${getAutoAppChildName()} is Installed?: ${val}"
    atomicState.autoAppInstalled = val
}

def getInstAutoTypesDesc() {
    def remSenCnt = 0
    def conWatCnt = 0
    def extTmpCnt = 0
    def nModeCnt = 0
    childApps?.each { a ->
        def type = a?.getAutomationType()
        //log.debug "automation type: $type"
        switch(type) {
            case "remSen":
                remSenCnt = remSenCnt+1
                break
            case "conWat":
                conWatCnt = conWatCnt+1
                break
            case "extTmp":
                extTmpCnt = extTmpCnt+1
                break
            case "nMode":
                nModeCnt = nModeCnt+1
                break
        }
    }
    def remSenDesc = (remSenCnt > 0) ? "\nRemote Sensor ($remSenCnt)" : ""
    def conWatDesc = (conWatCnt > 0) ? "\nContact  Sensor ($conWatCnt)" : ""
    def extTmpDesc = (extTmpCnt > 0) ? "\nExternal Sensor ($extTmpCnt)" : ""
    def nModeDesc = (nModeCnt > 0) ? "\nNest Modes ($nModeCnt)" : ""
    return "Automations Installed: ${remSenDesc}${conWatDesc}${extTmpDesc}${nModeDesc}"
}

def subscriber() {
    //subscribe(location, null, pollWatcher, [filterEvents:false])
    subscribe(app, onAppTouch)
    subscribe(location, "sunrise", pollWatcher, [filterEvents: false])
    subscribe(location, "sunset", pollWatcher, [filterEvents: false])
    subscribe(location, "mode", pollWatcher, [filterEvents: false])
    subscribe(location, "routineExecuted", pollWatcher, [filterEvents: false])
    if(temperatures) { subscribe(temperatures, "temperature", pollWatcher, [filterEvents: false]) }
    if(energies) { subscribe(energies, "energy", pollWatcher, [filterEvents: false]) }
    if(powers) { subscribe(powers, "power", pollWatcher, [filterEvents: false]) }
}

def setPollingState() {
    if (!atomicState?.thermostats && !atomicState?.protects && !atomicState?.weatherDevice) {
        LogAction("No Devices Selected...Polling is Off!!!", "info", true)
        unschedule()
        atomicState.pollingOn = false
    } else {
        if(!atomicState?.pollingOn) {
            LogAction("Polling is Now ACTIVE!!!", "info", true)
            atomicState.pollingOn = true
            def pollTime = !settings?.pollValue ? 180 : settings?.pollValue.toInteger()
            def pollStrTime = !settings?.pollStrValue ? 180 : settings?.pollStrValue.toInteger()
            def weatherTimer = pollTime
            if(atomicState?.weatherDevice) { weatherTimer = (pollWeatherValue ? pollWeatherValue.toInteger() : 900) }
            def timgcd = gcd([pollTime, pollStrTime, weatherTimer])
            def random = new Random()
            def random_int = random.nextInt(60)
            timgcd = (timgcd.toInteger() / 60) < 1 ? 1 : timgcd.toInteger()/60
            def random_dint = random.nextInt(timgcd.toInteger())
            LogAction("'Poll' scheduled using Cron (${random_int} ${random_dint}/${timgcd} * * * ?)", "info", true)
            schedule("${random_int} ${random_dint}/${timgcd} * * * ?", poll)  // this runs every timgcd minutes
            poll(true)
        }
    }
}

private gcd(a, b) {
    while (b > 0) {
        long temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

private gcd(input = []) {
    long result = input[0];
    for(int i = 1; i < input.size; i++) result = gcd(result, input[i]);
    return result;
}

def onAppTouch(event) {
    poll(true)
}

def refresh(child = null) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("Refresh Received from Device...${devId}", "debug", true)
    if(childDebug && child) { child?.log("refresh: ${devId}") }
    return sendNestApiCmd(atomicState?.structures, "poll", "poll", 0, devId)
}

/************************************************************************************************
|								API/Device Polling Methods										|
*************************************************************************************************/

def pollFollow() { if(isPollAllowed()) { poll() } }

def pollWatcher(evt) {
    if (isPollAllowed() && (ok2PollDevice() || ok2PollStruct())) { poll() }
}

def poll(force = false, type = null) {
    if(isPollAllowed()) {
        unschedule("postCmd")
        def dev = false
        def str = false
        if (force == true) { forcedPoll(type) }
        if ( !force && !ok2PollDevice() && !ok2PollStruct() ) {
            LogAction("No Device or Structure poll - Devices Last Updated: ${getLastDevicePollSec()} seconds ago... | Structures Last Updated ${getLastStructPollSec()} seconds ago...", "info", true)
        }
        else if(!force) {
            if(ok2PollStruct()) {
                LogAction("Updating Structure Data...(Last Updated: ${getLastStructPollSec()} seconds ago)", "info", true)
                str = getApiData("str")
            }
            if(ok2PollDevice()) {
                LogAction("Updating Device Data...(Last Updated: ${getLastDevicePollSec()} seconds ago)", "info", true)
                dev = getApiData("dev")
            }
        }
        if (atomicState?.pollBlocked) { return }
        if(updChildOnNewOnly) {
            if (dev || str || atomicState?.needChildUpd || (getLastChildUpdSec() > 1800)) { updateChildData() }
        } else { updateChildData() }

        updateWebStuff()
        notificationCheck() //Checks if a notification needs to be sent for a specific event
    }
}

def forcedPoll(type = null) {
    LogAction("forcedPoll($type) received...", "warn", true)
    def lastFrcdPoll = getLastForcedPollSec()
    def pollWaitVal = !settings?.pollWaitVal ? 10 : settings?.pollWaitVal.toInteger()
    if (lastFrcdPoll > pollWaitVal) { //<< This limits manual forces to 10 seconds or more
        atomicState?.lastForcePoll = getDtNow()
        atomicState?.pollBlocked = false
        LogAction("Forcing Data Update... Last Forced Update was ${lastFrcdPoll} seconds ago.", "info", true)
        if (type == "dev" || !type) {
            LogAction("Forcing Update of Device Data...", "info", true)
            getApiData("dev")
        }
        if (type == "str" || !type) {
            LogAction("Forcing Update of Structure Data...", "info", true)
            getApiData("str")
        }
        updateWebStuff(true)
    } else {
        LogAction("Too Soon to Force Data Update!!!!  It's only been (${lastFrcdPoll}) seconds of the minimum (${settings?.pollWaitVal})...", "debug", true)
        atomicState.needStrPoll = true
        atomicState.needDevPoll = true
    }
    updateChildData()
}

def postCmd() {
    //log.trace "postCmd()"
    poll()
}

def getApiData(type = null) {
    //log.trace "getApiData($type)"
    LogAction("getApiData($type)", "info", false)
    def result = false
    if(!type) { return result }

    def tPath = (type == "str") ? "/structures" : "/devices"
    try {
        def params = [
            uri: getNestApiUrl(),
            path: "$tPath",
            headers: ["Content-Type": "text/json", "Authorization": "Bearer ${atomicState?.authToken}"]
        ]
        if(type == "str") {
            atomicState?.lastStrucDataUpd = getDtNow()
            atomicState.needStrPoll = false
            httpGet(params) { resp ->
                if(resp.status == 200) {
                    LogTrace("API Structure Resp.Data: ${resp?.data}")
                    atomicState.apiIssues = false
                    if(!resp?.data?.equals(atomicState?.structData) || !atomicState?.structData) {
                        LogAction("API Structure Data HAS Changed... Updating State data...", "debug", true)
                        atomicState?.structData = resp?.data
                        result = true
                    }
                    else {
                        //LogAction("API Structure Data HAS NOT Changed... Skipping Child Update...", "debug", true)
                    }
                } else {
                    LogAction("getApiStructureData - Received a diffent Response than expected: Resp (${resp?.status})", "error", true, true)
                }
            }
        }
        else if(type == "dev") {
            atomicState?.lastDevDataUpd = getDtNow()
            atomicState?.needDevPoll = false
            httpGet(params) { resp ->
                if(resp?.status == 200) {
                    LogTrace("API Device Resp.Data: ${resp?.data}")
                    atomicState.apiIssues = false
                    if(!resp?.data.equals(atomicState?.deviceData) || !atomicState?.deviceData) {
                        LogAction("API Device Data HAS Changed... Updating State data...", "debug", true)
                        atomicState?.deviceData = resp?.data
                        result = true
                    }
                    else {
                        //LogAction("API Device Data HAS NOT Changed... Skipping Child Update...", "debug", true)
                    }
                } else {
                    LogAction("getApiDeviceData - Received a diffent Response than expected: Resp (${resp?.status})", "error", true, true)
                }
            }
        }
    }
    catch(ex) {
        atomicState.apiIssues = true
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            if (ex.message.contains("Too Many Requests")) {
                log.warn "Received '${ex.message}' response code..."
            }
        } else {
            LogAction("getApiData (type: $type) Exception: ${ex}", "error", true, true)
            if(type == "str") { atomicState.needStrPoll = true }
            else if(type == "dev") { atomicState?.needDevPoll = true }
        }
    }
    return result
}

def updateChildData() {
    LogAction("updateChildData()", "info", true)
    atomicState.needChildUpd = true
    runIn(40, "postCmd", [overwrite: true])
    try {
        atomicState?.lastChildUpdDt = getDtNow()
        getAllChildDevices().each {
            def devId = it.deviceNetworkId
            if(atomicState?.thermostats && atomicState?.deviceData?.thermostats[devId]) {
                def tData = atomicState?.deviceData?.thermostats[devId]
                LogTrace("UpdateChildData >> Thermostat id: ${devId} | data: ${tData}")
                it.generateEvent(tData) //parse received message from parent
                atomicState?.tDevVer = !it.devVer() ? "" : it.devVer()
                return true
            }
            else if(atomicState?.protects && atomicState?.deviceData?.smoke_co_alarms[devId]) {
                def pData = atomicState?.deviceData?.smoke_co_alarms[devId]
                LogTrace("UpdateChildData >> Protect id: ${devId} | data: ${pData}")
                it.generateEvent(pData) //parse received message from parent
                atomicState?.pDevVer = !it.devVer() ? "" : it.devVer()
                return true
            }
            else if(atomicState?.presDevice && devId == getNestPresId()) {
                LogTrace("UpdateChildData >> Presence id: ${devId}")
                def pData = ["debug":childDebug, "mt":useMilitaryTime, "pres":locationPresence(), "api":apiIssues()]
                it.generateEvent(pData)
                atomicState?.presDevVer = !it.devVer() ? "" : it.devVer()
                return true
            }
            else if(atomicState?.weatherDevice && devId == getNestWeatherId()) {
                LogTrace("UpdateChildData >> Weather id: ${devId}")
                it.generateEvent(null)
                atomicState?.weatDevVer = !it.devVer() ? "" : it.devVer()
                return true
            }
            else if(devId == getNestPresId()) {
                return true
            }
            else if(devId == getNestWeatherId()) {
                return true
            }
            else if(!atomicState?.deviceData?.thermostats[devId] && !atomicState?.deviceData?.smoke_co_alarms[devId]) {
                LogAction("Device connection removed? no data for ${devId}", "warn", true, true)
                return null
            }
            else {
                LogAction("updateChildData() for ${devId} after polling", "error", true, true)
                return null
            }
        }
    }
    catch (ex) {
        LogAction("updateChildData Exception: ${ex}", "error", true, true)
        atomicState?.lastChildUpdDt = null
        return
    }
    unschedule("postCmd")
    atomicState.needChildUpd = false
}

def locationPresence() {
    if (atomicState?.structData[atomicState?.structures]) {
        def data = atomicState?.structData[atomicState?.structures]
        LogAction("Location Presence: ${data?.away}", "debug", false)
        LogTrace("Location Presence: ${data?.away}")
        return data?.away.toString()
    }
    else { return null }
}

def apiIssues() {
    return atomicState?.apiIssues ? true : false
    LogAction("API Issues: ${atomicState.apiIssues}", "debug", false)
}

def ok2PollDevice() {
    if (atomicState?.pollBlocked) { return false }
    if (atomicState?.needDevPoll) { return true }
    def pollTime = !settings?.pollValue ? 180 : settings?.pollValue.toInteger()
    def val = pollTime/9
    if (val > 60) { val = 50 }
    return ( ((getLastDevicePollSec() + val) > pollTime) ? true : false )
}

def ok2PollStruct() {
    if (atomicState?.pollBlocked) { return false }
    if (atomicState?.needStrPoll) { return true }
    def pollStrTime = !settings?.pollStrValue ? 180 : settings?.pollStrValue.toInteger()
    def val = pollStrTime/9
    if (val > 60) { val = 50 }
    return ( ((getLastStructPollSec() + val) > pollStrTime) ? true : false )
}

def isPollAllowed() { return (atomicState?.pollingOn && (atomicState?.thermostats || atomicState?.protects || atomicState?.weatherDevice)) ? true : false }
def getLastDevicePollSec() { return !atomicState?.lastDevDataUpd ? 1000 : GetTimeDiffSeconds(atomicState?.lastDevDataUpd).toInteger() }
def getLastStructPollSec() { return !atomicState?.lastStrucDataUpd ? 1000 : GetTimeDiffSeconds(atomicState?.lastStrucDataUpd).toInteger() }
def getLastForcedPollSec() { return !atomicState?.lastForcePoll ? 1000 : GetTimeDiffSeconds(atomicState?.lastForcePoll).toInteger() }
def getLastChildUpdSec() { return !atomicState?.lastChildUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastChildUpdDt).toInteger() }
def getLastWebUpdSec() { return !atomicState?.lastWebUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastWebUpdDt).toInteger() }
def getLastWeatherUpdSec() { return !atomicState?.lastWeatherUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastWeatherUpdDt).toInteger() }
def getLastForecastUpdSec() { return !atomicState?.lastForecastUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastForecastUpdDt).toInteger() }
/************************************************************************************************
|										Nest API Commands										|
*************************************************************************************************/

private cmdProcState(Boolean value) { atomicState?.cmdIsProc = value }
private cmdIsProc() { return !atomicState?.cmdIsProc ? false : true }
private getLastProcSeconds() { return atomicState?.cmdLastProcDt ? GetTimeDiffSeconds(atomicState?.cmdLastProcDt) : 0 }

def apiVar() {
    def api = [
        types:	[ struct:"structures", cos:"devices/smoke_co_alarms", tstat:"devices/thermostats", meta:"metadata" ],
        objs:	[ targetF:"target_temperature_f", targetC:"target_temperature_c", targetLowF:"target_temperature_low_f",
                  targetLowC:"target_temperature_low_c", targetHighF:"target_temperature_high_f", targetHighC:"target_temperature_high_c",
                  fanActive:"fan_timer_active", fanTimer:"fan_timer_timeout", hvacMode:"hvac_mode", away:"away" ],
        modes: 	[ heat:"heat", cool:"cool", heatCool:"heat-cool", off:"off" ]
       ]
    return api
}

def setStructureAway(child, value) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    def val = value?.toBoolean()
    LogAction("Nest Manager(setStructureAway) - Setting Nest Location:${!devId ? "" : " ${devId}"} (${val ? "Away" : "Home"})", "debug", true)
    if(childDebug && child) { child?.log("setStructureAway: ${devId} | (${val})") }
    try {
        if(val) {
            return sendNestApiCmd(atomicState?.structures, apiVar().types.struct, apiVar().objs.away, "away", devId)
        }
        else {
            return sendNestApiCmd(atomicState?.structures, apiVar().types.struct, apiVar().objs.away, "home", devId)
        }
    }
    catch (ex) {
        LogAction("setStructureAway Exception: ${ex}", "debug", true, true)
        if (childDebug && child) { child?.log("setStructureAway Exception: ${ex}", "error") }
        return false
    }
}

def setFanMode(child, fanOn) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    def val = fanOn.toBoolean()
    LogAction("Nest Manager(setFanMode) - Setting Thermostat${!devId ? "" : " ${devId}"} Fan Mode to: (${val ? "On" : "Auto"})", "debug", true)
    if(childDebug && child) { child?.log("setFanMode( devId: ${devId}, fanOn: ${val})") }
    try {
        return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.fanActive, val, devId)
    }
    catch (ex) {
        LogAction("setFanMode Exception: ${ex}", "error", true, true)
        if(childDebug) { child?.log("setFanMode Exception: ${ex}", "error") }
        return false
    }
}

def setHvacMode(child, mode) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("Nest Manager(setHvacMode) - Setting Thermostat${!devId ? "" : " ${devId}"} Mode to: (${mode})", "debug", true)
    try {
        return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.hvacMode, mode.toString(), devId)
    }
    catch (ex) {
        LogAction("setHvacMode Exception: ${ex}", "error", true, true)
        if(childDebug && child) { child?.log("setHvacMode Received: ${devId} (${mode})", "debug") }
        return false
    }
}

def setTargetTemp(child, unit, temp) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("setTargetTemp: ${devId} | (${temp})${unit}", "debug", true)
    if(childDebug && child) { child?.log("setTargetTemp: ${devId} | (${temp})${unit}") }
    try {
        if(unit == "C") {
            return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.targetC, temp, devId)
        }
        else {
            return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.targetF, temp, devId)
        }
    }
    catch (ex) {
        LogAction("setTargetTemp Exception: ${ex}", "error", true, true)
        if(childDebug && child) { child?.log("setTargetTemp Exception: ${ex}", "error") }
        return false
    }
}

def setTargetTempLow(child, unit, temp) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("setTargetTempLow: ${devId} | (${temp})${unit}", "debug", true)
    if(childDebug && child) { child?.log("setTargetTempLow: ${devId} | (${temp})${unit}") }
    try {
        if(unit == "C") {
            return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.targetLowC, temp, devId)
        }
        else {
            return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.targetLowF, temp, devId)
        }
    }
    catch (ex) {
        LogAction("setTargetTempLow Exception: ${ex}", "error", true, true)
        if(childDebug && child) { child?.log("setTargetTempLow Exception: ${ex}", "error") }
        return false
    }
}

def setTargetTempHigh(child, unit, temp) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("setTargetTempHigh: ${devId} | (${temp})${unit}", "debug", true)
    if(childDebug && child) { child?.log("setTargetTempHigh: ${devId} | (${temp})${unit}") }
    try {
        if(unit == "C") {
            return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.targetHighC, temp, devId)
        }
        else {
            return sendNestApiCmd(devId, apiVar().types.tstat, apiVar().objs.targetHighF, temp, devId)
        }
    }
    catch (ex) {
        LogAction("setTargetTempHigh Exception: ${ex}", "error", true, true)
        if(childDebug && child) { child?.log("setTargetTempHigh Exception: ${ex}", "error") }
        return false
    }
}

def sendNestApiCmd(cmdTypeId, cmdType, cmdObj, cmdObjVal, childId) {
    def childDev = getChildDevice(childId)
    def cmdDelay = getChildWaitVal()
    if(childDebug && childDev) { childDev?.log("sendNestApiCmd... $cmdTypeId, $cmdType, $cmdObj, $cmdObjVal, $childId") }
    try {
        if(cmdTypeId) {
            def qnum = getQueueNumber(cmdTypeId, childId)
            if (qnum == -1 ) { return false }

            if (!atomicState?."cmdQ${qnum}" ) { atomicState."cmdQ${qnum}" = [] }
            def cmdQueue = atomicState?."cmdQ${qnum}"
            def cmdData = [cmdTypeId?.toString(), cmdType?.toString(), cmdObj?.toString(), cmdObjVal]

            if (cmdQueue?.contains(cmdData)) {
                LogAction("Command Exists in queue... Skipping...", "warn", true)
                if(childDebug && childDev) { childDev?.log("Command Exists in queue ${qnum}... Skipping...", "warn") }
                schedNextWorkQ(childId)
            } else {
                LogAction("Adding Command to Queue ${qnum}: $cmdTypeId, $cmdType, $cmdObj, $cmdObjVal, $childId", "info", false)
                if(childDebug && childDev) { childDev?.log("Adding Command to Queue ${qnum}: $cmdData") }
                atomicState?.pollBlocked = true
                cmdQueue = atomicState?."cmdQ${qnum}"
                cmdQueue << cmdData
                atomicState."cmdQ${qnum}" = cmdQueue
                atomicState?.lastQcmd = cmdData
                schedNextWorkQ(childId)
            }
            return true

        } else {
            if(childDebug && childDev) { childDev?.log("sendNestApiCmd null cmdTypeId... $cmdTypeId, $cmdType, $cmdObj, $cmdObjVal, $childId") }
            return false
        }
    }
    catch (ex) {
        LogAction("sendNestApiCmd Exception: ${ex}", "error", true, true)
        if(childDebug && childDev) { childDev?.log("sendNestApiCmd Exception: ${ex}", "error") }
        return false
    }
}

private getQueueNumber(cmdTypeId, childId) {
    def childDev = getChildDevice(childId)
    if (!atomicState?.cmdQlist) { atomicState.cmdQlist = [] }
    def cmdQueueList = atomicState?.cmdQlist
    def qnum = cmdQueueList.indexOf(cmdTypeId)
    if (qnum == -1) {
        cmdQueueList = atomicState?.cmdQlist
        cmdQueueList << cmdTypeId
        atomicState.cmdQlist = cmdQueueList
        qnum = cmdQueueList.indexOf(cmdTypeId)
        atomicState?."cmdQ${qnum}" = null
        setLastCmdSentSeconds(qnum, null)
        setRecentSendCmd(qnum, null)
    }
    qnum = cmdQueueList.indexOf(cmdTypeId)
    if (qnum == -1 ) { if(childDebug && childDev) { childDev?.log("getQueueNumber: NOT FOUND" ) } }
    if(childDebug && childDev) { childDev?.log("getQueueNumber: cmdTypeId ${cmdTypeId} is queue ${qnum}" ) }
    return qnum
}

void schedNextWorkQ(childId) {
    def childDev = getChildDevice(childId)
    def cmdDelay = getChildWaitVal()
    //
    // This is throttling the rate of commands to the Nest service for this access token.
    // If too many commands are sent Nest throttling could shut all write commands down for 1 hour to the device or structure
    // This allows up to 3 commands if none sent in the last hour, then only 1 per 60 seconds.   Nest could still
    // throttle this if the battery state on device is low.
    //

    if (!atomicState?.cmdQlist) { atomicState.cmdQlist = [] }
    def cmdQueueList = atomicState?.cmdQlist
    def done = false
    def nearestQ = 100
    def qnum = -1
    cmdQueueList.eachWithIndex { val, idx ->
        if (done || !atomicState?."cmdQ${idx}" ) { return }
        else {
            if ( (getRecentSendCmd(idx) > 0 ) || (getLastCmdSentSeconds(idx) > 60) ) {
                runIn(cmdDelay, "workQueue", [overwrite: true])
                qnum = idx
                done = true
                return
            } else {
                if ((60 - getLastCmdSentSeconds(idx) + cmdDelay) < nearestQ) {
                    nearestQ = (60 - getLastCmdSentSeconds(idx) + cmdDelay)
                    qnum = idx
                }
            }
        }
    }
    if (!done) {
         runIn(nearestQ, "workQueue", [overwrite: true])
    }
    if(childDebug && childDev) { childDev?.log("schedNextWorkQ queue: ${qnum} | recentSendCmd: ${getRecentSendCmd(qnum)} | last seconds: ${getLastCmdSentSeconds(qnum)} | cmdDelay: ${cmdDelay}") }
}

private getRecentSendCmd(qnum) {
    return atomicState?."recentSendCmd${qnum}"
}

private setRecentSendCmd(qnum, val) {
    atomicState."recentSendCmd${qnum}" = val
    return
}

private getLastCmdSentSeconds(qnum) { return atomicState?."lastCmdSentDt${qnum}" ? GetTimeDiffSeconds(atomicState?."lastCmdSentDt${qnum}") : 3601 }

private setLastCmdSentSeconds(qnum, val) {
    atomicState."lastCmdSentDt${qnum}" = val
    atomicState.lastCmdSentDt = val
}


void workQueue() {
    //log.trace "workQueue..."
    def cmdDelay = getChildWaitVal()

    if (!atomicState?.cmdQlist) { atomicState.cmdQlist = [] }
    def cmdQueueList = atomicState?.cmdQlist
    def done = false
    def nearestQ = 100
    def qnum = 0
    cmdQueueList.eachWithIndex { val, idx ->
        if (done || !atomicState?."cmdQ${idx}" ) { return }
        else {
            if ( (getRecentSendCmd(idx) > 0 ) || (getLastCmdSentSeconds(idx) > 60) ) {
                qnum = idx
                done = true
                return
            } else {
                if ((60 - getLastCmdSentSeconds(idx) + cmdDelay) < nearestQ) {
                    nearestQ = (60 - getLastCmdSentSeconds(idx) + cmdDelay)
                    qnum = idx
                }
            }
        }
    }

    log.trace("workQueue Run queue: ${qnum}" )
    if (!atomicState?."cmdQ${qnum}") { atomicState."cmdQ${qnum}" = [] }
    def cmdQueue = atomicState?."cmdQ${qnum}"
    try {
        if(cmdQueue?.size() > 0) {
            runIn(60, "workQueue", [overwrite: true])  // lost schedule catchall
            atomicState?.pollBlocked = true
            cmdQueue = atomicState?."cmdQ${qnum}"
            def cmd = cmdQueue?.remove(0)
            atomicState?."cmdQ${qnum}" = cmdQueue

            if (getLastCmdSentSeconds(qnum) > 3600) { setRecentSendCmd(qnum, 3) } // if nothing sent in last hour, reset 3 command limit

            if (cmd[1] == "poll") {
                atomicState.needStrPoll = true
                atomicState.needDevPoll = true
                atomicState.needChildUpd = true
            } else {
                cmdProcState(true)
                def cmdres = procNestApiCmd(getNestApiUrl(), cmd[0], cmd[1], cmd[2], cmd[3], qnum)
                if ( !cmdres ) {
                    atomicState.needChildUpd = true
                    atomicState.pollBlocked = false
                    runIn((cmdDelay * 2), "postCmd", [overwrite: true])
                }
                cmdProcState(false)
            }

            atomicState.needDevPoll = true
            if(cmd[1] == apiVar().types.struct.toString()) {
                atomicState.needStrPoll = true
            }

            qnum = 0
            done = false
            nearestQ = 100
            cmdQueueList?.eachWithIndex { val, idx ->
                if (done || !atomicState?."cmdQ${idx}" ) { return }
                else {
                    if ( (getRecentSendCmd(idx) > 0 ) || (getLastCmdSentSeconds(idx) > 60) ) {
                        qnum = idx
                        done = true
                        return
                    } else {
                        if ((60 - getLastCmdSentSeconds(idx) + cmdDelay) < nearestQ) {
                            nearestQ = (60 - getLastCmdSentSeconds(idx) + cmdDelay)
                            qnum = idx
                        }
                    }
                }
            }

            if (!atomicState?."cmdQ${qnum}") { atomicState?."cmdQ${qnum}" = [] }
            cmdQueue = atomicState?."cmdQ${qnum}"
            if(cmdQueue?.size() == 0) {
                atomicState.pollBlocked = false
                atomicState.needChildUpd = true
                runIn(cmdDelay + 2, "postCmd", [overwrite: true])
            }
            else { schedNextWorkQ(null) }

            atomicState?.cmdLastProcDt = getDtNow()
            if(cmdQueue?.size() > 10) {
                sendMsg("There is now ${cmdQueue?.size()} events in the Command Queue...", "Warning")
                LogAction("There is now ${cmdQueue?.size()} events in the Command Queue. Something must be wrong...", "warn", true)
            }
            return
        } else { atomicState.pollBlocked = false }
    }
    catch (ex) {
        LogAction("workQueue Exception Error: ${ex}", "error", true, true)
        cmdProcState(false)
        atomicState.needDevPoll = true
        atomicState.needStrPoll = true
        atomicState.needChildUpd = true
        atomicState?.pollBlocked = false
        runIn(60, "workQueue", [overwrite: true])
        runIn((60 + 4), "postCmd", [overwrite: true])
        return
    }
}

def procNestApiCmd(uri, typeId, type, obj, objVal, qnum, redir = false) {
    LogTrace("procNestApiCmd: typeId: ${typeId}, type: ${type}, obj: ${obj}, objVal: ${objVal}, qnum: ${qnum},  isRedirUri: ${redir}")

    def result = false
    try {
        def urlPath = redir ? "" : "/${type}/${typeId}"
        def data = new JsonBuilder("${obj}":objVal)
        def params = [
            uri: uri,
            path: urlPath,
            contentType: "application/json",
            query: [ "auth": atomicState?.authToken ],
            body: data.toString()
        ]
        LogTrace("procNestApiCmd Url: $uri | params: ${params}")
        log.trace "procNestApiCmd Url: $uri | params: ${params}"
        atomicState?.lastCmdSent = "$type: (${obj}: ${objVal})"

        if (!redir && (getRecentSendCmd(qnum) > 0) && (getLastCmdSentSeconds(qnum) < 60)) {
            def val = getRecentSendCmd(qnum)
            val -= 1
            setRecentSendCmd(qnum, val)
        }
        setLastCmdSentSeconds(qnum, getDtNow())

        //log.trace "procNestApiCmd time update recentSendCmd:  ${getRecentSendCmd(qnum)}  last seconds:${getLastCmdSentSeconds(qnum)} queue: ${qnum}"

        httpPutJson(params) { resp ->
            if (resp.status == 307) {
                def newUrl = resp.headers.location.split("\\?")
                LogTrace("NewUrl: ${newUrl[0]}")
                if ( procNestApiCmd(newUrl[0], typeId, type, obj, objVal, qnum, true) ) {
                    result = true
                }
            }
            else if( resp.status == 200) {
                LogAction("procNestApiCmd Processed queue: ${qnum} ($type | ($obj:$objVal)) Successfully!!!", "info", true)
                atomicState?.apiIssues = false
                result = true
            }
            else if(resp.status == 400) {
                LogAction("procNestApiCmd 'Bad Request' Exception: ${resp.status} ($type | $obj:$objVal)", "error", true)
            }
            else {
                LogAction("procNestApiCmd 'Unexpected' Response: ${resp.status}", "warn", true)
            }
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            LogAction("procNestApiCmd 'HttpResponseException' Exception: ${resp?.status} ($type | $obj:$objVal)", "error", true)
        }
        if (ex.message.contains("Bad Request")) {
            LogAction("procNestApiCmd 'Bad Request' Exception: ${resp?.status} ($type | $obj:$objVal)", "error", true)
        }
        LogAction("procNestApiCmd Exception: ${ex} | ($type | $obj:$objVal)", "error", true, true)
        atomicState.apiIssues = true
    }
    return result
}

/************************************************************************************************
|								Push Notification Functions										|
*************************************************************************************************/
def notificationCheck() {
    if((recipients || usePush) && (daysOk(quietDays) && quietTimeOk() && modesOk(quietModes))) {
        if (missedPollNotif) { missedPollNotify() }
        if (updNotif && !appDevType()) { newUpdNotify() }
    }
}

def missedPollNotify() {
    try {
        def now = new Date()
        if(getLastDevicePollSec() > (atomicState?.misPollNotifyWaitVal.toInteger())) {
            if((getLastMisPollMsgSec() > atomicState?.misPollNotifyMsgWaitVal.toInteger())) {
                sendMsg("Warning", "${app.name} has not refreshed data in the last (${getLastDevicePollSec()}) seconds.  Please try refreshing manually.")
                atomicState?.lastMisPollMsgDt = formatDt(now)
            }
        }
    } catch (ex) { LogAction("missedPollNotify Exception: ${ex}", "error", true, true) }
}

def newUpdNotify() {
    try {
        def appUpd = isAppUpdateAvail()
        if(atomicState?.protects) { def pUpd = isProtUpdateAvail() }
        if(atomicState?.presDevice) { def prUpd = isPresUpdateAvail() }
        if(atomicState?.thermostats) { def tUpd = isTstatUpdateAvail() }
        if(atomicState?.weatherDevice) { def wUpd = isWeathUpdateAvail() }
        if(atomicState?.autoAppInstalled) { def autoUpd = isAutoAppUpdateAvail() }
        if((appUpd || pUpd || prUpd || tUpd || autoUpd || wUpd) && (getLastUpdMsgSec() > atomicState?.updNotifyWaitVal.toInteger())) {
            def appl = !appUpd ? "" : "Manager App: v${atomicState?.appData.versions.app.ver.toString()}, "
            def prot = !pUpd ? "" : "Protect: v${atomicState?.appData.versions.protect.ver.toString()}, "
            def pres = !prUpd ? "" : "Presence: v${atomicState?.appData.versions.presence.ver.toString()}, "
            def tstat = !tUpd ? "" : "Thermostat: v${atomicState?.appData.versions.thermostat.ver.toString()}"
            def autoApp = !autoUpd ? "" : "Automation App: v${atomicState?.appData.versions.autoapp.ver.toString()}"
            def weat = !wUpd ? "" : "Weather App: v${atomicState?.appData.versions.weather.ver.toString()}"
            def now = new Date()
            sendMsg("Info", "Update(s) are available: ${appl}${autoApp}${weat}${pres}${prot}${tstat}...  Please visit the IDE to Update your code...")
            atomicState?.lastUpdMsgDt = getDtNow()
        }
    } catch (ex) { LogAction("newUpdNotify Exception: ${ex}", "error", true, true) }
}

def sendMsg(String msg, String msgType, people = null) {
    try {
        def newMsg = "${msgType}: ${msg}"
        def who = people ? people : recipients
        if (location.contactBookEnabled) {
            if(who) {
                sendNotificationToContacts(newMsg, who)
                atomicState?.lastMsg = newMsg
                atomicState?.lastMsgDt = getDtNow()
                log.debug "Push Message Sent: ${atomicState?.lastMsgDt}"
            }
        } else {
            LogAction("ContactBook is NOT Enabled on your SmartThings Account...", "warn", true)
            if (usePush) {
                sendPush(newMsg)
                atomicState?.lastMsg = newMsg
                atomicState?.lastMsgDt = getDtNow()
                log.debug "Push Message Sent: ${atomicState?.lastMsgDt}"
            }
            else if (phone) {
                sendSms(phone, newMsg)
                atomicState?.lastMsg = newMsg
                atomicState?.lastMsgDt = getDtNow()
                log.debug "SMS Message Sent: ${atomicState?.lastMsgDt}"
            }
        }
    } catch (ex) { LogAction("sendMsg Exception: ${ex}", "error", true, true) }
}

def extSendMsg(msg, msgType, people = null, sms = null, push = null) {
    try {
        if(!getOk2Notify()) { 
            LogAction("No Notifications will be sent during Quiet Time...", "info", true)
        } else {
            def newMsg = "${msgType}: ${msg}"
            def who = people ? people : recipients
            if (location.contactBookEnabled) {
                if(who) {
                    sendNotificationToContacts(newMsg, who)
                    atomicState?.lastMsg = newMsg
                    atomicState?.lastMsgDt = getDtNow()
                    log.debug "Push Message Sent: ${atomicState?.lastMsgDt}"
                }
            } else {
                LogAction("ContactBook is NOT Enabled on your SmartThings Account...", "warn", true)
                if (push) {
                    sendPush(newMsg)
                    atomicState?.lastMsg = newMsg
                    atomicState?.lastMsgDt = getDtNow()
                    log.debug "Push Message Sent: ${atomicState?.lastMsgDt}"
                }
                else if (sms) {
                    sendSms(sms, newMsg)
                    atomicState?.lastMsg = newMsg
                    atomicState?.lastMsgDt = getDtNow()
                    log.debug "SMS Message Sent: ${atomicState?.lastMsgDt}"
                }
            }
        }
    } catch (ex) { LogAction("sendMsg Exception: ${ex}", "error", true, true) }
}

def getOk2Notify() {
    return (daysOk(quietDays) && quietTimeOk() && modesOk(quietModes))
}

def pushStatus() { return (recipients || phone || usePush) ? (usePush ? "Push Active" : "Active") : "Not Active" } //Keep this
def getLastMsgSec() { return !atomicState?.lastMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastMsgDt).toInteger() }
def getLastUpdMsgSec() { return !atomicState?.lastUpdMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastUpdMsgDt).toInteger() }
def getLastMisPollMsgSec() { return !atomicState?.lastMisPollMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastMisPollMsgDt).toInteger() }
def getRecipientsSize() { return !settings.recipients ? 0 : settings?.recipients.size() }

def getStZipCode() { return location?.zipCode.toString() }
def getNestZipCode() { return atomicState?.structData[atomicState?.structures].postal_code ? atomicState?.structData[atomicState?.structures]?.postal_code.toString() : "" }
def getNestTimeZone() { return atomicState?.structData[atomicState?.structures].time_zone ? atomicState?.structData[atomicState?.structures].time_zone : null}

def updateWebStuff(now = false) {
    //log.trace "updateWebStuff..."
    if (!atomicState?.appData || getLastWebUpdSec() > (1800)) {
        if(now) {
            getWebFileData()
        } else {
            if(canSchedule()) { runIn(10, "getWebFileData", [overwrite: true]) }  //This reads a JSON file from a web server with timing values and version numbers
        }
    }
    if(atomicState?.weatherDevice && getLastWeatherUpdSec() > (pollWeatherValue ? pollWeatherValue.toInteger() : 900)) {
        if(now) {
            getWeatherConditions(now)
        } else {
            if(canSchedule()) { runIn(15, "getWeatherConditions", [overwrite: true]) }
        }
    }
}

def getWeatherConditions(force = false) {
    //log.trace "getWeatherConditions..."
    if(atomicState?.weatherDevice) {
        try {
            LogAction("Retrieving Latest Local Weather Conditions", "info", true)
            def loc = ""
            def curWeather = ""
            def curForecast = ""
            def curAstronomy = ""
            def curAlerts = ""
            if (custLocStr) {
                loc = custLocStr
                curWeather = getWeatherFeature("conditions", loc)
                curAlerts = getWeatherFeature("alerts", loc)
            } else {
                curWeather = getWeatherFeature("conditions")
                curAlerts = getWeatherFeature("alerts")
            }
            if(getLastForecastUpdSec() > (1800)) {
                if (custLocStr) {
                    loc = custLocStr
                    curForecast = getWeatherFeature("forecast", loc)
                    curAstronomy = getWeatherFeature("astronomy", loc)
                } else {
                    curForecast = getWeatherFeature("forecast")
                    curAstronomy = getWeatherFeature("astronomy")
                }
                if(curForecast && curAstronomy) {
                    atomicState?.curForecast = curForecast
                    atomicState?.curAstronomy = curAstronomy
                    atomicState?.lastForecastUpdDt = getDtNow()
                } else {
                    LogAction("Could Not Retrieve Latest Local Forecast or astronomy Conditions", "warn", true)
                }
            }
            if(curWeather && curAlerts) {
                atomicState?.curWeather = curWeather
                atomicState?.curAlerts = curAlerts
                atomicState?.lastWeatherUpdDt = getDtNow()
            } else {
                LogAction("Could Not Retrieve Latest Local Weather Conditions or alerts", "warn", true)
                return false
            }
            if(curWeather || curAstronomy || curForecast || curAlerts) {
                atomicState.needChildUpd = true
                if (!force) { runIn(30, "postCmd", [overwrite: true]) }
                return true
            }
        }
        catch (ex) {
                LogAction("getWeatherConditions Exception: ${ex}", "error", true, true)
                return false
        }
    } else { return false }
}

def getWData() {
    if(atomicState?.curWeather) {
        return atomicState?.curWeather
    } else {
        if(getWeatherConditions(true)) {
            return atomicState?.curWeather
        }
    }
}

def getWForecastData() {
    if(atomicState?.curForecast) {
        return atomicState?.curForecast
    } else {
        if(getWeatherConditions(true)) {
            return atomicState?.curForecast
        }
    }
}

def getWAstronomyData() {
    if(atomicState?.curAstronomy) {
        return atomicState?.curAstronomy
    } else {
        if(getWeatherConditions(true)) {
            return atomicState?.curAstronomy
        }
    }
}

def getWAlertsData() {
    if(atomicState?.curAlerts) {
        return atomicState?.curAlerts
    } else {
        if(getWeatherConditions(true)) {
            return atomicState?.curAlerts
        }
    }
}

def getWebFileData() {
    //log.trace "getWebFileData..."

    def params = [
        uri: "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Data/appParams.json",
           contentType: 'application/json'
    ]
    def result = false
    try {
        httpGet(params) { resp ->
            if(resp.data) {
                LogAction("Getting Latest Data from appParams.json File...", "info", true)
                atomicState?.appData = resp?.data
                atomicState?.lastWebUpdDt = getDtNow()
            }
            LogTrace("getWebFileData Resp: ${resp?.data}")
            result = true
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
               log.warn  "appParams.json file not found..."
        } else {
            LogAction("getWebFileData Exception: ${ex}", "error", true, true)
        }
    }
    return result
}

def ver2IntArray(val) {
    def ver = val?.split("\\.")
    return [maj:"${ver[0]?.toInteger()}",min:"${ver[1]?.toInteger()}",rev:"${ver[2]?.toInteger()}"]
}

def getChildWaitVal() { return settings?.tempChgWaitVal ? settings?.tempChgWaitVal.toInteger() : 4 }

def isUpdateAvail(newVer, curVer) {
    try {
        def cVer = !curVer ? 100 : curVer?.toString().replaceAll("\\.", "").toInteger()
        def nVer = !newVer ? 100 : newVer?.toString().replaceAll("\\.", "").toInteger()
        if(cVer) {
            if (nVer > cVer) { return true }
        } else { return false }
    } catch (ex) { LogAction("isUpdateAvail Exception: ${ex}", "error", true, true) }
}

def isAppUpdateAvail() {
    if(isUpdateAvail(atomicState?.appData.versions.app.ver, appVersion())) {
        return true
    } else { return false }
}
def isPresUpdateAvail() {
    if(isUpdateAvail(atomicState?.appData.versions.presence.ver, atomicState?.presDevVer)) {
        return true
    } else { return false }
}

def isProtUpdateAvail() {
    if(isUpdateAvail(atomicState?.appData.versions.protect.ver, atomicState?.pDevVer)) {
        return true
    } else { return false }
}

def isTstatUpdateAvail() {
    if(isUpdateAvail(atomicState?.appData.versions.thermostat.ver, atomicState?.tDevVer)) {
        return true
    } else { return false }
}

def isAutoAppUpdateAvail() {
    if(isUpdateAvail(atomicState?.appData.versions.autoapp.ver, atomicState?.autoAppVer)) {
        return true
    } else { return false }
}

def isWeathUpdateAvail() {
    if(isUpdateAvail(atomicState?.appData.versions.weather.ver, atomicState?.weathAppVer)) {
        return true
    } else { return false }
}

/************************************************************************************************
|			This Section Discovers all structures and devices on your Nest Account.				|
|			It also Adds/Removes Devices from ST												|
*************************************************************************************************/

def getNestStructures() {
    LogTrace("Getting Nest Structures")
    def struct = [:]
    def thisstruct = [:]
    try {
        if(ok2PollStruct()) { getApiData("str") }

        if (atomicState?.structData) {
            def structs = atomicState?.structData
            structs.eachWithIndex { struc, index ->
                def strucId = struc?.key
                def strucData = struc?.value

                def dni = [strucData?.structure_id].join('.')
                struct[dni] = strucData?.name.toString()

                if (strucData?.structure_id == settings?.structures) {
                    thisstruct[dni] = strucData?.name.toString()
                } else {
                    if (atomicState?.structures) {
                        if (strucData?.structure_id == atomicState?.structures) {
                            thisstruct[dni] = strucData?.name.toString()
                        }
                    } else {
                        if (!settings?.structures) {
                            thisstruct[dni] = strucData?.name.toString()
                        }
                    }
                }
            }
            if (atomicState?.thermostats || atomicState?.protects || atomicState?.presDevice || atomicState?.weatherDevice || isAutoAppInst() ) {  // if devices are configured, you cannot change the structure until they are removed
                struct = thisstruct
            }
            if (ok2PollDevice()) { getApiData("dev") }
        } else { LogAction("atomicState.structData is: ${atomicState?.structData}", "debug", true, true) }

    } catch (ex) { LogAction("getNestStructures Exception: ${ex}", "error", true, true) }

    return struct
}

def getNestThermostats() {
    LogTrace("Getting Thermostat list")
    def stats = [:]
    def tstats = atomicState?.deviceData?.thermostats
    LogTrace("Found ${tstats?.size()} Thermostats...")
    tstats.each { stat ->
        def statId = stat?.key
        def statData = stat?.value

        def adni = [statData?.device_id].join('.')
        if (statData?.structure_id == settings?.structures) {
            stats[adni] = getThermostatDisplayName(statData)
        }
    }
    return stats
}

def getNestProtects() {
    LogTrace("Getting Nest Protect List...")
    def protects = [:]
    def nProtects = atomicState?.deviceData?.smoke_co_alarms
    LogTrace("Found ${nProtects?.size()} Nest Protects...")
    nProtects.each { dev ->
        def devId = dev?.key
        def devData = dev?.value

        def bdni = [devData?.device_id].join('.')
        if (devData?.structure_id == settings?.structures) {
            protects[bdni] = getProtectDisplayName(devData)
        }
    }
    return protects
}

def statState(val) {
    def stats = [:]
    def tstats = getNestThermostats()
    tstats.each { stat ->
        def statId = stat?.key
        def statData = stat?.value
        val.each { st ->
            if(statId == st) {
                def adni = [statId].join('.')
                stats[adni] = statData
            }
        }
    }
    return stats
}

def coState(val) {
    def protects = [:]
    def nProtects = getNestProtects()
    nProtects.each { dev ->
        val.each { pt ->
        if(dev?.key == pt) {
            def bdni = [dev?.key].join('.')
                protects[bdni] = dev?.value
            }
        }
    }
    return protects
}

def getThermostatDisplayName(stat) {
    if(stat?.name) { return stat.name.toString() }
}

def getProtectDisplayName(prot) {
    if(prot?.name) { return prot.name.toString() }
}

def getNestTstatDni(dni) {
    //log.debug "getNestTstatDni: $dni"
    def d1 = getChildDevice(dni?.key.toString())
    if(d1) { return dni?.key.toString() }
    else {
        def devt =  appDevName()
        return "NestThermostat-${dni?.value.toString()}${devt} | ${dni?.key.toString()}"
    }
    LogAction("getNestTstatDni Issue...", "warn", true)
}

def getNestProtDni(dni) {
    def d2 = getChildDevice(dni?.key.toString())
    if(d2) { return dni?.key.toString() }
    else {
        def devt =  appDevName()
        return "NestProtect-${dni?.value.toString()}${devt} | ${dni?.key.toString()}"
    }
    LogAction("getNestProtDni Issue...", "warn", true)
}

def getNestPresId() {
    def dni = "Nest Presence Device" // old name 1
    def d3 = getChildDevice(dni)
    if(d3) { return dni }
    else {
        if(atomicState?.structures) {
            dni = "NestPres${atomicState.structures}" // old name 2
            d3 = getChildDevice(dni)
            if(d3) { return dni }
        }
        def retVal = ""
        def devt =  appDevName()
        if(settings?.structures) { retVal = "NestPres${devt} | ${settings?.structures}" }
        else if(atomicState?.structures) { retVal = "NestPres${devt} | ${atomicState?.structures}" }
        else {
            LogAction("getNestPresID No structures ${atomicState?.structures}", "warn", true)
            return ""
        }
        return retVal
    }
}

def getNestWeatherId() {
    def dni = "Nest Weather Device (${location?.zipCode})"
    def d4 = getChildDevice(dni)
    if(d4) { return dni }
    else {
        if(atomicState?.structures) {
            dni = "NestWeather${atomicState.structures}"
            d4 = getChildDevice(dni)
            if(d4) { return dni }
        }
        def retVal = ""
        def devt = appDevName()
        if(settings?.structures) { retVal = "NestWeather${devt} | ${settings?.structures}" }
        else if(atomicState?.structures) { retVal = "NestWeather${devt} | ${atomicState?.structures}" }
        else {
            LogAction("getNestWeatherId No structures ${atomicState?.structures}", "warn", true)
            return ""
        }
        return retVal
    }
}

def getNestTstatLabel(name) {
    //log.trace "getNestTstatLabel: ${name}"
    def devt = appDevName()
    def defName = "Nest Thermostat${devt} - ${name}"
    if(atomicState?.useAltNames) { defName = "${location.name}${devt} - ${name}" }
    if(atomicState?.custLabelUsed) {
        return settings?."tstat_${name}_lbl" ? settings?."tstat_${name}_lbl" : defName
    }
    else { return defName }
}

def getNestProtLabel(name) {
    def devt = appDevName()
    def defName = "Nest Protect${devt} - ${name}"
    if(atomicState?.useAltNames) { defName = "${location.name}${devt} - ${name}" }
    if(atomicState?.custLabelUsed) {
        return settings?."prot_${name}_lbl" ? settings?."prot_${name}_lbl" : defName
    }
    else { return defName }
}

def getNestPresLabel() {
    def devt = appDevName()
    def defName = "Nest Presence Device${devt}"
    if(atomicState?.useAltNames) { defName = "${location.name}${devt} - Nest Presence Device" }
    if(atomicState?.custLabelUsed) {
        return settings?.presDev_lbl ? settings?.presDev_lbl.toString() : defName
    }
    else { return defName }
}

def getNestWeatherLabel() {
    def devt = appDevName()
    def wLbl = custLocStr ? custLocStr.toString() : "${getStZipCode()}"
    def defName = "Nest Weather${devt} (${wLbl})"
    if(atomicState?.useAltNames) { defName = "${location.name}${devt} - Nest Weather Device" }
    if(atomicState?.custLabelUsed) {
        return settings?.weathDev_lbl ? settings?.weathDev_lbl.toString() : defName
    }
    else { return defName }
}

def isWeatherDeviceInst() {
    def res = false
    def d = getChildDevice(getNestWeatherId())
    if(d) { res = true }
    return res
}

def addRemoveDevices(uninst = null) {
    //log.trace "addRemoveDevices..."
    def retVal = false
    try {
        def devsInUse = []
        def tstats
        def nProtects
        def devsCrt = 0
        if(!uninst) {
            //LogAction("addRemoveDevices() Nest Thermostats ${atomicState?.thermostats}", "debug", false)
            if (atomicState?.thermostats) {
                tstats = atomicState?.thermostats.collect { dni ->
                    def d = getChildDevice(getNestTstatDni(dni))
                    if(!d) {
                        def d1Label = getNestTstatLabel("${dni.value}")
                        d = addChildDevice(app.namespace, getThermostatChildName(), dni.key, null, [label: "${d1Label}"])
                        d.take()
                        devsCrt = devsCrt + 1
                        LogAction("Created: ${d.displayName} with (Id: ${dni.key})", "debug", true)
                    } else {
                        LogAction("Found: ${d.displayName} with (Id: ${dni.key}) already exists", "debug", true)
                    }
                    devsInUse += dni.key
                    return d
                }
            }
            //LogAction("addRemoveDevices Nest Protects ${atomicState?.protects}", "debug", false)
            if (atomicState?.protects) {
                nProtects = atomicState?.protects.collect { dni ->
                    def d2 = getChildDevice(getNestProtDni(dni).toString())
                    if(!d2) {
                        def d2Label = getNestProtLabel("${dni.value}")
                        d2 = addChildDevice(app.namespace, getProtectChildName(), dni.key, null, [label: "${d2Label}"])
                        d2.take()
                        devsCrt = devsCrt + 1
                        LogAction("Created: ${d2.displayName} with (Id: ${dni.key})", "debug", true)
                    } else {
                        LogAction("Found: ${d2.displayName} with (Id: ${dni.key}) already exists", "debug", true)
                    }
                    devsInUse += dni.key
                    return d2
                }
            }
            //if(devsCrt > 0) { LogAction("Created (${tstats?.size()}) Thermostat(s) and ${nProtects?.size()} Protect(s)", "debug", true) }

            if(atomicState?.presDevice) {
                try {
                    def dni = getNestPresId()
                    def d3 = getChildDevice(dni)
                    if(!d3) {
                        def d3Label = getNestPresLabel()
                        d3 = addChildDevice(app.namespace, getPresenceChildName(), dni, null, [label: "${d3Label}"])
                        d3.take()
                        devsCrt = devsCrt + 1
                        LogAction("Created: ${d3.displayName} with (Id: ${dni})", "debug", true)
                    } else {
                        LogAction("Found: ${d3.displayName} with (Id: ${dni}) already exists", "debug", true)
                    }
                    devsInUse += dni
                    //return d3
                } catch (ex) {
                    LogAction("Nest Presence Device Type is Likely not installed/published", "warn", true)
                    retVal = false
                }
            }

            if(atomicState?.weatherDevice) {
                try {
                    def dni = getNestWeatherId()
                    def d4 = getChildDevice(dni)
                    if(!d4) {
                        def d4Label = getNestWeatherLabel()
                        d4 = addChildDevice(app.namespace, getWeatherChildName(), dni, null, [label: "${d4Label}"])
                        d4.take()
                        atomicState?.lastWeatherUpdDt = null
                        atomicState?.lastForecastUpdDt = null
                        devsCrt = devsCrt + 1
                        LogAction("Created: ${d4.displayName} with (Id: ${dni})", "debug", true)
                    } else {
                        LogAction("Found: ${d4.displayName} with (Id: ${dni}) already exists", "debug", true)
                    }
                    devsInUse += dni
                    //return d4
                } catch (ex) {
                    LogAction("Nest Weather Device Type is Likely not installed/published", "warn", true)
                    retVal = false
                }
            }
            def presCnt = 0
            def weathCnt = 0
            if(atomicState?.presDevice) { presCnt = 1 }
            if(atomicState?.weatherDevice) { weathCnt = 1 }
            if(devsCrt > 0) {
                LogAction("Created Devices;  Current Devices: (${tstats?.size()}) Thermostat(s), ${nProtects?.size()} Protect(s), ${presCnt} Presence Device and ${weathCnt} Weather Device", "debug", true)
            }
        }

        if(uninst) {
            atomicState.thermostats = []
            atomicState.protects = []
            atomicState.presDevice = false
            atomicState.weatherDevice = false
        }

        if (!atomicState?.weatherDevice) {
            atomicState?.curWeather = null
            atomicState?.curForecast = null
            atomicState?.curAstronomy = null
            atomicState?.curAlerts = null
        }

        def delete
        LogAction("devicesInUse: ${devsInUse}", "debug", false)
        delete = getChildDevices().findAll { !devsInUse?.toString()?.contains(it?.deviceNetworkId) }

        if(delete?.size() > 0) {
            LogAction("delete: ${delete}, deleting ${delete.size()} devices", "debug", true)
            delete.each { deleteChildDevice(it.deviceNetworkId) }
        }
        retVal = true
    } catch (ex) {
        if(ex instanceof physicalgraph.exception.ConflictException) {
            def msg = "Error: Can't Delete App because Devices are still in use in other Apps, Routines, or Rules.  Please double check before trying again."
            sendPush(msg)
            LogAction("addRemoveDevices Exception | $msg", "warn", true)
        }
        else if(ex instanceof physicalgraph.app.exception.UnknownDeviceTypeException) {
            def msg = "Error: Device Handlers are likely Missing or Not Published.  Please verify all device handlers are present before continuing."
            sendPush(msg)
            LogAction("addRemoveDevices Exception | $msg", "warn", true, true)
        }
        else { LogAction("addRemoveDevices Exception: ${ex}", "error", true, true) }
        retVal = false
    }
    return retVal
}

def devNamePage() {
    def pagelbl = atomicState?.isInstalled ? "Device Labels" : "Custom Device Labels"
    dynamicPage(name: "devNamePage", title: pageLbl, nextPage: "", install: false) {
        def altName = (atomicState?.useAltNames) ? true : false
        def custName = (atomicState?.custLabelUsed) ? true : false
        section("Settings:") {
            if(atomicState?.isInstalled) {
                paragraph "Changes to device names are only allowed with new devices before they are installed.  Existing devices can only be edited in the devices settings page in the mobile app or in the IDE."
            } else {
                if(!useCustDevNames) {
                    input (name: "useAltNames", type: "bool", title: "Use Location Name as Prefix?", description: "", required: false, defaultValue: altName, submitOnChange: true, image: "" )
                }
                if(!useAltNames) {
                    input (name: "useCustDevNames", type: "bool", title: "Assign Custom Names?", description: "", required: false, defaultValue: custName, submitOnChange: true, image: "" )
                }
            }
            if(atomicState?.custLabelUsed) {
                paragraph "Custom Labels Are Active"
            }
            //paragraph "Current Device Handler Names", image: ""
        }
        def str1 = "\n\nName does not match whats expected.\nName Should be:"
        def str2 = "\n\nName cannot be customized"
        atomicState.useAltNames = useAltNames ? true : false
        atomicState.custLabelUsed = useCustDevNames ? true : false

        def found = false
        if(atomicState?.thermostats) {
            section ("Thermostat Device(s):") {
                atomicState?.thermostats?.each { t ->
                    found = true
                    def d = getChildDevice(getNestTstatDni(t))
                    def dstr = ""
                    if(d) {
                        dstr += "Found: ${d.displayName}"
                        if (d.displayName != getNestTstatLabel(t.value)) {
                            dstr += "$str1 ${getNestTstatLabel(t.value)}"
                        }
                        else if (atomicState?.custLabelUsed) { dstr += "$str2" }
                        //else { dstr += "\n      Matches settings" }
                    } else {
                        dstr += "New Name: ${getNestTstatLabel(t.value)}"
                    }
                    paragraph "${dstr}", image: (atomicState?.custLabelUsed && !d) ? " " : getAppImg("thermostat_icon.png")
                    if(atomicState.custLabelUsed && !d) {
                        input "tstat_${t.value}_lbl", "text", title: "Custom name for ${t.value}", defaultValue: getNestTstatLabel("${t.value}"), submitOnChange: true,
                                image: getAppImg("thermostat_icon.png")
                    }
                }
            }
        }
        if(atomicState?.protects) {
            section ("Protect Device Names:") {
                atomicState?.protects?.each { p ->
                    found = true
                    def dstr = ""
                    def d1 = getChildDevice(getNestProtDni(p))
                    if(d1) {
                        dstr += "Found: ${d1.displayName}"
                        if (d1.displayName != getNestProtLabel(p.value)) {
                            dstr += "$str1 ${getNestProtLabel(p.value)}"
                        }
                        else if (atomicState?.custLabelUsed) { dstr += "$str2" }
                        //else { dstr += "\n      Matches settings" }
                    } else {
                        dstr += "New Name: ${getNestProtLabel(p.value)}"
                    }
                    paragraph "${dstr}", image: (atomicState.custLabelUsed && !d1) ? " " : getAppImg("protect_icon.png")
                    if(atomicState.custLabelUsed && !d1) {
                        input "prot_${p.value}_lbl", "text", title: "Custom name for ${p.value}", defaultValue: getNestProtLabel("${p.value}"), submitOnChange: true,
                                image: getAppImg("protect_icon.png")
                    }
                }
            }
        }
        if(atomicState?.presDevice) {
            section ("Presence Device Name:") {
                found = true
                def p = getNestPresLabel()
                def dni = getNestPresId()
                def d3 = getChildDevice(dni)
                def dstr = ""
                if(d3) {
                    dstr += "Found: ${d3.displayName}"
                    if (d3.displayName != p) {
                        dstr += "$str1 ${p}"
                    }
                    else if (atomicState?.custLabelUsed) { dstr += "$str2" }
                    //else { dstr += "\n      Matches settings" }
                } else {
                    dstr += "New Name: ${p}"
                }
                paragraph "${dstr}", image: (atomicState.custLabelUsed && !d3) ? " " : getAppImg("presence_icon.png")
                if(atomicState.custLabelUsed && !d3) {
                    input "presDev_lbl", "text", title: "Custom name for Nest Presence Device", defaultValue: p, submitOnChange: true,
                            image: getAppImg("presence_icon.png")
                }
            }
        }
        if(atomicState?.weatherDevice) {
            section ("Weather Device Name:") {
                found = true
                def w = getNestWeatherLabel()
                def dni = getNestWeatherId()
                def d4 = getChildDevice(dni)
                def dstr = ""
                if(d4) {
                    dstr += "Found: ${d4.displayName}"
                    if (d4.displayName != w) {
                        dstr += "$str1 ${w}"
                    }
                    else if (atomicState?.custLabelUsed) { dstr += "$str2" }
                    //else { dstr += "\n      Matches settings" }
                } else {
                    dstr += "New Name: ${w}"
                }
                paragraph "${dstr}", image: (atomicState.custLabelUsed && !d4) ? " " : getAppImg("weather_icon.png")
                if(atomicState.custLabelUsed && !d4) {
                    input "weathDev_lbl", "text", title: "Custom name for Nest Weather Device", defaultValue: w, submitOnChange: true,
                            image: getAppImg("weather_icon.png")
                }
            }
        }
        if(!found) {
            paragraph "No Devices Selected"
        }
        /*if(!atomicState?.isInstalled && atomicState?.custLabelUsed) {
            section("Reset Device Names:") {
                href "devNameResetPage", title: "Reset Device Names?", description: "Tap to Configure...", image: getAppImg("settings_icon.png")
            }
        }*/
    }
}

def devNameResetPage() {
    dynamicPage(name: "devNameResetPage", title: "Customize Device Labels", nextPage: "", install: false) {
        if(!atomicState.custLabelUsed && found) {
            section ("Reset Custom Name Settings") {
                paragraph "This section allows reset of previously used Custom names.  These settings are optional and will not change already created devices."
            }
            if(atomicState?.thermostats) {
                section ("Thermostat Device Names:") {
                    atomicState?.thermostats?.each { t ->
                        def ccc = settings?."tstat_${t.value}_lbl"
                      //log.trace "t.value: ${t.value}  getNestTstatLabel: ${getNestTstatLabel(t.value)}  settings.tstat_${t.value}_lbl: ${ccc}"
                        if(ccc && ccc != getNestTstatLabel(t.value) ) {
                            input("tstat_${t.value}_lbl", "text", title: "Reset Custom Name for ${t.value}", required: false, defaultValue: getNestTstatLabel(t.value), submitOnChange: true)
                        } else { paragraph "Device name matches custom settings:\n ${getNestTstatLabel(t.value)}" }
                    }
                }
            }
            if(atomicState?.protects) {
                section ("Protect Device Names:") {
                    atomicState?.protects?.each { p ->
                        def bbb = settings?."prot_${p.value}_lbl"
                        if(bbb && bbb != getNestProtLabel(p.value)) {
                            input("prot_${p.value}_lbl", "text", title: "Reset Custom Name for ${p.value}", required: false, defaultValue: getNestProtLabel(p.value), submitOnChange: true)
                        } else { paragraph "Device name matches custom settings:\n ${getNestProtLabel(p.value)}" }
                    }
                }
            }
            if(atomicState?.presDevice) {
                section ("Presence Device Name:") {
                    def p = getNestPresLabel()
                    if (settings?.presDev_lbl && settings?.presDev_lbl != p ) {
                        input(name:"presDev_lbl", type:"text", title: "Reset Custom Name for Nest Presence Device", required: false, defaultValue: p, submitOnChange: true)
                    } else { paragraph "Device name matches custom settings:\n ${p}" }
                }
            }
            if(atomicState?.weatherDevice) {
                section ("Weather Device Name:") {
                    def w = getNestWeatherLabel()
                    if (settings?.weathDev_lbl && settings?.weathDev_lbl != w) {
                        input(name:"weathDev_lbl", type:"text", title: "Reset Custom Name for Nest Weather Device", required: false, defaultValue: w, submitOnChange: true)
                    } else { paragraph "Device name matches custom settings:\n ${w}" }
                }
            }
        }
    }
}

def deviceHandlerTest() {
    //log.trace "deviceHandlerTest()"
    atomicState.devHandlersTested = true
    return true

    if(atomicState?.devHandlersTested || atomicState?.isInstalled || (atomicState?.thermostats && atomicState?.protects && atomicState?.presDevice && atomicState?.weatherDevice)) {
        atomicState.devHandlersTested = true
        return true
    }
    try {
        def d1 = addChildDevice(app.namespace, getThermostatChildName(), "testNestThermostat-Install123", null, [label:"Nest Thermostat:InstallTest"])
        def d2 = addChildDevice(app.namespace, getPresenceChildName(), "testNestPresence-Install123", null, [label:"Nest Presence:InstallTest"])
        def d3 = addChildDevice(app.namespace, getProtectChildName(), "testNestProtect-Install123", null, [label:"Nest Protect:InstallTest"])
        def d4 = addChildDevice(app.namespace, getWeatherChildName(), "testNestWeather-Install123", null, [label:"Nest Weather:InstallTest"])

        log.debug "d1: ${d1.label} | d2: ${d2.label} | d3: ${d3.label} | d4: ${d4.label}"
        atomicState.devHandlersTested = true
        removeTestDevs()
        //runIn(4, "removeTestDevs")
        return true
    }
    catch (ex) {
        if(ex instanceof physicalgraph.app.exception.UnknownDeviceTypeException) {
            LogAction("Device Handlers are missing: ${getThermostatChildName()}, ${getPresenceChildName()}, and ${getProtectChildName()}, Verify the Device Handlers are installed and Published via the IDE", "error", true, true)
        } else { LogAction("deviceHandlerTest Exception: ${ex}", "error", true, true) }
        atomicState.devHandlersTested = false
        return false
    }
}

def removeTestDevs() {
    try {
        def names = [ "testNestThermostat-Install123", "testNestPresence-Install123", "testNestProtect-Install123", "testNestWeather-Install123" ]
        names?.each { dev ->
            //log.debug "dev: $dev"
            def delete = getChildDevices().findAll { it?.deviceNetworkId == dev }
            //log.debug "delete: ${delete}"
            if(delete) {
               delete.each { deleteChildDevice(it.deviceNetworkId) }
            }
        }
    } catch (ex) {
        LogAction("deviceHandlerTest Exception: ${ex}", "error", true, true)
    }
}

def preReqCheck() {
    //log.trace "preReqCheckTest()"
    if(!location?.timeZone || !location?.zipCode) {
        atomicState.preReqTested = false
        LogAction("SmartThings Location is not returning (TimeZone: ${location?.timeZone}) or (ZipCode: ${location?.zipCode}) Please edit these settings under the IDE...", "warn", true)
        return false
    }
    else {
        atomicState.preReqTested = true
        return true
    }
}

//This code really does nothing at the moment but return the dynamic url of the app's endpoints
def getEndpointUrl() {
    def params = [
        uri: "https://graph.api.smartthings.com/api/smartapps/endpoints",
        query: ["access_token": atomicState?.accessToken],
           contentType: 'application/json'
    ]
    try {
        httpGet(params) { resp ->
            LogAction("EndPoint URL: ${resp?.data?.uri}", "trace", false, false, true)
            return resp?.data?.uri
        }
    } catch (ex) { LogAction("getEndpointUrl Exception: ${ex}", "error", true, true) }
}

def getAccessToken() {
    try {
        if(!atomicState?.accessToken) { atomicState?.accessToken = createAccessToken() }
        else { return true }
    }
    catch (ex) {
        def msg = "Error: OAuth is not Enabled for the Nest Manager application!!!.  Please click remove and Enable Oauth under the SmartApp App Settings in the IDE..."
        sendPush(msg)
        LogAction("getAccessToken Exception | $msg", "warn", true)
        return false
    }
}

/************************************************************************************************
|					Below This line handle SmartThings >> Nest Token Authentication				|
*************************************************************************************************/

//These are the Nest OAUTH Methods to aquire the auth code and then Access Token.
def oauthInitUrl() {
    //log.debug "oauthInitUrl with callback: ${callbackUrl}"
    atomicState.oauthInitState = UUID?.randomUUID().toString()
    def oauthParams = [
        response_type: "code",
        client_id: clientId(),
        state: atomicState?.oauthInitState,
        redirect_uri: callbackUrl //"https://graph.api.smartthings.com/oauth/callback"
    ]
    redirect(location: "https://home.nest.com/login/oauth2?${toQueryString(oauthParams)}")
}

def callback() {
    try {
        LogTrace("callback()>> params: $params, params.code ${params.code}")
        def code = params.code
        LogTrace("Callback Code: ${code}")
        def oauthState = params.state
        LogTrace("Callback State: ${oauthState}")

        if (oauthState == atomicState?.oauthInitState){
            def tokenParams = [
                code: code.toString(),
                client_id: clientId(),
                client_secret: clientSecret(),
                grant_type: "authorization_code",
            ]
            def tokenUrl = "https://api.home.nest.com/oauth2/access_token?${toQueryString(tokenParams)}"
            httpPost(uri: tokenUrl) { resp ->
                atomicState.tokenExpires = resp?.data.expires_in
                atomicState.authToken = resp?.data.access_token
            }

            if (atomicState?.authToken) {
                LogAction("Nest AuthToken Generated Successfully...", "info", true)
                success()
            } else {
                LogAction("There was a Failure Generating the Nest AuthToken!!!", "error", true, true)
                fail()
            }
        }
        else { LogAction("callback() failed oauthState != atomicState.oauthInitState", "error", true, true) }
    }
    catch (ex) {
        LogAction("Callback Exception: ${ex}", "error", true, true)
    }
}

def revokeNestToken() {
    def params = [
        uri: "https://api.home.nest.com",
        path: "/oauth2/access_tokens/${atomicState?.authToken}",
        contentType: 'application/json'
    ]
    try {
        httpDelete(params) { resp ->
            if (resp.status == 204) {
                atomicState?.authToken = null
                LogAction("Your Nest Token has been revoked successfully...", "warn", true)
                return true
            }
        }
    }
    catch (ex) {
        LogAction("revokeNestToken Exception: ${ex}", "error", true, true)
        return false
    }
}

//HTML Connections Pages
def success() {
    def message = """
    <p>Your SmartThings Account is now connected to Nest!</p>
    <p>Click 'Done' to finish setup.</p>
    """
    connectionStatus(message)
}

def fail() {
    def message = """
    <p>The connection could not be established!</p>
    <p>Click 'Done' to return to the menu.</p>
    """
    connectionStatus(message)
}

def connectionStatus(message, redirectUrl = null) {
    def redirectHtml = ""
    if (redirectUrl) { redirectHtml = """<meta http-equiv="refresh" content="3; url=${redirectUrl}" />""" }

    def html = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta name="viewport" content="width=640">
        <title>SmartThings & Nest connection</title>
        <style type="text/css">
                @font-face {
                        font-family: 'Swiss 721 W01 Thin';
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot');
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot?#iefix') format('embedded-opentype'),
                                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.woff') format('woff'),
                                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.ttf') format('truetype'),
                                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.svg#swis721_th_btthin') format('svg');
                        font-weight: normal;
                        font-style: normal;
                }
                @font-face {
                        font-family: 'Swiss 721 W01 Light';
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot');
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot?#iefix') format('embedded-opentype'),
                                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.woff') format('woff'),
                                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.ttf') format('truetype'),
                                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.svg#swis721_lt_btlight') format('svg');
                        font-weight: normal;
                        font-style: normal;
                }
                .container {
                        width: 90%;
                        padding: 4%;
                        /*background: #eee;*/
                        text-align: center;
                }
                img {
                        vertical-align: middle;
                }
                p {
                        font-size: 2.2em;
                        font-family: 'Swiss 721 W01 Thin';
                        text-align: center;
                        color: #666666;
                        padding: 0 40px;
                        margin-bottom: 0;
                }
                span {
                        font-family: 'Swiss 721 W01 Light';
                }
        </style>
        </head>
        <body>
                <div class="container">
                        <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/st-logo%402x.png" alt="SmartThings logo" />
                        <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/connected-device-icn%402x.png" alt="connected device icon" />
                        <img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_icon128.png" alt="nest icon" />
                        ${message}
                </div>
        </body>
        </html>
        """
    render contentType: 'text/html', data: html
}

def getChildTstatsIdString() {
    return thermostats.collect { it.split(/\./).last() }.join(',')
}

def getChildProtectsIdString() {
    return protects.collect { it.split(/\./).last() }.join(',')
}

def toJson(Map m) {
    return new org.json.JSONObject(m).toString()
}

def toQueryString(Map m) {
    return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

def clientId() {
    //if (!appSettings.clientId) { return "63e9befa-dc62-4b73-aaf4-dcf3826dd704" }
    if (!appSettings.clientId) { return "31aea46c-4048-4c2b-b6be-cac7fe305d4c" } //token with cam support
    else { return appSettings.clientId }
}

def clientSecret() {
    //if (!appSettings.clientSecret) {return "8iqT8X46wa2UZnL0oe3TbyOa0" }
    if (!appSettings.clientSecret) {return "FmO469GXfdSVjn7PhKnjGWZlm" } //token with cam support
    else { return appSettings.clientSecret }
}

/************************************************************************************************
|									LOGGING AND Diagnostic										|
*************************************************************************************************/

def LogTrace(msg) {
    if(advAppDebug) { Logger(msg, "trace") }
}

def LogAction(msg, type = "debug", showAlways = false, diag = false) {
    try {
        if(showAlways) { Logger(msg, type) }

        else if (appDebug && !showAlways) { Logger(msg, type) }

        if (diagLogs && diag) {
            def now = new Date()
            def timeStmp = now.toTimestamp()
            def maxStateSize = 50000
            def logEntry = [logType: type, logTime: timeStmp, logMsg: msg]
            def tmpExLogs = atomicState?.exLogs
            def logMsgLngth = logEntry ? (logEntry.toString().length() * 2) : 50
            def curStateSize = state?.toString().length()
            if(!tmpExLogs) {
                tmpExLogs = []
                tmpExLogs << logEntry
            }
            else if (curStateSize < (maxStateSize - logMsgLngth)) {
                tmpExLogs << logEntry
            }
            else if (curStateSize > (maxStateSize - logMsgLngth)) {
                tmpExLogs.remove(0) // << Removes first item in the list to make room
                tmpExLogs << logEntry
            }
            else { log.debug "An error occurred saving to the diag log queue." }
            atomicState?.exLogs = tmpExLogs
        }
    } catch (ex) { log.error("LogAction Exception: ${ex}") }
}

def renderLogJson() {
    try {
        def values = []
        def exLogs = atomicState?.exLogs
        if (!exLogs) { values = [nothing: "found"] }
        else {
            def logJson = new groovy.json.JsonOutput().toJson(exLogs)
            def logString = new groovy.json.JsonOutput().prettyPrint(logJson)
            render contentType: "application/json", data: logString
        }
    } catch (ex) { LogAction("renderLogJson Exception: ${ex}", "error", true, true) }
}

def renderStateJson() {
    try {
        def values = []
        state?.each { item ->
            switch (item.key) {
                case ["accessToken", "authToken", "exLogs", "structData","deviceData"]:
                    break
                default:
                    values << item
                    break
            }
        }
        def logJson = new groovy.json.JsonOutput().toJson(values)
        def logString = new groovy.json.JsonOutput().prettyPrint(logJson)
        render contentType: "application/json", data: logString
    } catch (ex) { LogAction("renderStateJson Exception: ${ex}", "error", true, true) }
}

def renderDebugJson() {
    try {
        def dbgData = []
        def exLog = []
        if (!atomicState?.exLogs) { exLog = [nothing: "found"] }
        else { exLog = atomicState?.exLog }

        def stateVals = []
        state?.each { item ->
            switch (item.key) {
                case ["accessToken", "authToken",]:
                    break
                default:
                    stateVals << item
                    break
            }
        }
        dbgData = ["LogData":exLog, "StateData":stateVals]
        def logJson = new groovy.json.JsonOutput().toJson(dbgData)
        def logString = new groovy.json.JsonOutput().prettyPrint(logJson)
        render contentType: "application/json", data: logString
    } catch (ex) { LogAction("renderDebugJson Exception: ${ex}", "error", true, true) }
}

def Logger(msg, type) {
    if(msg && type) {
        switch(type) {
            case "debug":
                log.debug "${msg}"
                break
            case "info":
                log.info "${msg}"
                break
            case "trace":
                   log.trace "${msg}"
                break
            case "error":
                log.error "${msg}"
                break
            case "warn":
                log.warn "${msg}"
                break
            default:
                log.debug "${msg}"
                break
        }
    }
    else { log.error "Logger Error - type: ${type} | msg: ${msg}" }
}

//Return size of Diagnostic Logs State
def getExLogSize() {
    def cnt = 0
    def exLogs = atomicState?.exLogs
    exLogs.each { cnt = cnt + 1 }
    return (cnt > 0) ? cnt : 0
}

def setStateVar(frc = false) {
    //log.trace "setStateVar..."
    try {
        //If the developer changes the version in the web appParams JSON it will trigger
        //the app to create any new state values that might not exist or reset those that do to prevent errors
        def stateVer = 3
        def stateVar = !atomicState?.stateVarVer ? 0 : atomicState?.stateVarVer.toInteger()
        if(!atomicState?.stateVarUpd || frc || (stateVer < atomicState?.appData.state.stateVarVer.toInteger())) {
            if(!atomicState?.exLogs)                    { atomicState.exLogs = [] }
            if(!atomicState?.misPollNotifyWaitVal) 	    { atomicState.misPollNotifyWaitVal = 900 }
            if(!atomicState?.misPollNotifyMsgWaitVal) 	{ atomicState.misPollNotifyMsgWaitVal = 3600 }
            if(!atomicState?.updNotifyWaitVal) 		    { atomicState.updNotifyWaitVal = 7200 }
            if(!atomicState?.custLabelUsed)             { atomicState?.custLabelUsed = false }
            if(!atomicState?.useAltNames)               { atomicState.useAltNames = false }
            atomicState?.stateVarUpd = true
            atomicState?.stateVarVer = atomicState?.appData?.state?.stateVarVer ? atomicState?.appData?.state?.stateVarVer?.toInteger() : 0
        }
    } catch (ex) { LogAction("setStateVar Exception: ${ex}", "error", true, true) }
}

def stateCleanup() {
    log.trace "stateCleanup..."
    //Things that I need to clear up on updates go here
    // this must be run standalone, and exit after running as the cleanup occurs on exit
    state.remove("pollValue")
    state.remove("pollStrValue")
    state.remove("pollWaitVal")
    state.remove("tempChgWaitVal")
    state.remove("cmdDelayVal")
    state.remove("testedDhInst")
    state.remove("missedPollNotif")
    state.remove("updNotif")
    state.remove("updChildOnNewOnly")
    state.remove("disAppIcons")
    state.remove("showProtAlarmStateEvts")
    state.remove("showAwayAsAuto")
    state.remove("cmdQ")
    state.remove("recentSendCmd")
    state.remove("currentWeather")
    state.remove("altNames")
    state.remove("locstr")
    state.remove("custLocStr")
    if (!atomicState?.cmdQlist) {
        state.remove("cmdQ2")
        state.remove("cmdQ3")
        state.remove("cmdQ4")
        state.remove("cmdQ5")
        state.remove("cmdQ6")
        state.remove("cmdQ7")
        state.remove("cmdQ8")
        state.remove("cmdQ9")
        state.remove("cmdQ10")
        state.remove("cmdQ11")
        state.remove("cmdQ12")
        state.remove("cmdQ13")
        state.remove("cmdQ14")
        state.remove("cmdQ15")
        state.remove("lastCmdSentDt2")
        state.remove("lastCmdSentDt3")
        state.remove("lastCmdSentDt4")
        state.remove("lastCmdSentDt5")
        state.remove("lastCmdSentDt6")
        state.remove("lastCmdSentDt7")
        state.remove("lastCmdSentDt8")
        state.remove("lastCmdSentDt9")
        state.remove("lastCmdSentDt10")
        state.remove("lastCmdSentDt11")
        state.remove("lastCmdSentDt12")
        state.remove("lastCmdSentDt13")
        state.remove("lastCmdSentDt14")
        state.remove("lastCmdSentDt15")
        state.remove("recentSendCmd2")
        state.remove("recentSendCmd3")
        state.remove("recentSendCmd4")
        state.remove("recentSendCmd5")
        state.remove("recentSendCmd6")
        state.remove("recentSendCmd7")
        state.remove("recentSendCmd8")
        state.remove("recentSendCmd9")
        state.remove("recentSendCmd10")
        state.remove("recentSendCmd11")
        state.remove("recentSendCmd12")
        state.remove("recentSendCmd13")
        state.remove("recentSendCmd14")
        state.remove("recentSendCmd15")
    }
/*
    def clnCnt = 0
    def clnValues = ["pollValue", "pollStrValue","pollWaitVal", "tempChgWaitVal", "cmdDelayVal", "testedDhInst", "missedPollNotif", "updNotif", "updChildOnNewOnly",
                     "disAppIcons", "showProtAlarmStateEvts", "showAwayAsAuto", "cmdQ" , "recentSendCmd"  , "currentWeather" , "altNames", "locstr" ]
    clnValues?.each { item ->
        if(atomicState?."$item") { state.remove("$item"); clnCnt = clnCnt+1 }
    }
    if (!atomicState?.cmdQlist) {
        def q = 15
        def i = 1
        while(i <= q) {
            i = i + 1
            if(atomicState?."cmdQ${i}"){ state.remove("cmdQ${i}"); clnCnt = clnCnt+1 }
            if(atomicState?."lastCmdSentDt${i}"){ state.remove("lastCmdSentDt${i}"); clnCnt = clnCnt+1 }
            if(atomicState?."recentSendCmd${i}"){ state.remove("recentSendCmd${i}"); clnCnt = clnCnt+1 }
        }
    }
    log.debug "stateCleanup Removed ($clnCnt) Variables..."
*/
}


/******************************************************************************
*                			Keep These Methods				                  *
*******************************************************************************/
def getThermostatChildName() { return getChildDevName("Nest Thermostat") }
def getProtectChildName()    { return getChildDevName("Nest Protect") }
def getPresenceChildName()   { return getChildDevName("Nest Presence") }
def getWeatherChildName()    { return getChildDevName("Nest Weather") }
def getAutoAppChildName()    { return getChildDevName("Nest Automations") }

def getChildDevName(str)     { return "${str}${appDevName()}" }

def getServerUrl()          { return "https://graph.api.smartthings.com" }
def getShardUrl()           { return getApiServerUrl() }
def getCallbackUrl()		{ return "https://graph.api.smartthings.com/oauth/callback" }
def getBuildRedirectUrl()	{ return "${serverUrl}/oauth/initialize?appId=${app.id}&access_token=${atomicState?.accessToken}&apiServerUrl=${shardUrl}" }
def getNestApiUrl()			{ return "https://developer-api.nest.com" }
def getAppImg(imgName, on = null) 	{ return (!disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/App/$imgName" : "" }

def latestTstatVer()    { return atomicState?.appData?.versions?.thermostat ?: "unknown" }
def latestProtVer()     { return atomicState?.appData?.versions?.protect ?: "unknown" }
def latestPresVer()     { return atomicState?.appData?.versions?.presence ?: "unknown" }
def latestAutoAppVer()  { return atomicState?.appData?.versions?.autoapp ?: "unknown" }
def latestWeathVer()    { return atomicState?.appData?.versions?.weather ?: "unknown" }
def getUse24Time()      { return useMilitaryTime ? true : false }

private debugStatus() { return !appDebug ? "Off" : "On" } //Keep this
private deviceDebugStatus() { return !childDebug ? "Off" : "On" } //Keep this
private isAppDebug() { return !appDebug ? false : true } //Keep This
private isChildDebug() { return !childDebug ? false : true } //Keep This
def getQTimeStrtLbl() { return (qStartInput == "A specific time") ? (qStartTime ? "Start: ${time2Str(qStartTime)}" : null) : ((qStartInput == "sunset" || qStartInput == "sunrise") ? "Start: ${qstartInput.toString().capitalize()}" : null) }
def getQTimeStopLbl() { return (qStopInput == "A specific time") ? (qStopTime ? "Stop: ${time2Str(qStopTime)}" : null) : ((qStopInput == "sunset" || qStopInput == "sunrise") ? "Stop : ${qStopInput.toString().capitalize()}" : null) }
def getQModesLbl() { return quietModes ? ("${(((getQTimeStrtLbl() && getQTimeStopLbl()) || getQDayLbl()) ? "\n" : "")}Quiet Mode(s): ${quietModes}") : null }
def getQDayLbl() { return quietDays ? "Days: ${quietDays}" : null }
def getQTimeLabel() { return ((getQTimeStrtLbl() && getQTimeStopLbl()) || getQDayLbl() || getQModesLbl()) ? "${(getQTimeStrtLbl() && getQTimeStopLbl()) ? "${getQTimeStrtLbl()} - ${getQTimeStopLbl()}\n" : ""}${(quietDays ? "${getQDayLbl()}" : "")}${getQModesLbl()}" : "Tap to Set..." }

def getTimeZone() { 
    def tz = null
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = TimeZone.getTimeZone(getNestTimeZone()) }
    
    if(!tz) { LogAction("getTimeZone: Hub or Nest TimeZone is not found ...", "warn", true, true) }
    
    return tz
}

def formatDt(dt) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
    else {
        LogAction("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...", "warn", true, true)
    }
    return tf.format(dt)
}

//Returns time differences is seconds
def GetTimeDiffSeconds(lastDate) {
    try {
        if(lastDate?.contains("dtNow")) { return 10000 }
        def now = new Date()
        def lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
        def start = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(lastDt)).getTime()
        def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(now)).getTime()
        def diff = (int) (long) (stop - start) / 1000
        return diff
    }
    catch (ex) {
        LogAction("GetTimeDiffSeconds Exception: ${ex}", "error", true)
        return 10000
    }
}

def daysOk(dayVals) {
    try {
        if(dayVals) {
            def day = new SimpleDateFormat("EEEE")
            if(getTimeZone()) { day.setTimeZone(getTimeZone()) }
            return dayVals.contains(day.format(new Date())) ? false : true
        } else { return true }
    } catch (ex) { LogAction("daysOk() Exception: ${ex}", "error", true, true) }
}

def quietTimeOk() {
    try {
        def strtTime = null
        def stopTime = null
        def now = new Date()
        def sun = getSunriseAndSunset() // current based on geofence, previously was: def sun = getSunriseAndSunset(zipCode: zipCode)
        if(qStartTime && qStopTime) {
            if(qStartInput == "sunset") { strtTime = sun.sunset }
            else if(qStartInput == "sunrise") { strtTime = sun.sunrise }
            else if(qStartInput == "A specific time" && qStartTime) { strtTime = qStartTime }

            if(qStopInput == "sunset") { stopTime = sun.sunset }
            else if(qStopInput == "sunrise") { stopTime = sun.sunrise }
            else if(qStopInput == "A specific time" && qStopTime) { stopTime = qStopTime }
        } else { return true }
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), getTimeZone()) ? false : true
        } else { return true }
    } catch (ex) { LogAction("timeOk Exception: ${ex}", "error", true, true) }
}

def modesOk(modeEntry) {
    if (modeEntry) {
        modeEntry?.each { m ->
            if(m.toString() == location?.mode.toString()) { return false }
         }
        return true
    }
    return true
}

def time2Str(time) {
    if (time) {
        def t = timeToday(time, getTimeZone())
        def f = new java.text.SimpleDateFormat("h:mm a")
        f.setTimeZone(getTimeZone() ?: timeZone(time))
        f.format(t)
    }
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

def notifValEnum(allowCust = true) {
    def valsC = [
        60:"1 Minute", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes", 1800:"30 Minutes",
        3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours", 1000000:"Custom"
    ]
    def vals = [
        60:"1 Minute", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
    ]
    return allowCust ? valsC : vals
}

def pollValEnum() {
    def vals = [
        60:"1 Minute", 120:"2 Minutes", 180:"3 Minutes", 240:"4 Minutes", 300:"5 Minutes",
        600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 2700:"45 Minutes", 3600:"60 Minutes"
    ]
    return vals
}

def waitValEnum() {
    def vals = [
        1:"1 Second", 2:"2 Seconds", 3:"3 Seconds", 4:"4 Seconds", 5:"5 Seconds", 6:"6 Seconds", 7:"7 Seconds",
        8:"8 Seconds", 9:"9 Seconds", 10:"10 Seconds", 15:"15 Seconds", 30:"30 Seconds"
    ]
    return vals
}

def getInputEnumLabel(inputName, enumName) {
    def result = "unknown"
    if(input && enumName) {
        enumName.each { item ->
            if(item?.key.toString() == inputName?.toString()) {
                result = item?.value
            }
        }
    }
    return result
}

def appIcon(url) {
    return !disAppIcons ? url.toString() : ""
}

def getShowProtAlarmEvts() { return showProtAlarmStateEvts ? true : false }

/******************************************************************************
|                			Application Pages				                  |
*******************************************************************************/
def pollPrefPage() {
    dynamicPage(name: "pollPrefPage", install: false) {
        section("") {
            paragraph "\nPolling Preferences\n", image: getAppImg("timer_icon.png")
        }
        section("Device Polling:") {
            def pollValDesc = !pollValue ? "Default: 3 Minutes" : pollValue
            input ("pollValue", "enum", title: "Device Poll Rate\nDefault is (3 Minutes)", required: false, defaultValue: 180, metadata: [values:pollValEnum()],
                    description: pollValDesc, submitOnChange: true)
        }
        section("Location Polling:") {
            def pollStrValDesc = !pollStrValue ? "Default: 3 Minutes" : pollStrValue
            input ("pollStrValue", "enum", title: "Location Poll Rate\nDefault is (3 Minutes)", required: false, defaultValue: 180, metadata: [values:pollValEnum()],
                    description: pollStrValDesc, submitOnChange: true)
        }
        if(atomicState?.weatherDevice) {
            section("Weather Polling:") {
                def pollWeatherValDesc = !pollWeatherValue ? "Default: 15 Minutes" : pollWeatherValue
                input ("pollWeatherValue", "enum", title: "Weather Refresh Rate\nDefault is (15 Minutes)", required: false, defaultValue: 900, metadata: [values:notifValEnum(false)],
                        description: pollWeatherValDesc, submitOnChange: true)
            }
        }
        section("Wait Values:") {
            def pollWaitValDesc = !pollWaitVal ? "Default: 10 Seconds" : pollWaitVal
            input ("pollWaitVal", "enum", title: "Forced Refresh Limit\nDefault is (10 sec)", required: false, defaultValue: 10, metadata: [values:waitValEnum()],
                    description: pollWaitValDesc,submitOnChange: true)

            def tempChgWaitValDesc = !tempChgWaitVal ? "Default: 4 Seconds" : tempChgWaitVal
            input ("tempChgWaitVal", "enum", title: "Manual Temp Change Delay\nDefault is (4 sec)", required: false, defaultValue: 4, metadata: [values:waitValEnum()],
                    description: tempChgWaitValDesc, submitOnChange: true)
        }
        section("Other Options:") {
            input "updChildOnNewOnly", "bool", title: "Only Update Children On New Data?", description: "", required: false, defaultValue: true, submitOnChange: true
        }
        section("Advanced Polling Options:") {
            paragraph "If you are still experiencing Polling issues then you can select these devices to use there events to determine if a scheduled poll was missed\nPlease select as FEW devices as possible!\nMore devices will not make for a better polling."
            input "temperatures", "capability.temperatureMeasurement", title: "Which Temperature Sensors?", multiple: true, required: false, image: getAppImg("temperature.png")
            input "energies", "capability.energyMeter",	title: "Which Energy Meters?", multiple: true, required: false, image: getAppImg("lightning.png")
            input "powers",	"capability.powerMeter", title: "Which Power Meters?", multiple: true, required: false, image: getAppImg("power_meter.png")
        }
    }
}

def notifPrefPage() {
    dynamicPage(name: "notifPrefPage", install: false) {
        def notifDesc = !location.contactBookEnabled ? "Enable push notifications below..." : "Select People or Devices to Receive Notifications..."
        section("${notifDesc}") {
            if(!location.contactBookEnabled) {
                input "usePush", "bool", title: "Send Push Notitifications", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
            }
            input("recipients", "contact", title: "Send notifications to", required: false, submitOnChange: true,
                    image: getAppImg("notification_icon.png")) {
                   input ("phone", "phone", title: "Phone Number to send SMS to...", description: "Phone Number", required: false, submitOnChange: true)
            }

            if(recipients || phone || usePush) {
                   if((settings.recipients != recipients && recipients) || !atomicState.pushTested) {
                       sendMsg("Push Notification Test Successful... Notifications have been Enabled for ${textAppName()}", "info")
                       atomicState.pushTested = true
                   } else { atomicState.pushTested = true }
               } else { atomicState.pushTested = false }
        }
        if (recipients || phone || usePush) {
            section(title: "Time Restrictions") {
                href "quietTimePage", title: "Quiet Time...", description: "${getQTimeLabel()}", image: getAppImg("quiet_time_icon.png")
            }
            section("Missed Poll Notification:") {
                input "missedPollNotif", "bool", title: "Send for Missed Polls...", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("late_icon.png")
                if(missedPollNotif == true) {
                    def misPollNotifyWaitValDesc = !misPollNotifyWaitVal ? "Default: 15 Minutes" : misPollNotifyWaitVal
                    input ("misPollNotifyWaitVal", "enum", title: "Time Past the missed Poll?", required: false, defaultValue: 900, metadata: [values:notifValEnum()], description: misPollNotifyWaitValDesc, submitOnChange: true)
                    if(misPollNotifyWaitVal) {
                        atomicState.misPollNotifyWaitVal = !misPollNotifyWaitVal ? 900 : misPollNotifyWaitVal.toInteger()
                        if (misPollNotifyWaitVal.toInteger() == 1000000) {
                            input ("misPollNotifyWaitValCust", "number", title: "Custom Missed Poll Value in Seconds", range: "60..86400", required: false, defaultValue: 900, submitOnChange: true)
                            if(misPollNotifyWaitValCust) { atomicState?.misPollNotifyWaitVal = misPollNotifyWaitValCust ? misPollNotifyWaitValCust.toInteger() : 900 }
                        }
                    } else { atomicState.misPollNotifyWaitVal = !misPollNotifyWaitVal ? 900 : misPollNotifyWaitVal.toInteger() }

                    def misPollNotifyMsgWaitValDesc = !misPollNotifyMsgWaitVal ? "Default: 1 Hour" : misPollNotifyMsgWaitVal
                    input ("misPollNotifyMsgWaitVal", "enum", title: "Wait before sending another?", required: false, defaultValue: 3600, metadata: [values:notifValEnum()], description: misPollNotifyMsgWaitValDesc, submitOnChange: true)
                    if(misPollNotifyMsgWaitVal) {
                        atomicState.misPollNotifyMsgWaitVal = !misPollNotifyMsgWaitVal ? 3600 : misPollNotifyMsgWaitVal.toInteger()
                        if (misPollNotifyMsgWaitVal.toInteger() == 1000000) {
                            input ("misPollNotifyMsgWaitValCust", "number", title: "Custom Msg Wait Value in Seconds", range: "60..86400", required: false, defaultValue: 3600, submitOnChange: true)
                            if(misPollNotifyMsgWaitValCust) { atomicState.misPollNotifyMsgWaitVal = misPollNotifyMsgWaitValCust ? misPollNotifyMsgWaitValCust.toInteger() : 3600 }
                        }
                    } else { atomicState.misPollNotifyMsgWaitVal = !misPollNotifyMsgWaitVal ? 3600 : misPollNotifyMsgWaitVal.toInteger() }
                }
            }
            section("App and Device Updates:") {
                input "updNotif", "bool", title: "Send for Updates...", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("update_icon3.png")
                   if(updNotif == true) {
                    def updNotifyWaitValDesc = !updNotifyWaitVal ? "Default: 2 Hours" : updNotifyWaitVal
                    input ("updNotifyWaitVal", "enum", title: "Send reminders every?", required: false, defaultValue: 7200, metadata: [values:notifValEnum()], description: updNotifyWaitValDesc, submitOnChange: true)
                    if(updNotifyWaitVal) {
                        atomicState.updNotifyWaitVal = !updNotifyWaitVal ? 7200 : updNotifyWaitVal.toInteger()
                        if (updNotifyWaitVal.toInteger() == 1000000) {
                            input ("updNotifyWaitValCust", "number", title: "Custom Missed Poll Value in Seconds", range: "30..86400", required: false, defaultValue: 7200, submitOnChange: true)
                            if(updNotifyWaitValCust) { atomicState.updNotifyWaitVal = updNotifyWaitValCust ? updNotifyWaitValCust.toInteger() : 7200 }
                        }
                    } else { atomicState.updNotifyWaitVal = !updNotifyWaitVal ? 7200 : updNotifyWaitVal.toInteger() }
                }
            }
        }
    }
}

def devPrefPage() {
    dynamicPage(name: "devPrefPage", title: "Device Preferences", uninstall: false) {
        section("") {
            paragraph "\nDevice Preferences\n", image: getAppImg("device_pref_icon.png")
        }
        if(thermostats || protects || presDevice || weatherDevice) {
            section("Device Names:") {
                def devDesc = (atomicState?.custLabelUsed || atomicState?.useAltNames) ? "Custom Labels Set...\n\nTap to Modify..." : "Tap to Configure..."
                href "devNamePage", title: "Device Names...", description: devDesc, image: getAppImg("device_name_icon.png")
            }
        }
        if(atomicState?.protects) {
            section("Protect Devices:") {
                input "showProtActEvts", "bool", title: "Show Non-Alarm Events in Device Activity Feed?", description: "", required: false, defaultValue: true, submitOnChange: true,
                        image: getAppImg("list_icon.png")
            }
        }
        if(atomicState?.thermostats) {
            section("Thermostat Devices:") {
                paragraph "Nothing to see here yet!!!"
            }
        }
        if(atomicState?.presDevice) {
            section("Presence Device:") {
                paragraph "Nothing to see here yet!!!"
            }
        }
        if(atomicState?.weatherDevice) {
            section("Weather Device:") {
                href "custWeatherPage", title: "Customize Weather Location?", description: "Tap to configure...", image: getAppImg("weather_icon_grey.png")
                //paragraph "Nothing to see here yet!!!"
            }
        }
    }
}

/********************************************************************************
|            			DEVICE NAME CUSTOMIZATION CODE	     					|
*********************************************************************************/

def quietTimePage() {
    dynamicPage(name: "quietTimePage", title: "Quiet during certain times", uninstall: false) {
        section() {
            input "qStartInput", "enum", title: "Starting at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false,
                image: getAppImg("start_time_icon.png")
            if(qStartInput == "A specific time") {
                input "qStartTime", "time", title: "Start time", required: true }
        }
        section() {
            input "qStopInput", "enum", title: "Stopping at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false,
                image: getAppImg("stop_time_icon.png")
            if(qStopInput == "A specific time") {
                input "qStopTime", "time", title: "Stop time", required: true }
        }
        section() {
            input "quietDays", "enum", title: "Only on certain days of the week", multiple: true, required: false, image: getAppImg("day_calendar_icon.png"),
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
        }
        section() {
            input "quietModes", "mode", title: "Quiet when these Modes are Active", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode_icon.png")
        }
    }
}

def debugPrefPage() {
    dynamicPage(name: "debugPrefPage", install: false) {
        section ("Application Logs") {
            input (name: "appDebug", type: "bool", title: "Show App Logs in the IDE?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("log.png"))
            if (appDebug) {
                input (name: "advAppDebug", type: "bool", title: "Show Verbose Logs?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("list_icon.png"))
                LogAction("Debug Logs are Enabled...", "info", false)
            }
            else { LogAction("Debug Logs are Disabled...", "info", false) }
        }
        section ("Child Device Logs") {
            input (name: "childDebug", type: "bool", title: "Show Device Logs in the IDE?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("log.png"))
            if (childDebug) { LogAction("Device Debug Logs are Enabled...", "info", false) }
            else { LogAction("Device Debug Logs are Disabled...", "info", false) }
        }
    }
}

def infoPage () {
    dynamicPage(name: "infoPage", title: "Help, Info and Instructions", install: false) {
        section("About this App:") {
            paragraph appInfoDesc(), image: getAppImg("nest_manager%402x.png", true)
        }
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/README.html", style:"embedded", required:false, title:"Readme File",
                description:"View the Projects Readme File...", state: "complete", image: getAppImg("readme_icon.png")
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages",
                description:"View the Help and Instructions Page...", state: "complete", image: getAppImg("help_icon.png")
        }
        section("Created by:") {
            paragraph "Anthony S. (@tonesto7)"
        }
        section("Collaborators:") {
            paragraph "Ben W. (@desertblade)\nEric S. (@E_Sch)"
        }
        section("App Revision History:") {
            paragraph appVerInfo()
        }
        section("Donations:") {
            href url: textDonateLink(), style:"external", required: false, title:"Donations",
                description:"Tap to Open in Mobile Browser...", state: "complete", image: getAppImg("donate_icon.png")
        }
        section("Licensing Info:") {
            paragraph "${textCopyright()}\n${textLicense()}"
        }
    }
}

def uninstallPage() {
    dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        section("") {
            paragraph "Uninstall this App, Automation App and Child Devices"
        }
    }
}

def diagPage () {
    dynamicPage(name: "diagPage", install: false) {
        section("") {
            paragraph "This page will allow you to view/export diagnostic logs and state data to assist the developer in troubleshooting...", image: getAppImg("diag_icon.png")
        }
        section("Export or View the Logs") {
            href url:"${apiServerUrl("/api/smartapps/installations/${app.id}/renderLogs?access_token=${atomicState.accessToken}")}", style:"embedded", required:false,
                       title:"Diagnostic Logs", description: "Log Entries: (${getExLogSize()} Items)\n\nTap to view diagnostic logs...", state: "complete", image: getAppImg("log_data_icon.png")
            href url:"${apiServerUrl("/api/smartapps/installations/${app.id}/renderState?access_token=${atomicState.accessToken}")}", style:"embedded", required:false,
                       title:"State Data", description:"Tap to view State Data...", image: getAppImg("state_data_icon.png")
            href url:"${apiServerUrl("/api/smartapps/installations/${app.id}/renderDebug?access_token=${atomicState.accessToken}")}", style:"embedded", required:false,
                       title:"Developer Debug Data", description:"Tap to view Debug Data...", image: getAppImg("debug_data_icon.png")
        }
        section("Reset Diagnostic Queue:") {
            href "resetDiagQueuePage", title: "Reset Diagnostic Logs", description: "Tap to Reset the Logs...", image: getAppImg("reset_icon.png")
        }
    }
}

def resetDiagQueuePage() {
    return dynamicPage(name: "resetDiagQueuePage", nextPage: diagPage, install: false) {
        section ("Diagnostic Log Queue Reset..") {
            atomicState.exLogs = []
            paragraph "Diagnostic Logs have been reset...\nPress Done to return to previous page..."
        }
    }
}

def nestLoginPrefPage () {
    dynamicPage(name: "nestLoginPrefPage", install: false) {
        section("Nest Login Preferences:") {
            href "nestTokenResetPage", title: "Log Out and Reset your Nest Token", description: "Tap to Reset the Token...", image: getAppImg("reset_icon.png")
        }
    }
}

def nestTokenResetPage() {
    return dynamicPage(name: "nestTokenResetPage", install: false) {
        section ("Resetting Nest Token..") {
            revokeNestToken()
            atomicState.authToken = null
            paragraph "Token has been reset...\nPress Done to return to Login page..."
        }
    }
}

def nestInfoPage () {
    dynamicPage(name: "nestInfoPage", install: false) {
        section("About this page:") {
            paragraph "This Page will display the data returned from the API for each device that is selected..."
        }
        if(atomicState?.structures) {
            section("Locations") {
                href "structInfoPage", title: "Nest Location(s) Info...", description: "Tap to view Location info...", image: getAppImg("nest_structure_icon.png")
            }
        }
        if (atomicState?.thermostats) {
            section("Thermostats") {
                href "tstatInfoPage", title: "Nest Thermostat(s) Info...", description: "Tap to view Thermostat info...", image: getAppImg("nest_like.png")
            }
        }
        if (atomicState?.protects) {
            section("Protects") {
                href "protInfoPage", title: "Nest Protect(s) Info...", description: "Tap to view Protect info...", image: getAppImg("protect_icon.png")
            }
        }
        section("Last Nest Command") {
            def cmdTxt = atomicState.lastCmdSent ? atomicState?.lastCmdSent : "Nothing found..."
            def cmdDt = atomicState.lastCmdSentDt ? atomicState?.lastCmdSentDt : "Nothing found..."
            paragraph "Command: ${cmdTxt}\nDateTime: ${cmdDt}"
        }
    }
}

def structInfoPage () {
    dynamicPage(name: "structInfoPage", refreshInterval: 15, install: false) {
        section("") {
            paragraph "\nLocation Info:", image: getAppImg("nest_structure_icon.png")
        }
        for(str in atomicState?.structData) {
            if (str.key == atomicState?.structures) {
                def strId = str.key
                def strData = str.value
                section("Location Name: ${strData.name}") {
                    strData.each { item ->
                        switch (item.key) {
                            case [ "wheres" ]:
                                break
                            default:
                                paragraph "${item.key.toString().capitalize()}: ${item.value}"
                                break
                        }
                    }
                }
            }
        }
    }
}

def tstatInfoPage () {
    dynamicPage(name: "tstatInfoPage", refreshInterval: 15, install: false) {
        section("") {
            paragraph "\nThermostats:", image: getAppImg("nest_like.png")
        }
        for(tstat in atomicState?.thermostats) {
            def devs = []
            section("Thermostat Name: ${tstat.value}") {
                atomicState?.deviceData?.thermostats[tstat.key].each { dev ->
                    switch (dev.key) {
                        case [ "where_id" ]:  //<< Excludes certain keys from being shown
                            break
                        default:
                            devs << "${dev.key.toString().capitalize()}: ${dev.value}"
                            break
                    }
                }
                devs.sort().each { item ->
                    paragraph "${item}"
                }
            }
        }
    }
}

def protInfoPage () {
    dynamicPage(name: "protInfoPage", refreshInterval: 15, install: false) {
        section("") {
            paragraph "\nProtects:", image: getAppImg("protect_icon.png")
        }
        atomicState?.protects.each { prot ->
            def devs = []
            section("Protect Name: ${prot.value}") {
                atomicState?.deviceData?.smoke_co_alarms[prot.key].each { dev ->
                    log.debug "prot dev: $dev"
                    switch (dev.key) {
                        case [ "where_id" ]:  //<< Excludes certain keys from being shown
                            break
                        default:
                            devs << "${dev.key.toString().capitalize()}: ${dev.value}"
                            break
                    }
                }
                devs.sort().each { item ->
                    paragraph "${item}"
                }
            }
        }
    }
}


/******************************************************************************
*                Application Help and License Info Variables                  *
*******************************************************************************/
//Change This to rename the Default App Name
private def appName() 		{ "Nest Manager${appDevName()}" }
private def appAuthor() 	{ "Anthony S." }
private def appNamespace() 	{ "tonesto7" }
private def gitBranch()     { "master" }
private def appDevType()    { false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
private def appInfoDesc() 	{
    def cur = atomicState?.appData?.versions?.app?.ver.toString()
    def ver = (isAppUpdateAvail()) ? "${textVersion()} (Lastest: v${cur})" : textVersion()
    return "Name: ${textAppName()}\n${ver}\n${textModified()}"
}
private def textAppName()   { return "${appName()}" }
private def textVersion()   { return "Version: ${appVersion()}" }
private def textModified()  { return "Updated: ${appVerDate()}" }
private def textAuthor()    { return "${appAuthor()}" }
private def textNamespace() { return "${appNamespace()}" }
private def textVerInfo()   { return "${appVerInfo()}" }
private def textDonateLink(){ return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2CJEVN439EAWS" }
private def textCopyright() { return "Copyright© 2016 - Anthony S." }
private def textDesc()      { return "This SmartApp is used to integrate you're Nest devices with SmartThings...." }
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
