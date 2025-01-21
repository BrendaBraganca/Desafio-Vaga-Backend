package com.picpaysimplificado.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;

class TransactionServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private TransactionRepository repository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() throws Exception{
        MockitoAnnotations.openMocks(this);
    } 

    @Test
    @DisplayName("Should create transaction successfully when everything is ok")
    void createTransactionCase1() throws Exception{
        User sender = new User(1L, "Ana", "Souza", "12345678900", "ana@email.com", "123", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Jose", "Silveira", "98765432100", "jose@email.com", "321", new BigDecimal(10), UserType.COMMON);
        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(request);

        verify(repository, times(1)).save(any());
        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);


        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);
    }

    @Test
    @DisplayName("Should cthrwo Exception when transaction is not allowed")
    void createTransactionCase2() throws Exception{
        User sender = new User(1L, "Ana", "Souza", "12345678900", "ana@email.com", "123", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Jose", "Silveira", "98765432100", "jose@email.com", "321", new BigDecimal(10), UserType.COMMON);
        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class , () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
            transactionService.createTransaction(request);
        });

        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());


    }
}
