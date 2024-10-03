package mk.ukim.finki.librardf.presentation;

import mk.ukim.finki.librardf.models.User;
import mk.ukim.finki.librardf.requests.User.LoginRequest;
import mk.ukim.finki.librardf.requests.User.RegisterRequest;
import mk.ukim.finki.librardf.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public boolean register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}