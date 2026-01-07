package com.aurumx.repository;

import com.aurumx.entity.CesUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CesUserRepository extends JpaRepository<CesUser, Long> {
    Optional<CesUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
