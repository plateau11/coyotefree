package com.example.coyotefree;
import static android.app.PendingIntent.getActivity;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.core.app.NotificationManagerCompat;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.activity.EdgeToEdge;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewException;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

public class cryto extends AppCompatActivity implements PickiTCallbacks {
    CheckBox singleFile;
    CheckBox multipleFiles;
    Boolean selectedmultiple;
    Boolean selectedsingle;
    ArrayList<String> filesSelected;
    ArrayList<String> tempNames;
    int file_select_count;
    public static BillingClient billingClient;
    private ProductDetails productDetails;
    private static SharedPreferences prefs;

    //product Details
    private ProductDetails premiumProductDetails;
    private ProductDetails relaxProductDetails;
    public static ProductDetails filterProductDetails;


    //first product (quote section)
    private static SharedPreferences prefss;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_PREMIUM = "isPremiumUser";
    private static final String PRODUCT_ID = "premium_upgrade";


    //second product (relax section)
    private static SharedPreferences prefss2;
    private static final String PREFS_NAME2 = "MyAppPrefs2";
    private static final String KEY_PREMIUM2 = "isPremiumUser2";
    private static final String PRODUCT_ID2 = "relax_section";


    //third product (filter section)
    private static SharedPreferences prefss3;
    private static final String PREFS_NAME3 = "MyAppPrefs3";
    private static final String KEY_PREMIUM3 = "isPremiumUser3";
    private static final String PRODUCT_ID3 = "filter_types";


    //fourth product (multiple file section)
    private static SharedPreferences prefss4;
    private static final String PREFS_NAME4 = "MyAppPrefs4";
    private static final String KEY_PREMIUM4 = "isPremiumUser4";
    private static final String PRODUCT_ID4 = "select_multiplefiles";


    private static final int UPDATE_REQUEST_CODE = 530;
    private AlertDialog progressDialog;
    File dir;
    File dir2;
    ArrayList<String> myarraylist2;
    public static final Boolean temporary_flag = false;
    public static String finalEncrypted_text;
    EditText ed;
    Boolean over = true;
    private static final int STORAGE_PERMISSION_CODE = 23;
    private static final int NOTIFICATION_PERMISSION_CODE = 1002;
    String text_key;
    int res_code=0;
    CircularProgressBar progressBar;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static boolean directory_exists;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    private ActivityResultLauncher<Intent> mGetContent;
    private static int pswdIterations = 100;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private String AESSalt = "AES128bit";
    private String initializationVector = "2195081919109305";
    private static String mark = File.separator;
    PickiT pickiT;
    CheckBox originalDelete;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    private DBHandler dbHandler;
    NavigationView navigationView;
    String CHANNEL_ID = "mychannel";
    String textTitle = "Lock Complete!";
    String textContent = "File saved";
    private static String encrypt_dir=null;
    private static String decrypt_dir=null;
    private static String temp_filename = "";
    private static String filename="";
    private RadioGroup radioGroup;
    private RadioButton imageOption;
    private RadioButton docOption;
    private TextView errorMessage;
    private int filetype;
    LinearProgressIndicator lpi;
    private Thread progressBarThread;
    AppUpdateManager appUpdateManager;
    ReviewManager manager;
    FloatingActionButton chatbotButton;

