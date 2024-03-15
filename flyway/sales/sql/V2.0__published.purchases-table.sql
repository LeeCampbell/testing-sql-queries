CREATE TYPE sales_published.purchase_status AS ENUM (
  'Submitted', 
  'Accepted', 
  'Settled', 
  'Cancelled');

CREATE TABLE sales_published.purchases
(
  purchase_id             BIGSERIAL UNIQUE NOT NULL,
  account_id              INT              NOT NULL,
  created_at              TIMESTAMP        NOT NULL,
  amount                  DECIMAL(38,2)    NOT NULL,
  status	                sales_published.purchase_status  NOT NULL, 
  updated_at              TIMESTAMP        NOT NULL,
  
  PRIMARY KEY (purchase_id)
);