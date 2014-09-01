package com.jaaga.ribbit;

import android.app.Application;

import com.jaaga.ribbit.ui.MainActivity;
import com.jaaga.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class RibbitApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "h9NQRJONNsqhl8sLOxPTdxnbG8dQknB3sAwrcT1W",
				"tiiJKSPHf9HAoqdbx9lml8TSAzS7Cf9LkWKq9q6u");
		
		//PushService.setDefaultPushCallback(this, MainActivity.class);
		PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.ic_stat_ic_launcher);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("dsjns", "sjbn");
		//testObject.saveInBackground();
	}
	
	public static void updatePasreInstallation(ParseUser user){
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}
}
