package com.marathon.board.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

import com.marathon.board.dto.ArticleWithCommentsDto;

public record ArticleWithCommentsResponse(
    Long id,
    String title,
    String content,
    String hashtag,
    LocalDateTime createdAt,
    String email,
    String nickname,
    Set<ArticleCommentResponse> articleCommentResponse
) implements Serializable {

    /**
     * of 메서드
     * 정적팩토리 메서드라고 한다.
     * 생성자 대신 of 메서드를 써서 객체를 생성한다.
     * 장점으로는 객체생성의 편의성, 명명규칙적용, 불변객생성이 있다.
     * of 메서드를 사용하면 입력해야 하는 매개변수를 강제할 수 있기 때문에 장점이 있다.
     * */
    public static ArticleWithCommentsResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtag, createdAt, email, nickname, articleCommentResponses);
    }

    /**
     * from 메서드
     * 서로 다른 데이터 모델간의 변환을 할 때 사용한다.
     * 주로 DTO나 엔티티 객체와 같이 서로 다른 객체를 응답객체로 변환할 때 사용.
     * */
    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.articleCommentDtos().stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

}
