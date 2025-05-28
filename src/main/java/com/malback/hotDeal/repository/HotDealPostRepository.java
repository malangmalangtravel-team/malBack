package com.malback.hotDeal.repository;

import com.malback.hotDeal.entity.HotDealPost;
import com.malback.hotDeal.enums.HotDealBoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HotDealPostRepository extends JpaRepository<HotDealPost, Long> {

    // 전체조회
    Page<HotDealPost> findByDeletedAtIsNull(Pageable pageable);
    // 타입별 조회
    Page<HotDealPost> findByDeletedAtIsNullAndType(Pageable pageable, HotDealBoardType type);

    // 이전, 다음
    Optional<HotDealPost> findFirstByIdLessThanAndDeletedAtIsNullOrderByIdDesc(Long id); // 이전 게시글
    Optional<HotDealPost> findFirstByIdGreaterThanAndDeletedAtIsNullOrderByIdAsc(Long id); // 다음 게시글
    // 조회수 증가
    @Modifying
    @Query("UPDATE HotDealPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 사이트맵 추가
    List<HotDealPost> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}