package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.models.Book;
import mk.ukim.finki.librardf.models.GENRE;
import mk.ukim.finki.librardf.requests.Book.InsertRequest;
import mk.ukim.finki.librardf.requests.Book.UpdateRequest;
import mk.ukim.finki.librardf.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping()
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/filter/{filter}")
    public List<Book> getAllBooksFiltered(@PathVariable String filter){
        return this.bookService.getAllBooksFiltered(filter);
    }

    @GetMapping("/by-isbn/{isbn}")
    public Book getBookByIsbn(@PathVariable String isbn){
        return bookService.getBookByIsbn(isbn);
    }

    @GetMapping("/by-author/{authorId}")
    public List<Book> getAllBooksByAuthor(@PathVariable int authorId){
        return bookService.getAllBooksByAuthor(authorId);
    }

    @GetMapping("/by-genres/{genres}")
    public List<Book> getAllBooksByGenre(@PathVariable GENRE[] genres){
        return bookService.getAllBooksByGenre(genres);
    }

    @PostMapping("/insert")
    public boolean insert(@RequestBody InsertRequest request){
        return this.bookService.insert(request);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody UpdateRequest request){
        return this.bookService.update(request);
    }
}