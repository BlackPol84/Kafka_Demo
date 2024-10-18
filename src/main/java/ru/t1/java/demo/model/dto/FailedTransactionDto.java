package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FailedTransactionDto {

    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("transaction_id")
    private Long originalTransactionId;

    @NotNull
    @JsonProperty("account_id")
    private Long accountId;
}
