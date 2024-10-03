package mk.ukim.finki.librardf.requests.Book;

import mk.ukim.finki.librardf.models.GENRE;

public class InsertRequest {
    public String title;
    public String publisher;
    public String shortDescription;
    public String fullDescription;
    public String language;
    public String imageLink;
    public String link;
    public int year;
    public int pages;
    public String country;
    public int authorId;
    public GENRE[] genres;
}
