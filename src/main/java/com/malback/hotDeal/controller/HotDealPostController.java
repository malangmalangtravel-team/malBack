package com.malback.hotDeal.controller;

import com.malback.hotDeal.dto.hotDealPostDto.HotDealPostRequestDto;
import com.malback.hotDeal.dto.hotDealPostDto.HotDealPostResponseDto;
import com.malback.hotDeal.enums.HotDealBoardType;
import com.malback.hotDeal.service.HotDealPostService;
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
@RequestMapping("/api/hotDeal-posts")
@RequiredArgsConstructor
public class HotDealPostController {

    private final HotDealPostService hotDealPostService;

    @GetMapping
    public Map<String, Object> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) HotDealBoardType type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<HotDealPostResponseDto> posts = hotDealPostService.getAllPosts(pageable, type);

        Map<String, Object> response = new HashMap<>();
        response.put("content", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalPages", posts.getTotalPages());
        response.put("totalElements", posts.getTotalElements());
        response.put("size", posts.getSize());

        return response;
    }

    @GetMapping("/detail/{id}")
    public HotDealPostResponseDto getPostDetail(@PathVariable Long id) {
        return hotDealPostService.getPostById(id); // ✅ userRepository 제거
    }

    @PostMapping("/createPost")
    public HotDealPostResponseDto createPost(@Valid @RequestBody HotDealPostRequestDto request) {
        return hotDealPostService.createPost(request);
    }

    @PostMapping("/{id}")
    public HotDealPostResponseDto updatePost(@PathVariable Long id, @Valid @RequestBody HotDealPostRequestDto request) {
        return hotDealPostService.updatePost(id, request);
    }

    @PostMapping("/delete/{id}")
    public HotDealPostResponseDto softDeletePost(@PathVariable Long id) {
        return hotDealPostService.softDeletePost(id);
    }

    @GetMapping("/prev/{postId}")
    public HotDealPostResponseDto getPreviousPost(@PathVariable Long postId) {
        return hotDealPostService.getPreviousPost(postId);
    }

    @GetMapping("/next/{postId}")
    public HotDealPostResponseDto getNextPost(@PathVariable Long postId) {
        return hotDealPostService.getNextPost(postId);
    }
}

