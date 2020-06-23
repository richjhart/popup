package com.rjhartsoftware.popup;

import android.os.Bundle;

public final class PopupCheckboxChanged {
    public final boolean checkbox;
    public final String request;
    public final Bundle b;

    PopupCheckboxChanged(boolean checkbox, String request, Bundle bundle) {
        this.request = request;
        b = bundle;
        this.checkbox = checkbox;
    }
}
