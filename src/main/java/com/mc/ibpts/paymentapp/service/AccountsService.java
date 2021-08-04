package com.mc.ibpts.paymentapp.service;

import com.mc.ibpts.paymentapp.common.gen.api.AccountsApiDelegate;
import com.mc.ibpts.paymentapp.common.gen.model.AccountBalanceResponse;
import com.mc.ibpts.paymentapp.common.gen.model.AccountDetailsResponseData;
import com.mc.ibpts.paymentapp.common.gen.model.AccountStatementResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountsService implements AccountsApiDelegate {

    public ResponseEntity<AccountBalanceResponse> accountsAccountIdBalanceGet(String accountId) {

        return null;

    }

    public ResponseEntity<List<AccountStatementResponseData>> accountsAccountIdStatementsMiniGet(String accountId) {


        return null;
    }


    public ResponseEntity<List<AccountDetailsResponseData>> accountsGet() {


        return null;
    }
}
