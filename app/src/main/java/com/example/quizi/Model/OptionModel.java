package com.example.quizi.Model;

public class OptionModel {

    private int isCorrect, isSelectedByUser;
    private String option;

    public OptionModel(String option, int isCorrect, int isSelectedByUser) {
        this.isCorrect = isCorrect;
        this.isSelectedByUser = isSelectedByUser;
        this.option = option;
    }

    public int getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }

    public int getIsSelectedByUser() {
        return isSelectedByUser;
    }

    public void setIsSelectedByUser(int isSelectedByUser) {
        this.isSelectedByUser = isSelectedByUser;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}