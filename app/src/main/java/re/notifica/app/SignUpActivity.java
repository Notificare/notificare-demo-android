package re.notifica.app;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {
	
	private EditText nameField;
	private EditText emailField;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private ProgressDialog dialog;
	private AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

        Button signupButton = (Button) findViewById(R.id.buttonSignup);

        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passField);
        confirmPasswordField = (EditText) findViewById(R.id.confirmPassField);

        builder = new AlertDialog.Builder(this);

        Typeface lightFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Light.ttf");
        Typeface regularFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");
        emailField.setTypeface(lightFont);
        passwordField.setTypeface(lightFont);
        nameField.setTypeface(lightFont);
        confirmPasswordField.setTypeface(lightFont);
        signupButton.setTypeface(lightFont);

		findViewById(R.id.buttonSignup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signUp();
			}
		});
	}
	
	public void signUp() {
		

		
		String name = nameField.getText().toString();
		String email = emailField.getText().toString();
		String password = passwordField.getText().toString();
		String confirmPassword = confirmPasswordField.getText().toString();
		
		//info = (TextView) findViewById(R.id.infoText);
	
		if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
			//info.setText(R.string.error_sign_up);
            builder.setMessage(R.string.error_sign_up)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog dialogInfo = builder.create();
            dialogInfo.show();
		} else if (!password.equals(confirmPassword)) {
			//info.setText(R.string.error_pass_not_match);
            builder.setMessage(R.string.error_pass_not_match)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            confirmPasswordField.setText("");
                        }
                    });
            AlertDialog dialogInfo = builder.create();
            dialogInfo.show();
		} else if (password.length() < 6) {
			//info.setText(R.string.error_pass_too_short);
            builder.setMessage(R.string.error_pass_too_short)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            passwordField.setText("");
                            confirmPasswordField.setText("");
                        }
                    });
            AlertDialog dialogInfo = builder.create();
            dialogInfo.show();
		} else if (!email.contains("@")) {
			//info.setText(R.string.error_invalid_email);
            builder.setMessage(R.string.error_invalid_email)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            emailField.setText("");
                        }
                    });
            AlertDialog dialogInfo = builder.create();
            dialogInfo.show();
		} else {
			dialog = ProgressDialog.show(SignUpActivity.this, "", getString(R.string.loader_connection), true);
			
			Notificare.shared().createAccount(email, password, name, new NotificareCallback<Boolean>(){

				@Override
				public void onError(NotificareError arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
                    builder.setMessage(arg0.getMessage())
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    emailField.setText("");
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
					//info.setText(arg0.getMessage());
				}

				@Override
				public void onSuccess(Boolean arg0) {
					dialog.dismiss();
                    builder.setMessage(R.string.success_account_created)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
					//info.setText(R.string.success_account_created);
				}
			
			});
		}
	}
}
