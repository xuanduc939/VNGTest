package basic.com.vngtest;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import java.net.URL;
import java.util.ArrayList;

public class RecentPlace {
    private String id;
    private double latitude;
    private double longitude;
    private String name;
    private String phone;
    private String address;
    private Uri website;
    private Bitmap photos;

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getDistance(Location currentLocation){
        Location loc = new Location(name);
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return loc.distanceTo(currentLocation);
    }

    public String getWebsite() {
        if(website!= null)
            return website.toString();
        else
            return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getPhotos() {
        return photos;
    }

    public void setPhotos(Bitmap photos) {
        this.photos = photos;
    }

    public void setWebsite(Uri website) {
        this.website = website;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}