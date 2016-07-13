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

def appVersion() { "2.5.10" }
def appVerDate() { "7-12-2016" }
def appVerInfo() {
    def str = ""

    str += "V2.5.10 (July 12th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • FIXED: Bug in remote sensor fan logic."
    str += "\n • FIXED: Automation Name did not change After it was Disabled."

    str += "\n\nV2.5.9 (July 11th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Minor update to remote sensor fan logic and now there debug info is much easier to understand."
    str += "\n • UPDATED: Minor Tweaks to remote sensor temp logic debug logs so it's a little easier to understand."
    str += "\n • UPDATED: The Weather Device is now required in order to use local weather with external temperature automation."
    
    str += "\n\nV2.5.8 (July 8th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • FIXED: Added in app version info to exception data"
    str += "\n • CHANGED: Default token now reverted to the original v1 token that has been updated to support the Nest Cam when released. "

    str += "\n\nV2.5.7 (June 30th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • FIXED: Android Client Installs should now work correctly. Without any special hacks (Sorry for the issues)"
    str += "\n • UPDATED: Automation Naming was broken and should now work correctly."
    str += "\n • FIXED: Remote Sensor Eval Only modes was set as don't evaluate when in the selected modes."
    str += "\n • Lot's of little tweaks and cleanups."

    str += "\n\nV2.5.5 (June 30th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Android Client Installs should now work correctly. Without any special hacks"
    
    str += "\n\nV2.5.5 (June 26th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • WORKAROUND: Created solution to workaround the Android Client install bug."
    str += "\n • UPDATED: Added device version info to exception data that's sent to the developer."

    str += "\n\nV2.5.4 (June 24th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • FIXED: Fixed null bug preventing child update on new installs."
    str += "\n • FIXED: Automation Naming Bug."

    str += "\n\nV2.5.3 (June 23rd, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Modified the Thermostat setpoint automation to only show heat/cool temps if the device support them."
    str += "\n • ADDED: The Manager App will no longer update device data if it's version is not greater than a minimum version."

    str += "\n\nV2.5.2 (June 22nd, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • Minor Code cleanups."

    str += "\n\nV2.5.2 (June 21st, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • FIXED: Minor Code cleanups."
    str += "\n • FIXED: Phantom Info: null push notification has been fixed."
    str += "\n • FIXED: Lot's of modifications to the remote sensor logic to hopefully address the fan circulation issues."
    str += "\n • FIXED: Modified the change log to format properly in the Manager application."
    str += "\n • I Changed Remote Sensor Evaluation Wait input from Decimal to Enum input."

    str += "\n\nV2.5.0 (June 20th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • Version 2.5 Released to public."

    str += "\n\nV2.3.5 (June 17th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Fixed a few UI inconsistencies."
    str += "\n • FIXED: Remote Sensor heat cool adjustment operations."

    str += "\n\nV2.3.3 (June 16th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • FIXED: Remote Sensor was changing setpoints when not in a valid mode to evaluate."
    str += "\n • FIXED: Removed Thermostat Temp event from triggering Remote Sensor Rule evaluations."
    str += "\n • FIXED: Found commented out code in Nest Mode Automation that was preventing presence changes."
    str += "\n • Started working on bugfixes for Nest Mode Automation found by @scpickle."
    
    str += "\n\nV2.3.2 (June 15th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • FIXED: Fixed Motion Sensor Events in Remote Sensors."
    str += "\n • FIXED: Remote Sensor bugfixes found by @scpickle."
    
    str += "\n\nV2.3.0 (June 13th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • UPDATED: Various UI Tweaks."
    str += "\n • FIXED: Lot's of bug fixes.  I througly tested contact, external automations."
    str += "\n • FIXED: Voice Notifications now work correctly with contact automation."
    
    str += "\n\nV2.2.1 (June 9th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • ADDED: App now supports Broadcast message from developer."
    str += "\n • UPDATED: Tapping on the Nest Manager version app top of page will now take you a Changelog page which displays those changes."
    
    str += "\n\nV2.2.0 (June 8th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • NEW: When updates are available there is a link in the smartapp that takes you directly to the IDE in your mobile browser."
    str += "\n • NEW: Merged Manager and Automations into one codebase but it is still two apps... Thanks @ady264"
    str += "\n • NEW: Thermostat ST Mode TempSetpoint Automation to select your thermostats and each mode to use for that thermostat and then choose the heat/cool setpoints for each mode. This is completely dynamic and will allow different setpoints for each thermostat selected."
    str += "\n • NEW: You can now select devices to send Speech Notifications for Contact Automations."
    str += "\n • NEW: Remote Sensors now allows selection of switches to run along with the thermostat to help with comfort. This support includes automation detection of devices that support 3-speeds, and allows setting speed based on individual threshold temps."    
    str += "\n • ADDED: You can now use Day,Time,Mode filters in certain Nest Mode Automations."
    str += "\n • ADDED: Ability to Disable each automations individually."
    str += "\n • ADDED: When the Nest Weather device is installed you now have the option to receive push notifications for local weather alerts."
    str += "\n • UPDATED: There is new install setup now that flows much better and allows display of the important available options better to users."
    str += "\n • UPDATED: Child Device data updates have been modified to be much more efficient. All necessary data is sent at once eliminating the need for the devices to call back to the manager app constantly."
    str += "\n • NEW: Analytics have been added to the app to share installation data, it will also send generic exception error data. "
    str += "  So I can see trends among versions etc.  This data is completely transparent to you and can be disabled at any time.  I do not collect and identifiable data only the basics. We will also be putting up a dashboard for users who are nerdy just to see the data mapped out."
    str += "\n • FIXED: Remote Sensor's Fan circulation should now work like it was intended."
    str += "\n • FIXED: Nest Log Out function to actually take you back to auth screen after clearing token."
    str += "\n • ADDED: View all Apps/Devices state data under diagnostics."
    str += "\n • UPDATED: Lot's of tweaks and fixes for annoying UI bugs and to many subtle changes to list."

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
    page(name: "pollPrefPage")
    page(name: "debugPrefPage")
    page(name: "notifPrefPage")
    page(name: "diagPage")
    page(name: "appParamsDataPage")
    page(name: "devNamePage")
    page(name: "childAppDataPage")
    page(name: "childDevDataPage")
    page(name: "managAppDataPage")
    page(name: "devNameResetPage")
    page(name: "resetDiagQueuePage")
    page(name: "devPrefPage")
    page(name: "nestLoginPrefPage")
    page(name: "nestTokenResetPage")
    page(name: "uninstallPage")
    page(name: "custWeatherPage")
    page(name: "automationsPage")
    
    //Automation Pages
    page(name: "selectAutoPage" )
    page(name: "mainAutoPage")
    page(name: "nameAutoPage", install: true, uninstall: true)
    page(name: "remSensorPage")
    page(name: "remSenTstatFanSwitchPage")
    page(name: "remSenShowTempsPage")
    page(name: "extTempPage")
    page(name: "contactWatchPage")
    page(name: "fanVentPage" )
    page(name: "nestModePresPage")
    page(name: "tstatModePage")
    page(name: "tModeTstatConfModePage")
    page(name: "setRecipientsPage")
    page(name: "setDayModeTimePage")

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
    }
}

//This Page is used to load either parent or child app interface code
def startPage() {
    atomicState?.isParent = false
    if (parent) {
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
        return dynamicPage(name: "authPage", title: "Status Page", nextPage: "", install: false, uninstall:false) {
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
    //atomicState?.nestStructures = null

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
        return dynamicPage(name: "authPage", title: "Login Page", nextPage: "mainPage", install:false, uninstall: false) {
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
                        description:"Current: v${appVersion()} | New: ${atomicState?.appData?.versions?.app?.ver}\n\nTap to Open the IDE in your Mobile Browser...", state: "complete", image: getAppImg("update_icon.png")
            }
        }
        if(atomicState?.isInstalled) {
            section("Location & Devices:") {
                def devDesc = getDevicesDesc() ? "Current Devices: ${getDevicesDesc()}\n\nTap to Modify..." : "Tap to Configure..."
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
            if (structures) {
                atomicState.structures = structures ? structures : null
                def stats = getNestThermostats()
                def statDesc = stats.size() ? "Found (${stats.size()}) Thermostats..." : "No Thermostats"
                LogAction("Thermostats: Found ${stats?.size()} (${stats})", "info", false)

                def coSmokes = getNestProtects()
                def coDesc = coSmokes.size() ? "Found (${coSmokes.size()}) Protects..." : "No Protects"
                LogAction("Protects: Found ${coSmokes.size()} (${coSmokes})", "info", false)
                section("Select your Devices:") {
                    if (!stats?.size() && !coSmokes.size()) { paragraph "No Devices were found..." }
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
                }
            } 
        }
        if(atomicState?.isInstalled && atomicState?.structures && (atomicState?.thermostats || atomicState?.protects)) {
            def autoDesc = isAutoAppInst() ? "${getInstAutoTypesDesc()}\n\nTap to Modify..." : null
            section("Automations:") {
                href "automationsPage", title: "Automations...", description: (autoDesc ? autoDesc : "Tap to Configure..."), state: (autoDesc ? "complete" : null), image: getAppImg("automation_icon.png")
            }
        }
        if(atomicState?.isInstalled && atomicState?.structures && (atomicState?.thermostats || atomicState?.protects || atomicState?.weatherDevice)) {
            section("Diagnostics/Info:") {
                href "nestInfoPage", title: "View API & Diagnostic Info...", description: "Tap to view info...", image: getAppImg("api_diag_icon.png")
            }
        }
        if(atomicState?.isInstalled) {
            section("Preferences:") {
                def descStr = ""
                descStr += getAppNotifConfDesc() ?: ""
                descStr += getAppDebugDesc() ? "${getAppNotifConfDesc() ? "\n" : ""}${getAppDebugDesc() ?: ""}" : ""
                def prefDesc = (descStr != "") ? "${descStr}\n\nTap to Modify..." : "Tap to Configure..."
                href "prefsPage", title: "Preferences", description: prefDesc, state: ((pushStatus() || isAppDebug() || isChildDebug()) ? "complete" : null), image: getAppImg("settings_icon.png")
            }
            section(" ") {
                href "infoPage", title: "Help, Info and Instructions", description: "Tap to view...", image: getAppImg("info.png")
                href "uninstallPage", title: "Uninstall this App", description: "Tap to Remove...", image: getAppImg("uninstall_icon.png")
            }
        }
    }
}

def deviceSelectPage() {
    return dynamicPage(name: "deviceSelectPage", title: "Device Selection", nextPage: "mainPage", install: false, uninstall: false) {
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
                if (!stats?.size() && !coSmokes.size()) { paragraph "No Devices were found..." }
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
            }
        }
    }
}

def reviewSetupPage() {
    return dynamicPage(name: "reviewSetupPage", title: "Setup Review", install: true, uninstall: atomicState?.isInstalled) {
        if(!atomicState?.newSetupComplete) { atomicState.newSetupComplete = true }
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
            if(!atomicState?.isInstalled && (thermostats || protects || presDevice || weatherDevice)) {
                href "devNamePage", title: "Customize Device Names?", description: atomicState?.custLabelUsed ? "Tap to Modify..." : "Tap to configure...", state: (atomicState?.custLabelUsed ? "complete" : null), image: getAppImg("device_name_icon.png")
            }
            if(weatherDevice) {
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
            if (optInAppAnalytics != false) {
                input(name: "mobileClientType", title:"Mobile Client Type?", type: "enum", required: true, submitOnChange: true, metadata: [values:["android":"Android", "ios":"iOS", "winphone":"Windows Phone", "decline":"Decline"]],
                                image: getAppImg("${(mobileClientType && mobileClientType != "decline") ? "${mobileClientType}_icon" : "mobile_device_icon"}.png"))
                href url: getAppEndpointUrl("renderInstallData"), style:"embedded", title:"View the Data that will be Shared with the Developer", description: "Tap to view Data...", required:false, image: getAppImg("view_icon.png")
            }
        }
        section(" ") {
            href "infoPage", title: "Help, Info and Instructions", description: "Tap to view...", image: getAppImg("info.png")
        }
    }
}

