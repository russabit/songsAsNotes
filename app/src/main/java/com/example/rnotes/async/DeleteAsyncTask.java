package com.example.rnotes.async;

import android.os.AsyncTask;

import com.example.rnotes.models.Note;
import com.example.rnotes.persistence.NoteDao;

public class DeleteAsyncTask extends AsyncTask<Note, Void, Void> {

    private NoteDao rNoteDao;


    public DeleteAsyncTask(NoteDao noteDao) {
        rNoteDao = noteDao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        rNoteDao.delete(notes);
        return null;
    }
}
