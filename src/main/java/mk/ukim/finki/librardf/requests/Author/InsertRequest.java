package mk.ukim.finki.librardf.requests.Author;

import java.time.LocalDate;

public class InsertRequest {
    public String name;
    public String surname;
    public int[] genres;
    public LocalDate dob;
    public LocalDate dod;
    public String nationality;
    public String imageUrl;
    public String biography;
}
