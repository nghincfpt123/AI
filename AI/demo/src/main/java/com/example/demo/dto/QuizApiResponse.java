package com.example.demo.dto;

import java.util.List;

public class QuizApiResponse {
    private String status;
    private String message;
    private List<QuestionDto> questions; // Chứa danh sách các câu hỏi

    public QuizApiResponse() {}

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<QuestionDto> getQuestions() { return questions; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }

    @Override
    public String toString() {
        return "QuizApiResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", questionsCount=" + (questions != null ? questions.size() : 0) +
                '}';
    }
}

