package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.utils.FolderUtils;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class SearchAllFilesAsyncTask extends AsyncTask<Object, Object, List<RealmObject>> {

    AllFilesCallback callback;
    Context context;
    File currentDirectory;
    String itemType;
    String searchText;
    List<RealmObject> itemList;

    public SearchAllFilesAsyncTask(Context context, AllFilesCallback callback,String itemType, String searchText){
        this.context = context;
        this.callback = callback;
        this.itemType = itemType;
        this.searchText = searchText;
        itemList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        itemList = RealmManager.getInstance().getItemListFromRealm(ItemUtils.getClassByItemType(itemType), SharedPreferenceManager.getInstance(context));
    }

    @Override
    protected List<RealmObject> doInBackground(Object... params) {
        itemList = FolderUtils.searchAllFilesWithText(itemType, itemList, searchText);
        return itemList;
    }

    @Override
    protected void onPostExecute(List<RealmObject> fileItems) {
        super.onPostExecute(fileItems);
        callback.onSuccess(fileItems);
    }
}



