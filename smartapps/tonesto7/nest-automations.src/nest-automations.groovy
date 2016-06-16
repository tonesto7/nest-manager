/********************************************************************************************
|    Application Name: Nest Automations                                                   	|
|    Author: Anthony S. (@tonesto7)															|
|	 Contributors: Ben W. (@desertblade) | Eric S. (@E_sch)                  				|
|                                                                                           |
|********************************************************************************************
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
    parent: "${appParent()}",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/automation_icon.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/automation_icon.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/automation_icon.png",
    singleInstance: true)

def appVersion() { "1.2.5" }
def appVerDate() { "5-20-2016" }
def appVerInfo() {
    
    "V1.2.5 (May 20th, 2016)\n" +
    "Fixed: ScheduleOk bug in external temp and contact automations. \n\n" +
    
    "V1.2.4 (May 15th, 2016)\n" +
    "Updated Timezone selection for users without ST Hub timezones it will use Nest's\n\n" +
    
    "V1.2.3 (May 13th, 2016)\n" +
    "Fixed: Contact mode filter issue\n" +
    "Updated Certain Inputs to turn blue when there settings have been configured.\n\n" +
    
    "V1.2.1 (May 4th, 2016)\n" +
    "Fixes and UI updates\n\n"+
    
    "V1.2.0 (May 2nd, 2016)\n" +
    "Lot's of Fixes and the ability to use switches to set nest mode\n"+
    "Updated: Modified Interface to allow selecting one automation per child app.\n\n"+
    
    "V1.1.1 (Apr 28th, 2016)\n" +
    "Fixed: Bug Fixes...\n\n" +
    
    "V1.1.0 (Apr 27th, 2016)\n" +
    "Updated: standardized input and variable naming.\n" +
    "Updated: Select Custom Notification Recipients for Contacts, External Temps, and Mode Automations \n" + 
    "Fixed: Reworked the mode and contact event Logic...\n\n" +
    
    "V1.0.1 (Apr 22nd, 2016)\n" +
    "Updated: Cleaned up code \n" + 
    "Added: Links to in-app help pages. \n" +
    "Added: Ability to use presence to set Nest home/away...\n\n" +
    
    "V1.0.0 (Apr 21st, 2016)\n" +
    "Added... Everything is new...\n\n" +
    "------------------------------------------------"
}

preferences {
    page(name: "selectPage" )
    page(name: "mainPage")
    page(name: "namePage", install: true, uninstall: true)
    page(name: "remSensorPage")
    page(name: "remSensorTempsPage")
    page(name: "contactWatchPage")
    page(name: "fanVentPage" )
    page(name: "nestModePresPage")
    page(name: "extTempPage")
    page(name: "setRecipientsPage")
    page(name: "setDayModeTimePage")
}

def selectPage() {
    //log.trace "selectPage()..."
    if(!atomicState?.automationType) {
        return dynamicPage(name: "selectPage", title: "Choose an Automation Type...", uninstall: false, install: false, nextPage: "mainPage") {
            section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
                href "mainPage", title: "Remote Temp Sensors...", description: "", params: [autoType: "remSen"], image: getAppImg("remote_sensor_icon.png")
            }
            section("Turn Thermostat On/Off based on External Temps:") {
                href "mainPage", title: "External Temp Sensors...", description: "", params: [autoType: "extTmp"], image: getAppImg("external_temp_icon.png")
            }
            section("Turn Thermostat On/Off when a Door/Window is Opened:") {
                href "mainPage", title: "Contact Sensors...", description: "", params: [autoType: "conWat"], image: getAppImg("open_window.png")
            }
            section("Set Nest Presence Based on ST Modes, Presence Sensor, or Switches:") {
                href "mainPage", title: "Nest Mode Automations", description: "", params: [autoType: "nMode"], image: getAppImg("mode_automation_icon.png")
            }
        }
    }
    else { return mainPage( [autoType: atomicState?.automationType]) }
}

def mainPage(params) {
    //log.trace "mainPage()"
    if (!atomicState?.tempUnit) { atomicState?.tempUnit = getTemperatureScale()?.toString() }
    def autoType = null
    //If params.autotype is not null then save to atomicstate.  
    if (!params?.autoType) { autoType = atomicState?.automationType } 
    else { atomicState.automationType = params?.autoType; autoType = params?.autoType }

    // If the selected automation has not been configured take directly to the config page.  Else show main page
    if (autoType == "remSen" && !isRemSenConfigured()) { return remSensorPage() }
    else if (autoType == "extTmp" && !isExtTmpConfigured()) { return extTempPage() }
    else if (autoType == "conWat" && !isConWatConfigured()) { return contactWatchPage() }
    else if (autoType == "nMode" && !isNestModesConfigured()) { return nestModePresPage() }
    
    else { 
        // Main Page Entries
        def nxtPage = (atomicState?.automationType) ? "namePage" : ""
        return dynamicPage(name: "mainPage", title: "Automation Config Page...", uninstall: true, install: false, nextPage: "namePage" ) {
            if(autoType == "remSen") {
                section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
                    def remSenEnableDesc = atomicState?.remSenEnabled ? "" : "External Sensor Disabled...\n"
                    def remSenTstatTempDesc = remSenTstat ? "Thermostat Temp: (${getDeviceTemp(remSenTstat)}°${atomicState?.tempUnit})" : ""
                    def remSenTstatStatus = remSenTstat ? "\nThermostat Mode: (${remSenTstat?.currentThermostatOperatingState.toString()}/${remSenTstat?.currentThermostatMode.toString()})" : ""
                    def remSenDayDesc = remSensorDay ? ("\n${!remSensorNight ? "Remote" : "Day"} Sensor${(remSensorDay?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit})") : ""
                    def remSenNightDesc = remSensorNight ? ("\nNight Sensor${(remSensorNight?.size() > 1) ? " (avg):" : ":"} (${getDeviceTempAvg(remSensorNight)}°${atomicState?.tempUnit})") : ""
                    def remSenTypeUsed = getUseNightSensor() ? remSenNightDesc : remSenDayDesc
                    def remSenSetTemps = (getRemSenCoolSetTemp() && getRemSenHeatSetTemp()) ? "\nHeat/Cool Set To: (${getRemSenHeatSetTemp()}°${atomicState?.tempUnit}/${getRemSenCoolSetTemp()}°${atomicState?.tempUnit})" : ""
                    def remSenRuleType = remSenRuleType ? "\nRule-Type: (${getEnumValue(remSenRuleEnum(), remSenRuleType)})" : ""
                    def remSenSunDesc = remSenUseSunAsMode ? "\nSunrise: ${atomicState.sunriseTm} | Sunset: ${atomicState.sunsetTm}" : ""
                    def remSenMotInUse = remSenMotion ? ("\nMotion: ${((!remSenMotionModes || isInMode(remSenMotionModes)) ? "Active" : "Not Active")} ${isMotionActive(remSenMotion) ? "(Motion)" : "(No Motion)"}") : ""
                    def remSenSwitInUse = remSenSwitches ? ("\nSwitches Used: (${remSenSwitches?.size()}) | Triggers (${getEnumValue(switchEnumVals(), remSenSwitchOpt)})") : ""
                    def remSenModes = remSenModes ? "\nMode Filters Active" : ""
                    def remSenDesc = (isRemSenConfigured() ? ("${remSenTstatTempDesc}${remSenTstatStatus}${remSenTypeUsed}${remSenSetTemps}${remSenRuleType}${remSenSunDesc}${remSenMotInUse}"+
                                                              "${remSenSwitInUse}${remSenModes}\n\nTap to Modify...") : null)
                    href "remSensorPage", title: "Remote Sensors Config...", description: remSenDesc ? remSenDesc : "Tap to Configure...", state: (remSenDesc ? "complete" : null), image: getAppImg("remote_sensor_icon.png")
                }
            }

            if(autoType == "extTmp") { 
                section("Turn Thermostat On/Off based on External Temp:") {
                    def qOpt = (settings?.extTmpModes || settings?.extTmpDays || (settings?.extTmpStartTime && settings?.extTmpStopTime)) ? "\nSchedule Options Selected..." : ""
                    def extTmpTstatMode = extTmpTstat ? "\nThermostat Mode: (${extTmpTstat?.currentThermostatOperatingState.toString()}/${extTmpTstat?.currentThermostatMode.toString()})" : ""
                    def extTmpTstatDesc = extTmpTstat ? "${extTmpTstat?.label}: (${getDeviceTemp(extTmpTstat)}°${atomicState?.tempUnit})" : ""
                    def extTmpSenUsedDesc = (!extTmpUseWeather && extTmpTempSensor && extTmpTstat) ? "\nUsing External Sensor: (${getExtTmpTemperature()}°${atomicState?.tempUnit})" : ""
                    def extTmpWeaUsedDesc = (extTmpUseWeather && !extTmpTempSensor && extTmpTstat) ? "\nUsing Weather: (${getExtTmpTemperature()}°${atomicState?.tempUnit})" : ""
                    def extTmpDiffDesc = extTmpDiffVal ? "\nTemp Difference Value: (${extTmpDiffVal}°${atomicState?.tempUnit})" : ""
                    def extTmpOffDesc = extTmpOffDelay ? "\nOff Delay: (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})" : ""
                    def extTmpOnDesc = extTmpOnDelay ? "\nOn Delay: (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})" : ""
                    def extTmpConfDesc = ((extTmpTempSensor || extTmpUseWeather) && extTmpTstat) ? "\n\nTap to Modify..." : ""
                    def extTmpDesc = isExtTmpConfigured() ? ("${extTmpTstatDesc}${extTmpTstatMode}${extTmpWeaUsedDesc}${extTmpSenUsedDesc}${extTmpDiffDesc}${extTmpOffDesc}${extTmpOnDesc}${qOpt}${extTmpConfDesc}") : null
                    href "extTempPage", title: "External Temps Config...", description: extTmpDesc ? extTmpDesc : "Tap to Configure...", state: (extTmpDesc ? "complete" : null), image: getAppImg("external_temp_icon.png")
                } 
            }

            if(autoType == "conWat") { 
                section("Turn Thermostat On/Off when a Door or Window is Opened:") {
                    def qOpt = (settings?.conWatModes || settings?.conWatDays || (settings?.conWatStartTime && settings?.conWatStopTime)) ? "\nSchedule Options Selected..." : ""
                    def conWatTstatDesc = conWatTstat ? "Thermostat Mode: (${conWatTstat?.currentThermostatOperatingState.toString()}/${conWatTstat?.currentThermostatMode.toString()})" : ""
                    def conWatUsedDesc = (conWatContacts && conWatTstat) ? "\nContacts: (${getConWatContactsOk() ? "Closed" : "Open"})" : ""
                    def conWatOffDesc = conWatOffDelay ? "\nOff Delay: (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})" : ""
                    def conWatOnDesc = conWatOnDelay ? "\nOn Delay: (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})" : ""
                    def conWatConfDesc = (conWatContacts && conWatTstat) ? "\n\nTap to Modify..." : ""
                    def conWatDesc = isConWatConfigured() ? ("${conWatTstatDesc}${conWatUsedDesc}${conWatOffDesc}${conWatOnDesc}${qOpt}${conWatConfDesc}") : null
                    href "contactWatchPage", title: "Contact Sensors Config...", description: conWatDesc ? conWatDesc : "Tap to Configure...", state: (conWatDesc ? "complete" : null), image: getAppImg("open_window.png")
                } 
            } 

            if(autoType == "nMode") {
                section("Set Nest Presence Based on ST Modes, Presence Sensor, or Switches:") {
                    def nModeLocDesc = isNestModesConfigured() ? "Nest Mode: ${getNestLocPres().toString().capitalize()}" : ""
                    def nModesDesc = ((!nModePresSensor && !nModeSwitch) && (nModeAwayModes && nModeHomeModes)) ? "\n${nModeHomeModes ? "Home Modes: ${nModeHomeModes.size()} selected" : ""}${nModeAwayModes ? "\nAway Modes: ${nModeAwayModes.size()} selected" : ""}" : ""
                    def nPresDesc = (nModePresSensor && !nModeSwitch) ? "\nUsing Presence: (${nModePresSensor?.currentPresence?.toString().replaceAll("\\[|\\]", "")})" : ""
                    def nSwtchDesc = (nModeSwitch && !nModePresSensor) ? "\nUsing Switch: (Power is: ${isSwitchOn(nModeSwitch) ? "ON" : "OFF"})" : ""
                    def nModeDelayDesc = nModeDelay && nModeDelayVal ? "\nDelay: ${getEnumValue(longTimeSecEnum(), nModeDelayVal)}" : ""
                    def nModeConfDesc = (nModePresSensor || nModeSwitch) || (!nModePresSensor && !nModeSwitch && (nModeAwayModes && nModeHomeModes)) ? "\n\nTap to Modify..." : ""
                    def nModeDesc = isNestModesConfigured() ? "${nModeLocDesc}${nModesDesc}${nPresDesc}${nSwtchDesc}${nModeDelayDesc}${nModeConfDesc}" : null
                    href "nestModePresPage", title: "Nest Mode Automation Config", description: nModeDesc ? nModeDesc : "Tap to Configure...", state: (nModeDesc ? "complete" : null), image: getAppImg("mode_automation_icon.png")
                } 
            } 
        }
    }
}

def namePage() {
    def type = atomicState?.automationType
    def typeLabel = ""
    if (type == "remSen") { typeLabel = " (Remote)" }
    else if (type == "extTmp") { typeLabel = " (External)" }
    else if (type == "conWat") { typeLabel = " (Contact)" }
    else if (type == "nMode") { typeLabel = " (Mode)" }
    dynamicPage(name: "namePage") {
        section("Automation name") {
            label title: "Name this Automation", defaultValue: "${app.label}${typeLabel}", required: true
            paragraph "Make sure to name it something that will help easily recognize it later."
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
    //sends notification of uninstall
    sendNotificationEvent("${textAppName()} is uninstalled...")
}

def initialize() {
    unschedule()
    unsubscribe()
    subscribeToEvents()
    automationsInst()
    scheduler()
    atomicState?.timeZone = !location?.timeZone ? parent?.getNestTimeZone() : null
    if(extTmpUseWeather && atomicState?.isExtTmpConfigured) { 
        updateWeather() 
    }
}

def automationsInst() {
    atomicState.isRemSenConfigured = isRemSenConfigured() ? true : false
    atomicState.isExtTmpConfigured = isExtTmpConfigured() ? true : false
    atomicState.isConWatConfigured = isConWatConfigured() ? true : false
    atomicState.isNestModesConfigured = isNestModesConfigured() ? true : false
}

def getAutomationType() {
    return atomicState?.automationType ? atomicState?.automationType : null
}

def subscribeToEvents() {
    //Remote Sensor Subscriptions 
    def autoType = atomicState?.automationType
    if (autoType == "remSen") {
        if((remSensorDay || remSensorNight) && remSenTstat) {
            //subscribe(location, remSenLocationEvt)
            if(remSenModes || remSensorDayModes || remSensorNightModes) { subscribe(location, "mode", remSenModeEvt, [filterEvents: false]) }
            if(remSensorDay) { subscribe(remSensorDay, "temperature", remSenTempSenEvt) }
            if(remSensorNight) { subscribe(remSensorNight, "temperature", remSenTempSenEvt) }
            if(remSenTstat) {
                subscribe(remSenTstat, "temperature", remSenTstatTempEvt)
                subscribe(remSenTstat, "thermostatMode", remSenTstatModeEvt)
                if(remSenTstatFanSwitch) {
                    subscribe(remSenTstatFanSwitch, "switch", remSenTstatSwitchEvt)
                    subscribe(remSenTstat, "thermostatFanMode", remSenTstatFanEvt)
                    if(remSenTstatSwitchRunType.toInteger() == 1) { subscribe(remSenTstat, "thermostatOperatingState", remSenTstatOperEvt) }
                }
            }
            if(remSenMotion) { subscribe(remSenMotion, "motionSensor", remSenMotionEvt) }
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
            subscribe(extTmpTstat, "thermostatMode", extTmpTstatEvt) 
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
def remSensorPage() {
    def pName = atomicState?.automationType
    dynamicPage(name: "remSensorPage", title: "Remote Sensor Automation", uninstall: false, nextPage: "mainPage") {
        if(atomicState?.remSenEnabled == null) { atomicState?.remSenEnabled = true }
        def req = (remSensorDay || remSensorNight || remSenTstat) ? true : false
        def dupTstat = checkThermostatDupe(remSenTstat, remSenTstatMir)
        def tStatHeatSp = getTstatSetpoint(remSenTstat, "heat")
        def tStatCoolSp = getTstatSetpoint(remSenTstat, "cool")
        def tStatMode = remSenTstat ? remSenTstat?.currentThermostatMode : "unknown"
        def tStatTemp = "${getDeviceTemp(remSenTstat)}°${atomicState?.tempUnit}"
        def locMode = location?.mode
        
        section("Select the Allowed (Rule) Action Type:") {
            if(!remSenRuleType) { 
                paragraph "(Rule) Actions will be used to determine what actions are taken when the temperature threshold is reached. Using combinations of Heat/Cool/Fan to help balance" + 
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
                    setRemSenTstatCapabilities()
                    paragraph "Current Temperature: (${tStatTemp})\nHeat/Cool Setpoints: (${tStatHeatSp}°${atomicState?.tempUnit}/${tStatCoolSp}°${atomicState?.tempUnit})\nCurrent Mode: (${tStatMode})", image: getAppImg("instruct_icon.png")
                    input "remSenTstatsMir", "capability.thermostat", title: "Mirror Actions to these Thermostats", multiple: true, submitOnChange: true, required: false, image: getAppImg("thermostat_icon.png")
                    if(remSenTstatsMir && !dupTstat) { 
                        remSenTstatsMir?.each { t ->
                            paragraph "Thermostat Temp: ${getDeviceTemp(t)}${atomicState?.tempUnit}", image: " "
                        }
                    }
                    input "remSenTstatFanSwitch", "capability.switch", title: "Turn On Fan or Switch while Thermostat/Fan is running?", required: false, submitOnChange: true, multiple: false,
                            image: getAppImg("fan_ventilation_icon.png")
                    if(remSenTstatFanSwitch) {
                        paragraph "Switch is (${remSenTstatFanSwitch?.currentSwitch?.toString().capitalize()})", image: getAppImg("blank_icon.png")
                        input(name: "remSenTstatSwitchRunType", type: "enum", title: "Turn On When?", defaultValue: 1, metadata: [values:switchRunEnum()],  
                                required: (remSenTstatFanSwitch ? true : false), submitOnChange: true, image: getAppImg("setting_icon.png"))
                    }
                    
                }
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
                        //paragraph " ", image: " "
                        def tmpVal = "$dSenStr Sensor Temp${(remSensorDay?.size() > 1) ? " (avg):" : ":"} ${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit}"
                        if(remSensorDay.size() > 1) {
                            href "remSensorTempsPage", title: "View $dSenStr Sensor Temps...", description: "${tmpVal}", image: getAppImg("blank_icon.png")
                            //paragraph "Multiple temp sensors will return the average of those sensors.", image: getAppImg("i_icon.png")
                        } else { paragraph "${tmpVal}", image: getAppImg("instruct_icon.png") }
                    }
                }
                if(remSensorDay && ((!remSenHeatTempsReq() || !remSenCoolTempsReq()) || (remSenDayHeatTemp && remSenDayCoolTemp))) {
                    section("(Optional) Choose a second set of Temperature Sensor(s) to use in the Evening instead of the Thermostat's...") {
                        input "remSensorNight", "capability.temperatureMeasurement", title: "Evening Temp Sensors", submitOnChange: true, required: false, multiple: true, image: getAppImg("temperature_icon.png")
                        if(remSensorNight) {
                            input "remSenNightHeatTemp", "decimal", title: "Desired Evening Heat Temp (°${atomicState?.tempUnit})", submitOnChange: true, required: ((remSensorNight && remSenHeatTempsReq()) ? true : false), image: getAppImg("heat_icon.png")
                            input "remSenNightCoolTemp", "decimal", title: "Desired Evening Cool Temp (°${atomicState?.tempUnit})", submitOnChange: true, required: ((remSensorNight && remSenCoolTempsReq()) ? true : false), image: getAppImg("cool_icon.png")
                            //paragraph " ", image: " "
                            def tmpVal = "Evening Sensor Temp${(remSensorNight?.size() > 1) ? " (avg):" : ":"} ${getDeviceTempAvg(remSensorNight)}°${atomicState?.tempUnit}"
                            if(remSensorNight.size() > 1) {
                                href "remSensorTempsPage", title: "View Evening Sensor Temps...", description: "${tmpVal}", image: getAppImg("blank_icon.png")
                                //paragraph "Multiple temp sensors will return the average temp of those sensors.", image: getAppImg("i_icon.png")
                            } else { paragraph "${tmpVal}", image: getAppImg("instruct_icon.png") }
                        }
                    }
                }
                if(remSensorDay && remSensorNight) {
                    section("Day/Evening Detection Options:") {
                        input "remSenUseSunAsMode", "bool", title: "Use Sunrise/Sunset instead of Modes?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("sunrise_icon.png")
                        if(remSenUseSunAsMode) {
                            getSunTimeState()
                            paragraph "Sunrise: ${atomicState.sunriseTm} | Sunset: ${atomicState.sunsetTm}", image: getAppImg("blank_icon.png")
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
                            input "remSenTimeBetweenRuns", "enum", title: "Delay Between Fan Runs?", required: true, defaultValue: 3600, metadata: [values:longTimeSecEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                        }
                    }
                    section("(Optional) Use Motion Sensors to Evaluate Temps:") {
                        input "remSenMotion", "capability.motionSensor", title: "Motion Sensors", required: false, multiple: true, submitOnChange: true, image: getAppImg("motion_icon.png")
                        if(remSenMotion) {
                            paragraph "Motion State: (${isMotionActive(remSenMotion) ? "Active" : "Not Active"})", image: " "
                            input "remSenMotionDelayVal", "enum", title: "Delay before evaluating?", required: true, defaultValue: 60, metadata: [values:longTimeSecEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                            input "remSenMotionModes", "mode", title: "Use Motion in these Modes...", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode_icon.png")
                        }
                    }
                    section("(Optional) Use Switch Event(s) to Evaluate Temps:") {
                        input "remSenSwitches", "capability.switch", title: "Select Switches", required: false, multiple: true, submitOnChange: true, image: getAppImg("wall_switch_icon.png")
                        if(remSenSwitches) { 
                            input "remSenSwitchOpt", "enum", title: "Event Type to Trigger?", required: true, defaultValue: 2, metadata: [values:switchEnumVals()], submitOnChange: true, image: getAppImg("settings_icon.png")
                        }
                    }
                    section ("Optional Settings:") {
                        paragraph "The Action Threshold Temp is the temperature difference used to trigger a selected action.", image: getAppImg("instruct_icon.png")
                        input "remSenTempDiffDegrees", "decimal", title: "Action Threshold Temp (°${atomicState?.tempUnit})", required: true, defaultValue: 1.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                        if(remSenRuleType != "Circ") {
                            paragraph "The Change Temp Increments are the amount the temp is adjusted +/- when an action requires a temp change.", image: getAppImg("instruct_icon.png")
                            input "remSenTempChgVal", "decimal", title: "Change Temp Increments (°${atomicState?.tempUnit})", required: true, defaultValue: 2.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                        }
                        input "remSenModes", "mode", title: "Only Evaluate Actions in these Modes?", multiple: true, required: false, submitOnChange: true, image: getAppImg("mode_icon.png")
                        input "remSenWaitVal", "number", title: "Wait Time between Evaluations (seconds)?", required: false, defaultValue: 60, submitOnChange: true, image: getAppImg("delay_time_icon.png")
                    }
                }
            }
        }
        if (atomicState?.isRemSenConfigured) {
            section("Enable or Disable Remote Sensor Once Configured...") {
                input (name: "remSenEnabled", type: "bool", title: "Enable Remote Sensor Automation?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("switch_icon.png"))
                atomicState?.remSenEnabled = remSenEnabled ? true : false
            }
        } else { atomicState?.remSenEnabled = false }
        
        section("Help:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"Tap to View...", image: getAppImg("help_icon.png")
        }
    }
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
    log.debug "remSenMotionEvt: Motion Sensor (${evt?.displayName}) Motion is (${evt?.value})"
    if(atomicState?.remSenEnabled == false) { return }
    else if (remSenUseSunAsMode) { return}
    else {
        if(remSenMotionModes) {
            if(isInMode(remSenMotionModes)) {
                runIn(remSenMotionDelayVal.toInteger(), "remSenCheckMotion", [overwrite: true])
            }
        } else {
            runIn(remSenMotionDelayVal.toInteger(), "remSenCheckMotion", [overwrite: true])
        }
    }
}

def remSenTempSenEvt(evt) {
    //log.trace "remSenTempSenEvt: Remote Sensor (${evt?.displayName}) Temp is (${evt?.value}°${atomicState?.tempUnit})"
    if(atomicState?.remSenEnabled == false) { return }
    else { remSenEvtEval() }
}

def remSenTstatTempEvt(evt) {
    log.trace "remSenTstatTempEvt: Thermostat (${evt?.displayName}) Temp is (${evt?.value}°${atomicState?.tempUnit})"
    if(atomicState?.remSenEnabled == false) { return }
    else { remSenEvtEval() }
}

def remSenTstatModeEvt(evt) {
    log.trace "remSenTstatModeEvt: Thermostat (${evt?.displayName}) Mode is (${evt?.value.toString().toUpperCase()})"
    //if(atomicState?.remSenEnabled == false) { return }
    //else { remSenEvtEval() }
}

def remSenTstatSwitchEvt(evt) {
    log.trace "remSenTstatSwitchEvt: Thermostat Switch (${evt?.displayName}) is (${evt?.value})"
    
}

def remSenTstatFanEvt(evt) {
    log.trace "remSenTstatFanEvt: Thermostat (${evt?.displayName}) Fan is (${evt?.value})"
    def isFanOn = (evt?.value == "on") ? true : false
    if(!atomicState?.remSenEnabled) { return }
    else { 
        if(remSenTstatFanSwitch && (remSenTstatSwitchRunType.toInteger() == 1 || remSenTstatSwitchRunType.toInteger() == 2)) {
            def swOn = remSenTstatFanSwitch?.currentSwitch.toString() == "on" ? true : false
            if(isFanOn) {
                if(!swOn) { 
                    LogAction("remSenTstatFanEvt: Thermostat (${evt?.displayName}) Fan is (${evt?.value.toString().toUpperCase()}) | Turning '${remSenTstatFanSwitch}' Switch (ON)", "info", true)
                    remSenTstatFanSwitch*.on() 
                }
            }
            else {
                if(swOn) { 
                    LogAction("remSenTstatFanEvt: Thermostat (${evt?.displayName}) Fan is (${evt?.value.toString().toUpperCase()}) | Turning '${remSenTstatFanSwitch}' Switch (OFF)", "info", true)
                    remSenTstatFanSwitch*.off() 
                }
            }
        }
    }
}

def remSenTstatOperEvt(evt) {
    log.trace "remSenTstatOperEvt: Thermostat OperatingState is  (${evt?.value})"
    def isTstatIdle = (evt?.value == "idle") ? true : false
    
    if(!atomicState?.remSenEnabled) { return }
    else { 
        if(remSenTstatFanSwitch && remSenTstatSwitchRunType.toInteger() == 1) {
            
            def swOn = remSenTstatFanSwitch?.currentSwitch.toString() == "on" ? true : false
            if(!isTstatIdle) {
                if(!swOn) { 
                    LogAction("remSenTstatOperEvt: Thermostat (${evt?.displayName}) OperatingState is (${evt?.value.toString().toUpperCase()}) | Turning '${remSenTstatFanSwitch}' Switch (ON)", "info", true)
                    remSenTstatFanSwitch*.on() 
                }
            }
            else {
                if(swOn) { 
                    LogAction("remSenTstatOperEvt: Thermostat (${evt?.displayName}) OperatingState is (${evt?.value.toString().toUpperCase()}) | Turning '${remSenTstatFanSwitch}' Switch (OFF)", "info", true)
                    remSenTstatFanSwitch*.off() 

                }
            }
        }
    }
}

def remSenSunEvtHandler(evt) {
    if(remSenUseSunAsMode) { 
        getSunTimeState() 
        remSenEvtEval() 
    } else { return }
}

def remSenSwitchEvt(evt) {
    def evtType = evt?.value.toString()
    if(remSenSwitches) {
        def opt = remSenSwitchOpt?.toInteger()
        switch(opt) {
            case 0:
                if(evtType == "off") { remSenEvtEval() }
                break
            case 1:
                if (evtType == "on") { remSenEvtEval() }
                break
            case 2:
                if(evtType in ["on", "off"]) { remSenEvtEval() }
            default:
                LogAction("remSenSwitchEvt: Invalid Option Received...", "warn", true)
            break
        }
    }
}

def remSenModeEvt(evt) {
    log.debug "remSenModeEvt: Mode is (${evt?.value})"
    remSenEvtEval()
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
                remSensorDay?.each { t ->
                    paragraph "${t?.label}: ${getDeviceTemp(t)}°${atomicState?.tempUnit}", image: getAppImg("temperature_icon.png")
                }
            }
            section("Average Temp of $dSenStr Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(remSensorDay)}°${atomicState?.tempUnit}", image: getAppImg("instruct_icon.png")
            }
        }
        if(remSensorNight) { 
            section("Night Sensor Temps:") {
                remSensorNight?.each { ts ->
                    paragraph "${ts?.label}: ${getDeviceTemp(ts)}°${atomicState?.tempUnit}", image: getAppImg("temperature_icon.png")
                }
            }
            section("Average Temp of Night Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(remSensorNight)}°${atomicState?.tempUnit}", image: getAppImg("instruct_icon.png")
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

def getLastRemSenEvalSec() { return !atomicState?.lastRemSenEval ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenEval).toInteger() }

// Initially based off of Keep Me Cozy II
private remSenEvtEval() {
    //log.trace "remSenEvtEval....."
    if(remSenUseSunAsMode) { getSunTimeState() }
    if(getLastRemSenEvalSec() < (remSenWaitVal ? remSenWaitVal?.toInteger() : 60)) { 
        log.debug "Too Soon to Evaluate..."
        return 
    } 
    else { 
        atomicState?.lastRemSenEval = getDtNow()
        if (atomicState?.remSenEnabled && modesOk(remSenModes) && (remSensorDay || remSensorNight) && remSenTstat && getRemSenModeOk()) {
            def threshold = !remSenTempDiffDegrees ? 0 : remSenTempDiffDegrees.toDouble()
            def tempChangeVal = !remSenTempChgVal ? 0 : remSenTempChgVal.toDouble()
            def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null
            def curTstatTemp = getDeviceTemp(remSenTstat).toDouble()
            def curTstatOperState = remSenTstat?.currentThermostatOperatingState.toString()
            def curTstatFanMode = remSenTstat?.currentThermostatFanMode.toString()
            def curCoolSetpoint = getTstatSetpoint(remSenTstat, "cool")
            def curHeatSetpoint = getTstatSetpoint(remSenTstat, "heat")
            def remSenHtemp = getRemSenHeatSetTemp()
            def remSenCtemp = getRemSenCoolSetTemp()
            def curSenTemp = (remSensorDay || remSensorNight) ? getRemoteSenTemp().toDouble() : null
            
            log.trace "Remote Sensor Rule Type: ${remSenRuleType}"
            log.trace "Remote Sensor Temp: ${curSenTemp}"
            log.trace "Thermostat Info - ( Temperature: ($curTstatTemp) | HeatSetpoint: ($curHeatSetpoint) | CoolSetpoint: ($curCoolSetpoint) | HvacMode: ($hvacMode) | OperatingState: ($curTstatOperState) | FanMode: ($curTstatFanMode) )" 
            log.trace "Desired Temps - Heat: $remSenHtemp | Cool: $remSenCtemp"
            log.trace "Threshold Temp: $remSenTempDiffDegrees | Change Temp Increments: $remSenTempChgVal"
            
            if(hvacMode == "off") { return }
            
            else if (hvacMode in ["cool","auto"]) {
                if ((curSenTemp - remSenCtemp) >= threshold) {
                    if(remSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "COOL - Setting CoolSetpoint to (${(curTstatTemp - tempChangeVal)}°${atomicState?.tempUnit})"
                        remSenTstat?.setCoolingSetpoint(curTstatTemp - tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMir*.setCoolingSetpoint(curTstatTemp - tempChangeVal) }
                        log.debug "remSenTstat.setCoolingSetpoint(${curTstatTemp - tempChangeVal}), ON"
                    }
                }
                else if (((remSenCtemp - curSenTemp) >= threshold) && ((curTstatTemp - curCoolSetpoint) >= threshold)) {
                    if(remSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "COOL - Setting CoolSetpoint to (${(curTstatTemp + tempChangeVal)}°${atomicState?.tempUnit})"
                        remSenTstat?.setCoolingSetpoint(curTstatTemp + tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMirror*.setCoolingSetpoint(curTstatTemp - tempChangeVal) }
                        log.debug "remSenTstat.setCoolingSetpoint(${curTstatTemp + tempChangeVal}), OFF"
                    }
                } else {
                    //log.debug "FAN(COOL): $remSenRuleType | RuleOk: (${remSenRuleType in ["Circ", "Cool_Circ", "Heat_Cool_Circ"]})"
                    //log.debug "FAN(COOL): DiffOK (${getFanTempOk(curSenTemp, remSenCtemp, curCoolSetpoint, threshold)})"
                    if(remSenRuleType in ["Circ", "Cool_Circ", "Heat_Cool_Circ"]) {
                        if( getRemSenFanTempOk(curSenTemp, remSenCtemp, curCoolSetpoint, threshold) && getRemSenFanRunOk(curTstatOperState, curTstatFanMode) ) {
                            log.debug "Running $remSenTstat Fan for COOL Circulation..."
                            remSenTstat?.fanOn()
                            if(remSenTstatsMir) { 
                                remSenTstatsMir.each { mt -> 
                                    log.debug "Mirroring $mt Fan Run for COOL Circulation..."
                                    mt?.fanOn() 
                                }
                            }
                            atomicState?.lastRemSenFanRunDt = getDtNow()
                        }
                    }
                }
            }
            //Heat Functions....
            else if (hvacMode in ["heat", "emergency heat", "auto"]) {
                if ((remSenHtemp - curSenTemp) >= threshold) {
                    if(remSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) { 
                        log.debug "HEAT - Setting HeatSetpoint to (${(curTstatTemp + tempChangeVal)}°${atomicState?.tempUnit})"
                        remSenTstat?.setHeatingSetpoint(curTstatTemp + tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMir*.setHeatingSetpoint(curTstatTemp + tempChangeVal) }
                        log.debug "remSenTstat.setHeatingSetpoint(${curTstatTemp + tempChangeVal}), ON"
                    }
                }
                else if (((curSenTemp - remSenHtemp) >= threshold) && ((curHeatSetpoint - curTstatTemp) >= threshold)) {
                    if(remSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "HEAT - Setting HeatSetpoint to (${(curTstatTemp - tempChangeVal)}°${atomicState?.tempUnit})"
                        remSenTstat?.setHeatingSetpoint(curTstatTemp - tempChangeVal)
                        if(remSenTstatsMirror) { remSenTstatsMirror*.setHeatingSetpoint(curTstatTemp - tempChangeVal) }
                        log.debug "remSenTstat.setHeatingSetpoint(${curTstatTemp - tempChangeVal}), OFF"
                    }
                } else { 
                    //log.debug "FAN(HEAT): $remSenRuleType | RuleOk: (${remSenRuleType in ["Circ", "Heat_Circ", "Heat_Cool_Circ"]})"
                    //log.debug "FAN(HEAT): DiffOK (${getFanTempOk(curSenTemp, remSenHtemp, curHeatSetpoint, threshold)})"
                    if (remSenRuleType in ["Circ", "Heat_Circ", "Heat_Cool_Circ"]) {
                        if( getRemSenFanTempOk(curSenTemp, remSenHtemp, curHeatSetpoint, threshold) && getRemSenFanRunOk(curTstatOperState, curTstatFanMode) ) {
                            log.debug "Running $remSenTstat Fan for HEAT Circulation..."
                            remSenTstat?.fanOn()
                            if(remSenTstatsMir) { 
                                remSenTstatsMir.each { mt -> 
                                    log.debug "Mirroring $mt Fan Run for HEAT Circulation..."
                                    mt?.fanOn() 
                                }
                            }
                            atomicState?.lastRemSenFanRunDt = getDtNow()
                        }
                    }
                }
            } else { log.warn "remSenEvtEval: Did not receive a valid Thermostat Mode..." }
        }
        else {
            def remSenHtemp = getRemSenHeatSetTemp()
            def remSenCtemp = getRemSenCoolSetTemp()
            remSenTstat?.setHeatingSetpoint(remSenHtemp)
            remSenTstat?.setCoolingSetpoint(remSenCtemp)
            if(remSenTstatsMir) {
                remSenTstatsMir*.setHeatingSetpoint(remSenHtemp)
                remSenTstatsMirror*.setCoolingSetpoint(remSenCtemp)
            }
        }
    }
}

def getRemSenFanTempOk(Double senTemp, Double userTemp, Double curTemp, Double threshold) {
    def diff1 = (Math.abs(senTemp - userTemp)?.round(1) < threshold)
    def diff2 = (Math.abs(userTemp - curTemp)?.round(1) < threshold)
    log.debug "getRemSenFanTempOk: ( Sensor Temp - Set Temp: (${Math.abs(senTemp - userTemp).round(1)}) < Threshold Temp: (${threshold}) ) - ($diff1)"
    log.debug "getRemSenFanTempOk: ( Set Temp - Current Temp: (${Math.abs(userTemp - curTemp).round(1)}) < Threshold Temp: (${threshold}) ) - ($diff2)"
    return (diff1 && diff2) ? true : false
}

def getRemSenModeOk() {
    if(remSenUseSunAsMode) { return true }
    else if (remSensorDay && (!remSensorDayModes && !remSensorNight && !remSensorNightModes)) {
        return true
    }
    else if (remSensorDayModes || remSensorNightModes) {
        return (isInMode(remSensorDayModes) || isInMode(remSensorNightModes)) ? true : false
    } 
    else {
        return false
    }
}

def getRemSenFanRunOk(operState, fanState) { 
    log.trace "getRemSenFanRunOk($operState, $fanState)"
    def val = remSenTimeBetweenRuns ? remSenTimeBetweenRuns?.toInteger() : 3600
    def cond = ((remSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) && operState == "idle" && fanState == "auto") ? true : false
    def timeSince = (getLastRemSenFanRunDtSec() > val)
    def result = (timeSince && cond) ? true : false
    log.debug "getRemSenFanRunOk(): cond: $cond | timeSince: $timeSince | val: $val | $result"
    return result
}

def getLastRemSenFanRunDtSec() { return !atomicState?.lastRemSenFanRunDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenFanRunDt).toInteger() }

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

def setRemSenTstatCapabilities() {
    try {
        def canCool = true
        def canHeat = true
        def hasFan = true
        if(remSenTstat) { 
            canCool = (remSenTstat?.currentCanCool == "true") ? true : false
            canHeat = (remSenTstat?.currentCanHeat == "true") ? true : false
            hasFan = (remSenTstat?.currentHasFan == "true") ? true : false
        }
        atomicState?.remSenTstatCanCool = canCool
        atomicState?.remSenTstatCanHeat = canHeat
        atomicState?.remSenTstatHasFan = hasFan
    } catch (e) { }
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


/********************************************************************************  
|                			EXTERNAL TEMP AUTOMATION CODE	     				|
*********************************************************************************/
def extTmpPrefix() { return "extTmp" }

