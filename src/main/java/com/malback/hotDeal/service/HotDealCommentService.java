package com.malback.hotDeal.service;

import com.malback.common.CommentPageDto;
import com.malback.hotDeal.dto.hotDealCommentDto.HotDealCommentRequestDto;
import com.malback.hotDeal.dto.hotDealCommentDto.HotDealCommentResponseDto;
import com.malback.hotDeal.entity.HotDealComment;
import com.malback.hotDeal.entity.HotDealPost;
import com.malback.hotDeal.repository.HotDealCommentRepository;
import com.malback.hotDeal.repository.HotDealPostRepository;
import com.malback.user.entity.User;
import com.malback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HotDealCommentService {

    private final HotDealCommentRepository hotDealCommentRepository;
    private final HotDealPostRepository hotDealPostRepository;
    private final UserRepository userRepository;

    public CommentPageDto<HotDealCommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        List<HotDealComment> allComments = hotDealCommentRepository.findByHotDealPost_IdAndDeletedAtIsNull(postId);

        Map<Long, HotDealCommentResponseDto> dtoMap = new HashMap<>();
        List<HotDealCommentResponseDto> roots = new ArrayList<>();

        for (HotDealComment comment : allComments) {
            HotDealCommentResponseDto dto = HotDealCommentResponseDto.fromEntity(comment);
            dtoMap.put(dto.getId(), dto);
        }

        for (HotDealComment comment : allComments) {
            HotDealCommentResponseDto dto = dtoMap.get(comment.getId());
            if (dto.getParentCommentId() == null) {
                roots.add(dto);
            } else {
                HotDealCommentResponseDto parent = dtoMap.get(dto.getParentCommentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }

        // 페이징 수동 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), roots.size());
        List<HotDealCommentResponseDto> pagedRoots = roots.subList(start, end);

        return CommentPageDto.<HotDealCommentResponseDto>builder()
                .content(pagedRoots)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(roots.size())
                .totalPages((int) Math.ceil((double) roots.size() / pageable.getPageSize()))
                .build();
    }

    @Transactional
    public HotDealCommentResponseDto createComment(HotDealCommentRequestDto dto) {
        HotDealPost post = hotDealPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        HotDealComment parent = null;
        if (dto.getParentCommentId() != null) {
            parent = hotDealCommentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        HotDealComment comment = dto.toEntity(post, user, parent);
        HotDealComment saved = hotDealCommentRepository.save(comment);

        return HotDealCommentResponseDto.fromEntity(saved);
    }

    @Transactional
    public HotDealCommentResponseDto updateComment(Long id, String newContent) {
        HotDealComment comment = hotDealCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setContent(newContent);
        return HotDealCommentResponseDto.fromEntity(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        HotDealComment comment = hotDealCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setDeletedAt(LocalDateTime.now());
    }
}
