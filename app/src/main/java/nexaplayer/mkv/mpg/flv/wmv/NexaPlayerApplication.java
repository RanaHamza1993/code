package nexaplayer.mkv.mpg.flv.wmv;

import android.app.Application;
import androidx.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Sultan Ahmed on 12/20/2017.
 */

public class NexaPlayerApplication extends MultiDexApplication {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        sAnalytics = GoogleAnalytics.getInstance(this);
        Fresco.initialize(this);
        Realm.init(this);
        configureRealm();
    }

    private void configureRealm(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized static public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        UtilityMethods.stopAudioService(this);
        Fresco.shutDown();
    }
}
