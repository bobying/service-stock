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

    private Long sourceId;

    private String sourceTitle;

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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
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
            "}";
    }
}
