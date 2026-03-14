package com.example.booki.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.booki.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentProfile extends Fragment {

    TextInputEditText etName, etEmail, etPhone, etAddress;
    Button btnUpdate;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userId;

    public FragmentProfile() {}

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        etName = view.findViewById(R.id.et_fullname);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        etAddress = view.findViewById(R.id.et_address);
        btnUpdate = view.findViewById(R.id.btn_update);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        userId = auth.getCurrentUser().getUid();

        // Fetch data
        loadUserData();

        // Update button
        btnUpdate.setOnClickListener(v -> updateProfile());

        return view;
    }

    // Fetch user data
    private void loadUserData() {

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {

                        etName.setText(document.getString("fullname"));
                        etEmail.setText(document.getString("email"));
                        etPhone.setText(document.getString("phone"));
                        etAddress.setText(document.getString("address"));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    // Update user data
    private void updateProfile() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Name and Email required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("address", address);

        db.collection("users")
                .document(userId)
                .update(user)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show()
                );
    }
}