//Defines the Preference Page
def prefsPage() {
    def devSelected = (atomicState?.structures && (atomicState?.thermostats || atomicState?.protects || atomicState?.presDevice || atomicState?.weatherDevice))
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
            input(name: "mobileClientType", title:"Mobile Client Type (Primary)?", type: "enum", required: false, submitOnChange: true, metadata: [values:["android":"Android", "ios":"iOS", "winphone":"Windows Phone", "decline":"Decline"]],
                                image: getAppImg("${(mobileClientType && mobileClientType != "decline") ? "${mobileClientType}_icon" : "mobile_device_icon"}.png"))
            input ("optInAppAnalytics", "bool", title: "Opt In App Analytics?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("app_analytics_icon.png"))
            input ("optInSendExceptions", "bool", title: "Opt In Send Errors?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("diag_icon.png"))
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

def devCustomizePageDesc() {
    def str = ""
    str += weathAlertNotif  ? "• Weather Alerts: Enabled" : ""
    return (str != "") ? "${str}" : null
}

def automationsPage() {
    return dynamicPage(name: "automationsPage", title: "", nextPage: "mainPage", install: false) {
        
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
            def rText = "NOTICE:\nAutomations is still in BETA!!!\nIt may contain bugs or unforseen issues. Features may change or be removed during development without notice.\n" +
                        "We are not responsible for any damages caused by using this SmartApp.\n\n               USE AT YOUR OWN RISK!!!"
            paragraph "${rText}"//, required: true, state: null
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
    if(!parent) { 
        uninstManagerApp()
    }
    sendNotificationEvent("${textAppName()} is uninstalled...")
}

def initialize() {
    //log.debug "initialize..."
    if(parent) { initAutoApp() }
    else { initManagerApp() }
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
    if(thermostats || protects || presDevice || weatherDevice) {
        atomicState?.isInstalled = true
    } else { atomicState.isInstalled = false }
    subscriber()
    setPollingState()
    //If analytics are enabled this will send non-user identifiable data to firebase server
    if (optInAppAnalytics) { runIn(4, "sendInstallData", [overwrite: true]) }
    runIn(20, "stateCleanup", [overwrite: true])
}

def uninstManagerApp() {
    try {
        if(addRemoveDevices(true)) {
            //removes analytic data from the server        
            if (optInAppAnalytics) { 
                removeInstallData() 
                atomicState?.installationId = null
            }
            //Revokes Smartthings endpoint token...
            revokeAccessToken()
            //Revokes Nest Auth Token
            if(atomicState?.authToken) { revokeNestToken() }
            //sends notification of uninstall
            sendNotificationEvent("${textAppName()} is uninstalled...")
        }
    } catch (ex) {
        LogAction("uninstManagerApp Exception: ${ex}", "error", true)
        sendExceptionData(ex, "uninstManagerApp")
    }
}

def getChildAppVer(appName) { return appName?.appVersion() ? "v${appName?.appVersion()}" : "" }

def appBtnDesc(val) {
    return atomicState?.automationsActive ? (atomicState?.automationsActiveDesc ? "${atomicState?.automationsActiveDesc}\nTap to Modify..." : "Tap to Modify...") :  "Tap to Install..."
}

def isAutoAppInst() {
    return (childApps.size() > 0) ? true : false
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
    def tModeCnt = 0
    def disCnt = 0
    childApps?.each { a ->
        def type = a?.getAutomationType()
        def disabled = !a?.getIsAutomationDisabled() ? null : disCnt+1
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
            case "tMode":
                tModeCnt = tModeCnt+1
                break
        }
    }
    def remSenDesc = (remSenCnt > 0) ? "\n• Remote Sensor ($remSenCnt)" : ""
    def conWatDesc = (conWatCnt > 0) ? "\n• Contact Sensor ($conWatCnt)" : ""
    def extTmpDesc = (extTmpCnt > 0) ? "\n• External Sensor ($extTmpCnt)" : ""
    def nModeDesc = (nModeCnt > 0) ? "\n• Nest Modes ($nModeCnt)" : ""
    def tModeDesc = (tModeCnt > 0) ? "\n• Tstat Modes ($tModeCnt)" : ""
    def disabDesc = (disCnt > 0) ? "\n• Disabled Automations ($nModeCnt)" : ""
    atomicState?.installedAutomations = ["remoteSensor":remSenCnt, "contact":conWatCnt, "externalTemp":extTmpCnt, "nestMode":nModeCnt, "tstatMode":tModeCnt]
    return "Installed Automations: ${disabDesc}${remSenDesc}${conWatDesc}${extTmpDesc}${nModeDesc}${tModeDesc}"
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
        atomicState?.lastWebUpdDt = null
        atomicState?.lastWeatherUpdDt = null
        atomicState?.lastForecastUpdDt = null
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


def getMinTstatVersion() {

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
                    atomicState.apiIssues = false
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
        atomicState.apiIssues = true
        atomicState.needChildUpd = true
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            if (ex.message.contains("Too Many Requests")) {
                log.warn "Received '${ex.message}' response code..."
            }
        } else {
            LogAction("getApiData (type: $type) Exception: ${ex}", "error", true)
            if(type == "str") { atomicState.needStrPoll = true }
            else if(type == "dev") { atomicState?.needDevPoll = true }
        }
        sendExceptionData(ex, "getApiData")
    }
    return result
}

def updateChildData() {
    LogAction("updateChildData()", "info", true)
    atomicState.needChildUpd = true
    runIn(40, "postCmd", [overwrite: true])
    try {
        atomicState?.lastChildUpdDt = getDtNow()
        def useMt = !useMilitaryTime ? false : true
        def dbg = !childDebug ? false : true
        def nestTz = getNestTimeZone().toString()
        def api = !apiIssues() ? false : true
        def devVerChkCnt = atomicState?.devVerChkCnt ?: 0
        getAllChildDevices().each {
            def devId = it.deviceNetworkId
            if(atomicState?.thermostats && atomicState?.deviceData?.thermostats[devId]) {
                atomicState?.tDevVer = !it.devVer() ? "" : it.devVer()
                if(!atomicState?.tDevVer || (versionStr2Int(atomicState?.tDevVer) >= minDevVersions()?.thermostat)) {
                    def tData = ["data":atomicState?.deviceData?.thermostats[devId], "mt":useMt, "debug":dbg, "tz":nestTz, "apiIssues":api, 
                                "pres":locationPresence(), "childWaitVal":getChildWaitVal().toInteger(), "cssUrl":getCssUrl(), "latestVer":latestTstatVer()?.ver?.toString()]
                    LogTrace("UpdateChildData >> Thermostat id: ${devId} | data: ${tData}")
                    it.generateEvent(tData) //parse received message from parent
                    //atomicState?.tDevVer = !it.devVer() ? "" : it.devVer()
                    return true
                } else { 
                    LogAction("The Manager App will not send data to the Thermostat device because the device version (${versionStr2Int(atomicState?.tDevVer)}) is lower than the Minimum (v${minDevVersions()?.thermostat})... Please Update Thermostat Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(atomicState?.protects && atomicState?.deviceData?.smoke_co_alarms[devId]) {
                atomicState?.pDevVer = !it.devVer() ? "" : it.devVer()
                if(!atomicState?.pDevVer || (versionStr2Int(atomicState?.pDevVer) >= minDevVersions()?.protect)) {
                    def pData = ["data":atomicState?.deviceData?.smoke_co_alarms[devId], "mt":useMt, "debug":dbg, "showProtActEvts":(!showProtActEvts ? false : true),
                                "tz":nestTz, "cssUrl":getCssUrl(), "apiIssues":api, "latestVer":latestProtVer()?.ver?.toString()]
                    LogTrace("UpdateChildData >> Protect id: ${devId} | data: ${pData}")
                    it.generateEvent(pData) //parse received message from parent
                    //atomicState?.pDevVer = !it.devVer() ? "" : it.devVer()
                    return true
                } else { 
                    LogAction("The Manager App will not send data to the Protect device because the device version (${versionStr2Int(atomicState?.pDevVer)}) is lower than the Minimum (v${minDevVersions()?.protect})... Please Update Protect Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(atomicState?.presDevice && devId == getNestPresId()) {
                atomicState?.presDevVer = !it.devVer() ? "" : it.devVer()
                if(!atomicState?.presDevVer || (versionStr2Int(atomicState?.presDevVer) >= minDevVersions()?.presence)) {
                    LogTrace("UpdateChildData >> Presence id: ${devId}")
                    def pData = ["debug":dbg, "tz":nestTz, "mt":useMt, "pres":locationPresence(), "apiIssues":api, "latestVer":latestPresVer()?.ver?.toString()]
                    it.generateEvent(pData)
                    //atomicState?.presDevVer = !it.devVer() ? "" : it.devVer()
                    return true
                } else { 
                    LogAction("The Manager App will not send data to the Presence device because the device version (${versionStr2Int(atomicState?.presDevVer)}) is lower than the Minimum (v${minDevVersions()?.presence})... Please Update Presence Device Handler Code to latest version to resolve this issue...", "error", true)
                    return false
                }
            }
            else if(atomicState?.weatherDevice && devId == getNestWeatherId()) {
                atomicState?.weatDevVer = !it.devVer() ? "" : it.devVer()
                if(!atomicState?.weatDevVer || (versionStr2Int(atomicState?.weatDevVer) >= minDevVersions()?.weather)) {
                    LogTrace("UpdateChildData >> Weather id: ${devId}")
                    def wData = ["weatCond":getWData(), "weatForecast":getWForecastData(), "weatAstronomy":getWAstronomyData(), "weatAlerts":getWAlertsData()]
                    it.generateEvent(["data":wData, "tz":nestTz, "mt":useMt, "debug":dbg, "apiIssues":api, "cssUrl":getCssUrl(), "weathAlertNotif":weathAlertNotif, "latestVer":latestWeathVer()?.ver?.toString()])
                    //atomicState?.weatDevVer = !it.devVer() ? "" : it.devVer()
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
            else if(!atomicState?.deviceData?.thermostats[devId] && !atomicState?.deviceData?.smoke_co_alarms[devId]) {
                LogAction("Device connection removed? no data for ${devId}", "warn", true)
                return null
            }
            else {
                LogAction("updateChildData() for ${devId} after polling", "error", true)
                return null
            }
        }
    }
    catch (ex) {
        LogAction("updateChildData Exception: ${ex}", "error", true)
        sendExceptionData(ex, "updateChildData")
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
    return ( ((getLastStructPollSec() + val) > pollStrTime || !atomicState?.structData) ? true : false )
}

def isPollAllowed() { return (atomicState?.pollingOn && (atomicState?.thermostats || atomicState?.protects || atomicState?.weatherDevice)) ? true : false }
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
        rootTypes:	[ struct:"structures", cos:"devices/smoke_co_alarms", tstat:"devices/thermostats", meta:"metadata" ],
        cmdObjs:	[ targetF:"target_temperature_f", targetC:"target_temperature_c", targetLowF:"target_temperature_low_f",
                      targetLowC:"target_temperature_low_c", targetHighF:"target_temperature_high_f", targetHighC:"target_temperature_high_c",
                      fanActive:"fan_timer_active", fanTimer:"fan_timer_timeout", hvacMode:"hvac_mode", away:"away" ],
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
            if(presDevice) { devIds?.push(getNestPresId()) }
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
        LogAction("sendEvtUpdateToDevice Exception: ${ex}", "errorS", true)
        sendExceptionData(ex, "sendEvtUpdateToDevice")
    }
}

def setStructureAway(child, value) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    def val = value?.toBoolean()
    LogAction("Nest Manager(setStructureAway) - Setting Nest Location:${!devId ? "" : " ${devId}"} (${val ? "Away" : "Home"})", "debug", true)
    if(childDebug && child) { child?.log("setStructureAway: ${devId} | (${val})") }
    try {
        if(val) {
            return sendNestApiCmd(atomicState?.structures, apiVar().rootTypes.struct, apiVar().cmdObjs.away, "away", devId)
        }
        else {
            return sendNestApiCmd(atomicState?.structures, apiVar().rootTypes.struct, apiVar().cmdObjs.away, "home", devId)
        }
    }
    catch (ex) {
        LogAction("setStructureAway Exception: ${ex}", "debug", true)
        sendExceptionData(ex, "setStructureAway")
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
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.fanActive, val, devId)
    }
    catch (ex) {
        LogAction("setFanMode Exception: ${ex}", "error", true)
        sendExceptionData(ex, "setFanMode")
        if(childDebug) { child?.log("setFanMode Exception: ${ex}", "error") }
        return false
    }
}

def setHvacMode(child, mode) {
    def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId.toString()
    LogAction("Nest Manager(setHvacMode) - Setting Thermostat${!devId ? "" : " ${devId}"} Mode to: (${mode})", "debug", true)
    try {
        return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.hvacMode, mode.toString(), devId)
    }
    catch (ex) {
        LogAction("setHvacMode Exception: ${ex}", "error", true)
        sendExceptionData(ex, "setHvacMode")
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
            return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetC, temp, devId)
        }
        else {
            return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetF, temp, devId)
        }
    }
    catch (ex) {
        LogAction("setTargetTemp Exception: ${ex}", "error", true)
        sendExceptionData(ex, "setTargetTemp")
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
            return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetLowC, temp, devId)
        }
        else {
            return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetLowF, temp, devId)
        }
    }
    catch (ex) {
        LogAction("setTargetTempLow Exception: ${ex}", "error", true)
        sendExceptionData(ex, "setTargetTempLow")
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
            return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetHighC, temp, devId)
        }
        else {
            return sendNestApiCmd(devId, apiVar().rootTypes.tstat, apiVar().cmdObjs.targetHighF, temp, devId)
        }
    }
    catch (ex) {
        LogAction("setTargetTempHigh Exception: ${ex}", "error", true)
        sendExceptionData(ex, "setTargetTempHigh")
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
        LogAction("sendNestApiCmd Exception: ${ex}", "error", true)
        sendExceptionData(ex, "sendNestApiCmd")
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
        LogAction("workQueue Exception Error: ${ex}", "error", true)
        sendExceptionData(ex, "workQueue")
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
            if (resp?.status == 307) {
                def newUrl = resp?.headers?.location?.split("\\?")
                LogTrace("NewUrl: ${newUrl[0]}")
                if ( procNestApiCmd(newUrl[0], typeId, type, obj, objVal, qnum, true) ) {
                    result = true
                }
            }
            else if( resp?.status == 200) {
                LogAction("procNestApiCmd Processed queue: ${qnum} ($type | ($obj:$objVal)) Successfully!!!", "info", true)
                atomicState?.apiIssues = false
                result = true
                //attempts to update device event immediately after successful command.
                increaseCmdCnt()
                atomicState?.lastCmdSentStatus = "ok" 
                //sendEvtUpdateToDevice(typeId, type, obj, objVal)
            }
            else if(resp.status == 400) {
                LogAction("procNestApiCmd 'Bad Request' Exception: ${resp?.status} ($type | $obj:$objVal)", "error", true)
            }
            else {
                LogAction("procNestApiCmd 'Unexpected' Response: ${resp?.status}", "warn", true)
            }
        }
    }
    catch (ex) {
        LogAction("procNestApiCmd Exception: ${ex} | ($type | $obj:$objVal)", "error", true)
        sendExceptionData(ex, "procNestApiCmd")
        atomicState.apiIssues = true
        atomicState?.lastCmdSentStatus = "failed"
    }
    return result
}

def increaseCmdCnt() {
    try {
        def cmdCnt = atomicState?.apiCommandCnt ?: 0
        cmdCnt = cmdCnt?.toInteger()+1
        log.debug "cmdCnt: $cmdCnt"
        if(cmdCnt) { atomicState?.apiCommandCnt = cmdCnt?.toInteger() }
    } catch (ex) {
        LogAction("increaseCmdCnt Exception: ${ex}", "error", true)
        sendExceptionData(ex, "increaseCmdCnt")
    }
}


/************************************************************************************************
|								Push Notification Functions										|
*************************************************************************************************/
def pushStatus() { return (recipients || phone || usePush) ? (usePush ? "Push Enabled" : "Enabled") : null }
def getLastMsgSec() { return !atomicState?.lastMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastMsgDt).toInteger() }
def getLastUpdMsgSec() { return !atomicState?.lastUpdMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastUpdMsgDt).toInteger() }
def getLastMisPollMsgSec() { return !atomicState?.lastMisPollMsgDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastMisPollMsgDt).toInteger() }
def getRecipientsSize() { return !settings.recipients ? 0 : settings?.recipients.size() }

def getOk2Notify() { return (daysOk(settings?."${getPagePrefix()}quietDays") && notificationTimeOk() && modesOk(settings?."${getPagePrefix()}quietModes")) }
def isMissedPoll() { return (getLastDevicePollSec() > atomicState?.misPollNotifyWaitVal.toInteger()) ? true : false }

def notificationCheck() {
    if((recipients || usePush) && getOk2Notify()) {
        if (sendMissedPollMsg) { missedPollNotify() }
        if (sendAppUpdateMsg && !appDevType()) { appUpdateNotify() }
    }
}

def missedPollNotify() {
    try {
        if(isMissedPoll()) {
            if(getOk2Notify() && (getLastMisPollMsgSec() > atomicState?.misPollNotifyMsgWaitVal.toInteger())) {
                sendMsg("Warning", "${app.name} has not refreshed data in the last (${getLastDevicePollSec()}) seconds.  Please try refreshing manually.")
                atomicState?.lastMisPollMsgDt = getDtNow()
            }
        }
    } catch (ex) { 
        LogAction("missedPollNotify Exception: ${ex}", "error", true)
        sendExceptionData(ex, "missedPollNotify")
    }
}

def appUpdateNotify() {
    try {
        def appUpd = isAppUpdateAvail()
        if(atomicState?.protects) { def pUpd = isProtUpdateAvail() }
        if(atomicState?.presDevice) { def prUpd = isPresUpdateAvail() }
        if(atomicState?.thermostats) { def tUpd = isTstatUpdateAvail() }
        if(atomicState?.weatherDevice) { def wUpd = isWeathUpdateAvail() }
        if((appUpd || pUpd || prUpd || tUpd || autoUpd || wUpd) && (getLastUpdMsgSec() > atomicState?.updNotifyWaitVal.toInteger())) {
            def appl = !appUpd ? "" : "Manager App: v${atomicState?.appData.versions.app.ver.toString()}, "
            def prot = !pUpd ? "" : "Protect: v${atomicState?.appData.versions.protect.ver.toString()}, "
            def pres = !prUpd ? "" : "Presence: v${atomicState?.appData.versions.presence.ver.toString()}, "
            def tstat = !tUpd ? "" : "Thermostat: v${atomicState?.appData.versions.thermostat.ver.toString()}"
            def weat = !wUpd ? "" : "Weather App: v${atomicState?.appData.versions.weather.ver.toString()}"
            def now = new Date()
            sendMsg("Info", "Update(s) are available: ${appl}${weat}${pres}${prot}${tstat}...  Please visit the IDE to Update your code...")
            atomicState?.lastUpdMsgDt = getDtNow()
        }
    } catch (ex) { 
        LogAction("appUpdateNotify Exception: ${ex}", "error", true)
        sendExceptionData(ex, "appUpdateNotify")
    }
}

def updateHandler() {
    //log.trace "updateHandler..."
    if(atomicState?.isInstalled) {
        if(atomicState?.appData?.updater?.updateType.toString() == "critical" && atomicState?.lastCritUpdateInfo.ver.toInteger() != atomicState?.appData?.updater?.updateVer.toInteger()) {
            sendMsg("Critical", "There are Critical Updates available for the Nest Manager Application!!! Please visit the IDE and make sure to update the App and Devices Code...")
            atomicState?.lastCritUpdateInfo = ["dt":getDtNow(), "ver":atomicState?.appData?.updater.updateVer.toInteger()]
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
            } else {
                sendPushMessage(newMsg)
                log.debug "Broadcast Message Sent: ${newMsg} - ${atomicState?.lastMsgDt}"
            }
        }
    } catch (ex) { 
        LogAction("sendMsg Exception: ${ex}", "error", true)
        sendExceptionData(ex, "sendMsg")
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
            if(canSchedule()) { runIn(10, "getWebFileData", [overwrite: true]) }  //This reads a JSON file from a web server with timing values and version numbers
        }
    }
    if (optInAppAnalytics && atomicState?.isInstalled) {
        if (getLastAnalyticUpdSec() > (3600*24)) {
            sendInstallData()
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
            LogAction("getWeatherConditions Exception: ${ex}", "error", true)
            sendExceptionData(ex, "getWeatherConditions")
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
            }
            LogTrace("getWebFileData Resp: ${resp?.data}")
            result = true
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
               log.warn  "appParams.json file not found..."
        } else {
            LogAction("getWebFileData Exception: ${ex}", "error", true)
        }
        sendExceptionData(ex, "getWebFileData")
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

def getCssUrl() {
    if(atomicState?.appData?.css?.cssUrl) {
        return atomicState?.appData?.css?.cssUrl
    } else {
        if(getWebFileData()) {
            return atomicState?.appData?.css?.cssUrl
        }
    }
}

def ver2IntArray(val) {
    def ver = val?.split("\\.")
    return [maj:"${ver[0]?.toInteger()}",min:"${ver[1]?.toInteger()}",rev:"${ver[2]?.toInteger()}"]
}
def versionStr2Int(str) { return str ? str.toString().replaceAll("\\.", "").toInteger() : null }

def getChildWaitVal() { return settings?.tempChgWaitVal ? settings?.tempChgWaitVal.toInteger() : 4 }

def isNewUpdateAvail(newVer, curVer) {
    try {
        def cVer = !curVer ? 100 : curVer?.toString().replaceAll("\\.", "").toInteger()
        def nVer = !newVer ? 100 : newVer?.toString().replaceAll("\\.", "").toInteger()
        if(cVer) {
            if (nVer > cVer) { return true }
        } else { return false }
    } catch (ex) { 
        LogAction("isNewUpdateAvail Exception: ${ex}", "error", true)
        sendExceptionData(ex, "isNewUpdateAvail")
    }
}

def isAppUpdateAvail() {
    if(isNewUpdateAvail(atomicState?.appData?.versions?.app?.ver, appVersion())) {
        return true
    } else { return false }
}

def isPresUpdateAvail() {
    if(isNewUpdateAvail(atomicState?.appData?.versions?.presence?.ver, atomicState?.presDevVer)) {
        return true
    } else { return false }
}

def isProtUpdateAvail() {
    if(isNewUpdateAvail(atomicState?.appData?.versions?.protect?.ver, atomicState?.pDevVer)) {
        return true
    } else { return false }
}

def isTstatUpdateAvail() {
    if(isNewUpdateAvail(atomicState?.appData?.versions?.thermostat?.ver, atomicState?.tDevVer)) {
        return true
    } else { return false }
}

