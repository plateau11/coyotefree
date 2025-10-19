package com.example.coyotefree;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Help extends AppCompatActivity {

    Boolean isDarkModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);

        Window window = this.getWindow();

        if(isDarkModeEnabled){
            window.setStatusBarColor(Color.parseColor("#0C0F10"));
        }


        if(!isDarkModeEnabled) {
            //window.setStatusBarColor(Color.parseColor("#f2f2f2"));

            //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_help);

        // Set up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // FAQ 1
        TextView question1 = findViewById(R.id.question1);
        TextView answer1 = findViewById(R.id.answer1);
        Button toggle1 = findViewById(R.id.toggle1);

        toggle1.setOnClickListener(v -> {
            if (answer1.getVisibility() == View.GONE) {
                answer1.setVisibility(View.VISIBLE);
                toggle1.setText("Hide");
            } else {
                answer1.setVisibility(View.GONE);
                toggle1.setText("Show");
            }
        });

        // FAQ 2
        TextView question2 = findViewById(R.id.question2);
        TextView answer2 = findViewById(R.id.answer2);
        Button toggle2 = findViewById(R.id.toggle2);

        toggle2.setOnClickListener(v -> {
            if (answer2.getVisibility() == View.GONE) {
                answer2.setVisibility(View.VISIBLE);
                toggle2.setText("Hide");
            } else {
                answer2.setVisibility(View.GONE);
                toggle2.setText("Show");
            }
        });

        // FAQ 3
        TextView question3 = findViewById(R.id.question3);
        TextView answer3 = findViewById(R.id.answer3);
        Button toggle3 = findViewById(R.id.toggle3);

        toggle3.setOnClickListener(v -> {
            if (answer3.getVisibility() == View.GONE) {
                answer3.setVisibility(View.VISIBLE);
                toggle3.setText("Hide");
            } else {
                answer3.setVisibility(View.GONE);
                toggle3.setText("Show");
            }
        });

        // FAQ 4
        TextView question4 = findViewById(R.id.question4);
        TextView answer4 = findViewById(R.id.answer4);
        Button toggle4 = findViewById(R.id.toggle4);

        toggle4.setOnClickListener(v -> {
            if (answer4.getVisibility() == View.GONE) {
                answer4.setVisibility(View.VISIBLE);
                toggle4.setText("Hide");
            } else {
                answer4.setVisibility(View.GONE);
                toggle4.setText("Show");
            }
        });

        // FAQ 5
        TextView question5 = findViewById(R.id.question5);
        TextView answer5 = findViewById(R.id.answer5);
        Button toggle5 = findViewById(R.id.toggle5);

        toggle5.setOnClickListener(v -> {
            if (answer5.getVisibility() == View.GONE) {
                answer5.setVisibility(View.VISIBLE);
                toggle5.setText("Hide");
            } else {
                answer5.setVisibility(View.GONE);
                toggle5.setText("Show");
            }
        });

        // FAQ 6
        TextView question6 = findViewById(R.id.question6);
        TextView answer6 = findViewById(R.id.answer6);
        Button toggle6 = findViewById(R.id.toggle6);

        toggle6.setOnClickListener(v -> {
            if (answer6.getVisibility() == View.GONE) {
                answer6.setVisibility(View.VISIBLE);
                toggle6.setText("Hide");
            } else {
                answer6.setVisibility(View.GONE);
                toggle6.setText("Show");
            }
        });
    }
}