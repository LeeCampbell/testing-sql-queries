CREATE TYPE sales_published.refund_status AS ENUM (
  'Submitted', 
  'Refunded', 
  'Returned', 
  'Cancelled');

CREATE TABLE sales_published.refund
(
  refund_id               BIGSERIAL UNIQUE NOT NULL,
  account_id              INT              NOT NULL,
  created_at              TIMESTAMP        NOT NULL,
  amount                  DECIMAL(38,2)    NOT NULL,
  status	                sales_published.refund_status  NOT NULL, 
  updated_at              TIMESTAMP        NOT NULL,
  
  PRIMARY KEY (refund_id)
);