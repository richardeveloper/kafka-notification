package br.com.kafka.principal.consumers;

import br.com.kafka.principal.KafkaNotificationApplication;
import br.com.kafka.principal.models.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {

  private static final String SINGLE_TOPIC = "single-notification";
  private static final String BATCH_TOPIC = "batch-notification";

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  @Autowired
  private ObjectMapper objectMapper;

  @KafkaListener(topics = SINGLE_TOPIC, groupId = "single-group")
  public void receiveMessage(String message) {
    try {
      Notification notification = objectMapper.readValue(message, Notification.class);

      log.info("Notificação recebida com sucesso.");
      logNotification(notification);

      KafkaNotificationApplication.showResult(notification);
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @KafkaListener(topics = BATCH_TOPIC, groupId = "batch-group", containerFactory = "batchKafkaListenerContainerFactory")
  public void receiveMessages(List<String> messages) {
    log.info("Lote de notificações recebido com sucesso. Tamamho: {}", messages.size());

    try {
      for (String message : messages) {
        Notification notification = objectMapper.readValue(message, Notification.class);
        logNotification(notification);
      }
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private static void logNotification(Notification notification) {
    log.info("");
    log.info("CÓDIGO: {}", notification.getCode());
    log.info("EVENTO: {}", notification.getEventType());
    log.info("MENSAGEM: {}", notification.getMessage());
    log.info("PRIORIDADE: {}", notification.getPriority().getDescription());
    log.info("DATA DE ENVIO: {}", notification.getDate().format(DATE_TIME_FORMATTER));
    log.info("");
  }

}
