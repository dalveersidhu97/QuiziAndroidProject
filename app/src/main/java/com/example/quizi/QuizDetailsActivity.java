package com.example.quizi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class QuizDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getExtras().getString("title");
        String levelType = getIntent().getExtras().getString("levelType");
        int totalQuestions = getIntent().getExtras().getInt("totalQuestions");
        String totalTime = getIntent().getExtras().getString("totalTime");

        TextView txt_title = findViewById(R.id.txt_title);
        TextView txt_questions_no = findViewById(R.id.txt_questions_no);
        TextView txt_total_time = findViewById(R.id.txt_total_time);
        TextView txt_level = findViewById(R.id.txt_level);

        txt_title.setText(title);
        txt_questions_no.setText("Level: "+levelType);
        txt_total_time.setText("Total Questions: "+totalQuestions);
        txt_level.setText("Time: "+totalTime);

        Button startButton = findViewById(R.id.btn_start);

        // go to the start quiz activity and start quiz
        startButton.setOnClickListener(view->{
            Intent intent = new Intent(this, QuizStartActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}