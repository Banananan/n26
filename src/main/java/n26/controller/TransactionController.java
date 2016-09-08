package n26.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import n26.dto.TransactionDto;
import n26.dto.TransactionsSum;
import n26.service.TransactionService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping(value = "transactionservice", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__(@Autowired))
public class TransactionController {

    private final TransactionService transactionService;

    @RequestMapping(method = RequestMethod.PUT, value = "/transaction/{transactionId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity putTransaction(@PathVariable("transactionId") @NotNull Long transactionId,
                                         @RequestBody @Valid TransactionDto transactionDto) {
        transactionService.storeTransaction(transactionId, transactionDto);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transaction/{transactionId}")
    public TransactionDto getTransactionById(@PathVariable("transactionId") @NotNull Long transactionId) {
        return transactionService.getTransactionByTransactionId(transactionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/types/{transactionType}")
    public Set<Long> getTransactionsByType(@PathVariable("transactionType") @NotEmpty String transactionType) {
        return transactionService.getTransactionsIdsByType(transactionType);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sum/{parentTransactionId}")
    public TransactionsSum calculateSumWithParent(@PathVariable("parentTransactionId") @NotNull Long parentTransactionId) {
        return transactionService.calculateSumOfChildTransactions(parentTransactionId);
    }

}
