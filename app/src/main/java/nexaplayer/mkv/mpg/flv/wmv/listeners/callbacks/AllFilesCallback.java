package nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public interface AllFilesCallback {
    void onSuccess(List<? extends RealmObject> list);
}
