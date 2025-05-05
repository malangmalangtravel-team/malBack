package com.malback.travel.controller;

import com.malback.humor.dto.humorPostDto.HumorPostResponseDto;
import com.malback.travel.dto.travelPostDto.TravelPostRequestDto;
import com.malback.travel.dto.travelPostDto.TravelPostResponseDto;
import com.malback.travel.enums.BoardType;
import com.malback.travel.service.TravelPostService;
import com.malback.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/travel-posts")
@RequiredArgsConstructor
public class TravelPostController {
    private final TravelPostService travelPostService;
    private final UserRepository userRepository;

    @GetMapping
    public List<TravelPostResponseDto> getAllPosts() {
        return travelPostService.getAllPosts(userRepository);
    }

    @GetMapping("/country")
    public Map<String, Object> getPostsByCountry(
            @RequestParam String countryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BoardType type) {
        Page<TravelPostResponseDto> posts = travelPostService.getPosts(countryName, type, page, size, userRepository);

        Map<String, Object> response = new HashMap<>();
        response.put("content", posts.getContent());   // 게시글 리스트
        response.put("currentPage", posts.getNumber()); // 현재 페이지 번호
        response.put("totalPages", posts.getTotalPages()); // 전체 페이지 수
        response.put("totalElements", posts.getTotalElements()); // 전체 게시글 수
        response.put("size", posts.getSize()); // 한 페이지당 게시글 수

        return response;
    }


    @GetMapping("/detail/{id}")
    public TravelPostResponseDto getPostDetail(@PathVariable Long id) {
        return travelPostService.getPostById(id, userRepository);
    }

    @PostMapping("/createPost/{countryName}")
    public TravelPostResponseDto createPost(
            @PathVariable String countryName,
            @Valid @RequestBody TravelPostRequestDto request) {
        return travelPostService.createPost(countryName, request);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        travelPostService.deletePost(id);
    }

    @GetMapping("/prev/{postId}")
    public TravelPostResponseDto getPreviousPost(@PathVariable Long postId) {
        return travelPostService.getPreviousPost(postId);
    }

    @GetMapping("/next/{postId}")
    public TravelPostResponseDto getNextPost(@PathVariable Long postId) {
        return travelPostService.getNextPost(postId);
    }
}