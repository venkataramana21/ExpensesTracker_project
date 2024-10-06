package com.task.ExpensesTracker.Service;

import com.task.ExpensesTracker.Repository.TransactionRepository;
import com.task.ExpensesTracker.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    public void saveTransaction(Transaction transaction) {
       transactionRepository.save(transaction);

    }

    public BigDecimal getTotalIncomeByDate(LocalDate date) {
        return transactionRepository.findByDate(date).stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpensesByDate(LocalDate date) {
        return transactionRepository.findByDate(date).stream()
                .filter(transaction -> !transaction.isIncome())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public Map<String, BigDecimal> getAmountByCategoryAndDate(LocalDate date) {
        List<Transaction> transactions = transactionRepository.findByDate(date);
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        for (Transaction transaction : transactions) {
            categoryTotals.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
        }
        return categoryTotals;
    }

    public Map<String, BigDecimal> getMonthlyTotals(LocalDate month) {
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        Map<String, BigDecimal> totals = new HashMap<>();

        for (Transaction transaction : transactions) {
            String key = transaction.isIncome() ? "Income" : "Expense";
            totals.merge(key, transaction.getAmount(), BigDecimal::add);
        }

        return totals;
    }

    public BigDecimal getCurrentBalance() {
        List<Transaction> transactions = transactionRepository.findAll();
        BigDecimal totalIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = transactions.stream()
                .filter(transaction -> !transaction.isIncome())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalIncome.subtract(totalExpenses);
    }



}
