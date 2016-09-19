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

		// Crash logs are enabled by default.
		// However, you opt-out by using the following instruction
		//Notificare.shared().setCrashLogs(false);

        // The SDK supports a single optional placeholder, %s.
        // If the placeholder is provided, it will be replaced by the pass' description, if any.
		//Notificare.shared().setRelevanceText("Notificare demo: %s");
		//Notificare.shared().setRelevanceIcon(R.drawable.notificare_passbook_style);
	}

}