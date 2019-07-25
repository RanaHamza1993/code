package nexaplayer.mkv.mpg.flv.wmv.activity.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nexaplayer.mkv.mpg.flv.wmv.service.AudioPlayerService;
import nexaplayer.mkv.mpg.flv.wmv.service.InitService;

/**
 * Created by pbuhsoft on 10/02/2018.
 */

public class BaseTabActivity extends AppCompatActivity {
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
	}

	@Override
	public void onBackPressed() {
		Log.v("wasim","BaseTabActivity onBackPressed");
		Intent intentAudioService=new Intent(this, AudioPlayerService.class);
		startService(intentAudioService);
		Intent intentInitService=new Intent(this, InitService.class);
		startService(intentInitService);
		super.onBackPressed();
	}
}
