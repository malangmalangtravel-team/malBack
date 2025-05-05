package com.malback.travel.service;

import com.malback.humor.dto.humorPostDto.HumorPostResponseDto;
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
                ? travelPostRepository.findByCountry_CountryNameOrderByIdDesc(countryName, pageRequest)
                : travelPostRepository.findByCountry_CountryNameAndTypeOrderByIdDesc(countryName, type, pageRequest);

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
    public void deletePost(Long id) {
        travelPostRepository.deleteById(id);
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