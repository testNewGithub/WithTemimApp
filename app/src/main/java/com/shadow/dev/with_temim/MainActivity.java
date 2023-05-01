package com.shadow.dev.with_temim;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mopub.common.SdkConfiguration;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, RewardedVideoAdListener {

    // Replace video id with your videoId
    private String BASE_URL = "https://www.youtube.com";
    private String mYoutubeLink = BASE_URL + "/watch?v=";// + YOUTUBE_VIDEO_ID;
    public  MyRecyclerViewAdapter adapter;
    public  RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private TextView videoTitle;
    private ImageButton favoriteVideoButton;
    private int actuelPosition;
    private String actuelVideoId;
    private String actuelVideoTitle;

    private LanguageClass languageClass;
    private DialogClass dialogClass;
    private RewardedVideoAd mRewardedVideoAd;
    public InterstitialAd mInterstitialAd;

    // Youtube Palyer View
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer mYouTubePlayer;
    private YouTubePlayerTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageClass=new LanguageClass(this);
        languageClass.loadLocate();

        setContentView(R.layout.activity);
        MobileAds.initialize(this,
                "ca-app-pub-4168864559615120~7713295680");

        //Banner Ad MoPub Init
        SdkConfiguration sdkConfiguration =
                new SdkConfiguration.Builder("5e86c415dd59440389b03b531461bfae").build();

        dialogClass=new DialogClass(this,this);
        Intent intent=getIntent();
        //actuelPosition=intent.getIntExtra(Tab1Fragment.VIDEO_POSITION,0);
        Bundle extras =getIntent().getExtras();
        actuelPosition=extras.getInt(Tab1Fragment.VIDEO_POSITION);
        actuelVideoId = extras.getString(Tab1Fragment.VIDEO_ID);
        actuelVideoTitle = extras.getString(Tab1Fragment.VIDEO_Title);

        // set Youtube View and Youtube Player
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        tracker = new YouTubePlayerTracker();

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                //youTubePlayer.loadVideo(VIDEO_ID, 0);
                mYouTubePlayer = youTubePlayer;
                youTubePlayer.loadVideo(actuelVideoId,0);
                youTubePlayer.addListener(tracker);

            }
