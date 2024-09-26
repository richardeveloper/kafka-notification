package br.com.kafka.principal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "LOG_NOTIFICATION")
public class LogNotificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "NOTIFICATION_CODE")
  private String notificationCode;

  @Lob
  @Column(name = "NOTIFICATION")
  private String notification;

  @Column(name = "BATCH_CODE")
  private Integer batchCode;

  @Column(name = "SCHEDULE_DATE")
  private LocalDateTime scheduleDate;

  @Column(name = "SEND_DATE")
  private LocalDateTime sendDate;

}
