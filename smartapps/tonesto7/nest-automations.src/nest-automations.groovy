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

def appVersion() { "1.0.0" }
def appVerDate() { "4-11-2016" }
def appVerInfo() {
    
    "V1.0.0 (Apr 11th, 2016)\n" +
    "Added... Everything is new...\n\n" +
    "------------------------------------------------"
}

preferences {
    page(name: "mainPage", title: "Nest Automations", nextPage:"", content:"mainPage", uninstall: true, install:true)
    page(name: "prefsPage")
    page(name: "debugPrefPage")
    page(name: "automationsPage")
    page(name: "extSensorPage")
    page(name: "wcPage")
    page(name: "modePresPage")
    page(name: "extTempsPage")
    page(name: "extSenShowTempsPage")
}

def mainPage() {
    //log.trace "mainPage()"
    state?.tempUnit = getTemperatureScale().toString()
    return dynamicPage(name: "mainPage", title: "Automation Page...", uninstall: false) {
        section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
            def senDayDesc = (extSensorDay) ? ("Day Sensor${(extSensorDay?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(extSensorDay)}°${state?.tempUnit}") : ""
            def senNightDesc = (extSensorNight) ? ("\nNight Sensor${(extSensorNight?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(extSensorNight)}°${state?.tempUnit}") : ""
            def senEnabDesc = !state?.extSenEnabled ? "External Sensor Disabled...\n" : ""
            def motInUse = extMotionSensors ? "\nMotion Triggers Active" : ""
            def senModes = extSenModes ? "\nMode Filters Active" : ""
            def senSetTemps = (extSenHeatTemp && extSenCoolTemp) ? "\nSet Temps: (Heat/Cool: ${extSenHeatTemp}°${state?.tempUnit}/${extSenCoolTemp}°${state?.tempUnit})" : ""
            def senRuleType = extSenRuleType ? "\nRule-Type: ${extSenRuleName()}" : ""
            def extSenDesc = ((extSensorDay || extSensorNight) && extSenHeatTemp && extSenCoolTemp && extSenTstat) ? 
                "${senEnabDesc}Thermostat Temp: ${getDeviceTemp(extSenTstat)}°${state?.tempUnit}\n${senDayDesc}${senNightDesc}${senSetTemps}${senRuleType}${motInUse}${senModes}\nTap to Modify..." : "Tap to Configure..."
            href "extSensorPage", title: "Use Remote Sensors...", description: extSenDesc, state: extSenDesc, image: imgIcon("remote_sensor_icon.png")
           }
        section("Turn a Thermostat Off when a Window or Door is Open:") {
            def qOpt = (wcModes || wcDays || (wcStartTime && wcStopTime)) ? "Schedule Options Selected...\n" : ""
            def desc = (wcContacts && wcTstat) ? "${wcTstat.label}\nwith (${wcContacts ? wcContacts.size() : 0}) Contact(s)\n${qOpt}\nTap to Modify..." : "Tap to Configure..."
            href "wcPage", title: "Configure Sensors to Watch...", description: desc, image: imgIcon("open_contact.png")
           }
        section("Turn a Thermostat Off based on Outside temps:") {
            def qOpt = (exModes || exDays || (exStartTime && exStopTime)) ? "Schedule Options Selected...\n" : ""
            def desc = (!exUseWeather && exTemp && exTstat) ? ("${exTstat?.label}\nwith External Temp Sensor\n${qOpt}\nTap to Modify...") : (exUseWeather ? "${exTstat?.label}\nwith External Weather\n${qOpt}\nTap to Modify..." : "Tap to Configure...")
            href "extTempsPage", title: "Turn off based on External Temps...", description: desc, image: imgIcon("external_temp_icon.png")
           }
        section("Set Nest Presence Based on ST Modes:") {
            def presDesc = (awayModes || homeModes) ? "${homeModes ? "Home: $homeModes" : ""}${awayModes ? "\n\nAway: $awayModes" : ""}\n\nTap to Modify..." : "Tap to Configure..."
            href "modePresPage", title: "Mode Automations", description: presDesc, image: imgIcon("mode_automation_icon.png")
           }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has been installed...")
    parent?.autoAppInst(true)
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
    getAutomationsActive()
    parent?.autoAppInst(true)
    sendNotificationEvent("${textAppName()} has updated settings...")
}

