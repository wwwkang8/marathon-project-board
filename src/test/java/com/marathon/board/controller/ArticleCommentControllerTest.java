package com.marathon.board.controller;

import static io.micrometer.core.instrument.binder.http.HttpRequestTags.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import com.marathon.board.config.SecurityConfig;
import com.marathon.board.config.TestSecurityConfig;
import com.marathon.board.dto.ArticleCommentDto;
import com.marathon.board.dto.request.ArticleCommentRequest;
import com.marathon.board.service.ArticleCommentService;
import com.marathon.board.service.PaginationService;
import com.marathon.board.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("View 컨트롤러 - 댓글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(ArticleCommentController.class)
public class ArticleCommentControllerTest {

    private final MockMvc mockMvc;

    private final FormDataEncoder formDataEncoder;

    ArticleCommentControllerTest(
        @Autowired MockMvc mvc,
        @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mockMvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @MockBean
    private ArticleCommentService articleCommentService;


    /**
     * @WithUserDetails 어노테이션
     * 이 어노테이션은 스프링 시큐리티 테스트에서 사용되는 어노테이션으로
     * 특정 사용자로 인증된 상태에서 테스트를 실행하기 위해 사용됩니다.
     *
     * unoTest 라는 사용자로 인증된 상태에서 테스트를 실행하도록 설정.
     * value 속성은 사용자의 이름 또는 식별자를 나타낸다.
     *
     * setupBefore : 테스트 실행이전에 언제 인증을 설정할지를 지정.
     * TEST_EXECUTION은 테스트 실행 이전에 인증을 설정하도록 지정하는 것을 의미한다.
     * */

    @WithUserDetails(value="unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 댓글 등록 - 정상호출")
    @Test
    void givenArticleCommentInfo_whenRequesting_thenSavesNewArticleComment() throws Exception {
        //Given
        long articleId = 1L;
        ArticleCommentRequest request = ArticleCommentRequest.of(articleId, "test comment");
        willDoNothing().given(articleCommentService).saveArticleComment(any(ArticleCommentDto.class));

        //when
        mockMvc.perform(
            post("/comment/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(formDataEncoder.encode(request))
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles" + articleId))
            .andExpect(redirectedUrl("/articles/"+articleId));

        then(articleCommentService).should().saveArticleComment(any(ArticleCommentDto.class));

    }

    @WithUserDetails(value="unoTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 댓글 삭제 - 정상 호출")
    @Test
    void givenArticleCommentIdToDelete_whenRequesting_thenDeletesArticleComment() throws Exception {
        //Given
        long articleId = 1L;
        long articleCommentId = 1L;
        String userId = "unoTest";
        willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId, userId);

        //When & Then
        mockMvc.perform(
            post("/comments/" + articleCommentId +"/delete")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                .with(csrf())
        );

        then(articleCommentService).should().deleteArticleComment(articleCommentId, userId);
    }



}
