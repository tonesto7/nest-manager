# Nest Manager (Unofficial)

### Nest Manager App
This is the "***unofficial***" SmartThings user created SmartApp and Device handlers.
The SmartApp and Device Handlers work together to provide integration to the SmartThings ecosystem using Nest's Official API.

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_newInst.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_1.png" width="281" height="500">

### Nest Automations App
This is a Child-SmartApp that allows you to creat different types of automations for your HVAC systems.

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/AutomationApp/automation_start.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/AutomationApp/automation_start_2.png" width="281" height="500">

### Nest Thermostat
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat2.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat3.PNG" width="281" height="500">

### Nest Protect
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_protect.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_protect2.PNG" width="281" height="500">

### Nest Weather
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_weather.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_weather2.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_nest_pres_dev.png" width="281" height="500">

### Nest Cam
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_camera.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_camera2.png" width="281" height="500">

### Nest Presence
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_nest_pres_dev.png" width="281" height="500">

## Author
* @tonesto7

## Contributors
* @E_sch
* @desertblade
* @ghesp

## Testers
* @shmookles, @keltymd - Thanks for all of your help testing!!!

## Version Info
__SmartApp:__ | __Version:__ |
:--- | :---: |
Nest Manager | *v3.1.0* |

__Device:__ | Version: |
:--- | :---: |
Nest Presence Device | *v3.1.0* |
Nest Protect Device | *v3.1.0* |
Nest Thermostat Device | *v3.1.0* |
Nest Virtual Thermostat Device | *v3.1.0* |
Nest Weather Device | *v3.1.0* |
Nest Camera Device | *v1.1.0* |

