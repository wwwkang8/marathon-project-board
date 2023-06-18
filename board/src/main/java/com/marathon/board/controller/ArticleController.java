package com.marathon.board.controller;

import java.util.List;

import com.marathon.board.domain.constant.FormStatus;
import com.marathon.board.domain.constant.SearchType;
import com.marathon.board.dto.UserAccountDto;
import com.marathon.board.dto.request.ArticleRequest;
import com.marathon.board.dto.response.ArticleResponse;
import com.marathon.board.dto.response.ArticleWithCommentsResponse;
import com.marathon.board.service.ArticleService;
import com.marathon.board.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;


    /**
     * 사용 목적 : 검색타입, 검색어, 페이징을 파라메터로 받아서
     * article 리스트를 응답해주는 api
     * */
    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ){
        /**
         * ModelMap 사용하는 이유
         * ModelMap에 데이터만 저장하여 View에서 사용하기 위해서이다.
         * ModelAndView는 데이터와 View 화면명 정보까지 같이 저장한다.
         *
         * @RequestParam : URL에서 파라미터 값과 이름을 함께 전달하는 방식으로 게시판 등에서 페이지 및 검색정보를
         * 함께 전달하는 방식으로 사용할 때 많이 쓴다. 주로 GET방식의 통신을 할 때 쓴다.
         * */

        /**
        * from 메서드 : 도메인 객체를 데이터전송객체(DTO)로 변환하는 용도로 사용.
        * articleService.searchArticles 메서드로 결과를 받아오고
        * .map(ArticleResponse::from)을 사용해서 Article객체를 ArticleResponse 객체로 변환.
        *
        * Page 인터페이스는 페이지 관련 정보와 함께 특정페이지의 데이터를 가져오는 기능을 한다.
         * 주요한 메서드로는 페이지번호, 페이지 크기 등이 있다.
        * */
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);

        /** SearchType ENUM값을 Values()로 배열 형태로 "searchTypes" 변수로 저장.
         * 이것을 Thymeleaf 화면상에서 받아서 검색옵션으로 뿌려준다. */
        map.addAttribute("searchTypes", SearchType.values());


        return "articles/index";
    }

    /**
     * 사용 목적 : /게시글ID 로 호출을 하면 해당 게시글과 게시글의 댓글을 반환하여
     * 게시글 상세페이지에서 보여준다.
     * */
    @GetMapping("/{articleId}")
    public String article(ModelMap map, @PathVariable Long articleId){
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());

        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(
        @RequestParam(required = false) String searchValue,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        ModelMap map
    ) {
        /**
         * 목적 : 해시태그를 조회해서 가져오는 메서드.
         * searchArticlesViaHashtag() : ArticleService로부터 해시태그에 딸린 게시글 목록을 조회받는다.
         * Pagination 처리
         * getHashtags() : ArticleService로부터 해시태그 목록을 받아온다.
         * */

        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();

        /**
         * View에 뿌려주기 위해서 model 객체에 ArticleService로부터 받아온
         * 데이터들을 추가해주는 로직.
         * */
        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map){
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }


    @PostMapping("/form")
    public String postNewArticle(ArticleRequest articleRequest) {
        //TODO : 인증정보를 넣어줘야 한다.
        articleService.saveArticle(articleRequest.toDto(UserAccountDto.of(
            "uno", "asdf1234", "uno@mail.com", "Uno", "memo", null, null, null, null
        )));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map){
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId, ArticleRequest articleRequest) {
        // TODO:인증정보를 넣어줘야 한다
        articleService.updateArticle(articleId, articleRequest.toDto(UserAccountDto.of(
            "uno", "asdf1234", "uno@mail.com", "Uno", "memo", null, null, null, null
        )));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId) {
        // TODO:인증정보를 넣어줘야 한다
        articleService.deleteArticle(articleId);

        return "redirect:/articles";
    }

}
