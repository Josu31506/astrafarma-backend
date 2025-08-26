package com.example.astrafarma.User.repository;

import com.example.astrafarma.User.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "categoryStats")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "categoryStats")
    Optional<User> findByEmail(String email);
}