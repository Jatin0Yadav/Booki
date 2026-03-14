package com.example.booki;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booki.Models.books_Model;

import java.util.ArrayList;

// extends RecViewAdapter of template category_viewholder.
// this adapter takes the arraylist
public class category_Adapter extends RecyclerView.Adapter<category_ViewHolder> {     // note the generic should be of viewHolder that we created.

    ArrayList<books_Model> ar;
    Context context;        // required for intent;
    public category_Adapter(ArrayList<books_Model> ar, Context context) {
        this.ar = ar;
        this.context = context;
    }

    @NonNull
    @Override
    public category_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater infalter = LayoutInflater.from(parent.getContext());
        View view = infalter.inflate(R.layout.row_catergory, parent, false);       // note: 3 args.
        return new category_ViewHolder(view);
    }

    // setOnClickListener is written in onBindViewHolder.
    @Override
    public void onBindViewHolder(@NonNull category_ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        books_Model book = ar.get(position);

        holder.book_name.setText(book.getBook_name());
        holder.book_amt.setText(book.getBook_amt());

        holder.itemView.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();          // we use it cuz recycler view keeps on changing its position.

            if (pos != RecyclerView.NO_POSITION) {
                books_Model clickedBook = ar.get(pos);

                Intent i = new Intent(context, each_Book.class);
                i.putExtra("book_ID", clickedBook.getBook_ID());
                context.startActivity(i);
            }
        });


//        If your list is static (like category books), you can safely use:

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, each_Book.class);
//                i.putExtra("book_ID", book.getBook_ID());
//                context.startActivity(i);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return ar.size();
    }
}