def isWeathUpdateAvail() {
    if(isNewUpdateAvail(atomicState?.appData?.versions?.weather?.ver, atomicState?.weatAppVer)) {
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

                if (strucData?.structure_id.toString() == structures.toString()) {
                    thisstruct[dni] = strucData?.name.toString()
                } else {
                    if (atomicState?.structures) {
                        if (strucData?.structure_id?.toString() == atomicState?.structures?.toString()) {
                            thisstruct[dni] = strucData?.name.toString()
                        }
                    } else {
                        if (!structures) {
                            thisstruct[dni] = strucData?.name.toString()
                        }
                    }
                }
            }
            if (atomicState?.thermostats || atomicState?.protects || atomicState?.presDevice || atomicState?.weatherDevice || isAutoAppInst() ) {  // if devices are configured, you cannot change the structure until they are removed
                struct = thisstruct
            }
            if (ok2PollDevice()) { getApiData("dev") }
        } else { LogAction("Missing: atomicState.structData  ${atomicState?.structData}", "warn", true) }

    } catch (ex) { 
        LogAction("getNestStructures Exception: ${ex}", "error", true)
        sendExceptionData(ex, "getNestStructures")
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
            def presCnt = 0
            def weathCnt = 0
            if(atomicState?.presDevice) { presCnt = 1 }
            if(atomicState?.weatherDevice) { weathCnt = 1 }
            if(devsCrt > 0) {
                LogAction("Created Devices;  Current Devices: (${tstats?.size()}) Thermostat(s), (${nProtects?.size()}) Protect(s), ${presCnt} Presence Device and ${weathCnt} Weather Device", "debug", true)
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
            LogAction("addRemoveDevices Exception | $msg", "warn", true)
        }
        else { LogAction("addRemoveDevices Exception: ${ex}", "error", true) }
        sendExceptionData(ex, "addRemoveDevices")
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
                        else if (atomicState?.custLabelUsed) { dstr += "$str2" }
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
                    else if (atomicState?.custLabelUsed) { dstr += "$str2" }
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
                    else if (atomicState?.custLabelUsed) { dstr += "$str2" }
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
            LogAction("Device Handlers are missing: ${getThermostatChildName()}, ${getPresenceChildName()}, and ${getProtectChildName()}, Verify the Device Handlers are installed and Published via the IDE", "error", true)
        } else { 
            LogAction("deviceHandlerTest Exception: ${ex}", "error", true)
            sendExceptionData(ex, "deviceHandlerTest")
        }
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
        LogAction("deviceHandlerTest Exception: ${ex}", "error", true)
        sendExceptionData(ex, "removeTestDevs")
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
        LogAction("getEndpointUrl Exception: ${ex}", "error", true)
        sendExceptionData(ex, "getEndpointUrl")
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
        sendExceptionData(ex, "getAccessToken")
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
        LogAction("Callback Exception: ${ex}", "error", true)
        sendExceptionData(ex, "callback")
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
        LogAction("revokeNestToken Exception: ${ex}", "error", true)
        sendExceptionData(ex, "revokeNestToken")
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
    if (!appSettings.clientId) { return "63e9befa-dc62-4b73-aaf4-dcf3826dd704" }
    //if (!appSettings.clientId) { return "31aea46c-4048-4c2b-b6be-cac7fe305d4c" } //token with cam support
    else { return appSettings.clientId }
}

def clientSecret() {
    if (!appSettings.clientSecret) {return "8iqT8X46wa2UZnL0oe3TbyOa0" }
    //if (!appSettings.clientSecret) {return "FmO469GXfdSVjn7PhKnjGWZlm" } //token with cam support
    else { return appSettings.clientSecret }
}

