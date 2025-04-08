package com.malback.user.service;

import com.malback.user.enums.Role;
import com.malback.user.entity.User;
import com.malback.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId(); // 예: "google"
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        // 세션에 직접 저장
        session.setAttribute("email", email);

        // 이메일 기준으로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .map(existingUser -> existingUser)  // 기존 유저 그대로 사용
                .orElse(User.builder()
                        .email(email)
                        .name(name)
                        .picture(picture)
                        .provider(provider)
                        .nickname(generateDefaultNickname(name)) // 기본 닉네임 생성
                        .role(Role.USER)
                        .build());

        // 저장 또는 갱신
        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );
    }

    private String generateDefaultNickname(String name) {
        // 기본 닉네임: 이름 + 랜덤 숫자 (또는 로직에 맞게 수정 가능)
        return name + "_" + (int) (Math.random() * 10000);
    }
}
