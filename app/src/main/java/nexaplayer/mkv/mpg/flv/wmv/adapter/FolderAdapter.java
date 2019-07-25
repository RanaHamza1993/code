package nexaplayer.mkv.mpg.flv.wmv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.FolderItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.model.FolderItem;
import nexaplayer.mkv.mpg.flv.wmv.viewholder.FbNativeAdsViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Sultan Ahmed on 12/5/2017.
 */

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // A menu item view type.
    private static final int FOLDER_ITEM = 0;
    private static final int NATIVE_AD = 1;

    ArrayList<Object> folderList;
    Context context;
    FolderItemClickListener listener;

    public FolderAdapter(Context context) {
        folderList = new ArrayList<>();
        this.context = context;
        listener = (FolderItemClickListener) context;
    }

    public FolderAdapter(Context context, FolderItemClickListener listener) {
        folderList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
    }

    public ArrayList<FolderItem> getFolderList() {
        return getAllFolderOnly();
    }

    public void setFolderList(ArrayList<? extends Object> folderList) {
        this.folderList = (ArrayList<Object>) folderList;
        Collections.sort(getAllFolderOnly(), new FolderItemComparator());
        notifyDataSetChanged();
    }

    public ArrayList<FolderItem> getAllFolderOnly() {
        ArrayList<FolderItem> tempList = new ArrayList<>();
        {
            for (Object object : folderList) {
                if (object instanceof FolderItem) {
                    tempList.add((FolderItem) object);
                }
            }
        }
        return tempList;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        public TextView folderName, itemsCount;
        public LinearLayout newItem;
        public ImageView image;

        public FolderViewHolder(View view) {
            super(view);
            folderName = view.findViewById(R.id.folder_name);
            itemsCount = view.findViewById(R.id.folder_count);
            newItem = view.findViewById(R.id.layout_new);
            image = view.findViewById(R.id.folder_icon);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case NATIVE_AD:
                View nativeAdViewLayout = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.item_fb_native_ad,
                        parent, false);
                return new FbNativeAdsViewHolder(nativeAdViewLayout);
            case FOLDER_ITEM:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_folder_item, parent, false);

                return new FolderViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return FOLDER_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        setupFolderItem((FolderViewHolder) holder,position);
     }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public void clearList() {
        folderList.clear();
        notifyDataSetChanged();
    }

    private class FolderItemComparator implements Comparator<FolderItem> {

        @Override
        public int compare(FolderItem o1, FolderItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private void setupFolderItem(FolderViewHolder holder, final  int position){
        FolderItem folderItem = (FolderItem) folderList.get(position);
        holder.folderName.setText(folderItem.getName());
        holder.itemsCount.setText(folderItem.getItemCount());
        holder.newItem.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFolderItemClicked((FolderItem) folderList.get(position));
            }
        });
    }



}
