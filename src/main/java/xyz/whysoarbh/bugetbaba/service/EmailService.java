package xyz.whysoarbh.bugetbaba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService
{
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body)
    {
        try
        {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    // New function to send Excel reports
    public void sendExcelReport(String to, String reportType, byte[] excelData) {
        try {
            String subject = reportType.substring(0, 1).toUpperCase() + reportType.substring(1) + " Report";
            String body = "Hello,\n\nPlease find attached your " + reportType + " report.\n\nRegards,\nBugetBaba Team";
            String filename = reportType + "-report.xlsx";

            // Create MIME message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            helper.addAttachment(filename, new ByteArrayResource(excelData));

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send " + reportType + " report: " + e.getMessage(), e);
        }
    }
}
