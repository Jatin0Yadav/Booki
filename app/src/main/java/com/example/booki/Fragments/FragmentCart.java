package com.example.booki.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.Models.cart_Model;
import com.example.booki.Payment;
import com.example.booki.R;
import com.example.booki.cart_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FragmentCart extends Fragment {

    RecyclerView cart_rcview;
    ArrayList<cart_Model> cartList;
    cart_Adapter adapter;

    TextView tvTotalAmount, tvItemCount;
    Button btnConfirm;

    FirebaseFirestore db;
    FirebaseAuth auth;
    String userId;

    int totalAmount = 0;

    public FragmentCart() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        cart_rcview   = view.findViewById(R.id.cartrecyclerView);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvItemCount   = view.findViewById(R.id.tvItemCount);
        btnConfirm    = view.findViewById(R.id.btn_confirm);

        cart_rcview.setLayoutManager(new LinearLayoutManager(getContext()));

        cartList = new ArrayList<>();
        adapter  = new cart_Adapter(cartList);
        cart_rcview.setAdapter(adapter);

        loadCartItems();

        btnConfirm.setOnClickListener(v -> {
            if (totalAmount == 0) {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getContext(), Payment.class);
            intent.putExtra("totalAmount", totalAmount);
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        cartList.clear();
                        totalAmount = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cart_Model item = document.toObject(cart_Model.class);
                            cartList.add(item);

                            // ✅ safe parseInt — won't crash if price is empty/null
                            try {
                                totalAmount += Integer.parseInt(item.getBook_amt().trim());
                            } catch (NumberFormatException e) {
                                // skip unparseable price
                            }
                        }

                        adapter.notifyDataSetChanged();

                        // ✅ String.valueOf() prevents Resources$NotFoundException crash
                        tvTotalAmount.setText("₹ " + String.valueOf(totalAmount));

                        // ✅ was tvItemCount.setText() with no argument — that's illegal
                        tvItemCount.setText(cartList.size() + " items");

                    } else {
                        Toast.makeText(getContext(), "Failed to load cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}