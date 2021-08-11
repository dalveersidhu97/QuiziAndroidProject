package com.example.quizi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizi.Model.CategoryModel;
import com.example.quizi.QuizDetailsActivity;
import com.example.quizi.R;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private final List<CategoryModel> categoryModelList;

    public CategoryAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList  = categoryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        CategoryModel categoryModel= categoryModelList.get(position);

        //set title for the card category
        holder.cat_name.setText(categoryModel.getCategory_name());

        // start quizDetail activity with appropriate data as the user click on easy, medium or hard quiz button
        holder.btn_easy.setOnClickListener(view -> startActivity(categoryModel, "easy", 10, 9));
        holder.btn_medium.setOnClickListener(view -> startActivity(categoryModel, "medium", 10, 7));
        holder.btn_hard.setOnClickListener(view ->startActivity(categoryModel, "hard", 15, 5));

    }

    // send to the quiz detail activity with quiz data
    private void startActivity(CategoryModel c, String level, int totalQuestions, int secsPerQuestion){

        int totalSecs = totalQuestions * secsPerQuestion;

        Intent intent = new Intent(context, QuizDetailsActivity.class);
        intent.putExtra("catID",c.getId());
        intent.putExtra("title",c.getCategory_name());
        intent.putExtra("levelType",level);
        intent.putExtra("totalQuestions",totalQuestions);
        intent.putExtra("totalTime",totalSecs / 60 +" min "+totalSecs % 60+" sec");
        intent.putExtra("millis", totalSecs*1000);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cat_name;
        private final Button btn_easy, btn_medium, btn_hard;

        ViewHolder(View view) {
            super(view);
            cat_name = view.findViewById(R.id.cat_name);
            btn_easy = view.findViewById(R.id.btn_easy);
            btn_medium = view.findViewById(R.id.btn_medium);
            btn_hard = view.findViewById(R.id.btn_hard);
        }
    }
}