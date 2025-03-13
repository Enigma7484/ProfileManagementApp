package com.example.profilemanagementapp.activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.database.DatabaseHelper;
import com.example.profilemanagementapp.models.User;
import com.example.profilemanagementapp.utils.EncryptionHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etDob, etAddress, etPhone, etUsername, etPassword;
    private Button btnRegister, btnSelectPic, btnCapturePic;
    private ImageView ivProfilePic;
    private DatabaseHelper dbHelper;
    private byte[] profilePicData = null;
    // Address pattern: starts with number, space, letter, and at least 5 total characters
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d+\\s+[A-Za-z].{3,}$");

    // Launcher for selecting an image from the gallery.
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        InputStream is = getContentResolver().openInputStream(
                                Objects.requireNonNull(result.getData().getData()));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        profilePicData = baos.toByteArray();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(profilePicData, 0, profilePicData.length);
                        ivProfilePic.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    // Launcher for capturing an image using the camera.
    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    // Convert bitmap to byte array.
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
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnSelectPic = findViewById(R.id.btnSelectPic);
        btnCapturePic = findViewById(R.id.btnCapturePic);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        // Disable direct DOB input; launch DatePicker on click.
        etDob.setFocusable(false);
        etDob.setOnClickListener(v -> showDatePickerDialog());

        // Format phone input.
        etPhone.addTextChangedListener(new PhoneTextWatcher());

        // Launch gallery picker.
        btnSelectPic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            galleryLauncher.launch(Intent.createChooser(intent, "Select Profile Picture"));
        });

        // Launch camera capture.
        btnCapturePic.setOnClickListener(v -> cameraLauncher.launch(null));

        btnRegister.setOnClickListener(v -> registerUser());
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

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().replaceAll("\\D", "");
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (dob.isEmpty()) {
            etDob.setError("Please select a valid date of birth.");
            return;
        }
        if (phone.length() != 10) {
            etPhone.setError("Phone number must be 10 digits (Canadian style).");
            return;
        }
        if (!ADDRESS_PATTERN.matcher(address).matches()) {
            etAddress.setError("Please enter a valid address, e.g. '123 Main St'");
            return;
        }
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[@#$%^&+=].*")) {
            etPassword.setError("Password must be >=8 chars, include 1 capital letter & 1 special char");
            return;
        }

        String hashedPassword = EncryptionHelper.hashPassword(password);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("dob", dob);
        values.put("address", address);
        values.put("phone", phone);
        values.put("username", username);
        values.put("password", hashedPassword);
        if (profilePicData != null) {
            values.put("profile_pic", profilePicData);
        }
        long result = db.insert("users", null, values);
        db.close();

        if (result != -1) {
            Snackbar.make(btnRegister, "Registration Successful", Snackbar.LENGTH_SHORT).show();
            finish();
        } else {
            Snackbar.make(btnRegister, "Registration Failed", Snackbar.LENGTH_SHORT).show();
        }
    }

    // TextWatcher to format phone as (XXX) XXX-XXXX.
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