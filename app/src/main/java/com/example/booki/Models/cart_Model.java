package com.example.booki.Models;

import com.google.firebase.firestore.PropertyName;

public class cart_Model {

    String cart_Id;
    String book_Id;
    String book_name;
    String book_amt;    // String because Firestore stores price as String
    String book_category;

    // ✅ REQUIRED by Firestore — without this toObject() crashes
    public cart_Model() {}

    public cart_Model(String cart_Id, String book_Id, String book_name, String book_amt, String book_category) {
        this.cart_Id = cart_Id;
        this.book_Id = book_Id;
        this.book_name = book_name;
        this.book_amt = book_amt;
        this.book_category = book_category;
    }

    // ✅ @PropertyName maps Firestore field names to your Java variable names
    // Firestore saves: cartId, bookId, title, price, category

    @PropertyName("cartId")
    public String getCart_Id() { return cart_Id; }
    @PropertyName("cartId")
    public void setCart_Id(String cart_Id) { this.cart_Id = cart_Id; }

    @PropertyName("bookId")
    public String getBook_Id() { return book_Id; }
    @PropertyName("bookId")
    public void setBook_Id(String book_Id) { this.book_Id = book_Id; }

    @PropertyName("title")
    public String getBook_name() { return book_name; }
    @PropertyName("title")
    public void setBook_name(String book_name) { this.book_name = book_name; }

    @PropertyName("price")
    public String getBook_amt() { return book_amt; }
    @PropertyName("price")
    public void setBook_amt(String book_amt) { this.book_amt = book_amt; }

    @PropertyName("category")
    public String getBook_category() { return book_category; }
    @PropertyName("category")
    public void setBook_category(String book_category) { this.book_category = book_category; }
}