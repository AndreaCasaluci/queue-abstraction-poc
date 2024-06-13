package org.orbyta.queueabstractionpoc.model;

import java.io.Serializable;
import java.util.UUID;


public class Book implements Serializable {
    private String id;
    private String title;
    private String author;

    // Constructor
    public Book(String title, String author) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}

