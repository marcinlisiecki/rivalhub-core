package com.rivalhub.user;

import org.apache.catalina.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.awt.print.Pageable;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserData, Long>, PagingAndSortingRepository<UserData, Long> {

    Optional<UserData> findByEmail(String email);
}
