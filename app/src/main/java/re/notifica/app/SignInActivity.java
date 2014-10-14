package re.notifica.app;


import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.model.NotificareUser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends Activity {
	
	private EditText emailField;
	private EditText passwordField;
	private TextView info;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		
		findViewById(R.id.buttonSignin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signIn();
			}
		});
	}
	
	public void signIn() {
	
		emailField = (EditText) findViewById(R.id.email);
		passwordField = (EditText) findViewById(R.id.pass);
		
		String email = emailField.getText().toString();
		String password = passwordField.getText().toString();
		
		info = (TextView) findViewById(R.id.infoText);
		
		
		if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
			info.setText(R.string.error_sign_in);
		} else if (password.length() < 6) {
			info.setText(R.string.error_pass_too_short);
		} else if (!email.contains("@")) {
			info.setText(R.string.error_invalid_email);
		} else {
			dialog = ProgressDialog.show(SignInActivity.this, "", getString(R.string.loader_signin), true);
			
			
			Notificare.shared().userLogin(email, password, new NotificareCallback<Boolean>(){

				@Override
				public void onError(NotificareError arg0) {
					info = (TextView) findViewById(R.id.infoText);
					info.setText(arg0.getMessage());
					dialog.dismiss();
				}

				@Override
				public void onSuccess(Boolean result) {
					
					Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {

						@Override
						public void onError(NotificareError arg0) {

						}

						@Override
						public void onSuccess(NotificareUser arg0) {
							Notificare.shared().setUserId(arg0.getUserId());
							Notificare.shared().registerDevice(Notificare.shared().getDeviceId(), arg0.getUserId(), arg0.getUserName(), new NotificareCallback<String>() {

								@Override
								public void onSuccess(String result) {

								}

								@Override
								public void onError(NotificareError error) {

								}

							});
						}
			        	
			        });
					
				}
				
			});
		}

	}
	
	public void goToSignup(View view) {
	
		Intent a = new Intent(SignInActivity.this, SignUpActivity.class);
		startActivity(a);
		
	}
	
	public void goToLostPass(View view) {
	
		Intent a = new Intent(SignInActivity.this, LostPassActivity.class);
		startActivity(a);
		
	}

}
