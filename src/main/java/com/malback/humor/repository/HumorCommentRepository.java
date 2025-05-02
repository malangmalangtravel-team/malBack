package com.malback.humor.repository;

import com.malback.humor.entity.HumorComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HumorCommentRepository extends JpaRepository<HumorComment, Long> {
    List<HumorComment> findByHumorPost_IdAndDeletedAtIsNull(Long postId);
}
