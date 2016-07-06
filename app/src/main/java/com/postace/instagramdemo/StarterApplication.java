package com.postace.instagramdemo;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Hai on 7/6/2016.
 */
public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable local DataStore
        Parse.enableLocalDatastore(this);
        // Init Parse
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("GbFhPJLeq5eI0J0i4aOlFAZ4AemBU1mkUsvZjMPB")
                .clientKey("u1vc167JoytEcEUCICwpWLHr0HC0EYmQA62U5vLX")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
