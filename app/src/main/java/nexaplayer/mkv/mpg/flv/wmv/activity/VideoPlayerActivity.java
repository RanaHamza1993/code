package nexaplayer.mkv.mpg.flv.wmv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;
import nexaplayer.mkv.mpg.flv.wmv.utils.VideoSize;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoPlayerActivity extends Activity implements IVLCVout.Callback, PopupMenu.OnMenuItemClickListener {

    public final static String TAG = "NexaVideoPlayerActivity";

    public final static String VIDEO_FILE_LIST_KEY = "VIDEO_FILE_LIST_KEY";
    public final static String VIDEO_FILE_INDEX_KEY = "VIDEO_FILE_INDEX_KEY";

    public final static int SELECT_SUBTITLE_REQUEST_CODE = 1001;

    private String mFilePath;


    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;
    // subtitle surface
    private SurfaceView subtitleSurface;
    private SurfaceHolder subtitleHolder;

    private SharedPreferenceManager sharedPreferenceManager;
    private RealmManager realmManager;

    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    private static long SEEK_PERIOD = 10000;
    private static long PREVIOUS_PERIOD = 5000;
    private static long TEN_MINUTES_IN_MILLISECONDS = 10 * 60 * 1000;
    private static long SECOND_TO_MICROSECONDS = 1000 * 1000;

    private List<String> mediaList;
    private int currentMedia;
    boolean isControllerLocked;
    private int audioDelay = 0;
    private boolean isAudioDelayChanged = false;
    private VideoSize screenSize;

    // control overlay
    private FrameLayout vlcOverlay;

    private ImageView vlcButtonPlayPause;
    private ImageView vlcButtonPrevious;
    private ImageView vlcButtonNext;
    private ImageView vlcButtonForwad;
    private ImageView vlcButtonRewind;
    private ImageView vlcButtonBrightnessLow;
    private ImageView vlcButtonBrightnessHigh;
    private ImageView vlcButtonVolumeLow;
    private ImageView vlcButtonVolumeHigh;
    private ImageView vlcButtonLocked;
    private ImageView vlcButtonCaptionClosed;
    private ImageView vlcButtonMusicPlayer;
    private ImageView vlcButtonFullScreen;
    private ImageView vlcButtonPlayerOptions;

    private Handler handlerOverlay;
    private Runnable runnableOverlay;
    private Handler handlerSeekbar;
    private Runnable runnableSeekbar;
    private SeekBar vlcSeekbar;
    private TextView vlcCurrentDuration;
    private TextView vlcTotalDuration;
    private TextView overlayTitle;
    private InterstitialAd mInterstitialAd;


    private VideoOrientationChangeListener orientationChangeListener;

    /*************
     * Activity
     *************/

    /*configuration changes*/
    private final static String CURRENT_ITEM = "CURRENT_ITEM";
    private final static String ITEM_POSITION = "ITEM_POSITION";
    private long itemPosition;
    private boolean isResolutionChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(VideoPlayerActivity.class.getName(), "onCreate");
        setContentView(R.layout.activity_video_player);
        // Receive path to play from intent
        initializeData();
        initializeViews();
        if (savedInstanceState != null) {
            currentMedia = savedInstanceState.getInt(CURRENT_ITEM);
            itemPosition = savedInstanceState.getLong(ITEM_POSITION);
            mFilePath = mediaList.get(currentMedia);
            isResolutionChanged = true;
        } else {
            isResolutionChanged = false;
        }
        intializeAdMob();
    }

    private void intializeAdMob() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5281860984128274/3771868405");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_ITEM, currentMedia);
        outState.putLong(ITEM_POSITION, mMediaPlayer.getTime());
        savePreviousPlayTimeInDatabase(mediaList.get(currentMedia), 0);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initializeData() {
        Intent intent = getIntent();
        String[] fileList = intent.getStringArrayExtra(VIDEO_FILE_LIST_KEY);
        currentMedia = intent.getIntExtra(VIDEO_FILE_INDEX_KEY, 0);
        mediaList = Arrays.asList(fileList);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        realmManager = RealmManager.getInstance();

        mFilePath = mediaList.get(currentMedia);
        Log.d(TAG, "Playing back " + mFilePath);

        orientationChangeListener = new VideoOrientationChangeListener(this);
        screenSize = VideoSize.NORMAL;
    }

    public void initializeViews() {

        mSurface = findViewById(R.id.surface);
        holder = mSurface.getHolder();
        subtitleSurface = findViewById(R.id.subtitle_surface);
        subtitleHolder = subtitleSurface.getHolder();
        subtitleHolder.setFormat(PixelFormat.RGBA_8888);
        subtitleSurface.setZOrderMediaOverlay(true);

        // OVERLAY / CONTROLS
        vlcOverlay = findViewById(R.id.vlc_overlay);

        vlcButtonPlayPause = findViewById(R.id.vlc_button_play_pause);
        vlcButtonNext = findViewById(R.id.vlc_next);
        vlcButtonPrevious = findViewById(R.id.vlc_previous);
        vlcButtonForwad = findViewById(R.id.vlc_forward);
        vlcButtonRewind = findViewById(R.id.vlc_rewind);
        vlcButtonBrightnessHigh = findViewById(R.id.video_brightness_high);
        vlcButtonBrightnessLow = findViewById(R.id.video_brightness_low);
        vlcButtonVolumeHigh = findViewById(R.id.video_volume_high);
        vlcButtonVolumeLow = findViewById(R.id.video_volume_low);
        vlcButtonMusicPlayer = findViewById(R.id.video_musicplayer);
        vlcButtonFullScreen = findViewById(R.id.video_fullscreen);
        vlcButtonLocked = findViewById(R.id.video_lock);
        vlcButtonCaptionClosed = findViewById(R.id.video_caption);
        vlcButtonPlayerOptions = findViewById(R.id.btn_video_options);

        vlcSeekbar = findViewById(R.id.vlc_seekbar);
        vlcTotalDuration = findViewById(R.id.vlc_total_duration);
        vlcCurrentDuration = findViewById(R.id.vlc_current_duration);
        overlayTitle = findViewById(R.id.vlc_overlay_title);
    }

    private void initializeListeners() {
        vlcButtonRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isControllerLocked) {
                    seekBackward();
                }
            }
        });

        vlcButtonForwad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isControllerLocked) {
                    seekForward();
                }
            }
        });

        vlcButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isControllerLocked) {
                    selectPrevious();
                }
            }
        });
        vlcButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isControllerLocked) {
                    selectNext();
                }
            }
        });

        vlcButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isControllerLocked) {
                    if (mMediaPlayer != null)
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                            vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_black));
                        } else {
                            mMediaPlayer.play();
                            vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black));
                        }
                }
            }
        });

        vlcOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isControllerLocked) {
                    if (mMediaPlayer.isPlaying()) {
                        vlcOverlay.setVisibility(View.GONE);
                    }
                }
            }
        });

        vlcButtonLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isControllerLocked = !isControllerLocked;
                setUpLockButton();
            }
        });


        vlcButtonMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioTracks();
            }
        });

        vlcButtonCaptionClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null && !isControllerLocked) {
                    addSubtitles();
                }
            }
        });

        vlcButtonPlayerOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null && !isControllerLocked) {
                    showVideoOptions();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       // setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UtilityMethods.reportGoogleAnalytics(VideoPlayerActivity.class.getName(), "onStart");
        createPlayer(mFilePath);
        initBrightnessTouch();
        orientationChangeListener.enable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilityMethods.reportGoogleAnalytics(VideoPlayerActivity.class.getName(), "onResume");
        initializeListeners();
        mMediaPlayer.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UtilityMethods.reportGoogleAnalytics(VideoPlayerActivity.class.getName(), "onPause");
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        itemPosition = mMediaPlayer.getTime();
        isResolutionChanged = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            long totalDuration = mMediaPlayer.getMedia().getDuration();
            long currentDuration = mMediaPlayer.getTime();
            if (totalDuration > TEN_MINUTES_IN_MILLISECONDS && (totalDuration - currentDuration) > PREVIOUS_PERIOD) {
                savePreviousPlayTimeInDatabase(mediaList.get(currentMedia), mMediaPlayer.getTime());
            } else {
                savePreviousPlayTimeInDatabase(mediaList.get(currentMedia), 0);
            }
        }
        UtilityMethods.reportGoogleAnalytics(VideoPlayerActivity.class.getName(), "onDestroy");
        releasePlayer();
        orientationChangeListener.disable();
    }


    /*************
     * Surface
     *************/
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if (holder == null || mSurface == null)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float screenAR = (float) w / (float) h;
        float videoAR = (float) mVideoWidth / (float) mVideoHeight;

        switch (screenSize.getTitle()){
            case AppConstants.VIDEO_SIZE_RATIO43:
                videoAR = (float) 4 / (float) 3;
                break;
            case AppConstants.VIDEO_SIZE_RATION169:
                videoAR = (float) 16 / (float) 9;
                break;
            case AppConstants.VIDEO_SIZE_FITSCREEN:
                videoAR = (float) w / (float) h;
                break;
            default:
                videoAR = (float) mVideoWidth / (float) mVideoHeight;
                break;
        }


        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        subtitleHolder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    /*************
     * Player
     *************/

    private void createPlayer(String media) {
        releasePlayer();
        setAudioDelay(0);
        setupControls();
        try {
            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            // options.add("--subsdec-encoding <encoding>");
            options.add("--aout=all");
            options.add("-vvv"); // verbosity
            libvlc = new LibVLC(this);
            if (holder == null) {
                holder = mSurface.getHolder();
            }
            if (subtitleHolder == null) {
                subtitleHolder = subtitleSurface.getHolder();
                subtitleHolder.setFormat(PixelFormat.RGBA_8888);
            }
            holder.setKeepScreenOn(true);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            vout.setSubtitlesView(subtitleSurface);
            vout.addCallback(this);
            vout.attachViews();
            Media m = new Media(libvlc, Uri.parse(media).getPath());
            mMediaPlayer.setMedia(m);
        } catch (Exception e) {
            Toast.makeText(this, "Error creating player!", Toast.LENGTH_LONG).show();
        }

    }

    // TODO: handle this cleaner
    private void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        if (!isAudioDelayChanged) {
            vlcSeekbar.setProgress(0);
        }
        vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_black));
        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    /*************
     * Events
     *************/

    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

