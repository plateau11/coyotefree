package com.example.coyotefree;
import static android.view.View.GONE;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
public class CourseGVAdapter extends ArrayAdapter<CourseModel> {
    public static ArrayList<Boolean> toggleList;
    private static int pswdIterations = 100;
    public static boolean selectall = false;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private String AESSalt = "AES128bit";
    private String initializationVector = "2195081919109305";
    private static String mark = File.separator;
    private static int activate_val=0;
    public CourseGVAdapter(@NonNull Context context, ArrayList<CourseModel> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = null;
        //manipulate card layout
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

      // if(History.gridvalue==3){
           // Set width and height programmatically
           CardView cv = listitemView.findViewById(R.id.card);
           ViewGroup.LayoutParams layoutParams = cv.getLayoutParams();
           layoutParams.width = History.cardwidth; // Width in pixels
           layoutParams.height = History.cardheight; // Height in pixels
           cv.setLayoutParams(layoutParams);

           ImageView iv =listitemView.findViewById(R.id.idIVcourse);
           ViewGroup.LayoutParams layoutParams2 = iv.getLayoutParams();
           layoutParams2.height = History.imgHeight;
           iv.setLayoutParams(layoutParams2);

       //}
       /*
        if(History.gridvalue==2){
            // Set width and height programmatically
            CardView cv = listitemView.findViewById(R.id.card);
            ViewGroup.LayoutParams layoutParams = cv.getLayoutParams();
            layoutParams.width = History.cardwidth; // Width in pixels
            layoutParams.height = History.cardheight; // Height in pixels
            cv.setLayoutParams(layoutParams);

            ImageView iv =listitemView.findViewById(R.id.idIVcourse);
            ViewGroup.LayoutParams layoutParams2 = iv.getLayoutParams();
            layoutParams2.height = History.imgHeight;
            iv.setLayoutParams(layoutParams2);
        }*/

        CourseModel courseModel = getItem(position);
        TextView courseTV = listitemView.findViewById(R.id.idTVCourse);
        ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);
        CheckBox checkBox = listitemView.findViewById(R.id.check);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tempPosition = position;
                if(checkBox.isChecked()){ //this block is for adding items
                    History.checkitems_hash_map.put(tempPosition,tempPosition);
                }
                if(checkBox.isChecked()==false){ //technically this block is for removing items
                    selectall = false;
                    History.selectall = false;
                    try {
                        History.checkitems_hash_map.remove(tempPosition);
                    }catch (Exception exception)
                    {
                        Toast.makeText(CourseGVAdapter.this.getContext(), "error: "+exception, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        if(!selectall) {
            if (activate_val == 1)
                turnCheckOn(checkBox);
            else
                turnCheckOff(checkBox);
        }else //when select all is pressed
        {
            turnCheckOn(checkBox);
            checkSelected(checkBox); //automatic tick
        }

        String text_key = "";
        /** new security code **/
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean hasRunBefore = prefs.getBoolean("hasRunBefore", false);
        if(!(hasRunBefore))
            text_key = getContext().getString(R.string.text_key);
        else{

            //String text_key;
            String second_test = null;
            try {
                second_test = keyfinal(this.getContext());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            text_key = second_test;
        }
        //Toast.makeText(this.getContext(), "Key final adapter: "+ text_key, Toast.LENGTH_SHORT).show();
        /** new security code **/
        String name = courseModel.getFile_name();
        String extension = "";
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < name.length() - 1)
            extension = name.substring(dotIndex + 1);
        if(extension.equalsIgnoreCase("mkv")||extension.equalsIgnoreCase("mp4")
                ||extension.equalsIgnoreCase("mpeg")){
            courseIV.setImageResource(R.mipmap.video4_foreground);
        }
        else if(extension.equalsIgnoreCase("gif")){
            courseIV.setImageResource(R.mipmap.gif1_foreground);
        }
        else if(extension.equalsIgnoreCase("opus")
                ||extension.equalsIgnoreCase("mp3")
                ||extension.equalsIgnoreCase("wav")||extension.equalsIgnoreCase("aac")){
            //statements
            courseIV.setImageResource(R.mipmap.music5_foreground);
        }
        else if(extension.equalsIgnoreCase("pdf")
             ){
            courseIV.setImageResource(R.mipmap.pdf1_foreground);
            //courseIV.setImageResource(R.drawable.pdf2);
        }
        else if(extension.equalsIgnoreCase("docx")
                ||extension.equalsIgnoreCase("doc")
        ){
            courseIV.setImageResource(R.mipmap.doc1_foreground);
            //courseIV.setImageResource(R.mipmap.mdocs);
        }
        else if(extension.equalsIgnoreCase("pptx")
                ||extension.equalsIgnoreCase("ppt")
        ){
            courseIV.setImageResource(R.mipmap.ppt24_foreground);
        }
        else if(extension.equalsIgnoreCase("xls")||
                extension.equalsIgnoreCase("xlsx")||
                extension.equalsIgnoreCase("csv")
        ){
            courseIV.setImageResource(R.mipmap.excel8_foreground);
            //courseIV.setImageResource(R.drawable.pdf2);
        }
        else {
            courseIV.setImageResource(R.mipmap.fileunknown2_foreground);
        }
        if(extension.equalsIgnoreCase("jpeg")||extension.equalsIgnoreCase("jpg")
                ||extension.equalsIgnoreCase("png")
                ||extension.equalsIgnoreCase("tiff")||
        extension.equalsIgnoreCase(("webp"))){
            Bitmap myBitmap = null;
            try {
                myBitmap = decryptModified(text_key,courseModel.getFile_name());
            } catch (Exception e) {
            }
            courseTV.setVisibility(GONE);
            courseIV.setImageBitmap(myBitmap);
            courseIV.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
            LinearLayout layout = listitemView.findViewById(R.id.llayout);

            layout.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
        }
        courseTV.setText(courseModel.getCourse_name());
        return listitemView;
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
    public Bitmap decryptModified(String text_key, String filename) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));
        String filename_only = filename.substring(filename.lastIndexOf(mark) + 1);
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
            //media player code
        }
        byte[] decryptedData = byteArrayOutputStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(decryptedData, 0, decryptedData.length);
        return bitmap;
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

    public String keyfinal(Context context) throws Exception {
        final String PREFS_NAME = "my_prefs";
        final String KEY_ENCRYPTED_STRING = "encrypted_string";

        // Step 1: Create KeyUpdate instance
        KeyUpdate keyUpdate = new KeyUpdate();
        // Step 2: Get or create key with alias "MyAppKey"
        SecretKey secretKey = keyUpdate.getOrCreateKey("MyAppKey");

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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


    /**
     * controls activation value
     * sets to 1
     */
    public void activate(){
        activate_val = 1;
    }
    /***
     * controls activation value
     * sets to 0
     */
    public void deactivate(){
        activate_val = 0;
    }
    /**
     * makes checkbox visible
     * @param check
     */
    public void turnCheckOn(CheckBox check){
        check.setVisibility(View.VISIBLE);
    }
    /**
     * makes checkbox invisible
     * @param check
     */
    public void turnCheckOff(CheckBox check){
        check.setVisibility(View.GONE);
    }
    public void setSize(int size){
    }
    /**
     * selects the empty checkbox
     * @param check
     */
    public void checkSelected(CheckBox check){
        check.setChecked(true);
    }

}
