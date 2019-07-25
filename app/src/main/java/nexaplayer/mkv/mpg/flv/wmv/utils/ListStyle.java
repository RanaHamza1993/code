package nexaplayer.mkv.mpg.flv.wmv.utils;

import static nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants.STYLE_ALL_FILES;
import static nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants.STYLE_ALL_FILES_AND_FOLDER;
import static nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants.STYLE_ALL_FOLDER;

/**
 * Created by Sultan Ahmed on 12/5/2017.
 */

public enum ListStyle {
    NORMAL(0, STYLE_ALL_FILES_AND_FOLDER ), ALL_FOLDERS(1, STYLE_ALL_FOLDER), ALL_FILES(2, STYLE_ALL_FILES);

    private int value;
    private String detail;

    ListStyle(int value, String detail){
        this.value = value;
        this.detail = detail;
    }

    public int getValue(){
        return this.value;
    }

    public String getDetail(){
        return this.detail;
    }

}
