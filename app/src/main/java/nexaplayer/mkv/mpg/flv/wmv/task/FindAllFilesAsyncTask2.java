package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
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

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class FindAllFilesAsyncTask2 extends AsyncTask<Object, Object, List<RealmObject>> {

    File currentDirectory;
    RealmManager realmManager;
    AllFilesCallback callback;
    Context context;
    String itemType;
    List<RealmObject> itemList;
    Map<String, RealmObject> oldItemList;
    Map<String, AudioItem> oldAudioList;
    Map<String, VideoItem> oldVideoList;
    Map<String, ImageItem> oldImageList;
    Map<String, DocumentItem> oldDocumentList;
    Map<String, ApkItem> oldApkList;

    public FindAllFilesAsyncTask2(Context context, AllFilesCallback callback, String itemType){
        this.context = context;
        this.callback = callback;
        this.itemType = itemType;
        this.currentDirectory = AppConstants.BASE_DIRECTORY;
        itemList = new ArrayList<>();
        realmManager = RealmManager.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        switch (itemType) {
            case AppConstants.ITEM_TYPE_AUDIO:
                oldAudioList =realmManager.getAudioItemListFromRealm();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                oldVideoList = realmManager.getVideoItemListFromRealm();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                oldImageList = realmManager.getImageItemListFromRealm();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                oldDocumentList = realmManager.getDocumentItemListFromRealm();
                break;
            case AppConstants.ITEM_TYPE_APK:
                oldApkList = realmManager.getApkItemListFromRealm();
                break;

        }

     }

    @Override
    protected List<RealmObject> doInBackground(Object... params) {
        itemList = FolderUtils.getAllFilesInList(context,currentDirectory, itemList,itemType);
        if(AppConstants.SECONDARY_STORAGE.exists()){
            List<RealmObject> tempList = FolderUtils.getAllFilesInList(context,AppConstants.SECONDARY_STORAGE, itemList,itemType);
            itemList.addAll((List)tempList);
        }
        Log.i(FindAllSupoortedFilesAsyncTask.class.getName(), "All files loaded successfully");
        switch (itemType) {
            case AppConstants.ITEM_TYPE_AUDIO:
                updateAudioItemList();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                updateVideoItemList();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                updateImageItemList();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                updateDocumentItemList();
                break;
            case AppConstants.ITEM_TYPE_APK:
                updateApkItemList();
                break;

        }
        Log.i(FindAllSupoortedFilesAsyncTask.class.getName(), "All files updated successfully");
        return itemList;
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
    @Override
    protected void onPostExecute(List<RealmObject> fileItems) {
        super.onPostExecute(fileItems);
        switch (itemType) {
            case AppConstants.ITEM_TYPE_AUDIO:
                realmManager.bulkDelete(AudioItem.class);
                realmManager.bulkInsertList(itemList);
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                realmManager.bulkDelete(VideoItem.class);
                realmManager.bulkInsertList(itemList);
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                realmManager.bulkDelete(ImageItem.class);
                realmManager.bulkInsertList(itemList);
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                realmManager.bulkDelete(DocumentItem.class);
                realmManager.bulkInsertList(itemList);
                break;
            case AppConstants.ITEM_TYPE_APK:
                realmManager.bulkDelete(ApkItem.class);
                realmManager.bulkInsertList(itemList);
                break;

        }
        callback.onSuccess(fileItems);
    }
}



