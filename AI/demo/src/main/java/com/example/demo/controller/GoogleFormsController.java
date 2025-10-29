package com.example.demo.controller;

import com.example.demo.service.GoogleFormsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/google")
@CrossOrigin(origins = "*")
public class GoogleFormsController {

    @Autowired
    private GoogleFormsService googleFormService;

    // Test lấy thông tin form
    @GetMapping("/form")
    public String getForm(@RequestParam String token, @RequestParam String formId) {
        return googleFormService.getFormDetails(token, formId);
    }

    // Test tạo form mới
//    @PostMapping("/form")
//    public String createForm(@RequestParam String token) {
//        return googleFormService.createFormWithThreeQuestions(token) ;
//    }

//    Lấy thông tin Form
    @PostMapping("/form")
    public String createForm(@RequestParam String token, @RequestParam String language) {
        return googleFormService.createFormWithDynamicQuestions(token,language);
    }

    @GetMapping("/test/data")
    public String testQuestionData( String language) {
        return googleFormService.testApiDataFetch(language);
    }

@PostMapping("/upload/test")
public String uploadMultipleFilesTest(@RequestParam("files") List<MultipartFile> files) {

    if (files.isEmpty()) {
        return "LỖI: Không nhận được file nào được gửi lên.";
    }
    StringBuilder result = new StringBuilder();
    int successCount = 0;

    for (MultipartFile file : files) {
        if (file.isEmpty()) {
            result.append("Cảnh báo: Phát hiện một phần tử rỗng trong danh sách.\n");
            continue;
        }

        String fileName = file.getOriginalFilename();
        long fileSizeKB = file.getSize() / 1024;

        result.append("- Đã nhận: ").append(fileName)
                .append(" (Kích thước: ").append(fileSizeKB).append(" KB)\n");

        System.out.println("Đã xử lý file: " + fileName + ", Size: " + fileSizeKB + " KB");
        successCount++;
    }

    // --- Trả về kết quả tổng hợp ---
    return String.format("THÀNH CÔNG! Đã nhận và xử lý %d file:\n%s", successCount, result.toString());
}
}