package com.bwjfstudios.drawlaphone.activity.host;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Slider grouped with text representation of progress
 */
public class SliderTextView {

  protected TextView textView; // Text representation
  protected SeekBar seekBar; // Used to select range of numbers

  // Constructor
  public SliderTextView(TextView textView, SeekBar seekBar) {
    this.textView = textView;
    this.seekBar = seekBar;
  }

  // Returns a positive number from the TextView
  public int getSeekNumber() {
    String numString = this.textView.getText().toString();
    int num = Integer.parseInt(numString);
    return Math.abs(num);
  }

  // Sets up the slider
  public void init() {
    this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override // Makes the slider pretty by only allowing whole numbers with a min of 3
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        changeProgress(progress);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  // Makes it so the slider only can select whole numbers
  public void changeProgress(int progress) {
    int num = Math.round(progress + 3);
    textView.setText(String.valueOf(num));
  }
}
