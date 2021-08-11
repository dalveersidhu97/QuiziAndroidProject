package com.example.quizi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quizi.Model.OptionModel;
import com.example.quizi.Model.YourAnswersModel;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(@Nullable Context context) {
        super(context, "Quizdb.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create Table Quiz(id INTEGER PRIMARY KEY AUTOINCREMENT, total INTEGER, score INTEGER DEFAULT 0, totalSecs INTEGER, secsTaken INTEGER, title Text, difficulty TEXT, status TEXT DEFAULT 'Not Completed')");
        db.execSQL("create Table QuizAnswers(id INTEGER PRIMARY KEY AUTOINCREMENT, questionId INTEGER , option Text, isCorrect INTEGER DEFAULT 0, isSelectedByUser INTEGER DEFAULT 0, FOREIGN KEY(questionId) REFERENCES QuizQuestions(id) ON DELETE CASCADE)");
        db.execSQL("create Table QuizQuestions(id INTEGER PRIMARY KEY AUTOINCREMENT, quizId INTEGER, question Text, FOREIGN KEY(quizId) REFERENCES Quiz(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Quiz");
    }

    public Boolean createQuiz(String categoryName, int total, String diffculty, int totalSecs){

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues(); // keep things in pair
        // (Coloum, value)
        contentValue.put("title",categoryName);
        contentValue.put("total", total);
        contentValue.put("difficulty", diffculty);
        contentValue.put("totalSecs", totalSecs);
        long result = DB.insert("Quiz",null,contentValue);

        // delete older quizes
        DB.delete("Quiz", "id<?", new String[]{String.valueOf(getLastId("Quiz")-19)});

        return result != -1;
    }

    public int insertQuestion(String question){

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put("question",question);
        contentValue.put("quizId", getLastId("Quiz"));
        long result = DB.insert("QuizQuestions",null,contentValue);

        if(result <= 0) return 0;
        return getLastId("QuizQuestions");
    }

    public Boolean insertOptions(int questionId, List<String> options, String correctAnswer){

        SQLiteDatabase DB = this.getWritableDatabase();

        if(options.size() == 0)
            return false;

        for(String option : options){
            ContentValues contentValue = new ContentValues();
            contentValue.put("questionId",questionId);
            contentValue.put("option", option);
            contentValue.put("isCorrect", (option.equals(correctAnswer)?1:0));
            DB.insert("QuizAnswers",null,contentValue);
        }

        return true;
    }

    public Boolean insertAnswer(int questionId, String answer, int isCorrect, int secsTaken){


        Log.d("TAG", "questionId: "+questionId);

        if(questionId==0)
            return false;

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValue = new ContentValues();
        contentValue.put("isSelectedByUser", 1);

        int result = DB.update("QuizAnswers",contentValue, "questionId = '"+questionId+"' and option = ?",new String[]{answer});

        if(result>0)
            return updateQuiz(isCorrect, secsTaken);

        Log.d("TAG", "update Quiz: False");
        return false;

    }
    public Boolean updateQuiz(int score, int secsTaken){
        SQLiteDatabase DB = this.getWritableDatabase();
        int lastQuizId = getLastId("Quiz");

        Log.d("TAG", "lastQuizId: "+lastQuizId);
        Cursor cursor = DB.rawQuery("UPDATE Quiz SET score = (score+?), secsTaken = ? where id = ?",
                new String[]{String.valueOf(score), String.valueOf(secsTaken), String.valueOf(lastQuizId)});

        if(cursor.getCount()>0)
            return true;
        return false;
    }

    public Boolean markQuizAsCompleted(String status){

        int quizId = getLastId("Quiz");

        if(quizId==0 )
            return false;

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues(); // keep things in pair
        contentValue.put("status", status);
        Cursor cursor = DB.rawQuery("UPDATE Quiz SET status = ? where id = ?", new String[]{status, String.valueOf(quizId)});

        if(cursor.getCount()>0)
            return true;

        return false;
    }

    public int getLastId(String tableName){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select id from '"+tableName+"' order by id desc limit 1", null) ;

        if(cursor.getCount()==1) {
            cursor.moveToNext();
            return cursor.getInt(0);
        }
        return 0;
    }

    public Cursor getQuizes(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Quiz order by id desc limit 20", null) ;
        return cursor;
    }

    public ArrayList<YourAnswersModel> getQuizData(int quizId){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select qq.id, qq.question from QuizQuestions qq inner join Quiz q on q.id=qq.quizId where q.id='"+quizId+"'", null) ;

        if(cursor.getCount()<=0) Log.d("TAG", "No Data");

        ArrayList<YourAnswersModel> yourAnswersModels = new ArrayList<>();

        while (cursor.moveToNext()) {

            int questionId = cursor.getInt(0);
            Cursor c = DB.rawQuery("Select qa.option, qa.isCorrect, qa.isSelectedbyUser from QuizQuestions qq inner join QuizAnswers qa on qq.id=qa.questionId where qq.id='" + questionId + "'", null);

            ArrayList<OptionModel> optionModels = new ArrayList<>();
            while (c.moveToNext()) {
                OptionModel optionModel = new OptionModel(c.getString(0), c.getInt(1), c.getInt(2));
                optionModels.add(optionModel);
            }

            YourAnswersModel yourAnswersModel = new YourAnswersModel(cursor.getString(1));
            yourAnswersModel.setOptionsModels(optionModels);
            yourAnswersModels.add(yourAnswersModel);
        }

        return yourAnswersModels;
    }
}