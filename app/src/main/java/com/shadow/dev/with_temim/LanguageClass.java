package com.shadow.dev.with_temim;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;
import static android.content.Context.MODE_PRIVATE;

public class LanguageClass {
    private Context mContext;

    LanguageClass(Context context){
        this.mContext=context;
    }

    // ***Change language***//

    //select Locale
    public void setLocale(String language){
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale=locale;
        mContext.getResources().updateConfiguration(config,mContext.getResources().getDisplayMetrics());

        // /Save Date For Next Time
        SharedPreferences.Editor editor=mContext.getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Language",language);
        editor.apply();
    }

    // Load locale language
    public void loadLocate(){
        SharedPreferences perfers=mContext.getSharedPreferences("Settings", MODE_PRIVATE);

        if(!perfers.contains("My_Language")) {
            return;
        }

        String language=perfers.getString("My_Language","");
        setLocale(language);
    }
}
