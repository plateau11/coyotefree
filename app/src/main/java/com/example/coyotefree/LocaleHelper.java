package com.example.coyotefree;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {
    public static boolean languageChanged = false;
    public static Context wrap(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("langpreference", Context.MODE_PRIVATE);
        String langCode = prefs.getString("lpref", "en"); // default English

        Locale newLocale = new Locale(langCode);
        Locale.setDefault(newLocale);

        Configuration config = context.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(newLocale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = newLocale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
}
