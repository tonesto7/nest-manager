/********************************************************************************************
|    Application Name: Nest Manager and Automations                                         |
|    Author: Anthony S. (@tonesto7),                                                        |
|    Contributors: Ben W. (@desertblade) | Eric S. (@E_sch)                                 |
|                                                                                           |
|*******************************************************************************************|
|    There maybe portions of the code that may resemble code from other apps in the         |
|    community. I may have used some of it as a point of reference.                         |
|    Thanks go out to those Authors!!!                                                      |
|    I apologize if i've missed anyone.  Please let me know and I will add your credits     |
|                                                                                           |
|    ### I really hope that we don't have a ton or forks being released to the community,   |
|    ### I hope that we can collaborate and make app and device type that will accomodate   |
|    ### every use case                                                                     |
*********************************************************************************************/

import groovy.json.*
import groovy.time.*
import java.text.SimpleDateFormat
import java.security.MessageDigest

definition(
    name: "${textAppName()}",
    namespace: "${textNamespace()}",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager%402x.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager%403x.png",
    singleInstance: true,
    oauth: true )

{
    appSetting "clientId"
    appSetting "clientSecret"
}

def appVersion() { "3.0.2" }
def appVerDate() { "8-17-2016" }
def appVerInfo() {
    def str = ""

    str += "V3.0.2 (August 17th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Timeout bugfixes"

    str += "V3.0.1 (August 16th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Lot's of little bugfixes"

    str += "\n\nV3.0.0 (August 16th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: V3.0 Release."

    return str
}

preferences {
    //startPage
    page(name: "startPage")

    //Manager Pages
    page(name: "authPage")
    page(name: "mainPage")
    page(name: "deviceSelectPage")
    page(name: "reviewSetupPage")
    page(name: "changeLogPage")
    page(name: "prefsPage")
    page(name: "infoPage")
    page(name: "nestInfoPage")
    page(name: "structInfoPage")
    page(name: "tstatInfoPage")
    page(name: "protInfoPage")
    page(name: "camInfoPage")
    page(name: "pollPrefPage")
    page(name: "debugPrefPage")
    page(name: "notifPrefPage")
    page(name: "diagPage")
    page(name: "appParamsDataPage")
    page(name: "devNamePage")
    page(name: "childAppDataPage")
    page(name: "childDevDataPage")
    page(name: "managAppDataPage")
    page(name: "alarmTestPage")
    page(name: "simulateTestEventPage")
    page(name: "safetyValuesPage")
    page(name: "devNameResetPage")
    page(name: "resetDiagQueuePage")
    page(name: "devPrefPage")
    page(name: "nestLoginPrefPage")
    page(name: "nestTokenResetPage")
    page(name: "uninstallPage")
    page(name: "custWeatherPage")
    page(name: "automationsPage")
    page(name: "automationKickStartPage")
    page(name: "automationGlobalPrefsPage")
    page(name: "automationStatisticsPage")

    //Automation Pages
    page(name: "selectAutoPage" )
    page(name: "mainAutoPage")
    page(name: "nameAutoPage")
    page(name: "remSensorPage")
    page(name: "remSenTstatFanSwitchPage")
    page(name: "remSenShowTempsPage")
    page(name: "fanControlPage")
    page(name: "extTempPage")
    page(name: "contactWatchPage")
    page(name: "leakWatchPage")
    page(name: "fanVentPage" )
    page(name: "nestModePresPage")
    page(name: "tstatModePage")
    page(name: "tModeTstatConfModePage")
    page(name: "setRecipientsPage")
    page(name: "setDayModeTimePage")
    page(name: "watchDogPage")

    //shared pages
    page(name: "setNotificationPage")
    page(name: "setNotificationTimePage")
}

mappings {
    if(!parent) {
        //used during Oauth Authentication
        path("/oauth/initialize") 	{action: [GET: "oauthInitUrl"]}
        path("/oauth/callback") 	{action: [GET: "callback"]}
        //Renders Json Data
        path("/renderInstallId")  {action: [GET: "renderInstallId"]}
        path("/renderInstallData"){action: [GET: "renderInstallData"]}
        //path("/receiveEventData") {action: [POST: "receiveEventData"]}
    }
}

//This Page is used to load either parent or child app interface code
def startPage() {
    if (parent) {
        atomicState?.isParent = false
        selectAutoPage()
    } else {
        atomicState?.isParent = true
        authPage()
    }
}

def authPage() {
    //log.trace "authPage()"
    getAccessToken()
    preReqCheck()
    deviceHandlerTest()

    if (!atomicState?.accessToken || (!atomicState?.isInstalled && !atomicState?.devHandlersTested)) {
        return dynamicPage(name: "authPage", title: "Status Page", nextPage: "", install: false, uninstall: false) {
            section ("Status Page:") {
                def desc
                if(!atomicState?.accessToken) {
                    desc = "OAuth is not Enabled for the Nest Manager application.  Please click remove and review the installation directions again..."
                }
                else if (!atomicState?.devHandlersTested) {
                    desc = "Device Handlers are likely Missing or Not Published.  Please read the installation instructions and verify all device handlers are present before continuing."
                }
                else {
                    desc = "Application Status has not received any messages to display"
                }
                LogAction("Status Message: $desc", "warn", true)
                paragraph "$desc", required: true, state: null
            }
        }
    }
    updateWebStuff(true)
    setStateVar(true)
    if (atomicState?.newSetupComplete) {
        def result = ((atomicState?.appData?.updater?.setupVersion && !atomicState?.setupVersion) || (atomicState?.setupVersion?.toInteger() < atomicState?.appData?.updater?.setupVersion?.toInteger())) ? true : false
        if(result) { atomicState?.newSetupComplete = null }
    }

    def description
    def oauthTokenProvided = false

    if(atomicState?.authToken) {
        description = "You are connected."
        oauthTokenProvided = true
    } else { description = "Click to enter Nest Credentials" }

    def redirectUrl = buildRedirectUrl
    //log.debug "RedirectUrl = ${redirectUrl}"

    if (!oauthTokenProvided && atomicState?.accessToken) {
        LogAction("AuthToken not found: Directing to Login Page...", "info", true)
        return dynamicPage(name: "authPage", title: "Login Page", nextPage: "mainPage", install: false, uninstall: false) {
            section("") {
                paragraph appInfoDesc(), image: getAppImg("nest_manager%402x.png", true)
            }
            section(""){
                paragraph "Tap 'Login to Nest' below to authorize SmartThings to access your Nest Account.\n\nAfter login you will be taken to the 'Works with Nest' page. Read the info and if you 'Agree' press the 'Accept' button."
                paragraph "❖ FYI: If you are using a Nest Family account please signin with the parent Nest account, family member accounts will not work correctly...", state: "complete"
                href url: redirectUrl, style:"embedded", required: true, title: "Login to Nest", description: description
            }
        }
    }
    else {
        return mainPage()
    }
}

def mainPage() {
    //log.trace "mainPage"
    def setupComplete = (!atomicState?.newSetupComplete || !atomicState.isInstalled) ? false : true
    return dynamicPage(name: "mainPage", title: "Main Page", nextPage: (!setupComplete ? "reviewSetupPage" : null), install: setupComplete, uninstall: false) {
        section("") {
            href "changeLogPage", title: "", description: "${appInfoDesc()}", image: getAppImg("nest_manager%402x.png", true)
            if(atomicState?.appData && !appDevType() && isAppUpdateAvail()) {
                href url: stIdeLink(), style:"external", required: false, title:"An Update is Available for ${appName()}!!!",
                        description:"Current: v${appVersion()} | New: ${atomicState?.appData?.updater?.versions?.app?.ver}\n\nTap to Open the IDE in your Mobile Browser...", state: "complete", image: getAppImg("update_icon.png")
            }
        }
        if(atomicState?.isInstalled) {
            section("Location & Devices:") {
                def devDesc = getDevicesDesc() ? "Nest Location: (${locationPresence().toString().capitalize()})\n\nCurrent Devices: ${getDevicesDesc()}\n\nTap to Modify..." : "Tap to Configure..."
                href "deviceSelectPage", title: "Location & Devices", description: devDesc, state: "complete", image: getAppImg("thermostat_icon.png")
            }
        }
        if(!atomicState?.isInstalled) {
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
            if (settings?.structures) {
                atomicState.structures = settings?.structures ?: null

                def stats = getNestThermostats()
                def statDesc = stats.size() ? "Found (${stats.size()}) Thermostats..." : "No Thermostats"
                LogAction("Thermostats: Found ${stats?.size()} (${stats})", "info", false)

                def coSmokes = getNestProtects()
                def coDesc = coSmokes.size() ? "Found (${coSmokes.size()}) Protects..." : "No Protects"
                LogAction("Protects: Found ${coSmokes.size()} (${coSmokes})", "info", false)

                def cams = getNestCameras()
                def camDesc = cams.size() ? "Found (${cams.size()}) Cameras..." : "No Cameras"
                LogAction("Cameras: Found ${cams.size()} (${cams})", "info", false)

                section("Select your Devices:") {
                    if (!stats?.size() && !coSmokes.size() && !cams.size()) { paragraph "No Devices were found..." }
                    if (stats?.size() > 0) {
                        input(name: "thermostats", title:"Nest Thermostats", type: "enum", required: false, multiple: true, submitOnChange: true, description: statDesc, metadata: [values:stats],
                                image: getAppImg("thermostat_icon.png"))
                    }
                    atomicState.thermostats =  settings?.thermostats ? statState(settings?.thermostats) : null
                    if (coSmokes.size() > 0) {
                        input(name: "protects", title:"Nest Protects", type: "enum", required: false, multiple: true, submitOnChange: true, description: coDesc, metadata: [values:coSmokes],
                                image: getAppImg("protect_icon.png"))
                    }
                    atomicState.protects = settings?.protects ? coState(settings?.protects) : null
                    if (cams.size() > 0) {
                        input(name: "cameras", title:"Nest Cameras", type: "enum", required: false, multiple: true, submitOnChange: true, description: camDesc, metadata: [values:cams],
                                image: getAppImg("camera_icon.png"))
                    }
                    atomicState.cameras = settings?.cameras ? camState(settings?.cameras) : null
                    input(name: "presDevice", title:"Add Presence Device?\n", type: "bool", description: "", default: false, required: false, submitOnChange: true, image: getAppImg("presence_icon.png"))
                    atomicState.presDevice = settings?.presDevice ?: null
                    input(name: "weatherDevice", title:"Add Weather Device?\n", type: "bool", description: "", default: false, required: false, submitOnChange: true, image: getAppImg("weather_icon.png"))
                    atomicState.weatherDevice = settings?.weatherDevice ?: null
                }
            }
        }
        if(atomicState?.isInstalled && atomicState?.structures && (atomicState?.thermostats || atomicState?.protects || atomicState?.cameras)) {
            def autoDesc = isAutoAppInst() ? "${getInstAutoTypesDesc()}\n\nTap to Modify..." : null
            section("Automations:") {
                href "automationsPage", title: "Automations...", description: (autoDesc ? autoDesc : "Tap to Configure..."), state: (autoDesc ? "complete" : null), image: getAppImg("automation_icon.png")
            }
        }
        if(atomicState?.isInstalled) {
            section("Preferences:") {
                def descStr = ""
                descStr += getAppNotifConfDesc() ?: ""
                descStr += getAppDebugDesc() ? "${getAppNotifConfDesc() ? "\n\n" : ""}${getAppDebugDesc() ?: ""}" : ""
                def prefDesc = (descStr != "") ? "${descStr}\n\nTap to Modify..." : "Tap to Configure..."
                href "prefsPage", title: "Preferences", description: prefDesc, state: ((pushStatus() || isAppDebug() || isChildDebug()) ? "complete" : null), image: getAppImg("settings_icon.png")
            }
            section("Help and Instructions") {
                href "infoPage", title: "Help, Info and Instructions", description: "Tap to view...", image: getAppImg("info.png")
            }
            if(atomicState?.isInstalled && atomicState?.structures && (atomicState?.thermostats || atomicState?.protects || atomicState?.weatherDevice)) {
                section("Diagnostics/Info:") {
                    href "nestInfoPage", title: "API | Diagnostics | Testing...", description: "Tap to view info...", image: getAppImg("api_diag_icon.png")
                }
            }
            section("  ") {
                href "uninstallPage", title: "Uninstall this App", description: "Tap to Remove...", image: getAppImg("uninstall_icon.png")
            }
        }
    }
}

def deviceSelectPage() {
    return dynamicPage(name: "deviceSelectPage", title: "Device Selection", nextPage: "startPage", install: false, uninstall: false) {
        def structs = getNestStructures()
        def structDesc = !structs?.size() ? "No Locations Found" : "Found (${structs?.size()}) Locations..."
        LogAction("Locations: Found ${structs?.size()} (${structs})", "info", false)
        if (atomicState?.thermostats || atomicState?.protects || atomicState?.cameras || atomicState?.presDevice || atomicState?.weatherDevice ) {  // if devices are configured, you cannot change the structure until they are removed
            section("Your Location:") {
                 paragraph "Location: ${structs[atomicState?.structures]}\n\n(Remove All Devices to Change!)", image: getAppImg("nest_structure_icon.png")
            }
        } else {
            section("Select your Location:") {
                input(name: "structures", title:"Nest Locations", type: "enum", required: true, multiple: false, submitOnChange: true, description: structDesc, metadata: [values:structs],
                        image: getAppImg("nest_structure_icon.png"))
            }
        }
        if (settings?.structures) {
            atomicState.structures = settings?.structures ?: null

            def stats = getNestThermostats()
            def statDesc = stats.size() ? "Found (${stats.size()}) Thermostats..." : "No Thermostats"
            LogAction("Thermostats: Found ${stats?.size()} (${stats})", "info", false)

            def coSmokes = getNestProtects()
            def coDesc = coSmokes.size() ? "Found (${coSmokes.size()}) Protects..." : "No Protects"
            LogAction("Protects: Found ${coSmokes.size()} (${coSmokes})", "info", false)

            def cams = getNestCameras()
            def camDesc = cams.size() ? "Found (${cams.size()}) Cameras..." : "No Cameras"
            LogAction("Cameras: Found ${cams.size()} (${cams})", "info", false)

            section("Select your Devices:") {
                if (!stats?.size() && !coSmokes.size() && !cams?.size()) { paragraph "No Devices were found..." }
                if (stats?.size() > 0) {
                    input(name: "thermostats", title:"Nest Thermostats", type: "enum", required: false, multiple: true, submitOnChange: true, description: statDesc, metadata: [values:stats],
                            image: getAppImg("thermostat_icon.png"))
                }
                atomicState.thermostats =  settings?.thermostats ? statState(settings?.thermostats) : null
                if (coSmokes.size() > 0) {
                    input(name: "protects", title:"Nest Protects", type: "enum", required: false, multiple: true, submitOnChange: true, description: coDesc, metadata: [values:coSmokes],
                            image: getAppImg("protect_icon.png"))
                }
                atomicState.protects = settings?.protects ? coState(settings?.protects) : null
                if (cams.size() > 0) {
                    input(name: "cameras", title:"Nest Cameras", type: "enum", required: false, multiple: true, submitOnChange: true, description: camDesc, metadata: [values:cams],
                            image: getAppImg("camera_icon.png"))
                }
                atomicState.cameras = settings?.cameras ? camState(settings?.cameras) : null
                input(name: "presDevice", title:"Add Presence Device?\n", type: "bool", description: "", default: false, required: false, submitOnChange: true, image: getAppImg("presence_icon.png"))
                atomicState.presDevice = settings?.presDevice ?: null
                input(name: "weatherDevice", title:"Add Weather Device?\n", type: "bool", description: "", default: false, required: false, submitOnChange: true, image: getAppImg("weather_icon.png"))
                atomicState.weatherDevice = settings?.weatherDevice ?: null
            }
        }
    }
}

def reviewSetupPage() {
    return dynamicPage(name: "reviewSetupPage", title: "Setup Review", install: true, uninstall: atomicState?.isInstalled) {
        if(!atomicState?.newSetupComplete) { atomicState.newSetupComplete = true }
        atomicState?.setupVersion = atomicState?.appData?.updater?.setupVersion?.toInteger() ?: 0
        section("Device Summary:") {
            def str = ""
            str += !atomicState?.isInstalled ? "Devices to Install:" : "Installed Devices:"
            str += getDevicesDesc() ?: ""
            paragraph "${str}"
            if(atomicState?.weatherDevice) {
                if(!getStZipCode() || getStZipCode() != getNestZipCode()) {
                    href "custWeatherPage", title: "Customize Weather Location?", description: "Tap to configure...", image: getAppImg("weather_icon_grey.png")
                }
            }
            if(!atomicState?.isInstalled && (settings?.thermostats || settings?.protects || settings?.cameras || settings?.presDevice || settings?.weatherDevice)) {
                href "devNamePage", title: "Customize Device Names?", description: atomicState?.custLabelUsed ? "Tap to Modify..." : "Tap to configure...", state: (atomicState?.custLabelUsed ? "complete" : null), image: getAppImg("device_name_icon.png")
            }
            if(settings?.weatherDevice) {
                input ("weathAlertNotif", "bool", title: "Notify on Weather Alerts?", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("weather_icon.png"))
            }
        }
        section("Notifications:") {
            href "notifPrefPage", title: "Notifications", description: (getAppNotifConfDesc() ? "${getAppNotifConfDesc()}\n\nTap to modify..." : "Tap to configure..."), state: (getAppNotifConfDesc() ? "complete" : null), image: getAppImg("notification_icon.png")
        }
        section("Polling:") {
            href "pollPrefPage", title: "Polling Preferences", description: "${getPollingConfDesc()}\n\nTap to configure...", state: (pollStatus != "Not Active" ? "complete" : null), image: getAppImg("timer_icon.png")
        }
        section("Share Data with Developer:") {
            paragraph "These options will send the developer non-identifiable app information as well as error data to help diagnose issues quicker and catch trending issues."
            input ("optInAppAnalytics", "bool", title: "Send Install Data?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("app_analytics_icon.png"))
            input ("optInSendExceptions", "bool", title: "Send Error Data?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("diag_icon.png"))
            if (settings?.optInAppAnalytics != false) {
                input(name: "mobileClientType", title:"Primary Mobile Device?", type: "enum", required: true, submitOnChange: true, metadata: [values:["android":"Android", "ios":"iOS", "winphone":"Windows Phone"]],
                                image: getAppImg("${(settings?.mobileClientType && settings?.mobileClientType != "decline") ? "${settings?.mobileClientType}_icon" : "mobile_device_icon"}.png"))
                href url: getAppEndpointUrl("renderInstallData"), style:"embedded", title:"View the Data that will be Shared with the Developer", description: "Tap to view Data...", required:false, image: getAppImg("view_icon.png")
            }
        }
        if(atomicState?.showHelp) {
            section(" ") {
                href "infoPage", title: "Help, Info and Instructions", description: "Tap to view...", image: getAppImg("info.png")
            }
        }
        if(!atomicState?.isInstalled) {
            section("  ") {
                href "uninstallPage", title: "Uninstall this App", description: "Tap to Remove...", image: getAppImg("uninstall_icon.png")
            }
        }
    }
}

//Defines the Preference Page
def prefsPage() {
    def devSelected = (atomicState?.structures && (atomicState?.thermostats || atomicState?.protects || atomicState?.cameras || atomicState?.presDevice || atomicState?.weatherDevice))
    dynamicPage(name: "prefsPage", title: "Application Preferences", nextPage: "", install: false, uninstall: false ) {
        section("Polling:") {
            href "pollPrefPage", title: "Polling Preferences", description: "${getPollingConfDesc()}\n\nTap to configure...", state: (pollStatus != "Not Active" ? "complete" : null), image: getAppImg("timer_icon.png")
        }
        if(devSelected) {
            section("Devices:") {
                href "devPrefPage", title: "Device Customization", description: (devCustomizePageDesc() ? "${devCustomizePageDesc()}\n\nTap to Modify..." : "Tap to configure..."),
                        state: (devCustomizePageDesc() ? "complete" : null), image: getAppImg("device_pref_icon.png")
            }
        }
        section("Notifications:") {
            href "notifPrefPage", title: "Notifications", description: (getAppNotifConfDesc() ? "${getAppNotifConfDesc()}\n\nTap to modify..." : "Tap to configure..."), state: (getAppNotifConfDesc() ? "complete" : null),
                    image: getAppImg("notification_icon.png")
        }
        section("Logging:") {
            href "debugPrefPage", title: "Logging", description: (getAppDebugDesc() ? "${getAppDebugDesc() ?: ""}\n\nTap to modify..." : "Tap to configure..."), state: ((isAppDebug() || isChildDebug()) ? "complete" : null),
                    image: getAppImg("log.png")
        }
        section("Share Data with Developer:") {
            paragraph "These options will send the developer non-identifiable app information as well as error data to help diagnose issues quicker and catch trending issues."
            input ("optInAppAnalytics", "bool", title: "Opt In App Analytics?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("app_analytics_icon.png"))
            input ("optInSendExceptions", "bool", title: "Opt In Send Errors?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("diag_icon.png"))
            input(name: "mobileClientType", title:"Primary Mobile Device?", type: "enum", required: false, submitOnChange: true, metadata: [values:["android":"Android", "ios":"iOS", "winphone":"Windows Phone"]],
                                image: getAppImg("${(settings?.mobileClientType && settings?.mobileClientType != "decline") ? "${settings?.mobileClientType}_icon" : "mobile_device_icon"}.png"))
        }
        section ("Misc. Options:") {
            input ("useMilitaryTime", "bool", title: "Use Military Time (HH:mm)?", description: "", defaultValue: false, submitOnChange: true, required: false, image: getAppImg("military_time_icon.png"))
            input ("disAppIcons", "bool", title: "Disable App Icons?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("no_icon.png"))
            atomicState.needChildUpd = true
        }
        section("Nest Login:") {
            href "nestLoginPrefPage", title: "Nest Login Preferences", description: "Tap to configure...", image: getAppImg("login_icon.png")
        }
        section("Change the Name of the App:") {
            label title:"Application Label (optional)", required:false
        }
    }
}

def automationsPage() {
    return dynamicPage(name: "automationsPage", title: "", nextPage: !parent ? "startPage" : "automationsPage", install: false) {
        def autoApp = findChildAppByName( appName() )
        if(autoApp) {
            section("Installed Automations...") { }
        } else {
            section("") {
                paragraph "You haven't created any Automations yet!!!\nTap Create New Automation to get Started..."
            }
        }
        section("Add a new Automation:") {
            app(name: "autoApp", appName: appName(), namespace: "tonesto7", multiple: true, title: "Create New Automation...", image: getAppImg("automation_icon.png"))
            def rText = "NOTICE:\nAutomations is still in BETA!!!\n" +
                        "We are not responsible for any damages caused by using this SmartApp.\n\n               USE AT YOUR OWN RISK!!!"
            paragraph "${rText}"//, required: true, state: null
        }
        if(isAutoAppInst()) {
            section("Automation Statistics:") {
                href "automationStatisticsPage", title: "View Automation Statistics", description: "Tap to view...", image: getAppImg("app_analytics_icon.png")
            }
        }
        section("Global Automation Preferences:") {
            def descStr = ""
            descStr += (settings?.locDesiredCoolTemp || settings?.locDesiredHeatTemp) ? "Comfort Settings:" : ""
            descStr += settings?.locDesiredHeatTemp ? "\n • Desired Heat Temp: (${settings?.locDesiredHeatTemp}°${getTemperatureScale()})" : ""
            descStr += settings?.locDesiredCoolTemp ? "\n • Desired Cool Temp: (${settings?.locDesiredCoolTemp}°${getTemperatureScale()})" : ""
            //descStr += (settings?.comfortDewpointMax) ? "${(settings?.locDesiredCoolTemp || settings?.locDesiredHeatTemp) ? "\n\n" : ""}Dew Point:" : ""
            descStr += settings?.comfortDewpointMax ? "\n • Max Dew Point: (${settings?.comfortDewpointMax}${getTemperatureScale()})" : ""
            //descStr += "${(settings?.locDesiredCoolTemp || settings?.locDesiredHeatTemp) ? "\n\n" : ""}${getSafetyValuesDesc()}" ?: ""
            def prefDesc = (descStr != "") ? "${descStr}\n\nTap to Modify..." : "Tap to Configure..."
            href "automationGlobalPrefsPage", title: "Global Automation Preferences", description: prefDesc, state: (descStr != "" ? "complete" : null), image: getAppImg("global_prefs_icon.png")
        }
        section("Automation Repair:") {
            href "automationKickStartPage", title: "Re-Initialize All Automations", description: "Tap to call the Update() action on each automation.\nTap to Begin...", image: getAppImg("reset_icon.png")
        }
    }
}

def automationStatisticsPage() {
    dynamicPage(name: "automationStatisticsPage", title: "Installed Automations Stats\n(Auto-Refresh Every 20 sec.)", refreshInterval: 20, uninstall: false) {
        def cApps = getChildApps()
        if(cApps) {
            cApps?.sort()?.each { chld ->
                def autoType = chld?.getAutomationType()
                if(autoType != "watchDog") {
                    section("${chld?.label} Stats:") {
                        def data = chld?.getAutomationStats()
                        def tf = new SimpleDateFormat("M/d/yyyy - h:mm a")
                            tf.setTimeZone(getTimeZone())
                        def lastModDt = data?.lastUpdatedDt ? tf.format(Date.parse("E MMM dd HH:mm:ss z yyyy", data?.lastUpdatedDt.toString())) : null
                        def lastEvtDt = data?.lastEvent?.date ? tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", data?.lastEvent?.date.toString())) : null
                        def lastActionDt = data?.lastActionData?.dt ? tf.format(Date.parse("E MMM dd HH:mm:ss z yyyy", data?.lastActionData?.dt.toString())) : null
                        def lastEvalDt = data?.lastEvalDt ? tf.format(Date.parse("E MMM dd HH:mm:ss z yyyy", data?.lastEvalDt.toString())) : null
                        def lastSchedDt = data?.lastSchedDt ? tf.format(Date.parse("E MMM dd HH:mm:ss z yyyy", data?.lastSchedDt.toString())) : null
                        def lastExecVal = data?.lastExecVal ?: null
                        def execAvgVal = data?.execAvgVal ?: null

                        def str = ""
                        str += lastModDt ? " • Last Modified:\n  └ (${lastModDt})" : "\n • Last Modified: (Not Available)"
                        str += lastEvtDt ? "\n\n • Last Event:" : ""
                        str += lastEvtDt ? "${(data?.lastEvent?.displayName.length() > 10) ? "\n  │ Dev:\n  │└ " : "\n  ├ Dev: "}${data?.lastEvent?.displayName}" : ""
                        str += lastEvtDt ? "\n  ├ Type: (${data?.lastEvent?.name.toString().capitalize()})" : ""
                        str += lastEvtDt ? "\n  ├ Value: (${data?.lastEvent?.value}${data?.lastEvent?.unit ? "${data?.lastEvent?.unit}" : ""})" : ""
                        str += lastEvtDt ? "\n  └ DateTime: (${lastEvtDt})" : "\n\n • Last Event: (Not Available)"
                        str += lastEvalDt ? "\n\n • Last Evaluation:\n  └ (${lastEvalDt})" : "\n\n • Last Evaluation: (Not Available)"
                        str += lastSchedDt ? "\n\n • Last Schedule:\n  └ (${lastSchedDt})" : "\n\n • Last Schedule: (Not Available)"
                        str += lastActionDt ? "\n\n • Last Action:\n  ├ DateTime: (${lastActionDt})\n  └ Action: ${data?.lastActionData?.actionDesc}" : "\n\n • Last Action: (Not Available)"
                        str += lastExecVal ? "\n\n • Execution History:\n  ${execAvgVal ? "├" : "└"} Last: (${lastExecVal} ms)${execAvgVal ? "\n  └ Avg: (${execAvgVal} ms)" : ""}" : "\n\n • Execution History: (Not Available)"
                        paragraph "${str}", state: "complete", image: getAutoIcon(autoType)
                    }
                }
                else if (autoType == "watchDog") {
                    section("") {
                        paragraph "No Valid Automations Installed..."
                    }
                }
            }
        }
    }
}

def automationKickStartPage() {
    dynamicPage(name: "automationKickStartPage", title: "This Page is running Update() on all of your installed Automations", nextPage: "automationsPage", install: false, uninstall: false) {
        def cApps = getChildApps()
        section("Running Update All Automations:") {
            if(cApps) {
                cApps?.sort()?.each { chld ->
                    chld?.update()
                    paragraph "${chld?.label}\n\nUpdate() Completed Successfully!!!", state: "complete"
                }
            } else {
                paragraph "No Automations Found..."
            }
        }
    }
}

def automationGlobalPrefsPage() {
    dynamicPage(name: "automationGlobalPrefsPage", title: "", nextPage: "", install: false) {
        if(atomicState?.thermostats) {
            section("Comfort Preferences:") {
                input "locDesiredHeatTemp", "decimal", title: "Desired Global Heat Temp (°${getTemperatureScale()})", range: (getTemperatureScale() == "C") ? "10..32" : "50..90",
                        submitOnChange: true, required: false, image: getAppImg("heat_icon.png")
                input "locDesiredCoolTemp", "decimal", title: "Desired Global Cool Temp (°${getTemperatureScale()})", range: (getTemperatureScale() == "C") ? "10..32" : "50..90",
                        submitOnChange: true, required: false, image: getAppImg("cool_icon.png")

                def tRange = (getTemperatureScale() == "C") ? "15..19" : "60..66"
                def wDev = getChildDevice(getNestWeatherId())
                def curDewPnt = wDev ? "${wDev?.currentDewpoint}°${getTemperatureScale()}" : 0
                input "comfortDewpointMax", "decimal", title: "Max. Dewpoint Allowed (${tRange} °${getTemperatureScale()})", required: false,  range: trange,
                        description: "Current Dew Point: (${curDewPnt})", submitOnChange: true, image: getAppImg("dewpoint_icon.png")
                href url: "https://en.wikipedia.org/wiki/Dew_point#Relationship_to_human_comfort", style:"embedded", title: "What is Dew Point?",
                        description:"", image: getAppImg("instruct_icon.png")
            }
            section("Safety Preferences:") {
                href "safetyValuesPage", title: "Configure Safety Values?", description: (getSafetyValuesDesc() ? "${getSafetyValuesDesc()}\n\nTap to Modify..." : " Tap to configure..."),
                state: (getSafetyValuesDesc() ? "complete" : null), image: getAppImg("thermostat_icon.png")
            }
        }
    }
}

def safetyValuesPage() {
    dynamicPage(name: "safetyValuesPage", title: "Configure Location Safety Values", uninstall: false) {
        if(atomicState?.thermostats) {
            atomicState?.thermostats?.each { ts ->
                def dev = getChildDevice(ts?.key)
                def canHeat = dev?.currentState("canHeat")?.stringValue == "false" ? false : true
                def canCool = dev?.currentState("canCool")?.stringValue == "false" ? false : true
                // need to ensure they are not the same (if not 0)
                section("${dev?.displayName} - Safety Values:") {
                    def srange = (getTemperatureScale() == "C") ? "10..32" : "50..90"
                    if(canHeat) {
                        input "${dev?.deviceNetworkId}_safety_temp_min", "decimal", title: "Min. Temp Allowed (${srange} °${getTemperatureScale()})", range: srange,
                                submitOnChange: true, required: false, image: getAppImg("heat_icon.png")
                    }
                    if(canCool) {
                        input "${dev?.deviceNetworkId}_safety_temp_max", "decimal", title: "Max. Temp Allowed (${srange} °${getTemperatureScale()})", range: srange,
                                submitOnChange: true, required: false,  image: getAppImg("cool_icon.png")
                    }
                    /*def hrange = "10..80"
                    input "${dev?.deviceNetworkId}_comfort_humidity_max", "number", title: "Max. Humidity Allowed (${hrange} %)", required: false,  range: hrange,
                            submitOnChange: true, image: getAppImg("humidity_icon.png")*/
                }
            }
        }
    }
}

def getSafetyValuesDesc() {
    def str = ""
    def tstats = atomicState?.thermostats
    if(tstats) {
        tstats?.each { ts ->
            def minTemp = settings?."${ts?.key}_safety_temp_min" ?: 0.0
            def maxTemp = settings?."${ts?.key}_safety_temp_max" ?: 0.0
            //def maxHum = settings?."${ts?.key}_comfort_humidity_max" ?: 80
            def maxDew = settings?.comfortDewpointMax ?: 0.0
            str += (ts && (minTemp || maxTemp)) ? "(${ts?.value}) Safety Values:" : ""
            str += minTemp ? "\n • Min. Temp: (${minTemp}°${getTemperatureScale()})" : ""
            str += maxTemp ? "\n • Max. Temp: (${maxTemp}°${getTemperatureScale()})" : ""
            //str += maxHum ? "\n • Max. Humidity: (${maxHum}%)" : ""
            str += (ts && (maxDew)) ? "\n\n(${ts?.value}) Comfort Values:" : ""
            str += maxDew ? "\n • Max. Dewpoint: (${maxDew}°${getTemperatureScale()})" : ""
            str += tstats?.size() > 1 ? "\n\n" : ""
        }
    }
    return (str != "") ? "${str}" : null
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
    if(parent) {
        atomicState?.lastUpdatedDt = getDtNow()
    }
}

def uninstalled() {
    log.debug "uninstalled..."
    if(parent) {
        uninstAutomationApp()
    } else {
        uninstManagerApp()
    }
    sendNotificationEvent("${textAppName()} is uninstalled...")
}

def initialize() {
    //log.debug "initialize..."
    if(parent) {
        initAutoApp()
    }
    else {
        initWatchdogApp()
        initManagerApp()
    }
}

def initManagerApp() {
    setStateVar()
    unschedule()
    unsubscribe()
    atomicState.pollingOn = false
    atomicState.lastChildUpdDt = null // force child update on next poll
    atomicState.lastForcePoll = null
    if (addRemoveDevices()) { // if we changed devices, reset queues and polling
        atomicState.cmdQlist = []
    }
    if(settings?.thermostats || settings?.protects || settings?.cameras || settings?.presDevice || settings?.weatherDevice) {
        atomicState?.isInstalled = true
    } else { atomicState.isInstalled = false }
    subscriber()
    setPollingState()
    //If analytics are enabled this will send non-user identifiable data to firebase server
    if (optInAppAnalytics) { runIn(4, "sendInstallData", [overwrite: true]) }
    runIn(20, "stateCleanup", [overwrite: true])
}

def uninstManagerApp() {
    log.trace "uninstManagerApp"
    try {
        if(addRemoveDevices(true)) {
            //removes analytic data from the server
            if (optInAppAnalytics) {
                if(removeInstallData()) {
                    atomicState?.installationId = null
                }
            }
            //Revokes Smartthings endpoint token...
            revokeAccessToken()
            //Revokes Nest Auth Token
            if(atomicState?.authToken) { revokeNestToken() }
            //sends notification of uninstall
            sendNotificationEvent("${textAppName()} is uninstalled...")
        }
    } catch (ex) {
        log.error "uninstManagerApp Exception: ${ex}", ex
        sendExceptionData(ex.message, "uninstManagerApp")
    }
}

def initWatchdogApp() {
    //log.trace "initWatchdogApp"
    def watDogCnt = 0
    def watDogApp
    childApps?.each { cApp ->
        if(cApp?.getAutomationType() == "watchDog") {
            watDogCnt = watDogCnt+1
            watDogApp = cApp
        }
    }
    if(watDogCnt <= 0) {
        //log.debug "adding New WatchDogApp..."
        addChildApp(textNamespace(), appName(), getWatchdogAppChildName(), [settings:[watchDogFlag: true]])
    } else {
        //log.debug "updating watDogApp..."
        watDogApp?.update()
    }
    //log.debug "watDogCnt: $watDogCnt | watDogApp: $watDogApp"
}

def getChildAppVer(appName) { return appName?.appVersion() ? "v${appName?.appVersion()}" : "" }

def appBtnDesc(val) {
    return atomicState?.automationsActive ? (atomicState?.automationsActiveDesc ? "${atomicState?.automationsActiveDesc}\nTap to Modify..." : "Tap to Modify...") :  "Tap to Install..."
}

def isAutoAppInst() {
    def chldCnt = 0
    childApps?.each { cApp ->
//        if(cApp?.name != getWatchdogAppChildName()) { chldCnt = chldCnt + 1 }
        chldCnt = chldCnt + 1
    }
    return (chldCnt > 0) ? true : false
}

def autoAppInst(Boolean val) {
    log.debug "${getAutoAppChildName()} is Installed?: ${val}"
    atomicState.autoAppInstalled = val
}

def getInstAutoTypesDesc() {
    def remSenCnt = 0
    def fanCtrlCnt = 0
    def conWatCnt = 0
    def leakWatCnt = 0
    def extTmpCnt = 0
    def nModeCnt = 0
    def tModeCnt = 0
    def watchDogCnt = 0
    def disCnt = 0
    childApps?.each { a ->
        def type = a?.getAutomationType()
        if(a?.getIsAutomationDisabled()) { disCnt = disCnt+1 }
        else {
            //log.debug "automation type: $type"
            switch(type) {
                case "remSen":
                    remSenCnt = remSenCnt+1
                    break
                case "fanCtrl":
                    fanCtrlCnt = fanCtrlCnt+1
                    break
                case "conWat":
                    conWatCnt = conWatCnt+1
                    break
                case "leakWat":
                    leakWatCnt = leakWatCnt+1
                    break
                case "extTmp":
                    extTmpCnt = extTmpCnt+1
                    break
                case "nMode":
                    nModeCnt = nModeCnt+1
                    break
                case "tMode":
                    tModeCnt = tModeCnt+1
                    break
                case "watchDog":
                    watchDogCnt = watchDogCnt+1
                    break
            }
        }
    }
    atomicState?.installedAutomations = ["remoteSensor":remSenCnt, "contact":conWatCnt, "leak":leakWatCnt, "fanCtrl":fanCtrlCnt, "externalTemp":extTmpCnt, "nestMode":nModeCnt, "tstatMode":tModeCnt, "watchDog":watchDogCnt]

    def str = ""
    str += "Installed Automations:"
    str += (watchDogCnt > 0) ? "\n• Nest Watchdog: (Active)" : ""
    str += (remSenCnt > 0) ? "\n• Remote Sensor ($remSenCnt)" : ""
    str += (fanCtrlCnt > 0) ? "\n• Fan Control ($fanCtrlCnt)" : ""
    str += (conWatCnt > 0) ? "\n• Contact Sensor ($conWatCnt)" : ""
    str += (leakWatCnt > 0) ? "\n• Leak Sensor ($leakWatCnt)" : ""
    str += (extTmpCnt > 0) ? "\n• External Sensor ($extTmpCnt)" : ""
    str += (nModeCnt > 0) ? "\n• Nest Modes ($nModeCnt)" : ""
    str += (tModeCnt > 0) ? "\n• Tstat Modes ($tModeCnt)" : ""
    str += (disCnt > 0) ? "\n\nDisabled Automations ($disCnt)" : ""
    return str
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
    if (!atomicState?.thermostats && !atomicState?.protects && !atomicState?.weatherDevice && !atomicState?.cameras) {
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
            if(atomicState?.weatherDevice) { weatherTimer = (settings?.pollWeatherValue ? settings?.pollWeatherValue.toInteger() : 900) }
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
        //unschedule("postCmd")
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
        if (atomicState?.pollBlocked) { schedNextWorkQ(null); return }
        if (dev || str || atomicState?.needChildUpd ) { updateChildData() }

        updateWebStuff(force)
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
        atomicState?.lastWebUpdDt = null
        atomicState?.lastWeatherUpdDt = null
        atomicState?.lastForecastUpdDt = null
        schedNextWorkQ(null)
    } else {
        LogAction("Too Soon to Force Data Update!!!!  It's only been (${lastFrcdPoll}) seconds of the minimum (${settings?.pollWaitVal})...", "debug", true)
        atomicState.needStrPoll = true
        atomicState.needDevPoll = true
    }
    updateChildData(true)
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
                if(resp?.status == 200) {
                    LogTrace("API Structure Resp.Data: ${resp?.data}")
                    apiIssueEvent(false)
                    if(!resp?.data?.equals(atomicState?.structData) || !atomicState?.structData) {
                        LogAction("API Structure Data HAS Changed... Updating State data...", "debug", true)
                        atomicState?.structData = resp?.data
                        result = true
                    }
                } else {
                    LogAction("getApiStructureData - Received a diffent Response than expected: Resp (${resp?.status})", "error", true)
                }
            }
        }
        else if(type == "dev") {
            atomicState?.lastDevDataUpd = getDtNow()
            atomicState?.needDevPoll = false
            httpGet(params) { resp ->
                if(resp?.status == 200) {
                    LogTrace("API Device Resp.Data: ${resp?.data}")
                    apiIssueEvent(false)
                    if(!resp?.data.equals(atomicState?.deviceData) || !atomicState?.deviceData) {
                        LogAction("API Device Data HAS Changed... Updating State data...", "debug", true)
                        atomicState?.deviceData = resp?.data
                        result = true
                    }
                } else {
                    LogAction("getApiDeviceData - Received a diffent Response than expected: Resp (${resp?.status})", "error", true)
                }
            }
        }
    }
    catch(ex) {
        apiIssueEvent(true)
        atomicState.needChildUpd = true
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            if (ex.message.contains("Too Many Requests")) {
                log.warn "Received '${ex.message}' response code..."
            }
        } else {
            log.error "getApiData (type: $type) Exception: ${ex}", ex
            if(type == "str") { atomicState.needStrPoll = true }
            else if(type == "dev") { atomicState?.needDevPoll = true }
        }
        sendExceptionData(ex.message, "getApiData")
    }
    return result
}

def schedUpdateChild() {
    runIn(25, "updateChildData", [overwrite: true])
}

def generateMD5_A(String s) {
    MessageDigest digest = MessageDigest.getInstance("MD5")
    digest.update(s.bytes)
    //new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
    return digest.digest().toString()
}

def updateChildData(force = false) {
    LogAction("updateChildData()", "info", true)
    if (atomicState?.pollBlocked) { return }
    def nforce = atomicState?.needChildUpd
    atomicState.needChildUpd = true
    //log.warn "force: $force   nforce: $nforce"
    //unschedule("schedUpdateChild")
    //runIn(40, "postCmd", [overwrite: true])
    try {
        atomicState?.lastChildUpdDt = getDtNow()
        def useMt = !useMilitaryTime ? false : true
        def dbg = !childDebug ? false : true
        def nestTz = getNestTimeZone()?.toString()
        def api = !apiIssues() ? false : true
        def htmlInfo = getHtmlInfo()
        def allowDbException = allowDbException()
        getAllChildDevices()?.each {
            def devId = it?.deviceNetworkId
            if(atomicState?.thermostats && atomicState?.deviceData?.thermostats[devId]) {
                def safetyTemps = [ "min":(settings?."${devId}_safety_temp_min" ?: 0.0), "max":(settings?."${devId}_safety_temp_max" ?: 0.0) ]
                //def comfortHumidity = [ "min":(settings?."${devId}_safety_humidity_min" ?: 0), "max":(settings?."${devId}_comfort_humidity_max" ?: 0) ]
                def comfortHumidity = settings?."${devId}_comfort_humidity_max" ?: 80
                def comfortDewpoint = settings?.comfortDewpointMax ?: 0.0
                atomicState?.tDevVer = it?.devVer() ?: ""
                if(!atomicState?.tDevVer || (versionStr2Int(atomicState?.tDevVer) >= minDevVersions()?.thermostat)) {
                    def tData = ["data":atomicState?.deviceData?.thermostats[devId], "mt":useMt, "debug":dbg, "tz":nestTz, "apiIssues":api, "safetyTemps":safetyTemps, "comfortHumidity":comfortHumidity,
                                "comfortDewpoint":comfortDewpoint, "pres":locationPresence(), "childWaitVal":getChildWaitVal().toInteger(), "htmlInfo":htmlInfo, "allowDbException":allowDbException,
                                "latestVer":latestTstatVer()?.ver?.toString()]
                    def oldTstatData = atomicState?."oldTstatData${devId}"
                    def tDataChecksum = generateMD5_A(tData.toString())
                    atomicState."oldTstatData${devId}" = tDataChecksum
                    tDataChecksum = atomicState."oldTstatData${devId}"
                    if (force || nforce || (oldTstatData != tDataChecksum)) {
                        LogTrace("UpdateChildData >> Thermostat id: ${devId} | data: ${tData}")
                        //log.warn "oldTstatData: ${oldTstatData} tDataChecksum: ${tDataChecksum} force: $force  nforce: $nforce"
                        it.generateEvent(tData) //parse received message from parent
                    }
                    return true
                } else {
                    LogAction("The Manager App will not send data to the Thermostat device because the device version (${versionStr2Int(atomicState?.tDevVer)}) is lower than the Minimum (v${minDevVersions()?.thermostat})... Please Update Thermostat Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(!atomicState?.pDevVer || (atomicState?.protects && atomicState?.deviceData?.smoke_co_alarms[devId])) {
                atomicState?.pDevVer = it?.devVer() ?: ""
                if(!atomicState?.pDevVer || (versionStr2Int(atomicState?.pDevVer) >= minDevVersions()?.protect)) {
                    def pData = ["data":atomicState?.deviceData?.smoke_co_alarms[devId], "mt":useMt, "debug":dbg, "showProtActEvts":(!showProtActEvts ? false : true),
                                "tz":nestTz, "htmlInfo":htmlInfo, "apiIssues":api, "allowDbException":allowDbException, "latestVer":latestProtVer()?.ver?.toString()]
                    def oldProtData = atomicState?."oldProtData${devId}"
                    def pDataChecksum = generateMD5_A(pData.toString())
                    atomicState."oldProtData${devId}" = pDataChecksum
                    pDataChecksum = atomicState."oldProtData${devId}"
                    if (force || nforce || (oldProtData != pDataChecksum)) {
                        LogTrace("UpdateChildData >> Protect id: ${devId} | data: ${pData}")
                        //log.warn "oldProtData: ${oldProtData} pDataChecksum: ${pDataChecksum} force: $force  nforce: $nforce"
                        it.generateEvent(pData) //parse received message from parent
                    }
                    return true
                } else {
                    LogAction("The Manager App will not send data to the Protect device because the device version (${versionStr2Int(atomicState?.pDevVer)}) is lower than the Minimum (v${minDevVersions()?.protect})... Please Update Protect Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(atomicState?.cameras && atomicState?.deviceData?.cameras[devId]) {
                atomicState?.camDevVer = it?.devVer() ?: ""
                if(!atomicState?.camDevVer || (versionStr2Int(atomicState?.camDevVer) >= minDevVersions()?.camera)) {
                    def camData = ["data":atomicState?.deviceData?.cameras[devId], "mt":useMt, "debug":dbg,
                                "tz":nestTz, "htmlInfo":htmlInfo, "apiIssues":api, "allowDbException":allowDbException, "latestVer":latestCamVer()?.ver?.toString()]
                    def oldCamData = atomicState?."oldCamData${devId}"
                    def cDataChecksum = generateMD5_A(camData.toString())
                    if (force || nforce || (oldCamData != cDataChecksum)) {
                        LogTrace("UpdateChildData >> Camera id: ${devId} | data: ${camData}")
                        it.generateEvent(camData) //parse received message from parent
                        atomicState."oldCamData${devId}" = cDataChecksum
                    }
                    return true
                } else {
                    LogAction("The Manager App will not send data to the Camera device because the device version (${versionStr2Int(atomicState?.camDevVer)}) is lower than the Minimum (v${minDevVersions()?.camera})... Please Update Camera Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(atomicState?.presDevice && devId == getNestPresId()) {
                atomicState?.presDevVer = it?.devVer() ?: ""
                if(!atomicState?.presDevVer || (versionStr2Int(atomicState?.presDevVer) >= minDevVersions()?.presence)) {
                    def pData = ["debug":dbg, "tz":nestTz, "mt":useMt, "pres":locationPresence(), "apiIssues":api, "allowDbException":allowDbException, "latestVer":latestPresVer()?.ver?.toString()]
                    def oldPresData = atomicState?."oldPresData${devId}"
                    def pDataChecksum = generateMD5_A(pData.toString())
                    atomicState."oldPresData${devId}" = pDataChecksum
                    pDataChecksum = atomicState."oldPresData${devId}"
                    if (force || nforce || (oldPresData != pDataChecksum)) {
                        LogTrace("UpdateChildData >> Presence id: ${devId}")
                        //log.warn "oldPresData: ${oldPresData} pDataChecksum: ${pDataChecksum} force: $force  nforce: $nforce"
                        it.generateEvent(pData)
                    }
                    return true
                } else {
                    LogAction("The Manager App will not send data to the Presence device because the device version (${versionStr2Int(atomicState?.presDevVer)}) is lower than the Minimum (v${minDevVersions()?.presence})... Please Update Presence Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(atomicState?.weatherDevice && devId == getNestWeatherId()) {
                atomicState?.weatDevVer = it?.devVer() ?: ""
                if(!atomicState?.weatDevVer || (versionStr2Int(atomicState?.weatDevVer) >= minDevVersions()?.weather)) {
                    def wData = ["weatCond":getWData(), "weatForecast":getWForecastData(), "weatAstronomy":getWAstronomyData(), "weatAlerts":getWAlertsData()]
                    def oldWeatherData = atomicState?."oldWeatherData${devId}"
                    def wDataChecksum = generateMD5_A(wData.toString())
                    atomicState."oldWeatherData${devId}" = wDataChecksum
                    wDataChecksum = atomicState."oldWeatherData${devId}"
                    if (force || nforce || (oldWeatherData != wDataChecksum)) {
                        //log.warn "oldWeatherData: ${oldWeatherData} wDataChecksum: ${wDataChecksum} force: $force  nforce: $nforce"
                        LogTrace("UpdateChildData >> Weather id: ${devId}")
                        it.generateEvent(["data":wData, "tz":nestTz, "mt":useMt, "debug":dbg, "apiIssues":api, "htmlInfo":htmlInfo, "allowDbException":allowDbException, "weathAlertNotif":weathAlertNotif, "latestVer":latestWeathVer()?.ver?.toString()])
                    }
                    return true
                } else {
                    LogAction("The Manager App will not send data to the Weather device because the device version (${versionStr2Int(atomicState?.weatDevVer)}) is lower than the Minimum (v${minDevVersions()?.weather})... Please Update Weather Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(devId == getNestPresId()) {
                return true
            }
            else if(devId == getNestWeatherId()) {
                return true
            }
            else if(!atomicState?.deviceData?.thermostats[devId] && !atomicState?.deviceData?.smoke_co_alarms[devId] && !atomicState?.deviceData?.cameras[devId]) {
                LogAction("Device connection removed? no data for ${devId}", "warn", true)
                return null
            }
            else {
                LogAction("updateChildData() for ${devId} after polling", "error", true)
                return null
            }
        }
        atomicState.needChildUpd = false
    }
    catch (ex) {
        log.error "updateChildData Exception: ${ex}", ex
        sendExceptionData(ex.message, "updateChildData")
        atomicState?.lastChildUpdDt = null
        return
    }
    //unschedule("postCmd")
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
    def result = state?.apiIssuesList.toString().contains("true") ? true : false
    if(result) {
        LogAction("Nest API Issues Detected... (${getDtNow()})", "warn", true)
    }
    return result
}

def apiIssueEvent(issue, cmd = null) {
    def list = state?.apiIssuesList ?: []
    //log.debug "listIn: $list (${list?.size()})"
    def listSize = 3
    if(list?.size() < listSize) {
        list.push(issue)
    }
    else if (list?.size() > listSize) {
        def nSz = (list?.size()-listSize) + 1
        //log.debug ">listSize: ($nSz)"
        def nList = list?.drop(nSz)
        //log.debug "nListIn: $list"
        nList?.push(issue)
        //log.debug "nListOut: $nList"
        list = nList
    }
    else if (list?.size() == listSize) {
        def nList = list?.drop(1)
        nList?.push(issue)
        list = nList
    }

    if(list) { state?.apiIssuesList = list }
    //log.debug "listOut: $list"
}

def ok2PollDevice() {
    if (atomicState?.pollBlocked) { return false }
    if (atomicState?.needDevPoll) { return true }
    def pollTime = !settings?.pollValue ? 180 : settings?.pollValue.toInteger()
    def val = pollTime/3
    if (val > 60) { val = 50 }
    return ( ((getLastDevicePollSec() + val) > pollTime) ? true : false )
}

def ok2PollStruct() {
    if (atomicState?.pollBlocked) { return false }
    if (atomicState?.needStrPoll) { return true }
    def pollStrTime = !settings?.pollStrValue ? 180 : settings?.pollStrValue.toInteger()
    def val = pollStrTime/3
    if (val > 60) { val = 50 }
    return ( ((getLastStructPollSec() + val) > pollStrTime || !atomicState?.structData) ? true : false )
}


def isPollAllowed() { return (atomicState?.pollingOn && (atomicState?.thermostats || atomicState?.protects || atomicState?.weatherDevice || atomicState?.cameras)) ? true : false }
def getLastDevicePollSec() { return !atomicState?.lastDevDataUpd ? 1000 : GetTimeDiffSeconds(atomicState?.lastDevDataUpd).toInteger() }
def getLastStructPollSec() { return !atomicState?.lastStrucDataUpd ? 1000 : GetTimeDiffSeconds(atomicState?.lastStrucDataUpd).toInteger() }
def getLastForcedPollSec() { return !atomicState?.lastForcePoll ? 1000 : GetTimeDiffSeconds(atomicState?.lastForcePoll).toInteger() }
def getLastChildUpdSec() { return !atomicState?.lastChildUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastChildUpdDt).toInteger() }

/************************************************************************************************
|										Nest API Commands										|
*************************************************************************************************/

private cmdProcState(Boolean value) { atomicState?.cmdIsProc = value }
private cmdIsProc() { return !atomicState?.cmdIsProc ? false : true }
private getLastProcSeconds() { return atomicState?.cmdLastProcDt ? GetTimeDiffSeconds(atomicState?.cmdLastProcDt) : 0 }

def apiVar() {
    def api = [
        rootTypes:	[ struct:"structures", cos:"devices/smoke_co_alarms", tstat:"devices/thermostats", cam:"devices/cameras", meta:"metadata" ],
        cmdObjs:	[ targetF:"target_temperature_f", targetC:"target_temperature_c", targetLowF:"target_temperature_low_f", setLabel:"label",
                      targetLowC:"target_temperature_low_c", targetHighF:"target_temperature_high_f", targetHighC:"target_temperature_high_c",
                      fanActive:"fan_timer_active", fanTimer:"fan_timer_timeout", hvacMode:"hvac_mode", away:"away", streaming:"is_streaming" ],
        hvacModes: 	[ heat:"heat", cool:"cool", heatCool:"heat-cool", off:"off" ]
    ]
    return api
}

def sendEvtUpdateToDevice(typeId, type, obj, objVal) {
    log.trace "sendEvtUpdateToDevice($typeId, $type, $obj, $objVal)..."
    try {
        def devId
        if(type == apiVar().rootTypes.tstat) {
            def tDev = getChildDevice(typeId)
            if(tDev) {
                switch(obj) {
                    case [apiVar()?.cmdObjs.targetF, apiVar()?.cmdObjs.targetC]:
                        sendEvent(device: tDev, name:'targetTemperature', value: objVal, unit: state?.tempUnit, descriptionText: "Target Temperature is ${objVal}", displayed: false, isStateChange: true)
                    break
                    case [apiVar()?.cmdObjs.targetLowF, apiVar()?.cmdObjs.targetLowC]:
                        sendEvent(device: tDev, name:'heatingSetpoint', value: objVal, unit: state?.tempUnit, descriptionText: "Heat Setpoint is ${objVal}" , displayed: disp, isStateChange: true, state: "heat")
                    break
                    case [apiVar()?.cmdObjs.targetHighF, apiVar()?.cmdObjs.targetHighC]:
                        sendEvent(device: tDev, name:'coolingSetpoint', value: objVal, unit: state?.tempUnit, descriptionText: "Cool Setpoint is ${objVal}" , displayed: disp, isStateChange: true, state: "cool")
                    break
                    case [apiVar()?.cmdObjs.fanActive]:
                        sendEvent(device: tDev, name: "thermostatFanMode", value: objVal, descriptionText: "Fan Mode is: ${objVal}", displayed: true, isStateChange: true, state: objVal)
                    break
                    case [apiVar()?.cmdObjs.hvacMode]:
                        sendEvent(device: tDev, name: "thermostatMode", value: objVal, descriptionText: "HVAC mode is ${objVal} mode", displayed: true, isStateChange: true)
                    break
                }
            }
        }
        //This handles away command events
        if(obj == apiVar()?.cmdObjs.away) {
            def pres = (objVal?.toString() == "home") ? "present" : "not present"
            def nestPres = (objVal?.toString() == "home") ? "home" : ((objVal?.toString() == "auto-away") ? "auto-away" : "away")
            def devIds = []
            if(settings?.presDevice) { devIds?.push(getNestPresId()) }
            if(atomicState?.thermostats) {
                atomicState?.thermostats.each { tstat ->
                    //log.debug "tstat: ${tstat.key}"
                    devIds?.push(tstat?.key.toString())
                }
            }
            //log.debug "devIds: $devIds"
            if(devIds) {
                devIds?.each { dev ->
                    //log.debug "dev: $dev"
                    def cDev = getChildDevice(dev?.toString())
                    log.debug "child: $cDev"
                    sendEvent(device: cDev, name: 'nestPresence', value: nestPres, descriptionText: "Nest Presence is: ${nestPres}", displayed: true, isStateChange: true )
                    sendEvent(device: cDev, name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: false, isStateChange: true)
                }
            }
        }
    } catch (ex) {
        log.error "sendEvtUpdateToDevice Exception: ${ex}", ex
        sendExceptionData(ex.message, "sendEvtUpdateToDevice")
    }
}

def setCamStreaming(child, streamOn) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    def val = streamOn.toBoolean() ? true : false
    LogAction("Nest Manager(setCamStreaming) - Setting Camera${!devId ? "" : " ${devId}"} Streaming to: (${val ? "On" : "Off"})", "debug", true)
    if(childDebug && child) { child?.log("setCamStreaming( devId: ${devId}, StreamOn: ${val})") }
    return sendNestApiCmd(devId, apiVar().rootTypes.cam, apiVar().cmdObjs.streaming, val, devId)
}

def setStructureAway(child, value) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    def val = value?.toBoolean()
    LogAction("Nest Manager(setStructureAway) - Setting Nest Location:${!devId ? "" : " ${devId}"} (${val ? "Away" : "Home"})", "debug", true)
    if(childDebug && child) { child?.log("setStructureAway: ${devId} | (${val})") }
    if(val) {
        return sendNestApiCmd(atomicState?.structures, apiVar().rootTypes.struct, apiVar().cmdObjs.away, "away", devId)
    }
    else {
        return sendNestApiCmd(atomicState?.structures, apiVar().rootTypes.struct, apiVar().cmdObjs.away, "home", devId)
    }
}

def setTstatLabel(child, label) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    def val = label
    LogAction("Nest Manager(setTstatLabel) - Setting Thermostat${!devId ? "" : " ${devId}"} Label to: (${val ? "On" : "Auto"})", "debug", true)
    if(childDebug && child) { child?.log("setTstatLabel( devId: ${devId}, newLabel: ${val})") }
    return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.setLabel, val, devId)
}

def setFanMode(child, fanOn) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    def val = fanOn.toBoolean()
    LogAction("Nest Manager(setFanMode) - Setting Thermostat${!devId ? "" : " ${devId}"} Fan Mode to: (${val ? "On" : "Auto"})", "debug", true)
    if(childDebug && child) { child?.log("setFanMode( devId: ${devId}, fanOn: ${val})") }
    return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.fanActive, val, devId)
}

def setHvacMode(child, mode) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("Nest Manager(setHvacMode) - Setting Thermostat${!devId ? "" : " ${devId}"} Mode to: (${mode})", "debug", true)
    return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.hvacMode, mode.toString(), devId)
}

def setTargetTemp(child, unit, temp) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("setTargetTemp: ${devId} | (${temp})${unit}", "debug", true)
    if(childDebug && child) { child?.log("setTargetTemp: ${devId} | (${temp})${unit}") }
    if(unit == "C") {
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetC, temp, devId)
    }
    else {
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetF, temp, devId)
    }
}

def setTargetTempLow(child, unit, temp) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("setTargetTempLow: ${devId} | (${temp})${unit}", "debug", true)
    if(childDebug && child) { child?.log("setTargetTempLow: ${devId} | (${temp})${unit}") }
    if(unit == "C") {
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetLowC, temp, devId)
    }
    else {
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetLowF, temp, devId)
    }
}

def setTargetTempHigh(child, unit, temp) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("setTargetTempHigh: ${devId} | (${temp})${unit}", "debug", true)
    if(childDebug && child) { child?.log("setTargetTempHigh: ${devId} | (${temp})${unit}") }
    if(unit == "C") {
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetHighC, temp, devId)
    }
    else {
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetHighF, temp, devId)
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
        log.error "sendNestApiCmd Exception: ${ex}", ex
        sendExceptionData(ex.message, "sendNestApiCmd")
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

    if (!atomicState?.cmdQlist) { atomicState?.cmdQlist = [] }
    def cmdQueueList = atomicState?.cmdQlist
    def done = false
    def nearestQ = 100
    def qnum = 0
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

    //log.trace("workQueue Run queue: ${qnum}" )
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
            if(cmd[1] == apiVar().rootTypes.struct.toString()) {
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
                sendMsg("Warning", "There is now ${cmdQueue?.size()} events in the Command Queue. Something must be wrong...")
                LogAction("There is now ${cmdQueue?.size()} events in the Command Queue. Something must be wrong...", "warn", true)
            }
            return
        } else { atomicState.pollBlocked = false }
    }
    catch (ex) {
        log.error "workQueue Exception Error: ${ex}", ex
        sendExceptionData(ex.message, "workQueue")
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
        LogAction("procNestApiCmd Url: $uri | params: ${params}", "trace", true)
        atomicState?.lastCmdSent = "$type: (${obj}: ${objVal})"

        if (!redir && (getRecentSendCmd(qnum) > 0) && (getLastCmdSentSeconds(qnum) < 60)) {
            def val = getRecentSendCmd(qnum)
            val -= 1
            setRecentSendCmd(qnum, val)
        }
        setLastCmdSentSeconds(qnum, getDtNow())

        //log.trace "procNestApiCmd time update recentSendCmd:  ${getRecentSendCmd(qnum)}  last seconds:${getLastCmdSentSeconds(qnum)} queue: ${qnum}"

        httpPutJson(params) { resp ->
            if (resp?.status == 307) {
                def newUrl = resp?.headers?.location?.split("\\?")
                LogTrace("NewUrl: ${newUrl[0]}")
                if ( procNestApiCmd(newUrl[0], typeId, type, obj, objVal, qnum, true) ) {
                    result = true
                }
            }
            else if( resp?.status == 200) {
                LogAction("procNestApiCmd Processed queue: ${qnum} ($type | ($obj:$objVal)) Successfully!!!", "info", true)
                apiIssueEvent(false)
                result = true
                //attempts to update device event immediately after successful command.
                increaseCmdCnt()
                atomicState?.lastCmdSentStatus = "ok"
                //sendEvtUpdateToDevice(typeId, type, obj, objVal)
            }
            else if(resp?.status == 400) {
                LogAction("procNestApiCmd 'Bad Request' Exception: ${resp?.status} ($type | $obj:$objVal)", "error", true)
            }
            else {
                LogAction("procNestApiCmd 'Unexpected' Response: ${resp?.status}", "warn", true)
            }
        }
    }
    catch (ex) {
        log.error "procNestApiCmd Exception: ${ex} | ($type | $obj:$objVal)", ex
        sendExceptionData(ex.message, "procNestApiCmd")
        apiIssueEvent(true)
        atomicState?.lastCmdSentStatus = "failed"
    }
    return result
}

def increaseCmdCnt() {
    try {
        def cmdCnt = atomicState?.apiCommandCnt ?: 0
        cmdCnt = cmdCnt?.toInteger()+1
        LogAction("Api CmdCnt: $cmdCnt", "info", false)
        if(cmdCnt) { atomicState?.apiCommandCnt = cmdCnt?.toInteger() }
    } catch (ex) {
        log.error "increaseCmdCnt Exception: ${ex}", ex
        sendExceptionData(ex.message, "increaseCmdCnt")
    }
}


/************************************************************************************************
|								Push Notification Functions										|
*************************************************************************************************/
def pushStatus() { return (settings?.recipients || settings?.phone || settings?.usePush) ? (settings?.usePush ? "Push Enabled" : "Enabled") : null }
def getLastMsgSec() { return !atomicState?.lastMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastMsgDt).toInteger() }
def getLastUpdMsgSec() { return !atomicState?.lastUpdMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastUpdMsgDt).toInteger() }
def getLastMisPollMsgSec() { return !atomicState?.lastMisPollMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastMisPollMsgDt).toInteger() }
def getRecipientsSize() { return !settings.recipients ? 0 : settings?.recipients.size() }

def getOk2Notify() { return (daysOk(settings?."${getAutoType()}quietDays") && notificationTimeOk() && modesOk(settings?."${getAutoType()}quietModes")) }
def isMissedPoll() { return (getLastDevicePollSec() > atomicState?.misPollNotifyWaitVal.toInteger()) ? true : false }

def notificationCheck() {
    if((settings?.recipients || settings?.usePush) && getOk2Notify()) {
        if (sendMissedPollMsg) { missedPollNotify() }
        if (sendAppUpdateMsg && !appDevType()) { appUpdateNotify() }
    }
}

def missedPollNotify() {
    if(isMissedPoll()) {
        if(getOk2Notify() && (getLastMisPollMsgSec() > atomicState?.misPollNotifyMsgWaitVal.toInteger())) {
            sendMsg("Warning", "${app.name} has not refreshed data in the last (${getLastDevicePollSec()}) seconds.  Please try refreshing manually.")
            atomicState?.lastMisPollMsgDt = getDtNow()
        }
    }
}

def appUpdateNotify() {
    def appUpd = isAppUpdateAvail()
    def protUpd = atomicState?.protects ? isProtUpdateAvail() : null
    def presUpd = atomicState?.presDevice ? isPresUpdateAvail() : null
    def tstatUpd = atomicState?.thermostats ? isTstatUpdateAvail() : null
    def weatherUpd = atomicState?.weatherDevice ? isWeatherUpdateAvail() : null
    def camUpd = atomicState?.cameras ? isCamUpdateAvail() : null
    if((appUpd || protUpd || presUpd || tstatUpd || weatherUpd || camUpd) && (getLastUpdMsgSec() > atomicState?.updNotifyWaitVal.toInteger())) {
        def str = ""
        str += !appUpd ? "" : "\nManager App: v${atomicState?.appData?.updater?.versions?.app?.ver?.toString()}, "
        str += !protUpd ? "" : "\nProtect: v${atomicState?.appData?.updater?.versions?.protect?.ver?.toString()}, "
        str += !camUpd ? "" : "\nCamera: v${atomicState?.appData?.updater?.versions?.camera?.ver?.toString()}, "
        str += !presUpd ? "" : "\nPresence: v${atomicState?.appData?.updater?.versions?.presence?.ver?.toString()}, "
        str += !tstatUpd ? "" : "\nThermostat: v${atomicState?.appData?.updater?.versions?.thermostat?.ver?.toString()}"
        str += !weatherUpd ? "" : "\nWeather App: v${atomicState?.appData?.updater?.versions?.weather?.ver?.toString()}"
        sendMsg("Info", "Update(s) are available: ${str}...  Please visit the IDE to Update your code...")
        atomicState?.lastUpdMsgDt = getDtNow()
    }
}

def updateHandler() {
    //log.trace "updateHandler..."
    if(atomicState?.isInstalled) {
        if(atomicState?.appData?.updater?.updateType.toString() == "critical" && atomicState?.lastCritUpdateInfo?.ver.toInteger() != atomicState?.appData?.updater?.updateVer.toInteger()) {
            sendMsg("Critical", "There are Critical Updates available for the Nest Manager Application!!! Please visit the IDE and make sure to update the App and Devices Code...")
            atomicState?.lastCritUpdateInfo = ["dt":getDtNow(), "ver":atomicState?.appData?.updater?.updateVer?.toInteger()]
        }
        if(atomicState?.appData?.updater?.updateMsg != "" && atomicState?.appData?.updater?.updateMsg != atomicState?.lastUpdateMsg) {
            if(getLastUpdateMsgSec() > 86400) {
                sendMsg("Info", "${atomicState?.updater?.updateMsg}")
                atomicState?.lastUpdateMsgDt = getDtNow()
            }
        }
    }
}

def sendMsg(msgType, msg, people = null, sms = null, push = null, brdcast = null) {
    try {
        if(!getOk2Notify()) {
            LogAction("No Notifications will be sent during Quiet Time...", "info", true)
        } else {
            def newMsg = "${msgType}: ${msg}"
            if(!brdcast) {
                def who = people ? people : settings?.recipients
                if (location.contactBookEnabled) {
                    if(who) {
                        sendNotificationToContacts(newMsg, who)
                        atomicState?.lastMsg = newMsg
                        atomicState?.lastMsgDt = getDtNow()
                        LogAction("Push Message Sent: ${atomicState?.lastMsgDt}", "debug", true)
                    }
                } else {
                    LogAction("ContactBook is NOT Enabled on your SmartThings Account...", "warn", true)
                    if (push) {
                        sendPush(newMsg)
                        atomicState?.lastMsg = newMsg
                        atomicState?.lastMsgDt = getDtNow()
                        LogAction("Push Message Sent: ${atomicState?.lastMsgDt}", "debug", true)
                    }
                    else if (sms) {
                        sendSms(sms, newMsg)
                        atomicState?.lastMsg = newMsg
                        atomicState?.lastMsgDt = getDtNow()
                        LogAction("SMS Message Sent: ${atomicState?.lastMsgDt}", "debug", true)
                    }
                }
            } else {
                sendPushMessage(newMsg)
                LogAction("Broadcast Message Sent: ${newMsg} - ${atomicState?.lastMsgDt}", "debug", true)
            }
        }
    } catch (ex) {
        log.error "sendMsg Exception: ${ex}", ex
        sendExceptionData(ex.message, "sendMsg")
    }
}

def getLastWebUpdSec() { return !atomicState?.lastWebUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastWebUpdDt).toInteger() }
def getLastWeatherUpdSec() { return !atomicState?.lastWeatherUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastWeatherUpdDt).toInteger() }
def getLastForecastUpdSec() { return !atomicState?.lastForecastUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastForecastUpdDt).toInteger() }
def getLastAnalyticUpdSec() { return !atomicState?.lastAnalyticUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastAnalyticUpdDt).toInteger() }
def getLastUpdateMsgSec() { return !atomicState?.lastUpdateMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastUpdateMsgDt).toInteger() }

def getStZipCode() { return location?.zipCode.toString() }
def getNestZipCode() { return atomicState?.structData[atomicState?.structures].postal_code ? atomicState?.structData[atomicState?.structures]?.postal_code.toString() : "" }
def getNestTimeZone() { return atomicState?.structData[atomicState?.structures].time_zone ? atomicState?.structData[atomicState?.structures].time_zone : null}

def updateWebStuff(now = false) {
    //log.trace "updateWebStuff..."
    if (!atomicState?.appData || (getLastWebUpdSec() > (3600*6))) {
        if(now) {
            getWebFileData()
        } else {
            if(canSchedule()) { runIn(3, "getWebFileData", [overwrite: true]) }  //This reads a JSON file from a web server with timing values and version numbers
        }
    }
    if (optInAppAnalytics && atomicState?.isInstalled) {
        if (getLastAnalyticUpdSec() > (3600*24)) {
            sendInstallData()
        }
    }
    if(atomicState?.weatherDevice && getLastWeatherUpdSec() > (settings?.pollWeatherValue ? settings?.pollWeatherValue.toInteger() : 900)) {
        if(now) {
            getWeatherConditions(now)
        } else {
            if(canSchedule()) { runIn(3, "getWeatherConditions", [overwrite: true]) }
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
                if (!force) { runIn(3, "postCmd", [overwrite: true]) }
                return true
            }
        }
        catch (ex) {
            log.error "getWeatherConditions Exception: ${ex}", ex
            sendExceptionData(ex.message, "getWeatherConditions")
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

def getWeatherDeviceInst() {
    return atomicState?.weatherDevice ? true : false
}

def getWebFileData() {
    //log.trace "getWebFileData..."
    def params = [ uri: "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Data/appParams.json", contentType: 'application/json' ]
    def result = false
    try {
        httpGet(params) { resp ->
            if(resp.data) {
                LogAction("Getting Latest Data from appParams.json File...", "info", true)
                atomicState?.appData = resp?.data
                atomicState?.lastWebUpdDt = getDtNow()
                updateHandler()
                broadcastCheck()
                helpHandler()
            }
            LogTrace("getWebFileData Resp: ${resp?.data}")
            result = true
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
               log.warn  "appParams.json file not found..."
        } else {
            log.error "getWebFileData Exception: ${ex}", ex
        }
        sendExceptionData(ex.message, "getWebFileData")
    }
    return result
}

def broadcastCheck() {
    if(atomicState?.isInstalled && atomicState?.appData.broadcast) {
        if(atomicState?.appData?.broadcast?.msgId != null && atomicState?.lastBroadcastId != atomicState?.appData?.broadcast?.msgId) {
            sendMsg(atomicState?.appData?.broadcast?.type.toString().capitalize(), atomicState?.appData?.broadcast?.message.toString(), null, null, null, true)
            atomicState?.lastBroadcastId = atomicState?.appData?.broadcast?.msgId
        }
    }
}

def helpHandler() {
    if(atomicState?.appData?.help) {
        atomicState.showHelp = atomicState?.appData?.help?.showHelp == "false" ? false : true
    }
}

def getHtmlInfo() {
    if(atomicState?.appData?.css?.cssUrl && atomicState?.appData?.css?.cssVer && atomicState?.appData?.html?.chartJsUrl && atomicState?.appData?.html?.chartJsVer ) {
        return ["cssUrl":atomicState?.appData?.html?.cssUrl, "cssVer":atomicState?.appData?.html?.cssVer, "chartJsUrl":atomicState?.appData?.html?.chartJsUrl, "chartJsVer":atomicState?.appData?.html?.chartJsVer]
    } else {
        if(getWebFileData()) {
            return ["cssUrl":atomicState?.appData?.html?.cssUrl, "cssVer":atomicState?.appData?.html?.cssVer, "chartJsUrl":atomicState?.appData?.html?.chartJsUrl, "chartJsVer":atomicState?.appData?.html?.chartJsVer]
        }
    }
}

def allowDbException() {
    if(atomicState?.appData?.database?.allowDbException) {
        return atomicState?.appData?.database?.allowDbException == false ? false : true
    } else {
        if(getWebFileData()) {
            return atomicState?.appData?.database?.allowDbException == false ? false : true
        }
    }
}

def ver2IntArray(val) {
    def ver = val?.split("\\.")
    return [maj:"${ver[0]?.toInteger()}",min:"${ver[1]?.toInteger()}",rev:"${ver[2]?.toInteger()}"]
}
def versionStr2Int(str) { return str ? str.toString().replaceAll("\\.", "").toInteger() : null }

def getChildWaitVal() { return settings?.tempChgWaitVal ? settings?.tempChgWaitVal.toInteger() : 4 }

def isCodeUpdateAvailable(newVer, curVer, type) {
    def result = false
    def latestVer
    if(newVer && curVer) {
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
    }
    //log.debug "type: $type | newVer: $newVer | curVer: $curVer | newestVersion: ${latestVer} | result: $result"
    return result
}

def isAppUpdateAvail() {
    if(isCodeUpdateAvailable(atomicState?.appData?.updater?.versions?.app?.ver, appVersion(), "manager")) {
        return true
    } else { return false }
}

def isPresUpdateAvail() {
    if(isCodeUpdateAvailable(atomicState?.appData?.updater?.versions?.presence?.ver, atomicState?.presDevVer, "presence")) {
        return true
    } else { return false }
}

def isProtUpdateAvail() {
    if(isCodeUpdateAvailable(atomicState?.appData?.updater?.versions?.protect?.ver, atomicState?.pDevVer, "protect")) {
        return true
    } else { return false }
}

def isCamUpdateAvail() {
    if(isCodeUpdateAvailable(atomicState?.appData?.updater?.versions?.camera?.ver, atomicState?.camDevVer, "camera")) {
        return true
    } else { return false }
}

def isTstatUpdateAvail() {
    if(isCodeUpdateAvailable(atomicState?.appData?.updater?.versions?.thermostat?.ver, atomicState?.tDevVer, "thermostat")) {
        return true
    } else { return false }
}

def isWeatherUpdateAvail() {
    if(isCodeUpdateAvailable(atomicState?.appData?.updater?.versions?.weather?.ver, atomicState?.weatDevVer, "weather")) {
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
            structs?.eachWithIndex { struc, index ->
                def strucId = struc?.key
                def strucData = struc?.value

                def dni = [strucData?.structure_id].join('.')
                struct[dni] = strucData?.name.toString()

                if (strucData?.structure_id.toString() == settings?.structures.toString()) {
                    thisstruct[dni] = strucData?.name.toString()
                } else {
                    if (atomicState?.structures) {
                        if (strucData?.structure_id?.toString() == atomicState?.structures?.toString()) {
                            thisstruct[dni] = strucData?.name.toString()
                        }
                    } else {
                        if (!settings?.structures) {
                            thisstruct[dni] = strucData?.name.toString()
                        }
                    }
                }
            }
            if (atomicState?.thermostats || atomicState?.protects || atomicState?.cameras || atomicState?.presDevice || atomicState?.weatherDevice || isAutoAppInst() ) {  // if devices are configured, you cannot change the structure until they are removed
                struct = thisstruct
            }
            if (ok2PollDevice()) { getApiData("dev") }
        } else { LogAction("Missing: atomicState.structData  ${atomicState?.structData}", "warn", true) }

    } catch (ex) {
        log.error "getNestStructures Exception: ${ex}", ex
        sendExceptionData(ex.message, "getNestStructures")
    }
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

def getNestCameras() {
    LogTrace("Getting Nest Camera List...")
    def cameras = [:]
    def nCameras = atomicState?.deviceData?.cameras
    LogTrace("Found ${nCameras?.size()} Nest Cameras...")
    nCameras.each { dev ->
        def devId = dev?.key
        def devData = dev?.value

        def bdni = [devData?.device_id].join('.')
        if (devData?.structure_id == settings?.structures) {
            cameras[bdni] = getCameraDisplayName(devData)
        }
    }
    return cameras
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

def camState(val) {
    def cams = [:]
    def nCameras = getNestCameras()
    nCameras.each { dev ->
        val.each { cm ->
        if(dev?.key == cm) {
            def bdni = [dev?.key].join('.')
                cams[bdni] = dev?.value
            }
        }
    }
    return cams
}

def getThermostatDisplayName(stat) {
    if(stat?.name) { return stat.name.toString() }
}

def getProtectDisplayName(prot) {
    if(prot?.name) { return prot.name.toString() }
}

def getCameraDisplayName(cam) {
    if(cam?.name) { return cam.name.toString() }
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

def getNestCamDni(dni) {
    def d5 = getChildDevice(dni?.key.toString())
    if(d5) { return dni?.key.toString() }
    else {
        def devt =  appDevName()
        return "NestCam-${dni?.value.toString()}${devt} | ${dni?.key.toString()}"
    }
    LogAction("getNestCamDni Issue...", "warn", true)
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

def getNestCamLabel(name) {
    def devt = appDevName()
    def defName = "Nest Camera${devt} - ${name}"
    if(atomicState?.useAltNames) { defName = "${location.name}${devt} - ${name}" }
    if(atomicState?.custLabelUsed) {
        return settings?."cam_${name}_lbl" ? settings?."cam_${name}_lbl" : defName
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

def getWeatherDevice() {
    def res = null
    def d = getChildDevice(getNestWeatherId())
    if(d) { return d }
    return res
}

def getTstats() {
    return atomicState?.thermostats
}

def getThermostatDevice(dni) {
    def d = getChildDevice(getNestTstatDni(dni))
    if(d) { return d }
    return null
}

def addRemoveDevices(uninst = null) {
    //log.trace "addRemoveDevices..."
    def retVal = false
    try {
        def devsInUse = []
        def tstats
        def nProtects
        def nCameras
        def devsCrt = 0
        if(!uninst) {
            //LogAction("addRemoveDevices() Nest Thermostats ${atomicState?.thermostats}", "debug", false)
            if (atomicState?.thermostats) {
                tstats = atomicState?.thermostats.collect { dni ->
                    def d1 = getChildDevice(getNestTstatDni(dni))
                    if(!d1) {
                        def d1Label = getNestTstatLabel("${dni?.value}")
                        d1 = addChildDevice(app.namespace, getThermostatChildName(), dni?.key, null, [label: "${d1Label}"])
                        d1.take()
                        devsCrt = devsCrt + 1
                        LogAction("Created: ${d1?.displayName} with (Id: ${dni?.key})", "debug", true)
                    } else {
                        LogAction("Found: ${d1?.displayName} with (Id: ${dni?.key}) already exists", "debug", true)
                    }
                    devsInUse += dni.key
                    return d1
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
                        LogAction("Created: ${d2?.displayName} with (Id: ${dni?.key})", "debug", true)
                    } else {
                        LogAction("Found: ${d2?.displayName} with (Id: ${dni?.key}) already exists", "debug", true)
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
                } catch (ex) {
                    LogAction("Nest Weather Device Type is Likely not installed/published", "warn", true)
                    retVal = false
                }
            }
            if (atomicState?.cameras) {
                nCameras = atomicState?.cameras.collect { dni ->
                    def d5 = getChildDevice(getNestCamDni(dni).toString())
                    if(!d5) {
                        def d5Label = getNestCamLabel("${dni.value}")
                        d5 = addChildDevice(app.namespace, getCameraChildName(), dni.key, null, [label: "${d5Label}"])
                        d5.take()
                        devsCrt = devsCrt + 1
                        LogAction("Created: ${d5?.displayName} with (Id: ${dni?.key})", "debug", true)
                    } else {
                        LogAction("Found: ${d5?.displayName} with (Id: ${dni?.key}) already exists", "debug", true)
                    }
                    devsInUse += dni.key
                    return d5
                }
            }
            def presCnt = 0
            def weathCnt = 0
            if(atomicState?.presDevice) { presCnt = 1 }
            if(atomicState?.weatherDevice) { weathCnt = 1 }
            if(devsCrt > 0) {
                LogAction("Created Devices;  Current Devices: (${tstats?.size()}) Thermostat(s), (${nProtects?.size()}) Protect(s), (${nCameras?.size()}) Cameras(s), ${presCnt} Presence Device and ${weathCnt} Weather Device", "debug", true)
            }
        }

        if(uninst) {
            atomicState.thermostats = []
            atomicState.protects = []
            atomicState.cameras = []
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
            LogAction("Deleting: ${delete}, Removing ${delete.size()} devices", "debug", true)
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
            LogAction("addRemoveDevices Exception | $msg", "warn", true)
        }
        else { log.error "addRemoveDevices Exception: ${ex}", ex }
        sendExceptionData(ex.message, "addRemoveDevices")
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
                    input (name: "useAltNames", type: "bool", title: "Use Location Name as Prefix?", required: false, defaultValue: altName, submitOnChange: true, image: "" )
                }
                if(!useAltNames) {
                    input (name: "useCustDevNames", type: "bool", title: "Assign Custom Names?", required: false, defaultValue: custName, submitOnChange: true, image: "" )
                }
            }
            if(atomicState?.custLabelUsed) {
                paragraph "Custom Labels Are Active"
            }
            if(atomicState?.useAltNames) {
                paragraph "Using Location Name as Prefix is Active"
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
//ERS
                    if(d) {
                        dstr += "Found: ${d.displayName}"
                        if (d.displayName != getNestTstatLabel(t.value)) {
                            dstr += "$str1 ${getNestTstatLabel(t.value)}"
                        }
                        else if (atomicState?.custLabelUsed || atomicState?.useAltNames) { dstr += "$str2" }
                    } else {
                        dstr += "New Name: ${getNestTstatLabel(t.value)}"
                    }
                    paragraph "${dstr}", state: "complete", image: (atomicState?.custLabelUsed && !d) ? " " : getAppImg("thermostat_icon.png")
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
                        else if (atomicState?.custLabelUsed || atomicState?.useAltNames) { dstr += "$str2" }
                    } else {
                        dstr += "New Name: ${getNestProtLabel(p.value)}"
                    }
                    paragraph "${dstr}", state: "complete", image: (atomicState.custLabelUsed && !d1) ? " " : getAppImg("protect_icon.png")
                    if(atomicState.custLabelUsed && !d1) {
                        input "prot_${p.value}_lbl", "text", title: "Custom name for ${p.value}", defaultValue: getNestProtLabel("${p.value}"), submitOnChange: true,
                                image: getAppImg("protect_icon.png")
                    }
                }
            }
        }
        if(atomicState?.cameras) {
            section ("Camera Device Names:") {
                atomicState?.cameras?.each { c ->
                    found = true
                    def dstr = ""
                    def d1 = getChildDevice(getNestCamDni(c))
                    if(d1) {
                        dstr += "Found: ${d1.displayName}"
                        if (d1.displayName != getNestCamLabel(c.value)) {
                            dstr += "$str1 ${getNestCamLabel(c.value)}"
                        }
                        else if (atomicState?.custLabelUsed || atomicState?.useAltNames) { dstr += "$str2" }
                    } else {
                        dstr += "New Name: ${getNestCamLabel(c.value)}"
                    }
                    paragraph "${dstr}", state: "complete", image: (atomicState.custLabelUsed && !d1) ? " " : getAppImg("camera_icon.png")
                    if(atomicState.custLabelUsed && !d1) {
                        input "cam_${c.value}_lbl", "text", title: "Custom name for ${c.value}", defaultValue: getNestCamLabel("${c.value}"), submitOnChange: true,
                                image: getAppImg("camera_icon.png")
                    }
                }
            }
        }
        if(atomicState?.presDevice) {
            section ("Presence Device Name:") {
                found = true
                def pLbl = getNestPresLabel()
                def dni = getNestPresId()
                def d3 = getChildDevice(dni)
                def dstr = ""
                if(d3) {
                    dstr += "Found: ${d3.displayName}"
                    if (d3.displayName != pLbl) {
                        dstr += "$str1 ${pLbl}"
                    }
                    else if (atomicState?.custLabelUsed || atomicState?.useAltNames) { dstr += "$str2" }
                } else {
                    dstr += "New Name: ${pLbl}"
                }
                paragraph "${dstr}", state: "complete", image: (atomicState.custLabelUsed && !d3) ? " " : getAppImg("presence_icon.png")
                if(atomicState.custLabelUsed && !d3) {
                    input "presDev_lbl", "text", title: "Custom name for Nest Presence Device", defaultValue: pLbl, submitOnChange: true, image: getAppImg("presence_icon.png")
                }
            }
        }
        if(atomicState?.weatherDevice) {
            section ("Weather Device Name:") {
                found = true
                def wLbl = getNestWeatherLabel()
                def dni = getNestWeatherId()
                def d4 = getChildDevice(dni)
                def dstr = ""
                if(d4) {
                    dstr += "Found: ${d4.displayName}"
                    if (d4.displayName != wLbl) {
                        dstr += "$str1 ${wLbl}"
                    }
                    else if (atomicState?.custLabelUsed || atomicState?.useAltNames) { dstr += "$str2" }
                } else {
                    dstr += "New Name: ${wLbl}"
                }
                paragraph "${dstr}", state: "complete", image: (atomicState.custLabelUsed && !d4) ? " " : getAppImg("weather_icon.png")
                if(atomicState.custLabelUsed && !d4) {
                    input "weathDev_lbl", "text", title: "Custom name for Nest Weather Device", defaultValue: wLbl, submitOnChange: true, image: getAppImg("weather_icon.png")
                }
            }
        }
        if(!found) {
            paragraph "No Devices Selected"
        }
    }
}

def deviceHandlerTest() {
    //log.trace "deviceHandlerTest()"
    atomicState.devHandlersTested = true
    return true

    if(atomicState?.devHandlersTested || atomicState?.isInstalled || (atomicState?.thermostats && atomicState?.protects && atomicState?.cameras && atomicState?.presDevice && atomicState?.weatherDevice)) {
        atomicState.devHandlersTested = true
        return true
    }
    try {
        def d1 = addChildDevice(app.namespace, getThermostatChildName(), "testNestThermostat-Install123", null, [label:"Nest Thermostat:InstallTest"])
        def d2 = addChildDevice(app.namespace, getPresenceChildName(), "testNestPresence-Install123", null, [label:"Nest Presence:InstallTest"])
        def d3 = addChildDevice(app.namespace, getProtectChildName(), "testNestProtect-Install123", null, [label:"Nest Protect:InstallTest"])
        def d4 = addChildDevice(app.namespace, getWeatherChildName(), "testNestWeather-Install123", null, [label:"Nest Weather:InstallTest"])
        def d5 = addChildDevice(app.namespace, getCameraChildName(), "testNestCamera-Install123", null, [label:"Nest Camera:InstallTest"])

        log.debug "d1: ${d1.label} | d2: ${d2.label} | d3: ${d3.label} | d4: ${d4.label} | d5: ${d5.label}"
        atomicState.devHandlersTested = true
        removeTestDevs()
        //runIn(4, "removeTestDevs")
        return true
    }
    catch (ex) {
        if(ex instanceof physicalgraph.app.exception.UnknownDeviceTypeException) {
            LogAction("Device Handlers are missing: ${getThermostatChildName()}, ${getPresenceChildName()}, and ${getProtectChildName()}, Verify the Device Handlers are installed and Published via the IDE", "error", true)
        } else {
            log.error "deviceHandlerTest Exception: ${ex}", ex
            sendExceptionData(ex.message, "deviceHandlerTest")
        }
        atomicState.devHandlersTested = false
        return false
    }
}

def removeTestDevs() {
    try {
        def names = [ "testNestThermostat-Install123", "testNestPresence-Install123", "testNestProtect-Install123", "testNestWeather-Install123", "testNestCamera-Install123" ]
        names?.each { dev ->
            //log.debug "dev: $dev"
            def delete = getChildDevices().findAll { it?.deviceNetworkId == dev }
            //log.debug "delete: ${delete}"
            if(delete) {
               delete.each { deleteChildDevice(it.deviceNetworkId) }
            }
        }
    } catch (ex) {
        log.error "deviceHandlerTest Exception: ${ex}", ex
        sendExceptionData(ex.message, "removeTestDevs")
    }
}

def preReqCheck() {
    //log.trace "preReqCheckTest()"
    generateInstallId()
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
    } catch (ex) {
        log.error "getEndpointUrl Exception: ${ex}", ex
        sendExceptionData(ex.message, "getEndpointUrl")
    }
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
        sendExceptionData(ex.message, "getAccessToken")
        return false
    }
}

def generateInstallId() {
    if(!atomicState?.installationId) { atomicState?.installationId = UUID?.randomUUID().toString() }
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
                if(atomicState?.authToken) { atomicState?.tokenCreatedDt = getDtNow() }
            }

            if (atomicState?.authToken) {
                LogAction("Nest AuthToken Generated Successfully...", "info", true)
                generateInstallId
                success()
            } else {
                LogAction("There was a Failure Generating the Nest AuthToken!!!", "error", true)
                fail()
            }
        }
        else { LogAction("callback() failed oauthState != atomicState.oauthInitState", "error", true) }
    }
    catch (ex) {
        log.error "Callback Exception: ${ex}", ex
        sendExceptionData(ex.message, "callback")
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
            if (resp?.status == 204) {
                atomicState?.authToken = null
                LogAction("Your Nest Token has been revoked successfully...", "warn", true)
                return true
            }
        }
    }
    catch (ex) {
        log.error "revokeNestToken Exception: ${ex}", ex
        sendExceptionData(ex.message, "revokeNestToken")
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
    return settings?.thermostats.collect { it.split(/\./).last() }.join(',')
}

def getChildProtectsIdString() {
    return settings?.protects.collect { it.split(/\./).last() }.join(',')
}

def toJson(Map m) {
    return new org.json.JSONObject(m).toString()
}

def toQueryString(Map m) {
    return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

def clientId() {
    if(!appSettings.clientId) {
        def tokenNum = atomicState?.appData?.token?.tokenNum?.toInteger() ?: 1
        switch(tokenNum) {
            case 1:
                return "63e9befa-dc62-4b73-aaf4-dcf3826dd704" // Original Token Updated with Cam/Image Support
                break
            case 2:
                return "31aea46c-4048-4c2b-b6be-cac7fe305d4c" //token v2 with cam support
                break
        }
    } else {
        return appSettings.clientId
    }
}

def clientSecret() {
    if(!appSettings.clientSecret) {
        def tokenNum = atomicState?.appData?.token?.tokenNum?.toInteger() ?: 1
        switch(tokenNum) {
            case 1:
                return "8iqT8X46wa2UZnL0oe3TbyOa0" // Original Token Updated with Cam/Image Support
                break
            case 2:
                return "FmO469GXfdSVjn7PhKnjGWZlm" //token v2 with cam support
                break
        }
    } else {
        return appSettings.clientSecret
    }
}

/************************************************************************************************
|									LOGGING AND Diagnostic										|
*************************************************************************************************/
def LogTrace(msg) {
    def trOn = advAppDebug ? true : false
    if(trOn) { Logger(msg, "trace") }
}

def LogAction(msg, type = "debug", showAlways = false) {
    def isDbg = parent ? ((atomicState?.showDebug || showDebug)  ? true : false) : (appDebug ? true : false)
    if(showAlways) { Logger(msg, type) }

    else if (isDbg && !showAlways) { Logger(msg, type) }
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

def setStateVar(frc = false) {
    //log.trace "setStateVar..."
    //If the developer changes the version in the web appParams JSON it will trigger
    //the app to create any new state values that might not exist or reset those that do to prevent errors
    def stateVer = 3
    def stateVar = !atomicState?.stateVarVer ? 0 : atomicState?.stateVarVer.toInteger()
    if(!atomicState?.stateVarUpd || frc || (stateVer < atomicState?.appData.state.stateVarVer.toInteger())) {
        if(!atomicState?.newSetupComplete) 	        { atomicState.newSetupComplete = false }
        if(!atomicState?.setupVersion)              { atomicState?.setupVersion = 0 }
        if(!atomicState?.misPollNotifyWaitVal) 	    { atomicState.misPollNotifyWaitVal = 900 }
        if(!atomicState?.misPollNotifyMsgWaitVal) 	{ atomicState.misPollNotifyMsgWaitVal = 3600 }
        if(!atomicState?.updNotifyWaitVal) 		    { atomicState.updNotifyWaitVal = 7200 }
        if(!atomicState?.custLabelUsed)             { atomicState?.custLabelUsed = false }
        if(!atomicState?.useAltNames)               { atomicState.useAltNames = false }
        if(!atomicState?.apiCommandCnt)             { atomicState?.apiCommandCnt = 0 }
        atomicState?.stateVarUpd = true
        atomicState?.stateVarVer = atomicState?.appData?.state?.stateVarVer ? atomicState?.appData?.state?.stateVarVer?.toInteger() : 0
    }
}

//Things that I need to clear up on updates go here
//IMPORTANT: This must be run in it's own thread, and exit after running as the cleanup occurs on exit//
def stateCleanup() {
    log.trace "stateCleanup..."

    state.remove("exLogs")
    state.remove("pollValue")
    state.remove("pollStrValue")
    state.remove("pollWaitVal")
    state.remove("tempChgWaitVal")
    state.remove("cmdDelayVal")
    state.remove("testedDhInst")
    state.remove("missedPollNotif")
    state.remove("updateMsgNotif")
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
    state.remove("autoAppInstalled")
    state.remove("nestStructures")
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
}

/******************************************************************************
*                			Keep These Methods				                  *
*******************************************************************************/
def getThermostatChildName() { return getChildName("Nest Thermostat") }
def getProtectChildName()    { return getChildName("Nest Protect") }
def getPresenceChildName()   { return getChildName("Nest Presence") }
def getWeatherChildName()    { return getChildName("Nest Weather") }
def getCameraChildName()     { return getChildName("Nest Camera") }
def getAutoAppChildName()    { return getChildName("Nest Automations") }
def getWatchdogAppChildName()    { return getChildName("Nest Location ${location.name} Watchdog") }

def getChildName(str)     { return "${str}${appDevName()}" }

def getServerUrl()          { return "https://graph.api.smartthings.com" }
def getShardUrl()           { return getApiServerUrl() }
def getCallbackUrl()		{ return "https://graph.api.smartthings.com/oauth/callback" }
def getBuildRedirectUrl()	{ return "${serverUrl}/oauth/initialize?appId=${app.id}&access_token=${atomicState?.accessToken}&apiServerUrl=${shardUrl}" }
def getNestApiUrl()			{ return "https://developer-api.nest.com" }
def getAppEndpointUrl(subPath) { return "${apiServerUrl("/api/smartapps/installations/${app.id}/${subPath}?access_token=${atomicState.accessToken}")}" }
def getHelpPageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html" }
def getReadmePageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/README.html" }
def getAutoHelpPageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help/nest-automations.html" }
def getFirebaseAppUrl() 	{ return "https://st-nest-manager.firebaseio.com" }
def getAppImg(imgName, on = null) 	{ return (!disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/App/$imgName" : "" }
def getDevImg(imgName, on = null) 	{ return (!disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/Devices/$imgName" : "" }

def latestTstatVer()    { return atomicState?.appData?.updater?.versions?.thermostat ?: "unknown" }
def latestProtVer()     { return atomicState?.appData?.updater?.versions?.protect ?: "unknown" }
def latestPresVer()     { return atomicState?.appData?.updater?.versions?.presence ?: "unknown" }
def latestWeathVer()    { return atomicState?.appData?.updater?.versions?.weather ?: "unknown" }
def latestCamVer()      { return atomicState?.appData?.updater?.versions?.camera ?: "unknown" }
def getUse24Time()      { return useMilitaryTime ? true : false }

//Returns app State Info
def getStateSize()      { return state?.toString().length() }
def getStateSizePerc()  { return (int) ((stateSize/100000)*100).toDouble().round(0) }

def debugStatus() { return !appDebug ? "Off" : "On" }
def deviceDebugStatus() { return !childDebug ? "Off" : "On" }
def isAppDebug() { return !appDebug ? false : true }
def isChildDebug() { return !childDebug ? false : true }

def getLocationModes() {
    def result = []
    location?.modes.sort().each {
        if(it) { result.push("${it}") }
    }
    return result
}

def getAutoType() { return !parent ? "" : atomicState?.automationType }

def getAutoIcon(type) {
    if(type) {
        switch(type) {
            case "remSen":
                return getAppImg("remote_sensor_icon.png")
                break
            case "fanCtrl":
                return getAppImg("fan_control_icon.png")
                break
            case "conWat":
                return getAppImg("open_window.png")
                break
            case "leakWat":
                return getAppImg("leak_icon.png")
                break
            case "extTmp":
                return getAppImg("external_temp_icon.png")
                break
            case "nMode":
                return getAppImg("mode_automation_icon.png")
                break
            case "tMode":
                return getAppImg("mode_setpoints_icon.png")
                break
        }
    }
}

def getShowHelp() { return atomicState?.showHelp == false ? false : true }

def getTimeZone() {
    def tz = null
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = TimeZone.getTimeZone(getNestTimeZone()) }
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

def daysOk(days) {
    if(days) {
        def dayFmt = new SimpleDateFormat("EEEE")
        if(getTimeZone()) { dayFmt.setTimeZone(getTimeZone()) }
        return days.contains(dayFmt.format(new Date())) ? false : true
    } else { return true }
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
    } catch (ex) {
        log.error "timeOk Exception: ${ex}", ex
        sendExceptionData(ex.message, "quietTimeOk")
    }
}

def notificationTimeOk() {
    try {
        def pName = getAutoType()
        def strtTime = null
        def stopTime = null
        def now = new Date()
        def sun = getSunriseAndSunset() // current based on geofence, previously was: def sun = getSunriseAndSunset(zipCode: zipCode)
        if(settings?."${pName}qStartTime" && settings?."${pName}qStopTime") {
            if(settings?."${pName}qStartInput" == "sunset") { strtTime = sun.sunset }
            else if(settings?."${pName}qStartInput" == "sunrise") { strtTime = sun.sunrise }
            else if(settings?."${pName}qStartInput" == "A specific time" && settings?."${pName}qStartTime") { strtTime = settings?."${pName}qStartTime" }

            if(settings?."${pName}qStopInput" == "sunset") { stopTime = sun.sunset }
            else if(settings?."${pName}qStopInput" == "sunrise") { stopTime = sun.sunrise }
            else if(settings?."${pName}qStopInput" == "A specific time" && settings?."${pName}qStopTime") { stopTime = settings?."${pName}qStopTime" }
        } else { return true }
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), getTimeZone()) ? false : true
        } else { return true }
    } catch (ex) {
        log.error "notificationTimeOk Exception: ${ex}", ex
        sendExceptionData(ex.message, "notificationTimeOk")
    }
}

def time2Str(time) {
    if (time) {
        def t = timeToday(time, getTimeZone())
        def f = new java.text.SimpleDateFormat("h:mm a")
        f.setTimeZone(getTimeZone() ?: timeZone(time))
        f.format(t)
    }
}

def epochToTime(tm) {
    def tf = new SimpleDateFormat("h:mm a")
        tf?.setTimeZone(getTimeZone())
    return tf.format(tm)
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

def modesOk(modeEntry) {
    def res = true
    if (modeEntry) {
        modeEntry?.each { m ->
            if(m.toString() == location?.mode.toString()) { res = false }
        }
    }
    return res
}

def isInMode(modeList) {
    if (modeList) {
        //log.debug "mode (${location.mode}) in list: ${modeList} | result: (${location?.mode in modeList})"
        return location.mode.toString() in modeList
    }
    return false
}

def minDevVersions() {
    return ["thermostat":301, "protect":301, "presence":301, "weather":301, "camera":101]
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
    def result = "Not Set"
    if(input && enumName) {
        enumName.each { item ->
            if(item?.key.toString() == inputName?.toString()) {
                result = item?.value
            }
        }
    }
    return result
}

def getShowProtAlarmEvts() { return showProtAlarmStateEvts ? true : false }

/******************************************************************************
|                			Application Pages				                  |
*******************************************************************************/
def pollPrefPage() {
    dynamicPage(name: "pollPrefPage", install: false) {
        section("") {
            paragraph "Polling Preferences", image: getAppImg("timer_icon.png")
        }
        section("Device Polling:") {
            def pollValDesc = !settings?.pollValue ? "Default: 3 Minutes" : settings?.pollValue
            input ("pollValue", "enum", title: "Device Poll Rate\nDefault is (3 Minutes)", required: false, defaultValue: 180, metadata: [values:pollValEnum()],
                    description: pollValDesc, submitOnChange: true)
        }
        section("Location Polling:") {
            def pollStrValDesc = !settings?.pollStrValue ? "Default: 3 Minutes" : settings?.pollStrValue
            input ("pollStrValue", "enum", title: "Location Poll Rate\nDefault is (3 Minutes)", required: false, defaultValue: 180, metadata: [values:pollValEnum()],
                    description: pollStrValDesc, submitOnChange: true)
        }
        if(atomicState?.weatherDevice) {
            section("Weather Polling:") {
                def pollWeatherValDesc = !settings?.pollWeatherValue ? "Default: 15 Minutes" : settings?.pollWeatherValue
                input ("pollWeatherValue", "enum", title: "Weather Refresh Rate\nDefault is (15 Minutes)", required: false, defaultValue: 900, metadata: [values:notifValEnum(false)],
                        description: pollWeatherValDesc, submitOnChange: true)
            }
        }
        section("Wait Values:") {
            def pollWaitValDesc = !settings?.pollWaitVal ? "Default: 10 Seconds" : settings?.pollWaitVal
            input ("pollWaitVal", "enum", title: "Forced Poll Refresh Limit\nDefault is (10 sec)", required: false, defaultValue: 10, metadata: [values:waitValEnum()],
                    description: pollWaitValDesc,submitOnChange: true)
        }
        section("Advanced Polling Options:") {
            paragraph "If you are still experiencing Polling issues then you can select these devices to use there events to determine if a scheduled poll was missed\nPlease select as FEW devices as possible!\nMore devices will not make for a better polling."
            input "temperatures", "capability.temperatureMeasurement", title: "Which Temperature Sensors?", multiple: true, required: false, image: getAppImg("temperature.png")
            input "energies", "capability.energyMeter",	title: "Which Energy Meters?", multiple: true, required: false, image: getAppImg("lightning.png")
            input "powers",	"capability.powerMeter", title: "Which Power Meters?", multiple: true, required: false, image: getAppImg("power_meter.png")
        }
    }
}

def getPollingConfDesc() {
    def pStr = ""
    pStr += "Polling: (${!atomicState?.pollingOn ? "Not Active" : "Active"})"
    pStr += "\n• Device: (${getInputEnumLabel(settings?.pollValue, pollValEnum())})"
    pStr += "\n• Structure: (${getInputEnumLabel(settings?.pollStrValue, pollValEnum())})"
    pStr += atomicState?.weatherDevice ? "\n• Weather Polling: (${getInputEnumLabel(settings?.pollWeatherValue, notifValEnum())})" : ""
    return pStr
}

def notifPrefPage() {
    dynamicPage(name: "notifPrefPage", install: false) {
        def sectDesc = !location.contactBookEnabled ? "Enable push notifications below..." : "Select People or Devices to Receive Notifications..."
        section(sectDesc) {
            if(!location.contactBookEnabled) {
                input(name: "usePush", type: "bool", title: "Send Push Notitifications", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("notification_icon.png"))
            } else {
                input(name: "recipients", type: "contact", title: "Send notifications to", required: false, submitOnChange: true, image: getAppImg("recipient_icon.png")) {
                    input ("phone", "phone", title: "Phone Number to send SMS to...", description: "Phone Number", required: false, submitOnChange: true, image: getAppImg("notification_icon.png"))
                }
            }
        }

        if (settings?.recipients || settings?.phone || settings?.usePush) {
            if(settings?.recipients && !atomicState?.pushTested) {
                sendMsg("Info", "Push Notification Test Successful... Notifications have been Enabled for ${textAppName()}")
                atomicState.pushTested = true
            } else { atomicState.pushTested = true }

            section(title: "Time Restrictions") {
                href "setNotificationTimePage", title: "Silence Notifications...", description: (getNotifSchedDesc() ?: "Tap to configure..."), state: (getNotifSchedDesc() ? "complete" : null), image: getAppImg("quiet_time_icon.png")
            }
            section("Missed Poll Notification:") {
                input (name: "sendMissedPollMsg", type: "bool", title: "Send Missed Poll Messages?", defaultValue: true, submitOnChange: true, image: getAppImg("late_icon.png"))
                if(sendMissedPollMsg == null || sendMissedPollMsg) {
                    def misPollNotifyWaitValDesc = !misPollNotifyWaitVal ? "Default: 15 Minutes" : misPollNotifyWaitVal
                    input (name: "misPollNotifyWaitVal", type: "enum", title: "Time Past the missed Poll?", description: misPollNotifyWaitValDesc, required: false, defaultValue: 900, metadata: [values:notifValEnum()], submitOnChange: true)
                    if(misPollNotifyWaitVal) {
                        atomicState.misPollNotifyWaitVal = !misPollNotifyWaitVal ? 900 : misPollNotifyWaitVal.toInteger()
                        if (misPollNotifyWaitVal.toInteger() == 1000000) {
                            input (name: "misPollNotifyWaitValCust", type: "number", title: "Custom Missed Poll Value in Seconds", range: "60..86400", required: false, defaultValue: 900, submitOnChange: true)
                            if(misPollNotifyWaitValCust) { atomicState?.misPollNotifyWaitVal = misPollNotifyWaitValCust ? misPollNotifyWaitValCust.toInteger() : 900 }
                        }
                    } else { atomicState.misPollNotifyWaitVal = !misPollNotifyWaitVal ? 900 : misPollNotifyWaitVal.toInteger() }

                    def misPollNotifyMsgWaitValDesc = !misPollNotifyMsgWaitVal ? "Default: 1 Hour" : misPollNotifyMsgWaitVal
                    input (name: "misPollNotifyMsgWaitVal", type: "enum", title: "Delay before sending again?", description: misPollNotifyMsgWaitValDesc, required: false, defaultValue: 3600, metadata: [values:notifValEnum()], submitOnChange: true)
                    if(misPollNotifyMsgWaitVal) {
                        atomicState.misPollNotifyMsgWaitVal = !misPollNotifyMsgWaitVal ? 3600 : misPollNotifyMsgWaitVal.toInteger()
                        if (misPollNotifyMsgWaitVal.toInteger() == 1000000) {
                            input (name: "misPollNotifyMsgWaitValCust", type: "number", title: "Custom Msg Wait Value in Seconds", range: "60..86400", required: false, defaultValue: 3600, submitOnChange: true)
                            if(misPollNotifyMsgWaitValCust) { atomicState.misPollNotifyMsgWaitVal = misPollNotifyMsgWaitValCust ? misPollNotifyMsgWaitValCust.toInteger() : 3600 }
                        }
                    } else { atomicState.misPollNotifyMsgWaitVal = !misPollNotifyMsgWaitVal ? 3600 : misPollNotifyMsgWaitVal.toInteger() }
                }
            }
            section("App and Device Updates:") {
                input (name: "sendAppUpdateMsg", type: "bool", title: "Send for Updates...", defaultValue: true, submitOnChange: true, image: getAppImg("update_icon.png"))
                if(sendMissedPollMsg == null || sendAppUpdateMsg) {
                    def updNotifyWaitValDesc = !updNotifyWaitVal ? "Default: 2 Hours" : updNotifyWaitVal
                    input (name: "updNotifyWaitVal", type: "enum", title: "Send reminders every?", description: updNotifyWaitValDesc, required: false, defaultValue: 7200, metadata: [values:notifValEnum()], submitOnChange: true)
                    if(updNotifyWaitVal) {
                        atomicState.updNotifyWaitVal = !updNotifyWaitVal ? 7200 : updNotifyWaitVal.toInteger()
                        if (updNotifyWaitVal.toInteger() == 1000000) {
                            input (name: "updNotifyWaitValCust", type: "number", title: "Custom Missed Poll Value in Seconds", range: "30..86400", required: false, defaultValue: 7200, submitOnChange: true)
                            if(updNotifyWaitValCust) { atomicState.updNotifyWaitVal = updNotifyWaitValCust ? updNotifyWaitValCust.toInteger() : 7200 }
                        }
                    } else { atomicState.updNotifyWaitVal = !updNotifyWaitVal ? 7200 : updNotifyWaitVal.toInteger() }
                }
            }
        } else { atomicState.pushTested = false }
    }
}

def getAppNotifConfDesc() {
    def str = ""
    str += pushStatus() ? "Notifications:" : ""
    str += (pushStatus() && settings?.recipients) ? "\n • Contacts: (${settings?.recipients?.size()})" : ""
    str += (pushStatus() && settings?.usePush) ? "\n • Push Messages: Enabled" : ""
    str += (pushStatus() && sms) ? "\n • SMS: (${sms?.size()})" : ""
    str += (pushStatus() && settings?.phone) ? "\n • SMS: (${settings?.phone?.size()})" : ""
    str += (pushStatus() && getNotifSchedDesc()) ? "\n${getNotifSchedDesc()}" : ""
    return pushStatus() ? "${str}" : null
}

def devPrefPage() {
    dynamicPage(name: "devPrefPage", title: "Device Preferences", uninstall: false) {
        section("") {
            paragraph "Device Preferences", image: getAppImg("device_pref_icon.png")
        }
        if(settings?.thermostats || settings?.protects || settings?.presDevice || settings?.weatherDevice) {
            section("Device Names:") {
                def devDesc = (atomicState?.custLabelUsed || atomicState?.useAltNames) ? "Custom Labels Set...\n\nTap to Modify..." : "Tap to Configure..."
                href "devNamePage", title: "Device Names...", description: devDesc, image: getAppImg("device_name_icon.png")
            }
        }
        if(atomicState?.thermostats) {
            section("Thermostat Devices:") {
                def tempChgWaitValDesc = !tempChgWaitVal ? "Default: 4 Seconds" : tempChgWaitVal
                input ("tempChgWaitVal", "enum", title: "Manual Temp Change Delay\nDefault is (4 sec)", required: false, defaultValue: 4, metadata: [values:waitValEnum()],
                    description: tempChgWaitValDesc, submitOnChange: true)
                atomicState.needChildUpd = true
            }
        }
        if(atomicState?.protects) {
            section("Protect Devices:") {
                input "showProtActEvts", "bool", title: "Show Non-Alarm Events in Device Activity Feed?", required: false, defaultValue: true, submitOnChange: true,
                        image: getAppImg("list_icon.png")
                atomicState.needChildUpd = true
            }
        }
        if(atomicState?.presDevice) {
            section("Presence Device:") {
                paragraph "Nothing to see here yet!!!"
            }
        }
        if(atomicState?.weatherDevice) {
            section("Weather Device:") {
                href "custWeatherPage", title: "Customize Weather Location?", description: (getWeatherConfDesc() ? "${getWeatherConfDesc()}\n\nTap to Modify..." : "Tap to configure..."), image: getAppImg("weather_icon_grey.png")
                input ("weathAlertNotif", "bool", title: "Notify on Weather Alerts?", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("weather_icon.png"))
                //paragraph "Nothing to see here yet!!!"
            }
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
            input("custLocStr", "text", title: "Set Custom Weather Location?", description: "Please enter a ZipCode or 'pws:station_id'", required: false, defaultValue: defZip, submitOnChange: true,
                    image: getAppImg("weather_icon_grey.png"))
            paragraph "Valid location entries are:${validEnt}", image: getAppImg("blank_icon.png")
            atomicState.lastWeatherUpdDt = 0
            atomicState?.lastForecastUpdDt = 0
        }
    }
}

def getWeatherConfDesc() {
    def str = ""
    def defZip = getStZipCode() ? getStZipCode() : getNestZipCode()
    str += custLocStr ? "• Custom Weather Location: ${custLocStr}" : "• Default Weather Location: ${defZip}"
    return (str != "") ? "${str}" : null
}

def devCustomizePageDesc() {
    def str = ""
    def defZip = getStZipCode() ? getStZipCode() : getNestZipCode()
    str += custLocStr ? "• Custom Weather Location: ${custLocStr}" : "• Default Weather Location: ${defZip}"
    str += weathAlertNotif  ? "\n• Weather Alerts: Enabled" : ""
    return (str != "") ? "${str}" : null
}

def getDevicesDesc() {
    def str = ""
    str += settings?.thermostats ? "\n • [${settings?.thermostats?.size()}] Thermostat${(settings?.thermostats?.size() > 1) ? "s" : ""}" : ""
    str += settings?.protects ? "\n • [${settings?.protects?.size()}] Protect${(settings?.protects?.size() > 1) ? "s" : ""}" : ""
    str += settings?.cameras ? "\n • [${settings?.cameras?.size()}] Camera${(settings?.cameras?.size() > 1) ? "s" : ""}" : ""
    str += settings?.presDevice ? "\n • [1] Presence Device" : ""
    str += settings?.weatherDevice ? "\n • [1] Weather Device" : ""
    str += (!settings?.thermostats && !settings?.protects && !settings?.presDevice && !settings?.weatherDevice) ? "\n • No Devices Selected..." : ""
    return (str != "") ? str : null
}

def debugPrefPage() {
    dynamicPage(name: "debugPrefPage", install: false) {
        section ("Application Logs") {
            input (name: "appDebug", type: "bool", title: "Show App Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("log.png"))
            if (appDebug) {
                input (name: "advAppDebug", type: "bool", title: "Show Verbose Logs?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("list_icon.png"))
                LogAction("Debug Logs are Enabled...", "info", false)
            }
            else { LogAction("Debug Logs are Disabled...", "info", false) }
        }
        section ("Child Device Logs") {
            input (name: "childDebug", type: "bool", title: "Show Device Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("log.png"))
            if (childDebug) { LogAction("Device Debug Logs are Enabled...", "info", false) }
            else { LogAction("Device Debug Logs are Disabled...", "info", false) }
        }
        atomicState.needChildUpd = true
    }
}

def getAppDebugDesc() {
    def str = ""
    str += isAppDebug() ? "App Debug: (${debugStatus()})${advAppDebug ? "(Trace)" : ""}" : ""
    str += isChildDebug() ? "${isAppDebug() ? "\n" : ""}Device Debug: (${deviceDebugStatus()})" : ""
    return (str != "") ? "${str}" : null
}

def infoPage () {
    dynamicPage(name: "infoPage", title: "Help, Info and Instructions", install: false) {
        section("About this App:") {
            paragraph appInfoDesc(), image: getAppImg("nest_manager%402x.png", true)
        }
        section("Help and Instructions:") {
            href url: getReadmePageUrl(), style:"embedded", required:false, title:"Readme File",
                description:"View the Projects Readme File...", state: "complete", image: getAppImg("readme_icon.png")
            href url: getHelpPageUrl(), style:"embedded", required:false, title:"Help Pages",
                description:"View the Help and Instructions Page...", state: "complete", image: getAppImg("help_icon.png")
        }
        section("Donations:") {
            href url: textDonateLink(), style:"external", required: false, title:"Donations",
                description:"Tap to Open in Mobile Browser...", state: "complete", image: getAppImg("donate_icon.png")
        }
        section("Created by:") {
            paragraph "Anthony S. (@tonesto7)", state: "complete"
        }
        section("Collaborators:") {
            paragraph "Ben W. (@desertblade)\nEric S. (@E_Sch)", state: "complete"
        }
        section("App Revision History:") {
            href "changeLogPage", title: "View App Change Log Info", description: "Tap to View...", image: getAppImg("change_log_icon.png")
        }
        if(atomicState?.installationId) {
            section("InstallationID:") {
                paragraph "InstallationID:\n${atomicState?.installationId}"
            }
        }
        section("Licensing Info:") {
            paragraph "${textCopyright()}\n${textLicense()}"
        }
    }
}

def changeLogPage () {
    dynamicPage(name: "changeLogPage", title: "View Change Info", install: false) {
        section("App Revision History:") {
            paragraph appVerInfo()
        }
    }
}

def uninstallPage() {
    dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        section("") {
            if(parent) {
                paragraph "This will uninstall the ${app?.label} Automation!!!"
            } else {
                paragraph "This will uninstall the App, All Automation Apps and Child Devices.\n\nPlease make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove."
            }
        }
    }
}

/******************************************************************************
*                			  NEST LOGIN PAGES                  	  		  *
*******************************************************************************/
def nestLoginPrefPage () {
    if (!atomicState?.authToken) {
        return authPage()
    } else {
        return dynamicPage(name: "nestLoginPrefPage", nextPage: atomicState?.authToken ? "" : "authPage", install: false) {
            section("Authorization Info:") {
                paragraph "Token Created:\n• ${atomicState?.tokenCreatedDt.toString() ?: "Not Found..."}"
                paragraph "Token Expires:\n• ${atomicState?.tokenExpires ? "Never" : "Not Found..."}"
                paragraph "Last Connection:\n• ${atomicState.lastDevDataUpd ? atomicState?.lastDevDataUpd.toString() : ""}"
            }
            section("Nest Login Preferences:") {
                href "nestTokenResetPage", title: "Log Out and Reset your Nest Token", description: "Tap to Reset the Token...", required: true, state: null, image: getAppImg("reset_icon.png")
            }
        }
    }
}

def nestTokenResetPage() {
    return dynamicPage(name: "nestTokenResetPage", install: false) {
        section ("Resetting Nest Token...") {
            revokeNestToken()
            atomicState.authToken = null
            paragraph "Token has been reset...\nPress Done to return to Login page..."
        }
    }
}


/******************************************************************************
*                			  NEST API INFO PAGES                  	  		  *
*******************************************************************************/

def nestInfoPage () {
    dynamicPage(name: "nestInfoPage", install: false) {

        section("Nest API Data") {
            if(atomicState?.structures) {
                href "structInfoPage", title: "Nest Location(s) Info...", description: "Tap to view...", image: getAppImg("nest_structure_icon.png")
            }
            if (atomicState?.thermostats) {
                href "tstatInfoPage", title: "Nest Thermostat(s) Info...", description: "Tap to view...", image: getAppImg("nest_like.png")
            }
            if (atomicState?.protects) {
                href "protInfoPage", title: "Nest Protect(s) Info...", description: "Tap to view...", image: getAppImg("protect_icon.png")
            }
            if (atomicState?.cameras) {
                href "camInfoPage", title: "Nest Camera(s) Info...", description: "Tap to view...", image: getAppImg("camera_icon.png")
            }
            if(!atomicState?.structures && !atomicState?.thermostats && !atomicState?.protects && !atomicState?.cameras) {
                paragraph "There is nothing to show here...", image: getAppImg("instruct_icon.png")
            }
        }

        if(atomicState?.protects) {
            section("Protect Alarm Testing") {
                if(atomicState?.protects) {
                    href "alarmTestPage", title: "Test your Protect Devices\nBy Simulating Alarm Events", required: true , image: getAppImg("test_icon.png"), state: null,
                            description: "Tap to Begin"
                }
            }
        }
        section("Diagnostics") {
            href "diagPage", title: "View Diagnostic Info...", description: null, image: getAppImg("diag_icon.png")
        }
    }
}

def alarmTestPage () {
    dynamicPage(name: "alarmTestPage", install: false, uninstall: false) {
        if(atomicState?.protects) {
            section("Select Carbon/Smoke Device to Test:") {
                input(name: "alarmCoTestDevice", title:"Select the Protect to Test", type: "enum", required: false, multiple: false, submitOnChange: true,
                        description: coDesc, metadata: [values:atomicState?.protects], image: getAppImg("protect_icon.png"))
            }
            if(alarmCoTestDevice) {
                section("Select the Events to Generate:") {
                    input "alarmCoTestDeviceSimSmoke", "bool", title: "Simulate a Smoke Event?", defaultValue: false, submitOnChange: true, image: getDevImg("smoke_emergency.png")
                    input "alarmCoTestDeviceSimCo", "bool", title: "Simulate a Carbon Event?", defaultValue: false, submitOnChange: true, image: getDevImg("co_emergency.png")
                    input "alarmCoTestDeviceSimLowBatt", "bool", title: "Simulate a Low Battery Event?", defaultValue: false, submitOnChange: true, image: getDevImg("battery_low.png")
                }
                if ((alarmCoTestDeviceSimLowBatt || alarmCoTestDeviceSimCo || alarmCoTestDeviceSimSmoke)) {
                    section("Execute Selected Tests from Above:") {
                        if (!atomicState?.isAlarmCoTestActive) {
                            paragraph "WARNING: If your protect devices are used Smart Home Monitor (SHM) it will not see these as a test and will trigger any action/alarms you have configured...",
                                    required: true, state: null
                        }
                        if(alarmCoTestDeviceSimSmoke && !alarmCoTestDeviceSimCo && !alarmCoTestDeviceSimLowBatt) {
                            href "simulateTestEventPage", title: "Simulate Smoke Event", params: ["testType":"smoke"], description: "Tap to Execute Test", required: true, state: null
                        }

                        if(alarmCoTestDeviceSimCo && !alarmCoTestDeviceSimSmoke && !alarmCoTestDeviceSimLowBatt) {
                            href "simulateTestEventPage", title: "Simulate Carbon Event", params: ["testType":"co"], description: "Tap to Execute Test", required: true, state: null
                        }

                        if(alarmCoTestDeviceSimLowBatt && !alarmCoTestDeviceSimCo && !alarmCoTestDeviceSimSmoke) {
                            href "simulateTestEventPage", title: "Simulate Battery Event", params: ["testType":"battery"], description: "Tap to Execute Test", required: true, state: null
                        }
                    }
                }
                section("Instructions") {

                    if(atomicState?.isAlarmCoTestActive && (alarmCoTestDeviceSimLowBatt || alarmCoTestDeviceSimCo || alarmCoTestDeviceSimSmoke)) {
                        paragraph "FYI: Clear ALL Selected Tests to Reset for New Alarm Test", required: true, state: null
                    }

                    if (!alarmCoTestDeviceSimLowBatt && !alarmCoTestDeviceSimCo && !alarmCoTestDeviceSimSmoke) {
                        atomicState?.isAlarmCoTestActive = false
                        atomicState?.curProtTestPageData = null
                    }
                }
            }
        }
    }
}

def simulateTestEventPage(params) {
    def pName = getAutoType()
    def testType
    if(params?.testType) {
        atomicState.curProtTestType = params?.testType
        testType = params?.testType
    } else {
        testType = atomicState?.curProtTestType
    }
    dynamicPage(name: "simulateTestEventPage", refreshInterval: 10, install: false, uninstall: false) {
        if(alarmCoTestDevice) {
            def dev = getChildDevice(alarmCoTestDevice)
            def testText
            if(dev) {
                section("Testing ${dev}...") {
                    def isRun = false
                    if(!atomicState?.isAlarmCoTestActive) {
                        atomicState?.isAlarmCoTestActive = true
                        if(testType == "co") {
                            testText = "Carbon 'Detected'"
                            dev?.runCoTest()
                        }
                        else if (testType == "smoke") {
                            testText = "Smoke 'Detected'"
                            dev?.runSmokeTest()
                        }
                        else if (testType == "co") {
                            testText = "Battery 'Replace'"
                            dev?.runBatteryTest()
                        }
                        LogAction("Sending ${testText} Event to '$dev'", "info", true)
                        paragraph "Sending ${testText} Event to '$dev'", state: "complete"
                    } else {
                        paragraph "Skipping... A Test is already Running...", required: true, state: null
                    }
                }
            }
        }
    }
}

def structInfoPage () {
    dynamicPage(name: "structInfoPage", refreshInterval: 30, install: false) {
        def noShow = [ "wheres", "cameras", "thermostats", "smoke_co_alarms", "structure_id" ]
        section("") {
            paragraph "Locations", state: "complete", image: getAppImg("nest_structure_icon.png")
        }
        atomicState?.structData?.each { struc ->
            if (struc?.key == atomicState?.structures) {
                def str = ""
                def cnt = 0
                section("Location Name: ${struc?.value?.name}") {
                    def data = struc?.value.findAll { !(it.key in noShow) }
                    data?.sort().each { item ->
                        cnt = cnt+1
                        str += "${(cnt <= 1) ? "" : "\n\n"}• ${item?.key?.toString()}: (${item?.value})"
                    }
                    paragraph "${str}"
                }
            }
        }
    }
}

def tstatInfoPage () {
    dynamicPage(name: "tstatInfoPage", refreshInterval: 30, install: false) {
        def noShow = [ "where_id", "device_id", "structure_id" ]
        section("") {
            paragraph "Thermostats", state: "complete", image: getAppImg("nest_like.png")
        }
        atomicState?.thermostats?.sort().each { tstat ->
            def str = ""
            def cnt = 0
            section("Thermostat Name: ${tstat?.value}") {
                def data = atomicState?.deviceData?.thermostats[tstat?.key].findAll { !(it.key in noShow) }
                data?.sort().each { item ->
                    cnt = cnt+1
                    str += "${(cnt <= 1) ? "" : "\n\n"}• ${item?.key?.toString()}: (${item?.value})"
                }
                paragraph "${str}"
            }
        }
    }
}

def protInfoPage () {
    dynamicPage(name: "protInfoPage", refreshInterval: 30, install: false) {
        def noShow = [ "where_id", "device_id", "structure_id" ]
        section("") {
            paragraph "Protects", state: "complete", image: getAppImg("protect_icon.png")
        }
        atomicState?.protects.sort().each { prot ->
            def str = ""
            def cnt = 0
            section("Protect Name: ${prot?.value}") {
                def data = atomicState?.deviceData?.smoke_co_alarms[prot?.key].findAll { !(it.key in noShow) }
                data?.sort().each { item ->
                    cnt = cnt+1
                    str += "${(cnt <= 1) ? "" : "\n\n"}• ${item?.key?.toString()}: (${item?.value})"
                }
                paragraph "${str}"
            }
        }
    }
}

def camInfoPage () {
    dynamicPage(name: "camInfoPage", refreshInterval: 30, install: false) {
        def noShow = [ "where_id", "device_id", "structure_id" ]
        section("") {
            paragraph "Cameras", state: "complete", image: getAppImg("camera_icon.png")
        }
        atomicState?.cameras.sort().each { cam ->
            def str = ""
            def evtStr = ""
            def cnt = 0
            def cnt2 = 0
            section("Camera Name: ${cam?.value}") {
                def data = atomicState?.deviceData?.cameras[cam?.key].findAll { !(it.key in noShow) }
                data?.sort().each { item ->
                    if (item?.key != "last_event") {
                        if (item?.key in ["app_url", "web_url"]) {
                            href url: item?.value, style:"external", required: false, title: item?.key.toString().replaceAll("\\_", " ").capitalize(), description:"Tap to View in Mobile Browser...", state: "complete"
                        } else {
                            cnt = cnt+1
                            str += "${(cnt <= 1) ? "" : "\n\n"}• ${item?.key?.toString()}: (${item?.value})"
                        }
                    } else {
                        item?.value?.sort().each { item2 ->
                            if (item2?.key in ["app_url", "web_url", "image_url", "animated_image_url"]) {
                                href url: item2?.value, style:"external", required: false, title: "LastEvent: ${item2?.key.toString().replaceAll("\\_", " ").capitalize()}", description:"Tap to View in Mobile Browser...", state: "complete"
                            }
                            else {
                                cnt2 = cnt2+1
                                evtStr += "${(cnt2 <= 1) ? "" : "\n\n"}  • (LastEvent) ${item2?.key?.toString()}: (${item2?.value})"
                            }
                        }
                    }
                }
                paragraph "${str}"
                if(evtStr != "") {
                    paragraph "Last Event Data:\n\n${evtStr}"
                }
            }
        }
    }
}

def diagPage () {
    dynamicPage(name: "diagPage", install: false) {
        section("") {
            paragraph "This page will allow you to view all diagnostic data related to the apps/devices in order to assist the developer in troubleshooting...", image: getAppImg("diag_icon.png")
        }
        section("State Size Info:") {
            paragraph "Current State Usage:\n${getStateSizePerc()}% (${getStateSize()} bytes)", required: true, state: (getStateSizePerc() <= 70 ? "complete" : null),
                    image: getAppImg("progress_bar.png")
        }
        section("View Apps & Devices Data") {
            href "managAppDataPage", title:"View Manager Data", description:"Tap to view...", image: getAppImg("view_icon.png")
            href "childAppDataPage", title:"View Automations Data", description:"Tap to view...", image: getAppImg("view_icon.png")
            href "childDevDataPage", title:"View Device Data", description:"Tap to view...", image: getAppImg("view_icon.png")
            href "appParamsDataPage", title:"View AppParams Data", description:"Tap to view...", image: getAppImg("view_icon.png")
        }
        if(optInAppAnalytics || optInSendExceptions) {
            section("Analytics Data") {
                if (optInAppAnalytics) {
                    href url: getAppEndpointUrl("renderInstallData"), style:"embedded", required: false, title:"View Shared Install Data", description:"Tap to view Data...", image: getAppImg("app_analytics_icon.png")
                }
                href url: getAppEndpointUrl("renderInstallId"), style:"embedded", required: false, title:"View Your Installation ID", description:"Tap to view...", image: getAppImg("view_icon.png")
            }
        }
        section("Recent Nest Command") {
            def cmdDesc = ""
            cmdDesc += "Last Command Details:"
            cmdDesc += "\n • DateTime: (${atomicState.lastCmdSentDt ?: "Nothing found..."})"
            cmdDesc += "\n • Cmd Sent: (${atomicState.lastCmdSent ?: "Nothing found..."})"
            cmdDesc += "\n • Cmd Result: (${atomicState?.lastCmdSentStatus ?: "Nothing found..."})"

            cmdDesc += "\n\n • Totals Commands Sent: (${!atomicState?.apiCommandCnt ? 0 : atomicState?.apiCommandCnt})"
            paragraph "${cmdDesc}"
        }
    }
}

def appParamsDataPage() {
    dynamicPage(name: "appParamsDataPage", refreshInterval: 30, install: false) {
        if(atomicState?.appData) {
            atomicState?.appData?.sort().each { sec ->
                section("${sec?.key.toString().capitalize()}:") {
                    def str = ""
                    def cnt = 0
                    sec?.value.each { par ->
                        cnt = cnt+1
                        str += "${(cnt <= 1) ? "" : "\n\n"}• ${par?.key.toString()}: ${par?.value}"
                    }
                    paragraph "${str}"
                }
            }
        }
    }
}

def managAppDataPage() {
    dynamicPage(name: "managAppDataPage", refreshInterval:30, install: false) {
        def noShow = ["accessToken", "authToken" /*, "curAlerts", "curAstronomy", "curForecast", "curWeather"*/]
        section("SETTINGS DATA:") {
            def str = ""
            def cnt = 0
            def data = settings?.findAll { !(it.key in noShow) }
               data?.sort().each { item ->
                cnt = cnt+1
                str += "${(cnt <= 1) ? "" : "\n\n"}• ${item?.key.toString()}: (${item?.value})"
            }
            paragraph "${str}"
        }
        section("STATE DATA:") {
            def str = ""
            def cnt = 0
            def data = state?.findAll { !(it.key in noShow) }
            data?.sort().each { item ->
                cnt = cnt+1
                str += "${(cnt <= 1) ? "" : "\n\n"}• ${item?.key.toString()}: (${item?.value})"
            }
            paragraph "${str}"
        }
        section("APP METADATA:") {
            def str = ""
            def cnt = 0
            getMetadata()?.sort().each { item ->
                cnt = cnt+1
                str += "${(cnt <= 1) ? "" : "\n\n\n"}${item?.key.toString().toUpperCase()}:\n\n"
                def cnt2 = 0
                item?.value.sort().each { vals ->
                    cnt2 = cnt2+1
                    str += "${(cnt2 <= 1) ? "" : "\n\n"}• ${vals?.key.toString()}: (${vals?.value})"
                }
            }
            paragraph "${str}"
        }
    }
}

def childAppDataPage() {
    dynamicPage(name: "childAppDataPage", refreshInterval:30, install:false) {
        def apps = getChildApps()
        if(apps) {
            apps?.each { ca ->
                def str = ""
                section("${ca?.label.toString().capitalize()}:") {
                    str += "   ─────SETTINGS DATA─────"
                    def setData = ca?.getSettingsData()
                    setData?.sort().each { sd ->
                        str += "\n\n• ${sd?.key.toString()}: (${sd?.value})"
                    }
                    def appData = ca?.getAppStateData()
                    str += "\n\n\n  ───────STATE DATA──────"
                    appData?.sort().each { par ->
                        str += "\n\n• ${par?.key.toString()}: (${par?.value})"
                    }
                    paragraph "${str}"
                }
            }
        } else {
            section("") { paragraph "No Child Apps Installed..." }
        }
    }
}

def childDevDataPage() {
    dynamicPage(name: "childDevDataPage", refreshInterval:180, install: false) {
        getAllChildDevices().each { dev ->
            def str = ""
            section("${dev?.displayName.toString().capitalize()}:") {
                str += "  ───────STATE DATA──────"
                dev?.getDeviceStateData()?.sort().each { par ->
                    str += "\n\n• ${par?.key.toString()}: (${par?.value})"
                }
                str += "\n\n\n  ────SUPPORTED ATTRIBUTES────"
                def devData = dev?.supportedAttributes.collect { it as String }
                devData?.sort().each {
                    str += "\n\n• ${"$it" as String}: (${dev.currentValue("$it")})"
                }
                   str += "\n\n\n  ────SUPPORTED COMMANDS────"
                dev?.supportedCommands?.sort().each { cmd ->
                    //paragraph "${cmd.name}(${!cmd?.arguments ? "" : cmd?.arguments.toString().toLowerCase().replaceAll("\\[|\\]", "")})"
                    str += "\n\n• ${cmd.name}(${!cmd?.arguments ? "" : cmd?.arguments.toString().toLowerCase().replaceAll("\\[|\\]", "")})"
                }

                str += "\n\n\n  ─────DEVICE CAPABILITIES─────"
                dev?.capabilities?.sort().each { cap ->
                    str += "\n\n• ${cap}"
                }
                paragraph "${str}"
            }
        }
    }
}


/******************************************************************************
*                			Firebase Analytics Functions                  	  *
*******************************************************************************/
def createInstallDataJson() {
    try {
        generateInstallId()
        def tsVer = atomicState?.tDevVer ?: "Not Installed"
        def ptVer = atomicState?.pDevVer ?: "Not Installed"
        def cdVer = atomicState?.camDevVer ?: "Not Installed"
        def pdVer = atomicState?.presDevVer ?: "Not Installed"
        def wdVer = atomicState?.weatDevVer ?: "Not Installed"
        def versions = ["apps":["manager":appVersion()?.toString()], "devices":["thermostat":tsVer, "protect":ptVer, "camera":cdVer, "presence":pdVer, "weather":wdVer]]

        def tstatCnt = atomicState?.thermostats?.size() ?: 0
        def protCnt = atomicState?.protects?.size() ?: 0
        def camCnt = atomicState?.cameras?.size() ?: 0
        def automations = !atomicState?.installedAutomations ? "No Automations Installed" : atomicState?.installedAutomations
        def tz = getTimeZone()?.ID?.toString()
        def apiCmdCnt = !atomicState?.apiCommandCnt ? 0 : atomicState?.apiCommandCnt
        def cltType = !mobileClientType ? "Not Configured" : mobileClientType?.toString()
        def appErrCnt = !atomicState?.appExceptionCnt ? 0 : atomicState?.appExceptionCnt
        def devErrCnt = !atomicState?.childExceptionCnt ? 0 : atomicState?.childExceptionCnt
        def data = [
            "guid":atomicState?.installationId, "versions":versions, "thermostats":tstatCnt, "protects":protCnt, "cameras":camCnt, "appErrorCnt":appErrCnt, "devErrorCnt":devErrCnt,
            "automations":automations, "timeZone":tz, "apiCmdCnt":apiCmdCnt, "stateUsage":"${getStateSizePerc()}%", "mobileClient":cltType, "datetime":getDtNow()?.toString()
        ]
        def resultJson = new groovy.json.JsonOutput().toJson(data)
        return resultJson

    } catch (ex) {
        log.error "createInstallDataJson: Exception: ${ex}", ex
        sendExceptionData(ex.message, "createInstallDataJson")
    }
}

def renderInstallData() {
    try {
        def resultJson = createInstallDataJson()
        def resultString = new groovy.json.JsonOutput().prettyPrint(resultJson)
        render contentType: "application/json", data: resultString
    } catch (ex) { log.error "renderInstallData Exception: ${ex}", ex }
}

def renderInstallId() {
    try {
        def resultJson = new groovy.json.JsonOutput().toJson(atomicState?.installationId)
        render contentType: "application/json", data: resultJson
    } catch (ex) { log.error "renderInstallId Exception: ${ex}", ex }
}

def sendInstallData() {
    if (optInAppAnalytics) {
        sendFirebaseData(createInstallDataJson(), "installData/clients/${atomicState?.installationId}.json")
    }
}

def removeInstallData() {
    if (optInAppAnalytics) {
        return removeFirebaseData("installData/clients/${atomicState?.installationId}.json")
    }
}

def sendExceptionData(exMsg, methodName, isChild = false, autoType = null) {
    if(atomicState?.appData?.database?.disableExceptions == true) {
      return
    } else {
        def exCnt = 0
        def exString = "${exMsg}"
        exCnt = atomicState?.appExceptionCnt ? atomicState?.appExceptionCnt + 1 : 1
        atomicState?.appExceptionCnt = exCnt ?: 1
        if (optInSendExceptions) {
            def appType = isChild && autoType ? "automationApp/${autoType}" : "managerApp"
            def exData
            if(isChild) {
                exData = ["methodName":methodName, "automationType":autoType, "appVersion":(appVersion() ?: "Not Available"),"errorMsg":exString, "errorDt":getDtNow().toString()]
            } else {
                exData = ["methodName":methodName, "appVersion":(appVersion() ?: "Not Available"),"errorMsg":exString, "errorDt":getDtNow().toString()]
            }
            def results = new groovy.json.JsonOutput().toJson(exData)
            sendFirebaseExceptionData(results, "errorData/${appType}/${methodName}.json")
        }
    }
}

def sendChildExceptionData(devType, devVer, exMsg, methodName) {
    def exCnt = 0
    def exString = "${exMsg}"
    exCnt = atomicState?.childExceptionCnt ? atomicState?.childExceptionCnt + 1 : 1
    atomicState?.childExceptionCnt = exCnt ?: 1
    if (optInSendExceptions) {
        def exData = ["deviceType":devType, "devVersion":(devVer ?: "Not Available"), "methodName":methodName, "errorMsg":exString, "errorDt":getDtNow().toString()]
        def results = new groovy.json.JsonOutput().toJson(exData)
        sendFirebaseExceptionData(results, "errorData/${devType}/${methodName}.json")
    }
}

def sendFirebaseData(data, pathVal) {
    //log.trace "sendFirebaseData(${data}, ${pathVal}"
    def json = new groovy.json.JsonOutput().prettyPrint(data)
    def result = false
    def params = [ uri: "${getFirebaseAppUrl()}/${pathVal}", body: json.toString() ]
    try {
        httpPutJson(params) { resp ->
            //log.debug "resp: ${resp}"
            if( resp?.status == 200) {
                LogAction("sendFirebaseData: Data Sent Successfully!!!", "info", true)
                atomicState?.lastAnalyticUpdDt = getDtNow()
                result = true
            }
            else if(resp?.status == 400) {
                LogAction("sendFirebaseData: 'Bad Request' Exception: ${resp?.status}", "error", true)
            }
            else {
                LogAction("sendFirebaseData: 'Unexpected' Response: ${resp?.status}", "warn", true)
            }
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            LogAction("sendFirebaseData: 'HttpResponseException' Exception: ${ex}", "error", true)
        }
        else { log.error "sendFirebaseData: Exception: ${ex}", ex }
        sendExceptionData(ex.message, "sendFirebaseData")
    }
    return result
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
        else { log.error "sendFirebaseExceptionData: Exception: ${ex}", ex }
    }
    return result
}

def removeFirebaseData(pathVal) {
    log.trace "removeFirebaseData(${pathVal}"
    def result = true
    try {
        httpDelete(uri: "${getFirebaseAppUrl()}/${pathVal}") { resp ->
            log.debug "resp status: ${resp?.status}"
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.ResponseParseException) {
            LogAction("removeFirebaseData: Response: ${ex.message}", "info", true)
        } else {
            LogAction("removeFirebaseData: Exception: ${ex}", "error", true)
            sendExceptionData(ex.message, "removeFirebaseData")
            result = false
        }
    }
    return result
}

/////////////////////////////////////////////////////////////////////////////////////////////
/********************************************************************************************
|    Application Name: Nest Automations                                                   	|
|    Author: Anthony S. (@tonesto7)															|
|********************************************************************************************/
/////////////////////////////////////////////////////////////////////////////////////////////

def selectAutoPage() {
    //log.trace "selectAutoPage()..."
    if(!atomicState?.automationType) {
        return dynamicPage(name: "selectAutoPage", title: "Choose an Automation Type...", uninstall: false, install: false, nextPage: "mainAutoPage") {
            section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
                href "mainAutoPage", title: "Remote Temp Sensors...", description: "", params: [autoType: "remSen"], image: getAppImg("remote_sensor_icon.png")
            }
            section("Control Fan/Switch Based on Thermostat Status:") {
                href "mainAutoPage", title: "Fan Control...", description: "", params: [autoType: "fanCtrl"], image: getAppImg("fan_control_icon.png")
            }
            section("Turn Thermostat On/Off based on External Temps:") {
                href "mainAutoPage", title: "External Temp Sensors...", description: "", params: [autoType: "extTmp"], image: getAppImg("external_temp_icon.png")
            }
            section("Turn Thermostat On/Off when a Door/Window is Opened:") {
                href "mainAutoPage", title: "Contact Sensors...", description: "", params: [autoType: "conWat"], image: getAppImg("open_window.png")
            }
            section("Set Nest Presence Based on ST Modes, Presence Sensor, or Switches:") {
                href "mainAutoPage", title: "Nest Mode Automations", description: "", params: [autoType: "nMode"], image: getAppImg("mode_automation_icon.png")
            }
            section("Set Thermostats Setpoints Based on ST Modes:") {
                href "mainAutoPage", title: "Thermostat Mode Automations", description: "", params: [autoType: "tMode"], image: getAppImg("mode_setpoints_icon.png")
            }
            section("Turn Thermostat Off if Water Leak is Detected:") {
                href "mainAutoPage", title: "Leak Sensors...", description: "", params: [autoType: "leakWat"], image: getAppImg("leak_icon.png")
            }
        }
    }
    else { return mainAutoPage( [autoType: atomicState?.automationType]) }
}

def mainAutoPage(params) {
    //log.trace "mainAutoPage()"
    if (!atomicState?.tempUnit) { atomicState?.tempUnit = getTemperatureScale()?.toString() }
    if (!atomicState?.disableAutomation) { atomicState.disableAutomation = false }
    atomicState?.showHelp = (parent?.getShowHelp() != null) ? parent?.getShowHelp() : true
    def autoType = null
    //If params.autoType is not null then save to atomicState.
    if (!params?.autoType) { autoType = atomicState?.automationType }
    else { atomicState.automationType = params?.autoType; autoType = params?.autoType }

    // If the selected automation has not been configured take directly to the config page.  Else show main page
    if (autoType == "remSen" && !isRemSenConfigured())          { return remSensorPage() }
    else if (autoType == "fanCtrl" && !isFanCtrlConfigured())   { return fanControlPage() }
    else if (autoType == "extTmp" && !isExtTmpConfigured())     { return extTempPage() }
    else if (autoType == "conWat" && !isConWatConfigured())     { return contactWatchPage() }
    else if (autoType == "nMode" && !isNestModesConfigured())   { return nestModePresPage() }
    else if (autoType == "tMode" && !isTstatModesConfigured())  { return tstatModePage() }
    else if (autoType == "leakWat" && !isLeakWatConfigured())   { return leakWatchPage() }
    else if (autoType == "watchDog" && !isWatchdogConfigured()) { return watchDogPage() }

    else {
        // Main Page Entries
        def nxtPage = (atomicState?.automationType) ? "nameAutoPage" : ""
        return dynamicPage(name: "mainAutoPage", title: "Automation Config Page...", uninstall: false, install: false, nextPage: "nameAutoPage" ) {
            if(disableAutomation) {
                section() {
                    paragraph "This Automation is currently disabled!!!\nTurn it back on to to make changes or resume operation...", required: true, state: null, image: getAppImg("instruct_icon.png")
                }
            }
            if(autoType == "remSen" && !disableAutomation) {
                section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
                    if(remSenUseSunAsMode) { getSunTimeState() }
                    def remSenDescStr = ""
                    remSenDescStr += remSenRuleType ? "Rule-Type: ${getEnumValue(remSenRuleEnum(), remSenRuleType)}" : ""
                    remSenDescStr += (remSenEvalModes || remSenMotion || remSenSwitches) ? "\n\nRule Evaluation Triggers:" : ""
                    remSenDescStr += remSenTempDiffDegrees ? ("\n • Temp Threshold: (${remSenTempDiffDegrees}°${atomicState?.tempUnit})") : ""
                    remSenDescStr += remSenTstatTempChgVal ? ("\n • Change Temp: (${remSenTstatTempChgVal}°${atomicState?.tempUnit})") : ""
                    remSenDescStr += remSenMotion ? ("\n • Motion Sensors: (${remSenMotion?.size()})${remSenMotionModes ? "\n└ Mode Filters: ${remSenMotionModes ? "(${remSenMotionModes.size()})" : "(0)"}" : ""}\n└ Status: ${isMotionActive(remSenMotion) ? "(Motion)" : "(No Motion)"}") : ""
                    remSenDescStr += remSenSwitches ? ("\n • Switches: (${remSenSwitches?.size()})\n└ Trigger Type: (${getEnumValue(switchEnumVals(), remSenSwitchOpt)})") : ""
                    remSenDescStr += remSenEvalModes ? "\n • Mode Filters: (${remSenEvalModes.size()})\n└ Status: ${isInMode(remSenEvalModes) ? "Eval Allowed" : "Eval Blocked"}" : ""

                    remSenDescStr += remSenTstat ? "\n\n${remSenTstat.displayName}:" : ""
                    remSenDescStr += remSenTstat ? "\n ├ Temp: (${getDeviceTemp(remSenTstat)}°${atomicState?.tempUnit})" : ""
                    remSenDescStr += remSenTstat ? "\n ├ Mode: (${remSenTstat?.currentThermostatOperatingState.toString().capitalize()}/${remSenTstat?.currentThermostatMode.toString().capitalize()})" : ""
                    remSenDescStr += (remSenTstat && atomicState?.remSenTstatHasFan) ? "\n ├ Fan Mode: (${remSenTstat?.currentThermostatFanMode.toString().capitalize()})" : ""
                    remSenDescStr += (remSenTstat) ? "\n ├ Presence: (${getTstatPresence(remSenTstat).toString().capitalize()})" : ""
                    remSenDescStr += remSenTstat && getSafetyTemps(remSenTstat) ? "\n └ Safefy Temps: \n     └ Min: ${getSafetyTemps(remSenTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(remSenTstat).max}°${atomicState?.tempUnit}" : ""
                    remSenDescStr += (remSensorDay && remSensorNight) ? "\n\nSensor Mode:" : ""
                    remSenDescStr += (remSensorDay && remSensorNight) ? "\n• Current Sensors: (${getUseNightSensor() ? "☽ Night" : "☀ Day"})" : ""
                    remSenDescStr += (remSenUseSunAsMode && remSensorDay && remSensorNight) ? "\n• Day: ${atomicState?.sunriseTm}\n• Night: ${atomicState?.sunsetTm}" : ""
                    def tf = new SimpleDateFormat("M/d/yyyy - h:mm a")
                            tf.setTimeZone(getTimeZone())
                    def remSenNightStartDt = settings["${autoType}NightStartTime"] ? tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSz", settings["${autoType}NightStartTime"].toString())) : null
                    def remSenNightStopDt = settings["${autoType}NightStopTime"] ? tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSz", settings["${autoType}NightStopTime"].toString())) : null
                    remSenDescStr += (remSenUseTimeForMode && remSensorDay && remSensorNight && remSenNightStartDt && remSenNightStopDt) ?
                            "\n• Night Schedule:\n ├ Start: (${remSenNightStartDt})\n └ Stop: (${remSenNightStopDt})" : ""
                    //remote sensor/Day
                    def dayModeDesc = ""
                        dayModeDesc += remSensorDay ? "\n\n${!remSensorNight ? "Remote" : "Day"} Sensor:" : ""
                        dayModeDesc += remSensorDay ? "\n• Temp${(remSensorDay?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit})" : ""
                        dayModeDesc += (remSensorDay && remSensorDayModes && remSensorNight && remSensorNightModes) ? "\n• Day Modes: (${remSensorDayModes.size()})" : ""
                        dayModeDesc += (remSensorDay && (remSenDayHeatTemp || remSenDayCoolTemp)) ? "\n• Desired Temps: (H: ${remSenDayHeatTemp ?: 0}°${atomicState?.tempUnit}/C: ${remSenDayCoolTemp ?: 0}°${atomicState?.tempUnit})" : ""
                    remSenDescStr += remSensorDay ? "${dayModeDesc}" : ""
                    //remote sensor Night
                    def nightModeDesc = ""
                        nightModeDesc += remSensorNight ? "\n\nNight Sensor:" : ""
                        nightModeDesc += remSensorNight ? ("\n• Temp${(remSensorNight?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorNight)}°${atomicState?.tempUnit})") : ""
                        nightModeDesc += (remSensorNight && remSensorNightModes) ? "\n• Night Modes: (${remSensorNightModes.size()})" : ""
                        nightModeDesc += (remSensorNight && (remSenNightHeatTemp || remSenNightCoolTemp)) ? "\n• Desired Temps: (H: ${remSenNightHeatTemp ?: 0}°${atomicState?.tempUnit}/C: ${remSenNightCoolTemp ?: 0}°${atomicState?.tempUnit})" : ""
                    remSenDescStr += remSensorNight ? "${nightModeDesc}" : ""
                    remSenDescStr += getRemSenTstatFanSwitchDesc() ? "\n\n${getRemSenTstatFanSwitchDesc()}" : ""
                    def remSenDesc = (isRemSenConfigured() ? "${remSenDescStr}\n\nTap to Modify..." : null)
                    href "remSensorPage", title: "Remote Sensors Config...", description: remSenDesc ? remSenDesc : "Tap to Configure...", state: (remSenDesc ? "complete" : null), image: getAppImg("remote_sensor_icon.png")
                }
            }

            if(autoType == "fanCtrl" && !disableAutomation) {
                section("Control Fan/Switches based on Thermostat:") {
                    def fanCtrlDescStr = ""
                    fanCtrlDescStr += fanCtrlTstat ? "\n\n${fanCtrlTstat.displayName}:" : ""
                    fanCtrlDescStr += fanCtrlTstat ? "\n ├ Temp: (${getDeviceTemp(fanCtrlTstat)}°${atomicState?.tempUnit})" : ""
                    fanCtrlDescStr += fanCtrlTstat ? "\n ├ Mode: (${fanCtrlTstat?.currentThermostatOperatingState.toString().capitalize()}/${fanCtrlTstat?.currentThermostatMode.toString().capitalize()})" : ""
                    fanCtrlDescStr += (fanCtrlTstat && atomicState?.fanCtrlTstatHasFan) ? "\n ├ Fan Mode: (${fanCtrlTstat?.currentThermostatFanMode.toString().capitalize()})" : ""
                    fanCtrlDescStr += (fanCtrlTstat) ? "\n ├ Presence: (${getTstatPresence(fanCtrlTstat).toString().capitalize()})" : ""
                    fanCtrlDescStr += fanCtrlTstat && getSafetyTemps(fanCtrlTstat) ? "\n └ Safefy Temps: \n     └ Min: ${getSafetyTemps(fanCtrlTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(fanCtrlTstat).max}°${atomicState?.tempUnit}" : ""
                    fanCtrlDescStr += getFanCtrlFanSwitchDesc() ? "\n\n${getFanCtrlFanSwitchDesc()}" : ""
                    def fanCtrlDesc = (isFanCtrlConfigured() ? "${fanCtrlDescStr}\n\nTap to Modify..." : null)
                    href "fanControlPage", title: "Fan Control Config...", description: fanCtrlDesc ?: "Tap to Configure...", state: (fanCtrlDesc ? "complete" : null), image: getAppImg("fan_control_icon.png")
                }
            }

            if(autoType == "extTmp" && !disableAutomation) {
                section("Turn Thermostat On/Off based on External Temp:") {
                    def extDesc = ""
                    extDesc += extTmpTstat ? "${extTmpTstat?.label}" : ""
                    extDesc += extTmpTstat ? "\n ├ Temp: (${getDeviceTemp(extTmpTstat)}°${atomicState?.tempUnit})" : ""
                    extDesc += extTmpTstat ? "\n ├ Mode: (${extTmpTstat?.currentThermostatOperatingState.toString().capitalize()}/${extTmpTstat?.currentThermostatMode.toString().capitalize()})" : ""
                    extDesc += extTmpTstat ? "\n ├ Presence: (${getTstatPresence(extTmpTstat) == "present" ? "Home" : "Away"})" : ""
                    extDesc += extTmpTstat && getSafetyTemps(extTmpTstat) ? "\n └ Safefy Temps: \n     └ Min: ${getSafetyTemps(extTmpTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(extTmpTstat).max}°${atomicState?.tempUnit}" : ""
                    extDesc += ((extTmpUseWeather || extTmpTempSensor) && extTmpTstat) ? "\n\nTrigger Status:" : ""
                    extDesc += (!extTmpUseWeather && extTmpTempSensor && extTmpTstat) ? "\n • Using Sensor: (${getExtTmpTemperature()}°${atomicState?.tempUnit})" : ""
                    extDesc += (extTmpUseWeather && !extTmpTempSensor && extTmpTstat) ? "\n • Using Weather: (${getExtTmpTemperature()}°${atomicState?.tempUnit})" : ""
                    extDesc += extTmpDiffVal ? "\n • Temp Threshold: (${extTmpDiffVal}°${atomicState?.tempUnit})" : ""
                    extDesc += extTmpOffDelay ? "\n • Off Delay: (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})" : ""
                    extDesc += extTmpOnDelay ? "\n • On Delay: (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})" : ""
                    extDesc += extTmpTstat ? "\n • Last Mode: (${atomicState?.extTmpRestoreMode ? atomicState?.extTmpRestoreMode.toString().capitalize() : "Not Set"})" : ""
                    extDesc += (settings?."${getAutoType()}Modes" || settings?."${getAutoType()}Days" || (settings?."${getAutoType()}StartTime" && settings?."${getAutoType()}StopTime")) ?
                            "\n • Evaluation Allowed: (${autoScheduleOk(getAutoType()) ? "ON" : "OFF"})" : ""
                    extDesc += ((extTmpTempSensor || extTmpUseWeather) && extTmpTstat) ? "\n\nTap to Modify..." : ""
                    def extTmpDesc = isExtTmpConfigured() ? "${extDesc}" : null
                    href "extTempPage", title: "External Temps Config...", description: extTmpDesc ?: "Tap to Configure...", state: (extTmpDesc ? "complete" : null), image: getAppImg("external_temp_icon.png")
                }
            }

            if(autoType == "conWat" && !disableAutomation) {
                section("Turn Thermostat On/Off when a Door or Window is Opened:") {
                    def conDesc = ""
                    conDesc += conWatTstat ? "${conWatTstat?.label}" : ""
                    conDesc += conWatTstat ? "\n ├ Temp: (${getDeviceTemp(conWatTstat)}°${atomicState?.tempUnit})" : ""
                    conDesc += conWatTstat ? "\n ├ Mode: (${conWatTstat?.currentThermostatOperatingState.toString().capitalize()}/${conWatTstat?.currentThermostatMode.toString().capitalize()})" : ""
                    conDesc += conWatTstat ? "\n ├ Presence: (${getTstatPresence(conWatTstat) == "present" ? "Home" : "Away"})" : ""
                    conDesc += conWatTstat && getSafetyTemps(conWatTstat) ? "\n └ Safefy Temps: \n     └ Min: ${getSafetyTemps(conWatTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(conWatTstat).max}°${atomicState?.tempUnit}" : ""
                    conDesc += (conWatContacts && conWatTstat && conWatContactDesc()) ? "\n\n${conWatContactDesc()}" : ""
                    conDesc += (conWatContacts && conWatTstat) ? "\n\nTrigger Status:" : ""
                    conDesc += conWatOffDelay ? "\n • Off Delay: (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})" : ""
                    conDesc += conWatOnDelay ? "\n • On Delay: (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})" : ""
                    conDesc += conWatTstat ? "\n • Last Mode: (${atomicState?.conWatRestoreMode ? atomicState?.conWatRestoreMode.toString().capitalize() : "Not Set"})" : ""
                    conDesc += (settings?."${getAutoType()}Modes" || settings?."${getAutoType()}Days" || (settings?."${getAutoType()}StartTime" && settings?."${getAutoType()}StopTime")) ?
                            "\n • Evaluation Allowed: (${autoScheduleOk(getAutoType()) ? "ON" : "OFF"})" : ""
                    conDesc += (settings["${getAutoType()}AllowSpeechNotif"] && (settings["${getAutoType()}SpeechDevices"] || settings["${getAutoType()}SpeechMediaPlayer"]) && getVoiceNotifConfigDesc()) ?
                            "\n\nVoice Notifications:${getVoiceNotifConfigDesc()}" : ""
                    conDesc += (conWatContacts && conWatTstat) ? "\n\nTap to Modify..." : ""
                    def conWatDesc = isConWatConfigured() ? "${conDesc}" : null
                    href "contactWatchPage", title: "Contact Sensors Config...", description: conWatDesc ?: "Tap to Configure...", state: (conWatDesc ? "complete" : null), image: getAppImg("open_window.png")
                }
            }

            if(autoType == "nMode" && !disableAutomation) {
                section("Set Nest Presence Based on ST Modes, Presence Sensor, or Switches:") {
                    def nDesc = ""
                    nDesc += isNestModesConfigured() ? "Nest Mode:\n • Status: (${getNestLocPres().toString().capitalize()})" : ""
                    if (((!nModePresSensor && !nModeSwitch) && (nModeAwayModes && nModeHomeModes))) {
                        nDesc += nModeHomeModes ? "\n • Home Modes: (${nModeHomeModes.size()})" : ""
                        nDesc += nModeAwayModes ? "\n • Away Modes: (${nModeAwayModes.size()})" : ""
                    }
                    nDesc += (nModePresSensor && !nModeSwitch) ? "\n\n${nModePresenceDesc()}" : ""
                    nDesc += (nModeSwitch && !nModePresSensor) ? "\n • Using Switch: (State: ${isSwitchOn(nModeSwitch) ? "ON" : "OFF"})" : ""
                    nDesc += (nModeDelay && nModeDelayVal) ? "\n • Delay: ${getEnumValue(longTimeSecEnum(), nModeDelayVal)}" : ""
                    nDesc += (settings?."${getAutoType()}Modes" || settings?."${getAutoType()}Days" || (settings?."${getAutoType()}StartTime" && settings?."${getAutoType()}StopTime")) ?
                            "\n • Evaluation Allowed: (${autoScheduleOk(getAutoType()) ? "ON" : "OFF"})" : ""
                    nDesc += (nModePresSensor || nModeSwitch) || (!nModePresSensor && !nModeSwitch && (nModeAwayModes && nModeHomeModes)) ? "\n\nTap to Modify..." : ""
                    def nModeDesc = isNestModesConfigured() ? "${nDesc}" : null
                    href "nestModePresPage", title: "Nest Mode Automation Config", description: nModeDesc ?: "Tap to Configure...", state: (nModeDesc ? "complete" : null), image: getAppImg("mode_automation_icon.png")
                }
            }

            if(autoType == "tMode" && !disableAutomation) {
                section("Set Multiple Thermostat Temps based on ST Modes:") {
                    def tDesc = ""
                    //def qOpt = (settings?.nModeModes || settings?.nModeDays || (settings?.nModeStartTime && settings?.nModeStopTime)) ? "\nSchedule Options Selected..." : ""
                    tDesc += tModeTstats ? getTstatModeDesc() : ""
                    tDesc += (tModeDelay && tModeDelayVal) ? "\nDelay: ${getEnumValue(longTimeSecEnum(), tModeDelayVal)}" : ""
                    tDesc += tModeTstats ? "\n\nTap to Modify..." : ""
                    def tModeDesc = isTstatModesConfigured() ? "${tDesc}" : null
                    href "tstatModePage", title: "Thermostat Mode Automation Config", description: tModeDesc ?: "Tap to Configure...", state: (tModeDesc ? "complete" : null), image: getAppImg("mode_setpoints_icon.png")
                }
            }

            if(autoType == "leakWat" && !disableAutomation) {
                section("Turn Thermostat Off if Water Leak is Detected:") {
                    def leakDesc = ""
                    leakDesc += leakWatTstat ? "${leakWatTstat?.label}\n • Temp: (${getDeviceTemp(leakWatTstat)}°${atomicState?.tempUnit})" : ""
                    leakDesc += leakWatTstat ? "\n • Mode: (${leakWatTstat?.currentThermostatOperatingState.toString()}/${leakWatTstat?.currentThermostatMode.toString()})" : ""
                    leakDesc += leakWatTstat && getSafetyTemps(leakWatTstat) ? "\n • Safefy Temps: \n     • Min: ${getSafetyTemps(leakWatTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(leakWatTstat).max}°${atomicState?.tempUnit}" : ""
                    leakDesc += (leakWatSensors && leakWatTstat && leakWatSensorsDesc()) ? "\n\n${leakWatSensorsDesc()}" : ""
                    leakDesc += (leakWatSensors && leakWatTstat) ? "\n\nTrigger Status:" : ""
                    //  leakDesc += leakWatOffDelay ? "\n • Off Delay: (${getEnumValue(longTimeSecEnum(), leakWatOffDelay)})" : ""
                    leakDesc += leakWatOnDelay ? "\n • On Delay: (${getEnumValue(longTimeSecEnum(), leakWatOnDelay)})" : ""
                    leakDesc += leakWatTstat ? "\n • Last Mode: (${atomicState?.leakWatRestoreMode ? atomicState?.leakWatRestoreMode.toString().capitalize() : "Not Set"})" : ""
                    leakDesc += (settings?."${getAutoType()}Modes" || settings?."${getAutoType()}Days" || (settings?."${getAutoType()}StartTime" && settings?."${getAutoType()}StopTime")) ?
                            "\n • Evaluation Allowed: (${autoScheduleOk(getAutoType()) ? "ON" : "OFF"})" : ""
                    leakDesc += (settings["${getAutoType()}AllowSpeechNotif"] && (settings["${getAutoType()}SpeechDevices"] || settings["${getAutoType()}SpeechMediaPlayer"]) && getVoiceNotifConfigDesc()) ?
                            "\n\nVoice Notifications:${getVoiceNotifConfigDesc()}" : ""
                    leakDesc += (leakWatContacts && leakWatTstat) ? "\n\nTap to Modify..." : ""
                    def leakWatDesc = isLeakWatConfigured() ? "${leakDesc}" : null
                    href "leakWatchPage", title: "Leak Sensors Config...", description: leakWatDesc ?: "Tap to Configure...", state: (leakWatDesc ? "complete" : null), image: getAppImg("leak_icon.png")
                }
            }

            if(autoType == "watchDog" && !disableAutomation) {
                section("Watch your Nest Location for Events:") {
                    def watDogDesc = ""
                    watDogDesc += (settings["${getAutoType()}AllowSpeechNotif"] && (settings["${getAutoType()}SpeechDevices"] || settings["${getAutoType()}SpeechMediaPlayer"]) && getVoiceNotifConfigDesc()) ?
                            "\n\nVoice Notifications:${getVoiceNotifConfigDesc()}" : ""
                    def leakWatDesc = isWatchdogConfigured() ? "${watDogDesc}" : null
                    href "watchDogPage", title: "Nest Location Watchdog...", description: watDogDesc ?: "Tap to Configure...", state: (watDogDesc ? "complete" : null), image: getAppImg("watchdog_icon.png")
                }
            }

            if (atomicState?.isInstalled && (isRemSenConfigured() || isExtTmpConfigured() || isConWatConfigured() || isNestModesConfigured() || isTstatModesConfigured() || isWatchdogConfigured())) {
                section("Enable/Disable this Automation") {
                    input "disableAutomation", "bool", title: "Disable this Automation?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("switch_off_icon.png")
                    if(!atomicState?.disableAutomation && disableAutomation) {
                        LogAction("This Automation was Disabled at (${getDtNow()})", "info", true)
                        atomicState?.disableAutomationDt = getDtNow()
                    } else if (atomicState?.disableAutomation && !disableAutomation) {
                        LogAction("This Automation was Restored at (${getDtNow()})", "info", true)
                        atomicState?.disableAutomationDt = null
                    }
                }
                section("Debug Options") {
                    input (name: "showDebug", type: "bool", title: "Show App Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("log.png"))
                    atomicState?.showDebug = showDebug
                }
            }
        }
    }
}

def nameAutoPage() {
    dynamicPage(name: "nameAutoPage", install: true, uninstall: false) {
        section("Automation name") {
            if(getAutoType() == "watchDog") {
                paragraph "${app?.label}"
            } else {
                label title: "Name this Automation", defaultValue: "${getAutoTypeLabel()}", required: true
                paragraph "Make sure to name it something that will help you easily identify the app later."
            }
        }
    }
}

def initAutoApp() {
    if(settings["watchDogFlag"]) {
        atomicState?.automationType = "watchDog"
    }
    unschedule()
    unsubscribe()
    automationsInst()
    subscribeToEvents()
    scheduler()
    app.updateLabel(getAutoTypeLabel())
    watchDogAutomation()
}

def uninstAutomationApp() { log.trace "uninstAutomationApp..." }

def getAutoTypeLabel() {
    def type = atomicState?.automationType
    def appLbl = app?.label?.toString()
    def newName = appName() == "Nest Manager" ? "Nest Automations" : "${appName()}"
    def typeLabel = ""
    def newLbl
    def dis = disableAutomation ? "\n(Disabled)" : ""
    if (type == "remSen")           { typeLabel = "${newName} (RemoteSensor)" }
    else if (type == "fanCtrl")     { typeLabel = "${newName} (FanControl)" }
    else if (type == "extTmp")      { typeLabel = "${newName} (ExternalTemp)" }
    else if (type == "conWat")      { typeLabel = "${newName} (Contact)" }
    else if (type == "nMode")       { typeLabel = "${newName} (NestMode)" }
    else if (type == "tMode")       { typeLabel = "${newName} (TstatMode)" }
    else if (type == "leakWat")     { typeLabel = "${newName} (LeakSensor)" }
    else if (type == "watchDog")    { typeLabel = "Nest Location ${location.name} Watchdog"}

    //if(appLbl != typeLabel && appLbl != "Nest Manager" && !appLbl?.contains("(Disabled)")) {
    if(appLbl != "Nest Manager") {
        if(appLbl.contains("\n(Disabled)")) {
            newLbl = appLbl.replaceAll("\\\n\\(Disabled\\)", "")
        } else {
            newLbl = appLbl
        }
    } else {
        newLbl = typeLabel
    }
    return "${newLbl}${dis}"
}

def getAppStateData() {
    return getState()
}

def getSettingsData() {
    def sets = []
    settings?.sort().each { st ->
        sets << st
    }
    return sets
}

def getSettingVal(var) {
    def val = settings[var]
    return val ?: null
}

def automationsInst() {
    atomicState.isRemSenConfigured = isRemSenConfigured() ? true : false
    atomicState.isFanCtrlConfigured = isFanCtrlConfigured() ? true : false
    atomicState.isExtTmpConfigured = isExtTmpConfigured() ? true : false
    atomicState.isConWatConfigured = isConWatConfigured() ? true : false
    atomicState.isLeakWatConfigured = isLeakWatConfigured() ? true : false
    atomicState.isNestModesConfigured = isNestModesConfigured() ? true : false
    atomicState.isTstatModesConfigured = isTstatModesConfigured() ? true : false
    atomicState.isWatchdogConfigured = isWatchdogConfigured() ? true : false
    atomicState?.isInstalled = true
}

def getAutomationType() {
    return atomicState?.automationType ?: null
}

def getIsAutomationDisabled() {
    return disableAutomation ? true : false
}

def subscribeToEvents() {
    //Remote Sensor Subscriptions
    def autoType = getAutoType()
    if (autoType == "remSen") {
        if((remSensorDay || remSensorNight) && remSenTstat) {
            //subscribe(location, remSenLocationEvt)
            if(remSenEvalModes || remSensorDayModes || remSensorNightModes) { subscribe(location, "mode", remSenModeEvt, [filterEvents: false]) }
            if(remSensorDay) { subscribe(remSensorDay, "temperature", remSenTempSenEvt) }
            if(remSensorNight) { subscribe(remSensorNight, "temperature", remSenTempSenEvt) }
            if(remSenTstat) {
                subscribe(remSenTstat, "temperature", remSenTstatTempEvt)
                subscribe(remSenTstat, "thermostatMode", remSenTstatModeEvt)
                subscribe(remSenTstat, "presence", remSenTstatPresenceEvt)
                subscribe(remSenTstat, "thermostatOperatingState", remSenTstatOperEvt)
                subscribe(remSenTstat, "coolingSetpoint", remSenTstatCTempEvt)
                subscribe(remSenTstat, "heatingSetpoint", remSenTstatHTempEvt)
                if(remSenTstatFanSwitches) {
                    subscribe(remSenTstatFanSwitches, "switch", remSenFanSwitchEvt)
                    subscribe(remSenTstat, "thermostatFanMode", remSenTstatFanEvt)
                }
            }
            if(remSenMotion) { subscribe(remSenMotion, "motion", remSenMotionEvt) }
            if(remSenSwitches) { subscribe(remSenSwitches, "switch", remSenSwitchEvt) }
            if(remSenUseSunAsMode) {
                subscribe(location, "sunset", remSenSunEvtHandler)
                subscribe(location, "sunrise", remSenSunEvtHandler)
                subscribe(location, "sunriseTime", remSenSunEvtHandler)
                subscribe(location, "sunsetTime", remSenSunEvtHandler)
            }
        }
    }
    //Fan Control Subscriptions
    if (autoType == "fanCtrl") {
        if(fanCtrlFanSwitches) {
            subscribe(fanCtrlFanSwitches, "switch", fanCtrlFanSwitchEvt)
            subscribe(fanCtrlFanSwitches, "level", fanCtrlFanSwitchEvt)
            subscribe(fanCtrlTstat, "thermostatFanMode", fanCtrlTstatFanEvt)
            subscribe(fanCtrlTstat, "temperature", fanCtrlTstatTempEvt)
        }
    }

    //External Temp Subscriptions
    if (autoType == "extTmp") {
        if(!extTmpUseWeather && extTmpTempSensor) { subscribe(extTmpTempSensor, "temperature", extTmpTempEvt, [filterEvents: false]) }
        if(extTmpUseWeather) {
            if(parent?.getWeatherDeviceInst()) {
                def weather = parent.getWeatherDevice()
                if (weather) {
                    subscribe(weather, "temperature", extTmpTempEvt)
                    subscribe(weather, "dewpoint", extTmpDpEvt)
                }
            } else { LogAction("No weather device found", "error", true) }
        }
        if(extTmpTstat) {
            subscribe(extTmpTstat, "thermostatMode", extTmpTstatModeEvt)
            subscribe(extTmpTstat, "temperature", extTmpTstatTempEvt)
        }
    }
    //Contact Watcher Subscriptions
    if (autoType == "conWat") {
        if(conWatContacts && conWatTstat) {
            subscribe(conWatContacts, "contact", conWatContactEvt)
            subscribe(conWatTstat, "thermostatMode", conWatTstatModeEvt)
            subscribe(conWatTstat, "temperature", conWatTstatTempEvt)
        }
    }
    //Leak Watcher Subscriptions
    if (autoType == "leakWat") {
        if(leakWatSensors && leakWatTstat) {
            subscribe(leakWatSensors, "water", leakWatSensorEvt)
            subscribe(leakWatTstat, "thermostatMode", leakWatTstatModeEvt)
            subscribe(leakWatTstat, "temperature", leakWatTstatTempEvt)
        }
    }
    //Nest Mode Subscriptions
    if (autoType == "nMode") {
        if (!nModePresSensor && !nModeSwitch && (nModeHomeModes || nModeAwayModes)) { subscribe(location, "mode", nModeModeEvt, [filterEvents: false]) }
        if (nModePresSensor && !nModeSwitch) { subscribe(nModePresSensor, "presence", nModePresEvt) }
        if (nModeSwitch && !nModePresSensor) { subscribe(nModeSwitch, "switch", nModeSwitchEvt) }
    }
    //ST Thermostat Mode Subscriptions
    if (autoType == "tMode") {
        if(isTstatModesConfigured()) {
            subscribe(location, "mode", tModeModeEvt, [filterEvents: false])
            subscribe(tModeTstats, "presence", tModePresEvt)
        }
    }
    //watchDog Subscriptions
    if (autoType == "watchDog") {
        def tstats = parent?.getTstats()
        def foundTstats

        if(tstats) {
            foundTstats = tstats?.collect { dni ->
                def d1 = parent.getThermostatDevice(dni)
                if(d1) {
                    LogAction("Found: ${d1?.displayName} with (Id: ${dni?.key})", "debug", true)

                    // temperature is for DEBUG
                    subscribe(d1, "temperature", watchdogSafetyTempEvt)
                    subscribe(d1, "safetyTempExceeded", watchdogSafetyTempEvt)
                }
                return d1
            }
        }
    }
    //Alarm status monitoring
    if(settings["${autoType}AlarmDevices"]) {
        if(settings["${autoType}_Alert_1_Use_Alarm"] || settings["${autoType}_Alert_2_Use_Alarm"]) {
            subscribe(settings["${autoType}AlarmDevices"], "alarm", alarmAlertEvt)
        }
    }
}

def scheduler() {
    def random = new Random()
    def random_int = random.nextInt(60)
    def random_dint = random.nextInt(9)
    LogAction("watchDogAutomation scheduled using Cron (${random_int} ${random_dint}/30 * * * ?)", "info", true)
    schedule("${random_int} ${random_dint}/30 * * * ?", watchDogAutomation)

    def autoType = getAutoType()
    if (autoType == "remSen") {   }
    if (autoType == "extTmp") {  }
}

def watchDogAutomation() {
    LogAction("watchDogAutomation...", "trace", false)
    runAutomationEval()
}

def scheduleAutomationEval(schedtime = 20) {
    if (schedtime < 20) { schedtime = 20 }
    if (getLastAutomationSchedSec() > 14) {
        atomicState?.lastAutomationSchedDt = getDtNow()
        runIn(schedtime, "runAutomationEval", [overwrite: true])
    }
}

def getLastAutomationSchedSec() { return !atomicState?.lastAutomationSchedDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastAutomationSchedDt).toInteger() }

def runAutomationEval() {
    LogAction("runAutomationEval...", "trace", false)
    def autoType = getAutoType()
    switch(autoType) {
        case "remSen":
            if (isRemSenConfigured()) {
                remSenCheck()
            }
            break
        case "fanCtrl":
            if (isFanCtrlConfigured()) {
                fanCtrlCheck()
            }
            break
        case "extTmp":
            if(extTmpUseWeather && extTmpTstat) {
                getExtConditions()
            }
            if (isExtTmpConfigured()) {
                extTmpTempCheck()
            }
            break
        case "conWat":
            if (isConWatConfigured()) {
                conWatCheck()
            }
            break
        case "nMode":
            if (isNestModesConfigured()) {
                checkNestMode()
            }
            break
        case "tMode":
            if (isTstatModesConfigured()) {
                checkTstatMode()
            }
            break
        case "leakWat":
            if (isLeakWatConfigured()) {
                leakWatCheck()
            }
            break
        case "watchDog":
            if (isWatchdogConfigured()) {
                watchDogCheck()
            }
            break

        default:
            LogAction("runAutomationEval: Invalid Option Received... ${autoType}", "warn", true)
            break
    }
}

def getAutomationStats() {
    return [
        "lastUpdatedDt":atomicState?.lastUpdatedDt,
        "lastEvalDt":atomicState?.lastEvalDt,
        "lastEvent":atomicState?.lastEventData,
        "lastActionData":getAutoActionData(),
        "lastSchedDt":atomicState?.lastAutomationSchedDt,
        "lastExecVal":atomicState?.lastExecutionTime,
        "execAvgVal":(atomicState?.evalExecutionHistory != [] ? getAverageValue(atomicState?.evalExecutionHistory) : null)
    ]
}

def storeLastAction(actionDesc, actionDt) {
    if(actionDesc && actionDt) {
        atomicState?.lastAutoActionData = ["actionDesc":actionDesc, "dt":actionDt]
    }
}

def getAutoActionData() {
    if(atomicState?.lastAutoActionData) {
        return atomicState?.lastAutoActionData
    }
}

/******************************************************************************
|                			WATCHDOG AUTOMATION CODE	                      |
*******************************************************************************/
def watchDogPrefix() { return "watchDog" }

def watchDogPage() {
    def pName = watchDogPrefix()
    dynamicPage(name: "watchDogPage", title: "Nest Location Watchdog", uninstall: true, install: true) {
        section("Notifications:") {
            href "setNotificationPage", title: "Configure Push/Voice\nNotifications...", description: getNotifConfigDesc(), params: ["pName":pName, "allowSpeech":true, "showSchedule":true, "allowAlarm":true],
                    state: (getNotificationOptionsConf() ? "complete" : null), image: getAppImg("notification_icon.png")
        }
    }
}

def watchdogSafetyTempEvt(evt) {
    LogAction("watchDogSafetyTempEvt | Thermostat Temp: '${evt.displayName}' (${evt.value})", "trace", true)
    if(disableAutomation) { return }
    else {
        if(evt?.value == "true") {
            scheduleAutomationEval()
        }
    }
}

//
// Alarms will repeat every watDogRepateMsgDelay (1 hr default) ALL thermostats
//
def watchDogCheck() {
    if(disableAutomation) { return }
    else {
        def tstats = parent?.getTstats()
        def foundTstats
        if(tstats) {
            foundTstats = tstats?.collect { dni ->
                def d1 = parent.getThermostatDevice(dni)
                if(d1) {
                    def exceeded = d1?.currentValue("safetyTempExceeded")?.toString()
                    if (exceeded == "true") {
                        watchDogAlarmActions(d1.displayName, dni, "temp")
                        LogAction("watchDogCheck() | Thermostat: ${d1?.displayName} Temp Exceeded: ${exceeded}", "trace", true)
                    }
                    return d1
                }
            }
        }
    }
}

def watchDogAlarmActions(dev, dni, actType) {
    def allowNotif = (settings["${getAutoType()}NotificationsOn"] && (settings["${getAutoType()}NofifRecips"] || settings["${getAutoType()}NotifPhones"] || settings["${getAutoType()}UsePush"]))  ? true : false
    def allowSpeech = allowNotif && settings?."${getAutoType()}AllowSpeechNotif" ? true : false
    def allowAlarm = allowNotif && settings?."${getAutoType()}AllowAlarmNotif" ? true : false
    def evtNotifMsg = ""
    def evtVoiceMsg = ""
    switch(actType) {
        case "temp":
            evtNotifMsg = "Safety Temp has been exceeded on ${dev}."
            evtVoiceMsg = "Safety Temp has been exceeded on ${dev}."
            break
    }
    if(getLastWatDogSafetyAlertDtSec(dni) > getWatDogRepeatMsgDelayVal()) {
        LogAction("watchDogAlarmActions() | ${evtNotifMsg}", "trace", true)

        if (allowNotif) {
            sendEventPushNotifications(evtNotifMsg, "Warning")
        } else {
            sendNofificationMsg("Warning", evtNotifMsg)
        }
        if (allowSpeech) {
            sendEventVoiceNotifications(voiceNotifString(evtVoiceMsg))
        }
        if (allowAlarm) {
            scheduleAlarmOn()
        }
        atomicState?."lastWatDogSafetyAlertDt${dni}" = getDtNow()
    }
}

def getLastWatDogSafetyAlertDtSec(dni) { return !atomicState?."lastWatDogSafetyAlertDt{$dni}" ? 10000 : GetTimeDiffSeconds(atomicState?."lastWatDogSafetyAlertDt${dni}").toInteger() }
def getWatDogRepeatMsgDelayVal() { return !watDogRepeatMsgDelay ? 3600 : watDogRepeatMsgDelay.toInteger() }

def isWatchdogConfigured() {
    return (atomicState?.automationType == "watchDog") ? true : false
}

/******************************************************************************
|                			REMOTE SENSOR AUTOMATION CODE	                  |
*******************************************************************************/
/*
    Add in dynamic remote sensor options > Select modes for the sensor and allow current choices and triggers for each
    maybe just allow toggle for advanced options
*/

def remSenPrefix() { return "remSen" }

def remSensorPage() {
    def pName = remSenPrefix()
    dynamicPage(name: "remSensorPage", title: "Remote Sensor Automation", uninstall: false, nextPage: "mainAutoPage") {
        def req = (remSensorDay || remSensorNight || remSenTstat) ? true : false
        def dupTstat = checkThermostatDupe(remSenTstat, remSenTstatMir)
        def tStatHeatSp = getTstatSetpoint(remSenTstat, "heat")
        def tStatCoolSp = getTstatSetpoint(remSenTstat, "cool")
        def tStatMode = remSenTstat ? remSenTstat?.currentThermostatMode : "unknown"
        def tStatTemp = "${getDeviceTemp(remSenTstat)}°${atomicState?.tempUnit}"
        def locMode = location?.mode

// need to get the thermostat first, so we can narrow down options on what is available??
        section("Choose a Thermostat... ") {
            input "remSenTstat", "capability.thermostat", title: "Which Thermostat?", submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
            if(dupTstat) {
                paragraph "Duplicate Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", image: getAppImg("error_icon.png")
            }
            if(remSenTstat) {
                getTstatCapabilities(remSenTstat, remSenPrefix())
                def str = ""
                str += remSenTstat ? "Thermostat Status:" : ""
                str += remSenTstat ? "\n├ Temp: (${tStatTemp})" : ""
                str += remSenTstat ? "\n├ Mode: (${tStatMode.toString().capitalize()})" : ""
                str += (remSenTstat && atomicState?.remSenTstatHasFan) ? "\n├ FanMode: (${remSenTstat?.currentThermostatFanMode.toString().capitalize()})" : ""
                str += remSenTstat ? "\n├ Setpoints: (H: ${tStatHeatSp}°${atomicState?.tempUnit} | C: ${tStatCoolSp}°${atomicState?.tempUnit})" : ""
                str += remSenTstat ? "\n${settings?."${getAutoType()}UseSafetyTemps" ? "├" : "└"} Presence: (${getTstatPresence(remSenTstat) == "present" ? "Home" : "Away"})" : ""
                paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")

                input "remSenTstatMir", "capability.thermostat", title: "Mirror Changes to these Thermostats", description: "", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("thermostat_icon.png")
                if(remSenTstatMir && !dupTstat) {
                    remSenTstatMir?.each { t ->
                        paragraph "Thermostat Temp: ${getDeviceTemp(t)}${atomicState?.tempUnit}", image: " "
                    }
                }
            }
        }
        if(remSenTstat) {
            section("Select the Allowed (Rule) Action Type:") {
                if(!remSenRuleType) {
                    paragraph "(Rule) Actions will be used to determine the actions the automation can take when the temperature threshold is reached. Using combinations of Heat/Cool/Fan to help balance" +
                            " out the temperatures in your home in an attempt to make it more comfortable...", image: getAppImg("instruct_icon.png")
                }
                input(name: "remSenRuleType", type: "enum", title: "(Rule) Action Type", options: remSenRuleEnum(), required: true, submitOnChange: true, image: getAppImg("rule_icon.png"))
            }
        }
        if(remSenRuleType) {
            if(remSenTstat) {
                def dSenStr = !remSensorNight ? "Remote" : "Daytime"
                section("Choose $dSenStr Sensor(s) to use instead of the Thermostat's...") {
                    def dSenReq = (((remSensorNight && !remSensorDay) || !remSensorNight) && remSenTstat) ? true : false
                    input "remSensorDay", "capability.temperatureMeasurement", title: "${dSenStr} Temp Sensors", submitOnChange: true, required: dSenReq,
                            multiple: true, image: getAppImg("temperature_icon.png")
                    if(remSensorDay) {
                        def tmpVal = "$dSenStr Temp${(remSensorDay?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit})"
                        if(remSensorDay.size() > 1) {
                            href "remSenShowTempsPage", title: "View $dSenStr Sensor Temps...", description: "${tmpVal}", state: "complete", image: getAppImg("blank_icon.png")
                            //paragraph "Multiple temp sensors will return the average of those sensors.", image: getAppImg("i_icon.png")
                        } else { paragraph "${tmpVal}", state: "complete", image: getAppImg("instruct_icon.png") }

                        def tempStr = !remSensorNight ? "" : "Day "
                        if(remSenHeatTempsReq()) {
                            input "remSenDayHeatTemp", "decimal", title: "Desired ${tempStr}Heat Temp (°${atomicState?.tempUnit})", range: (atomicState?.tempUnit == "C") ? "10..32" : "50..90",
                                submitOnChange: true, required: remSenHeatTempsReq(), image: getAppImg("heat_icon.png")
                        }
                        if(remSenCoolTempsReq()) {
                            input "remSenDayCoolTemp", "decimal", title: "Desired ${tempStr}Cool Temp (°${atomicState?.tempUnit})", range: (atomicState?.tempUnit == "C") ? "10..32" : "50..90",
                                submitOnChange: true, required: remSenCoolTempsReq(), image: getAppImg("cool_icon.png")

                        }
                        paragraph "Action Threshold Temp:\nThis is the temperature difference used to trigger a selected action.", image: getAppImg("instruct_icon.png")
                        input "remSenTempDiffDegrees", "decimal", title: "Action Threshold Temp (°${atomicState?.tempUnit})", required: true, defaultValue: 2.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                        if(remSenRuleType != "Circ") {
                            paragraph "Change Temp Increments:\n This is the amount the temp is adjusted +/- when a temp change is required.", image: getAppImg("instruct_icon.png")
                            input "remSenTstatTempChgVal", "decimal", title: "Change Temp Increments (°${atomicState?.tempUnit})", required: true, defaultValue: 5.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                        }
                    }
                }
                if(remSensorDay && ((!remSenHeatTempsReq() || !remSenCoolTempsReq()) || (remSenDayHeatTemp && remSenDayCoolTemp))) {
                    section("(Optional) Choose a second set of Temperature Sensor(s) to use in the Evening instead of the Thermostat's...") {
                        input "remSensorNight", "capability.temperatureMeasurement", title: "Evening Temp Sensors", description: "Tap to configure...", submitOnChange: true, required: false, multiple: true, image: getAppImg("temperature_icon.png")
                        if(remSensorNight) {
                            if(remSenHeatTempsReq()) {
                                input "remSenNightHeatTemp", "decimal", title: "Desired Evening Heat Temp (°${atomicState?.tempUnit})", range: (atomicState?.tempUnit == "C") ? "10..32" : "50..90",
                                    submitOnChange: true, required: ((remSensorNight && remSenHeatTempsReq()) ? true : false), image: getAppImg("heat_icon.png")
                            }
                            if(remSenCoolTempsReq()) {
                                input "remSenNightCoolTemp", "decimal", title: "Desired Evening Cool Temp (°${atomicState?.tempUnit})", range: (atomicState?.tempUnit == "C") ? "10..32" : "50..90",
                                    submitOnChange: true, required: ((remSensorNight && remSenCoolTempsReq()) ? true : false), image: getAppImg("cool_icon.png")
                            }
                            //paragraph " ", image: " "
                            def tmpVal = "Evening Temp${(remSensorNight?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorNight)}°${atomicState?.tempUnit})"
                            if(remSensorNight.size() > 1) {
                                href "remSenShowTempsPage", title: "View Evening Sensor Temps...", description: "${tmpVal}", state: "complete", image: getAppImg("blank_icon.png")
                                //paragraph "Multiple temp sensors will return the average temp of those sensors.", image: getAppImg("i_icon.png")
                            } else { paragraph "${tmpVal}", state: "complete", image: getAppImg("instruct_icon.png") }
                        }
                    }
                }
                if(remSensorDay && remSensorNight) {
                    section("Day/Evening Detection Options:") {
                        if(!remSenUseTimeForMode && !remSensorDayModes && !remSensorNightModes) {
                            if(remSenUseSunAsMode) { getSunTimeState() }
                            def inDesc = remSenUseSunAsMode ? "Day/Night Mode Triggers:\n• ☀ Day: ${atomicState?.sunriseTm}\n• ☽ Night: ${atomicState?.sunsetTm}" : null
                            input "remSenUseSunAsMode", "bool", title: "Use Sunrise/Sunset to Determine Day/Night Sensors?", description: inDesc ?: "", required: false, defaultValue: false, submitOnChange: true, state: inDesc ? "complete" : null,
                                    image: getAppImg("sunrise_icon.png")
                        }

                        if(!remSenUseSunAsMode && (!remSensorDayModes || !remSensorNightModes)) {
                            input "remSenUseTimeForMode", "bool", title: "Set Start/End Time to use Night Sensors?", description: "", required: false, defaultValue: false, submitOnChange: true, state: inDesc ? "complete" : null,
                                    image: getAppImg("sunrise_icon.png")
                            if(remSenUseTimeForMode) {
                                def nightTimeReq = (settings["${pName}NightStartTime"] || settings["${pName}NightStopTime"]) ? true : false
                                def timeChkErr = settings["${pName}NightStopTime"] > settings["${pName}NightStopTime"] ? true : false
                                if(timeChkErr) {
                                    paragraph "Stop Time is before Start Time!!!.  Please Correct...", state: null, required: true, image: getAppImg("error_icon.png")
                                }
                                input "${pName}NightStartTime", "time", title: "Night Start time", required: nightTimeReq, submitOnChange: true, image: getAppImg("start_time_icon.png")
                                input "${pName}NightStopTime", "time", title: "Night Stop time", required: nightTimeReq, submitOnChange: true, image: getAppImg("stop_time_icon.png")
                            }
                        }
                        if(!remSenUseSunAsMode && !remSenUseTimeForMode) {
                            if(checkModeDuplication(remSensorDayModes, remSensorNightModes)) {
                                paragraph "Duplicate Mode(s) found under the Day or Evening Sensor!!!.  Please Correct...", image: getAppImg("error_icon.png")
                            }
                            def modesReq = (!remSenUseSunAsMode && !remSenUseTimeForMode && (remSensorDay && remSensorNight)) ? true : false
                            input "remSensorDayModes", "mode", title: "Daytime Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                            input "remSensorNightModes", "mode", title: "Evening Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                        }
                        if(remSenUseSunAsMode || (remSenUseTimeForMode && settings["${pName}NightStartTime"] && settings["${pName}NightStopTime"]) || (remSensorDayModes && remSensorNightModes)) {
                            def str = ""
                            str += "Current Active Sensor:"
                            str += "\n └ ${getUseNightSensor() ? "Night" : "Day"} Sensor"
                            paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")
                        }
                    }
                }
                section("Turn On a Fan/Switch While your Thermostat is Running:") {
                    href "remSenTstatFanSwitchPage", title: "Control a Fan/Switch when Thermostat in Running?", description: getRemSenTstatFanSwitchDesc() ?: "", state: (getRemSenTstatFanSwitchDesc() ? "complete" : null), image: getAppImg("fan_ventilation_icon.png")
                }
                if(remSenTstat && (remSensorDay || remSensorNight)) {
                    if(remSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) {
                        section("Fan Settings:") {
                            paragraph "Default Fan Runtime is 15 minutes\nThis can be adjusted under your Nest Mobile App.", image: getAppImg("instruct_icon.png")
                            input "remSenTimeBetweenRuns", "enum", title: "Wait Time between Fan Runs?", required: true, defaultValue: 3600, metadata: [values:longTimeSecEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                        }
                    }
                    section("(Optional) Use Motion Sensors to Evaluate Temps:") {
                        input "remSenMotion", "capability.motionSensor", title: "Motion Sensors", required: false, multiple: true, submitOnChange: true, state: remSenMotion ? "complete" : null, image: getAppImg("motion_icon.png")
                        if(remSenMotion) {
                            paragraph "• Motion State: (${isMotionActive(remSenMotion) ? "Active" : "Not Active"})", state: "complete", image: getAppImg("instruct_icon.png")
                            input "remSenMotionDelayVal", "enum", title: "Delay before evaluating?", required: true, defaultValue: 60, metadata: [values:longTimeSecEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                            input "remSenMotionModes", "mode", title: "Use Motion in these Modes...", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode_icon.png")
                        }
                    }
                    section("(Optional) Use Switch Event(s) to Evaluate Temps:") {
                        input "remSenSwitches", "capability.switch", title: "Select Switches", description: "", required: false, multiple: true, submitOnChange: true, image: getAppImg("wall_switch_icon.png")
                        if(remSenSwitches) {
                            input "remSenSwitchOpt", "enum", title: "Switch Event to Trigger Evaluation?", required: true, defaultValue: 2, metadata: [values:switchEnumVals()], submitOnChange: true, image: getAppImg("settings_icon.png")
                        }
                    }
                    section("Rule Evaluation Options:") {
                        input "remSenEvalModes", "mode", title: "Only Evaluate Actions in these Modes?", description: "", multiple: true, required: false, submitOnChange: true, image: getAppImg("mode_icon.png")
                        input "remSenWaitVal", "enum", title: "Wait Time between Evaluations?", required: false, defaultValue: 60, metadata: [values:longTimeSecEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                    }
                }
            }
        }
        if(atomicState?.showHelp) {
            section("Help:") {
                href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
            }
        }
    }
}

def remSenTstatFanSwitchPage() {
    dynamicPage(name: "remSenTstatFanSwitchPage", uninstall: false) {
        section("Configure External Fans/Switches\n(3-Speed Fans Supported)") {
            input "remSenTstatFanSwitches", "capability.switch", title: "Select the Switches?", required: false, submitOnChange: true, multiple: true,
                    image: getAppImg("fan_ventilation_icon.png")
            if(remSenTstatFanSwitches) {
                paragraph "${getRemSenTstatFanSwitchDesc(false)}", state: getRemSenTstatFanSwitchDesc() ? "complete" : null, image: getAppImg("blank_icon.png")
            }
        }
        if(remSenTstatFanSwitches) {
            atomicState?.remSenTstatFanSwitchSpeedEnabled = getRemSenTstatFanSwitchesSpdChk() ? true : false
            section("Fan Event Triggers") {
                paragraph "These are all event based triggers and will not occur until the Thermostat device sends the event.  Depending on your configured Poll time it may take 1 minute or more",
                        image: getAppImg("instruct_icon.png")
                input(name: "remSenTstatFanSwitchTriggerType", type: "enum", title: "Control Switches When?", defaultValue: 1, metadata: [values:switchRunEnum()],
                    submitOnChange: true, image: getAppImg("${remSenTstatFanSwitchTriggerType == 1 ? "thermostat" : "home_fan"}_icon.png"))
                input(name: "remSenTstatFanSwitchHvacModeFilter", type: "enum", title: "Thermostat Mode Triggers?", defaultValue: "any", metadata: [values:fanModeTrigEnum()],
                        submitOnChange: true, image: getAppImg("mode_icon.png"))
            }
            if(atomicState?.remSenTstatFanSwitchSpeedEnabled) {
                section("Fan Speed Options") {
                    input(name: "remSenTstatFanSwitchSpeedCtrl", type: "bool", title: "Enable Speed Control?", defaultValue: (atomicState?.remSenTstatFanSwitchSpeedEnabled ? true : false), submitOnChange: true, image: getAppImg("speed_knob_icon.png"))
                    if(remSenTstatFanSwitchSpeedCtrl) {
                        input "remSenTstatFanSwitchLowSpeed", "decimal", title: "Low Speed Threshold (°${atomicState?.tempUnit})", required: true, defaultValue: 1.0, submitOnChange: true, image: getAppImg("fan_low_speed.png")
                        input "remSenTstatFanSwitchMedSpeed", "decimal", title: "Medium Speed Threshold (°${atomicState?.tempUnit})", required: true, defaultValue: 2.0, submitOnChange: true, image: getAppImg("fan_med_speed.png")
                        input "remSenTstatFanSwitchHighSpeed", "decimal", title: "High Speed Threshold (°${atomicState?.tempUnit})", required: true, defaultValue: 4.0, submitOnChange: true, image: getAppImg("fan_high_speed.png")
                    }
                }
            }
        }
    }
}

def getRemSenTstatFanSwitchDesc(showOpt = true) {
    def swDesc = ""
    def swCnt = 0
    if(showOpt) {
        swDesc += (remSenTstatFanSwitches && (remSenTstatFanSwitchSpeedCtrl || remSenTstatFanSwitchTriggerType || remSenTstatFanSwitchHvacModeFilter)) ? "Fan Switch Config:" : ""
    }
    swDesc += remSenTstatFanSwitches ? "${showOpt ? "\n" : ""}  • Fan Switches:" : ""
    def rmSwCnt = remSenTstatFanSwitches?.size() ?: 0
    remSenTstatFanSwitches?.each { sw ->
        swCnt = swCnt+1
        swDesc += "${swCnt >= 1 ? "${swCnt == rmSwCnt ? "\n └" : "\n ├"}" : "\n └"} ${sw?.label}: (${sw?.currentSwitch?.toString().capitalize()})"
        swDesc += "${checkFanSpeedSupport(sw) ? "\n   └ 3Spd (${sw?.currentValue("currentSpeed").toString()})" : ""}"
    }
    if(showOpt) {
        swDesc += (remSenTstatFanSwitches && (remSenTstatFanSwitchSpeedCtrl || remSenTstatFanSwitchTriggerType || remSenTstatFanSwitchHvacModeFilter)) ? "\n\nFan Triggers:" : ""
        swDesc += (remSenTstatFanSwitches && remSenTstatFanSwitchSpeedCtrl) ? "\n  • 3-Speed Fan Support: (Active)" : ""
        swDesc += (remSenTstatFanSwitches && remSenTstatFanSwitchTriggerType) ? "\n  • Fan Trigger: (${getEnumValue(switchRunEnum(), remSenTstatFanSwitchTriggerType)})" : ""
        swDesc += (remSenTstatFanSwitches && remSenTstatFanSwitchHvacModeFilter) ? "\n  • Hvac Mode Filter: (${getEnumValue(fanModeTrigEnum(), remSenTstatFanSwitchHvacModeFilter)})" : ""
    }
    return (swDesc == "") ? null : "${swDesc}"
}

def getRemSenTstatFanSwitchesSpdChk() {
    def devCnt = 0
    if(remSenTstatFanSwitches) {
        remSenTstatFanSwitches?.each { sw ->
            if(checkFanSpeedSupport(sw)) { devCnt = devCnt+1 }
        }
    }
    return (devCnt >= 1) ? true : false
}

//Requirements Section
def remSenCoolTempsReq() { return (remSenRuleType in [ "Cool", "Heat_Cool", "Cool_Circ", "Heat_Cool_Circ" ]) ? true : false }
def remSenHeatTempsReq() { return (remSenRuleType in [ "Heat", "Heat_Cool", "Heat_Circ", "Heat_Cool_Circ" ]) ? true : false }
def remSenDayHeatTempOk()   { return (!remSenHeatTempsReq() || (remSenHeatTempsReq() && remSenDayHeatTemp)) ? true : false }
def remSenDayCoolTempOk()   { return (!remSenCoolTempsReq() || (remSenCoolTempsReq() && remSenDayCoolTemp)) ? true : false }
def remSenNightHeatTempOk() { return (!remSenHeatTempsReq() || (remSenHeatTempsReq() && remSenNightHeatTemp)) ? true : false }
def remSenNightCoolTempOk() { return (!remSenCoolTempsReq() || (remSenCoolTempsReq() && remSenNightCoolTemp)) ? true : false }

def isRemSenConfigured() {
    def devOk = ((remSensorDay || (remSensorDay && remSensorNight)) && remSenTstat) ? true : false
    def nightOk = ((!remSensorNight && remSensorDay) || (remSensorDay && remSensorNight && remSenNightCoolTempOk() && remSenNightHeatTempOk())) ? true : false
    def dayOk = ((remSensorDay && !remSensorNight) || ((remSensorDay || (remSensorDay && remSensorNight)) && remSenDayHeatTempOk() && remSenDayHeatTempOk())) ? true : false
    //log.debug "devOk: $devOk | nightOk: $nightOk | dayOk: $dayOk"
    return (devOk && nightOk && dayOk) ? true : false
}

def remSenMotionEvt(evt) {
    LogAction("RemoteSensor Event | Motion Sensor: ${evt?.displayName} Motion State is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        storeLastEventData(evt)
        def dorunIn = false
        def delay = remSenMotionDelayVal.toInteger()

        if(remSenMotionModes) {
            if(isInMode(remSenMotionModes) && remSenMotionDelayVal) {
                dorunIn = true
            } else {
                LogAction("remSenMotionEvt: Skipping Motion Check because the current mode is not allowed", "info", true)
            }
        }
        else {
            dorunIn = true
        }
        if (dorunIn) {
            if (delay > 20) {
                LogAction("remSenMotionEvt: Scheduling Motion Check for (${remSenMotionDelayVal} Seconds)", "info", true)
                scheduleAutomationEval(delay)
            } else { scheduleAutomationEval() }
        }
    }
}

def remSenCheckMotion() {
    if(isMotionActive(remSenMotion)) { scheduleAutomationEval() }
}

def isMotionActive(sensors) {
    return sensors?.currentState("motion")?.value.contains("active") ? true : false
}

def remSenTempSenEvt(evt) {
    LogAction("RemoteSensor Event | Sensor Temp: ${evt?.displayName} - Temperature is (${evt?.value}°${atomicState?.tempUnit})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenTstatTempEvt(evt) {
    LogAction("RemoteSensor Event | Thermostat Temp: ${evt?.displayName} - Temperature is (${evt?.value}°${atomicState?.tempUnit})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenTstatModeEvt(evt) {
    LogAction("RemoteSensor Event | Thermostat Mode: ${evt?.displayName} - Mode is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenTstatPresenceEvt(evt) {
    LogAction("RemoteSensor Event | Presence: ${evt?.displayName} - Presence is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenFanSwitchEvt(evt) {
    LogAction("RemoteSensor Event | Fan Switch: ${evt?.displayName} - is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenTstatFanEvt(evt) {
    LogAction("RemoteSensor Event | Thermostat Fan: ${evt?.displayName} - Fan is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenTstatOperEvt(evt) {
    LogAction("RemoteSensor Event | Thermostat Operating State: ${evt?.displayName} - OperatingState is  (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenTstatCTempEvt(evt) {
    LogAction("RemoteSensor Event | Thermostat Cooling Setpoint: ${evt?.displayName} - Cooling Setpoint is  (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenTstatHTempEvt(evt) {
    LogAction("RemoteSensor Event | Thermostat Heating Setpoint: ${evt?.displayName} - Heating Setpoint is  (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenSunEvtHandler(evt) {
    if(disableAutomation) { return }
    if(remSenUseSunAsMode) {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def remSenSwitchEvt(evt) {
    LogAction("RemoteSensor Event | Evaluation Switch: ${evt?.displayName} is now (${evt?.value.toString().toUpperCase()})", "trace", false)
    def evtType = evt?.value?.toString()
    if(disableAutomation) { return }
    else if(remSenSwitches) {
        storeLastEventData(evt)
        def swOpt = settings?.remSenSwitchOpt
        switch(swOpt.toInteger()) {
            case 0:
                if(evtType == "off") { scheduleAutomationEval() }
                break
            case 1:
                if (evtType == "on") { scheduleAutomationEval() }
                break
            case 2:
                if(evtType in ["on", "off"]) { scheduleAutomationEval() }
                break
            default:
                LogAction("remSenSwitchEvt: Invalid Option Received... ${swOpt.toInteger()}", "warn", true)
                break
        }
    }
}

def remSenModeEvt(evt) {
    LogAction("RemoteSensor Event | ST Mode is (${evt?.value.toString().toUpperCase()})", "trace", false)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def coolingSetpointHandler(evt) { log.debug "coolingSetpointHandler()" }

def heatingSetpointHandler(evt) { log.debug "heatingSetpointHandler()" }

def getUseNightSensor() {
    def day = !remSensorDayModes ? false : isInMode(remSensorDayModes)
    def night = !remSensorNightModes ? false : isInMode(remSensorNightModes)

    if (remSenUseSunAsMode && !remSenUseTimeForMode) { return getTimeAfterSunset() }
    if (!remSenUseSunAsMode && remSenUseTimeForMode) { return getRemSenUseNightTimeOk() }
    else if (night && !day) { return true }
    else if (day && !night) { return false }
    else { return null }
}

def getRemSenUseNightTimeOk() {
    def pName = getAutoType()
    if(remSensorDayModes && remSensorNightModes && remSenUseTimeForMode && settings["${pName}NightStartTime"] && settings["${pName}NightStopTime"] && !remSenUseSunAsMode) {
        return timeOfDayIsBetween(settings?."${pName}NightStartTime", settings?."${pName}NightStopTime", new Date(), getTimeZone()) ?: false
    }
    return false
}

def getDeviceTempAvg(items) {
    def tmpAvg = []
    def tempVal = 0
    if(!items) { return tempVal }
    else if(items?.size() > 1) {
        tmpAvg = items*.currentTemperature
        if(tmpAvg && tmpAvg?.size() > 1) { tempVal = (tmpAvg?.sum().toDouble() / tmpAvg?.size().toDouble()).round(1) }
    }
    else { tempVal = getDeviceTemp(items) }
    return tempVal.toDouble()
}

def remSenShowTempsPage() {
    dynamicPage(name: "remSenShowTempsPage", uninstall: false) {
        if(remSensorDay) {
            def dSenStr = !remSensorNight ? "Remote" : "Daytime"
            section("$dSenStr Sensor Temps:") {
                def cnt = 0
                def rCnt = remSensorDay?.size()
                def str = ""
                str += "Sensor Temp (average): (${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit})\n│"
                remSensorDay?.each { t ->
                    cnt = cnt+1
                    str += "${(cnt >= 1) ? "${(cnt == rCnt) ? "\n└" : "\n├"}" : "\n└"} ${t?.label}: ${(t?.label.length() > 10) ? "\n${(rCnt == 1 || cnt == rCnt) ? "    " : "│"}└ " : ""}(${getDeviceTemp(t)}°${atomicState?.tempUnit})"
                }
                paragraph "${str}", state: "complete", image: getAppImg("temperature_icon.png")
            }
        }
        if(remSensorNight) {
            section("Night Sensor Temps:") {
                def cnt = 0
                def rCnt = remSensorNight?.size()
                def str = ""
                str += "Sensor Temp (average): ${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit}\n│"
                remSensorNight?.each { ts ->
                    cnt = cnt+1
                    str += "${(cnt >= 1) ? "${(cnt == rCnt) ? "\n└" : "\n├"}" : "\n└"} ${ts?.label}: ${(ts?.label.length() > 10) ? "\n${(rCnt == 1 || cnt == rCnt) ? "    " : "│"}└ " : ""}(${getDeviceTemp(ts)}°${atomicState?.tempUnit})"
                }
                paragraph "${str}", state: "complete", image: getAppImg("temperature_icon.png")
            }
        }
    }
}

def getTimeAfterSunset() {
    def sun = getSunriseAndSunset()
    def result = true
    if (sun) {
        def timeNow = now()
        def start = sun?.sunset.time
        def stop = sun?.sunrise.time
        //log.debug "timeNow: $timeNow | start: $start | stop: $stop"
        result = (start < stop) ? ((timeNow >= start) && (timeNow <= stop)) : ((timeNow <= stop) || (timeNow >= start))
    }
    return result
}

def getRemSenReqSetpointTemp(curTemp) {
    def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null
    def operState = remSenTstat ? remSenTstat?.currentThermostatOperatingState.toString() : null
    def opType = hvacMode.toString()

    def reqHeatSetPoint = getRemSenHeatSetTemp()
    def reqCoolSetPoint = getRemSenCoolSetTemp()

    if((hvacMode == "cool") || (operState == "cooling")) {
        opType = "cool"
    } else if((hvacMode == "heat") || (operState == "heating")) {
        opType = "heat"
    } else if(hvacMode == "auto") {
        def coolDiff = (curTemp - reqCoolSetPoint)
        def heatDiff = (curTemp - reqHeatSetPoint)
        opType = coolDiff < heatDiff ? "cool" : "heat"
    }
    def temp = (opType == "cool") ?  reqCoolSetPoint.toDouble() : reqHeatSetPoint.toDouble()
    return temp
}

def remSenTstatFanSwitchCheck() {
    //LogAction("RemoteSensor Event | Fan Switch Check", "trace", false)
    try {
        if(disableAutomation) { return }
        if(!remSenTstatFanSwitches) { return }

        //def execTime = now()
        def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null
        def curTstatOperState = remSenTstat?.currentThermostatOperatingState.toString()
        def curTstatFanMode = remSenTstat?.currentThermostatFanMode.toString()

        def reqSenHeatSetPoint = getRemSenHeatSetTemp()
        def reqSenCoolSetPoint = getRemSenCoolSetTemp()

        def curSenTemp = (remSensorDay || remSensorNight) ? getRemoteSenTemp().toDouble() : null
        def remSenReqSetPoint = getRemSenReqSetpointTemp(curSenTemp)
        def tempDiff = Math.abs(remSenReqSetPoint - curSenTemp)

        LogAction("remSenTstatFanSwitchCheck: Remote Sensor Temp: ${curSenTemp}", "info", false)
        def curTstatTemp = getDeviceTemp(remSenTstat).toDouble()
        def curCoolSetpoint = getTstatSetpoint(remSenTstat, "cool")
        def curHeatSetpoint = getTstatSetpoint(remSenTstat, "heat")
        LogAction("remSenTstatFanSwitchCheck: Thermostat Info - ( Temperature: (${curTstatTemp}) | HeatSetpoint: (${curHeatSetpoint}) | CoolSetpoint: (${curCoolSetpoint}) | HvacMode: (${hvacMode}) | OperatingState: (${curTstatOperState}) | FanMode: (${curTstatFanMode}) )", "info", false)
        LogAction("remSenTstatFanSwitchCheck: Desired Temps - Heat: ${reqSenHeatSetPoint} | Cool: ${reqSenCoolSetPoint} Requested: ${remSenReqSetPoint}", "info", false)


        def hvacFanOn = false
         //1:"Heating/Cooling", 2:"With Fan Only"

        if( remSenTstatFanSwitchTriggerType.toInteger() ==  1) {
            hvacFanOn = (curTstatOperState in ["heating", "cooling"]) ? true : false
        }
        if( remSenTstatFanSwitchTriggerType.toInteger() ==  2) {
            hvacFanOn = (curTstatFanMode in ["on", "circulate"]) ? true : false
        }
        if(remSenTstatFanSwitchHvacModeFilter != "any" && (remSenTstatFanSwitchHvacModeFilter != hvacMode)) {
            LogAction("remSenTstatFanSwitchCheck: Evaluating turn fans off Because Thermostat Mode does not Match the required Mode to Run Fans", "info", true)
            hvacFanOn = false  // force off of fans
        }

        remSenTstatFanSwitches.each { sw ->
            def swOn = (sw?.currentSwitch.toString() == "on") ? true : false
            if(hvacFanOn) {
                if(!swOn) {
                    LogAction("remSenTstatFanSwitchCheck: Fan Switch (${sw?.displayName}) is (${swOn ? "ON" : "OFF"}) | Turning '${sw.label}' Switch (ON)", "info", true)
                    storeLastAction("Turned On ($remSenTstatFanSwitches)", getDtNow())
                    sw.on()
                }
                if(checkFanSpeedSupport(sw) && atomicState?.remSenTstatFanSwitchSpeedEnabled && remSenTstatFanSwitchHighSpeed && remSenTstatFanSwitchMedSpeed && remSenTstatFanSwitchLowSpeed) {
                    def speed = sw?.currentValue("currentSpeed") ?: null

                    if(tempDiff < remSenTstatFanSwitchMedSpeed.toDouble()) {
                        if (speed != "LOW") {
                            sw.lowSpeed()
                            LogAction("remSenTstatFanSwitchCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is BELOW the Medium Speed Threshold of ($remSenTstatFanSwitchMedSpeed) | Turning '${sw.label}' Fan Switch on (LOW SPEED)", "info", true)
                            storeLastAction("Set Fan $sw to Low Speed", getDtNow())
                        }
                    }
                    else if(tempDiff >= remSenTstatFanSwitchMedSpeed.toDouble() && tempDiff < remSenTstatFanSwitchHighSpeed.toDouble()) {
                        if (speed != "MED") {
                            sw.medSpeed()
                            LogAction("remSenTstatFanSwitchCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is ABOVE the Medium Speed Threshold of ($remSenTstatFanSwitchMedSpeed) | Turning '${sw.label}' Fan Switch on (MEDIUM SPEED)", "info", true)
                            storeLastAction("Set Fan $sw to Medium Speed", getDtNow())
                        }
                    }
                    else if(tempDiff >= remSenTstatFanSwitchHighSpeed.toDouble()) {
                        if (speed != "HIGH") {
                            sw.highSpeed()
                            LogAction("remSenTstatFanSwitchCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is ABOVE the High Speed Threshold of ($remSenTstatFanSwitchHighSpeed) | Turning '${sw.label}' Fan Switch on (HIGH SPEED)", "info", true)
                            storeLastAction("Set Fan $sw to High Speed", getDtNow())
                        }
                    }
                }
            } else {
                if(swOn) {
                    LogAction("remSenTstatFanSwitchCheck: Fan Switch (${sw?.displayName}) Fan is (${swOn ? "ON" : "OFF"}) | Turning '${sw?.label}' Switch (OFF)", "info", true)
                    storeLastAction("Turned Off (${sw.label})", getDtNow())
                    sw.off()
                }
            }
        }
        //storeExecutionHistory((now()-execTime), "remSenTstatFanSwitchCheck")
    } catch (ex) {
        log.error "remSenTstatFanSwitchCheck Exception: (${ex})", ex
        parent?.sendExceptionData(ex, "remSenTstatFanSwitchCheck", true, getAutoType())
    }
}

private remSenCheck() {
    //LogAction("remSenCheck.....", "trace", false)
    if(disableAutomation) { return }
    def remWaitVal = remSenWaitVal?.toInteger() ?: 60
    if (getLastRemSenEvalSec() < remWaitVal) {
        def schChkVal = ((remWaitVal - getLastRemSenEvalSec()) < 30) ? 30 : (remWaitVal - getLastRemSenEvalSec())
        scheduleAutomationEval(schChkVal)
        LogAction("Remote Sensor: Too Soon to Evaluate Actions...Scheduling Re-Evaluation in (${schChkVal} seconds)", "info", true)
    }
    else {
        remSenEvtEval()
        remSenTstatFanSwitchCheck()
    }
}

def getLastRemSenEvalSec() { return !atomicState?.lastRemSenEval ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenEval).toInteger() }

private remSenEvtEval() {
    //LogAction("remSenEvtEval.....", "trace", false)
    if(disableAutomation) { return }
    try {
        //
        // This automation could create a virtual Nest thermostat in ST, so users could control temp (with averaging) via this thermostat
        //    This would make it easier to adjust set points and to display status to the user
        //
        def execTime = now()
        atomicState?.lastEvalDt = getDtNow()
        def home = false
        def away = false
        if (remSenTstat && getTstatPresence(remSenTstat) == "present") { home = true }
        else { away = true }

        def noGoDesc = ""
        if( (!remSensorDay && !remSensorNight) || !remSenTstat || !home) {
            noGoDesc += !remSensorDay && !remSensorNight ? "Missing Required Day or Night Sensor Selections..." : ""
            noGoDesc += !remSenTstat ? "Missing Required Thermostat device" : ""
            noGoDesc += !home ? "Ignoring because thermostat is in away mode." : ""
            LogAction("Remote Sensor NOT Evaluating...Evaluation Status: ${noGoDesc}", "warn", true)
        } else if (home) {
            //log.info "remSenEvtEval:  Evaluating Event..."
            def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null

            if(hvacMode == "off") {
                LogAction("Remote Sensor: Skipping Evaluation... The Current Thermostat Mode is 'OFF'...", "info", true)
                return
            }
            def reqSenHeatSetPoint = getRemSenHeatSetTemp()
            def reqSenCoolSetPoint = getRemSenCoolSetTemp()

            if (hvacMode in ["auto"]) {
                // check that requested setpoints make sense & notify
                def coolheatDiff = Math.abs(reqSenCoolSetPoint - reqSenHeatSetPoint)
                if( !((reqSenCoolSetPoint >= reqSenHeatSetPoint) && (coolheatDiff > 2)) ) {
                    LogAction("remSenEvtEval: Bad requested setpoints with auto mode ${reqSenCoolSetPoint} ${reqSenHeatSetPoint}...", "warn", true)
                    storeExecutionHistory((now() - execTime), "remSenEvtEval")
                    return
                }
            }

            atomicState?.lastRemSenEval = getDtNow()

            if(remSenUseSunAsMode) { getSunTimeState() }
            def threshold = !remSenTempDiffDegrees ? 2.0 : remSenTempDiffDegrees.toDouble()
            def tempChangeVal = !remSenTstatTempChgVal ? 5.0 : remSenTstatTempChgVal.toDouble()
            def maxTempChangeVal = tempChangeVal * 3
            def curTstatTemp = getDeviceTemp(remSenTstat).toDouble()
            def curSenTemp = (remSensorDay || remSensorNight) ? getRemoteSenTemp().toDouble() : null

            def curTstatOperState = remSenTstat?.currentThermostatOperatingState.toString()
            def curTstatFanMode = remSenTstat?.currentThermostatFanMode.toString()
            def fanOn = (curTstatFanMode == "on" || curTstatFanMode == "circulate") ? true : false
            def curCoolSetpoint = getTstatSetpoint(remSenTstat, "cool")
            def curHeatSetpoint = getTstatSetpoint(remSenTstat, "heat")
            def acRunning = (curTstatOperState == "cooling") ? true : false
            def heatRunning = (curTstatOperState == "heating") ? true : false

            LogAction("remSenEvtEval: Remote Sensor Rule Type: ${getEnumValue(remSenRuleEnum(), remSenRuleType)}", "info", false)
            LogAction("remSenEvtEval: Remote Sensor Temp: ${curSenTemp}", "info", false)
            LogAction("remSenEvtEval: Thermostat Info - ( Temperature: (${curTstatTemp}) | HeatSetpoint: (${curHeatSetpoint}) | CoolSetpoint: (${curCoolSetpoint}) | HvacMode: (${hvacMode}) | OperatingState: (${curTstatOperState}) | FanMode: (${curTstatFanMode}) )", "info", false)
            LogAction("remSenEvtEval: Desired Temps - Heat: ${reqSenHeatSetPoint} | Cool: ${reqSenCoolSetPoint}", "info", false)
            LogAction("remSenEvtEval: Threshold Temp: ${threshold} | Change Temp Increments: ${tempChangeVal}", "info", false)

            def modeOk = (!remSenEvalModes || (remSenEvalModes && isInMode(remSenEvalModes))) ? true : false
            if(!modeOk || !getRemSenModeOk()) {
                noGoDesc = ""
                noGoDesc += (!modeOk && getRemSenModeOk()) ? "Mode Filters were set and the current mode was not selected for Evaluation" : ""
                noGoDesc += (!getRemSenModeOk() && modeOk) ? "This mode is not one of those selected for evaluation..." : ""
                LogAction("Remote Sensor: Skipping Evaluation...Remote Sensor Evaluation Status: ${noGoDesc}", "info", true)
            }

            def chg = false
            def chgval = 0
            if (hvacMode in ["cool","auto"]) {
                //Changes Cool Setpoints
                if (remSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                    def onTemp = reqSenCoolSetPoint + threshold
                    def offTemp = reqSenCoolSetPoint
                    def turnOn = false
                    def turnOff = false

                    LogAction("Remote Sensor: COOL - (Sensor Temp: ${curSenTemp} - Sensor CoolSetpoint: ${reqSenCoolSetPoint})", "trace", false)
                    if (curSenTemp <= offTemp) {
                        turnOff = true
                    } else if (curSenTemp >= onTemp) {
                        turnOn = true
                    }

                    if(!modeOk || !getRemSenModeOk()) {
                        turnOff = true   // system should be off
                        turnOn = false
                    }

                    if (turnOff && acRunning) {
                        chgval = curTstatTemp + tempChangeVal
                        chg = true
                        LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to Turn Off Thermostat", "info", true)
                        acRunning = false
                    } else if (turnOn && !acRunning) {
                        chgval = curTstatTemp - tempChangeVal
                        chg = true
                        acRunning = true
                        LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to Turn On Thermostat", "info", true)
                    } else {
                        // logic to decide if we need to nudge thermostat to keep it on or off
                        if (acRunning) {
                            chgval = curTstatTemp - tempChangeVal
                        } else {
                            chgval = curTstatTemp + tempChangeVal
                        }
                        def coolDiff1 = Math.abs(curTstatTemp - curCoolSetpoint)
                        LogAction("Remote Sensor: COOL - coolDiff1: ${coolDiff1} tempChangeVal: ${tempChangeVal}", "trace", false)
                        if (coolDiff1 < (tempChangeVal / 2)) {
                            chg = true
                            LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to maintain state", "info", true)
                        }
                    }
                    if (chg) {
                        chgval = (chgval > (onTemp + maxTempChangeVal)) ? onTemp + maxTempChangeVal : chgval
                        chgval = (chgval < (offTemp - maxTempChangeVal)) ? offTemp - maxTempChangeVal : chgval
                        if (chgval != curCoolSetpoint) {
                            runIn(60, "remSenCheck", [overwrite: true])
                            def cHeat = null
                            if (hvacMode in ["auto"]) {
                                if (curHeatSetpoint > (chgval-5.0)) {
                                    cHeat = chgval - 5.0
                                    LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to (${cHeat}°${atomicState?.tempUnit}) to allow COOL setting", "info", true)
                                    storeLastAction("Adjusted Heat Setpoint to (${cHeat}°${atomicState?.tempUnit})", getDtNow())
                                    curHeatSetpoint =  cHeat
                                    if(remSenTstatMir) { remSenTstatMir*.setHeatingSetpoint(cHeat) }
                                }
                            }

                            if (setTstatAutoTemps(remSenTstat, chgval, cHeat)) {
                                LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to (${chgval}°${atomicState?.tempUnit}) ", "info", true)
                                storeLastAction("Adjusted Cool Setpoint to (${chgval}°${atomicState?.tempUnit})", getDtNow())
                                curCoolSetpoint = chgval
                                if(remSenTstatMir) { remSenTstatMir*.setCoolingSetpoint(chgval) }
                            }
                            storeExecutionHistory((now() - execTime), "remSenEvtEval")
                            return  // let all this take effect

                        } else {
                            LogAction("Remote Sensor: COOL - CoolSetpoint is already (${chgval}°${atomicState?.tempUnit}) ", "info", true)
                        }
                    } else {
                        LogAction("Remote Sensor: NO CHANGE TO COOL - CoolSetpoint is (${curCoolSetpoint}°${atomicState?.tempUnit}) ", "info", true)
                    }
                }
            }

            chg = false
            chgval = 0

            LogAction("remSenEvtEval: Thermostat Info - ( Temperature: (${curTstatTemp}) | HeatSetpoint: (${curHeatSetpoint}) | CoolSetpoint: (${curCoolSetpoint}) | HvacMode: (${hvacMode}) | OperatingState: (${curTstatOperState}) | FanMode: (${curTstatFanMode}) )", "info", false)

            //Heat Functions....
            if (hvacMode in ["heat", "emergency heat", "auto"]) {
                if (remSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) {
                    def onTemp = reqSenHeatSetPoint - threshold
                    def offTemp = reqSenHeatSetPoint
                    def turnOn = false
                    def turnOff = false

                    LogAction("Remote Sensor: HEAT - (Sensor Temp: ${curSenTemp} - Sensor HeatSetpoint: ${reqSenHeatSetPoint})", "trace", false)
                    if (curSenTemp <= onTemp) {
                        turnOn = true
                    } else if (curSenTemp >= offTemp) {
                        turnOff = true
                    }

                    if(!modeOk || !getRemSenModeOk()) {
                        turnOff = true   // system should be off
                        turnOn = false
                    }

                    if (turnOff && heatRunning) {
                        chgval = curTstatTemp - tempChangeVal
                        chg = true
                        LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to Turn Off Thermostat", "info", true)
                        heatRunning = false
                    } else if (turnOn && !heatRunning) {
                        chgval = curTstatTemp + tempChangeVal
                        chg = true
                        LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to Turn On Thermostat", "info", true)
                        heatRunning = true
                    } else {
                        // logic to decide if we need to nudge thermostat to keep it on or off
                        if (heatRunning) {
                            chgval = curTstatTemp + tempChangeVal
                        } else {
                            chgval = curTstatTemp - tempChangeVal
                        }
                        def heatDiff1 = Math.abs(curTstatTemp - curHeatSetpoint)
                        LogAction("Remote Sensor: HEAT - heatDiff1: ${heatDiff1} tempChangeVal: ${tempChangeVal}", "trace", false)
                        if (heatDiff1 < (tempChangeVal / 2)) {
                            chg = true
                            LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to maintain state", "info", true)
                        }
                    }
                    if (chg) {
                        chgval = (chgval < (onTemp - maxTempChangeVal)) ? onTemp - maxTempChangeVal : chgval
                        chgval = (chgval > (offTemp + maxTempChangeVal)) ? offTemp + maxTempChangeVal : chgval
                        if (chgval != curHeatSetpoint) {
                            runIn(60, "remSenCheck", [overwrite: true])
                            def cCool = null
                            if (hvacMode in ["auto"]) {
                                if (curCoolSetpoint < (chgval+5)) {
                                    cCool = chgval + 5.0
                                    LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to (${cCool}°${atomicState?.tempUnit}) to allow HEAT setting", "info", true)
                                    storeLastAction("Adjusted Cool Setpoint to (${cCool}°${atomicState?.tempUnit})", getDtNow())
                                    curCoolSetpoint = cCool
                                    if(remSenTstatMir) { remSenTstatMir*.setCoolingSetpoint(cCool) }
                                }
                            }

                            if (setTstatAutoTemps(remSenTstat, cCool, chgval)) {
                                LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to (${chgval}°${atomicState?.tempUnit})", "info", true)
                                storeLastAction("Adjusted Heat Setpoint to (${cCool}°${atomicState?.tempUnit})", getDtNow())
                                curHeatSetpoint = chgval
                                if(remSenTstatMir) { remSenTstatMir*.setHeatingSetpoint(chgval) }
                            }
                            storeExecutionHistory((now() - execTime), "remSenEvtEval")
                            return  // let all this take effect

                        } else {
                            LogAction("Remote Sensor: HEAT - HeatSetpoint is already (${chgval}°${atomicState?.tempUnit})", "info", true)
                        }
                    } else {
                        LogAction("Remote Sensor: NO CHANGE TO HEAT - HeatSetpoint is already (${curHeatSetpoint}°${atomicState?.tempUnit})", "info", true)
                    }
                }
            }

            // Determines Heat/Cool Fan Temps
            if(remSenRuleType in ["Circ", "Cool_Circ", "Heat_Circ", "Heat_Cool_Circ"]) {
                if(!modeOk || !getRemSenModeOk()) {
                    if (fanOn) {
                        LogAction("Remote Sensor: Turning OFF '${remSenTstat?.displayName}' Fan as modes do not match evaluation", "info", true)
                        storeLastAction("Turned ${remSenTstat} Fan to (Auto)", getDtNow())
                        remSenTstat?.fanAuto()
                        if(remSenTstatMir) { remSenTstatMir*.fanAuto() }

                        // with Nest, it automatically turns off fan after a defined time;  ensure we don't turn it on again
                        storeExecutionHistory((now() - execTime), "remSenEvtEval")
                        return
                    }
                }  else {
                    if (hvacMode in ["heat", "auto", "cool"]) {
                        def sTemp = getFanAutoModeTemp(hvacMode, curTstatOperState, reqSenHeatSetPoint, reqSenCoolSetPoint, curSenTemp)
                        remSenFanControl(remSenTstat, remSenTstatMir, hvacMode, curTstatOperState, curTstatFanMode, sTemp?.type?.toString(), curSenTemp, sTemp?.req?.toDouble(), threshold, fanOn)
                    }
                }
            }
        }
        else {
            //
            // if all thermostats (primary and mirrors) are Nest, then AC/HEAT & fan will be off (or set back) with away mode.
            // if thermostats were not all Nest, then non Nest units could still be on for AC/HEAT or FAN...
            // current presumption in this implementation is:
            //      they are all nests or integrated with Nest (Works with Nest) as we don't have away/home temps for each mirror thermostats.   (They could be mirrored from primary)
            //      all thermostats in an automation are in the same Nest structure, so that all react to home/away changes
            //
            LogAction("Remote Sensor: Skipping Evaluation... Thermostat is set to away...", "info", true)
        }
        storeExecutionHistory((now() - execTime), "remSenEvtEval")
    } catch (ex) {
        log.error "remSenEvtEval Exception: ${ex}", ex
        parent?.sendExceptionData(ex.message, "remSenEvtEval", true, getAutoType())
    }
}

def getFanAutoModeTemp(hvacMode, operState, reqHeatSetTemp, reqCoolSetTemp, curSenTemp) {
    def opType = hvacMode.toString()

    if((hvacMode == "cool") || (operState == "cooling")) {
        opType = "cool"
    }
    else if((hvacMode == "heat") || (operState == "heating")) {
        opType = "heat"
    }
    else if(hvacMode == "auto") {
        def coolDiff = (curSenTemp - reqCoolSetTemp)
        def heatDiff = (curSenTemp - reqHeatSetTemp)
        opType = coolDiff < heatDiff ? "cool" : "heat"
    }
    def reqTemp = (opType == "cool") ? reqCoolSetTemp : reqHeatSetTemp
    LogAction("getFanAutoModeTemp: reqTemp: ($reqTemp) | hvacMode: ${hvacMode} | opType: $opType", "debug", false)

    return ["req":reqTemp, "type":opType]
}

def getLastRemSenFanRunDtSec() { return !atomicState?.lastRemSenFanRunDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenFanRunDt).toInteger() }
def getLastRemSenFanOffDtSec() { return !atomicState?.lastRemSenFanOffDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenFanOffDt).toInteger() }


// CONTROLS THE THERMOSTAT FAN
def remSenFanControl(tstat, tstatsMir, curHvacMode, curOperState, curFanMode, operType, Double curSenTemp, Double reqSetpointTemp, Double threshold, Boolean fanOn) {
    def returnToAuto = false

    def ruleTypeOk = (remSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) ? true : false
    if (!ruleTypeOk) {
        LogAction("Remote Sensor Fan Run: The Selected Rule-Type is not valid... Skipping... | RuleType: ${remSenRuleType}", "info", true)
        return
    }

    def tstatOperStateOk = (curOperState == "idle") ? true : false
    // if ac or is on, we should put fan back to auto
    if (!tstatOperStateOk) {
        LogAction("Remote Sensor Fan Run: The Thermostat OperatingState is Currently (${curOperState?.toString().toUpperCase()})... Skipping!!!", "info", true)

        if( atomicState?.lastRemSenFanOffDt > atomicState?.lastRemSenFanRunDt) { return }
        returnToAuto = true
    }

    if(tstat) {
        def fanTempOk = getRemSenFanTempOk(curSenTemp, reqSetpointTemp, threshold, fanOn, operType)

        if(fanTempOk && !fanOn && !returnToAuto) {
            def waitTimeVal = remSenTimeBetweenRuns?.toInteger() ?: 3600
            def timeSinceLastOffOk = (getLastRemSenFanOffDtSec() > waitTimeVal) ? true : false
            if(!timeSinceLastOffOk) {
                LogAction("Remote Sensor Fan Run: Wants to RUN Fan BUT | The Time Since Last Auto command (${getLastRemSenFanOffDtSec()} Seconds) is not greater than Required value (${waitTimeVal} seconds)", "info", true)
                runIn(300, "remSenCheck", [overwrite: true])
                return
            }
            LogAction("Remote Sensor: Activating '${tstat?.displayName}'' Fan for ${operType.toString().toUpperCase()}ING Circulation...", "debug", true)
            tstat?.fanOn()
            storeLastAction("Turned ${tstat} Fan 'On'", getDtNow())
            if(tstatsMir) {
                tstatsMir?.each { mt ->
                    LogAction("Remote Sensor: Mirroring Primary Thermostat: Activating '${mt?.displayName}' Fan for ${operType.toString().toUpperCase()}ING Circulation", "debug", true)
                    mt?.fanOn()
                }
            }
            atomicState?.lastRemSenFanRunDt = getDtNow()

        } else {
            if (fanOn && (returnToAuto || !fanTempOk)) {
                if (!returnToAuto) {
                    def timeSinceLastRunOk = (getLastRemSenFanRunDtSec() > 600) ? true : false
                    if(!timeSinceLastRunOk) {
                        LogAction("Remote Sensor Fan Run: Wants to STOP Fan BUT | The Time Since Last Run (${getLastRemSenFanRunDtSec()} Seconds) is not greater than Required value (600 seconds)", "info", true)
                        runIn(120, "remSenCheck", [overwrite: true])
                        return
                    }
                }
                LogAction("Remote Sensor: Turning OFF '${remSenTstat?.displayName}' Fan that was used for ${operType.toString().toUpperCase()}ING Circulation", "info", true)
                tstat?.fanAuto()
                storeLastAction("Turned ${tstat} Fan to 'Auto'", getDtNow())
                if(tstatsMir) {
                    tstatsMir?.each { mt ->
                        LogAction("Remote Sensor: Mirroring Primary Thermostat: Turning OFF '${mt?.displayName}' Fan that was used for ${operType.toString().toUpperCase()}ING Circulation", "info", true)
                        mt?.fanAuto()
                    }
                }
                atomicState?.lastRemSenFanOffDt = getDtNow()
            }
        }
    }
}

def getRemSenFanTempOk(Double senTemp, Double reqsetTemp, Double threshold, Boolean fanOn, operType) {
    LogAction("RemSenFanTempOk Debug:", "debug", false)

    def turnOn = false
    def adjust = (getTemperatureScale() == "C") ? 0.5 : 1.0
    if (threshold > (adjust * 2.0)) {
        adjust = adjust * 2.0
    }

    if (adjust >= threshold) {
        LogAction("Remote Sensor Fan Temp: Bad threshold setting ${threshold} <= ${adjust}", "warn", true)
        return false
    }

    LogAction(" ├ adjust: ${adjust}}°${atomicState?.tempUnit}", "debug", false)
    LogAction(" ├ operType: (${operType.toString().toUpperCase()})", "debug", false)
    LogAction(" ├ Sensor Temp: ${senTemp}°${atomicState?.tempUnit} |  Requsted Setpoint Temp: ${reqsetTemp}°${atomicState?.tempUnit}", "debug", false)

    def ontemp
    def offtemp

    if (operType == "cool") {
        ontemp = reqsetTemp + threshold
        offtemp = reqsetTemp
        if ((senTemp > offtemp) && (senTemp <= (ontemp - adjust))) { turnOn = true }
    }

    if (operType == "heat") {
        ontemp = reqsetTemp - threshold
        offtemp = reqsetTemp
        if ((senTemp < offtemp) && (senTemp >= (ontemp + adjust))) { turnOn = true }
    }

    LogAction(" ├ onTemp: ${ontemp}   | offTemp: ${offtemp}}°${atomicState?.tempUnit}", "debug", false)
    LogAction(" ├ FanAlreadyOn: (${fanOn.toString().toUpperCase()})", "debug", false)
    LogAction(" ┌ Final Result: (${turnOn.toString().toUpperCase()})", "debug", false)
    LogAction("getRemSenFanTempOk: ", "debug", false)

    if(!turnOn && fanOn) {
        LogAction("Remote Sensor Fan Temp: The Temperature Difference is Outside of Threshold Limits | Turning Thermostat Fan OFF", "info", true)
    }

    if(turnOn && !fanOn) {
        LogAction("Remote Sensor Fan Temp: The Temperature Difference is within the Threshold Limit | Turning Thermostat Fan ON", "info", true)
    }

    return turnOn
}

def getRemSenTempsToList() {
    try {
        def sensors = getUseNightSensor() ? remSensorNight : remSensorDay
        if (sensors?.size() >= 1) {
            def info = []
            sensors?.sort().each {
                info.push("${it?.displayName}": " ${it?.currentTemperature.toString()}°${atomicState?.tempUnit}")
            }
            return info
        }
    } catch (ex) {
        log.error "getRemSenTempsToList Exception: ${ex}", ex
    }
}

def getRemSenModeOk() {
    def result = false
    if(remSenUseSunAsMode) { result = true }
    else if (remSenUseTimeForMode) { result = true }
    else if (remSensorDay && !remSensorNight) { result = true }
    else if (remSensorDayModes && remSensorNightModes) {
        result = (remSensorNight && getUseNightSensor()) ? (isInMode(remSensorNightModes) ? true : false) : (isInMode(remSensorDayModes) ? true : false)
    }
    //log.debug "getRemSenModeOk: $result"
    return result
}

def getDeviceTemp(dev) {
    return dev ? dev?.currentValue("temperature")?.toString().replaceAll("\\[|\\]", "").toDouble() : 0
}

def getTstatSetpoint(tstat, type) {
    if(tstat) {
        def coolSp = !tstat?.currentCoolingSetpoint ? 0 : tstat?.currentCoolingSetpoint.toDouble()
        def heatSp = !tstat?.currentHeatingSetpoint ? 0 : tstat?.currentHeatingSetpoint.toDouble()
        return (type == "cool") ? coolSp : heatSp
    }
    else { return 0 }
}

def getRemoteSenTemp() {
    if(!getUseNightSensor() && remSensorDay) {
        return getDeviceTempAvg(remSensorDay).toDouble()
    }
    else if(getUseNightSensor() && remSensorNight) {
        return getDeviceTempAvg(remSensorNight).toDouble()
    }
    else {
        log.warn "getRemoteSenTemp: No Temperature Found!!!"
        return 0.0
    }
}

def getRemSenCoolSetTemp() {
    if(!getUseNightSensor() && remSenDayCoolTemp) {
        return remSenDayCoolTemp?.toDouble()
    }
    else if(getUseNightSensor() && remSenNightCoolTemp) {
        return remSenNightCoolTemp?.toDouble()
    }
    else {
        def desiredCoolTemp = getGlobalDesiredCoolTemp()
        if (desiredCoolTemp) { return desiredCoolTemp.toDouble() }
        else { return remSenTstat ? getTstatSetpoint(remSenTstat, "cool") : 0 }
    }
}

def getRemSenHeatSetTemp() {
    if(!getUseNightSensor() && remSenDayHeatTemp) {
        return remSenDayHeatTemp?.toDouble()
    }
    else if(getUseNightSensor() && remSenNightHeatTemp) {
        return remSenNightHeatTemp?.toDouble()
    }
    else {
        def desiredHeatTemp = getGlobalDesiredHeatTemp()
        if (desiredHeatTemp) { return desiredHeatTemp.toDouble() }
        else { return remSenTstat ? getTstatSetpoint(remSenTstat, "heat") : 0 }
    }
}

def remSenRuleEnum() {
    // Determines that available rules to display based on the selected thermostats capabilites.
    def canCool = atomicState?.remSenTstatCanCool ? true : false
    def canHeat = atomicState?.remSenTstatCanHeat ? true : false
    def hasFan = atomicState?.remSenTstatHasFan ? true : false
    def vals = []
    //log.debug "remSenRuleEnum -- hasFan: $hasFan (${atomicState?.remSenTstatHasFan} | canCool: $canCool (${atomicState?.remSenTstatCanCool} | canHeat: $canHeat (${atomicState?.remSenTstatCanHeat}"
    if (canCool && !canHeat && hasFan) { vals = ["Cool":"Cool", "Circ":"Circulate(Fan)", "Cool_Circ":"Cool/Circulate(Fan)"] }
    else if (canCool && !canHeat && !hasFan) { vals = ["Cool":"Cool"] }
    else if (!canCool && canHeat && hasFan) { vals = ["Circ":"Circulate(Fan)", "Heat":"Heat", "Heat_Circ":"Heat/Circulate(Fan)"] }
    else if (!canCool && canHeat && !hasFan) { vals = ["Heat":"Heat"] }
    else if (!canCool && !canHeat && hasFan) { vals = ["Circ":"Circulate(Fan)"] }
    else if (canCool && canHeat && !hasFan) { vals = ["Heat_Cool":"Auto", "Heat":"Heat", "Cool":"Cool"] }
    else { vals = [ "Heat_Cool":"Auto", "Heat":"Heat", "Cool":"Cool", "Circ":"Circulate(Fan)", "Heat_Cool_Circ":"Auto/Circulate(Fan)", "Heat_Circ":"Heat/Circulate(Fan)", "Cool_Circ":"Cool/Circulate(Fan)" ] }
    //log.debug "remSenRuleEnum vals: $vals"
    return vals
}

/********************************************************************************
|                		    FAN CONTROL AUTOMATION CODE	     				    |
*********************************************************************************/

def fanCtrlPrefix() { return "fanCtrl" }

def fanControlPage() {
    dynamicPage(name: "fanControlPage", uninstall: false) {
        def tStatMode = fanCtrlTstat ? fanCtrlTstat?.currentThermostatMode : "unknown"
        def tStatTemp = "${getDeviceTemp(fanCtrlTstat)}°${atomicState?.tempUnit}"
        section("Control Fans/Switches based on your Thermostat\n(3-Speed Fans Supported)") {
            input "fanCtrlFanSwitches", "capability.switch", title: "Select the Switches?", required: false, submitOnChange: true, multiple: true,
                    image: getAppImg("fan_ventilation_icon.png")
            if(fanCtrlFanSwitches && fanCtrlTstat) {
                paragraph "${getFanCtrlFanSwitchDesc(false)}", state: getFanCtrlFanSwitchDesc() ? "complete" : null, image: getAppImg("blank_icon.png")
            }
        }
        section("Choose a Thermostat... ") {
            input "fanCtrlTstat", "capability.thermostat", title: "Which Thermostat?", submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
            if(fanCtrlTstat) {
                getTstatCapabilities(fanCtrlTstat, fanCtrlPrefix())
                def str = ""
                str += fanCtrlTstat ? "Thermostat Status:" : ""
                str += fanCtrlTstat ? "\n├ Temp: (${tStatTemp})" : ""
                str += fanCtrlTstat ? "\n├ Mode: (${tStatMode.toString().capitalize()})" : ""
                str += (fanCtrlTstat && atomicState?.fanCtrlTstatHasFan) ? "\n├ FanMode: (${fanCtrlTstat?.currentThermostatFanMode.toString().capitalize()})" : ""
                str += fanCtrlTstat ? "\n├ Setpoints: (H: ${fanCtrlTstat?.currentHeatingSetpoint}°${atomicState?.tempUnit} | C: ${fanCtrlTstat?.currentCoolingSetpoint}°${atomicState?.tempUnit})" : ""
                str += fanCtrlTstat ? "\n${settings?."${getAutoType()}UseSafetyTemps" ? "├" : "└"} Presence: (${getTstatPresence(fanCtrlTstat) == "present" ? "Home" : "Away"})" : ""
                paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")
            }
        }
        if(fanCtrlFanSwitches && fanCtrlTstat) {
            atomicState?.fanCtrlFanSwitchSpeedEnabled = getFanCtrlFanSwitchesSpdChk() ? true : false
            section("Fan Event Triggers") {
                paragraph "These are all event based triggers and will not occur until the Thermostat device sends the event.  Depending on your configured Poll time it may take 1 minute or more",
                        image: getAppImg("instruct_icon.png")
                input(name: "fanCtrlFanSwitchTriggerType", type: "enum", title: "Control Switches When?", defaultValue: 1, metadata: [values:switchRunEnum()],
                    submitOnChange: true, image: getAppImg("${fanCtrlFanSwitchTriggerType == 1 ? "thermostat" : "home_fan"}_icon.png"))
                input(name: "fanCtrlFanSwitchHvacModeFilter", type: "enum", title: "Thermostat Mode Triggers?", defaultValue: "any", metadata: [values:fanModeTrigEnum()],
                        submitOnChange: true, image: getAppImg("mode_icon.png"))
            }
            if(atomicState?.remSenTstatFanSwitchSpeedEnabled) {
                section("Fan Speed Options") {
                    input(name: "fanCtrlFanSwitchSpeedCtrl", type: "bool", title: "Enable Speed Control?", defaultValue: (atomicState?.remSenTstatFanSwitchSpeedEnabled ? true : false), submitOnChange: true, image: getAppImg("speed_knob_icon.png"))
                    if(remSenTstatFanSwitchSpeedCtrl) {
                        input "fanCtrlFanSwitchLowSpeed", "decimal", title: "Low Speed Threshold (°${atomicState?.tempUnit})", required: true, defaultValue: 1.0, submitOnChange: true, image: getAppImg("fan_low_speed.png")
                        input "fanCtrlFanSwitchMedSpeed", "decimal", title: "Medium Speed Threshold (°${atomicState?.tempUnit})", required: true, defaultValue: 2.0, submitOnChange: true, image: getAppImg("fan_med_speed.png")
                        input "fanCtrlFanSwitchHighSpeed", "decimal", title: "High Speed Threshold (°${atomicState?.tempUnit})", required: true, defaultValue: 4.0, submitOnChange: true, image: getAppImg("fan_high_speed.png")
                    }
                }
            }
        }
    }
}

def isFanCtrlConfigured() {
    def devOk = (fanCtrlFanSwitches  && fanCtrlTstat) ? true : false
    return devOk
}

def getFanCtrlFanSwitchDesc(showOpt = true) {
    def swDesc = ""
    def swCnt = 0
    if(showOpt) {
        swDesc += (fanCtrlFanSwitches && (fanCtrlFanSwitchSpeedCtrl || fanCtrlFanSwitchTriggerType || fanCtrlFanSwitchHvacModeFilter)) ? "Fan Switch Config:" : ""
    }
    swDesc += fanCtrlFanSwitches ? "${showOpt ? "\n" : ""}  • Fan Switches:" : ""
    def rmSwCnt = fanCtrlFanSwitches?.size() ?: 0
    fanCtrlFanSwitches?.each { sw ->
        swCnt = swCnt+1
        swDesc += "${swCnt >= 1 ? "${swCnt == rmSwCnt ? "\n └" : "\n ├"}" : "\n └"} ${sw?.label}: (${sw?.currentSwitch?.toString().capitalize()})"
        swDesc += "${checkFanSpeedSupport(sw) ? "\n   └ 3Spd (${sw?.currentValue("currentSpeed").toString()})" : ""}"
    }
    if(showOpt) {
        swDesc += (fanCtrlFanSwitches && (fanCtrlFanSwitchSpeedCtrl || fanCtrlFanSwitchTriggerType || fanCtrlFanSwitchHvacModeFilter)) ? "\n\nFan Triggers:" : ""
        swDesc += (fanCtrlFanSwitches && fanCtrlFanSwitchSpeedCtrl) ? "\n  • 3-Speed Fan Support: (Active)" : ""
        swDesc += (fanCtrlFanSwitches && fanCtrlFanSwitchTriggerType) ? "\n  • Fan Trigger: (${getEnumValue(switchRunEnum(), fanCtrlFanSwitchTriggerType)})" : ""
        swDesc += (fanCtrlFanSwitches && fanCtrlFanSwitchHvacModeFilter) ? "\n  • Hvac Mode Filter: (${getEnumValue(fanModeTrigEnum(), fanCtrlFanSwitchHvacModeFilter)})" : ""
    }
    return (swDesc == "") ? null : "${swDesc}"
}

def getFanCtrlFanSwitchesSpdChk() {
    def devCnt = 0
    if(fanCtrlFanSwitches) {
        fanCtrlFanSwitches?.each { sw ->
            if(checkFanSpeedSupport(sw)) { devCnt = devCnt+1 }
        }
    }
    return (devCnt >= 1) ? true : false
}

def fanCtrlFanSwitchEvt(evt) {
    LogAction("FanControl Event | Fan Switch: ${evt?.displayName} is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def fanCtrlTstatFanEvt(evt) {
    LogAction("FanControl Event | Thermostat Fan: ${evt?.displayName} Fan is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def fanCtrlTstatTempEvt(evt) {
    LogAction("FanControl Event | Thermostat Temp: ${evt?.displayName} Temperature is (${evt?.value}°${atomicState?.tempUnit})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

// Eric this is where I need you help with the fan temp logic when the multi speed fans are being used

def getfanCtrlCoolSetTemp() {
    def desiredCoolTemp = getGlobalDesiredCoolTemp()
    if (desiredCoolTemp) { return desiredCoolTemp.toDouble() }
    else { return fanCtrlTstat ? getTstatSetpoint(fanCtrlTstat, "cool") : 0 }
}

def getfanCtrlHeatSetTemp() {
    def desiredHeatTemp = getGlobalDesiredHeatTemp()
    if (desiredHeatTemp) { return desiredHeatTemp.toDouble() }
    else { return fanCtrlTstat ? getTstatSetpoint(fanCtrlTstat, "heat") : 0 }
}


def getFanCtrlSetpointTemp(curTemp) {
    def hvacMode = fanCtrlTstat ? fanCtrlTstat?.currentThermostatMode.toString() : null
    def operState = fanCtrlTstat ? fanCtrlTstat?.currentThermostatOperatingState.toString() : null
    def opType = hvacMode.toString()

    def reqHeatSetPoint = getfanCtrlHeatSetTemp()
    def reqCoolSetPoint = getfanCtrlCoolSetTemp()

    if((hvacMode == "cool") || (operState == "cooling")) {
        opType = "cool"
    } else if((hvacMode == "heat") || (operState == "heating")) {
        opType = "heat"
    } else if(hvacMode == "auto") {
        def coolDiff = (curTemp - reqCoolSetPoint)
        def heatDiff = (curTemp - reqHeatSetPoint)
        opType = coolDiff < heatDiff ? "cool" : "heat"
    }
    def temp = (opType == "cool") ?  reqCoolSetPoint.toDouble() : reqHeatSetPoint.toDouble()
    return temp
}


def fanCtrlCheck() {
    //LogAction("FanControl Event | Fan Switch Check", "trace", false)
    try {
        if(disableAutomation) { return }
        if(!fanCtrlFanSwitches) { return }

        def execTime = now()
        atomicState?.lastEvalDt = getDtNow()
        def hvacMode = fanCtrlTstat ? fanCtrlTstat?.currentThermostatMode.toString() : null
        def curTstatOperState = fanCtrlTstat?.currentThermostatOperatingState.toString()
        def curTstatFanMode = fanCtrlTstat?.currentThermostatFanMode.toString()

        def reqHeatSetPoint = getfanCtrlHeatSetTemp()
        def reqCoolSetPoint = getfanCtrlCoolSetTemp()

        def curTstatTemp = getDeviceTemp(fanCtrlTstat).toDouble()
        def curSetPoint = getFanCtrlSetpointTemp(curTstatTemp) ?: 0
        def tempDiff = Math.abs(curSetPoint - curTstatTemp)


        def curHeatSetpoint = getTstatSetpoint(fanCtrlTstat, "heat")
        def curCoolSetpoint = getTstatSetpoint(fanCtrlTstat, "cool")
        LogAction("fanCtrlCheck: Thermostat Info - ( Temperature: (${curTstatTemp}) | HeatSetpoint: (${curHeatSetpoint}) | CoolSetpoint: (${curCoolSetpoint}) | HvacMode: (${hvacMode}) | OperatingState: (${curTstatOperState}) | FanMode: (${curTstatFanMode}) )", "info", false)
        LogAction("fanCtrlCheck: Desired Temps - Heat: ${reqHeatSetPoint} | Cool: ${reqCoolSetPoint}", "info", false)


        def hvacFanOn = false
         //1:"Heating/Cooling", 2:"With Fan Only"

        if( fanCtrlFanSwitchTriggerType.toInteger() ==  1) {
            hvacFanOn = (curTstatOperState in ["heating", "cooling"]) ? true : false
        }
        if( fanCtrlFanSwitchTriggerType.toInteger() ==  2) {
            hvacFanOn = (curTstatFanMode in ["on", "circulate"]) ? true : false
        }
        if(fanCtrlFanSwitchHvacModeFilter != "any" && (fanCtrlFanSwitchHvacModeFilter != hvacMode)) {
            LogAction("fanCtrlCheck: Evaluating turn fans off Because Thermostat Mode does not Match the required Mode to Run Fans", "info", true)
            hvacFanOn = false  // force off of fans
        }


        fanCtrlFanSwitches.each { sw ->
            def swOn = (sw?.currentSwitch.toString() == "on") ? true : false
            if(hvacFanOn) {
                if(!swOn) {
                    LogAction("fanCtrlCheck: Fan Switch (${sw?.displayName}) is (${swOn ? "ON" : "OFF"}) | Turning '${sw.label}' Switch (ON)", "info", true)
                    storeLastAction("Turned On ($fanCtrlFanSwitches)", getDtNow())
                    sw.on()
                }
                if(checkFanSpeedSupport(sw) && atomicState?.fanCtrlFanSwitchSpeedEnabled && fanCtrlFanSwitchHighSpeed && fanCtrlFanSwitchMedSpeed && fanCtrlFanSwitchLowSpeed) {
                    def speed = sw?.currentValue("currentSpeed") ?: null

                    if(tempDiff < fanCtrlFanSwitchMedSpeed.toDouble()) {
                        if (speed != "LOW") {
                            sw.lowSpeed()
                            LogAction("fanCtrlCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is BELOW the Medium Speed Threshold of ($fanCtrlFanSwitchMedSpeed) | Turning '${sw.label}' Fan Switch on (LOW SPEED)", "info", true)
                            storeLastAction("Set Fan $sw to Low Speed", getDtNow())
                        }
                    }
                    else if(tempDiff >= fanCtrlFanSwitchMedSpeed.toDouble() && tempDiff < fanCtrlFanSwitchHighSpeed.toDouble()) {
                        if (speed != "MED") {
                            sw.medSpeed()
                            LogAction("fanCtrlCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is ABOVE the Medium Speed Threshold of ($fanCtrlFanSwitchMedSpeed) | Turning '${sw.label}' Fan Switch on (MEDIUM SPEED)", "info", true)
                            storeLastAction("Set Fan $sw to Medium Speed", getDtNow())
                        }
                    }
                    else if(tempDiff >= fanCtrlFanSwitchHighSpeed.toDouble()) {
                        if (speed != "HIGH") {
                            sw.highSpeed()
                            LogAction("fanCtrlCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is ABOVE the High Speed Threshold of ($fanCtrlFanSwitchHighSpeed) | Turning '${sw.label}' Fan Switch on (HIGH SPEED)", "info", true)
                            storeLastAction("Set Fan $sw to High Speed", getDtNow())
                        }
                    }
                }
            } else {
                if(swOn) {
                    LogAction("fanCtrlCheck: Fan Switch (${sw?.displayName}) is (${swOn ? "ON" : "OFF"}) | Turning '${sw?.label}' Switch (OFF)", "info", true)
                    storeLastAction("Turned Off (${sw.label})", getDtNow())
                    sw.off()
                }
            }
        }
        storeExecutionHistory((now()-execTime), "fanCtrlCheck")
    } catch (ex) {
        log.error "fanCtrlCheck Exception: (${ex})", ex
        parent?.sendExceptionData(ex.message, "fanCtrlCheck", true, getAutoType())
    }
}

/********************************************************************************
|                			EXTERNAL TEMP AUTOMATION CODE	     				|
*********************************************************************************/
def extTmpPrefix() { return "extTmp" }

def extTempPage() {
    def pName = extTmpPrefix()
    dynamicPage(name: "extTempPage", title: "Thermostat/External Temps Automation", uninstall: false, nextPage: "mainAutoPage") {
        section("Select the External Temps to Use:") {
            if(!parent?.getWeatherDeviceInst()) {
                paragraph "Please Enable the Weather Device under the Manager App before trying to use External Weather as an External Sensor!!!", required: true, state: null
            } else {
                if(!extTmpTempSensor) {
                    input "extTmpUseWeather", "bool", title: "Use Local Weather as External Sensor?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("weather_icon.png")
                    if(extTmpUseWeather){
                        getExtConditions()
                        def tmpVal = (location?.temperatureScale == "C") ? atomicState?.curWeatherTemp_c : atomicState?.curWeatherTemp_f
                        paragraph "Local Weather:\n• ${atomicState?.curWeatherLoc} (${tmpVal}°${atomicState?.tempUnit})", state: "complete", image: getAppImg("instruct_icon.png")
                    }
                }
            }
            if(!extTmpUseWeather) {
                def senReq = (!extTmpUseWeather && !extTmpTempSensor) ? true : false
                input "extTmpTempSensor", "capability.temperatureMeasurement", title: "Select a Temp Sensor?", submitOnChange: true, multiple: false, required: senReq,
                        image: getAppImg("temperature_icon.png")
                if(extTmpTempSensor) {
                    def str = ""
                    str += extTmpTempSensor ? "Sensor Status:" : ""
                    str += extTmpTempSensor ? "\n└ Temp: (${extTmpTempSensor?.currentTemperature}°${atomicState?.tempUnit})" : ""
                    paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")
                }
            }
        }
        if(extTmpUseWeather || extTmpTempSensor) {
            def req = (extTmpUseWeather || (!extTmpUseWeather && extTmpTempSensor)) ? true : false
            def dupTstat = checkThermostatDupe(extTmpTstat, extTmpTstatMir)
            section("When the Threshold Temp is Reached\nTurn Off this Thermostat...") {
                input name: "extTmpTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
                if(dupTstat) {
                    paragraph "Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", state: null, required: true, image: getAppImg("error_icon.png")
                }
                if(extTmpTstat) {
                    getTstatCapabilities(extTmpTstat, extTmpPrefix())
                    def str = ""
                    str += extTmpTstat ? "Thermostat Status:" : ""
                    str += extTmpTstat ? "\n├ Temp: (${extTmpTstat?.currentTemperature}°${atomicState?.tempUnit})" : ""
                    str += extTmpTstat ? "\n├ Mode: (${extTmpTstat?.currentThermostatOperatingState.toString().capitalize()}/${extTmpTstat?.currentThermostatMode.toString().capitalize()})" : ""
                    str += extTmpTstat ? "\n${(settings?."${getAutoType()}UseSafetyTemps" && getSafetyTemps(extTmpTstat)) ? "├" : "└"} Presence: (${getTstatPresence(extTmpTstat) == "present" ? "Home" : "Away"})" : ""
                    str += (extTmpTstat && settings?."${getAutoType()}UseSafetyTemps" && getSafetyTemps(extTmpTstat)) ? "\n└ Safefy Temps: \n     └ Min: ${getSafetyTemps(extTmpTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(extTmpTstat).max}°${atomicState?.tempUnit}" : ""
                    paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")

                    input name: "extTmpTstatMir", type: "capability.thermostat", title: "Mirror commands to these Thermostats?", multiple: true, submitOnChange: true, required: false,
                            image: getAppImg("thermostat_icon.png")

                    if(extTmpTstatMir && !dupTstat) {
                        extTmpTstatMir?.each { t ->
                            paragraph "Thermostat Temp: ${getDeviceTemp(t)}${atomicState?.tempUnit}", image: " "
                        }
                    }

                    input name: "extTmpDiffVal", type: "decimal", title: "When Thermostat temp is within this many degrees of the external temp (°${atomicState?.tempUnit})?", defaultValue: 1.0, submitOnChange: true, required: true,
                            image: getAppImg("temp_icon.png")
                }
            }
        }
        if((extTmpUseWeather || extTmpTempSensor) && extTmpTstat) {
// need to check if safety temps are set and != to each other
            section("Restoration Preferences (Optional):") {
                input "${getAutoType()}UseSafetyTemps", "bool", title: "Restore When Safety Temps are Exceeded?", defaultValue: true, submitOnChange: true, image: getAppImg("switch_icon.png")
                input "${getAutoType()}OffTimeout", "enum", title: "Auto Restore after Time\n(Optional)", defaultValue: 3600, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                if(!settings?."${getAutoType}OffTimeout") { atomicState?.timeOutScheduled = false }
            }
            section("Delay Values:") {
                input name: "extTmpOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                input name: "extTmpOnDelay", type: "enum", title: "Delay Restore On (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
            }
            section(getDmtSectionDesc(extTmpPrefix())) {
                def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc ? "complete" : null),
                        image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                href "setNotificationPage", title: "Configure Alerts...", description: getNotifConfigDesc(), params: ["pName":pName, "allowSpeech":true, "showSchedule":true, "allowAlarm":true],
                        state: (getNotificationOptionsConf() ? "complete" : null), image: getAppImg("notification_icon.png")
            }
        }
        if(atomicState?.showHelp) {
            section("Help and Instructions:") {
                href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
            }
        }
    }
}

def isExtTmpConfigured() {
    def devOk = ((extTmpUseWeather || extTmpTempSensor) && extTmpTstat && extTmpDiffVal) ? true : false
    return devOk
}

def getExtConditions( doEvent = false ) {
    //log.trace "getExtConditions..."
    def origTempF = atomicState?.curWeatherTemp_f
    def origTempC = atomicState?.curWeatherTemp_c
    def origDpTempF = atomicState?.curWeatherDewpointTemp_f
    def origDpTempC = atomicState?.curWeatherDewpointTemp_c
    def cur = parent?.getWData()
    atomicState?.curWeather = cur?.current_observation
    atomicState?.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f).toInteger()
    atomicState?.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c.toDouble())
    atomicState?.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
    atomicState?.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
    if(parent?.getWeatherDeviceInst()) {
        def weather = parent.getWeatherDevice()
        def dp = 0.0
        if (weather) {
            dp = weather?.currentValue("dewpoint")?.toString().replaceAll("\\[|\\]", "").toDouble()
        }
        def c_temp = 0.0
        def f_temp = 0 as Integer
        if (getTemperatureScale() == "C") {
            c_temp = dp as Double
            f_temp = c_temp * 9/5 + 32
        } else {
            f_temp = dp as Integer
            c_temp = (f_temp - 32) * 5/9 as Double
        }
        atomicState?.curWeatherDewpointTemp_c = Math.round(c_temp.round(1) * 2) / 2.0f
        atomicState?.curWeatherDewpointTemp_f = Math.round(f_temp) as Integer
    }

/*    if (doEvent) {
        if (origTempF != atomicState?.curWeatherTemp_f || origTempC != atomicState?.curWeatherTemp_c) {
            LogAction("${atomicState?.curWeatherLoc} Weather | humidity: ${atomicState?.curWeatherHum} | temp_f: ${atomicState?.curWeatherTemp_f} | temp_c: ${atomicState?.curWeatherTemp_c}", "debug", false)
            if (isExtTmpConfigured() && !disableAutomation) {
                def evtset = ["displayName":"Nest Weather Device", "value": (atomicState?.tempUnit == "C") ? atomicState?.curWeatherTemp_c : atomicState?.curWeatherTemp_f]
                extTmpTempEvt(evtset)
            }
        }
        if (origDpTempF != atomicState?.curWeatherDewpointTemp_f || origDpTempC != atomicState?.curWeatherDewpointTemp_c) {
            LogAction("${atomicState?.curWeatherLoc} Weather | Dew point temp_f: ${atomicState?.curWeatherDewpointTemp_f} | Dew point temp_c: ${atomicState?.curWeatherDewpointTemp_c}", "debug", false)
            if (isExtTmpConfigured() && !disableAutomation) {
                def evtset = ["displayName":"Nest Weather Device", "value": (atomicState?.tempUnit == "C") ? atomicState?.curWeatherDewpointTemp_c : atomicState?.curWeatherDewpointTemp_f]
                extTmpDpEvt(evtset)
            }
        }
    } */
}

def getExtTmpTemperature() {
    def extTemp = 0.0
    if (!extTmpUseWeather && extTmpTempSensor) {
        extTemp = getDeviceTemp(extTmpTempSensor)
    } else {
        if(extTmpUseWeather && (atomicState?.curWeatherTemp_f || atomicState?.curWeatherTemp_c)) {
            if(location?.temperatureScale == "C" && atomicState?.curWeatherTemp_c) { extTemp = atomicState?.curWeatherTemp_c.toDouble() }
            else { extTemp = atomicState?.curWeatherTemp_f.toDouble() }
        }
    }
    return extTemp
}

def getExtTmpDewPoint() {
    def extDp = 0.0
    if(extTmpUseWeather && (atomicState?.curWeatherDewpointTemp_f || atomicState?.curWeatherDewpointTemp_c)) {
        if(location?.temperatureScale == "C" && atomicState?.curWeatherDewpointTemp_c) { extDp = atomicState?.curWeatherDewpointTemp_c.toDouble() }
        else { extDp = atomicState?.curWeatherDewpointTemp_f.toDouble() }
    }
    return extDp
}

def extTmpTempOk() {
    //log.trace "extTmpTempOk..."
    try {
        def execTime = now()
        def desiredHeatTemp = getGlobalDesiredHeatTemp()
        def desiredCoolTemp = getGlobalDesiredCoolTemp()
        def intTemp = extTmpTstat ? extTmpTstat?.currentTemperature.toDouble() : null
        def extTemp = getExtTmpTemperature()
        def curMode = extTmpTstat.currentThermostatMode.toString()
        def dpLimit = getComfortDewpoint() ?: (getTemperatureScale() == "C" ? 19 : 66)
        def curDp = getExtTmpDewPoint()
        def diffThresh = getExtTmpTempDiffVal()
        def dpOk = (curDp < dpLimit) ? true : false
        def modeOff = (curMode == "off") ? true : false
        def modeCool = (curMode == "cool") ? true : false
        def modeHeat = (curMode == "heat") ? true : false
        def modeAuto = (curMode == "auto") ? true : false

        def desiredTemp = 0
        if (desiredHeatTemp && modeHeat) { desiredTemp = desiredHeatTemp }
        if (desiredCoolTemp && modeCool) { desiredTemp = desiredCoolTemp  }
        if (desiredHeatTemp && desiredCoolTemp && (desiredHeatTemp < desiredCoolTemp) && modeAuto) { desiredTemp = (desiredCoolTemp + desiredHeatTemp)/2.0  }

        LogAction("extTmpTempOk: curMode: ${curMode} | modeOff: ${modeOff} | atomicState.extTmpTstatOffRequested: ${atomicState?.extTmpTstatOffRequested}", "debug", false)
        LogAction("extTmpTempOk: Inside Temp: ${intTemp} | Desired Temp: ${desiredTemp} | Desired Heat Temp: ${desiredHeatTemp} | Desired Cool Temp: ${desiredCoolTemp}", "debug", false)

        intTemp = desiredTemp ?: intTemp
        def tempDiff = Math.abs(extTemp - intTemp)

        LogAction("extTmpTempOk: Outside Temp: ${extTemp} | Temp Threshold: ${diffThresh} | Actual Difference: ${tempDiff} | Outside Dew point: ${curDp} | Dew point Limit: ${dpLimit}", "debug", false)

        def retval = true
        def tempOk = true
        def str = "enough different (${tempDiff})"
        if(intTemp && extTemp && diffThresh) {
            if (!modeAuto && tempDiff < diffThresh) {
                retval = false
                tempOk = false
            }
            def extTempHigh = (extTemp > intTemp - diffThresh) ? true : false
            def extTempLow = (extTemp < intTemp + diffThresh) ? true : false
            def oldMode = atomicState?.extTmpRestoreMode
            if (modeCool || oldMode == "cool") {
                str = "greater than"
                if (extTempHigh) { retval = false; tempOk = false }
            }
            if (modeHeat || oldMode == "heat") {
                str = "less than"
                if (extTempLow) { retval = false; tempOk = false }
            }
            if (modeAuto) { retval = false; str = "in supported mode" } // no point in turning off if in auto mode
            if (!dpOk) { retval = false }
            LogAction("extTmpTempOk: extTempHigh: ${extTempHigh} | extTempLow: ${extTempLow} | dpOk: ${dpOk}", "debug", false)
        }
        LogAction("extTmpTempOk: Inside Temp: (${intTemp}°${atomicState?.tempUnit}) is ${tempOk ? "" : "Not"} ${str} $diffThresh° of Outside Temp: (${extTemp}°${atomicState?.tempUnit}) or Dewpoint: (${curDp}°${atomicState?.tempUnit}) is ${dpOk ? "ok" : "too high"}", "info", true)
        storeExecutionHistory((now() - execTime), "getExtTmpTempOk")
        return retval
    } catch (ex) {
        log.error "getExtTmpTempOk Exception: ${ex}", ex
        parent?.sendExceptionData(ex.message, "extTmpTempOk", true, getAutoType())
    }
}

def extTmpScheduleOk() { return autoScheduleOk(extTmpPrefix()) }
def getExtTmpTempDiffVal() { return !settings?.extTmpDiffVal ? 1.0 : settings?.extTmpDiffVal.toDouble() }
def getExtTmpGoodDtSec() { return !atomicState?.extTmpTempGoodDt ? 100000 : GetTimeDiffSeconds(atomicState?.extTmpTempGoodDt).toInteger() }
def getExtTmpBadDtSec() { return !atomicState?.extTmpTempBadDt ? 100000 : GetTimeDiffSeconds(atomicState?.extTmpTempBadDt).toInteger() }
def getExtTmpOffDelayVal() { return !extTmpOffDelay ? 300 : extTmpOffDelay.toInteger() }
def getExtTmpOnDelayVal() { return !extTmpOnDelay ? 300 : extTmpOnDelay.toInteger() }

def extTmpTempCheck(cTimeOut = false) {
    //log.trace "extTmpTempCheck..."

    try {
        if(disableAutomation) { return }
        else {
            def execTime = now()
            atomicState?.lastEvalDt = getDtNow()
            if(!atomicState?.timeOutOn) { atomicState.timeOutOn = false }
            if(cTimeOut) { atomicState.timeOutOn = true }
            def timeOut = atomicState.timeOutOn ?: false

            def curMode = extTmpTstat?.currentThermostatMode?.toString()
            def modeOff = (curMode == "off") ? true : false
            def safetyOk = getSafetyTempsOk(extTmpTstat)
            def schedOk = extTmpScheduleOk()
            def okToRestore = (modeOff && atomicState?.extTmpTstatOffRequested) ? true : false
            def allowNotif = settings?."${getAutoType()}NotificationsOn" ? true : false
            def allowSpeech = allowNotif && settings?."${getAutoType()}AllowSpeechNotif" ? true : false
            def allowAlarm = allowNotif && settings?."${getAutoType()}AllowAlarmNotif" ? true : false
            def speakOnRestore = allowSpeech && settings?."${getAutoType()}SpeechOnRestore" ? true : false

            def tempWithinThreshold = extTmpTempOk()

            if (!modeOff) { atomicState.timeOutOn = false; timeOut = false }

            if(!tempWithinThreshold || timeOut || !safetyOk) {
                if(okToRestore) {
                    if(getExtTmpGoodDtSec() >= (getExtTmpOnDelayVal() - 5) || timeOut || !safetyOk) {
                        def lastMode = null
                        if(atomicState?.extTmpRestoreMode) { lastMode = atomicState?.extTmpRestoreMode }
                        if(lastMode && (lastMode != curMode || timeOut || !safetyOk)) {
                            scheduleAutomationEval(180)
                            if(setTstatMode(extTmpTstat, lastMode)) {
                                storeLastAction("Restored Mode ($lastMode)", getDtNow())
                                atomicState?.extTmpRestoreMode = null
                                atomicState?.extTmpTstatOffRequested = false
                                atomicState?.extTmpRestoredDt = getDtNow()
                                atomicState.timeOutOn = false
                                unschedTimeoutRestore()
                                modeOff = false

                                if(extTmpTstatMir) {
                                    if(setMultipleTstatMode(extTmpTstatMir, lastMode)) {
                                        LogAction("Mirroring (${lastMode}) Restore to ${extTmpTstatMir}", "info", true)
                                    }
                                }

                                if(!safetyOk) {
                                    LogAction("Restoring '${extTmpTstat?.label}' to '${lastMode.toUpperCase()}' mode because External Temp Safefy Temps have been reached...", "info", true)
                                }
                                else if (timeOut) {
                                    LogAction("Restoring '${extTmpTstat?.label}' to '${lastMode?.toString().toUpperCase()}' mode because the (${getEnumValue(longTimeSecEnum(), extTmpOffTimeout)}) Timeout has been reached...", "info", true)
                                }
                                else {
                                    LogAction("Restoring '${extTmpTstat?.label}' to '${lastMode.toUpperCase()}' mode because External Temp has been above the Threshold for (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})...", "info", true)
                                }
                                if(allowNotif) {
                                    if(!timeOut && safetyOk) {
                                        sendEventPushNotifications("Restoring '${extTmpTstat?.label}' to '${lastMode.toUpperCase()}' Mode because External Temp has been above the Threshold for (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})...", "Info")
                                        if(speakOnRestore) { sendEventVoiceNotifications(voiceNotifString(atomicState?."${getAutoType()}OnVoiceMsg")) }
                                    }
                                }
                                return

                            } else { LogAction("extTmpTempCheck() | There was problem restoring the last mode to '...", "error", true) }
                        } else {
                            if(!timeOut && safetyOk) { LogAction("extTmpTstatCheck() | Skipping Restore because the Mode to Restore is same as Current Mode ${curMode}", "info", true) }
                            if(!safetyOk) { LogAction("extTmpTempCheck() | Unable to restore mode and safety temperatures are exceeded", "warn", true) }
                        }
                    } else { if (safetyOk) { scheduleAutomationEval(60) } }
                } else {
                    if (modeOff) {
                        if (timeout || !safetyOk) {
                            LogAction("extTmpTempCheck() | Timeout or Safety temps exceeded and Unable to restore settings okToRestore is false", "warn", true)
                            atomicState.timeOutOn = false
                        }
                        else if (!atomicState?.extTmpRestoreMode && atomicState?.extTmpTstatOffRequested) {
                            LogAction("extTmpTempCheck() | Unable to restore settings because previous mode was not found. Likely due to other automation making changes.", "warn", true)
                            atomicState?.extTmpTstatOffRequested = false
                        }
                    }
                }
            }

            if (tempWithinThreshold && !timeOut && safetyOk && schedOk) {
                if(!modeOff) {
                    if(getExtTmpBadDtSec() >= (getExtTmpOffDelayVal() - 2)) {
                        atomicState.timeOutOn = false
                        atomicState?.extTmpRestoreMode = curMode
                        LogAction("extTmpTempCheck: Saving ${extTmpTstat?.label} (${atomicState?.extTmpRestoreMode.toString().toUpperCase()}) mode for Restore later.", "info", true)
                        scheduleAutomationEval(180)
                        if(setTstatMode(extTmpTstat, "off")) {
                            storeLastAction("Turned Off Thermostat", getDtNow())
                            atomicState?.extTmpTstatOffRequested = true
                            scheduleTimeoutRestore()
                            LogAction("${extTmpTstat} has been turned 'Off' because External Temp is at the temp threshold for (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})!!!", "info", true)
                            if(extTmpTstatMir) {
                                setMultipleTstatMode(extTmpTstatMir, "off") {
                                    LogAction("Mirroring (Off) Mode to ${extTmpTstatMir}", "info", true)
                                }
                            }
                            if(allowNotif) {
                                sendEventPushNotifications("${extTmpTstat?.label} has been turned 'Off' because External Temp is at the temp threshold for (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})!!!", "Info")
                                if (speakOnRestore) { sendEventVoiceNotifications(voiceNotifString(atomicState?."${getAutoType()}OffVoiceMsg")) }
                            }
                        } else { LogAction("extTmpTempCheck(): Error turning themostat Off", "warn", true) }
                    } else { scheduleAutomationEval(30) }
                } else {
                   LogAction("extTmpTempCheck() | Skipping change because '${extTmpTstat?.label}' mode is already 'OFF'", "info", true)
                }
            } else {
                if (!schedOk) { LogAction("extTmpTempCheck: Skipping because of Schedule Restrictions...", "info", true) }
                if (!safetyOk) { LogAction("extTmpTempCheck: Skipping because of Safety Temps Exceeded...", "info", true) }
            }
            storeExecutionHistory((now() - execTime), "extTmpTempCheck")
        }
    } catch (ex) {
        log.error "extTmpTempCheck Exception: (${ex})", ex
        parent?.sendExceptionData(ex.message, "extTmpTempCheck", true, getAutoType())
    }
}

def extTmpTstatModeEvt(evt) {
    LogAction("extTmpTstatModeEvt Event | Thermostat Mode: ${evt?.displayName} - Mode is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        def modeOff = (evt?.value == "off") ? true : false
        if(!modeOff) { atomicState?.extTmpTstatTurnedOff = false }
        else { atomicState?.extTmpTstatTurnedOff = true }
    }
    scheduleAutomationEval()
    storeLastEventData(evt)
}

def extTmpTstatTempEvt(evt) {
    LogAction("extTmpTstatTempEvt Event | Thermostat Temperature: ${evt?.displayName} - Temperature is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    else {
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def extTmpTempEvt(evt) {
    LogAction("extTmpTempEvt Event | External Sensor Temperature: ${evt?.displayName} - Temperature is (${evt?.value.toString().toUpperCase()})", "trace", false)
    if(disableAutomation) { return }
    else {
        def pName = extTmpPrefix()
        def curMode = extTmpTstat?.currentThermostatMode.toString()
        def modeOff = (curMode == "off") ? true : false
        def offVal = getExtTmpOffDelayVal()
        def onVal = getExtTmpOnDelayVal()
        def timeVal
        if (!modeOff) {
            atomicState.extTmpTempGoodDt = getDtNow()
            timeVal = ["valNum":offVal, "valLabel":getEnumValue(longTimeSecEnum(), offVal)]
        } else {
            atomicState.extTmpTempBadDt = getDtNow()
            timeVal = ["valNum":onVal, "valLabel":getEnumValue(longTimeSecEnum(), onVal)]
        }
        LogAction("extTmpTempEvt() ${!evt ? "" : "'${evt?.displayName}': (${evt?.value}°${atomicState?.tempUnit}) received... | "}External Temp Check scheduled for (${timeVal?.valLabel})...", "info", true)
        if (timeVal?.valNum > 20) {
            scheduleAutomationEval(timeVal?.valNum)
        } else {
            scheduleAutomationEval()
            storeLastEventData(evt)
        }
    }
}

def extTmpDpEvt(evt) {
    LogAction("extTmpDpEvt Event | External Sensor Dew point: ${evt?.displayName} - Dew point Temperature is (${evt?.value.toString().toUpperCase()})", "trace", false)
    if(disableAutomation) { return }
    else {
        def pName = extTmpPrefix()
        def curMode = extTmpTstat?.currentThermostatMode.toString()
        def modeOff = (curMode == "off") ? true : false
        def offVal = getExtTmpOffDelayVal()
        def onVal = getExtTmpOnDelayVal()
        def timeVal
        if (!modeOff) {
            atomicState.extTmpTempGoodDt = getDtNow()
            timeVal = ["valNum":offVal, "valLabel":getEnumValue(longTimeSecEnum(), offVal)]
        } else {
            atomicState.extTmpTempBadDt = getDtNow()
            timeVal = ["valNum":onVal, "valLabel":getEnumValue(longTimeSecEnum(), onVal)]
        }
        storeLastEventData(evt)
        LogAction("extTmpDpEvt() ${!evt ? "" : "'${evt?.displayName}': (${evt?.value}°${atomicState?.tempUnit}) received... | "}External Temp Check scheduled for (${timeVal?.valLabel})...", "info", true)
        if (timeVal?.valNum > 20) {
            scheduleAutomationEval(timeVal?.valNum)
        } else {
            scheduleAutomationEval()
        }
    }
}

/******************************************************************************
|                			WATCH CONTACTS AUTOMATION CODE	                  |
*******************************************************************************/
def conWatPrefix() { return "conWat" }

def contactWatchPage() {
    def pName = conWatPrefix()
    dynamicPage(name: "contactWatchPage", title: "Thermostat/Contact Automation", uninstall: false, nextPage: "mainAutoPage") {
        def dupTstat = checkThermostatDupe(conWatTstat, conWatTstatMir)
        section("When These Contacts are open, Turn Off this Thermostat") {
            def req = (conWatContacts || conWatTstat) ? true : false
            input name: "conWatContacts", type: "capability.contactSensor", title: "Which Contact(s)?", multiple: true, submitOnChange: true, required: req,
                    image: getAppImg("contact_icon.png")
            input name: "conWatTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: getAppImg("thermostat_icon.png")
            if(dupTstat) {
                paragraph "Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", state: null, required: true, image: getAppImg("error_icon.png")
            }
            if (conWatContacts && conWatTstat) {
                getTstatCapabilities(conWatTstat, conWatPrefix())
                def str = ""
                str += conWatContacts ? "${conWatContactDesc()}\n" : ""
                str += conWatTstat ? "\nThermostat Status:" : ""
                str += conWatTstat ? "\n├ Temp: (${conWatTstat?.currentTemperature}°${atomicState?.tempUnit})" : ""
                str += conWatTstat ? "\n├ Mode: (${conWatTstat?.currentThermostatOperatingState.toString().capitalize()}/${conWatTstat?.currentThermostatMode.toString().capitalize()})" : ""
                str += conWatTstat ? "\n${(settings?."${getAutoType()}UseSafetyTemps" && getSafetyTemps(conWatTstat)) ? "├" : "└"} Presence: (${getTstatPresence(conWatTstat) == "present" ? "Home" : "Away"})" : ""
                str += (conWatTstat && settings?."${getAutoType()}UseSafetyTemps" && getSafetyTemps(conWatTstat)) ? "\n└ Safefy Temps: \n     └ Min: ${getSafetyTemps(conWatTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(conWatTstat).max}°${atomicState?.tempUnit}" : ""
                paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")
            }
            if(conWatTstat) {
                input name: "conWatTstatMir", type: "capability.thermostat", title: "Mirror commands to these Thermostats?", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("thermostat_icon.png")

                if(conWatTstatMir && !dupTstat) {
                    conWatTstatMir?.each { t ->
                        paragraph "Thermostat Temp: ${getDeviceTemp(t)}${atomicState?.tempUnit}", image: " "
                    }
                }
            }
        }
        if(conWatContacts && conWatTstat) {
// need to check if safety temps are set and != to each other
            section("Restoration Preferences (Optional):") {
                input "${getAutoType()}UseSafetyTemps", "bool", title: "Restore When Safety Temps are Exceeded?", defaultValue: true, submitOnChange: true, image: getAppImg("switch_icon.png")
                input "${getAutoType()}OffTimeout", "enum", title: "Auto Restore after Time\n(Optional)", defaultValue: 3600, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                if(!settings?."${getAutoType}OffTimeout") { atomicState?.timeOutScheduled = false }
            }
            section("Trigger Actions:") {

                input name: "conWatOffDelay", type: "enum", title: "Delay Off When Opened\n(in Minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")

                input name: "conWatOnDelay", type: "enum", title: "Delay Restore On When Closed\n(in Minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                input name: "conWatRestoreDelayBetween", type: "enum", title: "Delay Between Off / On Cycles\n(Optional)", defaultValue: 900, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
            }

            section(getDmtSectionDesc(conWatPrefix())) {
                def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc ? "complete" : null),
                        image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                href "setNotificationPage", title: "Configure Alerts...", description: getNotifConfigDesc(), params: ["pName":pName, "allowSpeech":true, "allowAlarm":true, "showSchedule":true],
                        state: (getNotifConfigDesc() ? "complete" : null), image: getAppImg("notification_icon.png")
            }
        }
        if(atomicState?.showHelp) {
            section("Help:") {
                href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
            }
        }
    }
}

def conWatContactDesc() {
    if(conWatContacts) {
        def cCnt = conWatContacts?.size() ?: 0
        def str = ""
        def cnt = 0
        str += "Contact Status:"
        conWatContacts?.each { dev ->
            cnt = cnt+1
            str += "${(cnt >= 1) ? "${(cnt == cCnt) ? "\n└" : "\n├"}" : "\n└"} ${dev?.label}: (${dev?.currentContact?.toString().capitalize()})"
        }
        return str
    }
    return null
}

def isConWatConfigured() {
    return (conWatContacts && conWatTstat && conWatOffDelay) ? true : false
}

def getConWatContactsOk() { return conWatContacts?.currentState("contact")?.value.contains("open") ? false : true }
def conWatContactOk() { return (!conWatContacts && !conWatTstat) ? false : true }
def conWatScheduleOk() { return autoScheduleOk(conWatPrefix()) }
def getConWatOpenDtSec() { return !atomicState?.conWatOpenDt ? 100000 : GetTimeDiffSeconds(atomicState?.conWatOpenDt).toInteger() }
def getConWatCloseDtSec() { return !atomicState?.conWatCloseDt ? 100000 : GetTimeDiffSeconds(atomicState?.conWatCloseDt).toInteger() }
def getConWatRestoreDelayBetweenDtSec() { return !atomicState?.conWatRestoredDt ? 100000 : GetTimeDiffSeconds(atomicState?.conWatRestoredDt).toInteger() }
def getConWatOffDelayVal() { return !conWatOffDelay ? 300 : (conWatOffDelay.toInteger()) }
def getConWatOnDelayVal() { return !conWatOnDelay ? 300 : (conWatOnDelay.toInteger()) }
def getConWatRestoreDelayBetweenVal() { return !conWatRestoreDelayBetween ? 600 : conWatRestoreDelayBetween.toInteger() }

def conWatCheck(cTimeOut = false) {
    //log.trace "conWatCheck..."
    //
    // Should consider not turning thermostat off, as much as setting it more toward away settings?
    // There should be monitoring of actual temps for min and max warnings given on/off automations
    //
    // Should have some check for stuck contacts
    // if we cannot save/restore settings, don't bother turning things off
    //
    try {
        if (disableAutomation) { return }
        else {
            def execTime = now()
            atomicState?.lastEvalDt = getDtNow()
            if(!atomicState?.timeOutOn) { atomicState.timeOutOn = false }
            if(cTimeOut) { atomicState.timeOutOn = true }
            def timeOut = atomicState.timeOutOn ?: false
            def curMode = conWatTstat?.currentState("thermostatMode")?.value.toString()
            def curNestPres = getTstatPresence(conWatTstat)
            def modeOff = (curMode == "off") ? true : false
            def openCtDesc = getOpenContacts(conWatContacts) ? " '${getOpenContacts(conWatContacts)?.join(", ")}' " : " a selected contact "
            def safetyOk = getSafetyTempsOk(conWatTstat)
            def schedOk = conWatScheduleOk()
            def okToRestore = (modeOff && atomicState?.conWatTstatOffRequested) ? true : false
            def allowNotif = settings?."${getAutoType()}NotificationsOn" ? true : false
            def allowSpeech = allowNotif && settings?."${getAutoType()}AllowSpeechNotif" ? true : false
            def allowAlarm = allowNotif && settings?."${getAutoType()}AllowAlarmNotif" ? true : false
            def speakOnRestore = allowSpeech && settings?."${getAutoType()}SpeechOnRestore" ? true : false

            //log.debug "curMode: $curMode | modeOff: $modeOff | conWatRestoreOnClose: $conWatRestoreOnClose | lastMode: $lastMode"
            //log.debug "conWatTstatOffRequested: ${atomicState?.conWatTstatOffRequested} | getConWatCloseDtSec(): ${getConWatCloseDtSec()}"

            if (!modeOff) { atomicState.timeOutOn = false; timeOut = false }

            if(getConWatContactsOk() || timeOut || !safetyOk) {
                if (allowAlarm) { alarmEvtSchedCleanup() }

                if(okToRestore) {
                    if(getConWatCloseDtSec() >= (getConWatOnDelayVal() - 5) || timeOut || !safetyOk) {
                        def lastMode = null
                        if(atomicState?.conWatRestoreMode) { lastMode = atomicState?.conWatRestoreMode }
                        if(lastMode && (lastMode != curMode || timeOut || !safetyOk)) {
                            scheduleAutomationEval(180)
                            if(setTstatMode(conWatTstat, lastMode)) {
                                storeLastAction("Restored Mode ($lastMode) to $conWatTstat", getDtNow())
                                atomicState?.conWatRestoreMode = null
                                atomicState?.conWatTstatOffRequested = false
                                atomicState?.conWatRestoredDt = getDtNow()
                                atomicState.timeOutOn = false
                                unschedTimeoutRestore()
                                modeOff = false

                                if(conWatTstatMir) {
                                    if(setMultipleTstatMode(conWatTstatMir, lastMode)) {
                                        LogAction("Mirroring (${lastMode}) Restore to ${conWatTstatMir}", "info", true)
                                    }
                                }
                                if(!safetyOk) {
                                    LogAction("Restoring '${conWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' mode because Global Safefy Values have been reached...", "info", true)
                                }
                                else if (timeOut) {
                                    LogAction("Restoring '${conWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' mode because the (${getEnumValue(longTimeSecEnum(), conWatOffTimeout)}) Timeout has been reached...", "info", true)
                                }
                                else {
                                    LogAction("Restoring '${conWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' Mode because ALL contacts have been 'Closed' again for (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})...", "info", true)
                                }

                                if(allowNotif) {
                                    if(!timeOut && safetyOk) {
                                        sendEventPushNotifications("Restoring '${conWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' Mode because ALL contacts have been 'Closed' again for (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})...", "Info")
                                        if(speakOnRestore) { sendEventVoiceNotifications(voiceNotifString(atomicState?."${getAutoType()}OnVoiceMsg")) }
                                    }
                                }
                            } else { LogAction("conWatCheck() | There was Problem Restoring the Last Mode to ($lastMode)", "error", true) }
                        } else {
                            if(!timeOut && safetyOk) { LogAction("conWatCheck() | Skipping Restore because the Mode to Restore is same as Current Mode ${curMode}", "info", true) }
                            if(!safetyOk) { LogAction("conWatCheck() | Unable to restore mode and safety temperatures are exceeded", "warn", true) }
                        }
                    } else { if (safetyOk) { scheduleAutomationEval(60) } }
                } else {
                    if (modeOff) {
                        if (timeOut || !safetyOk) {
                            LogAction("conWatCheck() | Timeout or Safety temps exceeded and Unable to restore settings okToRestore is false", "warn", true)
                            atomicState.timeOutOn = false
                        }
                        else if (!atomicState?.conWatRestoreMode && atomicState?.conWatTstatOffRequested) {
                            LogAction("conWatCheck() | Unable to restore settings because previous mode was not found. Likely due to other automation making changes.", "warn", true)
                            atomicState?.conWatTstatOffRequested = false
                        }
                    }
                }
            }

            if (!getConWatContactsOk() && safetyOk && !timeOut && schedOk) {
                if(!modeOff) {
                    if((getConWatOpenDtSec() >= (getConWatOffDelayVal() - 2)) && (getConWatRestoreDelayBetweenDtSec() >= (getConWatRestoreDelayBetweenVal() - 2))) {
                        atomicState.timeOutOn = false
                        atomicState?.conWatRestoreMode = curMode
                        LogAction("conWatCheck: Saving ${conWatTstat?.label} mode (${atomicState?.conWatRestoreMode.toString().toUpperCase()}) for Restore later.", "info", true)
                        LogAction("conWatCheck: ${openCtDesc}${getOpenContacts(conWatContacts).size() > 1 ? "are" : "is"} still Open: Turning 'OFF' '${conWatTstat?.label}'", "debug", true)
                        scheduleAutomationEval(180)
                        if(setTstatMode(conWatTstat, "off")) {
                            storeLastAction("Turned Off $conWatTstat", getDtNow())
                            atomicState?.conWatTstatOffRequested = true
                            scheduleTimeoutRestore()
                            if (allowAlarm) { scheduleAlarmOn() }
                            if(conWatTstatMir) {
                                setMultipleTstatMode(conWatTstatMir, "off") {
                                    LogAction("Mirroring (Off) Mode to ${conWatTstatMir}", "info", true)
                                }
                            }
                            LogAction("conWatCheck: '${conWatTstat.label}' has been turned 'OFF' because${openCtDesc}has been Opened for (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})...", "warn", true)

                            if(allowNotif) {
                                sendEventPushNotifications("'${conWatTstat.label}' has been turned 'OFF' because${openCtDesc}has been Opened for (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})...", "Info")
                                sendEventVoiceNotifications(voiceNotifString(atomicState?."${getAutoType()}OffVoiceMsg"))
                            }
                        } else { LogAction("conWatCheck(): Error turning themostat Off", "warn", true) }
                    } else {
                        if (getConWatRestoreDelayBetweenDtSec() < (getConWatRestoreDelayBetweenVal() - 2)) {
                            LogAction("conWatCheck() | Skipping change because the delay since last restore has been less than (${getEnumValue(longTimeSecEnum(), conWatRestoreDelayBetween)})", "info", false)
                            scheduleAutomationEval(getConWatRestoreDelayBetweenVal() - getConWatRestoreDelayBetweenDtSec())
                        } else { scheduleAutomationEval(60) }
                    }
                } else {
                    LogAction("conWatCheck() | Skipping change because '${conWatTstat?.label}' mode is already 'OFF'", "info", false)
                }
            } else {
                if (!schedOk) { LogAction("conWatCheck: Skipping because of Schedule Restrictions...", "info", true) }
                if (!safetyOk) { LogAction("conWatCheck: Skipping because of Safety Temps Exceeded...", "warn", true) }
            }
            storeExecutionHistory((now() - execTime), "conWatCheck")
        }
    } catch (ex) {
        log.error "conWatCheck Exception: (${ex})", "error", ex
        parent?.sendExceptionData(ex.message, "conWatCheck", true, getAutoType())
    }
}

def conWatTstatModeEvt(evt) {
    LogAction("ContactWatch Thermostat Mode Event | '${evt?.displayName}' Mode is now (${evt?.value.toString().toUpperCase()})", "trace", false)
    if (disableAutomation) { return }
    else {
        def modeOff = (evt?.value == "off") ? true : false
        if(!modeOff) { atomicState?.conWatTstatTurnedOff = false }
        else { atomicState?.conWatTstatTurnedOff = true }
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def conWatTstatTempEvt(evt) {
    LogAction("conWatTstatTempEvt Event | Thermostat Temperature: ${evt?.displayName} - Temperature is (${evt?.value.toString().toUpperCase()})", "trace", false)
    if(disableAutomation) { return }
    scheduleAutomationEval()
    storeLastEventData(evt)
}

def conWatContactEvt(evt) {
    LogAction("ContactWatch Contact Event | '${evt?.displayName}' is now (${evt?.value.toString().toUpperCase()})", "trace", false)
    if (disableAutomation) { return }
    else {
        def pName = conWatPrefix()
        def curMode = conWatTstat?.currentThermostatMode.toString()
        def isModeOff = (curMode == "off") ? true : false
        def conOpen = (evt?.value == "open") ? true : false
        def canSched = false
        def timeVal
        if (conOpen) {
            atomicState?.conWatOpenDt = getDtNow()
            timeVal = ["valNum":getConWatOffDelayVal(), "valLabel":getEnumValue(longTimeSecEnum(), getConWatOffDelayVal())]
            canSched = true
        }
        else if (!conOpen && getConWatContactsOk()) {
            if(isModeOff) {
                atomicState.conWatCloseDt = getDtNow()
                timeVal = ["valNum":getConWatOnDelayVal(), "valLabel":getEnumValue(longTimeSecEnum(), getConWatOnDelayVal())]
                canSched = true
            }
        }
        if(canSched) {
            LogAction("conWatContactEvt: ${!evt ? "A monitored Contact is " : "'${evt?.displayName}' is "} '${evt?.value.toString().toUpperCase()}' | Contact Check scheduled for (${timeVal?.valLabel})...", "info", true)
            storeLastEventData(evt)
            if (timeVal?.valNum > 20) {
                scheduleAutomationEval(timeVal?.valNum)
            } else { scheduleAutomationEval()}
        } else {
            LogAction("conWatContactEvt: Skipping Event...", "info", true)
        }
    }
}

/******************************************************************************
|                			WATCH FOR LEAKS AUTOMATION CODE	                  |
******************************************************************************/
def leakWatPrefix() { return "leakWat" }

def leakWatchPage() {
    def pName = leakWatPrefix()
    dynamicPage(name: "leakWatchPage", title: "Thermostat/Leak Automation", uninstall: false, nextPage: "mainAutoPage") {
        def dupTstat = checkThermostatDupe(leakWatTstat, leakWatTstatMir)
        section("When Leak is Detected, Turn Off this Thermostat") {
            def req = (leakWatSensors || leakWatTstat) ? true : false
            input name: "leakWatSensors", type: "capability.waterSensor", title: "Which Leak Sensor(s)?", multiple: true, submitOnChange: true, required: req,
                    image: getAppImg("water_icon.png")
            if(leakWatSensors) {
                paragraph "${leakWatSensorsDesc()}", state: "complete", image: getAppImg("instruct_icon.png")
            }
            input name: "leakWatTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: getAppImg("thermostat_icon.png")
            if(dupTstat) {
                paragraph "Duplicate Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", image: getAppImg("error_icon.png")
            }
            if(leakWatTstat) {
                getTstatCapabilities(leakWatTstat, leakWatPrefix())
                def str = ""
                str += leakWatTstat ? "Thermostat Status:" : ""
                str += leakWatTstat ? "\n├ Mode: (${leakWatTstat?.currentThermostatOperatingState.toString().capitalize()}/${leakWatTstat?.currentThermostatMode.toString().capitalize()})" : ""
                str += leakWatTstat ? "\n${settings?."${getAutoType()}UseSafetyTemps" ? "├" : "└"} Presence: (${getTstatPresence(leakWatTstat) == "present" ? "Home" : "Away"})" : ""
                str += leakWatTstat && getSafetyTemps(leakWatTstat) ? "\n└ Safefy Temps: \n     • Min: ${getSafetyTemps(leakWatTstat).min}°${atomicState?.tempUnit}/Max: ${getSafetyTemps(leakWatTstat).max}°${atomicState?.tempUnit}" : ""
                paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")

                input name: "leakWatTstatMir", type: "capability.thermostat", title: "Mirror commands to these Thermostats?", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("thermostat_icon.png")

                if(leakWatTstatMir && !dupTstat) {
                    leakWatTstatMir?.each { t ->
                        paragraph "Thermostat Temp: ${getDeviceTemp(t)}${atomicState?.tempUnit}", image: " "
                    }
                }
            }
        }
        if(leakWatSensors && leakWatTstat) {
// need to check if safety temps are set and != to each other
            section("Restoration Preferences (Optional):") {
                input "${getAutoType()}UseSafetyTemps", "bool", title: "Restore When Safety Temps are Exceeded?", defaultValue: true, submitOnChange: false, image: getAppImg("switch_icon.png")
            }
            section("Restore on when Dry:") {
                input name: "leakWatOnDelay", type: "enum", title: "Delay Restore (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
            }
            section("Notifications:") {
                href "setNotificationPage", title: "Configure Notifications...", description: getNotifConfigDesc(), params: ["pName":pName, "allowSpeech":true, "allowAlarm":true, "showSchedule":true],
                        state: (getNotificationOptionsConf() ? "complete" : null), image: getAppImg("notification_icon.png")
            }
        }
        if(atomicState?.showHelp) {
            section("Help:") {
                href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
            }
        }
    }
}

def leakWatSensorsDesc() {
    if(leakWatSensors) {
        def cCnt = leakWatSensors?.size() ?: 0
        def str = ""
        def cnt = 0
        str += "Leak Sensors:"
        leakWatSensors?.each { dev ->
            cnt = cnt+1
            str += "${(cnt >= 1) ? "${(cnt == cCnt) ? "\n└" : "\n├"}" : "\n└"} ${dev?.label}: ${dev?.currentWater?.toString().capitalize()}"
        }
        return str
    }
    return null
}

def isLeakWatConfigured() {
    return (leakWatSensors && leakWatTstat) ? true : false
}

def getLeakWatSensorsOk() { return leakWatSensors?.currentState("water")?.value.contains("wet") ? false : true }
def leakWatSensorsOk() { return (!leakWatSensors && !leakWatTstat) ? false : true }
def leakWatScheduleOk() { return autoScheduleOk(leakWatPrefix()) }
def getLeakWatOnDelayVal() { return !leakWatOnDelay ? 300 : (leakWatOnDelay.toInteger()) }
def getLeakWatDryDtSec() { return !atomicState?.leakWatDryDt ? 100000 : GetTimeDiffSeconds(atomicState?.leakWatDryDt).toInteger() }

def leakWatCheck() {
    //log.trace "leakWatCheck..."
//
// There should be monitoring of actual temps for min and max warnings given on/off automations
//   This could be set in Nest, but it is possible this automation is running on a non-Nest thermostat
//
// Should have some check for stuck contacts
// if we cannot save/restore settings, don't bother turning things off
//
    try {
        if (disableAutomation) { return }
        else {
            def execTime = now()
            atomicState?.lastEvalDt = getDtNow()
            def curMode = leakWatTstat?.currentState("thermostatMode")?.value.toString()
            def curNestPres = getTstatPresence(leakWatTstat)
            def modeOff = (curMode == "off") ? true : false
            def wetCtDesc = getWetWaterSensors(leakWatSensors) ? " '${getWetWaterSensors(leakWatSensors)?.join(", ")}' " : " a selected leak sensor "
            def safetyOk = getSafetyTempsOk(leakWatTstat)
            def schedOk = leakWatScheduleOk()
            def okToRestore = (modeOff && atomicState?.leakWatTstatOffRequested) ? true : false
            def allowNotif = settings?."${getAutoType()}NotificationsOn" ? true : false
            def allowSpeech = allowNotif && settings?."${getAutoType()}AllowSpeechNotif" ? true : false
            def allowAlarm = allowNotif && settings?."${getAutoType()}AllowAlarmNotif" ? true : false
            def speakOnRestore = allowSpeech && settings?."${getAutoType()}SpeechOnRestore" ? true : false

            if(getLeakWatSensorsOk() || !safetyOk) {
                if (allowAlarm) { alarmEvtSchedCleanup() }

                if(okToRestore) {
                    if(getLeakWatDryDtSec() >= (getLeakWatOnDelayVal() - 5) || !safetyOk) {
                        def lastMode = null
                        if(atomicState?.leakWatRestoreMode) { lastMode = atomicState?.leakWatRestoreMode }
                        if(lastMode && (lastMode != curMode || !safetyOk)) {
                            scheduleAutomationEval(180)
                            if(setTstatMode(leakWatTstat, lastMode)) {
                                storeLastAction("Restored Mode ($lastMode) to $leakWatTstat", getDtNow())
                                atomicState?.leakWatTstatOffRequested = false
                                atomicState?.leakWatRestoreMode = null

                                if(leakWatTstatMir) {
                                    if(setMultipleTstatMode(leakWatTstatMir, lastmode)) {
                                        LogAction("leakWatCheck: Mirroring Restoring Mode (${lastMode}) to ${tstat}", "info", true)
                                    }
                                }
                                if(!safetyOk) {
                                    LogAction("Restoring '${leakWatTstat?.label}' to '${lastMode.toUpperCase()}' mode because External Temp Safefy Temps have been reached...", "info", true)
                                } else {
                                    LogAction("Restoring '${leakWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' Mode because ALL leak sensors have been 'Dry' again for (${getEnumValue(longTimeSecEnum(), leakWatOnDelay)})...", "info", true)
                                }

                                if(allowNotif) {
                                    if(safetyOk) {
                                        sendEventPushNotifications("Restoring '${leakWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' Mode because ALL leak sensors have been 'Dry' again for (${getEnumValue(longTimeSecEnum(), leakWatOnDelay)})...", "Info")
                                        if(speakOnRestore) { sendEventVoiceNotifications(voiceNotifString("Restoring ${leakWatTstat} to ${lastMode?.toString().toUpperCase()} Mode because ALL leak sensors have been Dry again for (${getEnumValue(longTimeSecEnum(), leakWatOnDelay)})")) }
                                    }
                                }
                            } else { LogAction("leakWatCheck() | There was problem restoring the last mode to ${lastMode}...", "error", true) }
                        } else {
                            if(!safetyOk) {
                                LogAction("leakWatCheck() | Unable to restore mode and safety temperatures are exceeded", "warn", true)
                            } else {
                                LogAction("leakWatCheck() | Skipping Restore because the Mode to Restore is same as Current Mode ${curMode}", "info", true)
                            }
                        }
                    } else { if (safetyOk) { scheduleAutomationEval(60) } }
                } else {
                    if (modeOff) {
                        if (!safetyOk) {
                            LogAction("leakWatCheck() | Safety temps exceeded and Unable to restore settings okToRestore is false", "warn", true)
                        }
                        else if (!atomicState?.leakWatRestoreMode && atomicState?.leakWatTstatOffRequested) {
                            LogAction("leakWatCheck() | Unable to restore settings because previous mode was not found. Likely due to other automation making changes.", "warn", true)
                            atomicState?.leakWatTstatOffRequested = false
                        }
                    }
                }
            }

// tough decision here:  there is a leak, do we care about schedule ?
//            if (!getLeakWatSensorsOk() && safetyOk && schedOk) {
            if (!getLeakWatSensorsOk() && safetyOk) {
                if(!modeOff) {
                    atomicState?.leakWatRestoreMode = curMode
                    LogAction("leakWatCheck: Saving ${leakWatTstat?.label} mode (${atomicState?.leakWatRestoreMode.toString().toUpperCase()}) for Restore later.", "info", true)
                    LogAction("leakWatCheck: ${wetCtDesc}${getWetWaterSensors(leakWatSensors).size() > 1 ? "are" : "is"} Wet: Turning 'OFF' '${leakWatTstat?.label}'", "debug", true)
                    scheduleAutomationEval(180)
                    if(setTstatMode(leakWatTstat, "off")) {
                        storeLastAction("Turned Off $leakWatTstat", getDtNow())
                        atomicState?.leakWatTstatOffRequested = true
                        if (allowAlarm) { scheduleAlarmOn() }

                        if(leakWatTstatMir) {
                            if(setMultipleTstatMode(leakWatTstatMir, "off")) {
                                LogAction("leakWatCheck: Mirroring (Off) Mode to ${tstat}", "info", true)
                            }
                        }
                        LogAction("leakWatCheck: '${leakWatTstat.label}' has been turned 'OFF' because${wetCtDesc}has reported it's WET...", "warn", true)
                        if(allowNotif) {
                            sendEventPushNotifications("'${leakWatTstat.label}' has been turned 'OFF' because${wetCtDesc}has reported it's WET...", "Info")
                            sendEventVoiceNotifications(voiceNotifString("${leakWatTstat} has been turned OFF because${wetCtDesc}has reported it's WET..."))
                        }
                    } else { LogAction("leakWatCheck(): Error turning themostat Off", "warn", true) }
                } else {
                    LogAction("leakWatCheck() | Skipping change because '${leakWatTstat?.label}' mode is already 'OFF'", "info", true)
                }
            } else {
                if (!schedOk) { LogAction("leakWatCheck: Skipping because of Schedule Restrictions...", "warn", true) }
                if (!safetyOk) { LogAction("leakWatCheck: Skipping because of Safety Temps Exceeded...", "warn", true) }
            }
            storeExecutionHistory((now() - execTime), "leakWatCheck")
        }
    } catch (ex) {
        log.error "leakWatCheck Exception: (${ex})", ex
        parent?.sendExceptionData(ex.message, "leakWatCheck", true, getAutoType())
    }
}

def leakWatTstatModeEvt(evt) {
    LogAction("LeakWatch Thermostat Mode Event | '${evt?.displayName}' Mode is now (${evt?.value.toString().toUpperCase()})", "trace", false)
    if (disableAutomation) { return }
    else {
        def modeOff = (evt?.value == "off") ? true : false
        if(!modeOff) { atomicState?.leakWatTstatTurnedOff = false }
        else { atomicState?.leakWatTstatTurnedOff = true }
        scheduleAutomationEval()
        storeLastEventData(evt)
    }
}

def leakWatTstatTempEvt(evt) {
    LogAction("leakWatTstatTempEvt Event | Thermostat Temperature: ${evt?.displayName} - Temperature is (${evt?.value.toString().toUpperCase()})", "trace", true)
    if(disableAutomation) { return }
    scheduleAutomationEval()
    storeLastEventData(evt)
}

def leakWatSensorEvt(evt) {
  LogAction("LeakWatch Sensor Event | '${evt?.displayName}' is now (${evt?.value.toString().toUpperCase()})", "trace", false)
   if (disableAutomation) {  return }
    else {
        def pName = leakWatPrefix()
        def curMode = leakWatTstat?.currentThermostatMode.toString()
        def isModeOff = (curMode == "off") ? true : false
        def leakWet = (evt?.value == "wet") ? true : false

        def canSched = false
        def timeVal
        if (leakWet) {
            canSched = true
        }
        else if (!leakWet && getLeakWatSensorsOk()) {
            if(isModeOff) {
                atomicState?.leakWatDryDt = getDtNow()
                timeVal = ["valNum":getLeakWatOnDelayVal(), "valLabel":getEnumValue(longTimeSecEnum(), getLeakWatOnDelayVal())]
                canSched = true
            }
        }

        if(canSched) {
            LogAction("leakWatSensorEvt: ${!evt ? "A monitored Leak Sensor is " : "'${evt?.displayName}' is "} '${evt?.value.toString().toUpperCase()}' | Leak Check scheduled for (${timeVal?.valLabel})...", "info", true)
            storeLastEventData(evt)
            if (timeVal?.valNum > 20) {
                scheduleAutomationEval(timeVal?.valNum)
            } else {
                scheduleAutomationEval()
            }
        } else {
            LogAction("leakWatSensorEvt: Skipping Event...", "info", true)
        }
    }
}

/********************************************************************************
|                			MODE AUTOMATION CODE	     						|
*********************************************************************************/
def nModePrefix() { return "nMode" }

def nestModePresPage() {
    def pName = nModePrefix()
    dynamicPage(name: "nestModePresPage", title: "Mode - Nest Home/Away Automation", uninstall: false, nextPage: "mainAutoPage") {
        if(!nModePresSensor && !nModeSwitch) {
            def modeReq = (!nModePresSensor && (nModeHomeModes || nModeAwayModes))
            section("Set Nest Presence with ST Modes:") {
                input "nModeHomeModes", "mode", title: "Modes that set Nest 'Home'", multiple: true, submitOnChange: true, required: modeReq,
                        image: getAppImg("mode_home_icon.png")
                if(checkModeDuplication(nModeHomeModes, nModeAwayModes)) {
                    paragraph "Duplicate Mode(s) found under the Home or Away Mode!!!.  Please Correct...", image: getAppImg("error_icon.png")
                }
                input "nModeAwayModes", "mode", title: "Modes that set Nest 'Away'", multiple: true, submitOnChange: true, required: modeReq,
                        image: getAppImg("mode_away_icon.png")
                if (nModeHomeModes && nModeAwayModes) {
                    def str = ""
                    str += location?.mode && parent?.locationPresence() ? "Location Status:" : ""
                    str += location?.mode ? "\n ├ SmartThings Mode: ${location?.mode}" : ""
                    str += parent?.locationPresence() ? "\n └ Nest Location: (${parent?.locationPresence() == "away" ? "Away" : "Home"})" : ""
                    paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")
                }
            }
        }
        if(!nModeHomeModes && !nModeAwayModes && !nModeSwitch) {
            section("(Optional) Set Nest Presence using Presence Sensor:") {
                //paragraph "Choose a Presence Sensor(s) to use to set your Nest to Home/Away", image: getAppImg("instruct_icon")
                input "nModePresSensor", "capability.presenceSensor", title: "Select a Presence Sensor", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("presence_icon.png")
                if(nModePresSensor) {
                    if (nModePresSensor.size() > 1) {
                        paragraph "Nest will be set 'Away' when all Presence sensors leave and will return to 'Home' when someone arrives", image: getAppImg("instruct_icon.png")
                    }
                    paragraph "${nModePresenceDesc()}", state: "complete", image: getAppImg("instruct_icon.png")
                }
            }
        }
        if(!nModePresSensor && !nModeHomeModes && !nModeAwayModes) {
            section("(Optional) Set Nest Presence using a Switch:") {
                input "nModeSwitch", "capability.switch", title: "Select a Switch", required: false, multiple: false, submitOnChange: true, image: getAppImg("wall_switch_icon.png")
                if(nModeSwitch) {
                    input "nModeSwitchOpt", "enum", title: "Switch State to Trigger 'Away'?", required: true, defaultValue: "On", options: ["On", "Off"], submitOnChange: true, image: getAppImg("settings_icon.png")
                }
            }
        }
        if((nModeHomeModes && nModeAwayModes) || nModePresSensor || nModeSwitch) {
            section("Delay Changes:") {
                input (name: "nModeDelay", type: "bool", title: "Delay Changes?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("switch_icon.png"))
                if(nModeDelay) {
                    input "nModeDelayVal", "enum", title: "Delay before Changing?", required: false, defaultValue: 60, metadata: [values:longTimeSecEnum()],
                            submitOnChange: true, image: getAppImg("delay_time_icon.png")
                }
                if(parent?.settings?.cameras) {
                    input (name: "nModeCamOnAway", type: "bool", title: "Turn On Nest Cams when Away?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("camera_green_icon.png"))
                    input (name: "nModeCamOffHome", type: "bool", title: "Turn Off Nest Cams when Home?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("camera_gray_icon.png"))
                }
            }
        }
        if(((nModeHomeModes && nModeAwayModes) && !nModePresSensor) || nModePresSensor) {
            /*
            section("Notifications:") {
                href "setRecipientsPage", title: "(Optional) Select Recipients", description: getNotifConfigDesc(), params: [pName: "${pName}"], state: (getNotificationOptionsConf() ? "complete" : null),
                        image: getAppImg("recipient_icon.png")
            }*/
            section(getDmtSectionDesc(conWatPrefix())) {
                def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc ? "complete" : null),
                        image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                href "setNotificationPage", title: "Configure Alerts...", description: getNotifConfigDesc(), params: ["pName":pName, "allowSpeech":true, "allowAlarm":false, "showSchedule":false],
                        state: (getNotifConfigDesc() ? "complete" : null), image: getAppImg("notification_icon.png")
            }
        }
        if(atomicState?.showHelp) {
            section("Help:") {
                href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
            }
        }
    }
}

def nModePresenceDesc() {
    if(nModePresSensor) {
        def cCnt = nModePresSensor?.size() ?: 0
        def str = ""
        def cnt = 0
        str += "Presence Status:"
        nModePresSensor?.each { dev ->
            cnt = cnt+1
            str += "${(cnt >= 1) ? "${(cnt == cCnt) ? "\n└" : "\n├"}" : "\n└"} ${dev?.label}: ${(dev?.label.length() > 10) ? "\n${(cCnt == 1 || cnt == cCnt) ? "    " : "│"}└ " : ""}(${dev?.currentPresence?.toString().capitalize()})"
        }
        return str
    }
    return null
}

def isNestModesConfigured() {
    def devOk = ((!nModePresSensor && !nModeSwitch && (nModeHomeModes && nModeAwayModes)) || (nModePresSensor && !nModeSwitch) || (!nModePresSensor && nModeSwitch)) ? true : false
    return devOk
}

def nModeModeEvt(evt) {
    if (disableAutomation) { return }
    else if(!nModePresSensor && !nModeSwitch) {
        storeLastEventData(evt)
        if(nModeDelay) {
            def delay = nModeDelayVal.toInteger()

            if (delay > 20) {
                LogAction("Mode Event: ST Mode is ${evt?.value.toString().toUpperCase()} | A Mode Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
                scheduleAutomationEval(delay)
            } else { scheduleAutomationEval() }
        } else {
            LogAction("Mode Event | ST Mode is (${evt?.value.toString().toUpperCase()})", "trace", true)
            scheduleAutomationEval()
        }
    }
}

def nModePresEvt(evt) {
    if (disableAutomation) { return }
    else if(nModeDelay) {
        storeLastEventData(evt)
        def delay = nModeDelayVal.toInteger()

        if (delay > 20) {
            LogAction("Mode Event | Presence: ${!evt ? "A monitored presence device is " : "SWITCH '${evt?.displayName}' is "} (${evt?.value.toString().toUpperCase()}) | A Presence Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
            scheduleAutomationEval(delay)
        } else { scheduleAutomationEval() }
    } else {
        LogAction("NestMode Event | Presence is (${evt?.value.toString().toUpperCase()})", "trace", true)
        scheduleAutomationEval()
    }
}

def nModeSwitchEvt(evt) {
    if (disableAutomation) { return }
    else if(nModeSwitch && !nModePresSensor) {
        storeLastEventData(evt)
        if(nModeDelay) {
            def delay = nModeDelayVal.toInteger()
            if (delay > 20) {
                LogAction("Mode Event | ${!evt ? "A monitored switch is " : "Switch (${evt?.displayName}) is "} (${evt?.value.toString().toUpperCase()}) | A Switch Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
                scheduleAutomationEval(delay)
            } else { scheduleAutomationEval() }
        } else {
            LogAction("Mode Event | Switch (${evt?.displayName}) is (${evt?.value.toString().toUpperCase()})", "trace", true)
            scheduleAutomationEval()
        }
    }
}

def nModeScheduleOk() { return autoScheduleOk(nModePrefix()) }

def checkNestMode() {
    LogAction("checkNestMode...", "trace", false)
//
// This automation only works with Nest as it toggles non-ST standard home/away
//
    try {
        if (disableAutomation) { return }
        else if(!nModeScheduleOk()) {
            LogAction("checkNestMode: Skipping because of Schedule Restrictions...", "info", true)
        } else {
            def execTime = now()
            atomicState?.lastEvalDt = getDtNow()
            def curStMode = location?.mode
            def allowNotif = settings?."${getAutoType()}NotificationsOn" ? true : false
            def nestModeAway = (getNestLocPres() == "home") ? false : true
            def awayPresDesc = (nModePresSensor && !nModeSwitch) ? "All Presence device(s) have left setting " : ""
            def homePresDesc = (nModePresSensor && !nModeSwitch) ? "A Presence Device is Now Present setting " : ""
            def awaySwitDesc = (nModeSwitch && !nModePresSensor) ? "${nModeSwitch} State is 'Away' setting " : ""
            def homeSwitDesc = (nModeSwitch && !nModePresSensor) ? "${nModeSwitch} State is 'Home' setting " : ""
            def modeDesc = ((!nModeSwitch && !nModePresSensor) && nModeHomeModes && nModeAwayModes) ? "The mode (${curStMode}) has triggered " : ""
            def awayDesc = "${awayPresDesc}${awaySwitDesc}${modeDesc}"
            def homeDesc = "${homePresDesc}${homeSwitDesc}${modeDesc}"
            def away = false
            def home = false
            if(nModePresSensor || nModeSwitch) {
                if (nModePresSensor && !nModeSwitch) {
                    if (!isPresenceHome(nModePresSensor)) { away = true }
                    else { home = true }
                }
                else if (nModeSwitch && !nModePresSensor) {
                    def swOptAwayOn = (nModeSwitchOpt == "On") ? true : false
                    if(swOptAwayOn) {
                        !isSwitchOn(nModeSwitch) ? (home = true) : (away = true)
                    } else {
                        !isSwitchOn(nModeSwitch) ? (away = true) : (home = true)
                    }
                }
            }
            else {
                if(nModeHomeModes && nModeAwayModes) {
                    if (isInMode(nModeHomeModes)) { home = true }
                    else {
                        if (isInMode(nModeAwayModes)) { away = true }
                    }
                }
            }

            LogAction("checkNestMode: isPresenceHome: (${nModePresSensor ? "${isPresenceHome(nModePresSensor)}" : "Presence Not Used"}) | ST-Mode: ($curStMode) | NestModeAway: ($nestModeAway) | Away?: ($away) | Home?: ($home)", "info", true)

            if (away && !nestModeAway) {
                LogAction("${awayDesc} Nest 'Away'", "info", true)
                if(parent?.setStructureAway(null, true)) {
                    storeLastAction("Set Nest Location (Away)", getDtNow())
                    atomicState?.nModeTstatLocAway = true
                    if(allowNotif) {
                        sendEventPushNotifications("${awayDesc} Nest 'Away'", "Info")
                    }
                    if(nModeCamOnAway) {
                        def cams = parent?.settings?.cameras
                        cams?.each { cam ->
                            def dev = getChildDevice(cam)
                            if(dev) {
                                //storeLastAction("Turned On Streaming for $cam", getDtNow())
                                dev?.on()
                                LogAction("checkNestMode: Turning Streaming On for (${dev}) because Location is now Away...", "info", true)
                            }
                        }
                    }
                } else {
                    LogAction("checkNestMode: There was an issue sending the AWAY command to Nest", "error", true)
                }
                scheduleAutomationEval(60)
            }
            else if (home && nestModeAway) {
                LogAction("${homeDesc} Nest 'Home'", "info", true)
                if (parent?.setStructureAway(null, false)) {
                    atomicState?.nModeTstatLocAway = false
                    if(allowNotif) {
                        sendEventPushNotifications("${awayDesc} Nest 'Home'", "Info")
                    }
                    if(nModeCamOffHome) {
                        def cams = parent?.settings?.cameras
                        cams?.each { cam ->
                            def dev = getChildDevice(cam)
                            if(dev) {
                                dev?.off()
                                LogAction("checkNestMode: Turning Streaming Off for (${dev}) because Location is now Home...", "info", true)
                                //storeLastAction("Turned Streaming Off for $cam", getDtNow())
                            }
                        }
                    }
                } else {
                    LogAction("checkNestMode: There was an issue sending the AWAY command to Nest", "error", true)
                }
                scheduleAutomationEval(60)
            }
            else {
                LogAction("checkNestMode: Conditions are not valid to change mode | isPresenceHome: (${nModePresSensor ? "${isPresenceHome(nModePresSensor)}" : "Presence Not Used"}) | ST-Mode: ($curStMode) | NestModeAway: ($nestModeAway) | Away?: ($away) | Home?: ($home)", "info", true)
            }
            storeExecutionHistory((now() - execTime), "checkNestMode")
        }
    } catch (ex) {
        log.error "checkNestMode Exception: (${ex})", ex
        parent?.sendExceptionData(ex.message, "checkNestMode", true, getAutoType())
    }
}

def getNestLocPres() {
    if (disableAutomation) { return }
    else if(!parent?.locationPresence()) { return null }
    else {
        return parent?.locationPresence()
    }
}

/********************************************************************************
|       ST MODE CHANGES ADJUST THERMOSTAT SETPOINTS (AND THERMOSTAT MODE) AUTOMATION CODE	 |
*********************************************************************************/
def tModePrefix() { return "tMode" }

def tstatModePage() {
    def pName = tModePrefix()
    dynamicPage(name: "tstatModePage", title: "Thermostat Setpoint Mode Automation", uninstall: false, nextPage: "mainAutoPage") {
        section("Select the Thermostats you would like to adjust:") {
            input name: "tModeTstats", type: "capability.thermostat", title: "Which Thermostats?", multiple: true, submitOnChange: true, required: true, image: getAppImg("thermostat_icon.png")
        }
        if (tModeTstats) {
            tModeTstats?.each { ts ->
                getTstatCapabilities(ts, tModePrefix(), true)
                def canHeat = atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanHeat"
                def canCool = atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanCool"
                section("${ts?.displayName} Configuration:") {
                    def str = ""
                    str += "Current Status:"
                    str += "\n• Temperature: (${getDeviceTemp(ts)}°${atomicState?.tempUnit})"
                    str += "\n• Setpoints: (${canHeat ? "H: ${getTstatSetpoint(ts, "heat")}°${atomicState?.tempUnit}" : ""}/${canCool ? "C: ${getTstatSetpoint(ts, "cool")}°${atomicState?.tempUnit}" : ""})"
                    str += "\n• Mode: (${ts ? ("${ts?.currentThermostatOperatingState.toString().capitalize()}/${ts?.currentThermostatMode.toString().capitalize()}") : "unknown"})"
                    def tstatDesc = (settings?."${getTstatModeInputName(ts)}" ? "Configured Modes:${getTstatModeDesc(ts)}" : "")
                    href "tModeTstatConfModePage", title: "Select Modes and Setpoints...", description: ( getTstatConfigured(ts) ? "${tstatDesc}\n\nTap to Modify" : "Tap to Configure..."),
                            params: [devName: "${ts?.displayName}", devId: "${ts?.device.deviceNetworkId}"],
                            state: ( getTstatConfigured(ts) ? "complete" : null ), image: getAppImg("thermostat_icon.png")
                    paragraph "${str}", image: getAppImg("instruct_icon.png")
                }
            }
        }
        if(tModeTstats) {
            section("Delay Changes:") {
                input (name: "tModeDelay", type: "bool", title: "Delay Changes?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("switch_icon.png"))
                if(tModeDelay) {
                    input "tModeDelayVal", "enum", title: "Delay before Changing?", required: false, defaultValue: 60, metadata: [values:longTimeSecEnum()],
                            submitOnChange: true, image: getAppImg("delay_time_icon.png")
                }
            }
        }
        if(atomicState?.showHelp) {
            section("Help:") {
                href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
            }
        }
    }
}

def tModeTstatConfModePage(params) {
    def devName
    def devId
    if (!params.devId && !params?.devName) {
        devId = atomicState?.curTstatModePageDevId
        devName = atomicState?.curTstatModePageDevName }
    else {
        atomicState.curTstatModePageDevName = params?.devName
        atomicState.curTstatModePageDevId = params?.devId
        devId = params?.devId
        devName = params?.devName
    }
    dynamicPage(name: "tModeTstatConfModePage", title: "${devName} Configuration", install: false, uninstall: false) {
        def preName = "tMode_|${devId}|_Modes"
        def canHeat = atomicState?."tMode_${devId}_TstatCanHeat"
        def canCool = atomicState?."tMode_${devId}_TstatCanCool"
        section(" ") {
            input "${preName}", "mode", title: "Select the Modes...", multiple: true, required: true, submitOnChange: true, image: getAppImg("mode_icon.png")
        }
        if (settings."${preName}") {
            settings."${preName}"?.each { md ->
                section("(${md.toString().toUpperCase()}) Options:") {
                    def tempReq = ( canHeat && canCool || (!canCool && canHeat && !settings."${preName}_${md}_HeatTemp") || (canCool && !canHeat && !settings."${preName}_${md}_CoolTemp") ) ? true : false
                    if(canHeat) {
                        input "${preName}_${md}_HeatTemp", "decimal", title: "${md}\nSet Heat Temp (°${atomicState?.tempUnit})", required: tempReq,
                                range: (atomicState?.tempUnit == "C") ? "10..32" : "50..90", submitOnChange: false, image: getAppImg("heat_icon.png")
                    }
                    if(canCool) {
                        input "${preName}_${md}_CoolTemp", "decimal", title: "${md}\nSet Cool Temp (°${atomicState?.tempUnit})", required: tempReq,
                                range: (atomicState?.tempUnit == "C") ? "10..32" : "50..90",submitOnChange: true, image: getAppImg("cool_icon.png")
                    }
                    input "${preName}_${md}_HvacMode", "enum", title: "${md}\nSet Hvac Mode (Optional)", required: false,  defaultValue: null, metadata: [values:tModeHvacEnum()],
                            submitOnChange: true, image: getAppImg("mode_icon.png")
                }
            }
        }
    }
}

def getTstatModeDesc(tstat = null) {
    if(tModeTstats) {
        def dstr = ""
        def num = 0
        def preName = null
        if(!tstat) {
            tModeTstats?.each { ts ->
                num = num+1
                preName = getTstatModeInputName(ts)
                def canHeat = atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanHeat"
                def canCool = atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanCool"
                dstr += "${num > 1 ? "\n\n" : ""}${ts?.displayName}:"
                if(settings?."${preName}") {
                    settings?."${preName}".each { md ->
                        dstr += "\n• ${md.toString().capitalize()}: ${md.length() > 10 ? "\n   " : ""}"
                        dstr += "(${canHeat ? "♨ ${settings?."${preName}_${md}_HeatTemp"}°${atomicState?.tempUnit}" : ""}${canHeat && canCool ? " | " : ""}${canCool ? "❆ ${settings?."${preName}_${md}_CoolTemp"}°${atomicState?.tempUnit}" : ""}"
                        dstr += (settings?."${preName}_${md}_HvacMode" && (getEnumValue(tModeHvacEnum(), settings?."${preName}_${md}_HvacMode") != "unknown")) ?
                            " | M: ${getEnumValue(tModeHvacEnum(), settings?."${preName}_${md}_HvacMode")})" : ")"
                    }
                }
            }
        } else {
            preName = getTstatModeInputName(tstat)
            if(settings?."${preName}") {
                def canHeat = atomicState?."tMode_${tstat?.device?.deviceNetworkId}_TstatCanHeat"
                def canCool = atomicState?."tMode_${tstat?.device?.deviceNetworkId}_TstatCanCool"
                settings?."${preName}".each { md ->
                    dstr += "\n• ${md.toString().capitalize()}: ${md.length() > 10 ? "\n   " : ""}"
                    dstr += "(${canHeat ? "♨ ${settings?."${preName}_${md}_HeatTemp"}°${atomicState?.tempUnit}" : ""}${canHeat && canCool ? " | " : ""}${canCool ? "❆ ${settings?."${preName}_${md}_CoolTemp"}°${atomicState?.tempUnit}" : ""}"
                    dstr += (settings?."${preName}_${md}_HvacMode" && (getEnumValue(tModeHvacEnum(), settings?."${preName}_${md}_HvacMode") != "unknown")) ?
                            " | M: ${getEnumValue(tModeHvacEnum(), settings?."${preName}_${md}_HvacMode")})" : ")"
                }
            }
        }
        return dstr
    }
}

def getTstatConfigured(tstat) {
    def result = true
    def preName = getTstatModeInputName(tstat)
    if(settings?."${preName}") {
        settings?."${preName}".each { md ->
            if (!settings?."${preName}_${md}_HeatTemp" || !settings?."${preName}_${md}_CoolTemp") { return false }
        }
    } else { return false }
    return result
}

def getTstatModeInputName(tstat) {
    if(tstat) {
        return "tMode_|${tstat?.device.deviceNetworkId}|_Modes"
    }
    return null
}

def isTstatModesConfigured() {
    def res = []
    if (tModeTstats) {
        tModeTstats?.each { ts ->
            res << [ getTstatConfigured(ts) ]
        }
        if(!res?.contains("false")) { return true }
    } else { return false}
    return false
}

def tModeModeEvt(evt) {
    if (disableAutomation) { return }
    else {
        storeLastEventData(evt)
        if(tModeDelay) {
            def delay = tModeDelayVal.toInteger()

            if (delay > 20) {
                LogAction("tModeModeEvt | ST Mode is: ${evt?.value} | A Mode Check is scheduled for (${getEnumValue(longTimeSecEnum(), tModeDelayVal)})", "info", true)
                scheduleAutomationEval(delay)
            } else { scheduleAutomationEval() }
        } else {
            LogAction("tModeModeEvt | ST Mode is: (${evt?.value})", "trace", true)
            scheduleAutomationEval()
        }
    }
}

def tModePresEvt(evt) {
    if (disableAutomation) { return }
    else {
        storeLastEventData(evt)
        if(tModeDelay) {
            def delay = tModeDelayVal.toInteger()

            if (delay > 20) {
                LogAction("TstatSetpoint Event | Presence: ${evt?.displayName} - Presence is (${evt?.value.toString().toUpperCase()}) | A Mode Check is scheduled for (${getEnumValue(longTimeSecEnum(), tModeDelayVal)})", "trace", true)
                scheduleAutomationEval(delay)
            } else { scheduleAutomationEval() }
        } else {
            LogAction("TstatSetpoint Event | Presence: ${evt?.displayName} - Presence is (${evt?.value.toString().toUpperCase()})", "trace", true)
            scheduleAutomationEval()
        }
    }
}

def checkTstatMode() {
    LogAction("checkTstatMode...", "trace", false)
//
// This automation only works with Nest as it checks non-ST presence & thermostat capabilities
// Presumes:
//       all thermostats in an automation are in the same Nest structure, so that all react to home/away changes
//
    try {
        if (disableAutomation) { return }
        def execTime = now()
        atomicState?.lastEvalDt = getDtNow()
        def away = (getNestLocPres() == "home") ? false : true

        //else if(!tModeScheduleOk()) {
          //  LogAction(": Skipping because of Schedule Restrictions...")
        //}
        if (away) {
            LogAction("checkTstatMode: Skipping because Nest is set AWAY", "info", true)
            return
        }
        else {
            def curStMode = location?.mode
            def heatTemp = 0.0
            def coolTemp = 0.0
            if (tModeTstats) {
                tModeTstats?.each { ts ->
                    def modes = settings?."${getTstatModeInputName(ts)}" ?: null
                    if (modes && (curStMode in modes)) {
                        def newHvacMode = settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HvacMode" ?
                            (settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HvacMode" == "heat-cool" ? "auto" : settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HvacMode") : null
                        def tstatHvacMode = ts?.currentThermostatMode?.toString()
                        if(newHvacMode && (newHvacMode.toString() != tstatHvacMode)) {
                            if(setTstatMode(ts, newHvacMode)) {
                                storeLastAction("Set $ts Mode to ${newHvacMode.toString().capitalize()}", getDtNow())
                                LogAction("checkTstatMode: Setting Thermostat Mode to '${newHvacMode?.toString().capitalize()}' on ($ts)", "info", true)
                            } else { LogAction("checkTstatMode: Error Setting Thermostat Mode to '${newHvacMode?.toString().capitalize()}' on ($ts)", "warn", true) }
                        }

                        def curMode = ts?.currentThermostatMode?.toString()
                        def isModeOff = (curMode == "off") ? true : false
                        tstatHvacMode = curMode

                        heatTemp = null
                        coolTemp = null

                        if(!isModeOff && atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanHeat") {
                            def oldHeat = ts?.currentHeatingSetpoint.toDouble()
                            heatTemp = settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HeatTemp".toDouble()
                            def temp = 0.0
                            if ( getTemperatureScale() == "C") {
                                temp = Math.round(heatTemp.round(1) * 2) / 2.0f
                            } else {
                                temp = Math.round(heatTemp.round(0)).toInteger()
                            }
                            heatTemp = temp
                            if(oldHeat != heatTemp) {
                                LogAction("checkTstatMode Setting Heat Setpoint to '${heatTemp}' on ($ts) old: ${oldHeat}", "info", false)
                                storeLastAction("Set $ts Heat Setpoint to ${heatTemp}", getDtNow())
//                                ts?.setHeatingSetpoint(heatTemp.toDouble())
                            } else { heatTemp = null }
                        }


                        if(!isModeOff && atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanCool") {
                            def oldCool = ts?.currentCoolingSetpoint.toDouble()
                            coolTemp = settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_CoolTemp".toDouble()
                            def temp = 0.0
                            if ( getTemperatureScale() == "C") {
                                temp = Math.round(coolTemp.round(1) * 2) / 2.0f
                            } else {
                                temp = Math.round(coolTemp.round(0)).toInteger()
                            }
                            coolTemp = temp
                            if(oldCool != coolTemp) {
                                LogAction("checkTstatMode: Setting Cool Setpoint to '${coolTemp}' on ($ts) old: ${oldCool}", "info", false)
//                                ts?.setCoolingSetpoint(coolTemp.toDouble())
                                storeLastAction("Set $ts Cool Setpoint to ${coolTemp}", getDtNow())
                            } else { coolTemp = null }
                        }
                        if (setTstatAutoTemps(ts, coolTemp?.toDouble(), heatTemp?.toDouble())) {
                            LogAction("checkTstatMode: Temp Change | $modes | newHvacMode: $newHvacMode | tstatHvacMode: $tstatHvacMode | heatTemp: $heatTemp | coolTemp: $coolTemp | curStMode: $curStMode", "info", true)
                        } else {
                            LogAction("checkTstatMode: set ERROR | $modes | newHvacMode: $newHvacMode | tstatHvacMode: $tstatHvacMode | heatTemp: $heatTemp | coolTemp: $coolTemp | curStMode: $curStMode", "info", true)
                        }
                    }
                }
            }
        }
        storeExecutionHistory((now() - execTime), "checkTstaMode")
    } catch (ex) {
        log.error "checkTstaMode Exception: (${ex})", ex
        parent?.sendExceptionData(ex.message, "checkTstatMode", true, getAutoType())
    }
}

/************************************************************************************************
|						              SCHEDULER METHOD						                	|
*************************************************************************************************/
def setRunSchedule(seconds, funct) {
    if(ssecondsec) {
        def timeVal = now()+(seconds * 1000)
        schedule(timeVal, "$funct", [overwrite: true])
    }
}

def storeLastEventData(evt) {
    if(evt) {
        atomicState?.lastEventData = ["name":evt.name, "displayName":evt.displayName, "value":evt.value, "date":evt.date, "unit":evt.unit]
        //log.debug "LastEvent: ${atomicState?.lastEventData}"
    }
}

def storeExecutionHistory(val, method = null) {
    //log.debug "storeExecutionHistory($val, $method)"
    try {
        if(method) {
            log.debug "${method} Execution Time: (${val} milliseconds)"
        }
        atomicState?.lastExecutionTime = val ?: null
        def list = atomicState?.evalExecutionHistory ?: []
        def listSize = 10
        if(list?.size() < listSize) {
            list.push(val)
        }
        else if (list?.size() > listSize) {
            def nSz = (list?.size()-listSize) + 1
            def nList = list?.drop(nSz)
            nList?.push(val)
            list = nList
        }
        else if (list?.size() == listSize) {
            def nList = list?.drop(1)
            nList?.push(val)
            list = nList
        }
        if(list) { atomicState?.evalExecutionHistory = list }
    } catch (ex) {
        log.error "storeExecutionHistory Exception: ${ex}", ex
        sendExceptionData(ex.message, "storeExecutionHistory")
    }
}

def getAverageValue(items) {
    def tmpAvg = []
    def val = 0
    if(!items) { return val }
    else if(items?.size() > 1) {
        tmpAvg = items
        if(tmpAvg && tmpAvg?.size() > 1) { val = (tmpAvg?.sum().toDouble() / tmpAvg?.size().toDouble()).round(0) }
    } else { val = item }
    return val.toInteger()
}

/************************************************************************************************
|				              SEND NOTIFICATIONS VIA PARENT APP							        |
*************************************************************************************************/
def sendNofificationMsg(msg, msgType, recips = null, sms = null, push = null) {
    if(recips || sms || push) {
        parent?.sendMsg(msgType, msg, recips, sms, push)
        //LogAction("Send Push Notification to $recips...", "info", true)
    } else {
        parent?.sendMsg(msgType, msg)
    }
}

/************************************************************************************************
|							        DYNAMIC NOTIFICATION PAGES							        |
*************************************************************************************************/

def setNotificationPage(params) {
    def pName = getAutoType()
    def allowSpeech = false
    def allowAlarm = false
    def showSched = false
    if(params?.pName) {
        atomicState.curNotifPageData = params
        allowSpeech = params?.allowSpeech?.toBoolean(); showSched = params?.showSchedule?.toBoolean(); allowAlarm = params?.allowAlarm?.toBoolean()
    } else {
        allowSpeech = atomicState?.curNotifPageData?.allowSpeech; showSched = atomicState?.curNotifPageData?.showSchedule; allowAlarm = atomicState?.curNotifPageData?.allowAlarm
    }
    dynamicPage(name: "setNotificationPage", title: "Configure Notification Options", uninstall: false) {
        section("Notification Preferences:") {
            if(!settings["${getAutoType()}NotificationsOn"]) {
                paragraph "Turn On to Allow:\nText, Voice, and Alarm Notifications...", required: true, image: getAppImg("instruct_icon.png"), state: null
            }
            input "${pName}NotificationsOn", "bool", title: "Enable Notifications?", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
        }
        if(settings["${pName}NotificationsOn"]) {
            def notifDesc = !location.contactBookEnabled ? "Enable Push Messages Below..." : "(Manager App Recipients are Used by Default)\n\nYou can customize who receives notifications"
            section("${notifDesc}") {
                if(!location.contactBookEnabled) {
                    input "${pName}UsePush", "bool", title: "Send Push Notitifications\n(Optional)", required: false, submitOnChange: true, defaultValue: false, image: getAppImg("notification_icon.png")
                } else {
                    input("${pName}NotifRecips", "contact", title: "Select Contacts...\n(Optional)", required: false, submitOnChange: true, image: getAppImg("recipient_icon.png")) {
                        input ("${pName}NotifPhones", "phone", title: "Phone Number to Send SMS to...\n(Optional)", submitOnChange: true, description: "Phone Number", required: false)
                    }
                }
            }
        }
        if(showSchedule && settings["${pName}NotificationsOn"]) {
            section(title: "Time Restrictions") {
                href "setNotificationTimePage", title: "Silence Notifications...", description: (getNotifSchedDesc() ?: "Tap to configure..."), state: (getNotifSchedDesc() ? "complete" : null), image: getAppImg("quiet_time_icon.png")
            }
        }

        if(allowSpeech && settings?."${pName}NotificationsOn") {
            section("Voice Notification Preferences:") {
                input "${pName}AllowSpeechNotif", "bool", title: "Enable Voice?", required: false, defaultValue: (settings?."${pName}AllowSpeechNotif" ? true : false), submitOnChange: true, image: getAppImg("speech_icon.png")
                if(settings["${pName}AllowSpeechNotif"]) {
                    if(pName == "conWat") {
                        if (!atomicState?."${pName}OffVoiceMsg" || !settings["${pName}UseCustomSpeechNotifMsg"]) { atomicState?."${pName}OffVoiceMsg" = "ATTENTION: %devicename% has been turned OFF because %opencontact% has been Opened for (%offdelay%)" }
                        if (!atomicState?."${pName}OnVoiceMsg" || !settings["${pName}UseCustomSpeechNotifMsg"]) { atomicState?."${pName}OnVoiceMsg" = "Restoring %devicename% to %lastmode% Mode because ALL contacts have been Closed again for (%ondelay%)" }
                    }
                    if(getAutoType() == "extTmp") {
                        if (!atomicState?."${pName}OffVoiceMsg" || !settings["${pName}UseCustomSpeechNotifMsg"]) { atomicState?."${pName}OffVoiceMsg" = "ATTENTION: %devicename% has been turned OFF because External Temp is above the temp threshold for (%offdelay%)" }
                        if (!atomicState?."${pName}OnVoiceMsg" || !settings["${pName}UseCustomSpeechNotifMsg"]) { atomicState?."${pName}OnVoiceMsg" = "Restoring %devicename% to %lastmode% Mode because External Temp has been above the temp threshold for (%ondelay%)" }
                    }
                    input "${pName}SpeechMediaPlayer", "capability.musicPlayer", title: "Select Media Player Devices", hideWhenEmpty: true, multiple: true, required: false, submitOnChange: true, image: getAppImg("media_player.png")
                    input "${pName}SpeechDevices", "capability.speechSynthesis", title: "Select Speech Synthesis Devices", hideWhenEmpty: true, multiple: true, required: false, submitOnChange: true, image: getAppImg("speech2_icon.png")
                    if(settings["${pName}SpeechMediaPlayer"]) {
                        input "${pName}SpeechVolumeLevel", "number", title: "Default Volume Level?", required: false, defaultValue: 30, range: "0::100", submitOnChange: true, image: getAppImg("volume_icon.png")
                        input "${pName}SpeechAllowResume", "bool", title: "Can Resume Playing Media?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("resume_icon.png")
                    }
                    if( (settings["${pName}SpeechMediaPlayer"] || settings["${pName}SpeechDevices"]) && getAutoType() in ["conWat", "extTmp","leakWat"]) {
                        def desc = ""
                        switch(getAutoType()) {
                            case "conWat":
                                desc = "Contact Close"
                                break
                            case "extTmp":
                                desc = "Temp Threshold Change"
                                break
                            case "leakWat":
                                desc = "Water Dried"
                                break
                        }
                        input name: "${pName}SpeechOnRestore", type: "bool", title: "Speak on ${desc}?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("speech_icon.png")
                    }
                    input "${pName}UseCustomSpeechNotifMsg", "bool", title: "Customize Notitification Message?", required: false, defaultValue: (settings?."${pName}AllowSpeechNotif" ? false : true), submitOnChange: true,
                        image: getAppImg("speech_icon.png")
                    if(settings["${pName}UseCustomSpeechNotifMsg"]) {
                        if(pName in ["conWat", "extTmp"]) {
                            def str = ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • DeviceName: %devicename%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • Last Mode: %lastmode%" : ""
                            str += (pName == "conWat") ? "\n • Open Contact: %opencontact%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • Off Delay: %offdelay%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • On Delay: %ondelay%" : ""
                            str += (pName == "extTmp") ? "\n • Temp Threshold: %tempthreshold%" : ""
                            paragraph "These Variables are accepted: ${str}"
                        }
                        input "${pName}CustomOffSpeechMessage", "text", title: "Turn Off Message?", required: false, defaultValue: atomicState?."${pName}OffVoiceMsg" , submitOnChange: true, image: getAppImg("speech_icon.png")
                        if(settings?."${pName}CustomOffSpeechMessage" && pName in ["conWat", "extTmp"]) {
                            atomicState?."${pName}OffVoiceMsg" = settings?."${pName}CustomOffSpeechMessage"
                            paragraph "Off Msg:\n" + voiceNotifString(atomicState?."${pName}OffVoiceMsg")
                        }
                        input "${pName}CustomOnSpeechMessage", "text", title: "Restore On Message?", required: false, defaultValue: atomicState?."${pName}OnVoiceMsg", submitOnChange: true, image: getAppImg("speech_icon.png")
                        if(settings?."${pName}CustomOnSpeechMessage" && getAutoType() in ["conWat", "extTmp"]) {
                            atomicState?."${pName}OnVoiceMsg" = settings?."${pName}CustomOnSpeechMessage"
                            paragraph "Restore On Msg:\n" + voiceNotifString(atomicState?."${pName}OnVoiceMsg")
                        }
                    }
                }
            }
        }
        if(allowAlarm && settings?."${pName}NotificationsOn") {
            section("Alarm/Siren Device Preferences:", hideWhenEmpty: true) {
                input "${pName}AllowAlarmNotif", "bool", title: "Enable Alarm|Siren?", required: false, defaultValue: (settings?."${pName}AllowAlarmNotif" ? true : false), submitOnChange: true,
                        image: getAppImg("alarm_icon.png")
                if(settings["${pName}AllowAlarmNotif"]) {
                    input "${pName}AlarmDevices", "capability.alarm", title: "Select Alarm/Siren Devices", multiple: true, required: false, submitOnChange: true, image: getAppImg("alarm_icon.png")
                }
            }
        }
        if(getAutoType() in ["conWat", "leakWat"] && settings["${pName}NotificationsOn"] && (settings["${pName}AllowSpeechNotif"] || settings["${pName}AllowAlarmNotif"])) {
            section("Notification Alert Options (1):") {
                input "${pName}_Alert_1_Delay", "enum", title: "First Alert Delay (in minutes)", defaultValue: null, required: false, submitOnChange: true, metadata: [values:longTimeSecEnum()],
                        image: getAppImg("alert_icon2.png")
                if(settings?."${pName}_Alert_1_Delay") {
                    if(settings?."${pName}NotificationsOn" && (settings["${pName}UsePush"] || settings["${pName}NotifRecips"] || settings["${pName}NotifPhones"])) {
                        input "${pName}_Alert_1_Send_Push", "bool", title: "Send Push Notification?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("notification_icon.png")
                        if(settings["${pName}_Alert_1_Send_Push"]) {
                            input "${pName}_Alert_1_Send_Custom_Push", "bool", title: "Custom Push Message?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("notification_icon.png")
                            if(settings["${pName}_Alert_1_Send_Custom_Push"]) {
                                input "${pName}_Alert_1_CustomPushMessage", "text", title: "Push Message to Send?", required: false, defaultValue: atomicState?."${pName}OffVoiceMsg" , submitOnChange: true, image: getAppImg("speech_icon.png")
                            }
                        }
                    }
                    if(settings?."${pName}AllowSpeechNotif") {
                        input "${pName}_Alert_1_Use_Speech", "bool", title: "Send Voice Notification?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("speech_icon.png")
                        if(settings["${pName}_Alert_1_Use_Speech"]) {
                            input "${pName}_Alert_1_Send_Custom_Speech", "bool", title: "Custom Speech Message?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("speech_icon.png")
                            if(settings["${pName}_Alert_1_Send_Custom_Speech"]) {
                                input "${pName}_Alert_1_CustomSpeechMessage", "text", title: "Push Message to Send?", required: false, defaultValue: atomicState?."${pName}OffVoiceMsg" , submitOnChange: true, image: getAppImg("speech_icon.png")
                            }
                        }
                    }
                    if(settings?."${pName}AllowAlarmNotif") {
                        input "${pName}_Alert_1_Use_Alarm", "bool", title: "Use Alarm Device", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("alarm_icon.png")
                        if(settings?."${pName}_Alert_1_Use_Alarm" && settings?."${pName}AlarmDevices") {
                            input "${pName}_Alert_1_AlarmType", "enum", title: "Alarm Type to use?", metadata: [values:alarmActionsEnum()], defaultValue: "strobe", submitOnChange: true, required: false, image: getAppImg("alarm_icon.png")
                            if(settings["${pName}_Alert_1_AlarmType"]) {
                                input "${pName}_Alert_1_Alarm_Runtime", "enum", title: "Turn off Alarm After (in seconds)?", metadata: [values:shortTimeEnum()], defaultValue: 15, required: false, submitOnChange: true,
                                        image: getAppImg("delay_time_icon.png")
                            }
                        }
                    }
                    if(settings["${pName}_Alert_1_Send_Custom_Speech"] || settings["${pName}_Alert_1_Send_Custom_Push"]) {
                        if(pName in ["conWat", "extTmp"]) {
                            def str = ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • DeviceName: %devicename%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • Last Mode: %lastmode%" : ""
                            str += (pName == "conWat") ? "\n • Open Contact: %opencontact%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • Off Delay: %offdelay%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • On Delay: %ondelay%" : ""
                            str += (pName == "extTmp") ? "\n • Temp Threshold: %tempthreshold%" : ""
                            paragraph "These Variables are accepted: ${str}", state: "complete", image: getAppImg("instruct_icon.png")
                        }
                    }
                }
            }
            if(settings["${pName}_Alert_1_Delay"]) {
                section("Notification Alert Options (2):") {
                    input "${pName}_Alert_2_Delay", "enum", title: "Second Alert Delay (in minutes)", defaultValue: null, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true, image: getAppImg("alert_icon2.png")
                    if(settings?."${pName}_Alert_2_Delay") {
                        if(settings?."${pName}NotificationsOn" && (settings["${pName}UsePush"] || settings["${pName}NotifRecips"] || settings["${pName}NotifPhones"])) {
                            input "${pName}_Alert_2_Send_Push", "bool", title: "Send Push Notification?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("notification_icon.png")
                            if(settings["${pName}_Alert_2_Send_Push"]) {
                                input "${pName}_Alert_2_Send_Custom_Push", "bool", title: "Custom Push Message?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("notification_icon.png")
                                if(settings["${pName}_Alert_2_Send_Custom_Push"]) {
                                    input "${pName}_Alert_2_CustomPushMessage", "text", title: "Push Message to Send?", required: false, defaultValue: atomicState?."${pName}OffVoiceMsg" , submitOnChange: true, image: getAppImg("speech_icon.png")
                                }
                            }
                        }
                        if(settings?."${pName}AllowSpeechNotif") {
                            input "${pName}_Alert_2_Use_Speech", "bool", title: "Send Voice Notification?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("speech_icon.png")
                            if(settings["${pName}_Alert_2_Use_Speech"]) {
                                input "${pName}_Alert_2_Send_Custom_Speech", "bool", title: "Custom Speech Message?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("speech_icon.png")
                                if(settings["${pName}_Alert_2_Send_Custom_Speech"]) {
                                    input "${pName}_Alert_2_CustomSpeechMessage", "text", title: "Push Message to Send?", required: false, defaultValue: atomicState?."${pName}OffVoiceMsg" , submitOnChange: true, image: getAppImg("speech_icon.png")
                                }
                            }
                        }
                        if(settings?."${pName}AllowAlarmNotif") {
                            input "${pName}_Alert_2_Use_Alarm", "bool", title: "Use Alarm Device?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("alarm_icon.png")
                            if(settings?."${pName}_Alert_2_Use_Alarm" && settings?."${pName}AlarmDevices") {
                                input "${pName}_Alert_2_AlarmType", "enum", title: "Alarm Type to use?", metadata: [values:alarmActionsEnum()], defaultValue: "strobe", submitOnChange: true, required: false, image: getAppImg("alarm_icon.png")
                                iif(settings["${pName}_Alert_2_AlarmType"]) {
                                    input "${pName}_Alert_2_Alarm_Runtime", "enum", title: "Turn off Alarm After (in minutes)?", metadata: [values:shortTimeEnum()], defaultValue: 15, required: false, submitOnChange: true,
                                            image: getAppImg("delay_time_icon.png")
                                }
                            }
                        }
                    }

                    if(settings["${pName}_Alert_2_Send_Custom_Speech"] || settings["${pName}_Alert_2_Send_Custom_Push"]) {
                        if(pName in ["conWat", "extTmp"]) {
                            def str = ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • DeviceName: %devicename%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • Last Mode: %lastmode%" : ""
                            str += (pName == "conWat") ? "\n • Open Contact: %opencontact%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • Off Delay: %offdelay%" : ""
                            str += (pName in ["conWat", "extTmp"]) ? "\n • On Delay: %ondelay%" : ""
                            str += (pName == "extTmp") ? "\n • Temp Threshold: %tempthreshold%" : ""
                            paragraph "These Variables are accepted: ${str}", state: "complete", image: getAppImg("instruct_icon.png")
                        }
                    }
                }
            }
        }
    }
}

//process custom tokens to generate final voice message (Copied from BigTalker)
def voiceNotifString(phrase) {
    //log.trace "conWatVoiceNotifString..."
    try {
        if (phrase.toLowerCase().contains("%tstatname%")) { phrase = phrase.toLowerCase().replace('%tstatname%', (settings?."${getAutoType()}Tstat"?.displayName.toString() ?: "unknown")) }
        if (phrase.toLowerCase().contains("%lastmode%")) { phrase = phrase.toLowerCase().replace('%lastmode%', (atomicState?."${getAutoType()}RestoreMode".toString() ?: "unknown")) }
        if (getAutoType() == "conWat" && phrase.toLowerCase().contains("%opencontact%")) {
            phrase = phrase.toLowerCase().replace('%opencontact%', (getOpenContacts(conWatContacts) ? getOpenContacts(conWatContacts)?.join(", ").toString() : "a selected contact")) }
        if (getAutoType() == "extTmp" && phrase.toLowerCase().contains("%tempthreshold%")) {
            phrase = phrase.toLowerCase().replace('%tempthreshold%', "${extTmpDiffVal.toString()}(°${atomicState?.tempUnit})") }
        if (phrase.toLowerCase().contains("%offdelay%")) { phrase = phrase.toLowerCase().replace('%offdelay%', getEnumValue(longTimeSecEnum(), settings?."${getAutoType()}OffDelay").toString()) }
        if (phrase.toLowerCase().contains("%ondelay%")) { phrase = phrase.toLowerCase().replace('%ondelay%', getEnumValue(longTimeSecEnum(), settings?."${getAutoType()}OnDelay").toString()) }
    } catch (ex) {
        log.error "voiceNotifString Exception: ${ex}", ex
        parent?.sendExceptionData(ex.message, "voiceNotifString", true, getAutoType())
    }
    return phrase
}

def getNotifConfigDesc() {
    def pName = getAutoType()
    def str = ""
    if (settings?."${pName}NotificationsOn") {
        str += ( getRecipientDesc() || (settings?."${pName}AllowSpeechNotif" && (settings?."${pName}SpeechDevices" || settings?."${pName}SpeechMediaPlayer"))) ?
            "Push Status:" : ""
        str += (settings?."${pName}NotifRecips") ? "${str != "" ? "\n" : ""} • Contacts: (${settings?."${pName}NotifRecips"?.size()})" : ""
        str += (settings?."${pName}UsePush") ? "\n • Push Messages: Enabled" : ""
        str += (settings?."${pName}NotifPhones") ? "\n • SMS: (${settings?."${pName}NotifPhones"?.size()})" : ""
        //str += (pushStatus() && phone) ? "\n • SMS: (${phone?.size()})" : ""
        str += getNotifSchedDesc() ? ("${!getRecipientDesc() ? "" : "\n"}Schedule Options Selected...") : ""
        str += getVoiceNotifConfigDesc() ? ("${(str != "") ? "\n\n" : "\n"}Voice Status:${getVoiceNotifConfigDesc()}") : ""
        str += getAlarmNotifConfigDesc() ? ("${(str != "") ? "\n\n" : "\n"}Alarm Status:${getAlarmNotifConfigDesc()}") : ""
        str += getAlertNotifConfigDesc() ? "\n${getAlertNotifConfigDesc()}" : ""
    }
    return (str != "") ? "${str}" : null
}

def getVoiceNotifConfigDesc() {
    def pName = getAutoType()
    def str = ""
    if(settings?."${pName}NotificationsOn" && settings["${pName}AllowSpeechNotif"]) {
        def speaks = getInputToStringDesc(settings?."${pName}SpeechDevices", true)
        def medias = getInputToStringDesc(settings?."${pName}SpeechMediaPlayer", true)
        str += speaks ? "\n • Speech Devices:${speaks.size() > 1 ? "\n" : ""}${speaks}" : ""
        str += medias ? "\n • Media Players:${medias.size() > 1 ? "\n" : ""}${medias}" : ""
        str += (medias && settings?."${pName}SpeechVolumeLevel") ? "\n      Volume: (${settings?."${pName}SpeechVolumeLevel"})" : ""
        str += (medias && settings?."${pName}SpeechAllowResume") ? "\n      Resume: (${settings?."${pName}SpeechAllowResume".toString().capitalize()})" : ""
        str += (settings?."${pName}UseCustomSpeechNotifMsg" && (medias || speaks)) ? "\n • Custom Message: (${settings?."${pName}UseCustomSpeechNotifMsg".toString().capitalize()})" : ""
    }
    return (str != "") ? "${str}" : null
}

def getAlarmNotifConfigDesc() {
    def pName = getAutoType()
    def str = ""
    if(settings?."${pName}NotificationsOn" && settings["${pName}AllowAlarmNotif"]) {
        def alarms = getInputToStringDesc(settings["${pName}AlarmDevices"], true)
        str += alarms ? "\n • Alarm Devices:${alarms.size() > 1 ? "\n" : ""}${alarms}" : ""
    }
    return (str != "") ? "${str}" : null
}

def getAlertNotifConfigDesc() {
    def pName = getAutoType()
    def str = ""
    if(settings?."${pName}NotificationsOn" && (settings["${pName}_Alert_1_Delay"] || settings["${pName}_Alert_2_Delay"]) && (settings["${pName}AllowSpeechNotif"] || settings["${pName}AllowAlarmNotif"])) {
        str += settings["${pName}_Alert_1_Delay"] ? "\nAlert (1) Status:\n  • Delay: (${getEnumValue(longTimeSecEnum(), settings["${pName}_Alert_1_Delay"])})" : ""
        str += settings["${pName}_Alert_1_Send_Push"] ? "\n  • Send Push: (${settings["${pName}_Alert_1_Send_Push"]})" : ""
        str += settings["${pName}_Alert_1_Use_Speech"] ? "\n  • Use Speech: (${settings["${pName}_Alert_1_Use_Speech"]})" : ""
        str += settings["${pName}_Alert_1_Use_Alarm"] ? "\n  • Use Alarm: (${settings["${pName}_Alert_1_Use_Alarm"]})" : ""
        str += (settings["${pNmae}_Alert_1_Use_Alarm"] && settings["${pName}_Alert_1_AlarmType"]) ? "\n ├ Alarm Type: (${getEnumValue(alarmActionsEnum(), settings["${pName}_Alert_1_AlarmType"])})" : ""
        str += (settings["${pNmae}_Alert_1_Use_Alarm"] && settings["${pName}_Alert_1_Alarm_Runtime"]) ? "\n └ Alarm Runtime: (${getEnumValue(shortTimeEnum(), settings["${pName}_Alert_1_Alarm_Runtime"])})" : ""
        str += settings["${pName}_Alert_2_Delay"] ? "${settings["${pName}_Alert_1_Delay"] ? "\n" : ""}\nAlert (2) Status:\n  • Delay: (${getEnumValue(longTimeSecEnum(), settings["${pName}_Alert_2_Delay"])})" : ""
        str += settings["${pName}_Alert_2_Send_Push"] ? "\n  • Send Push: (${settings["${pName}_Alert_2_Send_Push"]})" : ""
        str += settings["${pName}_Alert_2_Use_Speech"] ? "\n  • Use Speech: (${settings["${pName}_Alert_2_Use_Speech"]})" : ""
        str += settings["${pName}_Alert_2_Use_Alarm"] ? "\n  • Use Alarm: (${settings["${pName}_Alert_2_Use_Alarm"]})" : ""
        str += (settings["${pNmae}_Alert_2_Use_Alarm"] && settings["${pName}_Alert_2_AlarmType"]) ? "\n ├ Alarm Type: (${getEnumValue(alarmActionsEnum(), settings["${pName}_Alert_2_AlarmType"])})" : ""
        str += (settings["${pNmae}_Alert_2_Use_Alarm"] && settings["${pName}_Alert_2_Alarm_Runtime"]) ? "\n └ Alarm Runtime: (${getEnumValue(shortTimeEnum(), settings["${pName}_Alert_2_Alarm_Runtime"])})" : ""
    }
    return (str != "") ? "${str}" : null
}

def getInputToStringDesc(inpt, addSpace = null) {
    def cnt = 0
    def str = ""
    if(inpt) {
        inpt.sort().each { item ->
            cnt = cnt+1
            str += item ? (((cnt < 1) || (inpt?.size() > 1)) ? "\n      ${item}" : "${addSpace ? "      " : ""}${item}") : ""
        }
    }
    //log.debug "str: $str"
    return (str != "") ? "${str}" : null
}

def getNotifSchedDesc() {
    def sun = getSunriseAndSunset()
    //def schedInverted = settings?."${getAutoType()}DmtInvert"
    def startInput = settings?."${getAutoType()}qStartInput"
    def startTime = settings?."${getAutoType()}qStartTime"
    def stopInput = settings?."${getAutoType()}qStopInput"
    def stopTime = settings?."${getAutoType()}qStopTime"
    def dayInput = settings?."${getAutoType()}quietDays"
    def modeInput = settings?."${getAutoType()}quietModes"
    def notifDesc = ""
    def getNotifTimeStartLbl = ( (startInput == "Sunrise" || startInput == "Sunset") ? ( (startInput == "Sunset") ? epochToTime(sun?.sunset.time) : epochToTime(sun?.sunrise.time) ) : (startTime ? time2Str(startTime) : "") )
    def getNotifTimeStopLbl = ( (stopInput == "Sunrise" || stopInput == "Sunset") ? ( (stopInput == "Sunset") ? epochToTime(sun?.sunset.time) : epochToTime(sun?.sunrise.time) ) : (stopTime ? time2Str(stopTime) : "") )
    notifDesc += (getNotifTimeStartLbl && getNotifTimeStopLbl) ? " • Silent Time: ${getNotifTimeStartLbl} - ${getNotifTimeStopLbl}" : ""
    def days = getInputToStringDesc(dayInput)
    def modes = getInputToStringDesc(modeInput)
    notifDesc += days ? "${(getNotifTimeStartLbl || getNotifTimeStopLbl) ? "\n" : ""} • Silent Day${isPluralString(dayInput)}: ${days}" : ""
    notifDesc += modes ? "${(getNotifTimeStartLbl || getNotifTimeStopLbl || days) ? "\n" : ""} • Silent Mode${isPluralString(modeInput)}: ${modes}" : ""
    return (notifDesc != "") ? "${notifDesc}" : null
}

def isPluralString(obj) {
    return (obj?.size() > 1) ? "(s)" : ""
}

def getNotificationOptionsConf() {
    def pName = getAutoType()
    return (getRecipientDesc() || (settings?."${pName}AllowSpeechNotif" && (settings?."${pName}SpeechDevices" || settings?."${pName}SpeechMediaPlayer")) ) ? true : false
}

def setRecipientsPage(params) {
    def pName = getAutoType()
    dynamicPage(name: "setRecipientsPage", title: "Set Push Notifications Recipients", uninstall: false) {
        def notifDesc = !location.contactBookEnabled ? "Enable push notifications below..." : "Select People or Devices to Receive Notifications..."
        section("${notifDesc}:") {
            if(!location.contactBookEnabled) {
                input "${pName}UsePush", "bool", title: "Send Push Notitifications", required: false, defaultValue: false, image: getAppImg("notification_icon.png")
            } else {
                input("${pName}NotifRecips", "contact", title: "Send notifications to", required: false, image: getAppImg("notification_icon.png")) {
                    input ("${pName}NotifPhones", "phone", title: "Phone Number to send SMS to...", description: "Phone Number", required: false)
                }
            }
        }
    }
}

def getRecipientsNames(val) {
    def n = ""
    def i = 0
    if(val) {
        val?.each { r ->
            i = i + 1
            n += "${(i == val?.size()) ? "${r}" : "${r},"}"
        }
    }
    return n?.toString().replaceAll("\\,", "\n")
}

def getRecipientDesc() {
    return ((settings?."${getAutoType()}NotifRecips") || (settings?."${getAutoType()}NotifPhones" || settings?."${getAutoType()}NotifUsePush")) ? "${getRecipientsNames(settings?."${getAutoType()}NotifRecips")}" : null
}

def setNotificationTimePage() {
    def pName = getAutoType()
    dynamicPage(name: "setNotificationTimePage", title: "Prevent Notifications\nDuring these Days, Times or Modes", uninstall: false) {
        def timeReq = (settings["${pName}qStartTime"] || settings["${pName}qStopTime"]) ? true : false
        section() {
            input "${pName}qStartInput", "enum", title: "Starting at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("start_time_icon.png")
            if(settings["${pName}qStartInput"] == "A specific time") {
                input "${pName}qStartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time_icon.png")
            }
            input "${pName}qStopInput", "enum", title: "Stopping at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("stop_time_icon.png")
            if(settings?."${pName}qStopInput" == "A specific time") {
                input "${pName}qStopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time_icon.png")
            }
            input "${pName}quietDays", "enum", title: "Only on these days of the week", multiple: true, required: false, image: getAppImg("day_calendar_icon.png"),
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            input "${pName}quietModes", "mode", title: "When these Modes are Active", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode_icon.png")
        }
    }
}

def setDayModeTimePage(params) {
    def pName = getAutoType()
    dynamicPage(name: "setDayModeTimePage", title: "Select Days, Times or Modes", uninstall: false) {
        def secDesc = settings["${pName}DmtInvert"] ? "Not" : "Only"
        def inverted = settings["${pName}DmtInvert"] ? true : false
        section("") {
            input "${pName}DmtInvert", "bool", title: "When Not in Any of These?...", defaultValue: false, submitOnChange: true, image: getAppImg("switch_icon.png")
        }
        section("${secDesc} During these Days, Times, or Modes:") {
            def timeReq = (settings?."${pName}StartTime" || settings."${pName}StopTime") ? true : false
            input "${pName}StartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time_icon.png")
            input "${pName}StopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time_icon.png")
            input "${pName}Days", "enum", title: "${inverted ? "Not": "Only"} on These Days of the week", multiple: true, required: false,
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], image: getAppImg("day_calendar_icon.png")
            input "${pName}Modes", "mode", title: "${inverted ? "Not": "Only"} in These Modes...", multiple: true, required: false, image: getAppImg("mode_icon.png")
        }
    }
}

def getDayModeTimeDesc(pName) {
    def startTime = settings?."${pName}StartTime"
    def stopInput = settings?."${pName}StopInput"
    def stopTime = settings?."${pName}StopTime"
    def dayInput = settings?."${pName}Days"
    def modeInput = settings?."${pName}Modes"
    def inverted = settings?."${pName}DmtInvert" ?: null
    def str = ""
    def days = getInputToStringDesc(dayInput)
    def modes = getInputToStringDesc(modeInput)
    str += ((startTime && stopTime) || modes || days) ? "${!inverted ? "When" : "When Not"}:" : ""
    str += (startTime && stopTime) ? "\n • Time: ${time2Str(settings?."${pName}StartTime")} - ${time2Str(settings?."${pName}StopTime")}"  : ""
    str += days ? "${(startTime || stopTime) ? "\n" : ""}\n • Day${isPluralString(dayInput)}: ${days}" : ""
    str += modes ? "${(startTime || stopTime || days) ? "\n" : ""}\n • Mode${isPluralString(modeInput)}: ${modes}" : ""
    str += (str != "") ? "\n\nTap to Modify..." : ""
    return str
}

def getDmtSectionDesc(autoType) {
    return settings["${autoType}DmtInvert"] ? "Do Not Act During these Days, Times, or Modes:" : "Only Act During these Days, Times, or Modes:"
}

/************************************************************************************************
|   				              AUTOMATION SCHEDULE CHECK 							        |
*************************************************************************************************/
def autoScheduleOk(autoType) {
    try {
        def inverted = settings?."${autoType}DmtInvert" ? true : false
        def modeOk = true
        modeOk = (!settings?."${autoType}Modes" || ((isInMode(settings?."${autoType}Modes") && !inverted) || (!isInMode(settings?."${autoType}Modes") && inverted))) ? true : false
        //dayOk
        def dayOk = true
        def dayFmt = new SimpleDateFormat("EEEE")
        dayFmt.setTimeZone(getTimeZone())
        def today = dayFmt.format(new Date())
        def inDay = (today in settings?."${autoType}Days") ? true : false
        dayOk = (!settings?."${autoType}Days" || ((inDay && !inverted) || (!inDay && inverted))) ? true : false

        //scheduleTimeOk
        def timeOk = true
        if (settings?."${autoType}StartTime" && settings?."${autoType}StopTime") {
            def inTime = (timeOfDayIsBetween(settings?."${autoType}StartTime", settings?."${autoType}StopTime", new Date(), getTimeZone())) ? true : false
            timeOk = ((inTime && !inverted) || (!inTime && inverted)) ? true : false
        }

        LogAction("autoScheduleOk( dayOk: $dayOk | modeOk: $modeOk | dayOk: ${dayOk} | timeOk: $timeOk | inverted: ${inverted})", "info", false)
        return (modeOk && dayOk && timeOk) ? true : false
    } catch (ex) {
        log.error "${autoType}-autoScheduleOk Exception: ${ex}", ex
        parent?.sendExceptionData(ex.message, "${autoType}-autoScheduleOk", true, getAutoType())
    }
}

/************************************************************************************************
|							GLOBAL Code | Logging AND Diagnostic							    |
*************************************************************************************************/

def sendEventPushNotifications(message, type) {
    if(allowNotif) {
        if(settings["${getAutoType()}_Alert_1_Send_Push"] || settings["${getAutoType()}_Alert_2_Send_Push"]) {
            if(settings["${getAutoType()}_Alert_1_CustomPushMessage"]) {
                sendNofificationMsg(settings["${getAutoType()}_Alert_1_CustomPushMessage"].toString(), type, settings?."${getAutoType()}NofifRecips", settings?."${getAutoType()}NotifPhones", settings?."${getAutoType()}UsePush")
            } else {
                sendNofificationMsg(message, type, settings?."${getAutoType()}NofifRecips", settings?."${getAutoType()}NotifPhones", settings?."${getAutoType()}UsePush")
            }
        } else {
            sendNofificationMsg(message, type, settings?."${getAutoType()}NofifRecips", settings?."${getAutoType()}NotifPhones", settings?."${getAutoType()}UsePush")
        }
    }
}

def sendEventVoiceNotifications(vMsg) {
    def allowNotif = settings?."${getAutoType()}NotificationsOn" ? true : false
    def allowSpeech = allowNotif && settings?."${getAutoType()}AllowSpeechNotif" ? true : false
    def speakOnRestore = allowSpeech && settings?."${getAutoType()}SpeechOnRestore" ? true : false
    if(allowNotif && allowSpeech) {
        sendTTS(vMsg)
    }
}

def scheduleAlarmOn() {
    log.debug "a1DelayVal: ${getAlert1DelayVal()}"
    def timeVal = getAlert1DelayVal().toInteger()
    log.debug "scheduleAlarmOn timeVal: $timeVal"
    if (timeVal > 0) {
        if(getAutoType() == "watchDog") {
            schedule(5, "alarm3FollowUp", [overwrite: true])
            LogAction("scheduleAlarmOn: Scheduling Alarm Off in 5 seconds...", "info", true)
        } else {
            schedule(timeVal, "alarm0FollowUp", [overwrite: true])
            LogAction("scheduleAlarmOn: Scheduling Alarm Followup 0...", "info", true)
        }
    }
}

def alarm0FollowUp() {
    log.debug "a1OffVal: ${getAlert1AlarmEvtOffVal()}"
    def timeVal = getAlert1AlarmEvtOffVal().toInteger()
    log.debug "alarm0FollowUp timeVal: $timeVal"
    if (timeVal > 0 && sendEventAlarmAction(1)) {
        schedule(timeVal, "alarm1FollowUp", [overwrite: true])
        LogAction("alarm0FollowUp: Scheduling Alarm Followup 1...", "info", true)
    }
}

def alarm1FollowUp() {
    def aDev = settings["${getAutoType()}AlarmDevices"]
    if (aDev) { aDev?.off() }
    LogAction("alarm1FollowUp: Turning OFF ${aDev}", "info", true)
    def timeVal = getAlert2DelayVal().toInteger()
    if (timeVal > 0) {
        schedule(timeVal, "alarm2FollowUp", [overwrite: true] )
        LogAction("alarm1FollowUp: Scheduling Alarm Followup 2...", "info", true)
    }
}

def alarm2FollowUp() {
    def timeVal = getAlert2AlarmEvtOffVal()
    if (timeVal > 0 && sendEventAlarmAction(2)) {
        schedule(timeVal, "alarm3FollowUp", [overwrite: true] )
        LogAction("alarm2FollowUp: Scheduling Alarm Followup 3...", "info", true)
    }
}

def alarm3FollowUp() {
    def aDev = settings["${getAutoType()}AlarmDevices"]
    if (aDev) { aDev?.off() }
    LogAction("alarm3FollowUp: Turning OFF ${aDev}", "info", true)
}

def alarmEvtSchedCleanup() {
    LogAction("Cleaning Up Alarm Event Schedules...", "info", true)
    def items = ["alarm0FollowUp","alarm1FollowUp", "alarm2FollowUp", "alarm3FollowUp"]
    items.each {
        unschedule("$it")
    }
    alarm3FollowUp()
}

def sendEventAlarmAction(evtNum) {
    try {
        def resval = false
        def allowNotif = settings?."${getAutoType()}NotificationsOn" ? true : false
        def allowAlarm = allowNotif && settings?."${getAutoType()}AllowAlarmNotif" ? true : false
        if(allowNotif && allowAlarm && settings["${getAutoType()}AlarmDevices"]) {
            if(settings["${getAutoType()}_Alert_${evtNum}_Use_Alarm"] && canSchedule()) {
                resval = true
                def alarmType = settings["${getAutoType()}_Alert_${evtNum}_AlarmType"].toString()
                def aDev = settings["${getAutoType()}AlarmDevices"]
                switch (alarmType) {
                    case "both":
                        atomicState?."alarmEvt${evtNum}StartDt" = getDtNow()
                        aDev?.both()
                        break
                    case "siren":
                        atomicState?."alarmEvt${evtNum}StartDt" = getDtNow()
                        aDev?.siren()
                        break
                    case "strobe":
                        atomicState?."alarmEvt${evtNum}StartDt" = getDtNow()
                        aDev?.strobe()
                        break
                    default:
                        resval = false
                        break
                }
            }
        }
    } catch (ex) {
        log.error "sendEventAlarmAction Exception: ($evtNum) - (${ex})", ex
        parent?.sendExceptionData(ex.message, "sendEventAlarmAction", true, getAutoType())
    }
   return resval
}

def alarmAlertEvt(evt) {
    log.trace "alarmAlertEvt: ${evt.displayName} Alarm State is Now (${evt.value})"
}

def getAlert1DelayVal() { return !settings["${getAutoType()}_Alert_1_Delay"] ? 300 : (settings["${getAutoType()}_Alert_1_Delay"].toInteger()) }
def getAlert2DelayVal() { return !settings["${getAutoType()}_Alert_2_Delay"] ? 300 : (settings["${getAutoType()}_Alert_2_Delay"].toInteger()) }

def getAlert1AlarmEvtOffVal() { return !settings["${getAutoType()}_Alert_1_Alarm_Runtime"] ? 15 : (settings["${getAutoType()}_Alert_1_Alarm_Runtime"].toInteger()) }
def getAlert2AlarmEvtOffVal() { return !settings["${getAutoType()}_Alert_2_Alarm_Runtime"] ? 15 : (settings["${getAutoType()}_Alert_2_Alarm_Runtime"].toInteger()) }

def getAlarmEvt1RuntimeDtSec() { return !atomicState?.alarmEvt1StartDt ? 100000 : GetTimeDiffSeconds(atomicState?.alarmEvt1StartDt).toInteger() }
def getAlarmEvt2RuntimeDtSec() { return !atomicState?.alarmEvt2StartDt ? 100000 : GetTimeDiffSeconds(atomicState?.alarmEvt2StartDt).toInteger() }

def scheduleTimeoutRestore() {
    def timeOutVal = settings["${getAutoType()}OffTimeout"]?.toInteger()
    if(timeOutVal && !atomicState?.timeOutScheduled) {
        runIn(timeOutVal.toInteger(), "restoreAfterTimeOut", [overwrite: true])
        LogAction("Mode Restoration Timeout Scheduled for (${getEnumValue(longTimeSecEnum(), settings?."${getAutoType()}OffTimeout")})", "info", true)
        atomicState?.timeOutScheduled = true
    }
}

def unschedTimeoutRestore() {
    def timeOutVal = settings["${getAutoType()}OffTimeout"]?.toInteger()
    if(timeOutVal && atomicState?.timeOutScheduled) {
        unschedule("restoreAfterTimeOut")
        LogAction("The Scheduled Mode Restoration Timeout has been cancelled because all Triggers are now clear...", "info", true)
    }
    atomicState?.timeOutScheduled = false
}

def restoreAfterTimeOut() {
    if(settings?."${getAutoType()}OffTimeout") {
        switch(pName) {
            case "conWat":
                atomicState?.timeOutScheduled = false
                conWatCheck(true)
                break
            case "leakWat":
                //leakWatCheck(true)
                break
            case "extTmp":
                atomicState?.timeOutScheduled = false
                extTmpTempCheck(true)
                break
        }
    }
}

def checkThermostatDupe(tstatOne, tstatTwo) {
    def result = false
    if(tstatOne && tstatTwo) {
        def pTstat = tstatOne?.deviceNetworkId.toString()
        def mTstatAr = []
        tstatTwo?.each { ts ->
            mTstatAr << ts?.deviceNetworkId.toString()
        }
        if (pTstat in mTstatAr) { return true }
    }
    return result
}

def checkModeDuplication(modeOne, modeTwo) {
    def result = false
    if(modeOne && modeTwo) {
         modeOne?.each { dm ->
            if(dm in modeTwo) {
                result = true
            }
        }
    }
    return result
}

private getDeviceSupportedCommands(dev) {
    return dev?.supportedCommands.findAll { it as String }
}

def checkFanSpeedSupport(dev) {
    def req = ["lowSpeed", "medSpeed", "highSpeed"]
    def devCnt = 0
    def devData = getDeviceSupportedCommands(dev)
    devData.each { cmd ->
        if(cmd.name in req) { devCnt = devCnt+1 }
    }
    def speed = dev?.currentValue("currentSpeed") ?: null
    return (speed && devCnt == 3) ? true : false
}

def getTstatCapabilities(tstat, autoType, dyn = false) {
    try {
        def canCool = true
        def canHeat = true
        def hasFan = true
        if(tstat?.currentCanCool) { canCool = tstat?.currentCanCool.toBoolean() }
        if(tstat?.currentCanHeat) { canHeat = tstat?.currentCanHeat.toBoolean() }
        if(tstat?.currentHasFan) { hasFan = tstat?.currentHasFan.toBoolean() }

        atomicState?."${autoType}${dyn ? "_${tstat?.deviceNetworkId}_" : ""}TstatCanCool" = canCool
        atomicState?."${autoType}${dyn ? "_${tstat?.deviceNetworkId}_" : ""}TstatCanHeat" = canHeat
        atomicState?."${autoType}${dyn ? "_${tstat?.deviceNetworkId}_" : ""}TstatHasFan" = hasFan
    } catch (ex) {
        log.error "getTstatCapabilities Exception: ${ex}", ex
        parent?.sendExceptionData("${tstat} - ${autoType} | ${ex}", "getTstatCapabilities", true, getAutoType())
    }
}

def getSafetyTemps(tstat) {
    def minTemp = tstat?.currentValue("safetyTempMin") ?: 0
    def maxTemp = tstat?.currentValue("safetyTempMax") ?: 0
    if(minTemp || maxTemp) {
        return ["min":minTemp, "max":maxTemp]
    }
    return null
}

def getComfortHumidity(tstat) {
    def maxHum = tstat?.currentValue("comfortHumidityMax") ?: 0
    if(maxHum) {
        //return ["min":minHumidity, "max":maxHumidity]
        return maxHum
    }
    return null
}

def getComfortDewpoint() {
    def maxDew = parent?.settings?.comfortDewpointMax ?: 0
    if(maxDew) {
        //return ["min":minHumidity, "max":maxHumidity]
        return maxDew.toDouble()
    }
    return null
}

def getSafetyTempsOk(tstat) {
    if(settings?."${getAutoType()}UseSafetyTemps") {
        def sTemps = getSafetyTemps(tstat)
        //log.debug "sTempsOk: $sTemps"
        if(sTemps) {
            def curTemp = tstat?.currentTemperature?.toDouble()
            //log.debug "curTemp: ${curTemp}"
            if( ((sTemps?.min.toDouble() != 0) && (curTemp < sTemps?.min.toDouble())) || ((sTemps?.max?.toDouble() != 0) && (curTemp > sTemps?.max?.toDouble())) ) {
                return false
            }
        } else { log.debug "getSafetyTempsOk: no safety Temps" }
    }
    return true
}

def getGlobalDesiredHeatTemp() {
    return parent?.settings?.locDesiredHeatTemp?.toDouble() ?: null
}

def getGlobalDesiredCoolTemp() {
    return parent?.settings?.locDesiredCoolTemp?.toDouble() ?: null
}

def getClosedContacts(contacts) {
    if(contacts) {
        def cnts = contacts?.findAll { it?.currentContact == "closed" }
        return cnts ?: null
    }
    return null
}

def getOpenContacts(contacts) {
    if(contacts) {
        def cnts = contacts?.findAll { it?.currentContact == "open" }
        return cnts ?: null
    }
    return null
}

def getDryWaterSensors(sensors) {
    if(sensors) {
        def cnts = sensors?.findAll { it?.currentWater == "dry" }
        return cnts ?: null
    }
    return null
}

def getWetWaterSensors(sensors) {
    if(sensors) {
        def cnts = sensors?.findAll { it?.currentWater == "wet" }
        return cnts ?: null
    }
    return null
}

def isContactOpen(con) {
    def res = false
    if(con) {
        if (con?.currentSwitch == "on") { res = true }
    }
    return res
}

def isSwitchOn(swit) {
    def res = false
    if(swit) {
        swit?.each { dev ->
            if (dev?.currentSwitch == "on") { res = true }
        }
    }
    return res
}

def isPresenceHome(presSensor) {
    def res = false
    if(presSensor) {
        presSensor?.each { d ->
            if (d?.currentPresence == "present") { res = true }
        }
    }
    return res
}

def getTstatPresence(tstat) {
    def pres = "not present"
    if (tstat) { pres = tstat?.currentPresence }
    return pres
}

def setTstatMode(tstat, mode) {
    def result = false
    try {
        if(mode) {
            if (mode == "auto") { tstat.auto(); result = true }
            else if (mode == "heat") { tstat.heat(); result = true }
            else if (mode == "cool") { tstat.cool(); result = true }
            else if (mode == "off") { tstat.off(); result = true }

            if(result) { LogAction("setTstatMode: '${tstat?.label}' Mode has been set to (${mode.toString().toUpperCase()})", "info", false) }
            else { LogAction("setTstatMode() | Invalid or Missing Mode received: ${mode}", "error", true) }
        } else {
            LogAction("setTstatMode() | Invalid or Missing Mode received: ${mode}", "warn", true)
        }
    }
    catch (ex) {
        log.error "setTstatMode() Exception | ${ex}", ex
        parent?.sendExceptionData(ex.message, "setTstatMode", true, getAutoType())
    }
    return result
}

def setMultipleTstatMode(tstats, mode) {
    def result = false
    try {
        if(tstats && md) {
            tstats?.each { ts ->
                if(setTstatMode(ts, mode)) {
                    LogAction("Setting ${ts} Mode to (${mode})", "info", true)
                    result = true
                } else {
                    return false
                }
            }
        }
    } catch (ex) {
        log.error "setMultipleTstatMode() Exception | ${ex}", ex
        parent?.sendExceptionData(ex.message, "setMultipleTstatMode", true, getAutoType())
    }
    return result
}

def setTstatAutoTemps(tstat, coolSetpoint, heatSetpoint) {
    LogAction("setTstatAutoTemps: tstat: ${tstat?.displayName}  coolSetpoint: ${coolSetpoint}   heatSetpoint: ${heatSetpoint}°${atomicState?.tempUnit} ", "info", true)
    def retVal = false
    if (tstat) {
        def hvacMode = tstat?.currentThermostatMode.toString()
        def curCoolSetpoint = getTstatSetpoint(tstat, "cool")
        def curHeatSetpoint = getTstatSetpoint(tstat, "heat")
        def diff = atomicState?.tempUnit == "C" ? 2.0 : 3.0

        def reqCool =  coolSetpoint?.toDouble() ?: null
        def reqHeat =  heatSetpoint?.toDouble() ?: null

        if (hvacMode in ["auto"]) {
            if (!reqCool && reqHeat) { reqCool = (double) (curCoolSetpoint > (reqHeat + diff)) ? curCoolSetpoint : (reqHeat + diff) }
            if (!reqHeat && reqCool) { reqHeat = (double) (curHeatSetpoint < (reqCool - diff)) ? curHeatSetpoint : (reqCool - diff) }
            if ((reqCool && reqHeat) && (reqCool >= (reqHeat + diff))) {
                def heatFirst
                if (reqHeat <= curHeatSetpoint) { heatFirst = true }
                    else if (reqCool >= curCoolSetpoint) { heatFirst = false }
                    else if (reqHeat > curHeatSetpoint) { heatFirst = false }
                    else { heatFirst = true }
                if (heatFirst) {
                    LogAction("setTstatAutoTemps() | Setting tstat: ${tstat?.displayName} mode: ${hvacMode} heatSetpoint: ${reqHeat}   coolSetpoint: ${reqCool}°${atomicState?.tempUnit} ", "info", true)
                    if (reqHeat != curHeatSetpoint) { tstat?.setHeatingSetpoint(reqHeat); retVal = true }
                    if (reqCool != curCoolSetpoint) { tstat?.setCoolingSetpoint(reqCool); retVal = true }
                } else {
                    LogAction("setTstatAutoTemps() | Setting tstat: ${tstat?.displayName} mode: ${hvacMode} coolSetpoint: ${reqCool}   heatSetpoint: ${reqHeat}°${atomicState?.tempUnit} ", "info", true)
                    if (reqCool != curCoolSetpoint) { tstat?.setCoolingSetpoint(reqCool); retVal = true }
                    if (reqHeat != curHeatSetpoint) { tstat?.setHeatingSetpoint(reqHeat); retVal = true }
                }
            } else { LogAction("setTstatAutoTemps() | Setting tstat: ${tstat?.displayName} mode: ${hvacMode} missing cool or heat set points ${reqCool} ${reqHeat} or not separated by ${diff}", "info", true) }

        } else if (hvacMode in ["cool"] && reqCool) {
            if (reqCool != curCoolSetpoint) { tstat?.setCoolingSetpoint(reqCool); retVal = true }

        } else if (hvacMode in ["heat"] && reqHeat) {
            if (reqHeat != curHeatSetpoint) { tstat?.setHeatingSetpoint(reqHeat); retVal = true }

        } else { LogAction("setTstatAutoTemps() | thermostat ${tstat?.displayName} mode is not AUTO COOl or HEAT", "info", true) }
    }
    return retVal
}


/******************************************************************************
*                			Keep These Methods				                  *
*******************************************************************************/
def switchEnumVals() { return [0:"Off", 1:"On", 2:"On/Off"] }

def longTimeMinEnum() {
    def vals = [
        1:"1 Minute", 2:"2 Minutes", 3:"3 Minutes", 4:"4 Minutes", 5:"5 Minutes", 10:"10 Minutes", 15:"15 Minutes", 20:"20 Minutes", 25:"25 Minutes", 30:"30 Minutes",
        45:"45 Minutes", 60:"1 Hour", 120:"2 Hours", 240:"4 Hours", 360:"6 Hours", 720:"12 Hours", 1440:"24 Hours"
    ]
    return vals
}

def longTimeSecEnum() {
    def vals = [
        60:"1 Minute", 120:"2 Minutes", 180:"3 Minutes", 240:"4 Minutes", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 2700:"45 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours", 10:"10 Seconds(Testing)"
    ]
    return vals
}

def shortTimeEnum() {
    def vals = [
        1:"1 Second", 2:"2 Seconds", 3:"3 Seconds", 4:"4 Seconds", 5:"5 Seconds", 6:"6 Seconds", 7:"7 Seconds",
        8:"8 Seconds", 9:"9 Seconds", 10:"10 Seconds", 15:"15 Seconds", 30:"30 Seconds", 60:"60 Seconds"
    ]
    return vals
}

def smallTempEnum() {
    def tempUnit = atomicState?.tempUnit
    def vals = [
        1:"1°${tempUnit}", 2:"2°${tempUnit}", 3:"3°${tempUnit}", 4:"4°${tempUnit}", 5:"5°${tempUnit}", 6:"6°${tempUnit}", 7:"7°${tempUnit}",
        8:"8°${tempUnit}", 9:"9°${tempUnit}", 10:"10°${tempUnit}"
    ]
    return vals
}

def switchRunEnum() {
    def vals = [
        1:"Heating/Cooling", 2:"With Fan Only"
    ]
    return vals
}

def fanModeTrigEnum() {
    def vals = ["auto":"Auto", "cool":"Cool", "heat":"Heat", "any":"Any Mode"]
    return vals
}

def tModeHvacEnum() {
    def vals = ["auto":"Auto", "cool":"Cool", "heat":"Heat"]
    return vals
}

def alarmActionsEnum() {
    def vals = ["siren":"Siren", "strobe":"Strobe", "both":"Both (Siren/Strobe)"]
    return vals
}

def getEnumValue(enumName, inputName) {
    def result = "unknown"
    if(enumName) {
        enumName?.each { item ->
            if(item?.key.toString() == inputName?.toString()) {
                result = item?.value
            }
        }
    }
    return result
}

def getSunTimeState() {
    def tz = TimeZone.getTimeZone(location.timeZone.ID)
    def sunsetTm = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSX", location?.currentValue('sunsetTime')).format('h:mm a', tz)
    def sunriseTm = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSX", location?.currentValue('sunriseTime')).format('h:mm a', tz)
    atomicState.sunsetTm = sunsetTm
    atomicState.sunriseTm = sunriseTm
}

void sendTTS(txt) {
    log.trace "sendTTS(data: ${txt})"
    try {
        def pName = getAutoType()
        def msg = txt.toString().replaceAll("\\[|\\]|\\(|\\)|\\'|\\_", "")
        def spks = settings?."${pName}SpeechDevices"
        def meds = settings?."${pName}SpeechMediaPlayer"
        def res = settings?."${pName}SpeechAllowResume"
        def vol = settings?."${pName}SpeechVolumeLevel"
        log.debug "msg: $msg | speaks: $spks | medias: $meds | resume: $res | volume: $vol"
        if (settings?."${pName}AllowSpeechNotif") {
            if(spks) {
                spks*.speak(msg)
            }
            if(meds) {
                meds?.each {
                    if(res) {
                        def currentStatus = it.latestValue('status')
                        def currentTrack = it.latestState("trackData")?.jsonValue
                        def currentVolume = it.latestState("level")?.integerValue ? it.currentState("level")?.integerValue : 0
                        if(vol) {
                            it?.playTextAndResume(msg, vol?.toInteger())
                        } else {
                            it?.playTextAndResume(msg)
                        }
                    }
                    else {
                        it?.playText(msg)
                    }
                }
            }
        }
    } catch (ex) {
        log.error "sendTTS Exception: (${ex})", ex
        parent?.sendExceptionData(ex.message, "sendTTS", true, getAutoType())
    }
}

///////////////////////////////////////////////////////////////////////////////
/******************************************************************************
*                Application Help and License Info Variables                  *
*******************************************************************************/
///////////////////////////////////////////////////////////////////////////////
private def appName() 		{ return "${parent ? "Nest Automations" : "Nest Manager"}${appDevName()}" }
private def appAuthor() 	{ return "Anthony S." }
private def appNamespace() 	{ return "tonesto7" }
private def gitBranch()     { return "master" }
private def betaMarker()    { return false }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
private def appInfoDesc() 	{
    def cur = atomicState?.appData?.updater?.versions?.app?.ver.toString()
    def beta = betaMarker() ? "" : ""
    def str = ""
    str += "${textAppName()}"
    str += isAppUpdateAvail() ? "\n• ${textVersion()} (Lastest: v${cur})${beta}" : "\n• ${textVersion()}${beta}"
    str += "\n• ${textModified()}"
    return str
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
private def textDesc()      { return "This SmartApp is used to integrate you're Nest devices with SmartThings as well as allow you to create child automations triggered by user selected actions..." }
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
