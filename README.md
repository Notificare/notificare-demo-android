notificare-demo-android
=========
Demo for GCM, Tags, Location Services and OAuth2 features of Notificare

##Setup

1. Add the Google Maps V2 key in ```AndroidManifest.xml```
2. Expand ```assets``` and click in ```notificareconfig.properties```, change the API keys accordingly. Get them in your https://dashboard.notifica.re under **Settings > App Keys**.
3. Create server keys in Google Developer Console and add it to Notificare as explained in: https://notificare.atlassian.net/wiki/display/notificare/1.+Set+up+GCM
4. To change the items in the navigation drawer make sure you change the ```string-array``` in ```/res/values/strings.xml``` accordingly.
5. Start sending rich push messages, using geo-targeting, geo-fences and/or iBeacons.
6. Authentication will only work if you subscribe for the OAuth2 Service in https://dashboard.notifica.re under **Settings > Services**
