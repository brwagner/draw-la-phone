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

// Custom, tricked out GameArrayAdapter to deal with our lists of tricked out, totally awesome games
public class GameArrayAdapter extends ArrayAdapter<ParseObject> {

    public GameArrayAdapter(Context context, ArrayList<ParseObject> games) {
        super(context, R.layout.game_item, games);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);
        }
        ParseObject game = getItem(position);

        TextView nameView = (TextView) convertView.findViewById(R.id.game_item_name);
        nameView.setText(game.getString("name"));

        TextView capacityView = (TextView) convertView.findViewById(R.id.game_item_data);
        capacityView.setText(String.valueOf(game.getInt("currentNumPlayers")) + "/" + String.valueOf(game.getInt("maxPlayers") + " players | " + game.getString("currentPlayerName") + "'s move"));

        return convertView;
    }
}
