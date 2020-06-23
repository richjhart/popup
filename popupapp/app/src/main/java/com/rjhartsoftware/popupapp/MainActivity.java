package com.rjhartsoftware.popupapp;

import android.os.Bundle;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;

import com.rjhartsoftware.fragments.FragmentTransactions;
import com.rjhartsoftware.logcatdebug.D;
import com.rjhartsoftware.popup.FragmentMessage;
import com.rjhartsoftware.popup.PopupCheckboxChanged;
import com.rjhartsoftware.popup.PopupResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        D.init(BuildConfig.VERSION_NAME, BuildConfig.DEBUG);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        FragmentTransactions.activityCreated(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentMessage.Builder builder;

            builder = new FragmentMessage.Builder("second")
                    .allowCancel(false)
                    .inactiveNegativeButton("Cancel")
                    .positiveButton("OK")
                    .style(R.style.Alert)
                    .transparent()
                    .message("This is the second message");
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(this), builder.getTag())
                    .dontDuplicateTag()
                    .commit();

            builder = new FragmentMessage.Builder("third")
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
                    .add(builder.getFragment(this), builder.getTag())
                    .dontDuplicateTag()
                    .commit();

            builder = new FragmentMessage.Builder("fourth")
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
                    .add(builder.getFragment(this), builder.getTag())
                    .addToBackStack(null)
                    .dontDuplicateTag()
                    .commit();

            builder = new FragmentMessage.Builder("fifth")
                    .allowCancel(false)
                    .allowCancelOnTouchOutside(false)
                    .positiveButton("OK")
                    .title("Non-scrollable message")
                    .message("This message will not scroll<br><br><br><br>.")
                    .mustViewAll("More");
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(this), builder.getTag())
                    .dontDuplicateTag()
                    .commit();

            String raw_html = getString(R.string.raw_html);
            builder = new FragmentMessage.Builder("sixth")
                    .allowCancel(false)
                    .allowCancelOnTouchOutside(false)
                    .positiveButton("OK")
                    .title("Must-scroll message")
                    .message(raw_html)
                    .mustViewAll("More");
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(this), builder.getTag())
                    .dontDuplicateTag()
                    .commit();

            new FragmentMessage.Builder("seventh")
                    .message("Long buttons")
                    .inactivePositiveButton("This is a long positive button")
                    .inactiveNegativeButton("This is a long negative button")
                    .inactiveNeutralButton("This is a long neutral button")
                    .show(this);

            builder = new FragmentMessage.Builder("first")
                    .allowCancel(false)
                    .inactiveNegativeButton("Cancel")
                    .positiveButton("OK")
                    .message("This is the first message")
                    .input("What do you want to return?");
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(this), builder.getTag())
                    .dontDuplicateTag()
                    .commit();

            builder = new FragmentMessage.Builder("email")
                    .allowCancel(false)
                    .inactiveNegativeButton("Cancel")
                    .positiveButton("OK")
                    .message("This is the first message")
                    .input("Email address", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            FragmentTransactions
                    .beginTransaction(this)
                    .add(builder.getFragment(this), builder.getTag())
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onPopupResult(PopupResult result) {
        D.log(D.GENERAL, "Message closed: " + result.request + ". which: " + result.b);

    }

    @Subscribe
    public void onPopupCheckboxChanged(PopupCheckboxChanged result) {
        D.log(D.GENERAL, "Checkbox changed: " + result.request + ". checkbox: " + result.checkbox);
    }
}
