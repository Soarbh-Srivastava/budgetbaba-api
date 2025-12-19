package xyz.whysoarbh.bugetbaba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.whysoarbh.bugetbaba.dto.ExpenseDTO;
import xyz.whysoarbh.bugetbaba.dto.IncomeDTO;
import xyz.whysoarbh.bugetbaba.dto.RecentTransactionDTO;
import xyz.whysoarbh.bugetbaba.entity.ProfileEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashBoardService
{
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String,Object> getDashBoardData()
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String,Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncome = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpense = expenseService.getLastest5ExpensesForCurrentUser();
        List<RecentTransactionDTO> recentTransaction = concat(latestIncome.stream().map(income ->
                RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("income")
                        .build()),
                latestExpense.stream().map(expense ->
                        RecentTransactionDTO.builder()
                        .id(expense.getId())
                        .profileId(profile.getId())
                        .icon(expense.getIcon())
                        .name(expense.getName())
                        .amount(expense.getAmount())
                        .date(expense.getDate())
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .type("expense")
                        .build()))
                .sorted((a,b) ->{
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp == 0 && a.getCreatedAt()!=null && b.getCreatedAt()==null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                        }).collect(Collectors.toUnmodifiableList());
        returnValue.put("totalBalance",
                incomeService.getTotalIncomeForCurrentUser().
                        subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totalIncome",incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpense",expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Expense",latestExpense);
        returnValue.put("recent5Income",latestIncome);
        returnValue.put("recentTransaction",recentTransaction);
        return returnValue;
    }
}
