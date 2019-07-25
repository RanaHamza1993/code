package nexaplayer.mkv.mpg.flv.wmv.fragment.favorite;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyalbira.loadingdots.LoadingDots;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.DetailsDialog;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.RenameDialog;
import nexaplayer.mkv.mpg.flv.wmv.adapter.ApkAdapter;
import nexaplayer.mkv.mpg.flv.wmv.adapter.AudioAdapter;
import nexaplayer.mkv.mpg.flv.wmv.adapter.DocumentAdapter;
import nexaplayer.mkv.mpg.flv.wmv.adapter.ImageAdapter;
import nexaplayer.mkv.mpg.flv.wmv.adapter.VideoAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ApkItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.AudioItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.DocumentItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ImageItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.VideoItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FavoriteFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class FavoriteFragment extends Fragment implements TabActivityListener, RenameListener<RealmObject> {


    /*image listing types*/
    public static final String FAVORITE_ITEM_TYPE = "FAVORITE_ITEM_TYPE";

    private RecyclerView recyclerView;
    private String itemType;
    private TextView tvItemNotFound;

    /*All adapters*/
    AudioAdapter audioAdapter;
    VideoAdapter videoAdapter;
    ImageAdapter imageAdapter;
    DocumentAdapter documentAdapter;
    ApkAdapter apkAdapter;

    /*All listeners*/
    AudioItemSelectListener audioItemSelectListener;
    VideoItemSelectListener videoItemSelectListener;
    ImageItemSelectListener imageItemSelectListener;
    DocumentItemSelectListener documentItemSelectListener;
    ApkItemSelectListener apkItemSelectListener;

    View fragmentView;
    Context context;
    LoadingDots loadingDots;

    private int itemCount = 0;
    boolean isActivityVisible;

    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(FavoriteFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_favorite, container, false);
        initalizeViews();
        initializeData();
        return fragmentView;
    }

    public void onResume() {
        super.onResume();
        UtilityMethods.hideKeyboard(getActivity());
        isActivityVisible = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeListeners();
        clearRecyclerView();
        audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
        listFavoriteFiles();
        isActivityVisible = true;

    }

    private void initializeListeners(){
        
    }

    private void listFavoriteFiles(){
        showLoading();
        AllFilesCallback callback = new AllFilesCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                setRecyclerViewVisibility(list);
                setUpSearchRecyclerView(list);
                itemCount = list.size();
                hideLoading();
            }
        };
        clearRecyclerView();
        new FavoriteFilesAsyncTask(context, callback, itemType).execute();
    }

    private void setRecyclerViewVisibility(List<? extends RealmObject> list){
        tvItemNotFound.setVisibility(list.size() <= 0 ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(list.size() > 0 ? View.VISIBLE: View.GONE);
    }


    @Override
    public void onStop() {
        super.onStop();
        isActivityVisible = false;
    }

    private void initalizeViews(){
        recyclerView = fragmentView.findViewById(R.id.search_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
        tvItemNotFound = fragmentView.findViewById(R.id.tv_items_not_found);
    }

    private void initializeData(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            itemType = bundle.getString(FAVORITE_ITEM_TYPE);
        }
        context = getActivity();
        /*initializing all adapters*/
        initializeAdapters();
        /*initializing all listeners*/
        audioItemSelectListener = (AudioItemSelectListener) getActivity();
        videoItemSelectListener = (VideoItemSelectListener) getActivity();
        imageItemSelectListener = (ImageItemSelectListener) getActivity();
        documentItemSelectListener = (DocumentItemSelectListener) getActivity();
        apkItemSelectListener = (ApkItemSelectListener) getActivity();
    }

    private void initializeAdapters(){
        audioAdapter = new AudioAdapter(context);
        videoAdapter = new VideoAdapter(context);
        imageAdapter = new ImageAdapter(context);
        documentAdapter = new DocumentAdapter(context);
        apkAdapter = new ApkAdapter(context);
    }

    private void setUpSearchRecyclerView(List<? extends RealmObject> itemList){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.Adapter adapter = initialzeAndGetAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }

    private void clearRecyclerView(){
        recyclerView.setAdapter(null);
        initializeAdapters();
        audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
        tvItemNotFound.setVisibility(View.GONE);
    }


    private RecyclerView.Adapter initialzeAndGetAdapter(List<?extends RealmObject> list){
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                audioAdapter.setaudioList((ArrayList<AudioItem>) list);
                return audioAdapter;
            case AppConstants.ITEM_TYPE_VIDEO:
                videoAdapter.setVideoList((ArrayList<VideoItem>) list);
                return videoAdapter;
            case AppConstants.ITEM_TYPE_IMAGE:
                imageAdapter.setImageList((ArrayList<ImageItem>) list);
                return imageAdapter;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                documentAdapter.setdocumentList((ArrayList<DocumentItem>) list);
                return documentAdapter;
            case AppConstants.ITEM_TYPE_APK:
                apkAdapter.setapkList((ArrayList<ApkItem>) list);
                return apkAdapter;
        }
        return  null;
    }

    private RecyclerView.Adapter getAdapter(){
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                return audioAdapter;
            case AppConstants.ITEM_TYPE_VIDEO:
                return videoAdapter;
            case AppConstants.ITEM_TYPE_IMAGE:
                return imageAdapter;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                return documentAdapter;
            case AppConstants.ITEM_TYPE_APK:
                return apkAdapter;
        }
        return  null;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
    public boolean isStackEmpty() {
        return true;
    }

    @Override
    public void selectAllItems(boolean value) {
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                audioAdapter.selectAllItems(value);
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                videoAdapter.selectAllItems(value);
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                imageAdapter.selectAllItems(value);
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                documentAdapter.selectAllItems(value);
                break;
            case AppConstants.ITEM_TYPE_APK:
                apkAdapter.selectAllItems(value);
                break;
        }
    }

    @Override
    public boolean isItemSelected() {
        boolean itemSelected = false;
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                itemSelected = audioAdapter.isAnyItemSelected();
                audioAdapter.selectAllItems(false);
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                itemSelected = videoAdapter.isAnyItemSelected();
                videoAdapter.selectAllItems(false);
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                itemSelected = imageAdapter.isAnyItemSelected();
                imageAdapter.selectAllItems(false);
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                itemSelected = documentAdapter.isAnyItemSelected();
                documentAdapter.selectAllItems(false);
                break;
            case AppConstants.ITEM_TYPE_APK:
                itemSelected = apkAdapter.isAnyItemSelected();
                apkAdapter.selectAllItems(false);
                break;
        }
        return itemSelected;
    }

    @Override
    public void onDeleteClicked() {
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                onAudioDelete();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                onVideoDelete();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                onImageDelete();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                onDocumentDelete();
                break;
            case AppConstants.ITEM_TYPE_APK:
                onApkDelete();
                break;
        }
    }

    @Override
    public void onDetailClicked() {
        DetailsDialog detailsDialog;
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                AudioItem audioItem = audioAdapter.getSelectedItemList().get(0);
                detailsDialog = new DetailsDialog(context, (RealmObject) audioItem, AppConstants.ITEM_TYPE_AUDIO);
                detailsDialog.show();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                VideoItem videoItem = videoAdapter.getSelectedItemList().get(0);
                detailsDialog = new DetailsDialog(context, (RealmObject) videoItem, AppConstants.ITEM_TYPE_VIDEO);
                detailsDialog.show();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                ImageItem imageItem = imageAdapter.getSelectedItemList().get(0);
                detailsDialog = new DetailsDialog(context, (RealmObject) imageItem, AppConstants.ITEM_TYPE_IMAGE);
                detailsDialog.show();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                DocumentItem documentItem = documentAdapter.getSelectedItemList().get(0);
                detailsDialog = new DetailsDialog(context, (RealmObject) documentItem, AppConstants.ITEM_TYPE_DOCUMENT);
                detailsDialog.show();
                break;
            case AppConstants.ITEM_TYPE_APK:
                ApkItem apkItem = apkAdapter.getSelectedItemList().get(0);
                detailsDialog = new DetailsDialog(context, (RealmObject) apkItem, AppConstants.ITEM_TYPE_APK);
                detailsDialog.show();
                break;
        }

    }

    @Override
    public void onRenameClicked() {
        RenameDialog renameDialog;
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                renameDialog = new RenameDialog(context, this, audioAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_AUDIO );
                renameDialog.show();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                renameDialog = new RenameDialog(context, this, videoAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_VIDEO );
                renameDialog.show();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                renameDialog = new RenameDialog(context, this, imageAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_IMAGE );
                renameDialog.show();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                renameDialog = new RenameDialog(context, this, documentAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_DOCUMENT );
                renameDialog.show();
                break;
            case AppConstants.ITEM_TYPE_APK:
                renameDialog = new RenameDialog(context, this, apkAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_APK );
                renameDialog.show();
                break;
        }
    }

    @Override
    public void onShareClicked() {
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                onAudioShare();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                onVideoShare();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                onImageShare();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                onDocumentShare();
                break;
            case AppConstants.ITEM_TYPE_APK:
                onApkShare();
                break;
        }
    }

    @Override
    public void onFavoriteClicked() {
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                markAudioItemUnFavorite();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                markVideoItemUnFavorite();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                markImageItemUnFavorite();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                markDocumentItemUnFavorite();
                break;
            case AppConstants.ITEM_TYPE_APK:
                markApkItemUnFavorite();
                break;
        }
        UtilityMethods.showToast(context, "Items have been marked unfavorite");
    }

    @Override
    public void onItemRename(RealmObject object, String oldName) {
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                audioItemRename((AudioItem) object, oldName);
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                videoItemRename((VideoItem) object, oldName);
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                imageItemRename((ImageItem) object, oldName);
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                documentItemRename((DocumentItem) object, oldName);
                break;
            case AppConstants.ITEM_TYPE_APK:
                apkItemRename((ApkItem) object, oldName);
                break;
        }
    }




    private void onAudioDelete(){
        List<String> deleteList = audioAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, AudioItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        audioAdapter.deleteAllSelectedItem();
        audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
    }

    private void onVideoDelete(){
        List<String> deleteList = videoAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, VideoItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        videoAdapter.deleteAllSelectedItem();
        videoItemSelectListener.onVideoItemSelect(new ArrayList<VideoItem>(), false);
    }

    private void onImageDelete(){
        List<String> deleteList = imageAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, ImageItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        imageAdapter.deleteAllSelectedItem();
        imageItemSelectListener.onImageItemSelect(new ArrayList<ImageItem>(), false);
    }

    private void onDocumentDelete(){
        List<String> deleteList = documentAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, DocumentItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        documentAdapter.deleteAllSelectedItem();
        documentItemSelectListener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
    }
    
    private void onApkDelete(){
        List<String> deleteList = apkAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, ApkItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        apkAdapter.deleteAllSelectedItem();
        apkItemSelectListener.onApkItemSelect(new ArrayList<ApkItem>(), false);
    }

    public void onDocumentShare() {
        List<String> selectedItem = documentAdapter.getSelectedItemNameList();
        ArrayList<Uri> selectedItemUri = new ArrayList<>();
        for(String item: selectedItem){
            selectedItemUri.add(Uri.parse(item));
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedItemUri);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Document");
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }
    
    public void onApkShare() {
        List<String> selectedItem = apkAdapter.getSelectedItemNameList();
        ArrayList<Uri> selectedItemUri = new ArrayList<>();
        for(String item: selectedItem){
            selectedItemUri.add(Uri.parse(item));
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedItemUri);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Apk");
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    public void onImageShare() {
        List<String> selectedItem = imageAdapter.getSelectedItemNameList();
        ArrayList<Uri> selectedItemUri = new ArrayList<>();
        for(String item: selectedItem){
            selectedItemUri.add(Uri.parse(item));
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedItemUri);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Image");
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }


    public void onVideoShare() {
        List<String> selectedItem = videoAdapter.getSelectedItemNameList();
        ArrayList<Uri> selectedItemUri = new ArrayList<>();
        for(String item: selectedItem){
            selectedItemUri.add(Uri.parse(item));
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedItemUri);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }


    public void onAudioShare() {
        List<String> selectedItem = audioAdapter.getSelectedItemNameList();
        ArrayList<Uri> selectedItemUri = new ArrayList<>();
        for(String item: selectedItem){
            selectedItemUri.add(Uri.parse(item));
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedItemUri);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Audio");
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    private void markAudioItemUnFavorite(){
        List<AudioItem> selectedItemList =  audioAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(AudioItem audioItem : selectedItemList){
            audioItem.setFavorite(false);
            selectedItemRealmList.add(audioItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        audioAdapter.deleteAllSelectedItem();
        audioAdapter.selectAllItems(false);
        setRecyclerViewVisibility(audioAdapter.getAllAudioOnly());
        audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
    }

    private void markVideoItemUnFavorite(){
        List<VideoItem> selectedItemList =  videoAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(VideoItem videoItem : selectedItemList){
            videoItem.setFavorite(false);
            selectedItemRealmList.add(videoItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        videoAdapter.deleteAllSelectedItem();
        videoAdapter.selectAllItems(false);
        setRecyclerViewVisibility(videoAdapter.getAllVideoOnly());
        videoItemSelectListener.onVideoItemSelect(new ArrayList<VideoItem>(), false);
    }

    private void markImageItemUnFavorite(){
        List<ImageItem> selectedItemList =  imageAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(ImageItem imageItem : selectedItemList){
            imageItem.setFavorite(false);
            selectedItemRealmList.add(imageItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        imageAdapter.deleteAllSelectedItem();
        imageAdapter.selectAllItems(false);
        setRecyclerViewVisibility(imageAdapter.getAllImagesOnly());
        imageItemSelectListener.onImageItemSelect(new ArrayList<ImageItem>(), false);
    }

    private void markDocumentItemUnFavorite(){
        List<DocumentItem> selectedItemList =  documentAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(DocumentItem documentItem : selectedItemList){
            documentItem.setFavorite(false);
            selectedItemRealmList.add(documentItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        documentAdapter.deleteAllSelectedItem();
        documentAdapter.selectAllItems(false);
        setRecyclerViewVisibility(documentAdapter.getAllDocumentOnly());
        documentItemSelectListener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
    }
    
    private void markApkItemUnFavorite(){
        List<ApkItem> selectedItemList =  apkAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(ApkItem documentItem : selectedItemList){
            documentItem.setFavorite(false);
            selectedItemRealmList.add(documentItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        apkAdapter.deleteAllSelectedItem();
        apkAdapter.selectAllItems(false);
        setRecyclerViewVisibility(apkAdapter.getapkList());
        apkItemSelectListener.onApkItemSelect(new ArrayList<ApkItem>(), false);
    }


    private void audioItemRename(AudioItem item, String oldName){
        RealmManager.getInstance().renameObject(oldName, item, AudioItem.class);
        audioAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        audioAdapter.selectAllItems(false);
        audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
    }
    private void videoItemRename(VideoItem item, String oldName){
        RealmManager.getInstance().renameObject(oldName, item, VideoItem.class);
        videoAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        videoAdapter.selectAllItems(false);
        videoItemSelectListener.onVideoItemSelect(new ArrayList<VideoItem>(), false);
    }
    private void imageItemRename(ImageItem item, String oldName){
        RealmManager.getInstance().renameObject(oldName, item, ImageItem.class);
        imageAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        imageAdapter.selectAllItems(false);
        imageItemSelectListener.onImageItemSelect(new ArrayList<ImageItem>(), false);
    }
    private void documentItemRename(DocumentItem item, String oldName){
        RealmManager.getInstance().renameObject(oldName, item, DocumentItem.class);
        documentAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        documentAdapter.selectAllItems(false);
        documentItemSelectListener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
    }
    
    private void apkItemRename(ApkItem item, String oldName){
        RealmManager.getInstance().renameObject(oldName, item, ApkItem.class);
        apkAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        apkAdapter.selectAllItems(false);
        apkItemSelectListener.onApkItemSelect(new ArrayList<ApkItem>(), false);
    }
}
