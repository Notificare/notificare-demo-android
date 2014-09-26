package re.notifica.app;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LostPassActivity extends Activity {
	
	private EditText emailField;
	private TextView info;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lostpass);
		
		findViewById(R.id.buttonLostPass).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recoverPassword();
			}
		});
	}
	
	public void recoverPassword() {
		
		emailField = (EditText) findViewById(R.id.email);

		String email = emailField.getText().toString();
		
		info = (TextView) findViewById(R.id.infoText);
		
		
		if (TextUtils.isEmpty(email) ) {
			info.setText(R.string.error_lost_pass);
		}  else if (!email.contains("@")) {
			info.setText(R.string.error_invalid_email);
		} else {
			dialog = ProgressDialog.show(LostPassActivity.this, "", getString(R.string.loader_connection), true);
			
			Notificare.shared().sendPassword(email, new NotificareCallback<Boolean>(){

				@Override
				public void onError(NotificareError arg0) {
					info = (TextView) findViewById(R.id.infoText);
					info.setText(arg0.getMessage());
					dialog.dismiss();
				}

				@Override
				public void onSuccess(Boolean arg0) {
					info = (TextView) findViewById(R.id.infoText);
					info.setText(R.string.success_email_found);
					dialog.dismiss();
				}
				
			});
		}
			
	}
}
