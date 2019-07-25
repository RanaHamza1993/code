package nexaplayer.mkv.mpg.flv.wmv.utils;

import android.content.Context;

import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.FolderItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.model.detail.ChildDetails;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/8/2017.
 */

public class FolderUtils {

	public static FolderItem getFolderItem(File file, boolean hasNew, int itemCount) {
		FolderItem folderItem = new FolderItem();
		folderItem.setPath(file.getAbsolutePath());
		folderItem.setName(file.getName());
		folderItem.setItemCount(itemCount + " files");
		folderItem.setNew(hasNew);
		return folderItem;
	}

	public static List<RealmObject> getAllFilesInList(Context context, File parentDirectory, final List<RealmObject> itemList) {
		ChildDetails childDetails = getChildDetailsOfAllTypes(parentDirectory.listFiles());
		if (childDetails.getChildItemsCount().size() == 0 && childDetails.getChildDirectories().size() == 0) {
			return itemList;
		}

		for (File file : childDetails.getChildItemsCount()) {
			itemList.add(ItemUtils.getItem(context, file));
			try {
				if (ItemUtils.getItemType(file).equals(AppConstants.ITEM_TYPE_VIDEO)) {
					itemList.add(ItemUtils.getAudioRealmItem(context, file));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

//        final ExecutorService executor = Executors.newCachedThreadPool();
//        final List<Future<?>> futures = new ArrayList<>();
		for (final File childDirectory : childDetails.getChildDirectories()) {
//            Future<?> future = executor.submit(new Runnable() {
//                @Override
//                public void run() {
			itemList.addAll(getAllFilesInList(context, childDirectory, new ArrayList<RealmObject>()));
//                }
//            });
//            futures.add(future);
//        }
//        try {
//            for (Future<?> future : futures) {future.get();}
//        } catch (Exception e) {
//            Log.e("Find All files", "file error");
//            e.printStackTrace();
		}
		return itemList;
	}

	public static List<RealmObject> getAllFilesInList(Context context, File parentDirectory, final List<RealmObject> itemList, String itemType) {
		ChildDetails childDetails = getChildDetailsOfAllTypes(parentDirectory.listFiles());
		if (childDetails.getChildItemsCount().size() == 0 && childDetails.getChildDirectories().size() == 0) {
			return itemList;
		}

		for (File file : childDetails.getChildItemsCount()) {
			RealmObject item = ItemUtils.getItem(context, file, itemType);
			if (item != null) {
				itemList.add(item);
				try {
					if (ItemUtils.getItemType(file).equals(AppConstants.ITEM_TYPE_VIDEO)) {
						itemList.add(ItemUtils.getAudioRealmItem(context, file));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

//        final ExecutorService executor = Executors.newCachedThreadPool();
//        final List<Future<?>> futures = new ArrayList<>();
		for (final File childDirectory : childDetails.getChildDirectories()) {
//            Future<?> future = executor.submit(new Runnable() {
//                @Override
//                public void run() {
			itemList.addAll(getAllFilesInList(context, childDirectory, new ArrayList<RealmObject>()));
//                }
//            });
//            futures.add(future);
//        }
//        try {
//            for (Future<?> future : futures) {future.get();}
//        } catch (Exception e) {
//            Log.e("Find All files", "file error");
//            e.printStackTrace();
		}
		return itemList;
	}

	public static ChildDetails getChildDetails(File[] childs, String itemType) {
		ChildDetails childDetails = new ChildDetails();
		if (childs == null) {
			return childDetails;
		}
		for (File child : childs) {
			if (child.isDirectory()) {
				childDetails.addDirectoryItem(child);
			} else if (ItemUtils.verifyItemType(child, itemType)) {
				childDetails.addChildItem(child);
				childDetails.setHasNew(ItemUtils.isFileNew(child));
			}
		}
		return childDetails;
	}

	public static ChildDetails getChildDetailsOfAllTypes(File[] childs) {
		ChildDetails childDetails = new ChildDetails();
		if (childs == null) {
			return childDetails;
		}
		for (File child : childs) {
			if (child.isDirectory()) {
				childDetails.addDirectoryItem(child);
			} else if (ItemUtils.verifySupportedItemType(child)) {
				childDetails.addChildItem(child);
				childDetails.setHasNew(ItemUtils.isFileNew(child));
			}
		}
		return childDetails;
	}

	public static MultiMap<String, List<AudioItem>> showAllArtist(List<RealmObject> audioList) {
		MultiMap<String, List<AudioItem>> artistMap = new MultiValueMap<>();
		for (RealmObject object : audioList) {
			AudioItem model = (AudioItem) object;
			artistMap.put(model.getArtist(), model);
		}
		return artistMap;
	}

	public static MultiMap<String, List<AudioItem>> showAllAlbum(List<RealmObject> audioList) {
		MultiMap<String, List<AudioItem>> albumMap = new MultiValueMap<>();
		for (RealmObject object : audioList) {
			AudioItem model = (AudioItem) object;
			albumMap.put(model.getAlbum(), model);
		}
		return albumMap;
	}

	public static List<RealmObject> searchAllFilesWithText(String itemType, List<RealmObject> list, final String searchText) {
		List<RealmObject> searchList = new ArrayList<>();
		switch (itemType) {
			case AppConstants.ITEM_TYPE_AUDIO:
				for (RealmObject object : list) {
					AudioItem model = (AudioItem) object;
					if (UtilityMethods.containsString(model.getName(), searchText)) {
						searchList.add(model);
					}
				}
				break;
			case AppConstants.ITEM_TYPE_VIDEO:
				for (RealmObject object : list) {
					VideoItem model = (VideoItem) object;
					if (UtilityMethods.containsString(model.getName(), searchText)) {
						searchList.add(model);
					}
				}
				break;
			case AppConstants.ITEM_TYPE_IMAGE:
				for (RealmObject object : list) {
					ImageItem model = (ImageItem) object;
					if (UtilityMethods.containsString(model.getName(), searchText)) {
						searchList.add(model);
					}
				}
				break;
			case AppConstants.ITEM_TYPE_DOCUMENT:
				for (RealmObject object : list) {
					DocumentItem model = (DocumentItem) object;
					if (UtilityMethods.containsString(model.getName(), searchText)) {
						searchList.add(model);
					}
				}
				break;
			case AppConstants.ITEM_TYPE_APK:
				for (RealmObject object : list) {
					ApkItem model = (ApkItem) object;
					if (UtilityMethods.containsString(model.getName(), searchText)) {
						searchList.add(model);
					}
				}
				break;
		}
		return searchList;
	}

	public static MultiMap<String, List<RealmObject>> getFolderList(List<RealmObject> list, String itemType) {
		MultiMap<String, List<RealmObject>> multiMap = new MultiValueMap<>();
		switch (itemType) {
			case AppConstants.ITEM_TYPE_AUDIO:
				for (RealmObject object : list) {
					AudioItem model = (AudioItem) object;
					multiMap.put(model.getParentPath(), object);
				}
				break;
			case AppConstants.ITEM_TYPE_VIDEO:
				for (RealmObject object : list) {
					VideoItem model = (VideoItem) object;
					multiMap.put(model.getParentPath(), object);
				}
				break;
			case AppConstants.ITEM_TYPE_IMAGE:
				for (RealmObject object : list) {
					ImageItem model = (ImageItem) object;
					multiMap.put(model.getParentPath(), object);
				}
				break;
			case AppConstants.ITEM_TYPE_DOCUMENT:
				for (RealmObject object : list) {
					DocumentItem model = (DocumentItem) object;
					multiMap.put(model.getParentPath(), object);
				}
				break;
			case AppConstants.ITEM_TYPE_APK:
				for (RealmObject object : list) {
					ApkItem model = (ApkItem) object;
					multiMap.put(model.getParentPath(), object);
				}
				break;
		}
		return multiMap;
	}

}
