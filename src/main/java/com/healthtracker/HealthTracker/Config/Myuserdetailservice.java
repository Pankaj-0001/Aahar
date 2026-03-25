package com.healthtracker.HealthTracker.Config;


import com.healthtracker.HealthTracker.Entity.User;
import com.healthtracker.HealthTracker.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class Myuserdetailservice implements UserDetailsService {


    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userinfo = repo.findByemail(username);
        if (userinfo.isPresent()){
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userinfo.get().getName())
                    .password(userinfo.get().getPassword())
                    .roles(String.valueOf(Collections.singleton(new SimpleGrantedAuthority("USER"))))
                    .build();
        }else {
            throw new UsernameNotFoundException("USER NOT FOUND");
        }
    }
}
