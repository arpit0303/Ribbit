package com.jaaga.ribbit;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "h9NQRJONNsqhl8sLOxPTdxnbG8dQknB3sAwrcT1W",
				"tiiJKSPHf9HAoqdbx9lml8TSAzS7Cf9LkWKq9q6u");
		
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("dsjns", "sjbn");
		//testObject.saveInBackground();
	}
}
