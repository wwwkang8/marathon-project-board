package com.marathon.board.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.marathon.board.domain.Article} entity
 * record로 만들어졌는데 이게 뭐지??
 */
public record ArticleDto(
    LocalDateTime createdAt,
    String createdBy,
    String title,
    String content,
    String hashtag
) {
    public static ArticleDto of(LocalDateTime createdAt, String createdBy, String title, String content, String hashtag) {
        return new ArticleDto(createdAt, createdBy, title, content, hashtag);
    }
}