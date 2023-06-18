package com.marathon.board.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.UserAccount;

/**
 * A DTO for the {@link com.marathon.board.domain.Article} entity
 * record로 만들어졌는데 이게 뭐지??
 */
public record ArticleDto(
    Long id,
    UserAccountDto userAccountDto,
    String title,
    String content,
    String hashtag,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, String hashtag) {
        return new ArticleDto(null, userAccountDto, title, content, hashtag, null, null, null, null);
    }

    public static ArticleDto from(Article entity) {
        return new ArticleDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getTitle(),
            entity.getContent(),
            entity.getHashTag(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    /**
     * toEntity 메서드 사용이유
     * Article 엔티 객체를 생성해서 반환하는 메서드
     * 장점
     * 1) DTO -> Entity로 변환
     * 2) 객체생성의 편의성 : 엔티티생성에 필요한 매개변수 강제할수 있다.
     * */
    public Article toEntity(UserAccount userAccount) {
        return Article.of(
            userAccount,
            title,
            content,
            hashtag
        );
    }

}