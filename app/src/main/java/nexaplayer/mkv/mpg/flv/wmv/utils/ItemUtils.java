package nexaplayer.mkv.mpg.flv.wmv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.system.Os;
import android.util.Log;

import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/8/2017.
 */

public class ItemUtils {

    public static boolean verifyItemType(File item, String itemType) {
        boolean isItem = false;
        String extension = FilenameUtils.getExtension(item.getName()).toLowerCase();
        if (!UtilityMethods.isEmptyString(extension)) {
            switch (itemType) {
                case AppConstants.ITEM_TYPE_AUDIO:
                    List<String> audioList = Arrays.asList(AppConstants.AUDIO_FILE_TYPES);
                    isItem = (audioList.contains(extension) && !item.isDirectory());
                    break;
                case AppConstants.ITEM_TYPE_VIDEO:
                    List<String> videoList = Arrays.asList(AppConstants.VIDEO_FILE_TYPES);
                    isItem = (videoList.contains(extension) && !item.isDirectory());
                    break;
                case AppConstants.ITEM_TYPE_IMAGE:
                    List<String> imageList = Arrays.asList(AppConstants.IMAGE_FILE_TYPES);
                    isItem = (imageList.contains(extension) && !item.isDirectory());
                    break;
                case AppConstants.ITEM_TYPE_DOCUMENT:
                    List<String> documentList = Arrays.asList(AppConstants.DOCUMENT_FILE_TYPES);
                    isItem = (documentList.contains(extension) && !item.isDirectory());
                    break;
                case AppConstants.ITEM_TYPE_APK:
                    List<String> apkList = Arrays.asList(AppConstants.APK_FILE_TYPES);
                    isItem = (apkList.contains(extension) && !item.isDirectory());
                    break;
            }
        }
        return isItem;
    }

    public static String getItemType(File item){
        String extension = FilenameUtils.getExtension(item.getName()).toLowerCase();
        List<String> audioList = Arrays.asList(AppConstants.AUDIO_FILE_TYPES);
        List<String> videoList = Arrays.asList(AppConstants.VIDEO_FILE_TYPES);
        List<String> imageList = Arrays.asList(AppConstants.IMAGE_FILE_TYPES);
        List<String> documentList = Arrays.asList(AppConstants.DOCUMENT_FILE_TYPES);
        List<String> apkList = Arrays.asList(AppConstants.APK_FILE_TYPES);
        if(videoList.contains(extension)){
            return AppConstants.ITEM_TYPE_VIDEO;
        }
        else if(audioList.contains(extension)){
            return AppConstants.ITEM_TYPE_AUDIO;
        }
        else if(imageList.contains(extension)){
            return AppConstants.ITEM_TYPE_IMAGE;
        }
        else if(documentList.contains(extension)){
            return AppConstants.ITEM_TYPE_DOCUMENT;
        }
        else if(apkList.contains(extension)){
            return AppConstants.ITEM_TYPE_APK;
        }
        return "";
    }

    public static boolean verifySupportedItemType(File item) {
        String extension = FilenameUtils.getExtension(item.getName()).toLowerCase();
        if (!UtilityMethods.isEmptyString(extension)) {
                    List<String> audioList = Arrays.asList(AppConstants.AUDIO_FILE_TYPES);
                    List<String> videoList = Arrays.asList(AppConstants.VIDEO_FILE_TYPES);
                    List<String> imageList = Arrays.asList(AppConstants.IMAGE_FILE_TYPES);
                    List<String> documentList = Arrays.asList(AppConstants.DOCUMENT_FILE_TYPES);
                    List<String> apkList = Arrays.asList(AppConstants.APK_FILE_TYPES);
                    return ((audioList.contains(extension) || videoList.contains(extension) || imageList.contains(extension))
                            || documentList.contains(extension) || apkList.contains(extension) && !item.isDirectory());
        }
        return false;
    }

    public static RealmObject getItem(Context context,File file){

        switch (getItemType(file)){
            case AppConstants.ITEM_TYPE_AUDIO:
                return getAudioRealmItem(context,file);
            case AppConstants.ITEM_TYPE_VIDEO:
                return getVideoRealmItem(file);
            case AppConstants.ITEM_TYPE_IMAGE:
                return getImageRealmItem(file);
            case AppConstants.ITEM_TYPE_DOCUMENT:
                return getDocumentRealmItem(file);
            case AppConstants.ITEM_TYPE_APK:
                return getApkRealmItem(file);
            default:
                return null;
        }
    }

