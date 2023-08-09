package com.rivalhub.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserData, Long>, PagingAndSortingRepository<UserData, Long> {

    Optional<UserData> findByEmail(String email);
    Optional<UserData> findByActivationHash(String activationHash);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM USER_DATA WHERE ACTIVATION_TIME IS NULL AND JOIN_TIME < ?1", nativeQuery = true)
    void deleteInactiveUsers(LocalDateTime time);
}
