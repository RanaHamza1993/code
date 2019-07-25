package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFolderCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.model.FolderItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.FolderUtils;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;

import org.apache.commons.collections4.MultiMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class FindAllFoldersAsyncTask extends AsyncTask<Object, Object, List<FolderItem>> {

    AllFolderCallback callback;
    Context context;
    String itemType;
    List<FolderItem> folderList;
    List<RealmObject> itemList;

    public FindAllFoldersAsyncTask(Context context,AllFolderCallback callback, String itemType){
        this.context = context;
        this.callback = callback;
        this.itemType = itemType;
        this.folderList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        itemList = RealmManager.getInstance().getItemListFromRealm(ItemUtils.getClassByItemType(itemType), SharedPreferenceManager.getInstance(context));
    }

    @Override
    protected List<FolderItem> doInBackground(Object... params) {
        MultiMap<String, List<RealmObject>> nameList = FolderUtils.getFolderList(itemList, itemType);
        for(String folderName : nameList.keySet()){
            List<RealmObject> objectList = (List<RealmObject>) nameList.get(folderName);
            folderList.add(FolderUtils.getFolderItem(new File(folderName), false, objectList.size()));
        }

        return folderList;
    }

    @Override
    protected void onPostExecute(List<FolderItem> folderItems) {
        super.onPostExecute(folderItems);
        callback.onSuccess(folderItems);
    }
}



