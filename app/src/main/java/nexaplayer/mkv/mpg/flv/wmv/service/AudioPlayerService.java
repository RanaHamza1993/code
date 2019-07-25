package nexaplayer.mkv.mpg.flv.wmv.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;

import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.AUDIO_FILE_INDEX_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.AUDIO_FILE_LIST_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.IS_AUDIO_FILE_OPEN;
import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.TAG;
import static nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants.AUDIOSERVICE_BROADCAST_PLAY_PAUSE;

/**
 * Created by Valdio Veliu on 16-07-11.
 */
public class AudioPlayerService extends Service implements
        AudioManager.OnAudioFocusChangeListener {

    NotificationCompat.Builder notificationBuilder;

    public enum PlaybackStatus {
        PLAYING,
        PAUSED
    }

    public static final String ACTION_PLAY = "com.valdioveliu.valdio.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.valdioveliu.valdio.audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.valdioveliu.valdio.audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.valdioveliu.valdio.audioplayer.ACTION_NEXT";
    public static final String ACTION_REWIND = "com.valdioveliu.valdio.audioplayer.ACTION_REWIND";
    public static final String ACTION_FORWARD = "com.valdioveliu.valdio.audioplayer.ACTION_FORWARD";
    public static final String ACTION_STOP = "com.valdioveliu.valdio.audioplayer.ACTION_STOP";

    private MediaPlayer mediaPlayer;
    private Handler handlerSeekbar;
    private Runnable runnableSeekbar;
    private LibVLC libvlc;
    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    private SharedPreferenceManager sharedPreferenceManager;
    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;

    private static long SEEK_PERIOD = 10000;
    private static long PREVIOUS_PERIOD = 5000;

    PlayAudioBroadcastReceiver playAudioBroadcastReceiver;

    //Used to pause/resume MediaPlayer
    private int resumePosition;
    private boolean isLoading;
    //AudioFocus
    private AudioManager audioManager;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    //List of available Audio files
    private String[] audioList;
    private int audioIndex = -1;
    private boolean isAudioItemClicked;
    private String activeAudio; //an object on the currently playing audio


    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    /**
     * Service lifecycle methods
     */
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        callStateListener();
        registerBecomingNoisyReceiver();
        playAudioBroadcastReceiver = new PlayAudioBroadcastReceiver(this);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        registerPlayNewAudio();
    }

    private void registerPlayNewAudio() {
        registerReceiver(playAudioBroadcastReceiver, getIntentFilter());
    }

    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //Load data from SharedPreferences
            audioList = intent.getStringArrayExtra(AUDIO_FILE_LIST_KEY);
            audioIndex = intent.getIntExtra(AUDIO_FILE_INDEX_KEY, 0);
            isAudioItemClicked = intent.getBooleanExtra(IS_AUDIO_FILE_OPEN, false);

            if (audioIndex != -1 && audioIndex < audioList.length) {
                //index is in a valid range
                activeAudio = audioList[audioIndex];
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                if(Build.VERSION.SDK_INT >=21) {
                    initMediaSession();
                }
                initMediaPlayer();

            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void setupControlls() {

        handlerSeekbar = new Handler();
        runnableSeekbar = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) {
                        sendCurrentIndexBroadcast();
                        sendCurrentTimeBroadcast((int) mediaPlayer.getTime());
                        sendNameBroadcast();
                        sendSeekBarProgressBroadcast();
                        sendTotalTimeBroadcast();
                        if (mediaPlayer.isPlaying()) {
                            sendPlayBroadcast();
                        } else {
                            sendPauseBroadCast();
                        }
                    }
                    handlerSeekbar.postDelayed(runnableSeekbar, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        runnableSeekbar.run();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        //    removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        exitService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitService();
    }
public void exitService(){
    removeNotification();
    if (mediaPlayer != null) {
        stopMedia();
        mediaPlayer.release();
    }
    removeAudioFocus();
    //Disable the PhoneStateListener
    if (phoneStateListener != null&&telephonyManager!=null) {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }
    //unregister BroadcastReceivers
    if (becomingNoisyReceiver!=null)
    unregisterReceiver(becomingNoisyReceiver);
    if (playAudioBroadcastReceiver!=null)
    unregisterReceiver(playAudioBroadcastReceiver);
}
    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AudioPlayerService.this;
        }
    }


    @Override
    public void onAudioFocusChange(int focusState) {

        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.play();
                mediaPlayer.setVolume(100);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(10);
                break;
        }
    }


    /**
     * AudioFocus
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    private void releasePlayer() {
        if (libvlc == null)
            return;
        mediaPlayer.stop();
        if (libvlc != null) {
            libvlc.release();
        }
        libvlc = null;

    }


    /**
     * MediaPlayer actions
     */
    private void initMediaPlayer() {
        try {
            releasePlayer();
            setupControlls();
            ArrayList<String> options = new ArrayList<String>();
            // options.add("--subsdec-encoding <encoding>");
            options.add("--aout=all");
            libvlc = new LibVLC( this);
            mediaPlayer = new MediaPlayer(libvlc);//new MediaPlayer instance
            //Set up MediaPlayer event listeners
            //Reset so that the MediaPlayer is not pointing to another data source
            mediaPlayer.setEventListener(mPlayerListener);
            Media media = new Media(libvlc, audioList[audioIndex]);
            // Set the data source to the mediaFile location
            mediaPlayer.setMedia(media);
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
        if (isAudioItemClicked) {
            playMedia();
        }
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.play();
        }
        sendPlayBroadcast();
        buildNotification(PlaybackStatus.PLAYING);
        isLoading = false;
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            sendStopBroadcast();
         //   removeNotification();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = (int) mediaPlayer.getTime();
        }
        sendPauseBroadCast();
        buildNotification(PlaybackStatus.PAUSED);
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setTime(resumePosition);
            mediaPlayer.play();
        }
        sendPlayBroadcast();
        buildNotification(PlaybackStatus.PLAYING);
    }

    private void skipToNext() {
        if (!isLoading) {
            if (sharedPreferenceManager.getAudioFileShuffle()) {
                Random random = new Random();
                audioIndex = random.nextInt(audioList.length);
            } else {
                if (audioIndex == audioList.length - 1) {
                    audioIndex = 0;
                } else {
                    //get next in playlist
                    audioIndex++;
                }
            }
            activeAudio = audioList[audioIndex];
            stopAndInitMediaPlayer();
            sendCurrentIndexBroadcast();
            sendNameBroadcast();
            //    setupNotification();
        }
    }

    private void skipToPrevious() {
        if(!isLoading) {
            if (mediaPlayer.getTime() < PREVIOUS_PERIOD) {
                if (sharedPreferenceManager.getAudioFileShuffle()) {
                    Random random = new Random();
                    audioIndex = random.nextInt(audioList.length);
                } else {
                    if (audioIndex == 0) {
                        //if first in playlist
                        //set index to the last of audioList
                        audioIndex = audioList.length - 1;

                    } else {
                        //get previous in playlist
                        --audioIndex;
                    }
                }
                activeAudio = audioList[audioIndex];
                stopAndInitMediaPlayer();
                sendCurrentIndexBroadcast();
                sendNameBroadcast();
                sendCurrentTimeBroadcast((int) mediaPlayer.getTime());
                sendTotalTimeBroadcast();
                sendSeekBarProgressBroadcast();
                //      setupNotification();
            } else {
                seekTo(0);
            }
        }
    }

    public void setPosition(int progress) {
        float mediaPosition = ((float) progress / 100.0f);
        mediaPlayer.setPosition(mediaPosition);
    }

    public void seekForward() {
        if (mediaPlayer.getMedia() != null) {
            long endTime = mediaPlayer.getMedia().getDuration();
            if (mediaPlayer.getTime() + SEEK_PERIOD < endTime) {
                seekTo(mediaPlayer.getTime() + SEEK_PERIOD);
            } else {
                seekTo(endTime - 10);
            }
        }

    }

    public void seekBackward() {
        int startTime = 0;
        if (mediaPlayer.getTime() - SEEK_PERIOD > startTime) {
            seekTo(mediaPlayer.getTime() - SEEK_PERIOD);
        } else {
            seekTo(startTime + 10);
        }
    }

    public void seekTo(long time) {
        mediaPlayer.setTime(time);

        sendCurrentIndexBroadcast();
        sendNameBroadcast();
        sendCurrentTimeBroadcast(mediaPlayer.getTime());
        sendTotalTimeBroadcast();
        sendSeekBarProgressBroadcast();
    }


    private void stopAndInitMediaPlayer() {
        //Update stored index
        isLoading = true;
        stopMedia();
        //reset mediaPlayer
        releasePlayer();
        initMediaPlayer();
    }

    private void sendCurrentIndexBroadcast() {
        Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_INDEX);
        intent.putExtra(AppConstants.CURRENT_INDEX_KEY, audioIndex);
        sendBroadcast(intent);
    }

    private void sendCurrentTimeBroadcast(long currentTime) {
        Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_CURRENT_TIME);
        intent.putExtra(AppConstants.CURRENT_TIME_KEY, currentTime);
        sendBroadcast(intent);
    }

    private void sendNameBroadcast() {
        File file = new File(audioList[audioIndex]);
        Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_NAME);
        intent.putExtra(AppConstants.NAME_KEY, file.getName());
        sendBroadcast(intent);
    }

    private void sendSeekBarProgressBroadcast() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getMedia() != null) {
                int progress = (int) ((float) (mediaPlayer.getTime() * 100) / mediaPlayer.getMedia().getDuration());
                Log.i(TAG, "Seek Progress " + progress);
                Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_PROGRESS);
                intent.putExtra(AppConstants.PROGRESS_KEY, progress);
                sendBroadcast(intent);
            }
        }
    }

    private void sendTotalTimeBroadcast() {
        if (mediaPlayer.getMedia() != null) {
            long totalTime = mediaPlayer.getMedia().getDuration();
            Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_UPDATE_TOTAL_DURATION);
            intent.putExtra(AppConstants.TOTAL_DURATION_KEY, totalTime);
            sendBroadcast(intent);
        }
    }

    private void sendPauseBroadCast() {
        Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_PAUSE);
        sendBroadcast(intent);
    }

    private void sendPlayBroadcast() {
        Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_PLAY);
        sendBroadcast(intent);
    }

    private void sendStopBroadcast() {
        Intent intent = new Intent(AppConstants.AUDIOACTIVITY_BROADCAST_PLAY);
        sendBroadcast(intent);
    }

    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     * Handle PhoneState changes
     */
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * MediaSession and Notification actions
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();

                resumeMedia();
            }

            @Override
            public void onPause() {
                super.onPause();

                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                //       removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    public void playPauseMedia() {
        if (mediaPlayer.isPlaying()) {
            pauseMedia();
        } else {
            resumeMedia();
        }
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_video_blue); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .build());
    }

    private void buildNotification(PlaybackStatus playbackStatus) {

        /**
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         */

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.ic_pause_black;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.ic_play_black;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher); //replace with your own image
        try {
            // Create a new Notification
            notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    // Hide the timestamp
                    .setShowWhen(false)
                    .setOngoing(true)
                    // Set the Notification style
                    // Set the Notification color
                    .setColor(getResources().getColor(R.color.colorTextGrey))
                    // Set the large and small icons
                    .setLargeIcon(largeIcon)
                    .setContentTitle((new File(audioList[audioIndex])).getName())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    // Add playback actions
                    .setContentIntent(openAudioPlayer())
                    .addAction(R.drawable.ic_previous_audio, "previous", playbackAction(3))
                    .addAction(R.drawable.ic_rewind, "rewind", playbackAction(4))
                    .addAction(notificationAction, "pause", play_pauseAction)
                    .addAction(R.drawable.ic_forward, "forward", playbackAction(5))
                    .addAction(R.drawable.ic_next_audio, "next", playbackAction(2));

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private PendingIntent openAudioPlayer(){
        Intent audioPlayerIntent = new Intent(this, AudioPlayerActivity.class);
        audioPlayerIntent.putExtra(AUDIO_FILE_LIST_KEY, audioList);
        audioPlayerIntent.putExtra(AUDIO_FILE_INDEX_KEY, audioIndex);
        audioPlayerIntent.putExtra(IS_AUDIO_FILE_OPEN, false);
        return PendingIntent.getActivity(this, 0, audioPlayerIntent, 0);
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent();
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(AppConstants.AUDIOSERVICE_BROADCAST_PLAY_PAUSE);
                return PendingIntent.getBroadcast(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(AppConstants.AUDIOSERVICE_BROADCAST_PLAY_PAUSE);
                return PendingIntent.getBroadcast(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(AppConstants.AUDIOSERVICE_BROADCAST_NEXT);
                return PendingIntent.getBroadcast(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(AppConstants.AUDIOSERVICE_BROADCAST_PREVIOUS);
                return PendingIntent.getBroadcast(this, actionNumber, playbackAction, 0);
            case 4:
                // Rewind
                playbackAction.setAction(AppConstants.AUDIOSERVICE_BROADCAST_REWIND);
                return PendingIntent.getBroadcast(this, actionNumber, playbackAction, 0);
            case 5:
                // Forward
                playbackAction.setAction(AppConstants.AUDIOSERVICE_BROADCAST_FORWARD);
                return PendingIntent.getBroadcast(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_REWIND)) {
            seekBackward();
        } else if (actionString.equalsIgnoreCase(ACTION_FORWARD)) {
            seekForward();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }


    /**
     * Play new Audio
     */
    private class PlayAudioBroadcastReceiver extends BroadcastReceiver {
        AudioPlayerService player;

        public PlayAudioBroadcastReceiver(AudioPlayerService player) {
            this.player = player;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case AUDIOSERVICE_BROADCAST_PLAY_PAUSE:
                    player.playPauseMedia();
                    break;
                case AppConstants.AUDIOSERVICE_BROADCAST_REWIND:
                    player.seekBackward();
                    break;
                case AppConstants.AUDIOSERVICE_BROADCAST_FORWARD:
                    player.seekForward();
                    break;
                case AppConstants.AUDIOSERVICE_BROADCAST_PREVIOUS:
                    player.skipToPrevious();
                    break;
                case AppConstants.AUDIOSERVICE_BROADCAST_NEXT:
                    player.skipToNext();
                    break;
                case AppConstants.AUDIOSERVICE_BROADCAST_SEEKBAR_POSITION:
                    player.setPosition(intent.getIntExtra(AppConstants.PROGRESS_KEY, 0));
                    break;
                case AppConstants.AUDIOSERVICE_BROADCAST_PLAY_CURRENT_POSITION:
                    audioIndex = intent.getIntExtra(AppConstants.CURRENT_INDEX_KEY, 0);
                    stopAndInitMediaPlayer();
                    break;
                case AppConstants.AUDIOSERVICE_BROADCAST_DESTROY:
                    if (!mediaPlayer.isPlaying()) {
                        UtilityMethods.stopAudioService(context);
                    }
                    break;
            }

            //Get the new media index form SharedPreferences
            if (audioIndex != -1 && audioIndex < audioList.length) {
                //index is in a valid range
                activeAudio = audioList[audioIndex];
            } else {
                stopSelf();
            }

//            //A PLAY_NEW_AUDIO action received
//            //reset mediaPlayer to play the new Audio
//            stopMedia();
//            mediaPlayer.reset();
//            initMediaPlayer();
//            updateMetaData();
//            buildNotification(PlaybackStatus.PLAYING);
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AUDIOSERVICE_BROADCAST_PLAY_PAUSE);
        filter.addAction(AppConstants.AUDIOSERVICE_BROADCAST_REWIND);
        filter.addAction(AppConstants.AUDIOSERVICE_BROADCAST_FORWARD);
        filter.addAction(AppConstants.AUDIOSERVICE_BROADCAST_PREVIOUS);
        filter.addAction(AppConstants.AUDIOSERVICE_BROADCAST_NEXT);
        filter.addAction(AppConstants.AUDIOSERVICE_BROADCAST_DESTROY);
        filter.addAction(AppConstants.AUDIOSERVICE_BROADCAST_SEEKBAR_POSITION);
        filter.addAction(AppConstants.AUDIOSERVICE_BROADCAST_PLAY_CURRENT_POSITION);
        return filter;
    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<AudioPlayerService> mOwner;

        public MyPlayerListener(AudioPlayerService owner) {
            mOwner = new WeakReference<AudioPlayerService>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            AudioPlayerService player = mOwner.get();
            Log.d(TAG, "Player EVENT");
            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    Log.d(TAG, "MediaPlayerEndReached");
                    player.skipToNext();
                    break;
                case MediaPlayer.Event.EncounteredError:
                    Log.d(TAG, "Media Player Error, re-try");
                    //player.releasePlayer();
                    break;
                case MediaPlayer.Event.Playing:
                    player.playMedia();
                    break;
                case MediaPlayer.Event.Paused:
                    player.pauseMedia();
                    break;
                case MediaPlayer.Event.Stopped:
                    player.stopMedia();
                    break;
                default:
                    break;
            }
        }
    }


}
