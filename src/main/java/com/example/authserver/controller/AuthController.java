package com.example.authserver.controller;

import com.example.authserver.beans.AuthBean;
import com.example.authserver.beans.Users;
import com.example.authserver.dao.UserRepo;
import com.example.authserver.service.JwtService;
import com.example.authserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


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

    @PostMapping("/getRegisteredUsers")
    public Mono<ResponseEntity<Users>> getUsers(@RequestBody Users us) {
        return userRepo.findByUserName(us.getUserName())
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR)));

    }

    @PostMapping("/filterRegisteredUsers")
    public Mono<ResponseEntity<List<String>>> filterRegisteredUsers(@RequestBody List<String> phoneNumbers) {
        return userRepo.findByUserNameIn(phoneNumbers)
                .collectList()
                .map(users -> {
                    List<String> matched = users.stream()
                            .map(Users::getUserName)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(matched);
                });
    }






}
