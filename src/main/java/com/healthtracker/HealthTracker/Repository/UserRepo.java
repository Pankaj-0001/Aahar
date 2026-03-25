package com.healthtracker.HealthTracker.Repository;

import com.healthtracker.HealthTracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByemail(String email);
}
