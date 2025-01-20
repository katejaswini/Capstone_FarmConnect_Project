package com.example.farmconnect.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.farmconnect.ExtraClasses.ConvertImage;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product_Data_Model implements Parcelable {
    String name,image,location,buyorrent,userid,deliverymode,measure,duration,productid,category,userlocation;
    double rating;
    int amount,stock,time;
    Date date;
    ArrayList<String> feature;
    Context context;

    public Product_Data_Model() {}

    public Product_Data_Model(String name, String image, String location, String buyorrent, String userid, String deliverymode, String measure, String duration, String productid, String category, double rating, int amount, int stock, int time, Date date, ArrayList<String> feature) {
        this.name = name;
        this.image = image;
        this.location = location;
        this.buyorrent = buyorrent;
        this.userid = userid;
        this.deliverymode = deliverymode;
        this.measure = measure;
        this.duration = duration;
        this.productid = productid;
        this.category = category;
        this.rating = rating;
        this.amount = amount;
        this.stock = stock;
        this.time = time;
        this.date = date;
        this.feature = feature;
    }

    protected Product_Data_Model(Parcel in) {
        name = in.readString();
        image = in.readString();
        location = in.readString();
        buyorrent = in.readString();
        userid = in.readString();
        deliverymode = in.readString();
        measure = in.readString();
        duration = in.readString();
        productid = in.readString();
        rating = in.readDouble();
        amount = in.readInt();
        stock = in.readInt();
        time = in.readInt();
        feature = in.createStringArrayList();
        category=in.readString();
    }

    public static final Creator<Product_Data_Model> CREATOR = new Creator<Product_Data_Model>() {
        @Override
        public Product_Data_Model createFromParcel(Parcel in) {
            return new Product_Data_Model(in);
        }

        @Override
        public Product_Data_Model[] newArray(int size) {
            return new Product_Data_Model[size];
        }
    };

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public ArrayList<String> getFeature() {
        return feature;
    }

    public void setFeature(ArrayList<String> feature) {
        this.feature = feature;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Bitmap getImage() {
        return ConvertImage.getBitmap(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBuyorrent() {
        return buyorrent;
    }

    public void setBuyorrent(String buyorrent) {
        this.buyorrent = buyorrent;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Float getUserlocation() {
        float distanceInMeters = 0;
        try {
            Location location1 = getLocationFromAddress(context, userlocation);
            Location location2 = getLocationFromAddress(context, location);

            if (location1 != null && location2 != null) {
                distanceInMeters = location1.distanceTo(location2);
                Log.d("Distance", "Distance: " + distanceInMeters + " meters");
            } else {
                Log.d("Distance", "One or both addresses could not be geocoded.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return distanceInMeters;
    }

    public void setUserlocation(String userlocation) {
        this.userlocation = userlocation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(location);
        dest.writeString(buyorrent);
        dest.writeString(userid);
        dest.writeString(deliverymode);
        dest.writeString(measure);
        dest.writeString(duration);
        dest.writeString(productid);
        dest.writeDouble(rating);
        dest.writeInt(amount);
        dest.writeInt(stock);
        dest.writeInt(time);
        dest.writeStringList(feature);
        dest.writeString(category);
    }
    public static Location getLocationFromAddress(Context context, String address) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = geocoder.getFromLocationName(address, 1);

        if (addresses != null && !addresses.isEmpty()) {
            Address location = addresses.get(0);
            Location loc = new Location("geocoder");
            loc.setLatitude(location.getLatitude());
            loc.setLongitude(location.getLongitude());
            return loc;
        } else {
            return null;
        }
    }
}
