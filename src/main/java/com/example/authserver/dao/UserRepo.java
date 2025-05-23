package com.example.authserver.dao;

import com.example.authserver.beans.Users;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface UserRepo extends ReactiveCrudRepository<Users, Long> {

    Mono<Users> findByUserName(String username);

    Flux<Users> findByUserNameIn(List<String> userNames);
}
