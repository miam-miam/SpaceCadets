package miammiam100.typer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 500;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = new TextView(this);
        textView.setText("0 wpm");
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));
        EditText input = findViewById(R.id.input);
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);

        runnable = new WPMUpdater(handler, textView, 500);
        TextView text = findViewById(R.id.textView);

        SpannableString word = new SpannableString("Hello World! this is a test I am going to keep on writing in here to see what is going to happHello World! this is a test I am going to keep on writing in here to see what is going to happenHello World! this is a test I am going to keep on writing in here to see what is going to happenen");
        input.addTextChangedListener(new TypingWatcher(word, (WPMUpdater) runnable, text));
    }

    @Override
    protected void onResume() {
        handler.postDelayed(runnable, delay);
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        super.onPause();
    }

}