package dev.haguel.moveotask.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.Calendar;
import dev.haguel.moveotask.Utils;
import dev.haguel.moveotask.DatabaseManager;
import dev.haguel.moveotask.LocationAndPermissionManager;
import dev.haguel.moveotask.R;
import dev.haguel.moveotask.entities.NoteEntity;

public class NoteActivity extends AppCompatActivity implements OnSuccessListener<Location>, View.OnClickListener {

    // Finals
    private static final String NOTE_INTENT_KEY = "note_intent_key";

    // UI Views
    private TextView tvDate, tvDateCreated, tvLastModified;
    private EditText etTitle, etBody;
    private Button btnSave, btnDelete;
    private ImageView ivNoteImage;

    // Data
    private NoteEntity note;
    private boolean shouldAttachLocation;

    public static void startNotActivity (Activity act, NoteEntity note){
        Intent intent = new Intent(act, NoteActivity.class);

        if (note != null)
            intent.putExtra(NOTE_INTENT_KEY, note);

        act.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Fetch Views
        tvDate = findViewById(R.id.tvNoteDate);
        tvDateCreated = findViewById(R.id.tvNoteDateCreated);
        tvLastModified = findViewById(R.id.tvNoteLastModified);
        etTitle = findViewById(R.id.etNoteTitle);
        etBody = findViewById(R.id.etNoteBody);
        ivNoteImage = findViewById(R.id.ivNoteImage);
        btnSave = findViewById(R.id.btnNoteSave);
        btnDelete = findViewById(R.id.btnNoteDelete);

        // Handle Clicks
        tvDate.setOnClickListener(this);
        ivNoteImage.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        // Loading Current Note
        if (getIntent().hasExtra(NOTE_INTENT_KEY) && getIntent().getSerializableExtra(NOTE_INTENT_KEY) != null) {
            note = (NoteEntity) getIntent().getSerializableExtra(NOTE_INTENT_KEY);
            btnDelete.setEnabled(true);
            shouldAttachLocation = false;
        } else {
            note = new NoteEntity();
            btnDelete.setEnabled(false);
            note.setDate(System.currentTimeMillis());
            shouldAttachLocation = true;
        }


        // Set Data To UI
        tvDate.setText(Utils.longToDateFormatter(note.getDate(), false));

        if (note.getCreated() == 0){
            tvDateCreated.setText("-");
        } else {
            tvDateCreated.setText(Utils.longToDateFormatter(note.getCreated(), true));
        }
        if (!TextUtils.isEmpty(note.getTitle())){
            etTitle.setText(note.getTitle());
        }
        if (!TextUtils.isEmpty(note.getBody())){
            etBody.setText(note.getBody());
        }
        if (note.getUpdate() > 0) {
            tvLastModified.setText(Utils.longToDateFormatter(note.getUpdate(), true));
        } else {
            tvLastModified.setText("-");
        }

        if (!TextUtils.isEmpty(note.getImage())) {
            Bitmap bitmap = Utils.StringToBitMap(note.getImage());
            ivNoteImage.setImageBitmap(bitmap);
        } else {
            ivNoteImage.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocationAndPermissionManager.instance().initLocationWithPermission(this, this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DatabaseManager.GEO_LOCATION_REQUEST_CODE && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationAndPermissionManager.instance().refreshLastLocation(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                // Show Selected Image In UI
                ivNoteImage.setImageURI(uri);

                try {
                    // Save Selected Image To Note
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    this.note.setImage(Utils.BitMapToString(bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    // OnSuccessListener<Location> interface callback
    public void onSuccess(Location location) {
        if (shouldAttachLocation && location != null && note != null){
            note.setLatitude(location.getLatitude());
            note.setLongitude(location.getLongitude());
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == tvDate.getId()) {
            showDatePicker();
        }
        if (id == ivNoteImage.getId()) {
            showImagePicker();
        }
        if (id == btnSave.getId()) {
            onSavePressed();
        }
        if (id == btnDelete.getId()) {
            onDeletePressed();
        }
    }

    private void showDatePicker(){
        final Calendar newCalendar = Calendar.getInstance();

        final DatePickerDialog startTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                // Update UI and Data
                note.setDate(newDate.getTimeInMillis());
                tvDate.setText(Utils.longToDateFormatter(note.getDate(), false));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        startTime.show();
    }

    private void showImagePicker(){
        ImagePicker.with(NoteActivity.this)
                .cropSquare()
                .compress(100)
                .maxResultSize(75, 75)
                .start();
    }

    private void onSavePressed(){
        if (etTitle.getText().toString().trim().isEmpty()){
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }
        if (note.getCreated() != 0) {
            note.setUpdate(System.currentTimeMillis());
        }
        if (note.getCreated() == 0){
            note.setCreated(System.currentTimeMillis());
        }
        note.setTitle(etTitle.getText().toString());
        note.setBody(etBody.getText().toString().trim());

        DatabaseManager.instance().createOrEditNote(note);
        finish();
    }

    private void onDeletePressed(){
        if (note != null && note.getCreated() > 0)
            DatabaseManager.instance().deleteNote(note.getCreated());
        finish();
    }


}