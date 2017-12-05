package com.sberbank.cms.backend;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity(name = "UserInfo")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 4, max = 255)
    private String password;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String role;

    private boolean locked = false;

    public User(String email, String name, String password, String role) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(name);
        Objects.requireNonNull(password);
        Objects.requireNonNull(role);

        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }
}