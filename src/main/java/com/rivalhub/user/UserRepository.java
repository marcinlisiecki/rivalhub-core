package com.rivalhub.user;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<UserData, Long>, PagingAndSortingRepository<UserData, Long> {

    Optional<UserData> findByEmail(String email);

    Optional<UserData> findByActivationHash(String activationHash);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM USER_DATA WHERE ACTIVATION_TIME IS NULL AND JOIN_TIME < ?1
            """, nativeQuery = true)
    void deleteInactiveUsers(LocalDateTime time);

    @Query("""
            SELECT u
            FROM Organization o
            JOIN o.userList u
            WHERE o.id = :id
            """)
    Page<UserData> findByOrganizationId(Long id, Pageable pageable);

    @Query(value = """
            SELECT USER_DATA.USER_ID, NAME, EMAIL, PROFILE_PICTURE_URL, ACTIVATION_TIME FROM USER_DATA
            JOIN ORGANIZATION_USERS ON USER_DATA.USER_ID=ORGANIZATION_USERS.USER_ID
            WHERE ORGANIZATION_ID=?1
            """, nativeQuery = true)
    Set<Tuple> getAllUsersByOrganizationId(Long id);

    @Query(value = """
            SELECT ORGANIZATION.ORGANIZATION_ID, ORGANIZATION.NAME, ORGANIZATION.IMAGE_URL, ORGANIZATION.COLOR FROM ORGANIZATION
            LEFT JOIN ORGANIZATION_USERS ON ORGANIZATION.ORGANIZATION_ID=ORGANIZATION_USERS.ORGANIZATION_ID
            LEFT JOIN USER_DATA ON ORGANIZATION_USERS.USER_ID = USER_DATA.USER_ID
            WHERE USER_DATA.USER_ID = ?1
            """, nativeQuery = true)
    Set<Tuple> getOrganizationsByUserId(Long id);

    @Query(value = """
            SELECT ORGANIZATION_ADMIN_USERS.ORGANIZATION_ORGANIZATION_ID FROM ORGANIZATION
            JOIN ORGANIZATION_ADMIN_USERS ON ORGANIZATION_ADMIN_USERS.ORGANIZATION_ORGANIZATION_ID = ORGANIZATION.ORGANIZATION_ID
            WHERE ORGANIZATION_ADMIN_USERS.ADMIN_USERS_USER_ID= ?1
            """, nativeQuery = true)
    Set<Tuple> getOrganizationIdsWhereUserIsAdmin(Long id);


    @Query(value = """
            SELECT DISTINCT O1.ORGANIZATION_ID
            FROM ORGANIZATION_USERS O1
            JOIN ORGANIZATION_USERS O2 ON O1.ORGANIZATION_ID = O2.ORGANIZATION_ID
            WHERE O1.USER_ID = ?1 AND O2.USER_ID = ?2
            """, nativeQuery = true)
    Set<Tuple> getSharedOrganizationsIds(Long requestUserId, Long userId);

    @Query(value = """
            SELECT USER_DATA.USER_ID, NAME, EMAIL, PROFILE_PICTURE_URL, ACTIVATION_TIME FROM USER_DATA
            JOIN ORGANIZATION_USERS ON USER_DATA.USER_ID=ORGANIZATION_USERS.USER_ID
            WHERE ORGANIZATION_ID=:id AND REPLACE(LOWER(NAME), ' ', '') LIKE REPLACE(LOWER(:namePhrase), ' ', '')
            """, nativeQuery = true)
    Set<Tuple> findByNamePhraseAndOrganizationId(Long id, String namePhrase);


    @Query(value = """
        SELECT u
        FROM UserData u
        JOIN u.notifications n
        WHERE u.id = ?1
            """)
    Optional<UserData> findUserWithNotifications(Long id);
}
