package miammiam100.typer;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

class TypingWatcher implements TextWatcher {
    private final String[] text;
    private final SpannableString span;
    private final WPMUpdater wpmUpdater;
    private int index = -1;
    private final TextView textView;
    private final UnderlineSpan underline = new UnderlineSpan();
    private final ForegroundColorSpan correct = new ForegroundColorSpan(Color.GREEN);
    private final BackgroundColorSpan fail = new BackgroundColorSpan(Color.RED);
    private int lastWordIndex = 0;

    public TypingWatcher(SpannableString text, WPMUpdater wpmUpdater, TextView textView) {
        this.text = text.toString().split("(?<= )");
        this.span = text;
        this.wpmUpdater = wpmUpdater;
        this.textView = textView;

        this.span.setSpan(underline, 0, this.text[0].length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.textView.setText(this.span);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (index == -1) {
            wpmUpdater.startTime = System.nanoTime();
            index = 0;
        }

        String input = s.toString();

        if (s.toString().equals(text[index])) {
            s.clear();
            wpmUpdater.charsWritten += text[index].length();
            index += 1;
            span.setSpan(underline, wpmUpdater.charsWritten, wpmUpdater.charsWritten + text[index].length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            boolean isCorrect = input.length() <= text[index].length() && input.startsWith(text[index].substring(0, input.length()));
            if (isCorrect) {
                span.setSpan(correct, wpmUpdater.charsWritten, wpmUpdater.charsWritten + input.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.removeSpan(fail);
            } else {
                span.setSpan(fail, wpmUpdater.charsWritten, wpmUpdater.charsWritten + input.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.removeSpan(correct);
            }
        }
        this.textView.setText(this.span);
    }
}
