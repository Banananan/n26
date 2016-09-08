package n26.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository extends BaseRepository<Transaction> {

    @Autowired
    TransactionRepository(@Qualifier("transactionStoreValidator") StoreValidator<Transaction> storeValidator,
                          ModelValidator<Transaction> modelValidator) {
        super(storeValidator, modelValidator);
    }

    public Optional<Transaction> findByTransactionId(Long transactionId) {
        return store.stream()
                .filter(transaction -> Objects.equals(transaction.getTransactionId(), transactionId))
                .findAny();
    }

    public Set<Transaction> findByType(String type) {
        return store.stream()
                .filter(transaction -> Objects.equals(transaction.getType(), type))
                .collect(Collectors.toSet());
    }

    public Set<Transaction> findByParentTransactionId(Long parentTransactionId) {
        return store.stream()
                .filter(transaction -> Objects.equals(transaction.getParentTransactionId(), parentTransactionId))
                .collect(Collectors.toSet());
    }
}
