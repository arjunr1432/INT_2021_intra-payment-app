package com.mc.ibpts.paymentapp.controller;

import com.mc.ibpts.paymentapp.common.gen.api.AccountsApi;
import com.mc.ibpts.paymentapp.common.gen.api.AccountsApiDelegate;
import com.mc.ibpts.paymentapp.common.gen.model.AccountBalanceResponse;
import com.mc.ibpts.paymentapp.common.gen.model.AccountDetailsResponseData;
import com.mc.ibpts.paymentapp.common.gen.model.AccountStatementResponseData;
import com.mc.ibpts.paymentapp.common.gen.model.ErrorResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountsController implements AccountsApi {

    private final AccountsApiDelegate accountsService;

    /**
     * GET /accounts/{account_id}/balance : This API will return the balance details of a particular account.
     *
     * @param accountId Unique id associated with each accounts. (required)
     * @return Successful response: Balance details a particular account. (status code 200)
     *         or Failed response: Bad request (status code 400)
     *         or Failed response: Account details not found (status code 404)
     */
    @ApiOperation(value = "This API will return the balance details of a particular account.", nickname = "accountsAccountIdBalanceGet", notes = "", response = AccountBalanceResponse.class, authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Account Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: Balance details a particular account.", response = AccountBalanceResponse.class),
            @ApiResponse(code = 400, message = "Failed response: Bad request", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Failed response: Account details not found", response = ErrorResponse.class) })
    @GetMapping(
            value = "/accounts/{account_id}/balance",
            produces = { "application/json" }
    )
    public ResponseEntity<AccountBalanceResponse> accountsAccountIdBalanceGet(@ApiParam(value = "Unique id associated with each accounts.",required=true) @PathVariable("account_id") String accountId) {
        return accountsService.accountsAccountIdBalanceGet(accountId);
    }


    /**
     * GET /accounts/{account_id}/statements/mini : This API will return a mini statement of most recent 20 transactions of a particular account.
     *
     * @param accountId Unique id associated with each accounts. (required)
     * @return Successful response: Balance details a a particular account. (status code 200)
     *         or Failed response: Bad request (status code 400)
     *         or Failed response: Account details not found (status code 404)
     */
    @ApiOperation(value = "This API will return a mini statement of most recent 20 transactions of a particular account.", nickname = "accountsAccountIdStatementsMiniGet", notes = "", response = AccountStatementResponseData.class, responseContainer = "List", authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Account Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: Balance details a a particular account.", response = AccountStatementResponseData.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Failed response: Bad request", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Failed response: Account details not found", response = ErrorResponse.class) })
    @GetMapping(
            value = "/accounts/{account_id}/statements/mini",
            produces = { "application/json" }
    )
    public ResponseEntity<List<AccountStatementResponseData>> accountsAccountIdStatementsMiniGet(@ApiParam(value = "Unique id associated with each accounts.",required=true) @PathVariable("account_id") String accountId) {
        return accountsService.accountsAccountIdStatementsMiniGet(accountId);
    }


    /**
     * GET /accounts : This API will return the complete account details, including new and deleted accounts.
     *
     * @return Successful response: List of all the accounts. (status code 200)
     */
    @ApiOperation(value = "This API will return the complete account details, including new and deleted accounts.", nickname = "accountsGet", notes = "", response = AccountDetailsResponseData.class, responseContainer = "List", authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Account Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: List of all the accounts.", response = AccountDetailsResponseData.class, responseContainer = "List") })
    @GetMapping(
            value = "/accounts",
            produces = { "application/json" }
    )
    public ResponseEntity<List<AccountDetailsResponseData>> accountsGet() {
        return accountsService.accountsGet();
    }


}
