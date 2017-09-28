package com.newdun.cloud.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;



import io.github.jhipster.service.filter.ZonedDateTimeFilter;


/**
 * Criteria class for the Info entity. This class is used in InfoResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /infos?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class InfoCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private ZonedDateTimeFilter date;

    private StringFilter title;

    private StringFilter stock;

    private LongFilter sourceId;

    private LongFilter judgeId;

    public InfoCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getDate() {
        return date;
    }

    public void setDate(ZonedDateTimeFilter date) {
        this.date = date;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getStock() {
        return stock;
    }

    public void setStock(StringFilter stock) {
        this.stock = stock;
    }

    public LongFilter getSourceId() {
        return sourceId;
    }

    public void setSourceId(LongFilter sourceId) {
        this.sourceId = sourceId;
    }

    public LongFilter getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(LongFilter judgeId) {
        this.judgeId = judgeId;
    }

    @Override
    public String toString() {
        return "InfoCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (stock != null ? "stock=" + stock + ", " : "") +
                (sourceId != null ? "sourceId=" + sourceId + ", " : "") +
                (judgeId != null ? "judgeId=" + judgeId + ", " : "") +
            "}";
    }

}
