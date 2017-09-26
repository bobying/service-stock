package com.newdun.cloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A Info.
 */
public class StockResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long status;

    private List<List<String>> hq;

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public List<List<String>> getHq() {
		return hq;
	}

	public void setHq(List<List<String>> hq) {
		this.hq = hq;
	}
}
