package com.mc.ibpts.paymentapp.dvo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionInfo {
    private Long senderAccountId;
    private Long receiverAccountId;
    private Double amount;
    private String currency;
    private LocalDateTime transactionDate;
    private String referenceId;
    private String type;
}