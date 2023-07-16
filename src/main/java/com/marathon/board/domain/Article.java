package com.marathon.board.domain;

import java.time.LocalDateTime;
import java.util.Collection;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@ToString(callSuper = true)
@Table(indexes = {
    @Index(columnList="title"),
    @Index(columnList="createdAt"),
    @Index(columnList="createdBy")
})
@Entity
public class Article extends AuditingFields {

  //본문 인덱스 : 본문검색에는 인덱스를 걸지 않는다. 너무 길어서 본문에는 인덱스 X. 그리고 인덱스에 용량이 한계가 있다.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // JoinColumn을 사용하여 외래키(FK) 칼럼을 지정하고, 엔티티간의 관계를 설정
  @Setter @ManyToOne(optional = false) @JoinColumn(name = "userId") private UserAccount userAccount; // 유저 정보 (ID)

  @Setter
  @Column(nullable = false)
  private String title; // 제목
  @Setter
  @Column(nullable = false, length = 10000)
  private String content; // 본문

  /**
   * 이 Article에 연동된 comment는 중복을 허용하지 않고
   * 다 여기서 모아서 컬렉션으로 보겠다는 의도.
   * mappedBy : article 테이블로부터 온 것이다라는 표기
   * cascade : cascade가 강하게 묶여있으면 데이터 마이그레이션, 데이터 삭제할 때 불편한 점이 있다.
   * 그래서 일부러 foreign키를 안 걸고 운영하는 경우도 있다.
   * 다만 여기는 공부 목적이기 때문에 cascade를 건다.
   *
   * @ToString.Exclude를 지운 이유는 Article, ArticleComments를 Tostring을 하다보면
   * 순환참조가 일어나서 메모리가 터진다. ArticleComment에서 Article을 찾는 일은 많지만, 반대는 적기 때문에
   * Article에서 지우는게 맞다. 한쪽만 지우면 된다.
   * */
  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
  @OrderBy("createdAt DESC")
  private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

  @ToString.Exclude
  @JoinTable(
      name = "article_hashtag",
      joinColumns = @JoinColumn(name = "articleId"),
      inverseJoinColumns = @JoinColumn(name = "hashtagId")
  )
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<Hashtag> hashtags = new LinkedHashSet<>();

  public void addHashtag(Hashtag hashtag) {
    this.getHashtags().add(hashtag);
  }

  /** 사용을 편하게 하기 위해서 Set에 추가, 삭제를 위한 것 만듬 */
  public void addHashtags(Collection<Hashtag> hashtags) {
    this.getHashtags().addAll(hashtags);
  }

  public void clearHashtags() {
    this.getHashtags().clear();
  }


  protected Article (){

  }

  private Article(UserAccount userAccount, String title, String content) {
    this.userAccount = userAccount;
    this.title = title;
    this.content = content;
  }

  public static Article of(UserAccount userAccount, String title, String content) {
    return new Article(userAccount, title, content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Article article)) {
      return false;
    }

    // 아직 영속화가 되지 않았다면 id가 null일 수 있기 때문에 null체크도 해야한다.
    // Entitiy를 데이터베이스에 영속화 되기 전이면 동등성 검사에서 false
    return id != null && id.equals(article.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
