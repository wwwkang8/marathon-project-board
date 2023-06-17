package com.marathon.board.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@ToString(callSuper = true)
@Table(indexes = {
    @Index(columnList="content"),
    @Index(columnList="createdAt"),
    @Index(columnList="createdBy")
})
@EntityListeners(AuditingEntityListener.class)
public class ArticleComment extends AuditingFields {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(optional = false)
  private Article article;

  @Setter
  @ManyToOne(optional = false)
  @JoinColumn( name = "userId") // JoinColumn을 사용하여 외래키(FK) 칼럼을 지정하고, 엔티티간의 관계를 설정
  private UserAccount userAccount; // 유저 정보 (ID)

  @Setter
  @Column(nullable = false, length = 500)
  private String content; // 내용


  protected ArticleComment() {
  }

  private ArticleComment(Article article, UserAccount userAccount, String content) {
    this.article = article;
    this.userAccount = userAccount;
    this.content = content;
  }

  public static ArticleComment of(Article article, UserAccount userAccount, String content) {
    return new ArticleComment(article, userAccount, content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ArticleComment that)) {
      return false;
    }
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
