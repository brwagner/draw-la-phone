package com.bwjfstudios.drawlaphone.activity.join;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class JoinActivity extends AActivity {

    private ListView gameList;
    private GameArrayAdapter gameListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.initAdapter();
        this.initUI();
    }

    private void initAdapter() {
        ArrayList<ParseObject> games = new ArrayList<ParseObject>();
        this.gameListAdapter = new GameArrayAdapter(this, games);
    }

    private void initUI() {
        initGamesList();
    }

    private void initGamesList() {
        this.gameList = (ListView) this.findViewById(R.id.search_list);
        this.gameList.setAdapter(gameListAdapter);
        this.gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Runnable runnable = getJoinGameRunnable(position);
                getSingletonThread().startThread(runnable);
            }
        });
    }

    private Runnable getJoinGameRunnable(final int position) {
        return new Runnable() {
            @Override
            public void run() {
                joinGame(position);
            }
        };
    }

    private void joinGame(int position) {
        try {
            final ParseObject game = gameListAdapter.getItem(position);
            int currentNumPlayers = game.getInt("currentNumPlayers");
            int maxPlayers = game.getInt("maxPlayers");
            if (currentNumPlayers < maxPlayers) {
                game.getRelation("players").add(ParseUser.getCurrentUser());
                if (currentNumPlayers + 1 == maxPlayers) {
                    game.put("isStart", true);
                }
                game.put("currentNumPlayers", currentNumPlayers + 1);
                game.save();
                makeText("Successfully joined game");
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

    private void initGames(final String search) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
                    final List<ParseObject> games = query
                            .whereStartsWith("name", search)
                            .whereNotEqualTo("isComplete", true)
                            .whereNotEqualTo("players", ParseUser.getCurrentUser()).setLimit(10).find();
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
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    gameListAdapter.clear();
                    initGames(query);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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

    private void updateSmiley(int visibility) {
        TextView textView = (TextView) findViewById(R.id.search_text);
        textView.setVisibility(visibility);
        ImageView imageView = (ImageView) findViewById(R.id.search_image);
        imageView.setVisibility(visibility);
    }
}