    public static RealmObject getItem(Context context,File file,String targetType){
        String type=getItemType(file);
        if (!type.equalsIgnoreCase(targetType)) {
            return null;
        }
        switch (type){
            case AppConstants.ITEM_TYPE_AUDIO:
                return getAudioRealmItem(context,file);
            case AppConstants.ITEM_TYPE_VIDEO:
                return getVideoRealmItem(file);
            case AppConstants.ITEM_TYPE_IMAGE:
                return getImageRealmItem(file);
            case AppConstants.ITEM_TYPE_DOCUMENT:
                return getDocumentRealmItem(file);
            case AppConstants.ITEM_TYPE_APK:
                return getApkRealmItem(file);
            default:
                return null;
        }
    }

    public static AudioItem getAudioItem(File file){
        AudioItem audioItem = new AudioItem();
        audioItem.setName(file.getName());
        audioItem.setExtension(FilenameUtils.getExtension(file.getName()));
        audioItem.setNew(isFileNew(file));
        audioItem.setPath(file.getAbsolutePath());
        audioItem.setDate(new Date(file.lastModified()));
        audioItem.setSize(UtilityMethods.getFileSize(file));
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(audioItem.getPath());
            audioItem.setDuration(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            return audioItem;
        }
    }

    public static VideoItem getVideoItem(File file){
        VideoItem videoItem = new VideoItem();
        videoItem.setName(file.getName());
        videoItem.setExtension(FilenameUtils.getExtension(file.getName()));
        videoItem.setNew(isFileNew(file));
        videoItem.setPath(file.getAbsolutePath());
        videoItem.setDate(new Date(file.lastModified()));
        videoItem.setSize(UtilityMethods.getFileSize(file));
        return videoItem;
    }

    public static ImageItem getImageItem(File file){
        ImageItem imageItem = new ImageItem();
        imageItem.setName(file.getName());
        imageItem.setExtension(FilenameUtils.getExtension(file.getName()));
        imageItem.setNew(isFileNew(file));
        imageItem.setPath(file.getAbsolutePath());
        imageItem.setDate(new Date(file.lastModified()));
        imageItem.setSize(UtilityMethods.getFileSize(file));
        return imageItem;
    }

    public static AudioItem getAudioRealmItem(Context context,File file){
        AudioItem audioItem = new AudioItem();
        audioItem.setName(file.getName());
        audioItem.setExtension(FilenameUtils.getExtension(file.getName()));
        audioItem.setNew(isFileNew(file));
        audioItem.setPath(file.getAbsolutePath());
        audioItem.setParentPath(file.getParent());
        audioItem.setDate(new Date(file.lastModified()));
        audioItem.setSize(UtilityMethods.getFileSize(file));
        String artistName = null;
        String albumName = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Log.v("wasim","audio path="+audioItem.getPath());
            mediaMetadataRetriever.setDataSource(context,Uri.fromFile(new File(audioItem.getPath())));
            audioItem.setDuration(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            artistName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        }
        catch (Exception e){
            Log.i("Find All files", "setting audio data error");
            e.printStackTrace();
        }
        finally {
            if (UtilityMethods.isEmptyString(artistName)) {
                artistName = "unknown";
            }
            if (UtilityMethods.isEmptyString(albumName)) {
                albumName = "unknown";
            }
            audioItem.setAlbum(albumName);
            audioItem.setArtist(artistName);
            return audioItem;
        }
    }

    public static VideoItem getVideoRealmItem(File file){
        VideoItem videoItem = new VideoItem();
        videoItem.setName(file.getName());
        videoItem.setExtension(FilenameUtils.getExtension(file.getName()));
        videoItem.setNew(isFileNew(file));
        videoItem.setPath(file.getAbsolutePath());
        videoItem.setParentPath(file.getParent());
        videoItem.setDate(new Date(file.lastModified()));
        videoItem.setSize(UtilityMethods.getFileSize(file));
        return videoItem;
    }

    public static ImageItem getImageRealmItem(File file){
        ImageItem imageItem = new ImageItem();
        imageItem.setName(file.getName());
        imageItem.setExtension(FilenameUtils.getExtension(file.getName()));
        imageItem.setNew(isFileNew(file));
        imageItem.setParentPath(file.getParent());
        imageItem.setPath(file.getAbsolutePath());
        imageItem.setDate(new Date(file.lastModified()));
        imageItem.setSize(UtilityMethods.getFileSize(file));
        return imageItem;
    }

    public static DocumentItem getDocumentRealmItem(File file){
        DocumentItem documentItem = new DocumentItem();
        documentItem.setName(file.getName());
        documentItem.setExtension(FilenameUtils.getExtension(file.getName()));
        documentItem.setParentPath(file.getParent());
        documentItem.setPath(file.getAbsolutePath());
        documentItem.setDate(new Date(file.lastModified()));
        documentItem.setSize(UtilityMethods.getFileSize(file));
        return documentItem;
    }

