package com.sparta.springauth.kakao.controller;

import com.sparta.springauth.kakao.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocialAuthController {

    private final KakaoService kakaoService;

    @GetMapping("/api/auth/social/kakao")
    public String kakaoCallback(@RequestParam String code) {

        // 1. 인가코드를 이용해서 엑세스 토큰을 받아오기
        String accessToken = kakaoService.getAccessToken(code);

        // 2. 엑세스 토큰를 활용해서 사용자 정보를 가져오는 메서드
        String userInfo = kakaoService.getUserInfo(accessToken);
        return "[  인증 코드 ]:<br> " + code + "<br><br>[  Access Token  ]: <br>" + accessToken + "<br><br>[  User Info  ]: <br>" + userInfo;
        //return "엑세스 "+ accessToken + "인가코드: " + code;
    }
}