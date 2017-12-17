package com.sberbank.cms.backend.content;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static javax.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@Entity
public class Place implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @NotNull(message = "Name is required")
    @Size(min = 1, max = 40)
    @Column(unique = true)
    private String name = "new place";

    public Place(String name){
        this.name = name;
    }
}