/************************************************************************************************
|									LOGGING AND Diagnostic										|
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

def setStateVar(frc = false) {
    //log.trace "setStateVar..."
    try {
        //If the developer changes the version in the web appParams JSON it will trigger
        //the app to create any new state values that might not exist or reset those that do to prevent errors
        def stateVer = 3
        def stateVar = !atomicState?.stateVarVer ? 0 : atomicState?.stateVarVer.toInteger()
        if(!atomicState?.stateVarUpd || frc || (stateVer < atomicState?.appData.state.stateVarVer.toInteger())) {
            if(!atomicState?.newSetupComplete) 	        { atomicState.newSetupComplete = false }
            if(!atomicState?.misPollNotifyWaitVal) 	    { atomicState.misPollNotifyWaitVal = 900 }
            if(!atomicState?.misPollNotifyMsgWaitVal) 	{ atomicState.misPollNotifyMsgWaitVal = 3600 }
            if(!atomicState?.updNotifyWaitVal) 		    { atomicState.updNotifyWaitVal = 7200 }
            if(!atomicState?.custLabelUsed)             { atomicState?.custLabelUsed = false }
            if(!atomicState?.useAltNames)               { atomicState.useAltNames = false }
            if(!atomicState?.apiCommandCnt)             { atomicState?.apiCommandCnt = 0 }
            atomicState?.stateVarUpd = true
            atomicState?.stateVarVer = atomicState?.appData?.state?.stateVarVer ? atomicState?.appData?.state?.stateVarVer?.toInteger() : 0
        }
    } catch (ex) { 
        LogAction("setStateVar Exception: ${ex}", "error", true) 
        sendExceptionData(ex, "setStateVar")        
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
def getAutoAppChildName()    { return getChildName("Nest Automations") }

def getChildName(str)     { return "${str}${appDevName()}" }

def getServerUrl()          { return "https://graph.api.smartthings.com" }
def getShardUrl()           { return getApiServerUrl() }
def getCallbackUrl()		{ return "https://graph.api.smartthings.com/oauth/callback" }
def getBuildRedirectUrl()	{ return "${serverUrl}/oauth/initialize?appId=${app.id}&access_token=${atomicState?.accessToken}&apiServerUrl=${shardUrl}" }
def getNestApiUrl()			{ return "https://developer-api.nest.com" }
def getAppEndpointUrl(subPath) { return "${apiServerUrl("/api/smartapps/installations/${app.id}/${subPath}?access_token=${atomicState.accessToken}")}" }
def getHelpPageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html" }
def getAutoHelpPageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help/nest-automations.html" }
def getFirebaseAppUrl() 	{ return "https://st-nest-manager.firebaseio.com" }
def getAppImg(imgName, on = null) 	{ return (!disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/App/$imgName" : "" }

def latestTstatVer()    { return atomicState?.appData?.versions?.thermostat ?: "unknown" }
def latestProtVer()     { return atomicState?.appData?.versions?.protect ?: "unknown" }
def latestPresVer()     { return atomicState?.appData?.versions?.presence ?: "unknown" }
def latestAutoAppVer()  { return atomicState?.appData?.versions?.autoapp ?: "unknown" }
def latestWeathVer()    { return atomicState?.appData?.versions?.weather ?: "unknown" }
def getUse24Time()      { return useMilitaryTime ? true : false }

//Returns app State Info
def getStateSize()      { return state?.toString().length() }
def getStateSizePerc()  { return (int) ((stateSize/100000)*100).toDouble().round(0) }

private debugStatus() { return !appDebug ? "Off" : "On" }
private deviceDebugStatus() { return !childDebug ? "Off" : "On" }
private isAppDebug() { return !appDebug ? false : true }
private isChildDebug() { return !childDebug ? false : true }
def getPagePrefix() { return !parent ? "" : atomicState?.automationType }

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
        sendExceptionData(ex, "GetTimeDiffSeconds")
        return 10000
    }
}

def daysOk(days) {
    try {
        if(days) {
            def dayFmt = new SimpleDateFormat("EEEE")
            if(getTimeZone()) { dayFmt.setTimeZone(getTimeZone()) }
            return days.contains(dayFmt.format(new Date())) ? false : true
        } else { return true }
    } catch (ex) { 
        LogAction("daysOk() Exception: ${ex}", "error", true)
        sendExceptionData(ex, "daysOk")
    }
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
        LogAction("timeOk Exception: ${ex}", "error", true) 
        sendExceptionData(ex, "quietTimeOk")    
    }
}

def notificationTimeOk() {
    try {
        def pName = getPagePrefix()
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
        LogAction("notificationTimeOk Exception: ${ex}", "error", true) 
        sendExceptionData(ex, "notificationTimeOk")    
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
    return ["thermostat":250, "protect":250, "presence":250, "weather":250]
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
        section("Other Options:") {
            input "updChildOnNewOnly", "bool", title: "Only Update Devices on New Data?", description: "", required: false, defaultValue: true, submitOnChange: true
        }
        section("Wait Values:") {
            def pollWaitValDesc = !pollWaitVal ? "Default: 10 Seconds" : pollWaitVal
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
    pStr += "\n• Device: (${getInputEnumLabel(pollValue, pollValEnum())})"
    pStr += "\n• Structure: (${getInputEnumLabel(pollStrValue, pollValEnum())})"
    pStr += atomicState?.weatherDevice ? "\n• Weather Polling: (${getInputEnumLabel(pollWeatherValue, notifValEnum())})" : ""
    pStr += "\n• Update Child Data: ${(updChildOnNewOnly == null || updChildOnNewOnly == true) ? "(Only on New)" : "(Always)"}"
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
        
        if (recipients || phone || usePush) {
            if(recipients && !atomicState?.pushTested) {
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
    str += (pushStatus() && recipients) ? "\n • Contacts: (${recipients?.size()})" : ""
    str += (pushStatus() && usePush) ? "\n • Push Messages: Enabled" : ""
    str += (pushStatus() && sms) ? "\n • SMS: (${sms?.size()})" : ""
    str += (pushStatus() && phone) ? "\n • SMS: (${phone?.size()})" : ""
    str += (pushStatus() && getNotifSchedDesc()) ? "\n${getNotifSchedDesc()}" : ""
    return pushStatus() ? "${str}" : null
}

def devPrefPage() {
    dynamicPage(name: "devPrefPage", title: "Device Preferences", uninstall: false) {
        section("") {
            paragraph "Device Preferences", image: getAppImg("device_pref_icon.png")
        }
        if(thermostats || protects || presDevice || weatherDevice) {
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
                //paragraph "Nothing to see here yet!!!"
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
                href "custWeatherPage", title: "Customize Weather Location?", description: "Tap to configure...", image: getAppImg("weather_icon_grey.png")
                input ("weathAlertNotif", "bool", title: "Notify on Weather Alerts?", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("weather_icon.png"))
                //paragraph "Nothing to see here yet!!!"
            }
        }
    }
}

def getDevicesDesc() {
    def str = ""
    str += thermostats ? "\n• (${thermostats?.size()}) Thermostat${(thermostats?.size() > 1) ? "s" : ""}" : ""
    str += protects ? "\n• (${protects?.size()}) Protect${(protects?.size() > 1) ? "s" : ""}" : ""
    str += presDevice ? "\n• (1) Presence Device" : ""
    str += weatherDevice ? "\n• (1) Weather Device" : ""
    str += (!thermostats && !protects && !presDevice && !weatherDevice) ? "• No Devices Selected..." : ""
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
    str += isChildDebug() ? "${isAppDebug() ? "\n" : ""}• Device Debug: (${deviceDebugStatus()})" : ""
    return (str != "") ? "${str}" : null
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
            paragraph "This will uninstall the App, All Automation Apps and Child Devices.\n\nPlease make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove."
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
        section("About this page:") {
            paragraph "The info displayed below is the data received directly from the Nest API..."
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
        section("Recent Command") {
            def cmdDesc = ""
            cmdDesc += "Last Command Details:"
            cmdDesc += "\n • DateTime: (${atomicState.lastCmdSentDt ?: "Nothing found..."})"
            cmdDesc += "\n • Cmd Sent: (${atomicState.lastCmdSent ?: "Nothing found..."})"
            cmdDesc += "\n • Cmd Result: (${atomicState?.lastCmdSentStatus ?: "Nothing found..."})"

            cmdDesc += "\n\n • Totals Commands Sent: (${!atomicState?.apiCommandCnt ? 0 : atomicState?.apiCommandCnt})"
            paragraph "${cmdDesc}"
        }
        section("Diagnostics") {
            href "diagPage", title: "View Diagnostic Info...", description: null, state: (diagDesc ? "complete" : null), image: getAppImg("diag_icon.png")
        }
    }
}

def structInfoPage () {
    dynamicPage(name: "structInfoPage", refreshInterval: 30, install: false) {
        def noShow = [ "wheres", "thermostats", "smoke_co_alarms", "structure_id" ]
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

def diagPage () {
    dynamicPage(name: "diagPage", install: false) {
        section("") {
            paragraph "This page will allow you to view all diagnostic data related to the apps/devices in order to assist the developer in troubleshooting...", image: getAppImg("diag_icon.png")
        }
        section("State Size Info:") {
            paragraph "Current State Size: ${getStateSizePerc()}% (${getStateSize()})"
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
    dynamicPage(name: "childDevDataPage", refreshInterval:30, install: false) {
        getAllChildDevices().each { dev ->
            def str = ""
            section("${dev?.displayName.toString().capitalize()}:") {
                str += "   ───────STATE DATA──────"
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
        def pdVer = atomicState?.presDevVer ?: "Not Installed"
        def wdVer = atomicState?.weatDevVer ?: "Not Installed"
        def versions = ["apps":["manager":appVersion()?.toString()], "devices":["thermostat":tsVer, "protect":ptVer, "presence":pdVer, "weather":wdVer]]
        
        def tstatCnt = atomicState?.thermostats?.size() ?: 0
        def protCnt = atomicState?.protects?.size() ?: 0
        def automations = !atomicState?.installedAutomations ? "No Automations Installed" : atomicState?.installedAutomations
        def tz = getTimeZone()?.ID?.toString()
        def apiCmdCnt = !atomicState?.apiCommandCnt ? 0 : atomicState?.apiCommandCnt
        def cltType = !mobileClientType ? "Not Configured" : mobileClientType?.toString()
        def data = [
            "guid":atomicState?.installationId, "versions":versions, "thermostats":tstatCnt, "protects":protCnt, 
            "automations":automations, "timeZone":tz, "apiCmdCnt":apiCmdCnt, "stateUsage":"${getStateSizePerc()}%", "mobileClient":cltType, "datetime":getDtNow()?.toString() 
        ]
        def resultJson = new groovy.json.JsonOutput().toJson(data)
        return resultJson
                
    } catch (ex) { 
        LogAction("createInstallDataJson: Exception: ${ex}", "error", true) 
        sendExceptionData(ex, "createInstallDataJson")
    }
}

def renderInstallData() {
    try {
        def resultJson = createInstallDataJson()
        def resultString = new groovy.json.JsonOutput().prettyPrint(resultJson)
        render contentType: "application/json", data: resultString
    } catch (ex) { LogAction("renderInstallData Exception: ${ex}", "error", true) }
}

def renderInstallId() {
    try {
        def resultJson = new groovy.json.JsonOutput().toJson(atomicState?.installationId)
        render contentType: "application/json", data: resultJson
    } catch (ex) { LogAction("renderInstallId Exception: ${ex}", "error", true) }
}

def sendInstallData() {
    if (optInAppAnalytics) {
        sendAnalyticData(createInstallDataJson(), "installData/clients/${atomicState?.installationId}.json")
    }
}

def removeInstallData() {
    if (optInAppAnalytics) {
        removeAnalyticData("installData/clients/${atomicState?.installationId}.json")
    }
}

def sendExceptionData(exMsg, methodName) {
    if (optInSendExceptions) {
        def appType = !parent ? "managerApp" : "automationApp"
        def exData = ["methodName":methodName, "appVersion":(appVersion() ?: "Not Available"),"errorMsg":exMsg.toString(), "errorDt":getDtNow().toString()]
        def results = new groovy.json.JsonOutput().toJson(exData)
        sendAnalyticExceptionData(results, "errorData/${appType}/${methodName}.json")
    }
}

def sendChildExceptionData(devType, devVer, exMsg, methodName) {
    if (optInSendExceptions) {
        def exData = ["deviceType":devType, "devVersion":(devVer ?: "Not Available"), "methodName":methodName, "errorMsg":exMsg.toString(), "errorDt":getDtNow().toString()]
        def results = new groovy.json.JsonOutput().toJson(exData)
        sendAnalyticExceptionData(results, "errorData/${devType}/${methodName}.json")
    }
}

def sendAnalyticData(data, pathVal) {
    //log.trace "sendAnalyticData(${data}, ${pathVal}"
    def json = new groovy.json.JsonOutput().prettyPrint(data)
    def result = false
    def params = [ uri: "${getFirebaseAppUrl()}/${pathVal}", body: json.toString() ]
       try {
        httpPutJson(params) { resp ->
            //log.debug "resp: ${resp}"
            if( resp.status == 200) {
                LogAction("sendAnalyticData: Install Data Sent Successfully!!!", "info", true)
                atomicState?.lastAnalyticUpdDt = getDtNow()
                result = true
            }
            else if(resp.status == 400) {
                LogAction("sendAnalyticData: 'Bad Request' Exception: ${resp.status}", "error", true)
            }
            else {
                LogAction("sendAnalyticData: 'Unexpected' Response: ${resp.status}", "warn", true)
            }
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            LogAction("sendAnalyticData: 'HttpResponseException' Exception: ${ex}", "error", true)
        }
        else { LogAction("sendAnalyticData: Exception: ${ex}", "error", true) }
        sendExceptionData(ex, "sendAnalyticData")
    }
    return result
}

def sendAnalyticExceptionData(data, pathVal) {
    //log.trace "sendExceptionData(${data}, ${pathVal}"
    def json = new groovy.json.JsonOutput().prettyPrint(data)
    def result = false
    def params = [ uri: "${getFirebaseAppUrl()}/${pathVal}", body: json.toString() ]
       try {
        httpPostJson(params) { resp ->
            //log.debug "resp: ${resp}"
            if( resp.status == 200) {
                LogAction("sendExceptionData: Exception Data Sent Successfully!!!", "info", true)
                atomicState?.lastSentExceptionDataDt = getDtNow()
                result = true
            }
            else if(resp.status == 400) {
                LogAction("sendExceptionData: 'Bad Request' Exception: ${resp.status}", "error", true)
            }
            else {
                LogAction("sendExceptionData: 'Unexpected' Response: ${resp.status}", "warn", true)
            }
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            LogAction("sendExceptionData: 'HttpResponseException' Exception: ${ex}", "error", true)
        }
        else { LogAction("sendExceptionData: Exception: ${ex}", "error", true) }
    }
    return result
}

def removeAnalyticData(pathVal) {
    log.trace "removeAnalyticData(${pathVal}"
    def result = true
    try {
        httpDelete(uri: "${getFirebaseAppUrl()}/${pathVal}") { resp ->
            log.debug "resp status: ${resp?.status}"
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.ResponseParseException) {
            LogAction("removeAnalyticData: Response: ${ex.message}", "info", true)
        } else {
            LogAction("removeAnalyticData: Exception: ${ex}", "error", true)
            sendExceptionData(ex, "removeAnalyticData")
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
        }
    }
    else { return mainAutoPage( [autoType: atomicState?.automationType]) }
}

def mainAutoPage(params) {
    //log.trace "mainAutoPage()"
    if (!atomicState?.tempUnit) { atomicState?.tempUnit = getTemperatureScale()?.toString() }
    if (!atomicState?.disableAutomation) { atomicState.disableAutomation = false }
    def autoType = null
    //If params.autotype is not null then save to atomicState.  
    if (!params?.autoType) { autoType = atomicState?.automationType } 
    else { atomicState.automationType = params?.autoType; autoType = params?.autoType }

    // If the selected automation has not been configured take directly to the config page.  Else show main page
    if (autoType == "remSen" && !isRemSenConfigured()) { return remSensorPage() }
    else if (autoType == "extTmp" && !isExtTmpConfigured()) { return extTempPage() }
    else if (autoType == "conWat" && !isConWatConfigured()) { return contactWatchPage() }
    else if (autoType == "nMode" && !isNestModesConfigured()) { return nestModePresPage() }
    else if (autoType == "tMode" && !isTstatModesConfigured()) { return tstatModePage() }
    
    else { 
        // Main Page Entries
        def nxtPage = (atomicState?.automationType) ? "nameAutoPage" : ""
        return dynamicPage(name: "mainAutoPage", title: "Automation Config Page...", uninstall: true, install: false, nextPage: "nameAutoPage" ) {
            if(disableAutomation) {
                section() {
                    paragraph "This Automation is currently disabled!!!\nTurn it back on to resume operation...", image: getAppImg("instruct_icon.png")
                }
            }
            if(autoType == "remSen" && !disableAutomation) {
                section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
                    def remSenDescStr = ""
                    remSenDescStr += remSenRuleType ? "Rule-Type: ${getEnumValue(remSenRuleEnum(), remSenRuleType)}" : ""
                    remSenDescStr += (remSenEvalModes || remSenMotion || remSenSwitches) ? "\n\nRule Evaluation Triggers:" : ""
                    remSenDescStr += remSenTempDiffDegrees ? ("\n • Temp Threshold: (${remSenTempDiffDegrees}°${atomicState?.tempUnit})") : ""
                    remSenDescStr += remSenMotion ? ("\n • Motion Sensors: (${remSenMotion?.size()})${remSenMotionModes ? "\n└ Mode Filters: ${remSenMotionModes ? "(${remSenMotionModes.size()})" : "(0)"}" : ""}\n└ Status: ${isMotionActive(remSenMotion) ? "(Motion)" : "(No Motion)"}") : ""
                    remSenDescStr += remSenSwitches ? ("\n • Switches: (${remSenSwitches?.size()})\n└ Trigger Type: (${getEnumValue(switchEnumVals(), remSenSwitchOpt)})") : ""
                    remSenDescStr += remSenEvalModes ? "\n • Mode Filters: (${remSenEvalModes.size()})\n└ Status: ${isInMode(remSenEvalModes) ? "Eval Allowed" : "Eval Blocked"}" : ""
                    
                    remSenDescStr += remSenTstat ? "\n\nThermostat:" : ""
                    remSenDescStr += remSenTstat ? "\n• Name: (${remSenTstat.displayName})" : ""
                    remSenDescStr += remSenTstat ? "\n• Temp: (${getDeviceTemp(remSenTstat)}°${atomicState?.tempUnit})" : ""
                    remSenDescStr += remSenTstat ? "\n• Mode: (${remSenTstat?.currentThermostatOperatingState.toString().capitalize()}/${remSenTstat?.currentThermostatMode.toString().capitalize()})" : ""
                    remSenDescStr += (remSenTstat && atomicState?.remSenTstatHasFan) ? "\n• Fan Mode: (${remSenTstat?.currentThermostatFanMode.toString().capitalize()})" : ""
                    remSenDescStr += (remSensorDay && remSensorNight) ? "\n\nSensor Mode:" : ""
                    remSenDescStr += (remSensorDay && remSensorNight) ? "\n• Current Mode: (${getUseNightSensor() ? "☽ Night" : "☀ Day"})" : ""
                    remSenDescStr += (remSenUseSunAsMode && remSensorDay && remSensorNight) ? "\n• Day: ${atomicState?.sunriseTm}\n• Night: ${atomicState?.sunsetTm}" : ""
                    //remote sensor/Day
                    def dayModeDesc = ""
                        dayModeDesc += remSensorDay ? "\n\n${!remSensorNight ? "Remote" : "Day"} Sensor:" : ""
                        dayModeDesc += remSensorDay ? "\n• Temp${(remSensorDay?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit})" : ""
                        dayModeDesc += (remSensorDay && remSensorDayModes && remSensorNight && remSensorNightModes) ? "\n• Day Modes: (${remSensorDayModes.size()})" : ""
                        dayModeDesc += (remSensorDay && remSenDayHeatTemp && remSenDayCoolTemp) ? "\n• Desired Temps: (H: ${remSenDayHeatTemp}°${atomicState?.tempUnit}/C: ${remSenDayCoolTemp}°${atomicState?.tempUnit})" : ""
                    remSenDescStr += remSensorDay ? "${dayModeDesc}" : ""
                    //remote sensor Night
                    def nightModeDesc = ""
                        nightModeDesc += remSensorNight ? "\n\nNight Sensor:" : ""
                        nightModeDesc += remSensorNight ? ("\n• Temp${(remSensorNight?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorNight)}°${atomicState?.tempUnit})") : ""
                        nightModeDesc += (remSensorNight && remSensorNightModes) ? "\n• Night Modes: (${remSensorNightModes.size()})" : ""
                        nightModeDesc += (remSensorNight && remSenNightHeatTemp && remSenNightCoolTemp) ? "\n• Desired Temps: (H: ${remSenNightHeatTemp}°${atomicState?.tempUnit}/C: ${remSenNightCoolTemp}°${atomicState?.tempUnit})" : ""
                    remSenDescStr += remSensorNight ? "${nightModeDesc}" : ""
                    remSenDescStr += getRemSenTstatFanSwitchDesc() ? "\n\n${getRemSenTstatFanSwitchDesc()}" : ""
                    def remSenDesc = (isRemSenConfigured() ? "${remSenDescStr}\n\nTap to Modify..." : null)
                    href "remSensorPage", title: "Remote Sensors Config...", description: remSenDesc ? remSenDesc : "Tap to Configure...", state: (remSenDesc ? "complete" : null), image: getAppImg("remote_sensor_icon.png")
                }
            }

            if(autoType == "extTmp" && !disableAutomation) { 
                section("Turn Thermostat On/Off based on External Temp:") {
                    def extDesc = ""
                    extDesc += extTmpTstat ? "${extTmpTstat?.label}\n • Temp: (${getDeviceTemp(extTmpTstat)}°${atomicState?.tempUnit})" : ""
                    extDesc += extTmpTstat ? "\n • Mode: (${extTmpTstat?.currentThermostatOperatingState.toString()}/${extTmpTstat?.currentThermostatMode.toString()})" : ""
                    extDesc += ((extTmpUseWeather || extTmpTempSensor) && extTmpTstat) ? "\n\nTrigger Status:" : ""
                    extDesc += (!extTmpUseWeather && extTmpTempSensor && extTmpTstat) ? "\n • Using Sensor: (${getExtTmpTemperature()}°${atomicState?.tempUnit})" : ""
                    extDesc += (extTmpUseWeather && !extTmpTempSensor && extTmpTstat) ? "\n • Using Weather: (${getExtTmpTemperature()}°${atomicState?.tempUnit})" : ""
                    extDesc += extTmpDiffVal ? "\n • Temp Threshold: (${extTmpDiffVal}°${atomicState?.tempUnit})" : ""
                    extDesc += extTmpOffDelay ? "\n • Off Delay: (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})" : ""
                    extDesc += extTmpOnDelay ? "\n • On Delay: (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})" : ""
                    extDesc += extTmpRestoreOnTemp ? "\n • Last Mode: (${atomicState?.extTmpRestoreMode.toString().capitalize() ?: "Not Set"})" : ""
                    extDesc += extTmpRestoreAutoMode ? "\n • Restore to Auto: (True)" : ""
                    extDesc += (settings?."${getPagePrefix()}Modes" || settings?."${getPagePrefix()}Days" || (settings?."${getPagePrefix()}StartTime" && settings?."${getPagePrefix()}StopTime")) ? 
                            "\n • Evaluation Allowed: (${autoScheduleOk(getPagePrefix()) ? "ON" : "OFF"})" : ""
                    extDesc += ((extTmpTempSensor || extTmpUseWeather) && extTmpTstat) ? "\n\nTap to Modify..." : ""
                    def extTmpDesc = isExtTmpConfigured() ? "${extDesc}" : null
                    href "extTempPage", title: "External Temps Config...", description: extTmpDesc ?: "Tap to Configure...", state: (extTmpDesc ? "complete" : null), image: getAppImg("external_temp_icon.png")
                } 
            }

            if(autoType == "conWat" && !disableAutomation) { 
                section("Turn Thermostat On/Off when a Door or Window is Opened:") {
                    def conDesc = ""
                    conDesc += conWatTstat ? "${conWatTstat?.label}\n • Temp: (${getDeviceTemp(conWatTstat)}°${atomicState?.tempUnit})" : ""
                    conDesc += conWatTstat ? "\n • Mode: (${conWatTstat?.currentThermostatOperatingState.toString()}/${conWatTstat?.currentThermostatMode.toString()})" : ""
                    conDesc += (conWatContacts && conWatTstat && conWatContactDesc()) ? "\n\n${conWatContactDesc()}" : ""
                    conDesc += (conWatContacts && conWatTstat) ? "\n\nTrigger Status:" : ""
                    conDesc += conWatOffDelay ? "\n • Off Delay: (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})" : ""
                    conDesc += conWatOnDelay ? "\n • On Delay: (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})" : ""
                    conDesc += conWatRestoreOnClose ? "\n • Last Mode: (${atomicState?.conWatRestoreMode ? atomicState?.conWatRestoreMode.toString().capitalize() : "Not Set"})" : ""
                    conDesc += conWatRestoreAutoMode ? "\n • Restore to Auto: (True)" : ""
                    conDesc += (settings?."${getPagePrefix()}Modes" || settings?."${getPagePrefix()}Days" || (settings?."${getPagePrefix()}StartTime" && settings?."${getPagePrefix()}StopTime")) ? 
                            "\n • Evaluation Allowed: (${autoScheduleOk(getPagePrefix()) ? "ON" : "OFF"})" : ""
                    conDesc += (settings["${getPagePrefix()}AllowSpeechNotif"] && (settings["${getPagePrefix()}SpeechDevices"] || settings["${getPagePrefix()}SpeechMediaPlayer"]) && getVoiceNotifConfigDesc()) ? 
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
                    nDesc += (settings?."${getPagePrefix()}Modes" || settings?."${getPagePrefix()}Days" || (settings?."${getPagePrefix()}StartTime" && settings?."${getPagePrefix()}StopTime")) ? 
                            "\n • Evaluation Allowed: (${autoScheduleOk(getPagePrefix()) ? "ON" : "OFF"})" : ""
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

            if (atomicState?.isInstalled && (isRemSenConfigured() || isExtTmpConfigured() || isConWatConfigured() || isNestModesConfigured() || isTstatModesConfigured())) {
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
    dynamicPage(name: "nameAutoPage") {
        section("Automation name") {
            label title: "Name this Automation", defaultValue: "${getAutoTypeLabel()}", required: true
            paragraph "Make sure to name it something that will help you easily identify the app later."
        } 
    }
}

def initAutoApp() {
    unschedule()
    unsubscribe()
    subscribeToEvents()
    automationsInst()
    scheduler()
    atomicState?.timeZone = !location?.timeZone ? parent?.getNestTimeZone() : null
    if(extTmpUseWeather && atomicState?.isExtTmpConfigured) { 
        updateWeather() 
    }
    app.updateLabel(getAutoTypeLabel())
}

def getAutoTypeLabel() {
    def type = atomicState?.automationType
    def appLbl = app?.label?.toString()
    def newName = appName() == "Nest Manager" ? "Nest Automations" : "${appName()}"
    def typeLabel = ""
    def newLbl
    def dis = disableAutomation ? "\n(Disabled)" : ""
    if (type == "remSen")       { typeLabel = "${newName} (RemoteSensor)" }
    else if (type == "extTmp")  { typeLabel = "${newName} (ExternalTemp)" }
    else if (type == "conWat")  { typeLabel = "${newName} (Contact)" }
    else if (type == "nMode")   { typeLabel = "${newName} (NestMode)" }
    else if (type == "tMode")   { typeLabel = "${newName} (TstatMode)" }
    
    if(appLbl != typeLabel && appLbl != "Nest Manager" && !appLbl?.contains("(Disabled)")) {
        newLbl = appLbl
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
    atomicState.isExtTmpConfigured = isExtTmpConfigured() ? true : false
    atomicState.isConWatConfigured = isConWatConfigured() ? true : false
    atomicState.isNestModesConfigured = isNestModesConfigured() ? true : false
    atomicState.isTstatModesConfigured = isTstatModesConfigured() ? true : false
    atomicState?.isInstalled = true
}

def getAutomationType() {
    return atomicState?.automationType ? atomicState?.automationType : null
}

def getIsAutomationDisabled() {
    return disableAutomation ? true : false
}

def subscribeToEvents() {
    //Remote Sensor Subscriptions 
    def autoType = atomicState?.automationType
    if (autoType == "remSen") {
        if((remSensorDay || remSensorNight) && remSenTstat) {
            //subscribe(location, remSenLocationEvt)
            if(remSenEvalModes || remSensorDayModes || remSensorNightModes) { subscribe(location, "mode", remSenModeEvt, [filterEvents: false]) }
            if(remSensorDay) { subscribe(remSensorDay, "temperature", remSenTempSenEvt) }
            if(remSensorNight) { subscribe(remSensorNight, "temperature", remSenTempSenEvt) }
            if(remSenTstat) {
                subscribe(remSenTstat, "temperature", remSenTstatTempEvt)
                subscribe(remSenTstat, "thermostatMode", remSenTstatModeEvt)
                if(remSenTstatFanSwitches) {
                    subscribe(remSenTstatFanSwitches, "switch", remSenTstatFanSwitchEvt)
                    subscribe(remSenTstat, "thermostatFanMode", remSenTstatFanEvt)
                    if(remSenTstatFanSwitchTriggerType.toInteger() == 1) { subscribe(remSenTstat, "thermostatOperatingState", remSenTstatOperEvt) }
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
            remSenEvtEval()
        }
    }
    //External Temp Subscriptions
    if (autoType == "extTmp") {
        if(!extTmpUseWeather && extTmpTempSensor) { subscribe(extTmpTempSensor, "temperature", extTmpTempEvt, [filterEvents: false]) }
        if(extTmpTstat ) {
            subscribe(extTmpTstat, "thermostatMode", extTmpTstatModeEvt) 
        }
    }
    //Contact Watcher Subscriptions
    if (autoType == "conWat") {
        if(conWatContacts && conWatTstat) {
            subscribe(conWatContacts, "contact", conWatContactEvt)
            subscribe(conWatTstat, "thermostatMode", conWatTstatModeEvt)
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
        }
    }
}

def scheduler() {
    def wVal = getExtTmpWeatherUpdVal()
    //log.debug "wVal: ${wVal}"
    if(extTmpUseWeather && extTmpTstat) { 
        //schedule("0 15 * * * ?", "updateWeather")
        schedule("0 0/${wVal} * * * ?", "updateWeather")
    }
}

def updateWeather() {
    if(extTmpUseWeather) { 
        getExtConditions() 
        extTmpTempEvt(null)
    }
}

/******************************************************************************  
|                			REMOTE SENSOR AUTOMATION CODE	                  |
*******************************************************************************/
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
        
        section("Select the Allowed (Rule) Action Type:") {
            if(!remSenRuleType) { 
                paragraph "(Rule) Actions will be used to determine the actions the automation can take when the temperature threshold is reached. Using combinations of Heat/Cool/Fan to help balance" + 
                          " out the temperatures in your home in an attempt to make it more comfortable...", image: getAppImg("instruct_icon.png")
            }
            input(name: "remSenRuleType", type: "enum", title: "(Rule) Action Type", options: remSenRuleEnum(), required: true, submitOnChange: true, image: getAppImg("rule_icon.png"))
        }
        if(remSenRuleType) {
            section("Choose a Thermostat... ") {
                input "remSenTstat", "capability.thermostat", title: "Which Thermostat?", submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
                if(dupTstat) {
                    paragraph "Duplicate Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", image: getAppImg("error_icon.png")
                }
                if(remSenTstat) { 
                    getTstatCapabilities(remSenTstat, remSenPrefix())
                    paragraph "• Temp: (${tStatTemp})\n• Mode: (${tStatMode.toString().capitalize()})${(remSenTstat && atomicState?.remSenTstatHasFan) ? "\n• FanMode: (${remSenTstat?.currentThermostatFanMode.toString().capitalize()})" : ""}"+
                            "\n• Setpoints: (H: ${tStatHeatSp}°${atomicState?.tempUnit} | C: ${tStatCoolSp}°${atomicState?.tempUnit})",
                            state: "complete", image: getAppImg("instruct_icon.png")
                    input "remSenTstatsMir", "capability.thermostat", title: "Mirror Changes to these Thermostats", description: "", multiple: true, submitOnChange: true, required: false, 
                            image: getAppImg("thermostat_icon.png")
                    if(remSenTstatsMir && !dupTstat) { 
                        remSenTstatsMir?.each { t ->
                            paragraph "Thermostat Temp: ${getDeviceTemp(t)}${atomicState?.tempUnit}", image: " "
                        }
                    }
                }
            }
            section("Turn On a Fan/Switch While your Thermostat is Running:") {
                href "remSenTstatFanSwitchPage", title: "Control a Fan/Switch when Thermostat in Running?", description: getRemSenTstatFanSwitchDesc() ?: "", state: (getRemSenTstatFanSwitchDesc() ? "complete" : null), image: getAppImg("fan_ventilation_icon.png")
            }
            if(remSenTstat) {
                def dSenStr = !remSensorNight ? "Remote" : "Daytime"
                section("Choose $dSenStr Sensor(s) to use instead of the Thermostat's...") {
                    def dSenReq = (((remSensorNight && !remSensorDay) || !remSensorNight) && remSenTstat) ? true : false
                    input "remSensorDay", "capability.temperatureMeasurement", title: "${dSenStr} Temp Sensors", submitOnChange: true, required: dSenReq,
                            multiple: true, image: getAppImg("temperature_icon.png")
                    if(remSensorDay) {
                        def tempStr = !remSensorNight ? "" : "Day "
                        input "remSenDayHeatTemp", "decimal", title: "Desired ${tempStr}Heat Temp (°${atomicState?.tempUnit})", submitOnChange: true, required: remSenHeatTempsReq(), image: getAppImg("heat_icon.png")
                        input "remSenDayCoolTemp", "decimal", title: "Desired ${tempStr}Cool Temp (°${atomicState?.tempUnit})", submitOnChange: true, required: remSenCoolTempsReq(), image: getAppImg("cool_icon.png")
                        
                        def tmpVal = "$dSenStr Temp${(remSensorDay?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit})"
                        if(remSensorDay.size() > 1) {
                            href "remSenShowTempsPage", title: "View $dSenStr Sensor Temps...", description: "${tmpVal}", state: "complete", image: getAppImg("blank_icon.png")
                            //paragraph "Multiple temp sensors will return the average of those sensors.", image: getAppImg("i_icon.png")
                        } else { paragraph "${tmpVal}", state: "complete", image: getAppImg("instruct_icon.png") }
                    }
                }
                if(remSensorDay && ((!remSenHeatTempsReq() || !remSenCoolTempsReq()) || (remSenDayHeatTemp && remSenDayCoolTemp))) {
                    section("(Optional) Choose a second set of Temperature Sensor(s) to use in the Evening instead of the Thermostat's...") {
                        input "remSensorNight", "capability.temperatureMeasurement", title: "Evening Temp Sensors", description: "Tap to configure...", submitOnChange: true, required: false, multiple: true, image: getAppImg("temperature_icon.png")
                        if(remSensorNight) {
                            input "remSenNightHeatTemp", "decimal", title: "Desired Evening Heat Temp (°${atomicState?.tempUnit})", submitOnChange: true, required: ((remSensorNight && remSenHeatTempsReq()) ? true : false), image: getAppImg("heat_icon.png")
                            input "remSenNightCoolTemp", "decimal", title: "Desired Evening Cool Temp (°${atomicState?.tempUnit})", submitOnChange: true, required: ((remSensorNight && remSenCoolTempsReq()) ? true : false), image: getAppImg("cool_icon.png")
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
                        def inDesc = remSenUseSunAsMode ? "Day/Night Mode Triggers:\n• ☀ Day: ${atomicState?.sunriseTm}\n• ☽ Night: ${atomicState?.sunsetTm}" : null
                        input "remSenUseSunAsMode", "bool", title: "Use Sunrise/Sunset to Determine Day/Night Sensors?", description: inDesc ?: "", required: false, defaultValue: false, submitOnChange: true, state: inDesc ? "complete" : null,
                                image: getAppImg("sunrise_icon.png")
                        if(remSenUseSunAsMode) {
                            getSunTimeState()
                            //paragraph "Day/Night Mode Triggers:\n• ☀ Day: ${atomicState?.sunriseTm}\n• ☽ Night: ${atomicState?.sunsetTm}", state: "complete", image: getAppImg("blank_icon.png")
                        } 
                        if(!remSenUseSunAsMode) { 
                            if(!checkModeDuplication(remSensorDayModes, remSensorNightModes)) {
                                def modesReq = (!remSenUseSunAsMode && (remSensorDay && remSensorNight)) ? true : false
                                input "remSensorDayModes", "mode", title: "Daytime Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                                input "remSensorNightModes", "mode", title: "Evening Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                            } else {
                                paragraph "Duplicate Mode(s) found under the Day or Evening Sensor!!!.  Please Correct...", image: getAppImg("error_icon.png")
                            }
                        }
                    }
                }
                if(remSenTstat && (remSensorDay || remSensorNight)) {
                    if(remSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) {
                        section("Fan Settings:") {
                            paragraph "The default fan runtime is 15 minutes.\nThis can be adjusted under your nest account.", image: getAppImg("instruct_icon.png")
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
                    section ("Optional Settings:") {
                        paragraph "The Action Threshold Temp is the temperature difference used to trigger a selected action.", image: getAppImg("instruct_icon.png")
                        input "remSenTempDiffDegrees", "decimal", title: "Action Threshold Temp (°${atomicState?.tempUnit})", required: true, defaultValue: 1.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                        if(remSenRuleType != "Circ") {
                            paragraph "The Change Temp Increments are the amount the temp is adjusted +/- when an action requires a temp change.", image: getAppImg("instruct_icon.png")
                            input "remSenTempChgVal", "decimal", title: "Change Temp Increments (°${atomicState?.tempUnit})", required: true, defaultValue: 2.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                        }
                    }
                    section("Rule Evaluation Options:") {
                        input "remSenEvalModes", "mode", title: "Only Evaluate Actions in these Modes?", description: "", multiple: true, required: false, submitOnChange: true, image: getAppImg("mode_icon.png")
                        input "remSenWaitVal", "enum", title: "Wait Time between Evaluations?", required: false, defaultValue: 300, metadata: [values:longTimeSecEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                    }
                }
            }
        }
        section("Help:") {
            href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
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
        swDesc += (remSenTstatFanSwitchSpeedCtrl || remSenTstatFanSwitchTriggerType || remSenTstatFanSwitchHvacModeFilter) ? "Fan Switch Config:" : ""
    }
    swDesc += remSenTstatFanSwitches ? "${showOpt ? "\n" : ""}  • Fan Switches:" : ""
    def rmSwCnt = remSenTstatFanSwitches?.size() ?: 0
    remSenTstatFanSwitches?.each { sw ->
        swCnt = swCnt+1
        swDesc += "${swCnt >= 1 ? "${swCnt == rmSwCnt ? "\n └" : "\n ├"}" : "\n └"} ${sw?.label}: (${sw?.currentSwitch?.toString().capitalize()})${checkFanSpeedSupport(sw) ? "(3Spd)" : ""}"
    }
    if(showOpt) {
        swDesc += (remSenTstatFanSwitchSpeedCtrl || remSenTstatFanSwitchTriggerType || remSenTstatFanSwitchHvacModeFilter) ? "\n\nFan Triggers:" : ""
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
    //else if (remSenUseSunAsMode) { return}
    else {
        if(remSenMotionModes) {
            if(isInMode(remSenMotionModes) && remSenMotionDelayVal) {
                LogAction("remSenMotionEvt: Scheduling Motion Check for (${remSenMotionDelayVal} Seconds)", "info", true)
                runIn(remSenMotionDelayVal.toInteger(), "remSenCheckMotion", [overwrite: true])
            } else {
                LogAction("remSenMotionEvt: Skipping Motion Check because the current mode is not allowed", "info", true)
            }
        } 
        else {
            LogAction("remSenMotionEvt: Scheduling Motion Check for (${remSenMotionDelayVal} Seconds)", "info", true)
            runIn(remSenMotionDelayVal.toInteger(), "remSenCheckMotion", [overwrite: true])
        }
    }
}

def remSenTempSenEvt(evt) {
    log.trace "RemoteSensor Event | Sensor Temp: ${evt?.displayName} Temperature is (${evt?.value}°${atomicState?.tempUnit})"
    if(disableAutomation) { return }
    else { remSenEvtEval() }
}

def remSenTstatTempEvt(evt) {
    log.trace "RemoteSensor Event | Thermostat Temp: ${evt?.displayName} Temperature is (${evt?.value}°${atomicState?.tempUnit})"
    //if(disableAutomation) { return }
    //else { remSenEvtEval() }
}

def remSenTstatModeEvt(evt) {
    log.trace "RemoteSensor Event | Thermostat Mode: ${evt?.displayName} Mode is (${evt?.value.toString().toUpperCase()})"
    if(disableAutomation) { return }
    //else { remSenEvtEval() }
}

def remSenTstatFanSwitchEvt(evt) {
    log.trace "RemoteSensor Event | Thermostat Fan Switch: ${evt?.displayName} is (${evt?.value.toString().toUpperCase()})"
    if(disableAutomation) { return }
}

def remSenTstatFanEvt(evt) {
    log.trace "RemoteSensor Event | Thermostat Fan: ${evt?.displayName} Fan is (${evt?.value.toString().toUpperCase()})"
    if(disableAutomation) { return }
    else { 
        remSenTstatFanSwitchCheck(["fan":evt?.value])
    }
}

def remSenTstatOperEvt(evt) {
    log.trace "RemoteSensor Event | Thermostat OperatingState: ${evt?.displayName} OperatingState is  (${evt?.value.toString().toUpperCase()})"
    def isTstatIdle = (evt?.value == "idle") ? true : false
    if(disableAutomation) { return }
    else { 
        remSenTstatFanSwitchCheck()
    }
}

def remSenTstatFanSwitchCheck(evt) {
    LogAction("RemoteSensor Event | Fan Switch Trigger: ${evt?.displayName} is now (${evt?.value.toString().toUpperCase()})", "trace", false)
    try {
        def hvacFanOn = (evt.fan && evt.fan in ["fanOn", "fanCirculate"]) ? true : false
        def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null
        def remSenReqSetPoint = getRemSenReqSetpointTemp()
        def curSenTemp = (remSensorDay || remSensorNight) ? getRemoteSenTemp().toDouble() : null
        def tempDiff = Math.abs(remSenReqSetPoint - curSenTemp)
        if(disableAutomation) { return }
        else if(remSenTstatFanSwitchHvacModeFilter != "any" && (remSenTstatFanSwitchHvacModeFilter =! hvacMode)) {
            LogAction("remSenTstatFanSwitchCheck: Skipping Because Thermostat Mode does not Match the required Mode to Trigger Running of the Fan", "info", true)
            return
        }
        else {
            if(remSenTstatFanSwitches && (remSenTstatFanSwitchTriggerType.toInteger() == 1 || remSenTstatFanSwitchTriggerType.toInteger() == 2)) {
                if(hvacFanOn) {
                    if(atomicState?.remSenTstatFanSwitchSpeedEnabled && remSenTstatFanSwitchHighSpeed && remSenTstatFanSwitchMedSpeed && remSenTstatFanSwitchLowSpeed) {
                        remSenTstatFanSwitches.each { sw ->
                            def swOn = (sw?.currentSwitch.toString() == "on") ? true : false
                            if(!swOn) { 
                                if(checkFanSpeedSupport(sw)) {
                                    if(tempDiff < remSenTstatFanSwitchLowSpeed.toDouble()) {
                                        sw?.off()
                                        LogAction("remSenTstatFanSwitchCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is BELOW the Low Speed Threshold of ($remSenTstatFanSwitchLowSpeed) | Turning '${sw.label}' Fan Switch (OFF)", "info", true)
                                    }
                                    else if(tempDiff >= remSenTstatFanSwitchLowSpeed.toDouble() && tempDiff < remSenTstatFanSwitchMedSpeed.toDouble()) {
                                        sw?.lowSpeed()
                                        LogAction("remSenTstatFanSwitchCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is ABOVE the Low Speed Threshold of ($remSenTstatFanSwitchLowSpeed) | Turning '${sw.label}' Fan Switch on (LOW SPEED)", "info", true)
                                    }
                                    else if(tempDiff >= remSenTstatFanSwitchMedSpeed.toDouble() && tempDiff < remSenTstatFanSwitchHighSpeed.toDouble()) {
                                        sw?.medSpeed()
                                        LogAction("remSenTstatFanSwitchCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is ABOVE the Medium Speed Threshold of ($remSenTstatFanSwitchMedSpeed) | Turning '${sw.label}' Fan Switch on (MEDIUM SPEED)", "info", true)
                                    }
                                    else if(tempDiff >= remSenTstatFanSwitchHighSpeed.toDouble()) {
                                        sw?.highSpeed()
                                        LogAction("remSenTstatFanSwitchCheck: Temp Difference (${tempDiff}°${atomicState?.tempUnit}) is ABOVE the High Speed Threshold of ($remSenTstatFanSwitchHighSpeed) | Turning '${sw.label}' Fan Switch on (HIGH SPEED)", "info", true)
                                    }
                                } else {
                                    LogAction("remSenTstatFanSwitchCheck: Thermostat (${remSenTstat?.displayName}) Fan is (${swOn ? "ON" : "OFF"}) | Turning '${sw.label}' Switch (ON)", "info", true)
                                    sw?.on() 
                                }
                            }
                        }
                    }
                }
                else {
                    remSenTstatFanSwitches.each { sw ->
                        def swOn = (sw?.currentSwitch.toString() == "on") ? true : false
                        if(swOn) { 
                            LogAction("remSenTstatFanSwitchCheck: Thermostat (${remSenTstat?.displayName}) Fan is (${swOn ? "ON" : "OFF"}) | Turning '${sw?.label}' Switch (OFF)", "info", true)
                            sw?.off()
                        } 
                    }
                }
            }
        }
    } catch (ex) {
        LogAction("remSenTstatFanSwitchCheck Exception: (${ex})", "error", true)
        sendExceptionData(ex, "remSenTstatFanSwitchCheck")
    }
}

def remSenSunEvtHandler(evt) {
    if(disableAutomation) { return }
    else if(remSenUseSunAsMode) { 
        getSunTimeState() 
        remSenEvtEval() 
    } else { return }
}

def remSenSwitchEvt(evt) {
    LogAction("RemoteSensor Event | Evaluation Switch: ${evt?.displayName} is now (${evt?.value.toString().toUpperCase()})", "trace", true)
    def evtType = evt?.value?.toString()
    if(disableAutomation) { return }
    else if(remSenSwitches) {
        def swOpt = settings?.remSenSwitchOpt
        switch(swOpt.toInteger()) {
            case 0:
                if(evtType == "off") { remSenEvtEval() }
                break
            case 1:
                if (evtType == "on") { remSenEvtEval() }
                break
            case 2:
                if(evtType in ["on", "off"]) { remSenEvtEval() }
                break
            default:
                LogAction("remSenSwitchEvt: Invalid Option Received...", "warn", true)
            break
        }
    }
}

def remSenModeEvt(evt) {
    log.trace "RemoteSensor Event | ST Mode is (${evt?.value.toString().toUpperCase()})"
    //if(disableAutomation) { return }
    //else { remSenEvtEval() }
}

def coolingSetpointHandler(evt) { log.debug "coolingSetpointHandler()" }

def heatingSetpointHandler(evt) { log.debug "heatingSetpointHandler()" }

def isMotionActive(sensors) {
    return sensors?.currentState("motion")?.value.contains("active") ? true : false
}

def remSenCheckMotion() {
    if(isMotionActive(remSenMotion)) { remSenEvtEval() }
}

def getUseNightSensor() {
    def day = !remSensorDayModes ? false : isInMode(remSensorDayModes)
    def night = !remSensorNightModes ? false : isInMode(remSensorNightModes)
    if (remSenUseSunAsMode) { return getTimeAfterSunset() }
    else if (night && !day) { return true }
    else if (day && !night) { return false }
    else { return null }
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

def getRemSenReqSetpointTemp() {
    def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null
    def operState = remSenTstat ? remSenTstat?.currentThermostatOperatingState.toString() : null
    def temp = 0.0
    if((hvacMode == "cool") || (hvacMode == "auto" && operState == "cooling")) {
        temp = getRemSenCoolSetTemp().toDouble()
    }
    else if((hvacMode == "heat") || (hvacMode == "auto" && operState == "heating")) {
        temp = getRemSenHeatSetTemp().toDouble()
    }
    return temp
}

def getLastRemSenEvalSec() { return !atomicState?.lastRemSenEval ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenEval).toInteger() }
def getLastRemSenFanRunDtSec() { return !atomicState?.lastRemSenFanRunDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenFanRunDt).toInteger() }

// Initially based off of Keep Me Cozy II
private remSenEvtEval() {
    //LogAction("remSenEvtEval.....", "trace", true)
    if(disableAutomation) { return }
    if(remSenUseSunAsMode) { getSunTimeState() }
    def modeOk = (!remSenEvalModes || (remSenEvalModes && isInMode(remSenEvalModes))) ? true : false
    if(modeOk && (getLastRemSenEvalSec() < (remSenWaitVal?.toInteger() ?: 300))) {
        def schChkVal = ((remSenWaitVal - getLastRemSenEvalSec()) < 4) ? 4 : (remSenWaitVal - getLastRemSenEvalSec())
        runIn( schChkVal.toInteger(), "remSenEvtEval", [overwrite: true] )
        LogAction("Remote Sensor: Too Soon to Evaluate Actions...Scheduling Re-Evaluation in (${schChkVal} seconds)", "info", true)
    } 
    else { 
        atomicState?.lastRemSenEval = getDtNow()
        if(!modeOk || (!remSensorDay && !remSensorNight) || !remSenTstat || !getRemSenModeOk()) {
            def noGoDesc = ""
            noGoDesc += !modeOk ? "Ignoring Event the because Mode Filters were set and the current mode was not selected for Evaluation" : ""
            noGoDesc += !remSensorDay && !remSensorNight ? "Missing Required Day or Night Sensor Selections..." : ""
            noGoDesc += !remSenTstat ? "Missing Required Thermostat device" : ""
            noGoDesc += !getRemSenModeOk() ? "Ignoring because this mode is not one of those selected for evaluation..." : ""
            LogAction("Remote Sensor Evaluation Status: ${noGoDesc}", "warn", true)
        } else if (modeOk && getRemSenModeOk()) {
            //log.info "remSenEvtEval:  Evaluating Event..."
            def threshold = !remSenTempDiffDegrees ? 0 : remSenTempDiffDegrees.toDouble()
            def thresholdMax = threshold+5.0
            def tempChangeVal = !remSenTempChgVal ? 0 : remSenTempChgVal.toDouble()
            def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null
            def curTstatTemp = getDeviceTemp(remSenTstat).toDouble()
            def curTstatOperState = remSenTstat?.currentThermostatOperatingState.toString()
            def curTstatFanMode = remSenTstat?.currentThermostatFanMode.toString()
            def fanOn = (curTstatFanMode == "fanOn" || curTstatFanMode == "fanCirculate") ? true : false 
            def curCoolSetpoint = getTstatSetpoint(remSenTstat, "cool")
            def curHeatSetpoint = getTstatSetpoint(remSenTstat, "heat")
            def reqSenHeatSetPoint = getRemSenHeatSetTemp()
            def reqSenCoolSetPoint = getRemSenCoolSetTemp()
            def curSenTemp = (remSensorDay || remSensorNight) ? getRemoteSenTemp().toDouble() : null
            
            LogAction("remSenEvtEval: Remote Sensor Rule Type: ${getEnumValue(remSenRuleEnum(), remSenRuleType)}", "trace", false)
            LogAction("remSenEvtEval: Remote Sensor Temp: ${curSenTemp}", "trace", false)
            LogAction("remSenEvtEval: Thermostat Info - ( Temperature: (${curTstatTemp}) | HeatSetpoint: (${curHeatSetpoint}) | CoolSetpoint: (${curCoolSetpoint}) | HvacMode: (${hvacMode}) | OperatingState: (${curTstatOperState}) | FanMode: (${curTstatFanMode}) )", "trace", false) 
            LogAction("remSenEvtEval: Desired Temps - Heat: ${reqSenHeatSetPoint} | Cool: ${reqSenCoolSetPoint}", "trace", false)
            LogAction("remSenEvtEval: Threshold Temp: ${remSenTempDiffDegrees} | Change Temp Increments: ${remSenTempChgVal ?: "Not Set"}", "trace", false)
            
            if(hvacMode == "off") { 
                LogAction("Remote Sensor: Skipping Evaluation... The Current Thermostat Mode is 'OFF'...", "info", true)
                return 
            } 
            if (hvacMode in ["cool","auto"]) {
                //Changes Cool Setpoints
                if (remSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                    if(curSenTemp - reqSenCoolSetPoint > threshold) {
                        LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to (${(curTstatTemp - tempChangeVal)}°${atomicState?.tempUnit})", "info", true)
                        LogAction("Remote Sensor: COOL - (Sensor Temp: $curSenTemp - Sensor CoolSetpoint: $reqSenCoolSetPoint > Threshold: $threshold)=(${curSenTemp - reqSenCoolSetPoint > threshold})", "trace", false)
                        remSenTstat?.setCoolingSetpoint(curTstatTemp - tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMir*.setCoolingSetpoint(curTstatTemp - tempChangeVal) }
                    }
                    else if (reqSenCoolSetPoint - curSenTemp > threshold && curTstatTemp - curCoolSetpoint > threshold) {
                        LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to (${(curTstatTemp + tempChangeVal)}°${atomicState?.tempUnit})", "info", true)
                        LogAction("Remote Sensor: COOL - (Sensor CoolSetpoint: $reqSenCoolSetPoint - Current Sensor Temp: $curSenTemp > Threshold: $threshold)=(${reqSenCoolSetPoint - curSenTemp > threshold}) and (Tstat Temp: $curTstatTemp - Tstat CoolSetpoint: $curCoolSetpoint > Threshold: $threshold)=(${curTstatTemp - curCoolSetpoint > threshold})", "trace", false)
                        remSenTstat?.setCoolingSetpoint(curTstatTemp + tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMirror*.setCoolingSetpoint(curTstatTemp - tempChangeVal) }
                    }
                    else {
                        if(curCoolSetpoint != reqSenCoolSetPoint) {
                            LogAction("Remote Sensor: COOL - Adjusting CoolSetpoint to (${(reqSenCoolSetPoint)}°${atomicState?.tempUnit})", "info", true)
                            remSenTstat?.setCoolingSetpoint(reqSenCoolSetPoint)
                            if(remSenTstatsMirror) { remSenTstatsMirror*.setCoolingSetpoint(reqSenCoolSetPoint) }
                        }
                    }
                }
            }

            //Heat Functions....
            if (hvacMode in ["heat", "emergency heat", "auto"]) {
                if (remSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) { 
                    if (reqSenHeatSetPoint - curSenTemp > threshold) {
                        LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to (${(curTstatTemp + tempChangeVal)}°${atomicState?.tempUnit})", "debug", true)
                        LogAction("Remote Sensor: HEAT - (Sensor HeatSetpoint: $reqSenHeatSetPoint - Sensor Temp: $curSenTemp > Threshold: $threshold)=(${reqSenHeatSetPoint - curSenTemp > threshold})", "trace", false)
                        remSenTstat?.setHeatingSetpoint(curTstatTemp + tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMir*.setHeatingSetpoint(curTstatTemp + tempChangeVal) }
                    }
                    else if (curSenTemp - reqSenHeatSetPoint > threshold && curHeatSetpoint - curTstatTemp > threshold) {
                        LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to (${(curTstatTemp - tempChangeVal)}°${atomicState?.tempUnit})", "debug", true)
                        LogAction("Remote Sensor: HEAT - (Sensor Temp: $curSenTemp - Sensor HeatSetpoint: $reqSenHeatSetPoint > Threshold: $threshold)=(${curSenTemp - reqSenHeatSetPoint > threshold}) and (Tstat HeatSetpoint: $curHeatSetpoint - Tstat Temp: $curTstatTemp > Threshold: $threshold)-(${curHeatSetpoint - curTstatTemp > threshold})", "trace", false)
                        remSenTstat?.setHeatingSetpoint(curTstatTemp - tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMirror*.setHeatingSetpoint(curTstatTemp - tempChangeVal) }
                    }
                    else {
                        if(curHeatSetpoint != reqSenHeatSetPoint) {
                            LogAction("Remote Sensor: HEAT - Adjusting HeatSetpoint to (${(reqSenHeatSetPoint)}°${atomicState?.tempUnit})", "debug", true)
                            remSenTstat?.setHeatingSetpoint(reqSenHeatSetPoint)
                            if(remSenTstatsMirror) { remSenTstatsMirror*.setHeatingSetpoint(reqSenHeatSetPoint) }
                        }
                    }
                }
            }
            // Determines Heat/Cool Fan Temps
            if(remSenRuleType in ["Circ", "Cool_Circ", "Heat_Circ", "Heat_Cool_Circ"]) {
                def sTemp = getFanAutoModeTemp(hvacMode, curTstatOperState, reqSenHeatSetPoint, reqSenCoolSetPoint, curHeatSetpoint, curCoolSetpoint, curSenTemp)
                remSenFanControl(remSenTstat, remSenTstatsMir, hvacMode, remSenRuleType, curTstatOperState, curTstatFanMode, sTemp?.type?.toString(), curSenTemp, sTemp?.req?.toDouble(), sTemp?.cur?.toDouble(), threshold, fanOn)
            } 
        }
        else {
            LogAction("Remote Sensor: Skipping Evaluation... Location or Sensor Mode is not valid for Rule Evaluation...", "info", true)
        }
    }
}

def getFanAutoModeTemp(hvacMode, operState, reqHeatSetTemp, reqCoolSetTemp, curHeatSetTemp, curCoolSetTemp, curSenTemp, operType = null) {
    def coolDiff = (curSenTemp - reqCoolSetTemp)
    def heatDiff = (curSenTemp - reqHeatSetTemp)
    def opType = !operType ? ((hvacMode == "auto") ? (coolDiff < heatDiff ? "cool" : "heat") : hvacMode.toString()) : operType
    def reqTemp = (opType == "cool") ? reqCoolSetTemp : reqHeatSetTemp
    def sTemp = (opType == "cool") ? curCoolSetTemp : curHeatSetTemp
    //log.debug "reqTemp: ($reqTemp) | sTemp: ($sTemp) | hvacMode: ${hvacMode} | opType: $opType | operState: ${curTstatOperState}"
    return ["req":reqTemp, "cur":sTemp, "type":opType]
}

def remSenFanControl(tstat, tstatsMir, curHvacMode, ruleType, curOperState, curFanMode, operType, Double curSenTemp, Double reqSetpointTemp, Double curSetpointTemp, Double threshold, Boolean fanOn) {
    //CONTROLS THE FAN
    if(tstat) {
        def fanRunOk = getRemSenFanRunOk(curOperState, curFanMode)
        def fanTempOk = getRemSenFanTempOk(curHvacMode, curSenTemp, reqSetpointTemp, curSetpointTemp, threshold, fanOn, operType)
        if(fanRunOk && fanTempOk) { 
            LogAction("Remote Sensor: Activating '${tstat?.displayName}'' Fan for ${operType.toString().toUpperCase()}ING Circulation...", "debug", true)
            tstat?.fanOn()
            if(tstatsMir) { 
                tstatsMir?.each { mt -> 
                    LogAction("Remote Sensor: Mirroring Primary Thermostat: Activating '${mt?.displayName}' Fan for ${operType.toString().toUpperCase()}ING Circulation", "debug", true)
                    mt?.fanOn() 
                }
            }
            atomicState?.lastRemSenFanRunDt = getDtNow()
            atomicState?.remSenFanIsRunning = true
        }
        else {
            if (!fanTempOk && (fanOn || atomicState?.remSenFanIsRunning)) {
                LogAction("Remote Sensor: Turning OFF '${remSenTstat?.displayName}' Fan that was used for ${operType.toString().toUpperCase()}ING Circulation", "info", true)
                tstat?.fanAuto()
                if(tstatsMir) { 
                    tstatsMir?.each { mt -> 
                        LogAction("Remote Sensor: Mirroring Primary Thermostat: Turning OFF '${mt?.displayName}' Fan that was used for ${operType.toString().toUpperCase()}ING Circulation", "info", true)
                        mt?.fanAuto() 
                    }
                }
                atomicState?.remSenFanIsRunning = false
            }
        }
    }
}

def getRemSenFanRunOk(operState, fanState) { 
    log.trace "getRemSenFanRunOk($operState, $fanState)"
    def fanAlreadyOn = (fanState in ["fanOn", "fanCirculate"]) ? true : false 
    def waitTimeVal = remSenTimeBetweenRuns?.toInteger() ?: 3600
    def ruleTypeOk = (remSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) ? true : false
    def tstatOperStateOk = (operState == "idle") ? true : false
    def fanModeOk = (fanState == "fanAuto") ? true : false
    def timeSinceLastRunOk = (getLastRemSenFanRunDtSec() > waitTimeVal) ? true : false
     
    if (!tstatOperStateOk) {
        LogAction("Remote Sensor Fan Run: The Thermostat OperatingState is Currently (${operState?.toString().toUpperCase()})... Skipping!!!", "info", true)
    }
    if (!ruleTypeOk) {
        LogAction("Remote Sensor Fan Run: The Selected Rule-Type is not valid... Skipping... | RuleType: ${remSenRuleType}", "info", true) 
    }
    if(!fanAlreadyOn && !timeSinceLastRunOk) { 
        LogAction("Remote Sensor Fan Run: Required Conditions to Run Fan are not met!!! | The Time Since Last Run (${getLastRemSenFanRunDtSec()} Seconds) is not greater than Required value (${waitTimeVal} seconds)", "info", true) 
    }
    
    def result = (timeSinceLastRunOk && ruleTypeOk && tstatOperStateOk && (fanModeOk && !fanAlreadyOn)) ? true : false
    LogAction("RemSenFanRunOk Debug:", "debug", false)
    LogAction(" ├ RuleTypeOk: (${ruleTypeOk.toString().toUpperCase()}) | (RuleType: $remSenRuleType) ", "debug", false)
    LogAction(" ├ ThermostatOperatingStateOk: (${tstatOperStateOk.toString().toUpperCase()}) | (Current: $operState)", "debug", false) 
    LogAction(" ├ FanModeOk: (${fanModeOk.toString().toUpperCase()}) | (Current: $fanState)", "debug", false)
    LogAction(" ├ FanAlreadyOn: (${fanAlreadyOn.toString().toUpperCase()})", "debug", false) 
    LogAction(" ├ TimeSinceOk: (${timeSinceLastRunOk.toString().toUpperCase()}) (${getLastRemSenFanRunDtSec()} Seconds > $waitTimeVal Seconds)", "debug", false) 
    LogAction(" ├ LastFanRunSec: (${getLastRemSenFanRunDtSec()} Seconds)", "debug", false) 
    LogAction(" ┌ Final Fan Run OK Result: (${result.toString().toUpperCase()})", "debug", false)
    return result
}

def getRemSenFanTempOk(hvacMode, Double senTemp, Double setTemp, Double curTemp, Double threshold, Boolean fanOn, operType) {
    def threshVal = (remSenRuleType == "Circ") ? threshold+1.0 : threshold
    def maxFanSwing = threshVal+2.0
    def diffVal = Math.abs(senTemp-setTemp)?.round(1)
    def diff1 = (diffVal <= threshVal) ? true : false
    //def diff2 = (diffVal <= maxSwing) ? true : false
    def diff2 = (senTemp != setTemp) ? true : false
    if(fanOn) {
        if(!diff1 && diff2) {
            LogAction("Remote Sensor Fan Temp: The Temperature Difference is Outside of Threshold Limits | Turning Thermostat Fan OFF", "info", true)
        }
        if(!diff2 && diff1) {
            LogAction("Remote Sensor Fan Temp: The Temp has now reached the Desired Setpoint (${setTemp}°${atomicState?.tempUnit}) | Turning Thermostat Fan OFF", "info", true)
        }
    } else {
        if(diff1 && diff2) {
            LogAction("Remote Sensor Fan Temp: The Temperature Difference is within the Threshold Limit | Turning Thermostat Fan ON", "info", true)
        }
    }
    LogAction("RemSenFanTempOk Debug:", "debug", false)
    LogAction(" ├ Result 1: (Threshold Val: ${threshVal} <= Temp Diff: ${diffVal}°${atomicState?.tempUnit}) is (${diff1.toString().toUpperCase()})", "debug", false)
    LogAction(" ├ Result 2: (Sensor Temp: ${senTemp}°${atomicState?.tempUnit} != Setpoint Temp: ${setTemp}°${atomicState?.tempUnit}) is (${diff2.toString().toUpperCase()})", "debug", false)
    LogAction(" ├ FanAlreadyOn: (${fanOn.toString().toUpperCase()})", "debug", false)
    LogAction(" ┌ Final Result: (${(diff1 && diff2).toString().toUpperCase()})", "debug", false)
    return (diff1 && diff2) ? true : false
}

def getRemSenTempOk(modeType, Double senTemp, Double curTemp, Double threshold) {
    def tempDiff = (modeType == "heat") ? ((curTemp - senTemp) > threshold) : ((senTemp - curTemp) > threshold)
    //log.debug "getRemSenTempOk: ${tempDiff} | Heat: (${(curTemp - senTemp)}) | Cool: (${(senTemp - curTemp)})"
    return tempDiff
}

def getRemSenModeOk() {
    def result = false
    if(remSenUseSunAsMode) { result = true }
    else if (remSensorDay && !remSensorNight) {
        result = true
    }
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
        return remSenTstat ? getTstatSetpoint(remSenTstat, "cool") : 0
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
        return remSenTstat ? getTstatSetpoint(remSenTstat, "heat") : 0
    }
}

def remSenRuleEnum() {
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
    return (devCnt) >= 1 ? true : false
}
/********************************************************************************  
|                			EXTERNAL TEMP AUTOMATION CODE	     				|
*********************************************************************************/
def extTmpPrefix() { return "extTmp" }

def extTempPage() {
    def pName = extTmpPrefix()
    dynamicPage(name: "extTempPage", title: "Thermostat/External Temps Automation", uninstall: false, nextPage: "mainAutoPage") {
        section("External Temps to use to Turn off the Thermostat Below:") {
            if(!parent?.getWeatherDeviceInst()) {
                paragraph "Please Enable the Weather Device under the Manager App before trying to use External Weather as an External Sensor!!!", required: true, state: null
            } else {
                input "extTmpUseWeather", "bool", title: "Use Local Weather as External Sensor?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("weather_icon.png")
                if(extTmpUseWeather){
                    getExtConditions()
                    def wReq = (extTmpTstat && !extTmpTempSensor) ? true : false
                    def tmpVal = (location?.temperatureScale == "C") ? atomicState?.curWeatherTemp_c : atomicState?.curWeatherTemp_f
                    paragraph "Local Weather:\n• ${atomicState?.curWeatherLoc} (${tmpVal}°${atomicState?.tempUnit})", state: "complete", image: getAppImg("instruct_icon.png")
                    input name: "extTmpWeatherUpdateVal", type: "enum", title: "Update Weather (in Minutes)?", defaultValue: 15, metadata: [values:longTimeMinEnum()], submitOnChange: true, required: wReq,
                            image: getAppImg("reset_icon.png")
                }
            }
            if(!extTmpUseWeather) {
                def senReq = (!extTmpUseWeather && extTmpTstat) ? true : false
                input "extTmpTempSensor", "capability.temperatureMeasurement", title: "Select a Temp Sensor?", submitOnChange: true, multiple: false, required: senReq, 
                        image: getAppImg("temperature_icon.png")
                if(extTmpTempSensor) {
                    def tmpVal = "${extTmpTempSensor?.currentValue("temperature").toString()}"
                    paragraph "Current Sensor Temp: ${tmpVal}°${atomicState?.tempUnit}", image: getAppImg("blank_icon.png")
                }
            }
        }
        if(extTmpUseWeather || extTmpTempSensor) {
            def req = (extTmpUseWeather || (!extTmpUseWeather && extTmpTempSensor)) ? true : false
            section("When the External Temp Reaches a Certain Threshold Turn Off this Thermostat.  ") {
                input name: "extTmpTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
                if(extTmpTstat) {
                    getTstatCapabilities(extTmpTstat, extTmpPrefix())
                    def str = ""
                    str += extTmpTstat ? "Current Status:" : ""
                    str += extTmpTstat ? "\n• Temp: ${extTmpTstat?.currentTemperature}°${atomicState?.tempUnit}" : ""
                    str += extTmpTstat ? "\n• Mode: ${extTmpTstat?.currentThermostatOperatingState.toString().capitalize()}/${extTmpTstat?.currentThermostatMode.toString().capitalize()}" : ""
                    paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")
                    input name: "extTmpDiffVal", type: "decimal", title: "When Thermostat temp is within this many degrees of the external temp (°${atomicState?.tempUnit})?", defaultValue: 1.0, submitOnChange: true, required: true,
                            image: getAppImg("temp_icon.png")
                }
            }
        }
        if((extTmpUseWeather || extTmpTempSensor) && extTmpTstat) {
            section("Delay Values:") {
                input name: "extTmpOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                input name: "extTmpRestoreOnTemp", type: "bool", title: "Restore Previous Mode when Temp is below Threshold?", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("restore_icon.png")
                if(extTmpRestoreOnTemp) {
                    if(atomicState?."${extTmpPrefix()}TstatCanCool" && atomicState?."${extTmpPrefix()}TstatCanHeat") {
                        input name: "extTmpRestoreAutoMode", type: "bool", title: "Restore to Auto Mode?", description: "", required: false, defaultValue: false, submitOnChange: true,
                                image: getAppImg("restore_icon.png")
                    }
                    input name: "extTmpOnDelay", type: "enum", title: "Delay Restore (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                            image: getAppImg("delay_time_icon.png")
                }
            }
            section(getDmtSectionDesc(extTmpPrefix())) {
                def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc ? "complete" : null),
                        image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                href "setRecipientsPage", title: "(Optional) Select Recipients", description: getNotifConfigDesc(), params: [pName: "${pName}"], state: (getNotificationOptionsConf() ? "complete" : null),
                        image: getAppImg("recipient_icon.png")
            }
        }
        section("Help and Instructions:") {
            href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
        }
    }
}

def isExtTmpConfigured() {
    def devOk = ((extTmpUseWeather || extTmpTempSensor) && extTmpTstat) ? true : false
    return devOk
}

def getExtConditions() {
    def cur = parent?.getWData()
    atomicState?.curWeather = cur?.current_observation
    atomicState?.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f).toInteger()
    atomicState?.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c).toInteger()
    atomicState?.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
    atomicState?.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
    //log.debug "${atomicState?.curWeatherLoc} Weather | humidity: ${atomicState?.curWeatherHum} | temp_f: ${atomicState?.curWeatherTemp_f} | temp_c: ${atomicState?.curWeatherTemp_c}"
    if (isExtTmpConfigured() && !disableAutomation) { extTmpTempCheck() }
}

