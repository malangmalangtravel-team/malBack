package com.malback.travel.repository;

import com.malback.travel.entity.TravelBoard;
import com.malback.travel.entity.TravelPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelBoardRepository extends JpaRepository<TravelBoard, Long> {
    List<TravelBoard> findByCountry_CountryName(String countryName);
}
