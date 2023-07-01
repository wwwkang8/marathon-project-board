package com.marathon.board.service;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.UserAccount;
import com.marathon.board.domain.constant.SearchType;
import com.marathon.board.dto.ArticleDto;
import com.marathon.board.dto.ArticleWithCommentsDto;
import com.marathon.board.dto.UserAccountDto;
import com.marathon.board.repository.ArticleRepository;
import com.marathon.board.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    /**
     * BDD : 행위 주도 개발
     * 테스트 대상의 상태변화 테스트를 하는 것.
     * BDD 권장 행동패턴은 Given, When, Then
     * */

    /** Mock 객체를 생성하고 */
    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;

    /** InjectMocks 어노테이션으로 ArticleService에 ArticleRepository Mock 객체를 주입한다. */
    @InjectMocks private ArticleService sut;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // Given : 상태를 만든다
        /**
         * Given : 최초 상태, 출발점 설정.
         * 페이지 설정은 20페이지를 보여질 수 있도록 상태설정.
         * articleRepository.findAll() 했을 때 조회된 페이지가 나오도록 설정
         *
         * When : 어떤 상태의 변화를 가했을때. 즉 메서드를 실행했을 때
         * ArticleService 객체에서 searchArticles 함수를 호출한다.(검색타입null, 검색어null, 최대20페이지)
         *
         * Then : 예상되는 결과는 이렇다.
         * 1) 아무것도 조회되지 않는다. -> 애초에 given에서 아무것도 조회되지 않도록 했기 때문에.
         * 2) then, should 함수의 사용 이유를 모르겠다.
         * */
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    /**
     * Given : 최초 상태, 출발점 설정.
     * 페이지 설정으 20페이지
     * articleRepository에서 TitleContaining 함수로 조회할 때 페이지가 조회되도록 설정.
     *
     * When : 어떤 상태의 변화를 가했을때. 즉 메서드를 실행했을 때
     * searchArticles()를 검색어, 검색타입을 이용해서 호출.
     *
     * Then : 예상되는 결과는 이렇다.
     * 2) then, should 함수의 사용 이유를 모르겠다.
     * */
    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleDto dto = sut.getArticle(articleId);

        // Then
        assertThat(dto)
            .hasFieldOrPropertyWithValue("title", article.getTitle())
            .hasFieldOrPropertyWithValue("content", article.getContent())
            .hasFieldOrPropertyWithValue("hashtag", article.getHashTag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 ID로 조회하면, 댓글 달린 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenReturnsArticleWithComments() {
        //Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //when
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        //then
        assertThat(dto)
            .hasFieldOrPropertyWithValue("title", article.getTitle())
            .hasFieldOrPropertyWithValue("content", article.getContent())
            .hasFieldOrPropertyWithValue("hashtag", article.getHashTag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 달린 게시글이 없으면, 예외를 던진다")
    @Test
    void givenNonexistentArticleId_whenSearchingArticleWithComments_thenThrowsException() {

        //given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //when
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        //then
        assertThat(t)
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("게시글이 없습니다 - articleId : " + articleId);
        then(articleRepository).should().findById(articleId);

    }

    @DisplayName("게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        // Given
        ArticleDto dto = createArticleDto();
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // When
        sut.saveArticle(dto);

        // Then
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // Given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // When
        sut.saveArticle(dto);

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());

        // When
        sut.updateArticle(dto.id(), dto);

        // Then
        assertThat(article)
            .hasFieldOrPropertyWithValue("title", dto.title())
            .hasFieldOrPropertyWithValue("content", dto.content())
            .hasFieldOrPropertyWithValue("hashTag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        /**
        * getReferenceById의 특이한 특징
        *
        * */

        // When
        sut.updateArticle(dto.id(), dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        String userId = "unoTest";
        willDoNothing().given(articleRepository).deleteByIdAndUserAccount_UserId(articleId, userId);

        // When
        sut.deleteArticle(1L, userId);

        // Then
        then(articleRepository).should().deleteByIdAndUserAccount_UserId(articleId, userId);
    }

    @DisplayName("검색어 없이 게시글을 해시태그 검색하면, 빈 페이지를 반환한다")
    @Test
    void givenNoSearchParameters_whenSearchingArticlesViaHashtag_thenReturnEmptyPage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);


        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("게시글을 해시태그 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnArticlePages() {
        // Given
        String hashtag = "#java";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashTag(hashtag, pageable)).willReturn(Page.empty(pageable));


        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashTag(hashtag, pageable);
    }

    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        // Given : 주어진 상태
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);


        // When : 테스트 대상의 행위로 인해 상태 변화가 가해지면
        List<String> actualHashtags = sut.getHashtags();

        // Then : 실행결과로서 기대하는 상태로 완료되어야함.
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();
    }


    private UserAccount createUserAccount() {
        return UserAccount.of(
            "uno",
            "password",
            "uno@email.com",
            "Uno",
            null
        );
    }

    private Article createArticle() {
        Article article = Article.of(
            createUserAccount(),
            "title",
            "content",
            "#java"
        );
        ReflectionTestUtils.setField(article, "id", 1L);

        return article;
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(
            1L,
            createUserAccountDto(),
            title,
            content,
            hashtag,
            LocalDateTime.now(),
            "Uno",
            LocalDateTime.now(),
            "Uno");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
            "uno",
            "password",
            "uno@mail.com",
            "Uno",
            "This is memo",
            LocalDateTime.now(),
            "uno",
            LocalDateTime.now(),
            "uno"
        );
    }

}
