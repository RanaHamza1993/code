package nexaplayer.mkv.mpg.flv.wmv.fragment.image;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eyalbira.loadingdots.LoadingDots;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.ImageSlideShowActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.DetailsDialog;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.RenameDialog;
import nexaplayer.mkv.mpg.flv.wmv.adapter.ImageAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DialogItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ImageItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesInFolderCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ImageItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesInFolderAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

import static nexaplayer.mkv.mpg.flv.wmv.activity.ImageSlideShowActivity.IMAGE_FILE_INDEX_KEY;

public class ImageFragment extends Fragment implements ImageItemClickListener, DialogItemClickListener, TabActivityListener, RenameListener<ImageItem> {

    public static final String IMAGE_SHOW_KEY = "image_show_key";
    public static final String FOLDER_PATH_KEY = "folder_path_key";
    
    /*image listing types*/
    public static final int IMAGE_SHOW_IMAGE = 0;
    public static final int IMAGE_SHOW_FOLDER = 1;

    RecyclerView imageRecyclerView;
    LinearLayoutManager imageLayoutManager;
    ImageAdapter imageAdapter;
    ArrayList<ImageItem> imageList;
    ImageItemSelectListener listener;

    private int imagePlayType;
    private String currentFolderPath = AppConstants.BASE_DIRECTORY.getPath();

    View fragmentView;
    Context context;
    LoadingDots loadingDots;

