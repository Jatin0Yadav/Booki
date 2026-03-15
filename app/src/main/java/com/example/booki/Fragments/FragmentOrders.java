package com.example.booki.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.Models.OrderModel;
import com.example.booki.order_adapter;
import com.example.booki.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FragmentOrders extends Fragment {

    private static final String TAG = "FragmentOrders"; // ✅ for Logcat filtering

    RecyclerView recyclerOrders;

    FirebaseFirestore db;
    FirebaseAuth auth;

    List<OrderModel> orderList;
    order_adapter adapter;

    String userId;

    public FragmentOrders() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "User is not logged in");
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        userId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Logged in userId: " + userId); // ✅ Step 1: verify userId

        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter   = new order_adapter(getContext(), orderList);
        recyclerOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        Log.d(TAG, "loadOrders() called with userId: " + userId); // ✅ Step 2: confirm method runs

        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {

                    Log.d(TAG, "Query success — documents found: " + query.size()); // ✅ Step 3: how many docs matched

                    orderList.clear();

                    if (query.isEmpty()) {
                        Log.w(TAG, "No orders found for userId: " + userId); // ✅ Step 4: confirms userId mismatch
                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        Log.d(TAG, "Order doc ID: " + doc.getId()); // ✅ Step 5: log each order doc
                        Log.d(TAG, "Order data: " + doc.getData()); // ✅ Step 6: print ALL fields in doc

                        String paymentId = doc.getString("paymentId");
                        Long amountLong  = doc.getLong("totalAmount");
                        int amount       = amountLong != null ? amountLong.intValue() : 0;

                        List<String> bookIds = (List<String>) doc.get("bookIds");

                        Log.d(TAG, "bookIds: " + bookIds); // ✅ Step 7: confirm bookIds field exists

                        if (bookIds != null && !bookIds.isEmpty()) {

                            for (String bookId : bookIds) {
                                Log.d(TAG, "Fetching book: " + bookId); // ✅ Step 8: confirm book fetch starts

                                db.collection("books")
                                        .document(bookId)
                                        .get()
                                        .addOnSuccessListener(bookDoc -> {

                                            Log.d(TAG, "Book doc exists: " + bookDoc.exists()); // ✅ Step 9: confirm book found
                                            Log.d(TAG, "Book data: " + bookDoc.getData());       // ✅ Step 10: print book fields

                                            String bookName = bookDoc.getString("title");
                                            if (bookName == null) bookName = "Unknown Book";

                                            OrderModel order = new OrderModel(
                                                    doc.getId(),
                                                    paymentId,
                                                    amount,
                                                    bookName
                                            );

                                            orderList.add(order);
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Failed to fetch book: " + bookId + " — " + e.getMessage()); // ✅ Step 11
                                            Toast.makeText(getContext(), "Failed to load book details", Toast.LENGTH_SHORT).show();
                                        });
                            }

                        } else {
                            Log.w(TAG, "bookIds is null or empty for order: " + doc.getId()); // ✅ Step 12
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore query failed: " + e.getMessage()); // ✅ Step 13
                    Toast.makeText(getContext(), "Failed to load orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}