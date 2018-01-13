package com.sberbank.cms.backend.content;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    @Query(value = "SELECT * FROM campaign c WHERE c.content_kind_str_id=?1", nativeQuery = true)
    List<Campaign> findByContentKind(String kindStrId);

    @Query(value = "SELECT * FROM campaign c WHERE c.content_kind_str_id=?1 AND c.name LIKE %?2% --#pageable\n", nativeQuery = true)
    List<Campaign> findByContentKindAndNameLike(String kindStrId, String name, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM campaign c WHERE c.content_kind_str_id=?1 AND c.name LIKE %?2%", nativeQuery = true)
    Integer countByContentKindAndNameLike(String kindStrId, String name);
}