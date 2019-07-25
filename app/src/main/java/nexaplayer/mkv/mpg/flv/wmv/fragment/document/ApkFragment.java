package nexaplayer.mkv.mpg.flv.wmv.fragment.document;

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
import android.webkit.MimeTypeMap;

import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.gms.ads.formats.NativeAd;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.DetailsDialog;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.RenameDialog;
import nexaplayer.mkv.mpg.flv.wmv.adapter.ApkAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ApkItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DialogItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ApkItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class ApkFragment extends Fragment implements ApkItemClickListener, DialogItemClickListener, TabActivityListener, RenameListener<ApkItem> {


    /*apk listing types*/
    public static final int APK_SHOW_APK = 0;
    public static final int APK_SHOW_FOLDER = 1;

    RecyclerView apkRecyclerView;
    LinearLayoutManager apkLayoutManager;
    ApkAdapter apkAdapter;
    ArrayList<ApkItem> apkList;
    ApkItemSelectListener listener;

    private String currentFolderPath = AppConstants.BASE_DIRECTORY.getPath();

    View fragmentView;
    Context context;
    LoadingDots loadingDots;

    // List of native ads that have been successfully loaded.
    private List<NativeAd> mNativeAds = new ArrayList<>();
    private int itemCount = 0;
    boolean isActivityVisible;
//    private MoPubRecyclerAdapter myMoPubAdapter;
//    private RequestParameters mRequestParameters;

    public ApkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(ApkFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_apk, container, false);
        initalizeViews();
        initializeData();
        initializeListeners();

        setupViews();
        return fragmentView;
    }


    @Override
    public void onStart() {
        super.onStart();
        apkAdapter.clearList();
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
        apkRecyclerView = fragmentView.findViewById(R.id.apk_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
    }

    private void initializeListeners(){
    }

    private void setupViews(){
        setupApkRecyclerView();
    }

    private void initializeData(){
        context = getActivity();
        listener = (ApkItemSelectListener) getActivity();
        apkList = new ArrayList<>();
    }

    private void listFiles(){
        apkAdapter.clearList();
        showLoading();
        listener.onApkItemSelect(new ArrayList<ApkItem>(), false);
        showAllApkFiles();
    }

    @Override
    public void onApkItemClicked(ApkItem item){
        Intent intent = new Intent();
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.getExtension());
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(item.getPath())), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            UtilityMethods.showToast(context, "Unable to open file");
            e.printStackTrace();
        }
    }

    @Override
    public void onApkItemLongClicked(List<ApkItem> item, boolean isAllItemSelected) {
        listener.onApkItemSelect(item, isAllItemSelected);
    }

    private void setupApkRecyclerView(){
        apkLayoutManager = new LinearLayoutManager(context);
        apkAdapter = new ApkAdapter(context, this);
        apkRecyclerView.setLayoutManager(apkLayoutManager);
        apkRecyclerView.setItemAnimator(new DefaultItemAnimator());
        apkRecyclerView.setAdapter(apkAdapter);
        apkAdapter.setapkList(apkList);
        // Pass the recycler Adapter your original adapter.
//        myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), apkAdapter);
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
//        apkRecyclerView.setAdapter(myMoPubAdapter);
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
    private void showAllApkFiles(){
        apkAdapter.clearList();
        AllFilesCallback callback = new AllFilesCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                apkList = (ArrayList<ApkItem>) list;
                setupApkRecyclerView();
                itemCount = list.size();
                hideLoading();
            }
        };

        new FindAllFilesAsyncTask(context, callback, AppConstants.ITEM_TYPE_APK).execute();
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

    private void onPlayerIconClicked(){
        openApk(apkAdapter.getApkList(), false, 0);
    }

    private void openApk(String[] apkList, boolean isItemClicked, int position){

    }

    @Override
    public void onDeleteItem(RealmObject item) {
        apkAdapter.deleteItem((ApkItem) item);
    }

    @Override
    public boolean isStackEmpty() {
        return true;
    }

    @Override
    public void selectAllItems(boolean value) {
        apkAdapter.selectAllItems(value);
    }

    @Override
    public boolean isItemSelected() {
        boolean itemSelected = apkAdapter.isAnyItemSelected();
        apkAdapter.selectAllItems(false);
        return itemSelected;
    }

    @Override
    public void onDeleteClicked() {
        List<String> deleteList = apkAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, ApkItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        apkAdapter.deleteAllSelectedItem();
        listener.onApkItemSelect(new ArrayList<ApkItem>(), false);
    }

    @Override
    public void onDetailClicked() {
        ApkItem selectedItem = apkAdapter.getSelectedItemList().get(0);
        DetailsDialog detailsDialog = new DetailsDialog(context, (RealmObject) selectedItem, AppConstants.ITEM_TYPE_APK);
        detailsDialog.show();
    }

    @Override
    public void onRenameClicked() {
        RenameDialog renameDialog = new RenameDialog(context, this, apkAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_APK );
        renameDialog.show();
    }

    @Override
    public void onShareClicked() {
        List<String> selectedItem = apkAdapter.getSelectedItemNameList();
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
        List<ApkItem> selectedItemList =  apkAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(ApkItem ApkItem : selectedItemList){
            ApkItem.setFavorite(true);
            selectedItemRealmList.add(ApkItem);
        }
        UtilityMethods.showToast(context, "Items have been marked favorite");
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        apkAdapter.selectAllItems(false);
        listener.onApkItemSelect(new ArrayList<ApkItem>(), false);
    }

    @Override
    public void onItemRename(ApkItem item, String oldName) {
        RealmManager.getInstance().renameObject(oldName, item, ApkItem.class);
        apkAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        apkAdapter.selectAllItems(false);
        listener.onApkItemSelect(new ArrayList<ApkItem>(), false);
    }
}
