
CREATE TABLE ACCOUNTS (
  account_id NUMBER(10, 0)  PRIMARY KEY,
  currency VARCHAR(3) NOT NULL,
  balance NUMBER(10, 2) NOT NULL,
  account_status VARCHAR(10) NOT NULL
);

CREATE TABLE TRANSACTIONS (
    id bigint auto_increment  PRIMARY KEY,
    sender_account_id NUMBER(10, 0) NOT NULL,
    receiver_account_id NUMBER(10, 0) NOT NULL,
    amount NUMBER(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_date TIMESTAMP(6) NOT NULL,
    reference_id VARCHAR(50) NOT NULL
);
ALTER TABLE TRANSACTIONS
    ADD FOREIGN KEY (sender_account_id)
    REFERENCES ACCOUNTS(account_id);
ALTER TABLE TRANSACTIONS
    ADD FOREIGN KEY (receiver_account_id)
    REFERENCES ACCOUNTS(account_id);
