package br.com.kafka.principal;

import br.com.kafka.principal.models.Notification;
import br.com.kafka.principal.producers.KafkaProducer;
import br.com.kafka.principal.repositories.LogNotificationRepository;
import br.com.kafka.principal.views.KafkaNotificationView;

import java.util.List;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaNotificationApplication implements CommandLineRunner {

  private static JFrame frame;

  private static KafkaNotificationView view;

  public static void main(String[] args) {
    frame = new JFrame("Kafka Notification");
    SpringApplication.run(KafkaNotificationApplication.class, args);
  }

  @Autowired
  private KafkaProducer kafkaProducer;

  @Autowired
  private LogNotificationRepository logNotificationRepository;

  @Override
  public void run(String... args) throws Exception {
    view = new KafkaNotificationView(frame, kafkaProducer, logNotificationRepository);

    frame.setVisible(true);
    frame.setLocationRelativeTo(frame);
    frame.setSize(950, 550);
    frame.setContentPane(view.getMainPanel());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static void showResult(List<Notification> notifications) {
    view.showResult(frame, notifications);
  }

}