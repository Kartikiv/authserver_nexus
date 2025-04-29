package com.example.authserver.dao;

import com.example.authserver.beans.Users;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


public interface UserRepo extends ReactiveCrudRepository<Users, Long> {

    Mono<Users> findByUserName(String username);
}
