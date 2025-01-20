package com.example.farmconnect.Model;

import android.graphics.Bitmap;

import com.example.farmconnect.ExtraClasses.ConvertImage;
import java.util.Date;

public class Comment_Model {
    String name,image,content,userid,documentid;
    Date timestamp;
    public Comment_Model() {}

    public Comment_Model(String name, String image, String content, String userid) {
        this.name = name;
        this.image = image;
        this.content = content;
        this.userid = userid;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Bitmap getImage() {
        return ConvertImage.getBitmap(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

}
