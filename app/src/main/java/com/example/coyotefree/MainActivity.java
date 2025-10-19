package com.example.coyotefree;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.authenticator.PasscodeViewPinAuthenticator;
import com.kevalpatel.passcodeview.indicators.CircleIndicator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.keys.KeyNamesBuilder;
import com.kevalpatel.passcodeview.keys.RoundKey;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    public static Boolean isTabletConfirmed = false;

    String pinName = "";
    public boolean pinchangeoption = false;
    Boolean over = false;
    int incorrectAttempts=0;
    int[] firstPassword;
    int[] secondPassword;
    SharedPreferences storePassword;
    Boolean firstPinSet = false;
    private static final int STORAGE_PERMISSION_CODE = 23;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().unsubscribeFromTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Unsubscribed!";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("generalTesting")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        if(isTablet()){
            isTabletConfirmed=true; //for controlling tablet history view in history class
        }
        //first gaining necessary file permissions
        //Toast.makeText(this, "Random check", Toast.LENGTH_SHORT).show();

        RiverBasin();
        //sweet spot
        if (checkStoragePermissions()) {
            //main code for passkey logic
        } else {
            //privacyDialog();
            requestForStoragePermissions();
        }
    }
    private void requestForStoragePermissions() {
        //Android is 11 (R) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                //Toast.makeText(this, "First", Toast.LENGTH_SHORT).show();
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                //Toast.makeText(this, "Second", Toast.LENGTH_SHORT).show();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }finally{
            }
        } else {
            //Below android 11
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }// Request Notification Permission (Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE
            );
        }
    }
    public boolean checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        }
        else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }
    public void RiverBasin(){
        storePassword = getSharedPreferences("true_password", MODE_PRIVATE);
        String true_password = storePassword.getString("truePass","default");
        //block to check if user is entering app
        //for the first time
        SharedPreferences userEnter = getSharedPreferences("entry", MODE_PRIVATE);
        boolean firstTimeEntry = userEnter.getBoolean("entry_check", true);
        //passkey block
        if(firstTimeEntry){
            //user is entering for the first time
//            SharedPreferences.Editor editor = userEnter.edit();
//            editor.putBoolean("entry_check", false);
//            editor.apply();
            //Toast.makeText(this, "another code running", Toast.LENGTH_SHORT).show();
            passkeyBlockFirst();
        }else{
            if(true_password.equals("default")){ //case when the user has not set the password
                                   //in the first time but tries to enter the app
                                   //in second time
                passkeyBlockFirst();
            }
            else {
                if(SettingsActivity.pinchange){
                    pinchangeoption=true;
                    //Toast.makeText(this, "just check", Toast.LENGTH_SHORT).show();
                    passkeyBlockNotFirst2();
                }
                else {
                    //user is not entering for the first time
                    passkeyBlockNotFirst();
                }
            }
        }
        //Checking dark mode enabled or not
        // Load saved preferences
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        // Enable or disable dark mode
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                //Android is 11 (R) or above
                                if (Environment.isExternalStorageManager()) {
                                    //Manage External Storage Permissions Granted
                                    Log.d("check", "onActivityResult: Manage External Storage Permissions Granted");
                                    over=true;
                                } else {
                                    Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //Below android 11
                            }
                        }

                    });
    public void passkeyBlockFirst(){
        PinView pinView;
        pinView = (PinView) findViewById(R.id.pin_view);
        //Toast.makeText(this, "Random2", Toast.LENGTH_SHORT).show();
        int[] temp = new int[]{0, 0, 0, 0};
        pinView.setTitle(getString(R.string.Enternewpin));
        pinView.setPinAuthenticator(new PasscodeViewPinAuthenticator(temp));
        pinView.setPinLength(PinView.DYNAMIC_PIN_LENGTH);
        //Build the desired key shape and pass the theme parameters.
        //REQUIRED
        if(isTablet()){
            //Toast.makeText(this, "tablet detected", Toast.LENGTH_SHORT).show();
            pinView.setKey(new RoundKey.Builder(pinView)
                    .setKeyPadding(getResources().getDimension(R.dimen.key_padding))
                    .setKeyStrokeColorResource(R.color.colorAccent)
                    .setKeyStrokeWidth(getResources().getDimension(R.dimen.key_stroke_width))
                    .setKeyTextColorResource(R.color.colorAccent)
                    .setKeyTextSize(getResources().getDimension(R.dimen.key_text_size)));

            pinView.setIndicator(new CircleIndicator.Builder(pinView)
                    .setIndicatorRadius(getResources().getDimension(R.dimen.indicator_radius))
                    .setIndicatorFilledColorResource(R.color.colorAccent)
                    .setIndicatorStrokeColorResource(R.color.colorAccent)
                    .setIndicatorStrokeWidth(getResources().getDimension(R.dimen.indicator_stroke_width)));

            View myView = findViewById(R.id.pin_view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
            params.leftMargin = dptopxconverter(100);
            params.rightMargin = dptopxconverter(100);
            params.bottomMargin = dptopxconverter(90);
            params.topMargin = dptopxconverter(100);
        }
        if(!isTablet()){
            pinView.setKey(new RoundKey.Builder(pinView)
                    .setKeyPadding(R.dimen.key_padding)
                    .setKeyStrokeColorResource(R.color.colorAccent)
                    .setKeyStrokeWidth(R.dimen.key_stroke_width)
                    .setKeyTextColorResource(R.color.colorAccent)
                    .setKeyTextSize(R.dimen.key_text_size));

            pinView.setIndicator(new CircleIndicator.Builder(pinView)
                    .setIndicatorRadius(R.dimen.indicator_radius)
                    .setIndicatorFilledColorResource(R.color.colorAccent)
                    .setIndicatorStrokeColorResource(R.color.colorAccent)
                    .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width));

        }

        pinView.setKeyNames(new KeyNamesBuilder()
                .setKeyOne(this, R.string.key_1)
                .setKeyTwo(this, R.string.key_2)
                .setKeyThree(this, R.string.key_3)
                .setKeyFour(this, R.string.key_4)
                .setKeyFive(this, R.string.key_5)
                .setKeySix(this, R.string.key_6)
                .setKeySeven(this, R.string.key_7)
                .setKeyEight(this, R.string.key_8)
                .setKeyNine(this, R.string.key_9)
                .setKeyZero(this, R.string.key_0));
        pinView.setTitleColor(getColor(R.color.lib_key_default_color));
        pinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                //User authenticated successfully.
                //Toast.makeText(MainActivity.this, "Random3", Toast.LENGTH_SHORT).show();
                int[] userEnteredPin;
                //Toast.makeText(MainActivity.this, "check", Toast.LENGTH_SHORT).show();
                userEnteredPin = pinView.getCurrentTypedPin(); //currently typed pin
                String typedpin="";
                int size;
                size = userEnteredPin.length;
                for (int i=0;i<size;i++){
                    String ch = String.valueOf(userEnteredPin[i]);
                    typedpin = typedpin.concat(ch);
                }
                try {
                    if (!firstPinSet && size == 4) {
                        pinView.setTitle(getString(R.string.EnterPinAgain));
                        firstPinSet = true;
                        firstPassword = userEnteredPin;
                    } else {
                        secondPassword = userEnteredPin;
                        if (comparePassword(firstPassword, secondPassword)) {
                            firstPinSet = false;
                            storePassword = getSharedPreferences("true_password", MODE_PRIVATE);
                            SharedPreferences.Editor editor = storePassword.edit();
                            editor.putString("truePass", typedpin);
                            editor.apply();
                            Toast.makeText(MainActivity.this, getString(R.string.Typedpin) + typedpin, Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, getString(R.string.PinSetSuccessfully), Toast.LENGTH_SHORT).show();
                            Intent intent;
                            if(pinchangeoption) {
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                //Toast.makeText(MainActivity.this, "Running", Toast.LENGTH_SHORT).show();
                            }else
                                intent = new Intent(MainActivity.this, cryto.class);
                            intent.putExtra("password", "just");
                            SettingsActivity.pinchange=false;
                            pinchangeoption=false;
                            startActivity(intent);
                            finish();
                        } else {
                            dialog();
                            pinView.setTitle(getString(R.string.Enternewpin));
                            firstPinSet = false;
                        }
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onAuthenticationFailed() {
                //Calls whenever authentication is failed or user is unauthorized.
                int[] userEnteredPin;
                //Toast.makeText(MainActivity.this, "check", Toast.LENGTH_SHORT).show();
                userEnteredPin = pinView.getCurrentTypedPin(); //currently typed pin
                String typedpin="";
                int size;
                size = userEnteredPin.length;
                for (int i=0;i<size;i++){
                    String ch = String.valueOf(userEnteredPin[i]);
                    typedpin = typedpin.concat(ch);
                }
                try {
                    if (!firstPinSet && size == 4) {
                        pinView.setTitle(getString(R.string.EnterPinAgain));
                        firstPinSet = true;
                        firstPassword = userEnteredPin;
                    } else {
                        secondPassword = userEnteredPin;
                        if (comparePassword(firstPassword, secondPassword)) {
                            firstPinSet = false;
                            storePassword = getSharedPreferences("true_password", MODE_PRIVATE);
                            SharedPreferences.Editor editor = storePassword.edit();
                            editor.putString("truePass", typedpin);
                            editor.apply();
                            Toast.makeText(MainActivity.this, getString(R.string.PinSetSuccessfully), Toast.LENGTH_SHORT).show();
                            Intent intent;
                            if(pinchangeoption) {
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                //Toast.makeText(MainActivity.this, "Running", Toast.LENGTH_SHORT).show();
                            }else
                                intent = new Intent(MainActivity.this, cryto.class);
                            intent.putExtra("password", "just");
                            SettingsActivity.pinchange=false;
                            pinchangeoption=false;
                            startActivity(intent);
                            finish();
                        } else {
                            dialog();
                            pinView.setTitle(getString(R.string.Enternewpin));
                            firstPinSet = false;
                        }
                    }
                }catch (Exception e){

                }
            }
        });

    }
    public void passkeyBlockNotFirst(){
        PinView pinView;
        pinView = (PinView) findViewById(R.id.pin_view);
        int[] true_pass = new int[]{0, 0, 0, 0};
        storePassword = getSharedPreferences("true_password", MODE_PRIVATE);
        String true_password = storePassword.getString("truePass","default");
        //Toast.makeText(this, "password: "+ true_password, Toast.LENGTH_SHORT).show();
        int sizeOfPassword = true_password.length();
        for(int i=0; i<sizeOfPassword;i++){
            int num = Character.getNumericValue(true_password.charAt(i));
            true_pass[i] = num;
        }
        pinView.setPinAuthenticator(new PasscodeViewPinAuthenticator(true_pass));
        pinView.setPinLength(PinView.DYNAMIC_PIN_LENGTH);
        //Build the desired key shape and pass the theme parameters.
        //REQUIRED
        if(isTablet()){
            //Toast.makeText(this, "tablet detected", Toast.LENGTH_SHORT).show();
            pinView.setKey(new RoundKey.Builder(pinView)
                    .setKeyPadding(getResources().getDimension(R.dimen.key_padding))
                    .setKeyStrokeColorResource(R.color.colorAccent)
                    .setKeyStrokeWidth(getResources().getDimension(R.dimen.key_stroke_width))
                    .setKeyTextColorResource(R.color.colorAccent)
                    .setKeyTextSize(getResources().getDimension(R.dimen.key_text_size)));

            pinView.setIndicator(new CircleIndicator.Builder(pinView)
                    .setIndicatorRadius(getResources().getDimension(R.dimen.indicator_radius))
                    .setIndicatorFilledColorResource(R.color.colorAccent)
                    .setIndicatorStrokeColorResource(R.color.colorAccent)
                    .setIndicatorStrokeWidth(getResources().getDimension(R.dimen.indicator_stroke_width)));

            View myView = findViewById(R.id.pin_view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
            params.leftMargin = dptopxconverter(60);
            params.rightMargin = dptopxconverter(60);
            params.bottomMargin = dptopxconverter(40);
            params.topMargin = dptopxconverter(40);

        }
        if(!isTablet()){
            //Toast.makeText(this, "Normal phone detected", Toast.LENGTH_SHORT).show();
            pinView.setKey(new RoundKey.Builder(pinView)
                    .setKeyPadding(R.dimen.key_padding)
                    .setKeyStrokeColorResource(R.color.colorAccent)
                    .setKeyStrokeWidth(R.dimen.key_stroke_width)
                    .setKeyTextColorResource(R.color.colorAccent)
                    .setKeyTextSize(R.dimen.key_text_size));

            pinView.setIndicator(new CircleIndicator.Builder(pinView)
                    .setIndicatorRadius(R.dimen.indicator_radius)
                    .setIndicatorFilledColorResource(R.color.colorAccent)
                    .setIndicatorStrokeColorResource(R.color.colorAccent)
                    .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width));
        }

        pinView.setKeyNames(new KeyNamesBuilder()
                .setKeyOne(this, R.string.key_1)
                .setKeyTwo(this, R.string.key_2)
                .setKeyThree(this, R.string.key_3)
                .setKeyFour(this, R.string.key_4)
                .setKeyFive(this, R.string.key_5)
                .setKeySix(this, R.string.key_6)
                .setKeySeven(this, R.string.key_7)
                .setKeyEight(this, R.string.key_8)
                .setKeyNine(this, R.string.key_9)
                .setKeyZero(this, R.string.key_0));
        //pinView.setTitle();

        pinName = getString(R.string.PinTitle);
        pinView.setTitle(pinName);
        pinView.setTitleColor(getColor(R.color.lib_key_default_color));
        pinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                //User authenticated successfully.
                //Navigate to next screens.
                int ar[] = pinView.getCurrentTypedPin();
                int size=0;
                String passcode="";
                String ch;
                size = ar.length;
                for (int i=0;i<size;i++){
                    ch = Integer.toString(ar[i]);
                    passcode = passcode.concat(ch);
                }
                Intent intent = new Intent(MainActivity.this, cryto.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                //Calls whenever authentication is failed or user is unauthorized.
                ++incorrectAttempts;
                if(incorrectAttempts==4){
                    dialog2();
                }
            }
        });
    }

    public void passkeyBlockNotFirst2(){  // for pinchange option coming from settings
        PinView pinView;
        pinView = (PinView) findViewById(R.id.pin_view);
        int[] true_pass = new int[]{0, 0, 0, 0};
        storePassword = getSharedPreferences("true_password", MODE_PRIVATE);
        String true_password = storePassword.getString("truePass","default");
        //Toast.makeText(this, "password: "+ true_password, Toast.LENGTH_SHORT).show();
        int sizeOfPassword = true_password.length();
        for(int i=0; i<sizeOfPassword;i++){
            int num = Character.getNumericValue(true_password.charAt(i));
            true_pass[i] = num;
        }
        pinView.setPinAuthenticator(new PasscodeViewPinAuthenticator(true_pass));
        pinView.setPinLength(PinView.DYNAMIC_PIN_LENGTH);
        //Build the desired key shape and pass the theme parameters.
        //REQUIRED
        if(isTablet()){
            //Toast.makeText(this, "tablet detected", Toast.LENGTH_SHORT).show();
            pinView.setKey(new RoundKey.Builder(pinView)
                    .setKeyPadding(getResources().getDimension(R.dimen.key_padding))
                    .setKeyStrokeColorResource(R.color.colorAccent)
                    .setKeyStrokeWidth(getResources().getDimension(R.dimen.key_stroke_width))
                    .setKeyTextColorResource(R.color.colorAccent)
                    .setKeyTextSize(getResources().getDimension(R.dimen.key_text_size)));

            pinView.setIndicator(new CircleIndicator.Builder(pinView)
                    .setIndicatorRadius(getResources().getDimension(R.dimen.indicator_radius))
                    .setIndicatorFilledColorResource(R.color.colorAccent)
                    .setIndicatorStrokeColorResource(R.color.colorAccent)
                    .setIndicatorStrokeWidth(getResources().getDimension(R.dimen.indicator_stroke_width)));

            View myView = findViewById(R.id.pin_view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
            params.leftMargin = dptopxconverter(100);
            params.rightMargin = dptopxconverter(100);
            params.bottomMargin = dptopxconverter(90);
            params.topMargin = dptopxconverter(100);

        }
        if(!isTablet()){
            //Toast.makeText(this, "Normal phone detected", Toast.LENGTH_SHORT).show();
            pinView.setKey(new RoundKey.Builder(pinView)
                    .setKeyPadding(R.dimen.key_padding)
                    .setKeyStrokeColorResource(R.color.colorAccent)
                    .setKeyStrokeWidth(R.dimen.key_stroke_width)
                    .setKeyTextColorResource(R.color.colorAccent)
                    .setKeyTextSize(R.dimen.key_text_size));

            pinView.setIndicator(new CircleIndicator.Builder(pinView)
                    .setIndicatorRadius(R.dimen.indicator_radius)
                    .setIndicatorFilledColorResource(R.color.colorAccent)
                    .setIndicatorStrokeColorResource(R.color.colorAccent)
                    .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width));
        }

        pinView.setKeyNames(new KeyNamesBuilder()
                .setKeyOne(this, R.string.key_1)
                .setKeyTwo(this, R.string.key_2)
                .setKeyThree(this, R.string.key_3)
                .setKeyFour(this, R.string.key_4)
                .setKeyFive(this, R.string.key_5)
                .setKeySix(this, R.string.key_6)
                .setKeySeven(this, R.string.key_7)
                .setKeyEight(this, R.string.key_8)
                .setKeyNine(this, R.string.key_9)
                .setKeyZero(this, R.string.key_0));
        //pinView.setTitle();
        pinView.setTitle(getString(R.string.Enteroldpin));
        pinView.setTitleColor(getColor(R.color.lib_key_default_color));
        pinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                //User authenticated successfully.
                //Navigate to next screens.
                int ar[] = pinView.getCurrentTypedPin();
                int size=0;
                String passcode="";
                String ch;
                size = ar.length;
                for (int i=0;i<size;i++){
                    ch = Integer.toString(ar[i]);
                    passcode = passcode.concat(ch);
                }
                passkeyBlockFirst();
            }

            @Override
            public void onAuthenticationFailed() {
                //Calls whenever authentication is failed or user is unauthorized.
                dialog3();
            }
        });
    }
    public void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.Enteragain))
                .setTitle(getString(R.string.Passworddonnotmatch));

        // Add the buttons.
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
            }
        });
        // Create the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        String message = getString(R.string.pressoktosetnewpin)+"\n\n" +
                getString(R.string.reminder)+"\n\n" +
                getString(R.string.settingANewPinWillErase);

        SpannableString spannableMessage = new SpannableString(message);

        // Find the starting and ending index of the word "Reminder"
        int start = message.indexOf(getString(R.string.reminder));
        int end = start + getString(R.string.reminder).length();

        // Apply bold style to the word "Reminder"
        spannableMessage.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setMessage(spannableMessage)
                .setTitle(getString(R.string.ForgotPassword));

        // Add the buttons.
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                //data wiping code goes here
                datawiper();
                incorrectAttempts=0;
                passkeyBlockFirst();
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

    public void datawiper(){
        //clears locked files
        DBHandler obj = new DBHandler(MainActivity.this);
        try {
            Boolean msg = obj.deleteAllRowsAndProcessFirstColumn();
            if(msg){
                Toast.makeText(this, getString(R.string.DataWipedSuccessfully), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, getString(R.string.ErrorInWipingData), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //clears restore history
        try {
            DBHandler2 obj2;
            obj2 = new DBHandler2(MainActivity.this);
            obj2.resetTable();
        }catch (Exception e){
            //statements
        }

        //clears notepad
        try {
            DatabaseHelper obj3 = new DatabaseHelper(MainActivity.this);
            List<Note> notes = obj3.getAllNotes();

            int sizeOfNotes = notes.size();

            ArrayList<Long> noteids = obj3.getAllNoteIds();

            obj3.deleteAllNotes(noteids,sizeOfNotes);
        }catch (Exception e){
            //statements
        }
    }

    public void dialog3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.Failed))
                .setTitle(getString(R.string.Passworddonnotmatch));

        // Add the buttons.
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
            }
        });
        // Create the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public Boolean comparePassword(int[] pass1,int[] pass2){
        int size = pass1.length;
        Boolean flag=false;
        for(int i=0;i<size;i++){
            if(pass1[i]!=pass2[i])
                flag=true;
        }
        if(flag)
            return false;
        else
            return true;
    }
    public boolean isTablet() {
        int smallestScreenWidthDp = getResources().getConfiguration().smallestScreenWidthDp;
        return smallestScreenWidthDp >= 600; // Tablets usually have at least 600dp.
    }

    public int dptopxconverter(int marginInDp){
        float scale = getResources().getDisplayMetrics().density;
        return ((int) (marginInDp * scale + 0.5f));
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


