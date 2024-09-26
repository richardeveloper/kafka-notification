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

  public static PriorityEnum parse(String description) {
    if (description == null || description.isEmpty()) {
      return null;
    }

    for (PriorityEnum priorityEnum : PriorityEnum.values()) {
      if (priorityEnum.description.equals(description)) {
        return priorityEnum;
      }
    }

    throw new AssertionError("Não foi encontrada prioridade para a descrição informada.");
  }
}