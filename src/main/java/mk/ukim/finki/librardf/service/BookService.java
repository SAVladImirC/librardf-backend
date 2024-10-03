package mk.ukim.finki.librardf.service;

import mk.ukim.finki.librardf.models.Book;
import mk.ukim.finki.librardf.models.GENRE;
import mk.ukim.finki.librardf.repository.AuthorRepository;
import mk.ukim.finki.librardf.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public boolean insert(Book book){
        if(authorRepository.getAuthorById(book.getAuthor().getId()).getId() > 0)
            return this.bookRepository.insert(book);
        return false;
    }

    public boolean update(Book book){
        if(!Objects.equals(getBookByIsbn(book.getIsbn()).getIsbn(), "") && authorRepository.getAuthorById(book.getAuthor().getId()).getId() > 0)
            return this.bookRepository.update(book);
        return false;
    }

    public Book getBookByIsbn(String isbn){
        return this.bookRepository.getBookByIsbn(isbn);
    }

    public List<Book> getAllBooks(){
        return this.bookRepository.getAllBooks();
    }

    public List<Book> getAllBooksByAuthor(int authorId){
        return this.bookRepository.getAllBooksByAuthor(authorId);
    }

    public List<Book> getAllBooksByGenre(GENRE[] genres){
        return this.bookRepository.getAllBooksByGenre(genres);
    }
}