package mk.ukim.finki.librardf.service;

import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.repository.AuthorRepository;
import mk.ukim.finki.librardf.requests.Author.InsertRequest;
import mk.ukim.finki.librardf.requests.Author.UpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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

    public List<Author> getAllAuthorsFiltered(String filter){
        return authorRepository.getAllAuthorsFiltered(filter);
    }

    public Author getAuthorById(int id){
        return authorRepository.getAuthorById(id);
    }

    public boolean insert(InsertRequest request){
        return authorRepository.insert(request);
    }

    public boolean update(UpdateRequest request){
        Author a = getAuthorById(request.id);
        if(a.getId() > 0)
            return authorRepository.update(request);
        else return false;
    }
}
