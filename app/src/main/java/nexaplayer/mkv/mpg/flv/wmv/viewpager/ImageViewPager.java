package nexaplayer.mkv.mpg.flv.wmv.viewpager;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import nexaplayer.mkv.mpg.flv.wmv.activity.ImageSlideShowActivity;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by Sultan Ahmed on 12/14/2017.
 */

public class ImageViewPager extends ViewPager {
    private static final String TAG = "ImageViewPager";
    public static final String VIEW_PAGER_OBJECT_TAG = "image#";

    private int previousPosition;

    private OnPageSelectedListener onPageSelectedListener;

    public ImageViewPager(Context context) {
        super(context);
        init();
    }

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnPageSelectedListener(OnPageSelectedListener listener) {
        onPageSelectedListener = listener;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ImageViewTouch) {
            Log.d(ImageSlideShowActivity.class.getName(), "Can Item Scroll with position : " + ((ImageViewTouch) v).canScroll(dx));
            Log.d(ImageSlideShowActivity.class.getName(), " :Can Item Scroll with scale " + ((ImageViewTouch) v).canScroll());
            return ((ImageViewTouch) v).canScroll();


        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }

    public interface OnPageSelectedListener {

        void onPageSelected(int position);

    }

    private void init() {
        previousPosition = getCurrentItem();

        setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (onPageSelectedListener != null) {
                    onPageSelectedListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_SETTLING && previousPosition != getCurrentItem()) {
                    try {
                        ImageViewTouch imageViewTouch = findViewWithTag(VIEW_PAGER_OBJECT_TAG + getCurrentItem());
                        if (imageViewTouch != null) {
                            imageViewTouch.zoomTo(1f, 300);
                        }

                        previousPosition = getCurrentItem();
                    } catch (ClassCastException ex) {
                        Log.e(TAG, "This view pager should have only ImageViewTouch as a children.", ex);
                    }
                }
            }
        });
    }
}
