package com.rjhartsoftware.popup;

import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.text.style.TabStopSpan;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

@SuppressWarnings("unused")
class FromHtml {

    private static final int INDENT = 80;

    private FromHtml() {
    }

    static Spanned fromHtml(String html) {
        String[] overrideTags = new String[]{"ul", "ol", "li"};
        for (String tag : overrideTags) {
            html = html.replace("<" + tag + ">", "<_" + tag + ">");
            html = html.replace("<" + tag + " ", "<_" + tag + " ");
            html = html.replace("</" + tag + ">", "</_" + tag + ">");
        }

        return Html.fromHtml(html, null,
                new Html.TagHandler() {

                    private Deque<HtmlList> lists = new ArrayDeque<>();
                    private Truss truss;

                    @Override
                    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                        if (truss == null) {
                            truss = new Truss((SpannableStringBuilder) output);
                        }
                        Attributes attributes = new Attributes(xmlReader);

                        if (opening && "_ul".equals(tag)) {
                            lists.push(new HtmlList(HtmlList.TYPE_BULLET, lists.peek()));
                            checkNewline(output);
                            //noinspection ConstantConditions we know peek() can't be null because we just pushed an item
                            truss.pushSpan(new LeadingMarginSpan.Standard(lists.peek().getFirstIndent(), lists.peek().getOtherIndent()));
                        }
                        if (!opening && "_ul".equals(tag)) {
                            lists.pop();
                            checkNewline(output);
                            truss.popSpan();
                        }
                        if (opening && "_ol".equals(tag)) {
                            int type = HtmlList.TYPE_INT;
                            switch (attributes.get("type")) {
                                case "a":
                                    type = HtmlList.TYPE_ALPHA;
                                    break;
                                case "A":
                                    type = HtmlList.TYPE_ALPHA_UPPER;
                                    break;
                                case "i":
                                    type = HtmlList.TYPE_ROMAN;
                                    break;
                                case "I":
                                    type = HtmlList.TYPE_ROMAN_UPPER;
                                    break;
                                case "1":
                                default:
                                    // do nothing
                                    break;
                            }
                            HtmlList newList = new HtmlList(type, lists.peek());
                            if (attributes.get("indent").equals("false")) {
                                newList.dontIndent();
                            }
                            lists.push(newList);
                            checkNewline(output);
                            truss.pushSpan(new LeadingMarginSpan.Standard(newList.getFirstIndent(), newList.getOtherIndent()));
                        }
                        if (!opening && "_ol".equals(tag)) {
                            lists.pop();
                            checkNewline(output);
                            truss.popSpan();
                        }
                        if (opening && "_li".equals(tag)) {
                            checkNewline(output);
                            truss.pushSpan(new TabStopSpan.Standard(INDENT));
                            output.append(lists.peek().getNextLabel());
                            output.append("\t");
                        }
                        if (!opening && "_li".equals(tag)) {
                            checkNewline(output);
                            truss.popSpan();
                        }
                        if (opening && "tab".equals(tag)) {
                            output.append("\t");
                        }

                    }

                    private void checkNewline(Editable output) {
                        if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                    }

                });

    }

    private static final class Attributes {
        private final HashMap<String, String> mAttributes = new HashMap<>();

        private Attributes(XMLReader reader) {
            try {
                Field elementField = reader.getClass().getDeclaredField("theNewElement");
                elementField.setAccessible(true);
                Object element = elementField.get(reader);
                if (element != null) {
                    Field attsField = element.getClass().getDeclaredField("theAtts");
                    attsField.setAccessible(true);
                    Object atts = attsField.get(element);
                    Field dataField = atts.getClass().getDeclaredField("data");
                    dataField.setAccessible(true);
                    String[] data = (String[]) dataField.get(atts);
                    Field lengthField = atts.getClass().getDeclaredField("length");
                    lengthField.setAccessible(true);
                    int len = (Integer) lengthField.get(atts);

                    for (int i = 0; i < len; i++) {
                        mAttributes.put(data[i * 5 + 1], data[i * 5 + 4]);
                    }
                }
            } catch (Exception ignore) {
            }

        }

        private boolean has(String key) {
            return mAttributes.containsKey(key) && mAttributes.get(key) != null;
        }

        private String get(String key) {
            String val = mAttributes.get(key);
            if (val == null) {
                return "";
            } else {
                return val;
            }
        }

        private String getCanBeNull(String key) {
            return mAttributes.get(key);
        }

    }

    private static class Truss {
        private final SpannableStringBuilder builder;
        private final Deque<Span> stack;

        Truss() {
            builder = new SpannableStringBuilder();
            stack = new ArrayDeque<>();
        }

        Truss(SpannableStringBuilder ssb) {
            builder = ssb;
            stack = new ArrayDeque<>();
        }

        public Truss append(String string) {
            builder.append(string);
            return this;
        }

        public Truss append(CharSequence charSequence) {
            builder.append(charSequence);
            return this;
        }

        public Truss append(char c) {
            builder.append(c);
            return this;
        }

        public Truss append(int number) {
            builder.append(String.valueOf(number));
            return this;
        }

        /**
         * Starts {@code span} at the current position in the builder.
         */
        Truss pushSpan(Object span) {
            stack.addLast(new Span(builder.length(), span));
            return this;
        }

        /**
         * End the most recently pushed span at the current position in the builder.
         */
        Truss popSpan() {
            Span span = stack.removeLast();
            builder.setSpan(span.span, span.start, builder.length(), SPAN_INCLUSIVE_EXCLUSIVE);
            return this;
        }

        /**
         * Create the final {@link CharSequence}, popping any remaining spans.
         */
        public CharSequence build() {
            while (!stack.isEmpty()) {
                popSpan();
            }
            return builder; // TODO make immutable copy?
        }

    }

    private static final class Span {
        final int start;
        final Object span;

        Span(int start, Object span) {
            this.start = start;
            this.span = span;
        }
    }

    private static final class HtmlList {
        private final int mType;
        private int index = 0;
        private final HtmlList mParent;
        private boolean dontIndent = false;

        private static final int TYPE_BULLET = 0;
        private static final int TYPE_INT = 1;
        private static final int TYPE_ROMAN = 2;
        private static final int TYPE_ALPHA = 3;
        private static final int TYPE_ALPHA_UPPER = 4;
        private static final int TYPE_ROMAN_UPPER = 5;

        private static final String[] ROMAN_NUMERALS = new String[]{
                "I", "II", "III", "IV", "V", "VI", "VII", "IIX", "IX", "X",
                "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XIIX", "XIX", "XX"
        };

        private HtmlList(int type, HtmlList parent) {
            mType = type;
            mParent = parent;
        }

        private void dontIndent() {
            dontIndent = true;
        }

        int getFirstIndent() {
            return (getLevel() - 1) * INDENT;
        }

        int getOtherIndent() {
            if (mParent != null) {
                return getFirstIndent();
            } else {
                return INDENT;
            }
        }

        private int getLevel() {
            if (mParent == null) {
                return 1;
            } else {
                return mParent.getLevel() + (dontIndent ? 0 : 1);
            }
        }

        String getNextLabel() {
            index++;
            return getCurrentLabel();
        }

        private String getCurrentLabel() {
            StringBuilder builder = new StringBuilder();
            switch (mType) {
                case TYPE_BULLET:
                    builder.append("\u2022");
                    break;
                case TYPE_INT:
                    if (mParent != null && mParent.mType == TYPE_INT) {
                        builder.append(mParent.getCurrentLabel());
                        builder.append(".");
                    }
                    builder.append(index);
                    break;
                case TYPE_ROMAN:
                    builder.append(ROMAN_NUMERALS[index - 1].toLowerCase());
                    builder.append(".");
                    break;
                case TYPE_ROMAN_UPPER:
                    builder.append(ROMAN_NUMERALS[index - 1]);
                    builder.append(".");
                    break;
                case TYPE_ALPHA:
                    builder.append("(");
                    builder.append((char) ('a' + (index - 1)));
                    builder.append(")");
                    break;
                case TYPE_ALPHA_UPPER:
                    builder.append("(");
                    builder.append((char) ('A' + (index - 1)));
                    builder.append(")");
                    break;
            }
            return builder.toString();
        }

    }
}
