package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController("book")
public class BookController {

    @GetMapping()
    public List<Book> getAllBooks(){
        return new ArrayList<>();
    }

    @GetMapping("/{bookId}")
    public Book getBookById(@PathVariable int bookId){
        return null;
    }

    @GetMapping("/{authorId}")
    public List<Book> getAllBooksByAuthor(@PathVariable int authorId){
        return new ArrayList<>();
    }

    @GetMapping("/{genres}")
    public List<Book> getAllBooksByGenre(@PathVariable String genres){
        return new ArrayList<>();
    }

    @PostMapping("/insert")
    public Book insert(){
        return null;
    }

    @PostMapping("/update")
    public Book update(){
        return null;
    }
}