package com.marathon.board.repository.querydsl;

import java.util.List;

/**
 * ArticleRepositoryCustom 인터페이스를 만들어서
 * findAllDistinctHashtags 메서드를 선언해서 이 인터페이스를 구현할 때
 * findAllDistinctHashtags 메서드를 강제로 구현해야 한다.
 * */
public interface ArticleRepositoryCustom {

    List<String> findAllDistinctHashtags();

}
