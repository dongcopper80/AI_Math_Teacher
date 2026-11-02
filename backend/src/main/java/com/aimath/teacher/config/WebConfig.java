package com.aimath.teacher.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class WebConfig implements WebMvcConfigurer {
 @Override public void addCorsMappings(CorsRegistry r) { r.addMapping("/api/**").allowedOriginPatterns("http://localhost:3000").allowedMethods("GET","POST","OPTIONS").allowedHeaders("*"); }
}