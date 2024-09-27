package br.com.kafka.principal.views;

import br.com.kafka.principal.entities.LogNotificationEntity;
import br.com.kafka.principal.enums.PriorityEnum;
import br.com.kafka.principal.models.Notification;
import br.com.kafka.principal.producers.KafkaProducer;
import br.com.kafka.principal.repositories.LogNotificationRepository;
import br.com.kafka.principal.utils.FileUtils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import lombok.Getter;

public class KafkaNotificationView {

  @Getter
  private JPanel mainPanel;

  private JLabel title;

  private JPanel contentPanel;

  private JPanel eventPanel;
  private JRadioButton activityReminderRadioButton;
  private JRadioButton scheduleChangeRadioButton;
  private JRadioButton importantEventsRadioButton;

  private JPanel contentTextPanel;
  private JTextField contentTextField;

  private JPanel buttonPanel;
  private JButton sendButton;
  private JButton logButton;
  private JButton closeButton;

  private JDialog dialog;

  private final KafkaProducer kafkaProducer;
  private final LogNotificationRepository repository;

  public KafkaNotificationView(JFrame frame, KafkaProducer kafkaProducer, LogNotificationRepository repository) {
    this.kafkaProducer = kafkaProducer;
    this.repository = repository;

    setUpTextField();

    actionSendButton(frame);
    actionLogButton(frame);
    actionActivityReminderRadioButton();
    actionScheduleChangeRadioButton();
    actionImportantEventsRadioButton();
    actionCloseButton(frame);
  }

  public void actionSendButton(JFrame frame) {
    this.sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (isInvalidButtons()) {
          JOptionPane.showMessageDialog(
            frame,
            "Favor selecionar o tipo de evento a ser notificado.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
          );
          return;
        }

        if (contentTextField.getText().isEmpty()) {
          JOptionPane.showMessageDialog(
            frame,
            "Favor inserir o contéudo da notificação.",
            "Aviso",
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

  public void actionLogButton(JFrame frame) {
    this.logButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        List<LogNotificationEntity> logs = repository.findAll();

        if (logs.isEmpty()) {
          JOptionPane.showMessageDialog(
            frame,
            "No momento não existem registros de logs.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
          );
          return;
        }

        JScrollPane scrollPane = createScrollPane(logs);

        JOptionPane.showMessageDialog(
          frame,
          scrollPane,
          "Relatório de Logs",
          JOptionPane.INFORMATION_MESSAGE,
          FileUtils.getReportIcon()
        );
      }
    });
  }

  public void actionActivityReminderRadioButton() {
    this.activityReminderRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearTextField();
        disableAllButtons();
        activityReminderRadioButton.setSelected(true);
        contentTextField.setText("Atenção, a data limite para entrega e apresentação da atividade é dia 04/10/2024.");
      }
    });
  }

  public void actionScheduleChangeRadioButton() {
    this.scheduleChangeRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearTextField();
        disableAllButtons();
        scheduleChangeRadioButton.setSelected(true);
        contentTextField.setText("Em virtude do feriado nacional do dia da Consciência Negra, não haverá aula no dia 20/11/2024.");
      }
    });
  }

  public void actionImportantEventsRadioButton() {
    this.importantEventsRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearTextField();
        disableAllButtons();
        importantEventsRadioButton.setSelected(true);
        contentTextField.setText("Dia 12/12/2024 acontecerá o evento de apresentação das atualizações e novidades do Java Swing.");
      }
    });
  }

  public void actionCloseButton(JFrame frame) {
    this.closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
  }

  public void showResult(JFrame frame, List<Notification> notifications) {
    dialog.dispose();

    JTabbedPane tabbedPane = createTabbedPane(notifications);

    JOptionPane.showMessageDialog(
      frame,
      tabbedPane,
      notifications.size() > 1 ? "Lote de notificações enviado": "Notificação enviada",
      JOptionPane.INFORMATION_MESSAGE,
      FileUtils.getNotificationIcon()
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

  private JScrollPane createScrollPane(List<LogNotificationEntity> logs) {
    String[] columns = { "#", "Data de Agendamento", "Código da Notificação", "Código do Lote", "Data de Envio", "Notificação"};

    Object[][] rows = new Object[logs.size()][columns.length];

    for (int i = 0; i < logs.size(); i++) {
      rows[i][0] = (i + 1);
      rows[i][1] = formatDate(logs.get(i).getScheduleDate());
      rows[i][2] = logs.get(i).getNotificationCode();
      rows[i][3] = logs.get(i).getBatchCode() != null ? logs.get(i).getBatchCode() : "-";
      rows[i][4] = formatDate(logs.get(i).getSendDate());
      rows[i][5] = logs.get(i).getNotification();
    }

    DefaultTableModel tableModel = new DefaultTableModel(rows, columns);

    JTable table = new JTable(tableModel);
    table.getTableHeader().setFont(new Font("JetBrains Mono", Font.PLAIN,  14));
    table.setRowHeight(25);

    table.getColumnModel().getColumn(0).setPreferredWidth(60);
    table.getColumnModel().getColumn(1).setPreferredWidth(210);
    table.getColumnModel().getColumn(2).setPreferredWidth(160);
    table.getColumnModel().getColumn(3).setPreferredWidth(160);
    table.getColumnModel().getColumn(4).setPreferredWidth(210);
    table.getColumnModel().getColumn(5).setPreferredWidth(1800);

    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    for (int i = 0; i < columns.length; i++) {
      table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(new Dimension(1000, 500));
    scrollPane.setEnabled(false);

    return scrollPane;
  }

  private Notification createNotification() {
    String code = UUID.randomUUID().toString()
      .replace("-", "")
      .substring(0, 16);

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

  private boolean isInvalidButtons() {
    return !activityReminderRadioButton.isSelected()
      && !scheduleChangeRadioButton.isSelected()
      && !importantEventsRadioButton.isSelected();
  }

  private void disableAllButtons() {
    activityReminderRadioButton.setSelected(false);
    scheduleChangeRadioButton.setSelected(false);
    importantEventsRadioButton.setSelected(false);
  }

  private void clearTextField() {
    contentTextField.setDocument(new PlainDocument());
  }

  private void showScheduledMessage(JFrame frame) {
    JOptionPane jOptionPane = new JOptionPane(
      "Notificação agendada com sucesso.",
      JOptionPane.INFORMATION_MESSAGE
    );

    dialog = jOptionPane.createDialog(frame, "Confirmação");
    dialog.setVisible(true);
  }

  private void setUpTextField() {
    ((AbstractDocument) contentTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
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

  private String formatDate(LocalDateTime date) {
    if (date == null) {
      return "-";
    }

    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
  }

}