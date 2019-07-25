package nexaplayer.mkv.mpg.flv.wmv.listeners;

import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 12/7/2017.
 */

public interface VideoItemClickListener {
    void onVideoItemClicked(String[] list, int position);
    void onVideoItemLongClicked(List<VideoItem> item, boolean isAllItemSelected);
}
