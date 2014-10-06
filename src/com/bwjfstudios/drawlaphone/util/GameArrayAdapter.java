package com.bwjfstudios.drawlaphone.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bwjfstudios.drawlaphone.R;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Used when displaying ParseObjects representing games in a ListView
 */
public class GameArrayAdapter extends ArrayAdapter<ParseObject> {

  public GameArrayAdapter(Context context, ArrayList<ParseObject> games) {
    super(context, R.layout.game_item, games);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // Get the game
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);
    }
    ParseObject game = getItem(position);

    // Display the name
    TextView nameView = (TextView) convertView.findViewById(R.id.game_item_name);
    nameView.setText(game.getString("name"));

    // Display the number of players and the player who is up
    TextView capacityView = (TextView) convertView.findViewById(R.id.game_item_data);
    capacityView.setText(String.valueOf(game.getInt("currentNumPlayers")) + "/" + String.valueOf(
        game.getInt("maxPlayers") + " players | " + game.getString("currentPlayerName")
        + "'s move"));

    return convertView;
  }
}
