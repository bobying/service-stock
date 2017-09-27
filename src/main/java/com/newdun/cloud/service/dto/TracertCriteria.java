package com.newdun.cloud.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;


import io.github.jhipster.service.filter.LocalDateFilter;



/**
 * Criteria class for the Tracert entity. This class is used in TracertResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /tracerts?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TracertCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private IntegerFilter days;

    private FloatFilter increase_day;

    private FloatFilter increase_total;

    private FloatFilter amplitude_day;

    private FloatFilter highest;

    private FloatFilter lowest;

    private LocalDateFilter date;

    private LongFilter infoId;

    public TracertCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getDays() {
        return days;
    }

    public void setDays(IntegerFilter days) {
        this.days = days;
    }

    public FloatFilter getIncrease_day() {
        return increase_day;
    }

    public void setIncrease_day(FloatFilter increase_day) {
        this.increase_day = increase_day;
    }

    public FloatFilter getIncrease_total() {
        return increase_total;
    }

    public void setIncrease_total(FloatFilter increase_total) {
        this.increase_total = increase_total;
    }

    public FloatFilter getAmplitude_day() {
        return amplitude_day;
    }

    public void setAmplitude_day(FloatFilter amplitude_day) {
        this.amplitude_day = amplitude_day;
    }

    public FloatFilter getHighest() {
        return highest;
    }

    public void setHighest(FloatFilter highest) {
        this.highest = highest;
    }

    public FloatFilter getLowest() {
        return lowest;
    }

    public void setLowest(FloatFilter lowest) {
        this.lowest = lowest;
    }

    public LocalDateFilter getDate() {
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public LongFilter getInfoId() {
        return infoId;
    }

    public void setInfoId(LongFilter infoId) {
        this.infoId = infoId;
    }

    @Override
    public String toString() {
        return "TracertCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (days != null ? "days=" + days + ", " : "") +
                (increase_day != null ? "increase_day=" + increase_day + ", " : "") +
                (increase_total != null ? "increase_total=" + increase_total + ", " : "") +
                (amplitude_day != null ? "amplitude_day=" + amplitude_day + ", " : "") +
                (highest != null ? "highest=" + highest + ", " : "") +
                (lowest != null ? "lowest=" + lowest + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (infoId != null ? "infoId=" + infoId + ", " : "") +
            "}";
    }

}
