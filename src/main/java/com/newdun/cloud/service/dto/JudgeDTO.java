package com.newdun.cloud.service.dto;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Judge entity.
 */
public class JudgeDTO implements Serializable {

    private Long id;

    private Integer score;

    private Float increase_total;

    private Integer increase_days;

    private Float day5;

    private Float day10;

    private Float day30;

    private Float day20;

    private Long infoId;

    private String infoTitle;

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

    public Float getDay5() {
        return day5;
    }

    public void setDay5(Float day5) {
        this.day5 = day5;
    }

    public Float getDay10() {
        return day10;
    }

    public void setDay10(Float day10) {
        this.day10 = day10;
    }

    public Float getDay30() {
        return day30;
    }

    public void setDay30(Float day30) {
        this.day30 = day30;
    }

    public Float getDay20() {
        return day20;
    }

    public void setDay20(Float day20) {
        this.day20 = day20;
    }

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
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
            ", day5='" + getDay5() + "'" +
            ", day10='" + getDay10() + "'" +
            ", day30='" + getDay30() + "'" +
            ", day20='" + getDay20() + "'" +
            "}";
    }
}
