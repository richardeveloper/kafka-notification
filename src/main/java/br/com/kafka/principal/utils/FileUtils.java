package br.com.kafka.principal.utils;

import br.com.kafka.principal.models.Notification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;

public class FileUtils {

  private static final String HTML_PATH = "src/main/resources/files/notification.html";
  private static final String ICON_PATH = "src/main/resources/files/notification.png";

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");

  public static String getHtml(Notification notification) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(HTML_PATH));

      StringBuilder builder = new StringBuilder();

      bufferedReader.lines().forEach(builder::append);

      return builder.toString()
        .replace("CODIGO", notification.getCode())
        .replace("EVENTO", notification.getEventType())
        .replace("MENSAGEM", notification.getMessage())
        .replace("PRIORIDADE", notification.getPriority().getDescription())
        .replace("DATA_AGENDAMENTO", notification.getScheduleDate().format(DATE_TIME_FORMATTER))
        .replace("DATA_ENVIO", notification.getSendDate().format(DATE_TIME_FORMATTER));
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static ImageIcon getIcon() {
    return new ImageIcon(ICON_PATH);
  }

}