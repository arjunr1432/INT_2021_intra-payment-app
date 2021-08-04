package com.mc.ibpts.paymentapp.service;

import com.mc.ibpts.paymentapp.common.gen.api.PaymentsApiDelegate;
import com.mc.ibpts.paymentapp.common.gen.model.PaymentTransferRequest;
import com.mc.ibpts.paymentapp.common.gen.model.PaymentTransferResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentsService implements PaymentsApiDelegate {

    public ResponseEntity<PaymentTransferResponse> paymentsTransferPost(PaymentTransferRequest paymentTransferRequest) {

        return null;

    }
}
