package com.example.booki;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class cart_ViewHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    TextView itemPrice, itemQuantity;
    ImageView minus;

    // ✅ plus removed — no longer needed in the UI
    // keeping the field as null-safe so cart_Adapter doesn't crash
    ImageView plus = null;

    public cart_ViewHolder(@NonNull View itemView) {
        super(itemView);
        itemName     = itemView.findViewById(R.id.order_item_name);
        itemPrice    = itemView.findViewById(R.id.order_itemAmt);
        itemQuantity = itemView.findViewById(R.id.order_item_quantity);
        minus        = itemView.findViewById(R.id.btn_minus);
        // plus is intentionally not bound to any view
    }
}