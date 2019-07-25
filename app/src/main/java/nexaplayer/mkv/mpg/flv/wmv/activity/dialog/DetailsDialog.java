package nexaplayer.mkv.mpg.flv.wmv.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.ItemUtils;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/22/2017.
 */

public class DetailsDialog extends Dialog {
    private RealmObject object;
    private String itemType;
    private Context context;

    private TextView title;
    private TextView path;
    private TextView date;
    private TextView size;
    private TextView duration;
    private TextView resolution;
    private TextView extenstion;
    private TextView btnOk;
    private LinearLayout durationLayout;
    private LinearLayout resolutionLayout;

    public DetailsDialog(Context context, RealmObject object, String itemType) {
        super(context);
        this.object = object;
        this.itemType = itemType;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_details);
        initializeViews();
        initializeData();
    }

    private void initializeViews() {
        title = findViewById(R.id.item_title);
        path = findViewById(R.id.path_value);
        date = findViewById(R.id.date_value);
        size = findViewById(R.id.size_value);
        duration = findViewById(R.id.duration_value);
        resolution = findViewById(R.id.resolution_value);
        extenstion = findViewById(R.id.extention_value);
        btnOk = findViewById(R.id.btnOk);
        durationLayout = findViewById(R.id.duration_layout);
        resolutionLayout = findViewById(R.id.resolution_layout);
    }

    private void initializeData() {
        switch (itemType) {
            case AppConstants.ITEM_TYPE_AUDIO:
                initializeDataWithAudioItem();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                initializeDataWithVideoItem();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                initializeDataWithImageItem();
                break;

            case AppConstants.ITEM_TYPE_DOCUMENT:
                initializeDataWithDocumentItem();
                break;

            case AppConstants.ITEM_TYPE_APK:
                initializeDataWithApkItem();
                break;

        }
        durationLayout.setVisibility(( itemType.equals(AppConstants.ITEM_TYPE_AUDIO) || itemType.equals(AppConstants.ITEM_TYPE_VIDEO)) ? View.VISIBLE : View.GONE);
        resolutionLayout.setVisibility(( itemType.equals(AppConstants.ITEM_TYPE_IMAGE) || itemType.equals(AppConstants.ITEM_TYPE_VIDEO)) ? View.VISIBLE : View.GONE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void initializeDataWithAudioItem() {
        AudioItem audioItem = (AudioItem) object;
        title.setText(audioItem.getName());
        path.setText(audioItem.getPath());
        date.setText(audioItem.getDateString());
        size.setText(audioItem.getSize());
        extenstion.setText(audioItem.getExtension());
        duration.setText(UtilityMethods.isEmptyString(audioItem.getDuration()) ? "unknown" : UtilityMethods.getFormattedDurationString(audioItem.getDuration()));
    }

    private void initializeDataWithVideoItem() {
        VideoItem videoItem = (VideoItem) object;
        title.setText(videoItem.getName());
        path.setText(videoItem.getPath());
        date.setText(videoItem.getDateString());
        size.setText(videoItem.getSize());
        extenstion.setText(videoItem.getExtension());
        ItemUtils.setMetaDataOnVideoItem(context,videoItem);
        duration.setText(UtilityMethods.getFormattedDurationString(videoItem.getDuration()));
        resolution.setText(UtilityMethods.isEmptyString(videoItem.getWidth()) ? "Unknown" : (videoItem.getWidth() + "x" + videoItem.getHeight()));

    }

    private void initializeDataWithImageItem() {
        ImageItem imageItem = (ImageItem) object;
        title.setText(imageItem.getName());
        path.setText(imageItem.getPath());
        date.setText(imageItem.getDateString());
        size.setText(imageItem.getSize());
        extenstion.setText(imageItem.getExtension());
        ItemUtils.setMetaDataOnImageItem(imageItem, context);
        resolution.setText(imageItem.getWidth() + "x" + imageItem.getHeight());
    }

    private void initializeDataWithDocumentItem() {
        DocumentItem documentItem = (DocumentItem) object;
        title.setText(documentItem.getName());
        path.setText(documentItem.getPath());
        date.setText(documentItem.getDateString());
        size.setText(documentItem.getSize());
        extenstion.setText(documentItem.getExtension());
    }

    private void initializeDataWithApkItem() {
        ApkItem apkItem = (ApkItem) object;
        title.setText(apkItem.getName());
        path.setText(apkItem.getPath());
        date.setText(apkItem.getDateString());
        size.setText(apkItem.getSize());
        extenstion.setText(apkItem.getExtension());
    }

}
