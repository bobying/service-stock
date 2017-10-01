package com.newdun.cloud.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the Judge entity. This class is used in JudgeResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /judges?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class JudgeCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private IntegerFilter score;

    private FloatFilter increase_total;

    private IntegerFilter increase_days;

    private FloatFilter day5;

    private FloatFilter day10;

    private FloatFilter day30;

    private FloatFilter day20;

    private LongFilter infoId;

    public JudgeCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getScore() {
        return score;
    }

    public void setScore(IntegerFilter score) {
        this.score = score;
    }

    public FloatFilter getIncrease_total() {
        return increase_total;
    }

    public void setIncrease_total(FloatFilter increase_total) {
        this.increase_total = increase_total;
    }

    public IntegerFilter getIncrease_days() {
        return increase_days;
    }

    public void setIncrease_days(IntegerFilter increase_days) {
        this.increase_days = increase_days;
    }

    public FloatFilter getDay5() {
        return day5;
    }

    public void setDay5(FloatFilter day5) {
        this.day5 = day5;
    }

    public FloatFilter getDay10() {
        return day10;
    }

    public void setDay10(FloatFilter day10) {
        this.day10 = day10;
    }

    public FloatFilter getDay30() {
        return day30;
    }

    public void setDay30(FloatFilter day30) {
        this.day30 = day30;
    }

    public FloatFilter getDay20() {
        return day20;
    }

    public void setDay20(FloatFilter day20) {
        this.day20 = day20;
    }

    public LongFilter getInfoId() {
        return infoId;
    }

    public void setInfoId(LongFilter infoId) {
        this.infoId = infoId;
    }

    @Override
    public String toString() {
        return "JudgeCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (score != null ? "score=" + score + ", " : "") +
                (increase_total != null ? "increase_total=" + increase_total + ", " : "") +
                (increase_days != null ? "increase_days=" + increase_days + ", " : "") +
                (day5 != null ? "day5=" + day5 + ", " : "") +
                (day10 != null ? "day10=" + day10 + ", " : "") +
                (day30 != null ? "day30=" + day30 + ", " : "") +
                (day20 != null ? "day20=" + day20 + ", " : "") +
                (infoId != null ? "infoId=" + infoId + ", " : "") +
            "}";
    }

}
