package com.marathon.board.domain.projection;

import java.time.LocalDateTime;

import com.marathon.board.domain.UserAccount;
import org.springframework.data.rest.core.config.Projection;

@Projection(name ="withoutPassword", types = UserAccount.class)
public interface UserAccountProjection {
    String getUserId();
    String getEmail();
    String getNickname();
    String getMemo();
    LocalDateTime getCreatedAt();
    String getCreatedBy();
    LocalDateTime getModifiedAt();
    String getModifiedBy();
}
