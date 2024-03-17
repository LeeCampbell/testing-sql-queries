CREATE VIEW marketing_internal.vw_refund 
AS
SELECT 
    amount, 
    status,
    updated_at 
FROM 
    sales_published.refund;
