package com.marathon.board.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("Data REST 테스트")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
//@WebMvcTest 미사용 이유 확인하기.
public class DataRestTest {

    // WebMvc 테스트는 내부적으로 MockMvc를 사용할 수 있게 해준다.

    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    void givenNotion_whenRequestArticles_thenReturnArticlesJsonResponse() throws Exception {
        //Given

        //When & Then
        // 단축키1 : 컨트롤 + 스페이스 : MockBuilderGet 사용
        // 단축키2: 옵션 + 스페이스 => 항상 스태틱으로 가져오기
        mvc.perform(get("/api/articles"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
            .andDo(print());

        /**
         * 테스트 실패 이유
         * WebMvcTest는 슬라이스 테스트이다.
         * 컨트롤러 이외의 빈은 로드하지 않는다. 붎필요하다고 생각하는 빈은 생성하지 않는다
         * DataRest의 AutoConfiguration을 읽지 않는다
         * 그래서 가장 간단한 방법으로는 이 테스트를 IntegrationTest로 하는 것이다.
         * */

    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void givenArticleId_whenRequestArticles_thenReturnArticlesJsonResponse() throws Exception {
        //Given

        //When & Then
        // 단축키1 : 컨트롤 + 스페이스 : MockBuilderGet 사용
        // 단축키2: 옵션 + 스페이스 => 항상 스태틱으로 가져오기
        mvc.perform(get("/api/articles/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
            .andDo(print());
    }

    @DisplayName("[api] 게시글 댓글 조회")
    @Test
    void givenArticleId_whenRequestArticleComments_thenReturnArticlesCommentJsonResponse() throws Exception {
        //Given

        //When & Then
        // 단축키1 : 컨트롤 + 스페이스 : MockBuilderGet 사용
        // 단축키2: 옵션 + 스페이스 => 항상 스태틱으로 가져오기
        mvc.perform(get("/api/articles/1/articleComments"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
            .andDo(print());
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestArticleComments_thenReturnArticlesCommentJsonResponse() throws Exception {
        //Given

        //When & Then
        // 단축키1 : 컨트롤 + 스페이스 : MockBuilderGet 사용
        // 단축키2: 옵션 + 스페이스 => 항상 스태틱으로 가져오기
        mvc.perform(get("/api/articleComments"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
            .andDo(print());
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void givenNothing_whenRequestArticleComment_thenReturnArticleCommentJsonResponse() throws Exception {
        //Given

        //When & Then
        // 단축키1 : 컨트롤 + 스페이스 : MockBuilderGet 사용
        // 단축키2: 옵션 + 스페이스 => 항상 스태틱으로 가져오기
        mvc.perform(get("/api/articleComments/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
            .andDo(print());
    }
}
