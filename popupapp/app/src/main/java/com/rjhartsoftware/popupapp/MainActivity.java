package com.rjhartsoftware.popupapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rjhartsoftware.fragments.FragmentTransactions;
import com.rjhartsoftware.logcatdebug.D;
import com.rjhartsoftware.popup.FragmentMessage;

public class MainActivity extends AppCompatActivity implements FragmentMessage.MessageCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        D.init(BuildConfig.VERSION_NAME, BuildConfig.DEBUG);
        super.onCreate(savedInstanceState);
        FragmentTransactions.activityCreated(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentMessage.Builder builder = new FragmentMessage.Builder(this, "first")
                    .allowCancel(false)
                    .inactiveNegativeButton("Cancel")
                    .positiveButton("OK")
                    .message("This is the first message")
                    .input("What do you want to return?");
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(), builder.getTag())
                    .dontDuplicateTag()
                    .commit();

            builder = new FragmentMessage.Builder(this, "second")
                    .allowCancel(false)
                    .inactiveNegativeButton("Cancel")
                    .positiveButton("OK")
                    .style(R.style.Alert)
                    .transparent()
                    .message("This is the second message");
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(), builder.getTag())
                    .dontDuplicateTag()
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        FragmentTransactions.activityStarted(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        FragmentTransactions.activityResumed(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        FragmentTransactions.activityPaused(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        FragmentTransactions.activityStopped(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        FragmentTransactions.activityDestroyed(this);
        super.onDestroy();
    }

    @Override
    public void onMessageDone(int which, String requestTag, Bundle args) {
        D.log(D.GENERAL, "Message closed: " + requestTag);
    }

    @Override
    public String getTag() {
        return null;
    }
}
