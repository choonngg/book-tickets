package com.project.ticket.domain.auth.repository;

import com.project.ticket.domain.auth.entity.Login;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {
    boolean existsByEmail(String email);

    Optional<Login> findByEmail(String email);
}
