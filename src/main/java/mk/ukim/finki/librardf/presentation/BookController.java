package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Book;
import mk.ukim.finki.librardf.models.GENRE;
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
    public boolean insert(@RequestBody Book book){
        return this.bookService.insert(book);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody Book book){
        return this.bookService.update(book);
    }
}