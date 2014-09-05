package com.bwjfstudios.drawlaphone.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class DrawLaPhoneApp extends Application {

    private static final String APPLICATION_ID = "YOUR_KEY_HERE";
    private static final String CLIENT_KEY = "YOUR_KEY_HERE";

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        // If you would like all objects to be private by default, remove this line.
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}