package com.aahar.Aahar.Repository;

import com.aahar.Aahar.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByemail(String email);
    Boolean existsByEmail(String email);
}
