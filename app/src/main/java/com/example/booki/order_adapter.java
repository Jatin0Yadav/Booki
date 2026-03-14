package com.example.booki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.Models.OrderModel;

import java.util.List;

public class order_adapter extends RecyclerView.Adapter<order_adapter.OrderViewHolder> {

    Context context;
    List<OrderModel> orderList;

    public order_adapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        OrderModel order = orderList.get(position);

        holder.tvBookName.setText(order.getBookName());
        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvPaymentId.setText("Payment ID: " + order.getPaymentId());
        holder.tvAmount.setText("₹" + order.getTotalAmount());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookName, tvOrderId, tvPaymentId, tvAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookName = itemView.findViewById(R.id.tvBookName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvPaymentId = itemView.findViewById(R.id.tvPaymentId);
            tvAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}