package com.bwjfstudios.drawlaphone.activity.game.write;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.game.AGameActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseRelation;

public class WriteActivity extends AGameActivity {

    private TextView instructions;
    private ParseImageView picture;
    private EditText guess;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        this.instructions = (TextView) this.findViewById(R.id.write_instructions_1);
        this.picture = (ParseImageView) this.findViewById(R.id.write_image);
        this.guess = (EditText) this.findViewById(R.id.write_field);

        this.initUI();
    }

    private void initUI() {
        try {
            this.initPicture();
        } catch (ParseException e) {
            this.instructions.setText(this.getResources().getString(R.string.write_instructions_success));
        }
    }

    private void initPicture() throws ParseException {
        ParseObject currentGame = this.getCurrentGame();
        ParseRelation<ParseObject> pictureRelation = currentGame.getRelation("pictures");
        ParseObject parsePicture = pictureRelation.getQuery().orderByDescending("createdAt").getFirst();
        if (parsePicture != null) {
            ParseFile parsePictureFile = parsePicture.getParseFile("content");
            this.picture.setParseFile(parsePictureFile);
            this.picture.loadInBackground();
        }
    }

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

    @Override
    protected String getCountString() {
        return "wordCount";
    }

    private void saveDataToCloud() throws ParseException {
        // make word
        ParseObject currentWord = getParseWord();

        // save word to game
        ParseObject currentGame = this.getCurrentGame();
        currentGame.getRelation("words").add(currentWord);
        currentGame.save();

        // move to the next player
        this.moveToNextTurn(currentGame);
    }

    private ParseObject getParseWord() throws ParseException {
        ParseObject currentWord = new ParseObject("Word");
        currentWord.put("content", this.guess.getText().toString());
        currentWord.save();
        return currentWord;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
