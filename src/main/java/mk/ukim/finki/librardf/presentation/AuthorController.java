package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Author;
import mk.ukim.finki.librardf.requests.Author.InsertRequest;
import mk.ukim.finki.librardf.requests.Author.UpdateRequest;
import mk.ukim.finki.librardf.service.AuthorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping()
    public List<Author> getAllBooks(){
        return authorService.getAllAuthors();
    }

    @GetMapping("/filter/{filter}")
    public List<Author> getAllAuthorsFiltered(@PathVariable String filter){
        return this.authorService.getAllAuthorsFiltered(filter);
    }

    @GetMapping("/{id}")
    public Author getAuthorById(@PathVariable int id){
        return authorService.getAuthorById(id);
    }

    @PostMapping("/insert")
    public boolean insert(@RequestBody InsertRequest request){
        return authorService.insert(request);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody UpdateRequest request){
        return authorService.update(request);
    }
}
