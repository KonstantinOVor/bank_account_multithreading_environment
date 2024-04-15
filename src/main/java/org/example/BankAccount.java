package org.example;

import java.math.BigDecimal;

public interface BankAccount {

    BigDecimal deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    BigDecimal getBalance();
}
