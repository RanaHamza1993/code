package nexaplayer.mkv.mpg.flv.wmv.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.item_select.RenameListener;
import nexaplayer.mkv.mpg.flv.wmv.model.ApkItem;
import nexaplayer.mkv.mpg.flv.wmv.model.AudioItem;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.model.ImageItem;
import nexaplayer.mkv.mpg.flv.wmv.model.VideoItem;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;
import nexaplayer.mkv.mpg.flv.wmv.utils.UtilityMethods;

import java.io.File;

import io.realm.RealmObject;

/**
 * Created by Sultan Ahmed on 12/22/2017.
 */

public class RenameDialog extends Dialog {
    private RealmObject object;
    private String itemType;
    private Context context;
    private RenameListener listener;

    private TextView btnRename;
    private TextView btnCancel;
    private EditText edName;
    private String currentName;
    private String currentFullName;

    public RenameDialog(Context context, RenameListener listener, RealmObject object, String itemType) {
        super(context);
        this.object = object;
        this.itemType = itemType;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_rename);
        initializeViews();
        initializeData();
        initializeListeners();
    }

    private void initializeViews() {
        btnCancel = findViewById(R.id.btnCancel);
        btnRename = findViewById(R.id.btnRename);
        edName = findViewById(R.id.filename);
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
    }

    private void initializeListeners(){
        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               btnRename.setEnabled(s.length() > 0 && (!s.equals(currentName)));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edName.getText().toString();
                if(!isFileExists(newName)){
                    listener.onItemRename(getObject(newName),currentFullName);
                    dismiss();
                }
                else {
                    UtilityMethods.showToast(context, "File with same name already exists");
                }
            }
        });
    }

    private RealmObject getObject(String newName){
        switch (itemType) {
            case AppConstants.ITEM_TYPE_AUDIO:
                AudioItem audioItem = (AudioItem) object;
                audioItem.setName(newName +"."+ audioItem.getExtension());
                audioItem.setPath(audioItem.getParentPath() + File.separator + newName +"."+ audioItem.getExtension());
                return audioItem;
            case AppConstants.ITEM_TYPE_VIDEO:
                VideoItem videoItem = (VideoItem) object;
                videoItem.setName(newName +"."+ videoItem.getExtension());
                videoItem.setPath(videoItem.getParentPath() + File.separator + newName +"."+ videoItem.getExtension());
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                ImageItem imageItem = (ImageItem) object;
                imageItem.setName(newName +"."+ imageItem.getExtension());
                imageItem.setPath(imageItem.getParentPath() + File.separator + newName +"."+ imageItem.getExtension());
                return imageItem;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                DocumentItem documentItem = (DocumentItem) object;
                documentItem.setName(newName +"."+ documentItem.getExtension());
                documentItem.setPath(documentItem.getParentPath() + File.separator + newName +"."+ documentItem.getExtension());
                return documentItem;
            case AppConstants.ITEM_TYPE_APK:
                ApkItem apkItem = (ApkItem) object;
                apkItem.setName(newName +"."+ apkItem.getExtension());
                apkItem.setPath(apkItem.getParentPath() + File.separator + newName +"."+ apkItem.getExtension());
                return apkItem;
        }
        return object;
    }

    private String getNewFullName(String newName){
        String newPath = "";
        switch (itemType){
            case AppConstants.ITEM_TYPE_AUDIO:
                AudioItem audioItem = (AudioItem) object;
                newPath = audioItem.getParentPath() + File.separator + newName +"."+ audioItem.getExtension();
                break;
            case AppConstants.ITEM_TYPE_VIDEO:
                VideoItem videoItem = (VideoItem) object;
                newPath = videoItem.getParentPath() + File.separator + newName +"."+ videoItem.getExtension();
                break;
            case AppConstants.ITEM_TYPE_IMAGE:
                ImageItem imageItem = (ImageItem) object;
                newPath = imageItem.getParentPath() + File.separator + newName +"."+ imageItem.getExtension();
                break;
            case AppConstants.ITEM_TYPE_DOCUMENT:
                DocumentItem documentItem = (DocumentItem) object;
                newPath = documentItem.getParentPath() + File.separator + newName +"."+ documentItem.getExtension();
                break;
            case AppConstants.ITEM_TYPE_APK:
                ApkItem apkItem = (ApkItem) object;
                newPath = apkItem.getParentPath() + File.separator + newName +"."+ apkItem.getExtension();
                break;
        }
        return newPath;
    }

    private boolean isFileExists(String newName){
        File file = new File(getNewFullName(newName));
        return file.exists();
    }

    private void initializeDataWithAudioItem() {
        AudioItem audioItem = (AudioItem) object;
        int pos = audioItem.getName().lastIndexOf(".");
        edName.setText(audioItem.getName().substring(0, pos));
        this.currentName = audioItem.getName().substring(0, pos);
        this.currentFullName = audioItem.getPath();
    }

    private void initializeDataWithVideoItem() {
        VideoItem videoItem = (VideoItem) object;
        int pos = videoItem.getName().lastIndexOf(".");
        edName.setText(videoItem.getName().substring(0, pos));
        this.currentName = videoItem.getName().substring(0, pos);
        this.currentFullName = videoItem.getPath();
    }

    private void initializeDataWithImageItem() {
        ImageItem imageItem = (ImageItem) object;
        int pos = imageItem.getName().lastIndexOf(".");
        edName.setText(imageItem.getName().substring(0, pos));
        this.currentName = imageItem.getName().substring(0, pos);
        this.currentFullName = imageItem.getPath();
    }

    private void initializeDataWithDocumentItem() {
        DocumentItem documentItem = (DocumentItem) object;
        int pos = documentItem.getName().lastIndexOf(".");
        edName.setText(documentItem.getName().substring(0, pos));
        this.currentName = documentItem.getName().substring(0, pos);
        this.currentFullName = documentItem.getPath();
    }

    private void initializeDataWithApkItem() {
        ApkItem apkItem = (ApkItem) object;
        int pos = apkItem.getName().lastIndexOf(".");
        edName.setText(apkItem.getName().substring(0, pos));
        this.currentName = apkItem.getName().substring(0, pos);
        this.currentFullName = apkItem.getPath();
    }

}
