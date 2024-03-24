CREATE OR REPLACE VIEW marketing_internal.vw_Purchase 
AS
SELECT 
    amount, 
    status, 
    updated_at 
FROM 
    sales_published.purchase;
