package nexaplayer.mkv.mpg.flv.wmv.model;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sultan Ahmed on 12/4/2017.
 */

public class AudioItem extends RealmObject implements Serializable {

    @SerializedName("duration")
    private String duration;
    @SerializedName("path")
    @PrimaryKey
    protected String path;
    @SerializedName("name")
    protected String name;
    @SerializedName("isNew")
    protected boolean isNew;
    @SerializedName("extension")
    protected String extension;
    @SerializedName("size")
    protected String size;
    @SerializedName("date")
    protected Date date;
    @SerializedName("parentPath")
    protected String parentPath;
    @SerializedName("album")
    protected String album;
    @SerializedName("artist")
    protected String artist;
    @SerializedName("isFavorite")
    protected boolean isFavorite;
    @SerializedName("isExist")
    protected String isExist;
    @Ignore
    protected boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String isExist() {
        return isExist;
    }

    public void setExist(String exist) {
        isExist = exist;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public AudioItem(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCompletePath(){
        return path + File.pathSeparator + name;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public CharSequence getDateString() {
        DateFormat dateFormat = new DateFormat();
        return DateFormat.format("dd-MM-yyyy", date);
    }

    public Date getDate(){
        return this.date;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public AudioItem(RealmObject object){
        AudioItem model = (AudioItem) object;
        this.setDate(model.getDate());
        this.setDuration(model.getDuration());
        this.setName(model.getName());
        this.setExtension(model.getExtension());
        this.setNew(model.isNew());
        this.setPath(model.getPath());
        this.setParentPath(model.getParentPath());
        this.setSize(model.getSize());
        this.setFavorite(model.isFavorite());
        this.setAlbum(model.getAlbum());
        this.setArtist(model.getArtist());
    }

}
