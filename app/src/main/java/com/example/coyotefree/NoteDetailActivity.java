package com.example.coyotefree;

// NoteDetailActivity.java
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;

public class NoteDetailActivity extends AppCompatActivity {
    private EditText noteHeadingEditText;
    private EditText noteDetailsEditText;
    private Button saveNoteButton;
    private Button createPdf;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        noteHeadingEditText = findViewById(R.id.noteHeadingEditText);
        noteDetailsEditText = findViewById(R.id.noteDetailsEditText);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        createPdf = findViewById(R.id.createPdf);

        // Get noteId from the intent (if available)
        noteId = getIntent().getLongExtra("noteId", -1);

        if (noteId != -1) {
            // Load existing note details for editing
            loadNoteDetails(noteId);
        }

        // Set click listener for saving a note
        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String path = createPdfInAppDocuments();
                createPdf obj = new createPdf();

                obj.pdfCreator(path,heading,details);*/
                /*
                String path = getExternalFilesDir(null) + "/PDFs/mynote.pdf";

                boolean success = new createPdf().pdfCreator(path, "My Header", "My note contents");

                if (success) {
                    Toast.makeText(NoteDetailActivity.this, "PDF saved successfully! in: " + path, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NoteDetailActivity.this, "Failed to save PDF!", Toast.LENGTH_SHORT).show();
                }
                */

                String heading = noteHeadingEditText.getText().toString();
                String details = noteDetailsEditText.getText().toString();
                Uri pdfUri = new createPdf().pdfCreator(
                        NoteDetailActivity.this,
                        "Note_" + System.currentTimeMillis(),
                        heading,
                        details
                );

                if (pdfUri != null) {
                    Toast.makeText(NoteDetailActivity.this, "Saved in Downloads/CoyotePDFs!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NoteDetailActivity.this, "Failed to save PDF!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void loadNoteDetails(long noteId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Note note = databaseHelper.getNoteById(noteId);

        if (note != null) {
            noteHeadingEditText.setText(note.getHeading());
            noteDetailsEditText.setText(note.getDetails());
        }
    }

    public String createPdfInAppDocuments() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "myFile.pdf");
        String filePath = file.getAbsolutePath();

        //createPdfWithLibrary(filePath);

        return filePath;
    }


    private void saveNote() {
        String heading = noteHeadingEditText.getText().toString();
        String details = noteDetailsEditText.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(heading)) {
            Toast.makeText(this, getString(R.string.heading), Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if (noteId == -1) {
            // New note
            long newNoteId = databaseHelper.insertNote(heading, details);
            if (newNoteId != -1) {
                Toast.makeText(this, getString(R.string.notesaved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.errorsavingnote), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Existing note
            boolean updated = databaseHelper.updateNote(noteId, heading, details);
            if (updated) {
                Toast.makeText(this, getString(R.string.noteupdated), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.errorupdating), Toast.LENGTH_SHORT).show();
            }
        }

        // Navigate back to the main page
        Intent intent = new Intent(this, Notes.class);
        startActivity(intent);
        finish();
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