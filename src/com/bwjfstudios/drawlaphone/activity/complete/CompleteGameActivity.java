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

import java.util.List;

/**
 * Shows the user games they have participated in that are complete
 */
public class CompleteGameActivity extends AActivity {

  private LinearLayout layout; // Layout within a ScrollView that displays pictures and words used

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_complete_game);

    // Assign Views
    this.layout = (LinearLayout) this.findViewById(R.id.LinearLayout1);

    // Initialize Views
    this.initGameHistory();
  }

  // Populates layout with pictures and words from the game selected
  void initGameHistory() {
    // Load in background
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // Get the words and pictures from the game selected
          ParseObject currentGame = getCurrentGame();
          List<ParseObject>
              pictures =
              currentGame.getRelation("pictures").getQuery().orderByAscending("createdAt").find();
          List<ParseObject>
              words =
              currentGame.getRelation("words").getQuery().orderByAscending("createdAt").find();

          // Alternate displaying words and pictures from the lists
          for (int i = 0; i < largestSize(pictures, words); i++) {
            // Display word
            if (i < words.size()) {
              displayWord(words, i);
            }

            // Display associated picture
            if (i < pictures.size()) {
              displayImage(pictures, i);
            }
          }
        } catch (ParseException e) {
          makeText("Could not load game at this time");
        }
      }

      // Create a TextView and add it to the layout with a given word
      private void displayWord(List<ParseObject> words, int i) {
        // Get the word
        String parseWord = words.get(i).getString("content");
        // Create the TextView
        final TextView textView = new TextView(getApplicationContext());
        textView.setText(parseWord);
        textView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Large);
        textView.setTextColor(getResources().getColor(R.color.black));
        // Add the view on the UI thread
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            layout.addView(textView);
          }
        });
      }

      // Create a TextView and add it to the layout with a given picture
      private void displayImage(List<ParseObject> pictures, int i) {
        // Get the image
        ParseFile file = pictures.get(i).getParseFile("content");
        // Create the ImageView
        final ParseImageView imageView = new ParseImageView(getApplicationContext());
        imageView.setParseFile(file);
        imageView.loadInBackground();
        // Add the view on the UI thread
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            layout.addView(imageView);
          }
        });
      }

      // Return the largest size of the two lists
      private int largestSize(List<ParseObject> pictures, List<ParseObject> words) {
        return Math.max(pictures.size(), words.size());
      }
    }).start();
  }
}
