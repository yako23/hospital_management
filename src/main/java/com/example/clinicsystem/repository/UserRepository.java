package com.example.clinicsystem.repository;

import com.example.clinicsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    // query to fetch users with status "ΕΚΚΡΕΜΕΙ"
    @Query("SELECT u FROM User u WHERE u.status = 'ΕΚΚΡΕΜΕΙ'")
    List<User> findPendingUsers();
}