def uninstalled() {
    //sends notification of uninstall
    sendNotificationEvent("${textAppName()} is uninstalled...")
    parent?.autoAppInst(false)
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
    parent.automationsActive(((remActive || conActive || nestModesActive) ? true : false), autoDesc)
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
        extSenEvtEval()
    }
    
    if(homeModes || awayModes) { subscribe(location, "mode", modeWatcher, [filterEvents: false]) }
    if(wcContacts) { subscribe(wcContacts, "contact", wcContactEvt) }
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
        section("Choose a Thermostat... ") {
            input "extSenTstat", "capability.thermostat", title: "Which Thermostat?", submitOnChange: true, required: req, image: imgIcon("thermostat_icon.png")
            if(extSenTstat) { 
                def tmpVal = "${getDeviceTemp(extSenTstat)}°${state?.tempUnit}"    
                paragraph "Thermostat Room Temp: ${tmpVal}", image: " "
                input "extSenTstatsMirror", "capability.thermostat", title: "Mirror Changes on these Thermostats", multiple: true, submitOnChange: true, required: false,
                        image: imgIcon("thermostat_icon.png")
                if(extSenTstatsMirror) { 
                    extSenTstatsMirror.each { t ->
                    	paragraph "Thermostat Temp: ${getDeviceTemp(t)}${state?.tempUnit}", image: " "
                    }
                }
            }
        }
        section("Choose Daytime Temperature Sensor(s) to use instead of the Thermostat's...") {
            input "extSensorDay", "capability.temperatureMeasurement", title: "Daytime Temp Sensors...", submitOnChange: true, required: ((!extSensorNight && !extSensorDay && extSenTstat) ? true : false),
            		multiple: true, image: imgIcon("temperature_icon.png")
            if(extSensorDay) {
                input "extSensorDayModes", "mode", title: "Daytime Modes...", multiple: true, submitOnChange: true, required: (extSensorDay ? true : false),
                        image: imgIcon("mode_icon.png")
                def tmpVal = "Day Sensor Temp${(extSensorDay?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(extSensorDay)}°${state?.tempUnit}"
                if(extSensorDay.size() > 1) {
                	href "extSenShowTempsPage", title: "View Day Sensor Temps...", description: tmpVal, image: " "
                    paragraph "When multiple Sensors are selected the Temp will become the average of those sensors."
                } else { paragraph "${tmpVal}", image: " " }
            }
         }
         section("Choose NightTime Temperature Sensor(s) to use instead of the Thermostat's...") {
            input "extSensorNight", "capability.temperatureMeasurement", title: "NightTime Temp Sensors", submitOnChange: true, required: ((!extSensorNight && !extSensorDay && extSenTstat) ? true : false), 
            		multiple: true, image: imgIcon("temperature_icon.png")
            if(extSensorNight) {
                input "extSensorNightModes", "mode", title: "NightTime Modes...", multiple: true, submitOnChange: true, required: (extSensorNight ? true : false),
                        image: imgIcon("mode_icon.png")
                def tmpVal = "Night Sensor Temp${(extSensorNight?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(extSensorNight)}°${state?.tempUnit}"
                if(extSensorNight.size() > 1) {
                	href "extSenShowTempsPage", title: "View Night Sensor Temps...", description: tmpVal, image: " "
                    paragraph "When multiple Sensors are selected the Temp will become the average of those sensors."
                } else { paragraph "${tmpVal}", image: " " }
            }
        }
        if(extSenTstat && (extSensorDay || extSensorNight)) {
            section("Select an Action Type:") {
                   input(name: "extSenRuleType", type: "enum", title: "Action Type", options: extSenRuleEnum(), required: true, submitOnChange: true,
                    image: imgIcon("rule_icon.png"))
            }
            section("Desired Temperatures..." ) {
                input "extSenHeatTemp", "number", title: "Set Heat Temp (°${state?.tempUnit})", submitOnChange: true, image: imgIcon("heat_icon.png")
                input "extSenCoolTemp", "number", title: "Set Cool Temp (°${state?.tempUnit})", submitOnChange: true, image: imgIcon("cool_icon.png")
            }
            section("Optionally Evaluate Temps when these sensors detect motion... ") {
                input "extMotionSensors", "capability.motionSensor", title: "Motion Sensors", required: false, multiple: true, submitOnChange: true,
                        image: imgIcon("motion_icon.png")
            }
            section ("Options") {
                input "extSenModes", "mode", title: "Only in These Modes?", multiple: true, required: false, submitOnChange: true, image: imgIcon("mode_icon.png")
                input "extTimeBetweenRuns", "number", title: "Time Between Fan Runs (In Minutes)", required: true, defaultValue: 60, submitOnChange: true
                input "degreesOfSeperation", "number", title: "Temp Threshold to Trigger Fan Run (degrees)", required: true, defaultValue: 3, submitOnChange: true
            }
        }
        if (((extSensorDay && extSensorDayModes) || (extSensorNight && extSensorNightModes)) && extSenTstat && extSenHeatTemp && extSenCoolTemp) {
            section("Enable or Disable Remote Sensor Once Configured...") {
                input (name: "extSenEnabled", type: "bool", title: "Enable Remote Sensor Automation?", required: false, defaultValue: true, submitOnChange: true, image: imgIcon("switch_icon.png"))
                state?.extSenEnabled = extSenEnabled ? true : false
            }
        } else { state?.extSenEnabled = false }
    }
}

