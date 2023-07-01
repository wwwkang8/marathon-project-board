package com.marathon.board.config;

import javax.swing.text.html.Option;
import java.util.Optional;

import com.marathon.board.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

  // 어떤 사용자가 수정, 생성을 했는지 조작자 정보를 가져오는 어노테이션
  @Bean
  public AuditorAware<String> auditorAware(){

    /**
     * 코드 해석
     *
     * SecurityContextHolder : 스프링 시큐리를 사용한다면, 시큐리티 정보를 모두 들고 있는 클래스이다.
     * 여기서 getContext()로 SecurityContext를 불러온다.
     * 현재 인증된 사용자의 정보를 포함한다.
     *
     * Optional.ofNullable : SecurityContext가 null인지 확인.
     *
     * Authentication  isAuthenticated 메서드
     * isAuthentication으로 인증여부를 반환한다.
     *
     * getPrincipal() : 현재 사용자를 나타내는 객체.
     *
     * BoardPrincipal.class::cast
     * Principal 객체를 BoardPrincipal 클래스 객체로 캐스팅 하는 것.
     *
     * BoardPrincipal::getUsername
     * 현재 인증된 객체의 사용자 이름을 가져오는 메서드
     *
     * 위의 과정을 거쳐 최종적으로 사용자 이름이 Optional<String> 형태로 반환되며,
     * 이는 AuditorAware 인터페이스의 구현체로 사용될 수 있습니다.
     * 스프링 데이터 JPA는 이 구현체를 통해 엔티티의 생성자나 수정자에 자동으로 사용자 이름을 할당할 수 있습니다.
     *
     * */

    return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                          .map(SecurityContext::getAuthentication)
                          .filter(Authentication::isAuthenticated)
                          .map(Authentication::getPrincipal)
                          .map(BoardPrincipal.class::cast)
                          .map(BoardPrincipal::getUsername);
  }

/**
    람다함수를 쓰지 않으면 Optional 함수를 아래와 같이 만들고 호출해야 함.
    람다함수를 사용하여 위와 같이 코드를 간결하게 사용가능.
    public Optional getOptional(){
      return Optional.of("uno");
    }
 */

}
