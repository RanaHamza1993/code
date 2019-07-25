package nexaplayer.mkv.mpg.flv.wmv.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.service.AudioPlayerService;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

public class AudioPlayerActivity extends Activity {

    private ImageView vlcButtonPlayPause;
    private ImageView vlcButtonPrevious;
    private ImageView vlcButtonNext;
    private ImageView vlcButtonForwad;
    private ImageView vlcButtonRewind;
    private SeekBar vlcSeekbar;
    private TextView vlcCurrentDuration;
    private TextView vlcTotalDuration;
    private TextView overlayTitle;


    private String[] audioItems;
    boolean serviceBound = false;
    AudioActivityBroadCastReceiver audioActivityBroadCastReceiver;

    public final static String TAG = "NexaAudioPlayerActivity";

    public final static String AUDIO_FILE_LIST_KEY = "AUDIO_FILE_LIST_KEY";
    public final static String AUDIO_FILE_INDEX_KEY = "AUDIO_FILE_INDEX_KEY";
    public final static String IS_AUDIO_FILE_OPEN = "IS_AUDIO_FILE_OPEN";

    private int currentIndex;
    private boolean isAudioFileOpen;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(AudioPlayerActivity.class.getName(), "onCreate");
        setContentView(R.layout.activity_audio_player);
        initializeData();
        initializeViews();
        playAudio();

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.app_admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

//                Toast.makeText(AudioPlayerActivity.this, "Ad failed to load", Toast.LENGTH_SHORT).show();
                Log.d("Ads", "onAdFailedToLoad: " + i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

//                Toast.makeText(AudioPlayerActivity.this, "Ad loaded.", Toast.LENGTH_SHORT).show();

                Log.d("Ads", "onAdLoaded");
            }

        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    private void initializeData() {
        Intent intent = getIntent();
        audioItems = intent.getStringArrayExtra(AUDIO_FILE_LIST_KEY);
        currentIndex = intent.getIntExtra(AUDIO_FILE_INDEX_KEY, 0);
        isAudioFileOpen = intent.getBooleanExtra(IS_AUDIO_FILE_OPEN, false);
        audioActivityBroadCastReceiver = new AudioActivityBroadCastReceiver(AudioPlayerActivity.this);
    }

    private void initializeViews() {
        vlcButtonPlayPause = findViewById(R.id.vlc_button_play_pause);
        vlcButtonNext = findViewById(R.id.vlc_next);
        vlcButtonPrevious = findViewById(R.id.vlc_previous);
        vlcButtonForwad = findViewById(R.id.vlc_forward);
        vlcButtonRewind = findViewById(R.id.vlc_rewind);

        vlcSeekbar = findViewById(R.id.vlc_seekbar);
        vlcTotalDuration = findViewById(R.id.vlc_total_duration);
        vlcCurrentDuration = findViewById(R.id.vlc_current_duration);
        overlayTitle = findViewById(R.id.vlc_overlay_title);
    }

