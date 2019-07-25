package nexaplayer.mkv.mpg.flv.wmv.activity.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.ImageSlideShowActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.VideoPlayerActivity;
import nexaplayer.mkv.mpg.flv.wmv.fragment.favorite.FavoriteFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.image.ImageFragment;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ApkItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.AudioItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DocumentItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ImageItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.VideoItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ApkItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.AudioItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.DocumentItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ImageItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.VideoItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.AUDIO_FILE_INDEX_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.AUDIO_FILE_LIST_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.ImageSlideShowActivity.IMAGE_FILE_INDEX_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.VideoPlayerActivity.VIDEO_FILE_INDEX_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.VideoPlayerActivity.VIDEO_FILE_LIST_KEY;

public class FavoriteTabsActivity extends BaseTabActivity implements AudioItemClickListener, VideoItemClickListener, ImageItemClickListener, DocumentItemClickListener,
        ApkItemClickListener, AudioItemSelectListener, VideoItemSelectListener, ImageItemSelectListener, DocumentItemSelectListener, ApkItemSelectListener{

    private static String TAG = FavoriteTabsActivity.class.getName();

    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;

    private String itemType;
    /*options layout item*/
    CheckBox itemCheckBox;
    TextView tvSelectItemCount;
    ImageView ivDeleteButton;
    ImageView ivRenameButton;
    ImageView ivInfoButton;
    ImageView ivShareButton;
    ImageView ivFavoriteButton;
    LinearLayout topOptionsLayout;
    LinearLayout bottomOptionsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_tabs);
        initializeViews();
        initializeListeners();
        displayFragment(AppConstants.FAVORITE_AUDIO_FRAGMENT);
    }



    private void initializeViews(){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
//        BottomNavigationViewHelper.resizeBottomNavigationIcons(this,bottomNavigationView);

        topOptionsLayout = (LinearLayout) findViewById(R.id.layout_options_top);
        bottomOptionsLayout = (LinearLayout) findViewById(R.id.layout_options_bottom);
        itemCheckBox = (CheckBox) findViewById(R.id.selectall_checkbox);
        tvSelectItemCount = (TextView) findViewById(R.id.tv_selectedItemCount);
        ivDeleteButton = (ImageView) findViewById(R.id.button_delete);
        ivRenameButton = (ImageView) findViewById(R.id.button_rename);
        ivInfoButton = (ImageView) findViewById(R.id.button_details);
        ivShareButton = (ImageView) findViewById(R.id.button_share);
        ivFavoriteButton = (ImageView) findViewById(R.id.button_favorite);
    }

    private void initializeListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorite_audio:
                        displayFragment(AppConstants.FAVORITE_AUDIO_FRAGMENT);
                        return true;
                    case R.id.favorite_video:
                        displayFragment(AppConstants.FAVORITE_VIDEO_FRAGMENT);
                        return true;
                    case R.id.favorite_image:
                        displayFragment(AppConstants.FAVORITE_IMAGE_FRAGMENT);
                        return true;
                    case R.id.favorite_document:
                        displayFragment(AppConstants.FAVORITE_DOCUMENT_FRAGMENT);
                        return true;
                    default:
                        displayFragment(AppConstants.ALL_AUDIO_FRAGMENT);
                        return true;
                }

            }
        });

        itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tvSelectItemCount.setText(getResources().getString(R.string.item_selected_count, isChecked ? "All" : "No"));
                if (currentFragment instanceof TabActivityListener) {
                    ((TabActivityListener) currentFragment).selectAllItems(isChecked);
                }
                if (!isChecked) {
                    onAudioItemSelect(new ArrayList<AudioItem>(), isChecked);
                }
            }
        });

        ivDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(FavoriteTabsActivity.this)
                        .setCancelable(true)
                        .setTitle(R.string.dialog_delete)
                        .setMessage(R.string.delete_title)
                        .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentFragment instanceof TabActivityListener){
                                    ((TabActivityListener) currentFragment).onDeleteClicked();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });
        ivRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TabActivityListener) currentFragment).onRenameClicked();
            }
        });
        ivInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TabActivityListener) currentFragment).onDetailClicked();
            }
        });
        ivShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TabActivityListener) currentFragment).onShareClicked();
            }
        });
        ivFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TabActivityListener) currentFragment).onFavoriteClicked();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
    }

    private void displayFragment(String fragmentName) {
        switch (fragmentName) {
            case AppConstants.FAVORITE_AUDIO_FRAGMENT:
                itemType = AppConstants.ITEM_TYPE_AUDIO;
                showFavoriteFragment();
                break;
            case AppConstants.FAVORITE_VIDEO_FRAGMENT:
                itemType = AppConstants.ITEM_TYPE_VIDEO;
                showFavoriteFragment();
                break;
            case AppConstants.FAVORITE_DOCUMENT_FRAGMENT:
                itemType = AppConstants.ITEM_TYPE_DOCUMENT;
                showFavoriteFragment();
                break;
            case AppConstants.FAVORITE_IMAGE_FRAGMENT:
                itemType = AppConstants.ITEM_TYPE_IMAGE;
                showFavoriteFragment();
                break;
            case AppConstants.FAVORITE_APK_FRAGMENT:
                itemType = AppConstants.ITEM_TYPE_APK;
                showFavoriteFragment();
                break;
            case AppConstants.FAVORITE_ADS_FRAGMENT:
                //showAdsFragment();
                break;
            default:
        }
    }

    private void showFavoriteFragment(){
        Fragment fragment = new FavoriteFragment();
        Bundle argument = new Bundle();
        argument.putString(FavoriteFragment.FAVORITE_ITEM_TYPE, itemType);
        fragment.setArguments(argument);
        currentFragment = fragment;
        replaceFragment(fragment);
    }


    private void replaceFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof TabActivityListener) {
            TabActivityListener listener = (TabActivityListener) currentFragment;
            if(listener.isItemSelected()) {
                onAudioItemSelect(new ArrayList<AudioItem>(), false);
                listener.selectAllItems(false);
            }
            else {
                super.onBackPressed();
                finish();
            }

        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilityMethods.hideKeyboard(this);
    }


    @Override
    public void onAudioItemClicked(String[] list, int position) {
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "audioItemClicked");
        Intent audioPlayerIntent = new Intent(this, AudioPlayerActivity.class);
        audioPlayerIntent.putExtra(AUDIO_FILE_LIST_KEY, list);
        audioPlayerIntent.putExtra(AUDIO_FILE_INDEX_KEY, position);
        startActivity(audioPlayerIntent);
    }

    @Override
    public void onAudioItemLongClicked(List<AudioItem> item, boolean isAllItemSelected) {
        onAudioItemSelect(item, isAllItemSelected);
    }

    @Override
    public void onImageItemClicked(String[] list, int position) {
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "imageItemClicked");
        Intent imagePlayerIntent = new Intent(this, ImageSlideShowActivity.class);
        //imagePlayerIntent.putExtra(IMAGE_FILE_LIST_KEY, list);
        ImageFragment.sharedList=list;
        imagePlayerIntent.putExtra(IMAGE_FILE_INDEX_KEY, position);
        startActivity(imagePlayerIntent);

    }

    @Override
    public void onImageItemLongClicked(List<ImageItem> item, boolean isAllItemSelected) {
        onImageItemSelect(item, isAllItemSelected);
    }

    @Override
    public void onVideoItemClicked(String[] list, int position) {
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "VideoItemClicked");
        Intent videoPlayerIntent = new Intent(this, VideoPlayerActivity.class);
        videoPlayerIntent.putExtra(VIDEO_FILE_LIST_KEY, list);
        videoPlayerIntent.putExtra(VIDEO_FILE_INDEX_KEY, position);
        startActivity(videoPlayerIntent);
    }

    @Override
    public void onVideoItemLongClicked(List<VideoItem> items, boolean allSelected) {
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "VideoItemLongClicked");
        onVideoItemSelect(items, allSelected);
    }
    

    @Override
    public void onDocumentItemClicked(DocumentItem item) {
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "DocumentItemClicked");
        Intent intent = new Intent();
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.getExtension());
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(item.getPath())), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(Intent.createChooser(intent, "Open with"));
        } catch (Exception e) {
            UtilityMethods.showToast(this, "Unable to open file");
            e.printStackTrace();
        }
    }

    @Override
    public void onDocumentItemLongClicked(List<DocumentItem> list, boolean allItemSelected) {
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "DocumentItemLongClicked");
        onDocumentItemSelect(list, allItemSelected);
    }

    @Override
    public void onApkItemClicked(ApkItem item){
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "ApkItemClicked");

        Intent intent = new Intent();
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.getExtension());
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(item.getPath())), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            UtilityMethods.showToast(this, "Unable to open file");
            e.printStackTrace();
        }
    }

    @Override
    public void onApkItemLongClicked(List<ApkItem> item, boolean isAllItemSelected) {
        UtilityMethods.reportGoogleAnalytics(FavoriteTabsActivity.class.getName(), "ApkItemLongClicked");
        onApkItemSelect(item, isAllItemSelected);
    }


    @Override
    public void onAudioItemSelect(List<AudioItem> itemList, boolean isAllItemSelected) {
        int size = itemList.size();
        updateViews(size, isAllItemSelected);
    }

    @Override
    public void onVideoItemSelect(List<VideoItem> itemList, boolean isAllItemSelected) {
        int size = itemList.size();
        updateViews(size, isAllItemSelected);
    }

    @Override
    public void onImageItemSelect(List<ImageItem> itemList, boolean isAllItemSelected) {
        int size = itemList.size();
        updateViews(size, isAllItemSelected);
    }

    @Override
    public void onDocumentItemSelect(List<DocumentItem> itemList, boolean isAllItemSelected) {
        int size = itemList.size();
        updateViews(size, isAllItemSelected);
    }

    @Override
    public void onApkItemSelect(List<ApkItem> itemList, boolean isAllItemSelected) {
        int size = itemList.size();
        updateViews(size, isAllItemSelected);
    }

    private void updateViews(int size, boolean isAllItemSelected){
        topOptionsLayout.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        bottomOptionsLayout.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        if (size > 0) {
            itemCheckBox.setChecked(isAllItemSelected);
            tvSelectItemCount.setText(getResources().getString(R.string.item_selected_count, size + ""));
            ivRenameButton.setEnabled(size > 1 ? false : true);
            ivInfoButton.setEnabled(size > 1 ? false : true);
        }
    }
}
