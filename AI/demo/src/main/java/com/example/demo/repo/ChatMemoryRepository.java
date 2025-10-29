//package com.example.demo.repo;
//
//import com.example.demo.entity.ChatMemory;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Date;
//import java.util.List;
//
//@Repository
//public interface  ChatMemoryRepository  extends JpaRepository<ChatMemory, Long> {
//
//    @Query(value = "SELECT * FROM spring_ai_chat_memory " +
//            "WHERE DATE(timestamp) = :date " +
//            "ORDER BY timestamp ASC", nativeQuery = true)
//    List<ChatMemory> findByDate(@Param("date") Date date);
//
//}
