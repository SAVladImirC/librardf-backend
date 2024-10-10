package mk.ukim.finki.librardf.requests.Book;

public class UpdateRequest {
    public String isbn;
    public String title;
    public String publisher;
    public String shortDescription;
    public String fullDescription;
    public String language;
    public String imageLink;
    public int year;
    public int pages;
    public String country;
    public int authorId;
    public int[] genres;
}