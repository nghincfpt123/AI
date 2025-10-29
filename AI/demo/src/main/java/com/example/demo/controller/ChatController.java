//package com.example.demo.controller;
//
//import com.example.demo.dto.ChatRequest;
//import com.example.demo.service.ChatService;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@CrossOrigin(origins = "*")
//public class ChatController {
//    private final ChatService chatService;
//
//    public ChatController(ChatService chatService) {
//        this.chatService = chatService;
//    }
//    @PostMapping("/chat")
//    String chat(@RequestBody ChatRequest request) {
//        return chatService.chat(request);
//    }
//
//    @PostMapping("/chat_image")
//    String  chatWithImage( @RequestParam(value = "file", required = false) MultipartFile file,
//                                 @RequestParam("message") String message) {
//        return chatService.chatWithImage(file, message);
//    }
//
//
//}
