package ru.t1.java.demo.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CheckResponse {
    private Boolean blocked;
}
