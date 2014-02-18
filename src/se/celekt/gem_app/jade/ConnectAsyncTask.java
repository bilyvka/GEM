package se.celekt.gem_app.jade;

import android.os.AsyncTask;

/**
 * Created by alisa on 6/11/13.
 */
public class ConnectAsyncTask extends AsyncTask<Configuration,Void,Boolean> { //change Object to required type
    private AsyncsTask listener;

    public ConnectAsyncTask(AsyncsTask listener){
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Configuration... params) {
        params[0].connect();
        return true;
    }

    //required methods

    protected void onPostExecute(Boolean o){
        listener.onComplete();
    }


}
