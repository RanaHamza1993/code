package nexaplayer.mkv.mpg.flv.wmv.listeners.item_select;

import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 1/3/2018.
 */

public interface ApkItemSelectListener {
    void onApkItemSelect(List<ApkItem> itemList, boolean allItemSelected) ;
}
