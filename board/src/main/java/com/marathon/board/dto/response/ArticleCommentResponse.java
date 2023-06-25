package com.marathon.board.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.marathon.board.dto.ArticleCommentDto;

public record ArticleCommentResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    String email,
    String nickname,
    String userId
) implements Serializable {

    /**
     * of 메서드
     * 이는 정적팩토리 메서드이다.
     * 정적팩토리 메서드를 사용하면 객체생성의 편의성이 좋아지고, 명명규칙이 생기고, 불변객체 생성의 장점이 있다.
     * */
    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        return new ArticleCommentResponse(id, content, createdAt, email, nickname, userId);
    }

    /**
     * from 메서드
     * 서로 다른 데이 모델간의 전환에 주로 사용된다.
     * 주로 DTO나 엔티티 객체와 같은 다른 모델의 데이터를 응답 객체로 변환하는데 사용한다.
     * */
    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleCommentResponse(
            dto.id(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.userAccountDto().userId()
        );
    }

}
