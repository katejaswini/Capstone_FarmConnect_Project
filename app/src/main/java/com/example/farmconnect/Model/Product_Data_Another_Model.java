package com.example.farmconnect.Model;

import android.graphics.Bitmap;

import com.example.farmconnect.ExtraClasses.ConvertImage;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Product_Data_Another_Model {
    String name,paymentmethod,consumerid,productid,image,documentid,location,sellerid;
    boolean paid,delivered;
    Date dateofreturn,soldAt;
    int boughtprice, boughtquantity;

    public Product_Data_Another_Model() {
    }

    public Product_Data_Another_Model(String name, String paymentmethod, String consumerid, String productid, String image, boolean paid, Date dateofreturn, Date soldAt, int broughtprice, int boughtquantity,String sellerid) {
        this.name = name;
        this.paymentmethod = paymentmethod;
        this.consumerid = consumerid;
        this.productid = productid;
        this.image = image;
        this.paid = paid;
        this.dateofreturn = dateofreturn;
        this.soldAt = soldAt;
        this.boughtprice = broughtprice;
        this.boughtquantity = boughtquantity;
        this.sellerid=sellerid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public Bitmap getImage() {
        return ConvertImage.getBitmap(image);
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public String getConsumerid() {
        return consumerid;
    }

    public void setConsumerid(String consumerid) {
        this.consumerid = consumerid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Date getDateofreturn() {
        return dateofreturn;
    }

    public void setDateofreturn(Date dateofreturn) {
        this.dateofreturn = dateofreturn;
    }

    public Date getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(Date soldAt) {
        this.soldAt = soldAt;
    }

    public int getBoughtprice() {
        return boughtprice;
    }

    public void setBoughtprice(int boughtprice) {
        this.boughtprice = boughtprice;
    }

    public int getBoughtquantity() {
        return boughtquantity;
    }

    public void setBoughtquantity(int boughtquantity) {
        this.boughtquantity = boughtquantity;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public Product_Data_Another_Model(DocumentSnapshot documentSnapshot){
        this.boughtprice=documentSnapshot.getLong("boughtprice").intValue();
        this.boughtquantity=documentSnapshot.getLong("boughtquantity").intValue();
        this.dateofreturn=documentSnapshot.getDate("dateofreturn");
        this.image=documentSnapshot.getString("image");
        this.name=documentSnapshot.getString("name");
        this.productid=documentSnapshot.getString("productid");
        this.soldAt=documentSnapshot.getDate("soldAt");
        this.sellerid=documentSnapshot.getString("sellerid");
    }
    public Map<String,Object> toMap(){
        Map<String,Object> product=new HashMap<>();
        product.put("name",name);
        product.put("boughtprice",boughtprice);
        product.put("boughtquantity",boughtquantity);
        product.put("dateofreturn",dateofreturn);
        product.put("location",location);
        product.put("paid",paid);
        product.put("paymentmethod",paymentmethod);
        product.put("productid",productid);
        product.put("soldAt",new Timestamp(new Date()));
        product.put("image",image);
        product.put("sellerid",sellerid);
        return product;
    }
}
