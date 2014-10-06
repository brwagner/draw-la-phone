/*  .java for the opening screen
 *  
 */
package com.bwjfstudios.drawlaphone.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.AActivity;
import com.bwjfstudios.drawlaphone.activity.complete.CompleteGameActivity;
import com.bwjfstudios.drawlaphone.activity.game.draw.DrawActivity;
import com.bwjfstudios.drawlaphone.activity.game.write.WriteActivity;
import com.bwjfstudios.drawlaphone.activity.host.HostActivity;
import com.bwjfstudios.drawlaphone.activity.join.JoinActivity;
import com.bwjfstudios.drawlaphone.util.GameArrayAdapter;
import com.bwjfstudios.drawlaphone.util.Util;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main hub the user sees after logging Features: Hosting games Joining games Viewing games
 * where it's the users turn Viewing games the user has joined View games the user has completed
 */
public class MainActivity extends AActivity {

  private Button hostButton; // Brings user to HostActivity
  private Button joinButton; // Brings user to JoinActivity
  private ListView gameList; // List of games
  private TextView smileFrownText; // Text accompanied by picture
  private ImageView smileFrownImage; // Picture accompanied by text
  private LAButtonRadioGroup laButtonGroup; // Group of buttons used in selecting games
  private long lastRefreshTime; // Prevents spamming refresh

  @Override // Refreshes the games list when the user reviews the activity
  protected void onStart() {
    populateAdapters(laButtonGroup.getAdapters());
    super.onStart();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Assign fields
    this.hostButton = (Button) findViewById(R.id.main_host_button);
    this.joinButton = (Button) findViewById(R.id.main_join_button);
    this.gameList = (ListView) findViewById(R.id.main_list);
    this.smileFrownText = (TextView) findViewById(R.id.main_smile_frown_text);
    this.smileFrownImage = (ImageView) findViewById(R.id.main_smile_frown_image);

    // Initialize UI elements
    this.initUI();
  }

  // Opens the screen
  private void initUI() {
    initLAButtonRadioGroup();
    initJoinButton();
    initHostButton();
    initGameButtons();
    initList();
  }

  // Initializes the Listener Adapter Button structure
  private void initLAButtonRadioGroup() {
    LAButton yourMove = new LAButton(
        new YourMoveOnItemClickListener(),
        new GameArrayAdapter(this, new ArrayList<ParseObject>()),
        (Button) findViewById(R.id.main_your_turn_button));
    LAButton current = new LAButton(
        null,
        new GameArrayAdapter(this, new ArrayList<ParseObject>()),
        (Button) findViewById(R.id.main_current_game_button));
    LAButton complete = new LAButton(
        new CompleteGameOnItemClickListener(),
        new GameArrayAdapter(this, new ArrayList<ParseObject>()),
        (Button) findViewById(R.id.main_complete_game_button));
    this.laButtonGroup = new LAButtonRadioGroup(Arrays.asList(yourMove, current, complete));
  }

