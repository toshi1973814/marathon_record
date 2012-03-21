package jp.walden.marathon;

import java.sql.Date;

import android.text.format.Time;

public class RunningRecord {

	private Integer runnerNumber;
	private Date date;
	private String distance;
	private Integer ranking;
	private Integer total;
	private String time;

	public RunningRecord(Integer runnerNumber, Date date,
			String distance, Integer ranking, Integer total, String time) {
		super();
		this.runnerNumber = runnerNumber;
		this.date = date;
		this.distance = distance;
		this.ranking = ranking;
		this.total = total;
		this.time = time;
	}

	@Override
	public String toString() {
		String rankingAndTotal = String.valueOf(ranking);
		if(total != null) {
			rankingAndTotal = rankingAndTotal + "/" + String.valueOf(total);
		}
		return "距離 : " + distance + "\n順位 : " + rankingAndTotal + "\nタイム : "
				+ time;
	}
}