//    @Override
//    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
//        if (width * height == 0)
//            return;
//        // store video size
//        mVideoWidth = width;
//        mVideoHeight = height;
//        setSize(mVideoWidth, mVideoHeight);
//    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {

    }

//    @Override
//    public void onHardwareAccelerationError(IVLCVout ivlcVout) {
//        // Handle errors with hardware acceleration
//        Log.e(TAG, "Error with hardware acceleration");
//        this.releasePlayer();
//
//        Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show();
//
//    }

    public void playStarted() {
        mMediaPlayer.setAudioDelay(getAudioDelay() * 1000 * 1000);

        String subtitleFile = realmManager.getVideoItemSubtitleFile(mediaList.get(currentMedia));
        if (!UtilityMethods.isEmptyString(subtitleFile) && mMediaPlayer.getSpuTracks() == null) {
            //   mMediaPlayer.addSlave(Media.Slave.Type.Subtitle, subtitleFile, true);
//            mMediaPlayer.setSubtitleFile(subtitleFile);
        }
        setUpLockButton();

        vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black));
        vlcOverlay.setVisibility(View.VISIBLE);
        if (isResolutionChanged || isAudioDelayChanged) {
            mMediaPlayer.setTime(itemPosition);
            isResolutionChanged = isAudioDelayChanged = false;
            seekTo(itemPosition);
            itemPosition = 0;
            return;
        }

        if (sharedPreferenceManager.getDisplayVideoFileResume()) {
            long previousTime = getPreviousPlayTimeFromDatabase(mediaList.get(currentMedia));
            isAudioDelayChanged = false;
            if (previousTime > 0) {
                mMediaPlayer.setTime(previousTime);
                seekTo(previousTime);
            } else {
                mMediaPlayer.setTime(20);
                seekTo(20);
            }
        }
    }

    public void playPaused() {
        vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_black));
        savePreviousPlayTimeInDatabase(mediaList.get(currentMedia), mMediaPlayer.getTime());
        vlcOverlay.setVisibility(View.VISIBLE);
    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<VideoPlayerActivity> mOwner;

        public MyPlayerListener(VideoPlayerActivity owner) {
            mOwner = new WeakReference<VideoPlayerActivity>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            VideoPlayerActivity player = mOwner.get();
            Log.d(TAG, "Player EVENT");
            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    Log.d(TAG, "MediaPlayerEndReached");
                    player.selectNext();
                    break;
                case MediaPlayer.Event.EncounteredError:
                    Log.d(TAG, "Media Player Error, re-try");
                    //player.releasePlayer();
                    break;
                case MediaPlayer.Event.Playing:
                    player.playStarted();
                    break;
                case MediaPlayer.Event.Paused:
                    player.playPaused();
                    break;
                case MediaPlayer.Event.Stopped:
                    // player.releasePlayer();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (!isControllerLocked) {
            super.onBackPressed();
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    public void selectNext() {
        savePreviousPlayTimeInDatabase(mediaList.get(currentMedia), 0);
        releasePlayer();
        if (currentMedia < (mediaList.size() - 1)) {
            currentMedia++;
            String currentFilePath = mediaList.get(currentMedia);
            File file = new File(currentFilePath);
            mFilePath = file.getName();
            createPlayer(currentFilePath);
            mMediaPlayer.play();
        }
    }

    public void selectPrevious() {
        savePreviousPlayTimeInDatabase(mediaList.get(currentMedia), 0);
        if (mMediaPlayer.getTime() > PREVIOUS_PERIOD) {
            seekTo(10);
        } else if (currentMedia > 0) {
            releasePlayer();
            currentMedia--;
            String currentFilePath = mediaList.get(currentMedia);
            File file = new File(currentFilePath);
            mFilePath = file.getName();
            createPlayer(currentFilePath);
            mMediaPlayer.play();
        }
    }

    public void seekForward() {
        if (mMediaPlayer.getMedia() != null) {
            long endTime = mMediaPlayer.getMedia().getDuration();
            if (mMediaPlayer.getTime() + SEEK_PERIOD < endTime) {
                seekTo(mMediaPlayer.getTime() + SEEK_PERIOD);
            } else {
                seekTo(endTime - 10);
            }
        }
    }

    public void seekBackward() {
        if (mMediaPlayer.getMedia() != null) {
            long startTime = 0;
            if (mMediaPlayer.getTime() - SEEK_PERIOD > startTime) {
                seekTo(mMediaPlayer.getTime() - SEEK_PERIOD);
            } else {
                seekTo(startTime + 10);
            }
        }
    }

    public void seekTo(long time) {
        mMediaPlayer.setTime(time);
        long curTime = mMediaPlayer.getTime();
        int minutes = (int) (curTime / (60 * 1000));
        int seconds = (int) ((curTime / 1000) % 60);

        String duration = String.format("%02d:%02d", minutes, seconds);
        vlcSeekbar.setProgress((int) (mMediaPlayer.getPosition() * 100));
        vlcCurrentDuration.setText(duration);
    }

    private void setupControls() {

        File file = new File(mFilePath);
        overlayTitle.setText(file.getName());
        // SEEKBAR
        handlerSeekbar = new Handler();
        runnableSeekbar = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null && !isAudioDelayChanged) {
                    if (mMediaPlayer.getMedia() != null) {
                        long totalTime = mMediaPlayer.getMedia().getDuration();
                        int endMinutes = (int) (totalTime / (60 * 1000));
                        int endSeconds = (int) ((totalTime / 1000) % 60);
                        String totalDuration = String.format("%02d:%02d", endMinutes, endSeconds);
                        vlcTotalDuration.setText(totalDuration);
                    }
                    long curTime = mMediaPlayer.getTime();
                    int minutes = (int) (curTime / (60 * 1000));
                    int seconds = (int) ((curTime / 1000) % 60);

                    String duration = String.format("%02d:%02d", minutes, seconds);
                    vlcSeekbar.setProgress((int) (mMediaPlayer.getPosition() * 100));
                    vlcCurrentDuration.setText(duration);
                }
                handlerSeekbar.postDelayed(runnableSeekbar, 1000);
            }
        };

        vlcButtonNext.setEnabled(currentMedia < (mediaList.size() - 1));
        vlcButtonPrevious.setEnabled(currentMedia > (0));

        runnableSeekbar.run();
        vlcSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.v("NEW POS", "pos is : " + i);
