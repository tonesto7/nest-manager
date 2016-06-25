# Nest Manager (Unofficial)

#####IMPORTANT: **Nest Automations has been merged into Nest Manager in a way that they are still 2 seperate apps but under one code base.  As such it will require you to remove your existing automations before upgrading.**

### Nest Manager App
This is the "***unofficial***" SmartThings user created SmartApp and Device handlers.
The SmartApp and Device Handlers work together to provide integration to the SmartThings ecosystem using Nest's Official API. 

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_newInst.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_1.png" width="281" height="500">

### Nest Automations App
This is a Child-SmartApp that allows you to creat different types of automations for your HVAC systems.

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/AutomationApp/automation_start.png" width="281" height="500">

### The Devices Types
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat2.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat3.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_protect.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_protect2.PNG" width="281" height="500">
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_weather.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_weather2.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_nest_pres_dev.png" width="281" height="500">

## Author
* @tonesto7

## Contributors 
* @desertblade
* @E_sch

## Testers
* @scpickle

## Version Info
__Latest App Version:__ |
----------- | -----------
Nest Manager | __*v2.5.0*__

__Latest Device Versions:__ |
----------- | -----------
Nest Presence Device | __*v2.5.0*__
Nest Protect Device | __*v2.5.0*__
Nest Thermostat Device | __*v2.5.0*__
Nest Weather Device | __*v2.5.0*__

## What's New
***Manager App:***

 * **NEW**: Merged Manager and Automations into one codebase but it is still two apps... Thanks @ady264"
 * **NEW**: Thermostat ST Mode TempSetpoint Automation to select your thermostats and each mode to use for that thermostat and then choose the heat/cool setpoints for each mode. This is completely dynamic and will allow different setpoints for each thermostat selected.
 * **NEW**: Remote Sensors now allows selection of switches to run along with the thermostat to help with comfort. This support includes automation detection of devices that support 3-speeds, and allows setting speed based on individual threshold temps.
 * **ADDED**: Ability to Disable each automations individually.
 * **ADDED**: You can now receive Local Weather Alerts push notifications when the Nest Weather device is installed.
 * **UPDATED**: There is new install setup now that flows much better and allows display of the important available options better to users.
 * **UPDATED**: Device data updates have been modified to be much more efficient. All necessary data is sent at once eliminating the need for the devices to call back to the manager app constantly.
 * **NEW**: Analytics have been added to the app to share very basic generic installation data, it will also send generic exception error data. So I can see trends among versions etc.  This data is completely transparent to you and can be disabled at any time.  I do not collect and identifiable data only the basics. We will also be putting up a dashboard for users who are nerdy just to see the data mapped out.
 * **FIXED**: Remote Sensor's Fan circulation should now work like it was intended.
 * **FIXED**: Nest Log Out function to actually take you back to auth screen after clearing token.
 * **ADDED**: You can now use Day, Time, Mode filters in most Nest Automations.
 * **ADDED**: View all Apps/Devices state data under diagnostics.
 * **ADDED**: Voice Notifications using Speech or MusicPlayer devices to Contact Automations Events work correctly with contact automation.
 * **ADDED**: App now supports Broadcast message from developer.
 * **NEW**: When updates are available there is a link in the smartapp that takes you directly to the IDE in your mobile browser.
 * **UPDATED**: Tapping on the Nest Manager version app top of page will now take you a Changelog page which displays those changes.
 * **UPDATED**: Lot's of tweaks and fixes for annoying UI bugs and to many subtle changes to list.

***Weather Device:***
 * **Updated** @desertblade remodeled the design to allow for modal popups for weather alerts, and forecast data.

## Links
#### [GitHub Project Issues Link](https://github.com/tonesto7/nest-manager/issues)

#### [SmartThings Community Forum Link](https://community.smartthings.com/t/release-nest-manager/)

#### [Projects Help Page](https://cdn.rawgit.com/tonesto7/nest-manager/master/Documents/help-page.html)

#### [SmartThings IDE GitHub Integration Instructions](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html)

## Things to Know
 * __This is still technically in still in post-BETA so you may experience issues!!!__
 * This app ***DOES NOT*** support Nest Cams **currently** mainly because I don't have any to test
 * Each install of this SmartApp will only support **One** location/structure and the Thermostats and Protects within
 * This version uses a new token which when we are ready to support it will allow access to Nest cams without needing to completely re-install everything. 
 * _The token used with this application is using my 'Works for Nest' distribution *appId* and *secret*.  It's an older one which allows 1000 individual user logins before I will need to certify with Nest._
 __I do not have the ability to see any of your data or who is even using the token__
 * Devices that use html tile will not refresh with going out of the device and back in again.
 
 *  ***There were a ton of changes to the core code so we can't guarantee there won't issue updating the code directly.  Once you update it is important that you open the smart app and press done to clean up old variables and switch to the new Cron scheduler.  If you have any issues after that I suggest you remove the old devices from any apps or routines they are under. Then remove the nest manager smartapp and start over.***


