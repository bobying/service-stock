package com.newdun.cloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Tracert.
 */
@Entity
@Table(name = "tracert")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "tracert")
public class Tracert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0)
    @Column(name = "days")
    private Integer days;

    @Column(name = "increase_day")
    private Float increase_day;

    @Column(name = "increase_total")
    private Float increase_total;

    @ManyToOne
    private Source source;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDays() {
        return days;
    }

    public Tracert days(Integer days) {
        this.days = days;
        return this;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Float getIncrease_day() {
        return increase_day;
    }

    public Tracert increase_day(Float increase_day) {
        this.increase_day = increase_day;
        return this;
    }

    public void setIncrease_day(Float increase_day) {
        this.increase_day = increase_day;
    }

    public Float getIncrease_total() {
        return increase_total;
    }

    public Tracert increase_total(Float increase_total) {
        this.increase_total = increase_total;
        return this;
    }

    public void setIncrease_total(Float increase_total) {
        this.increase_total = increase_total;
    }

    public Source getSource() {
        return source;
    }

    public Tracert source(Source source) {
        this.source = source;
        return this;
    }

    public void setSource(Source source) {
        this.source = source;
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
        Tracert tracert = (Tracert) o;
        if (tracert.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tracert.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Tracert{" +
            "id=" + getId() +
            ", days='" + getDays() + "'" +
            ", increase_day='" + getIncrease_day() + "'" +
            ", increase_total='" + getIncrease_total() + "'" +
            "}";
    }
}
