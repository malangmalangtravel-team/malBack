package com.malback.travel.service;

import com.malback.travel.dto.TravelCommentDto;
import com.malback.travel.entity.TravelComment;
import com.malback.travel.entity.TravelPost;
import com.malback.travel.repository.TravelCommentRepository;
import com.malback.travel.repository.TravelPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelCommentService {
    private final TravelCommentRepository travelCommentRepository;
    private final TravelPostRepository travelPostRepository;

    public List<TravelCommentDto> getAllCommentsByPost(Long postId) {
        return travelCommentRepository.findByTravelPostId(postId)
                .stream().map(TravelCommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    public TravelCommentDto getCommentById(Long id) {
        return travelCommentRepository.findById(id)
                .map(TravelCommentDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    @Transactional
    public TravelCommentDto createComment(TravelCommentDto dto) {
        // travelPost를 가져오기 (예: postId로 조회)
        TravelPost travelPost = travelPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));  // 포스트가 존재하지 않으면 예외 발생

        // parentComment가 존재하면 parentComment도 조회하여 설정
        TravelComment parentComment = null;
        if (dto.getParentCommentId() != null) {
            parentComment = travelCommentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        // DTO를 Entity로 변환하여 저장
        TravelComment comment = dto.toEntity(travelPost);  // TravelPost를 인자로 전달
        comment.setParentComment(parentComment);  // 부모 댓글 설정

        TravelComment saved = travelCommentRepository.save(comment);
        return TravelCommentDto.fromEntity(saved);
    }

    @Transactional
    public void deleteComment(Long id) {
        travelCommentRepository.deleteById(id);
    }
}