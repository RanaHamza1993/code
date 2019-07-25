package nexaplayer.mkv.mpg.flv.wmv.fragment.audio;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eyalbira.loadingdots.LoadingDots;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.DetailsDialog;
import nexaplayer.mkv.mpg.flv.wmv.activity.dialog.RenameDialog;
import nexaplayer.mkv.mpg.flv.wmv.adapter.AudioAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.AudioItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DialogItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllFilesInFolderCallback;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.AudioItemSelectListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllFilesInFolderAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.AUDIO_FILE_INDEX_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.AUDIO_FILE_LIST_KEY;
import static nexaplayer.mkv.mpg.flv.wmv.activity.AudioPlayerActivity.IS_AUDIO_FILE_OPEN;

public class AudioFragment extends Fragment implements AudioItemClickListener, DialogItemClickListener, TabActivityListener, RenameListener<AudioItem> {

    public static final String AUDIO_PLAY_KEY = "audio_play_key";
    public static final String FOLDER_PATH_KEY = "folder_path_key";
    public static final String AUDIO_LIST_KEY = "audio_list_key";

    /*audio listing types*/
    public static final int AUDIO_PLAY_AUDIO = 0;
    public static final int AUDIO_PLAY_FOLDER = 1;
    public static final int AUDIO_PLAY_ARTIST = 2;
    public static final int AUDIO_PLAY_ALBUM = 3;

    RecyclerView audioRecyclerView;
    ImageView audioPlayerIcon;
    LinearLayoutManager audioLayoutManager;
    AudioAdapter audioAdapter;
    ArrayList<AudioItem> audioList;
    AudioItemSelectListener listener;

    private int audioPlayType;
    private String currentFolderPath = AppConstants.BASE_DIRECTORY.getPath();

    View fragmentView;
    Context context;
    LoadingDots loadingDots;

    private int itemCount = 0;
    boolean isActivityVisible;
//    private MoPubRecyclerAdapter myMoPubAdapter;
//    private RequestParameters mRequestParameters;

