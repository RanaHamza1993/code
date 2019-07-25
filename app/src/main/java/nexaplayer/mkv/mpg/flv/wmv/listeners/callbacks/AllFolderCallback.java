package nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks;

import nexaplayer.mkv.mpg.flv.wmv.model.FolderItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public interface AllFolderCallback {
    void onSuccess(List<FolderItem> list);
}
