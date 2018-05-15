package com.masum.bird;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String WORD_NAME = "word_name";
    public static final String WORD_ID = "word_id";

    EditText editTextName;
    EditText editTextMeaning;
    Button buttonAddArtist;
    ListView listViewArtists;

    //a list to store all the artist from firebase database
    List<Word> words;

    //our database reference object
    DatabaseReference databaseArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting the reference of words node
        databaseArtists = FirebaseDatabase.getInstance().getReference("words");

        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextMeaning = (EditText) findViewById(R.id.editTextMeaning);
        listViewArtists = (ListView) findViewById(R.id.listViewArtists);

        buttonAddArtist = (Button) findViewById(R.id.buttonAddArtist);

        //list to store words
        words = new ArrayList<>();


        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();
            }
        });

        //attaching listener to listview
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Word artist = words.get(i);
                Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
                intent.putExtra(WORD_ID, artist.getWordId());
                intent.putExtra(WORD_NAME, artist.getWordEnglish());
                startActivity(intent);
            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Word artist = words.get(i);
                showUpdateDeleteDialog(artist.getWordId(), artist.getWordEnglish());
                return true;
            }
        });

        EditText editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Query query = databaseArtists.orderByChild("wordEnglish")
                        .startAt(s.toString())
                        .endAt(s + "\uf8ff");

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        words.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Word artist = postSnapshot.getValue(Word.class);
                            words.add(artist);

                        }

                        WordList artistAdapter = new WordList(MainActivity.this, words);
                        listViewArtists.setAdapter(artistAdapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

    }

    private void showUpdateDeleteDialog(final String artistId, String artistName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Spinner spinnerGenre = (Spinner) dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteArtist);

        dialogBuilder.setTitle(artistName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getSelectedItem().toString();
                if (!TextUtils.isEmpty(name)) {
                    updateArtist(artistId, name, genre);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
                b.dismiss();
            }
        });
    }

    private boolean updateArtist(String id, String name, String genre) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("words").child(id);

        //updating artist
        Word word = new Word();
        word.setWordId(id);
        word.setWordEnglish(name);
        word.setWordMeaning(genre);
        dR.setValue(word);
        Toast.makeText(getApplicationContext(), "Word has been Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteArtist(String id) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("words").child(id);

        //removing artist
        dR.removeValue();

        //getting the tracks reference for the specified artist
        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        //removing all tracks
        drTracks.removeValue();
        Toast.makeText(getApplicationContext(), "Artist Deleted", Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                words.clear();
                //clearing the previous artist list
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();



                if (td !=null) {
                    Log.d("allkeys", "onDataChange: " + td.values());
                }

                for (Object x : td.values()) {

                    if (x instanceof Map) {
                        Word word = new Word();
                        word.setWordId(((Map) x).get("wordId").toString());
                        word.setTimeStamp((long) ((Map) x).get("timeStampCreated"));
                        word.setWordEnglish(((Map) x).get("wordEnglish").toString());
                        word.setWordMeaning(((Map) x).get("wordMeaning").toString());
                        words.add(word);
                    }



                }

                for (int i = 0; i < td.size(); i++) {

                }






                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    /*Word word = postSnapshot.getValue(Word.class);

                    //Log.d("time", "onDataChange: " + postSnapshot.getChildren());
                    //adding artist to the list
                    words.add(word);*/
                }

                //creating adapter
                WordList artistAdapter = new WordList(MainActivity.this, words);
                //attaching adapter to the list view
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addArtist() {
        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String meaning = editTextMeaning.getText().toString();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            String id = databaseArtists.push().getKey();

            Word word = new Word();
            word.setWordEnglish(name.toLowerCase());
            word.setWordMeaning(meaning.toLowerCase());
            word.setTimeStamp(ServerValue.TIMESTAMP);
            word.setWordId(id);

            //Saving the Artist
            databaseArtists.child(id).setValue(word);

            editTextName.setText("");
            editTextMeaning.setText("");

            Toast.makeText(this, "New word is added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