    ReviewInfo reviewInfo;
    private static byte[] getRaw(String plainText, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt.getBytes(), pswdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    public int encryptaes(String text_key, String filename) throws Exception {
        //Toast.makeText(this, "enc: "+filename, Toast.LENGTH_SHORT).show();
        SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));
        FileInputStream fis=new FileInputStream(filename);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int dotIndex = filename.lastIndexOf('.');
        // Check if there is a dot and it's not the first character
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
        }
        File test = new File(filename);
        String new_filename = test.getName();
        //Toast.makeText(this, "new file name: "+new_filename, Toast.LENGTH_SHORT).show();
        //tempfunc(new_filename);
        cryto.temp_filename = encrypt_dir+"/"+new_filename;
        tempNames.add(cryto.temp_filename);  //used in run after encrypt function to delete the source files
        FileOutputStream fos = new FileOutputStream(cryto.temp_filename);
        byte[] b = new byte[1024];
        int i = cis.read(b);
        while (i != -1) {
            fos.write(b, 0, i);
            i = cis.read(b);
        }
        fos.close();
        fis.close();
        cis.close();
        res_code=1;
        return 1;
    }

    // Show dialog
    private void showPleaseWaitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // user cannot dismiss
        builder.setView(R.layout.dialog_progress); // custom layout with ProgressBar + text
        progressDialog = builder.create();
        progressDialog.show();
    }

    // Hide dialog
    private void hidePleaseWaitDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void decryptfilesforreencryption(String text_key, String filename, String filenameonly, String decrypt_dir) throws Exception {
        String temp_file_name = "";
        SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));

        /*String filename_only = filename.substring(filename.lastIndexOf(mark) + 1);
        int dotIndex = filename.lastIndexOf('.');
        // Check if there is a dot and it's not the first character
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            // Extract the extension
        }
         */

        FileInputStream fis = new FileInputStream(filename);

        CipherInputStream cis = new CipherInputStream(fis, cipher);

        //Toast.makeText(this, "check: "+ filenameonly, Toast.LENGTH_SHORT).show();

        temp_file_name = decrypt_dir+"/"+filenameonly;

        FileOutputStream fos = new FileOutputStream(temp_file_name);

        /*byte[] buf = new byte[1024];
        int read;
        while ((read = cis.read(buf)) != -1) {
            fos.write(buf, 0, read);
            //media player code
        }*/
        byte[] b = new byte[1024];
        int i = 0;
        try {
            i = cis.read(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (i != -1) {
            try {
                fos.write(b, 0, i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                i = cis.read(b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void reencryption() throws Exception {
        String oldkey = getString(R.string.text_key);

        /** new security code **/
        /*SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean hasRunBefore = prefs.getBoolean("hasRunBefore", false);

        if(!(hasRunBefore))
            oldkey = getString(R.string.text_key);
        else{
            //String text_key;
            String second_test = null;
            try {
                second_test = keyfinal();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            oldkey = second_test;
        }*/

        //Toast.makeText(this, "Key final: "+ text_key, Toast.LENGTH_SHORT).show();
        /** new security code **/

        /*String testString = null;
        try {
            testString = keyfinal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        oldkey = testString; */

        dbHandler = new DBHandler(cryto.this);
        //myarraylist2.clear();
        myarraylist2 = new ArrayList<>();
        myarraylist2 = dbHandler.readCourses();
        int size = myarraylist2.size();
        //first create a new temp decrypt directory
        dir2 = new File(this.getFilesDir(), "/testing47");
        directory_exists = directoryCheck(dir2);
        if(directory_exists) {
            decrypt_dir = dir2.getAbsolutePath();
            //Toast.makeText(this, "Directory already exists", Toast.LENGTH_SHORT).show();
        }
        else{
            if(dir2.mkdirs()) {
                decrypt_dir = dir2.getAbsolutePath();
                //Toast.makeText(this, "New directory created successfully", Toast.LENGTH_SHORT).show();
            }
                //Toast.makeText(this, "Directory creation failed", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < size; i++) {
            String filename = myarraylist2.get(i);  //full file name from
                                                    // encryption folder
            // Create a File object using the file path
            File file = new File(filename);
            // Get the name of the file (including the extension)
            String fileName = file.getName();
            // Find the index of the last dot (.) in the filename
            int dotIndex = fileName.lastIndexOf(".");
            String filenameonly = "";
            // If a dot exists and is not the first character, extract the filename without the extension
            if (dotIndex > 0) {
                filenameonly = fileName.substring(0, dotIndex);
            } else {
                // If there is no extension, just return the full filename
            }
            //run decrypt function for each files
            //Toast.makeText(this, "filename only1: "+ filenameonly, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, ": "+ filename, Toast.LENGTH_SHORT).show();

            File test = new File(filename);
            String new_filename = test.getName();
            //cryto.temp_filename = new_filename;
            //temp_name = new_filename;

            decryptfilesforreencryption(oldkey,filename,new_filename,decrypt_dir);


            //Toast.makeText(this, "Decrypted successfully", Toast.LENGTH_SHORT).show();
        }

        //encryption after decryption for reencryption
        String new_key;
        /** new security code **/
        //String text_key;
        String second_test = null;
        try {
            second_test = keyfinal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new_key = second_test;
       // Toast.makeText(cryto.this, "Key final: "+ second_test, Toast.LENGTH_SHORT).show();
        /** new security code **/
        for (int j = 0; j < size; j++) {
            String sourceFile="";
            String targetFile="";
            String text_key = new_key;
            String filename = myarraylist2.get(j);
            // Create a File object using the file path
            File file = new File(filename);
            // Get the name of the file (including the extension)
            String fileName = file.getName();

            String filenameonly = "";
            /*
            // Find the index of the last dot (.) in the filename
            int dotIndex = fileName.lastIndexOf(".");

            // If a dot exists and is not the first character, extract the filename without the extension
            if (dotIndex > 0) {
                filenameonly = fileName.substring(0, dotIndex);
            } else {
                // If there is no extension, just return the full filename
            }*/

            sourceFile = decrypt_dir+"/"+filenameonly;
            targetFile = encrypt_dir+"/"+filenameonly;

            encryptionAfterDecryptionForReencryption(sourceFile,targetFile,
                    text_key,filename);

        }//delete the decrypt directory after reencryption
        //Toast.makeText(this, "testing code", Toast.LENGTH_SHORT).show();
        // Usage example
        dir2 = new File(getFilesDir(), "testing47");
        if (deleteDirectory(dir2)) {
            //Toast.makeText(this, "Directory deleted", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Failed to delete directory", Toast.LENGTH_SHORT).show();
        }
    }
    public int encryptionAfterDecryptionForReencryption(String sourceFile, String targetFile,
                                                        String text_key, String filename) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));

        File test = new File(filename);
        String new_filename = test.getName();
        String temp_name="";
        temp_name = decrypt_dir+"/"+new_filename;


        //FileInputStream fis=new FileInputStream(sourceFile);

        FileInputStream fis = new FileInputStream(temp_name);

        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int dotIndex = filename.lastIndexOf('.');
        // Check if there is a dot and it's not the first character
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
        }

        //sourceFile = decrypt_dir+"/"+filenameonly;
        //targetFile = encrypt_dir+"/"+filenameonly;

        //File test = new File(filename);

        //new_filename = test.getName();
        //cryto.temp_filename = encrypt_dir+"/"+new_filename;

        String temp_name2 = "";
        temp_name2 = encrypt_dir+"/"+new_filename;

        //FileOutputStream fos = new FileOutputStream(targetFile);

        FileOutputStream fos = new FileOutputStream(temp_name2);

        byte[] b = new byte[1024];
        int i = cis.read(b);
        while (i != -1) {
            fos.write(b, 0, i);
            i = cis.read(b);
        }
        fos.close();
        fis.close();
        cis.close();
        res_code=1;
        return 1;
    }

    private boolean deleteDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    boolean success = deleteDirectory(child);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // delete file or empty directory
        return dir != null && dir.delete();
    }

    public void checkAppUpdate(){
        //automatic check for available app update
        appUpdateManager = AppUpdateManagerFactory.create(cryto.this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                    startAppUpdateImmediate(appUpdateInfo);
            }
        });
    }

    public void appReview(){
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
            } else {
                // There was some problem, log or handle the error code.
                @ReviewErrorCode int reviewErrorCode = ((ReviewException) task.getException()).getErrorCode();
            }
        });
    }

    public void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo){
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    cryto.this,
                    AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                    UPDATE_REQUEST_CODE
                    );
        }catch(Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LocaleHelper.languageChanged) {
            LocaleHelper.languageChanged = false;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        //refreshing ui based on language change from en to es and vice versa
        /*if (LocaleHelper.languageChanged) {
            LocaleHelper.languageChanged = false;
            recreate();
        }*/

        checkNewAppVersionState();
    }

    private void checkNewAppVersionState() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            //IMMEDIATE:
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                startAppUpdateImmediate(appUpdateInfo);
                            }
                        });

    }

    public static String generateSecureString() {
        final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        java.security.SecureRandom random = new java.security.SecureRandom();
        int length = 14; // 14 chars → strong against brute-force
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public SecretKey generateKey() throws Exception {
        // Step 1: Create KeyUpdate instance
        KeyUpdate keyUpdate = new KeyUpdate();
        // Step 2: Get or create key with alias "MyAppKey"
        SecretKey secretKey = keyUpdate.getOrCreateKey("MyAppKey");

        return secretKey;
    }

    public static String encrypt(SecretKey key, String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);  // Keystore generates IV

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));
        byte[] iv = cipher.getIV();  // Must store this for decryption

        // Combine IV + ciphertext
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        return android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT);
    }

    private Boolean encryptOnceAndStore(String plaintext) {
        final String PREFS_NAME = "my_prefs";
        final String KEY_ENCRYPTED_STRING = "encrypted_string";
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if encrypted string already exists
        String encryptedString = prefs.getString(KEY_ENCRYPTED_STRING, null);
        SecretKey secretKey;
        try {
            secretKey = generateKey(); // Your method to generate/load Keystore key
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (encryptedString == null) {
            // Encrypt plaintext and store for future
            try {
                encryptedString = encrypt(secretKey, plaintext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            prefs.edit().putString(KEY_ENCRYPTED_STRING, encryptedString).apply();
        }

        // Decrypt whenever needed
        /*String decryptedString;
        try {
            decryptedString = decrypt(secretKey, encryptedString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

        try {
            reencryption();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static String decrypt(SecretKey key, String base64Ciphertext) throws Exception {
        byte[] combined = android.util.Base64.decode(base64Ciphertext, android.util.Base64.DEFAULT);

        // Extract IV (first 12 bytes)
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extract ciphertext
        byte[] ciphertext = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, "UTF-8");
    }

    public String keyfinal() throws Exception {
        final String PREFS_NAME = "my_prefs";
        final String KEY_ENCRYPTED_STRING = "encrypted_string";

        // Step 1: Create KeyUpdate instance
        KeyUpdate keyUpdate = new KeyUpdate();
        // Step 2: Get or create key with alias "MyAppKey"
        SecretKey secretKey = keyUpdate.getOrCreateKey("MyAppKey");

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Check if encrypted string already exists
        String encryptedString = prefs.getString(KEY_ENCRYPTED_STRING, null);

        // Decrypt whenever needed
        String decryptedString;
        try {
            decryptedString = decrypt(secretKey, encryptedString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return decryptedString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.activity_cryto);
        System.gc();
        try {
            checkAppUpdate();  //checking for app update
        }catch(Exception e){

        }

        try{
            appReview(); //get review info object
            Task<Void> flow = manager.launchReviewFlow(cryto.this, reviewInfo);
            flow.addOnCompleteListener(task -> {
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
            });
        }catch (Exception e){

        }
        lpi = findViewById(R.id.lpi);
        lpi.setVisibility(GONE);

        chatbotButton = findViewById(R.id.chatbot_button);

        chatbotButton.setOnClickListener(v -> {
            ChatbotBottomSheet sheet = new ChatbotBottomSheet();
            sheet.show(getSupportFragmentManager(), "chatbotSheet");
        });

        dir = new File(this.getFilesDir(), "/testing46");
        directory_exists = directoryCheck(dir);
        if(directory_exists) {
            encrypt_dir = dir.getAbsolutePath();
        }
        else{
            if(dir.mkdirs()) {
                encrypt_dir = dir.getAbsolutePath();
                //Toast.makeText(this, encrypt_dir, Toast.LENGTH_SHORT).show();
            }else{}
                //Toast.makeText(this, "Directory creation failed", Toast.LENGTH_SHORT).show();
        }


        //-------------------------New security update code (start)----------------------------------
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean hasRunBefore = prefs.getBoolean("hasRunBefore", false);
        //Toast.makeText(this, "check: "+ hasRunBefore, Toast.LENGTH_SHORT).show();

        // When starting your long operation
        //showPleaseWaitDialog();

        // Run your code in background to avoid blocking UI
        //new Thread(() -> {
            //heavyOperation();
            if (!hasRunBefore) {
                //Toast.makeText(this, "Running code for the first time!", Toast.LENGTH_SHORT).show();
                String secureString = generateSecureString();
                Boolean resCode = encryptOnceAndStore(secureString);

                //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
                // mark as run
                /*SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("hasRunBefore", true);
                editor.apply();*/

                //Toast.makeText(this, "test 2", Toast.LENGTH_SHORT).show();
            }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("hasRunBefore", true);
        editor.apply();

            // when finished → hide dialog on UI thread
            //runOnUiThread(this::hidePleaseWaitDialog);
        //}).start();

        //---------------------------New security update code (end)----------------------------------


        //---------------------------In app purchase logic (start)--------------------------------------

        /*prefss = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefss2 = getSharedPreferences(PREFS_NAME2, MODE_PRIVATE);
        prefss3 = getSharedPreferences(PREFS_NAME3, MODE_PRIVATE);
        prefss4 = getSharedPreferences(PREFS_NAME4, MODE_PRIVATE);

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases(
                        PendingPurchasesParams.newBuilder()
                                .enableOneTimeProducts()  // Required for one-time in-app products
                                .build()
                )
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                        }
                    }
                })
                .build();


        // ✅ Start the connection
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Billing ready — query products and check owned purchases
                    queryProductDetails();
                    checkIfUserOwnsPremium();  // ✅ this is the call you were missing
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Optionally retry connection later
            }
        });
        */
        //---------------------------In app purchase logic (end)--------------------------------------

        //---------------------------check box files section (start)----------------------------------

        singleFile = findViewById(R.id.checkbox_single);
        multipleFiles = findViewById(R.id.checkbox_multiple);
        selectedsingle = false;
        selectedmultiple = false;

        singleFile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Uncheck the other checkbox
                multipleFiles.setChecked(false);
                singleFile.setChecked(true);
                selectedsingle = true;
                selectedmultiple = false;

                //Your logic for single file mode
                //Toast.makeText(this, "Single file mode selected", Toast.LENGTH_SHORT).show();

                // Example: Disable multiple file picker
                // enableSingleFilePicker();
            } else {
                // Optional: logic when unchecked
                // disableSingleFilePicker();
            }
        });

        multipleFiles.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Uncheck the other checkbox
                singleFile.setChecked(false);
                multipleFiles.setChecked(true);
                selectedsingle = false;
                selectedmultiple = true;

                //Your logic for multiple files mode
                //Toast.makeText(this, "Multiple files mode selected", Toast.LENGTH_SHORT).show();

                // Example: Enable multiple file picker
                // enableMultipleFilePicker();
            } else {
                // Optional: logic when unchecked
                // disableMultipleFilePicker();
            }
        });

        //---------------------------check box files section (end)------------------------------------

        filesSelected = new ArrayList<>();
        tempNames = new ArrayList<>();

        //checking the current locale
        Locale currentLocale;
        currentLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
        String languageCode = currentLocale.getLanguage();
        //Toast.makeText(this, "current locale: "+ languageCode, Toast.LENGTH_SHORT).show();

        //if dark mode is enabled
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        if(isDarkModeEnabled){
            Toolbar bar = findViewById(R.id.toolbar);
            bar.setBackgroundColor(Color.parseColor("#0C0F10"));
            //LinearLayout ll2 = findViewById(R.id.ll2);
            //ll2.setBackgroundColor(Color.parseColor("#0C0F10"));
            NavigationView navigationView = findViewById(R.id.navigationView);

            //Toast.makeText(this, "escaped", Toast.LENGTH_SHORT).show();
                View oldHeader = navigationView.getHeaderView(0);
                navigationView.removeHeaderView(oldHeader);
            // Inflate a new header layout
            View newHeader = navigationView.inflateHeaderView(R.layout.header_lay2);

            AppBarLayout abl = findViewById(R.id.barbackground2);
            abl.setBackgroundColor(Color.parseColor("#0C0F10"));

            LinearLayout backgroundLayout = findViewById(R.id.backgroundimageoption);
            backgroundLayout.setBackground(null);

        }
        filetype = 0;  //no radio button selected
        errorMessage = findViewById(R.id.errorMessage);
        imageOption = (RadioButton)findViewById(R.id.radio_image);
        docOption = (RadioButton)findViewById(R.id.radio_doc);
        imageOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filetype = 1;
            }
        });
        docOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filetype = 2;
            }
        });
        radioGroup = (RadioGroup)findViewById(R.id.groupradio);
        // Add the Listener to the RadioGroup
        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override
                    // The flow will come here when
                    // any of the radio buttons in the radioGroup
                    // has been clicked
                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId)
                    {
                        // Get the selected Radio Button
                        RadioButton
                                radioButton
                                = (RadioButton)group
                                .findViewById(checkedId);
                    }
                });
        navigationView= findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id==R.id.Feedback) {
                    Intent intent = new Intent(cryto.this, Feedback.class);
                    startActivity(intent);
                } else if (id==R.id.compress) {
                    Intent intent = new Intent(cryto.this, Compress.class);
                    startActivity(intent);
                }else if (id==R.id.ai) {
                    Intent intent = new Intent(cryto.this, AItools.class);
                    startActivity(intent);
                }
                /*else if (id==R.id.ppolicy) {
                    //Intent intent = new Intent(cryto.this, privacy_policy.class);
                    //startActivity(intent);
                    String url = "https://sites.google.com/view/coyotfilelocker-privacypolicy";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } */else if(id==R.id.About){
                    Intent intent = new Intent(cryto.this, About.class);
                    startActivity(intent);
                } else if (id==R.id.settings) {
                    Intent intent = new Intent(cryto.this, SettingsActivity.class);
                    startActivity(intent);
                } else if (id==R.id.share) {
                    String url = getString(R.string.downloadcoyote)+"\n"+
                            getString(R.string.checkitout)+"\n\n"+"https://play.google.com/store/apps/details?id=velocity.labs.coyotefree";
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity((Intent.createChooser(shareIntent,"Share via")));
                } else if (id==R.id.exit) {
                    dialogExitApp();
                }
                else if (id==R.id.support) {
                    Intent intent = new Intent(cryto.this, Help.class);
                    startActivity(intent);
                    //Toast.makeText(cryto.this, "Support", Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.note) {
                    Intent intent = new Intent(cryto.this, Notes.class);
                    startActivity(intent);
                    //Toast.makeText(cryto.this, "Support", Toast.LENGTH_SHORT).show();
                }

                else if (id==R.id.pro) {
                    String playStoreLink = "https://play.google.com/store/apps/details?id=velocity.labs.coyote"; // Replace with your app's actual link

                    try {
                        // Open in Play Store app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=velocity.labs.coyote"));
                        intent.setPackage("com.android.vending"); // Ensures Play Store app is used
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Open in browser if Play Store app is not available
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
                        startActivity(intent);
                    }
                }


                /*if (id == R.id.quote) {
                    //boolean isPremium = prefss.getBoolean(KEY_PREMIUM, false);
                    //SharedPreferences prefss = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    boolean isPremium = prefss.getBoolean(KEY_PREMIUM, false);
                    if (isPremium) {
                        startActivity(new Intent(cryto.this, quote.class));
                    } else {
                        if (premiumProductDetails != null) {
                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(
                                            Collections.singletonList(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                            .setProductDetails(premiumProductDetails)
                                                            .build()
                                            )
                                    )
                                    .build();
                            billingClient.launchBillingFlow(cryto.this, billingFlowParams);
                        } else {
                            Toast.makeText(cryto.this, "Purchase unavailable. Try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }*/

                if (id == R.id.quote) {
                    BillingManager billingManager = BillingManager.getInstance(cryto.this);
                    boolean isPremium = billingManager.hasAccess(KEY_PREMIUM, 1); // For relax_section
                    if (isPremium) {
                        startActivity(new Intent(cryto.this, quote.class));
                    } else {
                        billingManager.launchPurchaseFlow(cryto.this, "premium_upgrade");
                    }
                }

                /*if (id == R.id.relax) {
                    //boolean isPremium = prefss.getBoolean(KEY_PREMIUM, false);
                    //SharedPreferences prefss = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    boolean isPremium = prefss2.getBoolean(KEY_PREMIUM2, false);
                    if (isPremium) {
                        startActivity(new Intent(cryto.this, relax.class));
                    } else {
                            if (relaxProductDetails != null) {
                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(
                                            Collections.singletonList(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                            .setProductDetails(relaxProductDetails)
                                                            .build()
                                            )
                                    )
                                    .build();
                            billingClient.launchBillingFlow(cryto.this, billingFlowParams);
                        } else {
                            Toast.makeText(cryto.this, "Purchase unavailable. Try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }*/

                if (id == R.id.relax) {
                    BillingManager billingManager = BillingManager.getInstance(cryto.this);
                    boolean isPremium = billingManager.hasAccess(KEY_PREMIUM2, 2); // For relax_section
                    if (isPremium) {
                        startActivity(new Intent(cryto.this, relax.class));
                    } else {
                        billingManager.launchPurchaseFlow(cryto.this, "relax_section");
                    }
                }

                /*
                else if (id==R.id.relax) {
                    Intent intent = new Intent(cryto.this, relax.class);
                    startActivity(intent);
                    //Toast.makeText(cryto.this, "Support", Toast.LENGTH_SHORT).show();
                }
                */
                else if (id==R.id.rate) {

                    String playStoreLink = "https://play.google.com/store/apps/details?id=velocity.labs.coyotefree"; // Replace with your app's actual link

                    try {
                        // Open in Play Store app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=velocity.labs.coyotefree"));
                        intent.setPackage("com.android.vending"); // Ensures Play Store app is used
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Open in browser if Play Store app is not available
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
                        startActivity(intent);
                    }
                }
                else{
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        pickiT = new PickiT(this, this, this);
        //pickiT.getUriFromPicker(this);
        Button encrypt_button = (Button) findViewById(R.id.button_encrypt);
        Button browse_button = (Button) findViewById(R.id.button_browse);
        Button history = (Button) findViewById(R.id.history);

        dbHandler = new DBHandler(cryto.this);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cryto.this,History.class);
                startActivity(intent);
            }
        });

        browse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_select_count=0;
                filesSelected.clear();
                tempNames.clear();
                if (checkStoragePermissions()) {
                    if(checkNoticationPermission()) {

                        if(selectedmultiple){ //multiple file select case

                            BillingManager billingManager = BillingManager.getInstance(cryto.this);
                            boolean isPremium = billingManager.hasAccess(KEY_PREMIUM4, 4); // For relax_section
                            if (isPremium) {
                                //startActivity(new Intent(cryto.this, quote.class));
                                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                Intent intent;
                                //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                //intent.setType();
                                if (filetype == 1) {
                                    errorMessage.setVisibility(View.GONE);
                                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intent.setType("image/* video/*");
                                    // Allow multiple selection
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    mGetContent.launch(intent);
                                }
                                //intent.setPackage("com.google.android.apps.nbu.files");
                                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                                if (filetype == 2) {
                                    errorMessage.setVisibility(View.GONE);
                                /*if(Build.VERSION.SDK_INT==30){
                                    intent = new Intent(Intent.ACTION_PICK); //stable for version(30)
                                    mGetContent.launch(intent);
                                }*/
                                    //if(!(Build.VERSION.SDK_INT ==30)){
                                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //stable for all version(29,31)
                                    intent.setType("*/*");
                                    // Allow multiple selection
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    mGetContent.launch(intent);
                                    //}
                                }
                                //mGetContent.launch("*/*");
                                if (filetype == 0) {
                                    errorMessage.setVisibility(View.VISIBLE);
                                }
                            } else {
                                billingManager.launchPurchaseFlow(cryto.this, "select_multiplefiles");
                            }
                        }

                        else{  //single file select case

                                //startActivity(new Intent(cryto.this, quote.class));
                                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                Intent intent;
                                //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                //intent.setType();
                                if (filetype == 1) {
                                    errorMessage.setVisibility(View.GONE);
                                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intent.setType("image/* video/*");
                                    mGetContent.launch(intent);
                                }

                                //intent.setPackage("com.google.android.apps.nbu.files");
                                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                                if (filetype == 2) {
                                    errorMessage.setVisibility(View.GONE);
                                /*if(Build.VERSION.SDK_INT==30){
                                    intent = new Intent(Intent.ACTION_PICK); //stable for version(30)
                                    mGetContent.launch(intent);
                                }*/
                                    //if(!(Build.VERSION.SDK_INT ==30)){
                                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //stable for all version(29,31)
                                    intent.setType("*/*");
                                    mGetContent.launch(intent);
                                    //}
                                }
                                //mGetContent.launch("*/*");
                                if (filetype == 0) {
                                    errorMessage.setVisibility(View.VISIBLE);
                                }
                        }
                    }else{
                        dialog5();
                    }
                }else{
                    dialog4();
                }
            }
            });

        pdfPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //Toast.makeText(this, "checking45", Toast.LENGTH_SHORT).show();
                    pickiT.getPath(result.getData().getData(), Build.VERSION.SDK_INT);
                });

        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result-> {
                        if(selectedmultiple){
                            Intent data = result.getData();
                            if (data != null) {
                                if (data.getClipData() != null) {
                                    // Multiple files selected
                                    file_select_count = data.getClipData().getItemCount();
                                    for (int i = 0; i < file_select_count; i++) {
                                        Uri uri = data.getClipData().getItemAt(i).getUri();
                                        pickiT.getPath(uri, Build.VERSION.SDK_INT);
                                    }
                                } else if (data.getData() != null) {
                                    // Single file selected
                                    Uri uri = data.getData();
                                    pickiT.getPath(uri, Build.VERSION.SDK_INT);
                                } else {
                                    // No valid URI
                                    Toast.makeText(this, getString(R.string.filenotchosen), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                lpi.setVisibility(GONE);
                                Toast.makeText(this, getString(R.string.filenotchosen), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Intent data = result.getData();
                            if(data!=null){
                                Uri uri = data.getData();
                                if (uri != null) {
                                    pickiT.getPath(uri, Build.VERSION.SDK_INT);
                                    //pickiT.getPath(uri, 30);
                                } else {
                                    // Handle the case where uri is null
                                    //Log.e("MyApp", "URI is null");
                                    //Toast.makeText(this, "File not chosen", Toast.LENGTH_SHORT).show();
                                    // Show user-friendly message or perform an alternative action
                                }
                            }else {
                                lpi.setVisibility(GONE);
                                // Handle the case where data is null
                                Toast.makeText(this, getString(R.string.filenotchosen), Toast.LENGTH_SHORT).show();
                                // Show user-friendly message or perform an alternative action
                            }
                            //pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
                        }
                });

        //finalEncrypted_text = shortKey;
        //Toast.makeText(this, "short key: "+ shortKey, Toast.LENGTH_SHORT).show();
        encrypt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean exists = processPathAndCheck(filename, "velocity.labs.coyote");
                //before locking the file first check if storage permission is allowerd or not
                DBHandler dbHandler = new DBHandler(cryto.this);
                ArrayList<String> current_locker_files = dbHandler.readOrgFilename();
                Boolean match = false;
                int size_temporary = current_locker_files.size();
                for(int i=0;i<size_temporary;i++){
                    if(filename.equals(current_locker_files.get(i))) {
                        match = true;
                        //Toast.makeText(cryto.this, "locker file: "+ current_locker_files.get(i)+" browsed file: "+ filename, Toast.LENGTH_SHORT).show();
                    }
                }
                if (checkStoragePermissions()) {
                   if(!match) {
                       if (!exists) {
                           ed = findViewById(R.id.edit_filename);
                           ed.setText("");
                           lpi.setVisibility(VISIBLE);
                           //originalDelete.setVisibility(GONE);
                           String filename_only = filename.substring(filename.lastIndexOf("/") + 1);
                           if (!filename.equalsIgnoreCase("")) {
                               Intent intent = getIntent();
                               /** new security code **/
                               SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                               boolean hasRunBefore = prefs.getBoolean("hasRunBefore", false);

                               if(!(hasRunBefore))
                                   text_key = getString(R.string.text_key);
                               else{
                                   //String text_key;
                                   String second_test = null;
                                   try {
                                       second_test = keyfinal();
                                   } catch (Exception e) {
                                       throw new RuntimeException(e);
                                   }
                                   text_key = second_test;
                               }
                               //Toast.makeText(this, "Key final: "+ text_key, Toast.LENGTH_SHORT).show();
                               /** new security code **/
                               res_code = 0;
                               try {
                                   ExecutorService executorService = Executors.newSingleThreadExecutor();

                                   // Handler for posting back to the main thread
                                   Handler mainHandler = new Handler(Looper.getMainLooper());

                                   // Submit a background task
                                   executorService.execute(() -> {
                                       // Simulate background work
                                       try {
                                           //Toast.makeText(cryto.this, "enc: "+filename, Toast.LENGTH_SHORT).show();
                                           if(selectedmultiple) {
                                               for (int i = 0; i < filesSelected.size(); i++) {
                                                   encryptaes(text_key, filesSelected.get(i));
                                               }
                                           }else{
                                                encryptaes(text_key, filename);
                                           }
                                           //encryptaes(finalEncrypted_text, filename);
                                       } catch (Exception e) {
                                           throw new RuntimeException(e);
                                       }
                                       // Notify the main thread when work is done
                                       mainHandler.post(() -> {
                                           //System.out.println("Running post-thread function on the main thread.");
                                           runafterencrypt();
                                       });
                                   });

                                   executorService.shutdown();
                               } catch (Exception e) {
                               }


                           } else {
                               Toast.makeText(cryto.this, getString(R.string.filenotchosen), Toast.LENGTH_LONG).show();
                               lpi.setVisibility(GONE);
                           }


                       } else {
                           Toast.makeText(cryto.this, getString(R.string.pleasechooseanother), Toast.LENGTH_SHORT).show();
                       }
                   }else{
                       Toast.makeText(cryto.this, getString(R.string.filealreadyexists), Toast.LENGTH_SHORT).show();
                   }
                } else {
                    dialog4();
                }



            }
        });

    }

    /*
    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, queryProductDetailsResult) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();
                if (productDetailsList != null && !productDetailsList.isEmpty()) {
                    productDetails = productDetailsList.get(0);
                }
            }
        });
    }
    */

    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("premium_upgrade")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("relax_section")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("filter_types")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, queryProductDetailsResult) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();

                if (productDetailsList != null && !productDetailsList.isEmpty()) {
                    for (ProductDetails details : productDetailsList) {
                        switch (details.getProductId()) {
                            case "premium_upgrade":
                                premiumProductDetails = details;
                                break;
                            case "relax_section":
                                relaxProductDetails = details;
                                break;
                            case "filter_types":
                                filterProductDetails = details;
                                break;
                        }
                    }
                }
            } else {
                Log.e("Billing", "Failed to query product details: " + billingResult.getDebugMessage());
            }
        });
    }

     /*
    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgeParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgeParams, billingResult -> {
                    prefss.edit().putBoolean(KEY_PREMIUM, true).apply();
                    Toast.makeText(this, "✅ Premium Unlocked!", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }


    private void checkIfUserOwnsPremium() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                (billingResult, purchaseList) -> {
                    boolean hasPremium = false;
                    for (Purchase purchase : purchaseList) {
                        if (purchase.getProducts().contains(PRODUCT_ID) &&
                                purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                            hasPremium = true;
                            break;
                        }
                    }
                    prefss.edit().putBoolean(KEY_PREMIUM, hasPremium).apply();
                }
        );
    }
    */

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgeParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                billingClient.acknowledgePurchase(acknowledgeParams, billingResult -> {
                    for (String productId : purchase.getProducts()) {
                        if (productId.equals("premium_upgrade")) {
                            prefss.edit().putBoolean(KEY_PREMIUM, true).apply();
                            Toast.makeText(this, "✅ Premium Upgrade Unlocked!", Toast.LENGTH_SHORT).show();
                        } else if (productId.equals("relax_section")) {
                            prefss2.edit().putBoolean(KEY_PREMIUM2, true).apply();
                            Toast.makeText(this, "🧘 Relax Section Unlocked!", Toast.LENGTH_SHORT).show();
                        }
                        else if (productId.equals("filter_types")) {
                            prefss3.edit().putBoolean(KEY_PREMIUM3, true).apply();
                            Toast.makeText(this, "Filter Unlocked!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void checkIfUserOwnsPremium() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                (billingResult, purchaseList) -> {
                    boolean hasPremium1 = false;
                    boolean hasPremium2 = false;
                    boolean hasPremium3 = false;

                    for (Purchase purchase : purchaseList) {
                        for (String productId : purchase.getProducts()) {
                            if (productId.equals("premium_upgrade") &&
                                    purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                hasPremium1 = true;
                            } else if (productId.equals("relax_section") &&
                                    purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                hasPremium2 = true;
                            }
                            else if (productId.equals("filter_types") &&
                                    purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                hasPremium3 = true;
                            }
                        }
                    }
                    prefss.edit()
                            .putBoolean(KEY_PREMIUM, hasPremium1)
                            .apply();
                    prefss2.edit()
                            .putBoolean(KEY_PREMIUM2, hasPremium2)
                            .apply();
                    prefss3.edit()
                            .putBoolean(KEY_PREMIUM3, hasPremium3)
                            .apply();
                }
        );
    }

    private void createNotificationChannel2(String filename) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.animal10)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Find the last slash in the file path
        int lastSlashIndex = filename.lastIndexOf('/');
        String directoryPath = filename.substring(0, lastSlashIndex + 1);
        Uri mydir = Uri.parse(directoryPath);

        String temp = Environment.getExternalStorageDirectory()+"";

        String uncommonPart = getUncommonPart(filename, temp);

        //File file = new File(filename);
        //Uri mydir = Uri.parse(uncommonPart);
        File file = new File(uncommonPart);

        //Intent intent = new Intent(Intent.ACTION_VIEW);

        //intent.setDataAndType(mydir,"*/*");
        Intent intent = new Intent(cryto.this,History.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = getActivity(cryto.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("mychannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(0, builder.build());
        }
    }

    private void createNotificationChannel(String filename) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(cryto.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.animal10)
                .setColorized(true)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent intent = new Intent(cryto.this, History.class);

        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("mychannel", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
    public static String getUncommonPart(String path1, String path2) {
        // Find the length of the shorter path
        int minLength = Math.min(path1.length(), path2.length());

        // Find the index where they start to differ
        int index = 0;
        while (index < minLength && path1.charAt(index) == path2.charAt(index)) {
            index++;
        }

        // Return the uncommon portion from path1
        return path1.substring(index);
    }

    public static boolean processPathAndCheck(String path, String target) {
        // Create an ArrayList to store the components of the path
        List<String> pathComponents = new ArrayList<>();

        // Split the path by "/" and add the parts to the ArrayList
        String[] parts = path.split("/");

        // Loop through the parts and add to ArrayList (ignoring empty strings)
        for (String part : parts) {
            if (!part.isEmpty()) {
                pathComponents.add(part);
            }
        }

        // Now check if the target exists in the ArrayList
        return pathComponents.contains(target);
    }
    public boolean directoryCheck(File dir){
        if(dir.exists()) {
            return true;
        }
        else
            return false;
    }

    public void dialogExitApp(){
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.exitapp))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    //exit app code
                    finish();
                    System.exit(0);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void PickiTonUriReturned() {

    }


    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int i) {

    }

    @Override
    public void PickiTonCompleteListener(String s, boolean b, boolean b1, boolean b2, String s1) {
        filename = s;
        filesSelected.add(s);  //adding the files to the file select list
        ed = findViewById(R.id.edit_filename);
        ed.setText(s);
        boolean exists = processPathAndCheck(filename,"com.example.coyotefree");
        //originalDelete = findViewById(R.id.checkbox_meat);
        if(!exists) {
            //Toast.makeText(this, "present", Toast.LENGTH_SHORT).show();
            //originalDelete.setVisibility(VISIBLE);
        }
        else {
            //originalDelete.setVisibility(GONE);
        }
    }

    public void runafterencrypt(){
        if (res_code == 1) {
            //make progress bar invisible here
            lpi.setVisibility(GONE);
            //Toast.makeText(cryto.this, getString(R.string.filelocksuccessful), Toast.LENGTH_SHORT).show();
            createNotificationChannel(temp_filename);
            if(!selectedmultiple) {
                File originalFile = new File(filename);
                boolean confirm;
                confirm = originalFile.delete();
                if (confirm) {
                    Log.d("deleted", "file deleted");
                } else {
                    Log.d("not deleted", "file not deleted");
                }
                dbHandler.addNewCourse(cryto.temp_filename, filename);
            }else{
                for(int i=0;i<filesSelected.size();i++){
                    File originalFile = new File(filesSelected.get(i));
                    boolean confirm;
                    confirm = originalFile.delete();
                    if (confirm) {
                        Log.d("deleted", "file deleted");
                    } else {
                        Log.d("not deleted", "file not delted");
                    }
                    dbHandler.addNewCourse(tempNames.get(i), filesSelected.get(i));
                }
            }
            filename = "";
        } else {
        }
    }

    @Override
    public void PickiTonMultipleCompleteListener(ArrayList<String> arrayList, boolean b, String s) {

    }
    public boolean checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        }
        else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
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
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
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
        }
    }

    private boolean checkNoticationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED
                    && NotificationManagerCompat.from(this).areNotificationsEnabled();
        }
        else { // Android 12 and below (API 32 and below)
            return NotificationManagerCompat.from(this).areNotificationsEnabled();
        }
    }

    private void requestNotificationPermission(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        Toast.makeText(this, getString(R.string.enablenotificationpermission), Toast.LENGTH_LONG).show();
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
                                    //Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //Below android 11
                            }
                        }

                    });

    public void dialog4(){
        AlertDialog.Builder builder = new AlertDialog.Builder(cryto.this);
        builder.setMessage(getString(R.string.toenablelockpermission)+"\n\n"+
                        getString(R.string.doyouwantograntthispermission))
                .setTitle(getString(R.string.storagepermissionnotgranted));

        // Add the buttons.
        builder.setPositiveButton(getString(R.string.Sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                requestForStoragePermissions();
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

    public void dialog5(){
        AlertDialog.Builder builder = new AlertDialog.Builder(cryto.this);
        builder.setMessage(getString(R.string.tocreateaseamlessexperience)+"\n\n"+
                        getString(R.string.doyouwantograntthispermission))
                .setTitle(getString(R.string.notificationpermissionnotgranted));

        // Add the buttons.
        builder.setPositiveButton(getString(R.string.Sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                requestNotificationPermission();
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

    @Override
    protected void attachBaseContext(Context newBase) {
        /*SharedPreferences prefs = newBase.getSharedPreferences("langpreference", MODE_PRIVATE);
        String langPref = prefs.getString("lpref", "en");
        Context context = LocaleHelper.wrap(newBase, langPref);
        super.attachBaseContext(context);
        */
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

}


