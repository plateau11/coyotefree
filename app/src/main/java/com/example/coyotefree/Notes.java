package com.example.coyotefree;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class Notes extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {
    private ListView noteListView;
    private Button newNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        noteListView = findViewById(R.id.noteListView);
        newNoteButton = findViewById(R.id.newNoteButton);

        // Initialize DatabaseHelper and fetch notes from SQLite database
        final DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final List<Note> notes = fetchNotesFromDatabase(databaseHelper);

        // Set up NoteAdapter for the ListView with this as the OnNoteClickListener
        final NoteAdapter adapter = new NoteAdapter(this, notes, this);
        noteListView.setAdapter(adapter);

        // Set click listener for creating a new note
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Notes.this, NoteDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set long click listener for deleting a note
        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                showDeleteDialog(adapter, position);
                return true;
            }
        });
    }

    private List<Note> fetchNotesFromDatabase(DatabaseHelper databaseHelper) {
        return databaseHelper.getAllNotes();
    }

    private void showDeleteDialog(final NoteAdapter adapter, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.areyousure)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Note noteToDelete = adapter.getItem(position);
                        deleteNoteFromDatabase(noteToDelete.getId());
                        adapter.remove(noteToDelete);
                        adapter.notifyDataSetChanged();
                        // Inform the user that the note has been deleted
                        showToast(getString(R.string.NoteDeleted));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User canceled the operation
                    }
                })
                .show();
    }

    private void deleteNoteFromDatabase(long noteId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.deleteNoteById(noteId);
    }

    private void showToast(String message) {
        // Display a toast message (implement this method based on your preferences)
    }

    @Override
    public void onNoteClick(Note note) {
        // Handle note click (open NoteDetailActivity for editing)
        Intent intent = new Intent(Notes.this, NoteDetailActivity.class);
        intent.putExtra("noteId", note.getId());
        startActivity(intent);
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
