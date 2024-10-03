package br.com.kafka.principal.producers;

import br.com.kafka.principal.entities.LogNotificationEntity;
import br.com.kafka.principal.models.Notification;
import br.com.kafka.principal.models.NotificationBatch;
import br.com.kafka.principal.repositories.LogNotificationRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

  private static final int BATCH_SIZE = 5;

  private static final String SINGLE_TOPIC = "single-notification";
  private static final String BATCH_TOPIC = "batch-notification";

  private final NotificationBatch notificationBatch;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LogNotificationRepository logNotificationRepository;

  public KafkaProducer() {
    this.notificationBatch = new NotificationBatch();
  }

  public void sendMessage(Notification notification) {
    log.info("Iniciando processo de envio da notificação {}.", notification.getCode());

    try {
      notification.setScheduleDate(LocalDateTime.now());

      String json = objectMapper.writeValueAsString(notification);

      LogNotificationEntity logNotificationEntity = createLogNotification(notification, json);

      switch (notification.getPriority()) {
        case LOW:
          validateNotification(json, logNotificationEntity);

          if (notificationBatch.size() == BATCH_SIZE) {
            notificationBatch.forEach(message -> kafkaTemplate.send(BATCH_TOPIC, json, message));
            log.info("Lote de notificações enviado com sucesso.");
            notificationBatch.clear();
            break;
          }

          log.info("Notificação {} agendada para envio futuro.", notification.getCode());
          break;
        case MID:
        case HIGH:
          kafkaTemplate.send(SINGLE_TOPIC, notification.getCode(), json);
          log.info("Notificação {} agendada com sucesso.", notification.getCode());
          break;
      }

      logNotificationRepository.save(logNotificationEntity);
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private LogNotificationEntity createLogNotification(Notification notification, String json) {
    LogNotificationEntity logNotificationEntity = new LogNotificationEntity();
    logNotificationEntity.setNotification(json);
    logNotificationEntity.setNotificationCode(notification.getCode());
    logNotificationEntity.setScheduleDate(notification.getScheduleDate());

    return logNotificationEntity;
  }

  private void validateNotification(String json, LogNotificationEntity log) {
    if (!notificationBatch.contains(json)) {
      notificationBatch.add(json);
      log.setBatchCode(NotificationBatch.getCode());
    }
  }

}