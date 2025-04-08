package com.malback.travel.repository;

import com.malback.travel.entity.TravelComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TravelCommentRepository extends JpaRepository<TravelComment, Long> {
    List<TravelComment> findByTravelPostId(Long postId);
}
