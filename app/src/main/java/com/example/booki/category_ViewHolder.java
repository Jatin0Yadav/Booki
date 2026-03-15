package com.example.booki;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class category_ViewHolder extends RecyclerView.ViewHolder {
    TextView book_name;
    TextView book_amt;
    ImageView book_img;

    public category_ViewHolder(@NonNull View itemView) {
        super(itemView);

        book_name = itemView.findViewById(R.id.singlerowbookName);
        book_amt = itemView.findViewById(R.id.singlerowbookAmt);
        book_img = itemView.findViewById(R.id.singlerowbookImage);

    }

}
