package com.marathon.board.repository.querydsl;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.Hashtag;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class HashtagRepositoryCustomImpl extends QuerydslRepositorySupport implements HashtagRepositoryCustom{

    public HashtagRepositoryCustomImpl() {
        super(Hashtag.class);
    }
}
