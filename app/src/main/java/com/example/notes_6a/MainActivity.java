package com.example.notes_6a;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvNotes;
    FloatingActionButton fabAddNote;
    DatabaseReference reference;

    NotesAdapter notesAdapter;
    private final String KEY_PARENT = "Notes";
    private final String KEY_TITLE = "title";
    private final String KEY_DESC = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewNote();
            }
        });
    }

    private void addNewNote()
    {
        AlertDialog.Builder addNewNote = new AlertDialog.Builder(this);
        addNewNote.setTitle("NEW NOTE");
        View v = LayoutInflater.from(this)
                .inflate(R.layout.add_new_note_form, null, false);
        addNewNote.setView(v);
        EditText etTitle = v.findViewById(R.id.etTitle);
        EditText etDesc = v.findViewById(R.id.etDesc);

        addNewNote.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<Object, Object> data = new HashMap<>();
                data.put(KEY_TITLE, etTitle.getText().toString().trim());
                data.put(KEY_DESC, etDesc.getText().toString().trim());
                reference.child(KEY_PARENT).push()
                        .setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        addNewNote.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Thanks", Toast.LENGTH_SHORT).show();
            }
        });

        addNewNote.show();
    }

    private void init()
    {
        reference = FirebaseDatabase.getInstance().getReference();
        fabAddNote = findViewById(R.id.fabAddNote);
        rvNotes = findViewById(R.id.rvNotes);
        rvNotes.setHasFixedSize(true);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));

        Query query = reference
                .child(KEY_PARENT);

        FirebaseRecyclerOptions<Note> options =
                new FirebaseRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();

        notesAdapter = new NotesAdapter(this, options);
        rvNotes.setAdapter(notesAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notesAdapter.stopListening();
    }
}
