package com.example.booki;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Payment extends AppCompatActivity implements PaymentResultListener {

    TextView tvTotalAmount;
    Button btnPayNow;

    int totalAmount = 0;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        // Views
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        // Get total amount from FragmentCart
        totalAmount = getIntent().getIntExtra("totalAmount", 0);
        tvTotalAmount.setText("₹" + totalAmount);

        // Preload Razorpay
        Checkout.preload(getApplicationContext());

        btnPayNow.setOnClickListener(v -> {
            if (totalAmount <= 0) {
                Toast.makeText(this, "Amount is invalid", Toast.LENGTH_SHORT).show();
            } else {
                startPayment();
            }
        });
    }

    private void startPayment() {

        Checkout checkout = new Checkout();

        // Replace with your Razorpay Test Key
        checkout.setKeyID("rzp_test_SQFdOry4WI83dF");

        Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Booki");
            options.put("description", "Cart Payment");
            options.put("currency", "INR");

            // Amount should be in paise
            options.put("amount", totalAmount * 100);

            // Prefill details (optional)
            JSONObject prefill = new JSONObject();
            prefill.put("email", "test@booki.com");
            prefill.put("contact", "9876543210");

            options.put("prefill", prefill);

            checkout.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, "Payment error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();

        saveOrder(razorpayPaymentID);
        finish();
    }


    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_LONG).show();
    }


    // get all the books in the cart.
    public void saveOrder(String paymentId){

        db.collection("users").document(userId)
                .collection("cart").get()
                .addOnSuccessListener(query -> {

                List<String> allBooks = new ArrayList<>();
                for (DocumentSnapshot doc : query.getDocuments()) {
                    String bookId = doc.getString("bookId");
                    allBooks.add(bookId);       // creates the list of all the books in the cart
                }

                createOrder(paymentId, allBooks);
        });
    }

    // saving the order in the database.
    private void createOrder(String paymentId, List<String> allBooks) {

        Map<String, Object> order = new HashMap<>();

        order.put("userId", userId);
        order.put("paymentId", paymentId);
        order.put("totalAmount", totalAmount);
        order.put("bookIds", allBooks);
        order.put("timestamp", System.currentTimeMillis());

        db.collection("orders").add(order)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Order Created", Toast.LENGTH_SHORT).show();
                    clearCart();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Order Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearCart() {
        if (userId == null) return;

        db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(query -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : query.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }
}