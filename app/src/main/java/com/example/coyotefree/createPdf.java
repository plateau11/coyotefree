package com.example.coyotefree;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class createPdf {

    public Uri pdfCreator(Context context, String fileName, String header, String contents) {
        Uri pdfUri = null;
        OutputStream os = null;

        try {
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();

            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            int x = 40, y = 60;

            // Header
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            paint.setTextSize(20);
            canvas.drawText(header, x, y, paint);
            y += 40;

            // Content
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(16);

            for (String line : contents.split("\n")) {
                canvas.drawText(line, x, y, paint);
                y += 25;
            }

            pdfDocument.finishPage(page);

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".pdf");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // ANDROID 10+ : MediaStore public downloads
                values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS + "/CoyotePDFs/");
                pdfUri = context.getContentResolver()
                        .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (pdfUri != null) {
                    os = context.getContentResolver().openOutputStream(pdfUri);
                }
            } else {
                // ANDROID 8 & 9 : Direct storage access
                String folderPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                ) + "/CoyotePDFs/";

                File folder = new File(folderPath);
                if (!folder.exists()) folder.mkdirs();

                File file = new File(folder, fileName + ".pdf");
                os = new FileOutputStream(file);
                pdfUri = Uri.fromFile(file);
            }

            if (os != null) {
                pdfDocument.writeTo(os);
                os.close();
            }

            pdfDocument.close();

        } catch (Exception e) {
            e.printStackTrace();
            pdfUri = null;
        }

        return pdfUri;
    }

}
