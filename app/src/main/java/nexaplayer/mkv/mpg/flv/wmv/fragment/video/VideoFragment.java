package nexaplayer.mkv.mpg.flv.wmv.fragment.video;

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
import nexaplayer.mkv.mpg.flv.wmv.activity.VideoPlayerActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.DetailsDialog;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.RenameDialog;
import nexaplayer.mkv.mpg.flv.wmv.adapter.VideoAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DialogItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.VideoItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesInFolderCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.VideoItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesInFolderAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

import static nexaplayer.mkv.mpg.flv.wmv.activity.VideoPlayerActivity.VIDEO_FILE_INDEX_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.VideoPlayerActivity.VIDEO_FILE_LIST_KEY;

public class VideoFragment extends Fragment implements VideoItemClickListener, DialogItemClickListener, TabActivityListener, RenameListener<VideoItem> {

    public static final String VIDEO_PLAY_KEY = "video_play_key";
    public static final String FOLDER_PATH_KEY = "folder_path_key";
    public static final String VIDEO_LIST_KEY = "video_list_key";

    /*video listing types*/
    public static final int VIDEO_PLAY_VIDEO = 0;
    public static final int VIDEO_PLAY_FOLDER = 1;

    RecyclerView videoRecyclerView;
    LinearLayoutManager videoLayoutManager;
    VideoAdapter videoAdapter;
    ArrayList<VideoItem> videoList;
    VideoItemSelectListener listener;

    private int videoPlayType;
    private String currentFolderPath = AppConstants.BASE_DIRECTORY.getPath();

    View fragmentView;
    Context context;
    LoadingDots loadingDots;

    private int itemCount = 0;
    boolean isActivityVisible;
//    private MoPubRecyclerAdapter myMoPubAdapter;
//    private RequestParameters mRequestParameters;

    public VideoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(VideoFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_video, container, false);
        initalizeViews();
        initializeData();

        setupViews();
        return fragmentView;
    }


    @Override
    public void onStart() {
        super.onStart();
        videoAdapter.clearList();
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
        videoRecyclerView = fragmentView.findViewById(R.id.video_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
    }

    private void setupViews(){

        setupVideoRecyclerView();
    }


    private void initializeData(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            videoPlayType = bundle.getInt(VIDEO_PLAY_KEY);
            currentFolderPath = bundle.getString(FOLDER_PATH_KEY);
        }
        context = getActivity();
        listener = (VideoItemSelectListener) getActivity();
        videoList = new ArrayList<>();
    }

    private void listFiles(){
        videoAdapter.clearList();
        showLoading();
        switch (videoPlayType){
            case VIDEO_PLAY_VIDEO:
                showAllVideoFiles();
                break;
            case VIDEO_PLAY_FOLDER:
                showAllFilesInFolder(currentFolderPath);
                break;
            default:
                showAllVideoFiles();
        }
    }

    @Override
    public void onVideoItemClicked(String[] list, int position) {
        UtilityMethods.stopAudioService(context);
        Intent videoPlayerIntent = new Intent(context, VideoPlayerActivity.class);
        videoPlayerIntent.putExtra(VIDEO_FILE_LIST_KEY, list);
        videoPlayerIntent.putExtra(VIDEO_FILE_INDEX_KEY, position);
        startActivity(videoPlayerIntent);
    }

    @Override
    public void onVideoItemLongClicked(List<VideoItem> item, boolean isAllItemSelected) {
        listener.onVideoItemSelect(item, isAllItemSelected);
    }


    private void setupVideoRecyclerView(){
        videoLayoutManager = new LinearLayoutManager(context);
        videoAdapter = new VideoAdapter(context, this);
        videoRecyclerView.setLayoutManager(videoLayoutManager);
        videoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videoRecyclerView.setAdapter(videoAdapter);
        videoAdapter.setVideoList(videoList);

//        // Pass the recycler Adapter your original adapter.
//         myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), videoAdapter);
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
//       videoRecyclerView.setAdapter(myMoPubAdapter);
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
    public void onResume() {

        // Request ads when the user returns to this activity.
//        myMoPubAdapter.refreshAds(getString(R.string.mopub_ads_placement_id),mRequestParameters);
        super.onResume();
        isActivityVisible = true;
    }
    @Override
    public void onDestroy() {
//        myMoPubAdapter.destroy();
        super.onDestroy();
    }
    private void showAllFilesInFolder(String directoryPath){
        videoAdapter.clearList();
        AllFilesInFolderCallback callback = new AllFilesInFolderCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                videoList = (ArrayList<VideoItem>)list;
                itemCount = list.size();
                setupVideoRecyclerView();
                hideLoading();
            }
        };
        new FindAllFilesInFolderAsyncTask(context, callback, directoryPath, AppConstants.ITEM_TYPE_VIDEO).execute();
      }

    private void showAllVideoFiles(){
        videoAdapter.clearList();
        AllFilesCallback callback = new AllFilesCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                videoList = (ArrayList<VideoItem>) list;
                setupVideoRecyclerView();
                itemCount = list.size();
                hideLoading();
            }
        };

        new FindAllFilesAsyncTask(context, callback, AppConstants.ITEM_TYPE_VIDEO).execute();
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
        videoAdapter.deleteItem((VideoItem) item);
    }

    @Override
    public boolean isStackEmpty() {
        return true;
    }

    @Override
    public void selectAllItems(boolean value) {
        videoAdapter.selectAllItems(value);
    }

    @Override
    public boolean isItemSelected() {
        boolean itemSelected = videoAdapter.isAnyItemSelected();
        videoAdapter.selectAllItems(false);
        return itemSelected;
    }

    @Override
    public void onDeleteClicked() {
        List<String> deleteList = videoAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, VideoItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        videoAdapter.deleteAllSelectedItem();
        listener.onVideoItemSelect(new ArrayList<VideoItem>(), false);
    }

    @Override
    public void onDetailClicked() {
        VideoItem selectedItem = videoAdapter.getSelectedItemList().get(0);
        DetailsDialog detailsDialog = new DetailsDialog(context, (RealmObject) selectedItem, AppConstants.ITEM_TYPE_VIDEO);
        detailsDialog.show();
    }

    @Override
    public void onRenameClicked() {
        RenameDialog renameDialog = new RenameDialog(context, this, videoAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_VIDEO );
        renameDialog.show();
    }

    @Override
    public void onShareClicked() {
        List<String> selectedItem = videoAdapter.getSelectedItemNameList();
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
        List<VideoItem> selectedItemList =  videoAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(VideoItem videoItem : selectedItemList){
            videoItem.setFavorite(true);
            selectedItemRealmList.add(videoItem);
        }
        UtilityMethods.showToast(context, "Items have been marked favorite");
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        videoAdapter.selectAllItems(false);
        listener.onVideoItemSelect(new ArrayList<VideoItem>(), false);
    }

    @Override
    public void onItemRename(VideoItem item, String oldName) {
        RealmManager.getInstance().renameObject(oldName, item, VideoItem.class);
        videoAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        videoAdapter.selectAllItems(false);
        listener.onVideoItemSelect(new ArrayList<VideoItem>(), false);
    }
}
