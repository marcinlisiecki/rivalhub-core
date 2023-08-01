package com.rivalhub.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserData, Long> {
    UserData findByEmail(String email);
}
