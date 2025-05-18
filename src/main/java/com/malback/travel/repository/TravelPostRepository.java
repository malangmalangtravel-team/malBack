package com.malback.travel.repository;

import com.malback.travel.entity.TravelPost;
import com.malback.travel.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TravelPostRepository extends JpaRepository<TravelPost, Long> {

    @EntityGraph(attributePaths = {"country"}) // country를 JOIN해서 한 번에 가져옴
    List<TravelPost> findAll();

    // 특정 나라 게시판의 게시글 목록 조회 (정렬 포함)
    Page<TravelPost> findByCountry_CountryNameAndDeletedAtIsNullOrderByIdDesc(@Param("countryName") String countryName, Pageable pageable);


    // 특정 나라 게시판의 게시글 타입별 목록 조회 (정렬 포함)
    Page<TravelPost> findByCountry_CountryNameAndTypeAndDeletedAtIsNullOrderByIdDesc(@Param("countryName") String countryName, @Param("type") BoardType type, Pageable pageable);

    Optional<TravelPost> findFirstByIdLessThanOrderByIdDesc(Long id); // 이전 게시글
    Optional<TravelPost> findFirstByIdGreaterThanOrderByIdAsc(Long id); // 다음 게시글

    // 조회수 증가
    @Modifying
    @Query("UPDATE TravelPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 사이트맵 추가
    List<TravelPost> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}

