    package com.marathon.board.repository;

    import com.marathon.board.domain.Article;
    import com.marathon.board.domain.QArticle;
    import com.querydsl.core.types.dsl.DateTimeExpression;
    import com.querydsl.core.types.dsl.SimpleExpression;
    import com.querydsl.core.types.dsl.StringExpression;
    import com.querydsl.core.types.dsl.StringExpressions;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.querydsl.QuerydslPredicateExecutor;
    import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
    import org.springframework.data.querydsl.binding.QuerydslBindings;
    import org.springframework.data.rest.core.annotation.RepositoryRestResource;

    @RepositoryRestResource
    public interface ArticleRepository extends
        JpaRepository<Article, Long> ,
        QuerydslPredicateExecutor<Article>,
        QuerydslBinderCustomizer<QArticle>
    {
        @Override
        default void customize(QuerydslBindings bindings, QArticle root) {
            // 선택적으로 특정 필드에 대해서 검색 가능하게 하기 위한 장치
            // 인터페이스인데 인터페이스 내부에 메서드를 구현할 수 있다. => java8부터 가능
            bindings.excludeUnlistedProperties(true); //일단 검색에서 제외하도록 하고
            bindings.including(root.title, root.content, root.hashTag, root.createdAt, root.modifiedAt); //이 필드들만 검색 대상
            bindings.bind(root.title).first((StringExpression::containsIgnoreCase)); // like '%value%' 쿼리를 생성한다
            bindings.bind(root.content).first((StringExpression::containsIgnoreCase)); // like '%value%' 쿼리를 생성한다
            bindings.bind(root.hashTag).first((StringExpression::containsIgnoreCase)); // like '%value%' 쿼리를 생성한다
            bindings.bind(root.createdAt).first((DateTimeExpression::eq)); // like '%value%' 쿼리를 생성한다
            bindings.bind(root.createdBy).first((StringExpression::containsIgnoreCase)); // like '%value%' 쿼리를 생성한다

        }

    }

