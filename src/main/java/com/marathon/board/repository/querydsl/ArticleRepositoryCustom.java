package com.marathon.board.repository.querydsl;

import java.util.Collection;
import java.util.List;

import com.marathon.board.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ArticleRepositoryCustom 인터페이스를 만들어서
 * findAllDistinctHashtags 메서드를 선언해서 이 인터페이스를 구현할 때
 * findAllDistinctHashtags 메서드를 강제로 구현해야 한다.
 * */
public interface ArticleRepositoryCustom {

    /**
     * @deprecated 해시태그 도메인을 새로 만들었으므로 이 코드는 더 이상 사용할 필요 없다.
     */
    @Deprecated
    List<String> findAllDistinctHashtags();
    Page<Article> findByHashtagNames(Collection<String> hashtagNames, Pageable pageable);

}
