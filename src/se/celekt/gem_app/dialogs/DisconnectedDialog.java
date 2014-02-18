package se.celekt.gem_app.dialogs;

import com.actionbarsherlock.app.SherlockDialogFragment;

import se.celekt.R;
import se.celekt.gem_app.activities.GemActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DisconnectedDialog extends SherlockDialogFragment{
	private String message;

  
	public DisconnectedDialog(String message) {
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.simple_dialog, container);

        TextView messageView = (TextView) view.findViewById(R.id.message);
        messageView.setText(message);

        getDialog().setTitle("Warning");

        Button yes = (Button)view.findViewById(R.id.button_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ((GemActivity)getActivity()).disableView();
                getDialog().dismiss();
            }
        });
        return view;
    }
}
