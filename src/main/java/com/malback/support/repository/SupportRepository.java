package com.malback.support.repository;

import com.malback.support.enums.SupportBoardType;
import com.malback.support.entity.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupportRepository extends JpaRepository<Support, Long> {
    Page<Support> findByDeletedAtIsNullAndType(Pageable pageable, SupportBoardType type);
    // 조회수 증가
    @Modifying
    @Query("UPDATE Support p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}