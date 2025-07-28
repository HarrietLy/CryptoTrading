package com.project.harriet.repository;

import com.project.harriet.model.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Long> {
    List<AggregatedPrice> findByAsset(String asset);
}
