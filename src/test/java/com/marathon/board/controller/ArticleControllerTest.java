package com.marathon.board.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.marathon.board.config.SecurityConfig;
import com.marathon.board.config.TestSecurityConfig;
import com.marathon.board.domain.constant.FormStatus;
import com.marathon.board.domain.constant.SearchType;
import com.marathon.board.dto.ArticleDto;
import com.marathon.board.dto.ArticleWithCommentsDto;
import com.marathon.board.dto.UserAccountDto;
import com.marathon.board.dto.request.ArticleRequest;
import com.marathon.board.dto.response.ArticleResponse;
import com.marathon.board.service.ArticleService;
import com.marathon.board.service.PaginationService;
import com.marathon.board.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("View 컨트롤러 - 게시글")
@WebMvcTest(ArticleController.class) //이렇게 클래스 지정하면 모든 컨트롤러를 빈으로 생성하지 않고, 특정 클래스만 빈 생성. 부하 줄인다
@Import({TestSecurityConfig.class, FormDataEncoder.class})
class ArticleControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    public ArticleControllerTest(@Autowired MockMvc mvc,@Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @MockBean
    private ArticleService articleService;

    @MockBean
    private PaginationService paginationService;


    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 정상 호출")
    @Test
    public void given_whenSearchingArticlesView_thenReturnArticlesView() throws Exception {
        //Given
        SearchType searchType=SearchType.TITLE;
        String searchValue = "title";
        given(articleService.searchArticles(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));

        //When & Then
        mvc.perform(get("/articles")
                .queryParam("searchType", searchType.name())
                .queryParam("searchValue", searchValue)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attributeExists("searchTypes"));
        then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
    }

    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 검색어와 함께 호출")
    @Test
    public void givenSearchWord_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {
        //Given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());

        //When & Then
        mvc.perform(get("/articles"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"));
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 페이지 - 정상 호출, 인증된 사용자")
    @Test
    public void given_whenRequestingArticleView_thenReturnArticleView() throws Exception {
        //Given
        Long articleId = 1L;
        long totalCount = 1L;
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
        given(articleService.getArticleCount()).willReturn(totalCount);

        //When & Then
        mvc.perform(get("/articles/" + articleId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/detail"))
            .andExpect(model().attributeExists("article"))
            .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticleWithComments(articleId);
        then(articleService).should().getArticleCount();
    }

    @WithMockUser
    @DisplayName("[View][GET] 새 게시글 작성 페이지")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewArticlePage() throws Exception {
        //given

        //when&then
        mvc.perform(get("/articles/form"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/form"))
            .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithUserDetails(value="unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSavesNewArticle() throws Exception {
        /**등록하는 것에는 실제 유저정보가 있어야한다
         * Test_Excution :
         * userDetailsServiceBeanName = "userDetailsService" bean이 유일해서 알아서 잘 찾는다.
         */

        // Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        // When & Then
        mvc.perform(
                post("/articles/form")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(articleRequest))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles"))
            .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 수정 페이지")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedArticlePage() throws Exception {
        // Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/form"))
            .andExpect(model().attribute("article", ArticleResponse.from(dto)))
            .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(articleService).should().getArticle(articleId);
    }

    @WithUserDetails(value="unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenUpdatedArticleInfo_whenRequesting_thenUpdatesNewArticle() throws Exception {
        // Given
        long articleId = 1L;
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).updateArticle(eq(articleId), any(ArticleDto.class));

        // When & Then
        mvc.perform(
                post("/articles/" + articleId + "/form")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(articleRequest))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles/" + articleId))
            .andExpect(redirectedUrl("/articles/" + articleId));
        then(articleService).should().updateArticle(eq(articleId), any(ArticleDto.class));
    }

    @WithUserDetails(value="unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeletesArticle() throws Exception {
        // Given
        long articleId = 1L;
        String userId = "unoTest";
        //작성자만 게시글을 삭제할 수 있도록 변경
        willDoNothing().given(articleService).deleteArticle(articleId, userId);

        // When & Then
        mvc.perform(
                post("/articles/" + articleId + "/delete")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles"))
            .andExpect(redirectedUrl("/articles"));
        then(articleService).should().deleteArticle(articleId, userId);
    }


    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색전용페이지 - 정상 호출")
    @Test
    public void given_whenRequestingArticleSearchView_thenReturnArticleSearchView() throws Exception {
        //Given


        //When & Then
        mvc.perform(get("/articles/search"))
            .andExpect(status().isOk())
            .andExpect(view().name("/articles/search"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

    }

    @DisplayName("[view][GET] 게시글 해시태그 검색페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchHashtagView_thenReturnsArticleSearchHashtagView() throws Exception {
        //Given
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));
        given(articleService.getHashtags()).willReturn(hashtags);

        //When & Then
        mvc.perform(get("/articles/search-hashtag"))
            .andExpect(status().isOk())
            .andExpect(view().name("articles/search-hashtag"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(model().attribute("articles", Page.empty()))
            .andExpect(model().attribute("hashtags",hashtags))
            .andExpect(model().attributeExists("paginationBarNumbers"))
            .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        // 동작결과 검사
        then(articleService).should().searchArticlesViaHashtag(eq(null), any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색페이지 - 정상 호출, 해시태그 입력한 경우")
    @Test
    public void givenHashtag_whenRequestingArticleSearchHashtagView_thenReturnsArticleSearchHashtagView() throws Exception {
        //Given
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        String hashtag = "#java";
        given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));
        given(articleService.getHashtags()).willReturn(hashtags);

        //When & Then
        mvc.perform(get("/articles/search-hashtag")
                .queryParam("searchValue", hashtag)
                // 해시태그를 입력해서 searchValue 이름으로 hashtag 파라메터 전달
            )
            .andExpect(status().isOk())
            .andExpect(view().name("articles/search-hashtag"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(model().attribute("articles", Page.empty()))
            .andExpect(model().attribute("hashtags",hashtags))
            .andExpect(model().attributeExists("paginationBarNumbers"))
            .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        // 동작결과 검사
        then(articleService).should().searchArticlesViaHashtag(eq(hashtag), any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto(){
        return ArticleWithCommentsDto.of(
            1L,
            createUserAccountDto(),
            Set.of(),
            "title",
            "content",
            "#java",
            LocalDateTime.now(),
            "jeongho",
            LocalDateTime.now(),
            "Jake"
        );
    }

    private UserAccountDto createUserAccountDto(){
        return UserAccountDto.of(
            "Jake",
            "pw"
            ,"wow@gmail.com",
            "jake",
            "memo",
            LocalDateTime.now(),
            "Uno",
            LocalDateTime.now(),
            "jake"
            );
    }

    private ArticleDto createArticleDto() {
        return ArticleDto.of(
            createUserAccountDto(),
            "title",
            "content",
            "#java"
        );
    }




}