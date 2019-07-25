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
import nexaplayer.mkv.mpg.flv.wmv.listeners.VideoItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;
import nexaplayer.mkv.mpg.flv.wmv.viewholder.FbNativeAdsViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sultan Ahmed on 12/4/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int VIDEO_ITEM = 0;
	private static final int NATIVE_AD = 1;

	List<Object> videoList;
	Context context;
	VideoItemClickListener listener;
	SharedPreferenceManager sharedPreferenceManager;

	public VideoAdapter(Context context) {
		videoList = new ArrayList<>();
		this.context = context;
		listener = (VideoItemClickListener) context;
		sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
	}

	public VideoAdapter(Context context, VideoItemClickListener listener) {
		videoList = new ArrayList<>();
		this.context = context;
		this.listener = listener;
		sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
	}


	public void setVideoList(ArrayList<? extends Object> videoList) {
		this.videoList = (List<Object>) videoList;
		Collections.sort(getAllVideoOnly(), new VideoFileComparator());
		notifyDataSetChanged();
	}


	public ArrayList<VideoItem> getAllVideoOnly() {
		ArrayList<VideoItem> tempList = new ArrayList<>();
		{
			for (Object object : videoList) {
				if (object instanceof VideoItem) {
					tempList.add((VideoItem) object);
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

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case NATIVE_AD:
				View nativeAdViewLayout = LayoutInflater.from(
						parent.getContext()).inflate(R.layout.item_fb_native_ad,
						parent, false);
				return new FbNativeAdsViewHolder(nativeAdViewLayout);
			case VIDEO_ITEM:
			default:
				View itemView = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.layout_video_item, parent, false);

				return new VideoViewHolder(itemView);
		}

	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
		setupVideoItem((VideoViewHolder) viewHolder, position);
	}

	@Override
	public int getItemCount() {
		return videoList.size();
	}

	@Override
	public int getItemViewType(int position) {
		Object recyclerViewItem = videoList.get(position);
		/*if (recyclerViewItem instanceof Ad) {
			return NATIVE_AD;
		}*/
		return VIDEO_ITEM;
	}

	public void clearList() {
		videoList.clear();
		notifyDataSetChanged();
	}

	public String[] getVideoItemList() {
		List<VideoItem> tempList = getAllVideoOnly();
		String[] list = new String[tempList.size()];
		for (int i = 0; i < tempList.size(); i++) {
			list[i] = tempList.get(i).getPath();
		}
		return list;
	}



	public void deleteItem(VideoItem item) {
		RealmManager.getInstance().deleteObject("path", item.getPath(), VideoItem.class);
		int pos = getItemPosition(item);
		if (pos != -99) {
			videoList.remove(pos);
			notifyDataSetChanged();
			File file = new File(item.getPath());
			file.delete();
		}
	}

	public int getItemPosition(VideoItem item) {
		for (int i = 0; i < videoList.size(); i++) {
			Object listItem = videoList.get(i);
			if (listItem instanceof VideoItem) {
				if (item.getPath().equals(((VideoItem) listItem).getPath())) {
					return i;
				}
			}
		}
		return -99;
	}

	public int getItemPositionInItemList(VideoItem item) {
		List<VideoItem> tempList = getAllVideoOnly();
		for (int i = 0; i < tempList.size(); i++) {
			if (item.getPath().equals(tempList.get(i).getPath())) {
				return i;
			}
		}
		return 0;
	}


	private void setupVideoItem(VideoViewHolder holder, final int position) {
		final VideoItem videoItem = (VideoItem) videoList.get(position);
		ItemUtils.setMetaDataOnVideoItem(context, videoItem);
		holder.fileName.setText(videoItem.getName());
		holder.folderName.setText(videoItem.getPath());
		holder.fileDate.setText(videoItem.getDateString());
		holder.fileExtension.setText(videoItem.getExtension());
		holder.fileSize.setText(videoItem.getSize());
		holder.duration.setText(UtilityMethods.getFormattedDurationString(videoItem.getDuration()));
		holder.selectCheckBox.setChecked(videoItem.isSelected());

		holder.fileRes.setText(UtilityMethods.isEmptyString(videoItem.getWidth()) ? "Unknown" : (videoItem.getWidth() + "x" + videoItem.getHeight()));
		Glide.with(context).load(videoItem.getPath()).into(holder.thumbnail);

		holder.selectCheckBox.setVisibility(isAnyItemSelected() ? View.VISIBLE : View.GONE);
		holder.folderName.setVisibility(sharedPreferenceManager.getDisplayVideoFilePath() ? View.VISIBLE : View.GONE);
		holder.fileSize.setVisibility(sharedPreferenceManager.getDisplayVideoFileSize() ? View.VISIBLE : View.GONE);
		holder.fileDate.setVisibility(sharedPreferenceManager.getDisplayVideoFileDate() ? View.VISIBLE : View.GONE);
		holder.fileRes.setVisibility(sharedPreferenceManager.getDisplayVideoFileResolution() ? View.VISIBLE : View.GONE);
		holder.fileExtension.setVisibility(sharedPreferenceManager.getDisplayVideoFileExtenstion() ? View.VISIBLE : View.GONE);

		holder.newItem.setVisibility(View.GONE);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isAnyItemSelected()) {
					selectUnselectItem(position);
				} else {
					listener.onVideoItemClicked(getVideoItemList(), getItemPositionInItemList((VideoItem) videoList.get(position)));
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

	private void selectUnselectItem(int position) {
		VideoItem item = (VideoItem) videoList.get(position);
		item.setSelected(!item.isSelected());
		listener.onVideoItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllVideoOnly().size());
		notifyDataSetChanged();
	}

	public void deleteAllSelectedItem() {
		for (VideoItem item : getAllVideoOnly()) {
			if (item.isSelected()) {
				videoList.remove(item);
			}
		}
		notifyDataSetChanged();
	}

	public boolean isAnyItemSelected() {
		List<VideoItem> audioItemList = getAllVideoOnly();
		for (VideoItem item : audioItemList) {
			if (item.isSelected()) {
				return true;
			}
		}
		return false;
	}

	public List<VideoItem> getSelectedItemList() {
		List<VideoItem> audioItemList = new ArrayList<>();
		for (VideoItem item : getAllVideoOnly()) {
			if (item.isSelected()) {
				audioItemList.add(item);
			}
		}
		return audioItemList;
	}

	public List<String> getSelectedItemNameList() {
		List<String> audioItemList = new ArrayList<>();
		for (VideoItem item : getAllVideoOnly()) {
			if (item.isSelected()) {
				audioItemList.add(item.getPath());
			}
		}
		return audioItemList;
	}

	public void selectAllItems(boolean value) {
		for (VideoItem item : getAllVideoOnly()) {
			item.setSelected(value);
		}
		notifyDataSetChanged();
		listener.onVideoItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllVideoOnly().size());
	}

	public void renameItem(String oldPath, VideoItem newItem) {
		for (VideoItem item : getAllVideoOnly()) {
			if (item.getPath().equals(oldPath)) {
				item.setPath(newItem.getPath());
				item.setName(newItem.getName());
			}
		}
		notifyDataSetChanged();
	}

	public class VideoViewHolder extends RecyclerView.ViewHolder {
		public TextView fileName, folderName, fileDate, fileExtension, fileSize, duration, fileRes;
		public ImageView thumbnail;
		public LinearLayout newItem;
		public CheckBox selectCheckBox;

		public VideoViewHolder(View view) {
			super(view);
			fileName = view.findViewById(R.id.file_name);
			folderName = view.findViewById(R.id.file_path);
			fileDate = view.findViewById(R.id.file_date);
			fileExtension = view.findViewById(R.id.file_extension);
			fileSize = view.findViewById(R.id.file_size);
			fileRes = view.findViewById(R.id.file_dimension);
			newItem = view.findViewById(R.id.layout_new);
			duration = view.findViewById(R.id.duration);
			thumbnail = view.findViewById(R.id.video_thumbnail);
			selectCheckBox = view.findViewById(R.id.select_checkbox);

		}
	}

	private class VideoFileComparator implements Comparator<VideoItem> {

		@Override
		public int compare(VideoItem o1, VideoItem o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}
