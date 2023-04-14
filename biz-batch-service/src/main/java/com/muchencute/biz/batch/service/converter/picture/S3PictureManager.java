package com.muchencute.biz.batch.service.converter.picture;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.MD5;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class S3PictureManager implements PictureManager {

  private final MinioClient minioClient;

  private final String bucket;

  @Autowired
  public S3PictureManager(MinioClient minioClient,
                          @Value("${minio.bucket}") String bucket) {

    this.minioClient = minioClient;
    this.bucket = bucket;
  }

  @Override
  @SneakyThrows
  public String picture(byte[] bytes, String mime, String extName) {

    @Cleanup final var inputStream = new ByteArrayInputStream(bytes);
    final var object = String.format("images_and_videos/%s/%s.%s", DateUtil.today(),
      MD5.create().digestHex(bytes), extName);
    minioClient.putObject(PutObjectArgs.builder()
      .contentType(mime)
      .stream(inputStream, bytes.length, -1)
      .bucket(bucket)
      .object(object)
      .build());
    return String.format("/%s/%s", bucket, object);
  }
}
