package com.bwjfstudios.drawlaphone.activity.join;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.AActivity;
import com.bwjfstudios.drawlaphone.util.GameArrayAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen used by Users to join existing games
 */
public class JoinActivity extends AActivity {

  private ListView gameList; // List of games available
  private GameArrayAdapter gameListAdapter; // Adapts ParseObjects representing games for the list

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    this.initAdapter();
    this.initGameList();
  }

  // Sets up a new GameListAdapter
  private void initAdapter() {
    ArrayList<ParseObject> games = new ArrayList<ParseObject>();
    this.gameListAdapter = new GameArrayAdapter(this, games);
  }

  // Sets up the GameList
  private void initGameList() {
    // Assign list
    this.gameList = (ListView) this.findViewById(R.id.search_list);
    // Set adapter and listener
    this.gameList.setAdapter(gameListAdapter);
    this.gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        // Joins only one game at a time in the background
        Runnable runnable = getJoinGameRunnable(position);
        getSingletonThread().startThread(runnable);
      }
    });
  }

  // Gets a runnable version of the joinGame method
  private Runnable getJoinGameRunnable(final int position) {
    return new Runnable() {
      @Override
      public void run() {
        joinGame(position);
      }
    };
  }

  // Adds the User to the game selected
  private void joinGame(int position) {
    try {
      // Get the game from the adapter
      final ParseObject game = gameListAdapter.getItem(position);
      // Get the current number of players
      int currentNumPlayers = game.getInt("currentNumPlayers");
      // Get the maximum number of players
      int maxPlayers = game.getInt("maxPlayers");

      // Check if game is at capacity
      if (currentNumPlayers < maxPlayers) {
        // Add the User to the current game
        game.getRelation("players").add(ParseUser.getCurrentUser());

        // If game is at capacity, start the game
        if (currentNumPlayers + 1 == maxPlayers) {
          game.put("isStart", true);
        }

        // Increase the current number of players by 1
        game.put("currentNumPlayers", currentNumPlayers + 1);

        // Save the game and give confirmation
        game.save();
        makeText("Successfully joined game");

        // Remove the game the user just joined from the list
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            gameListAdapter.remove(game);
          }
        });
      } else {
        makeText("Game already full");
      }
    } catch (ParseException e) {
      makeText("Error joining game");
    }
  }

  // Populates the adapter with games matching a given name
  private void initGames(final String search) {
    // Run in background
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // Get the games the player can join
          ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
          final List<ParseObject> games = query
              .whereStartsWith("name", search)
              .whereNotEqualTo("isComplete", true)
              .whereNotEqualTo("players", ParseUser.getCurrentUser()).setLimit(10).find();

          // Populate the adapter
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              gameListAdapter.addAll(games);
            }
          });
        } catch (ParseException e) {
          makeText("Could not find games");
        }
      }
    }).start();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.search_activity_menu, menu);

    // Make a search bar to search games
    final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (query.length() > 0) {
          gameListAdapter.clear();
          initGames(query);
          InputMethodManager
              imm =
              (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
          updateSmiley(View.INVISIBLE);
        }
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0) {
          updateSmiley(View.VISIBLE);
        }
        return false;
      }
    });
    return true;
  }

  // Sets an image depending on if the User has found games
  private void updateSmiley(int visibility) {
    TextView textView = (TextView) findViewById(R.id.search_text);
    textView.setVisibility(visibility);
    ImageView imageView = (ImageView) findViewById(R.id.search_image);
    imageView.setVisibility(visibility);
  }
}
