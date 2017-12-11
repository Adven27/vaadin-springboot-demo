package com.sberbank.cms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DBUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    @Autowired
    public DBUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo u = repo.findByLogin(username);
        if (u != null) {
            return User.withUsername(u.getLogin()).
                    password(u.getPassword()).
                    roles(u.getRole()).
                    build();
        }
        throw new UsernameNotFoundException("No u present with username: " + username);
    }
}