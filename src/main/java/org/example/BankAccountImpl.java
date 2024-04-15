package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Getter
@Slf4j
public class BankAccountImpl implements BankAccount {
    private final UUID id;
    private final AtomicReference<BigDecimal> balance;
    private final Lock lock = new ReentrantLock();
    public BigDecimal deposit(BigDecimal amount) {
        log.info("Deposit {}", amount);

        balance.updateAndGet(currentValue -> currentValue.add(amount));
        return amount;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        log.info("Withdraw {}", amount);

        lock.lock();
        try {
            BigDecimal currentBalance = balance.get();
            if (currentBalance.compareTo(amount) >= 0) {
                balance.updateAndGet(currentValue -> currentValue.subtract(amount));
            } else {
                System.out.println("Insufficient funds");
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public BigDecimal getBalance() {
        return balance.get();
    }
}
