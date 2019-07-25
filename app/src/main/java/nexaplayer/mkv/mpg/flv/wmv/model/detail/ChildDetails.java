package nexaplayer.mkv.mpg.flv.wmv.model.detail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sultan Ahmed on 12/6/2017.
 */

public class ChildDetails {

    private List<File> childItemsCount;
    private boolean hasNew;
    private List<File> childDirectories;

    public ChildDetails(){
        hasNew = false;
        childItemsCount = new ArrayList<>();
        childDirectories = new ArrayList<>();
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }

    public List<File> getChildItemsCount() {
        return childItemsCount;
    }

    public void setChildItemsCount(List<File> childItemsCount) {
        this.childItemsCount = childItemsCount;
    }

    public List<File> getChildDirectories() {
        return childDirectories;
    }

    public void setChildDirectories(List<File> childDirectories) {
        this.childDirectories = childDirectories;
    }

    public void addChildItem(File file){
        childItemsCount.add(file);
    }

    public void addDirectoryItem(File name){
        childDirectories.add(name);
    }
}
