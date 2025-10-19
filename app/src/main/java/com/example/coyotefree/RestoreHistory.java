package com.example.coyotefree;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RestoreHistory extends AppCompatActivity {
    Boolean isDarkModeEnabled;
    DBHandler2 obj;
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
        setContentView(R.layout.activity_restore_history);

        obj = new DBHandler2(RestoreHistory.this);
        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog5();
            }
        });
        String temp = "";
        try {

            ArrayList<String> filenameDatetime = new ArrayList<>();
            filenameDatetime = obj.getFileNamesWithDates();

            int size = filenameDatetime.size();
            for(int i=size-1;i>=0;i--){
                temp+=filenameDatetime.get(i)+"\n\n";
            }
        }catch (Exception e){

        }


        // Get the TextView for terms and conditions
        TextView termsTextView = findViewById(R.id.terms_conditions_text);

        // Set the terms and conditions text with 12 points
        termsTextView.setText(temp);
    }

    public void dialog5(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RestoreHistory.this);
        builder.setMessage(getString(R.string.doyourreallywantto))
                .setTitle(getString(R.string.alert));

        // Add the buttons.
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                clear();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        // Create the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clear(){
        obj.resetTable();
        Intent intent = new Intent(RestoreHistory.this, RestoreHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}