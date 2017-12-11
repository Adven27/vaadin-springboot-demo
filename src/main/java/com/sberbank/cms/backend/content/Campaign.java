package com.sberbank.cms.backend.content;

import com.sberbank.cms.backend.utils.JsonbUserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.GenerationType.AUTO;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDef(name = "JsonbUserType", typeClass = JsonbUserType.class)
public class Campaign implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = AUTO)
    private long id;

    @NotNull
    @Type(type = "JsonbUserType")
    Map<String, String> data = new HashMap<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "content_kind_str_id")
    private ContentKind contentKind;
}