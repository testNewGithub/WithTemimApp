package com.shadow.dev.with_temim;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class VideoYtActivity extends AppCompatActivity  {

    private SectionPageAdapter sectionPageAdapter;
    private ViewPager mViewPager;
    private LanguageClass languageClass;
    private DialogClass dialogClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageClass=new LanguageClass(this);
        languageClass.loadLocate();
        setContentView(R.layout.video_yt_layout);

        MobileAds.initialize(this,
                "ca-app-pub-4168864559615120~7713295680");
        //Banner Ad MoPub Init
        SdkConfiguration sdkConfiguration =
                new SdkConfiguration.Builder("1cfa5a66dd684e31a4d57b737c76605f").build();

        MoPub.initializeSdk(this, sdkConfiguration, null);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager= findViewById(R.id.pager);
        setupViewPage(mViewPager);

        TabLayout tabLayout=findViewById(R.id.tablyt);
        tabLayout.setupWithViewPager(mViewPager);

        dialogClass=new DialogClass(this,this);
    }

    private void setupViewPage(ViewPager viewPager){
        SectionPageAdapter adapter=new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(),getString(R.string.tab_title_vd));
        adapter.addFragment(new Tab2Fragment(),getString(R.string.tab_title_others));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_layout,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        // select menu items actions
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
        Intent intent = new Intent(VideoYtActivity.this, OtherAppsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        dialogClass.exitApplicationDialog();
    }
}
