package nexaplayer.mkv.mpg.flv.wmv.fragment.video;

import android.content.Context;
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
import nexaplayer.mkv.mpg.flv.wmv.adapter.FolderAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.FolderFragmentListeners;
import nexaplayer.mkv.mpg.flv.wmv.listeners.FolderItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFolderCallback;
import nexaplayer.mkv.mpg.flv.wmv.model.FolderItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFoldersAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VideoFolderFragment extends Fragment implements FolderItemClickListener, TabActivityListener {

    RecyclerView folderRecyclerView;
    LinearLayoutManager folderLayoutManager;
    FolderAdapter folderAdapter;
    LoadingDots loadingDots;

    Stack<String> folderStack;
    String currentFolderPath;

    ArrayList<FolderItem> folderList;
    FolderFragmentListeners listeners;

    View fragmentView;
    Context context;

    private int itemCount = 0;
    private boolean isActivityVisible = false;
//    private MoPubRecyclerAdapter myMoPubAdapter;
//    private RequestParameters mRequestParameters;

    public VideoFolderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(VideoFolderFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_folder, container, false);
        initalizeViews();
        initializeData();
        setupViews();
        listFiles();
        return fragmentView;
    }

    @Override
    public void onResume() {
        // Request ads when the user returns to this activity.
//        myMoPubAdapter.refreshAds(getString(R.string.mopub_ads_placement_id),mRequestParameters);

        super.onResume();
        isActivityVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

    private void initalizeViews(){
        folderRecyclerView = fragmentView.findViewById(R.id.folder_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
    }

    private void setupViews() {
        setupFolderRecyclerView();
    }


    private void initializeData(){
        context = getActivity();
        folderStack = new Stack<>();
        folderList = new ArrayList<>();
        currentFolderPath = AppConstants.BASE_DIRECTORY.getAbsolutePath();
        Bundle bundle = getArguments();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listeners = (FolderFragmentListeners) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listeners = null;
    }



    private void listFiles(){
        folderAdapter.clearList();
        showLoading();
        showAllItemsFolders();
    }

    private void setupFolderRecyclerView(){
        folderLayoutManager = new LinearLayoutManager(context);
        folderAdapter = new FolderAdapter(context, this);
        folderRecyclerView.setLayoutManager(folderLayoutManager);
        folderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        folderRecyclerView.setAdapter(folderAdapter);
        folderAdapter.setFolderList(folderList);
        // Pass the recycler Adapter your original adapter.
//        myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), folderAdapter);
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
//        folderRecyclerView.setAdapter(myMoPubAdapter);
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

        private void showAllItemsFolders() {
            folderList.clear();
            AllFolderCallback callback = new AllFolderCallback() {
                @Override
                public void onSuccess(List<FolderItem> list) {
                    folderList = (ArrayList<FolderItem>) list;
                    setupFolderRecyclerView();
                    hideLoading();
                    itemCount = list.size();
                }
            };
            new FindAllFoldersAsyncTask(context, callback, AppConstants.ITEM_TYPE_VIDEO).execute();
        }

    @Override
    public void onFolderItemClicked(FolderItem item) {
        folderStack.push(item.getPath());
        showFilesInFolder(item.getPath());
    }

    public boolean isStackEmpty(){
        return folderStack.isEmpty();
    }

    @Override
    public void selectAllItems(boolean value) {

    }

    @Override
    public boolean isItemSelected() {
        return false;
    }

    private void showFilesInFolder(String path){
        folderAdapter.clearList();
        listeners.onFolderItemClicked(path);
    }
    @Override
    public void onDeleteClicked() {

    }

    @Override
    public void onDetailClicked() {

    }

    @Override
    public void onRenameClicked() {

    }

    @Override
    public void onShareClicked() {

    }

    @Override
    public void onFavoriteClicked() {

    }

    private void showLoading(){
        loadingDots.setVisibility(View.VISIBLE);
        loadingDots.startAnimation();
    }

    private void hideLoading(){
        loadingDots.setVisibility(View.GONE);
        loadingDots.stopAnimation();
    }



}
