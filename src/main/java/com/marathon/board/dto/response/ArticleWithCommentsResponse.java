package com.marathon.board.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

import com.marathon.board.dto.ArticleCommentDto;
import com.marathon.board.dto.ArticleWithCommentsDto;
import com.marathon.board.dto.HashtagDto;

public record ArticleWithCommentsResponse(
    Long id,
    String title,
    String content,
    Set<String> hashtags,
    LocalDateTime createdAt,
    String email,
    String nickname,
    String userId,
    Set<ArticleCommentResponse> articleCommentsResponse
) implements Serializable {

    /**
     * of 메서드
     * 정적팩토리 메서드라고 한다.
     * 생성자 대신 of 메서드를 써서 객체를 생성한다.
     * 장점으로는 객체생성의 편의성, 명명규칙적용, 불변객생성이 있다.
     * of 메서드를 사용하면 입력해야 하는 매개변수를 강제할 수 있기 때문에 장점이 있다.
     * */
    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtags, createdAt, email, nickname, userId, articleCommentResponses);
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
            dto.hashtagDtos().stream()
                .map(HashtagDto::hashtagName)
                .collect(Collectors.toUnmodifiableSet()),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.userAccountDto().userId(),
            organizeChildComments(dto.articleCommentDtos())
        );
    }

    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {

        /**
         * 댓글이 대댓글과 섞여있다.
         * 댓글과 대댓글, 댓글의 레벨을 구분해서 추출하기 위한 방법임.
         * */

        Map<Long, ArticleCommentResponse> map = dtos.stream()
            .map(ArticleCommentResponse::from)
            .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));
        map.values().stream()
            .filter(ArticleCommentResponse::hasParentComment)
            .forEach(comment -> {
                ArticleCommentResponse parentComment = map.get(comment.parentCommentId());
                parentComment.childComments().add(comment);
            });

        /** ! 느낌표가 의미하는 것은 자식댓글인지 확인하는 것. */
        return map.values().stream()
            .filter(comment -> !comment.hasParentComment())
            .collect(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(ArticleCommentResponse::createdAt) //생성시각 내림차순, id 오름차순
                    .reversed()
                    .thenComparingLong(ArticleCommentResponse::id)
                )));
    }

}