def extSenRuleEnum() {
	def vals = [ "H":"Heat","C":"Cool","H_C":"Auto","H_Circ":"Heat/Circulate","C_Circ":"Cool/Circulate","H_C_Circ":"Auto/Circulate" ]
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
            section("Day Sensor Temps:") {
                extSensorDay.each { t ->
                    paragraph "${t.label}: ${getDeviceTemp(t)}°${state?.tempUnit}"
                }
            }
            section("Average Temp of Day Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(extSensorDay)}°${state?.tempUnit}"
            }
        }
        if(extSensorNight) { 
            section("Night Sensor Temps:") {
                extSensorNight.each { t ->
                    paragraph "${t.label}: ${getDeviceTemp(t)}°${state?.tempUnit}"
                }
            }
            section("Average Temp of Night Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(extSensorNight)}°${state?.tempUnit}"
            }
        }
    }
}

def locationChgEvt(evt) {
    log.debug "locationChgEvt mode: $evt.value, heat: $heat, cool: $cool"
    extSenEvtEval()
}
def extSenMotionEvt(evt) {
    log.debug "extSenMotionEvt event: $evt.value"
    extSenEvtEval()
}
def extSenTempEvt(evt) {
    extSenEvtEval()
}

// Was based off of Keep Me Cozy II
private extSenEvtEval() {
    if (state?.extSenEnabled && modesOk(extSenModes) && (extSensorDay || extSensorNight) && extSenTstat && getExtModeOk()) {
        def threshold = degreesOfSeperation.toInteger()
        def hvacMode = extSenTstat?.currentThermostatMode.toString() 
        def curTstatTemp = getDeviceTemp(extSenTstat).toInteger()
        def curTstatOperState = extSenTstat?.currentThermostatOperatingState.toString()
        def curTstatFanMode = extSenTstat?.currentThermostatFanMode
        def curCoolSetpoint = extSenTstat?.currentCoolingSetpoint.toInteger()
        def curHeatSetpoint = extSenTstat?.currentHeatingSetpoint.toInteger()
        def curSenTemp = (extSensorDay || extSensorNight) ? getRemoteSenTemp() : null
        log.trace "Remote Sensor Rule Type: ${extSenRuleType}"
        log.trace "Remote Sensor Temp: ${curSenTemp}"
        log.trace "Thermostat Info - Temperature: $curTstatTemp | HeatSetpoint: $curHeatSetpoint | CoolSetpoint: $curCoolSetpoint | HvacMode: $hvacMode | OperatingState: $curTstatOperState | FanMode: $curTstatFanMode" 
        log.trace "Desired Temps - Heat: $extSenHeatTemp | Cool: $extSenCoolTemp"
        if (hvacMode in ["cool","auto"]) {
            if ((curSenTemp - extSenCoolTemp.toInteger()) > threshold) {
                if(extSenRuleType in ["C", "H_C", "H_C_Circ"]) {
                    extSenTstat?.setCoolingSetpoint(curTstatTemp - 2)
                    if(extSenTstatsMirror) { extSenTstatsMirror*.setCoolingSetpoint(curTstatTemp - 2) }
                    log.debug "extSenTstat.setCoolingSetpoint(${curTstatTemp - 2}), ON"
                }
            }
            else if (((extSenCoolTemp.toInteger() - curSenTemp) >= threshold) && (curTstatTemp - curCoolSetpoint) >= threshold) {
                if(extSenRuleType in ["C", "H_C", "H_C_Circ"]) {
                    extSenTstat?.setCoolingSetpoint(curTstatTemp + 2)
                    if(extSenTstatsMirror) { extSenTstatsMirror*.setCoolingSetpoint(curTstatTemp - 2) }
                    log.debug "extSenTstat.setCoolingSetpoint(${curTstatTemp + 2}), OFF"
                }
            } else {
                  if((extSenRuleType in ["C_Circ", "H_C_Circ"]) && (Math.abs(curSenTemp - curTstatTemp.toInteger()) <= threshold)) {
                    if(getFanRunOk(curTstatOperState, curTstatFanMode)) {
                        log.debug "Running Fan for Circulation on $extSenTstat"
                        extSenTstat?.fanOn()
                        if(extSenTstatsMirror) { 
                            extSenTstatsMirror.each { mt -> 
                                log.debug "Mirroring Fan Run for Circulation on $mt"
                                mt?.fanOn() 
                            }
                        }
                        atomicState?.lastFanRunDt = getDtNow()
                    }
                }
            }
        }
        
        if (hvacMode in ["heat", "emergency heat", "auto"]) {
            if ((extSenHeatTemp.toInteger() - curSenTemp) > threshold) {
                if(extSenRuleType in ["H", "H_C", "H_C_Circ"]) { 
                    extSenTstat?.setHeatingSetpoint(curTstatTemp + 2)
                    if(extSenTstatsMirror) { extSenTstatsMirror*.setHeatingSetpoint(curTstatTemp + 2) }
                    log.debug "extSenTstat.setHeatingSetpoint(${curTstatTemp + 2}), ON"
                }
            }
            else if (((curSenTemp - extSenHeatTemp) >= threshold) && (curHeatSetpoint - curTstatTemp) > threshold) {
                if(extSenRuleType in ["H", "H_C", "H_C_Circ"]) {
                    extSenTstat?.setHeatingSetpoint(curTstatTemp - 2)
                    if(extSenTstatsMirror) { extSenTstatsMirror*.setHeatingSetpoint(curTstatTemp - 2) }
                    log.debug "extSenTstatermostat.setHeatingSetpoint(${curTstatTemp - 2}), OFF"
                }
            } else { 
                log.debug "heat fan: ${Math.abs(curSenTemp - extSenHeatTemp.toInteger())}"
                if ((extSenRuleType in ["H_Circ", "H_C_Circ"]) && (Math.abs(curSenTemp - extSenHeatTemp.toInteger()) <= threshold)) {
                    if(getFanRunOk(curTstatOperState, curTstatFanMode)) {
                        log.debug "Running Fan for Circulation on $extSenTstat"
                        extSenTstat?.fanOn()
                        if(extSenTstatsMirror) { 
                            extSenTstatsMirror.each { mt -> 
                                log.debug "Mirroring Fan Run for Circulation on $mt"
                                mt?.fanOn() 
                            }
                        }
                        atomicState?.lastFanRunDt = getDtNow()
                    }
                }
            }
        }
    }
    else {
        extSenTstat?.setHeatingSetpoint(extSenHeatTemp.toDouble())
        extSenTstat?.setCoolingSetpoint(extSenCoolTemp.toDouble())
        if(extSenTstatsMirror) {
            extSenTstatsMirror*.setHeatingSetpoint(extSenHeatTemp.toDouble())
            extSenTstatsMirror*.setCoolingSetpoint(extSenCoolTemp.toDouble())
        }
    }
}

