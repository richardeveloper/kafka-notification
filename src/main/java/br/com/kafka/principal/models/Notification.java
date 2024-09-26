package br.com.kafka.principal.models;

import br.com.kafka.principal.enums.PriorityEnum;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {

  private String code;

  private String message;

  private PriorityEnum priority;

  private String eventType;

  private LocalDateTime date;

  public Notification(String code, String message, PriorityEnum priority, String eventType) {
    this.code = code;
    this.message = message;
    this.priority = priority;
    this.eventType = eventType;
    this.date = LocalDateTime.now();
  }
}
