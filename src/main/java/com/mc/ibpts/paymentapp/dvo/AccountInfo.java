package com.mc.ibpts.paymentapp.dvo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountInfo {
    private Long accountId;
    private String currency;
    private BigDecimal balance;
    private String accountStatus;

}