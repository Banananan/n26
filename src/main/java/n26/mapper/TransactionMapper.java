package n26.mapper;

import n26.dto.TransactionDto;
import n26.repository.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction map(long transactionId, TransactionDto transactionDto) {
        return Transaction.builder()
                .transactionId(transactionId)
                .parentTransactionId(transactionDto.getParent_id())
                .type(transactionDto.getType())
                .amount(transactionDto.getAmount())
                .build();
    }

    public TransactionDto map(Transaction transaction) {
        return TransactionDto.builder()
                .parent_id(transaction.getParentTransactionId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .build();
    }
}
