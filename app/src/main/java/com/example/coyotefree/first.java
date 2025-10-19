package com.example.coyotefree;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class first extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences userEnter = getSharedPreferences("entry", MODE_PRIVATE);
        boolean firstTimeEntry = userEnter.getBoolean("entry_check", true);
        if(firstTimeEntry){
            SharedPreferences.Editor editor = userEnter.edit();
            editor.putBoolean("entry_check", false);
            editor.apply();
            privacyDialog();

        }else{
            Intent intent = new Intent(first.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public void privacyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(first.this);

        // Create the message with links.
        String message = "By choosing Confirm, you agree to our Privacy Policy and Terms of Use.";

        SpannableString spannableMessage = new SpannableString(message);

        // Make the "Privacy Policy" and "Terms of Use" bold and clickable
        int privacyPolicyStart = message.indexOf("Privacy Policy");
        int privacyPolicyEnd = privacyPolicyStart + "Privacy Policy".length();
        int termsOfUseStart = message.indexOf("Terms of Use");
        int termsOfUseEnd = termsOfUseStart + "Terms of Use".length();

        // Make text bold
        spannableMessage.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), privacyPolicyStart, privacyPolicyEnd, 0);
        spannableMessage.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), termsOfUseStart, termsOfUseEnd, 0);

        // Make links clickable
        spannableMessage.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Open Privacy Policy link
                String url = "https://sites.google.com/view/coyotfilelocker-privacypolicy";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        }, privacyPolicyStart, privacyPolicyEnd, 0);

        spannableMessage.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Open Terms of Use link
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/coyotfilelocker-privacypolicy/home/termsconditions"));
                startActivity(browserIntent);
            }
        }, termsOfUseStart, termsOfUseEnd, 0);

        builder.setMessage(spannableMessage)
                .setTitle("Privacy Setting")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User taps OK button.
                        //RiverBasin();
                        dialog4();
                    }
                });

        // Set the link movement method
        AlertDialog dialog = builder.create();
        // Close the app if the user presses the back button or taps outside
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //finishAffinity();  // Closes the entire app
                dialog4();
            }
        });
        dialog.show();

        // Enable clickable links
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void dialog4(){
        AlertDialog.Builder builder = new AlertDialog.Builder(first.this);
        builder.setMessage("To perform lock operation we require your storage permission.\n\n"+
                        "Do you want to grant this permission ?")
                .setTitle("Storage Permission Required");

        // Add the buttons.
        builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                //requestForStoragePermissions();
                Intent intent = new Intent(first.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Create the AlertDialog.
        AlertDialog dialog = builder.create();

        // Close the app if the user presses the back button or taps outside
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = new Intent(first.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }
}