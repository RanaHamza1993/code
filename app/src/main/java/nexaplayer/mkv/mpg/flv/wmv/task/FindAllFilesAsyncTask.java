package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class FindAllFilesAsyncTask extends AsyncTask<Object, Object, List<RealmObject>> {

    AllFilesCallback callback;
    Context context;
    String itemType;
    List<RealmObject> itemList;

    public FindAllFilesAsyncTask(Context context, AllFilesCallback callback, String itemType){
        this.context = context;
        this.callback = callback;
        this.itemType = itemType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            itemList = RealmManager.getInstance().getItemListFromRealm(ItemUtils.getClassByItemType(itemType), SharedPreferenceManager.getInstance(context));
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



