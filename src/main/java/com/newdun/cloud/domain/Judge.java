package com.newdun.cloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Judge.
 */
@Entity
@Table(name = "judge")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "judge")
public class Judge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score")
    private Integer score;

    @Column(name = "increase_total")
    private Float increase_total;

    @Column(name = "increase_days")
    private Integer increase_days;

    @Column(name = "day_5")
    private Float day5;

    @Column(name = "day_10")
    private Float day10;

    @Column(name = "day_30")
    private Float day30;

    @OneToOne
    @JoinColumn(unique = true)
    private Info info;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public Judge score(Integer score) {
        this.score = score;
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Float getIncrease_total() {
        return increase_total;
    }

    public Judge increase_total(Float increase_total) {
        this.increase_total = increase_total;
        return this;
    }

    public void setIncrease_total(Float increase_total) {
        this.increase_total = increase_total;
    }

    public Integer getIncrease_days() {
        return increase_days;
    }

    public Judge increase_days(Integer increase_days) {
        this.increase_days = increase_days;
        return this;
    }

    public void setIncrease_days(Integer increase_days) {
        this.increase_days = increase_days;
    }

    public Float getDay5() {
        return day5;
    }

    public Judge day5(Float day5) {
        this.day5 = day5;
        return this;
    }

    public void setDay5(Float day5) {
        this.day5 = day5;
    }

    public Float getDay10() {
        return day10;
    }

    public Judge day10(Float day10) {
        this.day10 = day10;
        return this;
    }

    public void setDay10(Float day10) {
        this.day10 = day10;
    }

    public Float getDay30() {
        return day30;
    }

    public Judge day30(Float day30) {
        this.day30 = day30;
        return this;
    }

    public void setDay30(Float day30) {
        this.day30 = day30;
    }

    public Info getInfo() {
        return info;
    }

    public Judge info(Info info) {
        this.info = info;
        return this;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
    // jhipster-needle-entity-add-getters-setters - Jhipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Judge judge = (Judge) o;
        if (judge.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), judge.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Judge{" +
            "id=" + getId() +
            ", score='" + getScore() + "'" +
            ", increase_total='" + getIncrease_total() + "'" +
            ", increase_days='" + getIncrease_days() + "'" +
            ", day5='" + getDay5() + "'" +
            ", day10='" + getDay10() + "'" +
            ", day30='" + getDay30() + "'" +
            "}";
    }
}
