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

public class SignUpActivity extends Activity {
	
	private EditText nameField;
	private EditText emailField;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private TextView info;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		findViewById(R.id.buttonSignup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signUp();
			}
		});
	}
	
	public void signUp() {
		
		nameField = (EditText) findViewById(R.id.name);
		emailField = (EditText) findViewById(R.id.email);
		passwordField = (EditText) findViewById(R.id.pass);
		confirmPasswordField = (EditText) findViewById(R.id.confirmPass);
		
		String name = nameField.getText().toString();
		String email = emailField.getText().toString();
		String password = passwordField.getText().toString();
		String confirmPassword = confirmPasswordField.getText().toString();
		
		info = (TextView) findViewById(R.id.infoText);
	
		if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
			info.setText(R.string.error_sign_up);
		} else if (!password.equals(confirmPassword)) {
			info.setText(R.string.error_pass_not_match);
		} else if (password.length() < 6) {
			info.setText(R.string.error_pass_too_short);
		} else if (!email.contains("@")) {
			info.setText(R.string.error_invalid_email);
		} else {
			dialog = ProgressDialog.show(SignUpActivity.this, "", getString(R.string.loader_connection), true);
			
			Notificare.shared().createAccount(email, password, name, new NotificareCallback<Boolean>(){

				@Override
				public void onError(NotificareError arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					info.setText(arg0.getMessage());
				}

				@Override
				public void onSuccess(Boolean arg0) {
					dialog.dismiss();
					info.setText(R.string.success_account_created);
				}
			
			});
		}
	}
}
