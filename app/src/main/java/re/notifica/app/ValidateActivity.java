package re.notifica.app;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class ValidateActivity extends AppCompatActivity {

    private String token;
    private TextView info;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);

        token = Notificare.shared().parseValidateUserIntent(getIntent());

        if (token == null) {
            Toast.makeText(this, "No token provided", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Notificare.shared().validateUser(token, new NotificareCallback<Boolean>(){
                @Override
                public void onError(NotificareError arg0) {
                    info = (TextView) findViewById(R.id.infoText);
                    info.setText(arg0.getMessage());
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onSuccess(Boolean result) {
                    info = (TextView) findViewById(R.id.infoText);
                    info.setText("Account validated successfully. Thank you");
                    dialog.dismiss();
                    finish();
                }
            });
        }

    }

}

