package com.malback.user.repository;

import com.malback.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // 닉네임 중복검사
    Optional<User> findByNickname(String nickname);
}
