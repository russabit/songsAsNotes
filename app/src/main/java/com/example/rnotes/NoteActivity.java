package com.example.rnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rnotes.models.Note;
import com.example.rnotes.persistence.NoteRepository;
import com.example.rnotes.util.LinedEditText;
import com.example.rnotes.util.Utility;

public class NoteActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener {

    private static final String TAG = "NoteActivity";
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    // UI components
    private LinedEditText rLinedEditText;
    private EditText rEditTitle;
    private TextView rViewTitle;
    private RelativeLayout rCheckContainer, rBackArrowContainer;
    private ImageButton rCheck, rBackArrow;


    // vars
    private boolean rIsNewNote;
    private Note rInitialNote;
    private GestureDetector rGestureDetector;
    private int rMode;
    private NoteRepository rNoteRepository;
    private Note rFinalNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        rLinedEditText = findViewById(R.id.note_text);
        rEditTitle = findViewById(R.id.note_edit_title);
        rViewTitle = findViewById(R.id.note_text_title);
        rCheck = findViewById(R.id.toolbar_checkmark);
        rCheckContainer = findViewById(R.id.checkmark_container);
        rBackArrow = findViewById(R.id.toolbar_backarrow);
        rBackArrowContainer = findViewById(R.id.backarrow_container);

        rNoteRepository = new NoteRepository(this);

        if(getIncomingIntent()) {
            //THIS IS A NEW NOTE
            setNewNoteProperties();
            enableEditMode();
        }
            else {
                //this is NOT a new note
            setNoteProperties();
            disableContentInteraction();
        }

            setListeners();
    }

    private Boolean getIncomingIntent() {
        if (getIntent().hasExtra("selected_note")) {

            rInitialNote = getIntent().getParcelableExtra("selected_note");

            rFinalNote = new Note(rInitialNote.getTitle(), rInitialNote.getContent(), rInitialNote.getTimestamp());
            rFinalNote.setId(rInitialNote.getId());

            rMode = EDIT_MODE_DISABLED;

            rIsNewNote = false;
            return false;
        }
        rMode = EDIT_MODE_ENABLED;
        rIsNewNote = true;
        return true;
    }

    private void saveChanges() {
        if (rIsNewNote) {
            saveNewNote();
        }
        else {
            updateNote();
        }
    }

    private void updateNote() {
        rNoteRepository.updateNote(rFinalNote);
    }


    private void saveNewNote() {
        rNoteRepository.insertNoteTask(rFinalNote);
    }

    private void disableContentInteraction() {
        rLinedEditText.setKeyListener(null);
        rLinedEditText.setFocusable(false);
        rLinedEditText.setFocusableInTouchMode(false);
        rLinedEditText.setCursorVisible(false);
        rLinedEditText.clearFocus();
    }

    private void enableContentInteraction() {
        rLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        rLinedEditText.setFocusable(true);
        rLinedEditText.setFocusableInTouchMode(true);
        rLinedEditText.setCursorVisible(true);
        rLinedEditText.requestFocus();
    }

    private void enableEditMode() {
        rBackArrowContainer.setVisibility(View.GONE);
        rCheckContainer.setVisibility(View.VISIBLE);

        rViewTitle.setVisibility(View.GONE);
        rEditTitle.setVisibility(View.VISIBLE);

        rMode = EDIT_MODE_ENABLED;

        enableContentInteraction();
    }

    private void disableEditMode() {
        rBackArrowContainer.setVisibility(View.VISIBLE);
        rCheckContainer.setVisibility(View.GONE);

        rViewTitle.setText(rEditTitle.getText());
        rViewTitle.setVisibility(View.VISIBLE);
        rEditTitle.setVisibility(View.GONE);

        rMode = EDIT_MODE_DISABLED;

        disableContentInteraction();

        String temp = rLinedEditText.getText().toString();
        temp = temp.replace("\n", "");
        temp = temp.replace(" ", "");
        if (temp.length() > 0) {
            rFinalNote.setTitle(rEditTitle.getText().toString());
            rFinalNote.setContent(rLinedEditText.getText().toString());
            String timestamp = Utility.getCurrentTimeStamp();
            rFinalNote.setTimestamp(timestamp);
            if (!rFinalNote.getContent().equals(rInitialNote.getContent())
                || !rFinalNote.getTitle().equals(rInitialNote.getTitle())) {
                Log.d(TAG, "disableEditMode: called");
                saveChanges();
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void rEditTitleShowKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.showSoftInput(rEditTitle, 0);
    }

    private void setNewNoteProperties() {
        rViewTitle.setText("Note Title");
        rEditTitle.setText("Note Title");

        rInitialNote = new Note();
        rFinalNote = new Note();
        rInitialNote.setTitle("Note Title");
        rFinalNote.setTitle("Note Title");
    }

    private void setNoteProperties() {
        rViewTitle.setText(rInitialNote.getTitle());
        rEditTitle.setText(rInitialNote.getTitle());
        rLinedEditText.setText(rInitialNote.getContent());
    }

    private void setListeners() {
        rLinedEditText.setOnTouchListener(this);
        rGestureDetector = new GestureDetector(this,this);
        rViewTitle.setOnClickListener(this);
        rCheck.setOnClickListener(this);
        rBackArrow.setOnClickListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return rGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: double tapped!");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (rMode == EDIT_MODE_ENABLED) {
            onClick(rCheck);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {


       switch (v.getId()){
           case R.id.toolbar_checkmark: {
               hideSoftKeyboard();
               disableEditMode();
               break;
           }

           case R.id.note_text_title: {
               enableEditMode();
               rEditTitle.requestFocus();
               rEditTitle.setSelection(rEditTitle.length());
               rEditTitleShowKeyboard();
               break;
           }

           case R.id.toolbar_backarrow: {
               finish();
           }
       }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", rMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rMode = savedInstanceState.getInt("mode");
        if (rMode == EDIT_MODE_ENABLED) {
            enableEditMode();
        }
        else {
            disableEditMode();
        }
    }

}
