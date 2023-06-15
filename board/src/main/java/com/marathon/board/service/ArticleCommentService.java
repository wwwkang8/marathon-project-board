package com.marathon.board.service;


import java.util.List;

import com.marathon.board.domain.ArticleComment;
import com.marathon.board.dto.ArticleCommentDto;
import com.marathon.board.repository.ArticleCommentRepository;
import com.marathon.board.repository.ArticleRepository;
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

        try{
            articleCommentRepository.save(dto.toEntity(articleRepository.getReferenceById(dto.articleId())));
        }catch(EntityNotFoundException e){
            //log.warn("댓글 저장 실패. 댓글의 게시글을 찾을 수 없습니다 - dto: {}\", dto");
            e.printStackTrace();
        }


    }

    public void updateArticleComment(ArticleCommentDto dto) {

        try{
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if(dto.content() != null) { articleComment.setContent(dto.content()); }
        }catch(EntityNotFoundException e){
            e.printStackTrace();
        }

    }

    public void deleteArticleComment(Long articleCommentId) {
        articleCommentRepository.deleteById(articleCommentId);
    }

}
