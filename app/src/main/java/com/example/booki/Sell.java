package com.example.booki;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Sell extends AppCompatActivity {

    private static final String TAG = "SELL_DEBUG";
    private static final int CAMERA_REQUEST = 100;
    private static final int CAMERA_PERMISSION_CODE = 101;

    Spinner spCategory;
    com.google.android.material.card.MaterialCardView btnUploadImage;
    Button btnSubmit;
    ImageView imagePreview;

    EditText etBookName, etPrice, etDescription, etSubject, etAuthor, etEdition;
    CheckBox negotiable;

    Uri imageUri;
    File imageFile;

    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        db      = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://project-f3d16.firebasestorage.app");
        auth    = FirebaseAuth.getInstance();

        spCategory     = findViewById(R.id.spCategory);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSubmit      = findViewById(R.id.postBook);
        imagePreview   = findViewById(R.id.imagePreview);

        etBookName    = findViewById(R.id.etTitle);
        etPrice       = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etSubject     = findViewById(R.id.etSubject);
        etAuthor      = findViewById(R.id.etAuthor);
        etEdition     = findViewById(R.id.etEdition);
        negotiable    = findViewById(R.id.cbNegotiable);

        String[] categories = {"Select Category", "JEE", "NEET", "UPSC", "SSC", "Other"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item);
        spCategory.setAdapter(categoryAdapter);

        btnUploadImage.setOnClickListener(v -> checkCameraPermission());
        btnSubmit.setOnClickListener(v -> uploadBook());
    }

    // ─── CAMERA ──────────────────────────────────────────────────────────────

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        imageFile = createImageFile();
        if (imageFile == null) {
            Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show();
            return;
        }

        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                imageFile
        );

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // ✅ DO NOT use resolveActivity() on Android 11+ — it always returns null
        // due to package visibility restrictions, blocking the camera from opening.
        // Just call startActivityForResult directly — it is safe.
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private File createImageFile() {
        try {
            String fileName = "BOOK_" + System.currentTimeMillis();
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.e(TAG, "createImageFile error: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (imageFile != null && imageFile.exists() && imageFile.length() > 0) {
                    Log.d(TAG, "Image captured OK. Size=" + imageFile.length());
                    imagePreview.setImageURI(null);
                    imagePreview.setImageURI(imageUri);
                    Toast.makeText(this, "Image captured ✓", Toast.LENGTH_SHORT).show();
                } else {
                    imageUri = null;
                    imageFile = null;
                    Toast.makeText(this, "Capture failed, try again", Toast.LENGTH_LONG).show();
                }
            } else {
                imageUri = null;
                imageFile = null;
            }
        }
    }

    // ─── UPLOAD ──────────────────────────────────────────────────────────────

    private void uploadBook() {
        String name     = etBookName.getText().toString().trim();
        String price    = etPrice.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (name.isEmpty() || price.isEmpty() || category.equals("Select Category")) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null || imageFile == null) {
            Toast.makeText(this, "Please capture an image first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!imageFile.exists() || imageFile.length() == 0) {
            Toast.makeText(this, "Image file missing — capture again", Toast.LENGTH_LONG).show();
            imageUri = null;
            imageFile = null;
            return;
        }
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        String userId = auth.getCurrentUser().getUid();

        // ✅ Use putBytes() — reads entire file into memory first.
        // This is the most reliable upload method and avoids all URI/stream issues.
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Cannot read image", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                return;
            }

            // ✅ readAllBytes() only works on API 33+, this works on ALL Android versions
            byte[] imageBytes = readStreamBytes(inputStream);
            inputStream.close();

            Log.d(TAG, "Image bytes read: " + imageBytes.length);

            StorageReference ref = storage.getReference()
                    .child("book_images/" + System.currentTimeMillis() + ".jpg");

            ref.putBytes(imageBytes)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Upload SUCCESS");
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d(TAG, "Download URL: " + uri);
                                    saveToFirestore(uri.toString(), userId);
                                })
                                .addOnFailureListener(e -> {
                                    btnSubmit.setEnabled(true);
                                    Log.e(TAG, "getDownloadUrl failed: " + e.getMessage());
                                    Toast.makeText(Sell.this, "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        btnSubmit.setEnabled(true);
                        Log.e(TAG, "Upload FAILED: " + e.getMessage());
                        Toast.makeText(Sell.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } catch (IOException e) {
            btnSubmit.setEnabled(true);
            Log.e(TAG, "Read image bytes failed: " + e.getMessage());
            Toast.makeText(this, "Error reading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ─── HELPER: read all bytes from stream (works on all Android versions) ──

    private byte[] readStreamBytes(InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(chunk)) != -1) {
            buffer.write(chunk, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    // ─── SAVE TO FIRESTORE ───────────────────────────────────────────────────

    private void saveToFirestore(String imageUrl, String userId) {
        String bookId = db.collection("books").document().getId();

        Map<String, Object> book = new HashMap<>();
        book.put("bookId",       bookId);
        book.put("sellerId",     userId);
        book.put("title",        etBookName.getText().toString().trim());
        book.put("price",        etPrice.getText().toString().trim());
        book.put("category",     spCategory.getSelectedItem().toString());
        book.put("subject",      etSubject.getText().toString().trim());
        book.put("author",       etAuthor.getText().toString().trim());
        book.put("edition",      etEdition.getText().toString().trim());
        book.put("description",  etDescription.getText().toString().trim());
        book.put("isNegotiable", negotiable.isChecked());
        book.put("imageUrl",     imageUrl);
        book.put("timestamp",    System.currentTimeMillis());

        db.collection("books").document(bookId)
                .set(book)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> userUpdate = new HashMap<>();
                    userUpdate.put("isSeller", true);
                    db.collection("users").document(userId).set(userUpdate, SetOptions.merge());
                    Toast.makeText(Sell.this, "Book Posted Successfully! ✓", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    Log.e(TAG, "Firestore FAILED: " + e.getMessage());
                    Toast.makeText(Sell.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}