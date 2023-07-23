package com.marathon.board.repository.querydsl;

import java.util.List;

import com.marathon.board.domain.Article;
import com.marathon.board.domain.Hashtag;
import com.marathon.board.domain.QHashtag;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class HashtagRepositoryCustomImpl extends QuerydslRepositorySupport implements HashtagRepositoryCustom{

    public HashtagRepositoryCustomImpl() {
        super(Hashtag.class);
    }


    @Override
    public List<String> findAllHashtagNames() {

        QHashtag hashtag = QHashtag.hashtag;

        return from(hashtag)
                .select(hashtag.hashtagName)
                .fetch();

    }
}
