package com.rjhartsoftware.popup;

import android.os.Bundle;

public final class PopupResult {
    public final int which;
    public final String request;
    public final Bundle b;

    public PopupResult(int which, String request, Bundle bundle) {
        this.which = which;
        this.request = request;
        b = bundle;
    }

    public boolean checkboxResult() {
        return b.getBoolean(FragmentMessage.ARG_CHECKBOX_RESULT, false);
    }

    public String inputResult() {
        return b.getString(FragmentMessage.ARG_INPUT_RESULT, "");
    }

}
