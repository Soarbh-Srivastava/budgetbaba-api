package xyz.whysoarbh.bugetbaba.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.whysoarbh.bugetbaba.dto.ExpenseDTO;
import xyz.whysoarbh.bugetbaba.entity.ProfileEntity;
import xyz.whysoarbh.bugetbaba.repository.ExpenseRepository;
import xyz.whysoarbh.bugetbaba.repository.ProfileRepository;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;

    @Value("${buget.baba.frontend.url}")
    private String frontendUrl;


    //    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
    public void sendDailyIncomeExpenseNotification() {
        log.info("Job started: Sending Daily Income and Expense adding Notification");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            String body = "Hi " + profile.getFullName() + "<br><br>"
                    + "This is a friendly reminder to add your income and expense for today in Buget Baba.<br><br>"
                    + "<a href='" + frontendUrl + "' style='display:inline-block;padding:10px 20px;"
                    + "background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;"
                    + "font-weight:bold;'>Go to Buget Baba</a>"
                    + "<br><br>Best regards,<br>Buget Baba Team";
            emailService.sendEmail(profile.getEmail(), "Daily Expense Reminder: Add your income and expenses", body);
        }
        log.info("Job finished: Daily Expense Notification sent.");
    }

//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
    public void sendDailyExpenseSummaryNotification() {
        log.info("Job started: Sending Daily Expense Summary Notification");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todayExpenses =
                    expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));

            if (!todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();

                // Table header
                table.append("<table style='border-collapse:collapse;width:100%;border:1px solid #ddd;'>")
                        .append("<thead>")
                        .append("<tr style='background-color:#f2f2f2;text-align:left;'>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Date</th>")
                        .append("</tr>")
                        .append("</thead>")
                        .append("<tbody>");

                double total = 0.0;

                // Table rows
                for (ExpenseDTO expense : todayExpenses) {
                    table.append("<tr>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryName()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>₹").append(expense.getAmount()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getDate()).append("</td>")
                            .append("</tr>");
                    total += expense.getAmount().doubleValue();
                }

                // Total row
                table.append("<tr style='font-weight:bold;background-color:#f9f9f9;'>")
                        .append("<td colspan='2' style='border:1px solid #ddd;padding:8px;text-align:right;'>Total:</td>")
                        .append("<td colspan='2' style='border:1px solid #ddd;padding:8px;'>₹").append(total).append("</td>")
                        .append("</tr>");

                table.append("</tbody></table>");

                // Email body
                String body = "Hi " + profile.getFullName() + ",<br><br>"
                        + "Here is your daily expense summary for <b>" + LocalDate.now(ZoneId.of("Asia/Kolkata")) + "</b>:<br><br>"
                        + table
                        + "<br><br>Keep tracking your expenses with <a href='" + frontendUrl + "'>Budget Baba</a>!"
                        + "<br><br>Best regards,<br>Buget Baba Team";

                emailService.sendEmail(profile.getEmail(), "Your Daily Expense Summary", body);
            }
        }

        log.info("Job finished: Daily Expense Summary Notification sent.");
    }
}