package nexaplayer.mkv.mpg.flv.wmv.viewholder;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nexaplayer.mkv.mpg.flv.wmv.R;


/**
 * Created by Sultan Ahmed on 12/20/2017.
 */

public class FbNativeAdsViewHolder extends RecyclerView.ViewHolder {
    public ImageView adImage;
    public TextView tvAdTitle;
    public TextView tvAdBody;
    public Button btnCTA;
    public View container;
    public LinearLayout adChoicesContainer;

    public FbNativeAdsViewHolder(View itemView) {
        super(itemView);
        this.container = itemView;
        adImage = itemView.findViewById(R.id.adImage);
        tvAdTitle = itemView.findViewById(R.id.tvAdTitle);
        tvAdBody = itemView.findViewById(R.id.tvAdBody);
        btnCTA = itemView.findViewById(R.id.btnCTA);
        adChoicesContainer = itemView.findViewById(R.id.adChoicesContainer);
    }
}
