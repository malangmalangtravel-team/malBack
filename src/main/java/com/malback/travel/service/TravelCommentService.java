package com.malback.travel.service;

import com.malback.common.CommentPageDto;
import com.malback.travel.dto.travelCommentDto.TravelCommentRequestDto;
import com.malback.travel.dto.travelCommentDto.TravelCommentResponseDto;
import com.malback.travel.entity.TravelComment;
import com.malback.travel.entity.TravelPost;
import com.malback.travel.repository.TravelCommentRepository;
import com.malback.travel.repository.TravelPostRepository;
import com.malback.user.entity.User;
import com.malback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelCommentService {

    private final TravelCommentRepository travelCommentRepository;
    private final TravelPostRepository travelPostRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CommentPageDto<TravelCommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        // 전체 댓글 모두 가져오기 (삭제되지 않은 것만)
        List<TravelComment> allComments = travelCommentRepository
                .findByTravelPost_IdAndDeletedAtIsNull(postId);

        // Entity -> DTO 변환 + Map으로 저장
        Map<Long, TravelCommentResponseDto> dtoMap = new HashMap<>();
        List<TravelCommentResponseDto> roots = new ArrayList<>();

        for (TravelComment comment : allComments) {
            TravelCommentResponseDto dto = TravelCommentResponseDto.fromEntity(comment);
            dtoMap.put(dto.getId(), dto);
        }

        // 트리 구조 구성
        for (TravelComment comment : allComments) {
            TravelCommentResponseDto dto = dtoMap.get(comment.getId());
            Long parentId = comment.getParentCommentId();

            if (parentId == null) {
                roots.add(dto); // 루트 댓글
            } else {
                TravelCommentResponseDto parentDto = dtoMap.get(parentId);
                if (parentDto != null) {
                    parentDto.getChildren().add(dto); // 자식으로 추가
                }
            }
        }

        // 페이징 처리 (루트 댓글 기준)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), roots.size());
        List<TravelCommentResponseDto> pagedRootComments = roots.subList(start, end);

        return CommentPageDto.<TravelCommentResponseDto>builder()
                .content(pagedRootComments)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(roots.size())
                .totalPages((int) Math.ceil((double) roots.size() / pageable.getPageSize()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<TravelCommentResponseDto> getChildComments(Long parentCommentId) {
        List<TravelComment> children = travelCommentRepository.findByParentComment_IdAndDeletedAtIsNull(parentCommentId);
        return children.stream()
                .map(TravelCommentResponseDto::fromEntity)
                .collect(Collectors.toList());
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
    public TravelCommentResponseDto updateComment(Long id, String content) {
        TravelComment comment = travelCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // content만 수정
        comment.setContent(content);

        travelCommentRepository.save(comment);

        return TravelCommentResponseDto.fromEntity(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        TravelComment comment = travelCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 예시: 자식 댓글 존재 시 soft delete, 없으면 실제 삭제 고려 가능
        boolean hasChildren = !travelCommentRepository.findByParentComment_IdAndDeletedAtIsNull(id).isEmpty();
        if (hasChildren) {
            comment.setDeletedAt(LocalDateTime.now());
        } else {
            comment.setDeletedAt(LocalDateTime.now());
            // 필요시 실제 delete 처리 추가
        }
    }
}
