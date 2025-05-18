package com.malback.humor.controller;

import com.malback.common.CommentPageDto;
import com.malback.humor.dto.humorCommentDto.HumorCommentRequestDto;
import com.malback.humor.dto.humorCommentDto.HumorCommentResponseDto;
import com.malback.humor.service.HumorCommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/humor-comments")
@RequiredArgsConstructor
public class HumorCommentController {

    private final HumorCommentService humorCommentService;

    // 댓글 목록 조회
    @GetMapping
    public CommentPageDto<HumorCommentResponseDto> getCommentsByPostId(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return humorCommentService.getCommentsByPostId(postId, pageable);
    }

    // 댓글 등록
    @PostMapping
    public HumorCommentResponseDto createComment(@RequestBody HumorCommentRequestDto requestDto, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            throw new IllegalStateException("로그인한 사용자만 댓글을 작성할 수 있습니다.");
        }

        requestDto.setEmail(email);

        if (requestDto.getPostId() == null) {
            throw new IllegalArgumentException("postId는 필수입니다.");
        }

        return humorCommentService.createComment(requestDto);
    }

    // 댓글 수정
    @PostMapping("/{id}")
    public HumorCommentResponseDto updateComment(@PathVariable Long id, @RequestBody HumorCommentRequestDto requestDto) {
        return humorCommentService.updateComment(id, requestDto.getContent());
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        humorCommentService.deleteComment(id);
    }

    
}
