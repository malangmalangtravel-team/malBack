package com.malback.travel.service;

import com.malback.travel.dto.CountryDto;
import com.malback.travel.entity.Country;
import com.malback.travel.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll()
                .stream().map(CountryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public CountryDto getCountryById(Long id) {
        return countryRepository.findById(id)
                .map(CountryDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
    }

    @Transactional
    public CountryDto createCountry(CountryDto dto) {
        Country saved = countryRepository.save(dto.toEntity());
        return CountryDto.fromEntity(saved);
    }

    @Transactional
    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }
}