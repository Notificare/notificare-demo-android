package re.notifica.app;
import android.app.Application;

import re.notifica.Notificare;


public class AppBaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Notificare.shared().setDebugLogging(BuildConfig.DEBUG);
		//Notificare.shared().setUseLegacyGCM();
		Notificare.shared().launch(this);
	    Notificare.shared().setIntentReceiver(AppReceiver.class);
	    Notificare.shared().setUserPreferencesResource(R.xml.preferences);
        Notificare.shared().setSmallIcon(R.drawable.ic_stat_notify_msg);
		Notificare.shared().setAllowJavaScript(true);
		Notificare.shared().setAllowOrientationChange(true);
		Notificare.shared().setPassbookRelevanceOngoing(true);

		// Crash logs are enabled by default.
		// However, you opt-out by using the following instruction
		//Notificare.shared().setCrashLogs(false);

        // The SDK supports a single optional placeholder, %s.
        // If the placeholder is provided, it will be replaced by the pass' description, if any.
		//Notificare.shared().setRelevanceText("Notificare demo: %s");
		//Notificare.shared().setRelevanceIcon(R.drawable.notificare_passbook_style);
	}

}