    private void initializeListeners() {
        vlcButtonRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rewindIntent = new Intent(AppConstants.AUDIOSERVICE_BROADCAST_REWIND);
                sendBroadcast(rewindIntent);

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

            }
        });

        vlcButtonForwad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forwardIntent = new Intent(AppConstants.AUDIOSERVICE_BROADCAST_FORWARD);
                sendBroadcast(forwardIntent);
            }
        });

        vlcButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent previousIntent = new Intent(AppConstants.AUDIOSERVICE_BROADCAST_PREVIOUS);
                sendBroadcast(previousIntent);
            }
        });
        vlcButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(AppConstants.AUDIOSERVICE_BROADCAST_NEXT);
                sendBroadcast(nextIntent);
            }
        });

        vlcButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playIntent = new Intent(AppConstants.AUDIOSERVICE_BROADCAST_PLAY_PAUSE);
                sendBroadcast(playIntent);
            }
        });

        vlcSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int progress = (int) (vlcSeekbar.getMax() * motionEvent.getX() / vlcSeekbar.getWidth());
                Intent playIntent = new Intent(AppConstants.AUDIOSERVICE_BROADCAST_SEEKBAR_POSITION);
                playIntent.putExtra(AppConstants.PROGRESS_KEY, progress);
                sendBroadcast(playIntent);
                return true;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", serviceBound);
        outState.putInt("currentIndex", currentIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilityMethods.reportGoogleAnalytics(AudioPlayerActivity.class.getName(), "onResume");
        registerReceiver(audioActivityBroadCastReceiver, getIntentFilter());
        initializeListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(audioActivityBroadCastReceiver);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
        currentIndex = savedInstanceState.getInt("currentIndex");
    }


    private void playAudio() {

        if (isAudioFileOpen) {
            UtilityMethods.reportGoogleAnalytics(AudioPlayerActivity.class.getName(), "playAudio: selecteditem");
            UtilityMethods.stopAudioService(this);
            Intent playerIntent = new Intent(this, AudioPlayerService.class);
            playerIntent.putExtra(AUDIO_FILE_LIST_KEY, audioItems);
            playerIntent.putExtra(AUDIO_FILE_INDEX_KEY, currentIndex);
            playerIntent.putExtra(IS_AUDIO_FILE_OPEN, isAudioFileOpen);
            startService(playerIntent);
        } else {
            if (!UtilityMethods.isServiceRunning(this)) {
                UtilityMethods.reportGoogleAnalytics(AudioPlayerActivity.class.getName(), "service not running");
                UtilityMethods.stopAudioService(this);
                Intent playerIntent = new Intent(this, AudioPlayerService.class);
                playerIntent.putExtra(AUDIO_FILE_LIST_KEY, audioItems);
                playerIntent.putExtra(AUDIO_FILE_INDEX_KEY, currentIndex);
                playerIntent.putExtra(IS_AUDIO_FILE_OPEN, isAudioFileOpen);
                startService(playerIntent);
            }
            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
        }
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent(AppConstants.AUDIOSERVICE_BROADCAST_DESTROY);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    public void updateName(String fileName) {
        overlayTitle.setText(fileName);
    }

    public void updateCurrentTime(long currentTime) {
        int minutes = (int) ((float) currentTime / (60 * 1000));
        int seconds = (int) ((float) (currentTime / 1000) % 60);
        String duration = String.format("%02d:%02d", minutes, seconds);
        vlcCurrentDuration.setText(duration);

    }

    public void updateTotalTime(long totalTime) {
        int endMinutes = (int) (totalTime / (60 * 1000));
        int endSeconds = (int) ((totalTime / 1000) % 60);
        String totalDuration = String.format("%02d:%02d", endMinutes, endSeconds);
        vlcTotalDuration.setText(totalDuration);
    }

    public void updateCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        vlcButtonNext.setEnabled(currentIndex < (audioItems.length - 1));
        vlcButtonPrevious.setEnabled(currentIndex > 0);
    }

    public void updateCurrentProgress(int position) {
        vlcSeekbar.setProgress(position);
    }

    public void onPlayerPause() {
        vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
    }

    public void onPlayerPlay() {
        vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
    }

    public void onPlayerStop() {
        onPlayerPause();
        updateCurrentTime(0);
        currentIndex = 0;
    }

    public class AudioActivityBroadCastReceiver extends BroadcastReceiver {

        AudioPlayerActivity player;

        public AudioActivityBroadCastReceiver(AudioPlayerActivity player) {
            this.player = player;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_INDEX:
                    player.updateCurrentIndex(intent.getIntExtra(AppConstants.CURRENT_INDEX_KEY, currentIndex));
                    break;
                case AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_TIME:
                    player.updateCurrentTime(intent.getLongExtra(AppConstants.CURRENT_TIME_KEY, 0));
                    break;
                case AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_NAME:
                    player.updateName(intent.getStringExtra(AppConstants.NAME_KEY));
                    break;
                case AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_PROGRESS:
                    player.updateCurrentProgress(intent.getIntExtra(AppConstants.PROGRESS_KEY, 0));
                    break;
                case AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_TOTAL_DURATION:
                    player.updateTotalTime(intent.getLongExtra(AppConstants.TOTAL_DURATION_KEY, 0));
                    break;
                case AppConstants.AUDIOACTIVITY_BROADCAST_PAUSE:
                    player.onPlayerPause();
                    break;
                case AppConstants.AUDIOACTIVITY_BROADCAST_PLAY:
                    player.onPlayerPlay();
                    break;
                case AppConstants.AUDIOACTIVITY_BROADCAST_STOP:
                    player.onPlayerStop();
                    break;
            }

        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_INDEX);
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_TIME);
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_NAME);
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_PROGRESS);
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_TOTAL_DURATION);
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_PAUSE);
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_PLAY);
        filter.addAction(AppConstants.AUDIOACTIVITY_BROADCAST_STOP);
        return filter;
    }

}
