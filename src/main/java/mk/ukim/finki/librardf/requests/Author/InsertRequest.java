package mk.ukim.finki.librardf.requests.Author;

import mk.ukim.finki.librardf.models.GENRE;

import java.time.LocalDate;

public class InsertRequest {
    public String name;
    public String surname;
    public GENRE[] genres;
    public LocalDate dob;
    public LocalDate dod;
    public String nationality;
    public String imageUrl;
    public String biography;
}
