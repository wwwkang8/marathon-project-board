package com.marathon.board.service;

import java.time.LocalDateTime;
import java.util.List;

import com.marathon.board.domain.type.SearchType;
import com.marathon.board.dto.ArticleDto;
import com.marathon.board.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;


    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType title, String searchKeyword) {
        // Page 안에 이미 페이지네이션 구현이 되어있다.
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleDto searchArticle(long l) {

        ArticleDto articleDto = ArticleDto.of(LocalDateTime.now(), "Jake", "hello", "wow good", "firstcontent");

        return articleDto;
    }


}
