package com.sberbank.cms.backend;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findAllBy(Pageable pageable);

    List<Offer> findByNameLikeIgnoreCase(String nameFilter, Pageable pageable);

    long countByNameLike(String nameFilter);
}