package br.com.kafka.principal.enums;

import lombok.Getter;

@Getter
public enum PriorityEnum {

  HIGH("Alta"),
  MID("Média"),
  LOW("Baixa");

  private final String description;

  PriorityEnum(String description) {
    this.description = description;
  }

}