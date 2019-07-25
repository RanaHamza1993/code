package nexaplayer.mkv.mpg.flv.wmv.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.listeners.DocumentItemClickListener;
import nexaplayer.mkv.mpg.flv.wmv.manager.RealmManager;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.model.DocumentItem;
import nexaplayer.mkv.mpg.flv.wmv.viewholder.FbNativeAdsViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sultan Ahmed on 12/4/2017.
 */

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    // A menu item view type.
    private static final int DOCUMENT_ITEM = 0;
    private static final int NATIVE_AD = 1;


    List<Object> documentList;
    Context context;
    DocumentItemClickListener listener;
    SharedPreferenceManager sharedPreferenceManager;

    public DocumentAdapter(Context context){
        documentList = new ArrayList<>();
        this.context = context;
        listener = (DocumentItemClickListener) context;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public DocumentAdapter(Context context, DocumentItemClickListener listener){
        documentList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    public ArrayList<DocumentItem> getdocumentList() {
        return getAllDocumentOnly();
    }

    public void setdocumentList(ArrayList<? extends Object> documentList) {
        this.documentList = (List<Object>) documentList;
        Collections.sort(getAllDocumentOnly(), new DocumentAdapter.DocumentFileComparator());
        notifyDataSetChanged();
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName, folderName, fileDate, fileExtension, fileSize;
        public CheckBox selectCheckBox;
        
        public DocumentViewHolder(View view) {
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
            case DOCUMENT_ITEM:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_document_item, parent, false);
                return new DocumentAdapter.DocumentViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        setupDocumentItemRealmModel((DocumentAdapter.DocumentViewHolder) holder,position);

    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DOCUMENT_ITEM;
    }

    public void clearList(){
        documentList.clear();
        notifyDataSetChanged();
    }

    private class DocumentFileComparator implements Comparator<Object>{

        @Override
        public int compare(Object o1, Object o2) {
            return ((DocumentItem)o1).getName().compareTo(((DocumentItem)o2).getName());
        }
    }

    public String[] getDocumentList(){

        List<DocumentItem> tempList = getAllDocumentOnly();
        String[] list = new String[tempList.size()];
        for(int i=0; i<tempList.size(); i++){
            list[i] = tempList.get(i).getPath();
        }
        return list;
    }

    public ArrayList<DocumentItem> getAllDocumentOnly(){
        ArrayList<DocumentItem> tempList = new ArrayList<>();{
            for(Object object: documentList){
                if(object instanceof DocumentItem){
                    tempList.add((DocumentItem) object);
                }
            }
        }
        return tempList;
    }


    private void setupDocumentItemRealmModel(DocumentAdapter.DocumentViewHolder holder, final int position){
        DocumentAdapter.DocumentViewHolder documentViewHolder = holder;
        final DocumentItem documentItem = (DocumentItem) documentList.get(position);
        documentViewHolder.fileName.setText(documentItem.getName());
        documentViewHolder.folderName.setText(documentItem.getPath());
        documentViewHolder.fileDate.setText(documentItem.getDateString());
        documentViewHolder.fileExtension.setText(documentItem.getExtension());
        documentViewHolder.fileSize.setText(documentItem.getSize());
        documentViewHolder.selectCheckBox.setChecked(documentItem.isSelected());

        documentViewHolder.selectCheckBox.setVisibility(isAnyItemSelected()? View.VISIBLE: View.GONE);
        documentViewHolder.folderName.setVisibility(sharedPreferenceManager.getDisplayDocumentFilePath()? View.VISIBLE : View.GONE);
        documentViewHolder.fileSize.setVisibility(sharedPreferenceManager.getDisplayDocumentFileSize() ? View.VISIBLE : View.GONE);
        documentViewHolder.fileDate.setVisibility(sharedPreferenceManager.getDisplayDocumentFileDate() ? View.VISIBLE : View.GONE);
        documentViewHolder.fileExtension.setVisibility(sharedPreferenceManager.getDisplayDocumentFileExtension() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnyItemSelected()) {
                    selectUnselectItem(position);
                } else {
                    listener.onDocumentItemClicked((DocumentItem) documentList.get(position));
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




    public void deleteItem(DocumentItem item){
        RealmManager.getInstance().deleteObject("path", item.getPath(), DocumentItem.class);
        int pos = getItemPositionInItemList(item);
        if(pos != -99){
            documentList.remove(pos);
            notifyDataSetChanged();
            File file = new File(item.getPath());
            file.delete();
        }
    }

    private void selectUnselectItem(int position){
        DocumentItem item = (DocumentItem) documentList.get(position);
        item.setSelected(!item.isSelected());
        listener.onDocumentItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllDocumentOnly().size());
        notifyDataSetChanged();
    }


    public void deleteAllSelectedItem(){
        for(DocumentItem item: getAllDocumentOnly()){
            if(item.isSelected()){
                documentList.remove(item);
            }
        }
        notifyDataSetChanged();
    }



    public int getItemPositionInItemList(DocumentItem item){
        List<DocumentItem> tempList = getAllDocumentOnly();
        for(int i=0; i<tempList.size(); i++) {
            if (item.getPath().equals(tempList.get(i).getPath())) {
                return i;
            }
        }
        return 0;
    }

    public boolean isAnyItemSelected(){
        List<DocumentItem> audioItemList = getAllDocumentOnly();
        for(DocumentItem item : audioItemList){
            if(item.isSelected()){
                return true;
            }
        }
        return false;
    }

    public List<DocumentItem> getSelectedItemList(){
        List<DocumentItem> audioItemList = new ArrayList<>();
        for(DocumentItem item: getAllDocumentOnly()){
            if(item.isSelected()){
                audioItemList.add(item);
            }
        }
        return audioItemList;
    }

    public List<String> getSelectedItemNameList(){
        List<String> audioItemList = new ArrayList<>();
        for(DocumentItem item: getAllDocumentOnly()){
            if(item.isSelected()){
                audioItemList.add(item.getPath());
            }
        }
        return audioItemList;
    }

    public void selectAllItems(boolean value){
        for(DocumentItem item: getAllDocumentOnly()){
            item.setSelected(value);
        }
        notifyDataSetChanged();
        listener.onDocumentItemLongClicked(getSelectedItemList(), getSelectedItemList().size() == getAllDocumentOnly().size() );
    }

    public void renameItem(String oldPath, DocumentItem newItem){
        for(DocumentItem item : getAllDocumentOnly()){
            if(item.getPath().equals(oldPath)){
                item.setPath(newItem.getPath());
                item.setName(newItem.getName());
            }
        }
        notifyDataSetChanged();
    }
}
