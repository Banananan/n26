package n26.repository;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
public class Transaction extends Model {

    @NotNull
    private final Long transactionId;

    private final Long parentTransactionId;

    @NotEmpty
    private final String type;

    @NotNull
    private final Double amount;

    @Builder
    Transaction(Long transactionId, Long parentTransactionId, String type, double amount) {
        this.transactionId = transactionId;
        this.parentTransactionId = parentTransactionId;
        this.type = type;
        this.amount = amount;
    }
}
