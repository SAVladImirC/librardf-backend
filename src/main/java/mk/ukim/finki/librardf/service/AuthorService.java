package mk.ukim.finki.librardf.service;

import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.repository.AuthorRepository;
import mk.ukim.finki.librardf.repository.GenreRepository;
import mk.ukim.finki.librardf.requests.Author.InsertRequest;
import mk.ukim.finki.librardf.requests.Author.UpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public AuthorService(AuthorRepository authorRepository, GenreRepository genreRepository) {
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    public List<Author> getAllAuthors(){
        return authorRepository.getAllAuthors();
    }

    public List<Author> getAllAuthorsFiltered(String filter){
        return authorRepository.getAllAuthorsFiltered(filter);
    }

    public List<Author> getAllAuthorsByGenre(int[] genres){
        return authorRepository.getAllAuthorsByGenre(genres);
    }

    public Author getAuthorById(int id){
        return authorRepository.getAuthorById(id);
    }

    public boolean insert(InsertRequest request){
        for(int genreId: request.genres){
            if(genreRepository.getGenreById(genreId).getId() <= 0)
                return false;
        }
        return authorRepository.insert(request);
    }

    public boolean update(UpdateRequest request){
        Author a = getAuthorById(request.id);
        for(int genreId: request.genres){
            if(genreRepository.getGenreById(genreId).getId() <= 0)
                return false;
        }
        if(a.getId() > 0)
            return authorRepository.update(request);
        else return false;
    }
}
