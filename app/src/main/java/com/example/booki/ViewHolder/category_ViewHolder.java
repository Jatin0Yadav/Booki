package com.example.booki.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.R;


public class category_ViewHolder extends RecyclerView.ViewHolder {
    public TextView book_name;
    public TextView book_amt;
    public ImageView book_img;

    public category_ViewHolder(@NonNull View itemView) {
        super(itemView);

        book_name = itemView.findViewById(R.id.singlerowbookName);
        book_amt = itemView.findViewById(R.id.singlerowbookAmt);
        book_img = itemView.findViewById(R.id.singlerowbookImage);

    }

}
