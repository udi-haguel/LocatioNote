package dev.haguel.moveotask.entities;

import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class NoteEntity implements Serializable {

    // User Input
    private String title;
    private String body;
    private String image;
    private long date;
    // System Data
    private long created;
    private long update;

    private double latitude;
    private double longitude;

    public NoteEntity() {
    }

    public NoteEntity(String title, String body, String image, long date, long created, long update, double latitude, double longitude) {
        this.title = title;
        this.body = body;
        this.image = image;
        this.date = date;
        this.created = created;
        this.update = update;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdate() {
        return update;
    }

    public void setUpdate(long update) {
        this.update = update;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage(){
        return image;
    }

}
