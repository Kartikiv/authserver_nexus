package com.example.authserver.service;

import com.example.authserver.dao.UserRepo;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class UserDetailService implements ReactiveUserDetailsService {
    private final UserRepo userRepo;

    public UserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByUserName(username) // returns Mono<Users>
                .filter(Objects::nonNull)
                .map(user -> User.builder()
                        .username(user.getUserName())
                        .password(user.getPassword())
                        .roles("USER") // adapt as needed
                        .build());
    }

}