def extTempPage() {
    def pName = extTmpPrefix()
    dynamicPage(name: "extTempPage", title: "Thermostat/External Temps Automation", uninstall: false, nextPage: "mainPage") {
        section("External Temps to use to Turn off the Thermostat Below:") {
            input "extTmpUseWeather", "bool", title: "Use Local Weather as External Sensor?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("weather_icon.png")
            if(extTmpUseWeather){
                getExtConditions()
                def wReq = (extTmpTstat && !extTmpTempSensor) ? true : false
                def tmpVal = (location?.temperatureScale == "C") ? atomicState?.curWeatherTemp_c : atomicState?.curWeatherTemp_f
                paragraph "Current Weather Temp: ${tmpVal}°${atomicState?.tempUnit}", image: getAppImg("blank_icon.png")
                input name: "extTmpWeatherUpdateVal", type: "enum", title: "Update Weather (in Minutes)?", defaultValue: 15, metadata: [values:longTimeMinEnum()], submitOnChange: true, required: wReq,
                        image: getAppImg("reset_icon.png")
            }
            if(!extTmpUseWeather) {
                def senReq = (!extTmpUseWeather && extTmpTstat) ? true : false
                input "extTmpTempSensor", "capability.temperatureMeasurement", title: "Which Outside Temp Sensor?", submitOnChange: true, multiple: false, required: senReq, 
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
                    def tmpVal = "${extTmpTstat?.currentValue("temperature").toString()}"
                    paragraph "Current Thermostat Temp: ${tmpVal}°${atomicState?.tempUnit}", image: getAppImg("blank_icon.png")
                    input name: "extTmpDiffVal", type: "decimal", title: "When Thermostat temp is within this many degrees of the external temp (°${atomicState?.tempUnit})?", defaultValue: 1.0, submitOnChange: true, required: true,
                            image: getAppImg("temp_icon.png")
                }
            }
        }
        if((extTmpUseWeather || extTmpTempSensor) && extTmpTstat) {
            section("Delay Values:") {
                input name: "extTmpOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                input name: "extTmpRestoreMode", type: "bool", title: "Restore mode when Temp is below Threshold?", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("restore_icon.png")
                if(extTmpRestoreMode) {
                    input name: "extTmpOnDelay", type: "enum", title: "Delay Restore (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                }
            }
            section("Only Act During these Days, Times, or Modes:") {
                def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc != "Tap to Configure..." ? "complete" : null),
                		image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                input "extTmpPushMsgOn", "bool", title: "Send Push Notifications on Changes?", description: "", required: false, defaultValue: true, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
                if(extTmpPushMsgOn) {
                    def notifDesc = ((settings?."${pName}NotifRecips") || (settings?."${pName}NotifRecips" || settings?."${pName}NotifPhones")) ? 
                            "${getRecipientsNames(settings?."${pName}NotifRecips")}\n\nTap to Modify..." : "Tap to configure..."
                    href "setRecipientsPage", title: "(Optional) Select Recipients", description: notifDesc, params: [pName: "${pName}"], state: (notifDesc != "Tap to Configure..." ? "complete" : null),
                            image: getAppImg("notification_opt_icon.png")
                }
            }
        }
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
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
    if (isExtTmpConfigured()) { extTmpTempCheck() }
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
    } catch (ex) { LogAction("getExtTmpTempOk Exception: ${ex}", "error", true) }
}

