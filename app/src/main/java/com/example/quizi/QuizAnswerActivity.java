package com.example.quizi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.quizi.Adapter.AnswersAdapter;
import com.example.quizi.Model.QuizModel;
import com.example.quizi.Model.YourAnswersModel;

import java.util.ArrayList;
import java.util.Objects;

public class QuizAnswerActivity extends AppCompatActivity {

    TextView title, score, difficulty, status, time;
    QuizModel quizModel;
    DBHelper DB;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_answer);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        setAdapter();
    }
    // initialize all the variables and views
    public void init(){

        difficulty = findViewById(R.id.quiz_answer_difficulty);
        title = findViewById(R.id.quiz_answer_title);
        score = findViewById(R.id.quiz_answer_score);
        status = findViewById(R.id.quiz_answer_status);
        time = findViewById(R.id.quiz_answer_timeTaken);

        recyclerView = findViewById(R.id.answerRecycler);

        quizModel= (QuizModel) this.getIntent().getExtras().getSerializable("quizModel");

        title.setText("Category: "+quizModel.getTitle().trim());
        difficulty.setText("Difficulty: "+quizModel.getDifficulty());
        score.setText("Score: "+quizModel.getScore()+"/"+quizModel.getTotal());
        time.setText("Time taken: "+getTimeString(quizModel.getSecsTaken())+"/"+getTimeString(quizModel.getTotalSecs()));
        status.setText("Status: "+quizModel.getStatus());

        DB = new DBHelper(this);
    }

    // get all questions and answers
    // set adapter
    private void setAdapter(){

        ArrayList<YourAnswersModel> yourAnswersModels = DB.getQuizData(quizModel.getId());

        AnswersAdapter answersAdapter = new AnswersAdapter(this, yourAnswersModels);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(answersAdapter);

    }
    private String getTimeString(int secs){
        int mins = secs / 60;
        int seconds = secs % 60;
        String min = (mins<10)?"0"+mins+":":""+mins+":";
        String sec = (seconds < 10)?"0"+seconds:""+seconds;
        return min+sec;
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}