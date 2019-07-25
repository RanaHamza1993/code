package nexaplayer.mkv.mpg.flv.wmv.manager;

/**
 * Created by Sultan Ahmed on 5/28/2017.
 */

import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmManager {

    private static RealmManager instance;
    private final Realm realm;

    public RealmManager() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmManager getInstance() {

        if (instance == null)
            instance = new RealmManager();
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public void bulkInsert(final RealmObject object){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
            }
        });
    }

    public void bulkInsertList(final List<RealmObject> object){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
            }
        });
    }

    public void bulkDelete(final Class realmClass){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<?> rows = realm.where(realmClass).findAll();
                rows.deleteAllFromRealm();
            }
        });
    }

    public void deleteAll(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    public RealmModel getObject(Class realmClass){
        return realm.where(realmClass).findFirst();
    }

    public RealmModel fetchObject(String field , int id, Class realmClass){
        return realm.where(realmClass).equalTo(field, id).findFirst();
    }

    public RealmModel fetchObject(String field , String id, Class realmClass){
        return realm.where(realmClass).equalTo(field, id).findFirst();
    }

    public RealmResults fetchObjectListWithId(String field , String id, Class realmClass){
        return realm.where(realmClass).equalTo(field, id).findAll();
    }

    public RealmResults fetchObjectListWithBooleanId(String field , Boolean id, Class realmClass){
        return realm.where(realmClass).equalTo(field, id).findAll();
    }

    public int getDataSize(Class realmClass){


        return  realm.where(realmClass).findAll().size();
    }

    public RealmResults getAllObjects(Class realmClass){ return  realm.where(realmClass).findAll();
    }

    public void deleteObject(final String field, final String id, final Class realmClass){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<?> row = realm.where(realmClass).equalTo(field,id).findAll();
                row.deleteAllFromRealm();
            }
        });
    }

    public void deleteAllSelectedObject(final String field, List<String> itemName, final Class realmClass){

        for(final String path : itemName) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<?> row = realm.where(realmClass).equalTo(field, path).findAll();
                    row.deleteAllFromRealm();
                }
            });
        }
    }

    public void renameObject(String oldPath, RealmObject newObject, final Class realmClass){
        deleteObject("path", oldPath, realmClass);
        bulkInsert(newObject);
    }

    public void deleteObject(final String field, final String id){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<?> row = realm.where(RealmObject.class).equalTo(field,id).findAll();
                row.deleteAllFromRealm();
            }
        });
    }

    public void beginTransaction(){
        realm.beginTransaction();
    }
    public void commitTransaction(){
        realm.commitTransaction();
    }

    public List<RealmObject> getItemListFromRealm(Class classname, SharedPreferenceManager sharedPreferenceManager){
        List<RealmObject> tempList = new ArrayList<>();
        List<RealmObject> results = getAllObjects(classname);
        for(RealmObject object : results){
            if(object instanceof AudioItem){
                AudioItem audioItem = new AudioItem(object);
                if(sharedPreferenceManager.getDisplayAudioFileHidden() || !ItemUtils.isHiddenFolder(audioItem.getParentPath())){
                    tempList.add(audioItem);
                }
            }
            else if(object instanceof VideoItem){
                VideoItem videoItem = new VideoItem(object);
                if(sharedPreferenceManager.getDisplayVideoFileHidden() || !ItemUtils.isHiddenFolder(videoItem.getParentPath())){
                    tempList.add(videoItem);
                }
            }
            else if(object instanceof ImageItem){
                ImageItem imageItem = new ImageItem(object);
                if(sharedPreferenceManager.getDisplayImageFileHidden() || !ItemUtils.isHiddenFolder(imageItem.getParentPath())){
                    tempList.add(imageItem);
                }
            }
            else if(object instanceof DocumentItem){
                DocumentItem documentItem = new DocumentItem(object);
                if(sharedPreferenceManager.getDisplayDocumentFileHidden() || !ItemUtils.isHiddenFolder(documentItem.getParentPath())){
                    tempList.add(documentItem);
                }
            }
            else if(object instanceof ApkItem){
                ApkItem apkItem = new ApkItem(object);
                if(sharedPreferenceManager.getDisplayDocumentFileHidden() || !ItemUtils.isHiddenFolder(apkItem.getParentPath())){
                    tempList.add(apkItem);
                }
            }
        }
        return tempList;
    }

    public long getVideoItemPreviousDuration(String fileName){
        long duration= 0;
        RealmObject object = (RealmObject) fetchObject("path", fileName, VideoItem.class);
        if(object != null){
            VideoItem videoItem = new VideoItem(object);
            duration = videoItem.getPlayBackDuration();
        }
        return duration;
    }

    public String getVideoItemSubtitleFile(String fileName){
        String subtitle= "";
        RealmObject object = (RealmObject) fetchObject("path", fileName, VideoItem.class);
        if(object != null){
            VideoItem videoItem = new VideoItem(object);
            subtitle = videoItem.getSubtitleFile();
        }
        return subtitle;
    }

    public void saveVideoItemPreviousDuration(String fileName, long duration){
        RealmObject object = (RealmObject) fetchObject("path", fileName, VideoItem.class);
        if(object != null){
            VideoItem videoItem = new VideoItem(object);
            videoItem.setPlayBackDuration(duration);
            bulkInsert(videoItem);
        }
    }

    public void saveVideoItemSubtitleFile(String fileName, String subtitleFileName){
        RealmObject object = (RealmObject) fetchObject("path", fileName, VideoItem.class);
        if(object != null){
            VideoItem videoItem = new VideoItem(object);
            videoItem.setSubtitleFile(subtitleFileName);
            bulkInsert(videoItem);
        }
    }

    public List<RealmObject> getFavoriteItemListFromRealm(Class classname){
        List<RealmObject> tempList = new ArrayList<>();
        List<RealmObject> results = fetchObjectListWithBooleanId("isFavorite", true,classname);
        for(RealmObject object : results){
            if(object instanceof AudioItem){
                tempList.add(new AudioItem(object));
            }
            else if(object instanceof VideoItem){
                tempList.add(new VideoItem(object));
            }
            else if(object instanceof ImageItem){
                tempList.add(new ImageItem(object));
            }
            else if(object instanceof DocumentItem){
                tempList.add(new DocumentItem(object));
            }
            else if(object instanceof ApkItem){
                tempList.add(new ApkItem(object));
            }
        }
        return tempList;
    }

    public Map<String,AudioItem> getAudioItemListFromRealm(){
        Map<String,AudioItem> tempList = new HashMap<>();
        List<RealmObject> results = getAllObjects(AudioItem.class);
        for(RealmObject object : results){
                AudioItem audioItem = new AudioItem(object);
                tempList.put(audioItem.getPath(), audioItem);
        }
        return tempList;
    }

    public Map<String,VideoItem> getVideoItemListFromRealm(){
        Map<String, VideoItem> tempList = new HashMap<>();
        List<RealmObject> results = getAllObjects(VideoItem.class);
        for(RealmObject object : results){
            VideoItem videoItem = new VideoItem(object);
            tempList.put(videoItem.getPath(), videoItem);
        }
        return tempList;
    }

    public Map<String, ImageItem> getImageItemListFromRealm(){
        Map<String, ImageItem> tempList = new HashMap<>();
        List<RealmObject> results = getAllObjects(ImageItem.class);
        for(RealmObject object : results){
            ImageItem imageItem = new ImageItem(object);
            tempList.put(imageItem.getPath(), imageItem);
        }
        return tempList;
    }

    public Map<String,DocumentItem> getDocumentItemListFromRealm(){
        Map<String,DocumentItem> tempList = new HashMap<>();
        List<RealmObject> results = getAllObjects(DocumentItem.class);
        for(RealmObject object : results){
            DocumentItem documentItem = new DocumentItem(object);
            tempList.put(documentItem.getPath(), documentItem);
        }
        return tempList;
    }

    public Map<String,ApkItem> getApkItemListFromRealm(){
        Map<String,ApkItem> tempList = new HashMap<>();
        List<RealmObject> results = getAllObjects(ApkItem.class);
        for(RealmObject object : results){
            ApkItem apkItem = new ApkItem(object);
            tempList.put(apkItem.getPath(), apkItem);
        }
        return tempList;
    }

    public List<RealmObject> getItemWithIdFromRealm(String field , String id, Class realmClass){
        List<RealmObject> tempList = new ArrayList<>();
        List<RealmObject> results = fetchObjectListWithId(field,id,realmClass);
        for(RealmObject object : results){
            if(object instanceof AudioItem){
                tempList.add(new AudioItem(object));
            }
            else if(object instanceof VideoItem){
                tempList.add(new VideoItem(object));
            }
            else if(object instanceof ImageItem){
                tempList.add(new ImageItem(object));
            }
            else if(object instanceof ApkItem){
                tempList.add(new ApkItem(object));
            }
            else if(object instanceof DocumentItem){
                tempList.add(new DocumentItem(object));
            }
        }
        return tempList;
    }
}