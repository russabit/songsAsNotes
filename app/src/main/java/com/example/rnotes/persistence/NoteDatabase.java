package com.example.rnotes.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.rnotes.models.Note;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "notes_db";

    private static NoteDatabase instanse;

    static NoteDatabase getInstance(final Context context) {
        if (instanse == null) {
            instanse = Room.databaseBuilder(
                    context.getApplicationContext(),
                    NoteDatabase.class,
                    DATABASE_NAME).build();
        }
        return instanse;
    }

    public abstract NoteDao getNoteDao();
}
