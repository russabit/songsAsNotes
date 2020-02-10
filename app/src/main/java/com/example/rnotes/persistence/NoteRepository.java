package com.example.rnotes.persistence;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.rnotes.async.DeleteAsyncTask;
import com.example.rnotes.async.InsertAsyncTask;
import com.example.rnotes.async.UpdateAsyncTask;
import com.example.rnotes.models.Note;

import java.util.List;

public class NoteRepository {

    private NoteDatabase rNoteDatabase;

    public NoteRepository(Context context) {
        rNoteDatabase = NoteDatabase.getInstance(context);
    }

    public void insertNoteTask(Note note) {
        new InsertAsyncTask(rNoteDatabase.getNoteDao()).execute(note);
    }

    public void updateNote(Note note) {
        new UpdateAsyncTask(rNoteDatabase.getNoteDao()).execute(note);
    }

    public LiveData<List<Note>> retrieveNotesTask() {
    return rNoteDatabase.getNoteDao().getNotes();
    }

    public void deleteNotes(Note note) {
        new DeleteAsyncTask(rNoteDatabase.getNoteDao()).execute(note);
    }
}
