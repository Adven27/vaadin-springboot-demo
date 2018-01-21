package com.sberbank.cms.backend.domain.services;

import com.sberbank.cms.backend.domain.model.ContentKind;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentKindRepository extends JpaRepository<ContentKind, Long> {
    List<ContentKind> findByNameLikeIgnoreCase(String nameFilter);
    ContentKind findByStrId(String strId);
}