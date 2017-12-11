package com.sberbank.cms.backend.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    @Query(value = "SELECT * FROM campaign c WHERE c.content_kind_str_id=?1", nativeQuery = true)
    List<Campaign> findByContentKind(String kindStrId);

    /*@Query(value = "SELECT * FROM campaign b WHERE b.author->>'lastName' ILIKE CONCAT('%', ?1, '%')", nativeQuery = true)
    List<Campaign> findByAuthorLastName(String lastName);

    @Query(value = "SELECT * FROM campaign b WHERE b.author->>'firstName' ILIKE CONCAT('%', ?1, '%') AND  b.author->>'lastName' ILIKE CONCAT('%', ?2, '%')", nativeQuery = true)
    List<Campaign> findByAuthorFirstNameAndLastName(String firstName, String lastName);*/
}