    public AudioFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.reportGoogleAnalytics(AudioFragment.class.getName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_audio, container, false);
        initalizeViews();
        initializeData();
        initializeListeners();
        return fragmentView;
    }

    @Override
    public void onDestroy() {
//        myMoPubAdapter.destroy();
        super.onDestroy();
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
        audioAdapter.clearList();
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
        audioRecyclerView = fragmentView.findViewById(R.id.audio_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
        audioPlayerIcon = fragmentView.findViewById(R.id.player_icon);
    }

    private void initializeListeners(){
        audioPlayerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayerIconClicked();
            }
        });
    }
    private void setupViews(){

        setupAudioRecyclerView();
    }


    private void initializeData(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            audioPlayType = bundle.getInt(AUDIO_PLAY_KEY);
            currentFolderPath = bundle.getString(FOLDER_PATH_KEY);
        }
        context = getActivity();
        listener = (AudioItemSelectListener) getActivity();
        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(context, this);
    }

    private void listFiles(){
        audioAdapter.clearList();
        showLoading();
        listener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
        switch (audioPlayType){
            case AUDIO_PLAY_AUDIO:
                showAllAudioFiles();
                break;
            case AUDIO_PLAY_FOLDER:
                showAllFilesInFolder(currentFolderPath);
                break;
            case AUDIO_PLAY_ARTIST:
            case AUDIO_PLAY_ALBUM:
                showAllArtistFiles();
                break;
            default:
                showAllAudioFiles();
        }
    }

    @Override
    public void onAudioItemClicked(String[] list, int position) {
        openAudioPlayer(list, true, position);
    }

    @Override
    public void onAudioItemLongClicked(List<AudioItem> itemList, boolean isAllItemSelected) {
       // UtilityMethods.showDetailsDialog(context, item, AppConstants.ITEM_TYPE_AUDIO, this);
        listener.onAudioItemSelect(itemList, isAllItemSelected);
        audioPlayerIcon.setVisibility(itemList.size() > 0? View.GONE : View.VISIBLE);
    }

    private void setupAudioRecyclerView(){
        audioLayoutManager = new LinearLayoutManager(context);
        audioAdapter = new AudioAdapter(context, this);
        audioRecyclerView.setLayoutManager(audioLayoutManager);
        audioRecyclerView.setItemAnimator(new DefaultItemAnimator());
        audioRecyclerView.setAdapter(audioAdapter);
        audioAdapter.setaudioList(audioList);
        if (getActivity()==null){
            return;
        }
        // Pass the recycler Adapter your original adapter.
//        myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), audioAdapter);
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
//        audioRecyclerView.setAdapter(myMoPubAdapter);
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

    private void showAllFilesInFolder(String directoryPath){
        audioAdapter.clearList();
        audioPlayerIcon.setVisibility(View.GONE);
        AllFilesInFolderCallback callback = new AllFilesInFolderCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                audioList = (ArrayList<AudioItem>)list;
                itemCount = list.size();
                setupAudioRecyclerView();
                hideLoading();
                audioPlayerIcon.setVisibility(View.VISIBLE);

            }
        };
        new FindAllFilesInFolderAsyncTask(context, callback, directoryPath, AppConstants.ITEM_TYPE_AUDIO).execute();
      }

    private void showAllArtistFiles(){
        audioAdapter.clearList();
        audioPlayerIcon.setVisibility(View.GONE);
        Bundle arg = getArguments();
        audioList = ItemUtils.convertStringArrayToAudioList(context,arg.getStringArray(AUDIO_LIST_KEY));
        setupAudioRecyclerView();
        itemCount = audioList.size();
        hideLoading();
        audioPlayerIcon.setVisibility(View.VISIBLE);
    }

    private void showAllAudioFiles(){
        audioAdapter.clearList();
        audioPlayerIcon.setVisibility(View.GONE);
        AllFilesCallback callback = new AllFilesCallback() {
            @Override
            public void onSuccess(List<? extends RealmObject> list) {
                audioList = (ArrayList<AudioItem>) list;
                setupAudioRecyclerView();
                itemCount = list.size();
                hideLoading();
                audioPlayerIcon.setVisibility(View.VISIBLE);
            }
        };

        new FindAllFilesAsyncTask(context, callback, AppConstants.ITEM_TYPE_AUDIO).execute();
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
        openAudioPlayer(audioAdapter.getAudioList(), false, 0);
    }

    private void openAudioPlayer(String[] audioList, boolean isItemClicked, int position){
        Intent audioPlayerIntent = new Intent(context, AudioPlayerActivity.class);
        audioPlayerIntent.putExtra(AUDIO_FILE_LIST_KEY, audioList);
        audioPlayerIntent.putExtra(AUDIO_FILE_INDEX_KEY, position);
        audioPlayerIntent.putExtra(IS_AUDIO_FILE_OPEN, isItemClicked);
        startActivity(audioPlayerIntent);

    }

    @Override
    public void onDeleteItem(RealmObject item) {
        audioAdapter.deleteItem((AudioItem) item);
    }

    @Override
    public boolean isStackEmpty() {
        return true;
    }

    @Override
    public void selectAllItems(boolean value) {
        audioAdapter.selectAllItems(value);
        audioPlayerIcon.setVisibility(value ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean isItemSelected() {
        boolean itemSelected = audioAdapter.isAnyItemSelected();
        audioAdapter.selectAllItems(false);
        return itemSelected;
    }

    @Override
    public void onDeleteClicked() {
        List<String> deleteList = audioAdapter.getSelectedItemNameList();
        RealmManager.getInstance().deleteAllSelectedObject("path", deleteList, AudioItem.class);
        for(String item : deleteList){
            new File(item).delete();
        }
        audioAdapter.deleteAllSelectedItem();
        listener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
    }

    @Override
    public void onDetailClicked() {
        AudioItem selectedItem = audioAdapter.getSelectedItemList().get(0);
        DetailsDialog detailsDialog = new DetailsDialog(context, (RealmObject) selectedItem, AppConstants.ITEM_TYPE_AUDIO);
        detailsDialog.show();
    }

    @Override
    public void onRenameClicked() {
        RenameDialog renameDialog = new RenameDialog(context, this, audioAdapter.getSelectedItemList().get(0), AppConstants.ITEM_TYPE_AUDIO );
        renameDialog.show();
    }

    @Override
    public void onShareClicked() {
        List<String> selectedItem = audioAdapter.getSelectedItemNameList();
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
        List<AudioItem> selectedItemList =  audioAdapter.getSelectedItemList();
        List<RealmObject> selectedItemRealmList =  new ArrayList<>();
        for(AudioItem audioItem : selectedItemList){
            audioItem.setFavorite(true);
            selectedItemRealmList.add(audioItem);
        }
        UtilityMethods.showToast(context, "Items have been marked favorite");
        UtilityMethods.showToast(context, "Items have been marked favorite");
        RealmManager.getInstance().bulkInsertList(selectedItemRealmList);
        audioAdapter.selectAllItems(false);
        listener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
    }

    @Override
    public void onItemRename(AudioItem item, String oldName) {
        RealmManager.getInstance().renameObject(oldName, item, AudioItem.class);
        audioAdapter.renameItem(oldName, item);
        File oldFile = new File(oldName);
        File newFile = new File(item.getPath());
        oldFile.renameTo(newFile);
        audioAdapter.selectAllItems(false);
        listener.onAudioItemSelect(new ArrayList<AudioItem>(), false);
    }
}
