package com.task.ExpensesTracker.Controller;

import com.task.ExpensesTracker.Service.TransactionService;
import com.task.ExpensesTracker.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Void> createTransaction(@RequestBody Transaction transaction) {
        transactionService.saveTransaction(transaction);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total/{date}")
    public ResponseEntity<Map<String, BigDecimal>> getTotalAmountByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        BigDecimal totalIncome = transactionService.getTotalIncomeByDate(localDate);
        BigDecimal totalExpenses = transactionService.getTotalExpensesByDate(localDate);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return ResponseEntity.ok(Map.of(
                "totalIncome", totalIncome,
                "totalExpenses", totalExpenses,
                "balance", balance
        ));
    }
    @GetMapping("/category/{date}")
    public ResponseEntity<Map<String, BigDecimal>> getAmountByCategoryAndDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        Map<String, BigDecimal> categoryTotals = transactionService.getAmountByCategoryAndDate(localDate);
        return ResponseEntity.ok(categoryTotals);
    }

    @GetMapping("/monthly/{month}")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlyTotals(@PathVariable String month) {
        LocalDate localDate = LocalDate.parse(month + "-01");
        Map<String, BigDecimal> monthlyTotals = transactionService.getMonthlyTotals(localDate);
        return ResponseEntity.ok(monthlyTotals);
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getCurrentBalance() {
        BigDecimal balance = transactionService.getCurrentBalance();
        return ResponseEntity.ok(balance);
    }
}
