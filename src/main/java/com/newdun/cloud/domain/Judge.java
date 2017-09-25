package com.newdun.cloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
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

    @Min(value = -10)
    @Max(value = 10)
    @Column(name = "score")
    private Integer score;

    @Column(name = "increase_total")
    private Float increase_total;

    @Column(name = "increase_days")
    private Integer increase_days;

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
            "}";
    }
}
