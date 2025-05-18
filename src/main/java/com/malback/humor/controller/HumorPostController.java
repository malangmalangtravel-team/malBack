package com.malback.humor.controller;

import com.malback.humor.dto.humorPostDto.HumorPostRequestDto;
import com.malback.humor.dto.humorPostDto.HumorPostResponseDto;
import com.malback.humor.enums.HumorBoardType;
import com.malback.humor.service.HumorPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/humor-posts")
@RequiredArgsConstructor
public class HumorPostController {

    private final HumorPostService humorPostService;

    @GetMapping
    public Map<String, Object> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) HumorBoardType type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<HumorPostResponseDto> posts = humorPostService.getAllPosts(pageable, type);

        Map<String, Object> response = new HashMap<>();
        response.put("content", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalPages", posts.getTotalPages());
        response.put("totalElements", posts.getTotalElements());
        response.put("size", posts.getSize());

        return response;
    }

    @GetMapping("/detail/{id}")
    public HumorPostResponseDto getPostDetail(@PathVariable Long id) {
        return humorPostService.getPostById(id); // ✅ userRepository 제거
    }

    @PostMapping("/createPost")
    public HumorPostResponseDto createPost(@Valid @RequestBody HumorPostRequestDto request) {
        return humorPostService.createPost(request);
    }

    @PutMapping("/{id}")
    public HumorPostResponseDto updatePost(@PathVariable Long id, @Valid @RequestBody HumorPostRequestDto request) {
        return humorPostService.updatePost(id, request);
    }

    @PostMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        humorPostService.deletePost(id);
    }

    @GetMapping("/prev/{postId}")
    public HumorPostResponseDto getPreviousPost(@PathVariable Long postId) {
        return humorPostService.getPreviousPost(postId);
    }

    @GetMapping("/next/{postId}")
    public HumorPostResponseDto getNextPost(@PathVariable Long postId) {
        return humorPostService.getNextPost(postId);
    }
}

