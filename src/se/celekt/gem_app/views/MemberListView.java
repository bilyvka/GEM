package se.celekt.gem_app.views;

import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.activities.GemActivity;
import se.celekt.gem_app.adapters.GroupAdapter;
import se.celekt.gem_app.jade.Configuration;
import se.celekt.mvd.groups.Member;

/**
 * Created by alisa on 6/3/13.
 */
public class MemberListView {

    private GroupAdapter adapter;
    private GemActivity mContactActivity;


    public MemberListView(GroupAdapter adapter, GemActivity mContactActivity) {
       
        this.adapter = adapter;
        this.mContactActivity = mContactActivity;
    }

    public void updateContactList(){
        adapter.updateGroupView(ActivityKnowledge.getInstance().getName());
    }

    public void updateContactList(String concern){
        adapter.updateGroupView(concern, ActivityKnowledge.getInstance().getName());
    }

    public void updateDistance(String distance,String memberName){
        adapter.setDistanceView(distance,memberName);
    }


    public void turnOffInterface(boolean turnOff){
       // application.setInterfaceOffline(turnOff);
    }

    public void showDisconnectedDialog(String message){
        mContactActivity.showDisconnectedDialog(message);
    }

    public void requestPosition(Member member){
        if(member.getAgentID() != null){
            Configuration.requestPosition(member.getAgentID());
        }
    }

    public void showConnectionDialog(String message){
      //  mConnectActivity.showDialog(message);
    }

}
