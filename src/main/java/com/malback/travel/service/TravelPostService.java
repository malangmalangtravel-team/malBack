package com.malback.travel.service;

import com.malback.humor.dto.humorPostDto.HumorPostResponseDto;
import com.malback.support.dto.SupportRequestDto;
import com.malback.support.dto.SupportResponseDto;
import com.malback.support.entity.Support;
import com.malback.support.enums.SupportBoardType;
import com.malback.travel.dto.travelPostDto.TravelPostRequestDto;
import com.malback.travel.dto.travelPostDto.TravelPostResponseDto;
import com.malback.travel.entity.Country;
import com.malback.travel.entity.TravelPost;
import com.malback.travel.enums.BoardType;
import com.malback.travel.repository.CountryRepository;
import com.malback.travel.repository.TravelPostRepository;
import com.malback.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelPostService {
    private final TravelPostRepository travelPostRepository;
    private final CountryRepository countryRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<TravelPostResponseDto> getAllPosts(UserRepository userRepository) {
        return travelPostRepository.findAll()
                .stream()
                .map(post -> TravelPostResponseDto.fromEntity(post, userRepository))
                .collect(Collectors.toList());
    }

    // 특정 나라 게시판의 게시글 전체, 타입별 목록 조회
    public Page<TravelPostResponseDto> getPosts(String countryName, BoardType type, int page, int size, UserRepository userRepository) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<TravelPost> posts = (type == null)
                ? travelPostRepository.findByCountry_CountryNameAndDeletedAtIsNullOrderByIdDesc(countryName, pageRequest)
                : travelPostRepository.findByCountry_CountryNameAndTypeAndDeletedAtIsNullOrderByIdDesc(countryName, type, pageRequest);

        return posts.map(post -> TravelPostResponseDto.fromEntity(post, userRepository));
    }

    @Transactional
    public TravelPostResponseDto getPostById(Long id, UserRepository userRepository) {
        travelPostRepository.incrementViewCount(id); // 조회수 증가

        return travelPostRepository.findById(id)
                .map(post -> TravelPostResponseDto.fromEntity(post, userRepository))
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    @Transactional
    public TravelPostResponseDto createPost(String countryName, TravelPostRequestDto request) {
        // ✅ Country 조회 후 없으면 생성
        Country country = countryRepository.findByCountryName(countryName)
                .orElseGet(() -> countryRepository.save(new Country(countryName)));

        // ✅ 게시판 타입 변환
        BoardType boardType = BoardType.valueOf(request.getType().toUpperCase());

        // ✅ 게시글 엔티티 생성
        TravelPost post = TravelPost.builder()
                .country(country)  // 💡 여기에 Country 객체 필요함!
                .type(boardType)
                .title(request.getTitle())
                .content(request.getContent())
                .email(request.getEmail())
                .viewCount(0)
                .build();

        // ✅ 저장 후 응답 반환
        return TravelPostResponseDto.fromEntity(travelPostRepository.save(post));
    }

    @Transactional
    public TravelPostResponseDto updatePost(Long id, TravelPostRequestDto request) {
        TravelPost post = travelPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        BoardType boardType = BoardType.valueOf(request.getType().toUpperCase());

        post.setType(boardType);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return TravelPostResponseDto.fromEntity(travelPostRepository.save(post), userRepository);
    }

    @Transactional
    public TravelPostResponseDto softDeletePost(Long id) {
        TravelPost post = travelPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + id));

        if (post.getDeletedAt() != null) {
            throw new RuntimeException("이미 삭제된 게시글입니다.");
        }

        post.setDeletedAt(LocalDateTime.now());
        TravelPost savedPost = travelPostRepository.save(post);

        return TravelPostResponseDto.fromEntity(savedPost, userRepository);
    }

    // 이전 글
    public TravelPostResponseDto getPreviousPost(Long currentId) {
        return travelPostRepository.findFirstByIdLessThanOrderByIdDesc(currentId)
                .map(post -> TravelPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }

    // 다음 글
    public TravelPostResponseDto getNextPost(Long currentId) {
        return travelPostRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .map(post -> TravelPostResponseDto.fromEntity(post, userRepository))
                .orElse(null);
    }
}