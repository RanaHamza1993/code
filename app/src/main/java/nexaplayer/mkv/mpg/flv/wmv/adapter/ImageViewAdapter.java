package nexaplayer.mkv.mpg.flv.wmv.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.ImageDecodeCallback;
import nexaplayer.mkv.mpg.flv.wmv.task.ImageDecodeAsyncTask;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by Sultan Ahmed on 12/14/2017.
 */

public class ImageViewAdapter extends PagerAdapter {

    private String imageList[];
    private Context context;
    private LayoutInflater layoutInflater;
    private int currentIndex;

    public ImageViewAdapter(String[] imageList, Context context, int currentIndex){
        this.imageList = imageList;
        this.context = context;
        this.currentIndex = currentIndex;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(imageList != null) {
            return imageList.length;
        }
        else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item_image, container, false);

        ImageViewTouch imageView = itemView.findViewById(R.id.ivImage);
        selectRandomImage(imageList[position], imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public void selectRandomImage(String imagePath, final ImageViewTouch imageView) {
            //int position = (int) (Math.random() * count);
        final ImageDecodeCallback callback = new ImageDecodeCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if (null != bitmap) {
                    imageView.setImageBitmap(bitmap, null, -1, -1);
                }
            }
        };

        new ImageDecodeAsyncTask(context, callback, imagePath).execute();
    }

}
