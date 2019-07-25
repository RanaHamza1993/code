package nexaplayer.mkv.mpg.flv.wmv.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import nexaplayer.mkv.mpg.flv.wmv.NexaPlayerApplication;
import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.OptionsDialog;
import nexaplayer.mkv.mpg.flv.wmv.service.AudioPlayerService;

import java.io.File;

import io.realm.RealmObject;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Sultan Ahmed on 12/6/2017.
 */

public class UtilityMethods {


    public static long HOUR_IN_MILLISECONDS = 1000 * 60 * 60;
    public static long DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;
    public static long MIN_IN_MILLISECONDS = 1000 * 60;
    public static long SECONDS_IN_MILLISECONDS = 1000;

    private static ProgressDialog progressDialog;

    /*checking if string is empty or not*/
    public static boolean isEmptyString(String string){
        if(string == null){
            return true;
        }
        else return string.equals("");
    }

    /* get the size of the given file*/
    public static String getFileSize(File file){
        long size = file.length();
        if(size < 1024){
            return size + " B";
        }
        else if(size >= Math.pow(1024,3)){
            return  String.format("%.2f",((double)size /Math.pow(1024,3) )) + "GB";
        }
        else if(size > Math.pow(1024,2)){
            return  String.format("%.2f",((double)size / Math.pow(1024,2))) + "MB";
        }
        else{
            return  String.format("%.2f",((double)size / (1024))) + "KB";
        }
    }

    /*show toast message*/
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getFormattedDurationString(String durationString){
        if(durationString==null||durationString.equals("0")){
            return "--:--";
        }
        long duration = Long.valueOf(durationString);
        long hours = (duration / HOUR_IN_MILLISECONDS);
        duration = duration % HOUR_IN_MILLISECONDS;
        long min = duration / MIN_IN_MILLISECONDS;
        duration = duration % MIN_IN_MILLISECONDS;
        long sec = duration / SECONDS_IN_MILLISECONDS;

        String formattedDuration = "";
        if(hours > 0){
            formattedDuration = (hours > 9 ? hours : "0"+ hours) + ":";
        }
        formattedDuration = formattedDuration + (min > 9 ? min : "0"+ min) + ":" + (sec > 9 ? sec : "0"+ sec);

        return formattedDuration;
    }

    public static boolean containsString(String data, String substring){
        return data.toLowerCase().contains(substring.toLowerCase());
    }

    public static void showProgoressDialog(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog(){
        progressDialog.dismiss();
    }

    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void stopAudioService(Context context){
        Intent audioServiceIntent = new Intent(context, AudioPlayerService.class);
        context.stopService(audioServiceIntent);
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(AudioPlayerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void reportGoogleAnalytics(String className, String methodName){
        Tracker tracker= NexaPlayerApplication.getDefaultTracker();
        Log.i( className, methodName);
        tracker.setScreenName(className + ":"+methodName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void showDetailsDialog(Context context, RealmObject item, String itemType, Fragment fragment){
        OptionsDialog dialog = new OptionsDialog(context, item, itemType, fragment);
        dialog.show();
    }

}
