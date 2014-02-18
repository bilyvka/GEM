package se.celekt.gem_app.tabs;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import se.celekt.R;
import se.celekt.gem_app.activities.GemActivity;
import se.celekt.gem_app.adapters.GroupAdapter;
import se.celekt.gem_app.handlers.DisconnectedHandler;
import se.celekt.gem_app.handlers.DistanceHandler;
import se.celekt.gem_app.handlers.GroupHandlers;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.views.MemberListView;
import se.celekt.mvd.groups.Member;

/**
 * Created by alisa on 5/30/13.
 */
public class ContactView extends ListFragment {

    public  GroupAdapter mGroupAdapter;
    public List<Member> mMembers;

    private MemberListView mMemberListView;
    public static final String EXTRA_TITLE = "title";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMembers = new ArrayList<Member>();
        mGroupAdapter = new GroupAdapter(this.getActivity(),R.layout.element_layout,mMembers);
        mMemberListView = new MemberListView(mGroupAdapter,(GemActivity) getActivity());

        View header =  getActivity().getLayoutInflater().inflate(R.layout.header_contact_list, null);
        getListView().addHeaderView(header);

//        View footer = getActivity().getLayoutInflater().inflate(R.layout.footer_contact_list,null);
//        getListView().addFooterView(footer);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Member item = (Member) l.getItemAtPosition(position);
        mMemberListView.requestPosition(item);
    }

    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerEvents(mMemberListView);
        setListAdapter(mGroupAdapter);
        
        //fire event to GemActivity saying that the tab is done
        Events event = EventsMgr.getInstance().createEvent(Events.TAB_PRESENTED);
        event.addParam("tab", "Contacts");
        EventsMgr.getInstance().fireEvent(event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //setListAdapter(null);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
    //first saving my state, so the bundle wont be empty.
    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE"); super.onSaveInstanceState(outState);
    }

    public static void registerEvents(MemberListView memberListView){
        new DistanceHandler(memberListView);
        new GroupHandlers(memberListView);
        new DisconnectedHandler(memberListView);
    }

}
