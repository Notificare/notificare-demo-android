package re.notifica.app;

import java.util.List;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPassActivity extends AppCompatActivity {
	
	private String token;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private ProgressDialog dialog;
    private AlertDialog.Builder builder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpass);

        builder = new AlertDialog.Builder(this);

        Typeface lightFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Light.ttf");
        Typeface regularFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");

        Button resetButton = (Button) findViewById(R.id.buttonResetPass);
        passwordField = (EditText) findViewById(R.id.pass);
        confirmPasswordField = (EditText) findViewById(R.id.confirmPass);
        passwordField.setTypeface(lightFont);
        confirmPasswordField.setTypeface(lightFont);
        resetButton.setTypeface(lightFont);


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

		String password = passwordField.getText().toString();
		String confirmPassword = confirmPasswordField.getText().toString();

		if (TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
			//info.setText(R.string.error_reset_pass);
            builder.setMessage(R.string.error_reset_pass)
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
                            //do things
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
                            //do things
                            confirmPasswordField.setText("");
                            passwordField.setText("");
                        }
                    });
            AlertDialog dialogInfo = builder.create();
            dialogInfo.show();
		}else {
			dialog = ProgressDialog.show(ResetPassActivity.this, "", getString(R.string.loader_connection), true);
			
			Notificare.shared().resetPassword(password, token, new NotificareCallback<Boolean>(){

				@Override
				public void onError(NotificareError arg0) {
					dialog.dismiss();
					//info.setText(arg0.getMessage());
                    builder.setMessage(arg0.getMessage())
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    confirmPasswordField.setText("");
                                    passwordField.setText("");
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
				}

				@Override
				public void onSuccess(Boolean arg0) {
					dialog.dismiss();
					//info.setText(R.string.success_reset_pass);
                    builder.setMessage(R.string.success_reset_pass)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
				}
				
			});
		}
	}
}
