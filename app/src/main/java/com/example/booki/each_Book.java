package com.example.booki;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.booki.Models.books_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class each_Book extends AppCompatActivity {

    Button btnAddToCart;
    TextView bookTitle, bookCategory, bookPrice, bookDescription, sellerName, bookAuthor;

    FirebaseFirestore db;
    FirebaseAuth auth;

    Toolbar toolbar;
    String userId, bookId;
    books_Model currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_book);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );


        btnAddToCart    = findViewById(R.id.btnAddToCart);
        bookTitle       = findViewById(R.id.bookTitle);
        bookCategory    = findViewById(R.id.bookCategory);
        bookPrice       = findViewById(R.id.bookPrice);
        bookAuthor      = findViewById(R.id.bookAuthor);
        bookDescription = findViewById(R.id.bookDescription);
        sellerName      = findViewById(R.id.sellerName);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = auth.getCurrentUser().getUid();
        bookId = getIntent().getStringExtra("book_ID");

        loadBookDetails();
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void loadBookDetails() {
        db.collection("books")
                .document(bookId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        currentBook = document.toObject(books_Model.class);

                        // ✅ Always read sellerId directly from the raw document —
                        // more reliable than going through the model object
                        String sellerId = document.getString("sellerId");

                        // ✅ Null checks on every field — if Firestore returns null
                        // for any field, show a fallback instead of crashing or blank
                        bookTitle.setText(currentBook.getBook_name() != null
                                ? currentBook.getBook_name() : "No title");

                        bookCategory.setText(currentBook.getBook_category() != null
                                ? currentBook.getBook_category() : "No category");

                        bookPrice.setText(currentBook.getBook_amt() != null
                                ? "₹" + currentBook.getBook_amt() : "Price not set");

                        bookAuthor.setText(currentBook.getBook_author() != null
                                ? currentBook.getBook_author() : "Unknown author");

                        // ✅ This was likely showing blank — description field in Firestore
                        // must match @PropertyName("description") in books_Model
                        bookDescription.setText(currentBook.getBook_description() != null
                                ? currentBook.getBook_description() : "No description");

                        // ✅ Guard against null sellerId before making another Firestore call
                        if (sellerId != null && !sellerId.isEmpty()) {
                            db.collection("users")
                                    .document(sellerId)
                                    .get()
                                    .addOnSuccessListener(sellerDoc -> {
                                        if (sellerDoc.exists()) {
                                            String seller = sellerDoc.getString("name");
                                            sellerName.setText(seller != null
                                                    ? "Seller: " + seller : "Seller: Unknown");
                                        } else {
                                            sellerName.setText("Seller: Not found");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        sellerName.setText("Seller: Unavailable");
                                    });
                        } else {
                            sellerName.setText("Seller: Unknown");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addToCart() {

        if (currentBook == null) {
            Toast.makeText(this, "Book not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        String cartId = db.collection("users")
                .document(userId)
                .collection("cart")
                .document()
                .getId();

        Map<String, Object> cart = new HashMap<>();
        cart.put("cartId",    cartId);
        cart.put("bookId",    bookId);
        cart.put("title",     currentBook.getBook_name());
        cart.put("price",     currentBook.getBook_amt());
        cart.put("category",  currentBook.getBook_category());
        cart.put("sellerId",  currentBook.getUser_ID());
        cart.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userId)
                .collection("cart")
                .document(cartId)
                .set(cart)
                .addOnSuccessListener(unused -> {
                    btnAddToCart.setText("Added");
                    Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}