package com.example.notes_6a;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NotesAdapter extends FirebaseRecyclerAdapter<Note, NotesAdapter.ViewHolder> {

    private final String KEY_PARENT = "Notes";
    private final String KEY_TITLE = "title";
    private final String KEY_DESC = "description";
    Context context;
    public NotesAdapter(Context context, @NonNull FirebaseRecyclerOptions<Note> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Note note) {

        String key = getRef(i).getKey();

        viewHolder.tvTitle.setText(note.getTitle());
        viewHolder.tvDesc.setText(note.getDescription());
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v1) {
                AlertDialog.Builder updateNote = new AlertDialog.Builder(context);
                updateNote.setTitle("UPDATE NOTE");
                View v = LayoutInflater.from(context)
                        .inflate(R.layout.add_new_note_form, null, false);
                updateNote.setView(v);
                EditText etTitle = v.findViewById(R.id.etTitle);
                EditText etDesc = v.findViewById(R.id.etDesc);
                etTitle.setText(note.getTitle());
                etDesc.setText(note.getDescription());

                updateNote.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<Object, Object> data = new HashMap<>();
                        data.put(KEY_TITLE, etTitle.getText().toString().trim());
                        data.put(KEY_DESC, etDesc.getText().toString().trim());
                        assert key != null;
                        FirebaseDatabase.getInstance().getReference(KEY_PARENT)
                                .child(key)
                                .setValue(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Note updated", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
                updateNote.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assert key != null;
                        FirebaseDatabase.getInstance().getReference(KEY_PARENT)
                                .child(key)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                updateNote.show();

                return false;
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_note_item_design, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle, tvDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