    public static ApkItem getApkRealmItem(File file){
        ApkItem apkItem = new ApkItem();
        apkItem.setName(file.getName());
        apkItem.setExtension(FilenameUtils.getExtension(file.getName()));
        apkItem.setParentPath(file.getParent());
        apkItem.setPath(file.getAbsolutePath());
        apkItem.setDate(new Date(file.lastModified()));
        apkItem.setSize(UtilityMethods.getFileSize(file));
        return apkItem;
    }

    public static VideoItem setMetaDataOnVideoItem(Context context,VideoItem videoItem){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        //mediaMetadataRetriever.setDataSource(videoItem.getPath());
        try {
            Log.v("wasim", "setMetaDataOnVideoItem path=" + videoItem.getPath());
            mediaMetadataRetriever.setDataSource(context, Uri.fromFile(new File(videoItem.getPath())));
            videoItem.setDuration(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            videoItem.setHeight(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            videoItem.setWidth(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return videoItem;
    }

    public static ImageItem setMetaDataOnImageItem(ImageItem imageItem, Context context){
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageItem.getPath(), options);
            imageItem.setWidth(options.outWidth + "");
            imageItem.setHeight(options.outHeight + "");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            return imageItem;
        }
    }

    public static ArrayList<AudioItem> getAudioList(List<File> fileList){
        ArrayList<AudioItem> audioItems = new ArrayList<>();
        for(File file : fileList){
            audioItems.add(getAudioItem(file));
        }
        return audioItems;
    }

    public static ArrayList<VideoItem> getVideoList(List<File> fileList){
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        for(File file : fileList){
            videoItems.add(getVideoItem(file));
        }
        return videoItems;
    }

    public static ArrayList<ImageItem> getImageList(List<File> fileList){
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        for(File file : fileList){
            imageItems.add(getImageItem(file));
        }
        return imageItems;
    }

    private static long lastAccessTime(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                return Os.lstat(file.getAbsolutePath()).st_atime;
            }
            catch (Exception e){
                e.printStackTrace();
                return file.lastModified();
            }
        } else {
            try {
                Class<?> clazz = Class.forName("libcore.io.Libcore");
                Field field = clazz.getDeclaredField("os");
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object os = field.get(null);

                Method method = os.getClass().getMethod("lstat", String.class);
                Object lstat = method.invoke(os, file.getAbsolutePath());

                field = lstat.getClass().getDeclaredField("st_atime");
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field.getLong(lstat);
            }
            catch (Exception e){
                e.printStackTrace();
                return file.lastModified();
            }
        }
    }

    public static boolean isFileNew(File file){
        boolean isNew = false;
        if(file.lastModified() > (new Date().getTime() - (7 * 24 * 60 * 60 * 1000))){
           if(lastAccessTime(file) <= file.lastModified() ){
               isNew = true;
           }
        }
        return isNew;
    }

    public static Bitmap getBitmapFromImage(String filePath){
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath), 96, 96);
    }

    public static Bitmap createVideoThumbnailFromPath(String filePath, int type){
        return ThumbnailUtils.createVideoThumbnail(filePath, type);
    }

    public static String[] convertAudioFilesToStringArray(List<AudioItem> audioItemList){
        String[] array = new String[audioItemList.size()];
        for(int i=0;i< audioItemList.size(); i++){
            array[i] = audioItemList.get(i).getPath();
        }
        return array;
    }

    public static ArrayList<AudioItem> convertStringArrayToAudioList(Context context,String[] array){
        ArrayList<AudioItem> audioItemList = new ArrayList<>();
        for(String name: array){
            audioItemList.add(getAudioRealmItem(context,new File(name)));
        }
        return audioItemList;
    }

    public static Class getClassByItemType(String itemType){
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                return AudioItem.class;
            case AppConstants.ITEM_TYPE_VIDEO:
                return VideoItem.class;
            case AppConstants.ITEM_TYPE_IMAGE:
                return ImageItem.class;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                return DocumentItem.class;
            case AppConstants.ITEM_TYPE_APK:
                return ApkItem.class;
            default:
                return AudioItem.class;
        }
    }

    public static boolean isHiddenFolder(String parentPath){
        if(parentPath.contains(AppConstants.BASE_DIRECTORY.getAbsolutePath()+"/"+"Android/") ||
                parentPath.contains(AppConstants.SECONDARY_STORAGE.getAbsolutePath()+"/"+"Android/")){
            return true;
        }
        int lastIndex = parentPath.indexOf('.');
        if(lastIndex <= 0){
            return false;
        }
        char c = parentPath.charAt(lastIndex-1);
        if(c =='/' || c=='\\'){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isGreaterThan10minutes(long duration){
        if(duration > (10 * (60 * 1000))){
            return true;
        }
        else{
            return false;
        }
    }
}
