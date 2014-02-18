package se.celekt.gem_app.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.celekt.gem_app.adapters.AttemptAdapter;
import se.celekt.gem_app.objects.Attempt;

/**
 * Created by alisa on 6/7/13.
 */
public class AttemptView {

    private AttemptAdapter mAttemptAdapter;
    private Map<String,Integer> mAttempts;

    public AttemptView(AttemptAdapter attemptAdapter, Map<String, Integer> attempts) {
        mAttemptAdapter = attemptAdapter;
        mAttempts = attempts;
        setupAttempts();
    }

    public void updateAttempts(){

    }

    public void setupAttempts(){
        List<Attempt> attemptsList = new ArrayList<Attempt>();
        for(String point:mAttempts.keySet()){
            attemptsList.add(new Attempt(point,mAttempts.get(point)));
        }
        mAttemptAdapter.updateAttempt(attemptsList);
    }

}
