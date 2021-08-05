INSERT INTO ACCOUNTS (account_id, currency, balance, account_status) VALUES
  (111, 'GBP', 200.98, 'ACTIVE'),
  (222, 'GBP', 100.45, 'ACTIVE'),
  (333, 'GBP', 300.00, 'ACTIVE'),
  (444, 'GBP', 10.00, 'ACTIVE'),
  (555, 'NOK', 100.00, 'ACTIVE'),
  (666, 'GBP', 10.00, 'DELETED'),
  (777, 'NOK', 800.00, 'ACTIVE'),
  (999, 'GBP', 10000.00, 'ACTIVE');

INSERT INTO TRANSACTIONS (sender_account_id, receiver_account_id, amount, currency, transaction_date, reference_id) VALUES
  (111, 222, 200.00,'GBP', '2020-04-20 12:58:52.1234', 'b5f59f74-05f3-4af5-9b9f-394eb6c23b6a'),
  (222, 333, 100.00,'GBP', '2020-05-20 12:58:52.1234', 'b5f59f74-05f3-4af5-9b9f-394eb6c23b6a' ),
  (333, 444, 300.00,'GBP', '2020-02-20 12:58:52.1234', 'b5f59f74-05f3-4af5-9b9f-394eb6c23b6a'),
  (444, 111, 10.00,'GBP', '2020-07-20 12:58:52.1234', 'b5f59f74-05f3-4af5-9b9f-394eb6c23b6a'),
  (555, 111, 10.00,'GBP', '2020-04-24 12:58:52.1234', 'b5f59f74-05f3-4af5-9b9f-394eb6c23b6a'),
  (999, 111, 10.00,'GBP', '2020-04-22 12:58:52.1234', 'b5f59f74-05f3-4af5-9b9f-394eb6c23b6a');

