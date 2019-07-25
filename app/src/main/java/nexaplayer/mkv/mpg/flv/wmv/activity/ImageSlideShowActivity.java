package nexaplayer.mkv.mpg.flv.wmv.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.adapter.ImageViewAdapter;
import nexaplayer.mkv.mpg.flv.wmv.fragment.image.ImageFragment;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;
import nexaplayer.mkv.mpg.flv.wmv.viewpager.ImageViewPager;


public class ImageSlideShowActivity extends AppCompatActivity {


    public final static String IMAGE_FILE_LIST_KEY = "IMAGE_FILE_LIST_KEY";
    public final static String IMAGE_FILE_INDEX_KEY = "IMAGE_FILE_INDEX_KEY";

    private ImageViewPager viewPager;
    ImageViewAdapter adapter;

    private String[] imageList;
    int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(ImageSlideShowActivity.class.getName(), "onCreate");
        setContentView(R.layout.activity_image_slide_show);


        initializeData();
        initializeViews();
        setupAdapter();
    }

    private void initializeData() {
        imageList = getIntent().getStringArrayExtra(IMAGE_FILE_LIST_KEY);
        if (imageList == null) {
            imageList = ImageFragment.sharedList;
        }
        currentIndex = getIntent().getIntExtra(IMAGE_FILE_INDEX_KEY, 0);
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.myviewPager);
    }

    private void setupAdapter() {
        adapter = new ImageViewAdapter(imageList, this, currentIndex);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex, false);
    }

}