def extTmpTimeOk() {
    try {
        def strtTime = null
        def stopTime = null
        def now = new Date()
        if(settings?.extTmpStartTime && settings?.extTmpStopTime) { 
            if(settings?.extTmpStartTime) { strtTime = settings?.extTmpStartTime }
            if(settings?.extTmpStopTime) { stopTime = settings?.extTmpStopTime }
        } else { return true }  
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), getTimeZone()) ? false : true
        } else { return true }
    } catch (ex) { LogAction("extTmpTimeOk Exception: ${ex}", "error", true) }
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

def extTmpScheduleOk() { return ((!extTmpModes || isInMode(extTmpModes)) && daysOk(settings?.extTmpDays) && extTmpTimeOk()) ? true : false }
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
    if(extTmpTempOk()) {
        if(modeOff && extTmpRestoreMode && atomicState?.extTmpTstatTurnedOff) {
            if(getExtTmpGoodDtSec() >= (getExtTmpOnDelayVal() - 5)) {
                def lastMode = atomicState?.extTmpRestoreMode ? atomicState?.extTmpRestoreMode : curMode
                if(lastMode && (atomicState?.extTmpRestoreMode != curMode)) {
                    if(setTstatMode(extTmpTstat, lastMode)) {
                        //atomicState.extTmpTstatTurnedOff = false
                        atomicState?.extTmpTstatOffRequested = false
                        runIn(20, "extTmpFollowupCheck", [overwrite: true])
                        LogAction("Restoring '${extTmpTstat?.label}' to '${lastMode.toUpperCase()}' Mode because External Temp has been above the Threshhold for (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})...", "info", true)
                        if(extTmpPushMsgOn) {
                            sendNofificationMsg("Restoring '${extTmpTstat?.label}' to '${lastMode.toUpperCase()}' Mode because External Temp has been above the Threshhold for (${getEnumValue(longTimeSecEnum(), extTmpOnDelay)})...", "Info", 
                                    extTmpNotifRecips, extTmpNotifPhones, extTmpUsePush)
                        }
                    } else { LogAction("extTmpTempCheck() | lastMode was not found...", "error", true) }
                }
            }
        } 
    }
    if (!extTmpTempOk()) {
        if(!modeOff) {
            if(getExtTmpBadDtSec() >= (getExtTmpOffDelayVal() - 2)) {
                if(extTmpRestoreMode) { 
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
                    sendNofificationMsg("${extTmpTstat?.label} has been turned 'Off' because External Temp is at the temp threshold for (${getEnumValue(longTimeSecEnum(), extTmpOffDelay)})!!!", "Info", extTmpNotifRecips, extTmpNotifPhones, extTmpUsePush)
                }
            }
        } else { LogAction("extTmpTempCheck() | No change made because ${extTmpTstat?.label} is already 'Off'", "info", true) }
    }
}

