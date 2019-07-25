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

public class DocumentItem extends RealmObject implements Serializable {

    @SerializedName("path")
    @PrimaryKey
    protected String path;
    @SerializedName("name")
    protected String name;
    @SerializedName("extension")
    protected String extension;
    @SerializedName("size")
    protected String size;
    @SerializedName("date")
    protected Date date;
    @SerializedName("parentPath")
    protected String parentPath;
    @SerializedName("isFavorite")
    protected boolean isFavorite;
    @Ignore
    protected boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public DocumentItem(){}

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

    public DocumentItem(RealmObject object){
        DocumentItem model = (DocumentItem) object;
        this.setDate(model.getDate());
        this.setName(model.getName());
        this.setExtension(model.getExtension());
        this.setPath(model.getPath());
        this.setParentPath(model.getParentPath());
        this.setSize(model.getSize());
        this.setFavorite(model.isFavorite());
    }

}
