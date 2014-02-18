package se.celekt.gem_app.handlers;

import se.celekt.gem_app.activities.GemActivity;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.handlers.GuiEventHandler;

public class ReadyToConnect extends GuiEventHandler {
	GemActivity activity;
		
	
	public ReadyToConnect(GemActivity activity) {
		super();
		this.activity = activity;
	}



	@Override
	protected void processEvent(Events event) {
		// TODO Auto-generated method stub
			activity.readytoConnect();
	}

}
