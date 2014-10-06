package com.bwjfstudios.drawlaphone.activity.main;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.Button;

import com.bwjfstudios.drawlaphone.R;
import com.bwjfstudios.drawlaphone.util.GameArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple radio button selector used in the main activity to alternate between different lists of
 * games LA stands for Listener Adapter
 */
class LAButtonRadioGroup {

  private List<LAButton> LAButtons;
  private int selected;

  public LAButtonRadioGroup(List<LAButton> LAButtons) {
    this.LAButtons = LAButtons;
    this.selected = 0;
  }

  void select(int i) {
    Context context = this.LAButtons.get(i).getButton().getContext();

    for (LAButton lab : this.LAButtons) {
      lab.getButton().setBackgroundColor(context.getResources().getColor(R.color.white));
    }
    this.LAButtons.get(i).getButton()
        .setBackgroundColor(context.getResources().getColor(R.color.paleblue));

    this.selected = i;
  }

  public void select(Button button) {
    this.select(getButtons().indexOf(button));
  }

  List<Button> getButtons() {
    List<Button> dest = new ArrayList<Button>();
    for (LAButton laButton : LAButtons) {
      dest.add(laButton.getButton());
    }
    return dest;
  }

  public List<GameArrayAdapter> getAdapters() {
    List<GameArrayAdapter> dest = new ArrayList<GameArrayAdapter>();
    for (LAButton laButton : LAButtons) {
      dest.add(laButton.getAdapter());
    }
    return dest;
  }

  public List<AdapterView.OnItemClickListener> getListeners() {
    List<AdapterView.OnItemClickListener> dest = new ArrayList<AdapterView.OnItemClickListener>();
    for (LAButton laButton : LAButtons) {
      dest.add(laButton.getListener());
    }
    return dest;
  }

  public int getSelectedInt() {
    return selected;
  }

  public List<LAButton> getLAButtons() {
    return this.LAButtons;
  }
}
