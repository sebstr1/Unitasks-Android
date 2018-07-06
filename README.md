### Tasks from the Android Course at Mid Sweden University

##### Folder 07-dt031g-sest1601
Contains an project (App) Where we have a custom dialpad that plays a sound saying what number you press. The app has a webview that allows you to download new sounds that you can set from the settings menu. 

When you enter a number and press call, it will open android system dialer and initialize a call to the number entered. It will also log date and time of when you called. If you grant the app Access to device location, it will also log your location when calling.

Upon opening the MapActivity, all the locations you have called from will be visible on the map and if you click a marker, it will show info like date/time and number called.

App uses following permissions:

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

Structure of this project is not optimal as it has been built on in several steps without knowing what comes in the next assignment..

---

##### Folder Bathingsites

Key functions: Async Background tasks (threads), MapActivity, locationlistener, Sqllite with Room, Webview.

Course Final Project. This is an app Where you can add Bathingsites with info like Name, description address, coordinates, rating, watertemp. You can request wheater information from a bathingsite by pressing Weather in the menu and it will fetch it in a background task. When you save a bathingsite, it will check for duplicates in the DB and then save it. You can also go to a webview and download a ton of bathinsites from .csv files that will (in a background thread) download, parse and insert without duplicates to the database.

In the mapview you will see all bathingsites on the map within a distance by radius (changeable from settings menu) from your active position. Your position will update on the map as you move by a locationlistener.
  
App uses following permissions:

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.INTERNET" />
