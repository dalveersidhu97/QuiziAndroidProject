package com.example.quizi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizi.QuizAnswerActivity;
import com.example.quizi.Model.QuizModel;
import com.example.quizi.R;

import java.util.List;


public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private Context context;
    private final List<QuizModel> quizModelList;


    public QuizAdapter(Context context, List<QuizModel> quizModelList) {
        this.context = context;
        this.quizModelList  = quizModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        QuizModel quizModel= quizModelList.get(position);

        // show quiz details
        holder.title.setText("Category: "+quizModel.getTitle());
        holder.score.setText("Score: "+quizModel.getScore()+"/"+quizModel.getTotal());
        holder.difficulty.setText("Difficulty: "+quizModel.getDifficulty());
        holder.timeTaken.setText("Time taken: "+getTimeString(quizModel.getSecsTaken())+"/"+getTimeString(quizModel.getTotalSecs()));
        holder.status.setText("Status: "+quizModel.getStatus());

        // got the quiz Review activity when the quiz card is clicked
        holder.parent.setOnClickListener(v->{

            Intent intent = new Intent(context, QuizAnswerActivity.class);
            // send quiz model so that the next activity can fetch the quiz details from database for the clicked quiz
            Bundle bundle = new Bundle();
            bundle.putSerializable("quizModel", quizModel);
            intent.putExtras(bundle);
            // start Review activity
            context.startActivity(intent);
        });
    }

    // format the time
    private String getTimeString(int secs){
        int mins = secs / 60;
        int seconds = secs % 60;
        String min = (mins==0)?"0:": (mins<10)?"0"+mins+":":""+mins+":";
        String sec = (seconds < 10)?"0"+seconds:""+seconds;
        return min+sec;
    }

    @Override
    public int getItemCount() {
        return quizModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, score, difficulty, status, timeTaken;
        private final View parent;
        ViewHolder(View view) {
            super(view);
            parent = view;
            title = view.findViewById(R.id.quiz_title);
            score = view.findViewById(R.id.score);
            difficulty = view.findViewById(R.id.difficulty);
            status = view.findViewById(R.id.status);
            timeTaken = view.findViewById(R.id.timeTaken);
        }
    }
}