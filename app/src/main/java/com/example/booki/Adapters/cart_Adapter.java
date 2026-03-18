package com.example.booki.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.Models.cart_Model;
import com.example.booki.R;
import com.example.booki.ViewHolder.cart_ViewHolder;

import java.util.ArrayList;

public class cart_Adapter extends RecyclerView.Adapter<cart_ViewHolder> {

    ArrayList<cart_Model> ar;

    // ✅ Callback to notify the Activity when list changes
    public interface OnCartChangedListener {
        void onCartChanged(int newSize);
    }

    private OnCartChangedListener listener;

    public cart_Adapter(ArrayList<cart_Model> ar, OnCartChangedListener listener) {
        this.ar = ar;
        this.listener = listener;
    }

    @NonNull
    @Override
    public cart_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_cart, parent, false);
        return new cart_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cart_ViewHolder holder, int position) {
        holder.itemName.setText(ar.get(position).getBook_name());
        holder.itemPrice.setText("₹ " + ar.get(position).getBook_amt());
        holder.itemQuantity.setText(ar.get(position).getBook_category());

        if (holder.minus != null) {
            holder.minus.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition(); // ✅ always use getAdapterPosition()
                if (pos != RecyclerView.NO_POSITION) {
                    ar.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, ar.size());
                    listener.onCartChanged(ar.size()); // ✅ notify Activity
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ar.size();
    }
}