def extTmpTempOk() { 
    //log.trace "getExtTmpTempOk..."
    try {
        def intTemp = extTmpTstat ? extTmpTstat?.currentTemperature.toDouble() : null
        def extTemp = getExtTmpTemperature()
        def curMode = extTmpTstat.currentThermostatMode.toString()
        def diffThresh = getExtTmpTempDiffVal()
        
        if(intTemp && extTemp && diffThresh) { 
            def tempDiff = Math.abs(extTemp - intTemp)
            def extTempHigh = (extTemp >= intTemp) ? true : false
            def reachedThresh = (diffThresh >= tempDiff && !extTempHigh) ? true : false
            //log.debug "extTempHigh: $extTempHigh | reachedThresh: $reachedThresh"
            //log.debug "Inside Temp: ${intTemp} | Outside Temp: ${extTemp} | Temp Threshold: ${diffThresh} | Actual Difference: ${tempDiff}"
            if (extTempHigh) { return false }
            else if (reachedThresh) { return false }
        }
        return true
    } catch (ex) { 
        LogAction("getExtTmpTempOk Exception: ${ex}", "error", true)
        sendExceptionData(ex, "extTmpTempOk")
    }
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

def extTmpScheduleOk() { return autoScheduleOk(extTmpPrefix()) }
def getExtTmpTempDiffVal() { return !settings?.extTmpDiffVal ? 1.0 : settings?.extTmpDiffVal.toDouble() } 
def getExtTmpGoodDtSec() { return !atomicState?.extTmpTempGoodDt ? 100000 : GetTimeDiffSeconds(atomicState?.extTmpTempGoodDt).toInteger() }
def getExtTmpBadDtSec() { return !atomicState?.extTmpTempBadDt ? 100000 : GetTimeDiffSeconds(atomicState?.extTmpTempBadDt).toInteger() }
def getExtTmpOffDelayVal() { return !extTmpOffDelay ? 300 : extTmpOffDelay.toInteger() }
def getExtTmpOnDelayVal() { return !extTmpOnDelay ? 300 : extTmpOnDelay.toInteger() }
def getExtTmpWeatherUpdVal() { return !extTmpWeatherUpdateVal ? 15 : extTmpWeatherUpdateVal?.toInteger() }

def extTmpTempCheck() {
    //log.trace "extTmpTempCheck..."
    def curMode = extTmpTstat?.currentThermostatMode?.toString()
    def curNestPres = (getNestLocPres() == "home") ? "present" : "not present" //Use this to determine if thermostat can be turned back on.
    def modeOff = (curMode == "off") ? true : false
    def okToRestore = ((modeOff && extTmpRestoreOnTemp) && (atomicState?.extTmpTstatTurnedOff || (!atomicState?.extTmpTstatTurnedOff && extTmpRestoreAutoMode))) ? true : false
    
    if(extTmpTempOk()) {
        if(okToRestore) {
            if(getExtTmpGoodDtSec() >= (getExtTmpOnDelayVal() - 5)) {
                def lastMode = null
                if(extTmpRestoreOnTemp) {
                    if(!atomicState?.extTmpRestoreMode) {
                        if(extTmpRestoreAutoMode) {
                            lastMode = "auto"
                            LogAction("extTmpTempCheck: Setting Last Mode to 'Auto' because previous mode wasn't found and you said too do this", "info", true)
                        }
                    } else {
                        lastMode = atomicState?.extTmpRestoreMode
                    }
                }
                if(lastMode != curMode) {
                    if(setTstatMode(extTmpTstat, lastMode)) {
                        atomicState?.extTmpTstatTurnedOff = false
                        atomicState?.extTmpTstatOffRequested = false
                        runIn(20, "extTmpFollowupCheck", [overwrite: true])
                        LogAction("Restoring '${extTmpTstat?.label}' to '${lastMode.toUpperCase()}' mode because External Temp has been above the Threshold for (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})...", "info", true)
                        if(extTmpPushMsgOn) {
                            sendNofificationMsg("Restoring '${extTmpTstat?.label}' to '${lastMode.toUpperCase()}' Mode because External Temp has been above the Threshold for (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})...", "Info", 
                                    settings?."${getPagePrefix()}NofifRecips", settings?."${getPagePrefix()}NotifPhones", settings?."${getPagePrefix()}UsePush")
                        }
                    } else { 
                        LogAction("extTmpTempCheck() | There was problem restoring the last mode to '...", "error", true) 
                    }
                } else {
                    LogAction("extTmpTempCheck() | Skipping Restore because the Mode to Restore is same as Current Mode", "info", true)
                }
            } 
        }
    }
    if (!extTmpTempOk()) {
        if(!modeOff) {
            if(getExtTmpBadDtSec() >= (getExtTmpOffDelayVal() - 2)) {
                if(extTmpRestoreOnTemp) { 
                    atomicState?.extTmpRestoreMode = curMode
                    LogAction("Saving ${extTmpTstat?.label} (${atomicState?.extTmpRestoreMode.toString().toUpperCase()}) mode for Restore later.", "info", true)
                }
                //log.debug("External Temp has reached the temp threshold turning 'Off' ${extTmpTstat}")
                extTmpTstat?.off()
                atomicState?.extTmpTstatTurnedOff = true
                atomicState?.extTmpTstatOffRequested = true
                runIn(20, "extTmpFollowupCheck", [overwrite: true])
                LogAction("${extTmpTstat} has been turned 'Off' because External Temp is at the temp threshold for (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})!!!", "info", true)
                if(extTmpPushMsgOn) {
                    sendNofificationMsg("${extTmpTstat?.label} has been turned 'Off' because External Temp is at the temp threshold for (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})!!!", "Info", 
                        settings?."${getPagePrefix()}NofifRecips", settings?."${getPagePrefix()}NotifPhones", settings?."${getPagePrefix()}UsePush")
                }
            }
        } else { LogAction("extTmpTempCheck() | Skipping change because '${extTmpTstat?.label}' mode is already 'OFF'", "info", true) }
    }
}

def extTmpTstatModeEvt(evt) {
    log.trace "extTmpTstatModeEvt... '${evt?.displayName}' Mode is now (${evt?.value.toString().toUpperCase()})"
    if(disableAutomation) { return }
    else {
        def modeOff = (evt?.value == "off") ? true : false
        if(!modeOff) { atomicState?.extTmpTstatTurnedOff = false }
    }
}

def extTmpFollowupCheck() {
    //log.trace "extTmpFollowupCheck..."
    def curMode = extTmpTstat?.currentThermostatMode.toString()
    def modeOff = (curMode == "off") ? true : false
    def extTmpTstatReqOff = atomicState?.extTmpTstatOffRequested ? true : false
    if (modeOff != extTmpTstatReqOff) { extTmpCheck() }
}

def extTmpTempEvt(evt) {
    //log.debug "extTmpTempEvt: ${evt?.value}"
    if(disableAutomation) { return }
    else {
        def pName = extTmpPrefix()
        def curMode = extTmpTstat?.currentThermostatMode.toString()
        def modeOff = (curMode == "off") ? true : false
        def extTmpOk = extTmpTempOk()
        def offVal = getExtTmpOffDelayVal()
        def onVal = getExtTmpOnDelayVal()
        def timeVal
        def canSched = false
        //log.debug "extTmpOk: $extTmpOk | modeOff: $modeOff | extTmpTstatTurnedOff: ${atomicState?.extTmpTstatTurnedOff}"
        if(extTmpScheduleOk()) {
            if (!extTmpOk) { 
                if (!modeOff) {
                    atomicState.extTmpGoodDt = getDtNow()
                    timeVal = ["valNum":offVal, "valLabel":getEnumValue(longTimeSecEnum(), offVal)]
                    canSched = true
                } 
            }
            else if (extTmpOk) {
                if(modeOff && atomicState?.extTmpTstatTurnedOff) {
                    atomicState.extTmpBadDt = getDtNow()
                    timeVal = ["valNum":onVal, "valLabel":getEnumValue(longTimeSecEnum(), onVal)]
                    canSched = true
                }
            }
            if (canSched) {
                //log.debug "timeVal: $timeVal"
                LogAction("extTmpTempEvt() ${!evt ? "" : "'${evt?.displayName}': (${evt?.value}°${atomicState?.tempUnit}) received... | "}External Temp Check scheduled for (${timeVal?.valLabel})...", "info", true)
                runIn(timeVal?.valNum, "extTmpTempCheck", [overwrite: true])
            } else {
                unschedule("extTmpTempCheck")
                LogAction("extTmpTempEvt: Skipping Event... All External Temps are above the threshold... Any existing schedules have been cancelled...", "info", true)
            }
        } else {
            LogAction("extTmpTempEvt: Skipping Event... This Event did not happen during the required Day, Mode, Time...", "info", true)
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
            if(conWatContacts) {
                //def conDesc = "${getOpenContacts(conWatContacts) ? "(${getOpenContacts(conWatContacts).size()}) Opened" : "All Closed"}"
                paragraph "${conWatContactDesc()}", state: "complete", image: getAppImg("instruct_icon.png")
            }
            input name: "conWatTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: getAppImg("thermostat_icon.png")
            if (conWatTstat) {
                getTstatCapabilities(conWatTstat, conWatPrefix())
                def str = ""
                str += conWatTstat ? "Current Status:" : ""
                str += conWatTstat ? "\n• Temp: ${conWatTstat?.currentTemperature}°${atomicState?.tempUnit}" : ""
                str += conWatTstat ? "\n• Mode: ${conWatTstat?.currentThermostatOperatingState.toString().capitalize()}/${conWatTstat?.currentThermostatMode.toString().capitalize()}" : ""
                paragraph "${str}", state: (str != "" ? "complete" : null), image: getAppImg("instruct_icon.png")
            }
            if(dupTstat) {
                paragraph "Duplicate Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", image: getAppImg("error_icon.png")
            }
            if(conWatTstat) {
                input name: "conWatTstatMir", type: "capability.thermostat", title: "Mirror commands to these Thermostats?", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("thermostat_icon.png")
            }
        }
        if(conWatContacts && conWatTstat) {
            section("Delay Values:") {
                input name: "conWatOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")

                input name: "conWatRestoreOnClose", type: "bool", title: "Restore Previous Mode on Close?", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("restore_icon.png")
                if(conWatRestoreOnClose) {
                    if(atomicState?."${conWatPrefix()}TstatCanCool" && atomicState?."${conWatPrefix()}TstatCanHeat") {
                        input name: "conWatRestoreAutoMode", type: "bool", title: "Restore to Auto Mode if Already Off?", description: "", required: false, defaultValue: false, submitOnChange: true,
                                image: getAppImg("restore_icon.png")
                    }
                    input name: "conWatOnDelay", type: "enum", title: "Delay Restore (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                }
            }
            section(getDmtSectionDesc(conWatPrefix())) {
                def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc ? "complete" : null), 
                        image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                href "setNotificationPage", title: "Configure Notifications...", description: getNotifConfigDesc(), params: ["pName":pName, "allowSpeech":true, "showSchedule":true], 
                        state: (getNotificationOptionsConf() ? "complete" : null), image: getAppImg("notification_icon.png")
                if(settings?."${pName}AllowSpeechNotif") {
                    input name: "conWatSpeechOnRestore", type: "bool", title: "Speak on Mode Restoration?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("speech_icon.png")
                }
            }
        }
        section("Help:") {
            href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
        }
    }
}

