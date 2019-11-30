package manon.service.app.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.Cfg;
import manon.service.app.NotificationService;
import manon.util.Tools;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final Cfg cfg;
    private final JavaMailSender mailSender;

    @Override
    public void notifyBatchExecution(String batchName, String status) {
        String notificationTarget = cfg.getBatchNotificationEmailTo();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationTarget);
        message.setSubject("MANON executed batch '" + batchName + "', " + status);
        message.setText("MANON executed batch '" + batchName + "' that ended with status " + status + " (" + Tools.now() + ").");
        try {
            mailSender.send(message);
            log.debug("sent notification to " + notificationTarget);
        } catch (MailException e) {
            log.warn("cannot send notification to {}: {}", notificationTarget, e.getMessage());
        }
    }
}
