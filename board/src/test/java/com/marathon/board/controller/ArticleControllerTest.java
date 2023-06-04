package com.marathon.board.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.Set;

import com.marathon.board.config.SecurityConfig;
import com.marathon.board.domain.type.SearchType;
import com.marathon.board.dto.ArticleWithCommentsDto;
import com.marathon.board.dto.UserAccountDto;
import com.marathon.board.service.ArticleService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@DisplayName("View 컨트롤러 - 게시글")
@WebMvcTest(ArticleController.class) //이렇게 클래스 지정하면 모든 컨트롤러를 빈으로 생성하지 않고, 특정 클래스만 빈 생성. 부하 줄인다
@Import(SecurityConfig.class)
class ArticleControllerTest {

    private final MockMvc mvc;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @MockBean
    private ArticleService articleService;


    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 정상 호출")
    @Test
    public void given_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {
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

    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    public void given_whenRequestingArticleView_thenReturnArticleView() throws Exception {
        //Given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentDtos());

        //When & Then
        mvc.perform(get("/articles/" + articleId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("/articles/detail"))
            .andExpect(model().attributeExists("article"))
            .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticle(articleId);
    }

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
    public void given_whenRequestingArticleHashtagSearchView_thenReturnArticleHashtagSearchView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles/search-hashtag"))
            .andExpect(status().isOk())
            .andExpect(view().name("/articles/search-hashtag"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    private ArticleWithCommentsDto createArticleWithCommentDtos(){
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
        return UserAccountDto.of(1L,
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




}