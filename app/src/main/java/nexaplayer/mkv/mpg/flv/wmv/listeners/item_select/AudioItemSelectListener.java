package nexaplayer.mkv.mpg.flv.wmv.listeners.item_select;

import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 1/3/2018.
 */

public interface AudioItemSelectListener {
    void onAudioItemSelect(List<AudioItem> itemList, boolean allItemSelected) ;
}
