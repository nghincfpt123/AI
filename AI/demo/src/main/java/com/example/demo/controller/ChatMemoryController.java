//package com.example.demo.controller;
//
//import com.example.demo.entity.ChatMemory;
//import com.example.demo.service.ChatMemoryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "*")
//public class ChatMemoryController {
//
//    @Autowired
//    private ChatMemoryService chatMemoryService;
//
//    @GetMapping("/all")
//    public List<ChatMemory> getAllChatMemory() {
//        return chatMemoryService.getAllChatMemories();
//    }
//
////    @GetMapping("/date")
////    public List<ChatMemory> getMessagesByDate(@RequestParam String date) {
////        return chatMemoryService.getMessagesByDate(date);
////    }
//
//}
