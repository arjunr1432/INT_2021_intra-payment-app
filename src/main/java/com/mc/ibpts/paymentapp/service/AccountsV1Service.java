package com.mc.ibpts.paymentapp.service;

import com.mc.ibpts.paymentapp.common.gen.api.V1ApiDelegate;
import com.mc.ibpts.paymentapp.common.gen.model.*;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import com.mc.ibpts.paymentapp.repository.RepositoryService;
import com.mc.ibpts.paymentapp.utils.RequestResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountsV1Service implements V1ApiDelegate {

    private final RepositoryService cachedRepositoryServiceImpl;

    public ResponseEntity<AccountBalanceResponse> v1AccountsAccountIdBalanceGet(String accountId) {
        AtomicReference<AccountBalanceResponse> accountBalanceResponse = new AtomicReference<>();
        cachedRepositoryServiceImpl.fetchAccountInfo(Long.valueOf(accountId)).ifPresentOrElse(
                (accountInfo) -> {
                    log.info("Fetched account details from persistence.");
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


    public ResponseEntity<List<AccountStatementResponseData>> v1AccountsAccountIdStatementsMiniGet(String accountId) {
        AtomicReference<List<AccountStatementResponseData>> statementResponseData = new AtomicReference<>();
        /* Checking if account is available or not.*/
        cachedRepositoryServiceImpl.fetchAccountInfo(Long.valueOf(accountId)).ifPresentOrElse(
                (accountInfo) -> {
                    log.info("Fetched transaction details from persistence.");
                    List<TransactionInfo> list = cachedRepositoryServiceImpl.fetchMiniStatementByAccountId(
                            Long.valueOf(accountId));
                    if (CollectionUtils.isEmpty(list)) {
                        log.warn("No transactions exists for the account.");
                        throw  new CustomBusinessException(
                                HttpStatus.OK,
                                "No Transaction statement for the account.");
                    } else {
                        Collections.sort(list);
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
        return new ResponseEntity<>(statementResponseData.get(), HttpStatus.OK);
    }


    public ResponseEntity<List<AccountDetailsResponseData>> v1AccountsGet() {
        List<AccountDetailsResponseData> accountDetailsResponseData = cachedRepositoryServiceImpl.fetchAllAccountInfo()
                .stream().map(RequestResponseMapper.ACCOUNT_INFO_TO_ACCOUNT_DETAILS_RESPONSE).collect(Collectors.toList());
        log.info("Successfully retrieved account details with {} no of records.", accountDetailsResponseData.size());
        return new ResponseEntity<>(accountDetailsResponseData, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<PaymentTransferResponse> v1PaymentsTransferPost(
            String idempotencyKey, PaymentTransferRequest paymentTransferRequest) {
        /* Ensuring Idempotency of the request. */
        cachedRepositoryServiceImpl.insertIdempotencyKey(idempotencyKey);

        /* Validate sender and receiver account_id and balance info */
        validateAccountAndBalanceInfo(paymentTransferRequest);

        /* Make transaction */
        TransactionInfo transactionInfo = doTransaction(paymentTransferRequest);

        return new ResponseEntity<>(
                RequestResponseMapper.TRANSACTION_INFO_TO_PAYMENT_TRANSFER_RESPONSE.apply(transactionInfo),
                HttpStatus.ACCEPTED);
    }

    private TransactionInfo doTransaction(PaymentTransferRequest paymentTransferRequest) {
        /* Create and persist transaction details.*/
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setSenderAccountId(paymentTransferRequest.getSenderAccountId());
        transactionInfo.setReceiverAccountId(paymentTransferRequest.getReceiverAccountId());
        transactionInfo.setAmount(new BigDecimal(paymentTransferRequest.getAmount()));
        transactionInfo.setCurrency(paymentTransferRequest.getCurrency().getValue());
        transactionInfo.setReferenceId(MDC.get("requestID"));
        transactionInfo.setTransactionDate(LocalDateTime.now());
        cachedRepositoryServiceImpl.saveTransactionDetails(transactionInfo);

        /* Updating balance details for sender and receiver.*/
        cachedRepositoryServiceImpl.updateBalanceInfo(
                paymentTransferRequest.getSenderAccountId(),
                new BigDecimal(paymentTransferRequest.getAmount()).negate());
        cachedRepositoryServiceImpl.updateBalanceInfo(
                paymentTransferRequest.getReceiverAccountId(),
                new BigDecimal(paymentTransferRequest.getAmount()));

        return transactionInfo;
    }

    private void validateAccountAndBalanceInfo(PaymentTransferRequest paymentTransferRequest) {
        /* Sender related validations.*/
        cachedRepositoryServiceImpl.fetchAccountInfo(paymentTransferRequest.getSenderAccountId()).ifPresentOrElse(
                (accountInfo) -> {
                    log.info("Fetched Sender account details from persistence.");
                    /* Checking if account is operational or not*/
                    if (accountInfo.getAccountStatus().equals("DELETED")) {
                        log.error("Sender's account is in Deleted status, not able to perform the transaction.");
                        throw new CustomBusinessException(
                                HttpStatus.BAD_REQUEST,
                                "Sender's account is in Deleted status, not able to perform the transaction.");
                    }
                    /* Checking if sufficient balance or not.*/
                    if (accountInfo.getBalance().doubleValue() < new BigDecimal(paymentTransferRequest.getAmount()).doubleValue()) {
                        log.error("Not enough balance for transaction. availableBalance={}, requiredAmout={}",
                                accountInfo.getBalance(),
                                paymentTransferRequest.getAmount());
                        throw new CustomBusinessException(
                                HttpStatus.BAD_REQUEST,
                                "Insufficient funds, not able to perform the transaction.");
                    }
                    /* Check if currency is supported or not*/
                    if (!accountInfo.getCurrency().equals(paymentTransferRequest.getCurrency().getValue())) {
                        log.error("Sender's currency not matching the transaction currency. senderCurrency={}, transactionCurrency={}",
                                accountInfo.getCurrency(), paymentTransferRequest.getCurrency().getValue());
                        throw new CustomBusinessException(
                                HttpStatus.BAD_REQUEST,
                                "Sender's currency not supported.");
                    }
                },
                () -> {
                    log.error("Requested sender account_id={}, not found in the system.",
                            paymentTransferRequest.getSenderAccountId());
                    throw new CustomBusinessException(
                            HttpStatus.BAD_REQUEST,
                            "Sender's account_id not found.");
                }
        );

        /* Receiver related validations. */
        cachedRepositoryServiceImpl.fetchAccountInfo(paymentTransferRequest.getReceiverAccountId()).ifPresentOrElse(
                (accountInfo) -> {
                    log.info("Fetched Receiver account details from persistence.");
                    /* Checking if account is operational or not*/
                    if (accountInfo.getAccountStatus().equals("DELETED")) {
                        log.error("Receiver's account is in Deleted status, not able to perform the transaction.");
                        throw new CustomBusinessException(
                                HttpStatus.BAD_REQUEST,
                                "Receiver's account is in Deleted status, not able to perform the transaction.");
                    }
                    /* Check if currency is supported or not*/
                    if (!accountInfo.getCurrency().equals(paymentTransferRequest.getCurrency().getValue())) {
                        log.error("Receiver's currency not matching the transaction currency. receiverCurrency={}, transactionCurrency={}",
                                accountInfo.getCurrency(), paymentTransferRequest.getCurrency().getValue());
                        throw new CustomBusinessException(
                                HttpStatus.BAD_REQUEST,
                                "Receiver's currency not supported.");
                    }
                },
                () -> {
                    log.error("Requested receiver account_id={}, not found in the system.",
                            paymentTransferRequest.getReceiverAccountId());
                    throw new CustomBusinessException(
                            HttpStatus.BAD_REQUEST,
                            "Receiver's account_id not found.");
                }
        );

        /* Check if sending between same accounts*/
        if (paymentTransferRequest.getSenderAccountId().equals(paymentTransferRequest.getReceiverAccountId())) {
            log.error("Sending between same account numbers not allowed.");
            throw new CustomBusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Sending between same account numbers not permitted.");
        }
    }
}
