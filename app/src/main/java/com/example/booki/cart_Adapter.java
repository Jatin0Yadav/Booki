package com.example.booki;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.Models.cart_Model;

import java.util.ArrayList;

public class cart_Adapter extends RecyclerView.Adapter<cart_ViewHolder> {
    ArrayList<cart_Model> ar;

    public cart_Adapter(ArrayList<cart_Model> ar) {
        this.ar = ar;
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

        // ✅ plus is null — removed from layout, so never call setOnClickListener on it
        // minus = remove item from cart (optional: just leave empty for now)
        if (holder.minus != null) {
            holder.minus.setOnClickListener(v -> {
                ar.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, ar.size());
            });
        }
    }

    @Override
    public int getItemCount() {
        return ar.size();
    }
}