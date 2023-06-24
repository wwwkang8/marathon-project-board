package com.marathon.board.config;

import com.marathon.board.domain.UserAccount;
import com.marathon.board.dto.UserAccountDto;
import com.marathon.board.dto.security.BoardPrincipal;
import com.marathon.board.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * 1) /css, /img 등과 같은 공통 파일에 대한 접근은 허용
     * 2) GET, /, /articles, /articles/search-hashtag 허용
     * 3) 그 외의 URL로 접근하는 요청은 인증 필요.
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth->auth
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/",
                        "/articles",
                        "/articles/search-hashtag"
                    ).permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin().and()
                .logout()
                .logoutSuccessUrl("/")
                .and().build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        /**
//         * ignore란?
//         * 스프링 시큐리티 검증에서 아예 제외하겠다는 뜻.
//         * static resources, css - js 등등. 이런것들은 권한체크가 필요없다.
//         *
//         * 정적 리소스를 담는 폴더들을 이미 atCommonLocations에서 정의를 했다.
//         * 그래서 저렇게 설정하면 정적 리소스는 체크 제외.
//         *
//         * 하지만 이렇게 ignore 하는 것은 보안상으로 안좋기 때문에 권장하지 않음.
//         * */
//
//        return (web)->web.ignoring().requestMatchers();
//    }

    /**
     * UserDetailsService
     * 스프링 시큐리티가 사용하는 인터페이스로, 사용자의 인증과 관련된 정보를 제공하는 역할.
     *
     * 아래 메서드 목적
     * UserAccountRepository에서 사용자 정보를 username으로 가져오고, 이를 가지고
     * BoardPrincipal 객체를 생성하여 반환.
     *
     * 1) UserAccountRepository.findById(username)
     * username에 해당하는 사용자정보를 조회
     *
     * 2) UserAccountDto::from
     * UserAccountDto 객체로 변환한다. UserAccountDto는 사용자 정보를 담은 데이터전송 객체.
     *
     * 3) BoardPrincipal::from
     * 인증된 사용자정보를 담은 Principal 객체로 만든다.
     * */
    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return username -> userAccountRepository
                                .findById(username)
                                .map(UserAccountDto::from)
                                .map(BoardPrincipal::from)
                                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - username : " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 왜 쓰는지 보기
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
