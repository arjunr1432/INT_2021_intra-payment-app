package com.mc.ibpts.paymentapp.repository;

import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;

import java.util.List;
import java.util.Optional;

public interface RepositoryService {
    public Optional<AccountInfo> fetchAccountInfo(Long accountId);
    public List<AccountInfo> fetchAllAccountInfo();
    public List<TransactionInfo> fetchMiniStatementByAccountId(Long accountId);
}
