package nexaplayer.mkv.mpg.flv.wmv.activity;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.tabs.AudioTabsActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.tabs.DocumentTabsActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.tabs.FavoriteTabsActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.tabs.ImageTabsActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.tabs.SearchTabsActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.tabs.VideoTabsActivity;
import nexaplayer.mkv.mpg.flv.wmv.listeners.PreviousTabItemTypeListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.SuccessCallback;
import nexaplayer.mkv.mpg.flv.wmv.service.InitService;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllSupoortedFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.PermissionUtils;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

public class ParentTabsActivity extends TabActivity {

    private TabHost tabHost;
    private TabWidget tabWidget;
    private FrameLayout tabContent;
    private String previousTabId;
    private PreviousTabItemTypeListener listener;
    private View progressBar;
    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter intentFilterAction;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(ParentTabsActivity.class.getName(), "onCreate");
        setContentView(R.layout.activity_parent_tabs);
        initializeData();
        initializeTabs();

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.app_admob_app_id));
        intiliazeAdMob();

        boolean syncDone=getIntent().getBooleanExtra("sync",false);
        PermissionUtils.checkAndRequestPermissions(this);
        /*if (!syncDone&&PermissionUtils.checkAndRequestPermissions(this)) {
            Intent intent=new Intent(ParentTabsActivity.this,InitService.class);
            ParentTabsActivity.this.startService(intent);
        }*/
    }

    private void intiliazeAdMob() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener(){

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

//                Toast.makeText(ParentTabsActivity.this, "Ad failed to load", Toast.LENGTH_SHORT).show();
                Log.d("Ads", "onAdFailedToLoad: "+i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

//                Toast.makeText(ParentTabsActivity.this, "Ad loaded.", Toast.LENGTH_SHORT).show();

                Log.d("Ads", "onAdLoaded");
            }

        });

    }

    private void loadAllFilesFromStorage(){
        progressBar.setVisibility(View.VISIBLE);
        new FindAllSupoortedFilesAsyncTask(this, new SuccessCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }
        }).execute();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (PermissionUtils.RequestCodeResult(this, requestCode, permissions, grantResults)) {
            //Intent intent=new Intent(ParentTabsActivity.this,InitService.class);
            //ParentTabsActivity.this.startService(intent);
        }

    }
    private void initializeTabs() {
        setupTabs();
        initializeListeners();
        tabHost.setCurrentTab(3);
        tabHost.setCurrentTab(2);
        tabHost.setCurrentTab(1);
        tabHost.setCurrentTab(0);
    }

    @Override
    public void onBackPressed() {
        Log.v("wasim","onBackPressed tab actitity");
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myBroadcastReceiver, intentFilterAction);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myBroadcastReceiver!=null)
            unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeData() {
        tabHost = findViewById(android.R.id.tabhost); // initiate TabHost
        tabWidget = findViewById(android.R.id.tabs); // initiate TabHost
        progressBar=findViewById(R.id.progressBar);

        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilterAction = new IntentFilter();
        intentFilterAction.addAction(InitService.ACTION_OPERATION_START);
        intentFilterAction.addAction(InitService.ACTION_UPDATE_PROGRESS);
        intentFilterAction.addAction(InitService.ACTION_OPERATION_DONE);
    }

    private void setupTabs() {
        createTab(AppConstants.TAB_AUDIO, AudioTabsActivity.class, R.drawable.audio_tab_icon);
        createTab(AppConstants.TAB_VIDEO, VideoTabsActivity.class, R.drawable.video_tab_icon);
        createTab(AppConstants.TAB_IMAGES, ImageTabsActivity.class, R.drawable.image_tab_icon);
        createTab(AppConstants.TAB_APPS, DocumentTabsActivity.class, R.drawable.apps_tab_icon);
        createTab(AppConstants.TAB_SEARCH, SearchTabsActivity.class, R.drawable.search_tab_icon);
        createTab(AppConstants.TAB_FAVORITE, FavoriteTabsActivity.class, R.drawable.favorite_tabs_icon);
    }

    private void createTab(String tabId, Class tabClass, int drawableId) {
        TabHost.TabSpec spec = tabHost.newTabSpec(tabId); // Create a new TabSpec using tab host
        spec.setIndicator("", getResources().getDrawable(drawableId));
        // Create an Intent to launch an Activity for the tab (to be reused)
        Intent intent = new Intent(this, tabClass);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    private void initializeListeners() {

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case AppConstants.TAB_AUDIO:
                        previousTabId = AppConstants.ITEM_TYPE_AUDIO;
                        break;
                    case AppConstants.TAB_VIDEO:
                        previousTabId = AppConstants.ITEM_TYPE_VIDEO;
                        break;
                    case AppConstants.TAB_IMAGES:
                        previousTabId = AppConstants.ITEM_TYPE_IMAGE;
                        break;
                    case AppConstants.TAB_APPS:
                        previousTabId = AppConstants.ITEM_TYPE_DOCUMENT;
                        break;
                    case AppConstants.TAB_SEARCH:
                        listener = (PreviousTabItemTypeListener) getCurrentActivity();
                        listener.setPreviousTabItemType(getPreviousTabId());
                        break;
                    case AppConstants.TAB_MORE:
                        break;
                }
            }
        });
    }

    public String getPreviousTabId() {
        if (!UtilityMethods.isEmptyString(previousTabId)) {
            return previousTabId;
        } else {
            return AppConstants.ITEM_TYPE_AUDIO;
        }

    }
    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("wasim", "ACTION_CODE:" + intent.getAction());
            switch (intent.getAction()) {
                case InitService.ACTION_OPERATION_START:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case InitService.ACTION_UPDATE_PROGRESS:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case InitService.ACTION_OPERATION_DONE:
                    progressBar.setVisibility(View.GONE);
                    break;

            }
        }
    }
}
