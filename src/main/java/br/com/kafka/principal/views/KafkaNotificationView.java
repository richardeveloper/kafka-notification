package br.com.kafka.principal.views;

import br.com.kafka.principal.enums.PriorityEnum;
import br.com.kafka.principal.models.Notification;
import br.com.kafka.principal.producers.KafkaProducer;
import br.com.kafka.principal.utils.FileUtils;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import lombok.Getter;

@Getter
public class KafkaNotificationView {

  private JPanel mainPanel;

  private JLabel title;

  private JPanel contentPanel;

  private JPanel eventPanel;
  private JRadioButton activityReminderRadioButton;
  private JRadioButton scheduleChangeRadioButton;
  private JRadioButton importantEventsRadioButton;

  private JPanel contentTextPanel;
  private JTextField contentTextField;

  private JButton sendButton;
  private JPanel buttonPanel;

  private JDialog dialog;

  private final KafkaProducer kafkaProducer;

  public KafkaNotificationView(JFrame frame, KafkaProducer kafkaProducer) {
    this.kafkaProducer = kafkaProducer;

    setUpTextField();

    sendButton(frame);
    activityReminderRadioButton();
    scheduleChangeRadioButton();
    importantEventsRadioButton();
  }

  public void sendButton(JFrame frame) {
    this.sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (isInvalidButtons()) {
          JOptionPane.showMessageDialog(
            frame,
            "Favor selecionar o tipo do evento a ser notificado.",
            "AVISO",
            JOptionPane.WARNING_MESSAGE
          );
          return;
        }

        if (contentTextField.getText().isEmpty()) {
          JOptionPane.showMessageDialog(
            frame,
            "Favor inserir o contéudo da notificação.",
            "AVISO",
            JOptionPane.WARNING_MESSAGE
          );
          return;
        }

        Notification notification = createNotification();

        kafkaProducer.sendMessage(notification);

        showScheduledMessage(frame);
      }
    });
  }

  public void activityReminderRadioButton() {
    this.activityReminderRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        contentTextField.setDocument(new PlainDocument());
        disableAllButtons();
        activityReminderRadioButton.setSelected(true);
        contentTextField.setText("Atenção, a data limite para entrega da atividade é dia 04/09/2024.");
      }
    });
  }

  public void scheduleChangeRadioButton() {
    this.scheduleChangeRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        contentTextField.setDocument(new PlainDocument());
        disableAllButtons();
        scheduleChangeRadioButton.setSelected(true);
        contentTextField.setText("Em virtude do feriado nacional do dia da Consciência Negra, não haverá aula no dia 20/11/2024.");
      }
    });
  }

  public void importantEventsRadioButton() {
    this.importantEventsRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        contentTextField.setDocument(new PlainDocument());
        disableAllButtons();
        importantEventsRadioButton.setSelected(true);
        contentTextField.setText("Dia 12/12/2024 acontece a XXV conferência de apresentação das atualizações e novidades do Java Swing.");
      }
    });
  }

  public void showResult(JFrame frame, List<Notification> notifications) {
    dialog.dispose();

    JTabbedPane tabbedPane = createTabbedPane(notifications);

    JOptionPane.showMessageDialog(
      frame,
      tabbedPane,
      notifications.size() > 1 ? "NOTIFICAÇÕES ENVIADAS": "NOTIFICAÇÃO ENVIADA",
      JOptionPane.INFORMATION_MESSAGE,
      FileUtils.getIcon()
    );
  }

  private JTabbedPane createTabbedPane(List<Notification> notifications) {
    JTabbedPane tabbedPane = new JTabbedPane();

    int count = 1;

    for (Notification notification : notifications) {
      String html = FileUtils.getHtml(notification);

      JEditorPane editorPane = new JEditorPane("text/html", html);
      editorPane.setEditable(false);

      tabbedPane.add(String.valueOf(count), editorPane);

      count++;
    }

    return tabbedPane;
  }

  private boolean isInvalidButtons() {
    return !activityReminderRadioButton.isSelected()
      && !scheduleChangeRadioButton.isSelected()
      && !importantEventsRadioButton.isSelected();
  }

  private Notification createNotification() {
    String code = UUID.randomUUID().toString()
      .replace("-", "")
      .substring(0, 10);

    String message = contentTextField.getText();

    String event;
    PriorityEnum priority;

    if (activityReminderRadioButton.isSelected()) {
      event = activityReminderRadioButton.getText();
      priority = PriorityEnum.LOW;
    }
    else if (scheduleChangeRadioButton.isSelected()) {
      event = scheduleChangeRadioButton.getText();
      priority = PriorityEnum.MID;
    }
    else {
      event = importantEventsRadioButton.getText();
      priority = PriorityEnum.HIGH;
    }

    return new Notification(code, message, priority, event);
  }

  private void disableAllButtons() {
    activityReminderRadioButton.setSelected(false);
    scheduleChangeRadioButton.setSelected(false);
    importantEventsRadioButton.setSelected(false);
  }

  private void showScheduledMessage(JFrame frame) {
    JOptionPane jOptionPane = new JOptionPane("Notificação agendada com sucesso.", JOptionPane.INFORMATION_MESSAGE);
    dialog = jOptionPane.createDialog(frame, "AGENDAMENTO");
    dialog.setVisible(true);
  }

  private void setUpTextField() {
    ((AbstractDocument) contentTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
      @Override
      public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
      }

      @Override
      public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (fb.getDocument().getLength() <= 85) {
          super.insertString(fb, offset, string, attr);
        }
      }

      @Override
      public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (fb.getDocument().getLength() <= 85) {
          super.insertString(fb, offset, text, attrs);
        }
      }
    });
  }
}