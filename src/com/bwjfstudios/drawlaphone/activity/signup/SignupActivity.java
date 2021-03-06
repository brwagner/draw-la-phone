package com.bwjfstudios.drawlaphone.activity.signup;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.AActivity;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.regex.Pattern;

/**
 * Activity used to sign up for app
 */
public class SignUpActivity extends AActivity {

  private EditText usernameField; // Enter username
  private EditText password1Field; // Enter password
  private EditText password2Field; // Confirm password
  private Button createAccountButton; // Registers user

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);

    // Assign fields
    this.usernameField = (EditText) this.findViewById(R.id.signup_username_field);
    this.password1Field = (EditText) this.findViewById(R.id.signup_password1_field);
    this.password2Field = (EditText) this.findViewById(R.id.signup_password2_field);
    this.createAccountButton = (Button) this.findViewById(R.id.signup_create_account_button);

    // Initialize UI elements
    this.initUI();
  }

  // Sets up UI elements
  private void initUI() {
    // Sign up user if "Create Account" is selected
    this.createAccountButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        signUpAttempt();
      }
    });
  }

  // Attempts to register user with Parse
  private void signUpAttempt() {
    String username = this.usernameField.getText().toString();
    String password = this.password1Field.getText().toString();
    String confirmPass = this.password2Field.getText().toString();

    // Make sure username/password combination is valid, then create the user account
    if (this.isValid(username, password, confirmPass)) {
      animateViewSuccess(createAccountButton);
      getSingletonThread().startThread(registerUser(username, password));
    } else {
      animateViewFail(createAccountButton);
    }
  }

  private boolean isValid(String username, String password, String confirmPass) {
    // Check if passwords are the same
    if (!password.equals(confirmPass)) {
      makeText("Passwords Do Not Match");
      return false;
    }

    // Check if lengths are greater than 4 chars
    if (username.length() < 4 || password.length() < 4) {
      makeText("Username or Password less than 4 characters");
      return false;
    }

    // Check if username and password are alphanumeric strings
    Pattern p = Pattern.compile("[^a-zA-Z0-9]");
    boolean isAlphaNum = p.matcher(username).find() || p.matcher(password).find();
    if (isAlphaNum) {
      makeText("Username and Password must be alphanumeric");
      return false;
    }
    return true;
  }

  // Registering a user creates a Parse User on our Parse account
  private Runnable registerUser(final String username, final String password) {
    return new Runnable() {
      @Override
      public void run() {
        try {
          ParseUser user = new ParseUser();
          user.setUsername(username);
          user.setPassword(password);
          user.signUp();
          makeText("Successfully Signed up, please log in.");
          finish();
        } catch (ParseException e) {
          makeText("Error signing up");
        }
      }
    };
  }
}