## What's New

 * Please see the [Nest Manager Community Forum Link](https://community.smartthings.com/t/release-nest-manager-3-1/) for New Features

## Links
#### [GitHub Project Issues Link](https://github.com/tonesto7/nest-manager/issues)

#### [SmartThings Community Forum Link](https://community.smartthings.com/t/release-nest-manager-3-1/)

#### [Projects Help Page](https://cdn.rawgit.com/tonesto7/nest-manager/master/Documents/help-page.html)

#### [SmartThings IDE GitHub Integration Instructions](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html)

## Things to Know

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

SmartApp | Author |
:--- | :--- |
[Keenect](https://community.smartthings.com/t/release-keenect-v1-2-0-optional-separate-vo-settings-for-cooling-vent-obstruction-auto-clear/39119) | @Mike_Maxwell |
[SmartTiles](http://smarttiles.click/) | @625alex |
[CoRE](https://community.smartthings.com/t/beta-milestone-1-core-communitys-own-rules-engine/48189?u=tonesto7) | @ady624 |
[Ask Alexa](https://community.smartthings.com/t/release-ask-alexa) | @MichaelS |
Rule Machine (No Longer Available) | @bravenel |


## New Installations
Thanks @MichaelS for letting me borrow from your Ask Alexa Install Instructions...

### Method 1: Using Git Integration (Recommended)

_Enabling the GitHub Integration in your IDE is by far the easiest way to install and get the latest updates for Nest Manager App, Presence, Protect, Thermostat, and Weather devices._

If you don't already have Git Integration setup please visit __*[GitHub Integration Instructions](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html)*__

**Git Integration is not currently available outside of US**

For advanced users who have their SmartThings IDE integrated with GitHub, the installation and maintaining of code becomes very simple.

#### The Manager App
* First, find the **Settings** button at the top of your SmartThings IDE page (this will only appear after you integrate with GitHub)

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/IdeSettings.jpg)

* Clicking this button will open the GitHub Repository Integration page. Add the **Nest Manager** SmartApp code by selecting "Add new repository" and entering the information as you see it below:

**Owner:** tonesto7

**Name:** nest-manager

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/GithubIntegration.png)

* Close/save the GitHub Repository Integration page
* Next, click the **Update from Repo** button at the upper-right corner of the IDE and selct "nest-manager"
* On the right-hand column, select the apps you want to install. Typicallly this will be all of them.
* Check the Publish box and click the **Execute Update** in the bottom-right corner of the screen. When done syncing, the new apps should now appear in your IDE. If they ever change color, that indicates a new version is available.

 ***REMINDER!!!: Remember to [Enable OAuth under the Nest Manager's App Settings (Instructions Below)](#enabling-oauth)***

#### The Presence, Protect, Thermostat and Weather Device Handlers

 * Go to "**My Device Handlers**" in the IDE
 * Under My Device Handlers Click on ***Update from Repo*** and select ***nest-manager*** from the drop-down
 * Check all the boxes then check the **Publish** box and click **Execute Update**

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

 ***REMINDER!!!: Remember to [Enable OAuth under the Nest Manager's App Settings (Instructions Below)](#enabling-oauth)***

 - - -
#### The Presence, Protect, and Thermostat Device Handlers
 ***Thermostat Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-thermostat.src/nest-thermostat.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-thermostat.src/nest-thermostat.groovy)**

***Virtual Thermostat Source Code:***
 **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-virtual-thermostat.src/nest-virtual-thermostat.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-virtual-thermostat.src/nest-virtual-thermostat.groovy)**

 ***Protect Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-protect.src/nest-protect.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-protect.src/nest-protect.groovy)**

 ***Presence Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-presence.src/nest-presence.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-presence.src/nest-presence.groovy)**

 ***Weather Source Code:*** **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-weather.src/nest-weather.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-weather.src/nest-weather.groovy)**

 ***Camera Source Code:***
 **[https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-camera.src/nest-camera.groovy](https://github.com/tonesto7/nest-manager/blob/master/devicetypes/tonesto7/nest-camera.src/nest-camera.groovy)**

 ***Repeat these steps below in the same manner you did for the manager app under **My Device Handlers** for each device above:***

 * Go to "**My Device Handlers**"
 * Create "**New Device Handler**"
 * Choose "**From Code**"
 * Copy Source code for Device and click **Create**
 * Remember to click "**Publish**" and "**For me**"

------
###Enabling OAuth
**Nest Manager** requires OAuth to operate correctly.

To enable OAuth, navigate to your **Nest Manager** SmartApp in the IDE and click the **App Settings** button in the upper right corner of the page.

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

-----
## Setting up Nest Manager App
 * In the SmartThings Mobile App
 * Go to "**Marketplace**" and select "**SmartApps**"
 * At the bottom of the list, select "**My Apps**"
 * Select "**Nest Manager**" from the list.
 * Enter you Nest Login credentials when prompted.
 * Set your **Nest Location** and add your Nest Devices
 * Modify any preferences you would like (Optional)
 * Tap on "**Done**"


## Issues and Troubleshooting

### Issues
*If you are experiencing any issues, please let us know by heading over to projects issues page on GitHub. If you don't see the issue reported please help open a new one and provide as much detail as you can.*

**[Project Issues Link](https://github.com/tonesto7/nest-manager/issues)**

In an effort to make it easier for some of you troubleshoot without having to use the IDE constantly.  I have added in a diagnostic option under the preference section of the SmartApp.  
Once this is enabled it will begin to store non-user identifiable error logs from the app and store in a local state variable of the app.  
This feature will help you to view and export the logs (*also see the tip below*) directly from the Smart App. This also allows you to copy & paste them into the issues form on GitHub.

**Tip:** If you enable diagnostic logs in the app you can share the logs from with in the app you can store the direct link to log JSON file on you computer and browse directly to the log everytime you need to review the errors.

_______

### Troubleshooting
**Tip:** *The most common issue is forgetting to enable OAuth for the app under SmartThings*

Please check the Help Page before posting questions in the community forum.
**[Nest Manager Help Page](https://cdn.rawgit.com/tonesto7/nest-manager/master/Documents/help-page.html)**

***FYI: The help pages have not been updated yet for V3.0.0***
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
