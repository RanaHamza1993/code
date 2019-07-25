package nexaplayer.mkv.mpg.flv.wmv.fragment.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.TextView;

import com.eyalbira.loadingdots.LoadingDots;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.DetailsDialog;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.RenameDialog;
import nexaplayer.mkv.mpg.flv.wmv.adapter.AudioAdapter;
import nexaplayer.mkv.mpg.flv.wmv.adapter.DocumentAdapter;
import nexaplayer.mkv.mpg.flv.wmv.adapter.ImageAdapter;
import nexaplayer.mkv.mpg.flv.wmv.adapter.VideoAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.AudioItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.DocumentItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.ImageItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.VideoItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.task.SearchAllFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchFragment extends Fragment implements TabActivityListener, RenameListener<RealmObject> {


    /*image listing types*/
    public static final String SEARCH_ITEM_TYPE = "SEARCH_ITEM_TYPE";

    private SearchView searchView;
    private RecyclerView recyclerView;
    private String itemType;
    private TextView tvItemNotFound;

    /*All adapters*/
    AudioAdapter audioAdapter;
    VideoAdapter videoAdapter;
    ImageAdapter imageAdapter;
    DocumentAdapter documentAdapter;

    /*All listeners*/
    AudioItemSelectListener audioItemSelectListener;
    VideoItemSelectListener videoItemSelectListener;
    ImageItemSelectListener imageItemSelectListener;
    DocumentItemSelectListener documentItemSelectListener;

    View fragmentView;
    Context context;
    LoadingDots loadingDots;

    private int itemCount = 0;
    boolean isActivityVisible;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(SearchFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        initalizeViews();
        initializeData();
        return fragmentView;
    }

    public void onResume() {
        super.onResume();
        isActivityVisible = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeListeners();
        audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
        isActivityVisible = true;

    }

    private void initializeListeners(){
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchBarClick();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                UtilityMethods.hideKeyboard(getActivity());
                audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
                startSearchItemsTask(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() <= 0){
                    clearRecyclerView();
                }
                return false;
            }
        });
    }

    private void startSearchItemsTask(String query){
        showLoading();
        AllFilesCallback callback = new AllFilesCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                tvItemNotFound.setVisibility(list.size() <= 0 ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(list.size() > 0 ? View.VISIBLE: View.GONE);
                setUpSearchRecyclerView(list);
                searchView.setFocusable(false);
                searchView.clearFocus();
                UtilityMethods.hideKeyboard(getActivity());
                hideLoading();
            }
        };
        clearRecyclerView();
        new SearchAllFilesAsyncTask(context, callback, itemType, query).execute();
    }

    private void onSearchBarClick(){
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        InputMethodManager im = ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE));
        im.showSoftInput(searchView, 0);
    }


    @Override
    public void onStop() {
        super.onStop();
        isActivityVisible = false;
    }

    private void initalizeViews(){
        searchView = fragmentView.findViewById(R.id.searchView);
        recyclerView = fragmentView.findViewById(R.id.search_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
        tvItemNotFound = fragmentView.findViewById(R.id.tv_items_not_found);
    }

    private void initializeData(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            itemType = bundle.getString(SEARCH_ITEM_TYPE);
        }
        context = getActivity();
        /*initializing all adapters*/
        initializeAdapters();
        /*initializing all listeners*/
        audioItemSelectListener = (AudioItemSelectListener) getActivity();
        videoItemSelectListener = (VideoItemSelectListener) getActivity();
        imageItemSelectListener = (ImageItemSelectListener) getActivity();
        documentItemSelectListener = (DocumentItemSelectListener) getActivity();
    }

    private void initializeAdapters(){
        audioAdapter = new AudioAdapter(context);
        videoAdapter = new VideoAdapter(context);
        imageAdapter = new ImageAdapter(context);
        documentAdapter = new DocumentAdapter(context);
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
        }
    }

    @Override
    public void onFavoriteClicked() {
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                markAudioItemFavorite();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                markVideoItemFavorite();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                markImageItemFavorite();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                markDocumentItemFavorite();
                break;
        }
        UtilityMethods.showToast(context, "Items have been marked favorite");
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

    private void markAudioItemFavorite(){
        List<AudioItem> selectedItemList =  audioAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(AudioItem audioItem : selectedItemList){
            audioItem.setFavorite(true);
            selectedItemRealmList.add(audioItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        audioAdapter.selectAllItems(false);
        audioItemSelectListener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
    }

    private void markVideoItemFavorite(){
        List<VideoItem> selectedItemList =  videoAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(VideoItem videoItem : selectedItemList){
            videoItem.setFavorite(true);
            selectedItemRealmList.add(videoItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        videoAdapter.selectAllItems(false);
        videoItemSelectListener.onVideoItemSelect(new ArrayList<VideoItem>(), false);
    }

    private void markImageItemFavorite(){
        List<ImageItem> selectedItemList =  imageAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(ImageItem imageItem : selectedItemList){
            imageItem.setFavorite(true);
            selectedItemRealmList.add(imageItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        imageAdapter.selectAllItems(false);
        imageItemSelectListener.onImageItemSelect(new ArrayList<ImageItem>(), false);
    }

    private void markDocumentItemFavorite(){
        List<DocumentItem> selectedItemList =  documentAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(DocumentItem documentItem : selectedItemList){
            documentItem.setFavorite(true);
            selectedItemRealmList.add(documentItem);
        }
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        documentAdapter.selectAllItems(false);
        documentItemSelectListener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
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
}
