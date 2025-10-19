package com.example.coyotefree;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
//import com.github.barteksc.pdfviewer.PDFView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
//import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.PDFView;




public class displayItem3 extends AppCompatActivity {
    String text_key;
    private PDFView pdfView;
    private static int pswdIterations = 100;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private String AESSalt = "AES128bit";
    private String initializationVector = "2195081919109305";
    private static String mark = File.separator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_item3);
        //text_key=getString(R.string.text_key);
        /** new security code **/
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
        FileInputStream fis = new FileInputStream(filename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        byte[] buf = new byte[1024];
        int read;
        while ((read = cis.read(buf)) != -1) {
            byteArrayOutputStream.write(buf, 0, read);
        }
        byte[] decryptedData = byteArrayOutputStream.toByteArray();
        pdfView = findViewById(R.id.pdfView);
        pdfView.fromBytes(decryptedData)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .load();
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
}