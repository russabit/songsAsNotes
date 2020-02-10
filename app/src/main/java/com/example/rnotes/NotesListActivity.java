package com.example.rnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rnotes.adapters.NotesRecyclerAdapter;
import com.example.rnotes.models.Note;
import com.example.rnotes.persistence.NoteRepository;
import com.example.rnotes.util.VerticalSpacingItemDecorator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements
        NotesRecyclerAdapter.OnNoteListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";

    // UI components
    private RecyclerView recyclerView;

    // vars
    private ArrayList<Note> rArrayList = new ArrayList<>();
    private NotesRecyclerAdapter rNotesRecyclerAdapter;
    private NoteRepository rNoteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        findViewById(R.id.fab).setOnClickListener(this);

        rNoteRepository = new NoteRepository(this);

        initRecyclerView();
        retrieveNotes();

        //insertFakeNotes();

        setSupportActionBar((Toolbar)findViewById(R.id.notes_toolbar));
        setTitle("notes");
    }

    private void retrieveNotes() {
        rNoteRepository.retrieveNotesTask().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (rArrayList.size() > 0) {
                    rArrayList.clear();
                }
                if (rArrayList != null) {
                    rArrayList.addAll(notes);
                }
                rNotesRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

/*    private void insertFakeNotes() {
        for (int i = 0; i < 1000; i++) {
            Note note = new Note("title # " + i, "content of note # " + i, "Nov 2019");
            rArrayList.add(note);
        }
        rNotesRecyclerAdapter.notifyDataSetChanged();
    }*/

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        rNotesRecyclerAdapter = new NotesRecyclerAdapter(rArrayList, this);
        recyclerView.setAdapter(rNotesRecyclerAdapter);

        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        recyclerView.addItemDecoration(itemDecorator);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public void OnNoteClick(int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("selected_note", rArrayList.get(position));
        startActivity(intent);

        Log.d(TAG, "OnNoteClick: clicked # " + rArrayList.get(position));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    private void deleteNote(Note note) {
        rArrayList.remove(note);
        rNotesRecyclerAdapter.notifyDataSetChanged();
        rNoteRepository.deleteNotes(note);
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteNote(rArrayList.get(viewHolder.getAdapterPosition()));
        }
    };
}
