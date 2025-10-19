package com.example.coyotefree;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.bumptech.glide.Glide;

public class displayItem8 extends AppCompatActivity {
    File originalFile;
    FileOutputStream fos;
    FileInputStream fis;
    File pptFile;
    File filerandom;
    Context context;
    String text_key;
    String newPath;
    private static int pswdIterations = 100;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private String AESSalt = "AES128bit";
    private String initializationVector = "2195081919109305";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_item8);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                //Toast.makeText(context, "backpressed clicked", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                getOnBackPressedDispatcher().onBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(callback);
        text_key = "";
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
        context = displayItem8.this;
        Intent intent = getIntent();
        String stringValue = intent.getExtras().getString("filename");
        try {
            decryptModified(text_key,stringValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
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
    public void decryptModified(String text_key, String filename) throws Exception {

        SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));
        int dotIndex = filename.lastIndexOf('.');
        // Check if there is a dot and it's not the first character
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            // Extract the extension
        }
        fis = new FileInputStream(filename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        byte[] buf = new byte[1024];
        int read;
        while ((read = cis.read(buf)) != -1) {
            byteArrayOutputStream.write(buf, 0, read);
        }
        byte[] decryptedData = byteArrayOutputStream.toByteArray();
        //converting byte stream to a ppt file
        String originalPath = filename;
        filerandom = new File(originalPath);
        String newFileName = filerandom.getName()+"modified_excel"; // New file name without extension
        newPath = changeFileName(originalPath, newFileName); //pathname for newly created file in app space
        History.appSpaceFilepath = newPath;
        pptFile = new File(newPath);
        fos = null;
        boolean check = false;
        try {
            // Create a FileOutputStream to write byteArray to file
            fos = new FileOutputStream(pptFile);
            fos.write(decryptedData);  // Write the byte data to the file
            fos.flush();  // Ensure data is written out
            check =true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(check==true) {
            //use the glide library here
            //Toast.makeText(context, "newPath: "+ newPath, Toast.LENGTH_SHORT).show();
            ImageView imageView = (ImageView) findViewById(R.id.gifView);
            Glide.with(context)
                    .load(newPath)
                    .into(imageView);
            /*
            //third logic all document viewer
            Intent intent = new Intent(context, All_Document_Reader_Activity.class);
            intent.putExtra("path", newPath);
            //Toast.makeText(this, "new path: " + newPath, Toast.LENGTH_SHORT).show();
            intent.putExtra("fromAppActivity", true);
            context.startActivity(intent);
            finish();*/
        }
    }
    public static String changeFileName(String originalPath, String newFileName) {
        // Create a File object from the original path
        File originalFile = new File(originalPath);
        // Get the file extension
        String extension = "";
        String fileName = originalFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex); // Get the extension, e.g. ".txt"
        }
        // Create the new file path with the new file name and the same extension
        String newFilePath = originalPath.substring(0, originalPath.lastIndexOf(File.separator) + 1) + newFileName + extension;
        return newFilePath;
    }
    //for pdf creation and extension
    // Hypothetical method that renders the slide content to a Bitmap

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
}