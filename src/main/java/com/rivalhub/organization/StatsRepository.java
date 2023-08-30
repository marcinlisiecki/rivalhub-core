package com.rivalhub.organization;

import com.rivalhub.user.UserData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends CrudRepository<Stats, Long> {

    @Query("""
                SELECT s
                FROM Organization o
                JOIN o.stats s
                JOIN o.userList u
                WHERE o.id = ?1
                AND u IN ?2
            """)
    List<Stats> findByUserAndOrganization(Long organizationId, List<UserData> users);

    @Query("""
                SELECT s
                FROM Organization o
                JOIN o.stats s
                WHERE o.id = ?1
            """)
    List<Stats> findAllByOrganizationId(Long id);
}