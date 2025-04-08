package com.malback.travel.repository;

import com.malback.travel.entity.TravelPost;
import com.malback.travel.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TravelPostRepository extends JpaRepository<TravelPost, Long> {

    @EntityGraph(attributePaths = {"country"}) // country를 JOIN해서 한 번에 가져옴
    List<TravelPost> findAll();

    // 특정 나라 게시판의 게시글 목록 조회 (정렬 포함)
    Page<TravelPost> findByCountry_CountryNameOrderByIdDesc(@Param("countryName") String countryName, Pageable pageable);


    // 특정 나라 게시판의 게시글 타입별 목록 조회 (정렬 포함)
    Page<TravelPost> findByCountry_CountryNameAndTypeOrderByIdDesc(@Param("countryName") String countryName, @Param("type") BoardType type, Pageable pageable);

}

