package com.example.booki.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.Models.OrderModel;
import com.example.booki.Adapters.Order_Adapter;
import com.example.booki.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FragmentOrders extends Fragment {

    private static final String TAG = "FragmentOrders";

    RecyclerView recyclerOrders;
    LinearLayout layoutEmptyOrders; // ✅ empty state view

    FirebaseFirestore db;
    FirebaseAuth auth;

    List<OrderModel> orderList;
    Order_Adapter adapter;

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

        recyclerOrders   = view.findViewById(R.id.recyclerOrders);
        layoutEmptyOrders = view.findViewById(R.id.layoutEmptyOrders); // ✅ bind empty state

        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter   = new Order_Adapter(getContext(), orderList);
        recyclerOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {

                    orderList.clear();

                    if (query.isEmpty()) {
                        Log.w(TAG, "No orders found for userId: " + userId);
                        updateEmptyState(); // ✅ show empty state
                        return;
                    }

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String paymentId = doc.getString("paymentId");
                        Long amountLong  = doc.getLong("totalAmount");
                        int amount       = amountLong != null ? amountLong.intValue() : 0;

                        List<String> bookIds = (List<String>) doc.get("bookIds");

                        if (bookIds != null && !bookIds.isEmpty()) {
                            for (String bookId : bookIds) {
                                db.collection("books")
                                        .document(bookId)
                                        .get()
                                        .addOnSuccessListener(bookDoc -> {

                                            String bookName = bookDoc.getString("title");
                                            if (bookName == null) bookName = "Unknown Book";

                                            orderList.add(new OrderModel(
                                                    doc.getId(),
                                                    paymentId,
                                                    amount,
                                                    bookName
                                            ));

                                            adapter.notifyDataSetChanged();
                                            updateEmptyState(); // ✅ check after each book loads
                                        })
                                        .addOnFailureListener(e ->
                                                Log.e(TAG, "Failed to fetch book: " + e.getMessage())
                                        );
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore query failed: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to load orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    updateEmptyState(); // ✅ show empty state on failure too
                });
    }

    // ✅ toggles between empty state and recycler view
    private void updateEmptyState() {
        if (orderList.isEmpty()) {
            recyclerOrders.setVisibility(View.GONE);
            layoutEmptyOrders.setVisibility(View.VISIBLE);
        } else {
            recyclerOrders.setVisibility(View.VISIBLE);
            layoutEmptyOrders.setVisibility(View.GONE);
        }
    }
}