package xyz.whysoarbh.bugetbaba.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.whysoarbh.bugetbaba.entity.ExpenseEntity;
import xyz.whysoarbh.bugetbaba.entity.IncomeEntity;
import xyz.whysoarbh.bugetbaba.repository.ExpenseRepository;
import xyz.whysoarbh.bugetbaba.repository.IncomeRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public String generateReport(Long profileId) {
        LocalDate startDate = LocalDate.now();
        LocalDate lastExpenseDate = expenseRepository.findLastDateByProfileId(profileId);
        LocalDate lastIncomeDate = incomeRepository.findLastDateByProfileId(profileId);

        LocalDate endDate;
        if (lastExpenseDate != null && lastIncomeDate != null) {
            endDate = lastExpenseDate.isAfter(lastIncomeDate) ? lastExpenseDate : lastIncomeDate;
        } else if (lastExpenseDate != null) {
            endDate = lastExpenseDate;
        } else if (lastIncomeDate != null) {
            endDate = lastIncomeDate;
        } else {
            endDate = startDate;
        }

        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetween(profileId, startDate, endDate);
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetween(profileId, startDate, endDate);

        double totalExpense = expenses.stream().mapToDouble(e -> e.getAmount().doubleValue()).sum();
        double totalIncome = incomes.stream().mapToDouble(i -> i.getAmount().doubleValue()).sum();

        return """
                Income & Expense Report
                ========================
                Period: %s to %s

                Total Income: $%.2f
                Total Expense: $%.2f
                Balance: $%.2f
                """.formatted(startDate, endDate, totalIncome, totalExpense, (totalIncome - totalExpense));
    }

    public List<Map<String, Object>> getIncomeDetails(Long profileId) {
        List<IncomeEntity> incomes = incomeRepository.findByProfileId(profileId);
        log.info("Fetched {} income records for profileId={}", incomes.size(), profileId);
        incomes.forEach(i -> log.info("Income: Date={}, Name={}, Amount={}", i.getDate(), i.getName(), i.getAmount()));

        return incomes.stream()
                .map(i -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Date", i.getDate());      // Updated key to match Excel header
                    map.put("Name", i.getName());      // Updated key to match Excel header
                    map.put("Amount", i.getAmount());  // Updated key to match Excel header
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getExpenseDetails(Long profileId) {
        List<ExpenseEntity> expenses = expenseRepository.findByProfileId(profileId);
        log.info("Fetched {} expense records for profileId={}", expenses.size(), profileId);
        expenses.forEach(e -> log.info("Expense: Date={}, Name={}, Amount={}", e.getDate(), e.getName(), e.getAmount()));

        return expenses.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Date", e.getDate());       // Updated key to match Excel header
                    map.put("Name", e.getName());       // Updated key to match Excel header
                    map.put("Amount", e.getAmount());   // Updated key to match Excel header
                    return map;
                })
                .collect(Collectors.toList());
    }
}
