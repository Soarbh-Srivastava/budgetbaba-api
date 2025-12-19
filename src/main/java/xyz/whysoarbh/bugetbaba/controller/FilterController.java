package xyz.whysoarbh.bugetbaba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.whysoarbh.bugetbaba.dto.ExpenseDTO;
import xyz.whysoarbh.bugetbaba.dto.FilterDTO;
import xyz.whysoarbh.bugetbaba.dto.IncomeDTO;
import xyz.whysoarbh.bugetbaba.service.ExpenseService;
import xyz.whysoarbh.bugetbaba.service.IncomeService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController
{
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDTO filter) {
        LocalDate startDate = filter.getStartDate();
        LocalDate endDate = filter.getEndDate();

        // if both null → default today
        if (startDate == null && endDate == null) {
            startDate = LocalDate.now().minusDays(30);
            endDate = LocalDate.now();
        }

        if (startDate == null && endDate == null) {
            // Case 1: both null → default last 30 days
            startDate = LocalDate.now().minusDays(30);
            endDate = LocalDate.now();
        }

        if (startDate != null && endDate == null) {
            // Case 2: only startDate given → end = today
            endDate = LocalDate.now();
        }

        if (startDate == null && endDate != null) {
            // Case 3: only endDate given → start = endDate - 30 days
            startDate = endDate.minusDays(30);
        }

        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder())
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        if ("income".equalsIgnoreCase(filter.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncome(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        }
        else if ("expense".equalsIgnoreCase(filter.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        }
        else {
            return ResponseEntity.badRequest().body("Invalid type: Must be 'income' or 'expense'");
        }
    }

}
