package com.rivalhub.user;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<UserData, Long>, PagingAndSortingRepository<UserData, Long> {

    Optional<UserData> findByEmail(String email);
    Optional<UserData> findByActivationHash(String activationHash);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM USER_DATA WHERE ACTIVATION_TIME IS NULL AND JOIN_TIME < ?1", nativeQuery = true)
    void deleteInactiveUsers(LocalDateTime time);


    @Query(value = "SELECT USER_DATA.USER_ID, NAME, EMAIL, PROFILE_PICTURE_URL, ACTIVATION_TIME FROM USER_DATA \n" +
            "   JOIN ORGANIZATION_USERS ON USER_DATA.USER_ID=ORGANIZATION_USERS.USER_ID\n" +
            "   WHERE ORGANIZATION_ID=?1", nativeQuery = true)
    Set<Tuple> getAllUsersByOrganizationId(Long id);

    @Query(value = "SELECT USER_DATA.USER_ID, NAME, EMAIL, PROFILE_PICTURE_URL, ACTIVATION_TIME FROM USER_DATA \n" +
            "   JOIN ORGANIZATION_USERS ON USER_DATA.USER_ID=ORGANIZATION_USERS.USER_ID \n" +
            "   WHERE ORGANIZATION_ID=:id AND LOWER(NAME) LIKE %:namePhrase%", nativeQuery = true)
    Set<Tuple> findByNamePhraseAndOrganizationId(Long id, String namePhrase);
}
