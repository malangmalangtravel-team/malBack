package com.malback.travel.controller;

import com.malback.travel.dto.TravelCommentDto;
import com.malback.travel.entity.TravelComment;
import com.malback.travel.service.TravelCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/travel-comments")
@RequiredArgsConstructor
public class TravelCommentController {
    private final TravelCommentService travelCommentService;

    @GetMapping("/post/{postId}")
    public List<TravelCommentDto> getCommentsByPost(@PathVariable Long postId) {
        return travelCommentService.getAllCommentsByPost(postId);
    }

    @GetMapping("/{id}")
    public TravelCommentDto getComment(@PathVariable Long id) {
        return travelCommentService.getCommentById(id);
    }

    @PostMapping
    public TravelCommentDto createComment(@RequestBody TravelCommentDto dto) {
        return travelCommentService.createComment(dto);
    }

    @PostMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        travelCommentService.deleteComment(id);
    }
}