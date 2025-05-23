package com.malback.support.service;

import com.malback.support.dto.SupportRequestDto;
import com.malback.support.dto.SupportResponseDto;
import com.malback.support.entity.Support;
import com.malback.support.enums.SupportBoardType;
import com.malback.support.repository.SupportRepository;
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
public class SupportService {

    private final SupportRepository supportRepository;
    private final UserRepository userRepository;

    public Page<SupportResponseDto> getAllPosts(Pageable pageable, SupportBoardType type) {
        return supportRepository.findByDeletedAtIsNullAndType(pageable, type)
                .map(post -> SupportResponseDto.fromEntity(post, userRepository));
    }

    @Transactional
    public SupportResponseDto getPostById(Long id) {
        supportRepository.incrementViewCount(id); // 조회수 증가

        Support post = supportRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 게시글입니다."));

        return SupportResponseDto.fromEntity(post, userRepository);
    }

    @Transactional
    public SupportResponseDto createPost(SupportRequestDto request) {
        SupportBoardType boardType = SupportBoardType.valueOf(request.getType().toUpperCase());

        Support post = Support.builder()
                .type(boardType)
                .title(request.getTitle())
                .content(request.getContent())
                .email(request.getEmail())
                .viewCount(0)
                .build();

        return SupportResponseDto.fromEntity(supportRepository.save(post), userRepository);
    }

    @Transactional
    public SupportResponseDto updatePost(Long id, SupportRequestDto request) {
        Support post = supportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        SupportBoardType boardType = SupportBoardType.valueOf(request.getType().toUpperCase());

        post.setType(boardType);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return SupportResponseDto.fromEntity(supportRepository.save(post), userRepository);
    }


    @Transactional
    public SupportResponseDto softDeletePost(Long id) {
        Support post = supportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + id));

        if (post.getDeletedAt() != null) {
            throw new RuntimeException("이미 삭제된 게시글입니다.");
        }

        post.setDeletedAt(LocalDateTime.now());
        Support savedPost = supportRepository.save(post);

        return SupportResponseDto.fromEntity(savedPost, userRepository);
    }
}
