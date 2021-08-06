package com.mc.ibpts.paymentapp.dvo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionInfo implements Comparable<TransactionInfo> {
    private Long senderAccountId;
    private Long receiverAccountId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime transactionDate;
    private String referenceId;
    private String type;

    @Override
    public int compareTo(TransactionInfo o) {
        return -this.transactionDate.compareTo(o.getTransactionDate());
    }
}