## Advantages
 * Able to add/remove multiples devices from a single SmartApp
 * No need to many enter device serial number and login info in preferences
 * Nest Login info is not stored by the application
 * No need to use 3rd Party Polling apps for device updates
 * One single API call for all Nest devices
 * Since there is only a single poll for all devices, updates are more often
 * The devices look great :smile:

## 3rd-Party SmartApp Compatibility (Confirmed)
---------- | ----------
 [Keenect](https://community.smartthings.com/t/release-keenect-v1-2-0-optional-separate-vo-settings-for-cooling-vent-obstruction-auto-clear/39119) | @Mike_Maxwell 
 [SmartTiles](http://smarttiles.click/) | @625alex 
 [Rule Machine (Rules and Custom Commands)](https://community.smartthings.com/t/rule-machine-version-1-9-released/43204) | @bravenel 
 [CoRE](https://community.smartthings.com/t/beta-milestone-1-core-communitys-own-rules-engine/48189?u=tonesto7)  | @ady264
 Keep Me Cozy (I & II) (link) | 
 Routines | @smartthings

 
## New Installations

### Method 1: Using Git Integration (Recommended) 
_Enabling the GitHub Integration in your IDE is by far the easiest way to install and get the latest updates for Nest Connect App, Presence, Protect and Thermostat devices._

If you don't already have Git Integration setup please visit __*[GitHub Integration Instructions](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html)*__

For advanced users who have their SmartThings IDE integrated with GitHub, the installation and maintaining of code becomes very simple. This manual will not go into detail about setting up your IDE with GitHub; those instructions can be found on the SmartThings web site [[http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html?highlight=git]]


#### The Manager App
* First, find the **Settings** button at the top of your SmartThings IDE page (this will only appear after you integrate with GitHub)

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/IdeSettings.jpg)

* Clicking this button will open the GitHub Repository Integration page. To find the **Nest Manager** SmartApp code, enter the information as you see it below:

**Owner:** tonesto7

**Name:** nest-manager

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/GithubIntegration.png)

* Close the GitHub Repository Integration page
*Next, click the **Update from Repo** button at the upper-right corner of the IDE
*On the right-hand column, scroll down to click the apps you want to install. This will typically be:

    `**SmartApp**: smartapps/tonesto7/nest-manager.src/nest-manager.groovy`
    
* Check the Publish box and Click the **Execute Update** in the bottom-right corner of the screen. When done syncing, the new apps should now appear in your IDE. If they ever change color, that indicates a new version is available.

 ***REMINDER!!!: Remember to Enable OAuth under the Nest Manager's App Settings (Instructions Below)***

#### The Presence, Protect, Thermostat and Weather Device Handlers
 
 * Go to "**My Device Handlers**" in the IDE
 * Under My Device Handlers Click on ***Update from Repo*** and select ***nest-manager*** from the drop-down
 * Check the box next to ***nest-presence***, ***nest-protect***, ***nest-thermostat***, and ***nest-weather*** then click **Execute Update**
 * Click on each device link and select "**Publish** and **For Me**
 
That's it your Done in the IDE... Just install "**Nest Manager**" from the ***Marketplace > MyApps*** under the mobile app.

When updates are available to the source code you will see the Link color change from black in the IDE.

------

### Method 2: The Manual Way
#### Nest Manager Code Installation

The code for the SmartThings SmartApp is found on the GitHub site:

***Nest Manager Source:*** **[https://github.com/tonesto7/nest-manager/blob/master/smartapps/tonesto7/nest-manager.src/nest-manager.groovy](https://github.com/tonesto7/nest-manager/blob/master/smartapps/tonesto7/nest-manager.src/nest-manager.groovy)**

While on the GitHub site, find the **Raw** button and click it. This will bring up a non-formatted page with just the code present. Select all of the code (typically CTRL+A) and copy It (typically CTRL+C).

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/git_raw_ss.png)


* Next, point your browser to you SmartThings IDE for your country (i.e. [http://ide.smartthings.com](http://ide.smartthings.com) or [https://graph-eu01-euwest1.api.smartthings.com](https://graph-eu01-euwest1.api.smartthings.com) and **Log In**.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-Loginscreen.jpg)

* Once you are logged in, find the **My SmartApps** link on the top of the page.  Clicking **My SmartApps** will allow you to produce a new SmartApp.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-MySmartApps.png)

* Find the button on this page labeled **+New SmartApp** and click it.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/new_smartapp_button.png)

* Since you already have the code in your computer’s clipboard, find the tab along the top section called **From Code**. In the area provided, paste (typically CTRL+V) the code you copied from GitHub. Click **Create** in the bottom left corner of the page.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-NewSmartAppCreate.png)

* This will bring up another page, with the code now formatted within the IDE. If the code was copied correctly, there are no other steps except to save and publish the code. In the upper right corner of the page, find and click **Save**. Now, click **Publish (For Me)**, and you should receive a confirmation that the code has been published successfully.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/SavePublish.png)

 ***REMINDER!!!: Remember to Enable OAuth under the Nest Manager's App Settings (Instructions Below)***
 
 - - - 