def extTmpTstatEvt(evt) {
    //log.trace "extTmpTstatEvt... ${evt.value}"
    def modeOff = (evt?.value == "off") ? true : false
    if(!modeOff) { atomicState?.extTmpTstatTurnedOff = false }
}

def extTmpFollowupCheck() {
    log.trace "extTmpFollowupCheck..."
    def curMode = extTmpTstat?.currentThermostatMode.toString()
    def modeOff = (curMode == "off") ? true : false
    def extTmpTstatReqOff = atomicState?.extTmpTstatOffRequested ? true : false
    if (modeOff != extTmpTstatReqOff) { extTmpCheck() }
}

def extTmpTempEvt(evt) {
    //log.debug "extTmpTempEvt: ${evt?.value}"
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

/******************************************************************************  
|                			WATCH CONTACTS AUTOMATION CODE	                  |
*******************************************************************************/
def conWatPrefix() { return "conWat" }

def contactWatchPage() {
    def pName = conWatPrefix()
    dynamicPage(name: "contactWatchPage", title: "Thermostat/Contact Automation", uninstall: false, nextPage: "mainPage") {
        def dupTstat = checkThermostatDupe(conWatTstat, conWatTstatMir)
        section("When These Contacts are open, Turn Off this Thermostat") {
            def req = (conWatContacts || conWatTstat) ? true : false
            input name: "conWatContacts", type: "capability.contactSensor", title: "Which Contact(s)?", multiple: true, submitOnChange: true, required: req,
                    image: getAppImg("contact_icon.png")
            input name: "conWatTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: getAppImg("thermostat_icon.png")
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

                input name: "conWatRestoreOnClose", type: "bool", title: "Restore previous mode when Closed?", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("restore_icon.png")
                if(conWatRestoreOnClose) {
                    input name: "conWatOnDelay", type: "enum", title: "Delay Restore (in minutes)", defaultValue: 300, metadata: [values:longTimeSecEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                }
            }
            section("Only Act During these Days, Times, or Modes:") {
            	def pageDesc = getDayModeTimeDesc(pName)
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: pageDesc, params: [pName: "${pName}"], state: (pageDesc != "Tap to Configure..." ? "complete" : null), 
                		image: getAppImg("cal_filter_icon.png")
            }
            section("Notifications:") {
                input name: "conWatPushMsgOn", type: "bool", title: "Send Push Notifications on Changes?", description: "", required: false, defaultValue: true, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
                if(conWatPushMsgOn) {
                    def notifDesc = ((settings?."${pName}NotifRecips") || (settings?."${pName}NotifRecips" || settings?."${pName}NotifPhones")) ? 
                            "${getRecipientsNames(settings?."${pName}NotifRecips")}\n\nTap to Modify..." : "Tap to configure..."
                    href "setRecipientsPage", title: "(Optional) Select Recipients", description: notifDesc, params: [pName: "${pName}"], state: (notifDesc != "Tap to Configure..." ? "complete" : null),
                            image: getAppImg("notification_opt_icon.png")
                }
            }
        }
        section("Help:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"Tap to View...", image: getAppImg("help_icon.png")
        }
    }
}

def isConWatConfigured() {
    def devOk = (conWatContacts && conWatTstat) ? true : false
    return devOk
}

def conWatTimeOk() {
    try {
        def strtTime = null
        def stopTime = null
        def now = new Date()
        if(settings?.conWatStartTime && settings?.conWatStopTime) { 
            if(settings?.conWatStartTime) { strtTime = settings?.conWatStartTime }
            if(settings?.conWatStopTime) { stopTime = settings?.conWatStopTime }
        } else { return true } 
        
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), getTimeZone()) ? false : true
        } else { return true }
    } catch (ex) { LogAction("conWatTimeOk Exception: ${ex}", "error", true, true) }
}

