package nexaplayer.mkv.mpg.flv.wmv.fragment.image;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import nexaplayer.mkv.mpg.flv.wmv.R;
import nexaplayer.mkv.mpg.flv.wmv.manager.SharedPreferenceManager;
import nexaplayer.mkv.mpg.flv.wmv.utils.AppConstants;

public class ImageSettingsFragment extends Fragment {


    CardView viewDetailslayout;
    CardView hiddenLayout;
    CardView contactLayout;
    CheckBox hiddenCheckbox;

    View fragmentView;
    SharedPreferenceManager sharedPreferenceManager;

    public ImageSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_image_settings, container, false);
        intializeViews();
        setUpData();
        initializeListeners();
        return fragmentView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void intializeViews(){
        viewDetailslayout = fragmentView.findViewById(R.id.view_details_layout);
        hiddenLayout = fragmentView.findViewById(R.id.hidden_details_layout);
        hiddenCheckbox = fragmentView.findViewById(R.id.hidden_checkbox);
        contactLayout = fragmentView.findViewById(R.id.setting_contact_layout);
    }

    private void setUpData(){
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getContext());
        hiddenCheckbox.setChecked(sharedPreferenceManager.getDisplayImageFileHidden());
    }

    private void initializeListeners(){
        viewDetailslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewDetailsDialog();
            }
        });

        hiddenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenCheckbox.setChecked(!hiddenCheckbox.isChecked());
                changeHiddenValue();
            }
        });

        hiddenCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeHiddenValue();
            }
        });
        contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });
    }

    private void openEmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"+ getResources().getString(R.string.contact_email)));
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    private void showViewDetailsDialog(){
        final String[] viewTypeList  = {AppConstants.VIEW_TYPE_DATE, AppConstants.VIEW_TYPE_EXTENSION, AppConstants.VIEW_TYPE_SIZE, AppConstants.VIEW_TYPE_FOLDER, AppConstants.VIEW_TYPE_RESOLUTION };
        boolean[] viewTypeValue = {sharedPreferenceManager.getDisplayImageFileDate(), sharedPreferenceManager.getDisplayImageFileExtension(), sharedPreferenceManager.getDisplayImageFileSize(), sharedPreferenceManager.getDisplayImageFilePath(), sharedPreferenceManager.getDisplayImageFileResolution()};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_view_detials);
        final DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String styleName = viewTypeList[which];
                switch (styleName){
                    case AppConstants.VIEW_TYPE_DATE:
                        sharedPreferenceManager.saveDisplayImageFileDate(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_SIZE:
                        sharedPreferenceManager.saveDisplayImageFileSize(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_EXTENSION:
                        sharedPreferenceManager.saveDisplayImageFileExtension(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_FOLDER:
                        sharedPreferenceManager.saveDisplayImageFilePath(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_RESOLUTION:
                        sharedPreferenceManager.saveDisplayImageFileResolution(isChecked);
                        break;
                }
            }
        };
        builder.setMultiChoiceItems(viewTypeList, viewTypeValue, listener);
        builder.create();
        builder.show();
    }

    private void changeHiddenValue(){
        sharedPreferenceManager.saveDisplayImageFileHidden(hiddenCheckbox.isChecked());
    }
}
