package nexaplayer.mkv.mpg.flv.wmv.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.ImageItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;
import nexaplayer.mkv.mpg.flv.wmv.viewholder.FbNativeAdsViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sultan Ahmed on 12/4/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Object> imageList;
    Context context;
    ImageItemClickListener listener;
    SharedPreferenceManager sharedPreferenceManager;

    private static final int IMAGE_ITEM = 0;
    private static final int NATIVE_AD = 1;

    public ImageAdapter(Context context) {
        imageList = new ArrayList<>();
        this.context = context;
        listener = (ImageItemClickListener) context;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public ImageAdapter(Context context, ImageItemClickListener listener) {
        imageList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public ArrayList<ImageItem> getImageList() {
        return getAllImagesOnly();
    }

    public void setImageList(ArrayList<? extends Object> imageList) {
        this.imageList = (ArrayList<Object>) imageList;
        Collections.sort(getAllImagesOnly(), new ImageFileComparator());
        notifyDataSetChanged();
    }


    public ArrayList<ImageItem> getAllImagesOnly() {
        ArrayList<ImageItem> tempList = new ArrayList<>();
        {
            for (Object object : imageList) {
                if (object instanceof ImageItem) {
                    tempList.add((ImageItem) object);
                }
            }
        }
        return tempList;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName, folderName, fileDate, fileExtension, fileSize, dimensions;
        public ImageView thumbnail;
        public LinearLayout newItem;
        public CheckBox selectCheckBox;

        public ImageViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.file_name);
            folderName = view.findViewById(R.id.file_path);
            fileDate = view.findViewById(R.id.file_date);
            fileExtension = view.findViewById(R.id.file_extension);
            fileSize = view.findViewById(R.id.file_size);
            newItem = view.findViewById(R.id.layout_new);
            thumbnail = view.findViewById(R.id.image_thumbnail);
            dimensions = view.findViewById(R.id.file_dimension);
            selectCheckBox = view.findViewById(R.id.select_checkbox);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NATIVE_AD:
            default:
                View nativeAdViewLayout = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.item_fb_native_ad,
                        parent, false);
                return new FbNativeAdsViewHolder(nativeAdViewLayout);
            case IMAGE_ITEM:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_image_item, parent, false);

                return new ImageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        setupImageItem((ImageViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return IMAGE_ITEM;
    }
    public void clearList() {
        imageList.clear();
        notifyDataSetChanged();
    }

    private class ImageFileComparator implements Comparator<ImageItem> {

        @Override
        public int compare(ImageItem o1, ImageItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public String[] getImageNameList() {
        List<ImageItem> tempList = getAllImagesOnly();
        String[] list = new String[tempList.size()];
        for (int i = 0; i < tempList.size(); i++) {
            list[i] = tempList.get(i).getPath();
        }
        return list;
    }


    public void deleteItem(ImageItem item) {
        RealmManager.getInstance().deleteObject("path", item.getPath(), ImageItem.class);
        int pos = getItemPosition(item);
        if (pos != -99) {
            imageList.remove(pos);
            notifyDataSetChanged();
            File file = new File(item.getPath());
            file.delete();
        }
    }
    
    public int getItemPositionInItemList(ImageItem item){
        List<ImageItem> tempList = getAllImagesOnly();
        for(int i=0; i<tempList.size(); i++) {
            if (item.getPath().equals(tempList.get(i).getPath())) {
                return i;
            }
        }
        return 0;
    }

    public int getItemPosition(ImageItem item) {
        for (int i = 0; i < imageList.size(); i++) {
            Object listItem = imageList.get(i);
            if (listItem instanceof ImageItem) {
                if (item.getPath().equals(((ImageItem) listItem).getPath())) {
                    return i;
                }
            }
        }
        return -99;
    }





    private void setupImageItem(ImageViewHolder holder, final int position) {
        final ImageItem imageItem = (ImageItem) imageList.get(position);
        ItemUtils.setMetaDataOnImageItem(imageItem, context);
        holder.fileName.setText(imageItem.getName());
        holder.folderName.setText(imageItem.getPath());
        holder.fileDate.setText(imageItem.getDateString());
        holder.fileExtension.setText(imageItem.getExtension());
        holder.fileSize.setText(imageItem.getSize());
        holder.dimensions.setText(imageItem.getWidth() + "x" + imageItem.getHeight());
        holder.selectCheckBox.setChecked(imageItem.isSelected());

        Glide.with(context).load(imageItem.getPath()).thumbnail(0.2F).into(holder.thumbnail);
        holder.selectCheckBox.setVisibility(isAnyItemSelected()? View.VISIBLE: View.GONE);
        holder.folderName.setVisibility(sharedPreferenceManager.getDisplayImageFilePath()? View.VISIBLE : View.GONE);
        holder.fileSize.setVisibility(sharedPreferenceManager.getDisplayImageFileSize() ? View.VISIBLE : View.GONE);
        holder.fileDate.setVisibility(sharedPreferenceManager.getDisplayImageFileDate() ? View.VISIBLE : View.GONE);
        holder.fileExtension.setVisibility(sharedPreferenceManager.getDisplayImageFileExtension() ? View.VISIBLE : View.GONE);
        holder.dimensions.setVisibility(sharedPreferenceManager.getDisplayImageFileResolution() ? View.VISIBLE : View.GONE);
        holder.newItem.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnyItemSelected()) {
                    selectUnselectItem(position);
                } else {
                    listener.onImageItemClicked(getImageNameList(), getItemPositionInItemList((ImageItem) imageList.get(position)));
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
        ImageItem item = (ImageItem) imageList.get(position);
        item.setSelected(!item.isSelected());
        listener.onImageItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllImagesOnly().size());
        notifyDataSetChanged();
    }


    public void deleteAllSelectedItem(){
        for(ImageItem item: getAllImagesOnly()){
            if(item.isSelected()){
                imageList.remove(item);
            }
        }
        notifyDataSetChanged();
    }


    public boolean isAnyItemSelected(){
        List<ImageItem> audioItemList = getAllImagesOnly();
        for(ImageItem item : audioItemList){
            if(item.isSelected()){
              return true;
            }
        }
        return false;
    }

    public List<ImageItem> getSelectedItemList(){
        List<ImageItem> audioItemList = new ArrayList<>();
        for(ImageItem item: getAllImagesOnly()){
            if(item.isSelected()){
                audioItemList.add(item);
            }
        }
        return audioItemList;
    }

    public List<String> getSelectedItemNameList(){
        List<String> audioItemList = new ArrayList<>();
        for(ImageItem item: getAllImagesOnly()){
            if(item.isSelected()){
                audioItemList.add(item.getPath());
            }
        }
        return audioItemList;
    }

    public void selectAllItems(boolean value){
        for(ImageItem item: getAllImagesOnly()){
            item.setSelected(value);
        }
        notifyDataSetChanged();
        listener.onImageItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllImagesOnly().size() );
    }

    public void renameItem(String oldPath, ImageItem newItem){
        for(ImageItem item : getAllImagesOnly()){
            if(item.getPath().equals(oldPath)){
                item.setPath(newItem.getPath());
                item.setName(newItem.getName());
            }
        }
        notifyDataSetChanged();
    }
}
