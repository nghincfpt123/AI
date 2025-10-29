//package com.example.demo.service;
//
//import com.example.demo.entity.ChatMemory;
//import com.example.demo.repo.ChatMemoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.sql.Date;
//import java.time.LocalDate;
//
//import java.util.List;
//
//@Service
//public class ChatMemoryService {
//
//    @Autowired
//    private ChatMemoryRepository chatMemoryRepository;
//
//    public List<ChatMemory> getAllChatMemories() {
//        return chatMemoryRepository.findAll();
//    }
//
//    public List<ChatMemory> getMessagesByDate(LocalDate date) {
//        Date sqlDate = Date.valueOf(date);
//        return chatMemoryRepository.findByDate(sqlDate);
//    }
//}
