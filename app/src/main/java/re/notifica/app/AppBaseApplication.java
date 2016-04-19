package re.notifica.app;
import re.notifica.Notificare;

import android.app.Application;
import android.graphics.Color;

import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.logging.Loggers;


public class AppBaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Notificare.shared().launch(this);
        Notificare.shared().setDebugLogging(BuildConfig.DEBUG);
	    Notificare.shared().setIntentReceiver(AppReceiver.class);
	    Notificare.shared().setUserPreferencesResource(R.xml.preferences);
        Notificare.shared().setSmallIcon(R.drawable.ic_stat_notify_msg);
		Notificare.shared().setAllowJavaScript(true);
		Notificare.shared().setAllowOrientationChange(true);
	}

}