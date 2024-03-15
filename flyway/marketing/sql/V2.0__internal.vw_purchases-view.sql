CREATE VIEW marketing_internal.vw_Purchases 
AS
SELECT Amount, Status, Updated_at 
FROM sales_published.purchases;