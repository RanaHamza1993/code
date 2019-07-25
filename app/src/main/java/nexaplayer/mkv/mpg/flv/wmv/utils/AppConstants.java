package nexaplayer.mkv.mpg.flv.wmv.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Sultan Ahmed on 12/5/2017.
 */

public interface AppConstants {

    String TAB_VIDEO = "tab_video";
    String TAB_AUDIO = "tab_audio";
    String TAB_IMAGES = "tab_images";
    String TAB_SEARCH = "tab_search";
    String TAB_MORE = "tab_more";
    String TAB_FAVORITE = "tab_favorite";
    String TAB_APPS = "tab_app";

    String ITEM_TYPE_AUDIO = "item_type_audio";
    String ITEM_TYPE_VIDEO = "item_type_video";
    String ITEM_TYPE_IMAGE = "item_type_image";
    String ITEM_TYPE_DOCUMENT = "item_type_document";
    String ITEM_TYPE_APK = "item_type_apk";

    String STYLE_ALL_FILES_AND_FOLDER = "View folders and files";
    String STYLE_ALL_FOLDER = "View all folders";
    String STYLE_ALL_FILES = "View all files";

    String VIEW_TYPE_SIZE = "Size";
    String VIEW_TYPE_DATE = "Date";
    String VIEW_TYPE_EXTENSION = "Extension";
    String VIEW_TYPE_FOLDER = "Folder";
    String VIEW_TYPE_RESOLUTION = "Resolution";

    /*audio fragment list*/
    String ALL_AUDIO_FRAGMENT = "ALL_AUDIO_FRAGMENT";
    String AUDIO_ALL_FOLDER_FRAGMENT = "AUDIO_ALL_FOLDER_FRAGMENT";
    String ALL_ARTIST_FRAGMENT = "ALL_ARTIST_FRAGMENT";
    String ALL_ALBUM_FRAGMENT = "ALL_ALBUM_FRAGMENT";
    String AUDIO_SETTINGS_FRAGMENT = "AUDIO_SETTINGS_FRAGMENT";

    /*video fragment list*/
    String ALL_VIDEO_FRAGMENT = "ALL_VIDEO_FRAGMENT";
    String VIDEO_ALL_FOLDER_FRAGMENT = "VIDEO_ALL_FOLDER_FRAGMENT";
    String VIDEO_SETTINGS_FRAGMENT = "VIDEO_SETTINGS_FRAGMENT";

    /*image fragment list*/
    String ALL_IMAGE_FRAGMENT = "ALL_IMAGE_FRAGMENT";
    String IMAGE_ALL_FOLDER_FRAGMENT = "IMAGE_ALL_FOLDER_FRAGMENT";
    String IMAGE_SETTINGS_FRAGMENT = "IMAGE_SETTINGS_FRAGMENT";

    /*more fragment list*/
    String ALL_DOCUMENT_FRAGMENT = "ALL_DOCUMENT_FRAGMENT";
    String DOCUMENT_ALL_FOLDER_FRAGMENT = "DOCUMENT_ALL_FOLDER_FRAGMENT";
    String DOCUMENT_ALL_APK_FRAGMENT = "DOCUMENT_ALL_APK_FRAGMENT";
    String DOCUMENT_SETTINGS_FRAGMENT = "DOCUMENT_SETTINGS_FRAGMENT";


    /*search fragment list*/
    String SEARCH_AUDIO_FRAGMENT = "SEARCH_AUDIO_FRAGMENT";
    String SEARCH_VIDEO_FRAGMENT = "SEARCH_VIDEO_FRAGMENT";
    String SEARCH_IMAGE_FRAGMENT = "SEARCH_IMAGE_FRAGMENT";
    String SEARCH_DOCUMENT_FRAGMENT = "SEARCH_DOCUMENT_FRAGMENT";
    String SEARCH_ADS_FRAGMENT = "SEARCH_ADS_FRAGMENT";

    /*favorite fragment list*/
    String FAVORITE_AUDIO_FRAGMENT = "FAVORITE_AUDIO_FRAGMENT";
    String FAVORITE_VIDEO_FRAGMENT = "FAVORITE_VIDEO_FRAGMENT";
    String FAVORITE_IMAGE_FRAGMENT = "FAVORITE_IMAGE_FRAGMENT";
    String FAVORITE_DOCUMENT_FRAGMENT = "FAVORITE_DOCUMENT_FRAGMENT";
    String FAVORITE_APK_FRAGMENT = "FAVORITE_APK_FRAGMENT";
    String FAVORITE_ADS_FRAGMENT = "FAVORITE_ADS_FRAGMENT";

    int NEW_LABEL_DEFAULT_PERIOD = 7;

