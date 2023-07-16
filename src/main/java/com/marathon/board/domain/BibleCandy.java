package com.marathon.board.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString(callSuper = true) /** 부모클래스 객체까지 ToString 출력 */
@Table
@Entity
public class BibleCandy extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) /** auto increment */
    Long Id;

    @Setter
    @Column(nullable = false)
    String content;

    public BibleCandy() {
    }

    private BibleCandy(String content) {
        this.content = content;
    }

    public static BibleCandy  of(String content) {
        return new BibleCandy(content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BibleCandy that)) {
            return false;
        }
        return this.getId().equals(that.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
