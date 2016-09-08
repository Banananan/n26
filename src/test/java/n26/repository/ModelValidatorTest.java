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

import javax.validation.ValidationException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ModelValidatorTest {

    @MockBean
    private StoreValidator<Transaction> storeValidator;

    @Autowired
    private ModelValidator<Transaction> modelValidator;

    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        transactionRepository = new TransactionRepository(storeValidator, modelValidator);
    }

    @Test(expected = ValidationException.class)
    public void throwsExceptionForInvalidEntity() {
        modelValidator.validate(Transaction.builder()
                .transactionId(1L)
                .type("test")
                .amount(1000)
                .build());
    }

    @Test
    public void doesNotThrowExceptionForProperEntity() {
        try {
            Transaction transaction = transactionRepository.findById(
                    transactionRepository.save(Transaction.builder()
                            .transactionId(1L)
                            .type("test")
                            .amount(1000)
                            .build()))
                    .get();

            modelValidator.validate(transaction);
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
