package com.aimath.teacher.lesson;
import com.aimath.teacher.storage.ObjectStorageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
@RestController @Validated @RequestMapping("/api/v1")
public class LessonController {
 private final LessonService lessons; private final ObjectStorageService storage;
 public LessonController(LessonService s,ObjectStorageService o){lessons=s;storage=o;}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok");}
 @PostMapping(value="/lessons",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
 public LessonService.LessonResponse create(@RequestParam String educationLevel,@RequestParam @Min(1) @Max(12) int grade,@RequestParam(required=false)String problemText,@RequestParam(required=false,defaultValue="false") boolean useOnlineAi,@RequestParam(required=false,defaultValue="vi") String lessonLanguage,@RequestPart(required=false)MultipartFile image)throws Exception{
  if(!"vi".equals(lessonLanguage)&&!"en".equals(lessonLanguage))throw new IllegalArgumentException("lessonLanguage must be vi or en");
  boolean valid=switch(educationLevel){case "primary"->grade>=1&&grade<=5;case "middle"->grade>=6&&grade<=9;case "high"->grade>=10&&grade<=12;default->false;};
  if(!valid)throw new IllegalArgumentException("Grade does not belong to the selected education level");
  if((problemText==null||problemText.isBlank())&&(image==null||image.isEmpty()))throw new IllegalArgumentException("Provide problemText or image");
  if(image!=null&&!image.isEmpty()&&!useOnlineAi)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Để đọc đề từ ảnh, hãy bật chế độ AI online trước khi gửi.");
  byte[] imageBytes=image==null||image.isEmpty()?null:image.getBytes();String imageType=image==null?null:image.getContentType();
  if(imageBytes!=null&&!("image/jpeg".equals(imageType)||"image/png".equals(imageType)||"image/webp".equals(imageType)))throw new IllegalArgumentException("Chỉ hỗ trợ ảnh JPG, PNG hoặc WEBP");
  String key=image==null||image.isEmpty()?null:storage.store(image);return lessons.create(educationLevel,grade,problemText==null?"":problemText,key,useOnlineAi,imageBytes,imageType,lessonLanguage);
 }
}
