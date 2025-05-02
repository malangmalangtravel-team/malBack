package com.malback.travel.repository;

import com.malback.travel.entity.TravelComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TravelCommentRepository extends JpaRepository<TravelComment, Long> {

    // 특정 댓글의 자식 댓글 목록 (필요 시 사용)
    List<TravelComment> findByParentComment_IdAndDeletedAtIsNull(Long parentId);

    // 게시글의 모든 댓글 (삭제되지 않은 것만)
    List<TravelComment> findByTravelPost_IdAndDeletedAtIsNull(Long postId);
}
