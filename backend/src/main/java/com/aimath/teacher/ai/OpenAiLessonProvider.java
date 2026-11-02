package com.aimath.teacher.ai;

import com.aimath.teacher.lesson.LessonService.LessonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Base64;

@Component
@ConditionalOnProperty(name="app.ai.provider", havingValue="openai")
public class OpenAiLessonProvider implements AiLessonProvider {
    private final RestClient client;
    private final ObjectMapper mapper;
    private final String model;

    public OpenAiLessonProvider(@Value("${app.ai.base-url:https://api.openai.com/v1}") String baseUrl,
                                @Value("${app.ai.api-key:}") String apiKey,
                                @Value("${app.ai.model:gpt-4o-mini}") String model,
                                ObjectMapper mapper) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("APP_AI_API_KEY is required when APP_AI_PROVIDER=openai");
        }
        this.client = RestClient.builder().baseUrl(baseUrl.replaceAll("/$", ""))
                .defaultHeader("Authorization", "Bearer " + apiKey).build();
        this.mapper = mapper;
        this.model = model == null || model.isBlank() ? "gpt-4o-mini" : model;
    }

    @Override
    public LessonResponse createLesson(String level, int grade, String problem, boolean useOnlineAi, byte[] imageBytes, String imageContentType, String lessonLanguage) {
        boolean english = "en".equals(lessonLanguage);
        if (!useOnlineAi) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    english ? "Select online AI or switch AI_PROVIDER to demo." : "Hãy chọn chế độ AI online hoặc chuyển AI_PROVIDER sang demo.");
        }
        String schoolBand = switch (level) {
            case "primary" -> english ? "primary school" : "tiểu học";
            case "middle" -> english ? "middle school" : "trung học cơ sở";
            case "high" -> english ? "high school" : "trung học phổ thông";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, english ? "Invalid school level." : "Cấp học không hợp lệ");
        };
        String prompt = english
                ? "Create a math lesson in English for grade " + grade + " (" + schoolBand + "). Use clear, age-appropriate English and familiar school math vocabulary.\n" +
                  (problem == null || problem.isBlank() ? "The problem is in the attached image." : "Problem entered by the student: " + problem) + "\n" +
                  "Teach like a classroom teacher: read and analyze the information, name the relevant method, explain one action at a time and why it works, show clear equations on the board, check the result, and include units when needed. Do not invent missing information. If the image or problem is unclear, say what needs clarification and do not guess the answer. " +
                  (imageBytes == null ? "Set recognizedProblem to null. " : "Read the image carefully. In recognizedProblem, transcribe the exercise exactly in its original language, preserving numbers, units, signs, and expressions. Mark uncertain characters rather than guessing. ") +
                  "Return only JSON matching this schema: {\"title\":string,\"concept\":string,\"steps\":[{\"title\":string,\"narration\":string,\"board\":string}],\"answer\":string,\"practice\":string,\"recognizedProblem\":string|null}. Create 3 to 6 steps. Narration is spoken English; board is concise board content. Do not include markdown."
                : "Soạn lời giải toán bằng tiếng Việt cho học sinh lớp " + grade + " (" + schoolBand + ").\n" +
                  (problem == null || problem.isBlank() ? "Đề bài được cung cấp trong ảnh đính kèm." : "Đề bài người dùng nhập: " + problem) + "\n" +
                  "Hướng dẫn như giáo viên trên lớp: đọc và phân tích dữ kiện, nêu kiến thức/phương pháp phù hợp chương trình phổ thông Việt Nam, mỗi bước chỉ làm một việc và giải thích vì sao, trình bày phép tính rõ ràng trên bảng, kiểm tra kết quả, kết luận có đơn vị nếu cần. Dùng lời nói thân thiện, dễ hiểu đúng lứa tuổi; không bịa dữ kiện. Nếu đề thiếu/không đọc được, hãy nêu rõ trong lời giảng và hỏi lại, không tự đoán đáp số. " +
                  (imageBytes == null ? "recognizedProblem là null. " : "Đọc kỹ ảnh trước; chép nguyên văn phần đề bài nhìn thấy vào recognizedProblem, giữ số liệu, đơn vị, dấu và biểu thức chính xác. Nếu ảnh mờ hoặc có phần không chắc, ghi rõ ký tự chưa đọc được thay vì đoán. ") +
                  "Trả về duy nhất JSON theo schema: {\"title\":string,\"concept\":string,\"steps\":[{\"title\":string,\"narration\":string,\"board\":string}],\"answer\":string,\"practice\":string,\"recognizedProblem\":string|null}. Tạo từ 3 đến 6 bước; narration là câu để đọc thành tiếng; board là nội dung ngắn để viết bảng; không dùng markdown ngoài JSON.";
        try {
            Object userContent = prompt;
            if (imageBytes != null) {
                String mime = imageContentType == null ? "image/jpeg" : imageContentType;
                String dataUrl = "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
                userContent = List.of(Map.of("type", "text", "text", prompt),
                        Map.of("type", "image_url", "image_url", Map.of("url", dataUrl, "detail", "high")));
            }
            Map<?, ?> response = client.post().uri("/chat/completions")
                    .body(Map.of("model", model, "temperature", 0.2,
                            "response_format", Map.of("type", "json_object"),
                            "messages", List.of(Map.of("role", "system", "content", english ? "You are a careful classroom math teacher. Teach accurately at the student's grade level, in English." : "Bạn là giáo viên Toán Việt Nam, giải thích chính xác và đúng trình độ."),
                                    Map.of("role", "user", "content", userContent))))
                    .retrieve().body(Map.class);
            if (response == null) throw new IllegalStateException("AI không trả về nội dung");
            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> choice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) choice.get("message");
            String content = String.valueOf(message.get("content"));
            LessonResponse lesson = mapper.readValue(content, LessonResponse.class);
            if (lesson.steps() == null || lesson.steps().isEmpty()) throw new IllegalStateException("Thiếu các bước giải");
            return lesson;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    english ? "Online AI could not prepare the lesson. Check the connection, model, and API key, then try again." : "AI online chưa tạo được lời giải. Kiểm tra kết nối, model và API key rồi thử lại.", e);
        }
    }
}
