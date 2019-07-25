package nexaplayer.mkv.mpg.flv.wmv.utils;

/**
 * Created by Sultan Ahmed on 1/20/2018.
 */

public enum VideoSize {
    NORMAL(0, AppConstants.VIDEO_SIZE_NORMAL), RATIO43(1,AppConstants.VIDEO_SIZE_RATIO43 ), RATIO169(2, AppConstants.VIDEO_SIZE_RATION169), FITSCREEN(3, AppConstants.VIDEO_SIZE_FITSCREEN);

    private int value;
    private String title;

    VideoSize(int value, String title){
        this.value = value;
        this.title = title;
    }

    public int getValue(){
        return this.value;
    }
    public String getTitle() { return this.title; }

}
