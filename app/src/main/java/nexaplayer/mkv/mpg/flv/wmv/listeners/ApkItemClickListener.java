package nexaplayer.mkv.mpg.flv.wmv.listeners;

import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 12/7/2017.
 */

public interface ApkItemClickListener {
    void onApkItemClicked(ApkItem item);
    void onApkItemLongClicked(List<ApkItem> item, boolean isAllItemSelected);
}
