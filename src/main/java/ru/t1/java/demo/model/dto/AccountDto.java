package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.util.AccountType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("client_first_name")
    private String clientFirstName;

    @JsonProperty("client_last_name")
    private String clientLastName;

    @JsonProperty("client_middle_name")
    private String clientMiddleName;

    @JsonProperty("account_type")
    private AccountType type;

    @JsonProperty("balance")
    private Double balance;

}