package com.bank.bankapplication.repository;

import com.bank.bankapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
// this is called jpa Query
    boolean existsByMobileNumber(String mobileNumber);

    Optional<User> findByEmail(String email);
}
/*
User registration
      ↓
Email मिळाला
      ↓
existsByEmail()
      ↓
Database मध्ये search
      ↓
 ┌───────────────┐
 │ Email exists? │
 └───────────────┘
      ↓
 YES → true → Exception
 NO  → false → Registration continue
 */