package com.example.booki;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class cart_ViewHolder extends RecyclerView.ViewHolder {

    TextView itemName, itemPrice, itemQuantity;
    ImageView minus;

    public cart_ViewHolder(@NonNull View itemView) {
        super(itemView);
        itemName     = itemView.findViewById(R.id.order_item_name);
        itemPrice    = itemView.findViewById(R.id.order_itemAmt);
        itemQuantity = itemView.findViewById(R.id.order_item_quantity);
        minus        = itemView.findViewById(R.id.btn_minus);
    }
}