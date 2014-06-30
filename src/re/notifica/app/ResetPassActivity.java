package re.notifica.app;

import java.util.List;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPassActivity extends Activity {
	
	private String token;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private TextView info;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpass);
		
		Uri data = getIntent().getData();
		if (data != null) {
			List<String> pathSegments = data.getPathSegments();
			if (pathSegments.size() >= 4 && pathSegments.get(0).equals("oauth") && pathSegments.get(2).equals(Notificare.shared().getApplicationInfo().getId()) && pathSegments.get(1).equals("resetpassword")) {
				token = pathSegments.get(3);
			} else {
				token = null;
			}
		} else {
			token = getIntent().getStringExtra(Notificare.INTENT_EXTRA_TOKEN);
		}
		
		if (token == null) {
			Toast.makeText(this, "No token provided", Toast.LENGTH_LONG).show();
			finish();
		}

		findViewById(R.id.buttonResetPass).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetPassword();
			}
		});
	}
	
public void resetPassword() {
		
		passwordField = (EditText) findViewById(R.id.pass);
		confirmPasswordField = (EditText) findViewById(R.id.confirmPass);
	
		String password = passwordField.getText().toString();
		String confirmPassword = confirmPasswordField.getText().toString();
		
		info = (TextView) findViewById(R.id.infoText);
	
		if (TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
			info.setText(R.string.error_reset_pass);
		} else if (!password.equals(confirmPassword)) {
			info.setText(R.string.error_pass_not_match);
		} else if (password.length() < 6) {
			info.setText(R.string.error_pass_too_short);
		}else {
			dialog = ProgressDialog.show(ResetPassActivity.this, "", getString(R.string.loader_connection), true);
			
			Notificare.shared().resetPassword(password, token, new NotificareCallback<Boolean>(){

				@Override
				public void onError(NotificareError arg0) {
					dialog.dismiss();
					info.setText(arg0.getMessage());
				}

				@Override
				public void onSuccess(Boolean arg0) {
					dialog.dismiss();
					info.setText(R.string.success_reset_pass);
					
				}
				
			});
		}
	}
}
