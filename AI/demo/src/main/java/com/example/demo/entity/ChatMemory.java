//package com.example.demo.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Column;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "spring_ai_chat_memory")
//public class ChatMemory {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "conversation_id", length = 36, nullable = false)
//    private String conversationId;
//
//    @Column(name = "content", columnDefinition = "text", nullable = false)
//    private String content;
//
//    @Column(name = "type", length = 10, nullable = false)
//    private String type;
//
//    @Column(name = "timestamp", nullable = false)
//    private LocalDateTime timestamp;
//
//    public ChatMemory() {}
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getConversationId() {
//        return conversationId;
//    }
//
//    public void setConversationId(String conversationId) {
//        this.conversationId = conversationId;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public LocalDateTime getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(LocalDateTime timestamp) {
//        this.timestamp = timestamp;
//    }
//
//}
