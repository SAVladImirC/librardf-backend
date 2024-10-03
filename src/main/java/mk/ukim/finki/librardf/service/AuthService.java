package mk.ukim.finki.librardf.service;

import mk.ukim.finki.librardf.models.User;
import mk.ukim.finki.librardf.repository.AuthRepository;
import mk.ukim.finki.librardf.requests.User.LoginRequest;
import mk.ukim.finki.librardf.requests.User.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public boolean register(RegisterRequest request){
        return authRepository.register(request);
    }

    public User login(LoginRequest request){
        return authRepository.login(request);
    }
}