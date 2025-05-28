package com.malback.hotDeal.service;

import com.malback.hotDeal.dto.hotDealPostDto.HotDealPostRequestDto;
import com.malback.hotDeal.dto.hotDealPostDto.HotDealPostResponseDto;
import com.malback.hotDeal.entity.HotDealPost;
import com.malback.hotDeal.enums.HotDealBoardType;
import com.malback.hotDeal.repository.HotDealPostRepository;
import com.malback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HotDealPostService {

    private final HotDealPostRepository hotDealPostRepository;
    private final UserRepository userRepository;

    public Page<HotDealPostResponseDto> getAllPosts(Pageable pageable, HotDealBoardType type) {
        Page<HotDealPost> posts;
        if (type == null) {
            posts = hotDealPostRepository.findByDeletedAtIsNull(pageable);
        } else {
            posts = hotDealPostRepository.findByDeletedAtIsNullAndType(pageable, type);
        }
        return posts.map(post -> HotDealPostResponseDto.fromEntity(post, userRepository));
    }

    @Transactional
    public HotDealPostResponseDto getPostById(Long id) {
        hotDealPostRepository.incrementViewCount(id); // 조회수 증가

        HotDealPost post = hotDealPostRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 게시글입니다."));

        return HotDealPostResponseDto.fromEntity(post, userRepository);
    }

    @Transactional
    public HotDealPostResponseDto createPost(HotDealPostRequestDto request) {
        HotDealBoardType boardType = HotDealBoardType.valueOf(request.getType().toUpperCase());

        HotDealPost post = HotDealPost.builder()
                .type(boardType)
                .title(request.getTitle())
                .content(request.getContent())
                .email(request.getEmail())
                .viewCount(0)
                .build();

        return HotDealPostResponseDto.fromEntity(hotDealPostRepository.save(post), userRepository);
    }

    @Transactional
    public HotDealPostResponseDto updatePost(Long id, HotDealPostRequestDto request) {
        HotDealPost post = hotDealPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        HotDealBoardType boardType = HotDealBoardType.valueOf(request.getType().toUpperCase());

        post.setType(boardType);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return HotDealPostResponseDto.fromEntity(hotDealPostRepository.save(post), userRepository);
    }

    @Transactional
    public HotDealPostResponseDto softDeletePost(Long id) {
        HotDealPost post = hotDealPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + id));

        if (post.getDeletedAt() != null) {
            throw new RuntimeException("이미 삭제된 게시글입니다.");
        }

        post.setDeletedAt(LocalDateTime.now());
        HotDealPost savedPost = hotDealPostRepository.save(post);

        return HotDealPostResponseDto.fromEntity(savedPost, userRepository);
    }


    // 이전 글
    public HotDealPostResponseDto getPreviousPost(Long currentId) {
        return hotDealPostRepository.findFirstByIdLessThanAndDeletedAtIsNullOrderByIdDesc(currentId)
                .map(post -> HotDealPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }

    // 다음 글
    public HotDealPostResponseDto getNextPost(Long currentId) {
        return hotDealPostRepository.findFirstByIdGreaterThanAndDeletedAtIsNullOrderByIdAsc(currentId)
                .map(post -> HotDealPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }
}
