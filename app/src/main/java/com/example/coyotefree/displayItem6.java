package com.example.coyotefree;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import com.ahmadullahpk.alldocumentreader.activity.All_Document_Reader_Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.github.barteksc.pdfviewer.PDFView;


import org.apache.poi.xslf.usermodel.XMLSlideShow;

import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.xslf.usermodel.XSLFSlide;


import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import android.graphics.Color;
import android.graphics.Paint;

public class displayItem6 extends AppCompatActivity {
    Context context;
    String text_key;
    String newPath;
    private TextView docTextView;
    private PDFView pdfView;
    private static int pswdIterations = 100;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private WebView webView;
    private String AESSalt = "AES128bit";
    private String initializationVector = "2195081919109305";
    private static String mark = File.separator;
    private static String encrypt_dir=null;
    private static String temp_filename = "";
    private static String d_filename = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_item4);
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
        context = displayItem6.this;
        Intent intent = getIntent();
        //int intValue = intent.getIntExtra("position", 0);
        String stringValue = intent.getExtras().getString("filename");
        //Toast.makeText(this, "Position: "+ stringValue, Toast.LENGTH_SHORT).show();
        //ImageView myview = findViewById(R.id.displayimage);
        //CourseGVAdapter obj = new CourseGVAdapter();
        //Bitmap mymap;
        try {
            //mymap = decryptModified(text_key,stringValue);
            decryptModified(text_key,stringValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //myview.setImageBitmap(mymap);
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
        String filename_only = filename.substring(filename.lastIndexOf(mark) + 1);
        //String newfilename_only = filename_only.replace("FileCrypt_", "");
        //File encrytpted_files_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        int dotIndex = filename.lastIndexOf('.');
        String extension = "";
        // Check if there is a dot and it's not the first character
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            // Extract the extension
            extension = filename.substring(dotIndex + 1);

        }

        //FileOutputStream fos = new FileOutputStream(filename+"."+extension);

        d_filename = filename+"."+extension;

        //Toast.makeText(this, "decrypted file location: "+d_filename, Toast.LENGTH_SHORT).show();

        FileInputStream fis = new FileInputStream(filename);
        //CipherOutputStream cos = new CipherOutputStream(fos, cipher);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        CipherInputStream cis = new CipherInputStream(fis, cipher);

        byte[] buf = new byte[1024];
        int read;
        while ((read = cis.read(buf)) != -1) {
            byteArrayOutputStream.write(buf, 0, read);
        }
        byte[] decryptedData = byteArrayOutputStream.toByteArray();

        //converting byte stream to a ppt file

        String newPptFilename = "modified"+filename;
        String originalPath = filename;
        String newFileName = "modified_excel"; // New file name without extension
        newPath = changeFileName(originalPath, newFileName); //pathname for newly created file in app space
        History.appSpaceFilepath = newPath;
        //Toast.makeText(this, newPath, Toast.LENGTH_SHORT).show();

        File pptFile = new File(newPath);

        FileOutputStream fos = null;

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

        if(check==true){
            //Toast.makeText(this, "Ppt file created successfully", Toast.LENGTH_SHORT).show();

            /*first logic using intent ppt viewer

            File pptFile2 = new File(newPath);

            //using intent to open ppt file using available third party apps

            //Uri pptUri = Uri.fromFile(pptFile2);

            Uri pptUri = FileProvider.getUriForFile(displayItem5.this, displayItem5.this.getPackageName() + ".provider", pptFile2);

            // Create an Intent to view the file
            Intent intent = new Intent(Intent.ACTION_VIEW);

            // Set the MIME type for PowerPoint files
            intent.setDataAndType(pptUri, "application/vnd.openxmlformats-officedocument.presentationml.presentation");

            //application/vnd.ms-powerpoint --for ppt extension

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the activity to open the PowerPoint file
            startActivity(Intent.createChooser(intent, "Open PowerPoint File"));

             */

            /*second logic converting to pdf

            String new_pdf = changeFileNameWithExtension(filename,"modified","pdf");

            Toast.makeText(this, "just check", Toast.LENGTH_SHORT).show();

            File pdf_file = new File(new_pdf);

            File pptFile2 = new File(newPath);

            Toast.makeText(this, "just check 2", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, new_pdf, Toast.LENGTH_SHORT).show();

            try {
                convertPPTToPDF(pptFile2, pdf_file);
                Toast.makeText(this, "just check 4", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error converting PPT to PDF", Toast.LENGTH_SHORT).show();
                //Log.e(TAG, "Error converting PPT to PDF", e);
            }

            //utilising the newly created pdf file
            Toast.makeText(this, "another check", Toast.LENGTH_SHORT).show();
            byte[] pdfBytes = convertPDFToByteStream(new_pdf);

            pdfView = findViewById(R.id.pdfView);

            pdfView.fromBytes(pdfBytes)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load();

            Toast.makeText(this, "random check", Toast.LENGTH_SHORT).show();

             */

            //third logic all document viewer
            Intent intent = new Intent(context, All_Document_Reader_Activity.class);
            intent.putExtra("path", newPath);
            //Toast.makeText(this, "new path: "+ newPath, Toast.LENGTH_SHORT).show();
            intent.putExtra("fromAppActivity", true);

            context.startActivity(intent);

            finish();

        }//else
        //Toast.makeText(this, "Ppt not created.", Toast.LENGTH_SHORT).show();


        //webView = findViewById(R.id.webView);

        //String htmlContent = convertToHtml(decryptedData); // Implement a conversion function if needed

        // Load HTML content into WebView
        //webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

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
    public static String changeFileNameWithExtension(String originalPath, String newFileName, String extension) {
        // Create a File object from the original path
        File originalFile = new File(originalPath);

        // Get the file name without the extension
        String fileName = originalFile.getName();
        int dotIndex = fileName.lastIndexOf('.');

        // If there's an existing extension, remove it
        if (dotIndex > 0) {
            fileName = fileName.substring(0, dotIndex); // Remove the current extension
        }

        // Create the new file path with the new file name and the new extension
        String newFilePath = originalPath.substring(0, originalPath.lastIndexOf(File.separator) + 1)
                + newFileName + "." + extension; // Concatenate the new file name and extension

        return newFilePath;
    }

    public static byte[] convertPDFToByteStream(String pdfFilePath) {
        File file = new File(pdfFilePath);
        byte[] byteArray = null;

        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byteArray = baos.toByteArray(); // Convert ByteArrayOutputStream to byte array
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    private String convertToHtml(byte[] documentData) {
        // Implement conversion logic for DOC, PPT, or PDF to HTML
        return "<html><body><h1>Document Content</h1><p>...</p></body></html>";
    }

    private void convertPPTToPDF(File pptFile, File pdfFile) throws IOException {
        FileInputStream pptStream = new FileInputStream(pptFile);
        XMLSlideShow ppt = new XMLSlideShow(pptStream);

        // Create a PdfWriter instance
        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Create a Document instance
        Document document = new Document(pdfDoc);

        // Get slides from the PPTX file
        List<XSLFSlide> slides = ppt.getSlides();  //check required
        for (Slide slide : slides) {
            // Here, you could capture the slide as an image, then add it to the PDF.
            // For simplicity, we're assuming you have a method to render the slide as an image.
            Image slideImage = getSlideImage(slide);
            if (slideImage != null) {
                document.add(slideImage);
            }
            document.add(new com.itextpdf.layout.element.Paragraph("\n")); // New line after each image
        }

        document.close();
        pptStream.close();

        //Log.d(TAG, "PPT successfully converted to PDF: " + pdfFile.getAbsolutePath());
    }

    private Image getSlideImage(Slide slide) {
        try {
            // Render the slide to a Bitmap (Android equivalent of BufferedImage)
            Bitmap slideBitmap = renderSlideToImage(slide);

            // Convert the Bitmap to a PNG byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            slideBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // Compressing to PNG format
            baos.flush();

            // Create ImageData from the byte array
            com.itextpdf.io.image.ImageData imageData = ImageDataFactory.create(baos.toByteArray());

            // Return the iText Image object
            return new Image(imageData);
        } catch (IOException e) {
            //Log.e("getSlideImage", "Error rendering slide to image", e);
            return null;
        }
    }

    // Hypothetical method that renders the slide content to a Bitmap
    private Bitmap renderSlideToImage(Slide slide) {
        // Define size for the Bitmap
        int width = 800;  // Example width
        int height = 600; // Example height

        // Create a Bitmap and Canvas to draw on it
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Set up paint for drawing
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);  // Example font size

        // Render the slide content (this part depends on what the Slide class contains)
        // For now, just drawing some example text on the canvas
        canvas.drawText("Slide Content", 100, 100, paint);

        // Return the Bitmap with the slide content drawn on it
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