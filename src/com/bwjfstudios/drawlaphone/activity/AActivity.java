package com.bwjfstudios.drawlaphone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.util.SingletonThread;
import com.bwjfstudios.drawlaphone.util.Util;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * contains functionality needed in many activities
 */
public abstract class AActivity extends Activity {

    private SingletonThread singletonThread; // Executes only one tread at a time

    @Override // Default initialization
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singletonThread = new SingletonThread();
    }

    @Override // Default animations on load
    protected void onStart() {
        this.findViewById(R.id.root).setAnimation(Util.getFadeIn(1500));
        super.onStart();
    }

    // Performs Toast automatically on the UI thread
    protected void makeText(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Success animation (Fade in)
    protected void animateViewSuccess(final View button) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.startAnimation(Util.getSuccessAnim());
            }
        });
    }

    // Fail animation (Rotates Screen back and forth)
    protected void animateViewFail(final View button) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.startAnimation(Util.getFailAnim(getApplicationContext()));
            }
        });
    }

    // Return the game from parse that matches the GAME_ID passed into certain activities
    public ParseObject getCurrentGame() throws ParseException {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Game");
        return parseQuery.get(getIntent().getStringExtra("GAME_ID"));
    }

    // Accessor
    protected SingletonThread getSingletonThread() {
        return singletonThread;
    }
}
