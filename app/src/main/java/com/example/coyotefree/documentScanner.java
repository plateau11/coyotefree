package com.example.coyotefree;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;

import com.bumptech.glide.Glide;
import com.example.coyotefree.R;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/** Demonstrates the document scanner powered by Google Play services. */
public class documentScanner extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String FULL_MODE = "FULL";
    private static final String BASE_MODE = "BASE";
    private static final String BASE_MODE_WITH_FILTER = "BASE_WITH_FILTER";
    private String selectedMode = FULL_MODE;

    private TextView resultInfo;
    private ImageView firstPageView;
    private EditText pageLimitInputView;
    private ActivityResultLauncher<IntentSenderRequest> scannerLauncher;
    private boolean enableGalleryImport = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_scanner);

        resultInfo = findViewById(R.id.result_info);
        firstPageView = findViewById(R.id.first_page_view);
        pageLimitInputView = findViewById(R.id.page_limit_input);

        scannerLauncher =
                registerForActivityResult(new StartIntentSenderForResult(), this::handleActivityResult);
        populateModeSelector();
    }

    public void onEnableGalleryImportCheckboxClicked(View view) {
        enableGalleryImport = ((CheckBox) view).isChecked();
    }

    public void onScanButtonClicked(View view) {
        clearAppCache();
        resultInfo.setText(null);
        Glide.with(this).clear(firstPageView);

        GmsDocumentScannerOptions.Builder options =
                new GmsDocumentScannerOptions.Builder()
                        .setResultFormats(
                                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                                GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
                        .setGalleryImportAllowed(enableGalleryImport);

        switch (selectedMode) {
            case FULL_MODE:
                options.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL);
                break;
            case BASE_MODE:
                options.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE);
                break;
            case BASE_MODE_WITH_FILTER:
                options.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER);
                break;
            default:
                Log.e(TAG, "Unknown selectedMode: " + selectedMode);
        }

        String pageLimitInputText = pageLimitInputView.getText().toString();
        if (!pageLimitInputText.isEmpty()) {
            try {
                int pageLimit = Integer.parseInt(pageLimitInputText);
                options.setPageLimit(pageLimit);
            } catch (RuntimeException e) {
                resultInfo.setText(e.getMessage());
                return;
            }
        }

        GmsDocumentScanning.getClient(options.build())
                .getStartScanIntent(this)
                .addOnSuccessListener(
                        intentSender ->
                                scannerLauncher.launch(new IntentSenderRequest.Builder(intentSender).build()))
                .addOnFailureListener(
                        e -> resultInfo.setText(getString(R.string.error_default_message, e.getMessage())));
    }

    private void populateModeSelector() {
        Spinner featureSpinner = findViewById(R.id.mode_selector);
        List<String> options = new ArrayList<>();
        options.add(FULL_MODE);
        options.add(BASE_MODE);
        options.add(BASE_MODE_WITH_FILTER);

        // Creating adapter for featureSpinner
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        featureSpinner.setAdapter(dataAdapter);
        featureSpinner.setOnItemSelectedListener(
                new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                        selectedMode = parentView.getItemAtPosition(pos).toString();
                        // Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {}
                });
    }

    private void handleActivityResult(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        GmsDocumentScanningResult result =
                GmsDocumentScanningResult.fromActivityResultIntent(activityResult.getData());
        if (resultCode == Activity.RESULT_OK && result != null) {
            resultInfo.setText(getString(R.string.scan_result, result));

            List<GmsDocumentScanningResult.Page> pages = result.getPages();

            if (!result.getPages().isEmpty()) {
                Glide.with(this).load(result.getPages().get(0).getImageUri()).into(firstPageView);
                //Toast.makeText(this, "Test2", Toast.LENGTH_SHORT).show();
                Uri imageUri = result.getPages().get(0).getImageUri();
                if (pages.size() > 1) {
                    //Toast.makeText(this, "Page count: " + pages.size(), Toast.LENGTH_SHORT).show();

                    // Toas Toas Toast.makeText(this, "option1", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this)
                            .setTitle("Multiple Pages Detected")
                            .setMessage("Do you want to save the scanned pages as a PDF?")
                            .setPositiveButton("Yes", (dialog, which) -> {

                                //Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
                                if (result.getPdf() != null) {
                                    savePdfToGallery(result.getPdf().getUri());
                                } else {
                                    Toast.makeText(this, "PDF not available", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                //Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
                                for (GmsDocumentScanningResult.Page page : pages) {
                                    saveImageToGallery(page.getImageUri());
                                }
                            })
                            .show();
                } else {
                    //Toast.makeText(this, "option2", Toast.LENGTH_SHORT).show();
                    saveImageToGallery(pages.get(0).getImageUri());
                }
            }

            /*if (result.getPdf() != null) {
                Toast.makeText(this, "Test3", Toast.LENGTH_SHORT).show();
                File file = new File(result.getPdf().getUri().getPath());
                String name = file.getAbsolutePath();
                Toast.makeText(this, "file path: "+ name, Toast.LENGTH_SHORT).show();
                Uri externalUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setDataAndType(externalUri, "application/pdf");
                viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(viewIntent, "view pdf"));


            }*/
        } else if (resultCode == Activity.RESULT_CANCELED) {
            resultInfo.setText(getString(R.string.error_scanner_cancelled));
        } else {
            resultInfo.setText(getString(R.string.error_default_message));
        }
    }

    private void saveImageToGallery(Uri sourceUri) {
        try {
            String fileName = "scanned_image_" + System.currentTimeMillis() + ".jpg";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ScannedDocs"); // folder in Gallery

            Uri imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri newImageUri = getContentResolver().insert(imageCollection, values);

            if (newImageUri != null) {
                try (
                        InputStream in = getContentResolver().openInputStream(sourceUri);
                        OutputStream out = getContentResolver().openOutputStream(newImageUri)
                ) {
                    if (in != null && out != null) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                        out.flush();
                        Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void savePdfToGallery(Uri sourceUri) {
        try {
            String fileName = "scanned_document_" + System.currentTimeMillis() + ".pdf";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/ScannedDocs");

            Uri downloadsUri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                downloadsUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            Uri newPdfUri = getContentResolver().insert(downloadsUri, values);

            if (newPdfUri != null) {
                try (
                        InputStream in = getContentResolver().openInputStream(sourceUri);
                        OutputStream out = getContentResolver().openOutputStream(newPdfUri)
                ) {
                    if (in != null && out != null) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                        out.flush();
                        Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    // Recursively delete file or directory
    private void deleteFileOrDir(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            File[] children = fileOrDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteFileOrDir(child);
                }
            }
        }
        fileOrDir.delete();
    }

    // Clear internal and external cache directories
    private void clearAppCache() {
        try {
            // Clear internal cache
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        deleteFileOrDir(file);
                    }
                }
            }

            // Optionally, also clear external cache
            File externalCacheDir = getExternalCacheDir();
            if (externalCacheDir != null && externalCacheDir.isDirectory()) {
                File[] files = externalCacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        deleteFileOrDir(file);
                    }
                }
            }
            Log.d("CacheClear", "Cache cleared.");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to clear cache: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




}