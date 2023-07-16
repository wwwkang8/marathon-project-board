package com.marathon.board.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.marathon.board.domain.Hashtag;
import com.marathon.board.repository.querydsl.HashtagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface HashtagRepository extends
    JpaRepository<Hashtag, Long>,
    HashtagRepositoryCustom,
    QuerydslPredicateExecutor<Hashtag> {

    Optional<Hashtag> findByHashtagName(String hashtagName);
    List<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);


}
