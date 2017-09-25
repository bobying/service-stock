package com.newdun.cloud.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Tracert entity.
 */
public class TracertDTO implements Serializable {

    private Long id;

    @Min(value = 0)
    private Integer days;

    private Float increase_day;

    private Float increase_total;

    private Float amplitude_day;

    private Float highest;

    private Float lowest;

    private Long infoId;

    private String infoTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Float getIncrease_day() {
        return increase_day;
    }

    public void setIncrease_day(Float increase_day) {
        this.increase_day = increase_day;
    }

    public Float getIncrease_total() {
        return increase_total;
    }

    public void setIncrease_total(Float increase_total) {
        this.increase_total = increase_total;
    }

    public Float getAmplitude_day() {
        return amplitude_day;
    }

    public void setAmplitude_day(Float amplitude_day) {
        this.amplitude_day = amplitude_day;
    }

    public Float getHighest() {
        return highest;
    }

    public void setHighest(Float highest) {
        this.highest = highest;
    }

    public Float getLowest() {
        return lowest;
    }

    public void setLowest(Float lowest) {
        this.lowest = lowest;
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

        TracertDTO tracertDTO = (TracertDTO) o;
        if(tracertDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tracertDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TracertDTO{" +
            "id=" + getId() +
            ", days='" + getDays() + "'" +
            ", increase_day='" + getIncrease_day() + "'" +
            ", increase_total='" + getIncrease_total() + "'" +
            ", amplitude_day='" + getAmplitude_day() + "'" +
            ", highest='" + getHighest() + "'" +
            ", lowest='" + getLowest() + "'" +
            "}";
    }
}
