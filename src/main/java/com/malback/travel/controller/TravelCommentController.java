package com.malback.travel.controller;

import com.malback.common.CommentPageDto;
import com.malback.travel.dto.travelCommentDto.TravelCommentRequestDto;
import com.malback.travel.dto.travelCommentDto.TravelCommentResponseDto;
import com.malback.travel.service.TravelCommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-comments")
@RequiredArgsConstructor
public class TravelCommentController {

    private final TravelCommentService travelCommentService;

    // 부모 댓글 ID로 자식 댓글 조회
    @GetMapping("/child-comments")
    public List<TravelCommentResponseDto> getChildComments(@RequestParam Long parentCommentId) {
        return travelCommentService.getChildComments(parentCommentId);
    }


    // 댓글 조회 (페이징 적용)
    @GetMapping
    public CommentPageDto<TravelCommentResponseDto> getCommentsByPostId(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return travelCommentService.getCommentsByPostId(postId, pageable);
    }

    // 댓글 등록
    @PostMapping
    public TravelCommentResponseDto createComment(@RequestBody TravelCommentRequestDto requestDto, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            throw new IllegalStateException("로그인한 사용자만 댓글을 작성할 수 있습니다.");
        }

        requestDto.setEmail(email);

        // postId null 방어
        if (requestDto.getPostId() == null) {
            throw new IllegalArgumentException("postId는 필수입니다.");
        }

        return travelCommentService.createComment(requestDto);
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public TravelCommentResponseDto updateComment(@PathVariable Long id, @RequestBody TravelCommentRequestDto requestDto) {
        return travelCommentService.updateComment(id, requestDto.getContent());
    }

    // 댓글 삭제 (Soft delete)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        travelCommentService.deleteComment(id);
    }
}
