package com.sparta.springauth.config;

import com.sparta.springauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
/**
 * SecurityConfig- Spring Security 설정
 */
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;


    /**
     * SecurityFilterChain: 보안 설정을 기반으로 SecurityFilterChain 생성
     *
     * @param http : HttpSecurity객체로, CSRF 비활성화, 세션 관리, 요청 권한, 필터 추가
     * @return SecurityFilterChain객체 생성(http 요청의 보안 규칙이 적용된)
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable())  //csrf 비활성화

                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리를 stateless설정 jwt사용할것

                // 요청에 대한 권한 설정
                .authorizeHttpRequests(authorizeHttpRequests ->
                                authorizeHttpRequests
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                                        .anyRequest().permitAll() // 그 외 모든 요청 접근 허용
                        //.anyRequest().authenticated() // 그 외 모든 요청 인증처리
                )

                // OAuth2 로그인 설정:
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/login") // 이 설정을 통해 카카오 OAuth2 로그인을 처리
                                .defaultSuccessUrl("/loginSuccess") // 이 설정을 통해 카카오 OAuth2 로그인을 처리
                                .failureUrl("/loginFailure") // 로그인 실패 시 리다이렉트할 URL을 설정
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.userService(customOAuth2UserService) // 사용자 정보를 처리할 서비스로 CustomOAuth2UserService를 사용하도록 설정
                                )
                );


        // jwtAuthenticationFilter의 순서를 지정해주기위해 UsernamePasswordAuthenticationFilter전으로 위치 지정
        return http.build();
    }
}

