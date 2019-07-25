package nexaplayer.mkv.mpg.flv.wmv.model;

/**
 * Created by Sultan Ahmed on 12/4/2017.
 */

public class FolderItem extends Item{

    public FolderItem(){}

    public FolderItem(int id, String name, String itemCount) {
        this.id = id;
        this.name = name;
        this.itemCount = itemCount;
    }

    private int id;
    private String itemCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }
}
