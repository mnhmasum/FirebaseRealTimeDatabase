package com.masum.bird;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Belal on 2/26/2017.
 */
@IgnoreExtraProperties
public class Word {
    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWordEnglish() {
        return wordEnglish;
    }

    public void setWordEnglish(String wordEnglish) {
        this.wordEnglish = wordEnglish;
    }

    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }

    private String wordId;
    private String wordEnglish;
    private String wordMeaning;

    public void setTimeStamp(Map<String, String> time) {
   /*     HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);*/
        this.timeStampCreated = time;
    }

    public Object getTimeStampCreated(){
        return timeStampCreated;
    }

    public long getTitmeStamp(){
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }


    Map<String, String> timeStampCreated;
    Map<String, Long> timeStampCreated1;
    long timeStamp;


}
