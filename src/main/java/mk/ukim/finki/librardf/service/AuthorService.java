package mk.ukim.finki.librardf.service;

import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors(){
        return authorRepository.getAllAuthors();
    }

    public Author getAuthorById(int id){
        return authorRepository.getAuthorById(id);
    }

    public boolean insert(Author author){
        return authorRepository.insert(author);
    }

    public boolean update(Author author){
        Author a = getAuthorById(author.getId());
        if(a.getId() > 0)
            return authorRepository.update(author);
        else return false;
    }
}
