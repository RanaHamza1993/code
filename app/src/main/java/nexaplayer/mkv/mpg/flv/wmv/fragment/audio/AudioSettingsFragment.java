package nexaplayer.mkv.mpg.flv.wmv.fragment.audio;

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

public class AudioSettingsFragment extends Fragment {

    CardView viewDetailslayout;
    CardView hiddenLayout;
    CardView shuffleLayout;
    CardView contactLayout;

    CheckBox hiddenCheckbox;
    CheckBox shuffleCheckbox;

    View fragmentView;
    SharedPreferenceManager sharedPreferenceManager;

    public AudioSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_audio_settings, container, false);
        intializeViews();
        setUpData();
        initializeListeners();
        return  fragmentView;
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
        shuffleLayout = fragmentView.findViewById(R.id.shuffle_layout);
        hiddenCheckbox = fragmentView.findViewById(R.id.hidden_checkbox);
        shuffleCheckbox = fragmentView.findViewById(R.id.shuffle_checkbox);
        contactLayout = fragmentView.findViewById(R.id.setting_contact_layout);
    }

    private void setUpData(){
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getContext());
        hiddenCheckbox.setChecked(sharedPreferenceManager.getDisplayAudioFileHidden());
        shuffleCheckbox.setChecked(sharedPreferenceManager.getAudioFileShuffle());
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

        shuffleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleCheckbox.setChecked(!shuffleCheckbox.isChecked());
                changeShuffleValue();
            }
        });

        shuffleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeShuffleValue();
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
        final String[] viewTypeList  = {AppConstants.VIEW_TYPE_DATE, AppConstants.VIEW_TYPE_EXTENSION, AppConstants.VIEW_TYPE_SIZE, AppConstants.VIEW_TYPE_FOLDER };
        boolean[] viewTypeValue = {sharedPreferenceManager.getDisplayAudioFileDate(), sharedPreferenceManager.getDisplayAudioFileExtension(), sharedPreferenceManager.getDisplayAudioFileSize(), sharedPreferenceManager.getDisplayAudioFilePath()};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_view_detials);
        final DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String styleName = viewTypeList[which];
                switch (styleName){
                    case AppConstants.VIEW_TYPE_DATE:
                        sharedPreferenceManager.saveDisplayAudioFileDate(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_SIZE:
                        sharedPreferenceManager.saveDisplayAudioFileSize(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_EXTENSION:
                        sharedPreferenceManager.saveDisplayAudioFileExtension(isChecked);
                        break;
                    case AppConstants.VIEW_TYPE_FOLDER:
                        sharedPreferenceManager.saveDisplayAudioFilePath(isChecked);
                        break;
                  }
            }
        };
        builder.setMultiChoiceItems(viewTypeList, viewTypeValue, listener);
        builder.create();
        builder.show();
    }

    private void changeHiddenValue(){
        sharedPreferenceManager.saveDisplayAudioFileHidden(hiddenCheckbox.isChecked());
    }

    private void changeShuffleValue(){
        sharedPreferenceManager.saveAudioFileShuffle(shuffleCheckbox.isChecked());
    }
}
