package com.sberbank.cms.backend;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(unique = true)
    private String login;

    @NotNull
    @Size(min = 1, max = 255)
    private String password;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String role;

    private boolean locked = false;

    public UserInfo(String login, String name, String password, String role) {
        Objects.requireNonNull(login);
        Objects.requireNonNull(name);
        Objects.requireNonNull(password);
        Objects.requireNonNull(role);

        this.login = login;
        this.name = name;
        this.password = password;
        this.role = role;
    }
}