// for backward compatibility with existing subscriptions
def coolingSetpointHandler(evt) {
    log.debug "coolingSetpointHandler()"
}
def heatingSetpointHandler(evt) {
    log.debug "heatingSetpointHandler()"
}

def getExtModeOk() {
	if(extSensorDayModes || extSensorNightModes) {
    	return(isInMode(extSensorDayModes) || isInMode(extSensorNightModes)) ? true : false
    }
    return false
}
def getFanRunOk(operState, fanState) { 
    def val = extTimeBetweenRuns ? extTimeBetweenRuns.toInteger() * 60 : 3600
    def cond = ((extSenRuleType in ["H_Circ", "C_Circ", "H_C_Circ"]) && operState == "idle" && fanState == "auto") ? true : false
    def timeSince = (getLastFanRunDtSec() > val)
    def result = (timeSince && cond) ? true : false
    log.debug "getFanRunOk(): cond: $cond | timeSince: $timeSince | val: $val | $result"
    return result
}

def getLastFanRunDtSec() { return !state?.lastFanRunDt ? 100000 : GetTimeDiffSeconds(state?.lastFanRunDt).toInteger() }
def getDeviceTemp(dev) {
    return dev ? dev?.currentValue("temperature").toString().replaceAll("\\[|\\]", "") : null
}

