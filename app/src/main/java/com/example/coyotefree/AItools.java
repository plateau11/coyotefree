package com.example.coyotefree;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AItools extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aitools);
        TextView dscanner = findViewById(R.id.doc_scanner);
        dscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AItools.this, documentScanner.class);
                startActivity(intent);
                finish();
                //Toast.makeText(SettingsActivity.this, "grid size clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}