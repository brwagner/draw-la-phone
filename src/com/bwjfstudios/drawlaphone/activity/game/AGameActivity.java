package com.bwjfstudios.drawlaphone.activity.game;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.AActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public abstract class AGameActivity extends AActivity {
    private long lastRefreshTime;

    protected abstract Runnable getSendRunnable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lastRefreshTime = System.currentTimeMillis();
    }

    void send(Runnable runnable) {
        if (System.currentTimeMillis() - this.lastRefreshTime > 10000) {
            animateViewSuccess(findViewById(R.id.root));
            this.lastRefreshTime = System.currentTimeMillis();
            getSingletonThread().startThread(runnable);
        }
    }

    ParseUser getNextUser(List<ParseUser> users) {
        return users.get(this.getNextIndex(users));
    }

    int getNextIndex(List<ParseUser> users) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                return (i + 1) % users.size();
            }
        }
        return -1;
    }

    protected ParseObject getCurrentGame() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        return query.get(getIntent().getStringExtra("GAME_ID"));
    }

    boolean isEndOfRound(List<ParseUser> users) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                return i == users.size() - 1;
            }
        }
        return false;
    }

    protected void moveToNextTurn(ParseObject currentGame) throws ParseException {
        moveToNextPlayer(currentGame);
        incrementIndexedCount(currentGame);
        currentGame.save();
    }

    private void moveToNextPlayer(ParseObject currentGame) throws ParseException {
        ParseRelation<ParseUser> userRelation = currentGame.getRelation("players");
        List<ParseUser> users = userRelation.getQuery().orderByAscending("objectId").find();
        if (isEndOfRound(users)) {
            currentGame.put("currentRound", currentGame.getInt("currentRound") + 1);
            if (currentGame.getInt("currentRound") > currentGame.getInt("maxRounds")) {
                currentGame.put("isComplete", true);
            }
        }
        ParseUser nextUser = this.getNextUser(users);
        currentGame.put("currentPlayer", nextUser);
        currentGame.put("currentPlayerName", nextUser.getUsername());
    }

    private void incrementIndexedCount(ParseObject currentGame) {
        currentGame.put(getCountString(), currentGame.getInt(getCountString()) + 1);
    }

    protected abstract String getCountString();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.draw_write_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_done:
                this.send(getSendRunnable());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
