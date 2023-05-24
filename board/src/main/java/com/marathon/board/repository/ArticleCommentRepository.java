package com.marathon.board.repository;

import java.util.List;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.ArticleComment;
import com.marathon.board.domain.QArticle;
import com.marathon.board.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleCommentRepository extends
    JpaRepository<ArticleComment, Long>,
    QuerydslPredicateExecutor<ArticleComment>,
    QuerydslBinderCustomizer<QArticleComment>
{
    List<ArticleComment> findByArticle_Id(Long articleId);

    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {

        // 선택적으로 특정 필드에 대해서 검색 가능하게 하기 위한 장치
        bindings.excludeUnlistedProperties(true); //일단 검색에서 제외하도록 하고
        bindings.including(root.content, root.createdAt, root.modifiedAt); //이 필드들만 검색 대상
        bindings.bind(root.content).first((StringExpression::containsIgnoreCase)); // like '%value%' 쿼리를 생성한다
        bindings.bind(root.createdAt).first((DateTimeExpression::eq)); // like '%value%' 쿼리를 생성한다
        bindings.bind(root.createdBy).first((StringExpression::containsIgnoreCase)); // like '%value%' 쿼리를 생성한다

    }

}
