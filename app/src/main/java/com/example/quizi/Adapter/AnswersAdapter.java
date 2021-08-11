package com.example.quizi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizi.Model.OptionModel;
import com.example.quizi.R;
import com.example.quizi.Model.YourAnswersModel;

import java.util.List;


public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {
    private Context context;
    private final List<YourAnswersModel> yourAnswersModels;


    public AnswersAdapter(Context context, List<YourAnswersModel> yourAnswersModels) {
        this.context = context;
        this.yourAnswersModels  = yourAnswersModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        YourAnswersModel yourAnswersModel= yourAnswersModels.get(position);

        holder.questionTitle.setText(yourAnswersModel.getQuestion());
        List<OptionModel> optionModels = yourAnswersModels.get(position).getOptionsModels();

        // create text view for i number of options
        for (int i = 0; i < optionModels.size(); i++) {

            // dynamic text for
            TextView textView = new TextView(context);
            String text = optionModels.get(i).getOption().trim();
            String append = "";

            // options neither correct not selected by the user
            textView.setTextColor(context.getResources().getColor(R.color.black));
            textView.setBackgroundColor(context.getResources().getColor(R.color.light_grey));

            // options selected by the user
            if(optionModels.get(i).getIsSelectedByUser() == 1) {
                textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
                textView.setTextColor(context.getResources().getColor(android.R.color.white));
                append = " - Wrong";
            }
            // correct option
            if(optionModels.get(i).getIsCorrect() == 1) {
                textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                textView.setTextColor(context.getResources().getColor(android.R.color.white));
                append = " - Answer";
            }
            // green background it the option is correct and selected by the user
            if(optionModels.get(i).getIsCorrect() == 1 && optionModels.get(i).getIsSelectedByUser() == 1) {
                textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
                append = " - Correct";
            }
            // margins and paddings for the option text view
            textView.setTextSize(16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 0);
            textView.setLayoutParams(params);
            textView.setPadding(15, 10, 10, 10);

            textView.setText(text+append);

            holder.linearLayout.addView(textView);
        }

    }


    @Override
    public int getItemCount() {
        return yourAnswersModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionTitle;
        private final LinearLayout linearLayout;

        ViewHolder(View view) {
            super(view);
            questionTitle = view.findViewById(R.id.answer_question);
            linearLayout = view.findViewById(R.id.answer_options);
        }
    }
}