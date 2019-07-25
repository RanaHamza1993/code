package nexaplayer.mkv.mpg.flv.wmv.fragment.audio;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eyalbira.loadingdots.LoadingDots;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.adapter.ArtistAdapter;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ArtistFragmentListeners;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ArtistItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.TabActivityListener;
import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.AllArtistCallback;
import nexaplayer.mkv.mpg.flv.wmv.model.ArtistItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllArtistAsyncTask;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AudioArtistFragment extends Fragment implements ArtistItemClickListener, TabActivityListener {


    RecyclerView folderRecyclerView;
    LinearLayoutManager folderLayoutManager;
    ArtistAdapter artistAdapter;
    LoadingDots loadingDots;

    Stack<String> folderStack;
    String currentFolderPath;

    MultiMap<String, List<AudioItem>> artistMap;
    ArrayList<ArtistItem> artistList;
    ArtistFragmentListeners listeners;

    View fragmentView;
    Context context;

    private int itemCount = 0;

    boolean isActivityVisible = false;
//    private MoPubRecyclerAdapter myMoPubAdapter;
//    private RequestParameters mRequestParameters;

    public AudioArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_audio_artist, container, false);
        initalizeViews();
        initializeData();
        setupViews();
        listFiles();
        return fragmentView;

    }

    @Override
    public void onStart() {
        Log.e("AudioArtistFragment","onstartcalled");
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

    private void initalizeViews(){
        folderRecyclerView = fragmentView.findViewById(R.id.artist_recyclerview);
        loadingDots = fragmentView.findViewById(R.id.loading);
    }

    private void setupViews(){
        setupArtistRecyclerView();
    }


    private void initializeData(){
        context = getActivity();
        folderStack = new Stack<>();
        artistMap = new MultiValueMap<>();
        artistList = new ArrayList<>();
        currentFolderPath = AppConstants.BASE_DIRECTORY.getAbsolutePath();
        Bundle bundle = getArguments();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listeners = (ArtistFragmentListeners) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listeners = null;
    }

    private void listFiles(){
        artistAdapter.clearList();
        showLoading();
        showAllItemsArtists();
    }

    private void setupArtistRecyclerView(){
        folderLayoutManager = new LinearLayoutManager(context);
        artistAdapter = new ArtistAdapter(context, ArtistAdapter.VIEW_TYPE_ARTIST, this);
        folderRecyclerView.setLayoutManager(folderLayoutManager);
        folderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        folderRecyclerView.setAdapter(artistAdapter);
        artistAdapter.setArtistList(artistList);
        // Pass the recycler Adapter your original adapter.
//        myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), artistAdapter);
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

    private void showAllItemsArtists() {
        artistList.clear();
        AllArtistCallback callback = new AllArtistCallback() {
            @Override
            public void onSuccess(MultiMap<String, List<AudioItem>> map) {
                artistMap = map;
                for(String key: map.keySet()){
                    ArrayList<AudioItem> audioList = (ArrayList<AudioItem>) map.get(key);
                    artistList.add(new ArtistItem(key, audioList.size()));
                }
                setupArtistRecyclerView();
                hideLoading();
                itemCount = artistList.size();
            }
        };
        new FindAllArtistAsyncTask(context, callback)
                .execute();
    }

    @Override
    public void onArtistItemClicked(ArtistItem item) {
        folderStack.push(item.getName());
        showFilesInArtist(ItemUtils.convertAudioFilesToStringArray((List<AudioItem>)artistMap.get(item.getName())));
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

    private void showFilesInArtist(String[] list){
        artistAdapter.clearList();
        listeners.onArtistItemClicked(list);
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
