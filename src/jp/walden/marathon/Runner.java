package jp.walden.marathon;

public class Runner {
	private Integer number;
	private String name;

	public Runner(Integer number, String name) {
		super();
		this.number = number;
		this.name = name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return number + ": " + name;
	}
}
