notificare-demo-android
=========
Demo for GCM, Tags, Location Services and OAuth2 features of Notificare

##Setup

1. Open ```assets/notificareconfig.properties``` and replace the properties found in that file with the ones you get from the API Keys in your dashboard.
2. In ```GCMSender``` please use the project number found in the Google Developer Console.
3. To customize activities please take a look at ```res/layouts/*```.
4. To use Google Maps please create an Android Key as explained here: https://notificare.atlassian.net/wiki/display/notificare/3.1+Get+a+Android+Maps+V2+Key.
5. Run or Generate a signed APK
6. Start sending rich push messages, using geo-targeting and geo-fences.
7. Authentication will only work if you subscribe for the OAuth2 Service in https://dashboard.notifica.re under **Settings > Services**
8. To enable beacon support, download the AltBeacon library from http://altbeacon.github.io/android-beacon-library/download.html and put the .aar file in a 'libs' folder in your project. If you are not gonna use beacons, comment out the references in your app to beacon-specific classes and comment out the dependency in  build.gradle
