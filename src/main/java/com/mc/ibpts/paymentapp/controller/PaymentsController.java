package com.mc.ibpts.paymentapp.controller;

import com.mc.ibpts.paymentapp.common.gen.api.PaymentsApi;
import com.mc.ibpts.paymentapp.common.gen.api.PaymentsApiDelegate;
import com.mc.ibpts.paymentapp.common.gen.model.ErrorResponse;
import com.mc.ibpts.paymentapp.common.gen.model.PaymentTransferRequest;
import com.mc.ibpts.paymentapp.common.gen.model.PaymentTransferResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class PaymentsController implements PaymentsApi {

    private final PaymentsApiDelegate paymentsService;

    /**
     * POST /payments/transfer : This API will transfer money from one account to other.
     *
     * @param paymentTransferRequest Request payload for adding a new element to the existing Array. (required)
     * @return Successful response: Payment transfer completed. (status code 200)
     *         or Failed response: Bad request (status code 400)
     */
    @ApiOperation(value = "This API will transfer money from one account to other.", nickname = "paymentsTransferPost", notes = "", response = PaymentTransferResponse.class, authorizations = {

            @Authorization(value = "BasicAuth")
    }, tags={ "Payment Services", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response: Payment transfer completed.", response = PaymentTransferResponse.class),
            @ApiResponse(code = 400, message = "Failed response: Bad request", response = ErrorResponse.class) })
    @PostMapping(
            value = "/payments/transfer",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<PaymentTransferResponse> paymentsTransferPost(@ApiParam(value = "Request payload for adding a new element to the existing Array." ,required=true )  @Valid @RequestBody PaymentTransferRequest paymentTransferRequest) {
        return getDelegate().paymentsTransferPost(paymentTransferRequest);
    }
}
