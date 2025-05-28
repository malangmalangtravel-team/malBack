package com.malback.humor.service;

import com.malback.humor.dto.humorPostDto.HumorPostRequestDto;
import com.malback.humor.dto.humorPostDto.HumorPostResponseDto;
import com.malback.humor.entity.HumorPost;
import com.malback.humor.enums.HumorBoardType;
import com.malback.humor.repository.HumorPostRepository;
import com.malback.support.dto.SupportRequestDto;
import com.malback.support.dto.SupportResponseDto;
import com.malback.support.entity.Support;
import com.malback.support.enums.SupportBoardType;
import com.malback.travel.dto.travelPostDto.TravelPostResponseDto;
import com.malback.travel.entity.TravelPost;
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
    private final UserRepository userRepository;

    public Page<HumorPostResponseDto> getAllPosts(Pageable pageable, HumorBoardType type) {
        return humorPostRepository.findByDeletedAtIsNullAndType(pageable, type)
                .map(post -> HumorPostResponseDto.fromEntity(post, userRepository));
    }

    @Transactional
    public HumorPostResponseDto getPostById(Long id) {
        humorPostRepository.incrementViewCount(id); // 조회수 증가

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
    public HumorPostResponseDto updatePost(Long id, HumorPostRequestDto request) {
        HumorPost post = humorPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        HumorBoardType boardType = HumorBoardType.valueOf(request.getType().toUpperCase());

        post.setType(boardType);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return HumorPostResponseDto.fromEntity(humorPostRepository.save(post), userRepository);
    }

    @Transactional
    public HumorPostResponseDto softDeletePost(Long id) {
        HumorPost post = humorPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + id));

        if (post.getDeletedAt() != null) {
            throw new RuntimeException("이미 삭제된 게시글입니다.");
        }

        post.setDeletedAt(LocalDateTime.now());
        HumorPost savedPost = humorPostRepository.save(post);

        return HumorPostResponseDto.fromEntity(savedPost, userRepository);
    }


    // 이전 글
    public HumorPostResponseDto getPreviousPost(Long currentId) {
        return humorPostRepository.findFirstByIdLessThanAndDeletedAtIsNullOrderByIdDesc(currentId)
                .map(post -> HumorPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }

    // 다음 글
    public HumorPostResponseDto getNextPost(Long currentId) {
        return humorPostRepository.findFirstByIdGreaterThanAndDeletedAtIsNullOrderByIdAsc(currentId)
                .map(post -> HumorPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }
}
