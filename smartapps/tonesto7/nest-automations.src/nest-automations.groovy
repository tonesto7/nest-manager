/********************************************************************************************
|    Application Name: Nest Automations                                                   |
|    Author: Anthony S. (@tonesto7), 														|
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

def appVersion() { "1.0.1" }
def appVerDate() { "4-22-2016" }
def appVerInfo() {
    
    "V1.0.1 (Apr 22nd, 2016)\n" +
    "Updated: Cleaned up code \n" + 
    "Added: Links to in-app help pages. \n" +
    "Added: Ability to use presence to set Nest home/away...\n\n" +
    
    "V1.0.0 (Apr 21st, 2016)\n" +
    "Added... Everything is new...\n\n" +
    "------------------------------------------------"
}

preferences {
    page(name: "mainPage", title: "Nest Automations", content:"mainPage", uninstall: true, install: false, nextPage: "namePage")
    page(name: "prefsPage")
    page(name: "debugPrefPage")
    page(name: "automationsPage")
    page(name: "extSensorPage")
    page(name: "wcPage")
    page(name: "modePresPage")
    page(name: "extTempsPage")
    page(name: "extSenShowTempsPage")
    page(name: "namePage", install: true, uninstall: true)
}

def mainPage() {
    //log.trace "mainPage()"
    state?.tempUnit = getTemperatureScale().toString()
    return dynamicPage(name: "mainPage", title: "Automation Page...", uninstall: false) {
        section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
            def senDayDesc = (extSensorDay) ? ("Day Sensor${(extSensorDay?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(extSensorDay)}°${state?.tempUnit}") : ""
            def senNightDesc = (extSensorNight) ? ("\nNight Sensor${(extSensorNight?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(extSensorNight)}°${state?.tempUnit}") : ""
            def senEnabDesc = state?.extSenEnabled ? "" : "External Sensor Disabled...\n"
            def motInUse = extMotionSensors ? ("\nMotion Events: ${!extMotionSensorModes ? "Active" : (isInMode(extMotionSensorModes) ? "Active(Mode Ok)" : "Not Active(!Mode)")}") : ""
            def senModes = extSenModes ? "\nMode Filters Active" : ""
            def senSetTemps = (extSenHeatTemp && extSenCoolTemp) ? "\nSet Temps: (Heat/Cool: ${extSenHeatTemp}°${state?.tempUnit}/${extSenCoolTemp}°${state?.tempUnit})" : ""
            def senRuleType = extSenRuleType ? "\nRule-Type: ${extSenRuleName()}" : ""
            def extTstatStatus = extSenTstat ? "\nThermostat Mode: ${extSenTstat?.currentThermostatOperatingState.toString()}/${extSenTstat?.currentThermostatMode.toString()}" : ""
            def senTypeUsed = getUseNightSensor() ? senNightDesc : senDayDesc
            def extSenDesc = isExtSenConfigured() ? 
                "${senEnabDesc}Thermostat Temp: ${getDeviceTemp(extSenTstat)}°${state?.tempUnit}${extTstatStatus}\n${senTypeUsed}${senSetTemps}${senRuleType}${motInUse}${senModes}\nTap to Modify..." : "Tap to Configure..."
            href "extSensorPage", title: "Use Remote Sensors...", description: extSenDesc, state: extSenDesc, image: getAppImg("remote_sensor_icon.png")
        }
        section("Turn Off Thermostat when a Door Window is Opened:") {
            def qOpt = (wcModes || wcDays || (wcStartTime && wcStopTime)) ? "Schedule Options Selected...\n" : ""
            def desc = (wcContacts && wcTstat) ? "${wcTstat.label}\nwith (${wcContacts ? wcContacts.size() : 0}) Contact(s)\n${qOpt}\nTap to Modify..." : "Tap to Configure..."
            href "wcPage", title: "Configure Sensors to Watch...", description: desc, image: getAppImg("open_window.png")
        }
        section("Turn On/Off Thermostat based on Outside temps:") {
            def qOpt = (exModes || exDays || (exStartTime && exStopTime)) ? "Schedule Options Selected...\n" : ""
            def desc = (!exUseWeather && exTemp && exTstat) ? ("${exTstat?.label}\nwith External Temp Sensor\n${qOpt}\nTap to Modify...") : (exUseWeather ? "${exTstat?.label}\nwith External Weather\n${qOpt}\nTap to Modify..." : "Tap to Configure...")
            href "extTempsPage", title: "Turn off based on External Temps...", description: desc, image: getAppImg("external_temp_icon.png")
        }
        section("Set Nest Presence Based on ST Modes:") {
            def nLocDesc = "Nest Location: ${getNestLocPres().toString().capitalize()}"
            def nModeDesc = (!modePresSensor && (awayModes || homeModes)) ? "${homeModes ? "Home Modes: $homeModes" : ""}${awayModes ? "\nAway Modes: $awayModes" : ""}\n\nTap to Modify..." : "Tap to Configure..."
            def nPresDesc = modePresSensor ? "Presence Sensor Active...\nPresence is: ${modePresSensor?.currentPresence}" : ""
            def nPresDelayDesc = useModePresSensorDelay ? "Delay: ${getLongTimeEnumLabel(modePresSensorDelayVal)}" : "" 
            href "modePresPage", title: "Mode Automations", description: "${nLocDesc}\n${(modePresSensor ? "${nPresDesc}\n${nPresDelayDesc}" : nModeDesc)}", image: getAppImg("mode_automation_icon.png")
        }
        
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
     }
}

def namePage() {
    dynamicPage(name: "namePage") {
        section("Automation name") {
            label title: "Name this Automation", defaultValue: app.label, required: true
            paragraph "Make sure to name it something that will help easily recognize it later."
        } 
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has been installed...")
    //parent?.autoAppInst(true)
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
    //getAutomationsActive()
    //parent?.autoAppInst(true)
    sendNotificationEvent("${textAppName()} has updated settings...")
}

def uninstalled() {
    //sends notification of uninstall
    sendNotificationEvent("${textAppName()} is uninstalled...")
    //parent?.autoAppInst(false)
}

def initialize() {
    unschedule()
    unsubscribe()
    scheduler()
    subscriber()
    updateWeather()
}

def getAutomationsActive() { 
    def remActive = ((extSensorDay || extSensorNight)  && extSenTstat && extSenHeatTemp && extSenCoolTemp)
    def conActive = (wcContacts && wcTstat)
    def nestModesActive = (awayModes && homeModes)
    def autoDesc = "${remActive ? "Remote Sensors Active..." : ""}${conActive ? "\nContact Watcher Active..." : ""}${nestModesActive ? "Mode Automation Active..." : ""}"
    parent?.automationsActive(((remActive || conActive || nestModesActive) ? true : false), autoDesc)
}

def subscriber() {
    if(extSensorDay || extSensorNight || extSenTstat) {
        subscribe(location, locationChgEvt)
        if(extSensorDay) { subscribe(extSensorDay, "temperature", extSenTempEvt) }
        if(extSensorNight) { subscribe(extSensorNight, "temperature", extSenTempEvt) }
        if(extSenTstat) {
            subscribe(extSenTstat, "temperature", extSenTempEvt)
            subscribe(extSenTstat, "thermostatMode", extSenTempEvt) 
        }
        if(extMotionSensors) { subscribe(extMotionSensors, "motionSensor", extSenMotionEvt) }
           if(extSenUseSunAsMode) {
            subscribe(location, "sunset", sunEvtHandler)
            subscribe(location, "sunrise", sunEvtHandler)
            subscribe(location, "sunriseTime", sunEvtHandler)
            subscribe(location, "sunsetTime", sunEvtHandler)
        }
        extSenEvtEval()
    }
    
    if (homeModes || awayModes) { subscribe(location, "mode", modeWatcher, [filterEvents: false]) }
    if (modePresSensor) { subscribe(modePresSensor, "presence", modePresenceEvt) }
    if (wcContacts && wcTstat) { subscribe(wcContacts, "contact", wcContactEvt) }
    if(!exUseWeather && exTemp) { subscribe(exTemp, "temperature", exTempEvt, [filterEvents: false]) }
}

def scheduler() {
    if(!exUseWeather && exTstat) { schedule("0 0/1 * * * ?", "updateData") } 
    if(exUseWeather && exTstat) { schedule("0 0/${getExWeatherRefreshVal()} * * * ?", "updateWeather") }
}

def updateData() {
    //exTempEvt(null) 
}

def updateWeather() {
    if(exUseWeather) { 
        getExtConditions()
        exTempEvt(null) 
    }
}

/******************************************************************************  
|                			EXTERNAL SENSOR AUTOMATION CODE	                  |
*******************************************************************************/
def extSensorPage() {
    dynamicPage(name: "extSensorPage", title: "Remote Sensor Automation", uninstall: false) {
        if(state?.extSenEnabled == null) { state?.extSenEnabled = true }
        def req = (extSensorDay || extSensorNight || extSenTstat) ? true : false
        def dupTstat = extSenTstatDuplication()
        def tStatHeatSp = getTstatSetpoint(extSenTstat, "heat")
        def tStatCoolSp = getTstatSetpoint(extSenTstat, "cool")
        def tStatMode = extSenTstat ? extSenTstat?.currentThermostatMode : "unknown"
        def tStatTemp = "${getDeviceTemp(extSenTstat)}°${state?.tempUnit}"
        def locMode = location?.mode
        
        def coolTempsReq = (extSenRuleType in [ "Cool", "Heat_Cool", "Cool_Circ", "Heat_Cool_Circ" ]) ? true : false
        def heatTempsReq = (extSenRuleType in [ "Heat", "Heat_Cool", "Heat_Circ", "Heat_Cool_Circ" ]) ? true : false
        
        section("Select the Allowed (Rule) Action Type:") {
            if(!extSenRuleType) { 
                paragraph "(Rule) Actions will be used to determine what actions are taken when the temperature threshold is reached. Using combinations of Heat/Cool/Fan to help balance" + 
                          " out the temperatures in your home in an attempt to make it more comfortable...", image: getAppImg("instruct_icon.png")
            }
            input(name: "extSenRuleType", type: "enum", title: "(Rule) Action Type", options: extSenRuleEnum(), required: true, submitOnChange: true, image: getAppImg("rule_icon.png"))
        }
        if(extSenRuleType) {
            section("Choose a Thermostat... ") {
                input "extSenTstat", "capability.thermostat", title: "Which Thermostat?", submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
                if(dupTstat) {
                    paragraph "Duplicate Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", image: getAppImg("error_icon.png")
                }
                if(extSenTstat) { 
                    setTstatCapabilities()
                    paragraph "Current Temperature: ${tStatTemp}\nCool/Heat Setpoints: ${tStatCoolSp}°${state?.tempUnit}/${tStatHeatSp}°${state?.tempUnit}\nCurrent Mode: $tStatMode", image: getAppImg("instruct_icon.png")
                    input "extSenTstatsMirror", "capability.thermostat", title: "Mirror Actions to these Thermostats", multiple: true, submitOnChange: true, required: false, image: getAppImg("thermostat_icon.png")
                    if(extSenTstatsMirror && !dupTstat) { 
                        extSenTstatsMirror?.each { t ->
                            paragraph "Thermostat Temp: ${getDeviceTemp(t)}${state?.tempUnit}", image: " "
                        }
                    }
                }
            }
            def dSenStr = !extSensorNight ? "Remote" : "Daytime"
            section("Choose $dSenStr Sensor(s) to use instead of the Thermostat's...") {
                def dSenReq = (((extSensorNight && !extSensorDay) || !extSensorNight) && extSenTstat) ? true : false
                input "extSensorDay", "capability.temperatureMeasurement", title: "$dSenStr Temp Sensors", submitOnChange: true, required: dSenReq,
                        multiple: true, image: getAppImg("temperature_icon.png")
                if(extSensorDay) {
                    def tempStr = !extSensorNight ? "" : "Day "
                    input "extSenHeatTempDay", "decimal", title: "Desired ${tempStr}Heat Temp (°${state?.tempUnit})", submitOnChange: true, required: heatTempsReq, image: getAppImg("heat_icon.png")
                    input "extSenCoolTempDay", "decimal", title: "Desired ${tempStr}Cool Temp (°${state?.tempUnit})", submitOnChange: true, required: coolTempsReq, image: getAppImg("cool_icon.png")
                    //paragraph " ", image: " "
                    def tmpVal = "$dSenStr Sensor Temp${(extSensorDay?.size() > 1) ? " (avg):" : ":"} ${getDeviceTempAvg(extSensorDay)}°${state?.tempUnit}"
                    if(extSensorDay.size() > 1) {
                        href "extSenShowTempsPage", title: "View $dSenStr Sensor Temps...", description: "${tmpVal}", image: getAppImg("blank_icon.png")
                        //paragraph "Multiple temp sensors will return the average of those sensors.", image: getAppImg("i_icon.png")
                    } else { paragraph "${tmpVal}", image: getAppImg("instruct_icon.png") }
                }
            }
            if(extSensorDay && (!setTempsReq || (setTempsReq && extSenHeatTempDay && extSenCoolTempDay))) {
                section("(Optional) Choose a second set of Temperature Sensor(s) to use in the Evening instead of the Thermostat's...") {
                    input "extSensorNight", "capability.temperatureMeasurement", title: "Evening Temp Sensors", submitOnChange: true, required: false, multiple: true, image: getAppImg("temperature_icon.png")
                    if(extSensorNight) {
                        input "extSenHeatTempNight", "decimal", title: "Desired Evening Heat Temp (°${state?.tempUnit})", submitOnChange: true, required: ((extSensorNight && heatTempsReq) ? true : false), image: getAppImg("heat_icon.png")
                        input "extSenCoolTempNight", "decimal", title: "Desired Evening Cool Temp (°${state?.tempUnit})", submitOnChange: true, required: ((extSensorNight && coolTempsReq) ? true : false), image: getAppImg("cool_icon.png")
                        //paragraph " ", image: " "
                        def tmpVal = "Evening Sensor Temp${(extSensorNight?.size() > 1) ? " (avg):" : ":"} ${getDeviceTempAvg(extSensorNight)}°${state?.tempUnit}"
                        if(extSensorNight.size() > 1) {
                            href "extSenShowTempsPage", title: "View Evening Sensor Temps...", description: "${tmpVal}", image: getAppImg("blank_icon.png")
                            //paragraph "Multiple temp sensors will return the average temp of those sensors.", image: getAppImg("i_icon.png")
                        } else { paragraph "${tmpVal}", image: getAppImg("instruct_icon.png") }
                    }
                }
            }
            if(extSensorDay && extSensorNight) {
                section("Day/Evening Detection Options:") {
                    input "extSenUseSunAsMode", "bool", title: "Use Sunrise/Sunset instead of Modes?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("sunrise_icon.png")
                    if(!extSenUseSunAsMode && !extSenModeDuplication()) {
                        def modesReq = (!extSenUseSunAsMode && (extSensorDay && extSensorNight)) ? true : false
                        input "extSensorDayModes", "mode", title: "Daytime Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                        input "extSensorNightModes", "mode", title: "Evening Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                    } else {
                        paragraph "Duplicate Mode(s) found under the Day or Evening Sensor!!!.  Please Correct...", image: getAppImg("error_icon.png")
                    }
                }
            }
            if(extSenTstat && (extSensorDay || extSensorNight)) {
                if(extSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) {
                    section("Fan Settings:") {
                        paragraph "The default fan runtime is 15 minutes.\nThis can be adjusted under your nest account.", image: getAppImg("instruct_icon.png")
                        input "extTimeBetweenRuns", "enum", title: "Delay Between Fan Runs?", required: true, defaultValue: 3600, metadata: [values:longTimeEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                    }
                }
                section("(Optional) Use Motion Sensors to Evaluate Temps:") {
                    input "extMotionSensors", "capability.motionSensor", title: "Motion Sensors", required: false, multiple: true, submitOnChange: true, image: getAppImg("motion_icon.png")
                    if(extMotionSensors) {
                        paragraph "Motion State: (${isMotionActive(extMotionSensors) ? "Active" : "Not Active"})", image: " "
                        input "extMotionSensorModes", "mode", title: "Only evaluate Motion in these Modes...", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode_icon.png")
                        input "extMotionDelayVal", "enum", title: "Delay before evaluating?", required: true, defaultValue: 300, metadata: [values:longTimeEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                    }
                }
                section ("Optional Settings:") {
                    paragraph "The Action Threshold Temp is the temperature difference used to trigger a selected action.", image: getAppImg("instruct_icon.png")
                    input "extTempDiffDegrees", "decimal", title: "Action Threshold Temp (°${state?.tempUnit})", required: true, defaultValue: 1.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                    if(extSenRuleType != "Circ") {
                        paragraph "The Change Temp Increments are the amount the temp is adjusted +/- when an action requires a temp change.", image: getAppImg("instruct_icon.png")
                        input "extTempChgDegrees", "decimal", title: "Change Temp Increments (°${state?.tempUnit})", required: true, defaultValue: 2.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                    }
                    input "extSenModes", "mode", title: "Only Evaluate Actions in these Modes?", multiple: true, required: false, submitOnChange: true, image: getAppImg("mode_icon.png")
                    input "extSenEvalWait", "number", title: "Wait Time between Evaluations (seconds)?", required: false, defaultValue: 60, submitOnChange: true, image: getAppImg("delay_time_icon.png")
                }
            }
        }
        if (isExtSenConfigured()) {
            section("Enable or Disable Remote Sensor Once Configured...") {
                input (name: "extSenEnabled", type: "bool", title: "Enable Remote Sensor Automation?", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("switch_icon.png"))
                state?.extSenEnabled = extSenEnabled ? true : false
            }
        } else { state?.extSenEnabled = false }
        
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def isExtSenConfigured() {
    def devOk = ((extSensorDay || extSensorNight) && extSenTstat) ? true : false
    def nightOk = (!extSensorNight && extSensorDay) || (extSensorNight && (extSenRuleType == "Circ" && ((!extSenHeatTempNight || !extSenCoolTempNight) || (extSenHeatTempNight && extSenCoolTempNight)))) ? true : false
    def dayOk = (extSensorDay && (extSensorDay && !extSensorNight) || (extSensorDay && (extSenRuleType == "Circ" && ((!extSenHeatTempDay || !extSenCoolTempDay) || (extSenHeatTempDay && extSenCoolTempDay))))) ? true : false
    //log.debug "devOk: $devOk | nightOk: $nightOk | dayOk: $dayOk"
    return (devOk && nightOk && dayOk) ? true : false
}

def extSenMotionEvt(evt) {
    log.debug "extSenMotionEvt event: $evt.value"
    if(state?.extSenEnabled == false) { return }
    else if (extSenUseSunAsMode) { return}
    else {
        if(extMotionSensorModes) {
            if(isInMode(extMotionSensorModes)) {
                runIn(extMotionDelayVal.toInteger()*60, "extCheckMotion", [overwrite: true])
            }
        } else {
            runIn(extMotionDelayVal.toInteger()*60, "extCheckMotion", [overwrite: true])
        }
    }
}

def isMotionActive(sensors) {
    return sensors?.currentState("motion")?.value.contains("active") ? true : false
}

def extCheckMotion() {
    if(isMotionActive(extMotionSensors)) { extSenEvtEval() }
}

def extSenTempEvt(evt) {
    if(state?.extSenEnabled == false) { return }
    else { extSenEvtEval() }
}

def sunEvtHandler(evt) {
    if(extSenUseSunAsMode) { extSenEvtEval() }
    else { return }
}

def extSenTstatDuplication() {
    def result = false
    if(extSenTstat && extSenTstatsMirror) {
        def pTstat = extSenTstat?.deviceNetworkId.toString()
        def mTstatAr = []
        extSenTstatsMirror?.each { ts ->
            mTstatAr << ts?.deviceNetworkId.toString()
        }
        if (pTstat in mTstatAr) { return true }
    }
    return result
}

def extSenModeDuplication() {
    def result = false
    if(extSensorDayModes && extSensorNightModes) {
         extSensorDayModes?.each { dm ->
            if(dm in extSensorNightModes) {
                result = true
            }
        }
    }
    return result
}

def getUseNightSensor() {
    def day = !extSensorDayModes ? false : isInMode(extSensorDayModes)
    def night = !extSensorNightModes ? false : isInMode(extSensorNightModes)
    if(extSenUseSunAsMode) { return getTimeAfterSunset() ? true : false }
    else if(night && !day) { return true }
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

def longTimeEnum() {
    def vals = [
        60:"1 Minute", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes", 1800:"30 Minutes", 
        3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
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
    def tempUnit = state?.tempUnit
    def vals = [
        1:"1°${tempUnit}", 2:"2°${tempUnit}", 3:"3°${tempUnit}", 4:"4°${tempUnit}", 5:"5°${tempUnit}", 6:"6°${tempUnit}", 7:"7°${tempUnit}",
        8:"8°${tempUnit}", 9:"9°${tempUnit}", 10:"10°${tempUnit}"
    ]
    return vals
}

def extSenRuleName() {
    def result = "unknown"
    if(extSenRuleType) {
        extSenRuleEnum().each { item ->
            if(item?.key.toString() == extSenRuleType?.toString()) { 
                result = item?.value
            }
        }
    } 
    return result
}

def extSenShowTempsPage() {
    dynamicPage(name: "extSenShowTempsPage", uninstall: false) {
        if(extSensorDay) { 
            def dSenStr = !extSensorNight ? "Remote" : "Daytime"
            section("$dSenStr Sensor Temps:") {
                extSensorDay?.each { t ->
                    paragraph "${t?.label}: ${getDeviceTemp(t)}°${state?.tempUnit}", image: getAppImg("temperature_icon.png")
                }
            }
            section("Average Temp of $dSenStr Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(extSensorDay)}°${state?.tempUnit}", image: getAppImg("instruct_icon.png")
            }
        }
        if(extSensorNight) { 
            section("Night Sensor Temps:") {
                extSensorNight?.each { t ->
                    paragraph "${t?.label}: ${getDeviceTemp(t)}°${state?.tempUnit}", image: getAppImg("temperature_icon.png")
                }
            }
            section("Average Temp of Night Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(extSensorNight)}°${state?.tempUnit}", image: getAppImg("instruct_icon.png")
            }
        }
    }
}

def getTimeAfterSunset() {
    def sR = (location?.currentValue("sunriseTime"))
    def sS = (location?.currentValue("sunsetTime"))
    def result = true
    if (sS && sR) {
        def timeNow = now()
        def start = timeToday(sS, location?.timeZone).time
        def stop = timeToday(sR, location?.timeZone).time
        result = (start < stop) ? ((timeNow >= start) && (timeNow <= stop)) : ((timeNow <= stop) || (timeNow >= start))
    }
    return result
}

def locationChgEvt(evt) {
    log.debug "locationChgEvt mode: $evt.value, heat: $heat, cool: $cool"
    extSenEvtEval()
}

def getLastExtSenEvalDtSec() { return !atomicState?.lastExtSenEval ? 100000 : GetTimeDiffSeconds(atomicState?.lastExtSenEval).toInteger() }

// Based off of Keep Me Cozy II
private extSenEvtEval() {
    log.trace "extSenEvtEval....."
    //log.debug "Remote Sensor Enabled: ${state?.extSenEnabled} | extSenModesOk: ${modesOk(extSenModes)} | ext Sensor: ${(extSensorDay || extSensorNight)} | Thermostat: ${extSenTstat} | getExtModeOk(): ${getExtModeOk()}"
    if(getLastExtSenEvalDtSec() < (extSenEvalWait ? extSenEvalWait?.toInteger() : 60)) { 
        log.debug "Too Soon to Evaluate..."
        return 
    } 
    else { 
        atomicState?.lastExtSenEval = getDtNow()
        if (state?.extSenEnabled && modesOk(extSenModes) && (extSensorDay || extSensorNight) && extSenTstat && getExtModeOk()) {
            def threshold = !extTempDiffDegrees ? 0 : extTempDiffDegrees.toDouble()
            def chgTempVal = !extTempChgDegrees ? 0 : extTempChgDegrees.toDouble()
            def hvacMode = extSenTstat ? extSenTstat?.currentThermostatMode.toString() : null
            def curTstatTemp = getDeviceTemp(extSenTstat).toDouble()
            def curTstatOperState = extSenTstat?.currentThermostatOperatingState.toString()
            def curTstatFanMode = extSenTstat?.currentThermostatFanMode
            def curCoolSetpoint = getTstatSetpoint(extSenTstat, "cool")
            def curHeatSetpoint = getTstatSetpoint(extSenTstat, "heat")
            def extHtemp = getSenHeatSetpointTemp()
            def extCtemp = getSenCoolSetpointTemp()
            def curSenTemp = (extSensorDay || extSensorNight) ? getRemoteSenTemp().toDouble() : null
            
            log.trace "Remote Sensor Rule Type: ${extSenRuleType}"
            log.trace "Remote Sensor Temp: ${curSenTemp}"
            log.trace "Thermostat Info - ( Temperature: ($curTstatTemp) | HeatSetpoint: ($curHeatSetpoint) | CoolSetpoint: ($curCoolSetpoint) | HvacMode: ($hvacMode) | OperatingState: ($curTstatOperState) | FanMode: ($curTstatFanMode) )" 
            log.trace "Desired Temps - Heat: $extHtemp | Cool: $extCtemp"
            
            if(hvacMode == "off") { return }
            
            else if (hvacMode in ["cool","auto"]) {
                if ((curSenTemp - extCtemp) >= threshold) {
                    if(extSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "COOL - Setting CoolSetpoint to (${(curTstatTemp - chgTempVal)}°${state?.tempUnit})"
                        extSenTstat?.setCoolingSetpoint(curTstatTemp - chgTempVal)
                        if(extSenTstatsMirror) { extSenTstatsMirror*.setCoolingSetpoint(curTstatTemp - chgTempVal) }
                        log.debug "extSenTstat.setCoolingSetpoint(${curTstatTemp - chgTempVal}), ON"
                    }
                }
                else if (((extCtemp - curSenTemp) >= threshold) && ((curTstatTemp - curCoolSetpoint) >= threshold)) {
                    if(extSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "COOL - Setting CoolSetpoint to (${(curTstatTemp + chgTempVal)}°${state?.tempUnit})"
                        extSenTstat?.setCoolingSetpoint(curTstatTemp + chgTempVal)
                        if(extSenTstatsMirror) { extSenTstatsMirror*.setCoolingSetpoint(curTstatTemp - chgTempVal) }
                        log.debug "extSenTstat.setCoolingSetpoint(${curTstatTemp + chgTempVal}), OFF"
                    }
                } else {
                    //log.debug "FAN(COOL): $extSenRuleType | RuleOk: (${extSenRuleType in ["Circ", "Cool_Circ", "Heat_Cool_Circ"]})"
                    //log.debug "FAN(COOL): DiffOK (${getFanTempOk(curSenTemp, extCtemp, curCoolSetpoint, threshold)})"
                    if(extSenRuleType in ["Circ", "Cool_Circ", "Heat_Cool_Circ"]) {
                        if( getFanTempOk(curSenTemp, extCtemp, curCoolSetpoint, threshold) && getFanRunOk(curTstatOperState, curTstatFanMode) ) {
                            log.debug "Running $extSenTstat Fan for COOL Circulation..."
                            extSenTstat?.fanOn()
                            if(extSenTstatsMirror) { 
                                extSenTstatsMirror.each { mt -> 
                                    log.debug "Mirroring $mt Fan Run for COOL Circulation..."
                                    mt?.fanOn() 
                                }
                            }
                            atomicState?.lastFanRunDt = getDtNow()
                        }
                    }
                }
            }
            //Heat Functions....
            else if (hvacMode in ["heat", "emergency heat", "auto"]) {
                if ((extHtemp - curSenTemp) >= threshold) {
                    if(extSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) { 
                        log.debug "HEAT - Setting HeatSetpoint to (${(curTstatTemp + chgTempVal)}°${state?.tempUnit})"
                        extSenTstat?.setHeatingSetpoint(curTstatTemp + chgTempVal)
                        if(extSenTstatsMirror) { extSenTstatsMirror*.setHeatingSetpoint(curTstatTemp + chgTempVal) }
                        log.debug "extSenTstat.setHeatingSetpoint(${curTstatTemp + chgTempVal}), ON"
                    }
                }
                else if (((curSenTemp - extHtemp) >= threshold) && ((curHeatSetpoint - curTstatTemp) >= threshold)) {
                    if(extSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "HEAT - Setting HeatSetpoint to (${(curTstatTemp - chgTempVal)}°${state?.tempUnit})"
                        extSenTstat?.setHeatingSetpoint(curTstatTemp - chgTempVal)
                        if(extSenTstatsMirror) { extSenTstatsMirror*.setHeatingSetpoint(curTstatTemp - chgTempVal) }
                        log.debug "extSenTstat.setHeatingSetpoint(${curTstatTemp - chgTempVal}), OFF"
                    }
                } else { 
                    //log.debug "FAN(HEAT): $extSenRuleType | RuleOk: (${extSenRuleType in ["Circ", "Heat_Circ", "Heat_Cool_Circ"]})"
                    //log.debug "FAN(HEAT): DiffOK (${getFanTempOk(curSenTemp, extHtemp, curHeatSetpoint, threshold)})"
                    if (extSenRuleType in ["Circ", "Heat_Circ", "Heat_Cool_Circ"]) {
                        if( getFanTempOk(curSenTemp, extHtemp, curHeatSetpoint, threshold) && getFanRunOk(curTstatOperState, curTstatFanMode) ) {
                            log.debug "Running $extSenTstat Fan for HEAT Circulation..."
                            extSenTstat?.fanOn()
                            if(extSenTstatsMirror) { 
                                extSenTstatsMirror.each { mt -> 
                                    log.debug "Mirroring $mt Fan Run for HEAT Circulation..."
                                    mt?.fanOn() 
                                }
                            }
                            atomicState?.lastFanRunDt = getDtNow()
                        }
                    }
                }
            } else { log.warn "extSenEvtEval: Did not receive a valid Thermostat Mode..." }
        }
        else {
            def extHtemp = getSenHeatSetpointTemp()
            def extCtemp = getSenCoolSetpointTemp()
            extSenTstat?.setHeatingSetpoint(extHtemp)
            extSenTstat?.setCoolingSetpoint(extCtemp)
            if(extSenTstatsMirror) {
                extSenTstatsMirror*.setHeatingSetpoint(extHtemp)
                extSenTstatsMirror*.setCoolingSetpoint(extCtemp)
            }
        }
    }
}

def getFanTempOk(senTemp, userTemp, curTemp, threshold) {
    def diff1 = (Math.abs(senTemp - userTemp)?.round(1) < threshold)
    def diff2 = (Math.abs(userTemp - curTemp)?.round(1) < threshold)
    log.debug "getFanTempOk: ( Sensor Temp - Set Temp: (${Math.abs(senTemp - userTemp).round(1)}) < Threshold Temp: (${threshold}) ) - ($diff1)"
    log.debug "getFanTempOk: ( Set Temp - Current Temp: (${Math.abs(userTemp - curTemp).round(1)}) < Threshold Temp: (${threshold}) ) - ($diff2)"
    return (diff1 && diff2) ? true : false
}

// for backward compatibility with existing subscriptions
def coolingSetpointHandler(evt) { log.debug "coolingSetpointHandler()" }

def heatingSetpointHandler(evt) { log.debug "heatingSetpointHandler()" }

def getExtModeOk() {
    if (extSensorDay && (!extSensorDayModes && !extSensorNight && !extSensorNightModes)) {
        return true
    }
    else if (extSensorDayModes || extSensorNightModes) {
        return (isInMode(extSensorDayModes) || isInMode(extSensorNightModes)) ? true : false
    } 
    else {
        return false
    }
}

def getFanRunOk(operState, fanState) { 
    log.trace "getFanRunOk($operState, $fanState)"
    def val = extTimeBetweenRuns ? extTimeBetweenRuns.toInteger() : 3600
    def cond = ((extSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) && operState == "idle" && fanState == "auto") ? true : false
    def timeSince = (getLastFanRunDtSec() > val)
    def result = (timeSince && cond) ? true : false
    log.debug "getFanRunOk(): cond: $cond | timeSince: $timeSince | val: $val | $result"
    return result
}

def getLastFanRunDtSec() { return !atomicState?.lastFanRunDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastFanRunDt).toInteger() }

def getDeviceTemp(dev) {
    return dev ? dev?.currentValue("temperature").toString().replaceAll("\\[|\\]", "").toDouble() : null
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
    if(!getUseNightSensor() && extSensorDay) {
        return getDeviceTempAvg(extSensorDay).toDouble()  
    }
    else if(getUseNightSensor() && extSensorNight) {
        return getDeviceTempAvg(extSensorNight).toDouble()        
    }
    else {
        return 0.0
    }
}

def getSenCoolSetpointTemp() {
    if(!getUseNightSensor() && extSenCoolTempDay) {
        return extSenCoolTempDay?.toDouble()  
    }
    else if(getUseNightSensor() && extSenCoolTempNight) {
        return extSenCoolTempNight?.toDouble()        
    }
    else {
        return extSenTstat ? getTstatSetpoint(extSenTstat, "cool") : 0
    }
}

def getSenHeatSetpointTemp() {
    if(!getUseNightSensor() && extSenHeatTempDay) {
        return extSenHeatTempDay?.toDouble()  
    }
    else if(getUseNightSensor() && extSenHeatTempNight) {
        return extSenHeatTempNight?.toDouble()        
    }
    else {
        return extSenTstat ? getTstatSetpoint(extSenTstat, "heat") : 0
    }
}

def setTstatCapabilities() {
    try {
        def canCool = true
        def canHeat = true
        def hasFan = true
        if(extSenTstat) { 
            canCool = (extSenTstat?.currentCanCool == "true") ? true : false
            canHeat = (extSenTstat?.currentCanHeat == "true") ? true : false
            hasFan = (extSenTstat?.currentHasFan == "true") ? true : false
        }
        state?.extSenTstatCanCool = canCool
        state?.extSenTstatCanHeat = canHeat
        state?.extSenTstatHasFan = hasFan
    } catch (e) { }
}

def extSenRuleEnum() {
    def canCool = state?.extSenTstatCanCool ? true : false
    def canHeat = state?.extSenTstatCanHeat ? true : false
    def hasFan = state?.extSenTstatHasFan ? true : false
    def vals = []
    
    //log.debug "extSenRuleEnum -- hasFan: $hasFan (${atomicState?.extSenTstatHasFan} | canCool: $canCool (${atomicState?.extSenTstatCanCool} | canHeat: $canHeat (${atomicState?.extSenTstatCanHeat}"
    
    if (canCool && !canHeat && hasFan) { vals = ["Cool":"Cool", "Circ":"Circulate(Fan)", "Cool_Circ":"Cool/Circulate(Fan)"] }
    else if (canCool && !canHeat && !hasFan) { vals = ["Cool":"Cool"] }
    else if (!canCool && canHeat && hasFan) { vals = ["Circ":"Circulate(Fan)", "Heat":"Heat", "Heat_Circ":"Heat/Circulate(Fan)"] }
    else if (!canCool && canHeat && !hasFan) { vals = ["Heat":"Heat"] }
    else if (!canCool && !canHeat && hasFan) { vals = ["Circ":"Circulate(Fan)"] }
    else if (canCool && canHeat && !hasFan) { vals = ["Heat_Cool":"Auto", "Heat":"Heat", "Cool":"Cool"] }
    else { vals = [ "Heat_Cool":"Auto", "Heat":"Heat", "Cool":"Cool", "Circ":"Circulate(Fan)", "Heat_Cool_Circ":"Auto/Circulate(Fan)", "Heat_Circ":"Heat/Circulate(Fan)", "Cool_Circ":"Cool/Circulate(Fan)" ] }
    //log.debug "extSenRuleEnum vals: $vals"
    return vals
}


/******************************************************************************  
|                			WATCH CONTACTS AUTOMATION CODE	                  |
*******************************************************************************/

def wcPage() {
    dynamicPage(name: "wcPage", title: "Thermostat/Contact Automation", uninstall: false) {
        section("When These Contacts are open, Turn Off this Thermostat") {
            def req = (wcContacts || wcTstat) ? true : false
            input name: "wcContacts", type: "capability.contactSensor", title: "Which Contact(s)?", multiple: true, submitOnChange: true, required: req,
                    image: getAppImg("contact_icon.png")
            input name: "wcTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: getAppImg("thermostat_icon.png")
            if(wcTstat) {
                input name: "wcTstatMir", type: "capability.thermostat", title: "Mirror commands to these Thermostats?", multiple: true, submitOnChange: true, required: false,
                    image: getAppImg("thermostat_icon.png")
            }
        }
        if(wcContacts && wcTstat) {
            section("Only During these Days, Times, or Modes:") {
                def timeReq = (wcStartTime || wcStopTime) ? true : false
                input "wcStartTime", "time", title: "Start time", submitOnChange: true, required: timeReq, 
                        image: getAppImg("start_time_icon.png")
                input "wcStopTime", "time", title: "Stop time", submitOnChange: true, required: timeReq,
                        image: getAppImg("stop_time_icon.png")

                input "wcModes", "mode", title: "Only with These Modes...", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("mode_icon.png")
                input "wcDays", "enum", title: "Only on certain days of the week", multiple: true, required: false, submitOnChange: true,
                        options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
                        image: getAppImg("day_calendar_icon.png")
            }
            section("Delay Values:") {
                input name: "wcOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")

                input "restModeOnClose", "bool", title: "Restore Previous mode after Closed?", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("restore_icon.png")
                if(restModeOnClose) {
                    input name: "wcOnDelay", type: "enum", title: "Delay On (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                }
            }
            section("Notifications:") {
                input "sendPushOnWc", "bool", title: "Send Push Notifications on Changes?", required: false, defaultValue: true, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
            }
        }
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def wcTimeOk() {
    try {
        def strtTime = null
        def stopTime = null
        def now = new Date()
        if(wcStartTime && wcStopTime) { 
            if(wcStartTime) { strtTime = wcStartTime }
            if(wcStopTime) { stopTime = wcStopTime }
        } else { return true } 
        
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), location?.timeZone) ? false : true
        } else { return true }
    } catch (ex) { LogAction("wcTimeOk Exception: ${ex}", "error", true, true) }
}

def getWcContactsOk() { return wcContacts?.currentState("contact")?.value.contains("open") ? false : true }
def watchContactOk() { return (!wcContacts && !wcTstat) ? false : true }
def wcScheduleOk() { return (modesOk(wcModes) && daysOk(wcDays) && wcTimeOk()) ? true : false }
def getWcOpenDtSec() { return !state?.wcOpenDt ? 100000 : GetTimeDiffSeconds(state?.wcOpenDt).toInteger() }
def getWcCloseDtSec() { return !state?.wcCloseDt ? 100000 : GetTimeDiffSeconds(state?.wcCloseDt).toInteger() }
def getWcOffDelayVal() { return !wcOffDelay ? 300 : (wcOffDelay.toInteger()) }
def getWcOnDelayVal() { return !wcOnDelay ? 300 : (wcOnDelay.toInteger()) }

def wcCheck() {
    log.trace "wcCheck..."
    def curMode = wcTstat.currentState("thermostatMode").value.toString()
    if(getWcContactsOk()) {
        if(curMode == "off" && restModeOnClose && state?.wcTurnedOff) {
            if(getWcCloseDtSec() >= (getWcOnDelayVal()?.toInteger() - 2)) {
                def lastMode = state?.wcRestoreMode ?: curMode
                if(!state?.wcRestoreMode?.equals(curMode)) {
                    if(lastMode) {
                        if(setTstatMode(wcTstat, lastMode)) {
                            state.wcTurnedOff = false
                            if(wcTstatMir) { 
                                   wcTstatMir.each { t ->
                                    setTstatMode(t, lastMode)
                                    log.debug("Restoring ${lastMode} to ${t}")
                                }
                            }
                            LogAction("${wcTstat.label} has been restored to ${lastMode} Mode because a selected Contacts have Been Closed...", "info", true)
                            if(sendPushOnWc) {
                                parent?.sendMsg("Info", "${wcTstat.label} has been restored to ${lastMode} Mode because a selected Contacts have Been Closed...")
                            }
                        }
                    }
                    else { LogAction("wcCheck() | lastMode was not found...", "error", true) }
                }
            }
        } 
    }
    
    if (!getWcContactsOk()) {
        if(curMode != "off") {
            if(getWcOpenDtSec() >= (getWcOffDelayVal().toInteger() - 2)) {
                log.debug "!getWcContactsOk..."
                if(restModeOnClose) { 
                    state.wcRestoreMode = curMode
                    log.debug "restoreToMode Set to: ${atomicState?.wcRestoreMode}"
                }
                log.debug("Selected Contacts are Open turning off ${wcTstat}")
                state.wcTurnedOff = true
                wcTstat?.off()
                if(wcTstatMir) { 
                    wcTstatMir.each { t ->
                        t.off()
                        log.debug("Turned off ${t}")
                    }
                }
                LogAction("${wcTstat.label} has been turned off because a selected Contact has Been Opened", "info", true)
                if(sendPushOnWc) {
                    parent?.sendMsg("Alert", "${wcTstat.label} has been turned off because a selected Contact has Been Opened")
                }
            }
        } else { LogAction("wcCheck() | Skipping change because mode is already 'Off'", "info", true) }
    }
}

def wcContactEvt(evt) {
    log.debug "watchContactEvt: ${evt?.value}"
    def curMode = wcTstat?.currentThermostatMode.toString()
    if(wcScheduleOk()) {
        if (!getWcContactsOk() && curMode != "off") {
            state?.wcOpenDt = getDtNow()
            log.debug "wcContactEvt() | Scheduling Thermostat OFF in (${getWcOffDelayVal()} seconds)..."
            runIn(getWcOffDelayVal()?.toInteger(), "wcCheck", [overwrite: true]) 
        }
        else if(getWcContactsOk() && (restModeOnClose && curMode == "off" && state?.wcTurnedOff == true)) {
            state.wcCloseDt = getDtNow()
            log.debug "wcContactEvt() | Scheduling Thermostat ON in (${getWcOnDelayVal()} seconds)..."
            runIn(getWcOnDelayVal()?.toInteger(), "wcCheck", [overwrite: true])
        }
    }
}

/********************************************************************************  
|                			External Temp AUTOMATION CODE	     				|
*********************************************************************************/

def extTempsPage() {
    dynamicPage(name: "extTempsPage", title: "Thermostat/External Temps Automation", uninstall: false) {
        section("When External Temp reaches Turn Off this Thermostat when the Local Weather temp goes above a certain threshold.  ") {
            def req = ((exUseWeather || (!exUseWeather && exTemp)) || Tstat) ? true : false
            input "exUseWeather", "bool", title: "Use Local Weather as External Sensor?", required: req, defaultValue: false, submitOnChange: true,
                    image: getAppImg("weather_icon.png")
            if(exUseWeather){
                getExtConditions()
                def tmpVal = (location?.temperatureScale == "C") ? state?.curWeatherTemp_c : state?.curWeatherTemp_f
                paragraph "Current Weather Temp: $tmpVal", image: " "
                input name: "locZipcode", type: "number", title: "Custom ZipCode (Default is Hub Loction)?", submitOnChange: true, required: false,
                        image: getAppImg("location_icon.png")
                
                input name: "weatherRfrshVal", type: "number", title: "Update Weather (in Minutes)?", default: 5, submitOnChange: true, required: false,
                        image: getAppImg("start_time_icon.png")
            }
            if(!exUseWeather) {
                input "exTemp", "capability.temperatureMeasurement", title: "Which Temperature Sensors?", submitOnChange: true, multiple: false, required: req, 
                        image: getAppImg("temperature_icon.png")
                if(exTemp) {
                    def tmpVal = "${exTemp?.currentValue("temperature").toString()}${location?.temperatureScale.toString()}"
                    paragraph "Current Sensor Temp: ${tmpVal}", image: " "
                }
            }
            input name: "exTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: getAppImg("thermostat_icon.png")
            if(exTstat) {
                def tmpVal = "${exTstat?.currentValue("temperature").toString()}${location?.temperatureScale.toString()}"
                    paragraph "Current Thermostat Temp: ${tmpVal}", image: " "
                input name: "exTempDiffVal", type: "number", title: "When Inside Temp is within this many Degrees of External Temp?", range: "-30..30", default: 0, submitOnChange: true, required: false,
                        image: getAppImg("temp_icon.png")
            }
        }
        if((exUseWeather || exTemp) && exTstat) {
            section("Only During these Days, Times, or Modes:") {
                def timeReq = (exStartTime || exStopTime) ? true : false
                input "exStartTime", "time", title: "Start time", submitOnChange: true, required: timeReq, 
                                image: getAppImg("start_time_icon.png")
                input "exStopTime", "time", title: "Stop time", submitOnChange: true, required: timeReq,
                                image: getAppImg("stop_time_icon.png")

                input "exModes", "mode", title: "Only with These Modes...", multiple: true, submitOnChange: true, required: false,
                                image: getAppImg("mode_icon.png")
                input "exDays", "enum", title: "Only on certain days of the week", multiple: true, required: false, submitOnChange: true,
                                options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
                                image: getAppImg("day_calendar_icon.png")
            }
            section("Delay Values:") {
                input name: "exOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                                image: getAppImg("delay_time_icon.png")

                input "exRestoreMode", "bool", title: "Restore Previous Mode when Temp goes below Threshold?", required: false, defaultValue: false, submitOnChange: true,
                                image: getAppImg("restore_icon.png")
                if(exRestoreMode) {
                    input name: "exOnDelay", type: "enum", title: "Delay On (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                                image: getAppImg("delay_time_icon.png")
                }
            }
            section("Notifications:") {
                input "sendPushOnEx", "bool", title: "Send Push Notifications on Changes?", required: false, defaultValue: true, submitOnChange: true,
                                image: getAppImg("notification_icon.png")
            }
        }
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def getExtConditions() {
    def cur = getWeatherFeature("conditions")
    state.curWeather = cur?.current_observation
    state?.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f).toInteger()
    state?.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c).toInteger()
    state?.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
    state?.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
    log.debug "${state?.curWeatherLoc} Weather | humidity: ${state?.curWeatherHum} | temp_f: ${state?.curWeatherTemp_f} | temp_c: ${state?.curWeatherTemp_c}"
}

def getExTempOk() { 
    def intTemp = exTstat ? Math.round(exTstat?.currentValue("temperature")).toInteger() : null
    def extTemp = null
    
    if(!exUseWeather && exTemp) { extTemp = Math.round(exTemp?.currentValue("temperature")).toInteger() }
    else {
        if(exUseWeather && (state?.curWeatherTemp_f || state?.curWeatherTemp_c)) {
            if(location?.temperatureScale == "C" && state?.curWeatherTemp_c) { extTemp = state?.curWeatherTemp_c }
            else { extTemp = state?.curWeatherTemp_f }
        } else { return true }
    }
    log.debug "Inside Temp: $intTemp | Outside Temp: $extTemp | Temp Threshold: ${exTempDiffVal}"
    if(intTemp && extTemp && exTempDiffVal) { 
        def tempDiff = (extTemp < intTemp) ? -(extTemp - intTemp) : (extTemp - intTemp)
        log.debug "Inside Temp: $intTemp | Outside Temp: $extTemp | Temp Threshold: ${exTempDiffVal} | Actual Difference: $tempDiff"
        if(exTempDiffVal < 0 && exTempDiffVal <= tempDiff) { return false }
        else if(exTempDiffVal > 0 && tempDiff <= exTempDiffVal) { return false }
        
        return true
    }
    LogAction("getExTempOk() | Failed to complete the temp check", "error", true)
    return null
}

def exTimeOk() {
    try {
        def strtTime = null
        def stopTime = null
        def now = new Date()
        if(exStartTime && exStopTime) { 
            if(exStartTime) { strtTime = exStartTime }
            if(exStopTime) { stopTime = exStopTime }
        } else { return true }  
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), location?.timeZone) ? false : true
        } else { return true }
    } catch (ex) { LogAction("exTimeOk Exception: ${ex}", "error", true, true) }
}
def exScheduleOk() { return (modesOk(exModes) && daysOk(exDays) && exTimeOk()) ? true : false }
def getExTempGoodDtSec() { return !state?.exTempGoodDt ? 100000 : GetTimeDiffSeconds(state?.exTempGoodDt).toInteger() }
def getExTempBadDtSec() { return !state?.exTempBadDt ? 100000 : GetTimeDiffSeconds(state?.exTempBadDt).toInteger() }
def getExOffDelayVal() { return !exOffDelay ? 300 : (exOffDelay.toInteger()) }
def getExOnDelayVal() { return !exOnDelay ? 300 : (exOnDelay.toInteger()) }
def getExWeatherRefreshVal() { return !weatherRfrshVal ? 1 : (weatherRfrshVal.toInteger()) }

def exCheck() {
    log.trace "exCheck..."
    def curMode = exTstat.currentState("thermostatMode").value.toString()
    if(getExTempOk()) {
        if(curMode.equals("off") && exRestoreMode && state?.exTurnedOff == true) {
            if(getExTempGoodDtSec() >= (getExOnDelayVal().toInteger() - 2)) {
                def lastMode = state?.exRestoreMode ?: curMode
                if(!state?.exRestoreMode.equals(curMode)) {
                    if(lastMode) {
                        if(setTstatMode(exTstat, lastMode)) {
                            state.exTurnedOff = false
                            LogAction("${exTstat?.label} has been restored to ${lastMode} Mode because External Temp is above Threshhold...", "info", true)
                            if(sendPushOnWc) {
                                parent?.sendMsg("Info", "${exTstat?.label} has been restored to ${lastMode} Mode because External Temp is above Threshhold...")
                            }
                        }
                    } else { LogAction("exCheck() | lastMode was not found...", "error", true) }
                }
            }
        } 
    }
    
    if (!getExTempOk()) {
        if(!curMode.equals("off")) {
            if(getExTempBadDtSec() >= (getExOffDelayVal().toInteger() - 2)) {
                log.debug "!getExTempsOk..."
                if(exRestoreMode) { 
                    state.exRestoreMode = curMode
                    log.debug "exRestoreMode Saved as: ${state?.exRestoreMode}"
                }
                log.debug("External Temp is at Threshhold turning off ${exTstat}")
                exTstat?.off()
                state.exTurnedOff = true
                LogAction("${exTstat.label} has been turned off because External Temp is at Threshhold", "info", true)
                if(sendPushOnEx) {
                    parent?.sendMsg("Alert", "${exTstat.label} has been turned off because External Temp is at Threshhold")
                }
            }
        } else { LogAction("exCheck() | Skipping change because mode is already 'Off'", "info", true) }
    }
}

def exTempEvt(evt) {
    log.debug "exTempEvt: ${evt?.value}"
    def schedOff = false
    def schedOn = false
    def curMode = exTstat?.currentState("thermostatMode").value.toString()
    def exOk = getExTempOk()
    log.debug "exOk: $exOk"
    if(exScheduleOk()) {
        if (!exOk && !curMode.equals("off")) {
            state.exTempGoodDt = getDtNow()
            log.debug "exTempEvt() | Scheduling Thermostat OFF in (${getExOffDelayVal()} seconds)..."
            runIn(getExOffDelayVal().toInteger(), "exCheck", [overwrite: true]) 
        }
        else if(exOk && (exRestoreMode && state?.exTurnedOff == true)) {
            state.exTempBadDt = getDtNow()
            log.debug "exTempEvt() | Scheduling Thermostat ON in (${getExOnDelayVal()} seconds)..."
            runIn(getExOnDelayVal().toInteger(), "exCheck", [overwrite: true])
        }
    }
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


/********************************************************************************  
|                			MODE AUTOMATION CODE	     						|
*********************************************************************************/
def modePresPage() {
    dynamicPage(name: "modePresPage", title: "Mode - Nest Home/Away Automation", uninstall: false) {
        if(!modePresSensor) {
            section("Set Nest Presence with ST Modes:") {
                input "homeModes", "mode", title: "Modes that set Nest 'Home'", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("mode_home_icon.png")
                input "awayModes", "mode", title: "Modes that set Nest 'Away'", multiple: true, submitOnChange: true, required: false,
                        image: getAppImg("mode_away_icon.png")
            }
        }
        section("Set Nest Presence Via Presence Sensor:") {
            paragraph "Choose a Presence Sensor(s) to use to set your Nest to Home/Away", image: getAppImg("instruct_icon")
            input "modePresSensor", "capability.presenceSensor", title: "Select a Presence Sensor", multiple: true, submitOnChange: true, required: false,
                    image: getAppImg("presence_icon.png")
            if(modePresSensor) {
                if (modePresSensor.size() > 1) {
                    paragraph "Nest will be set 'Away' when all Presence sensors leave and will return to 'Home' arrive", getAppImg("instruct_icon.png")
                }
                paragraph "Presence State: ${modePresSensor.currentPresence}", image: " "
                input (name: "useModePresSensorDelay", type: "bool", title: "Delay Changes?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("switch_icon.png"))
                if(useModePresSensorDelay) {
                    input "modePresSensorDelayVal", "enum", title: "Delay before Changing?", required: false, defaultValue: 60, metadata: [values:longTimeEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                }
            }
        }
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def modeWatcher(evt) { 
    log.debug "modeWatcher: $evt"
    if(!modePresSensor) {
        checkNestPresMode()
    } 
}

def modePresenceEvt(evt) {
    log.debug "modePresenceEvt: [${evt?.displayName}] is (${evt?.value})"
    def curNestPres = (getNestLocPres() == "home") ? "present" : "not present"
    if((evt?.value.toString() != curNestPres) ? true : false) {
        if(useModePresSensorDelay) {
            runIn(modePresSensorDelayVal.toInteger(), "setNestModeWithPresence", [overwrite: true])
        } else {
            setNestModeWithPresence()
        }
    }
}

def setNestModeWithPresence() {
    try {
        if(modePresSensor) {
            if(getModePresSenAway()) {
                LogAction("The selected Presence device(s) No Longer Present setting Nest 'Away'", "info", true)
                parent?.setStructureAway(null, true) 
            } else {
                LogAction("A selected Presence device(s) Present setting Nest 'Home'", "info", true)
                parent?.setStructureAway(null, false) 
            }
        }
    } catch (ex) {
        LogAction("setNestModeWithPresence() Exception: $ex", "error", true)
    }
}

def checkNestPresMode() { 
    try {
        def curMode = location.mode.toString()
        if (homeModes) {
            homeModes?.each { m ->
                if(m?.toString() == curMode) { 
                    LogAction("The mode ($location.mode) has triggered Nest 'Home'", "info", true)
                    parent?.setStructureAway(null, false) 
                }
            }  
        } 
        if (awayModes) {
            awayModes?.each { m ->
                if(m?.toString() == curMode) { 
                    LogAction("The mode ($location.mode) has triggered Nest 'Away'", "info", true)
                    parent?.setStructureAway(null, true) 
                }
            }
        }
    } catch (ex) {
        LogAction("checkNestPresMode() Exception: $ex", "error", true)
    }
}    

def getNestToStModeDelay() { return (nestToStModeDelay ? nestToStModeDelay * 60 : 60) }

def getModePresSenAway() {
    if(modePresSensor) {
        def pres = modePresSensor?.find { it?.currentPresence == "present" }
        return !pres ? true : false
    }
}

def getNestLocPres() {
    if(!parent?.locationPresence()) { return null }
    else {
        return parent?.locationPresence()
    }
}

/************************************************************************************************
|									LOGGING AND Diagnostic										|
*************************************************************************************************/

def getLongTimeEnumLabel(val) {
    def result = "unknown"
    if(val) {
        longTimeEnum().each { item ->
            if(item?.key.toString() == val?.toString()) { 
                result = item?.value
            }
        }
    } 
    return result
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
                            
private debugStatus() { return state?.appDebug ? "On" : "Off" } //Keep this
private childDebugStatus() { return state?.childDebug ? "On" : "Off" } //Keep this
private isAppDebug() { return state?.appDebug ? true : false } //Keep This

def formatDt(dt) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
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
            if(location?.timeZone) { day.setTimeZone(location?.timeZone) }
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
            if(m.toString() == location?.mode.toString()) { res = true }
        }  
    }
    return res
}

def time2Str(time) {
    if (time) {
        def t = timeToday(time, location?.timeZone)
        def f = new java.text.SimpleDateFormat("h:mm a")
        f?.setTimeZone(location.timeZone ?: timeZone(time))
        f?.format(t)
    }
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
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
private def textDesc()      { return "This this app adds, updates your Nest devices..." }
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