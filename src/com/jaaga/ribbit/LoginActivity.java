package com.jaaga.ribbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	private TextView mSignUpText;
	protected EditText mUsername;
	protected EditText mPassword;
	protected Button mLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		
		mSignUpText = (TextView) findViewById(R.id.signupText);
		
		mSignUpText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
				startActivity(intent);
			}
		});
		
		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mLogin = (Button) findViewById(R.id.login);
		
		mLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String username = mUsername.getText().toString().trim();
				String password = mPassword.getText().toString().trim();
				
				if(username.isEmpty() || password.isEmpty()){
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setTitle("Oops!").setMessage(R.string.login_error_msg)
					.setPositiveButton(android.R.string.ok, null);
					
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else{
					//Login
					setProgressBarIndeterminateVisibility(true);
					
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						
						@Override
						public void done(ParseUser user, ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							
							if(e == null){
								//success!
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
								finish();
							}
							else{
								AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
								builder.setTitle("Oops!").setMessage(e.getMessage())
								.setPositiveButton(android.R.string.ok, null);
								
								AlertDialog dialog = builder.create();
								dialog.show();
							}
						}
					});
				}
			}
		});

	}

}
