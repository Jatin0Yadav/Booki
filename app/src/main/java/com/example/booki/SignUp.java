package com.example.booki;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    Button btn_login, btn_Go_SignUp;

    TextInputLayout regusername, regfullname, regphone, regpassword, regemail, regaddress;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        // Hooks to all xml elements in activity_sign_up.xml
        regfullname = findViewById(R.id.fullname);
        regusername = findViewById(R.id.username);
        regphone = findViewById(R.id.phone);
        regemail = findViewById(R.id.email);
        regpassword = findViewById(R.id.password);
        regaddress = findViewById(R.id.address);

        btn_login = findViewById(R.id.btn_signup_login);
        btn_login.setOnClickListener(v -> {
            Intent i = new Intent(SignUp.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        btn_Go_SignUp = findViewById(R.id.btn_go_signup);
        btn_Go_SignUp.setOnClickListener(v -> {
            registerUser(v);
        });

    }


    private boolean validateName() {
        String val = regfullname.getEditText().getText().toString();
        if (val.isEmpty()) {
            // got these functions from material view.
            regfullname.setError("Field cannot be empty");
            return false;
        } else {
            regfullname.setError(null);
            regfullname.setErrorEnabled(false);             // It will remove the allocated space for error, after removing the error.
            return true;
        }
    }
    private boolean validateusername() {
        String val = regusername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z"; // Allow a-z, A-Z, 0-9, _ and length of 4-20

        if (val.isEmpty()) {
            regusername.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regusername.setError("Username must be 4-20 characters long and can only contain letters, numbers, and underscores.");
            return false;
        } else {
            regusername.setError(null);
            regusername.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateemail() {
        String val = regemail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regemail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regemail.setError("Invalid email address");
            return false;
        } else {
            regemail.setError(null);
            regemail.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatepassword() {
        String val = regpassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            // got these functions from material view.
            regpassword.setError("Field cannot be empty");
            return false;
        } else if (val.length() < 6) {
            regpassword.setError("Password must be at least 6 characters long.");
            return false;
        } else {
            regpassword.setError(null);
            regpassword.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatephone() {
        String val = regphone.getEditText().getText().toString();
        if (val.isEmpty()) {
            regphone.setError("Field cannot be empty");
            return false;
        } else {
            regphone.setError(null);
            regphone.setErrorEnabled(false);
            return true;
        }
    }

    // Save data in firebase on button click.
    public void registerUser(View view) {

        if (!validateName() || !validateemail() || !validatepassword() || !validatephone() || !validateusername()) {
            return;
        }

        // these are string objects.
        String name = regfullname.getEditText().getText().toString().trim();
        String username = regusername.getEditText().getText().toString().trim();
        String phone = regphone.getEditText().getText().toString().trim();
        String email = regemail.getEditText().getText().toString().trim();
        String password = regpassword.getEditText().getText().toString().trim();
        String address = regaddress.getEditText().getText().toString().trim();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // create user map.
                        Toast.makeText(SignUp.this, "SignedIn", Toast.LENGTH_SHORT).show();

                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("username", username);
                        user.put("phone", phone);
                        user.put("email", email);
                        // user.put("password", password);  never do this, firestore always store password.
                        user.put("address", address);
                        user.put("isSeller", false);
                        user.put("timestamp", FieldValue.serverTimestamp());


                        // get userId
                        String userId = mAuth.getCurrentUser().getUid();


                        // add data to firebase.
                        db.collection("users").document(userId).set(user).addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful()) {
                                Intent i = new Intent(SignUp.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(SignUp.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Exception e = task.getException();
                        Toast.makeText(SignUp.this,
                                "Authentication Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

}
