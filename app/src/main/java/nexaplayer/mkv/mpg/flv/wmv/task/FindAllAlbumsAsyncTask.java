package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.os.AsyncTask;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllArtistCallback;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.FolderUtils;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class FindAllAlbumsAsyncTask extends AsyncTask<Object, Object, MultiMap<String, List<AudioItem>> > {

    AllArtistCallback callback;
    Context context;
    MultiMap<String, List<AudioItem>> albumMap;
    List<RealmObject> audioList;

    public FindAllAlbumsAsyncTask(Context context, AllArtistCallback callback){
        this.context = context;
        this.callback = callback;
        this.albumMap = new MultiValueMap<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        audioList = RealmManager.getInstance().getItemListFromRealm(AudioItem.class, SharedPreferenceManager.getInstance(context));
    }


    @Override
    protected MultiMap<String, List<AudioItem>> doInBackground(Object... params) {
        albumMap = FolderUtils.showAllAlbum(audioList);
        return albumMap;
    }

    @Override
    protected void onPostExecute(MultiMap<String, List<AudioItem>> artistMap) {
        super.onPostExecute(artistMap);
        callback.onSuccess(artistMap);
    }
}



