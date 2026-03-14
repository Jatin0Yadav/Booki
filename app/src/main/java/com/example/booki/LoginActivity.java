package com.example.booki;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    TextView welcome_msg_login;

    Button btn_go, btn_login_signup;
    ImageView image;
    TextInputLayout fullname, phone, email, password;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcome_msg_login = findViewById(R.id.welcome_msg_login);
        image = findViewById(R.id.imageView2);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_go = findViewById(R.id.Go);
        btn_login_signup = findViewById(R.id.btn_login_signup);

        mAuth = FirebaseAuth.getInstance();


        btn_go.setOnClickListener(v -> {
            loginUser();
        });


        btn_login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUp.class);

                Pair[] pair = new Pair[6];

                pair[0] = new Pair<View, String>(image, "trans_img_splash1");
                pair[1] = new Pair<View, String>(welcome_msg_login, "trans_login_txt");
                pair[2] = new Pair<View, String>(email, "trans_username");
                pair[3] = new Pair<View, String>(password, "trans_password");
                pair[4] = new Pair<View, String>(btn_go, "trans_Go");
                pair[5] = new Pair<View, String>(btn_login_signup, "trans_login_signup");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pair);
                startActivity(i, options.toBundle());

                // finish();   no finish, cuz we want to come back to this page.
            }

        });

    }

    private void loginUser() {
        String emailString = email.getEditText().getText().toString().trim();
        String passwordString = password.getEditText().getText().toString().trim();

        if (emailString.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (passwordString.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }


        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Authentication successful.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, Dashboard1.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
