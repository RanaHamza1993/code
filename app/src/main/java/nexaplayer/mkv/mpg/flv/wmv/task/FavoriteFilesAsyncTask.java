package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class FavoriteFilesAsyncTask extends AsyncTask<Object, Object, List<RealmObject>> {

    AllFilesCallback callback;
    Context context;
    File currentDirectory;
    String itemType;
    List<RealmObject> itemList;

    public FavoriteFilesAsyncTask(Context context, AllFilesCallback callback, String itemType){
        this.context = context;
        this.callback = callback;
        this.itemType = itemType;
        itemList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        itemList = RealmManager.getInstance().getFavoriteItemListFromRealm(ItemUtils.getClassByItemType(itemType));
    }

    @Override
    protected List<RealmObject> doInBackground(Object... params) {
        return itemList;
    }

    @Override
    protected void onPostExecute(List<RealmObject> fileItems) {
        super.onPostExecute(fileItems);
        callback.onSuccess(fileItems);
    }
}



