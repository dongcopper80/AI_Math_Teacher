package com.aimath.teacher.storage;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
@Service
public class ObjectStorageService {
 private final MinioClient client; private final String bucket;
 public ObjectStorageService(@Value("${app.storage.endpoint}")String endpoint,@Value("${app.storage.access-key}")String access,@Value("${app.storage.secret-key}")String secret,@Value("${app.storage.bucket}")String bucket){client=MinioClient.builder().endpoint(endpoint).credentials(access,secret).build();this.bucket=bucket;}
 public String store(MultipartFile file)throws Exception{String key=UUID.randomUUID()+"-"+file.getOriginalFilename().replaceAll("[^A-Za-z0-9._-]","_");if(!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build()))client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());client.putObject(PutObjectArgs.builder().bucket(bucket).object(key).stream(file.getInputStream(),file.getSize(),-1).contentType(file.getContentType()).build());return key;}
}