package com.example.coyotefree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    boolean isDarkModeEnabled;
    int[] selectedOption = {0};
    int[] selectedOptions = {0};
    int[] selectedOption3 = {0}; //for default language show only
    public static boolean pinchange =false;
    private Switch darkModeSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Window window = this.getWindow();
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#D8F2F5"));
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
          //      WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.activity_settings);

        //change the background color of toolbar to dark
        //if dark mode is enabled
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        if(isDarkModeEnabled){
            Toolbar bar = findViewById(R.id.toolbar3);
            bar.setBackgroundColor(Color.parseColor("#0C0F10"));
            AppBarLayout abl = findViewById(R.id.barbackground);
            abl.setBackgroundColor(Color.parseColor("#0C0F10"));
            window.setStatusBarColor(Color.parseColor("#0C0F10"));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS);
        }else{

        }

        TextView tv = findViewById(R.id.grid_size);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGridPreference();
                //Toast.makeText(SettingsActivity.this, "grid size clicked", Toast.LENGTH_SHORT).show();
            }
        });

        TextView lv = findViewById(R.id.Language);
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLangPreference();
                //Toast.makeText(SettingsActivity.this, "grid size clicked", Toast.LENGTH_SHORT).show();
            }
        });

        TextView pc = findViewById(R.id.pin_change);
        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinchange=true;
                Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                //Toast.makeText(SettingsActivity.this, "grid size clicked", Toast.LENGTH_SHORT).show();
            }
        });

        TextView history = findViewById(R.id.History);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,RestoreHistory.class);
                startActivity(intent);
                //Toast.makeText(SettingsActivity.this, "grid size clicked", Toast.LENGTH_SHORT).show();
            }
        });
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        // Enable the back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Load saved preferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        // Set the switch state
        darkModeSwitch.setChecked(isDarkModeEnabled);
        // Listener for toggle switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            // Enable or disable dark mode
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
    public void dialogGridPreference() {
        SharedPreferences gridpreference = getSharedPreferences("gridpreference", MODE_PRIVATE);
        String preference = gridpreference.getString("gpref","2x2");
        // Array of radio button options
        String[] gridSizes = {"2x2", "3x3"};
        if(preference.equalsIgnoreCase("2x2"))
            selectedOption = new int[]{0}; // Default selected option index
        if(preference.equalsIgnoreCase("3x3"))
            selectedOption = new int[]{1}; // Default selected option index
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.LockerGridSize))
                .setSingleChoiceItems(gridSizes, selectedOption[0], (dialog, which) -> {
                    // Update the selected option when a radio button is clicked
                    selectedOption[0] = which;
                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Handle the OK button click, using selectedOption[0] as the selected index
                    String selectedGridSize = gridSizes[selectedOption[0]];
                    //Toast.makeText(this, "Selected: " + selectedGridSize, Toast.LENGTH_SHORT).show();
                    SharedPreferences gridPreference = getSharedPreferences("gridpreference", MODE_PRIVATE);
                    SharedPreferences.Editor editor = gridPreference.edit();
                    if(selectedGridSize.equalsIgnoreCase("2x2")){
                        //Toast.makeText(this, "first selected", Toast.LENGTH_SHORT).show();
                        editor.putString("gpref", "2x2");
                    }
                    if(selectedGridSize.equalsIgnoreCase("3x3"))
                    {
                        //Toast.makeText(this, "second selected", Toast.LENGTH_SHORT).show();
                        editor.putString("gpref", "3x3");
                    }
                    editor.apply();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /*public void dialogLangPreference() {
        SharedPreferences langpreference = getSharedPreferences("langpreference", MODE_PRIVATE);
        String preference = langpreference.getString("lpref","English");
        // Array of radio button options
        String[] langSizes = {getString(R.string.English), getString(R.string.Spanish)};
        if(preference.equalsIgnoreCase(getString(R.string.English)))
            selectedOptions = new int[]{0}; // Default selected option index
        if(preference.equalsIgnoreCase(getString(R.string.Spanish)))
            selectedOptions = new int[]{1}; // Default selected option index
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.ChooseLanguage)
                .setSingleChoiceItems(langSizes, selectedOptions[0], (dialog, which) -> {
                    // Update the selected option when a radio button is clicked
                    selectedOptions[0] = which;
                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Handle the OK button click, using selectedOption[0] as the selected index
                    String selectedLanguage = langSizes[selectedOptions[0]];
                    //Toast.makeText(this, "Selected: " + selectedGridSize, Toast.LENGTH_SHORT).show();
                    SharedPreferences langPreference2 = getSharedPreferences("langpreference", MODE_PRIVATE);
                    SharedPreferences.Editor editor = langPreference2.edit();
                    if(selectedLanguage.equalsIgnoreCase(getString(R.string.English))){
                        //Toast.makeText(this, "first selected", Toast.LENGTH_SHORT).show();
                        editor.putString("lpref", "English");
                    }
                    if(selectedLanguage.equalsIgnoreCase(getString(R.string.Spanish)))
                    {
                        //Toast.makeText(this, "second selected", Toast.LENGTH_SHORT).show();
                        editor.putString("lpref", "Spanish");
                    }
                    editor.apply();

                    LocaleHelper.languageChanged = true;

                    recreate();

                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }*/

    public void dialogLangPreference() {
        SharedPreferences langpreference = getSharedPreferences("langpreference", MODE_PRIVATE);
        String preference = langpreference.getString("lpref", "en"); // Use language code

        // UI display strings
        String[] langSizes = {"English", "Spanish", "Arabic", "Farsi", "Japanese", "Turkish", "Nepali", "Telugu", "Kannada","Ukrainian", "Russian"}; // Add more as needed
        final String[] langCodes = {"en", "es", "ar", "fa", "ja", "tr", "ne", "te", "kn", "uk", "ru"};

        // Determine selected option index
        int selectedIndex = 0;
        for (int i = 0; i < langCodes.length; i++) {
            if (langCodes[i].equalsIgnoreCase(preference)) {
                selectedIndex = i;
                break;
            }
        }
        selectedOptions = new int[]{selectedIndex};

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.ChooseLanguage)
                .setSingleChoiceItems(langSizes, selectedOptions[0], (dialog, which) -> {
                    selectedOptions[0] = which;
                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    String selectedLangCode = langCodes[selectedOptions[0]];
                    SharedPreferences.Editor editor = langpreference.edit();
                    editor.putString("lpref", selectedLangCode);
                    editor.apply();

                    //LocaleHelper.languageChanged = true;
                    //recreate();
                    LocaleHelper.languageChanged = true;
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        /*SharedPreferences prefs = newBase.getSharedPreferences("langpreference", MODE_PRIVATE);
        String langPref = prefs.getString("lpref", "en");
        //String langCode = langPref.equalsIgnoreCase("es") ? "es" : "en";
        //String langCode = getLocaleCodeFromPreference(langPref);
        Context context = LocaleHelper.wrap(newBase, langPref);
        super.attachBaseContext(context);*/
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

}