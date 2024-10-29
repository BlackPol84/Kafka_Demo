package ru.t1.java.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.t1.java.demo.model.dto.FailedTransactionDto;
import ru.t1.java.demo.service.AccountService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService service;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    public void unlock_SuccessfulUnlock() throws Exception {

        FailedTransactionDto dto = new FailedTransactionDto();
        dto.setOriginalTransactionId(1L);
        dto.setAccountId(1L);

        when(service.unlockAccount(dto.getAccountId())).thenReturn("Unlocked");

        mockMvc.perform(post("/unlock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Account unlocked"));
    }

    @Test
    public void unlock_InsufficientFunds() throws Exception {

        FailedTransactionDto dto = new FailedTransactionDto();
        dto.setOriginalTransactionId(1L);
        dto.setAccountId(1L);

        when(service.unlockAccount(dto.getAccountId()))
                .thenReturn("Insufficient funds to unlock");

        mockMvc.perform(post("/unlock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                        .andExpect(status().isForbidden())
                        .andExpect(content()
                        .string("Insufficient funds to unlock account."));
    }

    @Test
    public void unlock_NotBlocked() throws Exception {

        FailedTransactionDto dto = new FailedTransactionDto();
        dto.setOriginalTransactionId(1L);
        dto.setAccountId(1L);

        when(service.unlockAccount(dto.getAccountId()))
                .thenReturn("Account is not blocked");

        mockMvc.perform(post("/unlock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Account is not blocked."));
    }
}
