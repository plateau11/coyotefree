package com.example.coyotefree;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
public class Feedback extends AppCompatActivity {
    // define objects for edit text and button
    Button button;
    EditText subject, body;

    Boolean isDarkModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
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
        setContentView(R.layout.activity_feedback);
        // Getting instance of edittext and button
        //sendto = findViewById(R.id.editText1);
        subject = findViewById(R.id.editText2);
        body = findViewById(R.id.editText3);
        button = findViewById(R.id.button);
        // attach setOnClickListener to button with Intent object define in it
        button.setOnClickListener(view -> {
            String emailsend = "velocitylabs99@gmail.com";
            String emailsubject = subject.getText().toString();
            String emailbody = body.getText().toString();
            // define Intent object with action attribute as ACTION_SEND
            Intent intent = new Intent(Intent.ACTION_SEND);
            // add three fields to intent using putExtra function
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsend});
            intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject);
            intent.putExtra(Intent.EXTRA_TEXT, emailbody);
            // set type of intent
            intent.setType("message/rfc822");
            // startActivity with intent with chooser as Email client using createChooser function
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        });
    }
}
