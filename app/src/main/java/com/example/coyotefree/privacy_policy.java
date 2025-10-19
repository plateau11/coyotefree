package com.example.coyotefree;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class privacy_policy extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.activity_privacy_policy);

        // Get the TextView for terms and conditions
        TextView termsTextView = findViewById(R.id.privacy_policy_text);

        // Set the privacy policy text without point 8
        termsTextView.setText("1. This app works locally i.e., we do not collect any data from the user.\n\n" +
                "2. The user is completely responsible for their own data.\n\n" +
                "3. Checking the Delete original file checkbox before locking will delete the file from the source folder.\n\n" +
                "4. Locked files and documents are encrypted and stored safely in the app space completely local to the app.\n\n" +
                "5. If a user forgets the pin, setting a new pin will completely wipe the user data to ensure user privacy and security.\n\n" +
                "6. Deleting an item from locker removes it from the app space.\n\n" +
                "7. The app does not share any user data with third parties.\n\n" +
                "8. The app may request permissions to access storage, but only for the purpose of locking and unlocking files.\n\n" +
                "9. The app does not track user activity or behavior.\n\n" +
                "10. Users are encouraged to use a strong PIN to enhance the security of their locked files.\n\n" +
                "11. By using this app, users agree to the terms outlined in this privacy policy.");
    }
}