package com.example.demo.dto;

import java.util.List;

public class QuestionDto {
    private String question;
    private List<String> options;
    private String answer; // Đáp án (A, B, C, D)
    private String explanation;
    private String difficulty;

    public QuestionDto() {}

    // Getters
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public String getAnswer() { return answer; }
    public String getExplanation() { return explanation; }
    public String getDifficulty() { return difficulty; }

    // Setters
    public void setQuestion(String question) { this.question = question; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setAnswer(String answer) { this.answer = answer; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "question='" + question.substring(0, Math.min(question.length(), 50)) + "...' " +
                ", options=" + options.size() + " options" +
                ", answer='" + answer + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}

