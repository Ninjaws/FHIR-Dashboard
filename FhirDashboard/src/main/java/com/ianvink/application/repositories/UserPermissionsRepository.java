package com.ianvink.application.repositories;

import com.ianvink.application.entities.UserPermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPermissionsRepository extends JpaRepository<UserPermissionsEntity, Long> {

    @Query("SELECT p FROM UserPermissionsEntity p JOIN FETCH p.user WHERE p.userId = :userId")
    Optional<UserPermissionsEntity> findByUserIdWithUser(@Param("userId") Long userId);

    @Query("SELECT p FROM UserPermissionsEntity p JOIN FETCH p.user")
    List<UserPermissionsEntity> findAllWithUser();
}
