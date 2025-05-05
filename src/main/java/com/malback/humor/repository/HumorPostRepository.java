package com.malback.humor.repository;

import com.malback.humor.entity.HumorPost;
import com.malback.humor.enums.HumorBoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HumorPostRepository extends JpaRepository<HumorPost, Long> {
    Page<HumorPost> findByDeletedAtIsNullAndType(Pageable pageable, HumorBoardType type);
    Optional<HumorPost> findFirstByIdLessThanOrderByIdDesc(Long id); // 이전 게시글
    Optional<HumorPost> findFirstByIdGreaterThanOrderByIdAsc(Long id); // 다음 게시글
    // 조회수 증가
    @Modifying
    @Query("UPDATE HumorPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 사이트맵 추가
    List<HumorPost> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}