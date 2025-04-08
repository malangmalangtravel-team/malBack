package com.malback.travel.controller;

import com.malback.travel.dto.TravelBoardDto;
import com.malback.travel.entity.TravelBoard;
import com.malback.travel.service.TravelBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/travel-boards")
@RequiredArgsConstructor
public class TravelBoardController {
    private final TravelBoardService travelBoardService;

    @GetMapping
    public List<TravelBoardDto> getAllBoards() {
        return travelBoardService.getAllTravelBoards();
    }

    @GetMapping("/{id}")
    public TravelBoardDto getBoard(@PathVariable Long id) {
        return travelBoardService.getTravelBoardById(id);
    }

    @PostMapping
    public TravelBoardDto createBoard(@RequestBody TravelBoardDto dto) {
        return travelBoardService.createTravelBoard(dto);
    }

    @PostMapping("/{id}")
    public void deleteBoard(@PathVariable Long id) {
        travelBoardService.deleteTravelBoard(id);
    }
}