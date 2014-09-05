package com.bwjfstudios.drawlaphone.activity.game.draw;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.activity.game.AGameActivity;
import com.bwjfstudios.drawlaphone.activity.game.draw.drawview.DrawView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.io.ByteArrayOutputStream;

public class DrawActivity extends AGameActivity {

    // All of the UI elements within the activity_draw layout
    //	part of the view the user actually draws on
    private DrawView drawView;
    //  color selection given to the user
    private LinearLayout palette;
    //  button displaying the current color
    private Button currentColor;
    //  slider for selecting the width of stroke
    private SeekBar sizeSelect;
    //  text displaying current stroke width
    private TextView sizeText;
    //  button for undoing previously drawn elements
    private Button undo;
    //  text of what to draw
    private TextView drawText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        // assign fields by ID in resources
        this.drawView = (DrawView) this.findViewById(R.id.drawView);
        this.palette = (LinearLayout) this.findViewById(R.id.palette);
        this.currentColor = (Button) this.findViewById(R.id.currentColor);
        this.sizeSelect = (SeekBar) this.findViewById(R.id.sizeSelect);
        this.sizeText = (TextView) this.findViewById(R.id.sizeText);
        this.undo = (Button) this.findViewById(R.id.undo);
        this.drawText = (TextView) this.findViewById(R.id.drawText);

        // setup what needs to be setup
        this.initUI();
    }

    private void initUI() {
        this.initPaletteColors();
        this.initCurrentColor();
        this.initSizeSelect();
        this.initSizeText();
        this.initUndo();
        this.initDrawText();
    }

    // get all of the colors enumerated in resources and create buttons with id's and backgrounds matching the colors
    private void initPaletteColors() {
        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 200, 1f);
        for (int col : this.getResources().getIntArray(R.array.rainbow)) {
            Button b = new Button(this);
            b.setId(col);
            b.setBackgroundColor(col);
            b.setText("");
            b.setLayoutParams(params);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // the ID of a button in the palette is the same as its color so just set the color to the id
                    drawView.setColor(v.getId());
                    currentColor.setBackgroundColor(v.getId());
                }
            });
            palette.addView(b);
        }
    }

    // set the current color background to the default black to start
    private void initCurrentColor() {
        this.currentColor.setBackgroundColor(this.getResources().getColor(R.color.black));
    }

    // set this activity as the listener for the slider
    private void initSizeSelect() {
        this.sizeSelect.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override // set the size of the stroke based on progress
            public void onStopTrackingTouch(SeekBar seekBar) {
                drawView.setSize(seekBar.getProgress());
            }

            @Override // display the current numeric position of the slider
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeText.setText(String.valueOf(progress));
            }

            @Override // i'm just a lonely old stub
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initSizeText() {
        this.sizeText.setText(String.valueOf(this.sizeSelect.getProgress()));
    }

    // set this activity as the listener for the undo button
    private void initUndo() {
        this.undo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.undoPath();
            }
        });
    }

    // gets the word from parse
    private void initDrawText() {
        this.drawText.setText(this.getWordFromParse());
    }

    // gets the current word
    private String getWordFromParse() {
        try {
            ParseObject currentGame = this.getCurrentGame();
            ParseRelation<ParseObject> wordRelation = currentGame.getRelation("words");
            ParseObject parseWord = wordRelation.getQuery().orderByDescending("createdAt").getFirst();
            if (parseWord != null) {
                return parseWord.getString("content");
            }
        } catch (ParseException e) {
            return getResources().getString(R.string.draw_instructions_success);
        }
        return getResources().getString(R.string.draw_instructions_fail);
    }

    // the method beams the image to Starship Enterpise for processing
    // Saves the drawn image as a JPEG to Parse
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
        return "pictureCount";
    }

    private void saveDataToCloud() throws ParseException {
        ParseObject currentGame = this.getCurrentGame();

        // get bitmap data
        byte[] data = this.getBitmapData();

        // create file using data and save
        ParseFile file = this.getParseFile(data);

        // create a new picture object using the file and save
        ParseObject pictureObject = this.getPictureObject(file);

        // create a relation between the picture and the game
        currentGame.getRelation("pictures").add(pictureObject);

        // move to the next player
        this.moveToNextTurn(currentGame);
    }

    // get bitmap data
    private byte[] getBitmapData() {
        Bitmap bitmap = drawView.getImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // create file using data and save
    private ParseFile getParseFile(byte[] data) throws ParseException {
        ParseFile file = new ParseFile("picture.jpg", data);
        file.save();
        return file;
    }

    private ParseObject getPictureObject(ParseFile file) throws ParseException {
        ParseObject pictureObject = new ParseObject("Picture");
        pictureObject.put("content", file);
        pictureObject.save();
        return pictureObject;
    }
}