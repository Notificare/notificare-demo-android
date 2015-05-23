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

public class LostPassActivity extends AppCompatActivity {
	
	private EditText emailField;
	private ProgressDialog dialog;
    private AlertDialog.Builder builder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lostpass);
        Button lostPassButton = (Button) findViewById(R.id.buttonLostPass);
        Typeface lightFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Light.ttf");
        Typeface regularFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");
        builder = new AlertDialog.Builder(this);
        emailField = (EditText) findViewById(R.id.emailField);
        emailField.setTypeface(lightFont);
        lostPassButton.setTypeface(lightFont);
		findViewById(R.id.buttonLostPass).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recoverPassword();
			}
		});
	}
	
	public void recoverPassword() {

		String email = emailField.getText().toString();

		if (TextUtils.isEmpty(email) ) {
			//info.setText(R.string.error_lost_pass);
            builder.setMessage(R.string.error_lost_pass)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog dialogInfo = builder.create();
            dialogInfo.show();
		}  else if (!email.contains("@")) {
			//info.setText(R.string.error_lost_pass);
            builder.setMessage(R.string.error_lost_pass)
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
			dialog = ProgressDialog.show(LostPassActivity.this, "", getString(R.string.loader_connection), true);
			
			Notificare.shared().sendPassword(email, new NotificareCallback<Boolean>(){

				@Override
				public void onError(NotificareError arg0) {
					//info = (TextView) findViewById(R.id.infoText);
					///info.setText(arg0.getMessage());
					dialog.dismiss();
                    builder.setMessage(arg0.getMessage())
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    emailField.setText("");
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
				}

				@Override
				public void onSuccess(Boolean arg0) {
					//info = (TextView) findViewById(R.id.infoText);
					//info.setText(R.string.success_email_found);
                    builder.setMessage(R.string.success_email_found)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    finish();
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
					dialog.dismiss();
				}
				
			});
		}
			
	}
}
