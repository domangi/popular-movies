package com.domenicoangilletta.popularmovies.models;

/**
 * Created by domangi on 12/02/17.
 */

public class Review {
    private String author;
    private String content;

    public Review(String author, String content){
        this.author = author;
        this.content = content;
    }

    public String getAuthor(){
        return author;
    }

    public String getContent(){
        return content;
    }
}
