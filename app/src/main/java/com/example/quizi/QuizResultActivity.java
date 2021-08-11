package com.example.quizi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.quizi.Adapter.QuizAdapter;
import com.example.quizi.Model.QuizModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizResultActivity extends AppCompatActivity {

    DBHelper DB;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Your Quizes");
        init();
        setAdapter();
    }

    // init Views
    private void init(){
        DB = new DBHelper(this);
        recyclerView = findViewById(R.id.quizRecycler);
    }

    // get Quiz list
    private List<QuizModel> getQuizModelList(){

        List<QuizModel> quizModels = new ArrayList<>();

        Cursor cursor = DB.getQuizes();
        if(cursor.getCount() > 0){

            while (cursor.moveToNext()){
                // id , total, score, totalSecs, secsTaken, title, difficulty, status
                QuizModel quizModel = new QuizModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7));
                quizModels.add(quizModel);
            }
        }

        return quizModels;
    }

    // set adapter
    private void setAdapter(){

        List<QuizModel> quizModels = getQuizModelList();
        QuizAdapter quizAdapter = new QuizAdapter(this, quizModels);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(quizAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}