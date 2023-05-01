package com.shadow.dev.with_temim;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_app_layout);
        launchMainActivity();

    }


    private void launchMainActivity(){
        Intent intent = new Intent(LauncherActivity.this, VideoYtActivity.class);
        LauncherActivity.this.startActivity(intent);
        LauncherActivity.this.finish();
    }


    @Override
    public void onBackPressed() {
    }


    @Override
    public void finish() {
        super.finish();
        Runtime.getRuntime().gc();
    }


}
