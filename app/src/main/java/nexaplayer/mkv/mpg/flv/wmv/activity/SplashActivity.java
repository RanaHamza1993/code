package nexaplayer.mkv.mpg.flv.wmv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eyalbira.loadingdots.LoadingDots;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.SuccessCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllSupoortedFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.PermissionUtils;

public class SplashActivity extends AppCompatActivity {

    LoadingDots loadingDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadingDots = (LoadingDots) findViewById(R.id.loading);
        if (PermissionUtils.checkAndRequestPermissions(this)) {
            loadAllFilesFromStorage();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (PermissionUtils.RequestCodeResult(this, requestCode, permissions, grantResults)) {
            loadAllFilesFromStorage();
        }

    }

    private void loadAllFilesFromStorage(){
        loadingDots.startAnimation();
        loadingDots.setVisibility(View.VISIBLE);
        RealmManager realmManager=RealmManager.getInstance();
        int size=realmManager.getDataSize(ImageItem.class);
        if (size==0) {
        new FindAllSupoortedFilesAsyncTask(this, new SuccessCallback() {
            @Override
            public void onSuccess() {
                loadingDots.stopAnimation();
                //loadingDots.setVisibility(View.GONE);
                moveToParentTabsActivity(true);
            }
        }).execute();
        }else {
            moveToParentTabsActivity(false);
        }
    }

    private void moveToParentTabsActivity(boolean syncDone){
        Intent intent = new Intent(this, ParentTabsActivity.class);
        intent.putExtra("sync",syncDone);
        startActivity(intent);
        finish();
    }


}
