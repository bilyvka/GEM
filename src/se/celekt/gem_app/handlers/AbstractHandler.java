package se.celekt.gem_app.handlers;

import android.content.Context;

/**
 * Created by alisa on 6/8/13.
 */
public abstract class AbstractHandler {

    private Context mContext;

    protected AbstractHandler(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
