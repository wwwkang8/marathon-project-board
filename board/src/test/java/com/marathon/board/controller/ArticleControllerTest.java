package com.marathon.board.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.marathon.board.config.SecurityConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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

    @DisplayName("[view][GET] 게시글 리스트 {게시판} 페이지 - 정상 호출")
    @Test
    public void given_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"));
    }

    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    public void given_whenRequestingArticleView_thenReturnArticleView() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/articles/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("/articles/detail"))
            .andExpect(model().attributeExists("article"))
            .andExpect(model().attributeExists("articleComments"));
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


}