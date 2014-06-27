package re.notifica.app;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import re.notifica.Notificare;
import android.app.Application;


public class AppBaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// Launch Notificare system
	    Notificare.shared().launch(this);
	    // Set our own intent receiver
	    Notificare.shared().setIntentReceiver(AppReceiver.class);
	    // Enable this device for push notifications
	    Notificare.shared().enableNotifications();
	    
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

	    if(preferences.contains("pref_key_location_services") && preferences.getBoolean("pref_key_location_services", true)) {
	    	Notificare.shared().enableLocationUpdates();
	    }
	    
	    //Notificare.shared().setAutoCancel(false);
	}

}