package com.mc.ibpts.paymentapp.controller;

import com.mc.ibpts.paymentapp.common.gen.api.V1Api;
import com.mc.ibpts.paymentapp.common.gen.api.V1ApiDelegate;
import com.mc.ibpts.paymentapp.common.gen.model.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountsV1Controller implements V1Api {

    private final V1ApiDelegate accountsV1Service;

    /**
     * GET /v1/accounts/{account_id}/balance : This API will return the balance details of a particular account.
     *
     * @param accountId Unique id associated with each accounts. (required)
     * @return Successful response: Balance details a particular account. (status code 200)
     *         or Failed response: Bad request (status code 400)
     *         or Failed response: Account details not found (status code 404)
     */
    @ApiOperation(value = "This API will return the balance details of a particular account.", nickname = "v1AccountsAccountIdBalanceGet", notes = "", response = AccountBalanceResponse.class, authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Account Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: Balance details a particular account.", response = AccountBalanceResponse.class),
            @ApiResponse(code = 400, message = "Failed response: Bad request", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Failed response: Account details not found", response = ErrorResponse.class) })
    @GetMapping(
            value = "/v1/accounts/{account_id}/balance",
            produces = { "application/json" }
    )
    public ResponseEntity<AccountBalanceResponse> v1AccountsAccountIdBalanceGet(@ApiParam(value = "Unique id associated with each accounts.",required=true) @PathVariable("account_id") String accountId) {
        log.info("Request received to fetch account balance details for account_id={}", accountId);
        return accountsV1Service.v1AccountsAccountIdBalanceGet(accountId);
    }


    /**
     * GET /v1/accounts/{account_id}/statements/mini : This API will return a mini statement of most recent 20 transactions of a particular account.
     *
     * @param accountId Unique id associated with each accounts. (required)
     * @return Successful response: Balance details a a particular account. (status code 200)
     *         or Failed response: Bad request (status code 400)
     *         or Failed response: Account details not found (status code 404)
     */
    @ApiOperation(value = "This API will return a mini statement of most recent 20 transactions of a particular account.", nickname = "v1AccountsAccountIdStatementsMiniGet", notes = "", response = AccountStatementResponseData.class, responseContainer = "List", authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Account Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: Balance details a a particular account.", response = AccountStatementResponseData.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Failed response: Bad request", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Failed response: Account details not found", response = ErrorResponse.class) })
    @GetMapping(
            value = "/v1/accounts/{account_id}/statements/mini",
            produces = { "application/json" }
    )
    public ResponseEntity<List<AccountStatementResponseData>> v1AccountsAccountIdStatementsMiniGet(@ApiParam(value = "Unique id associated with each accounts.",required=true) @PathVariable("account_id") String accountId) {
        log.info("Request received to fetch account mini statement details for account_id={}", accountId);
        return accountsV1Service.v1AccountsAccountIdStatementsMiniGet(accountId);
    }


    /**
     * GET /v1/accounts : This API will return the complete account details, including new and deleted accounts.
     *
     * @return Successful response: List of all the accounts. (status code 200)
     */
    @ApiOperation(value = "This API will return the complete account details, including new and deleted accounts.", nickname = "v1AccountsGet", notes = "", response = AccountDetailsResponseData.class, responseContainer = "List", authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Account Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: List of all the accounts.", response = AccountDetailsResponseData.class, responseContainer = "List") })
    @GetMapping(
            value = "/v1/accounts",
            produces = { "application/json" }
    )
    public ResponseEntity<List<AccountDetailsResponseData>> v1AccountsGet() {
        log.info("Request received to fetch all account details");
        return accountsV1Service.v1AccountsGet();
    }


    /**
     * POST /v1/payments/transfer : This API will transfer money from one account to other.
     *
     * @param idempotencyKey Unique identifier for idempotency (required)
     * @param paymentTransferRequest Request payload for adding a new element to the existing Array. (required)
     * @return Successful response: Payment transfer completed. (status code 200)
     *         or Failed response: Bad request (status code 400)
     */
    @ApiOperation(value = "This API will transfer money from one account to other.", nickname = "v1PaymentsTransferPost", notes = "", response = PaymentTransferResponse.class, authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Payment Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: Payment transfer completed.", response = PaymentTransferResponse.class),
            @ApiResponse(code = 400, message = "Failed response: Bad request", response = ErrorResponse.class) })
    @PostMapping(
            value = "/v1/payments/transfer",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<PaymentTransferResponse> v1PaymentsTransferPost(@ApiParam(value = "Unique identifier for idempotency" ,required=true) @RequestHeader(value="Idempotency-Key", required=true) String idempotencyKey,@ApiParam(value = "Request payload for adding a new element to the existing Array." ,required=true )  @Valid @RequestBody PaymentTransferRequest paymentTransferRequest) {
        log.info("Request received to transfer money fromAccountId={} to toAccountId={} with amount={}",
                paymentTransferRequest.getSenderAccountId(),
                paymentTransferRequest.getReceiverAccountId(),
                paymentTransferRequest.getAmount());
        return accountsV1Service.v1PaymentsTransferPost(idempotencyKey, paymentTransferRequest);
    }

}
