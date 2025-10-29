package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ExternalQuestion {

    private String title;
    private List<String> options;
    private String correctAnswer;
    private String explain;

    // SỬ DỤNG @JsonProperty ĐỂ ÁNH XẠ "ID" (JSON) VÀO id (JAVA)
    @JsonProperty("ID")
    private String id;

    // Lưu ý: Nếu các trường khác như 'correctAnswer' cũng dùng camelCase trong Java
    // nhưng dùng PascalCase trong JSON, bạn cũng cần thêm @JsonProperty cho chúng.
    // Hiện tại, ta chỉ tập trung vào 'ID'.

    public ExternalQuestion() {}

    // Getters
    public String getTitle() { return title; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplain() { return explain; }

    // Cập nhật Getter/Setter cho ID (chữ thường)
    public String getId() { return id; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setExplain(String explain) { this.explain = explain; }

    // Cập nhật Setter cho ID (chữ thường)
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return "ExternalQuestion{" +
                "title='" + title + '\'' +
                ", options=" + options +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", explain='" + explain + '\'' +
                ", ID='" + id + '\'' + // Dùng id (chữ thường) ở đây
                '}';
    }
}