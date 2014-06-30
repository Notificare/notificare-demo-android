package re.notifica.app;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends Activity implements NotificareCallback<Boolean> {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
	}
	
	public void signIn() {
	
		EditText email = (EditText) findViewById(R.id.email);
		EditText pass = (EditText) findViewById(R.id.pass);
		Notificare.shared().userLogin(email.getText().toString(), pass.getText().toString(), this);
	}
	
	public void goToSignup(View view) {
	
		Intent a = new Intent(SignInActivity.this, SignUpActivity.class);
		startActivity(a);
		
	}
	
	public void goToLostPass(View view) {
	
		Intent a = new Intent(SignInActivity.this, LostPassActivity.class);
		startActivity(a);
		
	}

	@Override
	public void onError(NotificareError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
