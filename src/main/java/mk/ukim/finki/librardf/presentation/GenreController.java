package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Genre;
import mk.ukim.finki.librardf.requests.Genre.UpdateRequest;
import mk.ukim.finki.librardf.service.GenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
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