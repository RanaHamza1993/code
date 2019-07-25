package nexaplayer.mkv.mpg.flv.wmv.activity.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.fragment.audio.AudioAlbumFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.audio.AudioArtistFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.audio.AudioFolderFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.audio.AudioFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.audio.AudioSettingsFragment;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ArtistFragmentListeners;
import nexaplayer.mkv.mpg.flv.wmv.listeners.FolderFragmentListeners;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.AudioItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;


import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;

public class AudioTabsActivity extends BaseTabActivity implements FolderFragmentListeners, ArtistFragmentListeners, AudioItemSelectListener {

    Fragment currentFragment;
    Fragment parentFragment;

    private BottomNavigationView bottomNavigationView;

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
    protected void onResume() {
        super.onResume();
        UtilityMethods.hideKeyboard(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_tabs);
        initializeViews();
        initializeListeners();
        showAllAudioFragment();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
//        BottomNavigationViewHelper.resizeBottomNavigationIcons(this, bottomNavigationView);

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
                    case R.id.menu_audio:
                        displayFragment(AppConstants.ALL_AUDIO_FRAGMENT);
                        return true;
                    case R.id.menu_folder:
                        displayFragment(AppConstants.AUDIO_ALL_FOLDER_FRAGMENT);
                        return true;
                    case R.id.menu_artist:
                        displayFragment(AppConstants.ALL_ARTIST_FRAGMENT);
                        return true;
                    case R.id.menu_album:
                        displayFragment(AppConstants.ALL_ALBUM_FRAGMENT);
                        return true;
                    case R.id.menu_settings:
                        displayFragment(AppConstants.AUDIO_SETTINGS_FRAGMENT);
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
                AlertDialog dialog = new AlertDialog.Builder(AudioTabsActivity.this)
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
            case AppConstants.ALL_AUDIO_FRAGMENT:
                showAllAudioFragment();
                break;
            case AppConstants.AUDIO_ALL_FOLDER_FRAGMENT:
                showAllAudioFolderFragment();
                break;
            case AppConstants.ALL_ARTIST_FRAGMENT:
                showAllAudioArtistFragment();
                break;
            case AppConstants.ALL_ALBUM_FRAGMENT:
                showAllAudioAlbumsFragment();
                break;
            case AppConstants.AUDIO_SETTINGS_FRAGMENT:
                showAudioSettingsFragment();
                break;
            default:
        }
    }

    private void showAllAudioFragment() {
        Fragment fragment = new AudioFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(AudioFragment.AUDIO_PLAY_KEY, AudioFragment.AUDIO_PLAY_AUDIO);
        arguments.putString(AudioFragment.FOLDER_PATH_KEY, AppConstants.BASE_DIRECTORY.getPath());
        fragment.setArguments(arguments);
        currentFragment = fragment;
        parentFragment = null;
        replaceFragment(fragment);
    }

    private void showAllAudioFolderFragment() {
        Fragment fragment = new AudioFolderFragment();
        currentFragment = fragment;
        parentFragment = null;
        replaceFragment(fragment);
    }

    private void showAllAudioArtistFragment() {
        Fragment fragment = new AudioArtistFragment();
        currentFragment = fragment;
        parentFragment = null;
        replaceFragment(fragment);
    }

    private void showAllAudioAlbumsFragment() {
        Fragment fragment = new AudioAlbumFragment();
        currentFragment = fragment;
        parentFragment = null;
        replaceFragment(fragment);
    }

    private void showAudioSettingsFragment() {
        Fragment fragment = new AudioSettingsFragment();
        currentFragment = fragment;
        parentFragment = null;
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Log.v("wasim","onBackPressed audio actitity");
        if (currentFragment instanceof TabActivityListener) {
            TabActivityListener listener = (TabActivityListener) currentFragment;
            if(listener != null) {
                if (listener.isItemSelected()) {
                    onAudioItemSelect(new ArrayList<AudioItem>(), false);
                    listener.selectAllItems(false);
                } else {
                    if (parentFragment == null) {
                        super.onBackPressed();
                    } else {
                        displayPreviousFragment();
                    }
                }
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

    public void displayPreviousFragment() {
        if (parentFragment instanceof AudioFolderFragment) {
            showAllAudioFolderFragment();
        } else if (parentFragment instanceof AudioArtistFragment) {
            showAllAudioArtistFragment();
        } else if (parentFragment instanceof AudioAlbumFragment) {
            showAllAudioAlbumsFragment();
        }
    }

    @Override
    public void onFolderItemClicked(String itemPath) {
        UtilityMethods.reportGoogleAnalytics(AudioTabsActivity.class.getName(), "onFolderItemClicked");
        AudioFragment audioAudioFragment = new AudioFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(AudioFragment.AUDIO_PLAY_KEY, AudioFragment.AUDIO_PLAY_FOLDER);
        arguments.putString(AudioFragment.FOLDER_PATH_KEY, itemPath);
        parentFragment = currentFragment;
        currentFragment = audioAudioFragment;
        audioAudioFragment.setArguments(arguments);
        replaceFragment(audioAudioFragment);
    }

    @Override
    public void onArtistItemClicked(String[] list) {
        UtilityMethods.reportGoogleAnalytics(AudioTabsActivity.class.getName(), "onFolderItemClicked");
        AudioFragment audioAudioFragment = new AudioFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(AudioFragment.AUDIO_PLAY_KEY, AudioFragment.AUDIO_PLAY_ARTIST);
        arguments.putString(AudioFragment.FOLDER_PATH_KEY, "");
        arguments.putStringArray(AudioFragment.AUDIO_LIST_KEY, list);
        parentFragment = currentFragment;
        currentFragment = audioAudioFragment;
        audioAudioFragment.setArguments(arguments);
        replaceFragment(audioAudioFragment);
    }

    @Override
    public void onAudioItemSelect(List<AudioItem> itemList, boolean isAllItemSelected) {
        int size = itemList.size();
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
