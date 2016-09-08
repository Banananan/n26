package n26.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import n26.dto.TransactionDto;
import n26.dto.TransactionsSum;
import n26.repository.Transaction;
import n26.service.TransactionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void returnsOkStatusForSuccessfullyStoredTransaction() {
        Mockito.doNothing()
                .when(transactionService).storeTransaction(Mockito.anyLong(), Mockito.any(TransactionDto.class));

        try {
            mvc.perform(MockMvcRequestBuilders.put("/transactionservice/transaction/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Transaction.builder()
                            .type("test")
                            .amount(1000)
                            .build())))
                    .andExpect(MockMvcResultMatchers.status()
                            .isOk());
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void returnsClientErrorStatusForIncorrectlyCreatedTransaction() {
        Mockito.doNothing()
                .when(transactionService).storeTransaction(Mockito.anyLong(), Mockito.any(TransactionDto.class));

        try {
            mvc.perform(MockMvcRequestBuilders.put("/transactionservice/transaction/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Transaction.builder()
                            .transactionId(1L)
                            .amount(1000)
                            .build())))
                    .andExpect(MockMvcResultMatchers.status()
                            .isBadRequest());
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void getsTransactionByTransactionId() {
        TransactionDto transaction = TransactionDto.builder()
                .type("test")
                .amount(1000D)
                .build();
        Mockito.when(transactionService.getTransactionByTransactionId(Mockito.anyLong()))
                .thenReturn(transaction);

        try {
            mvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/1")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .json(objectMapper.writeValueAsString(transaction)));
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void getsTransactionsIdsByType() {
        long transactionId = 1;
        Mockito.when(transactionService.getTransactionsIdsByType(Mockito.anyString()))
                .thenReturn(Collections.singleton(transactionId));

        try {
            mvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/test")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .json(Arrays.toString(new Long[]{transactionId})));
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void returnsEmptyArrayOfTransactionsIdsForNotExistingType() {
        Mockito.when(transactionService.getTransactionsIdsByType(Mockito.anyString()))
                .thenReturn(Collections.emptySet());

        try {
            mvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/test")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .json(Arrays.toString(new Long[]{})));
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void calculatesSumOfChildTransactions() {
        long parentTransactionId = 0L;
        String type = "test";
        HashSet<Transaction> childTransactions = new HashSet<>();
        childTransactions.add(Transaction.builder()
                .transactionId(1L)
                .parentTransactionId(parentTransactionId)
                .type(type)
                .amount(1000)
                .build());
        childTransactions.add(Transaction.builder()
                .transactionId(2L)
                .parentTransactionId(parentTransactionId)
                .type(type)
                .amount(2000)
                .build());
        TransactionsSum transactionsSum = TransactionsSum.builder()
                .sum(childTransactions.stream()
                        .mapToDouble(Transaction::getAmount)
                        .sum())
                .build();
        Mockito.when(transactionService.calculateSumOfChildTransactions(Mockito.anyLong()))
                .thenReturn(transactionsSum);

        try {
            mvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/" + parentTransactionId)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .json(objectMapper.writeValueAsString(transactionsSum)));
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void returns0AsSumForChildlessTransaction() {
        TransactionsSum transactionsSum = TransactionsSum.builder()
                .sum(0D)
                .build();
        Mockito.when(transactionService.calculateSumOfChildTransactions(Mockito.anyLong()))
                .thenReturn(transactionsSum);

        try {
            mvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/0")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .json(objectMapper.writeValueAsString(transactionsSum)));
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
