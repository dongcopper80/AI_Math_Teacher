package com.aimath.teacher.lesson;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="lessons")
public class Lesson {
 @Id private UUID id; private String educationLevel; private int grade;
 @Column(columnDefinition="text") private String problemText;
 private String imageObjectKey;
 @Column(columnDefinition="text") private String lessonJson;
 private Instant createdAt;
 protected Lesson() {}
 public Lesson(String educationLevel,int grade,String problem,String imageKey,String json) { id=UUID.randomUUID();this.educationLevel=educationLevel;this.grade=grade;problemText=problem;imageObjectKey=imageKey;lessonJson=json;createdAt=Instant.now(); }
 public UUID getId(){return id;} public String getEducationLevel(){return educationLevel;} public int getGrade(){return grade;} public String getProblemText(){return problemText;} public String getImageObjectKey(){return imageObjectKey;} public String getLessonJson(){return lessonJson;} public Instant getCreatedAt(){return createdAt;}
}