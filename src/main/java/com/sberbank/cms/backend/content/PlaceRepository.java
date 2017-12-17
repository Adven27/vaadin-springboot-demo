package com.sberbank.cms.backend.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query(value = "SELECT name FROM place", nativeQuery = true)
    List<String> findAllNames();
}