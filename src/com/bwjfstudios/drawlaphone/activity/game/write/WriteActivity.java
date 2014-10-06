package com.bwjfstudios.drawlaphone.activity.game.write;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.game.AGameActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Screen in which the user writes a word to describe a given picture and then sends it when done
 */
public class WriteActivity extends AGameActivity {

  private TextView instructions; // Text describing what to do
  private ParseImageView picture; // The picture from the previous turn
  private EditText guess; // What the player thinks the picture is

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write);

    // Assign fields
    this.instructions = (TextView) this.findViewById(R.id.write_instructions_1);
    this.picture = (ParseImageView) this.findViewById(R.id.write_image);
    this.guess = (EditText) this.findViewById(R.id.write_field);

    // Initialize UI
    this.initUI();
  }

  // Loads the picture from parse
  private void initUI() {
    try {
      // Get the picture if there is one
      this.initPicture();
    } catch (ParseException e) {
      // If there's no picture then use default text
      this.instructions.setText(getString(R.string.write_instructions_success));
    }
  }

  // Loads the most recent picture from "The Cloud"
  private void initPicture() throws ParseException {
    // Try to get the picture
    ParseObject currentGame = this.getCurrentGame();
    ParseRelation<ParseObject> pictureRelation = currentGame.getRelation("pictures");
    ParseObject
        parsePicture =
        pictureRelation.getQuery().orderByDescending("createdAt").getFirst();

    // Load the picture if it exists
    if (parsePicture != null) {
      ParseFile parsePictureFile = parsePicture.getParseFile("content");
      this.picture.setParseFile(parsePictureFile);
      this.picture.loadInBackground();
    }
  }

  // Sends the word the player wrote to Parse and then goes back to the main screen
  public Runnable getSendRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        try {
          saveDataToCloud();
          finish();
        } catch (ParseException e) {
          makeText("Error sending move");
        }
      }
    };
  }

  @Override // Used in getting the number of words already entered in the game
  protected String getCountString() {
    return "wordCount";
  }

  // Enters the User's word into the database and then moves control to the next player
  private void saveDataToCloud() throws ParseException {
    // Make word
    ParseObject currentWord = getParseWord();

    // Save word to game
    ParseObject currentGame = this.getCurrentGame();
    currentGame.getRelation("words").add(currentWord);
    currentGame.save();

    // Move to the next player
    this.moveToNextTurn(currentGame);
  }

  // Turns the User's guess into a ParseObject
  private ParseObject getParseWord() throws ParseException {
    ParseObject currentWord = new ParseObject("Word");
    currentWord.put("content", this.guess.getText().toString());
    currentWord.save();
    return currentWord;
  }
}
