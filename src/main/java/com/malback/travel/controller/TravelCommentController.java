package com.malback.travel.controller;

import com.malback.travel.dto.travelCommentDto.TravelCommentRequestDto;
import com.malback.travel.dto.travelCommentDto.TravelCommentResponseDto;
import com.malback.travel.service.TravelCommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-comments")
@RequiredArgsConstructor
public class TravelCommentController {

    private final TravelCommentService travelCommentService;

    // 댓글 목록 조회
    // /api/travel-comments?postId=17 형식 지원
    @GetMapping
    public List<TravelCommentResponseDto> getCommentsByPostId(@RequestParam Long postId) {
        return travelCommentService.getCommentsByPostId(postId);
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
    public TravelCommentResponseDto updateComment(@PathVariable Long id, @RequestBody String content) {
        return travelCommentService.updateComment(id, content);
    }

    // 댓글 삭제 (Soft delete)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        travelCommentService.deleteComment(id);
    }
}
