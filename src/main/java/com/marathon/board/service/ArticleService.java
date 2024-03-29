package com.marathon.board.service;


import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.Hashtag;
import com.marathon.board.domain.UserAccount;
import com.marathon.board.domain.constant.SearchType;
import com.marathon.board.dto.ArticleDto;
import com.marathon.board.dto.ArticleWithCommentsDto;
import com.marathon.board.dto.UserAccountDto;
import com.marathon.board.repository.ArticleRepository;
import com.marathon.board.repository.HashtagRepository;
import com.marathon.board.repository.UserAccountRepository;
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
    private final UserAccountRepository userAccountRepository;
    private final HashtagService hashtagService;
    private final HashtagRepository hashtagRepository;

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
            case HASHTAG -> articleRepository.findByHashtagNames(
                    Arrays.stream(searchKeyword.split(" ")).toList(),
                    pageable).map(ArticleDto::from);
        };

    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {

        return articleRepository.findById(articleId)
                                .map(ArticleWithCommentsDto::from)
                                .orElseThrow(()->new EntityNotFoundException("게시글이 없습니다 - articleId : "+articleId));

    }

    @Transactional
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                                .map(ArticleDto::from)
                                .orElseThrow(()->new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());

        Article article = dto.toEntity(userAccount);
        article.addHashtags(hashtags);
        articleRepository.save(article);
    }

    public void updateArticle(Long articleId, ArticleDto dto) {

        try{
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            /** 게시글의 사용자와 인증된 사용자가 일치하는지 여부 확인 */
            if(article.getUserAccount().equals(userAccount)){
                if(dto.title() != null) {article.setTitle(dto.title());}
                // record의 스펙이다. dto.getTitle()이 아니라 .title()로 값 가져오기 가능.
                // java 13, 14에서 새로 나온 기능이다.get 이 없는 title을 볼 수 있다.
                if(dto.content() != null) {article.setContent(dto.content());}

                Set<Long> hashtagIds = article.getHashtags().stream()
                                                .map(Hashtag::getId)
                                                .collect(Collectors.toUnmodifiableSet());
                article.clearHashtags();
                articleRepository.flush();

                hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);

                Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());
                article.addHashtags(hashtags);

            }

        }catch(EntityNotFoundException e){
            log.warn("게사글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
        }

        /**
         * save 함수는 필요없다.
         * 이 안에서는 class level Transaction이 묶여있다.
         * 트랜잭션이 끝날 때, 영속성 컨텍스트는 article이 변한 것을 감지하고, 변한부분에 대해서 쿼리를 날린다/
         * 그래서 save를 명시하지 않아도 자동으로 update 된다.
         * */
        //articleRepository.save(article);


    }

    public void deleteArticle(long articleId, String userId) {

        Article article = articleRepository.getReferenceById(articleId);
        Set<Long> hashtagIds = article.getHashtags().stream()
                                .map(Hashtag::getId)
                                    .collect(Collectors.toUnmodifiableSet());

        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
        articleRepository.flush();

        hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    /**
     * 목적 : 해시태그로 게시글들을 조회해 오는 메서드
     * 1) 해시태그가 null이거나 공백인경우 빈 페이지를 리턴한다
     * 2) findByHashTag : 해시태그를 사용해서 게시글들을 조회해온다.
     * -> 이 때 Querydsl이 필요하다.
     * */
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtagName, Pageable pageable) {

        if(hashtagName == null || hashtagName.isBlank()){
            return Page.empty(pageable); // 해시태그 없으면 빈페이지 보내준다.
        }

        return articleRepository.findByHashtagNames(List.of(hashtagName), pageable).map(ArticleDto::from);

    }

    /**
     * 목적 : 해시태그들을 모두 조회하되, distinct하게 가져온다.
     * */
    public List<String> getHashtags() {

        return hashtagRepository.findAllHashtagNames(); //TODO : hashtagService에서 제공하면 더 좋을 것 같다.
    }

    private Set<Hashtag> renewHashtagsFromContent(String content) {

        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);
        Set<Hashtag> hashtags = hashtagService.findHashtagsByNames(hashtagNamesInContent);
        Set<String> existingHashtagNames = hashtags.stream()
                                            .map(Hashtag::getHashtagName)
                                            .collect(Collectors.toUnmodifiableSet());
        hashtagNamesInContent.forEach(newHashtagName -> {
            if (!existingHashtagNames.contains(newHashtagName)) {
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });

        return hashtags;
    }
}
