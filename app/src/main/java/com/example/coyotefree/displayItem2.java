package com.example.coyotefree;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class displayItem2 extends AppCompatActivity implements ComponentCallbacks2{
    String text_key;
    private static int pswdIterations = 100;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private String AESSalt = "AES128bit";
    private String initializationVector = "2195081919109305";
    private static String mark = File.separator;
    private ExoPlayer exoPlayer;
    FileInputStream fis;
    byte[] buf;
    ByteArrayOutputStream byteArrayOutputStream;
    CipherInputStream cis;
    ByteArrayDataSource dataSource;
    ProgressiveMediaSource mediaSource;
    ExecutorService executor1;
    ExecutorService executor2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_item2);
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
        Toast.makeText(this, "Key final: "+ text_key, Toast.LENGTH_SHORT).show();
        /** new security code **/

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                //releases memory after using large files
                System.gc();
                buf=null;
                exoPlayer.release();
                executor1.shutdown();
                executor2.shutdown();
                exoPlayer.stop();
                try {
                    byteArrayOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                byteArrayOutputStream.reset();
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    cis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dataSource.close();
                finish();
                //Toast.makeText(displayItem2.this, "Backpressed clicked.", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(callback);
        Intent intent = getIntent();
        String stringValue = intent.getExtras().getString("filename");
        //Using thread to avoid Android Not Responding Dialog
        executor1 = Executors.newSingleThreadExecutor();
        executor1.execute(()->{
            decryptModified(text_key, stringValue);
            runOnUiThread(()->{
            });
        });
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
    public void decryptModified(String text_key, String filename){
        SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(cypherInstance);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        int dotIndex = filename.lastIndexOf('.');
        // Check if there is a dot and it's not the first character
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            // Extract the extension
        }
        executor2 = Executors.newSingleThreadExecutor();
        Cipher finalCipher = cipher;
        executor2.execute(()->{
            try {
                fis = new FileInputStream(filename);
                byteArrayOutputStream = new ByteArrayOutputStream();
                cis = new CipherInputStream(fis, finalCipher);
                // Wrap CipherInputStream in CipherDataSource
                buf = new byte[8192];
                ArrayList<byte[]> packets = new ArrayList<>();
                int read;
                while ((read = cis.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, read);
                }
                byte[] decryptedData = byteArrayOutputStream.toByteArray();
                dataSource = new ByteArrayDataSource(decryptedData);
            runOnUiThread(()->{
                StyledPlayerView styledPlayerView = findViewById(R.id.styled_player_view);
                DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
                exoPlayer = new ExoPlayer.Builder(displayItem2.this).setTrackSelector(trackSelector).build();
                styledPlayerView.setPlayer(exoPlayer);
                mediaSource = new ProgressiveMediaSource.Factory(() -> dataSource)
                        .createMediaSource(MediaItem.fromUri(Uri.EMPTY));
                exoPlayer.setMediaSource(mediaSource);
                exoPlayer.prepare();
                exoPlayer.play();
            });
            }catch (Exception e){
            }
        });
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
