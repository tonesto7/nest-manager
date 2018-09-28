/**
 *  Rkteck25/nest-manager
 *
 *  Copyright 2018 Ronny Kister
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Rkteck25/nest-manager", namespace: "Rkteck25", author: "Ronny Kister") {
		capability "Activity Lighting Mode"
		capability "Actuator"
		capability "Air Conditioner Mode"
		capability "Air Quality Sensor"
		capability "Alarm"
		capability "Alarm System"
		capability "Alarm System Arm Only"
		capability "Audio Mute"
		capability "Audio Notification"
		capability "Bridge"
		capability "Buffered Video Capture"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// TODO: define your main and details tiles here
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'lightingMode' attribute
	// TODO: handle 'airConditionerMode' attribute
	// TODO: handle 'airQuality' attribute
	// TODO: handle 'alarm' attribute
	// TODO: handle 'alarmSystemStatus' attribute
	// TODO: handle 'alarmSystemStatus' attribute
	// TODO: handle 'mute' attribute
	// TODO: handle 'clip' attribute

}

// handle commands
def setLightingMode() {
	log.debug "Executing 'setLightingMode'"
	// TODO: handle 'setLightingMode' command
}

def setAirConditionerMode() {
	log.debug "Executing 'setAirConditionerMode'"
	// TODO: handle 'setAirConditionerMode' command
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

def strobe() {
	log.debug "Executing 'strobe'"
	// TODO: handle 'strobe' command
}

def siren() {
	log.debug "Executing 'siren'"
	// TODO: handle 'siren' command
}

def both() {
	log.debug "Executing 'both'"
	// TODO: handle 'both' command
}

def sendEvent(alarmSystemStatus,off)() {
	log.debug "Executing 'sendEvent(alarmSystemStatus,off)'"
	// TODO: handle 'sendEvent(alarmSystemStatus,off)' command
}

def sendEvent(alarmSystemStatus,stay)() {
	log.debug "Executing 'sendEvent(alarmSystemStatus,stay)'"
	// TODO: handle 'sendEvent(alarmSystemStatus,stay)' command
}

def sendEvent(alarmSystemStatus,away)() {
	log.debug "Executing 'sendEvent(alarmSystemStatus,away)'"
	// TODO: handle 'sendEvent(alarmSystemStatus,away)' command
}

def sendEvent(alarmSystemStatus,stay)() {
	log.debug "Executing 'sendEvent(alarmSystemStatus,stay)'"
	// TODO: handle 'sendEvent(alarmSystemStatus,stay)' command
}

def sendEvent(alarmSystemStatus,away)() {
	log.debug "Executing 'sendEvent(alarmSystemStatus,away)'"
	// TODO: handle 'sendEvent(alarmSystemStatus,away)' command
}

def setMute() {
	log.debug "Executing 'setMute'"
	// TODO: handle 'setMute' command
}

def mute() {
	log.debug "Executing 'mute'"
	// TODO: handle 'mute' command
}

def unmute() {
	log.debug "Executing 'unmute'"
	// TODO: handle 'unmute' command
}

def playTrack() {
	log.debug "Executing 'playTrack'"
	// TODO: handle 'playTrack' command
}

def playTrackAndResume() {
	log.debug "Executing 'playTrackAndResume'"
	// TODO: handle 'playTrackAndResume' command
}

def playTrackAndRestore() {
	log.debug "Executing 'playTrackAndRestore'"
	// TODO: handle 'playTrackAndRestore' command
}

def capture() {
	log.debug "Executing 'capture'"
	// TODO: handle 'capture' command
}