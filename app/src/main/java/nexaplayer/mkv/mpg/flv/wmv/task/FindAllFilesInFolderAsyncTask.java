package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesInFolderCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class FindAllFilesInFolderAsyncTask extends AsyncTask<Object, Object, List<RealmObject> >{

    AllFilesInFolderCallback callback;
    Context context;
    String currentDirectory;
    String itemType;
    List<RealmObject> itemList;

    public FindAllFilesInFolderAsyncTask(Context context, AllFilesInFolderCallback callback, String currentDirectory, String itemType){
        this.context = context;
        this.callback = callback;
        this.currentDirectory = currentDirectory;
        this.itemType = itemType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        initializeList();
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

    private void initializeList(){
        itemList = RealmManager.getInstance().getItemWithIdFromRealm("parentPath", currentDirectory,ItemUtils.getClassByItemType(itemType));
    }
}



