package com.example.rnotes.async;

import android.os.AsyncTask;

import com.example.rnotes.models.Note;
import com.example.rnotes.persistence.NoteDao;

public class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {

    private NoteDao rNoteDao;


    public UpdateAsyncTask(NoteDao noteDao) {
        rNoteDao = noteDao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        rNoteDao.update(notes);
        return null;
    }
}
