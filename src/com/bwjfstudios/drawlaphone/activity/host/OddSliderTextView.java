package com.bwjfstudios.drawlaphone.activity.host;

import android.widget.SeekBar;
import android.widget.TextView;

public class OddSliderTextView extends SliderTextView {
    public OddSliderTextView(TextView textView, SeekBar seekBar) {
        super(textView, seekBar);
    }

    @Override
    public void changeProgress(int progress) {
        int num = Math.round(progress*2 + 3);
        textView.setText(String.valueOf(num));
    }

}
