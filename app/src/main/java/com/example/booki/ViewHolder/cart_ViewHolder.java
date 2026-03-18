package com.example.booki.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.R;

public class cart_ViewHolder extends RecyclerView.ViewHolder {

    public TextView itemName, itemPrice, itemQuantity;
    public ImageView minus;

    public cart_ViewHolder(@NonNull View itemView) {
        super(itemView);
        itemName     = itemView.findViewById(R.id.order_item_name);
        itemPrice    = itemView.findViewById(R.id.order_itemAmt);
        itemQuantity = itemView.findViewById(R.id.order_item_quantity);
        minus        = itemView.findViewById(R.id.btn_minus);
    }
}