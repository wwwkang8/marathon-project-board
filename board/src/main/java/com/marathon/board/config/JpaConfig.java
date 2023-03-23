package com.marathon.board.config;

import javax.swing.text.html.Option;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

  // 어떤 사용자가 수정, 생성을 했는지 조작자 정보를 가져오는 어노테이션
  @Bean
  public AuditorAware<String> auditorAware(){
    return () -> Optional.of("uno"); //TODO : 스프링 시큐리티로 인증기능을 붙일 때 수정하자.
  }

/**
    람다함수를 쓰지 않으면 Optional 함수를 아래와 같이 만들고 호출해야 함.
    람다함수를 사용하여 위와 같이 코드를 간결하게 사용가능.
    public Optional getOptional(){
      return Optional.of("uno");
    }
 */

}
