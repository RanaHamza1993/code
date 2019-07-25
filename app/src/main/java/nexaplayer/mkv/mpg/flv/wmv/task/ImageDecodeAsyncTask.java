package nexaplayer.mkv.mpg.flv.wmv.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.ImageDecodeCallback;
import nexaplayer.mkv.mpg.flv.wmv.utils.ImageUtils.DecoderUtils;

/**
 * Created by Sultan Ahmed on 12/9/2017.
 */

public class ImageDecodeAsyncTask extends AsyncTask<Object, Object, Bitmap> {

    ImageDecodeCallback callback;
    Context context;
    String filePath;

    public ImageDecodeAsyncTask(Context context, ImageDecodeCallback callback, String filePath){
        this.context = context;
        this.callback = callback;
        this.filePath = filePath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        }

    @Override
    protected Bitmap doInBackground(Object... params) {
        Uri imageUri = Uri.parse(filePath);
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int size = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) / 0.55);
        Bitmap bitmap = DecoderUtils.decode(context, imageUri, size, size);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        callback.onSuccess(bitmap);
    }
}



