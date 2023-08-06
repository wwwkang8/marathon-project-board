package com.marathon.board.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.ArticleComment;
import com.marathon.board.dto.ArticleCommentDto;

public record ArticleCommentResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    String email,
    String nickname,
    String userId,
    Long parentCommentId,
    Set<ArticleCommentResponse> childComments
) implements Serializable {

    /**
     * of 메서드
     * 이는 정적팩토리 메서드이다.
     * 정적팩토리 메서드를 사용하면 객체생성의 편의성이 좋아지고, 명명규칙이 생기고, 불변객체 생성의 장점이 있다.
     * */
    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        return ArticleCommentResponse.of(id, content, createdAt, email, nickname, userId, null);
    }

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname
                                    , String userId, Long parentCommentId) {
        /**
         * :: 더블콜론의 사용법 : 메서드 참조할 때 사용한다
         * 1) static 메서드 참조
         * 2) 특정객체의 인스턴스 메서드 참조
         * 3) 특정유형의 임의의 객체에 대한 인스턴스 메서드 참조
         * 4) 생성자 참조.
         * */

        /**
         * Comparing 사용이유
         * 1) ArticleComment(댓글)의 생성시각 오름차순 정렬
         * 2) id 오름차순 정렬
         *
         * 부모댓글에 딸린 자식댓글들은 Comparator의 comparing 함수로 정렬하여보여준다.
         * */
        Comparator<ArticleCommentResponse> childCommentComparator = Comparator
                                    .comparing(ArticleCommentResponse::createdAt)
                                    .thenComparing(ArticleCommentResponse::id);


        return new ArticleCommentResponse(id, content, createdAt, email, nickname, userId, parentCommentId, new TreeSet<>(childCommentComparator));

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

        return ArticleCommentResponse.of(
            dto.id(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.userAccountDto().userId(),
            dto.parentCommentId()
        );
    }


    public boolean hasParentComment() {
        return parentCommentId != null;
    }

}
