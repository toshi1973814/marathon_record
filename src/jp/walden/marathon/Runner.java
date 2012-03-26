package jp.walden.marathon;

import java.util.Date;

public class Runner {
	private Integer number;
	private String name;
	private Date created_at;

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Runner(Integer number, String name) {
		super();
		this.number = number;
		this.name = name;
		this.created_at = new Date();
	}

	@Override
	public String toString() {
		return number + ": " + name + ": " + created_at;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
