package com.marathon.board.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.marathon.board.config.JpaConfig;
import com.marathon.board.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest(
        @Autowired ArticleRepository articleRepository,
        @Autowired ArticleCommentRepository articleCommentRepository
    ) {
      this.articleRepository = articleRepository;
      this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("Select Test")
    @Test
    void givenTestData_whenSelecting_thenWorksFine(){
        // Given
        long previousCount = articleRepository.count();


        // When
        List<Article> articles = articleRepository.findAll();



        // Then
        Assertions.assertThat(articles)
            .isNotNull()
            .hasSize(1);
    }

    @DisplayName("Insert Test")
    @Test
    void givenTestData_whenInserting_thenWorksFine(){
        // Given
        long previousCount = articleRepository.count();


        // When
        Article savedArticles = articleRepository.save(Article.of("new article", "new content", "#hash"));


        // Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("Update Test")
    @Test
    void givenTestData_whenUpdating_thenWorksFine(){
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashTag = "#springboot";
        article.setHashTag(updatedHashTag);

        // When
        Article savedArticle = articleRepository.saveAndFlush(article);

        // Then
        Assertions.assertThat(savedArticle).hasFieldOrPropertyWithValue("hashTag", updatedHashTag);
    }

    @DisplayName("Delete Test")
    @Test
    void givenTestData_whenDeleting_thenWorksFine(){
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousAricleComment = articleCommentRepository.count();
        long deletedCommentsSize = article.getArticleComments().size(); // 양방향 바인딩으로 article에서 articleComment를 가져온다.


        // When
        articleRepository.delete(article);

        // Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        //Assertions.assertThat(articleCommentRepository.count()).isEqualTo(previousAricleComment - 1);
    }
}