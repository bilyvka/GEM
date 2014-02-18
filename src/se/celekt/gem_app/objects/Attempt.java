package se.celekt.gem_app.objects;

import java.io.Serializable;

public class Attempt implements Serializable{
	private static final long serialVersionUID = 6775773360431039775L;

	private String pointName;
	private int countAttempt;
	
	public Attempt() {
	}

	public Attempt(String pointName, int countAttempt) {
		super();
		this.pointName = pointName;
		this.countAttempt = countAttempt;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public int getCountAttempt() {
		return countAttempt;
	}

	public void setCountAttempt(int countAttempt) {
		this.countAttempt = countAttempt;
	}
	
}
