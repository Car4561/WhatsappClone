package com.carlos.whatsappclone.models;

import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {

    private String id;
    private long timestamp;
    private String writing;
    private ArrayList<String> contactsIds;
    private int numberMessages;

    public Chat() {
    }

    public Chat(String id, long timestamp, String writing, ArrayList<String> contactsIds, int numberMessages) {
        this.id = id;
        this.timestamp = timestamp;
        this.writing = writing;
        this.contactsIds = contactsIds;
        this.numberMessages = numberMessages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    public ArrayList<String> getContactsIds() {
        return contactsIds;
    }

    public void setContactsIds(ArrayList<String> contactsIds) {
        this.contactsIds = contactsIds;
    }

    public int getNumberMessages() {
        return numberMessages;
    }

    public void setNumberMessages(int numberMessages) {
        this.numberMessages = numberMessages;
    }
}
