package com.example.coyotefree;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class About extends AppCompatActivity {

    Boolean isDarkModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#EBFEFEFE"));
        if(!isDarkModeEnabled) {
            //sv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if(isDarkModeEnabled){
            window.setStatusBarColor(Color.parseColor("#0C0F10"));
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG);
        }

        setContentView(R.layout.activity_about);
        TextView versionTextView = findViewById(R.id.app_version);
        String version = getString(R.string.version)+" 1.1.19" ; // Automatically retrieves app version
        versionTextView.setText(version);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        /*SharedPreferences prefs = newBase.getSharedPreferences("langpreference", MODE_PRIVATE);
        String langPref = prefs.getString("lpref", "en");
        Context context = LocaleHelper.wrap(newBase, langPref);
        super.attachBaseContext(context);*/
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

}