  // Join Game button
  private void initJoinButton() {
    this.joinButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        animateViewSuccess(joinButton);
        Intent intent = new Intent(MainActivity.this, JoinActivity.class);
        startActivity(intent);
      }
    });
  }

  // Create a Game button
  private void initHostButton() {
    this.hostButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        animateViewSuccess(hostButton);
        Intent intent = new Intent(MainActivity.this, HostActivity.class);
        startActivity(intent);
      }
    });
  }

  // Iterates through the list of buttons and initializes them
  private void initGameButtons() {
    for (LAButton laButton : this.laButtonGroup.getLAButtons()) {
      this.initGameButton(laButton.getButton(), laButton.getAdapter(), laButton.getListener());
    }
  }

  // Sets up behavior for the given Listeners, Adapters, and Buttons
  private void initGameButton(final Button button, final GameArrayAdapter adapter,
                              final OnItemClickListener listener) {
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Visual result click
        animateViewSuccess(button);
        laButtonGroup.select(button);
        initSmileFrown(laButtonGroup.getAdapters().get(laButtonGroup.getSelectedInt()));
        // Update which listener and adapter is used
        gameList.setAdapter(adapter);
        gameList.setOnItemClickListener(listener);
        animateViewSuccess(gameList);
      }
    });
  }

  // Sets up initial state of the list of games
  private void initList() {
    this.gameList.setAdapter(this.laButtonGroup.getLAButtons().get(0).getAdapter());
    this.gameList.setOnItemClickListener(this.laButtonGroup.getLAButtons().get(0).getListener());
  }

  // Check if the user isn't spamming refresh and if not get game data from Parse
  private void populateAdapters(final List<GameArrayAdapter> adapters) {
    this.lastRefreshTime = System.currentTimeMillis();
    getSingletonThread().startThread(getPopulateAdaptersRunnable(adapters));
  }

  // Gets game data from parse
  private Runnable getPopulateAdaptersRunnable(final List<GameArrayAdapter> adapters) {
    return new Runnable() {
      @Override
      public void run() {
        try {
          // Set up adapters for each potential game list
          final GameArrayAdapter yourMoveAdapter = adapters.get(0);
          final GameArrayAdapter currentGameAdapter = adapters.get(1);
          final GameArrayAdapter completeGameAdapter = adapters.get(2);

          // Get game data from parse
          final List<ParseObject> games = getGames();

          // Filter games based on criteria
          final List<ParseObject> yourMoves = Util.filter(games, new IsYourMove());
          final List<ParseObject> currentGames = Util.filter(games, new IsCurrent());
          final List<ParseObject> completeGames = Util.filter(games, new IsComplete());

          // Update UI on separate thread
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              // Clear old games
              yourMoveAdapter.clear();
              currentGameAdapter.clear();
              completeGameAdapter.clear();
              // Add new games
              yourMoveAdapter.addAll(yourMoves);
              currentGameAdapter.addAll(currentGames);
              completeGameAdapter.addAll(completeGames);
              // Update visual
              initSmileFrown(laButtonGroup.getAdapters().get(laButtonGroup.getSelectedInt()));
            }
          });
        } catch (ParseException e) {
          makeText("Error loading game data");
        }
      }

      // Get initial games list using parse query
      private List<ParseObject> getGames() throws ParseException {
        ParseQuery<ParseObject> queryMoves = ParseQuery.getQuery("Game")
            .whereEqualTo("players", ParseUser.getCurrentUser());
        return queryMoves.find();
      }

      // Determines if it is the player's turn
      class IsYourMove implements Predicate<ParseObject> {

        @Override
        public boolean apply(ParseObject parseObject) {
          boolean isStart = parseObject.getBoolean("isStart");
          boolean
              userIdEqual =
              parseObject.getParseObject("currentPlayer").getObjectId()
                  .equals(ParseUser.getCurrentUser().getObjectId());
          boolean isNotDone = !parseObject.getBoolean("isComplete");
          return isStart && isNotDone && userIdEqual;
        }
      }

      // Determines if the player is currently in this game and it is currently active
      class IsCurrent implements Predicate<ParseObject> {

        @Override
        public boolean apply(ParseObject parseObject) {
          return !parseObject.getBoolean("isComplete");
        }
      }

      // Determines if the player is in this game and it is complete
      class IsComplete implements Predicate<ParseObject> {

        @Override
        public boolean apply(ParseObject parseObject) {
          return parseObject.getBoolean("isComplete");
        }
      }
    };
  }

  // Updates an image based on whether or not there are games in the list
  private void initSmileFrown(GameArrayAdapter adapter) {
    if (adapter.getCount() > 0) {
      this.smileFrownText.setText(getString(R.string.main_smile));
      this.smileFrownImage.setImageDrawable(getResources().getDrawable(R.drawable.smile));
    } else {
      this.smileFrownText.setText(getString(R.string.main_frown));
      this.smileFrownImage.setImageDrawable(getResources().getDrawable(R.drawable.frown));
    }
    animateViewSuccess(smileFrownText);
    animateViewSuccess(smileFrownImage);
  }

  @Override // Adds log out option
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_activity_menu, menu);
    return true;
  }

  @Override // Adds log out option
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.menu_log_out:
        ParseUser.logOut();
        finish();
        return true;
      case R.id.menu_refresh:
        refreshAdapters();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  // Repopulates the lists of games if the User has not been spamming
  private void refreshAdapters() {
    if (System.currentTimeMillis() - this.lastRefreshTime > 3000) {
      this.lastRefreshTime = System.currentTimeMillis();
      populateAdapters(laButtonGroup.getAdapters());
      animateViewFail(findViewById(R.id.root));
    }
  }

  // Moves the player to a game Activity if it is their turn
  private class YourMoveOnItemClickListener implements OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      try {
        // Get the game selected
        ParseObject
            currentGame =
            laButtonGroup.getLAButtons().get(0).getAdapter().getItem(position);
        // Get the number of words and pictures entered in the game
        int pictureCount = getCount(currentGame, "pictureCount");
        int wordCount = getCount(currentGame, "wordCount");
        // Check which activity the user should go to
        if (pictureCount < wordCount) {
          this.goToGameActivity(DrawActivity.class, currentGame);
        } else {
          this.goToGameActivity(WriteActivity.class, currentGame);
        }
      } catch (ParseException e) {
        makeText("Error joining game");
      }
    }

    // Gets the count of a specific list for a Game
    private int getCount(ParseObject currentGame, String that) throws ParseException {
      return currentGame.getInt(that);
    }

    // Used to start game activity
    private void goToGameActivity(Class activity, ParseObject currentGame) {
      Intent intent = new Intent(MainActivity.this, activity);
      intent.putExtra("GAME_ID", currentGame.getObjectId());
      startActivity(intent);
    }
  }

  // Moves player into a view of what happened in the selected game
  private class CompleteGameOnItemClickListener implements OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      ParseObject currentGame = laButtonGroup.getLAButtons().get(2).getAdapter().getItem(position);
      Intent intent = new Intent(MainActivity.this, CompleteGameActivity.class);
      intent.putExtra("GAME_ID", currentGame.getObjectId());
      startActivity(intent);
    }
  }
}