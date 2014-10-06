package com.bwjfstudios.drawlaphone.activity.host;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Slider grouped with text representation of progress that only allows odd numbers to be selected
 */
public class OddSliderTextView extends SliderTextView {

  public OddSliderTextView(TextView textView, SeekBar seekBar) {
    super(textView, seekBar);
  }

  @Override // Makes it so the slider can only select odd numbers
  public void changeProgress(int progress) {
    int num = Math.round(progress * 2 + 3);
    textView.setText(String.valueOf(num));
  }

}
