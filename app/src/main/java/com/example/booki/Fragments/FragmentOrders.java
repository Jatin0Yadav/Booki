package com.example.booki.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.booki.Models.OrderModel;
import com.example.booki.order_adapter;
import com.example.booki.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class FragmentOrders extends Fragment {

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

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerOrders = view.findViewById(R.id.recyclerOrders);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        userId = auth.getCurrentUser().getUid();

        orderList = new ArrayList<>();
        adapter = new order_adapter(getContext(), orderList);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerOrders.setAdapter(adapter);

        loadOrders();

        return view;
    }

    private void loadOrders() {

        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {

                    orderList.clear();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String paymentId = doc.getString("paymentId");
                        Long amountLong = doc.getLong("totalAmount");

                        List<String> bookIds = (List<String>) doc.get("bookIds");

                        int amount = amountLong != null ? amountLong.intValue() : 0;

                        if (bookIds != null && !bookIds.isEmpty()) {

                            String bookId = bookIds.get(0);

                            db.collection("books")
                                    .document(bookId)
                                    .get()
                                    .addOnSuccessListener(bookDoc -> {

                                        String bookName = bookDoc.getString("title");

                                        OrderModel order = new OrderModel(
                                                doc.getId(),
                                                paymentId,
                                                amount,
                                                bookName
                                        );

                                        orderList.add(order);
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
    }
}