package nexaplayer.mkv.mpg.flv.wmv.listeners.item_select;

import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 1/3/2018.
 */

public interface VideoItemSelectListener {
    void onVideoItemSelect(List<VideoItem> itemList, boolean allItemSelected) ;
}
