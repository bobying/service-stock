package com.newdun.cloud.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Judge entity.
 */
public class JudgeDTO implements Serializable {

    private Long id;

    @Min(value = -10)
    @Max(value = 10)
    private Integer score;

    private Float increase_total;

    private Integer increase_days;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Float getIncrease_total() {
        return increase_total;
    }

    public void setIncrease_total(Float increase_total) {
        this.increase_total = increase_total;
    }

    public Integer getIncrease_days() {
        return increase_days;
    }

    public void setIncrease_days(Integer increase_days) {
        this.increase_days = increase_days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JudgeDTO judgeDTO = (JudgeDTO) o;
        if(judgeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), judgeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "JudgeDTO{" +
            "id=" + getId() +
            ", score='" + getScore() + "'" +
            ", increase_total='" + getIncrease_total() + "'" +
            ", increase_days='" + getIncrease_days() + "'" +
            "}";
    }
}
