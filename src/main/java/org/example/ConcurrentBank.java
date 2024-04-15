package org.example;

import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
@Slf4j
public class ConcurrentBank {
    private Map<UUID, BankAccount> accounts = new ConcurrentHashMap<>();
    public BankAccount createAccount(BigDecimal initialBalance) {
        log.info("Creating account with initial balance {}", initialBalance);

        UUID id = UUID.randomUUID();
        AtomicReference<BigDecimal> balance = new AtomicReference<>(initialBalance);
        BankAccount account = new BankAccountImpl(id, balance);

        accounts.put(id, account);
        return account;
    }
    public void transfer(BankAccountImpl fromAccount, BankAccountImpl toAccount, BigDecimal amount) {
        log.info("Transferring {} from {} to {}", amount, fromAccount.getId(), toAccount.getId());

        UUID firstId = fromAccount.getId();
        UUID secondId = toAccount.getId();

        if (firstId.compareTo(secondId) < 0) {
            transferAccounts(fromAccount, toAccount, amount);
        } else {
            transferAccounts(toAccount, fromAccount, amount);
        }
    }

    private void transferAccounts(BankAccountImpl fromAccount, BankAccountImpl toAccount, BigDecimal amount) {
        log.info("Transferring {} from {} to {}", amount, fromAccount.getId(), toAccount.getId());

        Lock firstLock = fromAccount.getLock();
        Lock secondLock = toAccount.getLock();

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                fromAccount.withdraw(amount);
                toAccount.deposit(amount);
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    public BigDecimal getTotalBalance() {
        log.info("Calculating total balance");

        return accounts.values().stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
