package com.example.farmconnect.Model;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notification_Model {
    private String title,message,documentid;
    private Date timestamp;
    private Boolean seen;

    public Notification_Model() {
    }

    public Notification_Model(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public Notification_Model(String title, String message, Date timestamp, Boolean seen) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public Map<String,Object> getMapNotification(){
        Map<String, Object> notification=new HashMap<>();
        notification.put("title",title);
        notification.put("message",message);
        notification.put("timestamp",new Timestamp(new Date()));
        notification.put("seen",false);
        return notification;
    }
}
