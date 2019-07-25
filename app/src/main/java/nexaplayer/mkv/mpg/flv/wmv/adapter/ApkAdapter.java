package nexaplayer.mkv.mpg.flv.wmv.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ApkItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.viewholder.FbNativeAdsViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sultan Ahmed on 12/4/2017.
 */

public class ApkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    // A menu item view type.
    private static final int APK_ITEM = 0;
    private static final int NATIVE_AD = 1;


    List<Object> apkList;
    Context context;
    ApkItemClickListener listener;
    SharedPreferenceManager sharedPreferenceManager;

    public ApkAdapter(Context context){
        apkList = new ArrayList<>();
        this.context = context;
        listener = (ApkItemClickListener) context;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public ApkAdapter(Context context, ApkItemClickListener listener){
        apkList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public ArrayList<ApkItem> getapkList() {
        return getAllApkOnly();
    }

    public void setapkList(ArrayList<? extends Object> apkList) {
        this.apkList = (List<Object>) apkList;
        Collections.sort(getAllApkOnly(), new ApkAdapter.ApkFileComparator());
        notifyDataSetChanged();
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class ApkViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName, folderName, fileDate, fileExtension, fileSize;
        public CheckBox selectCheckBox;
        
        public ApkViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.file_name);
            folderName = view.findViewById(R.id.file_path);
            fileDate = view.findViewById(R.id.file_date);
            fileExtension = view.findViewById(R.id.file_extension);
            fileSize = view.findViewById(R.id.file_size);
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
            case APK_ITEM:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_apk_item, parent, false);
                return new ApkAdapter.ApkViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        setupApkItemRealmModel((ApkAdapter.ApkViewHolder) holder,position);
    }

    @Override
    public int getItemCount() {
        return apkList.size();
    }

    @Override
    public int getItemViewType(int position) {
         return APK_ITEM;
    }

    public void clearList(){
        apkList.clear();
        notifyDataSetChanged();
    }

    private class ApkFileComparator implements Comparator<Object>{

        @Override
        public int compare(Object o1, Object o2) {
            return ((ApkItem)o1).getName().compareTo(((ApkItem)o2).getName());
        }
    }

    public String[] getApkList(){

        List<ApkItem> tempList = getAllApkOnly();
        String[] list = new String[tempList.size()];
        for(int i=0; i<tempList.size(); i++){
            list[i] = tempList.get(i).getPath();
        }
        return list;
    }

    public ArrayList<ApkItem> getAllApkOnly(){
        ArrayList<ApkItem> tempList = new ArrayList<>();{
            for(Object object: apkList){
                if(object instanceof ApkItem){
                    tempList.add((ApkItem) object);
                }
            }
        }
        return tempList;
    }


    private void setupApkItemRealmModel(ApkAdapter.ApkViewHolder holder, final int position){
        ApkAdapter.ApkViewHolder apkViewHolder = holder;
        final ApkItem apkItem = (ApkItem) apkList.get(position);
        apkViewHolder.fileName.setText(apkItem.getName());
        apkViewHolder.folderName.setText(apkItem.getPath());
        apkViewHolder.fileDate.setText(apkItem.getDateString());
        apkViewHolder.fileExtension.setText(apkItem.getExtension());
        apkViewHolder.fileSize.setText(apkItem.getSize());
        apkViewHolder.selectCheckBox.setChecked(apkItem.isSelected());

        apkViewHolder.selectCheckBox.setVisibility(isAnyItemSelected()? View.VISIBLE: View.GONE);
        apkViewHolder.folderName.setVisibility(sharedPreferenceManager.getDisplayDocumentFilePath()? View.VISIBLE : View.GONE);
        apkViewHolder.fileSize.setVisibility(sharedPreferenceManager.getDisplayDocumentFileSize() ? View.VISIBLE : View.GONE);
        apkViewHolder.fileDate.setVisibility(sharedPreferenceManager.getDisplayDocumentFileDate() ? View.VISIBLE : View.GONE);
        apkViewHolder.fileExtension.setVisibility(sharedPreferenceManager.getDisplayDocumentFileExtension() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnyItemSelected()) {
                    selectUnselectItem(position);
                } else {
                    listener.onApkItemClicked((ApkItem) apkList.get(position));
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




    public void deleteItem(ApkItem item){
        RealmManager.getInstance().deleteObject("path", item.getPath(), ApkItem.class);
        int pos = getItemPositionInItemList(item);
        if(pos != -99){
            apkList.remove(pos);
            notifyDataSetChanged();
            File file = new File(item.getPath());
            file.delete();
        }
    }

    private void selectUnselectItem(int position){
        ApkItem item = (ApkItem) apkList.get(position);
        item.setSelected(!item.isSelected());
        listener.onApkItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllApkOnly().size());
        notifyDataSetChanged();
    }


    public void deleteAllSelectedItem(){
        for(ApkItem item: getAllApkOnly()){
            if(item.isSelected()){
                apkList.remove(item);
            }
        }
        notifyDataSetChanged();
    }



    public int getItemPositionInItemList(ApkItem item){
        List<ApkItem> tempList = getAllApkOnly();
        for(int i=0; i<tempList.size(); i++) {
            if (item.getPath().equals(tempList.get(i).getPath())) {
                return i;
            }
        }
        return 0;
    }

    public boolean isAnyItemSelected(){
        List<ApkItem> audioItemList = getAllApkOnly();
        for(ApkItem item : audioItemList){
            if(item.isSelected()){
                return true;
            }
        }
        return false;
    }

    public List<ApkItem> getSelectedItemList(){
        List<ApkItem> audioItemList = new ArrayList<>();
        for(ApkItem item: getAllApkOnly()){
            if(item.isSelected()){
                audioItemList.add(item);
            }
        }
        return audioItemList;
    }

    public List<String> getSelectedItemNameList(){
        List<String> audioItemList = new ArrayList<>();
        for(ApkItem item: getAllApkOnly()){
            if(item.isSelected()){
                audioItemList.add(item.getPath());
            }
        }
        return audioItemList;
    }

    public void selectAllItems(boolean value){
        for(ApkItem item: getAllApkOnly()){
            item.setSelected(value);
        }
        notifyDataSetChanged();
        listener.onApkItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllApkOnly().size() );
    }

    public void renameItem(String oldPath, ApkItem newItem){
        for(ApkItem item : getAllApkOnly()){
            if(item.getPath().equals(oldPath)){
                item.setPath(newItem.getPath());
                item.setName(newItem.getName());
            }
        }
        notifyDataSetChanged();
    }
}
