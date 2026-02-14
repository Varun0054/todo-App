package com.blackspider.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText username;
    private TextInputEditText password;
    private MaterialButton signupButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username_signup);
        password = findViewById(R.id.password_signup);
        signupButton = findViewById(R.id.signup_button);
        databaseHelper = new DatabaseHelper(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (databaseHelper.addUser(user, pass)) {
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
