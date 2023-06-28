package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GPTTaskDelegate {
    private TextView emojiTextView;

    private ProgressBar loader;

    private ImageView searchBtn;

    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emojiTextView = findViewById(R.id.emojiTextView);
        loader = findViewById(R.id.loader);
        searchText = findViewById(R.id.searchText);
        searchBtn = findViewById(R.id.searchBtn);

        loader.setVisibility(View.GONE);

        searchBtn.setOnClickListener(v -> {
            String query = searchText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                getEmoji(query);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a movie name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getEmoji(String movie) {
        loader.setVisibility(View.VISIBLE);
        (new GPTTask("Convert movie titles into emoji.", movie, this)).execute();
    }

    @Override
    public void onGotEmoji(String response) {
        emojiTextView.setText(response);
        loader.setVisibility(View.GONE);
    }
}
