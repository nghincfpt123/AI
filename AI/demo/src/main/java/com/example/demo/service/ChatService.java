//package com.example.demo.service;
//
//import com.example.demo.dto.ChatRequest;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.memory.MessageWindowChatMemory;
//import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
//import org.springframework.ai.chat.messages.SystemMessage;
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.content.Media;
//import org.springframework.stereotype.Service;
//import org.springframework.util.MimeTypeUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//public class ChatService {
//
//    private final ChatClient chatClient;
//    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
//
//    public ChatService(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository) {
//        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
//
//        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .chatMemoryRepository(jdbcChatMemoryRepository)
//                .maxMessages(500)
//                .build();
//
//        chatClient = builder
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
//                .build();
//    }
//
//    public String chatWithImage(MultipartFile file, String message){
//
//        if (file.isEmpty()){
//            String conversationId = "conversation1";
//            SystemMessage systemMessage = new SystemMessage("""
//             Your RIKKEI.AI, an AI assistant developed by RIKKEISOFT.
//                You should response with a formal voice
//                """);
//
//            UserMessage userMessage = new UserMessage(message);
//            Prompt prompt = new Prompt(systemMessage, userMessage);
//
//            return chatClient
//                    .prompt(prompt)
//                    .advisors(advisorSpec -> advisorSpec.param(
//                            ChatMemory.CONVERSATION_ID, conversationId
//                    ))
//                    .call()
//                    .content();
//        }else{
//
//            String conversationId = "conversation1";
//        Media media = Media.builder()
//                .mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
//                .data(file.getResource())
//                .build();
//
//        return chatClient.prompt()
//                .system("Your RIKKEI.AI, an AI assistant developed by RIKKEISOFT.")
//                .user(promptUserSpec
//                        -> promptUserSpec.media(media)
//                        .text(message))
//                .advisors(advisorSpec -> advisorSpec.param(
//                        ChatMemory.CONVERSATION_ID, conversationId
//                ))
//                .call()
//                .content();
//}
//    }
//
//    public String chat(ChatRequest request) {
//        String conversationId = "conversation1";
//        SystemMessage systemMessage = new SystemMessage("""
//                You are RIKKEI.AI create by RIKKEISOFT
//                You should response with a formal voice
//                """);
//
//        UserMessage userMessage = new UserMessage(request.message());
//        Prompt prompt = new Prompt(systemMessage, userMessage);
//
//        return chatClient
//                .prompt(prompt)
//                .advisors(advisorSpec -> advisorSpec.param(
//                        ChatMemory.CONVERSATION_ID, conversationId
//                ))
//                .call()
//                .content();
//    }
//}
