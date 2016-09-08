package n26.repository;

import n26.Application;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TransactionRepositoryTest {

    @Autowired
    private ModelValidator<Transaction> modelValidator;

    @Autowired
    private StoreValidator<Transaction> storeValidator;

    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        transactionRepository = new TransactionRepository(storeValidator, modelValidator);
    }

    @Test
    public void findsTransactionByTransactionId() {
        long transactionId = 1L;
        String type = "test";
        double amount = 1000;
        transactionRepository.save(Transaction.builder()
                .transactionId(transactionId)
                .type(type)
                .amount(amount)
                .build());

        Transaction transaction = transactionRepository.findByTransactionId(transactionId).get();
        Assertions.assertThat(transaction.getTransactionId()).isEqualTo(transactionId);
        Assertions.assertThat(transaction.getParentTransactionId()).isNull();
        Assertions.assertThat(transaction.getType()).isEqualTo(type);
        Assertions.assertThat(transaction.getAmount()).isEqualTo(amount);
    }

    @Test
    public void findsTransactionByType() {
        String type = "test";
        Transaction transaction = Transaction.builder()
                .transactionId(1L)
                .type(type)
                .amount(1000)
                .build();
        transactionRepository.save(transaction);

        Set<Transaction> transactions = transactionRepository.findByType(type);
        Assertions.assertThat(transactions).hasSize(1);
        Assertions.assertThat(transactions).contains(transaction);
    }

    @Test
    public void findsTransactionByParentTransactionId() {
        long parentTransactionId = 0L;
        transactionRepository.save(Transaction.builder()
                .transactionId(parentTransactionId)
                .type("test")
                .amount(1000)
                .build());
        Transaction childTransaction = Transaction.builder()
                .transactionId(1L)
                .parentTransactionId(parentTransactionId)
                .type("test")
                .amount(1000)
                .build();
        transactionRepository.save(childTransaction);

        Set<Transaction> childTransactions = transactionRepository.findByParentTransactionId(parentTransactionId);
        Assertions.assertThat(childTransactions).hasSize(1);
        Assertions.assertThat(childTransactions).contains(childTransaction);
    }
}
