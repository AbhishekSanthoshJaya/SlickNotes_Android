package com.aby.note_quasars_android.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "notes", foreignKeys = @ForeignKey(entity = Folder.class,parentColumns = "id", childColumns = "parentFolder", onDelete = CASCADE))
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "note")
    private String note;


    @ColumnInfo(name = "created_on")
    private Date createdOn;


    private int parentFolder;


    private ArrayList<String> photos;
    private ArrayList<String> texts;
    private ArrayList<String> viewOrders;
    private ArrayList<String> sounds;
    private String latitude;
    private String longitude;

    public Note(){

    }

    public Note(int id,String title,String note, int parentFolder){
        this.id = id;
        this.title = title;
        this.note = note;
        this.createdOn = new Date();
        this.parentFolder = parentFolder;
        this.photos = new ArrayList<>();

    }

    public Note(String title,String note, int parentFolder){
        this.title = title;
        this.note = note;
        this.createdOn = new Date();
        this.parentFolder = parentFolder;
        this.photos = new ArrayList<>();
        this.texts = new ArrayList<>();
        this.viewOrders = new ArrayList<>();
        this.sounds = new ArrayList<>();


    }

    public Note(String title,String note){
        this.title = title;
        this.note = note;
        this.createdOn = new Date();
        this.parentFolder = 1;
        this.photos = new ArrayList<>();
        this.texts = new ArrayList<>();
        this.viewOrders = new ArrayList<>();
        this.sounds = new ArrayList<>();

    }

    public Note(String title,String note,int parentFolder,  ArrayList<String> photos, ArrayList<String> texts,
                ArrayList<String> viewOrders,ArrayList<String> sounds){
        this.title = title;
        this.note = note;
        this.createdOn = new Date();
        this.parentFolder = parentFolder;
        this.photos = photos;
        this.texts = texts;
        this.viewOrders = viewOrders;
        this.sounds = sounds;


    }

    public Note(String title,String note,int parentFolder,  ArrayList<String> photos, ArrayList<String> texts,
                ArrayList<String> viewOrders,ArrayList<String> sounds, String latitude, String longitude){
        this.title = title;
        this.note = note;
        this.createdOn = new Date();
        this.parentFolder = parentFolder;
        this.photos = photos;
        this.texts = texts;
        this.viewOrders = viewOrders;
        this.sounds = sounds;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public int getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(int parentFolder) {
        this.parentFolder = parentFolder;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public ArrayList<String> getViewOrders() {
        return viewOrders;
    }

    public ArrayList<String> getSounds() {
        return sounds;
    }

    public void setTexts(ArrayList<String> texts) {
        this.texts = texts;
    }

    public void setViewOrders(ArrayList<String> viewOrders) {
        this.viewOrders = viewOrders;
    }

    public void setSounds(ArrayList<String> sounds) {
        this.sounds = sounds;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
