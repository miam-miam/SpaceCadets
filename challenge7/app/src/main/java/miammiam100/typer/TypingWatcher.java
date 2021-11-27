package miammiam100.typer;

import android.text.Editable;
import android.text.TextWatcher;

class TypingWatcher implements TextWatcher {
    private final String[] text;
    private final WPMUpdater wpmUpdater;
    private int index = -1;

    public TypingWatcher(String text, WPMUpdater wpmUpdater) {
        this.text = text.split("(?<= )");
        this.wpmUpdater = wpmUpdater;
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
        if (s.toString().equals(text[index])) {
            s.clear();
            wpmUpdater.charsWritten += text[index].length();
            index += 1;
        }
    }

    private void changeWPM(String text) {

    }
}
