package com.example.coyotefree;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class termsAndConditions extends AppCompatActivity {
    Boolean isDarkModeEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#EBFEFEFE"));
        if(!isDarkModeEnabled) {
            //sv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if(isDarkModeEnabled){
            window.setStatusBarColor(Color.parseColor("#0C0F10"));
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG);
        }
        setContentView(R.layout.activity_terms_and_conditions);

        // Get the TextView for terms and conditions
        TextView termsTextView = findViewById(R.id.terms_conditions_text);

        // Set the terms and conditions text with 12 points
        termsTextView.setText("1. By using this app, you agree to abide by these terms and conditions.\n\n" +
                "2. This app is intended for personal use only. Commercial use is strictly prohibited.\n\n" +
                "3. You are responsible for maintaining the confidentiality of your PIN and any actions taken under your account.\n\n" +
                "4. The app provides a secure space to lock and encrypt files, but you are solely responsible for the files you choose to store.\n\n" +
                "5. The app does not collect, store, or share any personal data without your consent.\n\n" +
                "6. You must not use the app for any illegal or unauthorized purposes.\n\n" +
                "7. The app may request storage permissions to lock and unlock files, but it does not access any data beyond what is necessary for its functionality.\n\n" +
                "8. If you forget your PIN, resetting it will result in the permanent deletion of all locked files for security reasons.\n\n" +
                "9. The app reserves the right to update or modify these terms and conditions at any time. You will be notified of significant changes.\n\n" +
                "10. The app is provided on an \"as is\" and \"as available\" basis. While we strive to ensure the app functions smoothly, we do not guarantee that it will be free from errors, interruptions, or defects.\n\n" +
                "11. By continuing to use the app, you acknowledge that you have read, understood, and agreed to these terms and conditions.");
    }
}