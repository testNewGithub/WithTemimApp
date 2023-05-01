package com.shadow.dev.with_temim;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;

public class ItemsList {

    public String youtubeUrl;
    public String videoTitle;
    public String imageVideoID;
    ItemsList(String youtubeUrl, String videoTitle, String imageVideoID){
        this.youtubeUrl=youtubeUrl;
        this.videoTitle=videoTitle;
        this.imageVideoID=imageVideoID;
    }

}
