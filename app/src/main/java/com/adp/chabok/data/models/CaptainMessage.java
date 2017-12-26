package com.adp.chabok.data.models;


import java.io.Serializable;
import java.sql.Timestamp;

public class CaptainMessage implements Serializable {

    private long id;
    private String message;
    private Timestamp sentDate;
    private Timestamp receivedDate;
    private String mData;
    private boolean read;

    public CaptainMessage() {
    }

    public CaptainMessage(long id, String message, Timestamp sentDate, Timestamp receivedDate, String mData, boolean read) {
        this.id = id;
        this.message = message;
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;
        this.mData = mData;
        this.read = read;
    }


    public CaptainMessage(String message, Timestamp sentDate, Timestamp receivedDate, String mData, boolean read) {
        this.message = message;
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;
        this.mData = mData;
        this.read = read;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getSentDate() {
        return sentDate;
    }

    public void setSentDate(Timestamp sentDate) {
        this.sentDate = sentDate;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getmData() {
        return mData;
    }

    public void setmData(String mData) {
        this.mData = mData;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
