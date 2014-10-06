package com.bwjfstudios.drawlaphone.activity.main;

import android.widget.AdapterView;
import android.widget.Button;

import com.bwjfstudios.drawlaphone.util.GameArrayAdapter;

/**
 * Structure representing a button paired with a game adapter and a listener
 */
class LAButton {

  private AdapterView.OnItemClickListener listener;
  private GameArrayAdapter adapter;
  private Button button;

  public LAButton(AdapterView.OnItemClickListener listener, GameArrayAdapter adapter,
                  Button button) {
    this.setListener(listener);
    this.setAdapter(adapter);
    this.setButton(button);
  }

  public AdapterView.OnItemClickListener getListener() {
    return listener;
  }

  void setListener(AdapterView.OnItemClickListener listener) {
    this.listener = listener;
  }

  public GameArrayAdapter getAdapter() {
    return adapter;
  }

  void setAdapter(GameArrayAdapter adapter) {
    this.adapter = adapter;
  }

  public Button getButton() {
    return button;
  }

  void setButton(Button button) {
    this.button = button;
  }
}
