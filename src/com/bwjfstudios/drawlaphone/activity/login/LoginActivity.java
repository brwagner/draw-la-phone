package com.bwjfstudios.drawlaphone.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.AActivity;
import com.bwjfstudios.drawlaphone.activity.main.MainActivity;
import com.bwjfstudios.drawlaphone.activity.signup.SignupActivity;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.usernameField = (EditText) this.findViewById(R.id.login_username_field);
        this.passwordField = (EditText) this.findViewById(R.id.login_password_field);
        this.loginButton = (Button) this.findViewById(R.id.login_login_button);
        this.signupButton = (Button) this.findViewById(R.id.login_signup_button);

        this.initAuthentication();
        this.initUI();
    }

    // Parse black magic that logs the user in
    private void initAuthentication() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            makeText(currentUser.getUsername() + " successfully logged in");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Opens the screen
    private void initUI() {
        initLoginButton();
        initSignupButton();
    }

    // Create an Account button for chumps without an account
    private void initSignupButton() {
        this.signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateViewSuccess(signupButton);
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // Sign in button for winners
    private void initLoginButton() {
        this.loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginAttempt();
            }
        });
    }

    // Handles logging on
    private void startLoginAttempt() {
        final String username = this.usernameField.getText().toString();
        final String password = this.passwordField.getText().toString();

        if (this.isEmpty(username, password)) {
            animateViewFail(loginButton);
            makeText("Please enter both username and password");
        } else {
            animateViewSuccess(loginButton);
            getSingletonThread().startThread(new Runnable() {
                @Override
                public void run() {
                    finishLoginAttempt(username, password);
                }
            });
        }
    }

    private void finishLoginAttempt(String username, String password) {
        try {
            ParseUser.logIn(username, password);
            makeText(ParseUser.getCurrentUser().getUsername() + " successfully logged in");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (ParseException e) {
            makeText("User Not Found");
        }
    }

    // Does literally exactly what you would think it would do in this context
    private boolean isEmpty(String username, String password) {
        return username.length() == 0 || password.length() == 0;
    }
}
