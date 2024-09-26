package br.com.kafka.principal.consumers;

import br.com.kafka.principal.KafkaNotificationApplication;
import br.com.kafka.principal.entities.LogNotificationEntity;
import br.com.kafka.principal.models.Notification;

import br.com.kafka.principal.repositories.LogNotificationRepository;
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

      LogNotificationEntity log = logNotificationRepository.findByNotificationCode(notification.getCode());
      log.setSendDate(notification.getSendDate());

      KafkaConsumer.log.info("Notificação recebida com sucesso.");
      logInfoNotification(notification);

      logNotificationRepository.save(log);

      KafkaNotificationApplication.showResult(List.of(notification));
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @KafkaListener(topics = BATCH_TOPIC, groupId = BATCH_GROUP, containerFactory = "batchKafkaListenerContainerFactory")
  public void receiveMessages(List<String> messages) {
    log.info("Lote de notificações recebido com sucesso. Tamamho: {}", messages.size());

    try {
      List<Notification> notifications = new ArrayList<>();

      for (String message : messages) {
        Notification notification = objectMapper.readValue(message, Notification.class);
        notification.setSendDate(LocalDateTime.now());

        LogNotificationEntity log = logNotificationRepository.findByNotificationCode(notification.getCode());
        log.setSendDate(notification.getSendDate());

        logInfoNotification(notification);

        notifications.add(notification);

        logNotificationRepository.save(log);
      }

      KafkaNotificationApplication.showResult(notifications);
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private static void logInfoNotification(Notification notification) {
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