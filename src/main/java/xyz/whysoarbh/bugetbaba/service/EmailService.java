package xyz.whysoarbh.bugetbaba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService
{
    @Value("${spring.mail.properties.mail.from}")
    private String fromEmail;

    @Value("${brevo.api.key:disabled}")
    private String brevoApiKey;

    @Value("${brevo.api.url:https://api.brevo.com/v3/smtp/email}")
    private String brevoApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String to, String subject, String body)
    {
        Map<String, Object> payload = buildBasePayload(to, subject, body);
        postEmail(payload, "Failed to send email");
    }

    // New function to send Excel reports
    public void sendExcelReport(String to, String reportType, byte[] excelData) {
        String subject = reportType.substring(0, 1).toUpperCase() + reportType.substring(1) + " Report";
        String body = "Hello,\n\nPlease find attached your " + reportType + " report.\n\nRegards,\nBugetBaba Team";
        String filename = reportType + "-report.xlsx";

        Map<String, Object> payload = buildBasePayload(to, subject, body);
        String encoded = Base64.getEncoder().encodeToString(excelData);
        payload.put("attachment", List.of(Map.of(
                "name", filename,
                "content", encoded
        )));

        postEmail(payload, "Failed to send " + reportType + " report");
    }

    private Map<String, Object> buildBasePayload(String to, String subject, String body) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("email", fromEmail));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("textContent", body);
        return payload;
    }

    private void postEmail(Map<String, Object> payload, String errorMessage) {
        try {
            if ("disabled".equals(brevoApiKey)) {
                System.out.println("Email service disabled. Skipping: " + errorMessage);
                return;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(brevoApiUrl, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(errorMessage + ": HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Warning: " + errorMessage + ": " + e.getMessage());
            // Don't throw - allow app to continue without email
        }
    }
}