/*
            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                CURRENTE_TIME=String.valueOf((int)second);
                Log.e("CurrentYoutubeVideoTime",String.valueOf((int)second));
                // super.onCurrentSecond(youTubePlayer, second);
            }

            @Override
            public void onVideoDuration(YouTubePlayer youTubePlayer, float duration) {
                VIDEO_DURATION=String.valueOf((int)duration);
                Log.e("DurationYoutubeVideo",String.valueOf((int)duration));
            }
*/
        });


        favoriteVideoButton=findViewById(R.id.idAddFavoriteVd);
        videoTitle=findViewById(R.id.idVideoTitle);
        // data to populate the RecyclerView with
        VideoYtList videoYt = new VideoYtList(this);
        ArrayList<Object> arrayList=videoYt.getVideoList();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvVidioShow);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
        //       mLayoutManager.getOrientation());
        //recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MyRecyclerViewAdapter(this, arrayList);
        adapter.setClickListener(this);
        //recyclerView.setAdapter(adapter);

        //extractYoutubeUrl(adapter.getItem(actuelPosition).youtubeUrl);
        //videoTitle.setText(adapter.getItem(actuelPosition).videoTitle);

        //set favorite video state (add star)
        if(stateFavoriteVideo(actuelPosition))
            favoriteVideoButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
        else
            favoriteVideoButton.setBackgroundResource(android.R.drawable.btn_star_big_off);


        //Floating Action menu
        setFloatingActionMenu();





        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();


          // Reintialise InterstitialAd Full screen Ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder()
                        .build());
            }
        });



        // Post to handler for video ad testing and set video position
        handler.post(runnable);
    }




    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.ad_video),
                new AdRequest.Builder().build());
    }

    private void setFloatingActionMenu(){
        //Floating Action menu
        FloatingActionButton floatingActionButton1 = findViewById(R.id.material_design_floating_action_menu_item1_main);
        FloatingActionButton floatingActionButton2 = findViewById(R.id.material_design_floating_action_menu_item2_main);
        FloatingActionButton floatingActionButton3 = findViewById(R.id.material_design_floating_action_menu_item3_main);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                dialogClass.dialogLauncher(R.string.share_title,R.string.share_message,1);
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                dialogClass.dialogLauncher(R.string.rate_title,R.string.rate_message,2);
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                dialogClass.exitApplicationDialog();
            }
        });
    }

 /*
    private String linkTest="https://www.youtube.com/watch?v=LIFIVBNU0Tc";
    @SuppressLint("StaticFieldLeak")
    private void extractYoutubeUrl(String mIdYoutubeVideo) {
        new YouTubeExtractor(this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = 22;
                    String downloadUrl = ytFiles.get(itag).getUrl();
                    playVideo(downloadUrl);
                }
            }
        }.extract(mYoutubeLink+mIdYoutubeVideo, true, true);

    }


    private void playVideo(String downloadUrl) {
        PlayerView mPlayerView = findViewById(R.id.mPlayerView);
        mPlayerView.setPlayer(ExoPlayerManager.getSharedInstance(MainActivity.this).getPlayerView().getPlayer());
        ExoPlayerManager.getSharedInstance(MainActivity.this).playStream(downloadUrl);
    }
*/

    //Save Favorite video in database
    private void setFavoriteVideo(boolean is_favorite, int position) {
        SharedPreferences.Editor editor = getSharedPreferences("Favorites", MODE_PRIVATE).edit();
        editor.putBoolean("Favorite" + position, is_favorite);
        editor.apply();
    }

    private boolean stateFavoriteVideo(int position){

        SharedPreferences perfers=getSharedPreferences("Favorites", Activity.MODE_PRIVATE);
        if(!perfers.contains("Favorite" + position)) {
            return false;
        }

        boolean state=perfers.getBoolean("Favorite" + position,false);
        return state;
    }
    // onClick add to favorite video button
    public void addVideotoFavorite(View view) {


        if(stateFavoriteVideo(actuelPosition)){
            setFavoriteVideo(!stateFavoriteVideo(actuelPosition), actuelPosition);
            favoriteVideoButton.setBackgroundResource(android.R.drawable.btn_star_big_off);
        } else {
            setFavoriteVideo(!stateFavoriteVideo(actuelPosition), actuelPosition);
            favoriteVideoButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }

        // For reloading adapter
        recyclerView.setAdapter(adapter);
    }

    // onClick share youtube video button
    public void shareYoutubeVideo(View view) {
              dialogLauncher(R.string.share_ytvideo_dialog_title,
                      R.string.share_ytvideo_dialog_message,
                      actuelPosition);
    }

    // Share and Rate Application Dialog
    private void dialogLauncher(int idTitle,  int idMessage, final int position ){

        final Dialog dialog = new Dialog(this);
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

        Animation anim = AnimationUtils.loadAnimation(this,R.anim.dialog_anim);
        final ImageView shine =(ImageView)dialog.findViewById(R.id.idShinePermission);
        shine.startAnimation(anim);

        // Allow
        dialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareYoutubeVideo(position);
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

    private void shareYoutubeVideo(int position){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //ToDO change text to share
        String _idVideo=adapter.getItem(position).youtubeUrl;
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_ytvideo_message)+"  "+ mYoutubeLink + _idVideo);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_with)));
    }



    SimpleExoPlayerView simpleExoPlayerView;
    LinearLayout.LayoutParams params;
    FloatingActionMenu floatingActionMenu;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
      //  simpleExoPlayerView=findViewById(R.id.youtube_player_view);
      //  params = (LinearLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
        floatingActionMenu=findViewById(R.id.material_design_android_floating_action_menu_main);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            //params.height=params.MATCH_PARENT;
            //simpleExoPlayerView.setLayoutParams(params);
            floatingActionMenu.hideMenu(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            youTubePlayerView.enterFullScreen();
        } else {
            getSupportActionBar().show();
            //params.height=params.WRAP_CONTENT;
            //simpleExoPlayerView.setLayoutParams(params);
            floatingActionMenu.showMenu(true);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            youTubePlayerView.exitFullScreen();
        }
    }

    private static int ADS=1;

    @Override
    public void onItemClick(View view, int position) {

        if(adapter.getItemViewType(position)==ADS)return;

        if(!isConnected()){
            dialogClass.connexionDialog();
            return;
        }

            videoTitle.setText(adapter.getItem(position).videoTitle);
            //extractYoutubeUrl(adapter.getItem(position).youtubeUrl);
            if(mYouTubePlayer==null) return;
            mYouTubePlayer.loadVideo(adapter.getItem(position).youtubeUrl,0);
            mYouTubePlayer.addListener(tracker);

            if (stateFavoriteVideo(position))
                favoriteVideoButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
            else
                favoriteVideoButton.setBackgroundResource(android.R.drawable.btn_star_big_off);

            actuelPosition = position;
            loadRewardedVideoAd();
    }


    // Action Bar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_layout,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id ==R.id.idPlayStoreApps ){
            moreApplication();
        }

        if(id ==R.id.idLangauge ){
            dialogClass.languageSelection();
        }

        if(id ==R.id.idPrivacyPolicy ){
            dialogClass.privacyPolicyDialog();
        }

        if(id==R.id.idShareApplication){
            dialogClass.dialogLauncher(R.string.share_title,R.string.share_message,1);
        }

        return super.onOptionsItemSelected(item);
    }



    private void moreApplication(){
        Intent intent = new Intent(MainActivity.this, OtherAppsActivity.class);
        startActivity(intent);
    }

    //Get Connectivity State
    private boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Handler to show video Ad
    private Handler handler = new Handler();
    private boolean isShowed=false;
    private int TIME_TO_SHOW_VIDEO=50;

    private Context context=this;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //int currentTimePercent = ExoPlayerManager.getSharedInstance(MainActivity.this).getCurrentPositionPerCent();
            int currentTimePercent =(int) (tracker.getCurrentSecond()/tracker.getVideoDuration() * 100);

            if(currentTimePercent ==TIME_TO_SHOW_VIDEO && !isShowed){
                    isShowed = true;
                    mRewardedVideoAd.show();
            }
            if(currentTimePercent >TIME_TO_SHOW_VIDEO && isShowed)  {
                TIME_TO_SHOW_VIDEO=95;
                isShowed=false;
                loadRewardedVideoAd();
            }
            if(currentTimePercent <50 && TIME_TO_SHOW_VIDEO!=50){
                TIME_TO_SHOW_VIDEO=50;
                isShowed=false;
                loadRewardedVideoAd();
            }

             handler.postDelayed(runnable, 1000);
        }
    };



    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // dialogClass.exitApplicationDialog();
        this.finish();

        //Launch Full Screen Ad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mInterstitialAd.show();
            }
        },300); // Millisecond 1000 = 1 sec

    }

    @Override
    protected void onPause() {
        mRewardedVideoAd.pause(this);
        //ExoPlayerManager.getSharedInstance(MainActivity.this).pausePlayer();
        if(mYouTubePlayer!=null) mYouTubePlayer.pause();
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        //ExoPlayerManager.getSharedInstance(MainActivity.this).resumePlayer();
        if(mYouTubePlayer!=null) mYouTubePlayer.play();
        handler.post(runnable);
        super.onResume();
    }

    @Override
    public void finish() {
        //ExoPlayerManager.getSharedInstance(MainActivity.this).destroyPlayer();
        youTubePlayerView.release();
        handler.removeCallbacks(runnable);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        //ExoPlayerManager.getSharedInstance(MainActivity.this).destroyPlayer();
        youTubePlayerView.release();
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {
        loadRewardedVideoAd();
    }


    private static String url = "https://api.npoint.io/dcb6e1ac4215ea78da7b";
    public class VideoYtList  {

        private ArrayList<Object> videoYt;
        private Context mContext;
        private ProgressDialog pDialog;


        VideoYtList(Context context ) {
            this.mContext=context;
            videoYt = new ArrayList<>();
            new GetVideoIdAndTitle().execute();
            //videoYt.add(new AdsObject());
        }

        public ArrayList<Object> getVideoList(){
            return videoYt;
        }

        private static final String IMAGE_BASE_URL = "http://i.ytimg.com/vi/";
        // 120 x 90
        private String getThumbUrl(String _videoId) {
            return IMAGE_BASE_URL + _videoId + "/maxresdefault.jpg";
        }


        /**
         * Async task class to get json by making HTTP call
         */


        private class GetVideoIdAndTitle extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                // Showing progress dialog
                pDialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
                pDialog.setMessage(getResources().getString(R.string.plz_wait));
                pDialog.setCancelable(false);
                pDialog.show();

            }

            private boolean isNotConnected = false;
            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONObject info = jsonObj.getJSONObject("info");
                        JSONArray videoTab = info.getJSONArray("TabVideos");
                        // looping through All Contacts
                        for (int i = 0; i < videoTab.length(); i++) {
                            JSONObject c = videoTab.getJSONObject(i);

                            String idVideo = c.getString("idVideo");
                            String titel = c.getString("titel");

                            if (i%8==0) {
                                videoYt.add(new AdsObject());
                            }
                            videoYt.add(new ItemsList(idVideo,titel, getThumbUrl(idVideo)));

                        }
                    } catch (JSONException e) {

                    }
                }else {
                    //Check Connection State
                   // dialogClass.connexionDialog();
                    //Toast.makeText(mContext,"No internet Connexion!",Toast.LENGTH_LONG).show();
                    isNotConnected = true;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Dismiss the progress dialog
                if (pDialog.isShowing()){
                    pDialog.dismiss();
                }

                if(isNotConnected) {
                    dialogClass.connexionDialog();
                    return;
                }
                recyclerView.setAdapter(adapter);

                videoTitle.setText(adapter.getItem(actuelPosition).videoTitle);
                //extractYoutubeUrl(adapter.getItem(actuelPosition).youtubeUrl);

            }
        }

    }

}