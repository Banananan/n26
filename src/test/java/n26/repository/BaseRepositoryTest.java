package n26.repository;

import n26.Application;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class BaseRepositoryTest {

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
    public void savesObject() {
        long transactionId = 1L;
        String type = "test";
        double amount = 1000;
        Long id = transactionRepository.save(Transaction.builder()
                .transactionId(transactionId)
                .type(type)
                .amount(amount)
                .build());

        Optional<Transaction> perhapsTransaction = transactionRepository.findById(id);
        Assertions.assertThat(perhapsTransaction).isPresent();
        Transaction transaction = perhapsTransaction.get();
        Assertions.assertThat(transaction.getTransactionId()).isEqualTo(transactionId);
        Assertions.assertThat(transaction.getType()).isEqualTo(type);
        Assertions.assertThat(transaction.getAmount()).isEqualTo(amount);
    }
}
