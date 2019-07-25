package nexaplayer.mkv.mpg.flv.wmv.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.AudioItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.viewholder.FbNativeAdsViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sultan Ahmed on 12/4/2017.
 */

public class AudioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // A menu item view type.
    private static final int AUDIO_ITEM = 0;
    private static final int NATIVE_AD = 1;


    List<Object> audioList;
    Context context;
    AudioItemClickListener listener;
    SharedPreferenceManager sharedPreferenceManager;

    public AudioAdapter(Context context){
        audioList = new ArrayList<>();
        this.context = context;
        listener = (AudioItemClickListener) context;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public AudioAdapter(Context context, AudioItemClickListener listener){
        audioList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public ArrayList<AudioItem> getaudioList() {
        return getAllAudioOnly();
    }

    public void setaudioList(ArrayList<? extends Object> audioList) {
        this.audioList = (List<Object>) audioList;
        Collections.sort(getAllAudioOnly(), new AudioFileComparator());
        notifyDataSetChanged();
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName, folderName, fileDate, fileExtension, fileSize;
        public LinearLayout newItem;
        public CheckBox selectCheckBox;

        public AudioViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.file_name);
            folderName = view.findViewById(R.id.file_path);
            fileDate = view.findViewById(R.id.file_date);
            fileExtension = view.findViewById(R.id.file_extension);
            fileSize = view.findViewById(R.id.file_size);
            newItem = view.findViewById(R.id.layout_new);
            selectCheckBox = view.findViewById(R.id.select_checkbox);
        }
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NATIVE_AD:
                View nativeAdViewLayout = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.item_fb_native_ad,
                        parent, false);
                return new FbNativeAdsViewHolder(nativeAdViewLayout);
            case AUDIO_ITEM:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_audio_item, parent, false);
                return new AudioViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        setupAudioItemRealmModel((AudioViewHolder) holder,position);
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return AUDIO_ITEM;
    }

    public void clearList(){
        audioList.clear();
        notifyDataSetChanged();
    }

    private class AudioFileComparator implements Comparator<Object>{

        @Override
        public int compare(Object o1, Object o2) {
            return ((AudioItem)o1).getName().compareTo(((AudioItem)o2).getName());
        }
    }

    public String[] getAudioList(){

        List<AudioItem> tempList = getAllAudioOnly();
        String[] list = new String[tempList.size()];
        for(int i=0; i<tempList.size(); i++){
            list[i] = tempList.get(i).getPath();
        }
        return list;
    }

    public ArrayList<AudioItem> getAllAudioOnly(){
        ArrayList<AudioItem> tempList = new ArrayList<>();{
            for(Object object: audioList){
                if(object instanceof AudioItem){
                    tempList.add((AudioItem) object);
                }
            }
        }
        return tempList;
    }


    private void setupAudioItemRealmModel(AudioViewHolder holder, final int position){
        AudioViewHolder audioViewHolder = holder;
        final AudioItem audioItem = (AudioItem) audioList.get(position);
        audioViewHolder.fileName.setText(audioItem.getName());
        audioViewHolder.folderName.setText(audioItem.getPath());
        audioViewHolder.fileDate.setText(audioItem.getDateString());
        audioViewHolder.fileExtension.setText(audioItem.getExtension());
        audioViewHolder.fileSize.setText(audioItem.getSize());
        audioViewHolder.selectCheckBox.setChecked(audioItem.isSelected());

        audioViewHolder.selectCheckBox.setVisibility(isAnyItemSelected()? View.VISIBLE: View.GONE);
        audioViewHolder.folderName.setVisibility((sharedPreferenceManager.getDisplayAudioFilePath()? View.VISIBLE : View.GONE));
        audioViewHolder.fileSize.setVisibility(sharedPreferenceManager.getDisplayAudioFileSize() ? View.VISIBLE : View.GONE);
        audioViewHolder.fileDate.setVisibility(sharedPreferenceManager.getDisplayAudioFileDate() ? View.VISIBLE : View.GONE);
        audioViewHolder.fileExtension.setVisibility(sharedPreferenceManager.getDisplayAudioFileExtension() ? View.VISIBLE : View.GONE);

        audioViewHolder.newItem.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnyItemSelected()){
                    selectUnselectItem(position);
                }
                else {
                    listener.onAudioItemClicked(getAudioList(), getItemPositionInItemList((AudioItem) audioList.get(position)));
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectUnselectItem(position);
                return true;
            }
        });
    }

    private void selectUnselectItem(int position){
        AudioItem item = (AudioItem) audioList.get(position);
        item.setSelected(!item.isSelected());
        listener.onAudioItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllAudioOnly().size());
        notifyDataSetChanged();
    }



    public void deleteItem(AudioItem item){
    }

    public void deleteAllSelectedItem(){
        for(AudioItem item: getAllAudioOnly()){
            if(item.isSelected()){
                audioList.remove(item);
            }
        }
        notifyDataSetChanged();
    }


    public int getItemPositionInItemList(AudioItem item){
        List<AudioItem> tempList = getAllAudioOnly();
        for(int i=0; i<tempList.size(); i++) {
            if (item.getPath().equals(tempList.get(i).getPath())) {
                return i;
            }
        }
        return 0;
    }

    public int getItemPosition(AudioItem item){
        for(int i=0; i<audioList.size(); i++){
            Object listItem = audioList.get(i);
            if(listItem instanceof AudioItem){
                if(item.getPath().equals(((AudioItem) listItem).getPath())){
                    return i;
                }
            }
        }
        return -99;
    }

    public boolean isAnyItemSelected(){
        List<AudioItem> audioItemList = getAllAudioOnly();
        for(AudioItem item : audioItemList){
            if(item.isSelected()){
              return true;
            }
        }
        return false;
    }

    public List<AudioItem> getSelectedItemList(){
        List<AudioItem> audioItemList = new ArrayList<>();
        for(AudioItem item: getAllAudioOnly()){
            if(item.isSelected()){
                audioItemList.add(item);
            }
        }
        return audioItemList;
    }

    public List<String> getSelectedItemNameList(){
        List<String> audioItemList = new ArrayList<>();
        for(AudioItem item: getAllAudioOnly()){
            if(item.isSelected()){
                audioItemList.add(item.getPath());
            }
        }
        return audioItemList;
    }

    public void selectAllItems(boolean value){
        for(AudioItem item: getAllAudioOnly()){
            item.setSelected(value);
        }
        notifyDataSetChanged();
        listener.onAudioItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllAudioOnly().size() );
    }

    public void renameItem(String oldPath, AudioItem newItem){
        for(AudioItem item : getAllAudioOnly()){
            if(item.getPath().equals(oldPath)){
                item.setPath(newItem.getPath());
                item.setName(newItem.getName());
            }
        }
        notifyDataSetChanged();
    }
}
