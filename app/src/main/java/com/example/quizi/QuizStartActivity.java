package com.example.quizi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizi.Model.QuestionModel;
import com.example.quizi.Model.QuizModel;
import com.example.quizi.retrofit.ApiClient;
import com.example.quizi.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizStartActivity extends AppCompatActivity {

    private Timer timer;
    ArrayList<QuestionModel> questionModels;
    TextView textViewQuestionTitle, textViewTimer, textViewNum;
    Button buttonNext;
    LinearLayout linearLayoutOptions;
    int currentQuestion, totalQuestions, totalMillis, secLeft, isCorrect, lastQuestionId,SCORE;
    String question, categoryTitle, selectedAnswer, difficulty, STATUS;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_start);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        init();

        startQuiz();

    }

    private void init() {
        // views
        textViewQuestionTitle = findViewById(R.id.question_title);
        textViewTimer = findViewById(R.id.timer);
        buttonNext = findViewById(R.id.btn_next);
        textViewNum = findViewById(R.id.question_num);
        linearLayoutOptions = findViewById(R.id.linearLayoutOptions);

        // vars
        timer = new Timer();
        totalQuestions = getIntent().getExtras().getInt("totalQuestions");
        categoryTitle = getIntent().getExtras().getString("title");
        totalMillis = getIntent().getExtras().getInt("millis");
        difficulty = getIntent().getExtras().getString("levelType");
        currentQuestion = -1;
        SCORE = 0;
        STATUS = "Not completed";
        secLeft = totalMillis / 1000;
        DB = new DBHelper(this);

        selectedAnswer = "FIRST QUESTION";

        buttonNext.setOnClickListener(view -> {
            setNextQuestion();
        });
    }

    private void endQuiz() {

        timer.cancel();
        buttonNext.setEnabled(false);

        // Save Last Answer before ending and set quiz status to completed
        DB.insertAnswer(lastQuestionId, selectedAnswer, isCorrect, totalMillis/1000-secLeft);
        DB.markQuizAsCompleted(STATUS);

        if(!selectedAnswer.isEmpty()) { // toast for last question
            Toast toast = Toast.makeText(getApplicationContext(), (isCorrect == 0) ? "Incorrect" : "Correct", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 250);
            toast.show();
        }

        // GO TO QUIZ RESULT ACTIVITY
        Intent intent = new Intent(getApplicationContext(), QuizAnswerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("quizModel", new QuizModel(DB.getLastId("Quiz"), totalQuestions, SCORE, totalMillis/1000, totalMillis/1000-secLeft, categoryTitle, difficulty, STATUS));
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void startQuiz() {

        // listen to api result and start the quiz after fetching questions form api
        OnDataListener onDataListener = (ArrayList<QuestionModel> questionList)->{

            if(questionList.size() > 0){
                // CREATE QUIZ
                boolean created = DB.createQuiz(categoryTitle, totalQuestions, difficulty, totalMillis/1000);
                Log.d("TAG", "Quiz created: "+created);

                questionModels = questionList;
                startTimer();
                setNextQuestion();
            }else{
                Log.d("TAG", "No Response...Retrying....: ");
                Toast.makeText(getApplicationContext(), "No Response...Please try again....: ", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        // get All questions from API And start the quiz
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String catID = getIntent().getExtras().getString("catID");
        appQuestions(apiInterface.appQuestions(totalQuestions, catID, difficulty), onDataListener);
    }

    private void setNextQuestion() {

        // if its last question change the button from next to finish
        if (currentQuestion == totalQuestions - 2) {
            buttonNext.setText("Finish");
            buttonNext.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }

        // end quiz last question was the last one
        if (currentQuestion >= totalQuestions - 1) {
            STATUS = "Completed";
            endQuiz();

        } else {

            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Please select an answer.", Toast.LENGTH_SHORT).show();
                return;

            }else if(!selectedAnswer.equals("FIRST QUESTION")){
                // SAVE LAST ANSWER
                Toast toast = Toast.makeText(getApplicationContext(), (isCorrect==0)?"Incorrect":"Correct", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,250);
                toast.show();
                DB.insertAnswer(lastQuestionId, selectedAnswer, isCorrect, totalMillis/1000-secLeft);
            }

            // SET SELECTED ANSWER EMPTY
            selectedAnswer = "";

            // SHOW NEXT QUESTION
            currentQuestion++;
            QuestionModel questionModel = questionModels.get(currentQuestion);
            ArrayList<String> incorrect_answers = new ArrayList<>(Arrays.asList(questionModel.getIncorrect_answers())); // incorrect answers
            ArrayList<String> options = new ArrayList<>(); // Empty options list

            int randomPosition = (int)(Math.random() * (incorrect_answers.size()+1)); // random position for correct answer
            Log.d("TAG", "random position: "+randomPosition);

            // PUT Correct Answer at random POSITION
            for (int i = 0; i <= incorrect_answers.size(); i++) {

                if(i==randomPosition)
                    options.add(questionModel.getCorrect_answer());
                if(i<incorrect_answers.size())
                    options.add(incorrect_answers.get(i));
            } // options list prepared

            // SAVE QUESTION AND Options
            lastQuestionId = DB.insertQuestion(questionModel.getQuestion());
            DB.insertOptions(lastQuestionId, options, questionModel.getCorrect_answer());

            // set question title
            textViewNum.setText((currentQuestion + 1) + "/" + totalQuestions);
            textViewQuestionTitle.setText(questionModel.getQuestion());

            // create option radio button
            createOptionsRadioGroup(options, questionModel);

        }
    }

    private void createOptionsRadioGroup(ArrayList<String> options, QuestionModel questionModel){

        //create a radio group
        RadioGroup radioGroup = new RadioGroup(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 0, 40, 0);
        radioGroup.setLayoutParams(params);

        // create radio buttons for all options
        for (int i = 0; i < options.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 40, 0, 0);
            radioButton.setPadding(15, 15, 15, 15);
            radioButton.setLayoutParams(params);
            radioButton.setText(options.get(i));
            radioButton.setId(ViewCompat.generateViewId());
            radioGroup.addView(radioButton);
        }

        // clear option
        linearLayoutOptions.removeAllViews();

        // add radio group to linearLayout
        linearLayoutOptions.addView(radioGroup);

        // add click listener to radio group
        radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId)->{
                RadioButton r = group.findViewById(checkedId);
                question = questionModel.getQuestion();
                selectedAnswer = r.getText().toString();
                isCorrect = (questionModel.getCorrect_answer().equals(selectedAnswer)) ? 1 : 0;
                SCORE += isCorrect;
                Log.d("TAG", "isCorrect: "+isCorrect);
        });
    }

    private void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (secLeft <= 0) {
                    Log.d("TAG", "TIME OVER");
                    runOnUiThread(()->{
                        Toast.makeText(getApplicationContext(), "Time OUT!", Toast.LENGTH_SHORT).show();
                        STATUS = "Time Over!";
                        secLeft = 0;
                        endQuiz();
                    });
                }else{
                    secLeft--;
                    runOnUiThread(()->textViewTimer.setText(secLeft / 60 + ":" + secLeft % 60));
                }
            }
        }, 0, 1000);
    }

    // Fetch all Questions from API
    private void appQuestions(Call<String> result, OnDataListener onDataListener) {
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful()) {

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(response.body());

                        // Got the questions List ..... Call Event Listener
                        onDataListener.onData(getQuestionList(jsonObject));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("TAG", e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // loop through the JSON Object Result and return QuestionModel List
    private ArrayList<QuestionModel> getQuestionList(JSONObject jsonObject) {

        ArrayList<QuestionModel> questionModels = new ArrayList<>();

        try{

            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObject = new JSONObject(jsonArray.getString(i));

                String category = URLDecoder.decode(questionObject.getString("category"), "UTF-8");
                String type = URLDecoder.decode(questionObject.getString("type"), "UTF-8");
                String question = URLDecoder.decode(questionObject.getString("question"), "UTF-8");
                String correct_answer = URLDecoder.decode(questionObject.getString("correct_answer"), "UTF-8");
                String difficulty = URLDecoder.decode(questionObject.getString("difficulty"), "UTF-8");

                JSONArray arrayIncorrectAnswers = questionObject.getJSONArray("incorrect_answers");
                String[] incorrect_answers = new String[arrayIncorrectAnswers.length()];

                for (int j = 0; j < arrayIncorrectAnswers.length(); j++)
                    incorrect_answers[j] = URLDecoder.decode(arrayIncorrectAnswers.getString(j), "UTF-8");

                if (category.indexOf(':') != -1)
                    category = category.substring(category.indexOf(':') + 1);

                QuestionModel questionModel = new QuestionModel(category, type, difficulty, question, correct_answer, incorrect_answers);

                questionModels.add(questionModel);
            }

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return questionModels;
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz?")
                .setMessage("Are you sure you want exit the Quiz?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        STATUS = "Not completed";
                        endQuiz();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}

interface OnDataListener {
    void onData(ArrayList<QuestionModel> questionList);
}