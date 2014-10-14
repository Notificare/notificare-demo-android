package re.notifica.app;
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
	    Notificare.shared().setUserPreferencesResource(R.xml.preferences);
	    // Enable this device for push notifications
	    Notificare.shared().enableNotifications();
	    Notificare.shared().enableLocationUpdates();
	    
	    //Notificare.shared().setAutoCancel(false);
	}

}