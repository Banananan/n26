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
public class StoreValidatorTest {

    @Autowired
    private StoreValidator<Transaction> storeValidator;

    @MockBean
    private ModelValidator<Transaction> modelValidator;

    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        transactionRepository = new TransactionRepository(storeValidator, modelValidator);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsIdUniquenessExceptionWhenCheckingWithDuplicatedEntity() {
        Transaction transaction = transactionRepository.findById(
                transactionRepository.save(Transaction.builder()
                        .transactionId(1L)
                        .type("test")
                        .amount(1000)
                        .build()))
                .get();
        HashSet<Transaction> store = new HashSet<>();
        store.add(transaction);

        storeValidator.validate(store, transaction);
    }

    @Test
    public void doesNotThrowIdUniquenessExceptionWhenCheckingWithEmptyStore() {
        try {
            storeValidator.validate(new HashSet<>(), transactionRepository.findById(
                    transactionRepository.save(Transaction.builder()
                            .transactionId(1L)
                            .type("test")
                            .amount(1000)
                            .build()))
                    .get());
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
