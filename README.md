notificare-demo-android
=========
Demo for GCM, Tags, Location Services and OAuth2 features of Notificare

##Setup

1. Create server keys in Google Developer Console and add it to Notificare as explained in: https://notificare.atlassian.net/wiki/display/notificare/1.+Set+up+GCM
2. Expand ```assets``` and click in ```notificareconfig.properties```, change the API keys accordingly. Get them in your https://dashboard.notifica.re under **Settings > App Keys**.
3. Add, modify and remove items from the navigation drawer by changing the two ```string-array``` in ```/res/values/strings.xml``` accordingly.
4. If you wish to customize the User Preferences screen please change the files ```/res/xml/preferences.xml``` and ```/res/values/strings_preferences.xml```.
5. Start sending rich push messages, using geo-targeting, geo-fences and/or iBeacons.
6. Authentication will only work if you subscribe for the OAuth2 Service in https://dashboard.notifica.re under **Settings > Services**
