package nexaplayer.mkv.mpg.flv.wmv.listeners;

import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;

import java.util.List;

/**
 * Created by Sultan Ahmed on 12/7/2017.
 */

public interface DocumentItemClickListener {
    void onDocumentItemClicked(DocumentItem item);
    void onDocumentItemLongClicked(List<DocumentItem> item, boolean isAllItemSelected);
}
