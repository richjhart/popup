package com.rjhartsoftware.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.rjhartsoftware.fragments.FragmentTransactions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class FragmentMessage extends DialogFragment implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //noinspection ConstantConditions
        getArguments().putString(ARG_INPUT_RESULT, s.toString());
        AlertDialog ad = (AlertDialog) getDialog();
        if (ad != null) {
            ad.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!TextUtils.isEmpty(s));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private static final String TAG = "_frag_message.";
    //    private static final String ARG_RESULT_CLASS = "class";
//    private static final String ARG_CHECKBOX_CHANGED_CLASS = "checkbox_class";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE_BUTTON = "positive";
    private static final String ARG_NEGATIVE_BUTTON = "negative";
    private static final String ARG_NEUTRAL_BUTTON = "neutral";
    private static final String ARG_POSITIVE_BUTTON_INACTIVE = "positive_action";
    private static final String ARG_NEGATIVE_BUTTON_INACTIVE = "negative_action";
    private static final String ARG_NEUTRAL_BUTTON_INACTIVE = "neutral_action";
    private static final String ARG_CANCEL = "allowCancel";
    private static final String ARG_CANCEL_TOUCH = "allowCancelOnTouch";
    private static final String ARG_REQUEST_TAG = "tag";
    private static final String ARG_INPUT = "input";
    static final String ARG_INPUT_RESULT = "input_result";
    private static final String ARG_INPUT_TYPE = "input_type";
    private static final String ARG_CHECKBOX = "checkbox";
    static final String ARG_CHECKBOX_RESULT = "checkbox_result";
    private static final String ARG_STYLE = "style";
    private static final String ARG_TRANSPARENT = "transparent";
    private static final String ARG_MUST_VIEW_ALL = "must_view_all";
    private static final String ARG_MUST_VIEW_ALL_MORE = "must_view_all_more";

    public FragmentMessage() {
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getArguments().getInt(ARG_STYLE));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogInterface = inflater.inflate(R.layout.fragment_dialog_message, null);
        TextView title = dialogInterface.findViewById(R.id.message_title);
        CharSequence title_text = getArguments().getCharSequence(ARG_TITLE, "");
        if (title_text instanceof String) {
            title_text = Html.fromHtml((String) title_text);
        }
        title.setText(title_text);
        TextView message = dialogInterface.findViewById(R.id.message_message);
        CharSequence msg_text = getArguments().getCharSequence(ARG_MESSAGE, "");
        if (msg_text instanceof String) {
            msg_text = FromHtml.fromHtml((String) msg_text);
        }
        message.setText(msg_text);
        MovementMethod m = message.getMovementMethod();
        if (!(m instanceof LinkMovementMethod)) {
            message.setMovementMethod(LinkMovementMethod.getInstance());
        }
        CheckBox checkbox = dialogInterface.findViewById(R.id.message_checkbox);
        if (getArguments().getCharSequence(ARG_CHECKBOX) != null) {
            checkbox.setText(getArguments().getCharSequence(ARG_CHECKBOX));
            if (getArguments().getBoolean(ARG_CHECKBOX_RESULT, false)) {
                checkbox.setChecked(true);
            }
            checkbox.setOnCheckedChangeListener(this);
        } else {
            checkbox.setVisibility(View.GONE);
        }
        EditText edit = dialogInterface.findViewById(R.id.message_input);
        TextInputLayout edit_layout = dialogInterface.findViewById(R.id.message_input_hint);
        if (getArguments().getString(ARG_INPUT) != null) {
            edit_layout.setHint(getArguments().getString(ARG_INPUT));
            if (getArguments().getString(ARG_INPUT_RESULT) != null) {
                edit.setText(getArguments().getString(ARG_INPUT_RESULT));
                edit.setSelection(0, edit.getText().length());
            }
            if (getArguments().containsKey(ARG_INPUT_TYPE)) {
                edit.setInputType(getArguments().getInt(ARG_INPUT_TYPE));
            }
            edit.addTextChangedListener(this);
        } else {
            edit_layout.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }
        builder.setView(dialogInterface);

        if (getArguments().getString(ARG_POSITIVE_BUTTON) != null) {
            builder.setPositiveButton(getArguments().getString(ARG_POSITIVE_BUTTON), this);
        }
        if (getArguments().getString(ARG_NEGATIVE_BUTTON) != null) {
            builder.setNegativeButton(getArguments().getString(ARG_NEGATIVE_BUTTON), this);
        }
        if (getArguments().getString(ARG_NEUTRAL_BUTTON) != null) {
            builder.setNeutralButton(getArguments().getString(ARG_NEUTRAL_BUTTON), this);
        }
        builder.setCancelable(getArguments().getBoolean(ARG_CANCEL));
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(getArguments().getBoolean(ARG_CANCEL_TOUCH));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                assert getArguments() != null;
                if (getArguments().getBoolean(ARG_TRANSPARENT)) {
                    if (((AlertDialog) dialog).getWindow() != null) {
                        //noinspection ConstantConditions
                        ((AlertDialog) dialog).getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
                    }
                }
                NestedScrollView sv = ((AlertDialog) dialog).findViewById(R.id.message_message_scroll);
                sv.setTag(false);
                if (getArguments().getBoolean(ARG_MUST_VIEW_ALL)) {
                    Button ok = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    if (ok != null) {
                        sv.setTag(true);
                        if (getArguments().getString(ARG_MUST_VIEW_ALL_MORE) == null) {
                            ok.setEnabled(false);
                        } else {
                            ok.setEnabled(true);
                            ok.setText(getArguments().getString(ARG_MUST_VIEW_ALL_MORE));
                        }
                        if (sv.canScrollVertically(1)) {
                            sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                    if (!v.canScrollVertically(1)) {
                                        ok.setEnabled(true);
                                        sv.setTag(false);
                                        ok.setText(getArguments().getString(ARG_POSITIVE_BUTTON));
                                    }
                                }
                            });
                            if (getArguments().getString(ARG_MUST_VIEW_ALL_MORE) != null) {
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if ((boolean) sv.getTag()) {
                                            sv.pageScroll(View.FOCUS_DOWN);
                                        } else {
                                            FragmentMessage.this.onClick(dialog, AlertDialog.BUTTON_POSITIVE);
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        } else {
                            ok.setEnabled(true);
                            sv.setTag(false);
                            ok.setText(getArguments().getString(ARG_POSITIVE_BUTTON));
                        }
                    }
                }
            }
        });
        return dialog;
    }

    private static class SavedString {
        private final String which;
        @StringRes
        private final int res;
        private final Object[] args;

        private SavedString(String which, @StringRes int res, Object... args) {
            this.which = which;
            this.res = res;
            this.args = args;
        }

        private void process(Bundle bundle, Context context) {
            bundle.putString(which, String.format(context.getString(res), args));
        }
    }

    private static int sAutoRequestId = 0;

    @SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
    public static class Builder {

        private final Bundle mArguments = new Bundle();
        private final List<SavedString> mStrings = new ArrayList<>();

        public Builder() {
            this(/*null,*/ null);
        }

        public Builder(/*Class<? extends PopupResult> callback,*/ String requestId) {
//            if (callback == null) {
//                callback = PopupResult.class;
//            }
            if (requestId == null) {
                requestId = "_auto_" + sAutoRequestId;
                sAutoRequestId++;
            }
//            mArguments.putString(ARG_RESULT_CLASS, callback.getCanonicalName());
            mArguments.putString(ARG_REQUEST_TAG, requestId);
//            mArguments.putString(ARG_CHECKBOX_CHANGED_CLASS, PopupCheckboxChanged.class.getCanonicalName());
            mArguments.putInt(ARG_STYLE, R.style.AlertDialogTheme);
        }

        private Builder setString(String which, @StringRes int value, Object... format) {
            mStrings.add(new SavedString(which, value, format));
            return this;
        }

        private Builder setString(String which, CharSequence value) {
            mArguments.putCharSequence(which, value);
            return this;
        }

//        public Builder onCheckboxChanged(Class<? extends PopupCheckboxChanged> callback) {
//            mArguments.putString(ARG_CHECKBOX_CHANGED_CLASS, callback.getCanonicalName());
//            return this;
//        }

        public Builder title(@StringRes int title, Object... format) {
            return setString(ARG_TITLE, title, format);
        }

        public Builder title(CharSequence title) {
            return setString(ARG_TITLE, title);
        }

        public Builder message(@StringRes int message, Object... format) {
            return setString(ARG_MESSAGE, message, format);
        }

        public Builder message(CharSequence message) {
            return setString(ARG_MESSAGE, message);
        }

        public Builder positiveButton(@StringRes int label, Object... format) {
            return setString(ARG_POSITIVE_BUTTON, label, format);
        }

        public Builder positiveButton(String label) {
            return setString(ARG_POSITIVE_BUTTON, label);
        }

        public Builder inactivePositiveButton(@StringRes int label, Object... format) {
            mArguments.putBoolean(ARG_POSITIVE_BUTTON_INACTIVE, true);
            return setString(ARG_POSITIVE_BUTTON, label, format);
        }

        public Builder inactivePositiveButton(String label) {
            mArguments.putBoolean(ARG_POSITIVE_BUTTON_INACTIVE, true);
            return setString(ARG_POSITIVE_BUTTON, label);
        }

        public Builder negativeButton(@StringRes int label, Object... format) {
            return setString(ARG_NEGATIVE_BUTTON, label, format);
        }

        public Builder negativeButton(String label) {
            return setString(ARG_NEGATIVE_BUTTON, label);
        }

        public Builder inactiveNegativeButton(@StringRes int label, Object... format) {
            mArguments.putBoolean(ARG_NEGATIVE_BUTTON_INACTIVE, true);
            return setString(ARG_NEGATIVE_BUTTON, label, format);
        }

        public Builder inactiveNegativeButton(String label) {
            mArguments.putBoolean(ARG_NEGATIVE_BUTTON_INACTIVE, true);
            return setString(ARG_NEGATIVE_BUTTON, label);
        }

        public Builder neutralButton(@StringRes int label, Object... format) {
            return setString(ARG_NEUTRAL_BUTTON, label, format);
        }

        public Builder neutralButton(String label) {
            return setString(ARG_NEUTRAL_BUTTON, label);
        }

        public Builder inactiveNeutralButton(@StringRes int label, Object... format) {
            mArguments.putBoolean(ARG_NEUTRAL_BUTTON_INACTIVE, true);
            return setString(ARG_NEUTRAL_BUTTON, label, format);
        }

        public Builder inactiveNeutralButton(String label) {
            mArguments.putBoolean(ARG_NEUTRAL_BUTTON_INACTIVE, true);
            return setString(ARG_NEUTRAL_BUTTON, label);
        }

        public Builder allowCancel(boolean allowCancel) {
            mArguments.putBoolean(ARG_CANCEL, allowCancel);
            return this;
        }

        public Builder allowCancelOnTouchOutside(boolean allowCancel) {
            mArguments.putBoolean(ARG_CANCEL_TOUCH, allowCancel);
            return this;
        }

        public Builder save(String key, String value) {
            mArguments.putString(key, value);
            return this;
        }

        public Builder transparent() {
            mArguments.putBoolean(ARG_TRANSPARENT, true);
            return this;
        }

        public Builder style(@StyleRes int style) {
            mArguments.putInt(ARG_STYLE, style);
            return this;
        }

        public Builder input(String query, int type) {
            mArguments.putString(ARG_INPUT, query);
            mArguments.putInt(ARG_INPUT_TYPE, type);
            return this;
        }

        public Builder input(String query) {
            mArguments.putString(ARG_INPUT, query);
            return this;
        }

        public Builder input(String query, String initial) {
            mArguments.putString(ARG_INPUT, query);
            mArguments.putString(ARG_INPUT_RESULT, initial);
            return this;
        }

        public Builder input(String query, String initial, int type) {
            mArguments.putString(ARG_INPUT, query);
            mArguments.putString(ARG_INPUT_RESULT, initial);
            mArguments.putInt(ARG_INPUT_TYPE, type);
            return this;
        }

        public Builder mustViewAll() {
            mArguments.putBoolean(ARG_MUST_VIEW_ALL, true);
            return this;
        }

        public Builder mustViewAll(String moreButton) {
            mArguments.putBoolean(ARG_MUST_VIEW_ALL, true);
            mArguments.putString(ARG_MUST_VIEW_ALL_MORE, moreButton);
            return this;
        }

        public Builder checkBox(CharSequence message, boolean checked) {
            mArguments.putBoolean(ARG_CHECKBOX_RESULT, checked);
            mArguments.putCharSequence(ARG_CHECKBOX, message);
            return this;
        }

        public Fragment getFragment(@NonNull Context context) {
            for (SavedString ss : mStrings) {
                ss.process(mArguments, context);
            }
            mStrings.clear();

            FragmentMessage frag = new FragmentMessage();
            frag.setArguments(mArguments);
            frag.setCancelable(mArguments.getBoolean(ARG_CANCEL));
            return frag;
        }

        public String getTag() {
            return TAG + mArguments.getString(ARG_REQUEST_TAG);
        }

        public void show(@Nullable AppCompatActivity activity) {
            if (activity == null) {
                return;
            }
            FragmentTransactions.beginTransaction(activity)
                    .add(getFragment(activity), getTag())
                    .commit();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        assert getArguments() != null;
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                if (getArguments().getBoolean(ARG_POSITIVE_BUTTON_INACTIVE, false)) {
                    return;
                }
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                if (getArguments().getBoolean(ARG_NEGATIVE_BUTTON_INACTIVE, false)) {
                    return;
                }
                break;
            case AlertDialog.BUTTON_NEUTRAL:
                if (getArguments().getBoolean(ARG_NEUTRAL_BUTTON_INACTIVE, false)) {
                    return;
                }
                break;
        }
        EventBus.getDefault().post(
                new PopupResult(
                        which,
                        getArguments().getString(ARG_REQUEST_TAG),
                        getArguments()
                )
        );
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //noinspection ConstantConditions
        getArguments().putBoolean(ARG_CHECKBOX_RESULT, isChecked);
        EventBus.getDefault().post(
                new PopupCheckboxChanged(
                        isChecked,
                        getArguments().getString(ARG_REQUEST_TAG),
                        getArguments()
                )
        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}