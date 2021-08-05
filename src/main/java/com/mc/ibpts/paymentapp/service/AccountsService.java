package com.mc.ibpts.paymentapp.service;

import com.mc.ibpts.paymentapp.common.gen.api.AccountsApiDelegate;
import com.mc.ibpts.paymentapp.common.gen.model.AccountBalanceResponse;
import com.mc.ibpts.paymentapp.common.gen.model.AccountDetailsResponseData;
import com.mc.ibpts.paymentapp.common.gen.model.AccountStatementResponseData;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import com.mc.ibpts.paymentapp.repository.RepositoryService;
import com.mc.ibpts.paymentapp.utils.RequestResponseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountsService implements AccountsApiDelegate {

    @Autowired
    private RepositoryService embeddedRepositoryService;

    public ResponseEntity<AccountBalanceResponse> accountsAccountIdBalanceGet(String accountId) {
        AtomicReference<AccountBalanceResponse> accountBalanceResponse = new AtomicReference<>();
        embeddedRepositoryService.fetchAccountInfo(Long.valueOf(accountId)).ifPresentOrElse(
                (accountInfo) -> {
                    log.info("Fetched account details from database.");
                    accountBalanceResponse.set(
                            RequestResponseMapper.ACCOUNT_INFO_TO_ACCOUNT_BALANCE_DETAILS_RESPONSE.apply(accountInfo));
                },
                () -> {
                    log.error("Account details not found. account_id={}", accountId);
                    throw  new CustomBusinessException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid account number.");
                }
        );
        log.info("Successfully retrieved account balance details for account_id={}", accountId);
        return new ResponseEntity<AccountBalanceResponse>(accountBalanceResponse.get(), HttpStatus.OK);

    }

    public ResponseEntity<List<AccountStatementResponseData>> accountsAccountIdStatementsMiniGet(String accountId) {
        AtomicReference<List<AccountStatementResponseData>> statementResponseData = new AtomicReference<>();
        embeddedRepositoryService.fetchAccountInfo(Long.valueOf(accountId)).ifPresentOrElse(
                (accountInfo) -> {
                    log.info("Fetched transaction details from database.");
                    List<TransactionInfo> list = embeddedRepositoryService.fetchMiniStatementByAccountId(
                            Long.valueOf(accountId));
                    if (CollectionUtils.isEmpty(list)) {
                        log.warn("No transactions exists for the account.");
                        throw  new CustomBusinessException(
                                HttpStatus.OK,
                                "No Transaction statement for the account.");
                    } else {
                        statementResponseData.set(list.stream().peek(transactionInfo -> {
                            if (transactionInfo.getSenderAccountId().equals(Long.valueOf(accountId))) {
                                transactionInfo.setType("DEBIT");
                            } else {
                                transactionInfo.setType("CREDIT");
                            }
                        }).map(RequestResponseMapper.TRANSACTION_INFO_TO_STATEMENT).collect(Collectors.toList()));
                    }
                },
                () -> {
                    log.error("Account details not found. account_id={}", accountId);
                    throw  new CustomBusinessException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid account number.");
                }
        );
        log.info("Successfully retrieved mini account statement details for account_id={} with {} no of records.",
                accountId, statementResponseData.get().size());
        return new ResponseEntity<>(statementResponseData.get(), HttpStatus.ACCEPTED);
    }


    public ResponseEntity<List<AccountDetailsResponseData>> accountsGet() {
        List<AccountDetailsResponseData> accountDetailsResponseData = embeddedRepositoryService.fetchAllAccountInfo()
                .stream().map(RequestResponseMapper.ACCOUNT_INFO_TO_ACCOUNT_DETAILS_RESPONSE).collect(Collectors.toList());
        log.info("Successfully retrieved account details with {} no of records.", accountDetailsResponseData.size());
        return new ResponseEntity<>(accountDetailsResponseData, HttpStatus.OK);
    }
}
