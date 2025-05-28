package com.malback.hotDeal.repository;

import com.malback.hotDeal.entity.HotDealComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotDealCommentRepository extends JpaRepository<HotDealComment, Long> {
    List<HotDealComment> findByHotDealPost_IdAndDeletedAtIsNull(Long postId);
}
