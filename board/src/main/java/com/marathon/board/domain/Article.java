package com.marathon.board.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Table
public class Article {

  //본문 인덱스 : 본문검색에는 인덱스를 걸지 않는다. 너무 길어서 본문에는 인덱스 X. 그리고 인덱스에 용량이 한계가 있다.
  private long id;
  private String title; // 제목

  private String content; // 본문
  private String hashTag; // 해시태그
  private LocalDateTime createdAt; // 생성일시
  private String createdBy; // 생성자
  private LocalDateTime modifiedAt; // 수정일시
  private String modifiedBy; // 수정자


}
