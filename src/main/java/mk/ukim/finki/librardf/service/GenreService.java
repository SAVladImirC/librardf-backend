package mk.ukim.finki.librardf.service;

import mk.ukim.finki.librardf.models.Genre;
import mk.ukim.finki.librardf.repository.GenreRepository;
import mk.ukim.finki.librardf.requests.Genre.UpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres(){
        return genreRepository.getAllGenres();
    }

    public Genre getGenreById(int id){
        return genreRepository.getGenreById(id);
    }

    public boolean insert(String name){
        if(!name.isBlank())
            return genreRepository.insert(name);
        return false;
    }

    public boolean update(UpdateRequest request){
        if(request.id > 0 && !request.name.isBlank())
            return genreRepository.update(request);
        return false;
    }
}
