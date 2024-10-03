package mk.ukim.finki.librardf.service;

import mk.ukim.finki.librardf.models.Book;
import mk.ukim.finki.librardf.models.GENRE;
import mk.ukim.finki.librardf.repository.AuthorRepository;
import mk.ukim.finki.librardf.repository.BookRepository;
import mk.ukim.finki.librardf.requests.Book.InsertRequest;
import mk.ukim.finki.librardf.requests.Book.UpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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

    public boolean insert(InsertRequest request){
        if(authorRepository.getAuthorById(request.authorId).getId() > 0)
            return this.bookRepository.insert(request);
        return false;
    }

    public boolean update(UpdateRequest request){
        if(!Objects.equals(getBookByIsbn(request.isbn).getIsbn(), "") && authorRepository.getAuthorById(request.authorId).getId() > 0)
            return this.bookRepository.update(request);
        return false;
    }

    public Book getBookByIsbn(String isbn){
        return this.bookRepository.getBookByIsbn(isbn);
    }

    public List<Book> getAllBooks(){
        return this.bookRepository.getAllBooks();
    }

    public List<Book> getAllBooksFiltered(String filter){
        return this.bookRepository.getAllBooksFiltered(filter);
    }

    public List<Book> getAllBooksByAuthor(int authorId){
        return this.bookRepository.getAllBooksByAuthor(authorId);
    }

    public List<Book> getAllBooksByGenre(GENRE[] genres){
        return this.bookRepository.getAllBooksByGenre(genres);
    }
}