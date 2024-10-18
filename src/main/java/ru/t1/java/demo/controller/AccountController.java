package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.model.dto.FailedTransactionDto;
import ru.t1.java.demo.service.AccountService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/unlock")
public class AccountController {

    private final AccountService service;

    @PostMapping()
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unlock(@RequestBody FailedTransactionDto dto) {

        String response = service.unlockAccount(dto.getAccountId());

        if("Unlocked".equals(response)) {
            return ResponseEntity.ok("Account unlocked");

        } else if ("Insufficient funds to unlock".equals(response)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Insufficient funds to unlock account.");

        } else {
            return ResponseEntity.ok("Account is not blocked.");
        }
    }
}
