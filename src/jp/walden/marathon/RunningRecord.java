package jp.walden.marathon;

import java.sql.Date;

import android.text.format.Time;

public class RunningRecord {

	private Integer runnerNumber;
	private Date date;
	private Integer distance;
	private Integer ranking;
	private String time;

	public RunningRecord(Integer runnerNumber, Date date,
			Integer distance, Integer ranking, String time) {
		super();
		this.runnerNumber = runnerNumber;
		this.date = date;
		this.distance = distance;
		this.ranking = ranking;
		this.time = time;
	}
}
