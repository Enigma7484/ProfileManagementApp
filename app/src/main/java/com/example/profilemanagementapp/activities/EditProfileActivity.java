package com.example.profilemanagementapp.activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.database.DatabaseHelper;
import com.example.profilemanagementapp.utils.EncryptionHelper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etFullName, etDob, etAddress, etPhone, etUsername, etPassword;
    private Button btnSaveChanges, btnDeleteProfile, btnSelectPic, btnCapturePic;
    private ImageView ivProfilePic;
    private DatabaseHelper dbHelper;
    private int userId;
    private byte[] profilePicData = null;

    // Store original credentials to detect changes.
    private String oldUsername, oldPassword;
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d+\\s+[A-Za-z].{3,}$");

    // Launcher for selecting image from gallery.
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        ByteArrayOutputStream baos;
                        try (InputStream is = getContentResolver().openInputStream(Objects.requireNonNull(result.getData().getData()))) {
                            baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while (true) {
                                assert is != null;
                                if ((bytesRead = is.read(buffer)) == -1) break;
                                baos.write(buffer, 0, bytesRead);
                            }
                        }
                        profilePicData = baos.toByteArray();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(profilePicData, 0, profilePicData.length);
                        ivProfilePic.setImageBitmap(bitmap);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    // Launcher for capturing image from camera.
    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if(bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    profilePicData = baos.toByteArray();
                    ivProfilePic.setImageBitmap(bitmap);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);
        btnSelectPic = findViewById(R.id.btnSelectPic);
        btnCapturePic = findViewById(R.id.btnCapturePic);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        // DatePicker for DOB
        etDob.setFocusable(false);
        etDob.setOnClickListener(v -> showDatePickerDialog());

        // Format phone input
        etPhone.addTextChangedListener(new PhoneTextWatcher());

        // Set up profile picture update options
        btnSelectPic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            galleryLauncher.launch(Intent.createChooser(intent, "Select Profile Picture"));
        });
        btnCapturePic.setOnClickListener(v -> cameraLauncher.launch(null));

        loadUserProfile();

        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnDeleteProfile.setOnClickListener(v -> confirmDeleteProfile());
    }

    private void loadUserProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            etFullName.setText(cursor.getString(1));
            etDob.setText(cursor.getString(2));
            etAddress.setText(cursor.getString(3));
            etPhone.setText(cursor.getString(4));
            etUsername.setText(cursor.getString(5));
            oldUsername = cursor.getString(5);
            oldPassword = cursor.getString(6);
            // Load profile picture if exists
            byte[] pic = cursor.getBlob(7);
            if (pic != null) {
                profilePicData = pic;
                Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                ivProfilePic.setImageBitmap(bitmap);
            }
        }
        cursor.close();
        db.close();
    }

    private void saveChanges() {
        String fullName = etFullName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phoneRaw = etPhone.getText().toString().replaceAll("\\D", "");
        String newUsername = etUsername.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();

        if (dob.isEmpty()) {
            etDob.setError("Please select a valid date of birth.");
            return;
        }
        if (phoneRaw.length() != 10) {
            etPhone.setError("Phone number must be 10 digits (Canadian style).");
            return;
        }
        if (!ADDRESS_PATTERN.matcher(address).matches()) {
            etAddress.setError("Please enter a valid address, e.g. '123 Main St'");
            return;
        }

        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("dob", dob);
        values.put("address", address);
        values.put("phone", phoneRaw);
        values.put("username", newUsername);
        if (!newPassword.isEmpty()) {
            values.put("password", EncryptionHelper.hashPassword(newPassword));
        }
        if (profilePicData != null) {
            values.put("profile_pic", profilePicData);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        db.close();

        if (updatedRows > 0) {
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
            if (!newUsername.equals(oldUsername) || !newPassword.isEmpty()) {
                Toast.makeText(this, "Credentials changed, please log in again.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                finish();
            }
        } else {
            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteProfile() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete your profile? This action cannot be undone and will remove all associated diary entries.")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProfile() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete("users", "id = ?", new String[]{String.valueOf(userId)});
        db.close();

        if (deletedRows > 0) {
            Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year1);
                    etDob.setText(selectedDate);
                },
                year, month, day
        );
        datePicker.show();
    }

    private class PhoneTextWatcher implements TextWatcher {
        private boolean isFormatting;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            if (isFormatting) return;
            isFormatting = true;
            String digits = s.toString().replaceAll("\\D", "");
            StringBuilder formatted = new StringBuilder();
            int len = digits.length();
            for (int i = 0; i < len; i++) {
                if (i == 0) formatted.append("(");
                if (i == 3) formatted.append(") ");
                if (i == 6) formatted.append("-");
                formatted.append(digits.charAt(i));
            }
            etPhone.removeTextChangedListener(this);
            etPhone.setText(formatted.toString());
            etPhone.setSelection(formatted.length());
            etPhone.addTextChangedListener(this);
            isFormatting = false;
        }
    }
}