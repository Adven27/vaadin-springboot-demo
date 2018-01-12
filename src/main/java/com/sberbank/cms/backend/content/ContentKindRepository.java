package com.sberbank.cms.backend.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentKindRepository extends JpaRepository<ContentKind, Long> {
    List<ContentKind> findByNameLikeIgnoreCase(String nameFilter);
    ContentKind findByStrId(String strId);
}