package n26.service;

import n26.Application;
import n26.dto.TransactionDto;
import n26.dto.TransactionsSum;
import n26.mapper.TransactionMapper;
import n26.repository.Transaction;
import n26.repository.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TransactionServiceTest {

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    private TransactionService transactionService;

    @Before
    public void setUp() {
        transactionService = new TransactionService(transactionRepository, transactionMapper);
    }

    @Test
    public void getsTransactionByTransactionId() {
        long transactionId = 1L;
        String type = "test";
        double amount = 1000;
        Mockito.when(transactionRepository.findByTransactionId(Mockito.anyLong()))
                .thenReturn(Optional.of(Transaction.builder()
                        .transactionId(transactionId)
                        .type(type)
                        .amount(amount)
                        .build()));

        TransactionDto transaction = transactionService.getTransactionByTransactionId(transactionId);

        Assertions.assertThat(transaction.getParent_id()).isNull();
        Assertions.assertThat(transaction.getType()).isEqualTo(type);
        Assertions.assertThat(transaction.getAmount()).isEqualTo(amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenTryingToGetNotExistingTransaction() {
        Mockito.when(transactionRepository.findByTransactionId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        transactionService.getTransactionByTransactionId(1L);
    }

    @Test
    public void getsTransactionsIdsByType() {
        long transactionId = 1L;
        String type = "test";
        Mockito.when(transactionRepository.findByType(Mockito.anyString()))
                .thenReturn(Collections.singleton(Transaction.builder()
                        .transactionId(transactionId)
                        .type(type)
                        .amount(1000)
                        .build()));

        Set<Long> transactionsIds = transactionService.getTransactionsIdsByType(type);

        Assertions.assertThat(transactionsIds).hasSize(1);
        Assertions.assertThat(transactionsIds).contains(transactionId);
    }

    @Test
    public void returnsEmptyCollectionOfTransactionsIdsWhenStoreIsEmpty() {
        Mockito.when(transactionRepository.findByType(Mockito.anyString()))
                .thenReturn(Collections.emptySet());

        Set<Long> transactionsIds = transactionService.getTransactionsIdsByType("test");

        Assertions.assertThat(transactionsIds).isEmpty();
    }

    @Test
    public void calculatesSumOfChildTransactions() {
        long parentTransactionId = 0L;
        String type = "test";
        HashSet<Transaction> childTransactions = new HashSet<>();
        childTransactions.add(Transaction.builder()
                .transactionId(1L)
                .parentTransactionId(parentTransactionId)
                .type(type)
                .amount(1000)
                .build());
        childTransactions.add(Transaction.builder()
                .transactionId(2L)
                .parentTransactionId(parentTransactionId)
                .type(type)
                .amount(2000)
                .build());
        Mockito.when(transactionRepository.findByParentTransactionId(Mockito.anyLong()))
                .thenReturn(childTransactions);

        TransactionsSum transactionsSum = transactionService.calculateSumOfChildTransactions(parentTransactionId);

        Assertions.assertThat(transactionsSum.getSum()).isEqualTo(childTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());
    }

    @Test
    public void returns0AsSumForChildlessTransaction() {
        Mockito.when(transactionRepository.findByParentTransactionId(Mockito.anyLong()))
                .thenReturn(Collections.emptySet());

        TransactionsSum transactionsSum = transactionService.calculateSumOfChildTransactions(0L);

        Assertions.assertThat(transactionsSum.getSum()).isEqualTo(0);
    }
}
