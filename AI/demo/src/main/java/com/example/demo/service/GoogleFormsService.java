package com.example.demo.service;

import com.example.demo.dto.ExternalQuestion;
import com.example.demo.dto.QuestionDto;
import com.example.demo.dto.QuizApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class GoogleFormsService {

    private static final String MOCK_API_URL = "http://10.1.5.24:8080/api/v1/gen-quiz/generate-quiz-multiple-documents";

    private static final String REFRESH_TOKEN = "1//04Coty3JBf5A3CgYIARAAGAQSNwF-L9IrGNLPVVwwpyLeod_0sW7NVD4dvX-I0Z_3_ly53h6hyBI0KE5dbmbKRrEM0kTglr2XH64";

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getFormDetails(String accessToken, String formId) {
        String url = "https://forms.googleapis.com/v1/forms/" + formId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    public String refreshAccessToken() throws Exception {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", REFRESH_TOKEN);
        map.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("access_token").asText();
        } else {
            throw new RuntimeException("Lỗi làm mới token: " + response.getBody());
        }
    }

    public String createFormWithDynamicQuestions(String currentAccessToken, String language) {
        String finalAccessToken = currentAccessToken;
        int maxRetries = 1;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                String creationResponse = createBaseForm(finalAccessToken);

                JsonNode root = objectMapper.readTree(creationResponse);
                String formId = root.path("formId").asText();
                String responderUri = root.path("responderUri").asText();

                if (formId.isEmpty() || responderUri.isEmpty()) {
                    throw new IllegalStateException("Không tìm thấy formId hoặc responderUri.");
                }

                // BƯỚC 2: CẬP NHẬT FORM (thêm câu hỏi động)
                // GỌI PHƯƠNG THỨC MỚI VÀ TRUYỀN URL API MOCK
                addDynamicQuestionsToForm(finalAccessToken, formId, MOCK_API_URL, language);

                // Thành công!
                return "Tạo Form thành công!\n"
                        + "Form ID: " + formId + "\n"
                        + "Link Xem Form: " + responderUri;

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED && attempt == 0) {
                    try {
                        System.out.println("Token hết hạn (401). Đang làm mới...");
                        finalAccessToken = refreshAccessToken();
                        continue;
                    } catch (Exception refreshEx) {
                        return "Lỗi làm mới token: " + refreshEx.getMessage();
                    }
                }
                return "Lỗi API Google: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();

            } catch (Exception e) {
                return "Lỗi xử lý: " + e.getMessage();
            }
        }
        return "Thất bại sau khi thử lại.";
    }

    private String createBaseForm(String accessToken) {
        String url = "https://forms.googleapis.com/v1/forms";
        String jsonBody = """
                {
                  "info": {
                    "title": "SecureQuiz"
                  }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return restTemplate.postForEntity(url, entity, String.class).getBody();
    }

    public String addDynamicQuestionsToForm(String accessToken, String formId, String externalApiUrl,String language) throws Exception {

        // 1. Lấy dữ liệu từ API nguồn (trả về List<QuestionDto>)
        // KHÔNG CẦN BƯỚC ÁNH XẠ 1.5 NỮA!
        List<QuestionDto> allQuestions = fetchQuestionsFromExternalApi(externalApiUrl,language);

        // 2. XỬ LÝ NGẪU NHIÊN VÀ GIỚI HẠN (SHUFFLE VÀ LIMIT)

        // Sắp xếp ngẫu nhiên toàn bộ danh sách
        java.util.Collections.shuffle(allQuestions);

        // Chọn ra 10 câu hỏi đầu tiên
        int limit = Math.min(10, allQuestions.size());
        List<QuestionDto> selectedQuestions = allQuestions.subList(0, limit); // <--- Dùng QuestionDto

        // 3. Bắt đầu xây dựng cấu trúc JSON
        List<Map<String, Object>> requests = new ArrayList<>();

        // --- Request 0: BẬT QUIZ MODE (Giữ nguyên) ---
        Map<String, Object> quizSettingsRequest = Map.of(
                "updateSettings", Map.of(
                        "settings", Map.of(
                                "quizSettings", Map.of("isQuiz", true)
                        ),
                        "updateMask", "quizSettings.isQuiz"
                )
        );
        requests.add(quizSettingsRequest);

        int index = 0;
        for (QuestionDto q : selectedQuestions) {

            String cleanedTitle = q.getQuestion().replaceAll("^\\d+\\.\\s*", "");
            String newTitle = (index + 1) + ". " + cleanedTitle;

            List<Map<String, String>> optionsList = q.getOptions().stream()
                    .map(option -> Map.of("value", option))
                    .toList();

            String answerChar = q.getAnswer(); // Lấy 'A', 'B', 'C', 'D'
            int answerIndex = answerChar.toUpperCase().charAt(0) - 'A'; // Chuyển 'A' -> 0, 'B' -> 1, ...

            String correctOptionValue = "";
            if (answerIndex >= 0 && answerIndex < q.getOptions().size()) {
                correctOptionValue = q.getOptions().get(answerIndex);
            } else {
                correctOptionValue = q.getOptions().get(0);
                System.err.println("CẢNH BÁO: Không tìm thấy đáp án hợp lệ cho câu hỏi: " + q.getQuestion());
            }

            Map<String, Object> grading = Map.of(
                    "pointValue", 1,
                    "correctAnswers", Map.of(
                            // Sử dụng correctOptionValue đã được tính toán
                            "answers", List.of(Map.of("value", correctOptionValue))
                    )
            );

            // 3. Xây dựng Request tạo Item (GIỮ NGUYÊN)
            Map<String, Object> createItemRequest = Map.of(
                    "createItem", Map.of(
                            "item", Map.of(
                                    "title", newTitle,
                                    "questionItem", Map.of(
                                            "question", Map.of(
                                                    "required", true,
                                                    "choiceQuestion", Map.of(
                                                            "type", "RADIO",
                                                            "options", optionsList
                                                    ),
                                                    "grading", grading
                                            )
                                    )
                            ),
                            "location", Map.of("index", index)
                    )
            );

            requests.add(createItemRequest);
            index++;
        }

        // 4. Đóng gói requests và chuyển thành chuỗi JSON cuối cùng
        Map<String, List<Map<String, Object>>> finalJsonMap = Map.of("requests", requests);
        String updateJsonBody = objectMapper.writeValueAsString(finalJsonMap);

        // 5. Gửi Request BatchUpdate
        String updateUrl = "https://forms.googleapis.com/v1/forms/" + formId + ":batchUpdate";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(updateJsonBody, headers);
        return restTemplate.postForEntity(updateUrl, entity, String.class).getBody();
    }
    /**
     * Phương thức kiểm thử: Gọi API Mock và in/trả về dữ liệu đã được ánh xạ (mapping).
     * GIỮ NGUYÊN testApiDataFetch
     */
    public String testApiDataFetch( String language) {
        try {
            List<QuestionDto> questions = fetchQuestionsFromExternalApi(MOCK_API_URL,language);

            if (questions.isEmpty()) {
                return "THÀNH CÔNG: API trả về, nhưng danh sách câu hỏi rỗng. Kiểm tra lại dữ liệu trên MockAPI.";
            }

            System.out.println("--- DỮ LIỆU CÂU HỎI ĐÃ ĐỌC THÀNH CÔNG (Chi tiết) ---");

            int count = 1;
            for (QuestionDto q : questions) {
                System.out.println("--- Câu hỏi " + (count++) + " ---");
                System.out.println(q); // Dựa vào toString() của QuestionDto
            }
            return "THÀNH CÔNG! Đã đọc thành công " + questions.size() + " câu hỏi từ MockAPI. Vui lòng kiểm tra console để xem chi tiết.";

        } catch (Exception e) {
            return "LỖI KHI ĐỌC DỮ LIỆU TỪ API MOCK: " + e.getMessage();
        }
    }

    private List<QuestionDto> fetchQuestionsFromExternalApi(String externalApiUrl, String language) throws Exception {

        String requestBody = objectMapper.writeValueAsString(Map.of("language", language));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<QuizApiResponse> response = restTemplate.exchange(
                externalApiUrl,
                HttpMethod.POST,
                entity,
                QuizApiResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            QuizApiResponse apiResponse = response.getBody();
            if ("success".equals(apiResponse.getStatus())) {
                return apiResponse.getQuestions();
            } else {
                throw new RuntimeException("API trả về LỖI: " + apiResponse.getMessage());
            }
        } else {
            throw new RuntimeException("Lỗi khi lấy dữ liệu từ API ngoài: " + response.getStatusCode());
        }
    }
}

