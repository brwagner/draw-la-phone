package com.bwjfstudios.drawlaphone.activity.host;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.AActivity;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Screen used by Users to create games
 */
public class HostActivity extends AActivity {

  private EditText nameField; // Game name used for searching purposes
  private SliderTextView numPlayers; // Max number of players in game
  private SliderTextView numRounds; // Max number of rounds in game
  private Button startButton; // Creates the game

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_host);

    // Assign fields
    this.nameField = (EditText) this.findViewById(R.id.host_game_name_field);
    this.numPlayers = new OddSliderTextView(
        (TextView) this.findViewById(R.id.host_number_players_int),
        (SeekBar) this.findViewById(R.id.host_number_players_bar)
    );
    this.numRounds = new SliderTextView(
        (TextView) this.findViewById(R.id.host_number_rounds_int),
        (SeekBar) this.findViewById(R.id.host_number_rounds_bar)
    );
    this.startButton = (Button) this.findViewById(R.id.host_start_button);

    // Initializes UI
    this.initUI();
  }

  // Sets up UI elements
  private void initUI() {
    this.initNameField();
    this.numPlayers.init();
    this.numRounds.init();
    this.initStartButton();
  }

  // Sets default text for game
  private void initNameField() {
    this.nameField.setText(ParseUser.getCurrentUser().getUsername() + "'s game");
  }

  // Sets default behaviour for button
  private void initStartButton() {
    this.startButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        animateViewSuccess(startButton);
        getSingletonThread().startThread(attemptHosting());
      }
    });
  }

  // Try to set up a game in the background
  private Runnable attemptHosting() {
    return new Runnable() {
      @Override
      public void run() {
        try {
          startGame();
          finish();
        } catch (ParseException e) {
          makeText("Error starting game");
        }
      }
    };
  }

  // Creates starting information required for a Game to function
  private void startGame() throws ParseException {
    // Make a new Game
    ParseObject game = new ParseObject("Game");

    // Game Name field at top of screen
    String name = this.nameField.getText().toString();
    game.put("name", name);

    // Creates of list of ParseUsers and adds the host-user to that list
    ParseRelation<ParseUser> players = game.getRelation("players");
    players.add(ParseUser.getCurrentUser());

    // Adds the user to his game
    game.put("currentPlayer", ParseUser.getCurrentUser());

    // set current player name
    game.put("currentPlayerName", ParseUser.getCurrentUser().getUsername());

    // Adds the current number of players
    game.put("currentNumPlayers", 1);

    // Slider stuff for the desired number of players
    game.put("maxPlayers", this.numPlayers.getSeekNumber());

    // Adds the current round
    game.put("currentRound", 1);

    // Slider stuff for the desired number of rounds
    game.put("maxRounds", this.numRounds.getSeekNumber());

    // set game to not started
    game.put("isStart", false);

    // Set game to incomplete
    game.put("isComplete", false);

    // Set picture and word count
    game.put("wordCount", 0);
    game.put("pictureCount", 0);

    // Makes games public
    ParseACL gameACL = new ParseACL();
    gameACL.setPublicReadAccess(true);
    gameACL.setPublicWriteAccess(true);
    game.setACL(gameACL);

    // Saves the game in the Parse cloud
    game.save();
  }
}

