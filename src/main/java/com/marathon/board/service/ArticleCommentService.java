package com.marathon.board.service;


import java.util.List;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.ArticleComment;
import com.marathon.board.domain.UserAccount;
import com.marathon.board.dto.ArticleCommentDto;
import com.marathon.board.repository.ArticleCommentRepository;
import com.marathon.board.repository.ArticleRepository;
import com.marathon.board.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    /**
     * 함수용도 : articleId로 조회된 ArticleComment 엔티티들을 ArticleCommentDto 객체로 변환한 후에
     * 리스트로 반환한다.
     * stream() : java8에 나온 함수로 컬렉션의 정렬, 필터를 쉽게 할 수 있게 해준다.(매우유용함!1)
     * */
    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId)
                                        .stream()
                                        .map(ArticleCommentDto::from)
                                        .toList();
    }

    public void saveArticleComment(ArticleCommentDto dto) {

        /**
         * 사용목적 : 댓글을 저장하는 메서드
         * ArticleCommentDto의 toEntity함수로 ArticleComment 도메인으로 저장.
         *
         * getReferenceById : 엔티티를 지연로으로 가져오기 위해서 사용된다.
         * 이 메서드는 실제로 엔티티 인스턴스를 가져오지 않고, 엔티티의 프록시를 반환한다.
         * 이 프록시는 필요한 경우에만 엔티티를 로딩하여 성능 최적화 가능.
         * */

        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            //댓글 생성시 게시글ID와 작성자 정보도 필요
            articleCommentRepository.save(dto.toEntity(article, userAccount));
        }catch(EntityNotFoundException e){
            //log.warn("댓글 저장 실패. 댓글의 게시글을 찾을 수 없습니다 - dto: {}\", dto");
            e.printStackTrace();
        }


    }

    /**
     * @Deprecated 댓글 수정 기능은 클라이언트에서 생각할 점이 많아지기 때문에, 이번 개발에서는 제공하지 않기로 했다.
     * */
    public void updateArticleComment(ArticleCommentDto dto) {

        try{
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if(dto.content() != null) { articleComment.setContent(dto.content()); }
        }catch(EntityNotFoundException e){
            e.printStackTrace();
        }

    }

    public void deleteArticleComment(Long articleCommentId, String userId) {
        articleCommentRepository.deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }

}
