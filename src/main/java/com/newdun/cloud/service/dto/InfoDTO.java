package com.newdun.cloud.service.dto;


import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the Info entity.
 */
public class InfoDTO implements Serializable {

    private Long id;

    private ZonedDateTime date;

    private String title;

    @Lob
    private String desc;

    private String stock;

    private Long sourceId;

    private String sourceTitle;

    private Long judgeId;

    private int increaseDays;
    
    private String increaseTotal;
    
    private String increasedDay5;
    private String increasedDay10;
    private String increasedDay20;
    private String increasedDay30;
    
    private int judgeScore;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

        InfoDTO infoDTO = (InfoDTO) o;
        if(infoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), infoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "InfoDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", title='" + getTitle() + "'" +
            ", desc='" + getDesc() + "'" +
            ", stock='" + getStock() + "'" +
            "}";
    }

	public int getIncreaseDays() {
		return increaseDays;
	}

	public void setIncreaseDays(int increaseDays) {
		this.increaseDays = increaseDays;
	}

	public String getIncreaseTotal() {
		return increaseTotal;
	}

	public void setIncreaseTotal(String increaseTotal) {
		this.increaseTotal = increaseTotal;
	}

	public int getJudgeScore() {
		return judgeScore;
	}

	public void setJudgeScore(int judgeScore) {
		this.judgeScore = judgeScore;
	}

	public Long getJudgeId() {
		return judgeId;
	}

	public void setJudgeId(Long judgeId) {
		this.judgeId = judgeId;
	}

	public String getIncreasedDay5() {
		return increasedDay5;
	}

	public void setIncreasedDay5(String increasedDay5) {
		this.increasedDay5 = increasedDay5;
	}

	public String getIncreasedDay10() {
		return increasedDay10;
	}

	public void setIncreasedDay10(String increasedDay10) {
		this.increasedDay10 = increasedDay10;
	}

	public String getIncreasedDay20() {
		return increasedDay20;
	}

	public void setIncreasedDay20(String increasedDay20) {
		this.increasedDay20 = increasedDay20;
	}

	public String getIncreasedDay30() {
		return increasedDay30;
	}

	public void setIncreasedDay30(String increasedDay30) {
		this.increasedDay30 = increasedDay30;
	}
}
