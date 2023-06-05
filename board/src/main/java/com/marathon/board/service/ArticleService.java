package com.marathon.board.service;


import com.marathon.board.domain.Article;
import com.marathon.board.domain.type.SearchType;
import com.marathon.board.dto.ArticleDto;
import com.marathon.board.dto.ArticleWithCommentsDto;
import com.marathon.board.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {

        if(searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        /**
         * 리턴할 때 switch문으로 상황에 맞춰서 리턴이 가능하다.
         * */
        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashTag("#"+searchKeyword, pageable).map(ArticleDto::from);
        };

    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {

        return articleRepository.findById(articleId)
                                .map(ArticleWithCommentsDto::from)
                                .orElseThrow(()->new EntityNotFoundException("게시글이 없습니다 - articleId : "+articleId));

    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {

        try{
            Article article = articleRepository.getReferenceById(dto.id());
            if(dto.title() != null) {article.setTitle(dto.title());}
            // record의 스펙이다. dto.getTitle()이 아니라 .title()로 값 가져오기 가능.
            // java 13, 14에서 새로 나온 기능이다.get 이 없는 title을 볼 수 있다.
            if(dto.content() != null) {article.setContent(dto.content());}
            article.setHashTag(dto.hashtag());
        }catch(EntityNotFoundException e){
            log.warn("게사글 업데이트 실패. 게시글을 찾을수 없습니다. - dto: {}", dto);
        }

        /**
         * save 함수는 필요없다.
         * 이 안에서는 class level Transaction이 묶여있다.
         * 트랜잭션이 끝날 때, 영속성 컨텍스트는 article이 변한 것을 감지하고, 변한부분에 대해서 쿼리를 날린다/
         * 그래서 save를 명시하지 않아도 자동으로 update 된다.
         * */
        //articleRepository.save(article);


    }

    public void deleteArticle(long articleId) {

        articleRepository.deleteById(articleId);

    }

}
