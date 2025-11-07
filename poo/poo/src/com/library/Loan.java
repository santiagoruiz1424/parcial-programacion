package com.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private Material material;
    private String borrower;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(Material material, String borrower, LocalDate loanDate, int daysLoan) {
        this.material = material;
        this.borrower = borrower;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(daysLoan);
        this.returnDate = null;
        material.setAvailable(false);
    }

    public Material getMaterial() { return material; }
    public String getBorrower() { return borrower; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate d) {
        this.returnDate = d;
        material.setAvailable(true);
    }

    public long daysLate(LocalDate ref) {
        LocalDate end = (returnDate != null) ? returnDate : ref;
        long days = ChronoUnit.DAYS.between(dueDate, end);
        return days > 0 ? days : 0;
    }

    public double fineAmount(double rate, LocalDate ref) {
        return daysLate(ref) * rate;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s | Prestado: %s | Vence: %s | Devuelto: %s",
            material.getId(), borrower, loanDate, dueDate, (returnDate==null?"<no>":returnDate.toString()));
    }
}