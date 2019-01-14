package com.rjhartsoftware.popupapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

            builder = new FragmentMessage.Builder(this, "third")
                    .allowCancel(false)
                    .allowCancelOnTouchOutside(false)
                    .positiveButton("OK")
                    .title("Scrollable message")
                    .message("This message will scroll<br><br><br><br>.<br><br><br><br><br>.<br><br><br><br><br>.<br><br><br><br><br><br>.<br><br><br><br><br><br>.<br><br><br><br><br><br>At least it should")
                    //.message("This message will scroll<br><br><br><br>At least it should")
                    .checkBox("Checkbox", false)
                    .mustViewAll();
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(), builder.getTag())
                    .dontDuplicateTag()
                    .commit();

            builder = new FragmentMessage.Builder(this, "fourth")
                    .allowCancel(false)
                    .allowCancelOnTouchOutside(false)
                    .positiveButton("OK")
                    .negativeButton("Cancel")
                    .title("Scrollable message")
                    .message("This message will scroll<br><br><br><br>.<br><br><br><br><br>.<br><br><br><br><br>.<br><br><br><br><br><br>.<br><br><br><br><br><br>.<br><br><br><br><br><br>At least it should")
                    //.message("This message will scroll<br><br><br><br>At least it should")
                    .mustViewAll("More");
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(), builder.getTag())
                    .addToBackStack(null)
                    .dontDuplicateTag()
                    .commit();

            builder = new FragmentMessage.Builder(this, "fifth")
                    .allowCancel(false)
                    .allowCancelOnTouchOutside(false)
                    .positiveButton("OK")
                    .title("Non-scrollable message")
                    .message("This message will not scroll<br><br><br><br>.")
                    .mustViewAll("More");
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
        D.log(D.GENERAL, "Message closed: " + requestTag + ". which: " + which);
    }

    @Override
    public String getTag() {
        return null;
    }
}
