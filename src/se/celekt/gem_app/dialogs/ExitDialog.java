package se.celekt.gem_app.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;

import se.celekt.gem_app.activities.Connect_MAS;
import se.celekt.gem_app.activities.GemActivity;
import se.celekt.gem_app.jade.Configuration;
import se.celekt.R;

/**
 * Created by alisa on 5/31/13.
 */
public class ExitDialog extends SherlockDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.exit_dialog, container);

        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText("Do you want to exit from app?");

        getDialog().setTitle("Exit");

        Button yes = (Button)view.findViewById(R.id.button_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                if(getActivity() instanceof GemActivity){
                    Configuration.shoutDownJade();
                  ((GemActivity)getActivity()).finish();

                }
                else {

                    ((Connect_MAS)getActivity()).closeActivity();

                }
            }
        });

        Button no = (Button)view.findViewById(R.id.button_no);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

}
