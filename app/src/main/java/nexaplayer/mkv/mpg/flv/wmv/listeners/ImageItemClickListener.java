package nexaplayer.mkv.mpg.flv.wmv.listeners;

import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 12/7/2017.
 */

public interface ImageItemClickListener {
    void onImageItemClicked(String[] list, int position);
    void onImageItemLongClicked(List<ImageItem> item, boolean isAllItemSelected);
}