    String SHARED_PREFERENCE_KEY = "SHARED_PREFERENCES_KEY";

    File BASE_DIRECTORY = Environment.getExternalStorageDirectory();
    File SECONDARY_STORAGE = (new File("/storage/"));

    String[] AUDIO_FILE_TYPES = {"aac", "mp3", "mp2", "wav", "ogg", "mp4", "mpg", "mkv", "vob", "avi", "3gp", "mpeg", "wmv", "webm", "vob", "mts", "mov", "m4v", "flv", "wma", "asf", "dvr-ms", "f4v", "f4p", "f4a", "f4b", "mk3d",
            "mka", "midi", "qt", "m4a", "ogv", "oga", "wave", "aiff", "aif", "ts", "ta", "tsv", "mxf", "rm"};
    String[] VIDEO_FILE_TYPES = {"mp4", "mpg", "mkv", "vob", "avi", "3gp", "mpeg", "wmv", "webm", "vob", "mts", "mov", "m4v","flv", "ogg", "dvr-ms", "f4v", "f4p", "f4a", "f4b", "mk3d", "qt", "ogv", "ts", "tsv", "mxf", "rm"};
    String[] IMAGE_FILE_TYPES = {"jpeg", "jpg", "gif", "bmp", "png"};
    String[] DOCUMENT_FILE_TYPES = {"doc", "docx", "pdf", "ppt", "pptx", "xls", "xlsx" , "txt"};
    String[] APK_FILE_TYPES = {"apk"};
    String[] SUBTITLES_FILE_TYPE = {"srt", "mks", "txt"};

    String VIDEO_SIZE_NORMAL = "VIDEO_SIZE_NORMAL";
    String VIDEO_SIZE_RATIO43 = "VIDEO_SIZE_RATIO43";
    String VIDEO_SIZE_RATION169 = "VIDEO_SIZE_RATION169";
    String VIDEO_SIZE_FITSCREEN = "VIDEO_SIZE_FITSCREEN";

    String AUDIOSERVICE_BROADCAST_RECEIVER_FILTER = "AUDIOSERVICE_BROADCAST_RECEIVER_FILTER";

    //audio activity broadcast intent extra types
    String AUDIOACTIVITY_BROADCAST_UPDATE_PROGRESS = "AUDIOACTIVITY_BROADCAST_UPDATE_PROGRESS";
    String AUDIOACTIVITY_BROADCAST_UPDATE_NAME = "AUDIOACTIVITY_BROADCAST_UPDATE_NAME";
    String AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_TIME = "AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_TIME";
    String AUDIOACTIVITY_BROADCAST_UPDATE_TOTAL_DURATION = "AUDIOACTIVITY_BROADCAST_UPDATE_TOTAL_DURATION";
    String AUDIOACTIVITY_BROADCAST_PAUSE = "AUDIOACTIVITY_BROADCAST_PAUSE";
    String AUDIOACTIVITY_BROADCAST_PLAY = "AUDIOACTIVITY_BROADCAST_PLAY";
    String AUDIOACTIVITY_BROADCAST_STOP = "AUDIOACTIVITY_BROADCAST_STOP";
    String AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_INDEX = "AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_INDEX";

    String PROGRESS_KEY = "PROGRESS_KEY";
    String NAME_KEY = "NAME_KEY";
    String CURRENT_TIME_KEY = "CURRENT_TIME_KEY";
    String TOTAL_DURATION_KEY = "TOTAL_DURATION_KEY";
    String CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY";

    //audio service broadcast intent extra types
    String AUDIOSERVICE_BROADCAST_PLAY_PAUSE = "AUDIOSERVICE_BROADCAST_PLAY_PAUSE";
    String AUDIOSERVICE_BROADCAST_REWIND = "AUDIOSERVICE_BROADCAST_REWIND";
    String AUDIOSERVICE_BROADCAST_FORWARD = "AUDIOSERVICE_BROADCAST_FORWARD";
    String AUDIOSERVICE_BROADCAST_PREVIOUS = "AUDIOSERVICE_BROADCAST_PREVIOUS";
    String AUDIOSERVICE_BROADCAST_NEXT = "AUDIOSERVICE_BROADCAST_NEXT";
    String AUDIOSERVICE_BROADCAST_DESTROY = "AUDIOSERVICE_BROADCAST_DESTROY";
    String AUDIOSERVICE_BROADCAST_SEEKBAR_POSITION = "AUDIOSERVICE_BROADCAST_SEEKBAR_POSITION";
    String AUDIOSERVICE_BROADCAST_PLAY_CURRENT_POSITION = "AUDIOSERVICE_BROADCAST_PLAY_CURRENT_POSITION";

}
