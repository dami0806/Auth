package com.sparta.springauth.service;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * DefaultOAuth2UserService를 상속하는 CustomOAuth2UserService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    // 사용자 정보를 로드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 속성 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();

        //이메일, 닉네임, 프로필사진 추출하기
        String email = (String) ((Map<String, Object>) attributes.get("kakao_account")).get("email");
        String name = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");
        String picture = (String) ((Map<String, Object>) attributes.get("properties")).get("profile_image");

        log.info("Email: {}", email);
        log.info("Name: {}", name);
        log.info("Profile Picture: {}", picture);


        /**
         * 이메일로 사용자 찾기 -> 있는사용자면 get으로 사용자 얻고 없다면 새로운 사용자로 만들겁니다.
         */
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        User user;

        // 사용자가 이미 존재하면 기존 사용자 이용
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = User.builder()
                    .email(email)
                    .name(name)
                    .profile(picture)
                    .build();
            userRepository.save(user);
        }

        return oAuth2User;
    }

}
