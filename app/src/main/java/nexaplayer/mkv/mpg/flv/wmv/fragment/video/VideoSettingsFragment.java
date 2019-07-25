package nexaplayer.mkv.mpg.flv.wmv.fragment.video;

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

public class VideoSettingsFragment extends Fragment {

    CardView viewDetailslayout;
    CardView resumeLayout;
    CardView hiddenLayout;
    CardView contactLayout;
    CheckBox resumeCheckbox;
    CheckBox hiddenCheckbox;

    View fragmentView;
    SharedPreferenceManager sharedPreferenceManager;

    public VideoSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentView = inflater.inflate(R.layout.fragment_video_settings, container, false);
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
        resumeLayout = fragmentView.findViewById(R.id.resume_details_layout);
        resumeCheckbox = fragmentView.findViewById(R.id.resume_checkbox);
        hiddenLayout = fragmentView.findViewById(R.id.hidden_details_layout);
        hiddenCheckbox = fragmentView.findViewById(R.id.hidden_checkbox);
        contactLayout = fragmentView.findViewById(R.id.setting_contact_layout);
    }

    private void setUpData(){
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getContext());
        resumeCheckbox.setChecked(sharedPreferenceManager.getDisplayVideoFileResume());
        hiddenCheckbox.setChecked(sharedPreferenceManager.getDisplayVideoFileHidden());
    }

    private void initializeListeners(){
        viewDetailslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewDetailsDialog();
            }
        });

        resumeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeCheckbox.setChecked(!resumeCheckbox.isChecked());
                changeResumeValue();
            }
        });

        resumeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeResumeValue();
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
        boolean[] viewTypeValue = {sharedPreferenceManager.getDisplayVideoFileDate(), sharedPreferenceManager.getDisplayVideoFileExtenstion(), sharedPreferenceManager.getDisplayVideoFileSize(), sharedPreferenceManager.getDisplayVideoFilePath(), sharedPreferenceManager.getDisplayVideoFileResolution()};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_view_detials);
        final DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String styleName = viewTypeList[which];
                switch (styleName){
                    case AppConstants.VIEW_TYPE_DATE:
                        sharedPreferenceManager.saveDisplayVideoFileDate(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_SIZE:
                        sharedPreferenceManager.saveDisplayVideoFileSize(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_EXTENSION:
                        sharedPreferenceManager.saveDisplayVideoFileExtension(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_FOLDER:
                        sharedPreferenceManager.saveDisplayVideoFilePath(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_RESOLUTION:
                        sharedPreferenceManager.saveDisplayVideoFileResolution(isChecked);
                        break;
                }
            }
        };
        builder.setMultiChoiceItems(viewTypeList, viewTypeValue, listener);
        builder.create();
        builder.show();
    }

    private void changeResumeValue(){
        sharedPreferenceManager.saveDisplayVideoFileResume(resumeCheckbox.isChecked());
    }
    private void changeHiddenValue(){
        sharedPreferenceManager.saveDisplayVideoFileHidden(hiddenCheckbox.isChecked());
    }

}
