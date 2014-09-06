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

/**
 * Screen in which the user draws a picture based on a given word and then sends it when done
 */
public class DrawActivity extends AGameActivity {

    private DrawView drawView; // Part of the view the user actually draws on
    private LinearLayout palette; // Color selection given to the user
    private Button currentColor; // Button displaying the current color
    private SeekBar sizeSelect; // Slider for selecting the width of stroke
    private TextView sizeText; // Text displaying current stroke width
    private Button undo; // Button for undoing previously drawn elements
    private TextView drawText; // Text of what to draw

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        // Assign fields
        this.drawView = (DrawView) this.findViewById(R.id.drawView);
        this.palette = (LinearLayout) this.findViewById(R.id.palette);
        this.currentColor = (Button) this.findViewById(R.id.currentColor);
        this.sizeSelect = (SeekBar) this.findViewById(R.id.sizeSelect);
        this.sizeText = (TextView) this.findViewById(R.id.sizeText);
        this.undo = (Button) this.findViewById(R.id.undo);
        this.drawText = (TextView) this.findViewById(R.id.drawText);

        // Initialize UI
        this.initUI();
    }

    // Sets up the screen for drawing
    private void initUI() {
        this.initPaletteColors(); // Color selection
        this.initCurrentColor(); // Color currently used
        this.initSizeSelect(); // Brush size currently used
        this.initSizeText(); // Display brush size number
        this.initUndo(); // Undo mistakes
        this.initDrawText(); // Word the user is drawing
    }

    // Set all of the colors enumerated in resources and create buttons with id's and backgrounds matching the colors
    private void initPaletteColors() {
        // Create a new Linear Layout
        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 200, 1f);

        // Iterate through predefined colors and create buttons for each color
        for (int col : this.getResources().getIntArray(R.array.rainbow)) {
            Button b = new Button(this);
            b.setId(col);
            b.setBackgroundColor(col);
            b.setText("");
            b.setLayoutParams(params);

            // Set what happens when a button is pressed
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // the ID of a button in the palette is the same as its color so just set the color to the id
                    drawView.setColor(v.getId());
                    currentColor.setBackgroundColor(v.getId());
                }
            });

            // Add the button to the color selection
            this.palette.addView(b);
        }
    }

    // Set the current color background to the default black to start
    private void initCurrentColor() {
        this.currentColor.setBackgroundColor(this.getResources().getColor(R.color.black));
    }

    // Set what happens when the size slider is changed
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

    // Set the TextView representing brush size
    private void initSizeText() {
        this.sizeText.setText(String.valueOf(this.sizeSelect.getProgress()));
    }

    // Set what happens when the Undo button is pressed
    private void initUndo() {
        this.undo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.undoPath();
            }
        });
    }

    // Gets the word from parse and displays it to player
    private void initDrawText() {
        this.drawText.setText(this.getWordFromParse());
    }

    // Gets the current word the User should draw
    private String getWordFromParse() {
        try {
            // Get the most recent word entered from Parse
            ParseObject currentGame = this.getCurrentGame();
            ParseRelation<ParseObject> wordRelation = currentGame.getRelation("words");
            ParseObject parseWord = wordRelation.getQuery().orderByDescending("createdAt").getFirst();
            if (parseWord != null) {
                return parseWord.getString("content");
            }
        }
        // If word is not available display default
        catch (ParseException e) {
            return getResources().getString(R.string.draw_instructions_success);
        }
        return getResources().getString(R.string.draw_instructions_fail);
    }

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

    @Override // Used in getting the number of pictures already entered in the game
    protected String getCountString() {
        return "pictureCount";
    }

    // Does the actual job of saving the data once sent and moving to the next player
    private void saveDataToCloud() throws ParseException {
        ParseObject currentGame = this.getCurrentGame();

        // Get bitmap data
        byte[] data = this.getBitmapData();

        // Create file using data and save
        ParseFile file = this.getParseFile(data);

        // Create a new picture object using the file and save
        ParseObject pictureObject = this.getPictureObject(file);

        // Create a relation between the picture and the game
        currentGame.getRelation("pictures").add(pictureObject);

        // Move to the next player
        this.moveToNextTurn(currentGame);
    }

    // Get bitmap data of picture
    private byte[] getBitmapData() {
        Bitmap bitmap = drawView.getImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // Create file using byte data and save
    private ParseFile getParseFile(byte[] data) throws ParseException {
        ParseFile file = new ParseFile("picture.jpg", data);
        file.save();
        return file;
    }

    // Creates a picture object in parse by using the newly made file
    private ParseObject getPictureObject(ParseFile file) throws ParseException {
        ParseObject pictureObject = new ParseObject("Picture");
        pictureObject.put("content", file);
        pictureObject.save();
        return pictureObject;
    }
}