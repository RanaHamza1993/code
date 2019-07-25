package nexaplayer.mkv.mpg.flv.wmv.listeners;

/**
 * Created by Sultan Ahmed on 12/26/2017.
 */

public interface TabActivityListener {
    boolean isStackEmpty();
    void selectAllItems(boolean value);
    boolean isItemSelected();
    void onDeleteClicked();
    void onDetailClicked();
    void onRenameClicked();
    void onShareClicked();
    void onFavoriteClicked();
}
