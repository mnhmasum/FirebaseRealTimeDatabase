package com.masum.bird;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 2/26/2017.
 */

public class WordList extends ArrayAdapter<Word> {
    private Activity context;
    List<Word> artists;

    public WordList(Activity context, List<Word> artists) {
        super(context, R.layout.layout_artist_list, artists);
        this.context = context;
        this.artists = artists;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = (TextView) listViewItem.findViewById(R.id.textViewGenre);

        Word artist = artists.get(position);
        textViewName.setText(artist.getWordEnglish().toUpperCase() + "--" + artist.getTitmeStamp());
        textViewGenre.setText(artist.getWordMeaning());

        return listViewItem;
    }
}