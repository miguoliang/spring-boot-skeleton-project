package com.muchencute.biz.service.config;

import io.minio.GetBucketNotificationArgs;
import io.minio.MinioClient;
import io.minio.SetBucketNotificationArgs;
import io.minio.messages.EventType;
import io.minio.messages.NotificationConfiguration;
import io.minio.messages.QueueConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;

@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
@Configuration
@Slf4j
public class MinioConfig {

  private final static String SQS = "arn:minio:sqs::TP:kafka";

  private String endpoint;

  private String username;

  private String password;

  private String bucket;

  private String bucketNotificationTopic;

  @Bean
  public MinioClient minioClient() {

    log.info("初始化 MinioClient ...");

    final var client = MinioClient.builder()
            .endpoint(this.endpoint)
            .credentials(username, password)
            .build();

    if (!existsBucketNotification(client)) {
      log.info("不存在 SQS 设置，正在自动设置 ...");
      setBucketNotification(client);
      log.info("Minio SQS 自动设置完成！");
    }

    log.info("MinioClient 初始化完成！");
    return client;
  }

  @SneakyThrows
  private boolean existsBucketNotification(MinioClient minioClient) {

    final var config = minioClient.getBucketNotification(
            GetBucketNotificationArgs.builder().bucket(bucket).build());

    return config.queueConfigurationList()
            .stream()
            .anyMatch(it -> SQS.equals(it.queue()));
  }

  @SneakyThrows
  private void setBucketNotification(MinioClient minioClient) {

    final var eventList = new LinkedList<EventType>();
    eventList.add(EventType.OBJECT_CREATED_PUT);

    final var queueConfiguration = new QueueConfiguration();
    // 此处 TP 一定要大写，ARN 要对应 Minio 启动后日志中 SQS 所在行的 ARN
    // TP 是通过 docker-compose.yml 中，MINIO 启动参数指定的标识符，
    // 例如本项目，在 docker-compose.yml 中，你能看到如下字样：
    // MINIO_NOTIFY_KAFKA_ENABLE_TP=on，其中环境变量末尾的 TP 就是配置分组的标识
    queueConfiguration.setQueue(SQS);
    queueConfiguration.setEvents(eventList);

    final var queueConfigurationList = new LinkedList<QueueConfiguration>();
    queueConfigurationList.add(queueConfiguration);

    final var config = new NotificationConfiguration();
    config.setQueueConfigurationList(queueConfigurationList);

    minioClient.setBucketNotification(
            SetBucketNotificationArgs.builder().bucket(bucket).config(config).build());
  }
}
