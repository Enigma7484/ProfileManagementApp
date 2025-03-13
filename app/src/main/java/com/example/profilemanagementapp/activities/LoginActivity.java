package com.example.profilemanagementapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.models.User;
import com.example.profilemanagementapp.services.AuthenticationService;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private AuthenticationService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthenticationService(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        User user = authService.authenticate(username, password);
        if (user != null) {
            Snackbar.make(btnLogin, "Login Successful!", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProfileActivity.class);
            // Pass the user ID so ProfileActivity can load user data
            intent.putExtra("userId", user.getId());
            startActivity(intent);
            finish();
        } else {
            Snackbar.make(btnLogin, "Invalid Credentials", Snackbar.LENGTH_SHORT).show();
        }
    }
}