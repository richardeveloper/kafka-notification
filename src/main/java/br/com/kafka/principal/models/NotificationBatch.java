package br.com.kafka.principal.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import lombok.Getter;

public class NotificationBatch {

  @Getter
  private static Integer code = 1;

  private final List<String> notifications;

  public NotificationBatch() {
    this.notifications = new ArrayList<>();
  }

  public void add(String notification) {
    this.notifications.add(notification);
  }

  public void forEach(Consumer<? super String> action) {
    Objects.requireNonNull(action);
    for (String t : notifications) {
      action.accept(t);
    }
  }

  public boolean contains(String notification) {
    return notifications.contains(notification);
  }

  public int size() {
    return notifications.size();
  }

  public void clear() {
    this.notifications.clear();
    code++;
  }

}
