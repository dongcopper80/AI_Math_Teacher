package com.aimath.teacher.lesson;
import com.aimath.teacher.ai.AiLessonProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class LessonService {
 private final AiLessonProvider provider; private final LessonRepository repository;
 public LessonService(AiLessonProvider p,LessonRepository r){provider=p;repository=r;}
 @Transactional public LessonResponse create(String educationLevel,int grade,String problem,String imageKey,boolean useOnlineAi,byte[] imageBytes,String imageContentType,String lessonLanguage){var result=provider.createLesson(educationLevel,grade,problem,useOnlineAi,imageBytes,imageContentType,lessonLanguage);try{repository.save(new Lesson(educationLevel,grade,problem,imageKey,new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result)));}catch(Exception e){throw new IllegalStateException(e);}return result;}
 public record Step(String title,String narration,String board){}
 public record LessonResponse(String title,String concept,List<Step> steps,String answer,String practice,String recognizedProblem){
  public LessonResponse(String title,String concept,List<Step> steps,String answer,String practice){this(title,concept,steps,answer,practice,null);}
 }
}
