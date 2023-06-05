package com.marathon.board.domain.projection;

import java.time.LocalDateTime;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.UserAccount;
import org.springframework.data.rest.core.config.Projection;

@Projection(name ="withUserAccount", types = Article.class)
public interface ArticleProjection {
    Long getId();
    UserAccount getUserAccount();
    String getTitle();
    String getContent();
    LocalDateTime getCreatedAt();
    String getCreatedBy();
    LocalDateTime getModifiedAt();
    String getModifiedBy();
}
