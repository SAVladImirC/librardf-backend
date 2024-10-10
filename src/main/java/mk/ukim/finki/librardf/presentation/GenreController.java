package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Genre;
import mk.ukim.finki.librardf.repository.GenreRepository;
import mk.ukim.finki.librardf.requests.Genre.UpdateRequest;
import mk.ukim.finki.librardf.service.GenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genre")
public class GenreController {
    private final GenreService genreService;
    private final GenreRepository genreRepository;

    public GenreController(GenreService genreService, GenreRepository genreRepository) {
        this.genreService = genreService;
        this.genreRepository = genreRepository;
    }

    @GetMapping("all/{names}")
    public boolean all(@PathVariable String[] names){
        return genreRepository.insertAll(names);
    }

    @GetMapping()
    public List<Genre> getAllGenres(){
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id){
        return genreService.getGenreById(id);
    }

    @PostMapping("/insert/{name}")
    public boolean insert(@PathVariable String name){
        if(!name.isBlank())
            return genreService.insert(name);
        return false;
    }

    @PostMapping("/update")
    public boolean update(@RequestBody UpdateRequest request){
        if(request.id > 0 && !request.name.isBlank())
            return genreService.update(request);
        return false;
    }
}