package com.adp.chabok.data.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class MessageTO implements Serializable{

    private long id;
    private String serverId;
    private String messageId;
    private String message;
    private Timestamp sentDate;
    private Timestamp receivedDate;
    private boolean read;
    private boolean header;
    private String mdata;
    private String senderId;
    private int seenCounter;
    private int send_status;  //0 just created - 1 sended

    public MessageTO() {
    }

    public MessageTO(String serverId,
                     String message,
                     Timestamp sentDate,
                     Timestamp receivedDate,
                     boolean read,
                     String mdata
            , String senderId
            , int send_status) {
        this.serverId = serverId;
        this.message = message;
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;
        this.read = read;
        this.mdata = mdata;
        this.senderId = senderId;
        this.send_status = send_status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getData() {
        return this.mdata;
    }

    public void setData(String mdata) {
        this.mdata = mdata;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String mdata) {
        this.senderId = mdata;
    }

    public int getSendStatus() {
        return this.send_status;
    }

    public void setSendStatus(int send_status) {
        this.send_status = send_status;
    }

    public int getSeenCounter() {
        return this.seenCounter;
    }

    public void setSeenCounter(int seenCounter) {
        this.seenCounter = seenCounter;
    }
}
