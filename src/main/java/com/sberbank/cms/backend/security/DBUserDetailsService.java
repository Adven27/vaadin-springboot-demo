package com.sberbank.cms.backend.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DBUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public DBUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo u = repo.findByLogin(username);
        if (u == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        }
        return User.withUsername(u.getLogin()).password(u.getPassword()).roles(u.getRole()).build();
    }
}