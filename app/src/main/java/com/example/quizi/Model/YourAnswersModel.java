package com.example.quizi.Model;

import java.util.List;

public class YourAnswersModel {

    private String question;
    private List<OptionModel> optionModels;

    public YourAnswersModel(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<OptionModel> getOptionsModels() {
        return optionModels;
    }

    public void setOptionsModels(List<OptionModel> optionModels) {
        this.optionModels = optionModels;
    }
}
