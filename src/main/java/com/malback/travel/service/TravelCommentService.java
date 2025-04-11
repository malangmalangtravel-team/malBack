package com.malback.travel.service;

import com.malback.travel.dto.travelCommentDto.TravelCommentRequestDto;
import com.malback.travel.dto.travelCommentDto.TravelCommentResponseDto;
import com.malback.travel.entity.TravelComment;
import com.malback.travel.entity.TravelPost;
import com.malback.travel.repository.TravelCommentRepository;
import com.malback.travel.repository.TravelPostRepository;
import com.malback.user.entity.User;
import com.malback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelCommentService {

    private final TravelCommentRepository travelCommentRepository;
    private final TravelPostRepository travelPostRepository;
    private final UserRepository userRepository;

    // 댓글 트리 구조
    public List<TravelCommentResponseDto> getCommentsByPostId(Long postId) {
        List<TravelComment> comments = travelCommentRepository.findByTravelPostId(postId).stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .toList();

        // 1. 엔티티 -> DTO 변환
        Map<Long, TravelCommentResponseDto> dtoMap = new HashMap<>();
        List<TravelCommentResponseDto> roots = new ArrayList<>();

        for (TravelComment comment : comments) {
            TravelCommentResponseDto dto = TravelCommentResponseDto.fromEntity(comment);
            dtoMap.put(dto.getId(), dto);
        }

        // 2. 트리 구성
        for (TravelCommentResponseDto dto : dtoMap.values()) {
            if (dto.getParentCommentId() == null) {
                roots.add(dto);
            } else {
                TravelCommentResponseDto parent = dtoMap.get(dto.getParentCommentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }

        return roots;
    }

    @Transactional
    public TravelCommentResponseDto createComment(TravelCommentRequestDto dto) {

        // 게시글 조회
        TravelPost post = travelPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 유저 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 부모 댓글 조회 (대댓글일 경우)
        TravelComment parent = null;
        if (dto.getParentCommentId() != null) {
            parent = travelCommentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        // 댓글 생성
        TravelComment comment = dto.toEntity(post, user, parent);
        TravelComment saved = travelCommentRepository.save(comment);

        return TravelCommentResponseDto.fromEntity(saved);
    }

    @Transactional
    public TravelCommentResponseDto updateComment(Long commentId, String newContent) {
        TravelComment comment = travelCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setContent(newContent);
        return TravelCommentResponseDto.fromEntity(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        TravelComment comment = travelCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setDeletedAt(LocalDateTime.now());
    }
}
