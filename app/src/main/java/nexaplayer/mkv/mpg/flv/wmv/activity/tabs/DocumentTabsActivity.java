package nexaplayer.mkv.mpg.flv.wmv.activity.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.view.MenuItem;
import android.view.View;
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
import nexaplayer.mkv.mpg.flv.wmv.fragment.document.ApkFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.document.DocumentFolderFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.document.DocumentFragment;
import nexaplayer.mkv.mpg.flv.wmv.fragment.document.DocumentSettingsFragment;
import nexaplayer.mkv.mpg.flv.wmv.listeners.FolderFragmentListeners;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ApkItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.DocumentItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.BottomNavigationViewHelper;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;

public class DocumentTabsActivity extends BaseTabActivity implements FolderFragmentListeners, DocumentItemSelectListener, ApkItemSelectListener {

    private Fragment currentFragment;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_tabs);
        initializeViews();
        initializeListeners();
        showAllDocumentFragment();
    }

    private void initializeViews(){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        BottomNavigationViewHelper.resizeBottomNavigationIcons(this,bottomNavigationView);

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
                    case R.id.menu_document:
                        displayFragment(AppConstants.ALL_DOCUMENT_FRAGMENT);
                        return true;
                    case R.id.menu_folder:
                        displayFragment(AppConstants.DOCUMENT_ALL_FOLDER_FRAGMENT);
                        return true;
                    case R.id.menu_apk:
                        displayFragment(AppConstants.DOCUMENT_ALL_APK_FRAGMENT);
                        return true;
                    case R.id.menu_settings:
                        displayFragment(AppConstants.DOCUMENT_SETTINGS_FRAGMENT);
                        return true;
                    default:
                        displayFragment(AppConstants.ALL_DOCUMENT_FRAGMENT);
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
                    onDocumentItemSelect(new ArrayList<DocumentItem>(), isChecked);
                }
            }
        });

        ivDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(DocumentTabsActivity.this)
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
    protected void onResume() {
        super.onResume();
        UtilityMethods.hideKeyboard(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
    }

    private void displayFragment(String fragmentName) {
        switch (fragmentName) {
            case AppConstants.ALL_DOCUMENT_FRAGMENT:
                showAllDocumentFragment();
                break;
            case AppConstants.DOCUMENT_ALL_FOLDER_FRAGMENT:
                showAllDocumentFolderFragment();
                break;
            case AppConstants.DOCUMENT_ALL_APK_FRAGMENT:
                showAllApkFragment();
                break;
            case AppConstants.DOCUMENT_SETTINGS_FRAGMENT:
                showDocumentSettingsFragment();
                break;
            default:
        }
    }

    private void showAllDocumentFragment(){
        Fragment fragment = new DocumentFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(DocumentFragment.DOCUMENT_SHOW_KEY, DocumentFragment.DOCUMENT_SHOW_DOCUMENT);
        arguments.putString(DocumentFragment.FOLDER_PATH_KEY, AppConstants.BASE_DIRECTORY.getPath());
        fragment.setArguments(arguments);
        currentFragment = fragment;
        parentFragment = null;
        bottomOptionsLayout.setWeightSum(5);
        replaceFragment(fragment);
    }

    private void showAllApkFragment(){
        Fragment fragment = new ApkFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        currentFragment = fragment;
        parentFragment = null;
        bottomOptionsLayout.setWeightSum(4);
        replaceFragment(fragment);
    }

    private void showAllDocumentFolderFragment(){
        Fragment fragment = new DocumentFolderFragment();
        currentFragment = fragment;
        parentFragment = null;
        bottomOptionsLayout.setWeightSum(5);
        replaceFragment(fragment);
    }

    private void showDocumentSettingsFragment(){
        Fragment fragment = new DocumentSettingsFragment();
        currentFragment = fragment;
        bottomOptionsLayout.setWeightSum(5);
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
        if(currentFragment instanceof TabActivityListener){
            TabActivityListener listener = (TabActivityListener) currentFragment;
            if(listener.isItemSelected()) {
                onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
                listener.selectAllItems(false);
            }
            else{
                if (parentFragment == null) {
                    super.onBackPressed();
                } else {
                    displayPreviousFragment();
                }
            }
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public void displayPreviousFragment(){
        if(parentFragment instanceof DocumentFolderFragment){
            showAllDocumentFragment();
        }
    }

    @Override
    public void onFolderItemClicked(String itemPath) {
        UtilityMethods.reportGoogleAnalytics(DocumentTabsActivity.class.getName(), "onFolderItemClicked");
        DocumentFragment documentDocumentFragment = new DocumentFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(DocumentFragment.DOCUMENT_SHOW_KEY, DocumentFragment.DOCUMENT_SHOW_FOLDER);
        arguments.putString(DocumentFragment.FOLDER_PATH_KEY, itemPath);
        parentFragment = currentFragment;
        currentFragment = documentDocumentFragment;
        documentDocumentFragment.setArguments(arguments);
        replaceFragment(documentDocumentFragment);
    }


    @Override
    public void onDocumentItemSelect(List<DocumentItem> itemList, boolean isAllItemSelected) {
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

    @Override
    public void onApkItemSelect(List<ApkItem> itemList, boolean allItemSelected) {
        int size = itemList.size();
        topOptionsLayout.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        bottomOptionsLayout.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        if (size > 0) {
            itemCheckBox.setChecked(allItemSelected);
            tvSelectItemCount.setText(getResources().getString(R.string.item_selected_count, size + ""));
            ivRenameButton.setEnabled(size > 1 ? false : true);
            ivInfoButton.setEnabled(size > 1 ? false : true);
        }
    }
}
