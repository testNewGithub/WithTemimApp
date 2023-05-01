package com.shadow.dev.with_temim;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private final static int USER = 0, ADS=1;
    private AdRequest adRequest;


    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Object> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view ;

        MobileAds.initialize(mContext, mContext.getString(R.string.ad_app_id));
        adRequest = new AdRequest.Builder()
                .build();

        switch (viewType) {
            case USER:
                view = mInflater.inflate(R.layout.recyclerview_row, viewGroup, false);
                return new ViewHolder1(view);
            case ADS:
                view = mInflater.inflate(R.layout.ads_item, viewGroup, false);
                return new ViewHolder2(view);
            default:
                view = mInflater.inflate(R.layout.recyclerview_row, viewGroup, false);
                return new ViewHolder1(view);
        }

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        switch (holder.getItemViewType()) {
            case USER:
                final ItemsList itemsList =(ItemsList) mData.get(position);
                ViewHolder1 holder1 = (ViewHolder1) holder;
                //holder1.imageVideo.setImageResource(itemsList.imageVideoID);
                Picasso.get().load(itemsList.imageVideoID).into(holder1.imageVideo);

                holder1.myTextView.setText(itemsList.videoTitle);
                holder1.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    { dialogLauncher(R.string.share_ytvideo_dialog_title,
                            R.string.share_ytvideo_dialog_message,
                            itemsList.youtubeUrl);
                    }
                });

                if(stateFavoriteVideo(position))
                    holder1.favoriteVideo.setBackgroundResource(android.R.drawable.btn_star_big_on);
                else
                    holder1.favoriteVideo.setBackgroundResource(android.R.drawable.btn_star_big_off);

                break;

            case ADS:
                ViewHolder2 holder2 = (ViewHolder2) holder;
                holder2.mAdView.loadAd(adRequest);
                break;
        }

    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof ItemsList) {
            return USER;
        }else if (mData.get(position) instanceof AdsObject) {
            return ADS;
        }
        return -1;
    }



    // Share and Rate Application Dialog
    private void dialogLauncher(int idTitle,  int idMessage, final String idVideo ){

        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        final TextView messageDialog=(TextView)dialog.findViewById(R.id.message);
        final TextView titleDialog=(TextView)dialog.findViewById(R.id.title);
        titleDialog.setText(idTitle);
        messageDialog.setText(idMessage);
        dialog.show();
        //RunAnimation(R.id.idShinePermission,R.anim.internet_button_anim);

        Animation anim = AnimationUtils.loadAnimation(mContext,R.anim.dialog_anim);
        final ImageView shine =(ImageView)dialog.findViewById(R.id.idShinePermission);
        shine.startAnimation(anim);

        // Allow
        dialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareYoutubeVideo(idVideo);
                dialog.cancel();
                shine.clearAnimation();
            }
        });

        // Cancel
        dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                shine.clearAnimation();
            }

        });

    }

    private String BASE_URL = "https://www.youtube.com";
    private String mYoutubeLink = BASE_URL + "/watch?v=";// + YOUTUBE_VIDEO_ID;

    private void shareYoutubeVideo(String _idVideo){
        //ToDO set this method in dialog launcher
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //ToDO change text to share
        shareIntent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share_ytvideo_message)+"  "+ mYoutubeLink + _idVideo);
        mContext.startActivity(Intent.createChooser(shareIntent, mContext.getResources().getString(R.string.share_with)));
    }




    //check video state (add star to favorite)
    private boolean stateFavoriteVideo(int position){
        SharedPreferences perfers=mContext.getSharedPreferences("Favorites", Activity.MODE_PRIVATE);
        if(!perfers.contains("Favorite" + position)) {
            return false;
        }

        boolean state=perfers.getBoolean("Favorite" + position,false);
        return state;
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView imageVideo;
        ImageButton shareButton;
        ImageButton favoriteVideo;

        ViewHolder1(View itemView) {
            super(itemView);
            imageVideo=itemView.findViewById(R.id.imageVideo);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            shareButton = itemView.findViewById(R.id.shareButton);
            favoriteVideo = itemView.findViewById(R.id.favoriteVideo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /// ViewHolder for AdBanner
    class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdView mAdView;
        ViewHolder2(View itemView) {
            super(itemView);
            // Ad Mob banner
            mAdView =itemView.findViewById(R.id.adViewItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // convenience method for getting data at click position
    ItemsList getItem(int id) {
        return (ItemsList) mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
