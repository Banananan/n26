package n26.repository;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Component
public class TransactionStoreValidator extends StoreValidator<Transaction> {

    @Override
    protected void validateSpecific(Collection<Transaction> store, Transaction transaction) {
        validateTransactionIdUniqueness(store, transaction.getTransactionId());
        validateParentTransactionIdExistence(store, transaction.getParentTransactionId());
    }

    private void validateTransactionIdUniqueness(Collection<Transaction> store, Long transactionId) {
        if (store.stream()
                .anyMatch(element -> Objects.equals(element.getTransactionId(), transactionId))) {
            throw new IllegalStateException("Unique constraint for transactionId violated; transactionId " +
                    transactionId + " already exists");
        }
    }

    private void validateParentTransactionIdExistence(Collection<Transaction> store, Long parentTransactionId) {
        if (Objects.nonNull(parentTransactionId) && store.stream()
                .noneMatch(element -> Objects.equals(
                        element.getTransactionId(),
                        parentTransactionId))) {
            throw new IllegalStateException("Existence constraint for parentTransactionId violated; parentTransactionId " +
                    parentTransactionId + " doesn't exist");
        }
    }
}
