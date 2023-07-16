package com.marathon.board.dto.request;

import java.util.Set;

import com.marathon.board.domain.Hashtag;
import com.marathon.board.dto.ArticleDto;
import com.marathon.board.dto.HashtagDto;
import com.marathon.board.dto.UserAccountDto;

public record ArticleRequest(
    String title,
    String content
) {

    /**
     * record 클래스 사용이유
     * 불변객체를 만들어주기 위함.
     * 자동으로 title, content, hashtag가 private final로 선언됨.
     * 모든 필드를 초기화하는 RequiredAllArgument 생성자가 생성된다.
     * 각 필드의 이름을 딴 getter가 자동으로 생성됨.
     * */

    public static ArticleRequest of(String title, String content) {
        /**
         * of 메서드
         * 이는 정적팩토리 메서드이다.
         * 정적팩토리 메서드를 사용하면 객체생성의 편의성이 좋아지고, 명명규칙이 생기고, 불변객체 생성의 장점이 있다.
         * */

        return new ArticleRequest(title, content);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        return toDto(userAccountDto, null);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto, Set<HashtagDto> hashtagDtos) {
        return ArticleDto.of(
            userAccountDto,
            title,
            content,
            hashtagDtos
        );
    }

}
