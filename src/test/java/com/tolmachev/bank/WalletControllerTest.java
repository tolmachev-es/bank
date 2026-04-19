package com.tolmachev.bank;

import com.tolmachev.bank.config.RetryExecutor;
import com.tolmachev.bank.repository.LedgerRepository;
import com.tolmachev.bank.repository.WalletRepository;
import com.tolmachev.bank.repository.entity.WalletEntity;
import com.tolmachev.bank.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
})
@AutoConfigureMockMvc
class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WalletService walletService;
    @MockBean
    private WalletRepository walletRepository;
    @MockBean
    private LedgerRepository ledgerRepository;
    @Autowired
    private RetryExecutor retryExecutor;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @MockBean
    private PlatformTransactionManager platformTransactionManager;

    @Test
    @Description("Тест обработки исключения WalletNotFoundException в методе POST /wallet с признаком списания")
    void shouldReturnErrorWhenWalletNotFoundWhenOperationTypeWithdraw() throws Exception {
        when(walletRepository.reserve(Mockito.any(), Mockito.any()))
                .thenReturn(0);

        when(walletRepository.existsById(Mockito.any(UUID.class))).thenReturn(false);
        mockMvc.perform(post("/wallet")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                               {
                                   "walletId": "9e0a5ba0-6ca5-405b-af69-1b4469a6641f",
                                   "operationType": "WITHDRAW",
                                   "amount": 1000
                               }
                               """))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Wallet with id 9e0a5ba0-6ca5-405b-af69-1b4469a6641f not found"));
    }

    @Test
    @Description("Тест обработки исключения BalanceIsLessThanWithdrawException в методе POST /wallet с признаком списания")
    void shouldReturnErrorWhenBalanceIsLessThanWithdrawExceptionOperationTypeWithdraw() throws Exception {
        when(walletRepository.reserve(Mockito.any(), Mockito.any()))
                .thenReturn(0);

        when(walletRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
        mockMvc.perform(post("/wallet")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                               {
                                   "walletId": "9e0a5ba0-6ca5-405b-af69-1b4469a6641f",
                                   "operationType": "WITHDRAW",
                                   "amount": 1000
                               }
                               """))
               .andExpect(status().isUnprocessableEntity())
               .andExpect(jsonPath("$.error").value("Balance less than withdraw amount on wallet 9e0a5ba0-6ca5-405b-af69-1b4469a6641f"));
    }

    @Test
    @Description("Тест обработки исключения WalletNotFoundException в методе POST /wallet с признаком зачисления")
    void shouldReturnErrorWhenWalletNotFoundWhenOperationTypeDeposit() throws Exception {
        when(walletRepository.deposit(Mockito.any(), Mockito.any()))
                .thenReturn(0);

        mockMvc.perform(post("/wallet")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                               {
                                   "walletId": "9e0a5ba0-6ca5-405b-af69-1b4469a6641f",
                                   "operationType": "DEPOSIT",
                                   "amount": 1000
                               }
                               """))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Wallet with id 9e0a5ba0-6ca5-405b-af69-1b4469a6641f not found"));
    }

    @Test
    @Description("Тест успешной обработки запроса в методе POST /wallet с признаком зачисления")
    void shouldReturnIsOkOperationTypeDeposit() throws Exception {
        when(walletRepository.deposit(Mockito.any(), Mockito.any()))
                .thenReturn(1);

        mockMvc.perform(post("/wallet")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                               {
                                   "walletId": "9e0a5ba0-6ca5-405b-af69-1b4469a6641f",
                                   "operationType": "DEPOSIT",
                                   "amount": 1000
                               }
                               """))
               .andExpect(status().isOk());
    }

    @Test
    @Description("Тест успешной обработки запроса в методе POST /wallet с признаком списания")
    void shouldReturnIsOkOperationTypeWithdraw() throws Exception {
        when(walletRepository.reserve(Mockito.any(), Mockito.any()))
                .thenReturn(1);

        mockMvc.perform(post("/wallet")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                               {
                                   "walletId": "9e0a5ba0-6ca5-405b-af69-1b4469a6641f",
                                   "operationType": "WITHDRAW",
                                   "amount": 1000
                               }
                               """))
               .andExpect(status().isOk());
    }

    @Test
    @Description("Тест обработки исключения WalletNotFoundException в методе GET /wallet/{id}")
    void shouldReturnWalletNotFoundExceptionOnGetBalance() throws Exception {
        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/wallet/{id}", "9e0a5ba0-6ca5-405b-af69-1b4469a6641f")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Wallet with id 9e0a5ba0-6ca5-405b-af69-1b4469a6641f not found"));
    }

    @Test
    @Description("Тест обработки исключения WalletNotFoundException в методе GET /wallet/{id}")
    void shouldReturnIsOkOnGetBalance() throws Exception {
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setId(UUID.fromString("9e0a5ba0-6ca5-405b-af69-1b4469a6641f"));
        walletEntity.setAvailableBalance(BigDecimal.valueOf(1000.01));

        when(walletRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(walletEntity));

        mockMvc.perform(get("/wallet/{id}", "9e0a5ba0-6ca5-405b-af69-1b4469a6641f")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.walletId").value("9e0a5ba0-6ca5-405b-af69-1b4469a6641f"))
               .andExpect(jsonPath("$.balance").value("1000.01"));
    }

}