# Create Nest Product for NST Manager

### _Step 1:_ Create Nest Developer Account
* Goto [Nest Developer Portal](https://developers.nest.com) and create a new account (*_If you don't have one!_*)

### _Step 2:_ Create New Product
##### ==_Notice: Highlighted Items are Required Items_==

1. Click on Create New Product. ![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/Dev_Docs/dev_account_1.png)

2. You will be taken to this page. ![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/Dev_Docs/dev_account_2.png)

3. Fill in the Product Name. (==This can be any name you choose==) ![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/Dev_Docs/dev_account_3.png)

4. Fill in the remaining items listed below ![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/Dev_Docs/dev_account_4.png) 

	_Description:_
	
	==`NST Manager allows you to integrate your Nest products with the SmartThings platform. It can be used to trigger or respond to various device events by using standard SmartThings capabilities. This allows for a high level customization of automations.`==
	
	_Categories:_
	
	* ==`HVAC`==
	* `Home Automation`

	_Support URL:_
	
	==`https://community.smartthings.com/t/release-nest-manager-v5-0/83228`==
	
	_Redirect URI:_
	
	<span style="background-color:#53adcb; color:white;">(Make sure your click on the input below in the Nest Portal twice. It will try to add http:// in front of the https://)</span>
	==`https://graph.api.smartthings.com/oauth/callback`==
	
	

5. Select All of the Items below to set the permissions ![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/Dev_Docs/dev_account_5.png) 
	_*<span style="color:#23D728;">It’s important that you match read/write permission with those items listed below!
</span>*_

	##### Permission Descriptions
	
	* _Away (read/write):_
		
		==`NST Manager use SmartThings (Presence, Mode, or Switches) to change your Nest's Home/Away status.`==
	* _Camera + Images (read/write):_
		
		==`NST Manager uses many combinations of SmartThings device events to turn your Nest Camera on/off.`==
	* _Postal Code (read):_
		
		==`NST Manager uses your postal code when we are unable to determine your SmartThings location info.`==
	* _Smoke + CO Alarm (read):_
		
		==`NST Manager can trigger delayed alerts in SmartThings Platform that can perform tasks based on the alarm type.`==
	* _Thermostat (read/write):_
		
		==`NST Manager can trigger actions in SmartThings when a temperature, humidity, HVAC mode events occurs.`==

6. When you are finished you will be returned to this page. ![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/Dev_Docs/dev_account_6.png)

### _Step 3:_ Adding ID and Secret to SmartApp

  * ==IMPORTANT==:  The Product ID and Product Secret will need to be copied and pasted into the NST Manager App Settings in the SmartThings IDE ![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/Dev_Docs/dev_account_7.png)
    * The Product ID will go in the Value slot to the right of clientId.
    * The Product Secret will go in the Value slot to the right of clientSecret.
    * After you’ve added those values to the NST Manager SmartApp Settings page please scroll down to the bottom and click on __Update__ button.
    
### _Step 4:_ You're All Done

Proceed with the install instruction on the wiki page.

 

	
	