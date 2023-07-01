package com.marathon.board.controller;

import com.marathon.board.dto.UserAccountDto;
import com.marathon.board.dto.request.ArticleCommentRequest;
import com.marathon.board.dto.security.BoardPrincipal;
import com.marathon.board.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(
        ArticleCommentRequest articleCommentRequest,
        @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {

        //TODO : 인증정보를 넣어줘야 한다.

        /**
         * 사용 목적 : 댓글 생성
         * ArticleCommentRequest를 toDto 함수로 댓글 정보를 생성.
         * ArticleRequest는 record 클래스. DTO로 사용된다.
         * */
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleCommentRequest.articleId();

    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(
                @PathVariable Long commentId,
                @AuthenticationPrincipal BoardPrincipal boardPrincipal,
                Long articleId) {
        // 게시글의 아이디와 댓글아이디 모두 필요.
        // 게시글 아이디를 알아야 하는 이유는 댓글 수정 후 view에 랜딩화면을 보여주기 위한 것.

        articleCommentService.deleteArticleComment(commentId, boardPrincipal.getUsername());

        return "redirect:/articles/" + articleId;
    }

}
