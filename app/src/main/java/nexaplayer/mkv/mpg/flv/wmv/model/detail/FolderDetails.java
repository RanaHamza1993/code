package nexaplayer.mkv.mpg.flv.wmv.model.detail;

/**
 * Created by Sultan Ahmed on 12/6/2017.
 */

public class FolderDetails {

    public FolderDetails(){
        hasItemType = false;
        hasNew = false;
        childItemsCount = 0;
        childFolderCount = 0;
    }

    boolean hasItemType;
    boolean hasNew;
    int childItemsCount;
    int childFolderCount;

    public boolean isHasItemType() {
        return hasItemType;
    }

    public void setHasItemType(boolean hasItemType) {
        this.hasItemType = hasItemType;
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }

    public int getChildItemsCount() {
        return childItemsCount;
    }

    public void setChildItemsCount(int childItemsCount) {
        this.childItemsCount = childItemsCount;
    }

    public int getChildFolderCount() {
        return childFolderCount;
    }

    public void setChildFolderCount(int childFolderCount) {
        this.childFolderCount = childFolderCount;
    }

    public void updateChildFolderCount(){
        childFolderCount++;
    }
}
