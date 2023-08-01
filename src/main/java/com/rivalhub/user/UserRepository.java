package com.rivalhub.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserData, Long> {

    Optional<UserData> findByEmail(String email);

}
