package com.emanbuhagiar.DiscoveringMalta;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialise Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initialise Parse SDK
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "dUkuNYMWVg02DhTuZX51orXIndHmaduSdoWB8NSO", "tykGbRKBkutdDy5pFphFpQkRNxOKtJ0Ay0KZsyRX");        //Parse.initialize(this, "5ef76760bf816f5d00a6f07e11b0dbef","919d751294189a5dcfdc4f961be6b87b");

        ParseFacebookUtils.initialize(this);
    }
}
