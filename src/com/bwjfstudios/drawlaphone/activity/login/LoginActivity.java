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
import com.bwjfstudios.drawlaphone.activity.signup.SignUpActivity;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Initial screen used to login to the app
 */
public class LoginActivity extends AActivity {

  private EditText usernameField; // Enter username
  private EditText passwordField; // Enter password
  private Button logInButton; // Logs user in if username and password match
  private Button signUpButton; // Goes to signUp activity

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Assign fields
    this.usernameField = (EditText) this.findViewById(R.id.login_username_field);
    this.passwordField = (EditText) this.findViewById(R.id.login_password_field);
    this.logInButton = (Button) this.findViewById(R.id.login_login_button);
    this.signUpButton = (Button) this.findViewById(R.id.login_signup_button);

    // Automatic login
    this.initAuthentication();

    // Set up UI if login fails
    this.initUI();
  }

  // Attempt to log the user in
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
    initSignUpButton();
  }

  // Create an Account button for users without an account
  private void initSignUpButton() {
    this.signUpButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Go to the sign up activity
        animateViewSuccess(signUpButton);
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
      }
    });
  }

  // Sign in button for users with an account
  private void initLoginButton() {
    this.logInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startLoginAttempt();
      }
    });
  }

  // Handles logging on
  private void startLoginAttempt() {
    // Get username and password
    final String username = this.usernameField.getText().toString();
    final String password = this.passwordField.getText().toString();

    // Check if username and password were entered
    if (this.isEmpty(username, password)) {
      animateViewFail(logInButton);
      makeText("Please enter username and password");
    } else {
      animateViewSuccess(logInButton);
      // Log user in in background
      getSingletonThread().startThread(new Runnable() {
        @Override
        public void run() {
          finishLoginAttempt(username, password);
        }
      });
    }
  }

  // Validate user
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

  // Checks if either username or password are empty
  private boolean isEmpty(String username, String password) {
    return username.length() == 0 || password.length() == 0;
  }
}
