package n26.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import n26.dto.TransactionDto;
import n26.dto.TransactionsSum;
import n26.mapper.TransactionMapper;
import n26.repository.Transaction;
import n26.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__(@Autowired))
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public void storeTransaction(Long transactionId, TransactionDto transactionDto) {
        transactionRepository.save(transactionMapper.map(transactionId, transactionDto));
    }

    public TransactionDto getTransactionByTransactionId(Long transactionId) {
        return transactionMapper.map(transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get transaction with transactionId " + transactionId)));
    }

    public Set<Long> getTransactionsIdsByType(String transactionType) {
        return transactionRepository.findByType(transactionType).stream()
                .map(Transaction::getTransactionId)
                .collect(Collectors.toSet());
    }

    public TransactionsSum calculateSumOfChildTransactions(Long parentTransactionId) {
        return TransactionsSum.builder()
                .sum(transactionRepository.findByParentTransactionId(parentTransactionId).stream()
                        .mapToDouble(Transaction::getAmount)
                        .sum())
                .build();
    }
}
