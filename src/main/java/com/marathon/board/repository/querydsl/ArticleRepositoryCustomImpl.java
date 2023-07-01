package com.marathon.board.repository.querydsl;

import java.util.List;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.QArticle;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    /**
     * 1. QuerydslRepositorySupport 역할
     * Querydsl 문법을 사용할 수 있도록 지원.
     * ArticleRepositoryCustom 인터페이스를 구현함으로서 강제로 메서드 오버라이딩.
     *
     * Querydsl의 장점
     * - JPA를 사용하는 경우 연관관계가 없는 두 테이블의 JOIN을 하거나, 복잡한 쿼리문을 사용하기 어렵다.
     *   이럴 때 사용하는게 Querydsl이다.
     * */


    /**
     * super(Article.class) 의미
     * Repository에서 접근할 Entity 타입을 선언.
     * Querydsl 쿼리 작성에 필요한 Q 클래스.(Article -> QArticle generated 폴더에 있다)
     * */
    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    /**
     * Querydsl 코드
     * */
    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        /**
         * 이렇게 distinct, isNotNull 이라는 조건을 넣어서
         * 데이터를 뽑아낼 수 있다.
         * */
        return from(article)
                                    .distinct()
                                    .select(article.hashTag)
                                    .where(article.hashTag.isNotNull())
                                    .fetch();

    }
}
