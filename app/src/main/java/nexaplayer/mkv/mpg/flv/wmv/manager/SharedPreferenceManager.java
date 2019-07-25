package nexaplayer.mkv.mpg.flv.wmv.manager;

import android.content.Context;
import android.content.SharedPreferences;

import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sultan Ahmed on 12/5/2017.
 */

public class SharedPreferenceManager {

    private final static String LIST_STYLE_KEY = "list_style_key";


    private final static String FILE_PATH_KEY = "file_path_key";
    private final static String FILE_DATE_KEY = "file_date_key";
    private final static String FILE_EXTENSION_KEY = "file_extension_key";
    private final static String FILE_SIZE_KEY = "file_size_key";
    private final static String FILE_RESOLUTION_KEY = "file_resolution_key";
    private final static String FILE_NEW_PERIOD = "file_new_period";


    private final static String AUDIO_FILE_PATH_KEY = "audio_file_path_key";
    private final static String AUDIO_FILE_DATE_KEY = "audio_file_date_key";
    private final static String AUDIO_FILE_EXTENSION_KEY = "audio_file_extension_key";
    private final static String AUDIO_FILE_SIZE_KEY = "audio_file_size_key";
    private final static String AUDIO_REPEAT_KEY = "audio_repeat_key";
    private final static String AUDIO_SHUFFLE_KEY = "audio_shuffle_key";
    private final static String AUDIO_HIDDEN_KEY = "audio_hidden_key";

    private final static String VIDEO_FILE_PATH_KEY = "video_file_path_key";
    private final static String VIDEO_FILE_DATE_KEY = "video_file_date_key";
    private final static String VIDEO_FILE_EXTENSION_KEY = "video_file_extension_key";
    private final static String VIDEO_FILE_SIZE_KEY = "video_file_size_key";
    private final static String VIDEO_FILE_RESOLUTION_KEY = "video_file_resolution_key";
    private final static String VIDEO_RESUME_KEY = "video_resume_key";
    private final static String VIDEO_HIDDEN_KEY = "video_hidden_key";

    private final static String IMAGE_FILE_PATH_KEY = "image_file_path_key";
    private final static String IMAGE_FILE_DATE_KEY = "image_file_date_key";
    private final static String IMAGE_FILE_EXTENSION_KEY = "image_file_extension_key";
    private final static String IMAGE_FILE_SIZE_KEY = "image_file_size_key";
    private final static String IMAGE_FILE_RESOLUTION_KEY = "image_file_resolution_key";
    private final static String IMAGE_HIDDEN_KEY = "image_hidden_key";


    private final static String DOCUMENT_FILE_PATH_KEY = "document_file_path_key";
    private final static String DOCUMENT_FILE_DATE_KEY = "document_file_date_key";
    private final static String DOCUMENT_FILE_EXTENSION_KEY = "document_file_extension_key";
    private final static String DOCUMENT_FILE_SIZE_KEY = "document_file_size_key";
    private final static String DOCUMENT_HIDDEN_KEY = "document_hidden_key";

    private Context context;

    private SharedPreferenceManager(){}

    private SharedPreferenceManager(Context context){
        this.context = context;
    }

    public static SharedPreferenceManager getInstance(Context context){
        return new SharedPreferenceManager(context);
    }

