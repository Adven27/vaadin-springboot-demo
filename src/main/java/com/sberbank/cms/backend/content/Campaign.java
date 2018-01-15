package com.sberbank.cms.backend.content;

import com.sberbank.cms.backend.utils.JsonbUserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDef(name = "JsonbUserType", typeClass = JsonbUserType.class)
public class Campaign implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @NotBlank
    @NotEmpty
    @Length(min = 1)
    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull
    @Type(type = "JsonbUserType")
    Map<String, Object> data = new HashMap<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "kind")
    private ContentKind kind;
}