def getRemoteSenTemp() {
    if(extSensorDay) {
        return getDeviceTempAvg(extSensorDay)
    }
    else if(extSensorNight) {
        return getDeviceTempAvg(extSensorNight)        
    }
    else {
        return 0
    }
}

def getDeviceTempAvg(items) {
    def tmpAvg = []
    def tempVal = 0
    if(!items) { return tempVal }
    else if(items?.size() > 1) {
        tmpAvg = items*.currentTemperature
        if(tmpAvg && tmpAvg.size() > 1) {
            tempVal = Math.round(tmpAvg.sum() / tmpAvg.size())
        }
    } 
    else { tempVal = getDeviceTemp(items) }
    return tempVal.toInteger()
}

/******************************************************************************  
|                			WATCH CONTACTS AUTOMATION CODE	                  |
*******************************************************************************/

def wcPage() {
    dynamicPage(name: "wcPage", title: "Thermostat/Contact Automation", uninstall: false) {
        section("When These Contacts are open, Turn Off this Thermostat") {
            def req = (wcContacts || wcTstat) ? true : false
            input name: "wcContacts", type: "capability.contactSensor", title: "Which Contact(s)?", multiple: true, submitOnChange: true, required: req,
                    image: imgIcon("contact_icon.png")
            input name: "wcTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: imgIcon("thermostat_icon.png")
            if(wcTstat) {
                input name: "wcTstatMir", type: "capability.thermostat", title: "Mirror commands to these Thermostats?", multiple: true, submitOnChange: true, required: false,
                    image: imgIcon("thermostat_icon.png")
            }
        }
        if(wcContacts && wcTstat) {
            section("Only During these Days, Times, or Modes:") {
                def timeReq = (wcStartTime || wcStopTime) ? true : false
                input "wcStartTime", "time", title: "Start time", submitOnChange: true, required: timeReq, 
                        image: imgIcon("start_time_icon.png")
                input "wcStopTime", "time", title: "Stop time", submitOnChange: true, required: timeReq,
                        image: imgIcon("stop_time_icon.png")

                input "wcModes", "mode", title: "Only with These Modes...", multiple: true, submitOnChange: true, required: false,
                        image: imgIcon("mode_icon.png")
                input "wcDays", "enum", title: "Only on certain days of the week", multiple: true, required: false, submitOnChange: true,
                        options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
                        image: imgIcon("day_calendar_icon.png")
            }
            section("Delay Values:") {
                input name: "wcOffDelay", type: "number", title: "Delay Off (in minutes)", defaultValue: 5, required: false, submitOnChange: true,
                        image: imgIcon("delay_time_icon.png")

                input "restModeOnClose", "bool", title: "Restore Previous mode after Closed?", required: false, defaultValue: false, submitOnChange: true,
                        image: imgIcon("restore_icon.png")
                if(restModeOnClose) {
                    input name: "wcOnDelay", type: "number", title: "Delay On (in minutes)", defaultValue: 5, required: false, submitOnChange: true,
                        image: imgIcon("delay_time_icon.png")
                }
            }
            section("Notifications:") {
                input "sendPushOnWc", "bool", title: "Send Push Notifications on Changes?", required: false, defaultValue: true, submitOnChange: true,
                        image: imgIcon("notification_icon.png")
            }
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

def getWcContactsOk() { return !wcContacts.currentState("contact").value.contains("open") ? true : false }
def watchContactOk() { return (!wcContacts && !wcTstat) ? false : true }
def wcScheduleOk() { return (modesOk(wcModes) && daysOk(wcDays) && wcTimeOk()) ? true : false }
def getWcOpenDtSec() { return !state?.wcOpenDt ? 100000 : GetTimeDiffSeconds(state?.wcOpenDt).toInteger() }
def getWcCloseDtSec() { return !state?.wcCloseDt ? 100000 : GetTimeDiffSeconds(state?.wcCloseDt).toInteger() }
def getWcOffDelayVal() { return !wcOffDelay ? 360 : (wcOffDelay.toInteger() * 60) }
def getWcOnDelayVal() { return !wcOnDelay ? 360 : (wcOnDelay.toInteger() * 60) }

def wcCheck() {
    log.trace "wcCheck..."
    def curMode = wcTstat.currentState("thermostatMode").value.toString()
    if(getWcContactsOk()) {
        if(curMode.equals("off") && restModeOnClose && state?.wcTurnedOff == true) {
            if(getWcCloseDtSec() >= (getWcOnDelayVal().toInteger() - 2)) {
                def lastMode = state?.wcRestoreMode ?: curMode
                if(!state?.wcRestoreMode.equals(curMode)) {
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
        if(!curMode.equals("off")) {
            if(getWcOpenDtSec() >= (getWcOffDelayVal().toInteger() - 2)) {
                log.debug "!getWcContactsOk..."
                if(restModeOnClose) { 
                    state.wcRestoreMode = curMode
                    log.debug "restoreToMode Set to: ${state?.wcRestoreMode}"
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
    //log.debug "watchContactEvt: ${evt.value}"
    def schedOff = false
    def schedOn = false
    def curMode = wcTstat.currentState("thermostatMode").value.toString()
    def conVal = evt.value.toString()
    def wcOk = getWcContactsOk()
    state.wcState = (evt.value == "closed") ? "closed" : "open"
    if(wcScheduleOk()) {
           if (conVal == "open" && !curMode == "off") {
               state.wcOpenDt = getDtNow()
            log.debug "wcContactEvt() | Scheduling Thermostat OFF in (${getWcOffDelayVal()} seconds)..."
            runIn(getWcOffDelayVal().toInteger(), "wcCheck", [overwrite: true]) 
        }
        else if(conVal == "closed" && (restModeOnClose && curMode == "off" && state?.wcTurnedOff == true)) {
            state.wcCloseDt = getDtNow()
               log.debug "wcContactEvt() | Scheduling Thermostat ON in (${getWcOnDelayVal()} seconds)..."
            runIn(getWcOnDelayVal().toInteger(), "wcCheck", [overwrite: true])
        }
    }
}

/********************************************************************************  
|                			MODE AUTOMATION CODE	     						|
*********************************************************************************/
def modePresPage() {
    dynamicPage(name: "modePresPage", title: "Mode - Nest Home/Away Automation", uninstall: false) {
        section("Change Nest Presence with ST Modes:") {
            input "homeModes", "mode", title: "Modes that set Nest 'Home'", multiple: true, submitOnChange: true, required: false,
                    image: imgIcon("mode_home_icon.png")
            input "awayModes", "mode", title: "Modes that set Nest 'Away'", multiple: true, submitOnChange: true, required: false,
                    image: imgIcon("mode_away_icon.png")
        }
        /*section("Nest Presence Set's SmartThings Mode:") {
            input "homeStMode", "mode", title: "Set this Mode When Nest 'Home'...", multiple: false, submitOnChange: true, required: false,
                    image: imgIcon("app_pres_home_Icon.png")
            input "awayStMode", "mode", title: "Set this Mode When Nest 'Away'...", multiple: false, submitOnChange: true, required: false,
                    image: imgIcon("app_pres_away_Icon.png")
            input name: "nestToStModeDelay", type: "decimal", title: "Delay this Number of minutes?", defaultValue: 1, required: false,
                    image: imgIcon("delay_time_icon.png")
        }*/
    }
}

def modeWatcher(evt) { 
    log.debug "modeWatcher: $evt.value"
    checkNestPresMode() 
}

def checkNestPresMode() { 
    def curMode = location.mode.toString()
    if (homeModes) {
        homeModes?.each { m ->
            if(m?.toString() == curMode) { 
                LogAction("The mode ($location.mode) has triggered Nest 'Home'", "info", true)
                setStructureAway(null, false) 
            }
         }  
    } 
       if (awayModes) {
        awayModes?.each { m ->
            if(m?.toString() == curMode) { 
                   LogAction("The mode ($location.mode) has triggered Nest 'Away'", "info", true)
                setStructureAway(null, true) 
            }
        }
    }
}    

def getNestToStModeDelay() { return (nestToStModeDelay ? nestToStModeDelay * 60 : 60) }


/********************************************************************************  
|                			External Temp AUTOMATION CODE	     				|
*********************************************************************************/

def extTempsPage() {
    dynamicPage(name: "extTempsPage", title: "Thermostat/External Temps Automation", uninstall: false) {
        section("When External Temp reaches Turn Off this Thermostat when the Local Weather temp goes above a certain threshold.  ") {
            def req = ((exUseWeather || (!exUseWeather && exTemp)) || Tstat) ? true : false
            input "exUseWeather", "bool", title: "Use Local Weather as External Sensor?", required: req, defaultValue: false, submitOnChange: true,
                    image: imgIcon("weather_icon.png")
            if(exUseWeather){
                getExtConditions()
                def tmpVal = (location?.temperatureScale == "C") ? state?.curWeatherTemp_c : state?.curWeatherTemp_f
                paragraph "Current Weather Temp: $tmpVal", image: " "
                input name: "locZipcode", type: "number", title: "Custom ZipCode (Default is Hub Loction)?", submitOnChange: true, required: false,
                        image: imgIcon("location_icon.png")
                
                input name: "weatherRfrshVal", type: "number", title: "Update Weather (in Minutes)?", default: 5, submitOnChange: true, required: false,
                        image: imgIcon("start_time_icon.png")
            }
            if(!exUseWeather) {
                input "exTemp", "capability.temperatureMeasurement", title: "Which Temperature Sensors?", submitOnChange: true, multiple: false, required: req, 
                        image: imgIcon("temperature_icon.png")
                if(exTemp) {
                    def tmpVal = "${exTemp?.currentValue("temperature").toString()}${location?.temperatureScale.toString()}"
                    paragraph "Current Sensor Temp: ${tmpVal}", image: " "
                }
            }
            input name: "exTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: imgIcon("thermostat_icon.png")
            if(exTstat) {
                def tmpVal = "${exTstat?.currentValue("temperature").toString()}${location?.temperatureScale.toString()}"
                    paragraph "Current Thermostat Temp: ${tmpVal}", image: " "
                input name: "exTempDiffVal", type: "number", title: "When Inside Temp is within this many Degrees of External Temp?", range: "-30..30", default: 0, submitOnChange: true, required: false,
                        image: imgIcon("temp_icon.png")
            }
        }
        if((exUseWeather || exTemp) && exTstat) {
            section("Only During these Days, Times, or Modes:") {
                def timeReq = (exStartTime || exStopTime) ? true : false
                input "exStartTime", "time", title: "Start time", submitOnChange: true, required: timeReq, 
                                image: imgIcon("start_time_icon.png")
                input "exStopTime", "time", title: "Stop time", submitOnChange: true, required: timeReq,
                                image: imgIcon("stop_time_icon.png")

                input "exModes", "mode", title: "Only with These Modes...", multiple: true, submitOnChange: true, required: false,
                                image: imgIcon("mode_icon.png")
                input "exDays", "enum", title: "Only on certain days of the week", multiple: true, required: false, submitOnChange: true,
                                options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
                                image: imgIcon("day_calendar_icon.png")
            }
            section("Delay Values:") {
                input name: "exOffDelay", type: "number", title: "Delay Off (in minutes)", defaultValue: 5, required: false, submitOnChange: true,
                                image: imgIcon("delay_time_icon.png")

                input "exRestoreMode", "bool", title: "Restore Previous Mode when Temp goes below Threshold?", required: false, defaultValue: false, submitOnChange: true,
                                image: imgIcon("restore_icon.png")
                if(exRestoreMode) {
                    input name: "exOnDelay", type: "number", title: "Delay On (in minutes)", defaultValue: 5, required: false, submitOnChange: true,
                                image: imgIcon("delay_time_icon.png")
                }
            }
            section("Notifications:") {
                input "sendPushOnEx", "bool", title: "Send Push Notifications on Changes?", required: false, defaultValue: true, submitOnChange: true,
                                image: imgIcon("notification_icon.png")
            }
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
def getExOffDelayVal() { return !exOffDelay ? 360 : (exOffDelay.toInteger() * 60) }
def getExOnDelayVal() { return !exOnDelay ? 360 : (exOnDelay.toInteger() * 60) }
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
                    }
                    else { LogAction("exCheck() | lastMode was not found...", "error", true) }
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
    log.debug "exTempEvt: ${evt.value}"
    def schedOff = false
    def schedOn = false
    def curMode = exTstat.currentState("thermostatMode").value.toString()
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


/************************************************************************************************
|									LOGGING AND Diagnostic										|
*************************************************************************************************/

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
def imgIcon(imgName, on = null) 	{ return (!parent?.settings?.disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/$imgName" : "" }
                            
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
            if(location?.timeZone) { day.setTimeZone(location?.timeZone) }
            return dayVals.contains(day.format(new Date())) ? false : true
        } else { return true }
    } catch (ex) { LogAction("daysOk() Exception: ${ex}", "error", true, true) }
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

def isInMode(modeList) {
    if (modeList) {
        modeList?.each { m ->
            if(m.toString() == location?.mode.toString()) { return true }
         }  
        return false
    }
    return false
}

def time2Str(time) {
    if (time) {
        def t = timeToday(time, location?.timeZone)
        def f = new java.text.SimpleDateFormat("h:mm a")
        f.setTimeZone(location.timeZone ?: timeZone(time))
        f.format(t)
    }
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

/******************************************************************************  
*                Application Help and License Info Variables                  *
*******************************************************************************/
//Change This to rename the Default App Name
private def appName() 		{ "Nest Automations" }
private def appAuthor() 	{ "Anthony S." }
private def appParent() 	{ "tonesto7:Nest Manager" }
private def appNamespace() 	{ "tonesto7" }
private def appInfoDesc() 	{ 
    //def cur = state?.appData?.versions?.app?.ver.toString()
    //def ver = (textVersion() != cur) ? "${textVersion()} (Lastest: v${cur})" : textVersion()
    //return "Name: ${textAppName()}\n${ver}\n${textModified()}" 
}
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

// Allow selection of other thermostats to mirror the automation functions of the primary thermostat.

/********************************************************************************  
|                			Nest to ST AUTOMATION CODE	     					|
*********************************************************************************/

/*def updLocInfo() {
    state.nestLocMode = parent?.locationPresence() ? parent?.locationPresence().toString() : null
    log.debug "updLocInfo: ${state?.nestLocMode}"
    return null
}

def nestModetoStMode() { 
    if(stHomeMode || stAwayMode) { checkNestToStMode() }
}
def getNestToStModeDtSec() { return !state?.nestToStModeDt ? 100000 : GetTimeDiffSeconds(atomicState?.nestToStModeDt).toInteger() }

def checkNestToStMode() {
    def nestPresent = (state?.nestLocMode == "home") ? true : false
    def sched = false
    if(nestPresent && stHomeMode) {
        def homeMd = stHomeMode?.toString()
        if (curMode != homeMd) {
            state.nestToStModeDt = getDtNow()
            if (location.modes?.find{it.name == homeMd}) {
                if( getNestToStModeDtSec() > getNestToStModeDelay()) {
                    setLocationMode(homeMd)
                    sched = false
                } else {
                    sched = true
                }
            } else {
                log.warn "Tried to change to undefined mode '${homeMd}'"
            }
        }
    }
    else if (!nestPresent && stAwayMode) {
        def awayMd = stAwayMode?.toString()
        if (curMode != awayMd) {
            state.nestToStModeDt = getDtNow()
            if (location.modes?.find{it.name == awayMd}) {
                if( getNestToStModeDtSec() > getNestToStModeDelay()) {
                    setLocationMode(awayMd)
                    sched = false
                } else {
                    sched = true
                }
            } else {
                log.warn "Tried to change to undefined mode '${awayMd}'"
            }
        }
    }
    
    if(sched) {
        if(!canSchedule()) { log.warn "Too Many Schedules Will Try Again later" }
        else { runIn( getNestToStModeDelay(), "checkNestToStMode", [overwrite: true]) }
    }
    else if (!sched) { unschedule("checkNestToStMode") }
}*/