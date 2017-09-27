package com.newdun.cloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
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

    @Column(name = "amplitude_day")
    private Float amplitude_day;

    @Column(name = "highest")
    private Float highest;

    @Column(name = "lowest")
    private Float lowest;

    @Column(name = "jhi_date")
    private LocalDate date;

    @ManyToOne
    private Info info;

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

    public Float getAmplitude_day() {
        return amplitude_day;
    }

    public Tracert amplitude_day(Float amplitude_day) {
        this.amplitude_day = amplitude_day;
        return this;
    }

    public void setAmplitude_day(Float amplitude_day) {
        this.amplitude_day = amplitude_day;
    }

    public Float getHighest() {
        return highest;
    }

    public Tracert highest(Float highest) {
        this.highest = highest;
        return this;
    }

    public void setHighest(Float highest) {
        this.highest = highest;
    }

    public Float getLowest() {
        return lowest;
    }

    public Tracert lowest(Float lowest) {
        this.lowest = lowest;
        return this;
    }

    public void setLowest(Float lowest) {
        this.lowest = lowest;
    }

    public LocalDate getDate() {
        return date;
    }

    public Tracert date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Info getInfo() {
        return info;
    }

    public Tracert info(Info info) {
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
            ", amplitude_day='" + getAmplitude_day() + "'" +
            ", highest='" + getHighest() + "'" +
            ", lowest='" + getLowest() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
