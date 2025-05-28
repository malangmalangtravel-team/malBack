package com.malback.hotDeal.controller;

import com.malback.common.CommentPageDto;
import com.malback.hotDeal.dto.hotDealCommentDto.HotDealCommentRequestDto;
import com.malback.hotDeal.dto.hotDealCommentDto.HotDealCommentResponseDto;
import com.malback.hotDeal.service.HotDealCommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotDeal-comments")
@RequiredArgsConstructor
public class HotDealCommentController {

    private final HotDealCommentService hotDealCommentService;

    // 댓글 목록 조회
    @GetMapping
    public CommentPageDto<HotDealCommentResponseDto> getCommentsByPostId(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return hotDealCommentService.getCommentsByPostId(postId, pageable);
    }

    // 댓글 등록
    @PostMapping
    public HotDealCommentResponseDto createComment(@RequestBody HotDealCommentRequestDto requestDto, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            throw new IllegalStateException("로그인한 사용자만 댓글을 작성할 수 있습니다.");
        }

        requestDto.setEmail(email);

        if (requestDto.getPostId() == null) {
            throw new IllegalArgumentException("postId는 필수입니다.");
        }

        return hotDealCommentService.createComment(requestDto);
    }

    // 댓글 수정
    @PostMapping("/{id}")
    public HotDealCommentResponseDto updateComment(@PathVariable Long id, @RequestBody HotDealCommentRequestDto requestDto) {
        return hotDealCommentService.updateComment(id, requestDto.getContent());
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        hotDealCommentService.deleteComment(id);
    }

    
}
