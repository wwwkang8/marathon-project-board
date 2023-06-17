package com.marathon.board.repository.querydsl;

import java.util.List;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.QArticle;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    /**
     * Querydsl 코드
     * */
    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        return from(article)
                                    .distinct()
                                    .select(article.hashTag)
                                    .where(article.hashTag.isNotNull())
                                    .fetch();

    }
}
