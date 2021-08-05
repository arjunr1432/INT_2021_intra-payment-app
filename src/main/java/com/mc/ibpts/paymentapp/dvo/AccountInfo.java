package com.mc.ibpts.paymentapp.dvo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfo {
    private Long accountId;
    private String currency;
    private Double balance;
    private String accountStatus;

}