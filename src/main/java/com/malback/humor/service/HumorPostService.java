package com.malback.humor.service;

import com.malback.humor.dto.humorPostDto.HumorPostRequestDto;
import com.malback.humor.dto.humorPostDto.HumorPostResponseDto;
import com.malback.humor.entity.HumorPost;
import com.malback.humor.enums.HumorBoardType;
import com.malback.humor.repository.HumorPostRepository;
import com.malback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HumorPostService {

    private final HumorPostRepository humorPostRepository;
    private final UserRepository userRepository; // ✅ 추가

    public Page<HumorPostResponseDto> getAllPosts(Pageable pageable, HumorBoardType type) {
        return humorPostRepository.findByDeletedAtIsNullAndType(pageable, type)
                .map(post -> HumorPostResponseDto.fromEntity(post, userRepository));
    }

    public HumorPostResponseDto getPostById(Long id) {
        HumorPost post = humorPostRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 게시글입니다."));
        return HumorPostResponseDto.fromEntity(post, userRepository);
    }

    @Transactional
    public HumorPostResponseDto createPost(HumorPostRequestDto request) {
        HumorBoardType boardType = HumorBoardType.valueOf(request.getType().toUpperCase());

        HumorPost post = HumorPost.builder()
                .type(boardType)
                .title(request.getTitle())
                .content(request.getContent())
                .email(request.getEmail())
                .viewCount(0)
                .build();

        return HumorPostResponseDto.fromEntity(humorPostRepository.save(post), userRepository);
    }

    @Transactional
    public void deletePost(Long id) {
        HumorPost post = humorPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.setDeletedAt(LocalDateTime.now());
        humorPostRepository.save(post);
    }

    // ✅ 이전 글
    public HumorPostResponseDto getPreviousPost(Long currentId) {
        return humorPostRepository.findFirstByIdLessThanOrderByIdDesc(currentId)
                .map(post -> HumorPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }

    // ✅ 다음 글
    public HumorPostResponseDto getNextPost(Long currentId) {
        return humorPostRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .map(post -> HumorPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }
}
