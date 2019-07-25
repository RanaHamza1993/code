package nexaplayer.mkv.mpg.flv.wmv.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;


import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DialogItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/21/2017.
 */

public class OptionsDialog extends Dialog {

    Button shareBtn;
    Button deleteBtn;
    Button detailsBtn;

    DialogItemClickListener listener;
    String itemType;
    Context context;
    RealmObject item;

    public OptionsDialog(Context context, RealmObject item, String itemType, Fragment fragment) {
        super(context);
        this.item = item;
        this.context = context;
        this.itemType = itemType;
        this.listener = (DialogItemClickListener) fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_options);
        intializeViews();
        initializeListeners();
    }

    private void intializeViews(){
        shareBtn = findViewById(R.id.share);
        deleteBtn = findViewById(R.id.delete);
        detailsBtn = findViewById(R.id.details);
    }

    private void initializeListeners(){
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile();
                dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteItem(item);
                dismiss();
            }
        });

        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                DetailsDialog detailsDialog = new DetailsDialog(context, item, itemType);
                detailsDialog.show();
            }
        });
    }

    private void shareFile(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                AudioItem audioItem = (AudioItem) item;
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(audioItem.getPath()));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, audioItem.getName());
                sharingIntent.setType("audio/*");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Audio");
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                VideoItem videoItem = (VideoItem) item;
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoItem.getPath()));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, videoItem.getName());
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                ImageItem imageItem = (ImageItem) item;
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageItem.getPath()));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, imageItem.getName());
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Image");
                break;
        }
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }
}