def getConWatContactsOk() { return conWatContacts?.currentState("contact")?.value.contains("open") ? false : true }
def conWatContactOk() { return (!conWatContacts && !conWatTstat) ? false : true }
def conWatScheduleOk() { return ((!conWatModes || isInMode(conWatModes)) && daysOk(conWatDays) && conWatTimeOk()) ? true : false }
def getConWatOpenDtSec() { return !atomicState?.conWatOpenDt ? 100000 : GetTimeDiffSeconds(atomicState?.conWatOpenDt).toInteger() }
def getConWatCloseDtSec() { return !atomicState?.conWatCloseDt ? 100000 : GetTimeDiffSeconds(atomicState?.conWatCloseDt).toInteger() }
def getConWatOffDelayVal() { return !conWatOffDelay ? 300 : (conWatOffDelay.toInteger()) }
def getConWatOnDelayVal() { return !conWatOnDelay ? 300 : (conWatOnDelay.toInteger()) }

def conWatCheck() {
    //log.trace "conWatCheck..."
    def curMode = conWatTstat.currentState("thermostatMode").value.toString()
    def curNestPres = (getNestLocPres() == "home") ? "present" : "not present" //Use this to determine if thermostat can be turned back on.

    def modeOff = (curMode == "off") ? true : false
    def lastMode = atomicState?.conWatRestoreMode ? atomicState?.conWatRestoreMode : curMode
    def openCtDesc = getOpenContacts(conWatContacts) ? " '${getOpenContacts(conWatContacts)?.join(", ")}' " : " a selected contact "
    //log.debug "curMode: $curMode | modeOff: $modeOff | conWatRestoreOnClose: $conWatRestoreOnClose | lastMode: $lastMode"
    //log.debug "conWatTstatTurnedOff: ${atomicState?.conWatTstatTurnedOff} | getConWatCloseDtSec(): ${getConWatCloseDtSec()}"
    if(getConWatContactsOk()) {
        if(modeOff && conWatRestoreOnClose && atomicState?.conWatTstatTurnedOff) {
            if(getConWatCloseDtSec() >= (getConWatOnDelayVal() - 5)) {
                if(lastMode && atomicState?.conWatRestoreMode != curMode) {
                    if(setTstatMode(conWatTstat, lastMode)) {
                        atomicState?.conWatTstatOffRequested = false
                        //atomicState.conWatTstatTurnedOff = false
                        if(conWatTstatMir) { 
                            conWatTstatMir.each { t ->
                                 setTstatMode(t, lastMode)
                                //log.debug("Restoring ${lastMode} to ${t}")
                            }
                        }
                        runIn(20, "conWatFollowupCheck", [overwrite: true])
                        LogAction("Restoring '${conWatTstat.label}' to '${lastMode.toString().toUpperCase()}' Mode because ALL contacts have been 'Closed' again for (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})...", "info", true)
                        if(conWatPushMsgOn) {
                            sendNofificationMsg("Restoring '${conWatTstat?.label}' to '${lastMode.toString().toUpperCase()}' Mode because ALL contacts have been 'Closed' again for (${getEnumValue(longTimeSecEnum(), conWatOnDelay)})...", "Info", 
                                    conWatNotifRecips, conWatNotifPhones, conWatUsePush)
                        }
                    }
                    else { LogAction("conWatCheck() | lastMode was not found...", "error", true) }
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
                log.debug("${openCtDesc} are Open: Turning off ${conWatTstat}")
                atomicState?.conWatTstatTurnedOff = true
                atomicState?.conWatTstatOffRequested = true
                conWatTstat?.off()
                if(conWatTstatMir) { 
                    conWatTstatMir.each { t ->
                        t.off()
                        log.debug("Mirrored Off to ${t}")
                    }
                }
                runIn(20, "conWatFollowupCheck", [overwrite: true])
                LogAction("conWatCheck: '${conWatTstat.label}' has been turned off because${openCtDesc}has been Opened for (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})...", "warning", true)
                if(conWatPushMsgOn) {
                    sendNofificationMsg("'${conWatTstat.label}' has been turned off because${openCtDesc}has been Opened for (${getEnumValue(longTimeSecEnum(), conWatOffDelay)})...", "Info", conWatNotifRecips, conWatNotifPhones, conWatUsePush)
                }
            }
        } else { LogAction("conWatCheck() | Skipping change because '${conWatTstat?.label}' Mode is already 'OFF'", "info", true) }
    }
}

def conWatFollowupCheck() {
    log.trace "conWatFollowupCheck..."
    def curMode = conWatTstat?.currentThermostatMode.toString()
    def modeOff = (curMode == "off") ? true : false
    def conWatTstatReqOff = atomicState?.conWatTstatOffRequested ? true : false
    if (modeOff != conWatTstatReqOff) { conWatCheck() }
}

def conWatTstatModeEvt(evt) {
    log.trace "conWatTstatModeEvt... ${evt?.value}"
    def modeOff = (evt?.value == "off") ? true : false
    if(!modeOff) { atomicState?.conWatTstatTurnedOff = false }
}

def conWatContactEvt(evt) {
    //log.debug "conWatContactEvt: ${evt?.value}"
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

/********************************************************************************  
|                			MODE AUTOMATION CODE	     						|
*********************************************************************************/
def nModePrefix() { return "nMode" }

def nestModePresPage() {
    def pName = nModePrefix()
    dynamicPage(name: "nestModePresPage", title: "Mode - Nest Home/Away Automation", uninstall: false, nextPage: "mainPage") {
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
            }
        }
        if(!nModeSwitch) {
            section("(Optional) Set Nest Presence using Presence Sensor:") {
                //paragraph "Choose a Presence Sensor(s) to use to set your Nest to Home/Away", image: getAppImg("instruct_icon")
                input "nModePresSensor", "capability.presenceSensor", title: "Select a Presence Sensor", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("presence_icon.png")
                if(nModePresSensor) {
                    if (nModePresSensor.size() > 1) {
                        paragraph "Nest will be set 'Away' when all Presence sensors leave and will return to 'Home' arrive", image: getAppImg("instruct_icon.png")
                    }
                    paragraph "Presence State: ${nModePresSensor.currentPresence}", image: " "
                }
            }
        }
        if(!nModePresSensor) {
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
            section("Notifications:") {
                input "nModePushMsgOn", "bool", title: "Send Push Notifications on Changes?", description: "", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
                if(nModePushMsgOn) {
                    def notifDesc = ((settings?."${pName}NotifRecips") || (settings?."${pName}NotifPhones" || settings?."${pName}NotifUsePush")) ? 
                            "${getRecipientsNames(settings?."${pName}NotifRecips")}\n\nTap to Modify..." : "Tap to configure..."
                    href "setRecipientsPage", title: "(Optional) Select Recipients", description: notifDesc, params: [pName: "${pName}"], state: (notifDesc != "Tap to Configure..." ? "complete" : null),
                            image: getAppImg("notification_opt_icon.png")
                }
            }
        }
        section("Help:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"Tap to View...", image: getAppImg("help_icon.png")
        }
    }
}

