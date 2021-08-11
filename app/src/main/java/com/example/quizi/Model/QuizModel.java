package com.example.quizi.Model;

import java.io.Serializable;

public class QuizModel implements Serializable {

    private int id, score, total, totalSecs, secsTaken;
    private String title, status, difficulty;

    public QuizModel(int id, int total, int score, int totalSecs, int secsTaken, String title, String difficulty, String status) {
        this.id = id;
        this.score = score;
        this.total = total;
        this.totalSecs = totalSecs;
        this.secsTaken = secsTaken;
        this.title = title;
        this.status = status;
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public int getTotalSecs() {
        return totalSecs;
    }

    public void setTotalSecs(int totalSecs) {
        this.totalSecs = totalSecs;
    }

    public int getSecsTaken() {
        return secsTaken;
    }

    public void setSecsTaken(int secsTaken) {
        this.secsTaken = secsTaken;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
