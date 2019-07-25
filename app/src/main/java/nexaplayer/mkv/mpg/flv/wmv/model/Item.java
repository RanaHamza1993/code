package nexaplayer.mkv.mpg.flv.wmv.model;

import android.text.format.DateFormat;

import java.io.File;
import java.util.Date;

/**
 * Created by Sultan Ahmed on 12/8/2017.
 */

public class Item {

    protected String name;
    protected String path;
    protected boolean isNew;
    protected String extension;
    protected String size;
    protected Date date;

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

    public CharSequence getDate() {
        DateFormat dateFormat = new DateFormat();
        return DateFormat.format("dd-MM-yyyy", date);
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
