package com.marathon.board.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
  @Column(updatable = false) // 업데이트 대상에서 제외한다. 부모댓글이 바뀌는 일은 없다.
  private Long parentCommentId; // 부모댓글 ID. 단방향으로 하기 위해서 Id로 직접 설정.

  @ToString.Exclude
  @OrderBy("createdAt ASC")
  @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL) // 부모 자식댓글이 긴밀해서 cascade 규칙을 모두 열었다.
  private Set<ArticleComment> childComments = new LinkedHashSet<>();


  @Setter
  @Column(nullable = false, length = 500)
  private String content; // 내용


  protected ArticleComment() {
  }

  private ArticleComment(Article article, UserAccount userAccount, Long parentCommentId, String content) {
    this.article = article;
    this.userAccount = userAccount;
    this.parentCommentId = parentCommentId;
    this.content = content;
  }

  /** 도메인이 변경되어도 of 메서드로 인해서 객체생성 하는데에 변경되는 영향도를 차단할 수 있다. */
  public static ArticleComment of(Article article, UserAccount userAccount, String content) {
    return new ArticleComment(article, userAccount, null, content);
  }

  public void addChildComment(ArticleComment child) {
    child.setParentCommentId(this.getId());
    this.getChildComments().add(child);
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
