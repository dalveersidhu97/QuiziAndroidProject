package com.example.quizi;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizi.Adapter.CategoryAdapter;
import com.example.quizi.Model.CategoryModel;
import com.example.quizi.retrofit.ApiClient;
import com.example.quizi.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private CategoryAdapter categoryAdapter;
    private RecyclerView categoryRecycler;
    private List<CategoryModel> categoryModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        initViews();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        // get categories form api and set adapter
        appCategories(apiInterface.appCategories());
    }

    // initialize views
    private void initViews(){
        categoryRecycler =  findViewById(R.id.categoryRecycler);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        categoryModelList = new ArrayList<>();
    }

    // get all the categories from API and set the Adapter
    private void appCategories(Call<String> result) {
            result.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response.body());
                            JSONArray jsonArray = jsonObject.getJSONArray("trivia_categories");

                            // extract category name and ids from json object
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject category = new JSONObject(jsonArray.getString(i));

                                String categoryName = URLDecoder.decode(category.getString("name"), "UTF-8");
                                String categoryId = URLDecoder.decode(category.getString("id"), "UTF-8");

                                if(categoryName.indexOf(':')!=-1)
                                    categoryName = categoryName.substring(categoryName.indexOf(':')+1);

                                CategoryModel categoryModel = new CategoryModel(categoryId, categoryName);
                                // prepare category model list for the adapter
                                categoryModelList.add(categoryModel);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        // set the adapter
                        categoryAdapter = new CategoryAdapter(MainActivity.this, categoryModelList);
                        categoryAdapter.notifyDataSetChanged();
                        categoryRecycler.setAdapter(categoryAdapter);
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_quizes) {
            Intent intent=new  Intent(getApplicationContext(),QuizResultActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

