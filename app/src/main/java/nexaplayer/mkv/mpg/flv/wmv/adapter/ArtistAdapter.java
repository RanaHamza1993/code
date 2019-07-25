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
import nexaplayer.mkv.mpg.flv.wmv.listeners.ArtistItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.model.ArtistItem;
import nexaplayer.mkv.mpg.flv.wmv.viewholder.FbNativeAdsViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sultan Ahmed on 12/5/2017.
 */

public class ArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // A menu item view type.
    private static final int ARTIST_ITEM = 0;
    private static final int NATIVE_AD = 1;

    public static int VIEW_TYPE_ARTIST = 3;
    public static int VIEW_TYPE_ALBUM = 4;

    private int viewType;
    ArrayList<Object> artistList;
    Context context;
    ArtistItemClickListener listener;

    public ArtistAdapter(Context context) {
        artistList = new ArrayList<>();
        this.context = context;
        listener = (ArtistItemClickListener) context;
    }

    public ArtistAdapter(Context context, int viewType, ArtistItemClickListener listener) {
        artistList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
        this.viewType = viewType;
    }

    public ArrayList<ArtistItem> getFolderList() {
        return getAllFolderOnly();
    }

    public void setArtistList(ArrayList<? extends Object> artistList) {
        Collections.sort((List<ArtistItem>)artistList, new ArtistItemComparator());
        this.artistList = (ArrayList<Object>) artistList;
        notifyDataSetChanged();
    }

    public ArrayList<ArtistItem> getAllFolderOnly() {
        ArrayList<ArtistItem> tempList = new ArrayList<>();
        {
            for (Object object : artistList) {
                if (object instanceof ArtistItem) {
                    tempList.add((ArtistItem) object);
                }
            }
        }
        return tempList;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        public TextView folderName, itemsCount;
        public ImageView image;
        public LinearLayout itemView;

        public ArtistViewHolder(View view) {
            super(view);
            folderName = view.findViewById(R.id.artist_name);
            itemsCount = view.findViewById(R.id.item_count);
            image = view.findViewById(R.id.folder_icon);
            itemView = view.findViewById(R.id.artist_item);
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
            case ARTIST_ITEM:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_artist_item, parent, false);

                return new ArtistViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ARTIST_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        setupArtistItem((ArtistViewHolder) holder,position);
     }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public void clearList() {
        artistList.clear();
        notifyDataSetChanged();
    }

    private class ArtistItemComparator implements Comparator<ArtistItem> {

        @Override
        public int compare(ArtistItem o1, ArtistItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private void setupArtistItem(ArtistViewHolder holder, final  int position){
        ArtistItem folderItem = (ArtistItem) artistList.get(position);
        holder.folderName.setText(folderItem.getName());
        holder.itemsCount.setText(folderItem.getItemCount() + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArtistItemClicked((ArtistItem) artistList.get(position));
            }
        });
        holder.image.setBackgroundResource(viewType == VIEW_TYPE_ARTIST ? R.drawable.ic_artist_blue : R.drawable.ic_album_blue);
    }



}
