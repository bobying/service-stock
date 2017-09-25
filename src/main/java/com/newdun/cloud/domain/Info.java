package com.newdun.cloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Info.
 */
@Entity
@Table(name = "info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "info")
public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jhi_date")
    private ZonedDateTime date;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "jhi_desc")
    private String desc;

    @Column(name = "stock")
    private String stock;

    @ManyToOne
    private Source source;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Info date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public Info title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public Info desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStock() {
        return stock;
    }

    public Info stock(String stock) {
        this.stock = stock;
        return this;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Source getSource() {
        return source;
    }

    public Info source(Source source) {
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
        Info info = (Info) o;
        if (info.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), info.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Info{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", title='" + getTitle() + "'" +
            ", desc='" + getDesc() + "'" +
            ", stock='" + getStock() + "'" +
            "}";
    }
}