def isNestModesConfigured() {
    def devOk = ((!nModePresSensor && !nModeSwitch && (nModeHomeModes && nModeAwayModes)) || (nModePresSensor && !nModeSwitch) || (!nModePresSensor && nModeSwitch)) ? true : false
    return devOk
}

def nModeModeEvt(evt) { 
    log.debug "nModeModeEvt: Mode is (${evt?.value})"
    if(!nModePresSensor && !nModeSwitch) {
        if(nModeDelay) {
            LogAction("nModeWatcherEvt: Mode is ${evt?.value} | A Mode Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
            runIn( nModeDelayVal.toInteger(), "checkNestMode", [overwrite: true] )
        } else {
            checkNestMode()
        }
    } 
}

def nModePresEvt(evt) {
    log.trace "nModePresEvt: Presence is (${evt?.value})"
    if(nModeDelay) {
        LogAction("nModePresEvt: ${!evt ? "A monitored presence device is " : "SWITCH '${evt?.displayName}' is "} (${evt?.value.toString().toUpperCase()}) | A Presence Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
        runIn( nModeDelayVal.toInteger(), "checkNestMode", [overwrite: true] )
    } else {
        checkNestMode()
    }
}

def nModeSwitchEvt(evt) {
    log.trace "nModeSwitchEvt: Switch (${evt?.displayName}) is (${evt?.value.toString().toUpperCase()})"
    if(nModeSwitch && !nModePresSensor) {
        if(nModeDelay) {
            LogAction("nModeSwitchEvt: ${!evt ? "A monitored switch is " : "Switch (${evt?.displayName}) is "} (${evt?.value.toString().toUpperCase()}) | A Switch Check is scheduled for (${getEnumValue(longTimeSecEnum(), nModeDelayVal)})", "info", true)
            runIn( nModeDelayVal.toInteger(), "checkNestMode", [overwrite: true] )
        } else {
            checkNestMode()
        }
    }
}

def nModeFollowupCheck() {
    def nestModeAway = (getNestLocPres() == "home") ? false : true
    def nModeAwayState = atomicState?.nModeTstatLocAway
    if(nestModeAway && !nModeAwayState) { checkNestMode() }
}

def checkNestMode() {
    //log.trace "checkNestMode..."
    try {
        def curStMode = location?.mode?.toString()
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
            LogAction("$awayDesc Nest 'Away'", "info", true)
            atomicState?.nModeTstatLocAway = true
            parent?.setStructureAway(null, true) 
            if(nModePushMsgOn) {
                sendNofificationMsg("$awayDesc Nest 'Away", "Info", nModeNotifRecips, nModeNotifPhones, nModeUsePush)
            }
            runIn(20, "nModeFollowupCheck", [overwrite: true])
        }
        else if (home) {
            LogAction("$homeDesc Nest 'Home'", "info", true)
            atomicState?.nModeTstatLocAway = false
            parent?.setStructureAway(null, false) 
            if(nModePushMsgOn) {
                sendNofificationMsg("$homeDesc Nest 'Home", "Info", nModeNotifRecips, nModeNotifPhones, nModeUsePush)
            }
            runIn(20, "nModeFollowupCheck", [overwrite: true])
        } 
        else {
            LogAction("checkNestMode: Conditions are not valid to change mode | isPresenceHome: (${nModePresSensor ? "${isPresenceHome(nModePresSensor)}" : "Presence Not Used"}) | ST-Mode: ($curStMode) | NestModeAway: ($nestModeAway) | Away?: ($away) | Home?: ($home)", "info", true)
        }
    } catch (ex) { LogAction("checkNestMode Exception: (${ex})", "error", true) }
    
}

def getNestLocPres() {
    if(!parent?.locationPresence()) { return null }
    else {
        return parent?.locationPresence()
    }
}

/************************************************************************************************
|						              SCHEDULER METHOD						                	|
*************************************************************************************************/
def setRunSchedule(sec, fnct) {
    if(sec) {
        def timeVal = now() + (sec * 1000)
        schedule(timeVal, "$fnct", [overwrite: true])
    }
}

/************************************************************************************************
|							              Dynamic Pages							                |
*************************************************************************************************/

def sendNofificationMsg(msg, msgType, recips = null, sms = null, push = null) {
    if(recips || sms || push) {
        parent?.extSendMsg(msg, msgType, recips, sms, push)
        //LogAction("Send Push Notification to $recips...", "info", true)
    } else {
        parent?.extSendMsg(msg, msgType)
    }
}

def setRecipientsPage(params) {
    def preFix
    if (!params?.pName) { preFix = atomicState?.curPagePrefix } 
    else {
        atomicState.curPagePrefix = params?.pName 
        preFix = params?.pName
    }
    dynamicPage(name: "setRecipientsPage", title: "Set Push Notifications Recipients", uninstall: false) {
        def notifDesc = !location.contactBookEnabled ? "Enable push notifications below..." : "Select People or Devices to Receive Notifications..."
        section("${notifDesc}:") {
            if(!location.contactBookEnabled) {
                input "${preFix}UsePush", "bool", title: "Send Push Notitifications", required: false, defaultValue: false, image: getAppImg("notification_icon.png")
            } else {
                input("${preFix}NotifRecips", "contact", title: "Send notifications to", required: false, image: getAppImg("notification_icon.png")) {
                    input ("${preFix}NotifPhones", "phone", title: "Phone Number to send SMS to...", description: "Phone Number", required: false)
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

def setDayModeTimePage(params) {
    def preFix
    if (!params?.pName) { preFix = atomicState?.curPagePrefix } 
    else {
        atomicState.curPagePrefix = params?.pName 
        preFix = params?.pName
    }
    dynamicPage(name: "setDayModeTimePage", title: "Select Days, Times or Modes", uninstall: false) {
        section("Only During these Days, Times, or Modes:") {
            def timeReq = (settings?."${preFix}StartTime" || settings."${preFix}StopTime") ? true : false
            input "${preFix}StartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time_icon.png")
            input "${preFix}StopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time_icon.png")
            input "${preFix}Days", "enum", title: "Only on certain days of the week", multiple: true, required: false,
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], image: getAppImg("day_calendar_icon.png")
            input "${preFix}Modes", "mode", title: "Only with These Modes...", multiple: true, required: false, image: getAppImg("mode_icon.png")
        }
    }
}

def getDayModeTimeDesc(var) {
    def pName = var
    def ss = (settings?."${pName}StartTime" && settings?."${pName}StopTime") ? "Start: ${time2Str(settings?."${pName}StartTime")} | Stop: ${time2Str(settings?."${pName}StopTime")}" : "" 
    def dys = settings?."${pName}Days".toString().replaceAll("\\[|\\]", "")
    def mds = settings?."${pName}Modes".toString().replaceAll("\\[|\\]", "")
    def md = settings?."${pName}Modes" ? "Modes: (${mds})" : ""
    def dy = settings?."${pName}Days" ? "Days: (${dys})" : ""
    def confDesc = ((settings?."${pName}StartTime" || settings?."${pName}StopTime") || settings?."${pName}Modes" || settings?."${pName}Days") ? 
            "${ss ? "$ss\n" : ""}${md ? "$md\n" : ""}${dy ? "$dy" : ""}\n\nTap to Modify..." : "Tap to Configure..."
}

/************************************************************************************************
|							GLOBAL Code | Logging AND Diagnostic							    |
*************************************************************************************************/

def checkThermostatDupe(tstat1, tstat2) {
    def result = false
    if(tstat1 && tstat2) {
        def pTstat = tstat1?.deviceNetworkId.toString()
        def mTstatAr = []
        tstat2?.each { ts ->
            mTstatAr << ts?.deviceNetworkId.toString()
        }
        if (pTstat in mTstatAr) { return true }
    }
    return result
}

def checkModeDuplication(mode1, mode2) {
    def result = false
    if(mode1 && mode2) {
         mode1?.each { dm ->
            if(dm in mode2) {
                result = true
            }
        }
    }
    return result
}

def getClosedContacts(contacts) {
    if(contacts) {
        def cnts = contacts?.findAll { it?.currentContact == "closed" }
        return cnts ? cnts : null
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

def isSwitchOn(sw) {
    def res = false
    if(sw) {
        sw?.each { d ->
            if (d?.currentSwitch == "on") { res = true }
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
    try {
        if(mode) {
            switch(mode) {
                case "auto":
                    tstat?.auto()
                    break
                case "heat":
                    tstat?.heat()
                    break
                case "cool":
                    tstat?.cool()
                    break
                case "off":
                    tstat?.off()
                    break
                default:
                    log.debug "setTstatMode() | Invalid LastMode received: ${mode}"
                    break
            }
            LogAction("${tstat.label} has been set to ${mode}...", "info", true)
            return true
        } else { 
            LogAction("setTstatMode() | mode was not found...", "error", true)
            return false
        }
    }
    catch (ex) { 
        LogAction("setTstatMode() Exception | ${ex}", "error", true) 
        return false
    }
}

def LogTrace(msg) { if(parent?.advAppDebug) { Logger(msg, "trace") } }

def LogAction(msg, type = "debug", showAlways = false) {
    try {
        if(showAlways) { Logger(msg, type) }
        else if (parent?.appDebug && !showAlways) { Logger(msg, type) }
    } catch (ex) { log.error("LogAction Exception: ${ex}") }
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


/******************************************************************************  
*                			Keep These Methods				                  *
*******************************************************************************/
def getAppImg(imgName, on = null) { return (!parent?.settings?.disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/App/$imgName" : "" }
                            
private debugStatus() { return atomicState?.appDebug ? "On" : "Off" } //Keep this
private childDebugStatus() { return atomicState?.childDebug ? "On" : "Off" } //Keep this
private isAppDebug() { return atomicState?.appDebug ? true : false } //Keep This

def getTimeZone() { 
    def tz = null
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = atomicState?.timeZone ? TimeZone.getTimeZone(atomicState?.timeZone) : null }
    
    if(!tz) { LogAction("getTimeZone: Hub or Nest TimeZone is not found ...", "warn", true, true) }
    
    return tz
}

def formatDt(dt) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(getTimeZone()) { tf?.setTimeZone(getTimeZone()) }
    else {
        LogAction("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...", "warn", true, true)
    }
    return tf.format(dt)
}

//Returns time differences is seconds 
def GetTimeDiffSeconds(lastDate) {
    try {
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
    def res = false
    if (modeList) {
        modeList?.each { m ->
        	//log.debug "ST Mode: ${location?.mode} | M: $m"
            if(m.toString() == location?.mode.toString()) { 
            	//log.debug "Is In Mode: ${location?.mode}"
                res = true 
            }
        }  
    }
    return res
}

def time2Str(time) {
    if (time) {
        def t = timeToday(time, getTimeZone())
        def f = new java.text.SimpleDateFormat("h:mm a")
        f?.setTimeZone(location.timeZone ?: timeZone(time))
        f?.format(t)
    }
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

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
        60:"1 Minute", 120:"2 Minutes", 180:"3 Minutes", 240:"4 Minutes", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes", 1800:"30 Minutes", 
        2700:"45 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
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
        1:"Thermostat is Running", 2:"Only Fan is On" 
    ]
    return vals
}

def getEnumValue(enm, inpt) {
    def result = "unknown"
    if(enm) {
        enm?.each { item ->
            if(item?.key.toString() == inpt?.toString()) { 
                result = item?.value
            }
        }
    } 
    return result
}

def getSunTimeState() { 
    def tz = TimeZone.getTimeZone(location.timeZone.ID)
    def sunsetTm = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSX", location.currentValue('sunsetTime')).format('h:mm a', tz)
    def sunriseTm = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSX", location.currentValue('sunriseTime')).format('h:mm a', tz)
    atomicState.sunsetTm = sunsetTm
    atomicState.sunriseTm = sunriseTm
}

/******************************************************************************  
*                Application Help and License Info Variables                  *
*******************************************************************************/
private def appName() 		{ "Nest Automations${appDevName()}" }
private def appAuthor() 	{ "Anthony S." }
private def appParent() 	{ "tonesto7:Nest Manager${appDevName()}" }
private def appNamespace() 	{ "tonesto7" }
private def gitBranch()     { "master" }
private def appDevType()    { false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
private def appInfoDesc() 	{ }
private def textAppName()   { return "${appName()}" }    
private def textVersion()   { return "Version: ${appVersion()}" }
private def textModified()  { return "Updated: ${appVerDate()}" }
private def textAuthor()    { return "${appAuthor()}" }
private def textNamespace() { return "${appNamespace()}" }
private def textVerInfo()   { return "${appVerInfo()}" }
private def textCopyright() { return "Copyright© 2016 - Anthony S." }
private def textDesc()      { return "This App allows you to create Automation tasks to control you Thermostat devices based on various scenarios..." }
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
        "limitations under the License. If you wish to use/modify this software please "+
        "reference to the author of this application."
}