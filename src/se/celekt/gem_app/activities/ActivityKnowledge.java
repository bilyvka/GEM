package se.celekt.gem_app.activities;

public class ActivityKnowledge {

	private static ActivityKnowledge activityKnowledge = new ActivityKnowledge();
    public static final String CONCERN = "activity";
	private String name;
	

	public static ActivityKnowledge getInstance(){
		return activityKnowledge;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	
	
}
