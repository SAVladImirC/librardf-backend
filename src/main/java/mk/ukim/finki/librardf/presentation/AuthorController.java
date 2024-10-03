package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Author;
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

    @GetMapping("/{id}")
    public Author getAuthorById(@PathVariable int id){
        return authorService.getAuthorById(id);
    }

    @PostMapping("/insert")
    public boolean insert(@RequestBody Author author){
        return authorService.insert(author);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody Author author){
        return authorService.update(author);
    }
}
