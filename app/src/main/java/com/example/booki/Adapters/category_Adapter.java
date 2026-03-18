package com.example.booki.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booki.Models.books_Model;
import com.example.booki.R;
import com.example.booki.ViewHolder.category_ViewHolder;
import com.example.booki.Each_Book;

import java.util.ArrayList;

public class category_Adapter extends RecyclerView.Adapter<category_ViewHolder> {

    ArrayList<books_Model> ar;
    Context context;

    public category_Adapter(ArrayList<books_Model> ar, Context context) {
        this.ar = ar;
        this.context = context;
    }

    @NonNull
    @Override
    public category_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_catergory, parent, false);
        return new category_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull category_ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        books_Model book = ar.get(position);

        holder.book_name.setText(book.getBook_name());
        holder.book_amt.setText(book.getBook_amt());

        Glide.with(context)
                .load(book.getBook_img())
                .into(holder.book_img);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                books_Model clickedBook = ar.get(pos);

                Intent i = new Intent(context, Each_Book.class);
                i.putExtra("book_ID", clickedBook.getBook_ID());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ar.size();
    }
}