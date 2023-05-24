package com.marathon.board.service;

import java.util.List;

import com.marathon.board.domain.type.SearchType;
import com.marathon.board.dto.ArticleDto;
import com.marathon.board.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@DisplayName("게시글 테스트")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnArticleLists(){
        // Given
        //SearchParameters searchParameters = SearchParameters.of();

        // When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");

        // Then
        Assertions.assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnArticle(){
        // Given
        //SearchParameters searchParameters = SearchParameters.of();

        // When
        ArticleDto articles = sut.searchArticle(1L);

        // Then
        Assertions.assertThat(articles).isNotNull();
    }

}