//                if (i != 0)
//                 mMediaPlayer.setPosition(((float) i / 100.0f));
                if (!isAudioDelayChanged) {
                    long curTime = mMediaPlayer.getTime();
                    int minutes = (int) (curTime / (60 * 1000));
                    int seconds = (int) ((curTime / 1000) % 60);

                    String duration = String.format("%02d:%02d", minutes, seconds);
                    vlcCurrentDuration.setText(duration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        vlcSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isControllerLocked && mMediaPlayer.isPlaying()) {
                    int progress = (int) (vlcSeekbar.getMax() * motionEvent.getX() / vlcSeekbar.getWidth());
                    Log.i(TAG, "seekbarposition " + progress);
                    mMediaPlayer.setPosition(((float) progress / 100.0f));
                    long curTime = mMediaPlayer.getTime();
                    int minutes = (int) (curTime / (60 * 1000));
                    int seconds = (int) ((curTime / 1000) % 60);

                    String duration = String.format("%02d:%02d", minutes, seconds);
                    vlcCurrentDuration.setText(duration);
                    return true;
                }
                return false;
            }
        });

        vlcButtonBrightnessLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isControllerLocked) {
                    doBrightnessTouch(false);
                }
            }
        });
        vlcButtonBrightnessHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isControllerLocked) {
                    doBrightnessTouch(true);
                }
            }
        });

        vlcButtonVolumeLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isControllerLocked) {
                    doVolumeTouch(false);
                }
            }
        });
        vlcButtonVolumeHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isControllerLocked) {
                    doVolumeTouch(true);
                }
            }
        });

        vlcButtonFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isControllerLocked) {
                    changeScreenSize();
                }
            }
        });


        // OVERLAY
        handlerOverlay = new Handler();
        runnableOverlay = new Runnable() {
            @Override
            public void run() {
                vlcOverlay.setVisibility(View.GONE);
                toggleFullscreen(true);
            }
        };
        final long timeToDisappear = 3000;
        handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
        mSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vlcOverlay.setVisibility(View.VISIBLE);

                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });
        subtitleSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vlcOverlay.setVisibility(View.VISIBLE);

                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });
    }

    private void toggleFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            mSurface.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            subtitleSurface.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }

    private void savePreviousPlayTimeInDatabase(String itemPath, long previousTime) {
        realmManager.saveVideoItemPreviousDuration(itemPath, previousTime);
    }

    private long getPreviousPlayTimeFromDatabase(String itemPath) {
        return realmManager.getVideoItemPreviousDuration(itemPath);
    }

    private void initBrightnessTouch() {
        float brightnesstemp = 0.01f;
        // Initialize the layoutParams screen brightness
        try {
            brightnesstemp = android.provider.Settings.System.getInt(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = brightnesstemp;
            getWindow().setAttributes(lp);
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void doBrightnessTouch(boolean isHigh) {
        // Set delta : 0.07f is arbitrary for now, it possibly will change in the future
        float delta = 0.07f;

        // Estimate and adjust Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (isHigh) {
            lp.screenBrightness = Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1);
        } else {
            lp.screenBrightness = Math.min(Math.max(lp.screenBrightness - delta, 0.01f), 1);
        }

        // Set Brightness
        getWindow().setAttributes(lp);
    }

    private void doVolumeTouch(boolean isHigh) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int delta = 1;
        if (isHigh) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume + delta, 0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume - delta, 0);
        }
    }

    private void setUpLockButton() {
        vlcButtonLocked.setActivated(isControllerLocked);
        vlcButtonLocked.setSelected(isControllerLocked);
    }

    private void toggleOrientation(int screenOrientation) {
        if (getRequestedOrientation() != screenOrientation) {
            setRequestedOrientation(screenOrientation);
        }
    }

    private void showAudioTracks() {
        final MediaPlayer.TrackDescription[] des = mMediaPlayer.getAudioTracks();
        if (des == null) {
            UtilityMethods.showToast(this, "No audio tracks found");
            return;
        }
        int selectedItem = 0;
        final String[] tracklist = new String[des.length];
        for (int i = 0; i < des.length; i++) {
            tracklist[i] = des[i].name;
            if (mMediaPlayer.getAudioTrack() == des[i].id) {
                selectedItem = i;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Audio Track");
        builder.setSingleChoiceItems(tracklist, selectedItem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.setAudioTrack(des[which].id);
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void addSubtitles() {
        PopupMenu subtitlePopup = new PopupMenu(this, vlcButtonCaptionClosed);
        subtitlePopup.inflate(R.menu.subtitle_menu);
        subtitlePopup.setOnMenuItemClickListener(this);
        subtitlePopup.show();
    }

    private void showVideoOptions() {
        PopupMenu videoOptionsPopup = new PopupMenu(this, vlcButtonPlayerOptions);
        videoOptionsPopup.inflate(R.menu.video_options_menu);
        videoOptionsPopup.setOnMenuItemClickListener(this);
        videoOptionsPopup.show();
    }

    private void changeScreenSize(){
        switch (screenSize.getTitle()){
            case AppConstants.VIDEO_SIZE_NORMAL:
                screenSize = VideoSize.RATIO43;
                break;
            case AppConstants.VIDEO_SIZE_RATIO43:
                screenSize = VideoSize.RATIO169;
                break;
            case AppConstants.VIDEO_SIZE_RATION169:
                screenSize = VideoSize.FITSCREEN;
                break;
            default:
                screenSize = VideoSize.NORMAL;
                break;
        }
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subtitle_track:
                showSubtitleTracks();
                return true;
            case R.id.subtitle_file:
                selectSubtitleFile();
                return true;
            case R.id.audio_delay:
                itemPosition = mMediaPlayer.getTime();
                showAudioDelayDialog();
                return true;
        }
        return false;
    }


    private void showSubtitleTracks() {
        final MediaPlayer.TrackDescription[] des = mMediaPlayer.getSpuTracks();
        if (des == null) {
            UtilityMethods.showToast(this, "No subtitles found");
            return;
        }
        int selectedItem = 0;
        final String[] tracklist = new String[des.length];
        for (int i = 0; i < des.length; i++) {
            tracklist[i] = des[i].name;
            if (mMediaPlayer.getSpuTrack() == des[i].id) {
                selectedItem = i;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Subtitle Track");
        builder.setSingleChoiceItems(tracklist, selectedItem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.setSpuTrack(des[which].id);
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();

    }

    private void showAudioDelayDialog() {
        final int minValue = -20;
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(40);
        numberPicker.setValue(getAudioDelay() - minValue);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return Integer.toString(value + minValue);
            }
        });
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setAudioDelay(newVal + minValue);

            }
        });
        isAudioDelayChanged = true;
        createPlayer(mFilePath);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.audio_delay);
        builder.setView(numberPicker);
        builder.create().show();
    }

    private void selectSubtitleFile() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.STORAGE_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = AppConstants.SUBTITLES_FILE_TYPE;

        FilePickerDialog dialog = new FilePickerDialog(this, properties);
        dialog.setTitle("Select subtitle file");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null) {
                    String subtitleFileName = files[0];
                    realmManager.saveVideoItemSubtitleFile(mediaList.get(currentMedia), subtitleFileName);
                    //  mMediaPlayer.addSlave(Media.Slave.Type.Subtitle, subtitleFileName, true);
//                    mMediaPlayer.setSubtitleFile(subtitleFileName);
                }
            }
        });

        dialog.show();

    }

    private void setAudioDelay(int delay) {
        this.audioDelay = delay;
    }

    private int getAudioDelay() {
        return this.audioDelay;
    }

    class VideoOrientationChangeListener extends OrientationEventListener{

        public VideoOrientationChangeListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            Log.i("Screen Orientation", "orientation : " + orientation);
            if (!isControllerLocked) {
                int screenOrientation = getRequestedOrientation();
                if((orientation <35 || orientation > 325)){
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
                else if(orientation > 145 && orientation < 215){
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                }
                else if(orientation >= 235 && orientation <= 305){
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }
                else if(orientation >= 55 && orientation < 125){
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                }
                toggleOrientation(screenOrientation);
            }
        }
    }

}
