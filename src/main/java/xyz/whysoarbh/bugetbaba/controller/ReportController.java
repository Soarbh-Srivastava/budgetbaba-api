package xyz.whysoarbh.bugetbaba.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.whysoarbh.bugetbaba.service.EmailService;
import xyz.whysoarbh.bugetbaba.service.ExcelReportService;
import xyz.whysoarbh.bugetbaba.service.ReportService;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ExcelReportService excelService;
    private final EmailService emailService;
    private final ReportService reportService;

    public ReportController(ExcelReportService excelService,
                            EmailService emailService,
                            ReportService reportService) {
        this.excelService = excelService;
        this.emailService = emailService;
        this.reportService = reportService;
    }

    // DTO to receive JSON body
    @Data
    public static class ReportRequest {
        private String type;       // "income" or "expense"
        private String email;      // recipient email
        private Long profileId;    // user profile ID
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendReport(@RequestBody ReportRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String type = request.getType();
            String email = request.getEmail();
            Long profileId = request.getProfileId();

            if (!"income".equalsIgnoreCase(type) && !"expense".equalsIgnoreCase(type)) {
                response.put("status", "error");
                response.put("message", "Invalid report type: " + type);
                return ResponseEntity.badRequest().body(response);
            }

            List<String> headers = Arrays.asList("Date", "Name", "Amount"); // Match keys in map
            List<Map<String, Object>> data;

            // Fetch data from DB using ReportService
            if ("income".equalsIgnoreCase(type)) {
                data = reportService.getIncomeDetails(profileId);
            } else {
                data = reportService.getExpenseDetails(profileId);
            }

            log.info("Fetched {} rows for profileId={} type={}", data.size(), profileId, type);
            data.forEach(d -> log.info("Row: {}", d));

            // Generate Excel and send email
            byte[] excelBytes = excelService.generateExcelReport(headers, data, type.toUpperCase());
            emailService.sendExcelReport(email, type, excelBytes);

            response.put("status", "success");
            response.put("message", "Report sent successfully to " + email);
            response.put("rows", data.size());
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Excel generation failed", e);
            response.put("status", "error");
            response.put("message", "Failed to generate Excel report: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            log.error("Sending report failed", e);
            response.put("status", "error");
            response.put("message", "Failed to send report: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
