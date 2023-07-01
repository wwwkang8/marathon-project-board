package com.marathon.board.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.marathon.board.dto.ArticleDto;

public record ArticleResponse(
    Long id,
    String title,
    String content,
    String hashtag,
    LocalDateTime createdAt,
    String email,
    String nickname
) implements Serializable {

    /**
     * of 메서드
     * 이를 정적팩토리 메서드라고 한다.
     * 생성자를 통해서 객체를 생성하는 것이 아닌 메서드를 통해서 객체를 생성하는 것을
     * 정적팩토리 메서드라고 한다.(이펙티브 자바에 나오는 개념이라고 한다)
     * 장점으로는 객체 생성의 편의성, 불변객체생성, 명명규적용이 있다.
     * */
    public static ArticleResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleResponse(id, title, content, hashtag, createdAt, email, nickname);
    }

    /**
     * from 메서드
     * 서로 다른 데이 모델간의 전환에 주로 사용된다.
     * 주로 DTO나 엔티티 객체와 같은 다른 모델의 데이터를 응답 객체로 변환하는데 사용한다.
     * */
    public static ArticleResponse from(ArticleDto dto) {

        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        // ArticleResponse 라는 응답객체로 만들기 위해서 from 메서드를 사용.
        return new ArticleResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname
        );
    }

}
