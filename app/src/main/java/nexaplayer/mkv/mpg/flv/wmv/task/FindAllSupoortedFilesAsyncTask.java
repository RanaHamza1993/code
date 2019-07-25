package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.SuccessCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.FolderUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class FindAllSupoortedFilesAsyncTask extends AsyncTask<Object, Object, List<RealmObject>> {

    SuccessCallback callback;
    Context context;
    File currentDirectory;
    String itemType;
    List<RealmObject> itemList;
    RealmManager realmManager;

    Map<String, AudioItem> oldAudioList;
    Map<String, VideoItem> oldVideoList;
    Map<String, ImageItem> oldImageList;
    Map<String, DocumentItem> oldDocumentList;
    Map<String, ApkItem> oldApkList;

    public FindAllSupoortedFilesAsyncTask(Context context, SuccessCallback callback){
        this.context = context;
        this.callback = callback;
        this.currentDirectory = AppConstants.BASE_DIRECTORY;
        itemList = new ArrayList<>();
        realmManager = RealmManager.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        oldAudioList = realmManager.getAudioItemListFromRealm();
        oldVideoList = realmManager.getVideoItemListFromRealm();
        oldImageList = realmManager.getImageItemListFromRealm();
        oldDocumentList = realmManager.getDocumentItemListFromRealm();
        oldApkList = realmManager.getApkItemListFromRealm();
    }
    @Override
    protected List<RealmObject> doInBackground(Object... params) {

        itemList = FolderUtils.getAllFilesInList(context,currentDirectory, itemList);
        Log.v("wasim","current dir:"+currentDirectory+" has no.of items="+itemList.size());
        if(AppConstants.SECONDARY_STORAGE.exists()){
            Log.v("wasim","2nd storage fetching...");
            List<RealmObject> tempList = FolderUtils.getAllFilesInList(context,AppConstants.SECONDARY_STORAGE, itemList);
            itemList.addAll((List)tempList);
        }
        Log.i(FindAllSupoortedFilesAsyncTask.class.getName(), "All files loaded successfully");
        //updateItemList();
        updateNewItemList();
        Log.i(FindAllSupoortedFilesAsyncTask.class.getName(), "All files updated successfully");
        return itemList;
    }

    @Override
    protected void onPostExecute(List<RealmObject> list) {
        super.onPostExecute(list);
        realmManager.deleteAll();
        realmManager.bulkInsertList(list);
        callback.onSuccess();
    }

    private void updateItemList(){
        updateAudioItemList();
        updateVideoItemList();
        updateImageItemList();
        updateDocumentItemList();
        updateApkItemList();
    }

    private void updateNewItemList(){
        final ExecutorService executor = Executors.newCachedThreadPool();
        final List<Future<?>> futures = new ArrayList<>();
        for(final RealmObject item: itemList) {
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    updateListItem(item);
                }
            });
            futures.add(future);
        }
        try {
            for (Future<?> future : futures) {future.get();}
        } catch (Exception e) {
            Log.e("Find All files", "file error");
            e.printStackTrace();
        }

    }

    private void updateAudioItemList(){
        for(RealmObject item: itemList){
            if(item instanceof AudioItem){
                AudioItem audioItem = (AudioItem) item;
                try {
                    AudioItem oldItem = oldAudioList.get(audioItem.getPath());
                    if(oldItem != null){
                        audioItem.setFavorite(oldItem.isFavorite());
                        break;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateVideoItemList(){
        for(RealmObject item: itemList){
            if(item instanceof VideoItem){
                VideoItem videoItem = (VideoItem) item;
                try{
                    VideoItem oldItem = oldVideoList.get(videoItem.getPath());
                    if(oldItem != null){
                    videoItem.setFavorite(oldItem.isFavorite());
                    videoItem.setPlayBackDuration(oldItem.getPlayBackDuration());
                        break;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateImageItemList(){
        for(RealmObject item: itemList){
            if(item instanceof ImageItem){
                ImageItem imageItem = (ImageItem) item;
                try{
                    ImageItem oldItem = oldImageList.get(imageItem.getPath());
                    if(oldItem != null){
                        imageItem.setFavorite(oldItem.isFavorite());
                        break;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateDocumentItemList(){
        for(RealmObject item: itemList){
            if(item instanceof DocumentItem){
                DocumentItem documentItem = (DocumentItem) item;
                try{
                    DocumentItem oldItem = oldDocumentList.get(documentItem.getPath());
                    if(oldItem != null){
                        documentItem.setFavorite(oldItem.isFavorite());
                        break;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateApkItemList(){
        for(RealmObject item: itemList){
            if(item instanceof ApkItem){
                ApkItem apkItem = (ApkItem) item;
                try{
                    ApkItem oldItem = oldApkList.get(apkItem.getPath());
                    if(oldItem != null){
                        apkItem.setFavorite(oldItem.isFavorite());
                        break;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateListItem(RealmObject item){
        if(item instanceof AudioItem){
                AudioItem audioItem = (AudioItem) item;
                try {
                    AudioItem oldItem = oldAudioList.get(audioItem.getPath());
                    if(oldItem != null){
                        audioItem.setFavorite(oldItem.isFavorite());
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        else if(item instanceof VideoItem){
                VideoItem videoItem = (VideoItem) item;
                try {
                    VideoItem oldItem = oldVideoList.get(videoItem.getPath());
                    if(oldItem != null){
                        videoItem.setFavorite(oldItem.isFavorite());
                        videoItem.setPlayBackDuration(oldItem.getPlayBackDuration());
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        else if(item instanceof ImageItem){
                ImageItem imageItem = (ImageItem) item;
                try {
                    ImageItem oldItem = oldImageList.get(imageItem.getPath());
                    if(oldItem != null){
                        imageItem.setFavorite(oldItem.isFavorite());
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        else if(item instanceof DocumentItem){
                DocumentItem documentItem = (DocumentItem) item;
                try {
                    DocumentItem oldItem = oldDocumentList.get(documentItem.getPath());
                    if(oldItem != null){
                        documentItem.setFavorite(oldItem.isFavorite());
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        else if(item instanceof ApkItem){
                ApkItem apkItem = (ApkItem) item;
                try {
                    ApkItem oldItem = oldApkList.get(apkItem.getPath());
                    if(oldItem != null){
                        apkItem.setFavorite(oldItem.isFavorite());
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

    }
}



