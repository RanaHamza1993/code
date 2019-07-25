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

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.DetailsDialog;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.RenameDialog;
import nexaplayer.mkv.mpg.flv.wmv.adapter.DocumentAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DialogItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DocumentItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesInFolderCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.DocumentItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesInFolderAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class DocumentFragment extends Fragment implements DocumentItemClickListener, DialogItemClickListener, TabActivityListener, RenameListener<DocumentItem> {

    public static final String DOCUMENT_SHOW_KEY = "document_show_key";
    public static final String FOLDER_PATH_KEY = "folder_path_key";
    public static final String DOCUMENT_LIST_KEY = "document_list_key";

    /*document listing types*/
    public static final int DOCUMENT_SHOW_DOCUMENT = 0;
    public static final int DOCUMENT_SHOW_FOLDER = 1;

    RecyclerView documentRecyclerView;
    LinearLayoutManager documentLayoutManager;
    DocumentAdapter documentAdapter;
    ArrayList<DocumentItem> documentList;
    DocumentItemSelectListener listener;

    private int documentPlayType;
    private String currentFolderPath = AppConstants.BASE_DIRECTORY.getPath();

    View fragmentView;
    Context context;
    LoadingDots loadingDots;

    private int itemCount = 0;
    boolean isActivityVisible;
//    private MoPubRecyclerAdapter myMoPubAdapter;
//    private RequestParameters mRequestParameters;

    public DocumentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(DocumentFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_document, container, false);
        initalizeViews();
        initializeData();
        initializeListeners();

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
        documentAdapter.clearList();
        setupViews();
        listFiles();
        isActivityVisible = true;

    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityVisible = false;
    }

    private void initalizeViews() {
        documentRecyclerView = fragmentView.findViewById(R.id.document_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
    }

    private void initializeListeners() {
    }

    private void setupViews() {

        setupDocumentRecyclerView();
    }


    private void initializeData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            documentPlayType = bundle.getInt(DOCUMENT_SHOW_KEY);
            currentFolderPath = bundle.getString(FOLDER_PATH_KEY);
        }
        context = getActivity();
        listener = (DocumentItemSelectListener) getActivity();
        documentList = new ArrayList<>();
    }

    private void listFiles() {
        documentAdapter.clearList();
        showLoading();
        listener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
        switch (documentPlayType) {
            case DOCUMENT_SHOW_DOCUMENT:
                showAllDocumentFiles();
                break;
            case DOCUMENT_SHOW_FOLDER:
                showAllFilesInFolder(currentFolderPath);
                break;
            default:
                showAllDocumentFiles();
        }
    }

    @Override
    public void onDocumentItemClicked(DocumentItem item) {
        openDocument(item);
    }

    @Override
    public void onDocumentItemLongClicked(List<DocumentItem> item, boolean isAllItemSelected) {
        listener.onDocumentItemSelect(item, isAllItemSelected);
    }

    private void setupDocumentRecyclerView() {
        documentLayoutManager = new LinearLayoutManager(context);
        documentAdapter = new DocumentAdapter(context, this);
        documentRecyclerView.setLayoutManager(documentLayoutManager);
        documentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        documentRecyclerView.setAdapter(documentAdapter);
        documentAdapter.setdocumentList(documentList);

//        // Pass the recycler Adapter your original adapter.
//        myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), documentAdapter);
//        // Create an ad renderer and view binder that describe your native ad layout.
//        ViewBinder myViewBinder = new ViewBinder.Builder(R.layout.item_mobpub_native_ad)
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
//        documentRecyclerView.setAdapter(myMoPubAdapter);
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

    private void showAllFilesInFolder(String directoryPath) {
        documentAdapter.clearList();
        AllFilesInFolderCallback callback = new AllFilesInFolderCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                documentList = (ArrayList<DocumentItem>) list;
                itemCount = list.size();
                setupDocumentRecyclerView();
                hideLoading();

            }
        };
        new FindAllFilesInFolderAsyncTask(context, callback, directoryPath, AppConstants.ITEM_TYPE_DOCUMENT).execute();
    }

    private void showAllDocumentFiles() {
        documentAdapter.clearList();
        AllFilesCallback callback = new AllFilesCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                documentList = (ArrayList<DocumentItem>) list;
                setupDocumentRecyclerView();
                itemCount = list.size();
                hideLoading();
            }
        };

        new FindAllFilesAsyncTask(context, callback, AppConstants.ITEM_TYPE_DOCUMENT).execute();
    }

    @Override
    public void onDestroy() {
//        myMoPubAdapter.destroy();
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void showLoading() {
        loadingDots.setVisibility(View.VISIBLE);
        loadingDots.startAnimation();
    }

    private void hideLoading() {
        loadingDots.setVisibility(View.GONE);
        loadingDots.stopAnimation();
    }

    private void openDocument(DocumentItem item) {
        Intent intent = new Intent();
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.getExtension());
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(item.getPath())), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(Intent.createChooser(intent, "Open with"));
        } catch (Exception e) {
            UtilityMethods.showToast(context, "Unable to open file");
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleteItem(RealmObject item) {
        documentAdapter.deleteItem((DocumentItem) item);
    }

    @Override
    public boolean isStackEmpty() {
        return true;
    }

    @Override
    public void selectAllItems(boolean value) {
        documentAdapter.selectAllItems(value);
    }


    @Override
    public boolean isItemSelected() {
        boolean itemSelected = documentAdapter.isAnyItemSelected();
        documentAdapter.selectAllItems(false);
        return itemSelected;
    }

    @Override
    public void onDeleteClicked() {
        List<String> deleteList = documentAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, DocumentItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        documentAdapter.deleteAllSelectedItem();
        listener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
    }

    @Override
    public void onDetailClicked() {
        DocumentItem selectedItem = documentAdapter.getSelectedItemList().get(0);
        DetailsDialog detailsDialog = new DetailsDialog(context, (RealmObject) selectedItem, AppConstants.ITEM_TYPE_DOCUMENT);
        detailsDialog.show();
    }

    @Override
    public void onRenameClicked() {
        RenameDialog renameDialog = new RenameDialog(context, this, documentAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_DOCUMENT );
        renameDialog.show();
    }

    @Override
    public void onShareClicked() {
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

    @Override
    public void onFavoriteClicked() {
        List<DocumentItem> selectedItemList =  documentAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(DocumentItem DocumentItem : selectedItemList){
            DocumentItem.setFavorite(true);
            selectedItemRealmList.add(DocumentItem);
        }
        UtilityMethods.showToast(context, "Items have been marked favorite");
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        documentAdapter.selectAllItems(false);
        listener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
    }

    @Override
    public void onItemRename(DocumentItem item, String oldName) {
        RealmManager.getInstance().renameObject(oldName, item, DocumentItem.class);
        documentAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        documentAdapter.selectAllItems(false);
        listener.onDocumentItemSelect(new ArrayList<DocumentItem>(), false);
    }
}
