package nexaplayer.mkv.mpg.flv.wmv.listeners.item_select;

/**
 * Created by Sultan Ahmed on 1/3/2018.
 */

public interface RenameListener<K> {
    void onItemRename(K item, String oldName);
}
