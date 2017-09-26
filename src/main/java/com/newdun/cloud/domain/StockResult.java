package com.newdun.cloud.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Info.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long status;
    private String code;
    private List<List<String>> hq;
//    private List<String> stat;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	public List<String> getStat() {
//		return stat;
//	}
//
//	public void setStat(List<String> stat) {
//		this.stat = stat;
//	}
}
