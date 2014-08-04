package com.jaaga.ribbit;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends Activity {

	protected EditText mUsername;
	protected EditText mPassword;
	protected EditText mEmail;
	protected Button mSignup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_sign_up);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		mUsername = (EditText) findViewById(R.id.editText1);
		mPassword = (EditText) findViewById(R.id.editText2);
		mEmail = (EditText) findViewById(R.id.editText3);
		mSignup = (Button) findViewById(R.id.signupbutton);
		
		mSignup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String username = mUsername.getText().toString().trim();
				String password = mPassword.getText().toString().trim();
				String email = mEmail.getText().toString().trim();
				
				if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
					builder.setTitle("Oops!").setMessage(R.string.error_msg)
					.setPositiveButton(android.R.string.ok, null);
					
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else{
					//create a new user!
					setProgressBarIndeterminateVisibility(true);
					ParseUser newuser = new ParseUser();
					newuser.setUsername(username);
					newuser.setPassword(password);
					newuser.setEmail(email);
					
					newuser.signUpInBackground(new SignUpCallback() {
						
						@Override
						public void done(ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							
							if(e == null){
								//success!
								Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
							else{
								AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
