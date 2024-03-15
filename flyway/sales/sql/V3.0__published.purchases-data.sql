INSERT INTO sales_published.purchases(
  account_id,
  created_at,
  amount,
  status, 
  updated_at
)
VALUES  (127, '2023-01-01 10:00:00', 100.00, 'Submitted', '2023-01-01 10:00:00'),
        (456, '2023-01-02 15:30:00',  50.50, 'Accepted',  '2023-01-02 15:31:00'),
        (127, '2023-01-04 09:20:00',  23.00, 'Cancelled', '2023-01-04 10:21:00'),
        (336, '2023-01-05 14:00:00', 150.00, 'Settled',   '2023-01-05 16:30:00');