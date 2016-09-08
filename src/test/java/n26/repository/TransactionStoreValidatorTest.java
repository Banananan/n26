package n26.repository;

import n26.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TransactionStoreValidatorTest {

    @Autowired
    private StoreValidator<Transaction> transactionStoreValidator;

    @MockBean
    private ModelValidator<Transaction> modelValidator;

    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        transactionRepository = new TransactionRepository(transactionStoreValidator, modelValidator);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsTransactionIdUniquenessExceptionWhenCheckingWithDuplicatedEntity() {
        long transactionId = 1L;
        Transaction firstTransaction = Transaction.builder()
                .transactionId(transactionId)
                .type("test")
                .amount(1000)
                .build();
        HashSet<Transaction> store = new HashSet<>();
        store.add(firstTransaction);

        Transaction secondTransaction = Transaction.builder()
                .transactionId(transactionId)
                .type("testing")
                .amount(2000)
                .build();

        transactionStoreValidator.validateSpecific(store, secondTransaction);
    }

    @Test
    public void doesNotThrowTransactionIdUniquenessExceptionWhenCheckingWithEmptyStore() {
        try {
            transactionStoreValidator.validateSpecific(new HashSet<>(), Transaction.builder()
                    .transactionId(1L)
                    .type("test")
                    .amount(1000)
                    .build());
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void throwsParentTransactionIdExistenceExceptionWhenCheckingWithEmptyStore() {
        transactionStoreValidator.validateSpecific(new HashSet<>(), Transaction.builder()
                .transactionId(1L)
                .parentTransactionId(0L)
                .type("test")
                .amount(1000)
                .build());
    }

    @Test
    public void doesNotThrowParentTransactionIdExistenceExceptionWhenCheckingWithParentTransaction() {
        long parentTransactionId = 1L;
        Transaction parentTransaction = Transaction.builder()
                .transactionId(parentTransactionId)
                .type("test")
                .amount(1000)
                .build();
        HashSet<Transaction> store = new HashSet<>();
        store.add(parentTransaction);

        Transaction childTransaction = Transaction.builder()
                .transactionId(2L)
                .parentTransactionId(parentTransactionId)
                .type("test")
                .amount(2000)
                .build();
        transactionStoreValidator.validateSpecific(store, childTransaction);
    }

    @Test
    public void doesNotThrowParentTransactionIdExistenceExceptionWhenCheckingWithParentlessTransaction() {
        try {
            transactionStoreValidator.validateSpecific(new HashSet<>(), Transaction.builder()
                    .transactionId(1L)
                    .type("test")
                    .amount(1000)
                    .build());
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
