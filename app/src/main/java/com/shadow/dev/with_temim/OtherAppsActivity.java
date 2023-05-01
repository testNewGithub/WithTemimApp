package com.shadow.dev.with_temim;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class OtherAppsActivity extends AppCompatActivity {

    private LanguageClass languageClass;
    private DialogClass dialogClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageClass=new LanguageClass(this);
        languageClass.loadLocate();

        setContentView(R.layout.other_apps_layout);

        dialogClass=new DialogClass(this,this);
    }

    // launch Play Store intent
    private void playStoreApps(String app_id) {
        Uri marketUri = Uri.parse("market://details?id=" + app_id);
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(marketIntent);
    }

    public void backButton(View view) { onBackPressed();
    this.finish();
    }



    public void maktebetiAppButton(View view) {
        playStoreApps("com.shadow.dev.maktebeti");
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

        //ToDO
        if(id ==R.id.idPlayStoreApps ){
           // moreApplication();
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


}
