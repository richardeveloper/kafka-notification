package br.com.kafka.principal.producers;

import br.com.kafka.principal.models.Notification;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

  private static final int BATCH_SIZE = 3;

  private static final String SINGLE_TOPIC = "single-notification";
  private static final String BATCH_TOPIC = "batch-notification";

  private final List<String> notificationBatch = new ArrayList<>();

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public void sendMessage(Notification notification) {
    log.info("Iniciando processo de envio da notificação {}.", notification.getCode());

    try {
      notification.setScheduleDate(LocalDateTime.now());
      String json = objectMapper.writeValueAsString(notification);

      switch (notification.getPriority()) {
        case LOW:
          if (!notificationBatch.contains(json)) {
            notificationBatch.add(json);
          }

          if (notificationBatch.size() == BATCH_SIZE) {
            notificationBatch.forEach(message -> kafkaTemplate.send(BATCH_TOPIC, json, message));

            log.info("Lote de notificações enviado com sucesso.");
            notificationBatch.clear();
            break;
          }

          log.info("Notificação {} agendada para envio.", notification.getCode());
          break;
        case MID:
        case HIGH:
          kafkaTemplate.send(SINGLE_TOPIC, notification.getCode(), json);
          log.info("Notificação {} agendada com sucesso.", notification.getCode());
          break;
      }

    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

}