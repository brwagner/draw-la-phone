package com.bwjfstudios.drawlaphone.activity.complete;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.AActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class CompleteGameActivity extends AActivity {

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_game);

        this.layout = (LinearLayout) this.findViewById(R.id.LinearLayout1);

        this.initGameHistory();
    }

    void initGameHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ParseObject currentGame = getCurrentGame();
                    List<ParseObject> pictures = currentGame.getRelation("pictures").getQuery().orderByAscending("createdAt").find();
                    List<ParseObject> words = currentGame.getRelation("words").getQuery().orderByAscending("createdAt").find();

                    for (int i = 0; i < largestSize(pictures, words); i++) {
                        if (i < words.size()) {
                            displayWord(words, i);
                        }

                        if (i < pictures.size()) {
                            displayImage(pictures, i);
                        }
                    }
                } catch (ParseException e) {
                    makeText("Could not load game at this time");
                }
            }

            private void displayWord(List<ParseObject> words, int i) {
                final TextView textView = new TextView(getApplicationContext());
                String parseWord = words.get(i).getString("content");
                textView.setText(parseWord);
                textView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Large);
                textView.setTextColor(getResources().getColor(R.color.black));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.addView(textView);
                    }
                });
            }

            private void displayImage(List<ParseObject> pictures, int i) {
                final ParseImageView imageView = new ParseImageView(getApplicationContext());
                ParseFile file = pictures.get(i).getParseFile("content");
                imageView.setParseFile(file);
                imageView.loadInBackground();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.addView(imageView);
                    }
                });
            }
        }).start();
    }

    private int largestSize(List<ParseObject> pictures, List<ParseObject> words) {
        if (pictures.size() > words.size()) {
            return pictures.size();
        } else {
            return words.size();
        }
    }

    private ParseObject getCurrentGame() throws ParseException {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Game");
        return parseQuery.get(getIntent().getStringExtra("GAME_ID"));
    }
}
