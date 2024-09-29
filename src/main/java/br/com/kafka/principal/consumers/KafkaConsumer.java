package br.com.kafka.principal.consumers;

import br.com.kafka.principal.KafkaNotificationApplication;
import br.com.kafka.principal.entities.LogNotificationEntity;
import br.com.kafka.principal.models.Notification;

import br.com.kafka.principal.repositories.LogNotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {

  private static final String SINGLE_TOPIC = "single-notification";
  private static final String SINGLE_GROUP = "single-group";

  private static final String BATCH_TOPIC = "batch-notification";
  private static final String BATCH_GROUP = "batch-group";
  private static final String CONTAINER_FACTORY = "batchKafkaListenerContainerFactory";

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LogNotificationRepository logNotificationRepository;

  @KafkaListener(topics = SINGLE_TOPIC, groupId = SINGLE_GROUP)
  public void receiveMessage(String message) {
    try {
      Notification notification = objectMapper.readValue(message, Notification.class);
      notification.setSendDate(LocalDateTime.now());

      log.info("Notificação recebida com sucesso.");
      logInfoNotification(notification);

      registerLogNotification(notification);

      KafkaNotificationApplication.showResult(List.of(notification));
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @KafkaListener(topics = BATCH_TOPIC, groupId = BATCH_GROUP, containerFactory = CONTAINER_FACTORY)
  public void receiveMessages(List<String> messages) {
    log.info("Lote de notificações recebido com sucesso. Tamamho: {}", messages.size());

    try {
      List<Notification> notificationList = new ArrayList<>();

      for (String message : messages) {
        Notification notification = objectMapper.readValue(message, Notification.class);
        notification.setSendDate(LocalDateTime.now());

        logInfoNotification(notification);

        notificationList.add(notification);

        registerLogNotification(notification);
      }

      KafkaNotificationApplication.showResult(notificationList);
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void registerLogNotification(Notification notification) throws JsonProcessingException {
    LogNotificationEntity logNotificationEntity = logNotificationRepository.findByNotificationCode(notification.getCode());
    logNotificationEntity.setNotification(objectMapper.writeValueAsString(notification));
    logNotificationEntity.setSendDate(notification.getSendDate());

    logNotificationRepository.save(logNotificationEntity);
  }

  private void logInfoNotification(Notification notification) {
    log.info("==============================================================================================================");
    log.info("DETALHES DA NOTIFICAÇÃO");
    log.info("==============================================================================================================");
    log.info("CÓDIGO: {}", notification.getCode());
    log.info("TIPO DE EVENTO: {}", notification.getEventType());
    log.info("MENSAGEM: {}", notification.getMessage());
    log.info("PRIORIDADE: {}", notification.getPriority().getDescription());
    log.info("DATA DE AGENDAMENTO: {}", notification.getScheduleDate().format(DATE_TIME_FORMATTER));
    log.info("DATA DE ENVIO: {}", notification.getSendDate().format(DATE_TIME_FORMATTER));
    log.info("==============================================================================================================");
  }

}