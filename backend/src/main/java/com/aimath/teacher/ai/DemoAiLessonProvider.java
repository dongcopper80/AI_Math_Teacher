package com.aimath.teacher.ai;
import com.aimath.teacher.lesson.LessonService.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.util.List;
@Component @ConditionalOnProperty(name="app.ai.provider",havingValue="demo",matchIfMissing=true)
public class DemoAiLessonProvider implements AiLessonProvider {
 public LessonResponse createLesson(String level,int grade,String problem,boolean useOnlineAi,byte[] imageBytes,String imageContentType,String lessonLanguage) {
  if(useOnlineAi||imageBytes!=null)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,"en".equals(lessonLanguage)?"Online AI is not configured. Set AI_PROVIDER=openai and add an API key in .env.":"Để AI đọc ảnh và giải bài, hãy cấu hình AI_PROVIDER=openai cùng API key có hỗ trợ đọc ảnh trong file .env.");
  if("en".equals(lessonLanguage))return englishLesson(level);
  if("middle".equals(level))return new LessonResponse("Giải phương trình bậc nhất","Đại số · Phương trình một ẩn",List.of(
   new Step("Đọc phương trình","Mình cần tìm giá trị của x để hai vế bằng nhau. Hãy giải phương trình hai x cộng năm bằng mười bảy nhé.","2x + 5 = 17"),
   new Step("Chuyển số hạng tự do","Trừ năm ở cả hai vế để giữ cho phương trình cân bằng. Ta được hai x bằng mười hai.","2x + 5 − 5 = 17 − 5\n2x = 12"),
   new Step("Tìm giá trị của x","Chia cả hai vế cho hai, vậy x bằng sáu.","2x ÷ 2 = 12 ÷ 2\nx = 6"),
   new Step("Thử lại kết quả","Thay sáu vào phương trình ban đầu: hai nhân sáu cộng năm bằng mười bảy. Kết quả đúng rồi!","2 × 6 + 5 = 17\n17 = 17 ✓")),"x = 6","Nếu 3x + 4 = 19, em tìm x bằng cách nào?");
  if("high".equals(level))return new LessonResponse("Tính đạo hàm của hàm số","Giải tích · Quy tắc đạo hàm cơ bản",List.of(
   new Step("Nhận diện các hạng tử","Ta cần tính đạo hàm của x bình phương cộng ba x. Hãy áp dụng quy tắc đạo hàm cho từng hạng tử.","f(x) = x² + 3x"),
   new Step("Đạo hàm từng hạng tử","Đạo hàm của x bình phương là hai x. Đạo hàm của ba x là ba.","(x²)′ = 2x\n(3x)′ = 3"),
   new Step("Cộng các kết quả","Cộng hai đạo hàm vừa tìm được, ta có f phẩy của x bằng hai x cộng ba.","f′(x) = 2x + 3"),
   new Step("Kiểm tra quy tắc","Mỗi hạng tử đã được đạo hàm riêng theo quy tắc lũy thừa. Vậy kết quả là hai x cộng ba.","(x² + 3x)′\n= 2x + 3")),"f′(x) = 2x + 3","Hãy thử tính đạo hàm của g(x) = x³ + 2x.");
  return new LessonResponse("Tìm số táo của Mai","Bài toán có lời văn · Lớp "+grade,List.of(
   new Step("Cùng đọc đề bài","Lan có 25 quả táo. Mai có ít hơn Lan 8 quả. Mình cùng tìm xem Mai có bao nhiêu quả táo nhé!","Lan có: 25 quả táo\nMai ít hơn: 8 quả táo\nMai có: ? quả táo"),
   new Step("Mình chọn phép tính","Mai có ít hơn Lan nên mình lấy số táo của Lan trừ đi phần ít hơn nhé.","Ít hơn → phép trừ\n25 − 8 = ?"),
   new Step("Tính thật cẩn thận","Hai mươi lăm trừ tám bằng mười bảy. Mình tách tám thành năm và ba nhé.","25 − 8\n= 25 − 5 − 3\n= 20 − 3\n= 17"),
   new Step("Trình bày bài giải","Vậy Mai có 17 quả táo. Nhớ ghi đơn vị và đáp số ở cuối nhé!","Bài giải\nSố quả táo của Mai là:\n25 − 8 = 17 (quả)\nĐáp số: 17 quả táo")),"17 quả táo","Nếu Mai có nhiều hơn Lan 8 quả táo, em sẽ làm phép tính nào?");
 }
 private LessonResponse englishLesson(String level){
  if("middle".equals(level))return new LessonResponse("Solve a linear equation","Algebra · One-variable equations",List.of(
   new Step("Read the equation","We need to find the value of x that makes both sides equal. Let’s solve two x plus five equals seventeen.","2x + 5 = 17"),
   new Step("Keep the equation balanced","Subtract five from both sides. This gives us two x equals twelve.","2x + 5 − 5 = 17 − 5\n2x = 12"),
   new Step("Find x","Divide both sides by two. So x equals six.","2x ÷ 2 = 12 ÷ 2\nx = 6"),
   new Step("Check the answer","Substitute six into the original equation: two times six plus five equals seventeen. The answer is correct.","2 × 6 + 5 = 17\n17 = 17 ✓")),"x = 6","How would you solve 3x + 4 = 19?");
  if("high".equals(level))return new LessonResponse("Find the derivative","Calculus · Basic differentiation rules",List.of(
   new Step("Identify each term","We will find the derivative of x squared plus three x. Differentiate each term separately.","f(x) = x² + 3x"),
   new Step("Differentiate each term","The derivative of x squared is two x. The derivative of three x is three.","(x²)′ = 2x\n(3x)′ = 3"),
   new Step("Combine the results","Add the two derivatives. We get f prime of x equals two x plus three.","f′(x) = 2x + 3"),
   new Step("Check the rule","We differentiated each term using the power rule. The answer is two x plus three.","(x² + 3x)′\n= 2x + 3")),"f′(x) = 2x + 3","Try finding the derivative of g(x) = x³ + 2x.");
  return new LessonResponse("How many apples does Mai have?","Word problem · Subtraction within 100",List.of(
   new Step("Read the problem","Lan has 25 apples. Mai has 8 fewer apples than Lan. Let’s find out how many apples Mai has.","Lan has: 25 apples\nMai has 8 fewer\nMai has: ? apples"),
   new Step("Choose an operation","Mai has fewer apples, so we subtract 8 from 25.","Fewer → subtraction\n25 − 8 = ?"),
   new Step("Calculate carefully","Twenty-five minus eight is seventeen. Subtract five to get twenty, then three more to get seventeen.","25 − 8\n= 25 − 5 − 3\n= 20 − 3\n= 17"),
   new Step("Write the answer","Mai has 17 apples. Remember to include the unit in your answer.","Solution\nMai has: 25 − 8 = 17 apples\nAnswer: 17 apples")),"17 apples","If Mai had 8 more apples than Lan, which operation would you use?");
 }
}
