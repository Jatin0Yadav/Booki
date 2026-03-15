package com.example.booki.Models;

import com.google.firebase.firestore.PropertyName;

public class books_Model {

    private String user_ID;
    private String book_ID;
    private String book_name;
    private String book_description;
    private String book_category;
    private String book_subject;
    private String book_edition;
    private String book_author;
    private Boolean isNegotiable;
    private String book_amt;
    private String book_img; // ✅ String for Firebase Storage URL

    public books_Model() {}

    public books_Model(String book_ID, String user_ID, String book_name, String book_category,
                       String book_subject, String book_author, String book_edition,
                       String book_amt, Boolean isNegotiable, String book_description, String book_img) {
        this.book_ID = book_ID;
        this.user_ID = user_ID;
        this.book_name = book_name;
        this.book_description = book_description;
        this.book_category = book_category;
        this.book_subject = book_subject;
        this.book_edition = book_edition;
        this.book_author = book_author;
        this.isNegotiable = isNegotiable;
        this.book_amt = book_amt;
        this.book_img = book_img; // ✅ added
    }

    @PropertyName("bookId")
    public String getBook_ID() { return book_ID; }
    @PropertyName("bookId")
    public void setBook_ID(String book_ID) { this.book_ID = book_ID; }

    @PropertyName("sellerId")
    public String getUser_ID() { return user_ID; }
    @PropertyName("sellerId")
    public void setUser_ID(String user_ID) { this.user_ID = user_ID; }

    @PropertyName("title")
    public String getBook_name() { return book_name; }
    @PropertyName("title")
    public void setBook_name(String book_name) { this.book_name = book_name; }

    @PropertyName("description")
    public String getBook_description() { return book_description; }
    @PropertyName("description")
    public void setBook_description(String book_description) { this.book_description = book_description; }

    @PropertyName("category")
    public String getBook_category() { return book_category; }
    @PropertyName("category")
    public void setBook_category(String book_category) { this.book_category = book_category; }

    @PropertyName("subject")
    public String getBook_subject() { return book_subject; }
    @PropertyName("subject")
    public void setBook_subject(String book_subject) { this.book_subject = book_subject; }

    @PropertyName("edition")
    public String getBook_edition() { return book_edition; }
    @PropertyName("edition")
    public void setBook_edition(String book_edition) { this.book_edition = book_edition; }

    @PropertyName("author")
    public String getBook_author() { return book_author; }
    @PropertyName("author")
    public void setBook_author(String book_author) { this.book_author = book_author; }

    @PropertyName("isNegotiable")
    public Boolean getNegotiable() { return isNegotiable; }
    @PropertyName("isNegotiable")
    public void setNegotiable(Boolean negotiable) { isNegotiable = negotiable; }

    @PropertyName("price")
    public String getBook_amt() { return book_amt; }
    @PropertyName("price")
    public void setBook_amt(String book_amt) { this.book_amt = book_amt; }

    // ✅ Image getter & setter — maps to "Image" field in Firestore
    @PropertyName("imageurl")
    public String getBook_img() { return book_img; }
    @PropertyName("imageurl")
    public void setBook_img(String book_img) { this.book_img = book_img; }
}