    private void setBoolean(String key, boolean value){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void setString(String key, String value){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setInt(String key, int value){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void setLong(String key, long value){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private void setFloat(String key, float value){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    private void setStringSet(String key, Set<String> value){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }
    private boolean getBoolean(String key){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    private String getString(String key){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return pref.getString(key, "");
    }

    private int getInt(String key){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    private int getInt(String key, int value){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return pref.getInt(key, value);
    }

    private long getLong(String key){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return pref.getLong(key, 0);
    }

    private float getFloat(String key){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return pref.getFloat(key, 0);
    }

    private Set<String> getStringSet(String key){
        SharedPreferences pref = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return pref.getStringSet(key, null);
    }

    public void saveListStyle(int value){
        setInt(LIST_STYLE_KEY, value);
    }
    public int getListStyle(){
        //return getInt(LIST_STYLE_KEY);
        return 1;
    }

    public void saveDisplayFilePath(boolean value){
        setBoolean(FILE_PATH_KEY, value);
    }
    public boolean getDisplayFilePath(){
        return getBoolean(FILE_PATH_KEY);
    }

    public void saveDisplayFileExtension(boolean value){
        setBoolean(FILE_EXTENSION_KEY, value);
    }
    public boolean getDisplayFileEntension(){
        return getBoolean(FILE_EXTENSION_KEY);
    }

    public void saveDisplayFileSize(boolean value){
        setBoolean(FILE_SIZE_KEY, value);
    }
    public boolean getDisplayFileSize(){
        return getBoolean(FILE_SIZE_KEY);
    }

    public void saveDisplayFileDate(boolean value){
        setBoolean(FILE_DATE_KEY, value);
    }
    public boolean getDisplayFileDate(){
        return getBoolean(FILE_DATE_KEY);
    }

    public void saveDisplayFileRes(boolean value){
        setBoolean(FILE_RESOLUTION_KEY, value);
    }
    public boolean getDisplayFileRes(){
        return getBoolean(FILE_RESOLUTION_KEY);
    }

    public void saveNewLabelPeriod(int value){
        setInt(FILE_NEW_PERIOD, value);
    }

    public int getNewLabelPeriod(){
       return getInt(FILE_NEW_PERIOD, AppConstants.NEW_LABEL_DEFAULT_PERIOD);
    }

    /*Audio Files Settings*/
    public void saveDisplayAudioFilePath(boolean value){
        setBoolean(AUDIO_FILE_PATH_KEY, value);
    }
    public boolean getDisplayAudioFilePath(){
       return getBoolean(AUDIO_FILE_PATH_KEY);
    }
    public void saveDisplayAudioFileDate(boolean value){
        setBoolean(AUDIO_FILE_DATE_KEY, value);
    }
    public boolean getDisplayAudioFileDate(){
       return getBoolean(AUDIO_FILE_DATE_KEY);
    }
    public void saveDisplayAudioFileExtension(boolean value){
        setBoolean(AUDIO_FILE_EXTENSION_KEY, value);
    }
    public boolean getDisplayAudioFileExtension(){
       return getBoolean(AUDIO_FILE_EXTENSION_KEY);
    }
    public void saveDisplayAudioFileSize(boolean value){
        setBoolean(AUDIO_FILE_SIZE_KEY, value);
    }
    public boolean getDisplayAudioFileSize(){
       return getBoolean(AUDIO_FILE_SIZE_KEY);
    }
    public void saveAudioFileRepeat(boolean value){
        setBoolean(AUDIO_REPEAT_KEY, value);
    }
    public boolean getAudioFileRepeat(){
       return getBoolean(AUDIO_REPEAT_KEY);
    }
    public void saveAudioFileShuffle(boolean value){
        setBoolean(AUDIO_SHUFFLE_KEY, value);
    }
    public boolean getAudioFileShuffle(){
       return getBoolean(AUDIO_SHUFFLE_KEY);
    }
    public void saveDisplayAudioFileHidden(boolean value){
        setBoolean(AUDIO_HIDDEN_KEY, value);
    }
    public boolean getDisplayAudioFileHidden(){
        return getBoolean(AUDIO_HIDDEN_KEY);
    }


    /*Video Settings*/
    public void saveDisplayVideoFilePath(boolean value){
        setBoolean(VIDEO_FILE_PATH_KEY, value);
    }
    public boolean getDisplayVideoFilePath(){
        return getBoolean(VIDEO_FILE_PATH_KEY);
    }
    public void saveDisplayVideoFileDate(boolean value){
        setBoolean(VIDEO_FILE_DATE_KEY, value);
    }
    public boolean getDisplayVideoFileDate(){
        return getBoolean(VIDEO_FILE_DATE_KEY);
    }
    public void saveDisplayVideoFileExtension(boolean value){
        setBoolean(VIDEO_FILE_EXTENSION_KEY, value);
    }
    public boolean getDisplayVideoFileExtenstion(){
        return getBoolean(VIDEO_FILE_EXTENSION_KEY);
    }
    public void saveDisplayVideoFileSize(boolean value){
        setBoolean(VIDEO_FILE_SIZE_KEY, value);
    }
    public boolean getDisplayVideoFileSize(){
        return getBoolean(VIDEO_FILE_SIZE_KEY);
    }
    public void saveDisplayVideoFileResolution(boolean value){
        setBoolean(VIDEO_FILE_RESOLUTION_KEY, value);
    }
    public boolean getDisplayVideoFileResolution(){
        return getBoolean(VIDEO_FILE_RESOLUTION_KEY);
    }
    public void saveDisplayVideoFileResume(boolean value){
        setBoolean(VIDEO_RESUME_KEY, value);
    }
    public boolean getDisplayVideoFileResume(){
        return getBoolean(VIDEO_RESUME_KEY);
    }
    public void saveDisplayVideoFileHidden(boolean value){
        setBoolean(VIDEO_HIDDEN_KEY, value);
    }
    public boolean getDisplayVideoFileHidden(){
        return getBoolean(VIDEO_HIDDEN_KEY);
    }

    /*Image File Settings*/
    public void saveDisplayImageFilePath(boolean value){
        setBoolean(IMAGE_FILE_PATH_KEY, value);
    }
    public boolean getDisplayImageFilePath(){
        return getBoolean(IMAGE_FILE_PATH_KEY);
    }
    public void saveDisplayImageFileDate(boolean value){
        setBoolean(IMAGE_FILE_DATE_KEY, value);
    }
    public boolean getDisplayImageFileDate(){
        return getBoolean(IMAGE_FILE_DATE_KEY);
    }
    public void saveDisplayImageFileExtension(boolean value){
        setBoolean(IMAGE_FILE_EXTENSION_KEY, value);
    }
    public boolean getDisplayImageFileExtension(){
        return getBoolean(IMAGE_FILE_EXTENSION_KEY);
    }
    public void saveDisplayImageFileSize(boolean value){
        setBoolean(IMAGE_FILE_SIZE_KEY, value);
    }
    public boolean getDisplayImageFileSize(){
        return getBoolean(IMAGE_FILE_SIZE_KEY);
    }
    public void saveDisplayImageFileResolution(boolean value){
        setBoolean(IMAGE_FILE_RESOLUTION_KEY, value);
    }
    public boolean getDisplayImageFileResolution(){
        return getBoolean(IMAGE_FILE_RESOLUTION_KEY);
    }
    public void saveDisplayImageFileHidden(boolean value){
        setBoolean(IMAGE_HIDDEN_KEY, value);
    }
    public boolean getDisplayImageFileHidden(){
        return getBoolean(IMAGE_HIDDEN_KEY);
    }

    /*Document Files Settings*/
    public void saveDisplayDocumentFilePath(boolean value){
        setBoolean(DOCUMENT_FILE_PATH_KEY, value);
    }
    public boolean getDisplayDocumentFilePath(){
        return getBoolean(DOCUMENT_FILE_PATH_KEY);
    }
    public void saveDisplayDocumentFileDate(boolean value){
        setBoolean(DOCUMENT_FILE_DATE_KEY, value);
    }
    public boolean getDisplayDocumentFileDate(){
        return getBoolean(DOCUMENT_FILE_DATE_KEY);
    }
    public void saveDisplayDocumentFileExtension(boolean value){
        setBoolean(DOCUMENT_FILE_EXTENSION_KEY, value);
    }
    public boolean getDisplayDocumentFileExtension(){
        return getBoolean(DOCUMENT_FILE_EXTENSION_KEY);
    }
    public void saveDisplayDocumentFileSize(boolean value){
        setBoolean(DOCUMENT_FILE_SIZE_KEY, value);
    }
    public boolean getDisplayDocumentFileSize(){
        return getBoolean(DOCUMENT_FILE_SIZE_KEY);
    }
    public void saveDisplayDocumentFileHidden(boolean value){
        setBoolean(DOCUMENT_HIDDEN_KEY, value);
    }
    public boolean getDisplayDocumentFileHidden(){
        return getBoolean(DOCUMENT_HIDDEN_KEY);
    }

}
