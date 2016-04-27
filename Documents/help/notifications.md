## Notification Preferences
Notifications allows you to receive a message when a certain action occurs.

#####Currently there are only two events that will trigger a notification: 

* When a scheduled poll hasn't occurred after a set amount of seconds.
* When there is a SmartApp or Device Type code update available.

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/notif_prefs_page_1.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/notif_prefs_page_2.png" width="281" height="500">

**The __Send Notifications to__ input will not be shown for users whose accounts do not have contact book enabled.  If you don't have it you can't enable it until SmartThings activates the feature again. If the Contact book is still not available.  You will see a toggle that will allow sending Push Messages instead of selecting individual users.**

----------
#### Quiet Time
If notifications are active.  It will not send them during the times and days selected.

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/develop/Images/Screenshots/App/notif_quiet_page.png" width="281" height="500">

* You can also use Modes to activate quiet time
* *The Start/Stop Time options available are:* 
	* A Specific Time
	* Sunrise
	* Sunset

----------

#### Missed Poll Notifications
This will send a notification after the set number of seconds passed the last poll.
The wait before sending another value is the time that must pass before being notified for this event again.

* *The default time passed value is every 15 minutes(900 seconds)*
* *The default wait value is 30 minutes(1800 seconds)*  
* ***The custom values allowed are between (30-84600) seconds***
	
----------
	
#### Code Update Notifications
When there is a SmartApp or Device Type code update available.

* *The default value is every 2 Hours(7200 seconds)*  
* ***The custom values allowed are between (30-84600) seconds***

----------