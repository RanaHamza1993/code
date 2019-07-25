package nexaplayer.mkv.mpg.flv.wmv.model;

/**
 * Created by Sultan Ahmed on 12/27/2017.
 */

public class ArtistItem {
    private String name;
    private int itemCount;

    public ArtistItem(String name, int itemCount) {
        this.name = name;
        this.itemCount = itemCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
