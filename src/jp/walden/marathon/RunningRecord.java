package jp.walden.marathon;

import java.util.Date;

public class RunningRecord {

	private Integer runnerNumber;
	private Date date;
	private String distance;
	private Integer ranking;
	private Integer total;
	private String time;
	private Integer line;
	private Date created_at;

	public RunningRecord(Integer runnerNumber, Date date,
			String distance, Integer ranking, Integer total, String time) {
		super();
		this.runnerNumber = runnerNumber;
		this.date = date;
		this.distance = distance;
		this.ranking = ranking;
		this.total = total;
		this.time = time;
		this.line = null;
		this.created_at = new Date();
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Integer getRunnerNumber() {
		return runnerNumber;
	}

	public void setRunnerNumber(Integer runnerNumber) {
		this.runnerNumber = runnerNumber;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public Integer getRanking() {
		return ranking;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		String rankingAndTotal = String.valueOf(ranking);
		if(total != null) {
			rankingAndTotal = rankingAndTotal + "/" + String.valueOf(total);
		}
		return "ナンバー : " + runnerNumber +  "\n日付 : " + date +  "\n距離 : "
			+ distance + "\n順位 : " + rankingAndTotal + "\nタイム : "
			+ time +  "\n行 : " + line +  "\n作成日 : " + created_at;
	}
}