def conWatContactDesc() {
    if(conWatContacts) {
        def cCnt = conWatContacts?.size() ?: 0
        def str = ""
        def cnt = 0
        str += "Contacts:"
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
def getConWatOffDelayVal() { return !conWatOffDelay ? 300 : (conWatOffDelay.toInteger()) }
def getConWatOnDelayVal() { return !conWatOnDelay ? 300 : (conWatOnDelay.toInteger()) }

def conWatCheck() {
    //log.trace "conWatCheck..."
    try {
        if (disableAutomation) { return }
        else {
            def curMode = conWatTstat?.currentState("thermostatMode")?.value.toString()
            def curNestPres = (getNestLocPres() == "home") ? "present" : "not present" //Use this to determine if thermostat can be turned back on.
            def modeOff = (curMode == "off") ? true : false
            def openCtDesc = getOpenContacts(conWatContacts) ? " '${getOpenContacts(conWatContacts)?.join(", ")}' " : " a selected contact "
            def okToRestore = ((modeOff && conWatRestoreOnClose) && (atomicState?.conWatTstatTurnedOff || (!atomicState?.conWatTstatTurnedOff && conWatRestoreAutoMode))) ? true : false
            def allowNotif = settings?."${getPagePrefix()}PushMsgOn" ? true : false
            def allowSpeech = allowNotif && settings?."${getPagePrefix()}AllowSpeechNotif" ? true : false
            def speakOnRestore = allowSpeech && settings?."${getPagePrefix()}SpeechOnRestore" ? true : false
            //log.debug "curMode: $curMode | modeOff: $modeOff | conWatRestoreOnClose: $conWatRestoreOnClose | lastMode: $lastMode"
            //log.debug "conWatTstatTurnedOff: ${atomicState?.conWatTstatTurnedOff} | getConWatCloseDtSec(): ${getConWatCloseDtSec()}"
            if(getConWatContactsOk()) {
                if(okToRestore) {
                    if(getConWatCloseDtSec() >= (getConWatOnDelayVal() - 5)) {
                        def lastMode = null
                        if(conWatRestoreOnClose) {
                            if(!atomicState?.conWatRestoreMode) {
                                if(conWatRestoreAutoMode) {
                                    lastMode = "auto"
                                    LogAction("conWatCheck: Setting Last Mode to 'Auto' because previous mode wasn't found and you said too do this", "info", true)
                                }
                            } else {
                                lastMode = atomicState?.conWatRestoreMode
                            }
                        }
                        if(lastMode != curMode) {
                            if(setTstatMode(conWatTstat, lastMode)) {
                                atomicState.conWatTstatTurnedOff = false
                                atomicState?.conWatTstatOffRequested = false
                                if(conWatTstatMir) { 
                                    conWatTstatMir?.each { tstat ->
                                        if(setTstatMode(tstat, lastMode)) {
                                            LogAction("Mirroring Restoring Mode (${lastMode}) to ${tstat}", "info", true)
                                        }
                                    }
                                }
                                if(canSchedule()) { runIn(20, "conWatFollowupCheck", [overwrite: true]) }
                                LogAction("Restoring '${conWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' Mode because ALL contacts have been 'Closed' again for (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})...", "info", true)
                                if(allowNotif) {
                                    sendNofificationMsg("Restoring '${conWatTstat?.label}' to '${lastMode?.toString().toUpperCase()}' Mode because ALL contacts have been 'Closed' again for (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})...", "Info", 
                                            settings?."${getPagePrefix()}NofifRecips", settings?."${getPagePrefix()}NotifPhones", settings?."${getPagePrefix()}UsePush")
                                    if(allowSpeech && speakOnRestore) {
                                        def msg = "Restoring ${conWatTstat} to ${lastMode?.toString().toUpperCase()} Mode because ALL contacts have been Closed again for (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})"
                                        sendTTS(msg)
                                    }
                                }
                            } else { 
                                LogAction("conWatCheck() | There was problem restoring the last mode to '...", "error", true) 
                            }
                        } else {
                            LogAction("conWatCheck() | Skipping Restore because the Mode to Restore is same as Current Modes", "info", true)
                        }
                    }
                } 
            }
            
            if (!getConWatContactsOk()) {
                if(!modeOff) {
                    if(getConWatOpenDtSec() >= (getConWatOffDelayVal() - 2)) {
                        if(conWatRestoreOnClose) { 
                            atomicState?.conWatRestoreMode = curMode
                            LogAction("Saving ${conWatTstat?.label} mode (${atomicState?.conWatRestoreMode.toString().toUpperCase()}) for Restore later.", "info", true)
                        }
                        log.debug("${openCtDesc} ${getOpenContacts(conWatContacts).size() > 1 ? "are" : "is"} still Open: Turning 'OFF' '${conWatTstat?.label}'")
                        atomicState?.conWatTstatTurnedOff = true
                        atomicState?.conWatTstatOffRequested = true
                        conWatTstat?.off()
                        if(conWatTstatMir) { 
                            conWatTstatMir?.each { tstat ->
                                tstat?.off()
                                log.debug("Mirrored Off Command to ${tstat}")
                            }
                        }
                        if(canSchedule()) { runIn(20, "conWatFollowupCheck", [overwrite: true]) }
                        LogAction("conWatCheck: '${conWatTstat.label}' has been turned 'OFF' because${openCtDesc}has been Opened for (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})...", "warning", true)
                        if(allowNotif) {
                            sendNofificationMsg("'${conWatTstat.label}' has been turned 'OFF' because${openCtDesc}has been Opened for (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})...", "Info", 
                                settings?."${getPagePrefix()}NofifRecips", settings?."${getPagePrefix()}NotifPhones", settings?."${getPagePrefix()}UsePush")
                            if(allowNotif && allowSpeech) {
                                def msg = "${conWatTstat} has been turned OFF because${openCtDesc}has been Opened for (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})"
                                sendTTS(msg)
                            }
                        }
                    }
                } else { LogAction("conWatCheck() | Skipping change because '${conWatTstat?.label}' mode is already 'OFF'", "info", true) }
            }
        }
    } catch (ex) {
        LogAction("conWatCheck Exception: (${ex})", "error", true)
        sendExceptionData(ex, "conWatCheck")
    }
}

def conWatFollowupCheck() {
    //log.trace "conWatFollowupCheck..."
    def curMode = conWatTstat?.currentThermostatMode.toString()
    def modeOff = (curMode == "off") ? true : false
    def conWatTstatReqOff = atomicState?.conWatTstatOffRequested ? true : false
    if (modeOff != conWatTstatReqOff) { conWatCheck() }
}

def conWatTstatModeEvt(evt) {
    LogAction("ContactWatch Thermostat Mode Event | '${evt?.displayName}' Mode is now (${evt?.value.toString().toUpperCase()})", "trace", false)
    if (disableAutomation) { return }
    else { 
        def modeOff = (evt?.value == "off") ? true : false
        //if(!modeOff) { atomicState?.conWatTstatTurnedOff = false }
    }
}

def conWatContactEvt(evt) {
    LogAction("ContactWatch Contact Event | '${evt?.displayName}' is now (${evt?.value.toString().toUpperCase()})", "trace", false)
    if (disableAutomation) { return }
    else {
        def pName = conWatPrefix()
        def curMode = conWatTstat?.currentThermostatMode.toString()
        def isModeOff = (curMode == "off") ? true : false
        def conOpen = (evt?.value == "open") ? true : false
        def contactsOk = getConWatContactsOk()
        
        def canSched = false
        def timeVal
        if (conWatScheduleOk()) {
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
                runIn(timeVal?.valNum, "conWatCheck", [overwrite: true]) 
            } else {
                unschedule("conWatCheck")
                LogAction("conWatContactEvt: Skipping Event... Any existing schedules have been cancelled...", "info", true)
            }
        } else {
            LogAction("conWatContactEvt: Skipping Event... This Event did not happen during the required Day, Mode, Time...", "info", true)
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
                    paragraph " • Location Mode: ${location?.mode}", state: "complete", image: getAppImg("instruct_icon.png")
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
            }
        }
        if(((nModeHomeModes && nModeAwayModes) && !nModePresSensor) || nModePresSensor) {
            section(getDmtSectionDesc(nModePrefix())) {
                def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc ? "complete" : null), 
                        image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                href "setRecipientsPage", title: "(Optional) Select Recipients", description: getNotifConfigDesc(), params: [pName: "${pName}"], state: (getNotificationOptionsConf() ? "complete" : null),
                        image: getAppImg("recipient_icon.png")
            }
        }
        section("Help:") {
            href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
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
        if(nModeDelay) {
            LogAction("NestMode Event: ST Location Mode is ${evt?.value.toString().toUpperCase()} | A Mode Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
            runIn( nModeDelayVal.toInteger(), "checkNestMode", [overwrite: true] )
        } else {
            LogAction("NestMode Event | ST Location Mode is (${evt?.value.toString().toUpperCase()})", "trace", true)
            checkNestMode()
        }
    } 
}

def nModePresEvt(evt) {
    if (disableAutomation) { return }
    else if(nModeDelay) {
        LogAction("NestMode Event | Presence: ${!evt ? "A monitored presence device is " : "SWITCH '${evt?.displayName}' is "} (${evt?.value.toString().toUpperCase()}) | A Presence Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
        runIn( nModeDelayVal.toInteger(), "checkNestMode", [overwrite: true] )
    } else {
        LogAction("NestMode Event | Presence is (${evt?.value.toString().toUpperCase()})", "trace", true)
        checkNestMode()
    }
}

def nModeSwitchEvt(evt) {
    if (disableAutomation) { return }
    else if(nModeSwitch && !nModePresSensor) {
        if(nModeDelay) {
            LogAction("NestMode Event | ${!evt ? "A monitored switch is " : "Switch (${evt?.displayName}) is "} (${evt?.value.toString().toUpperCase()}) | A Switch Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
            runIn( nModeDelayVal.toInteger(), "checkNestMode", [overwrite: true] )
        } else {
            LogAction("NestMode Event | Switch (${evt?.displayName}) is (${evt?.value.toString().toUpperCase()})", "trace", true)
            checkNestMode()
        }
    }
}

def nModeFollowupCheck() {
    def nestModeAway = (getNestLocPres() == "home") ? false : true
    def nModeAwayState = atomicState?.nModeTstatLocAway
    if(nestModeAway && !nModeAwayState) { checkNestMode() }
}

def nModeScheduleOk() { return autoScheduleOk(nModePrefix()) }

def checkNestMode() {
    LogAction("checkNestMode...", "trace", false)
    try {
        if (disableAutomation) { return }
        else if(!nModeScheduleOk()) { 
            LogAction("Check NestMode: Skipping because of Schedule Restrictions...", "info", true)
        } else {
            def curStMode = location?.mode
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
            
            if (away) {
                LogAction("${awayDesc} Nest 'Away'", "info", true)
                atomicState?.nModeTstatLocAway = true
                if(parent?.setStructureAway(null, true)) { 
                    if(nModePushMsgOn) {
                        sendNofificationMsg("${awayDesc} Nest 'Away", "Info", settings?."${getPagePrefix()}NofifRecips", settings?."${getPagePrefix()}NotifPhones", settings?."${getPagePrefix()}UsePush")
                    }
                } else {
                    LogAction("checkNestMode: There was an issue sending the AWAY command to Nest", "error", true)
                }
                runIn(20, "nModeFollowupCheck", [overwrite: true])
            }
            else if (home) {
                LogAction("${homeDesc} Nest 'Home'", "info", true)
                atomicState?.nModeTstatLocAway = false
                if (parent?.setStructureAway(null, false)) { 
                    if(nModePushMsgOn) {
                        sendNofificationMsg("${homeDesc} Nest 'Home", "Info", settings?."${getPagePrefix()}NofifRecips", settings?."${getPagePrefix()}NotifPhones", settings?."${getPagePrefix()}UsePush")
                    }
                } else {
                    LogAction("checkNestMode: There was an issue sending the AWAY command to Nest", "error", true)
                }
                runIn(20, "nModeFollowupCheck", [overwrite: true])
            } 
            else {
                LogAction("checkNestMode: Conditions are not valid to change mode | isPresenceHome: (${nModePresSensor ? "${isPresenceHome(nModePresSensor)}" : "Presence Not Used"}) | ST-Mode: ($curStMode) | NestModeAway: ($nestModeAway) | Away?: ($away) | Home?: ($home)", "info", true)
            }
        }
    } catch (ex) { 
        LogAction("checkNestMode Exception: (${ex})", "error", true)
        sendExceptionData(ex, "checkNestMode")
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
|                   ST MODE THERMOSTAT SETPOINT AUTOMATION CODE	     		    |
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

        section("Help:") {
            href url:"${getAutoHelpPageUrl()}", style:"embedded", required:false, title:"Help and Instructions...", description:"", image: getAppImg("info.png")
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
        if(tModeDelay) {
            LogAction("TstatSetpoint Event | ST Location Mode is: ${evt?.value} | A Mode Check is scheduled for (${getEnumValue(longTimeSecEnum(), tModeDelayVal)})", "info", true)
            runIn( tModeDelayVal.toInteger(), "checkTstatMode", [overwrite: true] )
        } else {
            LogAction("TstatSetpoint Event | ST Location Mode is: (${evt?.value})", "trace", true)
            checkTstatMode()
        }
    } 
}

def checkTstatMode() {
    LogAction("checkTstatMode...", "trace", false)
    try {
        if (disableAutomation) { return }
        //else if(!tModeScheduleOk()) { 
          //  LogAction("checkNestMode: Skipping because of Schedule Restrictions...")
        //} 
        else {
            def curStMode = location?.mode
            def heatTemp = 0
            def coolTemp = 0
            if (tModeTstats) {
                tModeTstats?.each { ts -> 
                    def modes = settings?."${getTstatModeInputName(ts)}" ?: null
                    if (modes && (curStMode in modes)) {
                        def newHvacMode = settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HvacMode" ? 
                            (settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HvacMode" == "heat-cool" ? "auto" : settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HvacMode") : null 
                        if(newHvacMode && (newHvacMode.toString() != tstatHvacMode)) {
                            if(setTstatMode(ts, newHvacMode)) {
                                sendEvent(device: ts, name: 'thermostatMode', value: newHvacMode, descriptionText: "HVAC mode is ${newHvacMode.toString().capitalize()}", displayed: true, isStateChange: true )
                                LogAction("Setting Thermostat Mode to '${newHvacMode?.toString().capitalize()}' on ($ts)", "info", true)
                            }
                        }
                        def tstatHvacMode = ts?.currentThermostatMode.toString()
                        if(atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanHeat") {
                            heatTemp = settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_HeatTemp".toInteger()
                            LogAction("Setting Heat Setpoint to '${heatTemp}' on ($ts)", "info", true)
                            ts?.setHeatingSetpoint(heatTemp.toInteger())
                        }
                        if(atomicState?."tMode_${ts?.device?.deviceNetworkId}_TstatCanCool") {
                            coolTemp = settings?."tMode_|${ts?.device.deviceNetworkId}|_Modes_${curStMode}_CoolTemp".toInteger()
                            LogAction("Setting Cool Setpoint to '${coolTemp}' on ($ts)", "info", true)
                            ts?.setCoolingSetpoint(coolTemp.toInteger())
                        }
                        //log.debug "tStatModes: $modes | newHvacMode: $newHvacMode | tstatHvacMode: $tstatHvacMode | heatTemp: $heatTemp | coolTemp: $coolTemp | curStMode: $curStMode"
                    }
                }
            }
        }
    } catch (ex) { 
        LogAction("checkTstaMode Exception: (${ex})", "error", true)
        sendExceptionData(ex, "checkTstatMode")
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
    def pName = getPagePrefix()
    def allowSpeech = false
    def showSched = false
    if(params?.pName) {
        atomicState.curNotifPageData = params 
        allowSpeech = params?.allowSpeech?.toBoolean(); showSched = params?.showSchedule?.toBoolean()
    } else { 
        allowSpeech = atomicState?.curNotifPageData?.allowSpeech; showSched = atomicState?.curNotifPageData?.showSchedule
    } 
    dynamicPage(name: "setNotificationPage", title: "Configure Notification Options", uninstall: false) {
        section("Push Notification Preferences:") {
            input "${pName}PushMsgOn", "bool", title: "Send Notifications on Events?", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
        }
        if(settings["${pName}PushMsgOn"]) {
            def notifDesc = !location.contactBookEnabled ? "Enable Push Messages Below..." : "Select People or Devices to Receive Notifications:\n(Manager App Recipients are Used by Default)"
            section("${notifDesc}") {
                if(!location.contactBookEnabled) {
                    input "${pName}UsePush", "bool", title: "Send Push Notitifications", required: false, defaultValue: false, image: getAppImg("notification_icon.png")
                } else {
                    input("${pName}NotifRecips", "contact", title: "Send notifications to", required: false, image: getAppImg("recipient_icon.png")) {
                        input ("${pName}NotifPhones", "phone", title: "Phone Number to send SMS to...", description: "Phone Number", required: false)
                    }
                }
            }
        }
        if(showSchedule && settings["${pName}PushMsgOn"]) {
            section(title: "Time Restrictions") {
                href "setNotificationTimePage", title: "Silence Notifications...", description: (getNotifSchedDesc() ?: "Tap to configure..."), state: (getNotifSchedDesc() ? "complete" : null), image: getAppImg("quiet_time_icon.png")
            }
        }
        
        if(allowSpeech) {
            section("Voice Notification Preferences:") {
                input "${pName}AllowSpeechNotif", "bool", title: "Enable Voice Notitifications?", required: false, defaultValue: (settings?."${pName}AllowSpeechNotif" ? true : false), submitOnChange: true, 
                        image: getAppImg("speech_icon.png")
                if(settings["${pName}AllowSpeechNotif"]) {
                    input "${pName}SpeechMediaPlayer", "capability.musicPlayer", title: "Select Media Player Devices", multiple: true, required: false, 
                            submitOnChange: true, image: getAppImg("media_player.png")
                    input "${pName}SpeechDevices", "capability.speechSynthesis", title: "Select Speech Synthesis Devices", multiple: true, required: false,
                            submitOnChange: true, image: getAppImg("speech2_icon.png")
                    if(settings["${pName}SpeechMediaPlayer"]) {
                        input "${pName}SpeechVolumeLevel", "number", title: "Default Volume Level?", required: false, defaultValue: 30, range: "0::100", submitOnChange: true, image: getAppImg("volume_icon.png")
                        input "${pName}SpeechAllowResume", "bool", title: "Allow Resume Playing Media?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("resume_icon.png")
                    }
                }
            }
        }
    }
}

def getNotifConfigDesc() {
    def pName = getPagePrefix()
    def str = ""
    str += (settings?."${pName}PushMsgOn" && (getRecipientDesc() || (settings?."${pName}AllowSpeechNotif" && (settings?."${pName}SpeechDevices" || settings?."${pName}SpeechMediaPlayer")))) ?
            "Notification Status:" : ""
    str += (settings?."${pName}NotifRecips") ? "${str != "" ? "\n" : ""} • Contacts: (${settings?."${pName}NotifRecips"?.size()})" : ""
    str += (settings?."${pName}UsePush") ? "\n • Push Messages: Enabled" : ""
    str += (settings?."${pName}NotifPhones") ? "\n • SMS: (${settings?."${pName}NotifPhones"?.size()})" : ""
    //str += (pushStatus() && phone) ? "\n • SMS: (${phone?.size()})" : ""
    str += (getNotifSchedDesc() ? "${!getRecipientDesc() ? "" : "\n"}Schedule Options Selected..." : "")
    str += getVoiceNotifConfigDesc() ? "${(str != "") ? "\n\n" : "\n"}Voice Status:${getVoiceNotifConfigDesc()}" : ""
    return (str != "") ? "${str}" : null
}

def getVoiceNotifConfigDesc() {
    def pName = getPagePrefix()
    def str = ""
    if(settings?."${pName}PushMsgOn") {
        def speaks = getInputToStringDesc(settings?."${pName}SpeechDevices", true)
        def medias = getInputToStringDesc(settings?."${pName}SpeechMediaPlayer", true)
        str += speaks ? "\n • Speech Devices:${speaks.size() > 1 ? "\n" : ""}${speaks}" : ""
        str += medias ? "\n • Media Players:${medias.size() > 1 ? "\n" : ""}${medias}" : ""
        str += (medias && settings?."${pName}SpeechVolumeLevel") ? "\n      Volume: (${settings?."${pName}SpeechVolumeLevel"})" : "" 
        str += (medias && settings?."${pName}SpeechAllowResume") ? "\n      Resume: (${settings?."${pName}SpeechAllowResume".toString().capitalize()})" : ""
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
    //def schedInverted = settings?."${getPagePrefix()}DmtInvert"
    def startInput = settings?."${getPagePrefix()}qStartInput"
    def startTime = settings?."${getPagePrefix()}qStartTime"
    def stopInput = settings?."${getPagePrefix()}qStopInput"
    def stopTime = settings?."${getPagePrefix()}qStopTime"
    def dayInput = settings?."${getPagePrefix()}quietDays"
    def modeInput = settings?."${getPagePrefix()}quietModes"
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
    def pName = getPagePrefix()
    return (getRecipientDesc() || (settings?."${pName}AllowSpeechNotif" && (settings?."${pName}SpeechDevices" || settings?."${pName}SpeechMediaPlayer")) ) ? true : false
}

def setRecipientsPage(params) {
    def pName = getPagePrefix()
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
    return ((settings?."${getPagePrefix()}NotifRecips") || (settings?."${getPagePrefix()}NotifPhones" || settings?."${getPagePrefix()}NotifUsePush")) ? "${getRecipientsNames(settings?."${getPagePrefix()}NotifRecips")}" : null
}

def setNotificationTimePage() {
    def pName = getPagePrefix()
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
    def pName = getPagePrefix()
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
        LogAction("${autoType}-autoScheduleOk Exception: ${ex}", "error", true)
        sendExceptionData(ex, "${autoType}-autoScheduleOk")
    }
}

/************************************************************************************************
|							GLOBAL Code | Logging AND Diagnostic							    |
*************************************************************************************************/

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
        sendExceptionData("${tstat} - ${autoType} | ${ex}", "getTstatCapabilities")
    }
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
        return cnts ? cnts : null
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

def setTstatMode(tstat, mode) {
    def result = false
    try {
        if(mode) {
            if (mode == "auto") { tstat?.auto(); result = true }
            else if (mode == "heat") { tstat?.heat(); result = true }
            else if (mode == "cool") { tstat?.cool(); result = true }
            else if (mode == "off") { tstat?.off(); result = true }
            
            if(result) { LogAction("setThermostatMode: '${tstat?.label}' Mode has been set to (${mode.toString().toUpperCase()})", "info", false) }
            else { LogAction("setTstatMode() | Invalid or Missing Mode received: ${mode}", "error", true) }
        } else { 
            LogAction("setTstatMode() | Invalid or Missing Mode received: ${mode}", "warn", true)
        }
    }
    catch (ex) { 
        LogAction("setTstatMode() Exception | ${ex}", "error", true)
        sendExceptionData(ex, "setTstatMode")
    }
    return result
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
        8:"8 Seconds", 9:"9 Seconds", 10:"10 Seconds", 15:"15 Seconds", 30:"30 Seconds"
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
        def pName = getPagePrefix()
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
        LogAction("sendTTS Exception: (${ex})", "error", true)
        sendExceptionData(ex, "sendTTS")
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
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
private def appInfoDesc() 	{
    def cur = atomicState?.appData?.versions?.app?.ver.toString()
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
