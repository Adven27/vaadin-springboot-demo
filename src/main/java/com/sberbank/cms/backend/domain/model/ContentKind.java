package com.sberbank.cms.backend.domain.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

@Builder
@Data
@ToString(exclude = "fields")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContentKind implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Size(min = 3, max = 255)
    @Column(unique = true)
    String strId;

    @NotNull(message = "Name is required")
    @Size(min = 3, max = 40, message = "name must be longer than 3 and less than 40 characters")
    private String name;

    @Temporal(TIMESTAMP)
    private Date creationDate = new Date();

    @OrderBy("ord")
    @OneToMany(mappedBy = "kind", cascade = ALL, orphanRemoval = true, fetch = EAGER)
    private List<ContentField> fields = new ArrayList<>();
}