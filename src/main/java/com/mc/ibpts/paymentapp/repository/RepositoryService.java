package com.mc.ibpts.paymentapp.repository;

import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RepositoryService {

    /** The method should return the Account details of a particular account_id.
     * The response should be an Optional one,
     * and if the account_id is not found in persistence, should return Optional.empty()
     * */
    public Optional<AccountInfo> fetchAccountInfo(Long accountId);

    /** The method will return the entire account details in the persistence storage.
     * The response will be a list of account informations.
     * */
    public List<AccountInfo> fetchAllAccountInfo();

    /** This method will return transaction details of a particular account_id
     * limited to maximum 20 transactions.
     * And the response list should be sorted based on transaction date in descending order.
     * */
    public List<TransactionInfo> fetchMiniStatementByAccountId(Long accountId);

    /** This method is used for ensuring the idempotency of the request.
    * If same key is again repeating, the method should throw an exception,
    * which will be handled in Controller advice.
    * */
    void insertIdempotencyKey(String idempotencyKey);

    /** This method will persist the transaction details.*/
    void saveTransactionDetails(TransactionInfo transactionInfo);

    /** This method will be updating the account balance for accounts.
     * It will add or subtract balance based on the value providing in amountToAdd filed.
     * if amountToAdd is +ve, the balance will be incremented and
     * if amountToAdd is -ve, the balance will bew decremented.
     * */
    void updateBalanceInfo(Long accountId, BigDecimal amountToAdd);
}
