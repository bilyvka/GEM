package se.celekt.gem_app.handlers;

import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.jade.handlers.GuiEventHandler;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.gem_app.views.MemberListView;

/**
 * Created by alisa on 6/11/13.
 */
public class DisconnectedHandler extends GuiEventHandler {

    private MemberListView mMemberListView;

    public DisconnectedHandler(MemberListView memberListView) {
        mMemberListView = memberListView;
        EventsMgr.getInstance().registerEvent(Events.GPS_SERVICE_DISCONNECT_EVENT, this);
    }

    @Override
    protected void processEvent(Events event) {

            String eventName = event.getName();

            // Handle case of new message
            if (eventName.equals(Events.GPS_SERVICE_DISCONNECT_EVENT)) {
                LogSaver.getInstance().writeLog(LogSaver.PhoneUnavailable, LogSaver.MessageType.INFO, "This phone is not available for usage");
                //show dialog
                mMemberListView.showDisconnectedDialog("The GPS quality of your phone is not good enough. Another phone will be assigned to you");
            }

    }
}
