package com.example.authserver.service;

import com.example.authserver.beans.Users;
import com.example.authserver.dao.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthManager;

    @Autowired
    private UserRepo userRepo; // NOTE: Should be ReactiveCrudRepository<Users, ID>

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Mono<Users> register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user); // userRepo should be reactive
    }

    public Mono<String> verify(Users user) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        return reactiveAuthManager.authenticate(authToken)
                .map(auth -> jwtService.generateToken(user.getUserName()))
                .switchIfEmpty(Mono.just("fail"));
    }
}