    private int itemCount = 0;
    boolean isActivityVisible;
//    private MoPubRecyclerAdapter myMoPubAdapter;
//    private RequestParameters mRequestParameters;
    public static String[] sharedList;
    public ImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(ImageFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_image, container, false);
        initalizeViews();
        initializeData();
        setupViews();
        return fragmentView;
    }

    public void onResume() {
        // Request ads when the user returns to this activity.
//        myMoPubAdapter.refreshAds(getString(R.string.mopub_ads_placement_id),mRequestParameters);
        super.onResume();
        isActivityVisible = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        imageAdapter.clearList();
        setupViews();
        listFiles();
        isActivityVisible = true;

    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityVisible = false;
    }

    private void initalizeViews(){
        imageRecyclerView = fragmentView.findViewById(R.id.image_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
    }

    private void setupViews(){

        setupImageRecyclerView();
    }


    private void initializeData(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            imagePlayType = bundle.getInt(IMAGE_SHOW_KEY);
            currentFolderPath = bundle.getString(FOLDER_PATH_KEY);
        }
        context = getActivity();
        listener = (ImageItemSelectListener) getActivity();
        imageList = new ArrayList<>();
    }

    private void listFiles(){
        imageAdapter.clearList();
        showLoading();
        listener.onImageItemSelect(new ArrayList<ImageItem>(), false);
        switch (imagePlayType){
            case IMAGE_SHOW_IMAGE:
                showAllImageFiles();
                break;
            case IMAGE_SHOW_FOLDER:
                showAllFilesInFolder(currentFolderPath);
                break;
            default:
                showAllImageFiles();
        }
    }

    @Override
    public void onImageItemClicked(String[] list, int position) {
        UtilityMethods.stopAudioService(context);
        Intent imagePlayerIntent = new Intent(context, ImageSlideShowActivity.class);
        //imagePlayerIntent.putExtra(IMAGE_FILE_LIST_KEY, list);
        sharedList=list;
        imagePlayerIntent.putExtra(IMAGE_FILE_INDEX_KEY, position);
        startActivity(imagePlayerIntent);
    }

    @Override
    public void onImageItemLongClicked(List<ImageItem> item, boolean isAllItemSelected) {
        listener.onImageItemSelect(item, isAllItemSelected);
    }

    private void setupImageRecyclerView(){
        imageLayoutManager = new LinearLayoutManager(context);
        imageAdapter = new ImageAdapter(context, this);
        imageRecyclerView.setLayoutManager(imageLayoutManager);
        imageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerView.setAdapter(imageAdapter);
        imageAdapter.setImageList(imageList);

        // Pass the recycler Adapter your original adapter.
//        myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), imageAdapter);
//        // Create an ad renderer and view binder that describe your native ad layout.
//        SimpleCursorAdapter.ViewBinder myViewBinder = new SimpleCursorAdapter.ViewBinder.Builder(R.layout.item_mobpub_native_ad)
//                .titleId(R.id.tvAdTitle)
//                .textId(R.id.tvAdBody)
//                //.mainImageId(R.id.my_ad_image)
//                .iconImageId(R.id.adImage)
//                .callToActionId(R.id.btnCTA)
//                .build();
//
//        MoPubStaticNativeAdRenderer myRenderer = new MoPubStaticNativeAdRenderer(myViewBinder);
//
//        myMoPubAdapter.registerAdRenderer(myRenderer);
//
//        // Set up the recycler view
//        imageRecyclerView.setAdapter(myMoPubAdapter);
//        // Setting desired assets on your request helps native ad networks and bidders
//        // provide higher-quality ads.
//        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
//                RequestParameters.NativeAdAsset.TITLE,
//                RequestParameters.NativeAdAsset.TEXT,
//                RequestParameters.NativeAdAsset.ICON_IMAGE,
//                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
//
//        mRequestParameters = new RequestParameters.Builder()
//                .desiredAssets(desiredAssets)
//                .build();
//        myMoPubAdapter.loadAds(getString(R.string.mopub_ads_placement_id),mRequestParameters);
    }

    @Override
    public void onDestroy() {
//        myMoPubAdapter.destroy();
        super.onDestroy();
    }

    private void showAllFilesInFolder(String directoryPath){
        imageAdapter.clearList();
        AllFilesInFolderCallback callback = new AllFilesInFolderCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                imageList = (ArrayList<ImageItem>)list;
                itemCount = list.size();
                setupImageRecyclerView();
                hideLoading();
            }
        };
        new FindAllFilesInFolderAsyncTask(context, callback, directoryPath, AppConstants.ITEM_TYPE_IMAGE).execute();
      }

    private void showAllImageFiles(){
        imageAdapter.clearList();
        AllFilesCallback callback = new AllFilesCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                imageList = (ArrayList<ImageItem>) list;
                setupImageRecyclerView();
                itemCount = list.size();
                hideLoading();
            }
        };

        new FindAllFilesAsyncTask(context, callback, AppConstants.ITEM_TYPE_IMAGE).execute();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void showLoading(){
        loadingDots.setVisibility(View.VISIBLE);
        loadingDots.startAnimation();
    }

    private void hideLoading(){
        loadingDots.setVisibility(View.GONE);
        loadingDots.stopAnimation();
    }

    @Override
    public void onDeleteItem(RealmObject item) {
        imageAdapter.deleteItem((ImageItem) item);
    }

    @Override
    public boolean isStackEmpty() {
        return true;
    }

    @Override
    public void selectAllItems(boolean value) {
        imageAdapter.selectAllItems(value);
    }

    @Override
    public boolean isItemSelected() {
        boolean itemSelected = imageAdapter.isAnyItemSelected();
        imageAdapter.selectAllItems(false);
        return itemSelected;
    }

    @Override
    public void onDeleteClicked() {
        List<String> deleteList = imageAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, ImageItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        imageAdapter.deleteAllSelectedItem();
        listener.onImageItemSelect(new ArrayList<ImageItem>(), false);
    }

    @Override
    public void onDetailClicked() {
        ImageItem selectedItem = imageAdapter.getSelectedItemList().get(0);
        DetailsDialog detailsDialog = new DetailsDialog(context, (RealmObject) selectedItem, AppConstants.ITEM_TYPE_IMAGE);
        detailsDialog.show();
    }

    @Override
    public void onRenameClicked() {
        RenameDialog renameDialog = new RenameDialog(context, this, imageAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_IMAGE );
        renameDialog.show();
    }

    @Override
    public void onShareClicked() {
        List<String> selectedItem = imageAdapter.getSelectedItemNameList();
        ArrayList<Uri> selectedItemUri = new ArrayList<>();
        for(String item: selectedItem){
            selectedItemUri.add(Uri.parse(item));
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedItemUri);
        sharingIntent.setType("audio/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Audio");
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    @Override
    public void onFavoriteClicked() {
        List<ImageItem> selectedItemList =  imageAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(ImageItem imageItem : selectedItemList){
            imageItem.setFavorite(true);
            selectedItemRealmList.add(imageItem);
        }

        UtilityMethods.showToast(context, "Items have been marked favorite");
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        imageAdapter.selectAllItems(false);
        listener.onImageItemSelect(new ArrayList<ImageItem>(), false);
    }

    @Override
    public void onItemRename(ImageItem item, String oldName) {
        RealmManager.getInstance().renameObject(oldName, item, ImageItem.class);
        imageAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        imageAdapter.selectAllItems(false);
        listener.onImageItemSelect(new ArrayList<ImageItem>(), false);
    }
}
