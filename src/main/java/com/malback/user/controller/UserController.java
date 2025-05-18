// src/main/java/com/malback/user/controller/UserController.java
package com.malback.user.controller;

import com.malback.user.dto.UpdateNicknameRequest;
import com.malback.user.entity.User;
import com.malback.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 닉네임 변경
    @PostMapping("/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody UpdateNicknameRequest request, HttpSession session) {
        String email = (String) session.getAttribute("email");
        userService.updateNickname(email, request.getNickname());
        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }

    // 내 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.ok().body(null);  // 로그인 안 된 경우에도 200 OK
        }

        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
