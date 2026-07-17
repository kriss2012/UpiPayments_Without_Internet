package com.demo.upimesh.service;

import com.demo.upimesh.model.Account;
import com.demo.upimesh.model.AccountRepository;
import com.demo.upimesh.model.PaymentInstruction;
import com.demo.upimesh.model.Transaction;
import com.demo.upimesh.model.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Where the actual ledger update happens. Wrapped in a DB transaction so either
 * BOTH the debit and credit happen, or neither does.
 *
 * The @Version column on Account gives us optimistic locking — if two threads
 * somehow get past idempotency and both try to debit the same account, the
 * second one will fail with OptimisticLockException rather than corrupting
 * the balance. (In a demo the idempotency layer should always catch this first,
 * but defense in depth.)
 */
@Service
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    @Autowired private AccountRepository accounts;
    @Autowired private TransactionRepository transactions;

    @Transactional
    public Transaction settle(PaymentInstruction instruction, String packetHash,
                              String bridgeNodeId, int hopCount) {

        Account sender = accounts.findById(instruction.getSenderVpa())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown sender VPA: " + instruction.getSenderVpa()));

        Account receiver = accounts.findById(instruction.getReceiverVpa())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown receiver VPA: " + instruction.getReceiverVpa()));

        BigDecimal amount = instruction.getAmount();
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance: {} has ₹{}, tried to send ₹{}",
                    sender.getVpa(), sender.getBalance(), amount);
            return recordRejected(instruction, packetHash, bridgeNodeId, hopCount);
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accounts.save(sender);
        accounts.save(receiver);

        Transaction tx = new Transaction();
        tx.setPacketHash(packetHash);
        tx.setSenderVpa(instruction.getSenderVpa());
        tx.setReceiverVpa(instruction.getReceiverVpa());
        tx.setAmount(amount);
        tx.setSignedAt(Instant.ofEpochMilli(instruction.getSignedAt()));
        tx.setSettledAt(Instant.now());
        tx.setBridgeNodeId(bridgeNodeId);
        tx.setHopCount(hopCount);
        tx.setStatus(Transaction.Status.SETTLED);
        transactions.save(tx);

        log.info("SETTLED ₹{} from {} to {} (packetHash={}, bridge={}, hops={})",
                amount, sender.getVpa(), receiver.getVpa(),
                packetHash.substring(0, 12) + "...", bridgeNodeId, hopCount);

        return tx;
    }

    private Transaction recordRejected(PaymentInstruction instruction, String packetHash,
                                       String bridgeNodeId, int hopCount) {
        Transaction tx = new Transaction();
        tx.setPacketHash(packetHash);
        tx.setSenderVpa(instruction.getSenderVpa());
        tx.setReceiverVpa(instruction.getReceiverVpa());
        tx.setAmount(instruction.getAmount());
        tx.setSignedAt(Instant.ofEpochMilli(instruction.getSignedAt()));
        tx.setSettledAt(Instant.now());
        tx.setBridgeNodeId(bridgeNodeId);
        tx.setHopCount(hopCount);
        tx.setStatus(Transaction.Status.REJECTED);
        return transactions.save(tx);
    }
}
