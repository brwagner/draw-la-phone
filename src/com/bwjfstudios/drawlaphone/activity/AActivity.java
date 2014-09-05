package com.bwjfstudios.drawlaphone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.util.SingletonThread;
import com.bwjfstudios.drawlaphone.util.Util;

// contains functionality needed in many activities
public abstract class AActivity extends Activity {

    private SingletonThread singletonThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singletonThread = new SingletonThread();
    }

    @Override
    protected void onStart() {
        this.findViewById(R.id.root).setAnimation(Util.getFadeIn(1500));
        super.onStart();
    }

    protected void makeText(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() { Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show(); }
        });
    }

    protected void animateViewSuccess(final View button) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.startAnimation(Util.getSuccessAnim());
            }
        });
    }

    protected void animateViewFail(final View button) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {button.startAnimation(Util.getFailAnim(getApplicationContext()));
            }
        });
    }

    protected SingletonThread getSingletonThread() {
        return singletonThread;
    }
}
