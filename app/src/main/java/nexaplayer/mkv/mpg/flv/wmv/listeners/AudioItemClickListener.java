package nexaplayer.mkv.mpg.flv.wmv.listeners;

import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 12/7/2017.
 */

public interface AudioItemClickListener {
    void onAudioItemClicked(String[] list, int position);
    void onAudioItemLongClicked(List<AudioItem> item, boolean isAllItemSelected);
}
