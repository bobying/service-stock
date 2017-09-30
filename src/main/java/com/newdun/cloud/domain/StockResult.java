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
    
    /**
     * 0：日期
     * 1：开盘
     * 2：收盘
     * 3：涨跌额
     * 4：涨跌幅度
     * 5：最低
     * 6：最高
     * 7：成交：万手
     * 8：成交量
     * 9：换手率
     */
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
