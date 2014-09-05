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

public class MainActivity extends AActivity {

    private Button hostButton;
    private Button joinButton;
    private ListView gameList;
    private TextView smileFrownText;
    private ImageView smileFrownImage;
    private LAButtonRadioGroup laButtonGroup;
    private long lastRefreshTime;

    @Override
    protected void onStart() {
        populateAdapters(laButtonGroup.getAdapters());
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.hostButton = (Button) findViewById(R.id.main_host_button);
        this.joinButton = (Button) findViewById(R.id.main_join_button);

        this.gameList = (ListView) findViewById(R.id.main_list);

        this.smileFrownText = (TextView) findViewById(R.id.main_smile_frown_text);
        this.smileFrownImage = (ImageView) findViewById(R.id.main_smile_frown_image);

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

    // inits the LAButton Data chunk
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

    private void initGameButtons() {
        for (LAButton laButton : this.laButtonGroup.getLAButtons()) {
            this.initGameButton(laButton.getButton(), laButton.getAdapter(), laButton.getListener());
        }
    }

    private void initGameButton(final Button button, final GameArrayAdapter adapter, final OnItemClickListener listener) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateViewSuccess(button);
                laButtonGroup.select(button);
                initSmileFrown(laButtonGroup.getAdapters().get(laButtonGroup.getSelectedInt()));

                gameList.setAdapter(adapter);
                gameList.setOnItemClickListener(listener);
                animateViewSuccess(gameList);
            }
        });
    }

    private void initList() {
        this.gameList.setAdapter(this.laButtonGroup.getLAButtons().get(0).getAdapter());
        this.gameList.setOnItemClickListener(this.laButtonGroup.getLAButtons().get(0).getListener());
    }

    private void populateAdapters(final List<GameArrayAdapter> adapters) {
        this.lastRefreshTime = System.currentTimeMillis();
        getSingletonThread().startThread(getPopulateAdaptersRunnable(adapters));
    }

    private Runnable getPopulateAdaptersRunnable(final List<GameArrayAdapter> adapters) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    final GameArrayAdapter yourMoveAdapter = adapters.get(0);
                    final GameArrayAdapter currentGameAdapter = adapters.get(1);
                    final GameArrayAdapter completeGameAdapter = adapters.get(2);

                    final List<ParseObject> games = getGames();

                    final List<ParseObject> yourMoves = Util.filter(games, new IsYourMove());
                    final List<ParseObject> currentGames = Util.filter(games, new IsCurrent());
                    final List<ParseObject> completeGames = Util.filter(games, new IsComplete());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            yourMoveAdapter.clear();
                            currentGameAdapter.clear();
                            completeGameAdapter.clear();

                            yourMoveAdapter.addAll(yourMoves);
                            currentGameAdapter.addAll(currentGames);
                            completeGameAdapter.addAll(completeGames);

                            initSmileFrown(laButtonGroup.getAdapters().get(laButtonGroup.getSelectedInt()));
                        }
                    });
                } catch (ParseException e) {
                    makeText("Error loading game data");
                }
            }

            // get initial games list using parse query
            private List<ParseObject> getGames() throws ParseException {
                ParseQuery<ParseObject> queryMoves = ParseQuery.getQuery("Game")
                        .whereEqualTo("players", ParseUser.getCurrentUser());
                return queryMoves.find();
            }

            class IsYourMove implements Predicate<ParseObject> {
                @Override
                public boolean apply(ParseObject parseObject) {
                    boolean isStart = parseObject.getBoolean("isStart");
                    boolean userIdEqual = parseObject.getParseObject("currentPlayer").getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
                    boolean isNotDone = !parseObject.getBoolean("isComplete");
                    return isStart && isNotDone && userIdEqual ;
                }
            }

            class IsCurrent implements Predicate<ParseObject> {
                @Override
                public boolean apply(ParseObject parseObject) {
                    return !parseObject.getBoolean("isComplete");
                }
            }

            class IsComplete implements Predicate<ParseObject> {
                @Override
                public boolean apply(ParseObject parseObject) {
                    return parseObject.getBoolean("isComplete");
                }
            }
        };
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
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

    private void refreshAdapters() {
        if (System.currentTimeMillis() - this.lastRefreshTime > 3000) {
            this.lastRefreshTime = System.currentTimeMillis();
            populateAdapters(laButtonGroup.getAdapters());
            animateViewFail(findViewById(R.id.root));
        }
    }

    private class YourMoveOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                ParseObject currentGame = laButtonGroup.getLAButtons().get(0).getAdapter().getItem(position);
                int pictureCount = getCount(currentGame, "pictureCount");
                int wordCount = getCount(currentGame, "wordCount");
                if (pictureCount < wordCount) {
                    this.goToActivity(DrawActivity.class, currentGame);
                } else {
                    this.goToActivity(WriteActivity.class, currentGame);
                }
            } catch (ParseException e) {
                makeText("Error joining game");
            }
        }

        private int getCount(ParseObject currentGame, String that) throws ParseException {
            return currentGame.getInt(that);
        }

        private void goToActivity(Class activity, ParseObject currentGame) {
            Intent intent = new Intent(MainActivity.this, activity);
            intent.putExtra("GAME_ID", currentGame.getObjectId());
            startActivity(intent);
        }
    }

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