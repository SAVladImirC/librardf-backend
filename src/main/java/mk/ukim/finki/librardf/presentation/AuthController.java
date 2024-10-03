package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.Author;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController("auth")
public class AuthController {

    @GetMapping()
    public List<Author> getAllAuthors(){
        return new ArrayList<>();
    }

    @GetMapping("/{authorId}")
    public Author getAuthorById(@PathVariable int authorId){
        return null;
    }

    @PostMapping("/insert")
    public Author insert(){
        return null;
    }

    @PostMapping("/update")
    public Author update(){
        return null;
    }
}