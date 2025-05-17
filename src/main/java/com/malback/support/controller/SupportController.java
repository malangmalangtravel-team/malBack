package com.malback.support.controller;

import com.malback.support.dto.SupportRequestDto;
import com.malback.support.dto.SupportResponseDto;
import com.malback.support.enums.SupportBoardType;
import com.malback.support.service.SupportService;
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
@RequestMapping("/api/support-posts")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @GetMapping
    public Map<String, Object> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SupportBoardType type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<SupportResponseDto> posts = supportService.getAllPosts(pageable, type);

        Map<String, Object> response = new HashMap<>();
        response.put("content", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalPages", posts.getTotalPages());
        response.put("totalElements", posts.getTotalElements());
        response.put("size", posts.getSize());

        return response;
    }

    @GetMapping("/detail/{id}")
    public SupportResponseDto getPostDetail(@PathVariable Long id) {
        return supportService.getPostById(id); // ✅ userRepository 제거
    }

    @PostMapping("/createPost")
    public SupportResponseDto createPost(@Valid @RequestBody SupportRequestDto request) {
        return supportService.createPost(request);
    }

    @PostMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        supportService.deletePost(id);
    }
}

