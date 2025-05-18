// src/main/java/com/malback/user/service/UserService.java
package com.malback.user.service;

import com.malback.user.entity.User;
import com.malback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateNickname(String email, String newNickname) {
        if (email == null || email.isBlank()) {
            log.warn("닉네임 변경 실패: 로그인하지 않았습니다. email: {}", email);
            throw new RuntimeException("로그인하지 않았습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("닉네임 변경 실패: 사용자를 찾을 수 없습니다. email: {}", email);
                    return new RuntimeException("사용자를 찾을 수 없습니다.");
                });

        // 이미 같은 닉네임이면 변경 불필요
        if (newNickname.equals(user.getNickname())) {
            log.info("닉네임 변경 필요 없음: 기존 닉네임과 동일합니다.");
            return;
        }

        // 중복 닉네임 검사 (다른 사용자가 같은 닉네임을 사용하는지 확인)
        boolean exists = userRepository.findByNickname(newNickname)
                .filter(u -> !u.getEmail().equals(email)) // 같은 사용자의 닉네임이면 제외
                .isPresent();

        if (exists) {
            log.warn("닉네임 변경 실패: 중복된 닉네임입니다. nickname: {}", newNickname);
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        user.setNickname(newNickname);
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            log.warn("로그인하지 않았습니다. email: {}", email);
            return null; // null 반환으로 변경
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}