package com.malback.humor.service;

import com.malback.common.CommentPageDto;
import com.malback.humor.dto.humorCommentDto.HumorCommentRequestDto;
import com.malback.humor.dto.humorCommentDto.HumorCommentResponseDto;
import com.malback.humor.entity.HumorComment;
import com.malback.humor.entity.HumorPost;
import com.malback.humor.repository.HumorCommentRepository;
import com.malback.humor.repository.HumorPostRepository;
import com.malback.user.entity.User;
import com.malback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HumorCommentService {

    private final HumorCommentRepository humorCommentRepository;
    private final HumorPostRepository humorPostRepository;
    private final UserRepository userRepository;

    public CommentPageDto<HumorCommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        List<HumorComment> allComments = humorCommentRepository.findByHumorPost_IdAndDeletedAtIsNull(postId);

        Map<Long, HumorCommentResponseDto> dtoMap = new HashMap<>();
        List<HumorCommentResponseDto> roots = new ArrayList<>();

        for (HumorComment comment : allComments) {
            HumorCommentResponseDto dto = HumorCommentResponseDto.fromEntity(comment);
            dtoMap.put(dto.getId(), dto);
        }

        for (HumorComment comment : allComments) {
            HumorCommentResponseDto dto = dtoMap.get(comment.getId());
            if (dto.getParentCommentId() == null) {
                roots.add(dto);
            } else {
                HumorCommentResponseDto parent = dtoMap.get(dto.getParentCommentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }

        // 페이징 수동 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), roots.size());
        List<HumorCommentResponseDto> pagedRoots = roots.subList(start, end);

        return CommentPageDto.<HumorCommentResponseDto>builder()
                .content(pagedRoots)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(roots.size())
                .totalPages((int) Math.ceil((double) roots.size() / pageable.getPageSize()))
                .build();
    }

    @Transactional
    public HumorCommentResponseDto createComment(HumorCommentRequestDto dto) {
        HumorPost post = humorPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        HumorComment parent = null;
        if (dto.getParentCommentId() != null) {
            parent = humorCommentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        HumorComment comment = dto.toEntity(post, user, parent);
        HumorComment saved = humorCommentRepository.save(comment);

        return HumorCommentResponseDto.fromEntity(saved);
    }

    @Transactional
    public HumorCommentResponseDto updateComment(Long id, String newContent) {
        HumorComment comment = humorCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setContent(newContent);
        return HumorCommentResponseDto.fromEntity(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        HumorComment comment = humorCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setDeletedAt(LocalDateTime.now());
    }
}
