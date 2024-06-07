package com.sparta.springauth.kakao.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class KakaoService {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}") // application.yml의 값을 주입
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") // application.yml의 값을 주입
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") // application.yml의 값을 주입
    private String redirectUri;

    // Jackson ObjectMapper 객체 생성
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. 인가코드를 이용해서 엑세스 토큰을 받아오기
    public String getAccessToken(String code) {
        // 카카오 토큰 요청 URL
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        //RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더
        HttpHeaders headers = new HttpHeaders();

        // 콘텐츠 타입 설정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //본문 요청에 들어갈 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        // HTTP 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(params, headers);

        // 카카오 인증 서버로 POST 요청 보내기
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, multiValueMapHttpEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode rootNode = parseResponse(response.getBody());
            // 엑세스 토큰 추출
            return rootNode.path("access_token").asText();
        } else {
            throw new RuntimeException("Failed to get access token: " + response.getBody());
        }
    }

    private JsonNode parseResponse(String responseBody) {
        try {
            // JSON 파싱
            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            throw new RuntimeException("엑세스 토큰 받기 실패:");
        }
    }

    // 사용자 정보를 가져오는 메서드
    public String getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me"; // 사용자 정보 요청 URL

        RestTemplate restTemplate = new RestTemplate(); // RestTemplate 객체 생성

        HttpHeaders headers = new HttpHeaders(); // HTTP 헤더 생성
        headers.setBearerAuth(accessToken); // 액세스 토큰을 헤더에 설정

        HttpEntity<String> request = new HttpEntity<>(headers); // HTTP 요청 객체 생성
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class); // 사용자 정보 요청 보내기
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info", e); // 실패 시 예외 발생
        }

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody()); // 응답 본문 파싱
            return rootNode.toString(); // 사용자 정보 반환
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse user info response", e); // 파싱 실패 시 예외 발생
        }
    }
}
