package nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks;

import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;

import org.apache.commons.collections4.MultiMap;

import java.util.List;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public interface AllArtistCallback {
    void onSuccess(MultiMap<String, List<AudioItem>> artistMap);
}