#### The Presence, Protect, and Thermostat Device Handlers
 ***Thermostat Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-thermostat.src/nest-thermostat.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-thermostat.src/nest-thermostat.groovy)**
 
 ***Protect Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-protect.src/nest-protect.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-protect.src/nest-protect.groovy)**

 ***Presence Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-presence.src/nest-presence.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-presence.src/nest-presence.groovy)**
 
 ***Weather Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-weather.src/nest-weather.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-weather.src/nest-weather.groovy)**
 
***Repeat these steps below in the same manner you did for the manager app under **My Device Handlers** for each device above:***

 * Go to "**My Device Handlers**"
 * Create "**New Device Handler**"
 * Choose "**From Code**"
 * Copy Source code for Device and click **Create**
 * Remember to click "**Publish**" and "**For me**"

------
###Enabling OAuth
**Nest Manager** requires OAuth to operate correctly.

To enable OAuth, first find and click the **App Settings** button in the upper right corner of the page.

From here, find the **OAuth** section toward the bottom of the page. 

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-OAuthBtn.png)
 
Clicking the **OAuth** link will reveal a button labeled **Enable OAuth in Smart App**. Click this button. The screen will change, giving you a unique code for your **Client ID** and **Client Secret**. These are the foundations of the security of your app and should be kept secret. You do not need to memorize or write down these codes; nor do you need to add any other information to this page.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-OAuth.jpg)

The final step is to press the **Update** button at the bottom left corner of the screen, or go back to your code by using the button in the upper-right region of the page, then **Save**, then **Publish** the SmartApp again. 

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/SavePublish.png)

------

## Applying Updates
Performing updates using Git Integration is the fastest method of updating.
Just follow the same methods as the install by clicking on update from repo.

If you don't have Git Integration you will just need to copy/paste the code from the source links above over each app/device and press **Save** the **Publish** for me. 

Just Remember to perform the following before upgrading any existing code using the methods above: 

* Remove any existing configured Nest Automations.
* Delete the Old Nest Automation app from the IDE.  
* Update Nest Manager code to latest version
* Update all Device Handler code to latest version
* Open the Manager App and complete the setup review and press done.

-----
## Setting up Nest Manager App
 * In the SmartThings Mobile App
 * Go to "**Marketplace**" and select "**SmartApps**"
 * At the bottom of the list, select "**My Apps**"
 * Select "**Nest Manager**" from the list.
 * Enter you Nest Login credentials when prompted.
 * Choose **Structure**
 * Choose **Thermostats**
 * Choose **Protects**
 * Choose *Add Presence Device* (Optional)
 * Choose *Add Weather Device* (Optional)
 * Modify any preferences you would like (Optional)
 * Tap on "**Done**"




## Issues and Troubleshooting

### Issues
*If you are experiencing any issues, please let us know by heading over to projects issues page on GitHub. If you don't see the issue reported please help open an new one and provide as much detail as you can.*

**[Project Issues Link](https://github.com/tonesto7/st-nest-unofficial/issues)** 

In an effort to make it easier for some of you troubleshoot without having to use the IDE constantly.  I have added in a diagnostic option under the preference section of the SmartApp.  
Once this is enabled it will begin to store non-user identifiable error logs from the app and store in a local state variable of the app.  
This feature will help you to view and export the logs (*also see the tip below*) directly from the Smart App. This also allows you to copy & paste them into the issues form on GitHub.

**Tip:** If you enable diagnostic logs in the app you can share the logs from with in the app you can store the direct link to log JSON file on you computer and browse directly to the log everytime you need to review the errors.

_______

### Troubleshooting
**Tip:** *The most common issue is forgetting to enable oAuth for the app under SmartThings*

Please check the Help Page before posting questions in the community forum. 
**[Nest Manager Help Page](https://cdn.rawgit.com/tonesto7/nest-manager/master/Documents/help-page.html)**

_______
## Feature Requests
 * We love new ideas so please head on over to GitHub and open an issue for the feature you would like to see.  This will help prioritize what is important and what is not.
 * There is a Google Form that will allow you to vote for the newest features. 

    ***[Voting Form](https://docs.google.com/forms/d/1bkGy14QyjLedpM31CQ4t6m7UIbxbNH8PCUAdB_-EB08/viewform)***

    ***[Feedback Form](http://goo.gl/forms/jGdwJIfqQl456L1h1)***
    
_______
### Donations
 * While donations are very much appreciated they are not expected or required.  If you feel the need to do so :smile: here is the ***[donation link](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2CJEVN439EAWS)***

_______
### Nest API Documentation
 [Nest Developer Documents](https://developer.nest.com/documentation/cloud/get-started)

 To view the json returned from the API just get your authToken from the SmartApp state data and add it to this Url
 
 *https://developer-api.nest.com/devices?auth=__yourAuthTokenHere__*
