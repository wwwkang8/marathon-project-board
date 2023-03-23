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
@ToString
@Table(indexes = {
    @Index(columnList="content"),
    @Index(columnList="createdAt"),
    @Index(columnList="createdBy")
})
@EntityListeners(AuditingEntityListener.class)
public class ArticleComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(optional = false)
  private Article article;

  @Setter
  @Column(nullable = false, length = 500)
  private String content; // 내용

  /** JPA Auditing 기능 : 날짜 일시가 자동으로 세팅되는 기능
   JpaConfig에 @EnableAuditing 어노테이션을 설정
   AuditAware을 사용해서 수정, 생성자가 자동으로 저장된다
   */
  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdAt; // 생성일시
  @CreatedBy
  @Column(nullable = false, length = 100)
  private String createdBy; // 생성자
  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime modifiedAt; // 수정일시
  @LastModifiedBy
  @Column(nullable = false, length = 100)
  private String modifiedBy; // 수정자


  protected ArticleComment() {
  }

  private ArticleComment(Article article, String content) {
    this.article = article;
    this.content = content;
  }

  public static ArticleComment of(Article article, String content) {
    return new ArticleComment(article, content);
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
