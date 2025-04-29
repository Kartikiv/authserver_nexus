package com.example.authserver.controller;

import com.example.authserver.beans.AuthBean;
import com.example.authserver.beans.Users;
import com.example.authserver.dao.UserRepo;
import com.example.authserver.service.JwtService;
import com.example.authserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {
    final UserRepo userRepo;
    final UserService userService;
    final JwtService jwtService;

    public AuthController(UserRepo userRepo, UserService userService, JwtService jwtService) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthBean>> login(@RequestBody Users user) {
        return userService.verify(user)
                .map(token -> {
                    if ("fail".equals(token)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<AuthBean>build();
                    }
                    return ResponseEntity.ok(new AuthBean(user.getUserName(), token));
                });
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<Users>> signup(@RequestBody Users user) {
        return userService.register(user)
                .map(savedUser -> new ResponseEntity<>(savedUser, HttpStatus.CREATED))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()));
    }

    @GetMapping("/hi")
    public Mono<String> hi() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> "hi + " + ctx.getAuthentication().getName());
    }
}
