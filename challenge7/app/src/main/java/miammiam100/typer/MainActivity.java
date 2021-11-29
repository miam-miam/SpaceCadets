package miammiam100.typer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 500;
    TextView textView;

    /**
     * Ran when the activity is first created.
     *
     * @param savedInstanceState Used to pass to the child onCreate.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = new TextView(this);
        textView.setText("0 wpm");
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textLayout.gravity = Gravity.CENTER;
        textLayout.weight = 1.0f;
        textView.setLayoutParams(textLayout);
        textView.setTextColor(getResources().getColor(R.color.white));

        Button reload = new Button(this);
        reload.setText("Reload");
        reload.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.gravity = Gravity.END;
        layout.weight = 1.0f;
        reload.setLayoutParams(layout);
        reload.setTextColor(getResources().getColor(R.color.white));
        reload.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        });

        LinearLayout topBar = new LinearLayout(this);
        topBar.setGravity(Gravity.CENTER);
        topBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        topBar.addView(textView);
        topBar.addView(reload);


        EditText input = findViewById(R.id.input);
        input.requestFocus();
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(topBar);


        runnable = new WPMUpdater(handler, textView, 800);
        TextView text = findViewById(R.id.textView);

        ScrollView scrollView = findViewById(R.id.scrollView);

        Random random = new Random();
        String textToWrite = getResources().getStringArray(R.array.texts)[random.nextInt(1000)];
        SpannableString word = new SpannableString(textToWrite);
        input.addTextChangedListener(new TypingWatcher(word, (WPMUpdater) runnable, text, scrollView, input));
    }

    /**
     * Resume the WPM Updater.
     */
    @Override
    protected void onResume() {
        handler.postDelayed(runnable, delay);
        super.onResume();
    }

    /**
     * Stop the WPM Updater.
     */
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        super.onPause();
    }

}