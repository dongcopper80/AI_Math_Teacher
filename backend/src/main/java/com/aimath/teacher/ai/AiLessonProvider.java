package com.aimath.teacher.ai;
import com.aimath.teacher.lesson.LessonService.LessonResponse;
public interface AiLessonProvider { LessonResponse createLesson(String educationLevel,int grade,String problemText,boolean useOnlineAi,byte[] imageBytes,String imageContentType,String lessonLanguage); }
