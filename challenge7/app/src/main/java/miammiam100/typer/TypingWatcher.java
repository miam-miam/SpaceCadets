package miammiam100.typer;

import android.graphics.Color;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * A Typing watcher that is run when whenever the user inputs something into the input field.
 */
class TypingWatcher implements TextWatcher {
    private final String[] text;
    private final SpannableString span;
    private final WPMUpdater wpmUpdater;
    private final TextView textView;
    private final UnderlineSpan underline = new UnderlineSpan();
    private final ForegroundColorSpan correct = new ForegroundColorSpan(Color.GREEN);
    private final BackgroundColorSpan fail = new BackgroundColorSpan(Color.RED);
    private final ScrollView scrollView;
    private final HashMap<Integer, Integer> charToLineHeight = new HashMap<>();
    private final EditText input;
    private final int lastWordIndex = 0;
    private int index = -1;

    public TypingWatcher(SpannableString text, WPMUpdater wpmUpdater, TextView textView, ScrollView scrollView, EditText input) {
        this.text = text.toString().split("(?<= )");
        this.span = text;
        this.wpmUpdater = wpmUpdater;
        this.textView = textView;
        this.span.setSpan(underline, 0, this.text[0].length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.textView.setText(this.span);
        this.scrollView = scrollView;
        this.input = input;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * Ran when the text is changed.
     *
     * @param s An editable view of the text inputted.
     */
    @Override
    public void afterTextChanged(Editable s) {
        // Only ran the first time
        if (index == -1) {
            wpmUpdater.startTime = System.nanoTime();
            index = 0;
            Layout layout = textView.getLayout();
            for (int line = 0; line < layout.getLineCount(); line++) {
                charToLineHeight.put(layout.getOffsetForHorizontal(line, 0), layout.getLineTop(line));
            }
        }

        String input = s.toString();

        // Check if exactly matches the text
        if (s.toString().equals(text[index])) {

            s.clear();
            wpmUpdater.charsWritten += text[index].length();
            index += 1;
            if (index == text.length) {
                s.clear();
                span.removeSpan(underline);
                span.setSpan(correct, 0, wpmUpdater.charsWritten, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                this.textView.setText(span);
                wpmUpdater.endTime = System.nanoTime();
                this.input.removeTextChangedListener(this);
                return;
            }
            span.setSpan(underline, wpmUpdater.charsWritten, wpmUpdater.charsWritten + text[index].length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(correct, 0, wpmUpdater.charsWritten, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Integer lineHeight = charToLineHeight.get(wpmUpdater.charsWritten);
            if (lineHeight != null) {
                scrollView.post(() -> scrollView.scrollTo(0, lineHeight));
            }
            // Get where the user got it wrong.
        } else {
            int correctIndex = getCorrectIndex(input);
            span.setSpan(correct, 0, wpmUpdater.charsWritten + correctIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(fail, wpmUpdater.charsWritten + correctIndex, wpmUpdater.charsWritten + input.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        this.textView.setText(this.span);
    }

    /**
     * Find where the input does not match with the text.
     *
     * @param input The string to compare to
     * @return The index at which the character no longer matches
     */
    public int getCorrectIndex(String input) {
        int i;
        for (i = 0; i < Math.min(input.length(), text[index].length()); i++) {
            if (input.charAt(i) != text[index].charAt(i)) {
                return i;
            }
        }
        return i;
    }
}
