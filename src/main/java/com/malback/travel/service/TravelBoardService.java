package com.malback.travel.service;

import com.malback.travel.dto.TravelBoardDto;
import com.malback.travel.entity.TravelBoard;
import com.malback.travel.repository.TravelBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelBoardService {
    private final TravelBoardRepository travelBoardRepository;

    public List<TravelBoardDto> getAllTravelBoards() {
        return travelBoardRepository.findAll()
                .stream().map(TravelBoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    public TravelBoardDto getTravelBoardById(Long id) {
        return travelBoardRepository.findById(id)
                .map(TravelBoardDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("TravelBoard not found"));
    }

    @Transactional
    public TravelBoardDto createTravelBoard(TravelBoardDto dto) {
        TravelBoard saved = travelBoardRepository.save(dto.toEntity());
        return TravelBoardDto.fromEntity(saved);
    }

    @Transactional
    public void deleteTravelBoard(Long id) {
        travelBoardRepository.deleteById(id);
    }
}