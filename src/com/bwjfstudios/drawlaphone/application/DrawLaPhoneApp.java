package com.bwjfstudios.drawlaphone.application;

import android.app.Application;

import com.bwjfstudios.drawlaphone.R;
import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Sets up the app to use the Parse API
 */
public class DrawLaPhoneApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Add your initialization code here
    Parse.initialize(this, getString(R.string.APPLICATION_ID), getString(R.string.CLIENT_KEY));

    // If you would like all objects to be private by default, remove this line.
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
  }
}